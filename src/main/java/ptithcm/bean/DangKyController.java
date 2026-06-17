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
 * Đăng ký lớp tín chỉ - SV only
 */
@Controller
@RequestMapping("/dangky")
public class DangKyController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String show(HttpSession session, ModelMap model) {
        if (!"SV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        String masv = (String) session.getAttribute("masv");
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

        // Thông tin SV
        List<Map<String, Object>> svInfo = jdbc.queryForList(
                "SELECT SV.MASV, SV.HO, SV.TEN, SV.MALOP, L.TENLOP " +
                "FROM SINHVIEN SV JOIN LOP L ON SV.MALOP=L.MALOP WHERE SV.MASV=?", masv);
        if (!svInfo.isEmpty()) {
            model.addAttribute("svInfo", svInfo.get(0));
        }

        // DS đã đăng ký
        List<Map<String, Object>> daDangKy = jdbc.queryForList(
                "SELECT DK.MALTC, MH.MAMH, MH.TENMH, LTC.NHOM, LTC.NIENKHOA, LTC.HOCKY, " +
                "GV.HO + ' ' + GV.TEN AS HOTENGV, DK.HUYDANGKY " +
                "FROM DANGKY DK " +
                "JOIN LOPTINCHI LTC ON DK.MALTC=LTC.MALTC " +
                "JOIN MONHOC MH ON LTC.MAMH=MH.MAMH " +
                "JOIN GIANGVIEN GV ON LTC.MAGV=GV.MAGV " +
                "WHERE DK.MASV=? AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL) " +
                "ORDER BY LTC.NIENKHOA DESC, LTC.HOCKY, MH.TENMH", masv);
        model.addAttribute("daDangKy", daDangKy);
        return "dangky";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestParam String nienkhoa,
                         @RequestParam int hocky,
                         HttpSession session, ModelMap model) {
        if (!"SV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        String masv = (String) session.getAttribute("masv");
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

        // Thông tin SV
        List<Map<String, Object>> svInfo = jdbc.queryForList(
                "SELECT SV.MASV, SV.HO, SV.TEN, SV.MALOP, L.TENLOP " +
                "FROM SINHVIEN SV JOIN LOP L ON SV.MALOP=L.MALOP WHERE SV.MASV=?", masv);
        if (!svInfo.isEmpty()) {
            model.addAttribute("svInfo", svInfo.get(0));
        }

        // DS lớp tín chỉ chưa hủy
        List<Map<String, Object>> dsltc = jdbc.queryForList(
                "SELECT LTC.MALTC, MH.MAMH, MH.TENMH, LTC.NHOM, " +
                "GV.HO + ' ' + GV.TEN AS HOTENGV, " +
                "(SELECT COUNT(*) FROM DANGKY DK WHERE DK.MALTC=LTC.MALTC AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL)) AS SOSVDK " +
                "FROM LOPTINCHI LTC " +
                "JOIN MONHOC MH ON LTC.MAMH=MH.MAMH " +
                "JOIN GIANGVIEN GV ON LTC.MAGV=GV.MAGV " +
                "WHERE LTC.NIENKHOA=? AND LTC.HOCKY=? AND LTC.HUYLOP=0 " +
                "ORDER BY MH.TENMH, LTC.NHOM",
                nienkhoa.trim(), hocky);
        model.addAttribute("dsltc", dsltc);
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);

        // DS đã đăng ký
        List<Map<String, Object>> daDangKy = jdbc.queryForList(
                "SELECT DK.MALTC FROM DANGKY DK WHERE DK.MASV=? AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL)", masv);
        model.addAttribute("daDangKy", daDangKy);
        return "dangky";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam int maltc,
                           HttpSession session, RedirectAttributes ra) {
        if (!"SV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        String masv = (String) session.getAttribute("masv");
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        try {
            // Kiểm tra đã đăng ký chưa
            int count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM DANGKY WHERE MALTC=? AND MASV=?",
                    Integer.class, maltc, masv);
            if (count > 0) {
                // Nếu đã hủy thì mở lại
                jdbc.update("UPDATE DANGKY SET HUYDANGKY=0 WHERE MALTC=? AND MASV=?",
                        maltc, masv);
            } else {
                jdbc.update("INSERT INTO DANGKY (MALTC, MASV, HUYDANGKY) VALUES (?,?,0)",
                        maltc, masv);
            }
            ra.addFlashAttribute("success", "Đăng ký thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi đăng ký: " + e.getMessage());
        }
        return "redirect:/dangky";
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public String cancel(@RequestParam int maltc,
                         HttpSession session, RedirectAttributes ra) {
        if (!"SV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        String masv = (String) session.getAttribute("masv");
        try {
            connHelper.getJdbcTemplate(session)
                    .update("UPDATE DANGKY SET HUYDANGKY=1 WHERE MALTC=? AND MASV=?",
                            maltc, masv);
            ra.addFlashAttribute("success", "Hủy đăng ký thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/dangky";
    }
}
