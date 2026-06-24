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
        List<Map<String, Object>> dsmh = StoredProcedure.query(jdbc, "SP_DanhSachMonHoc");
        List<Map<String, Object>> dsNienKhoa;
        if ("PGV".equals(nhomQuyen)) {
            dsNienKhoa = StoredProcedure.query(jdbc, "SP_DanhSachNienKhoa", (Object) null);
        } else {
            dsNienKhoa = StoredProcedure.query(jdbc, "SP_DanhSachNienKhoa", maKhoa);
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
            ltcRows = StoredProcedure.query(jdbc, "SP_TimLopTinChi",
                    nienkhoa.trim(), hocky, mamh.trim(), nhom, null);
        } else {
            ltcRows = StoredProcedure.query(jdbc, "SP_TimLopTinChi",
                    nienkhoa.trim(), hocky, mamh.trim(), nhom, maKhoa);
        }
        
        if (ltcRows.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy lớp tín chỉ!");
            // Reload dropdown data
            reloadDropdowns(jdbc, nhomQuyen, maKhoa, model);
            return "diem";
        }
        int maltc = ((Number) ltcRows.get(0).get("MALTC")).intValue();

        // Lấy tên môn
        String tenmh = StoredProcedure.object(jdbc, "SP_LayTenMonHoc", String.class, mamh.trim());

        // Load danh sách SV đã đăng ký
        List<Map<String, Object>> dssv = StoredProcedure.query(jdbc,
                "SP_DanhSachSinhVienNhapDiem", maltc);

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
                StoredProcedure.update(jdbc, "SP_CapNhatDiem",
                        diemCC, diemGK, diemCK, maltc, masvArr[i].trim());
            }
            ra.addFlashAttribute("success", "Ghi điểm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi ghi điểm: " + StoredProcedure.getErrorMessage(e));
        }
        // Redirect back with params
        return "redirect:/diem";
    }

    private void reloadDropdowns(JdbcTemplate jdbc, String nhomQuyen, String maKhoa, ModelMap model) {
        model.addAttribute("dsmh", StoredProcedure.query(jdbc, "SP_DanhSachMonHoc"));
        if ("PGV".equals(nhomQuyen)) {
            model.addAttribute("dsNienKhoa", StoredProcedure.query(jdbc, "SP_DanhSachNienKhoa", (Object) null));
        } else {
            model.addAttribute("dsNienKhoa", StoredProcedure.query(jdbc, "SP_DanhSachNienKhoa", maKhoa));
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
