package hoadon;

import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import khuyenmai.Khuyenmai;
import khuyenmai.SPKhMai;
import sanpham.SanPham;
import util.Ktradulieu;

public class CTHoaDon {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private long Giaban;
    private String MaHD;

    private long tienGiamThucTe;
    private long tienThueThucTe;
    private long tongTienSauThue;
    private int soLuongTangThucTe;
    private boolean daChotThanhToan;

    public CTHoaDon() {
        MaSP = "";
        TenSP = "";
        Soluong = 0;
        Giaban = 0;
        MaHD = "";
        tienGiamThucTe = 0;
        tienThueThucTe = 0;
        tongTienSauThue = 0;
        soLuongTangThucTe = 0;
        daChotThanhToan = false;
    }

    public CTHoaDon(String MaSP, String TenSP, int Soluong, long Giaban, String MaHD) {
        this();
        setMaSP(MaSP);
        setTenSP(TenSP);
        setSoluong(Soluong);
        setGiaban(Giaban);
        setMaHD(MaHD);
    }

    public String getMaSP() { return MaSP; }
    public String getTenSP() { return TenSP; }
    public int getSoluong() { return Soluong; }
    public int getSoLuong() { return Soluong; }
    public long getGiaban() { return Giaban; }
    public long getGiaBan() { return Giaban; }
    public String getMaHD() { return MaHD; }
    public long getTienGiamThucTe() { return tienGiamThucTe; }
    public long getTienThueThucTe() { return tienThueThucTe; }
    public long getTongTienSauThue() { return tongTienSauThue; }
    public int getSoLuongTangThucTe() { return soLuongTangThucTe; }
    public boolean isDaChotThanhToan() { return daChotThanhToan; }

    public void setMaSP(String MaSP) {
        if (MaSP == null || MaSP.trim().isEmpty())
            throw new IllegalArgumentException("Mã sản phẩm không được để trống!");
        this.MaSP = SanPham.chuanHoaMa(MaSP);
    }

    public void setTenSP(String TenSP) {
        if (TenSP == null || TenSP.trim().isEmpty())
            throw new IllegalArgumentException("Tên sản phẩm không được để trống!");
        this.TenSP = TenSP.trim();
    }

    public void setSoluong(int Soluong) {
        if (Soluong <= 0)
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0!");
        this.Soluong = Soluong;
    }

    public void setSoLuong(int soluong) {
        setSoluong(soluong);
    }

    public void setGiaban(long Giaban) {
        if (Giaban <= 0 || Giaban > SanPham.GIA_TOI_DA)
            throw new IllegalArgumentException(
                    "Giá bán phải lớn hơn 0 và không vượt quá " + SanPham.GIA_TOI_DA + "!");
        this.Giaban = Giaban;
    }

    public void setGiaBan(long giaBan) {
        setGiaban(giaBan);
    }

    public void setMaHD(String MaHD) {
        this.MaHD = MaHD == null || MaHD.trim().isEmpty()
                ? "" : SanPham.chuanHoaMa(MaHD);
    }

    public long TinhTienHang1SP() {
        return Math.multiplyExact((long) Soluong, Giaban);
    }

    private void kiemTraTongTien(long tongTien) {
        if (tongTien < 0)
            throw new IllegalArgumentException("Tổng tiền trước khuyến mãi không hợp lệ!");
    }

    public long tinhTienGiamTotNhat(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) return tienGiamThucTe;

        kiemTraTongTien(TongTienTruocKM);
        if (dsSPKM == null || dsKM == null) return 0;

        SPKhMai[] danhSach = dsSPKM.getDanhSach();
        if (danhSach == null) return 0;

        long thanhTienDong = TinhTienHang1SP();
        long tienGiamTotNhat = 0;

        for (SPKhMai spkm : danhSach) {
            if (spkm == null || spkm.getMaSP() == null || spkm.getMaKM() == null)
                continue;

            if (!spkm.getMaSP().equalsIgnoreCase(MaSP))
                continue;

            int viTri = dsKM.timKhuyenMai(spkm.getMaKM());
            if (viTri < 0) continue;

            Khuyenmai km = dsKM.getDanhSach()[viTri];
            if (km == null || !km.kiemTraApDung(MaSP, TongTienTruocKM, dsSPKM))
                continue;

            long tongGiam = km.tinhTongTienGiam(Giaban, Soluong);

            if (tongGiam < 0 || tongGiam > thanhTienDong)
                throw new IllegalArgumentException("Số tiền giảm không hợp lệ!");

            if (tongGiam > tienGiamTotNhat)
                tienGiamTotNhat = tongGiam;
        }

        return tienGiamTotNhat;
    }

    public int tinhSoLuongTangTotNhat(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) return soLuongTangThucTe;

        kiemTraTongTien(TongTienTruocKM);
        if (dsSPKM == null || dsKM == null) return 0;

        SPKhMai[] danhSach = dsSPKM.getDanhSach();
        if (danhSach == null) return 0;

        int tangTotNhat = 0;

        for (SPKhMai spkm : danhSach) {
            if (spkm == null || spkm.getMaSP() == null || spkm.getMaKM() == null)
                continue;

            if (!spkm.getMaSP().equalsIgnoreCase(MaSP))
                continue;

            int viTri = dsKM.timKhuyenMai(spkm.getMaKM());
            if (viTri < 0) continue;

            Khuyenmai km = dsKM.getDanhSach()[viTri];
            if (km == null || !km.kiemTraApDung(MaSP, TongTienTruocKM, dsSPKM))
                continue;

            int soTang = km.tinhSoLuongTang(Soluong);
            if (soTang < 0)
                throw new IllegalArgumentException("Số lượng tặng kèm không hợp lệ!");

            if (soTang > tangTotNhat)
                tangTotNhat = soTang;
        }

        return tangTotNhat;
    }

    public long tinhTienSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan)
            return Math.subtractExact(TinhTienHang1SP(), tienGiamThucTe);

        return Math.subtractExact(
                TinhTienHang1SP(),
                tinhTienGiamTotNhat(dsSPKM, dsKM, TongTienTruocKM)
        );
    }

    public long traCuuGiaSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (Soluong <= 0) return Giaban;

        long tienGiam = daChotThanhToan
                ? tienGiamThucTe
                : tinhTienGiamTotNhat(dsSPKM, dsKM, TongTienTruocKM);

        return (TinhTienHang1SP() - tienGiam) / Soluong;
    }

    private void kiemTraSanPham(SanPham sp) {
        if (sp == null)
            throw new IllegalArgumentException("Sản phẩm không được null!");

        if (!sp.getMaSP().equalsIgnoreCase(MaSP))
            throw new IllegalArgumentException("Mã sản phẩm không khớp với chi tiết hóa đơn!");
    }

    public long tinhTienThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) return tienThueThucTe;

        kiemTraSanPham(sp);

        double thue = sp.getThueSuat();
        if (!Double.isFinite(thue) || thue < 0 || thue > 100)
            throw new IllegalArgumentException("Thuế suất không hợp lệ!");

        long tienSauKM = tinhTienSauKM(dsSPKM, dsKM, TongTienTruocKM);
        return Math.round(tienSauKM * thue / 100.0);
    }

    public long tinhTongTienSauThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan) return tongTienSauThue;

        long tienSauKM = tinhTienSauKM(dsSPKM, dsKM, TongTienTruocKM);
        long tienThue = tinhTienThue(sp, dsSPKM, dsKM, TongTienTruocKM);

        return Math.addExact(tienSauKM, tienThue);
    }

    public void chotThanhToan(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            long TongTienTruocKM) {

        if (daChotThanhToan)
            throw new IllegalStateException("Chi tiết hóa đơn đã được chốt thanh toán!");

        kiemTraSanPham(sp);
        kiemTraTongTien(TongTienTruocKM);

        double thue = sp.getThueSuat();
        if (!Double.isFinite(thue) || thue < 0 || thue > 100)
            throw new IllegalArgumentException("Thuế suất không hợp lệ!");

        long giam = tinhTienGiamTotNhat(dsSPKM, dsKM, TongTienTruocKM);
        int tang = tinhSoLuongTangTotNhat(dsSPKM, dsKM, TongTienTruocKM);

        long tienHang = TinhTienHang1SP();
        long tienSauKM = Math.subtractExact(tienHang, giam);
        long thueThucTe = Math.round(tienSauKM * thue / 100.0);
        long tong = Math.addExact(tienSauKM, thueThucTe);

        this.tienGiamThucTe = giam;
        this.soLuongTangThucTe = tang;
        this.tienThueThucTe = thueThucTe;
        this.tongTienSauThue = tong;
        this.daChotThanhToan = true;
    }

    public void napSoLieuDaChot(long giam, long thue, long tong, int tang) {
        if (daChotThanhToan)
            throw new IllegalStateException("Chi tiết hóa đơn đã được chốt thanh toán!");

        if (Soluong <= 0 || Giaban <= 0)
            throw new IllegalStateException("Chưa có đầy đủ thông tin sản phẩm!");

        long tienHang = TinhTienHang1SP();

        if (giam < 0 || giam > tienHang || thue < 0 || tong < 0 || tang < 0)
            throw new IllegalArgumentException("Dữ liệu thanh toán đã chốt không hợp lệ!");

        long tongKiemTra = Math.addExact(Math.subtractExact(tienHang, giam), thue);

        if (tong != tongKiemTra)
            throw new IllegalArgumentException(
                    "Tổng tiền đã chốt không khớp với tiền hàng, tiền giảm và tiền thuế!");

        this.tienGiamThucTe = giam;
        this.tienThueThucTe = thue;
        this.tongTienSauThue = tong;
        this.soLuongTangThucTe = tang;
        this.daChotThanhToan = true;
    }

    public String toDongFile() {
        return MaSP + "|" + TenSP + "|" + Soluong + "|" + Giaban + "|" + MaHD
                + "|" + daChotThanhToan + "|" + tienGiamThucTe + "|" + tienThueThucTe
                + "|" + tongTienSauThue + "|" + soLuongTangThucTe;
    }

    public static CTHoaDon tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty()) return null;

        String[] t = dong.split("\\|", -1);

        if (t.length != 5 && t.length != 10)
            throw new IllegalArgumentException("Dòng dữ liệu chi tiết hóa đơn không đúng định dạng!");

        try {
            CTHoaDon ct = new CTHoaDon(
                    t[0], t[1],
                    Integer.parseInt(t[2]),
                    Long.parseLong(t[3]),
                    t[4]
            );

            if (t.length == 10) {
                if (!t[5].equalsIgnoreCase("true") && !t[5].equalsIgnoreCase("false"))
                    throw new IllegalArgumentException("Trạng thái chốt thanh toán không hợp lệ!");

                boolean daChot = Boolean.parseBoolean(t[5]);

                if (daChot) {
                    ct.napSoLieuDaChot(
                            Long.parseLong(t[6]),
                            Long.parseLong(t[7]),
                            Long.parseLong(t[8]),
                            Integer.parseInt(t[9])
                    );
                }
            }

            return ct;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Dữ liệu số trong chi tiết hóa đơn không hợp lệ!", e);
        }
    }

    @Override
    public String toString() {
        String ketQua = "Mã hóa đơn: " + MaHD
                + "\nMã sản phẩm: " + MaSP
                + "\nTên sản phẩm: " + TenSP
                + "\nSố lượng: " + Soluong
                + "\nGiá bán: " + Ktradulieu.dinhDangTien(Giaban)
                + "\nTiền hàng trước KM: " + Ktradulieu.dinhDangTien(TinhTienHang1SP());

        if (daChotThanhToan) {
            ketQua += "\nTiền giảm: " + Ktradulieu.dinhDangTien(tienGiamThucTe)
                    + (soLuongTangThucTe > 0
                    ? "\nSố lượng tặng kèm: " + soLuongTangThucTe : "")
                    + "\nTiền thuế: " + Ktradulieu.dinhDangTien(tienThueThucTe)
                    + "\nTổng tiền sau thuế: " + Ktradulieu.dinhDangTien(tongTienSauThue);
        }

        return ketQua;
    }
}