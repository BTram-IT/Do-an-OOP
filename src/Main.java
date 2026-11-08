public class Main {
    public static void main(String[] args) {
        // 1. Khởi tạo các danh sách dùng chung toàn hệ thống
        DSSanPham dsSP = new DSSanPham();
        DSNhanVien dsNV = new DSNhanVien();
        DsKhachHang dsKH = new DsKhachHang();
        DSDoiTac dsDoiTac = new DSDoiTac();
        DSNhaCungCap dsNhaCungCap = new DSNhaCungCap();
        DSKhuyenMai dsKM = new DSKhuyenMai();
        DSSanPhamKhuyenMai dsSPKM = new DSSanPhamKhuyenMai();
        DSHoadon dsHoaDonBan = new DSHoadon();
        DSHoadon dsHoaDonNhap = new DSHoadon();

        // 2. Khởi tạo các phân hệ nghiệp vụ với đúng các tham số constructor hiện tại
        QuanlyKho quanLyKho = new QuanlyKho(dsSP, dsNhaCungCap, dsHoaDonNhap);
        QuanLyCuaHang quanLyCuaHang = new QuanLyCuaHang(dsKH, dsSP, dsNV, dsDoiTac, dsKM, dsSPKM, dsHoaDonBan, dsHoaDonNhap);
        KeToan keToan = new KeToan(dsNV, dsSP, dsHoaDonBan, quanLyKho);
        ThuNgan thuNgan = new ThuNgan(dsKH, dsHoaDonBan);

        // 3. Khởi tạo Giaodien với 4 tham số và ủy quyền toàn bộ việc hiển thị menu
        Giaodien giaoDien = new Giaodien(keToan, thuNgan, quanLyKho, quanLyCuaHang);

        // Bắt đầu chạy ứng dụng
        giaoDien.menu();
    }
}