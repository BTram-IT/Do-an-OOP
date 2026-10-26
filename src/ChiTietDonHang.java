public class ChiTietDonHang {
    private SanPham sanPham;
    private int soLuong;

    public ChiTietDonHang() {
    }

    public ChiTietDonHang(SanPham sanPham, int soLuong) {
        this.sanPham = sanPham;
        this.soLuong = soLuong;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    // Tinh tien hang truoc thue
    public double tinhTienTruocThue() {
        return sanPham.getGiaBan() * soLuong;
    }

    // Tinh tien thue VAT
    public double tinhTienThue() {
        return sanPham.tinhThue() * soLuong;
    }

    // Tinh thanh tien sau thue
    public double tinhThanhTienSauThue() {
        return sanPham.tinhGiaSauThue() * soLuong;
    }

    // Hien thi chi tiet san pham trong don hang
    public void hienThi() {
        if (sanPham == null) {
            System.out.println("San pham khong ton tai!");
            return;
        }

        System.out.println("Ten san pham: " + sanPham.getTenSP());
        System.out.println("Don gia: " + sanPham.getGiaBan() + " VND");
        System.out.println("So luong: " + soLuong);
        System.out.println("Tien hang truoc thue: "
                + tinhTienTruocThue() + " VND");
        System.out.println("Tien thue VAT: "
                + tinhTienThue() + " VND");
        System.out.println("Thanh tien sau thue: "
                + tinhThanhTienSauThue() + " VND");
    }
}