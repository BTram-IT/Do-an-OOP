
import java.time.LocalDateTime;
import java.util.Scanner;

public abstract class HoaDon {
    protected String maHD;
    protected String maNV;
    protected LocalDateTime thoigianin;

    public HoaDon() {
        this.maHD = "";
        this.maNV = "";
        this.thoigianin = LocalDateTime.now();
    }

    public HoaDon(String maHD, String maNV) {
        this();
        setMaHD(maHD);
        setMaNV(maNV);
    }

    // Getter
    public String getMaHD() {
        return maHD;
    }

    public String getMaNV() {
        return maNV;
    }

    public LocalDateTime getThoigianin() {
        return thoigianin;
    }

    // Setter
    public void setMaHD(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma hoa don khong hop le!");
        }
        this.maHD = maHD.trim();
    }

    public void setMaNV(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma nhan vien khong hop le!");
        }
        this.maNV = maNV.trim();
    }

    public void setThoigianin(LocalDateTime thoigianin) {
        if (thoigianin == null) {
            throw new IllegalArgumentException("Thoi gian lap hoa don khong hop le!");
        }
        this.thoigianin = thoigianin;
    }

    // Nhap ma hoa don bang Ktradulieu
    public static String nhapMaHD(Scanner sc) {
        return Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma hoa don: ",
                ".+",
                "Ma hoa don khong duoc de trong!"
        );
    }

    // Nhap ma nhan vien bang Ktradulieu
    public static String nhapMaNV(Scanner sc) {
        return Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma nhan vien: ",
                ".+",
                "Ma nhan vien khong duoc de trong!"
        );
    }

    // Kiem tra trang thai thanh toan
    public boolean isDaThanhToan() {
        return this instanceof Hoadonban
                && ((Hoadonban) this).getTrangThai() != null
                && ((Hoadonban) this).getTrangThai().equalsIgnoreCase("Da thanh toan");
    }

    // Phuong thuc truu tuong
    public abstract double Tongtienhoadon();

    public abstract void inhoadon();

    @Override
    public abstract String toString();
}
