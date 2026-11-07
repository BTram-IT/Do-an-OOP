import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public abstract class ThanhToan {

    public static final String CHO_THANH_TOAN = "Cho thanh toan";
    public static final String THANH_CONG = "Thanh cong";
    public static final String THAT_BAI = "That bai";

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    protected String maGD;
    protected String trangThai;
    protected LocalDateTime ngayThanhToan;
    protected double soTien;
    protected String maNV;
    protected String loaiThanhToan;

    public ThanhToan(String maGD, String loaiThanhToan) {
        this.maGD = (maGD == null) ? "" : maGD.trim().toUpperCase();
        this.loaiThanhToan = loaiThanhToan;
        this.trangThai = CHO_THANH_TOAN;
        this.soTien = 0;
        this.maNV = "";
    }

    public abstract void nhap();

    public abstract void nhap(Scanner sc);

    public abstract boolean qTrinhThanhToan();

    protected boolean ketThuc(boolean thanhCong) {
        trangThai = thanhCong ? THANH_CONG : THAT_BAI;
        if (thanhCong) {
            ngayThanhToan = LocalDateTime.now();
        }
        return thanhCong;
    }

    public boolean isThanhCong() {
        return THANH_CONG.equals(trangThai);
    }

    public String getMaGD() {
        return maGD;
    }

    public void setMaGD(String maGD) {
        this.maGD = (maGD == null) ? "" : maGD.trim().toUpperCase();
    }

    public String getTrangThai() {
        return trangThai;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        this.soTien = Math.max(0, soTien);
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = (maNV == null) ? "" : maNV.trim();
    }

    public String getLoaiThanhToan() {
        return loaiThanhToan;
    }

    public String getPhuongThuc() {
        return loaiThanhToan;
    }

    @Override
    public String toString() {
        String ngay = ngayThanhToan == null ? "-" : ngayThanhToan.format(DINH_DANG_NGAY_GIO);
        return String.format("%s | %s | %,.0f VND | %s | %s | NV: %s",
                maGD, loaiThanhToan, soTien, trangThai, ngay, maNV);
    }
}