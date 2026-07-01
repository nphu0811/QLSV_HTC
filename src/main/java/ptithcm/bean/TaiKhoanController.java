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
        if (!"PGV".equals(nhomQuyen) && !"KHOA".equals(nhomQuyen)) {
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
        if (!"PGV".equals(sessionQuyen) && !"KHOA".equals(sessionQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

        if ("KHOA".equals(sessionQuyen)) {
            if (!"KHOA".equals(nhomQuyen.trim())) {
                ra.addFlashAttribute("error", "Lỗi: Tài khoản thuộc nhóm KHOA chỉ được tạo/sửa tài khoản thuộc nhóm KHOA.");
                return "redirect:/taikhoan";
            }
            try {
                Integer count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM sys.database_role_members RM " +
                    "JOIN sys.database_principals R ON RM.role_principal_id = R.principal_id " +
                    "JOIN sys.database_principals U ON RM.member_principal_id = U.principal_id " +
                    "WHERE R.name = 'PGV' AND U.name = ?", Integer.class, magv.trim());
                if (count != null && count > 0) {
                    ra.addFlashAttribute("error", "Lỗi: Nhóm KHOA không được sửa tài khoản thuộc nhóm PGV.");
                    return "redirect:/taikhoan";
                }
            } catch (Exception e) {
                // Ignore, let stored procedure handle it
            }
        }

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
        if (!"PGV".equals(sessionQuyen) && !"KHOA".equals(sessionQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

        if ("KHOA".equals(sessionQuyen)) {
            try {
                Integer count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM sys.database_role_members RM " +
                    "JOIN sys.database_principals R ON RM.role_principal_id = R.principal_id " +
                    "JOIN sys.database_principals U ON RM.member_principal_id = U.principal_id " +
                    "WHERE R.name = 'PGV' AND U.name = ?", Integer.class, magv.trim());
                if (count != null && count > 0) {
                    ra.addFlashAttribute("error", "Lỗi: Nhóm KHOA không được xóa tài khoản thuộc nhóm PGV.");
                    return "redirect:/taikhoan";
                }
            } catch (Exception e) {
                // Ignore, let stored procedure handle it
            }
        }

        try {
            StoredProcedure.update(jdbc, "SP_XoaTaiKhoanTheoGiangVien", magv.trim());
            ra.addFlashAttribute("success", "Xóa tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/taikhoan";
    }
}
