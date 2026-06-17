<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<nav class="navbar navbar-expand-lg navbar-dark navbar-custom fixed-top">
    <div class="container-fluid">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
            <i class="fas fa-graduation-cap"></i> QLDSV HTC
        </a>
        <div class="d-flex align-items-center">

            <span class="text-white me-2">
                <i class="fas fa-user-circle"></i>
                ${sessionScope.displayName}
                <span class="badge bg-warning text-dark">${sessionScope.nhomQuyen}</span>
            </span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light">
                <i class="fas fa-sign-out-alt"></i> Thoát
            </a>
        </div>
    </div>
</nav>
<div style="height: 58px;"></div>
