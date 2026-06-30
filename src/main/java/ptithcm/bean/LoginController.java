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

                session.setAttribute("nhomQuyen", "SV");
                session.setAttribute("masv", masv);
                session.setAttribute("hoTen", ho + " " + ten);
                session.setAttribute("displayName", ho + " " + ten);
                session.setAttribute("maLop", malop);
                // SQL Server login cho SV
                session.setAttribute("sqlLogin", "sv");
                session.setAttribute("sqlPassword", "sv123");

                // Lấy mã khoa từ lớp bằng connection của SV
                JdbcTemplate svJdbc = connHelper.getJdbcTemplate(session);
                String maKhoa = StoredProcedure.object(svJdbc,
                        "SP_LayKhoaTheoLop", String.class, malop).trim();
                session.setAttribute("maKhoa", maKhoa);

            } else {
                String loginName = username.trim();
                String loginPassword = password != null ? password : "";
                JdbcTemplate loginJdbc = connHelper.getJdbcTemplate(loginName, loginPassword);
                List<Map<String, Object>> rows = StoredProcedure.query(loginJdbc,
                        "SP_ThongTinDangNhapSql");
                if (rows.isEmpty() || rows.get(0).get("NhomQuyen") == null) {
                    model.addAttribute("error", "Login không thuộc nhóm quyền PGV/KHOA!");
                    return "login";
                }

                Map<String, Object> info = rows.get(0);
                String nhomQuyen = info.get("NhomQuyen").toString().trim();
                if (!"PGV".equals(nhomQuyen) && !"KHOA".equals(nhomQuyen)) {
                    model.addAttribute("error", "Tài khoản này không dùng cho màn hình giảng viên!");
                    return "login";
                }

                session.setAttribute("nhomQuyen", nhomQuyen);
                session.setAttribute("displayName", loginName);
                session.setAttribute("loginName", loginName);
                session.setAttribute("sqlLogin", loginName);
                session.setAttribute("sqlPassword", loginPassword);

                Object magv = info.get("MAGV");
                if (magv != null) {
                    session.setAttribute("magv", magv.toString().trim());
                }

                if ("PGV".equals(nhomQuyen)) {
                    List<Map<String, Object>> khoaList = StoredProcedure.query(loginJdbc,
                            "SP_DanhSachKhoa");
                    session.setAttribute("khoaList", khoaList);
                    if (!khoaList.isEmpty()) {
                        session.setAttribute("maKhoa",
                                khoaList.get(0).get("MAKHOA").toString().trim());
                    }
                } else if ("KHOA".equals(nhomQuyen)) {
                    Object mk = info.get("MAKHOA");
                    String maKhoa = (mk != null) ? mk.toString().trim() : "";
                    if (maKhoa.isEmpty()) {
                        model.addAttribute("error", "SQL login KHOA chưa xác định được mã khoa!");
                        return "login";
                    }
                    session.setAttribute("maKhoa", maKhoa);
                }
            }
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi đăng nhập: " + StoredProcedure.getErrorMessage(e));
            return "login";
        }
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
