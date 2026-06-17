<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Lớp tín chỉ</title>
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
            <h4><i class="fas fa-chalkboard"></i> Mở Lớp tín chỉ</h4>
            <p>Quản lý lớp tín chỉ thuộc khoa đang chọn</p>
        </div>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show">${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show">${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button></div>
        </c:if>

        <div class="card card-custom mb-3">
            <div class="card-header"><i class="fas fa-edit"></i> Thông tin Lớp tín chỉ</div>
            <div class="card-body">
                <form id="ltcForm" action="${pageContext.request.contextPath}/loptinchi/save" method="post">
                    <input type="hidden" id="ltcAction" name="action" value="add">
                    <input type="hidden" id="ltcMaltc" name="maltc" value="">
                    <div class="row g-3">
                        <div class="col-md-2">
                            <label class="form-label">Niên khóa</label>
                            <input type="text" name="nienkhoa" data-field="NIENKHOA"
                                   class="form-control" placeholder="2021-2022" maxlength="9" required>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Học kỳ</label>
                            <select name="hocky" data-field="HOCKY" class="form-select" required>
                                <option value="1">1</option>
                                <option value="2">2</option>
                                <option value="3">3</option>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">Môn học</label>
                            <select name="mamh" data-field="MAMH" class="form-select" required>
                                <c:forEach items="${dsmh}" var="mh">
                                    <option value="${mh.MAMH}">${mh.TENMH}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-1">
                            <label class="form-label">Nhóm</label>
                            <input type="number" name="nhom" data-field="NHOM"
                                   class="form-control" min="1" value="1" required>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">Giảng viên</label>
                            <select name="magv" data-field="MAGV" class="form-select" required>
                                <c:forEach items="${dsgv}" var="gv">
                                    <option value="${gv.MAGV}">${gv.HOTENGV}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label">SV tối thiểu</label>
                            <input type="number" name="sosvtoithieu" data-field="SOSVTOITHIEU"
                                   class="form-control" min="1" value="10" required>
                        </div>
                        <c:choose>
                            <c:when test="${sessionScope.nhomQuyen == 'PGV'}">
                                <div class="col-md-3">
                                    <label class="form-label">Khoa</label>
                                    <select name="maKhoa" data-field="MAKHOA" class="form-select" required>
                                        <c:forEach items="${khoaList}" var="k">
                                            <option value="${k.MAKHOA}">${k.TENKHOA}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="maKhoa" data-field="MAKHOA" value="${sessionScope.maKhoa}">
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="toolbar mt-3">
                        <c:if test="${sessionScope.nhomQuyen == 'PGV'}">
                            <button type="button" class="btn btn-primary btn-action" onclick="btnThemLTC()">
                                <i class="fas fa-plus"></i> Thêm</button>
                            <button type="button" class="btn btn-danger btn-action"
                                    onclick="btnXoaLTC('${pageContext.request.contextPath}/loptinchi/delete')">
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
                <form id="deleteFormLTC" action="${pageContext.request.contextPath}/loptinchi/delete" method="post" style="display:none;">
                    <input type="hidden" id="deleteMaltc" name="maltc" value="">
                </form>
            </div>
        </div>

        <div class="card card-custom">
            <div class="card-header"><i class="fas fa-list"></i> Danh sách Lớp tín chỉ</div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table id="ltcTable" class="table table-custom table-striped table-hover mb-0">
                        <thead><tr>
                            <th>MALTC</th><th>Niên khóa</th><th>HK</th><th>Môn học</th>
                            <th>Nhóm</th><th>Giảng viên</th><th>SV tối thiểu</th><th>SV đã ĐK</th><th>Hủy</th>
                        </tr></thead>
                        <tbody>
                            <c:forEach items="${dsltc}" var="l">
                                <tr onclick="selectLTC(this, ${l.MALTC})">
                                    <td data-col="MALTC">${l.MALTC}</td>
                                    <td data-col="NIENKHOA">${l.NIENKHOA}</td>
                                    <td data-col="HOCKY">${l.HOCKY}</td>
                                    <td>${l.TENMH} <span class="d-none" data-col="MAMH">${l.MAMH}</span></td>
                                    <td data-col="NHOM">${l.NHOM}</td>
                                    <td>${l.HOTENGV} <span class="d-none" data-col="MAGV">${l.MAGV}</span></td>
                                    <td data-col="SOSVTOITHIEU">${l.SOSVTOITHIEU} <span class="d-none" data-col="MAKHOA">${l.MAKHOA}</span></td>
                                    <td>${l.SOSVDK}</td>
                                    <td>${l.HUYLOP == true ? 'Đã hủy' : ''}</td>
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
function selectLTC(row, maltc) {
    document.querySelectorAll('#ltcTable tbody tr').forEach(function(r){r.classList.remove('selected');});
    row.classList.add('selected');
    document.getElementById('ltcMaltc').value = maltc;
    document.getElementById('deleteMaltc').value = maltc;
    document.getElementById('ltcAction').value = 'update';
    // Fill form
    var fields = row.querySelectorAll('[data-col]');
    fields.forEach(function(f){
        var inp = document.querySelector('[data-field="'+f.getAttribute('data-col')+'"]');
        if(inp) inp.value = f.textContent.trim();
    });
}
function btnThemLTC() {
    document.getElementById('ltcAction').value = 'add';
    document.getElementById('ltcMaltc').value = '';
    document.querySelectorAll('#ltcForm input[type="text"], #ltcForm input[type="number"]').forEach(function(i){i.value='';});
    document.querySelectorAll('#ltcTable tbody tr').forEach(function(r){r.classList.remove('selected');});
}
</script>
</body>
</html>
