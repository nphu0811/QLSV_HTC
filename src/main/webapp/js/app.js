/**
 * QLDSV_HTC - Client-side JavaScript & GSAP Animations
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
                    var valToSet = cell.textContent.trim();
                    if (input.tagName === 'SELECT') {
                        // Handle display values (like gender & status)
                        if (valToSet === 'Nữ') valToSet = 'true';
                        if (valToSet === 'Nam') valToSet = 'false';
                        if (valToSet === 'Có') valToSet = 'true';
                        if (valToSet === 'Không') valToSet = 'false';
                        
                        // Robust match for option value (ignores NCHAR trailing spaces)
                        var matched = false;
                        for (var i = 0; i < input.options.length; i++) {
                            if (input.options[i].value.trim() === valToSet) {
                                input.selectedIndex = i;
                                matched = true;
                                break;
                            }
                        }
                        if (!matched) {
                            input.value = valToSet;
                        }
                    } else {
                        input.value = valToSet;
                    }
                }
            });
            // Clear dirty state and alerts since programmatically loaded
            var form = document.getElementById(formPrefix + 'Form');
            if (form) {
                form.removeAttribute('data-dirty');
                clearFormWarning(form);
            }
            // Set action to update
            var actionField = document.getElementById(formPrefix + 'Action');
            if (actionField) actionField.value = 'update';
            // Disable primary key field on update
            var pkField = document.getElementById(formPrefix + 'PK');
            if (pkField) pkField.readOnly = true;

            // Micro-animation on selection
            if (typeof gsap !== 'undefined') {
                gsap.fromTo(this, { backgroundColor: 'rgba(91, 91, 214, 0.15)' }, { backgroundColor: '', duration: 0.4 });
            }
        });
    });
}

// ===== CRUD BUTTON HANDLERS =====
function btnThem(formPrefix) {
    var form = document.getElementById(formPrefix + 'Form');
    if (!form) return;
    
    var isDirty = form.getAttribute('data-dirty') === 'true';
    var confirmClear = form.getAttribute('data-confirm-clear') === 'true';

    if (isDirty && !confirmClear) {
        showFormWarning(form, 'Dữ liệu đang nhập chưa được lưu. Để lưu lại, vui lòng nhấn nút "Ghi". Hoặc nhấn nút "Thêm" một lần nữa để xác nhận xóa sạch dữ liệu.');
        form.setAttribute('data-confirm-clear', 'true');
        return;
    }

    clearFormWarning(form);
    form.removeAttribute('data-dirty');

    // Clear form for new entry (including date fields)
    var inputs = form.querySelectorAll('input[type="text"], input[type="number"], input[type="date"], select');
    inputs.forEach(function(inp) { 
        inp.value = ''; 
        inp.readOnly = false; 
        if (inp.tagName === 'SELECT') {
            inp.selectedIndex = 0; // Default to first option
        }
    });
    
    var actionField = document.getElementById(formPrefix + 'Action');
    if (actionField) actionField.value = 'add';
    var pkField = document.getElementById(formPrefix + 'PK');
    if (pkField) { pkField.readOnly = false; pkField.focus(); }
    // Deselect table rows
    document.querySelectorAll('.table-custom tbody tr').forEach(function(r) {
        r.classList.remove('selected');
    });

    // Form shake transition to indicate edit ready
    if (typeof gsap !== 'undefined') {
        gsap.fromTo('.card-custom:first-of-type', { y: -5 }, { y: 0, duration: 0.3, ease: 'bounce.out' });
    }
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
    
    var finalEl = row.querySelector('.diem-hm');
    if (finalEl) {
        var prevVal = parseFloat(finalEl.textContent) || 0;
        finalEl.textContent = diemHM.toFixed(1);
        
        // Pulsate score display if it changes
        if (typeof gsap !== 'undefined' && prevVal !== diemHM) {
            gsap.fromTo(finalEl, { scale: 1.3, color: '#0EA5A4' }, { scale: 1, color: '#5B5BD6', duration: 0.3 });
        }
    }
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

// ===== RESPONSIVE SIDEBAR TOGGLE (GSAP POWERED) =====
function initSidebarToggle() {
    var sidebarToggle = document.getElementById('sidebarToggle');
    var appSidebar = document.getElementById('appSidebar');
    var sidebarScrim = document.getElementById('sidebarScrim');
    if (!sidebarToggle || !appSidebar) return;

    if (typeof gsap !== 'undefined') {
        appSidebar.style.transition = 'none';
    }

    sidebarToggle.addEventListener('click', function() {
        var isExpanded = sidebarToggle.getAttribute('aria-expanded') === 'true';
        sidebarToggle.setAttribute('aria-expanded', !isExpanded);
        
        if (typeof gsap !== 'undefined') {
            if (!isExpanded) {
                appSidebar.classList.add('show');
                if (sidebarScrim) sidebarScrim.classList.add('show');
                gsap.killTweensOf(appSidebar);
                gsap.fromTo(appSidebar, { x: '-100%' }, { x: '0%', duration: 0.35, ease: 'power2.out' });
                if (sidebarScrim) gsap.fromTo(sidebarScrim, { opacity: 0 }, { opacity: 1, duration: 0.35 });
            } else {
                gsap.killTweensOf(appSidebar);
                gsap.to(appSidebar, { x: '-100%', duration: 0.3, ease: 'power2.in', onComplete: function() {
                    appSidebar.classList.remove('show');
                }});
                if (sidebarScrim) {
                    gsap.to(sidebarScrim, { opacity: 0, duration: 0.3, onComplete: function() {
                        sidebarScrim.classList.remove('show');
                    }});
                }
            }
        } else {
            // Fallback if GSAP is not loaded
            appSidebar.classList.toggle('show');
            if (sidebarScrim) sidebarScrim.classList.toggle('show');
        }
    });

    if (sidebarScrim) {
        sidebarScrim.addEventListener('click', function() {
            sidebarToggle.click();
        });
    }
}

// ===== ENTRANCE ANIMATIONS =====
function runEntranceAnimations() {
    if (typeof gsap === 'undefined') return;

    // Check if we are on the login page (has .login-card)
    if (document.querySelector('.login-card')) {
        runLoginAnimations();
        return;
    }

    var sidebar = document.querySelector('.sidebar');
    if (sidebar) {
        sidebar.style.transition = 'none';
    }

    // Main App Page load animation sequence
    var tl = gsap.timeline();

    // 1. Navbar slide down
    tl.from('.navbar-custom', {
        y: -64,
        opacity: 0,
        duration: 0.45,
        ease: 'power2.out'
    });

    // 2. Sidebar slide in
    tl.from('.sidebar', {
        x: -260,
        opacity: 0,
        duration: 0.4,
        ease: 'power2.out'
    }, '-=0.25');

    // 3. Sidebar items stagger
    tl.from('.sidebar .nav-item', {
        x: -20,
        opacity: 0,
        duration: 0.35,
        stagger: 0.04,
        ease: 'power2.out'
    }, '-=0.2');

    // 4. Page Header slide in
    tl.from('.page-header', {
        x: -30,
        opacity: 0,
        duration: 0.4,
        ease: 'power2.out'
    }, '-=0.2');

    // 5. Cards scale and rise
    tl.from('.card-custom', {
        y: 25,
        opacity: 0,
        scale: 0.98,
        duration: 0.5,
        stagger: 0.08,
        ease: 'power2.out'
    }, '-=0.25');

    // 6. Table rows stagger (if table present)
    if (document.querySelector('.table-custom tbody tr')) {
        tl.from('.table-custom tbody tr', {
            y: 12,
            opacity: 0,
            duration: 0.3,
            stagger: 0.02,
            ease: 'power1.out'
        }, '-=0.2');
    }

    // 7. List group items stagger (if list present)
    if (document.querySelector('.list-group-item')) {
        tl.from('.list-group-item', {
            x: -15,
            opacity: 0,
            duration: 0.3,
            stagger: 0.03,
            ease: 'power2.out'
        }, '-=0.3');
    }
}

// ===== LOGIN SPECIFIC HUD ANIMATIONS =====
function runLoginAnimations() {
    var tl = gsap.timeline();

    // 1. Entrance of wrapper background glow effects
    tl.from('.login-wrapper', {
        backgroundColor: '#05070a',
        duration: 1
    });

    // 2. Card slide-up and fade-in
    tl.from('.login-card', {
        y: 60,
        opacity: 0,
        scale: 0.97,
        duration: 0.8,
        ease: 'power3.out'
    }, '-=0.7');

    // 3. Header items staggered
    tl.from('.login-card .card-header > *', {
        y: -20,
        opacity: 0,
        duration: 0.5,
        stagger: 0.1,
        ease: 'power2.out'
    }, '-=0.4');

    // 4. Tab navigation pills scale-in
    tl.from('.login-card .nav-pills', {
        scale: 0.95,
        opacity: 0,
        duration: 0.4,
        ease: 'power2.out'
    }, '-=0.2');

    // 5. Active form content elements
    tl.from('.login-card .tab-pane.active form > *:not([type="hidden"])', {
        y: 15,
        opacity: 0,
        duration: 0.45,
        stagger: 0.08,
        ease: 'power2.out'
    }, '-=0.2');

    // Add tab click change animation trigger
    var tabElList = [].slice.call(document.querySelectorAll('.login-card button[data-bs-toggle="pill"]'));
    tabElList.forEach(function(tabEl) {
        tabEl.addEventListener('shown.bs.tab', function(event) {
            var targetId = event.target.getAttribute('data-bs-target');
            var pane = document.querySelector(targetId);
            if (pane) {
                gsap.fromTo(pane.querySelectorAll('form > *:not([type="hidden"])'), 
                    { y: 15, opacity: 0 }, 
                    { y: 0, opacity: 1, duration: 0.4, stagger: 0.06, ease: 'power2.out' }
                );
            }
        });
    });
}

// ===== INLINE FORM WARNING HELPERS =====
function showFormWarning(form, message) {
    if (!form) return;
    var warningDiv = form.querySelector('.form-alert-warning');
    if (!warningDiv) {
        warningDiv = document.createElement('div');
        warningDiv.className = 'alert alert-warning alert-dismissible fade show form-alert-warning mt-2 mb-2 shadow-sm';
        warningDiv.style.fontSize = '0.875rem';
        warningDiv.innerHTML = 
            '<i class="fas fa-exclamation-triangle"></i> <span class="warning-text"></span>' +
            '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';
        form.insertBefore(warningDiv, form.firstChild);
    }
    warningDiv.querySelector('.warning-text').textContent = message;
    warningDiv.style.display = 'block';
}

function clearFormWarning(form) {
    if (!form) return;
    var warningDiv = form.querySelector('.form-alert-warning');
    if (warningDiv) {
        warningDiv.style.display = 'none';
    }
    form.removeAttribute('data-confirm-clear');
}

function initFormDirtyListeners() {
    var forms = document.querySelectorAll('form');
    forms.forEach(function(form) {
        if (form.id === 'deleteForm' || form.id === 'deleteFormLTC' || form.id === 'deleteFormDiem') return;
        
        var inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(function(input) {
            if (input.type === 'hidden' || input.id === 'svPK' || input.id === 'gvPK' || input.id === 'lopPK' || input.id === 'mhPK' || input.id === 'ltcMaltc') return;
            
            var handler = function() {
                form.setAttribute('data-dirty', 'true');
                form.removeAttribute('data-confirm-clear');
            };
            input.addEventListener('input', handler);
            input.addEventListener('change', handler);
        });
    });
}

// ===== DOCUMENT READY =====
document.addEventListener('DOMContentLoaded', function() {
    // Initialize sidebar controls
    initSidebarToggle();

    // Initialize dirty listeners for all forms
    initFormDirtyListeners();

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
            if (typeof gsap !== 'undefined') {
                gsap.to(alert, { opacity: 0, height: 0, padding: 0, marginBottom: 0, duration: 0.5, onComplete: function() { alert.remove(); } });
            } else {
                alert.style.transition = 'all 0.5s';
                alert.style.opacity = '0';
                setTimeout(function() { alert.remove(); }, 500);
            }
        }, 5000);
    });

    // Run custom entrance animations using GSAP
    runEntranceAnimations();
});

// ===== PRINT REPORT =====
function printReport() {
    window.print();
}
