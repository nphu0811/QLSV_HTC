<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="sidebar" id="appSidebar" aria-label="Điều hướng chức năng">
    <div class="sidebar-inner">
        <div class="sidebar-workspace">
            <span class="sidebar-workspace-icon"><i class="fas fa-building-columns" aria-hidden="true"></i></span>
            <span>
                <small>Workspace</small>
                <strong>Quản lý đào tạo</strong>
            </span>
        </div>

        <ul class="nav flex-column">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/home">
                    <span class="nav-icon"><i class="fas fa-grid-2" aria-hidden="true"></i></span>
                    <span>Tổng quan</span>
                </a>
            </li>

            <c:if test="${sessionScope.nhomQuyen == 'PGV' || sessionScope.nhomQuyen == 'KHOA'}">
                <li class="nav-header">Dữ liệu cơ bản</li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/khoa">
                    <span class="nav-icon"><i class="fas fa-building-columns" aria-hidden="true"></i></span><span>Khoa</span></a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/lop">
                    <span class="nav-icon"><i class="fas fa-layer-group" aria-hidden="true"></i></span><span>Lớp</span></a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/sinhvien">
                    <span class="nav-icon"><i class="fas fa-user-group" aria-hidden="true"></i></span><span>Sinh viên</span></a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/monhoc">
                    <span class="nav-icon"><i class="fas fa-book-open" aria-hidden="true"></i></span><span>Môn học</span></a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/giangvien">
                    <span class="nav-icon"><i class="fas fa-person-chalkboard" aria-hidden="true"></i></span><span>Giảng viên</span></a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/loptinchi">
                    <span class="nav-icon"><i class="fas fa-chalkboard" aria-hidden="true"></i></span><span>Lớp tín chỉ</span></a></li>

                <li class="nav-header">Nghiệp vụ</li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/diem">
                    <span class="nav-icon"><i class="fas fa-pen-to-square" aria-hidden="true"></i></span><span>Nhập điểm</span></a></li>
                <c:if test="${sessionScope.nhomQuyen == 'PGV'}">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dangky">
                        <span class="nav-icon"><i class="fas fa-clipboard-list" aria-hidden="true"></i></span><span>Đăng ký hộ LTC</span></a></li>
                </c:if>

                <li class="nav-header">Báo cáo</li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/baocao">
                    <span class="nav-icon"><i class="fas fa-chart-simple" aria-hidden="true"></i></span><span>Báo cáo &amp; in ấn</span></a></li>

                <c:if test="${sessionScope.nhomQuyen == 'PGV'}">
                    <li class="nav-header">Hệ thống</li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/taikhoan">
                        <span class="nav-icon"><i class="fas fa-user-shield" aria-hidden="true"></i></span><span>Tài khoản</span></a></li>
                </c:if>
            </c:if>

            <c:if test="${sessionScope.nhomQuyen == 'SV'}">
                <li class="nav-header">Học tập</li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dangky">
                    <span class="nav-icon"><i class="fas fa-clipboard-list" aria-hidden="true"></i></span><span>Đăng ký lớp tín chỉ</span></a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/baocao">
                    <span class="nav-icon"><i class="fas fa-file-lines" aria-hidden="true"></i></span><span>Phiếu điểm</span></a></li>
            </c:if>
        </ul>

        <div class="sidebar-footer">
            <i class="fas fa-shield-halved" aria-hidden="true"></i>
            <span><strong>Phiên làm việc an toàn</strong><small>Dữ liệu được phân quyền</small></span>
        </div>
    </div>
</nav>
<button class="sidebar-scrim" id="sidebarScrim" type="button" aria-label="Đóng menu điều hướng"></button>
