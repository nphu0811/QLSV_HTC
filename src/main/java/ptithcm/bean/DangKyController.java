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
 * Đăng ký lớp tín chỉ - Cho phép cả SV và PGV. Chặn KHOA.
 */
@Controller
@RequestMapping("/dangky")
public class DangKyController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String show(@RequestParam(required = false) String masv,
                       HttpSession session, ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"SV".equals(nhomQuyen) && !"PGV".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String finalMasv = null;
        if ("SV".equals(nhomQuyen)) {
            finalMasv = (String) session.getAttribute("masv");
        } else { // PGV
            finalMasv = (masv != null && !masv.trim().isEmpty()) ? masv.trim() : null;
        }

        if (finalMasv != null) {
            List<Map<String, Object>> svInfo = StoredProcedure.query(jdbc,
                    "SP_ThongTinSinhVienDangKy", finalMasv);
            if (!svInfo.isEmpty()) {
                model.addAttribute("svInfo", svInfo.get(0));
                List<Map<String, Object>> daDangKy = StoredProcedure.query(jdbc,
                        "SP_DanhSachLopTinChiDaDangKy", finalMasv);
                model.addAttribute("daDangKy", daDangKy);
            } else {
                model.addAttribute("error", "Không tìm thấy sinh viên có mã: " + finalMasv);
            }
            model.addAttribute("masv", finalMasv);
        }
        return "dangky";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String search(@RequestParam String nienkhoa,
                         @RequestParam int hocky,
                         @RequestParam(required = false) String masv,
                         HttpSession session, ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"SV".equals(nhomQuyen) && !"PGV".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String finalMasv = null;
        if ("SV".equals(nhomQuyen)) {
            finalMasv = (String) session.getAttribute("masv");
        } else { // PGV
            finalMasv = (masv != null && !masv.trim().isEmpty()) ? masv.trim() : null;
        }

        if (finalMasv != null) {
            List<Map<String, Object>> svInfo = StoredProcedure.query(jdbc,
                    "SP_ThongTinSinhVienDangKy", finalMasv);
            if (!svInfo.isEmpty()) {
                model.addAttribute("svInfo", svInfo.get(0));
                List<Map<String, Object>> dsltc = StoredProcedure.query(jdbc,
                        "SP_DanhSachLopTinChiCoTheDangKy", nienkhoa.trim(), hocky);
                model.addAttribute("dsltc", dsltc);

                List<Map<String, Object>> daDangKy = StoredProcedure.query(jdbc,
                        "SP_DanhSachLopTinChiDaDangKy", finalMasv);
                model.addAttribute("daDangKy", daDangKy);
            } else {
                model.addAttribute("error", "Không tìm thấy sinh viên có mã: " + finalMasv);
            }
            model.addAttribute("masv", finalMasv);
        }
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);
        return "dangky";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam int maltc,
                           @RequestParam(required = false) String masv,
                           HttpSession session, RedirectAttributes ra) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"SV".equals(nhomQuyen) && !"PGV".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        
        String finalMasv = null;
        if ("SV".equals(nhomQuyen)) {
            finalMasv = (String) session.getAttribute("masv");
        } else { // PGV
            finalMasv = (masv != null && !masv.trim().isEmpty()) ? masv.trim() : null;
        }

        if (finalMasv == null || finalMasv.isEmpty()) {
            ra.addFlashAttribute("error", "Mã sinh viên không hợp lệ.");
            return "redirect:/dangky";
        }

        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        try {
            StoredProcedure.update(jdbc, "SP_DangKyLopTinChi", maltc, finalMasv);
            ra.addFlashAttribute("success", "Đăng ký thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi đăng ký: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/dangky?masv=" + finalMasv;
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public String cancel(@RequestParam int maltc,
                         @RequestParam(required = false) String masv,
                         HttpSession session, RedirectAttributes ra) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"SV".equals(nhomQuyen) && !"PGV".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        
        String finalMasv = null;
        if ("SV".equals(nhomQuyen)) {
            finalMasv = (String) session.getAttribute("masv");
        } else { // PGV
            finalMasv = (masv != null && !masv.trim().isEmpty()) ? masv.trim() : null;
        }

        if (finalMasv == null || finalMasv.isEmpty()) {
            ra.addFlashAttribute("error", "Mã sinh viên không hợp lệ.");
            return "redirect:/dangky";
        }

        try {
            StoredProcedure.update(connHelper.getJdbcTemplate(session),
                    "SP_HuyDangKyLopTinChi", maltc, finalMasv);
            ra.addFlashAttribute("success", "Hủy đăng ký thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/dangky?masv=" + finalMasv;
    }
}
