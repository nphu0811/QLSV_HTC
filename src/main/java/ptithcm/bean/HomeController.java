package ptithcm.bean;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(value = {"/", "/home"})
    public String home(HttpSession session, ModelMap model) {
        if (session.getAttribute("nhomQuyen") == null) {
            return "redirect:/login";
        }
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if ("PGV".equals(nhomQuyen)) {
            try {
                List<Map<String, Object>> khoaList = StoredProcedure.query(
                        connHelper.getJdbcTemplate(session), "SP_DanhSachKhoa");
                session.setAttribute("khoaList", khoaList);
            } catch (Exception e) {
                // ignore
            }
        }

        // Lấy tên khoa hiện tại
        String maKhoa = (String) session.getAttribute("maKhoa");
        if (maKhoa != null) {
            try {
                String tenKhoa = StoredProcedure.object(connHelper.getJdbcTemplate(session),
                        "SP_LayTenKhoa", String.class, maKhoa.trim());
                model.addAttribute("tenKhoa", tenKhoa);
            } catch (Exception e) {
                model.addAttribute("tenKhoa", maKhoa);
            }
        }
        return "home";
    }

    @RequestMapping(value = "/change-khoa", method = RequestMethod.POST)
    public String changeKhoa(@RequestParam String maKhoa, HttpSession session) {
        session.setAttribute("maKhoa", maKhoa.trim());
        // Redirect back to referring page
        return "redirect:/home";
    }
}
