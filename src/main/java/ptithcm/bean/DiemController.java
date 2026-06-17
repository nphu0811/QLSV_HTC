package ptithcm.bean;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Nhập điểm - PGV + KHOA
 * Điểm hết môn = CC*0.1 + GK*0.3 + CK*0.6
 */
@Controller
@RequestMapping("/diem")
public class DiemController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String show(HttpSession session, ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(nhomQuyen) && !"KHOA".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String maKhoa = (String) session.getAttribute("maKhoa");

        // Load môn học và niên khóa
        List<Map<String, Object>> dsmh = jdbc.queryForList("SELECT MAMH, TENMH FROM MONHOC ORDER BY TENMH");
        List<Map<String, Object>> dsNienKhoa;
        if ("PGV".equals(nhomQuyen)) {
            dsNienKhoa = jdbc.queryForList("SELECT DISTINCT NIENKHOA FROM LOPTINCHI ORDER BY NIENKHOA DESC");
        } else {
            dsNienKhoa = jdbc.queryForList("SELECT DISTINCT NIENKHOA FROM LOPTINCHI WHERE MAKHOA=? ORDER BY NIENKHOA DESC", maKhoa);
        }
        model.addAttribute("dsmh", dsmh);
        model.addAttribute("dsNienKhoa", dsNienKhoa);
        return "diem";
    }

    @RequestMapping(value = "/load", method = RequestMethod.POST)
    public String loadDiem(@RequestParam String nienkhoa,
                           @RequestParam int hocky,
                           @RequestParam String mamh,
                           @RequestParam int nhom,
                           HttpSession session, ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(nhomQuyen) && !"KHOA".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String maKhoa = (String) session.getAttribute("maKhoa");

        // Tìm MALTC
        List<Map<String, Object>> ltcRows;
        if ("PGV".equals(nhomQuyen)) {
            ltcRows = jdbc.queryForList(
                "SELECT MALTC FROM LOPTINCHI WHERE NIENKHOA=? AND HOCKY=? AND MAMH=? AND NHOM=?",
                nienkhoa.trim(), hocky, mamh.trim(), nhom);
        } else {
            ltcRows = jdbc.queryForList(
                "SELECT MALTC FROM LOPTINCHI WHERE NIENKHOA=? AND HOCKY=? AND MAMH=? AND NHOM=? AND MAKHOA=?",
                nienkhoa.trim(), hocky, mamh.trim(), nhom, maKhoa);
        }
        
        if (ltcRows.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy lớp tín chỉ!");
            // Reload dropdown data
            reloadDropdowns(jdbc, nhomQuyen, maKhoa, model);
            return "diem";
        }
        int maltc = (Integer) ltcRows.get(0).get("MALTC");

        // Lấy tên môn
        String tenmh = jdbc.queryForObject("SELECT TENMH FROM MONHOC WHERE MAMH=?", String.class, mamh.trim());

        // Load danh sách SV đã đăng ký
        List<Map<String, Object>> dssv = jdbc.queryForList(
                "SELECT DK.MASV, SV.HO + ' ' + SV.TEN AS HOTENSV, " +
                "DK.DIEM_CC, DK.DIEM_GK, DK.DIEM_CK " +
                "FROM DANGKY DK " +
                "JOIN SINHVIEN SV ON DK.MASV=SV.MASV " +
                "WHERE DK.MALTC=? AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL) " +
                "ORDER BY SV.TEN, SV.HO", maltc);

        model.addAttribute("dssv", dssv);
        model.addAttribute("maltc", maltc);
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);
        model.addAttribute("mamh", mamh.trim());
        model.addAttribute("tenmh", tenmh);
        model.addAttribute("nhom", nhom);
        reloadDropdowns(jdbc, nhomQuyen, maKhoa, model);
        return "diem";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveDiem(@RequestParam int maltc,
                           @RequestParam String nienkhoa,
                           @RequestParam int hocky,
                           @RequestParam String mamh,
                           @RequestParam int nhom,
                           @RequestParam("masv[]") String[] masvArr,
                           @RequestParam("diemCC[]") String[] diemCCArr,
                           @RequestParam("diemGK[]") String[] diemGKArr,
                           @RequestParam("diemCK[]") String[] diemCKArr,
                           HttpSession session, RedirectAttributes ra) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(nhomQuyen) && !"KHOA".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        try {
            for (int i = 0; i < masvArr.length; i++) {
                Integer diemCC = parseIntOrNull(diemCCArr[i]);
                Double diemGK = parseDoubleOrNull(diemGKArr[i]);
                Double diemCK = parseDoubleOrNull(diemCKArr[i]);
                jdbc.update("UPDATE DANGKY SET DIEM_CC=?, DIEM_GK=?, DIEM_CK=? WHERE MALTC=? AND MASV=?",
                        diemCC, diemGK, diemCK, maltc, masvArr[i].trim());
            }
            ra.addFlashAttribute("success", "Ghi điểm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi ghi điểm: " + e.getMessage());
        }
        // Redirect back with params
        return "redirect:/diem";
    }

    private void reloadDropdowns(JdbcTemplate jdbc, String nhomQuyen, String maKhoa, ModelMap model) {
        model.addAttribute("dsmh", jdbc.queryForList("SELECT MAMH, TENMH FROM MONHOC ORDER BY TENMH"));
        if ("PGV".equals(nhomQuyen)) {
            model.addAttribute("dsNienKhoa", jdbc.queryForList("SELECT DISTINCT NIENKHOA FROM LOPTINCHI ORDER BY NIENKHOA DESC"));
        } else {
            model.addAttribute("dsNienKhoa", jdbc.queryForList("SELECT DISTINCT NIENKHOA FROM LOPTINCHI WHERE MAKHOA=? ORDER BY NIENKHOA DESC", maKhoa));
        }
    }

    private Integer parseIntOrNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return null; }
    }

    private Double parseDoubleOrNull(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return Double.parseDouble(s.trim()); } catch (NumberFormatException e) { return null; }
    }
}
