package ptithcm.bean;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

/**
 * Báo cáo - 5 loại.
 */
@Controller
@RequestMapping("/baocao")
public class BaoCaoController {

    @Autowired
    private ConnectionHelper connHelper;

    @RequestMapping(method = RequestMethod.GET)
    public String show(HttpSession session) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if (!"PGV".equals(nhomQuyen) && !"KHOA".equals(nhomQuyen) && !"SV".equals(nhomQuyen)) {
            return "redirect:/login";
        }
        return "baocao";
    }

    @RequestMapping(value = "/ds-ltc", method = RequestMethod.GET)
    public String formDSLTC() {
        return "baocao";
    }

    @RequestMapping(value = "/ds-ltc", method = RequestMethod.POST)
    public String reportDSLTC(@RequestParam String nienkhoa,
                              @RequestParam int hocky,
                              @RequestParam(required = false) String maKhoa,
                              HttpSession session,
                              ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if ("SV".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String userKhoa = (String) session.getAttribute("maKhoa");
        
        String selectedKhoa = null;
        if ("PGV".equals(nhomQuyen)) {
            selectedKhoa = (maKhoa != null && !maKhoa.trim().isEmpty()) ? maKhoa.trim() : null;
            if (selectedKhoa == null) {
                model.addAttribute("error", "Vui lòng chọn một khoa cụ thể!");
                return "baocao_report";
            }
        } else { // KHOA
            selectedKhoa = userKhoa;
        }

        List<Map<String, Object>> data = StoredProcedure.query(jdbc,
                "SP_BaoCaoDSLopTinChi", nienkhoa.trim(), hocky, selectedKhoa);

        String tenKhoaDisplay = getTenKhoa(jdbc, selectedKhoa).toUpperCase();

        model.addAttribute("tenKhoa", tenKhoaDisplay);
        model.addAttribute("reportType", "DS_LTC");
        model.addAttribute("data", data);
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);
        return "baocao_report";
    }

    @RequestMapping(value = "/ds-sv-dk", method = RequestMethod.POST)
    public String reportDSSVDK(@RequestParam String nienkhoa,
                               @RequestParam int hocky,
                               @RequestParam String mamh,
                               @RequestParam int nhom,
                               HttpSession session,
                               ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if ("SV".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String userKhoa = (String) session.getAttribute("maKhoa");
        String khoaFilter = "PGV".equals(nhomQuyen) ? null : userKhoa;

        List<Map<String, Object>> ltcRows = StoredProcedure.query(jdbc,
                "SP_TimLopTinChi", nienkhoa.trim(), hocky, mamh.trim(), nhom, khoaFilter);
        
        if (ltcRows.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy lớp tín chỉ hợp lệ hoặc bạn không có quyền xem lớp này!");
            return "baocao_report";
        }
        int maltc = ((Number) ltcRows.get(0).get("MALTC")).intValue();

        // Lấy tên khoa thực tế của lớp tín chỉ
        try {
            String ltcKhoa = jdbc.queryForObject("SELECT MAKHOA FROM dbo.LOPTINCHI WHERE MALTC = ?", String.class, maltc);
            String tenKhoaDisplay = getTenKhoa(jdbc, ltcKhoa).toUpperCase();
            model.addAttribute("tenKhoa", tenKhoaDisplay);
        } catch (Exception e) {
            model.addAttribute("tenKhoa", "KHOA");
        }

        List<Map<String, Object>> data = StoredProcedure.query(jdbc,
                "SP_BaoCaoDSSinhVienDangKy", maltc);

        model.addAttribute("reportType", "DS_SV_DK");
        model.addAttribute("data", data);
        model.addAttribute("tenmh", getTenMonHoc(jdbc, mamh.trim()));
        model.addAttribute("nhom", nhom);
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);
        return "baocao_report";
    }

    @RequestMapping(value = "/bang-diem", method = RequestMethod.POST)
    public String reportBangDiem(@RequestParam String nienkhoa,
                                 @RequestParam int hocky,
                                 @RequestParam String mamh,
                                 @RequestParam int nhom,
                                 HttpSession session,
                                 ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if ("SV".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String userKhoa = (String) session.getAttribute("maKhoa");
        String khoaFilter = "PGV".equals(nhomQuyen) ? null : userKhoa;

        List<Map<String, Object>> ltcRows = StoredProcedure.query(jdbc,
                "SP_TimLopTinChi", nienkhoa.trim(), hocky, mamh.trim(), nhom, khoaFilter);
        
        if (ltcRows.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy lớp tín chỉ hợp lệ hoặc bạn không có quyền xem bảng điểm lớp này!");
            return "baocao_report";
        }
        int maltc = ((Number) ltcRows.get(0).get("MALTC")).intValue();

        // Lấy tên khoa thực tế của lớp tín chỉ
        try {
            String ltcKhoa = jdbc.queryForObject("SELECT MAKHOA FROM dbo.LOPTINCHI WHERE MALTC = ?", String.class, maltc);
            String tenKhoaDisplay = getTenKhoa(jdbc, ltcKhoa).toUpperCase();
            model.addAttribute("tenKhoa", tenKhoaDisplay);
        } catch (Exception e) {
            model.addAttribute("tenKhoa", "KHOA");
        }

        List<Map<String, Object>> data = StoredProcedure.query(jdbc,
                "SP_BaoCaoBangDiem", maltc);

        model.addAttribute("reportType", "BANG_DIEM");
        model.addAttribute("data", data);
        model.addAttribute("tenmh", getTenMonHoc(jdbc, mamh.trim()));
        model.addAttribute("nhom", nhom);
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);
        return "baocao_report";
    }

    @RequestMapping(value = "/phieu-diem", method = RequestMethod.POST)
    public String reportPhieuDiem(@RequestParam String masv,
                                   HttpSession session,
                                   ModelMap model) {
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        String finalMasv = null;
        if ("SV".equals(nhomQuyen)) {
            finalMasv = (String) session.getAttribute("masv");
        } else {
            finalMasv = masv.trim();
            if ("KHOA".equals(nhomQuyen)) {
                // Kiểm tra xem sinh viên có thuộc khoa của user không
                try {
                    String maKhoaSv = StoredProcedure.object(jdbc, "SP_LayKhoaTheoStudent", String.class, finalMasv).trim();
                    String maKhoaUser = (String) session.getAttribute("maKhoa");
                    if (!maKhoaUser.equals(maKhoaSv)) {
                        return "redirect:/home"; // Chặn KHOA xem phiếu điểm khoa khác
                    }
                } catch (Exception e) {
                    return "redirect:/home";
                }
            }
        }

        List<Map<String, Object>> svInfo = StoredProcedure.query(jdbc,
                "SP_BaoCaoPhieuDiemSinhVienInfo", finalMasv);
        List<Map<String, Object>> data = StoredProcedure.query(jdbc,
                "SP_BaoCaoPhieuDiem", finalMasv);

        model.addAttribute("reportType", "PHIEU_DIEM");
        model.addAttribute("data", data);
        model.addAttribute("svInfo", svInfo.isEmpty() ? null : svInfo.get(0));
        return "baocao_report";
    }

    @RequestMapping(value = "/bang-diem-tk", method = RequestMethod.POST)
    public String reportBangDiemTK(@RequestParam String malop,
                                    HttpSession session,
                                    ModelMap model) {
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if ("SV".equals(nhomQuyen)) {
            return "redirect:/home";
        }
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        if ("KHOA".equals(nhomQuyen)) {
            // Kiểm tra xem lớp có thuộc khoa của user không
            try {
                String maKhoaLop = StoredProcedure.object(jdbc, "SP_LayKhoaTheoLop", String.class, malop.trim()).trim();
                String maKhoaUser = (String) session.getAttribute("maKhoa");
                if (!maKhoaUser.equals(maKhoaLop)) {
                    return "redirect:/home"; // Chặn KHOA xem bảng điểm TK lớp khoa khác
                }
            } catch (Exception e) {
                return "redirect:/home";
            }
        }

        List<Map<String, Object>> lopInfo = StoredProcedure.query(jdbc,
                "SP_BaoCaoLopInfo", malop.trim());
        List<Map<String, Object>> dsmh = StoredProcedure.query(jdbc,
                "SP_BaoCaoMonHocTheoLop", malop.trim());
        List<Map<String, Object>> dssv = StoredProcedure.query(jdbc,
                "SP_BaoCaoSinhVienTheoLop", malop.trim());
        List<Map<String, Object>> diemData = StoredProcedure.query(jdbc,
                "SP_BaoCaoDiemTongKet", malop.trim());

        model.addAttribute("reportType", "BANG_DIEM_TK");
        model.addAttribute("lopInfo", lopInfo.isEmpty() ? null : lopInfo.get(0));
        model.addAttribute("dsmh", dsmh);
        model.addAttribute("dssv", dssv);
        model.addAttribute("diemData", diemData);
        return "baocao_report";
    }

    private String getTenKhoa(JdbcTemplate jdbc, String maKhoa) {
        try {
            return StoredProcedure.object(jdbc, "SP_LayTenKhoa", String.class, maKhoa);
        } catch (Exception e) {
            return maKhoa;
        }
    }

    private String getTenMonHoc(JdbcTemplate jdbc, String maMonHoc) {
        try {
            return StoredProcedure.object(jdbc, "SP_LayTenMonHoc", String.class, maMonHoc);
        } catch (Exception e) {
            return maMonHoc;
        }
    }
}
