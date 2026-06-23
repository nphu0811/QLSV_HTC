<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.2/gsap.min.js"></script>
<nav class="navbar navbar-custom fixed-top" aria-label="Thanh điều hướng chính">
    <div class="topbar-brand">
        <button class="sidebar-toggle" id="sidebarToggle" type="button"
                aria-label="Mở menu điều hướng" aria-controls="appSidebar" aria-expanded="false">
            <i class="fas fa-bars" aria-hidden="true"></i>
        </button>
        <a class="navbar-brand" href="${pageContext.request.contextPath}/home" aria-label="QLDSV HTC - Trang chủ">
            <span class="brand-mark"><i class="fas fa-graduation-cap" aria-hidden="true"></i></span>
            <span class="brand-copy">
                <strong>QLDSV</strong>
                <small>Hệ tín chỉ</small>
            </span>
        </a>
    </div>

    <div class="topbar-content">
        <div class="workspace-context d-none d-md-flex">
            <span class="status-dot" aria-hidden="true"></span>
            <span>Không gian học vụ</span>
        </div>

        <div class="topbar-actions">
            <div class="user-summary">
                <span class="user-avatar"><i class="fas fa-user" aria-hidden="true"></i></span>
                <span class="user-copy d-none d-sm-flex">
                    <strong>${sessionScope.displayName}</strong>
                    <small>${sessionScope.nhomQuyen}</small>
                </span>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-logout" title="Đăng xuất">
                <i class="fas fa-arrow-right-from-bracket" aria-hidden="true"></i>
                <span class="d-none d-lg-inline">Đăng xuất</span>
            </a>
        </div>
    </div>
</nav>
<div class="topbar-spacer" aria-hidden="true"></div>
