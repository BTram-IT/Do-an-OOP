
import java.time.LocalDateTime;
import java.util.Scanner;

public abstract class HoaDon {
    private String MaHD;
    private String MaNV;
    private LocalDateTime thoigianin;

    public HoaDon() {
        this.MaHD = "";
        this.MaNV = "";
        this.thoigianin = LocalDateTime.now();
    }

    public HoaDon(String maHD, String maNV, LocalDateTime thoigianin) {
        this();
        setMaHD(maHD);
        setMaNV(maNV);
        setThoigianin(thoigianin);
    }

    public String getMaHD() {
        return MaHD;
    }

    public void setMaHD(String maHD) {
        this.MaHD = (maHD == null) ? "" : Ktradulieu.chuanHoaMa(maHD);
    }

    public String getMaNV() {
        return MaNV;
    }

    public void setMaNV(String maNV) {
        this.MaNV = (maNV == null) ? "" : Ktradulieu.chuanHoaMa(maNV);
    }

    public LocalDateTime getThoigianin() {
        return thoigianin;
    }

    public void setThoigianin(LocalDateTime thoigianin) {
        this.thoigianin = (thoigianin == null)
                ? LocalDateTime.now() : thoigianin;
    }

    public LocalDateTime getThoiGianIn() {
        return getThoigianin();
    }

    public void setThoiGianIn(LocalDateTime thoiGianIn) {
        setThoigianin(thoiGianIn);
    }

    public LocalDateTime getThoiGian() {
        return getThoigianin();
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        setThoigianin(thoiGian);
    }

    public boolean isDaThanhToan() {
        return false;
    }

    public static String nhapMaHD(Scanner sc) {
        return Ktradulieu.nhapChuoi(
                sc,
                "Nhập mã hóa đơn: ",
                ".+",
                "Mã hóa đơn không được để trống!"
        ).toUpperCase();
    }

    public static String nhapMaNV(Scanner sc) {
        return Ktradulieu.nhapChuoi(
                sc,
                "Nhập mã nhân viên: ",
                ".+",
                "Mã nhân viên không được để trống!"
        ).toUpperCase();
    }

    public abstract void inhoadon();

    public abstract long Tongtienhoadon();

    public long tinhTongTien() {
        return Tongtienhoadon();
    }
}
