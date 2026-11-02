
public class CTHoaDon {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private double Giaban;
    private String MaHD;

    // Constructor mac dinh
    public CTHoaDon() {
        MaSP = "";
        TenSP = "";
        Soluong = 0;
        Giaban = 0;
        MaHD = "";
    }

    // Constructor day du
    public CTHoaDon(String MaSP, String TenSP, int Soluong,
                    double Giaban, String MaHD) {
        setMaSP(MaSP);
        setTenSP(TenSP);
        setSoluong(Soluong);
        setGiaban(Giaban);
        setMaHD(MaHD);
    }

    // Getter
    public String getMaSP() {
        return MaSP;
    }

    public String getTenSP() {
        return TenSP;
    }

    public int getSoluong() {
        return Soluong;
    }

    public double getGiaban() {
        return Giaban;
    }

    public String getMaHD() {
        return MaHD;
    }

    // Setter
    public void setMaSP(String MaSP) {
        if (MaSP == null || MaSP.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã sản phẩm không được để trống!");
        }

        this.MaSP = SanPham.chuanHoaMa(MaSP);
    }

    public void setTenSP(String TenSP) {
        if (TenSP == null || TenSP.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tên sản phẩm không được để trống!");
        }

        this.TenSP = TenSP.trim();
    }

    public void setSoluong(int Soluong) {
        if (Soluong <= 0) {
            throw new IllegalArgumentException(
                    "Số lượng phải lớn hơn 0!");
        }

        this.Soluong = Soluong;
    }

    public void setGiaban(double Giaban) {
        if (!Double.isFinite(Giaban) || Giaban <= 0) {
            throw new IllegalArgumentException(
                    "Giá bán phải lớn hơn 0!");
        }

        this.Giaban = Giaban;
    }

    public void setMaHD(String MaHD) {
        if (MaHD == null || MaHD.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã hóa đơn không được để trống!");
        }

        this.MaHD = MaHD.trim();
    }

    // Tim gia san pham sau khuyen mai
    public double traCuuGiaSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        double giaSauKM = Giaban;

        if (dsSPKM == null || dsKM == null) {
            return giaSauKM;
        }

        if (tongTienDonHang < 0) {
            throw new IllegalArgumentException(
                    "Tổng tiền đơn hàng không hợp lệ!");
        }

        if (dsSPKM.getDanhSach() == null) {
            return giaSauKM;
        }

        for (SPKhMai spkm : dsSPKM.getDanhSach()) {

            if (spkm == null || spkm.getMaSP() == null) {
                continue;
            }

            if (spkm.getMaSP().equalsIgnoreCase(MaSP)) {

                int viTri = dsKM.timKhuyenMai(spkm.getMaKM());

                if (viTri == -1) {
                    continue;
                }

                Khuyenmai km = dsKM.getDanhSach()[viTri];

                if (km == null) {
                    continue;
                }

                if (!km.kiemTraThoiHan()
                        || tongTienDonHang < km.getMucChiToiThieu()) {
                    continue;
                }

                giaSauKM = km.traCuuGiaSanPhamSauKM(MaSP);

                if (!Double.isFinite(giaSauKM)
                        || giaSauKM < 0
                        || giaSauKM > Giaban) {
                    throw new IllegalArgumentException(
                            "Giá sau khuyến mãi không hợp lệ!");
                }

                return giaSauKM;
            }
        }

        return giaSauKM;
    }

    // Tinh tien hang truoc khuyen mai
    public double tinhTienHang() {
        return Soluong * Giaban;
    }

    // Tinh tien hang sau khuyen mai
    public double tinhTienSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        double giaSauKM = traCuuGiaSauKM(
                dsSPKM, dsKM, tongTienDonHang);

        return Soluong * giaSauKM;
    }

    // Tinh tien giam gia
    public double tinhTienGiam(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        return tinhTienHang()
                - tinhTienSauKM(dsSPKM, dsKM, tongTienDonHang);
    }

    // Tinh tien thue san pham
    public double tinhTienThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        if (sp == null) {
            throw new IllegalArgumentException(
                    "Sản phẩm không được null!");
        }

        if (!sp.getMaSP().equalsIgnoreCase(MaSP)) {
            throw new IllegalArgumentException(
                    "Mã sản phẩm không khớp với chi tiết hóa đơn!");
        }

        double giaSauKM = traCuuGiaSauKM(
                dsSPKM, dsKM, tongTienDonHang);

        double thue = sp.getThueSuat();

        if (!Double.isFinite(thue) || thue < 0 || thue > 100) {
            throw new IllegalArgumentException(
                    "Thuế suất không hợp lệ!");
        }

        return Soluong * giaSauKM * thue / 100;
    }

    // Tinh tong tien sau khuyen mai va thue
    public double tinhTongTienSauThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        return tinhTienSauKM(dsSPKM, dsKM, tongTienDonHang)
                + tinhTienThue(sp, dsSPKM, dsKM, tongTienDonHang);
    }

    // Hien thi thong tin
    @Override
    public String toString() {
        return "Mã hóa đơn: " + MaHD
                + "\nMã sản phẩm: " + MaSP
                + "\nTên sản phẩm: " + TenSP
                + "\nSố lượng: " + Soluong
                + "\nGiá bán: " + Giaban
                + "\nTiền hàng: " + tinhTienHang();
    }
}
