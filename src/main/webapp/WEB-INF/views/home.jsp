<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Trang chủ</title>
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
            <h4><i class="fas fa-home"></i> Trang chủ</h4>
            <p>Chào mừng đến hệ thống Quản lý Điểm Sinh viên Hệ Tín Chỉ</p>
        </div>

        <div class="row g-4">
            <%-- Thông tin đăng nhập --%>
            <div class="col-md-6 col-lg-4">
                <div class="card card-custom h-100">
                    <div class="card-header"><i class="fas fa-user-circle"></i> Thông tin đăng nhập</div>
                    <div class="card-body">
                        <table class="table table-borderless mb-0">
                            <tr><th width="40%">Tên:</th><td>${sessionScope.displayName}</td></tr>
                            <tr><th>Nhóm quyền:</th><td><span class="badge bg-primary">${sessionScope.nhomQuyen}</span></td></tr>
                            <c:if test="${sessionScope.nhomQuyen != 'PGV'}">
                                <tr><th>Khoa:</th><td>${tenKhoa}</td></tr>
                            </c:if>
                            <c:if test="${sessionScope.nhomQuyen == 'SV'}">
                                <tr><th>Mã SV:</th><td>${sessionScope.masv}</td></tr>
                                <tr><th>Lớp:</th><td>${sessionScope.maLop}</td></tr>
                            </c:if>
                        </table>
                    </div>
                </div>
            </div>

            <%-- Chức năng nhanh --%>
            <c:if test="${sessionScope.nhomQuyen == 'PGV'}">
                <div class="col-md-6 col-lg-4">
                    <div class="card card-custom h-100">
                        <div class="card-header"><i class="fas fa-bolt"></i> Truy cập nhanh</div>
                        <div class="card-body">
                            <div class="d-grid gap-2">
                                <a href="${pageContext.request.contextPath}/lop" class="btn btn-outline-primary btn-sm text-start">
                                    <i class="fas fa-layer-group"></i> Quản lý Lớp</a>
                                <a href="${pageContext.request.contextPath}/sinhvien" class="btn btn-outline-primary btn-sm text-start">
                                    <i class="fas fa-user-friends"></i> Quản lý Sinh viên</a>
                                <a href="${pageContext.request.contextPath}/loptinchi" class="btn btn-outline-primary btn-sm text-start">
                                    <i class="fas fa-chalkboard"></i> Mở Lớp tín chỉ</a>
                                <a href="${pageContext.request.contextPath}/diem" class="btn btn-outline-primary btn-sm text-start">
                                    <i class="fas fa-pencil-alt"></i> Nhập điểm</a>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-6 col-lg-4">
                    <div class="card card-custom h-100">
                        <div class="card-header"><i class="fas fa-chart-bar"></i> Báo cáo</div>
                        <div class="card-body">
                            <div class="d-grid gap-2">
                                <a href="${pageContext.request.contextPath}/baocao" class="btn btn-outline-success btn-sm text-start">
                                    <i class="fas fa-file-alt"></i> Danh sách báo cáo</a>
                                <a href="${pageContext.request.contextPath}/taikhoan" class="btn btn-outline-warning btn-sm text-start">
                                    <i class="fas fa-users-cog"></i> Quản lý tài khoản</a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <c:if test="${sessionScope.nhomQuyen == 'KHOA'}">
                <div class="col-md-6 col-lg-4">
                    <div class="card card-custom h-100">
                        <div class="card-header"><i class="fas fa-bolt"></i> Chức năng</div>
                        <div class="card-body">
                            <div class="d-grid gap-2">
                                <a href="${pageContext.request.contextPath}/diem" class="btn btn-outline-primary btn-sm text-start">
                                    <i class="fas fa-pencil-alt"></i> Nhập điểm</a>
                                <a href="${pageContext.request.contextPath}/baocao" class="btn btn-outline-success btn-sm text-start">
                                    <i class="fas fa-file-alt"></i> Báo cáo</a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <c:if test="${sessionScope.nhomQuyen == 'SV'}">
                <div class="col-md-6 col-lg-4">
                    <div class="card card-custom h-100">
                        <div class="card-header"><i class="fas fa-bolt"></i> Chức năng</div>
                        <div class="card-body">
                            <div class="d-grid gap-2">
                                <a href="${pageContext.request.contextPath}/dangky" class="btn btn-outline-primary btn-sm text-start">
                                    <i class="fas fa-clipboard-list"></i> Đăng ký lớp tín chỉ</a>
                                <a href="${pageContext.request.contextPath}/baocao" class="btn btn-outline-success btn-sm text-start">
                                    <i class="fas fa-file-alt"></i> Xem phiếu điểm</a>
                            </div>
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
