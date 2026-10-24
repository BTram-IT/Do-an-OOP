import java.text.NumberFormat;
import java.util.Locale;

public abstract class SanPham {
    private String maSP;
    private String tenSP;
    private double donGia; // Gia ban
    private int soLuongTon;

    // Constructor
    public SanPham(String maSP, String tenSP,
                   double donGia, int soLuongTon) {

        if (maSP == null || maSP.isBlank()) {
            throw new IllegalArgumentException(
                    "Ma san pham khong duoc de trong");
        }

        if (tenSP == null || tenSP.isBlank()) {
            throw new IllegalArgumentException(
                    "Ten san pham khong duoc de trong");
        }

        if (!Double.isFinite(donGia) || donGia < 0) {
            throw new IllegalArgumentException(
                    "Don gia khong hop le");
        }

        if (soLuongTon < 0) {
            throw new IllegalArgumentException(
                    "So luong ton khong hop le");
        }

        this.maSP = maSP.trim();
        this.tenSP = tenSP.trim();
        this.donGia = donGia;
        this.soLuongTon = soLuongTon;
    }

    // Moi lop con tu xac dinh loai san pham
    public abstract String getLoai();

    // Tang ton kho khi nhap them hang
    public void tangTon(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "So luong tang phai > 0");
        }

        if (soLuong > Integer.MAX_VALUE - soLuongTon) {
            throw new IllegalArgumentException(
                    "So luong ton vuot gioi han");
        }

        soLuongTon += soLuong;
    }

    // Giam ton kho khi xuat hang / ban hang
    public void giamTon(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException(
                    "So luong giam phai > 0");
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

    // Setter
    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.isBlank()) {
            throw new IllegalArgumentException(
                    "Ten san pham khong duoc de trong");
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

    // Hien thi thong tin san pham
    @Override
    public String toString() {
        NumberFormat dinhDangTien =
                NumberFormat.getNumberInstance(
                        Locale.forLanguageTag("vi-VN"));
        dinhDangTien.setMaximumFractionDigits(0);

        return String.format(
                "[%s] %-8s | %-15s | Gia ban: %s VND | Ton: %d",
                getLoai(),
                maSP,
                tenSP,
                dinhDangTien.format(donGia),
                soLuongTon
        );
    }
}