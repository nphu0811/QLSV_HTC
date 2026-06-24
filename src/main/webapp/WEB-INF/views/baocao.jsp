<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Báo cáo</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=20260508" rel="stylesheet">
</head>
<body>
<%@ include file="layout/header.jsp" %>
<div class="d-flex">
    <%@ include file="layout/sidebar.jsp" %>
    <main class="content-area">
        <div class="page-header">
            <h4><i class="fas fa-file-alt"></i> Báo cáo / In ấn</h4>
            <p>Chọn loại báo cáo và nhập tham số</p>
        </div>

        <div class="row g-4">
            <%-- 1. DS Lớp tín chỉ --%>
            <c:if test="${sessionScope.nhomQuyen != 'SV'}">
                <div class="col-md-6">
                    <div class="card card-custom h-100">
                        <div class="card-header"><i class="fas fa-list-ol"></i> 1. Danh sách Lớp tín chỉ</div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/baocao/ds-ltc" method="post" target="_blank">
                                <div class="row g-2">
                                    <div class="col-6">
                                        <label class="form-label">Niên khóa</label>
                                        <input type="text" name="nienkhoa" class="form-control" placeholder="2021-2022" required>
                                    </div>
                                    <div class="col-6">
                                        <label class="form-label">Học kỳ</label>
                                        <select name="hocky" class="form-select"><option>1</option><option>2</option><option>3</option></select>
                                    </div>
                                    <c:if test="${sessionScope.nhomQuyen == 'PGV'}">
                                        <div class="col-12 mt-2">
                                            <label class="form-label">Khoa</label>
                                            <select name="maKhoa" class="form-select">
                                                <option value="">-- TOÀN TRƯỜNG --</option>
                                                <c:forEach items="${sessionScope.khoaList}" var="k">
                                                    <option value="${k.MAKHOA}">${k.TENKHOA}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </c:if>
                                </div>
                                <button type="submit" class="btn btn-primary w-100 mt-3"><i class="fas fa-print"></i> In báo cáo</button>
                            </form>
                        </div>
                    </div>
                </div>

                <%-- 2. DS SV đăng ký --%>
                <div class="col-md-6">
                    <div class="card card-custom h-100">
                        <div class="card-header"><i class="fas fa-user-check"></i> 2. DS Sinh viên đăng ký LTC</div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/baocao/ds-sv-dk" method="post" target="_blank">
                                <div class="row g-2">
                                    <div class="col-6">
                                        <label class="form-label">Niên khóa</label>
                                        <input type="text" name="nienkhoa" class="form-control" placeholder="2021-2022" required>
                                    </div>
                                    <div class="col-6">
                                        <label class="form-label">Học kỳ</label>
                                        <select name="hocky" class="form-select"><option>1</option><option>2</option><option>3</option></select>
                                    </div>
                                    <div class="col-8">
                                        <label class="form-label">Mã Môn học</label>
                                        <input type="text" name="mamh" class="form-control" required>
                                    </div>
                                    <div class="col-4">
                                        <label class="form-label">Nhóm</label>
                                        <input type="number" name="nhom" class="form-control" min="1" value="1" required>
                                    </div>
                                </div>
                                <button type="submit" class="btn btn-primary w-100 mt-3"><i class="fas fa-print"></i> In báo cáo</button>
                            </form>
                        </div>
                    </div>
                </div>

                <%-- 3. Bảng điểm hết môn --%>
                <div class="col-md-6">
                    <div class="card card-custom h-100">
                        <div class="card-header"><i class="fas fa-clipboard-check"></i> 3. Bảng điểm hết môn</div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/baocao/bang-diem" method="post" target="_blank">
                                <div class="row g-2">
                                    <div class="col-6">
                                        <label class="form-label">Niên khóa</label>
                                        <input type="text" name="nienkhoa" class="form-control" placeholder="2021-2022" required>
                                    </div>
                                    <div class="col-6">
                                        <label class="form-label">Học kỳ</label>
                                        <select name="hocky" class="form-select"><option>1</option><option>2</option><option>3</option></select>
                                    </div>
                                    <div class="col-8">
                                        <label class="form-label">Mã Môn học</label>
                                        <input type="text" name="mamh" class="form-control" required>
                                    </div>
                                    <div class="col-4">
                                        <label class="form-label">Nhóm</label>
                                        <input type="number" name="nhom" class="form-control" min="1" value="1" required>
                                    </div>
                                </div>
                                <button type="submit" class="btn btn-primary w-100 mt-3"><i class="fas fa-print"></i> In báo cáo</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:if>

            <%-- 4. Phiếu điểm SV --%>
            <div class="col-md-6">
                <div class="card card-custom h-100">
                    <div class="card-header"><i class="fas fa-file-invoice"></i> 4. Phiếu điểm Sinh viên</div>
                    <div class="card-body">
                        <form action="${pageContext.request.contextPath}/baocao/phieu-diem" method="post" target="_blank">
                            <c:choose>
                                <c:when test="${sessionScope.nhomQuyen == 'SV'}">
                                    <p class="text-muted">Xem phiếu điểm của bạn: <strong>${sessionScope.masv}</strong></p>
                                    <input type="hidden" name="masv" value="${sessionScope.masv}">
                                </c:when>
                                <c:otherwise>
                                    <div class="mb-2">
                                        <label class="form-label">Mã Sinh viên</label>
                                        <input type="text" name="masv" class="form-control" required>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            <button type="submit" class="btn btn-primary w-100 mt-3"><i class="fas fa-print"></i> In phiếu điểm</button>
                        </form>
                    </div>
                </div>
            </div>

            <%-- 5. Bảng điểm tổng kết --%>
            <c:if test="${sessionScope.nhomQuyen != 'SV'}">
                <div class="col-md-6">
                    <div class="card card-custom h-100">
                        <div class="card-header"><i class="fas fa-table"></i> 5. Bảng điểm tổng kết (Cross-Tab)</div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/baocao/bang-diem-tk" method="post" target="_blank">
                                <div class="mb-2">
                                    <label class="form-label">Mã Lớp</label>
                                    <input type="text" name="malop" class="form-control" required>
                                </div>
                                <button type="submit" class="btn btn-primary w-100 mt-3"><i class="fas fa-print"></i> In báo cáo</button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:if>
        </div>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
