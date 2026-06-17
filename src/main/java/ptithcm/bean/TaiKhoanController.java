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
 * Quản trị tài khoản - PGV only
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

        List<Map<String, Object>> dsgv = jdbc.queryForList(
                "SELECT G.MAGV, G.HO + ' ' + G.TEN AS HOTEN, T.Login, T.MatKhau, T.NhomQuyen, T.MAKHOA " +
                "FROM GIANGVIEN G LEFT JOIN TaiKhoan T ON G.MAGV = T.MAGV " +
                "ORDER BY G.TEN, G.HO");
        model.addAttribute("dsgv", dsgv);
        
        List<Map<String, Object>> khoaList = jdbc.queryForList("SELECT MAKHOA, TENKHOA FROM KHOA ORDER BY MAKHOA");
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
        if ("KHOA".equals(sessionQuyen) && "PGV".equals(nhomQuyen.trim())) {
            ra.addFlashAttribute("error", "Khoa không được cấp tài khoản nhóm PGV!");
            return "redirect:/taikhoan";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String mkhoa = "PGV".equals(nhomQuyen.trim()) ? null : (maKhoa != null && !maKhoa.trim().isEmpty() ? maKhoa.trim() : null);
        try {
            Long count = jdbc.queryForObject("SELECT COUNT(*) FROM TaiKhoan WHERE MAGV=?", Long.class, magv.trim());
            if (count == 0) {
                jdbc.update("INSERT INTO TaiKhoan (Login, MatKhau, NhomQuyen, MAGV, MAKHOA, TrangThai, NgayTao) " +
                            "VALUES (?,?,?,?,?, 'Active',GETDATE())",
                        login.trim(), matkhau, nhomQuyen.trim(), magv.trim(), mkhoa);
                ra.addFlashAttribute("success", "Tạo tài khoản thành công!");
            } else {
                jdbc.update("UPDATE TaiKhoan SET Login=?, MatKhau=?, NhomQuyen=?, MAKHOA=? WHERE MAGV=?",
                        login.trim(), matkhau, nhomQuyen.trim(), mkhoa, magv.trim());
                ra.addFlashAttribute("success", "Cập nhật tài khoản thay đổi thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
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
                String role = jdbc.queryForObject("SELECT NhomQuyen FROM TaiKhoan WHERE MAGV=?", String.class, magv.trim());
                if ("PGV".equals(role)) {
                    ra.addFlashAttribute("error", "Khoa không có quyền xóa tài khoản nhóm PGV!");
                    return "redirect:/taikhoan";
                }
            } catch (Exception e) {}
        }
        try {
            jdbc.update("DELETE FROM TaiKhoan WHERE MAGV=?", magv.trim());
            ra.addFlashAttribute("success", "Xóa tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/taikhoan";
    }
}
