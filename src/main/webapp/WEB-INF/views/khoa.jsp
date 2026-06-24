<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Khoa</title>
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
            <h4><i class="fas fa-building-columns"></i> Quản lý Khoa</h4>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle"></i> ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-times-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>

        <%-- Chỉ hiển thị form nhập/sửa đối với PGV --%>
        <c:choose>
            <c:when test="${sessionScope.nhomQuyen == 'PGV'}">
                <div class="card card-custom mb-3">
                    <div class="card-header"><i class="fas fa-edit"></i> Thông tin Khoa</div>
                    <div class="card-body">
                        <form id="khForm" action="${pageContext.request.contextPath}/khoa/save" method="post">
                            <input type="hidden" id="khAction" name="action" value="add">
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <label class="form-label">Mã Khoa</label>
                                    <input type="text" id="khPK" name="maKhoa" data-field="MAKHOA"
                                           class="form-control" maxlength="10" required>
                                </div>
                                <div class="col-md-8">
                                    <label class="form-label">Tên Khoa</label>
                                    <input type="text" name="tenKhoa" data-field="TENKHOA"
                                           class="form-control" maxlength="50" required>
                                </div>
                            </div>
                            <div class="toolbar mt-3">
                                <button type="button" class="btn btn-primary btn-action" onclick="btnThem('kh')">
                                    <i class="fas fa-plus"></i> Thêm</button>
                                <button type="button" class="btn btn-danger btn-action"
                                        onclick="btnXoa('kh','${pageContext.request.contextPath}/khoa/delete')">
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
                <%-- KHOA chỉ xem --%>
                <div class="toolbar mb-3">
                    <button type="button" class="btn btn-secondary btn-action"
                            onclick="btnThoat('${pageContext.request.contextPath}/home')">
                        <i class="fas fa-sign-out-alt"></i> Thoát</button>
                </div>
            </c:otherwise>
        </c:choose>

        <div class="card card-custom">
            <div class="card-header"><i class="fas fa-list"></i> Danh sách Khoa</div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table id="khTable" class="table table-custom table-striped table-hover mb-0">
                        <thead><tr>
                            <th style="width: 80px;">STT</th><th>Mã Khoa</th><th>Tên Khoa</th>
                        </tr></thead>
                        <tbody>
                            <c:forEach items="${dsKhoa}" var="k" varStatus="st">
                                <tr>
                                    <td>${st.index + 1}</td>
                                    <td data-col="MAKHOA">${k.MAKHOA}</td>
                                    <td data-col="TENKHOA">${k.TENKHOA}</td>
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
<script>
    <c:if test="${sessionScope.nhomQuyen == 'PGV'}">
        initTableSelection('khTable','kh');
    </c:if>
</script>
</body>
</html>
