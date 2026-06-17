<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="sidebar">
    <ul class="nav flex-column">

        <%-- ===== PGV và KHOA: Cùng menu, khác quyền thao tác bên trong trang ===== --%>
        <c:if test="${sessionScope.nhomQuyen == 'PGV' || sessionScope.nhomQuyen == 'KHOA'}">
            <li class="nav-header">Cơ bản</li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/lop">
                    <i class="fas fa-layer-group"></i> Lớp
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/sinhvien">
                    <i class="fas fa-user-friends"></i> Sinh viên
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/monhoc">
                    <i class="fas fa-book"></i> Môn học
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/giangvien">
                    <i class="fas fa-chalkboard-teacher"></i> Giảng viên
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/loptinchi">
                    <i class="fas fa-chalkboard"></i> Lớp tín chỉ
                </a>
            </li>
            
            <li class="nav-header">Nghiệp vụ</li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/diem">
                    <i class="fas fa-pencil-alt"></i> Nhập điểm
                </a>
            </li>

            <li class="nav-header">Báo cáo</li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/baocao">
                    <i class="fas fa-file-alt"></i> Báo cáo / In ấn
                </a>
            </li>

            <li class="nav-header">Hệ thống</li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/taikhoan">
                    <i class="fas fa-users-cog"></i> Tài khoản
                </a>
            </li>
        </c:if>

        <%-- ===== SV: Tối thiểu ===== --%>
        <c:if test="${sessionScope.nhomQuyen == 'SV'}">
            <li class="nav-header">Sinh viên</li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/dangky">
                    <i class="fas fa-clipboard-list"></i> Đăng ký lớp TC
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/baocao">
                    <i class="fas fa-file-alt"></i> Phiếu điểm
                </a>
            </li>
        </c:if>

    </ul>
</nav>
