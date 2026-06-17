<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Môn học</title>
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
            <h4><i class="fas fa-book"></i> Quản lý Môn học</h4>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show"><i class="fas fa-check-circle"></i> ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show"><i class="fas fa-times-circle"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>

        <div class="card card-custom mb-3">
            <div class="card-header"><i class="fas fa-edit"></i> Thông tin Môn học</div>
            <div class="card-body">
                <form id="mhForm" action="${pageContext.request.contextPath}/monhoc/save" method="post">
                    <input type="hidden" id="mhAction" name="action" value="add">
                    <div class="row g-3">
                        <div class="col-md-2">
                            <label class="form-label">Mã MH</label>
                            <input type="text" id="mhPK" name="mamh" data-field="MAMH"
                                   class="form-control" maxlength="10" required>
                        </div>
                        <div class="col-md-4">
                            <label class="form-label">Tên Môn học</label>
                            <input type="text" name="tenmh" data-field="TENMH"
                                   class="form-control" maxlength="50" required>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Số tiết LT</label>
                            <input type="number" name="sotietLT" data-field="SOTIET_LT"
                                   class="form-control" min="0" required>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Số tiết TH</label>
                            <input type="number" name="sotietTH" data-field="SOTIET_TH"
                                   class="form-control" min="0" required>
                        </div>
                    </div>
                    <div class="toolbar mt-3">
                        <c:if test="${sessionScope.nhomQuyen == 'PGV'}">
                            <button type="button" class="btn btn-primary btn-action" onclick="btnThem('mh')">
                                <i class="fas fa-plus"></i> Thêm</button>
                            <button type="button" class="btn btn-danger btn-action"
                                    onclick="btnXoa('mh','${pageContext.request.contextPath}/monhoc/delete')">
                                <i class="fas fa-trash"></i> Xóa</button>
                            <button type="submit" class="btn btn-success btn-action">
                                <i class="fas fa-save"></i> Ghi</button>
                            <button type="button" class="btn btn-warning btn-action" onclick="btnPhucHoi()">
                                <i class="fas fa-undo"></i> Phục hồi</button>
                        </c:if>
                        <button type="button" class="btn btn-secondary btn-action"
                                onclick="btnThoat('${pageContext.request.contextPath}/home')">
                            <i class="fas fa-sign-out-alt"></i> Thoát</button>
                    </div>
                </form>
            </div>
        </div>

        <div class="card card-custom">
            <div class="card-header"><i class="fas fa-list"></i> Danh sách Môn học</div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table id="mhTable" class="table table-custom table-striped table-hover mb-0">
                        <thead><tr>
                            <th>STT</th><th>Mã MH</th><th>Tên Môn học</th>
                            <th>Số tiết LT</th><th>Số tiết TH</th>
                        </tr></thead>
                        <tbody>
                            <c:forEach items="${dsmh}" var="mh" varStatus="st">
                                <tr>
                                    <td>${st.index + 1}</td>
                                    <td data-col="MAMH">${mh.MAMH}</td>
                                    <td data-col="TENMH">${mh.TENMH}</td>
                                    <td data-col="SOTIET_LT">${mh.SOTIET_LT}</td>
                                    <td data-col="SOTIET_TH">${mh.SOTIET_TH}</td>
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
<script>initTableSelection('mhTable','mh');</script>
</body>
</html>
