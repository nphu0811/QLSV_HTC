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

        <c:choose>
            <c:when test="${sessionScope.nhomQuyen == 'PGV'}">
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
                                <button type="button" class="btn btn-primary btn-action" onclick="btnThemLTC()">
                                    <i class="fas fa-plus"></i> Thêm</button>
                                <button type="button" class="btn btn-danger btn-action"
                                        onclick="btnXoaLTC('${pageContext.request.contextPath}/loptinchi/delete')">
                                    <i class="fas fa-trash"></i> Xóa</button>
                                <button type="submit" class="btn btn-success btn-action">
                                    <i class="fas fa-save"></i> Ghi</button>
                                <button type="button" class="btn btn-warning btn-action" onclick="btnPhucHoi()">
                                    <i class="fas fa-undo"></i> Phục hồi</button>
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
            </c:when>
            <c:otherwise>
                <div class="toolbar mb-3">
                    <button type="button" class="btn btn-secondary btn-action"
                            onclick="btnThoat('${pageContext.request.contextPath}/home')">
                        <i class="fas fa-sign-out-alt"></i> Thoát</button>
                </div>
            </c:otherwise>
        </c:choose>

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
                                <tr onclick="selectLTC(this, '${l.MALTC}')">
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
        if (inp) {
            var valToSet = f.textContent.trim();
            if (inp.tagName === 'SELECT') {
                var matched = false;
                for (var i = 0; i < inp.options.length; i++) {
                    if (inp.options[i].value.trim() === valToSet) {
                        inp.selectedIndex = i;
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    inp.value = valToSet;
                }
            } else {
                inp.value = valToSet;
            }
        }
    });
    // Clear dirty flag and warnings on selection
    var form = document.getElementById('ltcForm');
    if (form) {
        form.removeAttribute('data-dirty');
        if (typeof clearFormWarning === 'function') {
            clearFormWarning(form);
        }
    }
}

function getLTCText(row, fieldName) {
    var cell = row ? row.querySelector('[data-col="' + fieldName + '"]') : null;
    return cell ? cell.textContent.trim() : '';
}

function getLTCSignatureFromForm() {
    var form = document.getElementById('ltcForm');
    if (!form) return null;
    return {
        nienkhoa: (form.querySelector('[name="nienkhoa"]') || {}).value || '',
        hocky: (form.querySelector('[name="hocky"]') || {}).value || '',
        mamh: (form.querySelector('[name="mamh"]') || {}).value || '',
        nhom: (form.querySelector('[name="nhom"]') || {}).value || ''
    };
}

function getLTCSignatureFromRow(row) {
    return {
        nienkhoa: getLTCText(row, 'NIENKHOA'),
        hocky: getLTCText(row, 'HOCKY'),
        mamh: getLTCText(row, 'MAMH'),
        nhom: getLTCText(row, 'NHOM')
    };
}

function normalizeLTCValue(value) {
    return (value || '').toString().trim().toLowerCase();
}

function isSameLTCSignature(left, right) {
    if (!left || !right) return false;
    return normalizeLTCValue(left.nienkhoa) === normalizeLTCValue(right.nienkhoa)
        && normalizeLTCValue(left.hocky) === normalizeLTCValue(right.hocky)
        && normalizeLTCValue(left.mamh) === normalizeLTCValue(right.mamh)
        && normalizeLTCValue(left.nhom) === normalizeLTCValue(right.nhom);
}

function ltcExistsInCurrentTable(signature) {
    var rows = document.querySelectorAll('#ltcTable tbody tr');
    for (var i = 0; i < rows.length; i++) {
        if (isSameLTCSignature(signature, getLTCSignatureFromRow(rows[i]))) {
            return true;
        }
    }
    return false;
}

function getLTCSubjectName() {
    var select = document.querySelector('#ltcForm [name="mamh"]');
    if (!select || select.selectedIndex < 0) return '';
    return select.options[select.selectedIndex].text.trim();
}

function getLTCDetailText(signature) {
    if (!signature) return '';
    var subjectName = getLTCSubjectName() || signature.mamh;
    var parts = [];
    if (signature.nienkhoa) parts.push('niên khóa ' + signature.nienkhoa);
    if (signature.hocky) parts.push('học kỳ ' + signature.hocky);
    if (subjectName) parts.push('môn ' + subjectName);
    if (signature.nhom) parts.push('nhóm ' + signature.nhom);
    return parts.length ? ' (' + parts.join(', ') + ')' : '';
}

function getLTCAddModeWarningMessage() {
    var signature = getLTCSignatureFromForm();
    var detailText = getLTCDetailText(signature);

    if (ltcExistsInCurrentTable(signature)) {
        return 'Lớp tín chỉ' + detailText + ' đã tồn tại. Vui lòng đổi nhóm hoặc chọn dòng tương ứng để cập nhật.';
    }

    return 'Lớp tín chỉ' + detailText + ' chưa được ghi. Hãy nhấn "Ghi" để lưu, hoặc nhấn "Thêm" một lần nữa để làm trống form.';
}

function btnThemLTC() {
    var form = document.getElementById('ltcForm');
    if (!form) return;
    
    var isDirty = form.getAttribute('data-dirty') === 'true';
    var confirmClear = form.getAttribute('data-confirm-clear') === 'true';
    
    if (isDirty && !confirmClear) {
        if (typeof showFormWarning === 'function') {
            showFormWarning(form, getLTCAddModeWarningMessage());
        } else {
            if (!confirm(getLTCAddModeWarningMessage())) return;
        }
        form.setAttribute('data-confirm-clear', 'true');
        return;
    }
    
    if (typeof clearFormWarning === 'function') {
        clearFormWarning(form);
    }
    form.removeAttribute('data-dirty');
    
    document.getElementById('ltcAction').value = 'add';
    document.getElementById('ltcMaltc').value = '';
    document.querySelectorAll('#ltcForm input[type="text"], #ltcForm input[type="number"]').forEach(function(i){i.value='';});
    document.querySelectorAll('#ltcTable tbody tr').forEach(function(r){r.classList.remove('selected');});
}

document.addEventListener('DOMContentLoaded', function() {
    var form = document.getElementById('ltcForm');
    if (!form) return;

    form.addEventListener('submit', function(event) {
        var actionField = document.getElementById('ltcAction');
        if (!actionField || actionField.value !== 'add') return;

        var signature = getLTCSignatureFromForm();
        if (ltcExistsInCurrentTable(signature)) {
            event.preventDefault();
            if (typeof showFormWarning === 'function') {
                showFormWarning(form, 'Lớp tín chỉ' + getLTCDetailText(signature) + ' đã tồn tại. Vui lòng đổi nhóm hoặc chọn dòng tương ứng để cập nhật.');
            }
        }
    });
});
</script>
</body>
</html>
