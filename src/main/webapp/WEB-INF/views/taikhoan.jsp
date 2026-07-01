<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>QLDSV HTC - Tạo Tài Khoản</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=20260508" rel="stylesheet">
</head>
<body>
<%@ include file="layout/header.jsp" %>
<div class="d-flex">
    <%@ include file="layout/sidebar.jsp" %>
    <main class="content-area">
        <div class="content py-4">
            <div class="container d-flex justify-content-center">
        <div class="card shadow-sm" style="width: 800px; background-color: #e0f2fe; border: 2px solid #7dd3fc; border-radius:10px;">
            <div class="card-body p-5">
                <h3 class="text-center mb-5" style="color: #075985; font-family: serif; font-weight: bold;">
                    TẠO TÀI KHOẢN ĐĂNG NHẬP CHƯƠNG TRÌNH
                </h3>

                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/taikhoan/save" method="post" id="tkForm">
                    <div class="row mb-4 align-items-center">
                        <label class="col-sm-3 col-form-label text-end text-dark">Họ tên nhân viên</label>
                        <div class="col-sm-5">
                            <select id="selectGv" class="form-select bg-light" style="border: 1px solid #aaa;"
                                    onchange="onGvChange()">
                                <option value="">-- Chọn Giảng viên --</option>
                                <c:forEach items="${dsgv}" var="gv">
                                    <option value="${gv.MAGV}"
                                            data-login="${gv.Login}"
                                            data-mk="${gv.MatKhau}"
                                            data-quyen="${gv.NhomQuyen}"
                                            data-makhoa="${gv.MAKHOA}">
                                        ${gv.HOTEN}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <label class="col-sm-1 col-form-label text-end text-dark text-nowrap">Mã NV</label>
                        <div class="col-sm-3">
                            <input type="text" id="inputMaGV" name="magv" class="form-control bg-white fw-bold"
                                   style="border: 1px solid #777;" readonly required>
                        </div>
                    </div>

                    <div class="row mb-4 align-items-center">
                        <label class="col-sm-3 col-form-label text-end text-dark">Tài khoản</label>
                        <div class="col-sm-9">
                            <input type="text" id="inputLogin" name="login" class="form-control"
                                   style="border: 1px solid #0284c7;" readonly required>
                        </div>
                    </div>

                    <div class="row mb-4 align-items-center">
                        <label class="col-sm-3 col-form-label text-end text-dark">Mật mã</label>
                        <div class="col-sm-9">
                            <input type="password" id="inputMatKhau" name="matkhau" class="form-control"
                                   style="border: 1px solid #999;" required>
                        </div>
                    </div>

                    <div class="row mb-5 align-items-center">
                        <label class="col-sm-3 col-form-label text-end text-dark">Nhóm quyền</label>
                        <div class="col-sm-9">
                            <select id="inputQuyen" name="nhomQuyen" class="form-select"
                                    style="border: 1px solid #999;" required onchange="onQuyenChange()">
                                <c:if test="${sessionScope.nhomQuyen == 'PGV'}">
                                    <option value="PGV">PGV (Phòng Giáo vụ)</option>
                                </c:if>
                                <option value="KHOA">KHOA</option>
                            </select>
                        </div>
                    </div>

                    <div class="row mb-5 align-items-center" id="divKhoa" style="display:none;">
                        <label class="col-sm-3 col-form-label text-end text-dark">Khoa</label>
                        <div class="col-sm-9">
                            <select id="inputKhoa" name="maKhoa" class="form-select" style="border: 1px solid #999;"
                                    ${sessionScope.nhomQuyen == 'KHOA' ? 'disabled' : ''}>
                                <c:forEach items="${khoaList}" var="k">
                                    <option value="${k.MAKHOA}" ${sessionScope.nhomQuyen == 'KHOA' && sessionScope.maKhoa == k.MAKHOA ? 'selected' : ''}>${k.TENKHOA}</option>
                                </c:forEach>
                            </select>
                            <c:if test="${sessionScope.nhomQuyen == 'KHOA'}">
                                <input type="hidden" name="maKhoa" value="${sessionScope.maKhoa}">
                            </c:if>
                        </div>
                    </div>

                    <div class="d-flex justify-content-center gap-3 mt-4">
                        <button type="submit" class="btn btn-success fw-bold text-white px-4 py-2" id="btnGhi">
                            <i class="fas fa-save"></i> Ghi (Tạo/Sửa)
                        </button>
                        <button type="button" class="btn btn-danger fw-bold text-white px-4 py-2" id="btnXoa" onclick="deleteTk()">
                            <i class="fas fa-trash"></i> Xóa
                        </button>
                        <button type="button" class="btn btn-warning fw-bold text-white px-4 py-2" onclick="resetForm()">
                            <i class="fas fa-undo"></i> Phục hồi
                        </button>
                        <button type="button" class="btn btn-secondary fw-bold text-white px-4 py-2"
                                onclick="window.location.href='${pageContext.request.contextPath}/home'">
                            <i class="fas fa-sign-out-alt"></i> Thoát
                        </button>
                    </div>
                </form>

                <form action="${pageContext.request.contextPath}/taikhoan/delete" method="post" id="deleteForm" style="display:none;">
                    <input type="hidden" name="magv" id="deleteMaGV">
                </form>

            </div>
        </div>
    </div>
</div>

<script>
    function resetForm() {
        // Hủy bỏ nhập liệu đang sai dở, khôi phục lại ban đầu dựa vào GV đang select
        var dp = document.getElementById("selectGv");
        if(dp.selectedIndex > 0) {
            onGvChange();
        } else {
            document.getElementById("tkForm").reset();
        }
    }
    function onGvChange() {
        var userRole = "${sessionScope.nhomQuyen}";
        var dp = document.getElementById("selectGv");
        var selected = dp.options[dp.selectedIndex];
        var magv = selected.value;

        document.getElementById("inputMaGV").value = magv;

        var isPgvAccount = false;

        if(magv !== "") {
            var login = selected.getAttribute("data-login");
            var quyen = selected.getAttribute("data-quyen");
            var makhoa = selected.getAttribute("data-makhoa");

            if (quyen === 'PGV') {
                isPgvAccount = true;
            }

            if(login && login !== "") {
                document.getElementById("inputLogin").value = login;
                document.getElementById("inputMatKhau").value = "";
                if(quyen) {
                    var inputQ = document.getElementById("inputQuyen");
                    if (inputQ.querySelector("option[value='" + quyen + "']")) {
                        inputQ.value = quyen;
                    }
                }
                if(quyen === 'KHOA' && makhoa) {
                    document.getElementById("inputKhoa").value = makhoa;
                }
            } else {
                document.getElementById("inputLogin").value = magv;
                document.getElementById("inputMatKhau").value = "";
                document.getElementById("inputQuyen").selectedIndex = 0;
            }
        } else {
            document.getElementById("inputLogin").value = "";
            document.getElementById("inputMatKhau").value = "";
            document.getElementById("inputQuyen").selectedIndex = 0;
        }

        if (userRole === 'KHOA' && isPgvAccount) {
            document.getElementById("btnGhi").disabled = true;
            document.getElementById("btnXoa").disabled = true;
            alert("Tài khoản này thuộc nhóm PGV. Nhóm KHOA không có quyền thay đổi!");
        } else {
            document.getElementById("btnGhi").disabled = false;
            document.getElementById("btnXoa").disabled = false;
        }
        onQuyenChange();
    }

    function onQuyenChange() {
        var quyen = document.getElementById("inputQuyen").value;
        if(quyen === 'KHOA') {
            document.getElementById("divKhoa").style.display = "flex";
        } else {
            document.getElementById("divKhoa").style.display = "none";
        }
    }

    function deleteTk() {
        var magv = document.getElementById("inputMaGV").value;
        if(!magv) {
            alert("Vui lòng chọn nhân viên muốn xóa tài khoản!");
            return;
        }
        var dp = document.getElementById("selectGv");
        var selected = dp.options[dp.selectedIndex];
        var login = selected.getAttribute("data-login");
        if(!login || login === "") {
            alert("Nhân viên này chưa có tài khoản để xóa!");
            return;
        }

        if(confirm("Bạn có chắc chắn muốn xóa tài khoản của giảng viên " + selected.text + " không?")) {
            document.getElementById("deleteMaGV").value = magv;
            document.getElementById("deleteForm").submit();
        }
    }
</script>

            </div>
        </div>
    </main>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
