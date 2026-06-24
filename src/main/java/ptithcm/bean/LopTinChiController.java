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
            int currentYear = java.time.Year.now().getValue();
            int startYear = Integer.parseInt(nienkhoa.split("-")[0].trim());
            if (startYear < currentYear - 1) { // Cho phép trễ tối đa 1 năm (ví dụ đang 2024 có thể nhập 2023-2024)
                ra.addFlashAttribute("error", "Không được nhập lớp tín chỉ cho niên khóa đã qua (" + nienkhoa + ")");
                return "redirect:/loptinchi";
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Định dạng niên khóa không hợp lệ (VD: 2021-2022)");
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
