public class CTHoaDon {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private long Giaban;
    private String MaHD;

    // Các giá trị được chốt tại thời điểm thanh toán
    private long tienGiamThucTe;
    private long tienThueThucTe;
    private long tongTienSauThue;
    private int soLuongTangThucTe;
    private boolean daChotThanhToan;

    // Constructor mặc định
    public CTHoaDon() {
        this.MaSP = "";
        this.TenSP = "";
        this.Soluong = 0;
        this.Giaban = 0;
        this.MaHD = "";

        this.tienGiamThucTe = 0;
        this.tienThueThucTe = 0;
        this.tongTienSauThue = 0;
        this.soLuongTangThucTe = 0;
        this.daChotThanhToan = false;
    }

    // Constructor đầy đủ
    public CTHoaDon(String MaSP, String TenSP, int Soluong,
                    long Giaban, String MaHD) {
        this();
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

    public long getGiaban() {
        return Giaban;
    }

    public String getMaHD() {
        return MaHD;
    }

    public long getTienGiamThucTe() {
        return tienGiamThucTe;
    }

    public long getTienThueThucTe() {
        return tienThueThucTe;
    }

    public long getTongTienSauThue() {
        return tongTienSauThue;
    }

    public int getSoLuongTangThucTe() {
        return soLuongTangThucTe;
    }

    public boolean isDaChotThanhToan() {
        return daChotThanhToan;
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

    public void setGiaban(long Giaban) {
        if (Giaban <= 0 || Giaban > SanPham.GIA_TOI_DA) {
            throw new IllegalArgumentException(
                    "Giá bán phải lớn hơn 0 và không vượt quá " + SanPham.GIA_TOI_DA + "!");
        }
        this.Giaban = Giaban;
    }

    public void setMaHD(String MaHD) {
        if (MaHD == null || MaHD.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã hóa đơn không được để trống!");
        }
        this.MaHD = SanPham.chuanHoaMa(MaHD);
    }

    // Tính tiền hàng một dòng sản phẩm trước khuyến mãi
    public long TinhTienHang1SP() {
        return Math.multiplyExact((long) Soluong, Giaban);
    }

    // Tính số tiền giảm tốt nhất trên cả dòng sản phẩm
    public long tinhTienGiamTotNhat(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) {
            return tienGiamThucTe;
        }

        if (dsSPKM == null || dsKM == null) {
            return 0;
        }

        if (TongTienTruocKM < 0) {
            throw new IllegalArgumentException(
                    "Tổng tiền trước khuyến mãi không hợp lệ!");
        }

        long thanhTienDong = TinhTienHang1SP();
        long tienGiamTotNhat = 0;

        for (SPKhMai spkm : dsSPKM.getDanhSach()) {
            if (spkm == null || spkm.getMaSP() == null || spkm.getMaKM() == null) {
                continue;
            }

            if (!spkm.getMaSP().equalsIgnoreCase(MaSP)) {
                continue;
            }

            int viTri = dsKM.timKhuyenMai(spkm.getMaKM());
            if (viTri == -1) {
                continue;
            }

            Khuyenmai km = dsKM.getDanhSach()[viTri];
            if (km == null) {
                continue;
            }

            if (!km.kiemTraApDung(MaSP, TongTienTruocKM, dsSPKM)) {
                continue;
            }

            long tongGiam = km.tinhTongTienGiam(Giaban, Soluong);
            if (tongGiam < 0 || tongGiam > thanhTienDong) {
                throw new IllegalArgumentException("Số tiền giảm không hợp lệ!");
            }

            if (tongGiam > tienGiamTotNhat) {
                tienGiamTotNhat = tongGiam;
            }
        }

        return tienGiamTotNhat;
    }

    // Tính số lượng tặng kèm tốt nhất (cho chương trình Mua-Tặng)
    public int tinhSoLuongTangTotNhat(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) {
            return soLuongTangThucTe;
        }

        if (dsSPKM == null || dsKM == null || TongTienTruocKM < 0) {
            return 0;
        }

        int tangTotNhat = 0;

        for (SPKhMai spkm : dsSPKM.getDanhSach()) {
            if (spkm == null || spkm.getMaSP() == null || spkm.getMaKM() == null) {
                continue;
            }
            if (!spkm.getMaSP().equalsIgnoreCase(MaSP)) {
                continue;
            }
            int viTri = dsKM.timKhuyenMai(spkm.getMaKM());
            if (viTri == -1) {
                continue;
            }
            Khuyenmai km = dsKM.getDanhSach()[viTri];
            if (km == null || !km.kiemTraApDung(MaSP, TongTienTruocKM, dsSPKM)) {
                continue;
            }
            int soTang = km.tinhSoLuongTang(Soluong);
            if (soTang > tangTotNhat) {
                tangTotNhat = soTang;
            }
        }

        return tangTotNhat;
    }

    // Tính tiền hàng sau khuyến mãi
    public long tinhTienSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) {
            return TinhTienHang1SP() - tienGiamThucTe;
        }

        return TinhTienHang1SP()
                - tinhTienGiamTotNhat(dsSPKM, dsKM, TongTienTruocKM);
    }

    // Tìm giá trung bình một sản phẩm sau khuyến mãi
    public long traCuuGiaSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (Soluong <= 0) {
            return Giaban;
        }
        long tienGiam = daChotThanhToan
                ? tienGiamThucTe
                : tinhTienGiamTotNhat(dsSPKM, dsKM, TongTienTruocKM);

        return Giaban - (tienGiam / Soluong);
    }

    // Tính tiền thuế
    public long tinhTienThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) {
            return tienThueThucTe;
        }

        if (sp == null) {
            throw new IllegalArgumentException(
                    "Sản phẩm không được null!");
        }

        if (!sp.getMaSP().equalsIgnoreCase(MaSP)) {
            throw new IllegalArgumentException(
                    "Mã sản phẩm không khớp với chi tiết hóa đơn!");
        }

        double thue = sp.getThueSuat();

        if (!Double.isFinite(thue) || thue < 0 || thue > 100) {
            throw new IllegalArgumentException(
                    "Thuế suất không hợp lệ!");
        }

        long tienSauKM = tinhTienSauKM(
                dsSPKM, dsKM, TongTienTruocKM);

        return Math.round(tienSauKM * thue / 100.0);
    }

    // Tính tổng tiền sau khuyến mãi và thuế
    public long tinhTongTienSauThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) {
            return tongTienSauThue;
        }

        return tinhTienSauKM(dsSPKM, dsKM, TongTienTruocKM)
                + tinhTienThue(sp, dsSPKM, dsKM, TongTienTruocKM);
    }

    // Chốt giá trị hóa đơn tại thời điểm thanh toán
    public void chotThanhToan(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) {
            throw new IllegalStateException(
                    "Chi tiết hóa đơn đã được chốt thanh toán!");
        }

        if (sp == null || !sp.getMaSP().equalsIgnoreCase(MaSP)) {
            throw new IllegalArgumentException(
                    "Sản phẩm không hợp lệ!");
        }

        double thue = sp.getThueSuat();

        if (!Double.isFinite(thue) || thue < 0 || thue > 100) {
            throw new IllegalArgumentException(
                    "Thuế suất không hợp lệ!");
        }

        long giam = tinhTienGiamTotNhat(dsSPKM, dsKM, TongTienTruocKM);
        int tang = tinhSoLuongTangTotNhat(dsSPKM, dsKM, TongTienTruocKM);
        long tienSauKM = TinhTienHang1SP() - giam;

        this.tienGiamThucTe = giam;
        this.soLuongTangThucTe = tang;
        this.tienThueThucTe = Math.round(tienSauKM * thue / 100.0);
        this.tongTienSauThue = Math.addExact(tienSauKM, this.tienThueThucTe);
        this.daChotThanhToan = true;
    }

    @Override
    public String toString() {
        String ketQua = "Mã hóa đơn: " + MaHD
                + "\nMã sản phẩm: " + MaSP
                + "\nTên sản phẩm: " + TenSP
                + "\nSố lượng: " + Soluong
                + "\nGiá bán: " + Giaban
                + "\nTiền hàng trước KM: " + TinhTienHang1SP();

        if (daChotThanhToan) {
            ketQua += "\nTiền giảm: " + tienGiamThucTe
                    + (soLuongTangThucTe > 0 ? "\nSố lượng tặng kèm: " + soLuongTangThucTe : "")
                    + "\nTiền thuế: " + tienThueThucTe
                    + "\nTổng tiền sau thuế: " + tongTienSauThue;
        }

        return ketQua;
    }
}

