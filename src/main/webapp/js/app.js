/**
 * QLDSV_HTC - Client-side JavaScript
 */

// ===== TABLE ROW SELECTION =====
function initTableSelection(tableId, formPrefix) {
    var table = document.getElementById(tableId);
    if (!table) return;
    var rows = table.querySelectorAll('tbody tr');
    rows.forEach(function(row) {
        row.addEventListener('click', function() {
            // Deselect all
            rows.forEach(function(r) { r.classList.remove('selected'); });
            // Select clicked
            this.classList.add('selected');
            // Populate form fields from row data attributes
            var cells = this.querySelectorAll('td');
            var inputs = document.querySelectorAll('[data-field]');
            inputs.forEach(function(input) {
                var field = input.getAttribute('data-field');
                var cell = row.querySelector('[data-col="' + field + '"]');
                if (cell) {
                    input.value = cell.textContent.trim();
                }
            });
            // Set action to update
            var actionField = document.getElementById(formPrefix + 'Action');
            if (actionField) actionField.value = 'update';
            // Disable primary key field on update
            var pkField = document.getElementById(formPrefix + 'PK');
            if (pkField) pkField.readOnly = true;
        });
    });
}

// ===== CRUD BUTTON HANDLERS =====
function btnThem(formPrefix) {
    // Clear form for new entry
    var form = document.getElementById(formPrefix + 'Form');
    if (form) {
        var inputs = form.querySelectorAll('input[type="text"], input[type="number"], select');
        inputs.forEach(function(inp) { inp.value = ''; inp.readOnly = false; });
    }
    var actionField = document.getElementById(formPrefix + 'Action');
    if (actionField) actionField.value = 'add';
    var pkField = document.getElementById(formPrefix + 'PK');
    if (pkField) { pkField.readOnly = false; pkField.focus(); }
    // Deselect table rows
    document.querySelectorAll('.table-custom tbody tr').forEach(function(r) {
        r.classList.remove('selected');
    });
}

function btnXoa(formPrefix, deleteUrl) {
    var pkField = document.getElementById(formPrefix + 'PK');
    if (!pkField || !pkField.value.trim()) {
        alert('Vui lòng chọn dòng cần xóa!');
        return;
    }
    if (confirm('Bạn có chắc chắn muốn xóa?')) {
        var form = document.createElement('form');
        form.method = 'POST';
        form.action = deleteUrl;
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = pkField.name;
        input.value = pkField.value;
        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    }
}

function btnPhucHoi() {
    // Reload the page to restore original data
    window.location.reload();
}

function btnThoat(homeUrl) {
    window.location.href = homeUrl;
}

// ===== AUTO-CALCULATE GRADE =====
function tinhDiemHetMon(row) {
    var cc = parseFloat(row.querySelector('.diem-cc').value) || 0;
    var gk = parseFloat(row.querySelector('.diem-gk').value) || 0;
    var ck = parseFloat(row.querySelector('.diem-ck').value) || 0;
    var diemHM = cc * 0.1 + gk * 0.3 + ck * 0.6;
    row.querySelector('.diem-hm').textContent = diemHM.toFixed(1);
}

function initGradeCalculation() {
    document.querySelectorAll('.grade-row').forEach(function(row) {
        row.querySelectorAll('.grade-input').forEach(function(input) {
            input.addEventListener('input', function() {
                tinhDiemHetMon(row);
            });
            input.addEventListener('change', function() {
                // Validate range 0-10
                var val = parseFloat(this.value);
                if (isNaN(val) || val < 0) this.value = 0;
                if (val > 10) this.value = 10;
                // Round GK, CK to 0.5
                if (this.classList.contains('diem-gk') || this.classList.contains('diem-ck')) {
                    this.value = (Math.round(val * 2) / 2).toFixed(1);
                }
                tinhDiemHetMon(row);
            });
        });
    });
}

// ===== CONFIRM SUBMIT =====
function confirmSubmit(msg) {
    return confirm(msg || 'Bạn có chắc chắn?');
}

// ===== DOCUMENT READY =====
document.addEventListener('DOMContentLoaded', function() {
    // Highlight active sidebar link
    var currentPath = window.location.pathname;
    document.querySelectorAll('.sidebar .nav-link').forEach(function(link) {
        if (currentPath.indexOf(link.getAttribute('href')) !== -1 && link.getAttribute('href') !== '/') {
            link.classList.add('active');
        }
    });

    // Init grade calculation if on diem page
    if (document.querySelector('.grade-row')) {
        initGradeCalculation();
    }

    // Auto-dismiss alerts after 5s
    document.querySelectorAll('.alert-dismissible').forEach(function(alert) {
        setTimeout(function() {
            alert.style.transition = 'opacity 0.5s';
            alert.style.opacity = '0';
            setTimeout(function() { alert.remove(); }, 500);
        }, 5000);
    });
});

// ===== PRINT REPORT =====
function printReport() {
    window.print();
}
