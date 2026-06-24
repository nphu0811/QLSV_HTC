-- =============================================
-- QLDSV_HTC - PHÂN QUYỀN SQL SERVER
-- Script tạo Logins, Roles và phân quyền
-- =============================================

USE master;
GO

-- =============================================
-- 1. TẠO SQL SERVER LOGINS
-- =============================================

-- Login cho Phòng Giáo vụ (toàn quyền)
IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'pgv_admin')
    CREATE LOGIN pgv_admin WITH PASSWORD = '123456', DEFAULT_DATABASE = QLDSV_HTC, CHECK_POLICY = OFF;
GO

-- Login cho Khoa CNTT
IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'khoa_cntt')
    CREATE LOGIN khoa_cntt WITH PASSWORD = 'khoa123', DEFAULT_DATABASE = QLDSV_HTC, CHECK_POLICY = OFF;
GO

-- Login cho Khoa Viễn Thông
IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'khoa_vt')
    CREATE LOGIN khoa_vt WITH PASSWORD = 'khoa456', DEFAULT_DATABASE = QLDSV_HTC, CHECK_POLICY = OFF;
GO

-- Login chung cho các Khoa khác
IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'khoa_chung')
    CREATE LOGIN khoa_chung WITH PASSWORD = 'khoachung123', DEFAULT_DATABASE = QLDSV_HTC, CHECK_POLICY = OFF;
GO

-- Login chung cho tất cả Sinh viên
IF NOT EXISTS (SELECT * FROM sys.server_principals WHERE name = 'sv')
    CREATE LOGIN sv WITH PASSWORD = 'sv123', DEFAULT_DATABASE = QLDSV_HTC, CHECK_POLICY = OFF;
GO

-- =============================================
-- 2. TẠO DATABASE USERS
-- =============================================
USE QLDSV_HTC;
GO

IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'pgv_admin')
    CREATE USER pgv_admin FOR LOGIN pgv_admin;
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'khoa_cntt')
    CREATE USER khoa_cntt FOR LOGIN khoa_cntt;
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'khoa_vt')
    CREATE USER khoa_vt FOR LOGIN khoa_vt;
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'khoa_chung')
    CREATE USER khoa_chung FOR LOGIN khoa_chung;
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'sv')
    CREATE USER sv FOR LOGIN sv;
GO

-- =============================================
-- 3. TẠO DATABASE ROLES
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'PGV' AND type = 'R')
    CREATE ROLE PGV;
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'KHOA' AND type = 'R')
    CREATE ROLE KHOA;
GO
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = 'NHOM_SV' AND type = 'R')
    CREATE ROLE NHOM_SV;
GO

-- =============================================
-- 4. GÁN USERS VÀO ROLES
-- =============================================
ALTER ROLE PGV ADD MEMBER pgv_admin;
GO
ALTER ROLE KHOA ADD MEMBER khoa_cntt;
GO
ALTER ROLE KHOA ADD MEMBER khoa_vt;
GO
ALTER ROLE KHOA ADD MEMBER khoa_chung;
GO
ALTER ROLE NHOM_SV ADD MEMBER sv;
GO

-- =============================================
-- 5. PHÂN QUYỀN TRÊN BẢNG CHO TỪNG NHÓM
-- =============================================

-- ===== PGV (Phòng Giáo vụ) - TOÀN QUYỀN =====
GRANT SELECT, INSERT, UPDATE, DELETE ON KHOA TO PGV;
GRANT SELECT, INSERT, UPDATE, DELETE ON LOP TO PGV;
GRANT SELECT, INSERT, UPDATE, DELETE ON SINHVIEN TO PGV;
GRANT SELECT, INSERT, UPDATE, DELETE ON MONHOC TO PGV;
GRANT SELECT, INSERT, UPDATE, DELETE ON GIANGVIEN TO PGV;
GRANT SELECT, INSERT, UPDATE, DELETE ON LOPTINCHI TO PGV;
GRANT SELECT, INSERT, UPDATE, DELETE ON DANGKY TO PGV;
GRANT SELECT, INSERT, UPDATE, DELETE ON TaiKhoan TO PGV;
GO

-- ===== KHOA - QUYỀN HẠN CHẾ =====
-- Chỉ được xem (SELECT) trên các bảng danh mục
GRANT SELECT ON KHOA TO KHOA;
GRANT SELECT ON LOP TO KHOA;
GRANT SELECT ON SINHVIEN TO KHOA;
GRANT SELECT ON MONHOC TO KHOA;
GRANT SELECT ON GIANGVIEN TO KHOA;
GRANT SELECT ON LOPTINCHI TO KHOA;
-- Được xem điểm (SELECT), KHÔNG ĐƯỢC tự ý UPDATE trực tiếp trên bảng DANGKY
GRANT SELECT ON DANGKY TO KHOA;
GRANT SELECT ON TaiKhoan TO KHOA;
GO

-- ===== SV (Sinh viên) - QUYỀN TỐI THIỂU =====
GRANT SELECT ON KHOA TO NHOM_SV;
GRANT SELECT ON LOP TO NHOM_SV;
GRANT SELECT ON SINHVIEN TO NHOM_SV;
GRANT SELECT ON MONHOC TO NHOM_SV;
GRANT SELECT ON GIANGVIEN TO NHOM_SV;
GRANT SELECT ON LOPTINCHI TO NHOM_SV;
-- Được xem đăng ký: SELECT trên DANGKY, KHÔNG ĐƯỢC tự ý INSERT/UPDATE trực tiếp trên DANGKY
GRANT SELECT ON DANGKY TO NHOM_SV;
GO

-- =============================================
-- 6. GÁN QUYỀN THỰC THI STORED PROCEDURE
-- =============================================

-- Nhóm PGV (thực thi toàn bộ schema dbo)
IF DATABASE_PRINCIPAL_ID(N'PGV') IS NOT NULL
    GRANT EXECUTE ON SCHEMA::dbo TO PGV;
GO

-- Nhóm KHOA
IF DATABASE_PRINCIPAL_ID(N'KHOA') IS NOT NULL
BEGIN
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachKhoa TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_LayTenKhoa TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachMonHoc TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_LayTenMonHoc TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachLop TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachLopDropdown TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachGiangVien TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachGiangVienDropdown TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachLopTinChi TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachNienKhoa TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_TimLopTinChi TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachSinhVienNhapDiem TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_CapNhatDiem TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachTaiKhoanGiangVien TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_LayNhomQuyenTheoGiangVien TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_LuuTaiKhoanGiangVien TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_XoaTaiKhoanTheoGiangVien TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoDSLopTinChi TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoDSSinhVienDangKy TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoBangDiem TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoPhieuDiemSinhVienInfo TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoPhieuDiem TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoLopInfo TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoMonHocTheoLop TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoSinhVienTheoLop TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoDiemTongKet TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_LayKhoaTheoGiangVien TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_LayKhoaTheoStudent TO KHOA;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachTaiKhoanGiangVienCoKhoa TO KHOA;
END;
GO

-- Nhóm NHOM_SV (Sinh viên)
IF DATABASE_PRINCIPAL_ID(N'NHOM_SV') IS NOT NULL
BEGIN
    GRANT EXECUTE ON OBJECT::dbo.SP_LayTenKhoa TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_ThongTinSinhVienDangKy TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachLopTinChiDaDangKy TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachLopTinChiCoTheDangKy TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_DanhSachMaLopTinChiDaDangKy TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_DangKyLopTinChi TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_HuyDangKyLopTinChi TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoPhieuDiemSinhVienInfo TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_BaoCaoPhieuDiem TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_LayKhoaTheoStudent TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_LoginSinhVien TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_LoginTaiKhoan TO NHOM_SV;
    GRANT EXECUTE ON OBJECT::dbo.SP_LayKhoaTheoLop TO NHOM_SV;
END;
GO

-- =============================================
-- 7. CẬP NHẬT PASSWORD MẶC ĐỊNH CHO SINH VIÊN
-- =============================================
UPDATE SINHVIEN SET PASSWORD = '123456' WHERE PASSWORD IS NULL OR PASSWORD = '';
GO

-- =============================================
-- 8. ĐẢM BẢO DỮ LIỆU TAIKHOAN CÓ SẴN
-- =============================================
IF NOT EXISTS (SELECT * FROM TaiKhoan WHERE Login = 'pgv_admin')
    INSERT INTO TaiKhoan (Login, MatKhau, NhomQuyen, MAKHOA, TrangThai, NgayTao)
    VALUES ('pgv_admin', '123456', 'PGV', NULL, 'Active', GETDATE());
GO

IF NOT EXISTS (SELECT * FROM TaiKhoan WHERE Login = 'khoa_cntt')
    INSERT INTO TaiKhoan (Login, MatKhau, NhomQuyen, MAKHOA, TrangThai, NgayTao)
    VALUES ('khoa_cntt', 'khoa123', 'KHOA', 'CNTT', 'Active', GETDATE());
GO

IF NOT EXISTS (SELECT * FROM TaiKhoan WHERE Login = 'khoa_vt')
    INSERT INTO TaiKhoan (Login, MatKhau, NhomQuyen, MAKHOA, TrangThai, NgayTao)
    VALUES ('khoa_vt', 'khoa456', 'KHOA', 'VT', 'Active', GETDATE());
GO

PRINT N'=== PHÂN QUYỀN SQL SERVER HOÀN TẤT ===';
PRINT N'Logins: pgv_admin, khoa_cntt, khoa_vt, khoa_chung, sv';
PRINT N'Roles: PGV (toàn quyền), KHOA (hạn chế), NHOM_SV (tối thiểu)';
GO
