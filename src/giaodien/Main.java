//package giaodien;
//
//import doitac.DSNhaCungCap;
//import hoadon.DSHoadon;
//import java.io.IOException;
//import khuyenmai.DSKhuyenMai;
//import khuyenmai.DSSanPhamKhuyenMai;
//import person.DSNhanVien;
//import person.DsKhachHang;
//import person.KeToan;
//import person.QuanLyCuaHang;
//import person.ThuNgan;
//import sanpham.DSSanPham;
//
//public class Main {
//    public static void main(String[] args) throws IOException {
//        // 1. Khởi tạo các danh sách dùng chung toàn hệ thống
//        DSSanPham dsSP = new DSSanPham();
//        DSNhanVien dsNV = new DSNhanVien();
//        DsKhachHang dsKH = new DsKhachHang();
//        DSDoiTac dsDoiTac = new DSDoiTac();
//        DSNhaCungCap dsNhaCungCap = new DSNhaCungCap();
//        DSKhuyenMai dsKM = new DSKhuyenMai();
//        DSSanPhamKhuyenMai dsSPKM = new DSSanPhamKhuyenMai();
//        DSHoadon dsHoaDonBan = new DSHoadon();
//        DSHoadon dsHoaDonNhap = new DSHoadon();
//
//        // === BỔ SUNG: GỌI ĐỌC FILE NGAY TỪ HÀM MAIN ĐỂ LOAD DỮ LIỆU CŨ ===
//        System.out.println("Đang tải dữ liệu hệ thống từ ổ cứng...");
//        dsSP.docFile("data/sanpham.txt");
//        dsNV.docFile("data/nhanvien.txt");
//        dsKH.docFile("data/khachhang.txt");
//        dsDoiTac.docFile("data/doitac.txt");
//        dsNhaCungCap.docFile("data/nhacungcap.txt");
//        dsKM.docFile("data/khuyenmai.txt");
//        dsSPKM.docFile("data/sanphamkhuyenmai.txt");
//        dsHoaDonBan.docFile("data/hoadonban.txt");
//        dsHoaDonNhap.docFile("data/hoadonnhap.txt");
//        System.out.println("-> Tải dữ liệu hoàn tất!\n");
//
//        // 2. Khởi tạo các phân hệ nghiệp vụ với đúng các tham số constructor hiện tại
//        QuanlyKho quanLyKho = new QuanlyKho(dsSP, dsNhaCungCap, dsHoaDonNhap);
//        QuanLyCuaHang quanLyCuaHang = new QuanLyCuaHang(dsKH, dsSP, dsNV, dsDoiTac, dsKM, dsSPKM, dsHoaDonBan, dsHoaDonNhap);
//        KeToan keToan = new KeToan(dsNV, dsSP, dsHoaDonBan, quanLyKho);
//        ThuNgan thuNgan = new ThuNgan(dsKH, dsHoaDonBan);
//
//        // 3. Khởi tạo Giaodien với 4 tham số và ủy quyền toàn bộ việc hiển thị menu
//        Giaodien giaoDien = new Giaodien(keToan, thuNgan, quanLyKho, quanLyCuaHang);
//
//        // Bắt đầu chạy ứng dụng
//        giaoDien.menu();
//    }
//}