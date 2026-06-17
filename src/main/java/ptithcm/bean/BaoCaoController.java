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
 * Báo cáo - 5 loại:
 * 1. DS Lớp tín chỉ
 * 2. DS SV đăng ký LTC
 * 3. Bảng điểm hết môn
 * 4. Phiếu điểm SV
 * 5. Bảng điểm tổng kết (Cross-Tab)
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

    /** 1. Danh sách lớp tín chỉ */
    @RequestMapping(value = "/ds-ltc", method = RequestMethod.GET)
    public String formDSLTC() { return "baocao"; }

    @RequestMapping(value = "/ds-ltc", method = RequestMethod.POST)
    public String reportDSLTC(@RequestParam String nienkhoa, @RequestParam int hocky,
                              HttpSession session, ModelMap model) {
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        String maKhoa = (String) session.getAttribute("maKhoa");
        String tenKhoa = gettenKhoa(jdbc, maKhoa);

        List<Map<String, Object>> data;
        if ("PGV".equals(nhomQuyen)) {
            data = jdbc.queryForList(
                "SELECT MH.TENMH, LTC.NHOM, GV.HO + ' ' + GV.TEN AS HOTENGV, " +
                "LTC.SOSVTOITHIEU, " +
                "(SELECT COUNT(*) FROM DANGKY DK WHERE DK.MALTC=LTC.MALTC AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL)) AS SOSVDK " +
                "FROM LOPTINCHI LTC " +
                "JOIN MONHOC MH ON LTC.MAMH=MH.MAMH " +
                "JOIN GIANGVIEN GV ON LTC.MAGV=GV.MAGV " +
                "WHERE LTC.NIENKHOA=? AND LTC.HOCKY=? AND LTC.HUYLOP=0 " +
                "ORDER BY MH.TENMH, LTC.NHOM", nienkhoa.trim(), hocky);
            model.addAttribute("tenKhoa", "TOÀN TRƯỜNG");
        } else {
            data = jdbc.queryForList(
                "SELECT MH.TENMH, LTC.NHOM, GV.HO + ' ' + GV.TEN AS HOTENGV, " +
                "LTC.SOSVTOITHIEU, " +
                "(SELECT COUNT(*) FROM DANGKY DK WHERE DK.MALTC=LTC.MALTC AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL)) AS SOSVDK " +
                "FROM LOPTINCHI LTC " +
                "JOIN MONHOC MH ON LTC.MAMH=MH.MAMH " +
                "JOIN GIANGVIEN GV ON LTC.MAGV=GV.MAGV " +
                "WHERE LTC.NIENKHOA=? AND LTC.HOCKY=? AND LTC.MAKHOA=? AND LTC.HUYLOP=0 " +
                "ORDER BY MH.TENMH, LTC.NHOM", nienkhoa.trim(), hocky, maKhoa);
            model.addAttribute("tenKhoa", tenKhoa);
        }

        model.addAttribute("reportType", "DS_LTC");
        model.addAttribute("data", data);
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);
        return "baocao_report";
    }

    /** 2. DS Sinh viên đăng ký LTC */
    @RequestMapping(value = "/ds-sv-dk", method = RequestMethod.POST)
    public String reportDSSVDK(@RequestParam String nienkhoa, @RequestParam int hocky,
                               @RequestParam String mamh, @RequestParam int nhom,
                               HttpSession session, ModelMap model) {
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String maKhoa = (String) session.getAttribute("maKhoa");

        String nhomQuyen = (String) session.getAttribute("nhomQuyen");

        // Tìm MALTC
        List<Map<String, Object>> ltcRows;
        if ("PGV".equals(nhomQuyen)) {
            ltcRows = jdbc.queryForList("SELECT MALTC FROM LOPTINCHI WHERE NIENKHOA=? AND HOCKY=? AND MAMH=? AND NHOM=?", nienkhoa.trim(), hocky, mamh.trim(), nhom);
            model.addAttribute("tenKhoa", "TOÀN TRƯỜNG");
        } else {
            ltcRows = jdbc.queryForList("SELECT MALTC FROM LOPTINCHI WHERE NIENKHOA=? AND HOCKY=? AND MAMH=? AND NHOM=? AND MAKHOA=?", nienkhoa.trim(), hocky, mamh.trim(), nhom, maKhoa);
            model.addAttribute("tenKhoa", gettenKhoa(jdbc, maKhoa));
        }
        
        if (ltcRows.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy Lớp tín chỉ này!");
            return "baocao_report";
        }
        Integer maltc = (Integer) ltcRows.get(0).get("MALTC");

        List<Map<String, Object>> mhRows = jdbc.queryForList("SELECT TENMH FROM MONHOC WHERE MAMH=?", mamh.trim());
        String tenmh = mhRows.isEmpty() ? mamh.trim() : (String) mhRows.get(0).get("TENMH");

        List<Map<String, Object>> data = jdbc.queryForList(
                "SELECT SV.MASV, SV.HO, SV.TEN, SV.PHAI, SV.MALOP " +
                "FROM DANGKY DK JOIN SINHVIEN SV ON DK.MASV=SV.MASV " +
                "WHERE DK.MALTC=? AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL) " +
                "ORDER BY SV.TEN, SV.HO", maltc);

        model.addAttribute("reportType", "DS_SV_DK");
        model.addAttribute("data", data);
        model.addAttribute("tenmh", tenmh);
        model.addAttribute("nhom", nhom);
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);
        return "baocao_report";
    }

    /** 3. Bảng điểm hết môn */
    @RequestMapping(value = "/bang-diem", method = RequestMethod.POST)
    public String reportBangDiem(@RequestParam String nienkhoa, @RequestParam int hocky,
                                 @RequestParam String mamh, @RequestParam int nhom,
                                 HttpSession session, ModelMap model) {
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);
        String maKhoa = (String) session.getAttribute("maKhoa");

        String nhomQuyen = (String) session.getAttribute("nhomQuyen");

        List<Map<String, Object>> ltcRows;
        if ("PGV".equals(nhomQuyen)) {
             ltcRows = jdbc.queryForList("SELECT MALTC FROM LOPTINCHI WHERE NIENKHOA=? AND HOCKY=? AND MAMH=? AND NHOM=?", nienkhoa.trim(), hocky, mamh.trim(), nhom);
             model.addAttribute("tenKhoa", "TOÀN TRƯỜNG");
        } else {
             ltcRows = jdbc.queryForList("SELECT MALTC FROM LOPTINCHI WHERE NIENKHOA=? AND HOCKY=? AND MAMH=? AND NHOM=? AND MAKHOA=?", nienkhoa.trim(), hocky, mamh.trim(), nhom, maKhoa);
             model.addAttribute("tenKhoa", gettenKhoa(jdbc, maKhoa));
        }
        
        if (ltcRows.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy Lớp tín chỉ này!");
            return "baocao_report";
        }
        Integer maltc = (Integer) ltcRows.get(0).get("MALTC");

        List<Map<String, Object>> mhRows = jdbc.queryForList("SELECT TENMH FROM MONHOC WHERE MAMH=?", mamh.trim());
        String tenmh = mhRows.isEmpty() ? mamh.trim() : (String) mhRows.get(0).get("TENMH");

        List<Map<String, Object>> data = jdbc.queryForList(
                "SELECT SV.MASV, SV.HO, SV.TEN, DK.DIEM_CC, DK.DIEM_GK, DK.DIEM_CK " +
                "FROM DANGKY DK JOIN SINHVIEN SV ON DK.MASV=SV.MASV " +
                "WHERE DK.MALTC=? AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL) " +
                "ORDER BY SV.TEN, SV.HO", maltc);

        model.addAttribute("reportType", "BANG_DIEM");
        model.addAttribute("data", data);
        model.addAttribute("tenmh", tenmh);
        model.addAttribute("nhom", nhom);
        model.addAttribute("nienkhoa", nienkhoa.trim());
        model.addAttribute("hocky", hocky);
        return "baocao_report";
    }

    /** 4. Phiếu điểm SV */
    @RequestMapping(value = "/phieu-diem", method = RequestMethod.POST)
    public String reportPhieuDiem(@RequestParam String masv,
                                   HttpSession session, ModelMap model) {
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

        // Nếu SV thì chỉ xem phiếu điểm của mình
        String nhomQuyen = (String) session.getAttribute("nhomQuyen");
        if ("SV".equals(nhomQuyen)) {
            masv = (String) session.getAttribute("masv");
        }

        // Thông tin SV
        List<Map<String, Object>> svInfo = jdbc.queryForList(
                "SELECT SV.MASV, SV.HO, SV.TEN, SV.MALOP, L.TENLOP, K.TENKHOA " +
                "FROM SINHVIEN SV JOIN LOP L ON SV.MALOP=L.MALOP " +
                "JOIN KHOA K ON L.MAKHOA=K.MAKHOA WHERE SV.MASV=?", masv.trim());

        // Điểm: max của các lần thi, sắp xếp theo tên môn
        List<Map<String, Object>> data = jdbc.queryForList(
                "SELECT MH.TENMH, " +
                "MAX(ISNULL(DK.DIEM_CC,0)*0.1 + ISNULL(DK.DIEM_GK,0)*0.3 + ISNULL(DK.DIEM_CK,0)*0.6) AS DIEM " +
                "FROM DANGKY DK " +
                "JOIN LOPTINCHI LTC ON DK.MALTC=LTC.MALTC " +
                "JOIN MONHOC MH ON LTC.MAMH=MH.MAMH " +
                "WHERE DK.MASV=? AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL) " +
                "GROUP BY MH.TENMH ORDER BY MH.TENMH", masv.trim());

        model.addAttribute("reportType", "PHIEU_DIEM");
        model.addAttribute("data", data);
        model.addAttribute("svInfo", svInfo.isEmpty() ? null : svInfo.get(0));
        return "baocao_report";
    }

    /** 5. Bảng điểm tổng kết (Cross-Tab) */
    @RequestMapping(value = "/bang-diem-tk", method = RequestMethod.POST)
    public String reportBangDiemTK(@RequestParam String malop,
                                    HttpSession session, ModelMap model) {
        JdbcTemplate jdbc = connHelper.getJdbcTemplate(session);

        // Thông tin lớp
        List<Map<String, Object>> lopInfo = jdbc.queryForList(
                "SELECT L.MALOP, L.TENLOP, L.KHOAHOC, K.TENKHOA " +
                "FROM LOP L JOIN KHOA K ON L.MAKHOA=K.MAKHOA WHERE L.MALOP=?", malop.trim());

        // DS môn học mà lớp đã học
        List<Map<String, Object>> dsmh = jdbc.queryForList(
                "SELECT DISTINCT MH.MAMH, MH.TENMH FROM DANGKY DK " +
                "JOIN LOPTINCHI LTC ON DK.MALTC=LTC.MALTC " +
                "JOIN MONHOC MH ON LTC.MAMH=MH.MAMH " +
                "JOIN SINHVIEN SV ON DK.MASV=SV.MASV " +
                "WHERE SV.MALOP=? AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL) " +
                "ORDER BY MH.TENMH", malop.trim());

        // DS sinh viên
        List<Map<String, Object>> dssv = jdbc.queryForList(
                "SELECT MASV, HO + ' ' + TEN AS HOTENSV FROM SINHVIEN WHERE MALOP=? ORDER BY TEN, HO",
                malop.trim());

        // Điểm: max của các lần thi cho mỗi SV - mỗi môn
        List<Map<String, Object>> diemData = jdbc.queryForList(
                "SELECT DK.MASV, LTC.MAMH, " +
                "MAX(ISNULL(DK.DIEM_CC,0)*0.1 + ISNULL(DK.DIEM_GK,0)*0.3 + ISNULL(DK.DIEM_CK,0)*0.6) AS DIEM " +
                "FROM DANGKY DK " +
                "JOIN LOPTINCHI LTC ON DK.MALTC=LTC.MALTC " +
                "JOIN SINHVIEN SV ON DK.MASV=SV.MASV " +
                "WHERE SV.MALOP=? AND (DK.HUYDANGKY=0 OR DK.HUYDANGKY IS NULL) " +
                "GROUP BY DK.MASV, LTC.MAMH", malop.trim());

        model.addAttribute("reportType", "BANG_DIEM_TK");
        model.addAttribute("lopInfo", lopInfo.isEmpty() ? null : lopInfo.get(0));
        model.addAttribute("dsmh", dsmh);
        model.addAttribute("dssv", dssv);
        model.addAttribute("diemData", diemData);
        return "baocao_report";
    }

    private String gettenKhoa(JdbcTemplate jdbc, String maKhoa) {
        try {
            return jdbc.queryForObject("SELECT TENKHOA FROM KHOA WHERE MAKHOA=?", String.class, maKhoa);
        } catch (Exception e) {
            return maKhoa;
        }
    }
}
