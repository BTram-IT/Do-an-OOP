
import java.util.Scanner;

public class CTHoaDon {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private double Giaban;
    private String MaHD;

    private Khuyenmai khuyenMai;
    private double mucGiamGia;

    public CTHoaDon() {
        MaSP = "";
        TenSP = "";
        Soluong = 0;
        Giaban = 0;
        MaHD = "";
        khuyenMai = null;
        mucGiamGia = 0;
    }

    public CTHoaDon(String MaSP, String TenSP, int Soluong,
                    double Giaban, String MaHD) {
        setMaSP(MaSP);
        setTenSP(TenSP);
        setSoluong(Soluong);
        setGiaban(Giaban);
        setMaHD(MaHD);
        khuyenMai = null;
        mucGiamGia = 0;
    }

    // Nhập thông tin chi tiết hóa đơn
    public static CTHoaDon Nhap(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner khong hop le!");
        }

        String maSP = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma san pham: ",
                "(?i)(TA|DU)\\d{3}",
                "Ma san pham khong hop le!"
        );

        String tenSP = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ten san pham: ",
                ".+",
                "Ten san pham khong duoc de trong!"
        );

        long soLuong;
        do {
            soLuong = Ktradulieu.nhapSoNguyen(
                    sc,
                    "Nhap so luong: "
            );

            if (soLuong > Integer.MAX_VALUE) {
                System.out.println("So luong qua lon! Vui long nhap lai.");
            }
        } while (soLuong > Integer.MAX_VALUE);

        double giaBan = Ktradulieu.nhapSoThuc(
                sc,
                "Nhap gia ban: "
        );

        String maHD = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma hoa don: ",
                ".+",
                "Ma hoa don khong duoc de trong!"
        );

        return new CTHoaDon(
                maSP,
                tenSP,
                (int) soLuong,
                giaBan,
                maHD
        );
    }

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

    public Khuyenmai getKhuyenMai() {
        return khuyenMai;
    }

    public double getMucGiamGia() {
        return mucGiamGia;
    }

    public void setMaSP(String MaSP) {
        if (MaSP == null || !MaSP.trim().matches("(?i)(TA|DU)\\d{3}")) {
            throw new IllegalArgumentException("Ma san pham khong hop le!");
        }
        this.MaSP = SanPham.chuanHoaMa(MaSP);
    }

    public void setTenSP(String TenSP) {
        if (TenSP == null || TenSP.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten san pham khong hop le!");
        }
        this.TenSP = TenSP.trim();
    }

    public void setSoluong(int Soluong) {
        if (Soluong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0!");
        }
        this.Soluong = Soluong;
    }

    public void setGiaban(double Giaban) {
        if (!Double.isFinite(Giaban) || Giaban <= 0) {
            throw new IllegalArgumentException("Gia ban khong hop le!");
        }
        this.Giaban = Giaban;
    }

    public void setMaHD(String MaHD) {
        if (MaHD == null || MaHD.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma hoa don khong hop le!");
        }
        this.MaHD = MaHD.trim();
    }

    // Ap dung khuyen mai truc tiep cho chi tiet hoa don
    public void ApMaGiamGia(Khuyenmai km, double tongTienDonHang) {
        this.khuyenMai = km;
        this.mucGiamGia = 0;

        if (km == null || !km.kiemTraThoiHan()
                || tongTienDonHang < km.getMucChiToiThieu()) {
            return;
        }

        double giaSauKM = km.traCuuGiaSanPhamSauKM(MaSP);

        if (giaSauKM >= 0 && giaSauKM <= Giaban) {
            mucGiamGia = (Giaban - giaSauKM) * Soluong;
        }
    }

    // Tuong thich voi bo danh sach khuyen mai hien tai cua ban
    public double traCuuGiaSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        if (dsSPKM == null || dsKM == null) {
            return Giaban;
        }

        if (tongTienDonHang < 0) {
            throw new IllegalArgumentException("Tong tien khong hop le!");
        }

        for (SPKhMai spkm : dsSPKM.getDanhSach()) {
            if (spkm == null || spkm.getMaSP() == null) {
                continue;
            }

            if (spkm.getMaSP().equalsIgnoreCase(MaSP)) {
                int viTri = dsKM.timKhuyenMai(spkm.getMaKM());

                if (viTri < 0) {
                    continue;
                }

                Khuyenmai km = dsKM.getDanhSach()[viTri];

                if (km == null || !km.kiemTraThoiHan()
                        || tongTienDonHang < km.getMucChiToiThieu()) {
                    continue;
                }

                double giaSauKM = km.traCuuGiaSanPhamSauKM(MaSP);

                if (giaSauKM < 0) {
                    continue;
                }

                if (!Double.isFinite(giaSauKM) || giaSauKM > Giaban) {
                    throw new IllegalArgumentException("Gia sau khuyen mai khong hop le!");
                }

                return giaSauKM;
            }
        }

        return Giaban;
    }

    public double tinhTienHang() {
        return Soluong * Giaban;
    }

    public double tinhTienSauKM(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        return Soluong * traCuuGiaSauKM(dsSPKM, dsKM, tongTienDonHang);
    }

    public double tinhTienGiam(
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        return tinhTienHang()
                - tinhTienSauKM(dsSPKM, dsKM, tongTienDonHang);
    }

    public double tinhTienThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        if (sp == null || !sp.getMaSP().equalsIgnoreCase(MaSP)) {
            throw new IllegalArgumentException("San pham khong hop le!");
        }

        double giaSauKM = traCuuGiaSauKM(dsSPKM, dsKM, tongTienDonHang);
        double thueSuat = sp.getThueSuat();

        return Soluong * giaSauKM * thueSuat / 100;
    }

    public double tinhTongTienSauThue(
            SanPham sp,
            DSSanPhamKhuyenMai dsSPKM,
            DSKhuyenMai dsKM,
            double tongTienDonHang) {

        return tinhTienSauKM(dsSPKM, dsKM, tongTienDonHang)
                + tinhTienThue(sp, dsSPKM, dsKM, tongTienDonHang);
    }

    public double ThanhTienSauKhuyenMai() {
        return Math.max(0, tinhTienHang() - mucGiamGia);
    }

    @Override
    public String toString() {
        return "MaHD: " + MaHD
                + "\nMaSP: " + MaSP
                + "\nTenSP: " + TenSP
                + "\nSo luong: " + Soluong
                + "\nGia ban: " + String.format("%,.0f", Giaban)
                + "\nTien hang: " + String.format("%,.0f", tinhTienHang())
                + "\nTien giam: " + String.format("%,.0f", mucGiamGia)
                + "\nThanh tien: " + String.format("%,.0f", ThanhTienSauKhuyenMai());
    }
}
