package person;

import sanpham.DSSanPham;
import hoadon.DSHoadon;

public interface Thongke {

    // 1. THỐNG KÊ DOANH THU & LỢI NHUẬN
    void thongKeDoanhThuLoiNhuan(DSHoadon dsHD);

    // 2. THỐNG KÊ THUẾ
    void thongKeThue(DSHoadon dsHD);

    // 3. THỐNG KÊ SẢN PHẨM BÁN ĐƯỢC
    void thongKeSanPhamBanDuoc(DSHoadon dsHD);

    // 4. THỐNG KÊ TỒN KHO & CẢNH BÁO HẾT HẠN
    String thongKeTonKho(DSSanPham dsSP);

    // 5. BÁO CÁO TOÀN DIỆN THEO THÁNG, QUÝ, NĂM
    void baoCaoTheoThang(DSHoadon dsHD, int thang, int nam);
    void baoCaoTheoQuy(DSHoadon dsHD, int quy, int nam);
    void baoCaoTheoNam(DSHoadon dsHD, int nam);
}