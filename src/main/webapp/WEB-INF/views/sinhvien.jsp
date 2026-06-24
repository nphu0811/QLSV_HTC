<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Sinh viên</title>
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
            <h4><i class="fas fa-user-friends"></i> Danh sách Sinh viên</h4>
            <p>SubForm 2 cấp: Chọn Lớp → Xem / Quản lý Sinh viên</p>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle"></i> ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-times-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>

        <div class="row">
            <%-- Panel trái: Danh sách Lớp --%>
            <div class="col-md-3">
                <div class="card card-custom">
                    <div class="card-header"><i class="fas fa-layer-group"></i> Danh sách Lớp</div>
                    <div class="list-group list-group-flush">
                        <c:forEach items="${dslop}" var="l">
                            <a href="${pageContext.request.contextPath}/sinhvien?malop=${l.MALOP}"
                               class="list-group-item list-group-item-action ${l.MALOP.trim() == selectedLop ? 'active' : ''}">
                                <strong>${l.MALOP}</strong><br>
                                <small>${l.TENLOP}</small>
                            </a>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <%-- Panel phải: Sinh viên --%>
            <div class="col-md-9">
                <c:if test="${not empty selectedLop}">
                    <%-- Form nhập SV --%>
                    <div class="card card-custom mb-3">
                        <div class="card-header"><i class="fas fa-edit"></i> Thông tin Sinh viên - Lớp ${selectedLop}</div>
                        <div class="card-body">
                            <form id="svForm" action="${pageContext.request.contextPath}/sinhvien/save" method="post">
                                <input type="hidden" id="svAction" name="action" value="add">
                                <input type="hidden" name="malop" value="${selectedLop}">
                                <div class="row g-2">
                                    <div class="col-md-2">
                                        <label class="form-label">Mã SV</label>
                                        <input type="text" id="svPK" name="masv" data-field="MASV"
                                               class="form-control form-control-sm" maxlength="10" required>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Họ</label>
                                        <input type="text" name="ho" data-field="HO"
                                               class="form-control form-control-sm" maxlength="50" required>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Tên</label>
                                        <input type="text" name="ten" data-field="TEN"
                                               class="form-control form-control-sm" maxlength="10" required>
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Phái</label>
                                        <select name="phai" data-field="PHAI" class="form-select form-select-sm">
                                            <option value="false">Nam</option>
                                            <option value="true">Nữ</option>
                                        </select>
                                    </div>
                                    <div class="col-md-3">
                                        <label class="form-label">Ngày sinh</label>
                                        <input type="date" name="ngaysinh" data-field="NGAYSINH"
                                               class="form-control form-control-sm">
                                    </div>
                                    <div class="col-md-7">
                                        <label class="form-label">Địa chỉ</label>
                                        <input type="text" name="diachi" data-field="DIACHI"
                                               class="form-control form-control-sm" maxlength="100">
                                    </div>
                                    <div class="col-md-2">
                                        <label class="form-label">Đang nghỉ</label>
                                        <select name="danghihoc" data-field="DANGHIHOC" class="form-select form-select-sm">
                                            <option value="false">Không</option>
                                            <option value="true">Có</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="toolbar mt-3">
                                    <c:if test="${sessionScope.nhomQuyen == 'PGV'}">
                                        <button type="button" class="btn btn-sm btn-primary btn-action" onclick="btnThem('sv')">
                                            <i class="fas fa-plus"></i> Thêm</button>
                                        <button type="button" class="btn btn-sm btn-danger btn-action"
                                                onclick="btnXoa('sv','${pageContext.request.contextPath}/sinhvien/delete?malop=${selectedLop}')">
                                            <i class="fas fa-trash"></i> Xóa</button>
                                        <button type="submit" class="btn btn-sm btn-success btn-action">
                                            <i class="fas fa-save"></i> Ghi</button>
                                        <button type="button" class="btn btn-sm btn-warning btn-action" onclick="btnPhucHoi()">
                                            <i class="fas fa-undo"></i> Phục hồi</button>
                                    </c:if>
                                    <button type="button" class="btn btn-sm btn-secondary btn-action"
                                            onclick="btnThoat('${pageContext.request.contextPath}/home')">
                                        <i class="fas fa-sign-out-alt"></i> Thoát</button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <%-- Bảng SV --%>
                    <div class="card card-custom">
                        <div class="card-header"><i class="fas fa-list"></i> Danh sách Sinh viên</div>
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table id="svTable" class="table table-custom table-striped table-hover mb-0">
                                    <thead><tr>
                                        <th>STT</th><th>Mã SV</th><th>Họ</th><th>Tên</th>
                                        <th>Phái</th><th>Ngày sinh</th><th>Địa chỉ</th>
                                    </tr></thead>
                                    <tbody>
                                        <c:forEach items="${dssv}" var="sv" varStatus="st">
                                            <tr>
                                                <td>${st.index + 1}</td>
                                                <td data-col="MASV">${sv.MASV}</td>
                                                <td data-col="HO">${sv.HO}</td>
                                                <td data-col="TEN">${sv.TEN}</td>
                                                <td data-col="PHAI">${sv.PHAI == true ? 'Nữ' : 'Nam'}</td>
                                                <td data-col="NGAYSINH">${sv.NGAYSINH}</td>
                                                <td data-col="DIACHI">${sv.DIACHI}</td>
                                                <td data-col="DANGHIHOC" style="display: none;">${sv.DANGHIHOC}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </c:if>
                <c:if test="${empty selectedLop}">
                    <div class="card card-custom">
                        <div class="card-body text-center text-muted py-5">
                            <i class="fas fa-hand-pointer fa-3x mb-3"></i>
                            <h5>Vui lòng chọn một lớp từ danh sách bên trái</h5>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>initTableSelection('svTable','sv');</script>
</body>
</html>
