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
        String userKhoa = (String) session.getAttribute("maKhoa");

        // Sử dụng SP mới có trả về MAKHOA thực tế của giảng viên để lọc
        List<Map<String, Object>> dsgv = StoredProcedure.query(jdbc,
                "SP_DanhSachTaiKhoanGiangVienCoKhoa");
        if ("KHOA".equals(nhomQuyen)) {
            dsgv.removeIf(row -> {
                Object gvKhoa = row.get("MAKHOA");
                return gvKhoa == null || !userKhoa.equals(gvKhoa.toString().trim());
            });
        }
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
        String userKhoa = (String) session.getAttribute("maKhoa");

        String mkhoa = null;
        if ("KHOA".equals(sessionQuyen)) {
            // Validate giảng viên thuộc khoa mình
            try {
                String gvKhoa = StoredProcedure.object(jdbc, "SP_LayKhoaTheoGiangVien", String.class, magv.trim()).trim();
                if (!userKhoa.equals(gvKhoa)) {
                    ra.addFlashAttribute("error", "Bạn không được phép tạo/sửa tài khoản cho giảng viên khoa khác!");
                    return "redirect:/taikhoan";
                }
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Lỗi xác thực giảng viên: " + StoredProcedure.getErrorMessage(e));
                return "redirect:/taikhoan";
            }
            // Nhóm quyền tạo ra chỉ được là KHOA
            if (!"KHOA".equals(nhomQuyen.trim())) {
                ra.addFlashAttribute("error", "Khoa chỉ được tạo tài khoản nhóm KHOA!");
                return "redirect:/taikhoan";
            }
            mkhoa = userKhoa;
        } else { // PGV
            mkhoa = "PGV".equals(nhomQuyen.trim()) ? null
                    : (maKhoa != null && !maKhoa.trim().isEmpty() ? maKhoa.trim() : null);
        }

        try {
            StoredProcedure.update(jdbc, "SP_LuuTaiKhoanGiangVien",
                     magv.trim(), login.trim(), matkhau, nhomQuyen.trim(), mkhoa);
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
        String userKhoa = (String) session.getAttribute("maKhoa");

        if ("KHOA".equals(sessionQuyen)) {
            // Validate giảng viên thuộc khoa mình
            try {
                String gvKhoa = StoredProcedure.object(jdbc, "SP_LayKhoaTheoGiangVien", String.class, magv.trim()).trim();
                if (!userKhoa.equals(gvKhoa)) {
                    ra.addFlashAttribute("error", "Bạn không được phép xóa tài khoản của giảng viên khoa khác!");
                    return "redirect:/taikhoan";
                }
            } catch (Exception e) {
                ra.addFlashAttribute("error", "Lỗi xác thực giảng viên: " + StoredProcedure.getErrorMessage(e));
                return "redirect:/taikhoan";
            }

            try {
                String role = StoredProcedure.object(jdbc, "SP_LayNhomQuyenTheoGiangVien",
                        String.class, magv.trim());
                if ("PGV".equals(role)) {
                    ra.addFlashAttribute("error", "Khoa không có quyền xóa tài khoản nhóm PGV!");
                    return "redirect:/taikhoan";
                }
            } catch (Exception e) {
                // Không tìm thấy tài khoản, tiếp tục xóa an toàn
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
