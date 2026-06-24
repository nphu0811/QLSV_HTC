# Walkthrough - Báo Cáo Nghiệm Thu & Kiểm Thử QLSV_HTC

Tài liệu này tóm tắt kết quả sửa đổi dự án **QLSV_HTC** nhằm đáp ứng đầy đủ 8 ưu tiên trong Đề 3 môn Hệ quản trị cơ sở dữ liệu và các quy định bảo mật của giảng viên.

## 1. Kết quả chỉnh sửa
- **Sắp xếp file SQL (Yêu cầu mới):** Đã gộp toàn bộ stored procedure mới vào file `stored_procedures.sql` gốc và toàn bộ phân quyền mới vào file `setup_security.sql` gốc. Hai file SQL patch cũ đã được xóa hoàn toàn khỏi thư mục `sql/`.
- **Sửa lỗi trang Tài khoản:** Đã tạo stored procedure `SP_DanhSachTaiKhoanGiangVienCoKhoa` trong file `stored_procedures.sql` gốc và phân quyền thực thi cho KHOA và PGV. Trang `/taikhoan` đã hoạt động bình thường, không còn lỗi 500.
- **An toàn kết nối (Không dùng `sa`):** Đã cấu hình `ConnectionHelper` và `LoginController` sử dụng login `sv` để xác thực đăng nhập thay vì dùng `sa`. Chặn hoàn toàn fallback `sa` cho KHOA và PGV.
- **Đăng ký hộ LTC (Ưu tiên 1):** Sửa `DangKyController` cho phép PGV nhập `masv` đăng ký hộ, chặn KHOA, SV chỉ được thao tác của chính mình (lấy mã SV từ session ẩn).
- **Quản lý danh mục Khoa (Ưu tiên 2):** Bổ sung `KhoaController`, `khoa.jsp` và các stored procedure tương ứng. PGV có quyền CRUD, KHOA chỉ được xem, SV bị chặn hoàn toàn.
- **Chặn dữ liệu quá khứ (Ưu tiên 3):** Validate khóa học (`KHOAHOC`) và niên khóa (`NIENKHOA`) nghiêm ngặt (định dạng `YYYY-YYYY`, năm sau = năm trước + 1, năm bắt đầu $\ge 2026$).
- **Hoàn thiện phân quyền nhóm KHOA (Ưu tiên 4, 7):** Khóa/ẩn form CRUD trên tất cả các view đối với KHOA. Chặn KHOA xem lớp/sinh viên khoa khác. KHOA chỉ được tạo/xóa tài khoản nhóm KHOA thuộc giảng viên khoa mình.
- **Nhập điểm & Transaction (Ưu tiên 5):** Sửa `DiemController` validate điểm CC (nguyên 0-10), GK/CK (bước 0.5 từ 0-10). Áp dụng Connection Transaction thủ công để rollback toàn bộ khi có lỗi trên bất kỳ dòng sinh viên nào.
- **Báo cáo phân quyền (Ưu tiên 6):** SV chỉ xem phiếu điểm cá nhân. KHOA chỉ xem báo cáo khoa mình. PGV chọn khoa khi in danh sách LTC.

---

## 2. Hướng dẫn tài khoản kiểm thử
Hệ thống sử dụng các tài khoản kiểm thử mặc định sau để phân quyền:

| Phân hệ / Nhóm | Tài khoản (Login) | Mật khẩu (Password) | SQL Server Login tương ứng |
| :--- | :--- | :--- | :--- |
| **Phòng Giáo Vụ (PGV)** | `pgv_admin` | `123456` | `pgv_admin` (pass: `123456`) |
| **Khoa Công nghệ thông tin** | `khoa_cntt` | `khoa123` | `khoa_cntt` (pass: `khoa123`) |
| **Khoa Viễn thông** | `khoa_vt` | `khoa456` | `khoa_vt` (pass: `khoa456`) |
| **Sinh viên (SV)** | `N15DCCN001` | *Không cần mật khẩu* | `sv` (pass: `sv123`) |

---

## 3. Kịch bản kiểm thử (Manual Verification)

### Kịch bản 1: Phòng Giáo Vụ (PGV)
1. Đăng nhập bằng `pgv_admin` / `123456`.
2. Truy cập menu **Khoa**:
   - Nhấn **Thêm**, nhập mã khoa `TEST` và tên khoa `Khoa Test`.
   - Nhấn **Ghi** để lưu $\rightarrow$ Hệ thống báo thêm thành công.
   - Chọn dòng `TEST` vừa thêm, sửa tên thành `Khoa Test 2`, nhấn **Ghi** $\rightarrow$ Hệ thống cập nhật thành công.
   - Nhấn **Xóa** và xác nhận $\rightarrow$ Hệ thống xóa khoa thành công.
3. Truy cập menu **Đăng ký hộ LTC**:
   - Nhập mã sinh viên `N15DCCN001` $\rightarrow$ Nhấn **Tìm sinh viên**.
   - Giao diện tự động hiển thị thông tin chi tiết của sinh viên `N15DCCN001` và các lớp đã đăng ký.
   - Chọn niên khóa, học kỳ và đăng ký/hủy lớp hộ sinh viên đó thành công.

### Kịch bản 2: Khoa (KHOA - ví dụ CNTT)
1. Đăng nhập bằng `khoa_cntt` / `khoa123`.
2. Truy cập các menu **Khoa**, **Lớp**, **Môn học**, **Sinh viên**, **Giảng viên**, **Lớp tín chỉ**:
   - Form nhập liệu và các nút CRUD đều bị ẩn.
   - Chỉ hiển thị dữ liệu để xem.
3. Truy cập menu **Nhập điểm**:
   - Chọn môn học thuộc khoa CNTT (ví dụ: Cấu trúc dữ liệu & Giải thuật).
   - Thử sửa điểm một sinh viên bất kỳ thành giá trị lỗi (ví dụ điểm GK = `7.3`).
   - Nhấn **Ghi điểm** $\rightarrow$ Hệ thống rollback toàn bộ dữ liệu (không có sinh viên nào bị lưu điểm) và hiển thị thông báo lỗi tiếng Việt chi tiết chỉ rõ dòng sinh viên có điểm không hợp lệ.
4. Truy cập menu **Tài khoản**:
   - Chỉ hiển thị danh sách giảng viên thuộc khoa CNTT.
   - Drodown chọn nhóm quyền bị khóa cứng ở giá trị `KHOA`.
   - Chặn KHOA tạo tài khoản cho giảng viên khoa khác hoặc tạo tài khoản nhóm PGV.

### Kịch bản 3: Sinh viên (SV)
1. Đăng nhập bằng mã sinh viên `N15DCCN001` (Chọn loại tài khoản là Sinh viên, không cần nhập mật khẩu).
2. Kiểm tra menu điều hướng bên trái:
   - Chỉ xuất hiện 3 menu: **Tổng quan**, **Đăng ký lớp tín chỉ**, và **Phiếu điểm**.
   - Các menu quản lý dữ liệu cơ bản, nhập điểm, báo cáo khác, quản trị tài khoản đều bị ẩn hoàn toàn.
3. Truy cập menu **Đăng ký lớp tín chỉ**:
   - Không có form chọn/nhập mã sinh viên. Hệ thống tự động lấy mã `N15DCCN001` từ session để hiển thị thông tin và đăng ký lớp cho chính mình.
4. Truy cập menu **Phiếu điểm**:
   - Hệ thống hiển thị trực tiếp phiếu điểm của sinh viên `N15DCCN001` mà không cho phép thay đổi hay nhập mã sinh viên khác.
