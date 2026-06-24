# Walkthrough - Báo Cáo Nghiệm Thu & Kiểm Thử QLSV_HTC

Tài liệu này tóm tắt kết quả sửa đổi dự án **QLSV_HTC** nhằm đáp ứng đầy đủ các ưu tiên trong Đề 3 môn Hệ quản trị cơ sở dữ liệu và các quy định bảo mật bổ sung của giảng viên.

## 1. Kết quả chỉnh sửa mới nhất (Bảo mật sâu SQL Server & Ràng buộc nghiệp vụ)

- **Ngăn chặn truy cập trực tiếp bảng SINHVIEN và DANGKY:**
  - Đã thêm các câu lệnh `REVOKE SELECT` rõ ràng trên 2 bảng `SINHVIEN` và `DANGKY` của nhóm `NHOM_SV` (bao gồm login `sv`) trong [setup_security.sql](file:///c:/Users/admin/eclipse-workspace/HQTCSDL_De3/sql/setup_security.sql).
  - Sử dụng cơ chế **Ownership Chaining** của SQL Server: Sinh viên chỉ có quyền thực thi các stored procedure được cấp phép (`SP_LoginSinhVien`, `SP_ThongTinSinhVienDangKy`, `SP_DanhSachLopTinChiDaDangKy`, `SP_DanhSachLopTinChiCoTheDangKy`, `SP_DangKyLopTinChi`, `SP_HuyDangKyLopTinChi`, `SP_BaoCaoPhieuDiem`, v.v.). Khi đó sinh viên có thể đăng ký môn, xem phiếu điểm bình thường trên web nhưng nếu truy cập trực tiếp bằng login `sv` trong SSMS và chạy lệnh `SELECT *` sẽ bị SQL Server từ chối ngay lập tức.
  
- **Tự động kiểm tra khoa sở hữu trong SP_CapNhatDiem:**
  - Nâng cấp stored procedure `SP_CapNhatDiem` trong [stored_procedures.sql](file:///c:/Users/admin/eclipse-workspace/HQTCSDL_De3/sql/stored_procedures.sql) để tự động kiểm tra quyền của tài khoản SQL thực thi (`SUSER_SNAME()`):
    1. Tra cứu phòng ban (`MAKHOA`) của tài khoản trong bảng `dbo.TaiKhoan` (dành cho giảng viên đăng nhập qua web).
    2. Kiểm tra theo các login cứng hệ thống (`khoa_cntt`, `khoa_vt`, `khoa_chung`).
    3. So khớp linh hoạt dựa trên tên login (ví dụ: login chứa từ khóa `cntt`, `vt` hay tên khoa cụ thể) để chặn trường hợp giảng viên tạo login tùy ý trong SSMS rồi gán vào role `KHOA` để sửa điểm trái phép cho khoa khác.

- **Bắt buộc điểm GK và CK theo bước 0.5 trong SP_CapNhatDiem:**
  - Đã thêm kiểm tra trong `SP_CapNhatDiem` để đảm bảo nếu điểm giữa kỳ (`@DIEM_GK`) hoặc điểm cuối kỳ (`@DIEM_CK`) khác NULL thì bắt buộc phải là bội số của 0.5 (đáp ứng đúng yêu cầu của đề bài ngay tại mức Database).

- **Kiểm tra KHOA quản lý tài khoản giảng viên khoa khác:**
  - Thêm logic kiểm tra khoa của giảng viên đích so với khoa của tài khoản SQL đang thực thi trong `SP_LuuTaiKhoanGiangVien` và `SP_XoaTaiKhoanTheoGiangVien`. Nếu tài khoản thuộc nhóm `KHOA` cố tình lưu hoặc xóa tài khoản của giảng viên thuộc khoa khác bằng SSMS, stored procedure sẽ từ chối và báo lỗi rõ ràng.

---

## 2. Kết quả kiểm thử bảo mật (Database Verification)

### Ca kiểm thử 1: Kiểm tra quyền SELECT trực tiếp của Sinh viên (`sv`)
- **Cách thực hiện:** Kết nối SSMS bằng login `sv` (mật khẩu `sv123`), thử truy vấn trực tiếp:
  - `SELECT * FROM SINHVIEN`
  - `SELECT * FROM DANGKY`
- **Kết quả:** SQL Server chặn truy cập và báo lỗi:
  > *Msg 229, Level 14, State 5, Server ..., Line 1*
  > *The SELECT permission was denied on the object 'SINHVIEN', database 'QLDSV_HTC', schema 'dbo'.*
- **Kết quả thực thi qua SP:** Gọi `EXEC SP_LoginSinhVien 'N15DCCN001'` hoặc `EXEC SP_ThongTinSinhVienDangKy 'N15DCCN001'` thành công, trả về thông tin bình thường (Xác nhận Ownership Chaining hoạt động tốt).

### Ca kiểm thử 2: Kiểm tra phân quyền khoa sở hữu trong `SP_CapNhatDiem`
- **Cách thực hiện:** Kết nối SSMS bằng login `khoa_cntt` (mật khẩu `khoa123`).
- **Thử cập nhật lớp thuộc khoa CNTT (LTC 1):**
  - `EXEC SP_CapNhatDiem @DIEM_CC=10, @MALTC=1, @MASV='N15DCCN001'` -> Thành công.
- **Thử cập nhật lớp thuộc khoa Viễn thông (LTC 4):**
  - Đầu tiên gán tạm sinh viên vào LTC 4.
  - Chạy `EXEC SP_CapNhatDiem @DIEM_CC=10, @MALTC=4, @MASV='N15DCCN001'` -> Bị chặn và báo lỗi rõ ràng:
    > *Tài khoản khoa_cntt thuộc khoa CNTT không được phép sửa điểm của khoa VT.*

### Ca kiểm thử 3: Kiểm tra bước điểm 0.5 trong `SP_CapNhatDiem`
- **Cách thực hiện:** Đăng nhập bằng `khoa_cntt` và gọi `SP_CapNhatDiem` với điểm lẻ (không chia hết cho 0.5):
  - `EXEC SP_CapNhatDiem @DIEM_CC=10, @DIEM_GK=7.3, @MALTC=1, @MASV='N15DCCN001'`
- **Kết quả:** Báo lỗi từ chối thành công:
  > *Msg 50001, Level 16, State 1, Server ..., Procedure SP_CapNhatDiem, Line ...*
  > *Điểm giữa kỳ phải là bội số của 0.5.*

### Ca kiểm thử 4: Kiểm tra KHOA quản lý tài khoản giảng viên khoa khác
- **Cách thực hiện:** Đăng nhập bằng `khoa_cntt` và gọi lưu/xóa tài khoản giảng viên `GV04` (thuộc khoa `VT`):
  - `EXEC SP_LuuTaiKhoanGiangVien 'GV04', 'gv04_login', '123', 'KHOA'`
  - `EXEC SP_XoaTaiKhoanTheoGiangVien 'GV04'`
- **Kết quả:** Cả hai stored procedure đều từ chối và báo lỗi:
  > *Msg 50003, Level 16, State 1, Server ..., Procedure ..., Line ...*
  > *Không được quản lý tài khoản giảng viên thuộc khoa khác.*

---

## 3. Xác minh giao diện ứng dụng Sinh viên (Application Verification)

Hệ thống đã được chạy thực tế và kiểm tra luồng sinh viên bằng tác vụ duyệt web tự động. Sinh viên vẫn đăng nhập, xem thông tin đăng ký môn học và in phiếu điểm bình thường không phát sinh lỗi.

### Minh chứng ghi hình và chụp ảnh màn hình:

- **Phiếu điểm sinh viên được in thành công:**
  ![Phiếu điểm sinh viên](/Users/admin/.gemini/antigravity-ide/brain/6811f5f8-5667-42b3-a6b8-faf42f5c3a1a/student_grades_report_1782309802048.png)

- **Ghi hình toàn bộ luồng kiểm thử Sinh viên:**
  ![Ghi hình kiểm thử](/Users/admin/.gemini/antigravity-ide/brain/6811f5f8-5667-42b3-a6b8-faf42f5c3a1a/student_flow_verify_1782309716653.webp)

---

## 4. Hướng dẫn tài khoản kiểm thử mặc định

| Nhóm quyền | Tài khoản (Login) | Mật khẩu (Password) | SQL Server Login tương ứng |
| :--- | :--- | :--- | :--- |
| **Phòng Giáo Vụ (PGV)** | `pgv_admin` | `123456` | `pgv_admin` (pass: `123456`) |
| **Khoa CNTT** | `khoa_cntt` | `khoa123` | `khoa_cntt` (pass: `khoa123`) |
| **Khoa Viễn thông** | `khoa_vt` | `khoa456` | `khoa_vt` (pass: `khoa456`) |
| **Sinh viên (SV)** | `N15DCCN001` | *Không cần mật khẩu* | `sv` (pass: `sv123`) |
