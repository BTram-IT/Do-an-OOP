public interface Thongke {
    // Thống kê doanh thu
    public abstract double tinhDoanhThuTheoThang(int thang, int nam);
    public abstract double tinhDoanhThuTheoQuy(int quy, int nam);
    public abstract double tinhDoanhThuTheoNam(int nam);

    // Thống kê lợi nhuận
    public abstract double tinhLoiNhuanTheoThang(int thang, int nam);
    public abstract double tinhLoiNhuanTheoQuy(int quy, int nam);
    public abstract double tinhLoiNhuanTheoNam(int nam);

    // Thống kê thuế
    public abstract double thongKeThueTheoThang(int thang, int nam);
    public abstract double thongKeThueTheoNam(int nam);



    // Thống kê lương
    public abstract void thongKeLuong();

    public abstract void thongKeSanPhamBanDuocTheoThang(int thang, int nam);
    public abstract void thongKeSanPhamBanDuocTheoQuy();
    public abstract void thongKeSanPhamBanDuocTheoNam();
    public abstract void thongKeSanPhamBanRaTheoThang();
    public abstract void thongKeSanPhamBanRaTheoQuy();
    public abstract void thongKeSanPhamBanRaTheoNam();

}
