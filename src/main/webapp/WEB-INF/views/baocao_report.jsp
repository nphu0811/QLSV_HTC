<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Báo cáo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=20260508-2" rel="stylesheet">
    <style>
        body { background: #fff; }
        .report-container { max-width: 950px; margin: 20px auto; padding: 30px; }
    </style>
</head>
<body>
<div class="no-print text-center py-2" style="background:#075985;">
    <button onclick="window.print();" class="btn btn-accent btn-sm">
        <i class="fas fa-print"></i> In báo cáo
    </button>
    <button onclick="window.close();" class="btn btn-secondary btn-sm ms-2">Đóng</button>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</div>

<div class="report-container">

<c:if test="${not empty error}">
    <div class="alert alert-danger text-center shadow-sm">
        <h4 class="text-danger"><i class="fas fa-exclamation-triangle"></i> Lỗi dữ liệu</h4>
        <p class="mb-0 fs-5">${error}</p>
    </div>
</c:if>

<%-- ========== 1. DS LỚP TÍN CHỈ ========== --%>
<c:if test="${reportType == 'DS_LTC'}">
    <div class="report-title">
        <h5>KHOA ${tenKhoa}</h5>
        <p><strong>Niên khóa:</strong> ${nienkhoa} &nbsp;&nbsp; <strong>Học kỳ:</strong> ${hocky}</p>
    </div>
    <table class="table report-table">
        <thead><tr>
            <th>STT</th><th>Tên môn học</th><th>Nhóm</th>
            <th>Họ tên GV giảng</th><th>SV tối thiểu</th><th>SV đã đăng ký</th>
        </tr></thead>
        <tbody>
            <c:forEach items="${data}" var="d" varStatus="st">
                <tr>
                    <td class="text-center">${st.index+1}</td>
                    <td>${d.TENMH}</td>
                    <td class="text-center">${d.NHOM}</td>
                    <td>${d.HOTENGV}</td>
                    <td class="text-center">${d.SOSVTOITHIEU}</td>
                    <td class="text-center">${d.SOSVDK}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <p class="report-footer">Số lượng lớp đã mở: <strong>${data.size()}</strong></p>
</c:if>

<%-- ========== 2. DS SV ĐĂNG KÝ LTC ========== --%>
<c:if test="${reportType == 'DS_SV_DK'}">
    <div class="report-title">
        <h5>DANH SÁCH SINH VIÊN ĐĂNG KÝ LỚP TÍN CHỈ</h5>
        <h6>KHOA ${tenKhoa}</h6>
        <p><strong>Niên khóa:</strong> ${nienkhoa} &nbsp;&nbsp; <strong>Học kỳ:</strong> ${hocky}</p>
        <p><strong>Môn học:</strong> ${tenmh} &ndash; <strong>Nhóm:</strong> ${nhom}</p>
    </div>
    <table class="table report-table">
        <thead><tr>
            <th>STT</th><th>Mã SV</th><th>Họ</th><th>Tên</th><th>Phái</th><th>Mã lớp</th>
        </tr></thead>
        <tbody>
            <c:forEach items="${data}" var="d" varStatus="st">
                <tr>
                    <td class="text-center">${st.index+1}</td>
                    <td>${d.MASV}</td>
                    <td>${d.HO}</td>
                    <td>${d.TEN}</td>
                    <td class="text-center">${d.PHAI == true ? 'Nữ' : 'Nam'}</td>
                    <td>${d.MALOP}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <p class="report-footer">Số sinh viên đã đăng ký: <strong>${data.size()}</strong></p>
</c:if>

<%-- ========== 3. BẢNG ĐIỂM HẾT MÔN ========== --%>
<c:if test="${reportType == 'BANG_DIEM'}">
    <div class="report-title">
        <h5>BẢNG ĐIỂM HẾT MÔN</h5>
        <h6>KHOA ${tenKhoa}</h6>
        <p><strong>Niên khóa:</strong> ${nienkhoa} &nbsp;&nbsp; <strong>Học kỳ:</strong> ${hocky}</p>
        <p><strong>Môn học:</strong> ${tenmh} &ndash; <strong>Nhóm:</strong> ${nhom}</p>
    </div>
    <table class="table report-table">
        <thead><tr>
            <th>STT</th><th>Mã SV</th><th>Họ</th><th>Tên</th>
            <th>Điểm CC</th><th>Điểm GK</th><th>Điểm CK</th><th>Điểm hết môn</th>
        </tr></thead>
        <tbody>
            <c:forEach items="${data}" var="d" varStatus="st">
                <tr>
                    <td class="text-center">${st.index+1}</td>
                    <td>${d.MASV}</td>
                    <td>${d.HO}</td>
                    <td>${d.TEN}</td>
                    <td class="text-center">${d.DIEM_CC}</td>
                    <td class="text-center">${d.DIEM_GK}</td>
                    <td class="text-center">${d.DIEM_CK}</td>
                    <td class="text-center">
                        <c:if test="${d.DIEM_CC != null && d.DIEM_GK != null && d.DIEM_CK != null}">
                            <strong>
                                <fmt:formatNumber value="${d.DIEM_CC * 0.1 + d.DIEM_GK * 0.3 + d.DIEM_CK * 0.6}"
                                                  maxFractionDigits="1"/>
                            </strong>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <p class="report-footer">Số sinh viên: <strong>${data.size()}</strong></p>
</c:if>

<%-- ========== 4. PHIẾU ĐIỂM SV ========== --%>
<c:if test="${reportType == 'PHIEU_DIEM'}">
    <div class="report-title">
        <h5>PHIẾU ĐIỂM SINH VIÊN</h5>
        <c:if test="${svInfo != null}">
            <p><strong>Mã SV:</strong> ${svInfo.MASV} &nbsp;&nbsp;
               <strong>Họ tên:</strong> ${svInfo.HO} ${svInfo.TEN}</p>
            <p><strong>Lớp:</strong> ${svInfo.TENLOP} &nbsp;&nbsp;
               <strong>Khoa:</strong> ${svInfo.TENKHOA}</p>
        </c:if>
    </div>
    <table class="table report-table">
        <thead><tr>
            <th>STT</th><th>Tên Môn học</th><th>Điểm</th>
        </tr></thead>
        <tbody>
            <c:forEach items="${data}" var="d" varStatus="st">
                <tr>
                    <td class="text-center">${st.index+1}</td>
                    <td>${d.TENMH}</td>
                    <td class="text-center">
                        <strong><fmt:formatNumber value="${d.DIEM}" maxFractionDigits="1"/></strong>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>

<%-- ========== 5. BẢNG ĐIỂM TỔNG KẾT (Cross-Tab) ========== --%>
<c:if test="${reportType == 'BANG_DIEM_TK'}">
    <div class="report-title">
        <h5>BẢNG ĐIỂM TỔNG KẾT CUỐI KHÓA</h5>
        <c:if test="${lopInfo != null}">
            <p><strong>LỚP:</strong> ${lopInfo.TENLOP} &ndash;
               <strong>KHÓA HỌC:</strong> ${lopInfo.KHOAHOC}</p>
            <p><strong>KHOA:</strong> ${lopInfo.TENKHOA}</p>
        </c:if>
    </div>
    <div class="table-responsive">
        <table class="table report-table summary-report-table">
            <thead>
                <tr>
                    <th class="text-center align-middle">MASV - Họ tên</th>
                    <c:forEach items="${dsmh}" var="mh">
                        <th class="text-center align-middle summary-subject-col">
                            <div class="summary-subject-name">
                                ${mh.TENMH}
                            </div>
                        </th>
                    </c:forEach>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${dssv}" var="sv">
                    <tr>
                        <td><strong>${sv.MASV}</strong><br>${sv.HOTENSV}</td>
                        <c:forEach items="${dsmh}" var="mh">
                            <td class="text-center">
                                <c:forEach items="${diemData}" var="dd">
                                    <c:if test="${dd.MASV.trim() == sv.MASV.trim() && dd.MAMH.trim() == mh.MAMH.trim()}">
                                        <fmt:formatNumber value="${dd.DIEM}" maxFractionDigits="1"/>
                                    </c:if>
                                </c:forEach>
                            </td>
                        </c:forEach>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

</div>
</body>
</html>
