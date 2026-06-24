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
 * Đăng ký lớp tín chỉ - SV only.
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

        List<Map<String, Object>> svInfo = StoredProcedure.query(jdbc,
                "SP_ThongTinSinhVienDangKy", masv);
        if (!svInfo.isEmpty()) {
            model.addAttribute("svInfo", svInfo.get(0));
        }

        List<Map<String, Object>> daDangKy = StoredProcedure.query(jdbc,
                "SP_DanhSachLopTinChiDaDangKy", masv);
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

        List<Map<String, Object>> svInfo = StoredProcedure.query(jdbc,
                "SP_ThongTinSinhVienDangKy", masv);
        if (!svInfo.isEmpty()) {
            model.addAttribute("svInfo", svInfo.get(0));
        }

        List<Map<String, Object>> dsltc = StoredProcedure.query(jdbc,
                "SP_DanhSachLopTinChiCoTheDangKy", nienkhoa.trim(), hocky);
        model.addAttribute("dsltc", dsltc);
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);

        List<Map<String, Object>> daDangKy = StoredProcedure.query(jdbc,
                "SP_DanhSachLopTinChiDaDangKy", masv);
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
            StoredProcedure.update(jdbc, "SP_DangKyLopTinChi", maltc, masv);
            ra.addFlashAttribute("success", "Đăng ký thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi đăng ký: " + StoredProcedure.getErrorMessage(e));
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
            StoredProcedure.update(connHelper.getJdbcTemplate(session),
                    "SP_HuyDangKyLopTinChi", maltc, masv);
            ra.addFlashAttribute("success", "Hủy đăng ký thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/dangky";
    }
}
