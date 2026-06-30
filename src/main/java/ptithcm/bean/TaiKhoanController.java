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
 * Quản trị tài khoản.
 */
@Controller
@RequestMapping("/taikhoan")
public class TaiKhoanController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String show(HttpSession session, ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

        List<Map<String, Object>> dsgv = StoredProcedure.query(jdbc,
                "SP_DanhSachTaiKhoanGiangVienCoKhoa");
        model.addAttribute("dsgv", dsgv);

        List<Map<String, Object>> khoaList = StoredProcedure.query(jdbc, "SP_DanhSachKhoa");
        model.addAttribute("khoaList", khoaList);
        return "taikhoan";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@RequestParam String magv,
                       @RequestParam String login,
                       @RequestParam String matkhau,
                       @RequestParam String nhomQuyen,
                       @RequestParam(required=false) String maKhoa,
                       HttpSession session, RedirectAttributes ra) {
        String sessionQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(sessionQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

        String mkhoa = "PGV".equals(nhomQuyen.trim()) ? null
                : (maKhoa != null && !maKhoa.trim().isEmpty() ? maKhoa.trim() : null);
        String loginName = (login != null && !login.trim().isEmpty()) ? login.trim() : magv.trim();

        try {
            StoredProcedure.update(jdbc, "SP_LuuTaiKhoanGiangVien",
                     magv.trim(), loginName, matkhau, nhomQuyen.trim(), mkhoa);
            ra.addFlashAttribute("success", "Lưu tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/taikhoan";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam String magv,
                         HttpSession session, RedirectAttributes ra) {
        String sessionQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(sessionQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

        try {
            StoredProcedure.update(jdbc, "SP_XoaTaiKhoanTheoGiangVien", magv.trim());
            ra.addFlashAttribute("success", "Xóa tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/taikhoan";
    }
}
