#!/bin/bash
# ============================================
# Script khởi tạo database QLDSV_HTC
# Chờ SQL Server sẵn sàng rồi chạy SQL scripts
# ============================================

echo "=== Đang chờ SQL Server khởi động... ==="

# Wait for SQL Server to be ready
for i in {1..60}; do
    /opt/mssql-tools18/bin/sqlcmd -S "$SQL_SERVER" -U sa -P "$SA_PASSWORD" -C -Q "SELECT 1" > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo "=== SQL Server đã sẵn sàng! ==="
        break
    fi
    echo "Chờ SQL Server... ($i/60)"
    sleep 2
done

echo "=== Đang tạo database và import dữ liệu... ==="

# Run the main database script
/opt/mssql-tools18/bin/sqlcmd -S "$SQL_SERVER" -U sa -P "$SA_PASSWORD" -C \
    -i /docker-scripts/init-db.sql

if [ $? -eq 0 ]; then
    echo "=== Tạo database QLDSV_HTC thành công! ==="
else
    echo "=== Lỗi khi tạo database (có thể đã tồn tại) ==="
fi

# Add TaiKhoan table if not exists
echo "=== Đang tạo bảng TaiKhoan... ==="
/opt/mssql-tools18/bin/sqlcmd -S "$SQL_SERVER" -U sa -P "$SA_PASSWORD" -C -d QLDSV_HTC -Q "
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'TaiKhoan')
BEGIN
    CREATE TABLE TaiKhoan (
        Login NVARCHAR(50) PRIMARY KEY,
        MatKhau NVARCHAR(50) NOT NULL,
        NhomQuyen NVARCHAR(20) NOT NULL,
        MAKHOA NCHAR(10) NULL,
        TrangThai NVARCHAR(20) DEFAULT 'Active',
        NgayTao DATETIME DEFAULT GETDATE()
    );
    PRINT N'Đã tạo bảng TaiKhoan';
END
ELSE
    PRINT N'Bảng TaiKhoan đã tồn tại';
"

# Run the security/permissions script
echo "=== Đang thiết lập phân quyền... ==="
/opt/mssql-tools18/bin/sqlcmd -S "$SQL_SERVER" -U sa -P "$SA_PASSWORD" -C \
    -i /docker-scripts/setup_security.sql

if [ $? -eq 0 ]; then
    echo "=== Phân quyền thành công! ==="
else
    echo "=== Cảnh báo: Một số lệnh phân quyền có thể lỗi (không ảnh hưởng hoạt động) ==="
fi

echo ""
echo "============================================"
echo "  DATABASE QLDSV_HTC - KHỞI TẠO HOÀN TẤT  "
echo "============================================"
echo "  Server: $SQL_SERVER"
echo "  Database: QLDSV_HTC"
echo "  SA Password: $SA_PASSWORD"
echo "============================================"
