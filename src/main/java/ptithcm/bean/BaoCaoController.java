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
    public String show() {
        return "baocao";
    }

    @RequestMapping(value = "/ds-ltc", method = RequestMethod.GET)
    public String formDSLTC() {
        return "baocao";
    }

    @RequestMapping(value = "/ds-ltc", method = RequestMethod.POST)
    public String reportDSLTC(@RequestParam String nienkhoa,
                              @RequestParam int hocky,
                              HttpSession session,
                              ModelMap model) {
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        String maKhoa = (String) session.getAttribute("maKhoa");
        Object khoaFilter = "PGV".equals(nhomQuyen) ? null : maKhoa;

        List<Map<String, Object>> data = StoredProcedure.query(jdbc,
                "SP_BaoCaoDSLopTinChi", nienkhoa.trim(), hocky, khoaFilter);

        model.addAttribute("tenKhoa", "PGV".equals(nhomQuyen) ? "TOÀN TRƯỜNG" : getTenKhoa(jdbc, maKhoa));
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
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        String maKhoa = (String) session.getAttribute("maKhoa");
        Object khoaFilter = "PGV".equals(nhomQuyen) ? null : maKhoa;

        List<Map<String, Object>> ltcRows = StoredProcedure.query(jdbc,
                "SP_TimLopTinChi", nienkhoa.trim(), hocky, mamh.trim(), nhom, khoaFilter);
        model.addAttribute("tenKhoa", "PGV".equals(nhomQuyen) ? "TOÀN TRƯỜNG" : getTenKhoa(jdbc, maKhoa));

        if (ltcRows.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy lớp tín chỉ này!");
            return "baocao_report";
        }
        int maltc = ((Number) ltcRows.get(0).get("MALTC")).intValue();

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
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        String maKhoa = (String) session.getAttribute("maKhoa");
        Object khoaFilter = "PGV".equals(nhomQuyen) ? null : maKhoa;

        List<Map<String, Object>> ltcRows = StoredProcedure.query(jdbc,
                "SP_TimLopTinChi", nienkhoa.trim(), hocky, mamh.trim(), nhom, khoaFilter);
        model.addAttribute("tenKhoa", "PGV".equals(nhomQuyen) ? "TOÀN TRƯỜNG" : getTenKhoa(jdbc, maKhoa));

        if (ltcRows.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy lớp tín chỉ này!");
            return "baocao_report";
        }
        int maltc = ((Number) ltcRows.get(0).get("MALTC")).intValue();

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
        if ("SV".equals(nhomQuyen)) {
            masv = (String) session.getAttribute("masv");
        }

        List<Map<String, Object>> svInfo = StoredProcedure.query(jdbc,
                "SP_BaoCaoPhieuDiemSinhVienInfo", masv.trim());
        List<Map<String, Object>> data = StoredProcedure.query(jdbc,
                "SP_BaoCaoPhieuDiem", masv.trim());

        model.addAttribute("reportType", "PHIEU_DIEM");
        model.addAttribute("data", data);
        model.addAttribute("svInfo", svInfo.isEmpty() ? null : svInfo.get(0));
        return "baocao_report";
    }

    @RequestMapping(value = "/bang-diem-tk", method = RequestMethod.POST)
    public String reportBangDiemTK(@RequestParam String malop,
                                    HttpSession session,
                                    ModelMap model) {
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

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
