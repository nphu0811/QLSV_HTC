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
@RequestMapping("/giangvien")
public class GiangVienController {

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
        List<Map<String, Object>> dsgv;
        
        if ("PGV".equals(nhomQuyen)) {
            dsgv = StoredProcedure.query(jdbc, "SP_DanhSachGiangVien", (Object) null);
        } else {
            dsgv = StoredProcedure.query(jdbc, "SP_DanhSachGiangVien", maKhoa);
        }
        
        model.addAttribute("dsgv", dsgv);
        model.addAttribute("khoaList", StoredProcedure.query(jdbc, "SP_DanhSachKhoa"));
        return "giangvien";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@RequestParam String action,
                       @RequestParam String magv,
                       @RequestParam String ho,
                       @RequestParam String ten,
                       @RequestParam String hocvi,
                       @RequestParam String hocham,
                       @RequestParam String chuyenmon,
                       @RequestParam String maKhoa,
                       HttpSession session, RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        try {
            if ("add".equals(action)) {
                StoredProcedure.update(jdbc, "SP_ThemGiangVien",
                        magv.trim(), ho.trim(), ten.trim(), hocvi.trim(), hocham.trim(), chuyenmon.trim(), maKhoa.trim());
                ra.addFlashAttribute("success", "Thêm giảng viên thành công!");
            } else {
                StoredProcedure.update(jdbc, "SP_CapNhatGiangVien",
                        ho.trim(), ten.trim(), hocvi.trim(), hocham.trim(), chuyenmon.trim(), maKhoa.trim(), magv.trim());
                ra.addFlashAttribute("success", "Cập nhật giảng viên thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/giangvien";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam String magv,
                         HttpSession session, RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        try {
            StoredProcedure.update(connHelper.getJdbcTemplate(session),
                    "SP_XoaGiangVien", magv.trim());
            ra.addFlashAttribute("success", "Xóa giảng viên thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/giangvien";
    }
}
