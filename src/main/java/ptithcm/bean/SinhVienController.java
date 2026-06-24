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

@Controller
@RequestMapping("/sinhvien")
public class SinhVienController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String show(@RequestParam(value = "malop", required = false) String malop,
                       HttpSession session, ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(nhomQuyen) && !"KHOA".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        List<Map<String, Object>> dslop;
        
        // Danh sách lớp
        if ("PGV".equals(nhomQuyen)) {
             dslop = StoredProcedure.query(jdbc, "SP_DanhSachLopDropdown", (Object) null);
        } else {
             String maKhoa = (String) session.getAttribute("maKhoa");
             dslop = StoredProcedure.query(jdbc, "SP_DanhSachLopDropdown", maKhoa);
        }
        model.addAttribute("dslop", dslop);

        // Nếu chọn lớp -> load danh sách SV
        if (malop != null && !malop.isEmpty()) {
            List<Map<String, Object>> dssv = StoredProcedure.query(jdbc,
                    "SP_DanhSachSinhVienTheoLop", malop.trim());
            model.addAttribute("dssv", dssv);
            model.addAttribute("selectedLop", malop.trim());
        }
        return "sinhvien";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@RequestParam String action,
                       @RequestParam String masv,
                       @RequestParam String ho,
                       @RequestParam String ten,
                       @RequestParam String malop,
                       @RequestParam(required = false, defaultValue = "false") boolean phai,
                       @RequestParam(required = false) String ngaysinh,
                       @RequestParam(required = false) String diachi,
                       @RequestParam(required = false, defaultValue = "false") boolean danghihoc,
                       HttpSession session, RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String password = masv.trim();
        try {
            if ("add".equals(action)) {
                StoredProcedure.update(jdbc, "SP_ThemSinhVien",
                        masv.trim(), ho.trim(), ten.trim(), malop.trim(),
                        phai ? 1 : 0,
                        (ngaysinh != null && !ngaysinh.isEmpty()) ? ngaysinh : null,
                        (diachi != null && !diachi.isEmpty()) ? diachi.trim() : null,
                        danghihoc ? 1 : 0, password);
                ra.addFlashAttribute("success", "Thêm sinh viên thành công!");
            } else {
                StoredProcedure.update(jdbc, "SP_CapNhatSinhVien",
                        ho.trim(), ten.trim(), malop.trim(),
                        phai ? 1 : 0,
                        (ngaysinh != null && !ngaysinh.isEmpty()) ? ngaysinh : null,
                        (diachi != null && !diachi.isEmpty()) ? diachi.trim() : null,
                        danghihoc ? 1 : 0, password, masv.trim());
                ra.addFlashAttribute("success", "Cập nhật sinh viên thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/sinhvien?malop=" + malop.trim();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam String masv,
                         @RequestParam String malop,
                         HttpSession session, RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        try {
            StoredProcedure.update(connHelper.getJdbcTemplate(session),
                    "SP_XoaSinhVien", masv.trim());
            ra.addFlashAttribute("success", "Xóa sinh viên thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/sinhvien?malop=" + malop.trim();
    }
}
