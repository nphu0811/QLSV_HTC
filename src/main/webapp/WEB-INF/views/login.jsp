<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>QLDSV HTC - Đăng nhập</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=20260508" rel="stylesheet">
</head>
<body>
<div class="login-wrapper">
    <div class="card login-card text-white">
        <div class="card-header">
            <i class="fas fa-graduation-cap fa-2x mb-2" style="color:#bae6fd;"></i>
            <h3>QLDSV HTC</h3>
            <p>Hệ thống Quản lý Điểm Sinh viên Hệ Tín Chỉ</p>
        </div>
        <div class="card-body">
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-triangle"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <ul class="nav nav-pills nav-fill mb-4" role="tablist">
                <li class="nav-item" role="presentation">
                    <button class="nav-link active" data-bs-toggle="pill" data-bs-target="#gvTab" type="button">
                        <i class="fas fa-chalkboard-teacher"></i> Giảng viên
                    </button>
                </li>
                <li class="nav-item" role="presentation">
                    <button class="nav-link" data-bs-toggle="pill" data-bs-target="#svTab" type="button">
                        <i class="fas fa-user-graduate"></i> Sinh viên
                    </button>
                </li>
            </ul>

            <div class="tab-content">
                <%-- Tab Giảng viên --%>
                <div class="tab-pane fade show active" id="gvTab">
                    <form action="${pageContext.request.contextPath}/login" method="post">
                        <input type="hidden" name="loginType" value="GV">
                        <div class="mb-3">
                            <label class="form-label"><i class="fas fa-user"></i> Login</label>
                            <input type="text" name="username" class="form-control"
                                   placeholder="Nhập tài khoản..." required autofocus>
                        </div>
                        <div class="mb-3">
                            <label class="form-label"><i class="fas fa-lock"></i> Password</label>
                            <input type="password" name="password" class="form-control"
                                   placeholder="Nhập mật khẩu..." required>
                        </div>
                        <button type="submit" class="btn btn-accent w-100 fw-bold py-2">
                            <i class="fas fa-sign-in-alt"></i> Đăng nhập
                        </button>
                    </form>
                </div>

                <%-- Tab Sinh viên --%>
                <div class="tab-pane fade" id="svTab">
                    <form action="${pageContext.request.contextPath}/login" method="post">
                        <input type="hidden" name="loginType" value="SV">
                        <%--Chỉ nhập MÃ SV--%>
                        <div class="mb-4">
                            <label class="form-label"><i class="fas fa-id-card"></i> Mã Sinh viên</label>
                            <input type="text" name="username" class="form-control"
                                   placeholder="Nhập mã sinh viên của bạn (vd: N15DCCN001)..." required autofocus>
                        </div>
                        <button type="submit" class="btn btn-success w-100 fw-bold py-2 fs-5">
                            <i class="fas fa-sign-in-alt"></i> Đăng nhập
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
