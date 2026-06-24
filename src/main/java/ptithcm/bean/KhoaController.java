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
@RequestMapping("/khoa")
public class KhoaController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String show(HttpSession session, ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(nhomQuyen) && !"KHOA".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        List<Map<String, Object>> dsKhoa = StoredProcedure.query(jdbc, "SP_DanhSachKhoa");
        model.addAttribute("dsKhoa", dsKhoa);
        return "khoa";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@RequestParam String action,
                       @RequestParam String maKhoa,
                       @RequestParam String tenKhoa,
                       HttpSession session, RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        try {
            if ("add".equals(action)) {
                StoredProcedure.update(jdbc, "SP_ThemKhoa", maKhoa.trim(), tenKhoa.trim());
                ra.addFlashAttribute("success", "Thêm khoa thành công!");
            } else {
                StoredProcedure.update(jdbc, "SP_CapNhatKhoa", tenKhoa.trim(), maKhoa.trim());
                ra.addFlashAttribute("success", "Cập nhật khoa thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/khoa";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam String maKhoa,
                         HttpSession session, RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        try {
            StoredProcedure.update(connHelper.getJdbcTemplate(session), "SP_XoaKhoa", maKhoa.trim());
            ra.addFlashAttribute("success", "Xóa khoa thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/khoa";
    }
}
