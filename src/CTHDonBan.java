
public class CTHDonBan {
    private String maHD;
    private String maSP;
    private String tenSP;
    private int soLuong;
    private double giaBan;
    private Khuyenmai khuyenMai;
    private double mucGiamGia;

    public CTHDonBan() {
        this.maHD = "";
        this.maSP = "";
        this.tenSP = "";
        this.soLuong = 0;
        this.giaBan = 0;
        this.khuyenMai = null;
        this.mucGiamGia = 0;
    }

    public CTHDonBan(String maHD, String maSP, String tenSP,
                     int soLuong, double giaBan) {
        this.maHD = maHD;
        this.maSP = maSP;
        this.tenSP = tenSP;
        setSoLuong(soLuong);
        setGiaBan(giaBan);
        this.khuyenMai = null;
        this.mucGiamGia = 0;
    }

    // ================= GETTER =================

    public String getMaHD() {
        return maHD;
    }

    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public Khuyenmai getKhuyenMai() {
        return khuyenMai;
    }

    public double getMucGiamGia() {
        return mucGiamGia;
    }

    // ================= SETTER =================

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0!");
        }
        this.soLuong = soLuong;
    }

    public void setGiaBan(double giaBan) {
        if (giaBan < 0) {
            throw new IllegalArgumentException("Gia ban khong hop le!");
        }
        this.giaBan = giaBan;
    }

    // ================= TINH TIEN =================

    public double tinhTienHang() {
        return soLuong * giaBan;
    }

    // ================= KHUYEN MAI =================

    public void Apmagiamgia(Khuyenmai km) {
        if (km == null) {
            this.khuyenMai = null;
            this.mucGiamGia = 0;
            return;
        }

        this.khuyenMai = km;

        if (!km.kiemTraThoiHan()) {
            this.mucGiamGia = 0;
            return;
        }

        double giaSauKM = km.traCuuGiaSanPhamSauKM(maSP);

        if (giaSauKM < 0 || giaSauKM > giaBan) {
            giaSauKM = giaBan;
        }

        this.mucGiamGia = soLuong * (giaBan - giaSauKM);
    }

    public double ThanhTienSauKhuyenMai() {
        return Math.max(0, tinhTienHang() - mucGiamGia);
    }

    @Override
    public String toString() {
        return "Ma hoa don: " + maHD
                + " | Ma san pham: " + maSP
                + " | Ten san pham: " + tenSP
                + " | So luong: " + soLuong
                + " | Gia ban: " + String.format("%,.0f", giaBan)
                + " VND | Tien giam: "
                + String.format("%,.0f", mucGiamGia)
                + " VND | Thanh tien: "
                + String.format("%,.0f", ThanhTienSauKhuyenMai())
                + " VND";
    }
}
