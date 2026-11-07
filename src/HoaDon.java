import java.time.LocalDateTime;

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
        setMaHD(maHD);
        setMaNV(maNV);
        setThoigianin(thoigianin);
    }

    public String getMaHD() {
        return MaHD;
    }

    public void setMaHD(String maHD) {
        this.MaHD = (maHD == null) ? "" : SanPham.chuanHoaMa(maHD);
    }

    public String getMaNV() {
        return MaNV;
    }

    public void setMaNV(String maNV) {
        this.MaNV = (maNV == null) ? "" : SanPham.chuanHoaMa(maNV);
    }

    public LocalDateTime getThoigianin() {
        return thoigianin;
    }

    public void setThoigianin(LocalDateTime thoigianin) {
        this.thoigianin = (thoigianin == null) ? LocalDateTime.now() : thoigianin;
    }
    public boolean isDaThanhToan() {
        return false;
    }

    public abstract void inhoadon();

    public abstract long Tongtienhoadon();
}

