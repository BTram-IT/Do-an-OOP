public class CTHoaDon {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private double Giaban;
    private String MaHD;

    // Constructor mặc định
    public CTHoaDon() {
        MaSP = "";
        TenSP = "";
        Soluong = 0;
        Giaban = 0;
        MaHD = "";
    }

    // Constructor đầy đủ
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
            throw new IllegalArgumentException("Mã sản phẩm không được để trống!");
        }
        this.MaSP = SanPham.chuanHoaMa(MaSP);
    }

    public void setTenSP(String TenSP) {
        if (TenSP == null || TenSP.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        }
        this.TenSP = TenSP.trim();
    }

    public void setSoluong(int Soluong) {
        if (Soluong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0!");
        }
        this.Soluong = Soluong;
    }

    public void setGiaban(double Giaban) {
        if (!Double.isFinite(Giaban) || Giaban <= 0) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0!");
        }
        this.Giaban = Giaban;
    }

    public void setMaHD(String MaHD) {
        if (MaHD == null || MaHD.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống!");
        }
        this.MaHD = MaHD.trim();
    }

    // Tìm giá sản phẩm sau khuyến mãi
    public double traCuuGiaSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        // Mặc định không có khuyến mãi
        double giaSauKM = Giaban;

        // Kiểm tra dữ liệu đầu vào
        if (dsSPKM == null || dsKM == null) {
            return giaSauKM;
        }

        if (tongTienDonHang < 0) {
            throw new IllegalArgumentException("Tổng tiền đơn hàng không hợp lệ!");
        }

        if (dsSPKM.getDanhSach() == null) {
            return giaSauKM;
        }

        // Duyệt danh sách sản phẩm được áp dụng khuyến mãi
        for (SPKhMai spkm : dsSPKM.getDanhSach()) {

            if (spkm == null || spkm.getMaSP() == null) {
                continue;
            }

            if (spkm.getMaSP().equalsIgnoreCase(MaSP)) {

                // Tìm chương trình khuyến mãi theo mã
                int viTri = dsKM.timKhuyenMai(spkm.getMaKM());

                if (viTri == -1) {
                    continue;
                }

                Khuyenmai km = dsKM.getDanhSach()[viTri];

                if (km == null) {
                    continue;
                }

                // Kiểm tra thời hạn và mức chi tiêu tối thiểu
                if (!km.kiemTraThoiHan()
                        || tongTienDonHang < km.getMucChiToiThieu()) {
                    continue;
                }

                // Tính giá sau khuyến mãi
                giaSauKM = km.traCuuGiaSanPhamSauKM(MaSP);

                // Kiểm tra giá sau khuyến mãi
                if (!Double.isFinite(giaSauKM)
                        || giaSauKM < 0
                        || giaSauKM > Giaban) {
                    throw new IllegalArgumentException(
                            "Giá sau khuyến mãi không hợp lệ!");
                }

                // Một sản phẩm chỉ áp dụng một chương trình
                return giaSauKM;
            }
        }

        return giaSauKM;
    }

    // Tính tiền hàng trước khuyến mãi
    public double tinhTienHang() {
        return Soluong * Giaban;
    }

    // Tính tiền hàng sau khuyến mãi
    public double tinhTienSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        double giaSauKM = traCuuGiaSauKM(
                dsSPKM, dsKM, tongTienDonHang);

        return Soluong * giaSauKM;
    }

    // Tính tiền giảm giá
    public double tinhTienGiam(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        return tinhTienHang()
                - tinhTienSauKM(dsSPKM, dsKM, tongTienDonHang);
    }

    // Tính tiền thuế của sản phẩm
    public double tinhTienThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        // Kiểm tra sản phẩm
        if (sp == null) {
            throw new IllegalArgumentException("Sản phẩm không được null!");
        }

        if (!sp.getMaSP().equalsIgnoreCase(MaSP)) {
            throw new IllegalArgumentException(
                    "Mã sản phẩm không khớp với chi tiết hóa đơn!");
        }

        // Lấy giá sau khuyến mãi
        double giaSauKM = traCuuGiaSauKM(
                dsSPKM, dsKM, tongTienDonHang);

        // Lấy thuế suất theo dạng phần trăm
        double thue = sp.getThueSuat();

        // Kiểm tra thuế suất
        if (!Double.isFinite(thue) || thue < 0 || thue > 100) {
            throw new IllegalArgumentException("Thuế suất không hợp lệ!");
        }

        // Tiền thuế = số lượng * giá sau khuyến mãi * thuế suất / 100
        return Soluong * giaSauKM * thue / 100;
    }

    // Tính tổng tiền hàng sau khuyến mãi và thuế
    public double tinhTongTienSauThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        return tinhTienSauKM(dsSPKM, dsKM, tongTienDonHang)
                + tinhTienThue(sp, dsSPKM, dsKM, tongTienDonHang);
    }

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