# Walkthrough - QLSV_HTC

## Điểm sửa quan trọng về đăng nhập và phân quyền

- Không tạo bảng `dbo.TaiKhoan` trong database nghiệp vụ.
- Đăng nhập giảng viên/PGV/Khoa dùng trực tiếp SQL Server Login:
  - `pgv_admin` / `123456` thuộc role `PGV`.
  - `khoa_cntt` / `khoa123` thuộc role `KHOA`.
  - `khoa_vt` / `khoa456` thuộc role `KHOA`.
  - `sv` / `sv123` thuộc role vật lý `NHOM_SV`, tương ứng nhóm ứng dụng `SV`.
- App thử kết nối bằng chính SQL login người dùng nhập, sau đó gọi `SP_ThongTinDangNhapSql` để đọc role hiện tại qua `IS_MEMBER`.
- Khoa của login `khoa_cntt` và `khoa_vt` được suy ra từ tên login. Nếu tạo login riêng cho giảng viên, login phải trùng `MAGV`; khi đó khoa lấy từ `GIANGVIEN.MAKHOA`.
- Màn hình tài khoản không lưu mật khẩu vào bảng phụ. Nếu PGV tạo tài khoản giảng viên, stored procedure sẽ tạo SQL Login/User thật và add user vào role `PGV` hoặc `KHOA`.

## Cách giải thích nếu bị hỏi

"Em không thêm bảng tài khoản vào schema nghiệp vụ. Phần đăng nhập dùng SQL Server Login/User/Role: login được map với database user, user được add vào role, app đăng nhập xong thì dựa vào role của phiên SQL hiện tại. Các bảng nghiệp vụ như khoa, lớp, sinh viên, giảng viên, lớp tín chỉ, đăng ký vẫn dùng đúng schema đề. Nếu cần xác định khoa của giảng viên thì dùng `GIANGVIEN.MAKHOA`, không dùng bảng ánh xạ riêng."

## Kiểm thử nhanh

- Chạy `sql/stored_procedures.sql`.
- Chạy `sql/setup_security.sql`.
- Đăng nhập tab Giảng viên bằng `pgv_admin` / `123456`.
- Đăng nhập tab Giảng viên bằng `khoa_cntt` / `khoa123` và kiểm tra session thuộc nhóm `KHOA`, mã khoa `CNTT`.
- Đăng nhập tab Sinh viên bằng `N15DCCN001`; app dùng SQL login `sv` và chỉ thao tác dữ liệu qua stored procedure được cấp quyền.
