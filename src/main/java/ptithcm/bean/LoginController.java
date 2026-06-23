package ptithcm.bean;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLogin() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String doLogin(@RequestParam String loginType,
                          @RequestParam String username,
                          @RequestParam(required = false) String password,
                          HttpSession session,
                          ModelMap model) {
        JdbcTemplate jdbc = connHelper.getDefaultJdbcTemplate();
        try {
            if ("SV".equals(loginType)) {
                // === SINH VIÊN: kiểm tra bảng SINHVIEN ===
                List<Map<String, Object>> rows = StoredProcedure.query(jdbc,
                        "SP_LoginSinhVien", username.trim());
                if (rows.isEmpty()) {
                    model.addAttribute("error", "Mã sinh viên hoặc mật khẩu không đúng!");
                    return "login";
                }
                Map<String, Object> sv = rows.get(0);
                String masv = sv.get("MASV").toString().trim();
                String ho = sv.get("HO").toString().trim();
                String ten = sv.get("TEN").toString().trim();
                String malop = sv.get("MALOP").toString().trim();

                // Lấy mã khoa từ lớp
                String maKhoa = StoredProcedure.object(jdbc,
                        "SP_LayKhoaTheoLop", String.class, malop).trim();

                session.setAttribute("nhomQuyen", "SV");
                session.setAttribute("masv", masv);
                session.setAttribute("hoTen", ho + " " + ten);
                session.setAttribute("displayName", ho + " " + ten);
                session.setAttribute("maKhoa", maKhoa);
                session.setAttribute("maLop", malop);
                // SQL Server login cho SV
                session.setAttribute("sqlLogin", "sv");
                session.setAttribute("sqlPassword", "sv123");

            } else {
                // === GIẢNG VIÊN / PGV / KHOA: kiểm tra bảng TaiKhoan ===
                List<Map<String, Object>> rows = StoredProcedure.query(jdbc,
                        "SP_LoginTaiKhoan", username.trim(), password);
                if (rows.isEmpty()) {
                    model.addAttribute("error", "Login hoặc mật khẩu không đúng!");
                    return "login";
                }
                Map<String, Object> tk = rows.get(0);
                String nhomQuyen = tk.get("NhomQuyen").toString().trim();
                session.setAttribute("nhomQuyen", nhomQuyen);
                session.setAttribute("displayName", username.trim());
                session.setAttribute("loginName", username.trim());

                if ("PGV".equals(nhomQuyen)) {
                    session.setAttribute("sqlLogin", "pgv_admin");
                    session.setAttribute("sqlPassword", "123456");
                    // PGV: lấy danh sách khoa, mặc định khoa đầu tiên
                    List<Map<String, Object>> khoaList = StoredProcedure.query(jdbc,
                            "SP_DanhSachKhoa");
                    session.setAttribute("khoaList", khoaList);
                    if (!khoaList.isEmpty()) {
                        session.setAttribute("maKhoa",
                                khoaList.get(0).get("MAKHOA").toString().trim());
                    }
                } else if ("KHOA".equals(nhomQuyen)) {
                    Object mk = tk.get("MAKHOA");
                    String maKhoa = (mk != null) ? mk.toString().trim() : "";
                    session.setAttribute("maKhoa", maKhoa);
                    // SQL login theo khoa
                    if ("CNTT".equals(maKhoa)) {
                        session.setAttribute("sqlLogin", "khoa_cntt");
                        session.setAttribute("sqlPassword", "khoa123");
                    } else if ("VT".equals(maKhoa)) {
                        session.setAttribute("sqlLogin", "khoa_vt");
                        session.setAttribute("sqlPassword", "khoa456");
                    } else {
                        session.setAttribute("sqlLogin", "sa");
                        session.setAttribute("sqlPassword", "123");
                    }
                }
            }
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi đăng nhập: " + e.getMessage());
            return "login";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
