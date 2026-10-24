
public abstract class SanPham {
    private String maSP;
    private String tenSP;
    private double donGia; // price
    private int soLuongTon;
    private String donViTinh;

    // Constructor
    public SanPham(String maSP, String tenSP,
                   double donGia, int soLuongTon,
                   String donViTinh) {

        if (maSP == null || maSP.isBlank()) {
            throw new IllegalArgumentException(
                    "Ma san pham khong hop le");
        }

        if (tenSP == null || tenSP.isBlank()) {
            throw new IllegalArgumentException(
                    "Ten san pham khong hop le");
        }

        if (!Double.isFinite(donGia) || donGia < 0) {
            throw new IllegalArgumentException(
                    "Don gia khong hop le");
        }

        if (soLuongTon < 0) {
            throw new IllegalArgumentException(
                    "So luong ton khong hop le");
        }

        if (donViTinh == null || donViTinh.isBlank()) {
            throw new IllegalArgumentException(
                    "Don vi tinh khong hop le");
        }

        this.maSP = maSP.trim();
        this.tenSP = tenSP.trim();
        this.donGia = donGia;
        this.soLuongTon = soLuongTon;
        this.donViTinh = donViTinh.trim();
    }
    
    public abstract String getLoai();

    // Tang ton kho khi nhap hang
    public void tangTon(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "So luong phai > 0");
        }

        if (soLuong > Integer.MAX_VALUE - soLuongTon) {
            throw new IllegalArgumentException(
                    "So luong ton vuot gioi han");
        }

        soLuongTon += soLuong;
    }

    // Giam ton kho khi xuat hang
    public void giamTon(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "So luong phai > 0");
        }

        if (soLuong > soLuongTon) {
            throw new IllegalArgumentException(
                    "Khong du hang trong kho");
        }

        soLuongTon -= soLuong;
    }

    // Getter
    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public double getDonGia() {
        return donGia;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    // Setter
    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.isBlank()) {
            throw new IllegalArgumentException(
                    "Ten san pham khong hop le");
        }

        this.tenSP = tenSP.trim();
    }

    public void setDonGia(double donGia) {
        if (!Double.isFinite(donGia) || donGia < 0) {
            throw new IllegalArgumentException(
                    "Don gia khong hop le");
        }

        this.donGia = donGia;
    }

    public void setDonViTinh(String donViTinh) {
        if (donViTinh == null || donViTinh.isBlank()) {
            throw new IllegalArgumentException(
                    "Don vi tinh khong hop le");
        }

        this.donViTinh = donViTinh.trim();
    }

    // Hien thi thong tin san pham
    @Override
    public String toString() {
        return String.format(
                "[%s] %s | %-20s | Gia ban: %,.0f VND"
                        + " | Ton: %d %s",
                getLoai(),
                maSP,
                tenSP,
                donGia,
                soLuongTon,
                donViTinh
        );
    }
}
