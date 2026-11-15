package person;

import java.time.LocalDate;

import hoadon.DSHoadon;
import sanpham.DSSanPham;

public interface Thongke {

    void thongKeDoanhThuLoiNhuan(
            DSHoadon dsHD, LocalDate tuNgay, LocalDate denNgay);

    void thongKeThue(
            DSHoadon dsHD, LocalDate tuNgay, LocalDate denNgay);

    void thongKeSanPhamBanDuoc(
            DSHoadon dsHD, DSSanPham dsSP,
            LocalDate tuNgay, LocalDate denNgay);

    void thongKeTonKho(DSSanPham dsSP);

    void baoCaoTheoThang(DSHoadon dsHD, int thang, int nam);

    void baoCaoTheoQuy(DSHoadon dsHD, int quy, int nam);

    void baoCaoTheoNam(DSHoadon dsHD, int nam);

    void thongKeKhachHang(
            DSHoadon dsHD, DsKhachHang dsKH,
            LocalDate tuNgay, LocalDate denNgay);

    // Thống kê nhân viên
    void thongKeNhanVien(DSNhanVien dsNV);

    // Thống kê bảng lương
    void thongKeBangLuong(DSNhanVien dsNV);

    // Gộp thống kê nhân viên và bảng lương
    void thongKeNhanVienVaBangLuong(DSNhanVien dsNV);
}