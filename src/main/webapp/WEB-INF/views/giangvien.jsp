<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Giảng viên</title>
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
            <h4><i class="fas fa-chalkboard-teacher"></i> Quản lý Giảng viên</h4>
            <p>Danh sách giảng viên thuộc khoa quản lý</p>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle"></i> ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-times-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>

        <c:choose>
            <c:when test="${sessionScope.nhomQuyen == 'PGV'}">
                <div class="card card-custom mb-3">
                    <div class="card-header"><i class="fas fa-edit"></i> Thông tin Giảng viên</div>
                    <div class="card-body">
                        <form id="gvForm" action="${pageContext.request.contextPath}/giangvien/save" method="post">
                            <input type="hidden" id="gvAction" name="action" value="add">
                            <div class="row g-3">
                                <div class="col-md-2">
                                    <label class="form-label">Mã GV</label>
                                    <input type="text" id="gvPK" name="magv" data-field="MAGV"
                                           class="form-control" maxlength="10" required>
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label">Họ</label>
                                    <input type="text" name="ho" data-field="HO"
                                           class="form-control" maxlength="50" required>
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">Tên</label>
                                    <input type="text" name="ten" data-field="TEN"
                                           class="form-control" maxlength="10" required>
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">Học vị</label>
                                    <input type="text" name="hocvi" data-field="HOCVI"
                                           class="form-control" maxlength="20">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label">Chuyên môn</label>
                                    <input type="text" name="chuyenmon" data-field="CHUYENMON"
                                           class="form-control" maxlength="50">
                                </div>
                                <div class="col-md-2">
                                    <label class="form-label">Học hàm</label>
                                    <input type="text" name="hocham" data-field="HOCHAM"
                                           class="form-control" maxlength="20">
                                </div>
                                <div class="col-md-3">
                                    <label class="form-label">Khoa</label>
                                    <select name="maKhoa" data-field="MAKHOA" class="form-select" required>
                                        <c:forEach items="${khoaList}" var="k">
                                            <option value="${k.MAKHOA}">${k.TENKHOA}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="toolbar mt-3">
                                <button type="button" class="btn btn-primary btn-action" onclick="btnThem('gv')">
                                    <i class="fas fa-plus"></i> Thêm</button>
                                <button type="button" class="btn btn-danger btn-action"
                                        onclick="btnXoa('gv','${pageContext.request.contextPath}/giangvien/delete')">
                                    <i class="fas fa-trash"></i> Xóa</button>
                                <button type="submit" class="btn btn-success btn-action">
                                    <i class="fas fa-save"></i> Ghi</button>
                                <button type="button" class="btn btn-warning btn-action" onclick="btnPhucHoi()">
                                    <i class="fas fa-undo"></i> Phục hồi</button>
                                <button type="button" class="btn btn-secondary btn-action"
                                        onclick="btnThoat('${pageContext.request.contextPath}/home')">
                                    <i class="fas fa-sign-out-alt"></i> Thoát</button>
                            </div>
                        </form>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="toolbar mb-3">
                    <button type="button" class="btn btn-secondary btn-action"
                            onclick="btnThoat('${pageContext.request.contextPath}/home')">
                        <i class="fas fa-sign-out-alt"></i> Thoát</button>
                </div>
            </c:otherwise>
        </c:choose>

        <div class="card card-custom">
            <div class="card-header"><i class="fas fa-list"></i> Danh sách Giảng viên</div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table id="gvTable" class="table table-custom table-striped table-hover mb-0">
                        <thead><tr>
                            <th>Mã GV</th><th>Họ</th><th>Tên</th><th>Học vị</th>
                            <th>Học hàm</th><th>Chuyên môn</th><th>Khoa</th>
                        </tr></thead>
                        <tbody>
                            <c:forEach items="${dsgv}" var="g">
                                <tr>
                                    <td data-col="MAGV">${g.MAGV}</td>
                                    <td data-col="HO">${g.HO}</td>
                                    <td data-col="TEN">${g.TEN}</td>
                                    <td data-col="HOCVI">${g.HOCVI}</td>
                                    <td data-col="HOCHAM">${g.HOCHAM}</td>
                                    <td data-col="CHUYENMON">${g.CHUYENMON}</td>
                                    <td data-col="MAKHOA">${g.MAKHOA}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script>initTableSelection('gvTable','gv');</script>
</body>
</html>
