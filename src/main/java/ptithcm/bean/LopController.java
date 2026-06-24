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
@RequestMapping("/lop")
public class LopController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String showLop(HttpSession session, ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(nhomQuyen) && !"KHOA".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        loadData(session, model);
        return "lop";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveLop(@RequestParam String action,
                          @RequestParam String maLop,
                          @RequestParam String tenLop,
                          @RequestParam String khoaHoc,
                          @RequestParam String maKhoa,
                          HttpSession session,
                          RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        try {
            if ("add".equals(action)) {
                StoredProcedure.update(jdbc, "SP_ThemLop",
                        maLop.trim(), tenLop.trim(), khoaHoc.trim(), maKhoa.trim());
                ra.addFlashAttribute("success", "Thêm lớp thành công!");
            } else {
                StoredProcedure.update(jdbc, "SP_CapNhatLop",
                        tenLop.trim(), khoaHoc.trim(), maKhoa.trim(), maLop.trim());
                ra.addFlashAttribute("success", "Cập nhật lớp thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/lop";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteLop(@RequestParam String maLop,
                            HttpSession session, RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        try {
            StoredProcedure.update(connHelper.getJdbcTemplate(session),
                    "SP_XoaLop", maLop.trim());
            ra.addFlashAttribute("success", "Xóa lớp thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/lop";
    }

    private void loadData(HttpSession session, ModelMap model) {
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        List<Map<String, Object>> dslop;
        if ("PGV".equals(nhomQuyen)) {
            dslop = StoredProcedure.query(jdbc, "SP_DanhSachLop", (Object) null);
        } else {
            String maKhoa = (String) session.getAttribute("maKhoa");
            dslop = StoredProcedure.query(jdbc, "SP_DanhSachLop", maKhoa);
        }
        model.addAttribute("dslop", dslop);
        List<Map<String, Object>> khoaList = StoredProcedure.query(jdbc, "SP_DanhSachKhoa");
        model.addAttribute("khoaList", khoaList);
    }
}
