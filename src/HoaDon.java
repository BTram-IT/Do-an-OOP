import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public abstract class HoaDon  {
    private String MaHD;
    private String MaNV;
    private LocalDateTime thoigianin;

    public String getMaHD() {
        return MaHD;
    }

    public void setMaHD(String maHD) {
        MaHD = maHD;
    }

    public String getMaNV() {
        return MaNV;
    }

    public void setMaNV(String maNV) {
        MaNV = maNV;
    }

    public LocalDateTime getThoigianin() {
        return thoigianin;
    }

    public void setThoigianin(LocalDateTime thoigianin) {
        this.thoigianin = thoigianin;
    }

    public abstract void inhoadon();
    public abstract double Tongtienhoadon();



}