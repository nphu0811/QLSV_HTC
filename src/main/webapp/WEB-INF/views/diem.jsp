<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Nhập điểm</title>
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
            <h4><i class="fas fa-pencil-alt"></i> Nhập điểm</h4>
            <p>Điểm hết môn = CC × 0.1 + GK × 0.3 + CK × 0.6</p>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show">${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>

        <%-- Chọn lớp TC --%>
        <div class="card card-custom mb-3">
            <div class="card-header"><i class="fas fa-filter"></i> Chọn lớp tín chỉ</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/diem/load" method="post">
                    <div class="row g-3 align-items-end">
                        <div class="col-md-2">
                            <label class="form-label">Niên khóa</label>
                            <select name="nienkhoa" class="form-select">
                                <c:forEach items="${dsNienKhoa}" var="nk">
                                    <option value="${nk.NIENKHOA}" ${nk.NIENKHOA.trim() == nienkhoa ? 'selected' : ''}>${nk.NIENKHOA}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Học kỳ</label>
                            <select name="hocky" class="form-select">
                                <option value="1" ${hocky == 1 ? 'selected' : ''}>1</option>
                                <option value="2" ${hocky == 2 ? 'selected' : ''}>2</option>
                                <option value="3" ${hocky == 3 ? 'selected' : ''}>3</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Môn học</label>
                            <select name="mamh" class="form-select">
                                <c:forEach items="${dsmh}" var="mh">
                                    <option value="${mh.MAMH}" ${mh.MAMH.trim() == mamh ? 'selected' : ''}>${mh.TENMH}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-1">
                            <label class="form-label">Nhóm</label>
                            <input type="number" name="nhom" class="form-control" min="1" value="${nhom != null ? nhom : 1}">
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-play"></i> Bắt đầu
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <%-- Bảng nhập điểm --%>
        <c:if test="${not empty dssv}">
            <div class="card card-custom">
                <div class="card-header">
                    <i class="fas fa-table"></i> Nhập điểm: ${tenmh} - Nhóm ${nhom}
                    (NK: ${nienkhoa}, HK: ${hocky})
                </div>
                <div class="card-body p-0">
                    <form action="${pageContext.request.contextPath}/diem/save" method="post"
                          onsubmit="return confirm('Ghi tất cả điểm?');">
                        <input type="hidden" name="maltc" value="${maltc}">
                        <input type="hidden" name="nienkhoa" value="${nienkhoa}">
                        <input type="hidden" name="hocky" value="${hocky}">
                        <input type="hidden" name="mamh" value="${mamh}">
                        <input type="hidden" name="nhom" value="${nhom}">
                        <div class="table-responsive">
                            <table class="table table-custom table-striped mb-0">
                                <thead><tr>
                                    <th>STT</th><th>Mã SV</th><th>Họ tên SV</th>
                                    <th class="text-center">Điểm CC</th>
                                    <th class="text-center">Điểm GK</th>
                                    <th class="text-center">Điểm CK</th>
                                    <th class="text-center">Điểm hết môn</th>
                                </tr></thead>
                                <tbody>
                                    <c:forEach items="${dssv}" var="sv" varStatus="st">
                                        <tr class="grade-row">
                                            <td>${st.index + 1}</td>
                                            <td>${sv.MASV}
                                                <input type="hidden" name="masv[]" value="${sv.MASV}">
                                            </td>
                                            <td>${sv.HOTENSV}</td>
                                            <td class="text-center">
                                                <input type="number" name="diemCC[]"
                                                       class="grade-input diem-cc"
                                                       min="0" max="10" step="1"
                                                       value="${sv.DIEM_CC}">
                                            </td>
                                            <td class="text-center">
                                                <input type="number" name="diemGK[]"
                                                       class="grade-input diem-gk"
                                                       min="0" max="10" step="0.5"
                                                       value="${sv.DIEM_GK}">
                                            </td>
                                            <td class="text-center">
                                                <input type="number" name="diemCK[]"
                                                       class="grade-input diem-ck"
                                                       min="0" max="10" step="0.5"
                                                       value="${sv.DIEM_CK}">
                                            </td>
                                            <td class="text-center">
                                                <span class="diem-hm grade-final">
                                                    <c:if test="${sv.DIEM_CC != null && sv.DIEM_GK != null && sv.DIEM_CK != null}">
                                                        <fmt:formatNumber value="${sv.DIEM_CC * 0.1 + sv.DIEM_GK * 0.3 + sv.DIEM_CK * 0.6}"
                                                                          maxFractionDigits="1"/>
                                                    </c:if>
                                                </span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="p-3 text-end">
                            <button type="submit" class="btn btn-success btn-lg">
                                <i class="fas fa-save"></i> Ghi điểm
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
