# 🐳 Hướng dẫn chạy Project bằng Docker

## Yêu cầu
- **Docker Desktop** đã cài đặt ([Tải tại đây](https://www.docker.com/products/docker-desktop/))
- Không cần cài Tomcat, SQL Server, Java hay Eclipse!

## Cách chạy (chỉ 2 bước)

### Bước 1: Mở Terminal tại thư mục project
```bash
cd HQTCSDL_De3
```

### Bước 2: Chạy Docker Compose
```bash
docker-compose up --build
```

Chờ khoảng 1-2 phút để:
- SQL Server khởi động
- Database QLDSV_HTC được tạo tự động
- Tomcat 8.5 build và deploy WAR

### Bước 3: Truy cập ứng dụng
Mở trình duyệt: **http://localhost:8080/HQTCSDL_De3/login**

Hoặc: **http://localhost:8080/login** (ROOT context)

## Tài khoản đăng nhập

| Loại | Username | Password | Nhóm quyền |
|------|----------|----------|-------------|
| Phòng Giáo vụ | pgv_admin | 123456 | PGV (toàn quyền) |
| Khoa CNTT | khoa_cntt | khoa123 | KHOA |
| Khoa Viễn Thông | khoa_vt | khoa456 | KHOA |
| Sinh viên | N15DCCN001 | (không cần password) | SV |

## Lệnh hữu ích

```bash
# Chạy nền (background)
docker-compose up -d --build

# Xem logs
docker-compose logs -f webapp
docker-compose logs -f sqlserver

# Dừng tất cả
docker-compose down

# Dừng và xóa dữ liệu database
docker-compose down -v

# Build lại khi sửa code
docker-compose up --build
```

## Lưu ý
- SQL Server trong Docker dùng password: `DockerPass@123` (tự động cấu hình)
- Port 8080 (Tomcat) và 1433 (SQL Server) sẽ được mở trên máy
- Nếu port đã bị chiếm, sửa trong `docker-compose.yml`
- Project vẫn chạy bình thường trên Eclipse với localhost\SQLEXPRESS (không bị ảnh hưởng bởi Docker)
