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
@RequestMapping("/loptinchi")
public class LopTinChiController {

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
        List<Map<String, Object>> dsltc;
        List<Map<String, Object>> dsgv;
        if ("PGV".equals(nhomQuyen)) {
             dsltc = StoredProcedure.query(jdbc, "SP_DanhSachLopTinChi", (Object) null);
             dsgv = StoredProcedure.query(jdbc, "SP_DanhSachGiangVienDropdown", (Object) null);
        } else {
             dsltc = StoredProcedure.query(jdbc, "SP_DanhSachLopTinChi", maKhoa);
             dsgv = StoredProcedure.query(jdbc, "SP_DanhSachGiangVienDropdown", maKhoa);
        }
        model.addAttribute("dsltc", dsltc);
        model.addAttribute("dsmh", StoredProcedure.query(jdbc, "SP_DanhSachMonHoc"));
        model.addAttribute("dsgv", dsgv);
        model.addAttribute("khoaList", StoredProcedure.query(jdbc, "SP_DanhSachKhoa"));
        return "loptinchi";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@RequestParam String action,
                       @RequestParam(required = false) Integer maltc,
                       @RequestParam String nienkhoa,
                       @RequestParam int hocky,
                       @RequestParam String mamh,
                       @RequestParam int nhom,
                       @RequestParam String magv,
                       @RequestParam int sosvtoithieu,
                       @RequestParam(required = false) String maKhoa,
                       HttpSession session, RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String kh = (maKhoa != null && !maKhoa.isEmpty()) ? maKhoa.trim() : (String) session.getAttribute("maKhoa");
        
        // Kiểm tra niên khóa (không nhập quá khứ)
        try {
            String nk = nienkhoa.trim();
            if (!nk.matches("^\\d{4}-\\d{4}$")) {
                ra.addFlashAttribute("error", "Niên khóa phải có định dạng YYYY-YYYY (ví dụ: 2026-2027).");
                return "redirect:/loptinchi";
            }
            String[] years = nk.split("-");
            int startYear = Integer.parseInt(years[0]);
            int endYear = Integer.parseInt(years[1]);
            if (endYear != startYear + 1) {
                ra.addFlashAttribute("error", "Niên khóa không hợp lệ: năm sau phải bằng năm trước + 1.");
                return "redirect:/loptinchi";
            }
            
            int currentYear = java.time.Year.now().getValue();
            boolean isNewOrChanged = true;
            if ("update".equals(action) && maltc != null) {
                try {
                    String oldNienKhoa = jdbc.queryForObject("SELECT NIENKHOA FROM dbo.LOPTINCHI WHERE MALTC = ?", String.class, maltc);
                    if (oldNienKhoa != null && oldNienKhoa.trim().equals(nk)) {
                        isNewOrChanged = false;
                    }
                } catch (Exception e) {
                    // Bỏ qua
                }
            }
            
            if (isNewOrChanged && endYear < currentYear) {
                ra.addFlashAttribute("error", "Không được mở niên khóa đã kết thúc trong quá khứ (trước năm " + currentYear + ").");
                return "redirect:/loptinchi";
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Định dạng niên khóa không hợp lệ.");
            return "redirect:/loptinchi";
        }

        try {
            if ("add".equals(action)) {
                StoredProcedure.update(jdbc, "SP_ThemLopTinChi",
                        nienkhoa.trim(), hocky, mamh.trim(), nhom, magv.trim(), kh, sosvtoithieu);
                ra.addFlashAttribute("success", "Mở lớp tín chỉ thành công!");
            } else if (maltc != null) {
                StoredProcedure.update(jdbc, "SP_CapNhatLopTinChi",
                        nienkhoa.trim(), hocky, mamh.trim(), nhom, magv.trim(), sosvtoithieu, maltc);
                ra.addFlashAttribute("success", "Cập nhật lớp tín chỉ thành công!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/loptinchi";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@RequestParam int maltc,
                         HttpSession session, RedirectAttributes ra) {
        if (!"PGV".equals(session.getAttribute("nhomQuyen"))) {
            return "redirect:/home";
        }
        try {
            StoredProcedure.update(connHelper.getJdbcTemplate(session),
                    "SP_XoaLopTinChi", maltc);
            ra.addFlashAttribute("success", "Xóa lớp tín chỉ thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa: " + StoredProcedure.getErrorMessage(e));
        }
        return "redirect:/loptinchi";
    }
}
