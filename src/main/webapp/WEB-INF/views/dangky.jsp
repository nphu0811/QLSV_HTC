<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Đăng ký lớp tín chỉ</title>
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
            <h4><i class="fas fa-clipboard-list"></i> Đăng ký Lớp tín chỉ</h4>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show">${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>

        <%-- Thông tin SV --%>
        <c:if test="${not empty svInfo}">
            <div class="card card-custom mb-3">
                <div class="card-header"><i class="fas fa-user"></i> Thông tin Sinh viên</div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-3"><strong>Mã SV:</strong> ${svInfo.MASV}</div>
                        <div class="col-md-4"><strong>Họ tên:</strong> ${svInfo.HO} ${svInfo.TEN}</div>
                        <div class="col-md-3"><strong>Lớp:</strong> ${svInfo.TENLOP}</div>
                    </div>
                </div>
            </div>
        </c:if>

        <%-- Tìm kiếm lớp TC --%>
        <div class="card card-custom mb-3">
            <div class="card-header"><i class="fas fa-search"></i> Tìm lớp tín chỉ</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/dangky/search" method="post">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-3">
                            <label class="form-label">Niên khóa</label>
                            <input type="text" name="nienkhoa" class="form-control"
                                   placeholder="2021-2022" value="${nienkhoa}" required>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Học kỳ</label>
                            <select name="hocky" class="form-select" required>
                                <option value="1" ${hocky == 1 ? 'selected' : ''}>1</option>
                                <option value="2" ${hocky == 2 ? 'selected' : ''}>2</option>
                                <option value="3" ${hocky == 3 ? 'selected' : ''}>3</option>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-search"></i> Tìm
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <%-- DS Lớp tín chỉ --%>
        <c:if test="${not empty dsltc}">
            <div class="card card-custom mb-3">
                <div class="card-header"><i class="fas fa-list"></i> Lớp tín chỉ đang mở - NK: ${nienkhoa} - HK: ${hocky}</div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-custom table-striped table-hover mb-0">
                            <thead><tr>
                                <th>Mã MH</th><th>Tên Môn học</th><th>Nhóm</th>
                                <th>Giảng viên</th><th>SV đã ĐK</th><th>Thao tác</th>
                            </tr></thead>
                            <tbody>
                                <c:forEach items="${dsltc}" var="l">
                                    <c:set var="dadk" value="false"/>
                                    <c:forEach items="${daDangKy}" var="dk">
                                        <c:if test="${dk.MALTC == l.MALTC}"><c:set var="dadk" value="true"/></c:if>
                                    </c:forEach>
                                    <tr>
                                        <td>${l.MAMH}</td>
                                        <td>${l.TENMH}</td>
                                        <td>${l.NHOM}</td>
                                        <td>${l.HOTENGV}</td>
                                        <td>${l.SOSVDK}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${dadk == 'true'}">
                                                    <span class="badge bg-success">Đã ĐK</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <form action="${pageContext.request.contextPath}/dangky/register"
                                                          method="post" style="display:inline;">
                                                        <input type="hidden" name="maltc" value="${l.MALTC}">
                                                        <button type="submit" class="btn btn-sm btn-primary">
                                                            <i class="fas fa-plus"></i> Đăng ký
                                                        </button>
                                                    </form>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>

        <%-- DS đã đăng ký --%>
        <c:if test="${not empty daDangKy && empty dsltc}">
            <div class="card card-custom">
                <div class="card-header"><i class="fas fa-check-circle"></i> Các lớp đã đăng ký</div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-custom table-striped mb-0">
                            <thead><tr>
                                <th>STT</th><th>Mã MH</th><th>Tên Môn học</th>
                                <th>Nhóm</th><th>NK</th><th>HK</th><th>GV</th><th>Thao tác</th>
                            </tr></thead>
                            <tbody>
                                <c:forEach items="${daDangKy}" var="dk" varStatus="st">
                                    <tr>
                                        <td>${st.index+1}</td>
                                        <td>${dk.MAMH}</td><td>${dk.TENMH}</td>
                                        <td>${dk.NHOM}</td><td>${dk.NIENKHOA}</td><td>${dk.HOCKY}</td>
                                        <td>${dk.HOTENGV}</td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/dangky/cancel"
                                                  method="post" style="display:inline;"
                                                  onsubmit="return confirm('Hủy đăng ký môn này?');">
                                                <input type="hidden" name="maltc" value="${dk.MALTC}">
                                                <button type="submit" class="btn btn-sm btn-outline-danger">
                                                    <i class="fas fa-times"></i> Hủy
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </c:if>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
