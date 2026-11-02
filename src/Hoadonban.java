
import java.util.ArrayList;
import java.util.Scanner;

public class Hoadonban extends HoaDon {
    private String maKH;
    private ArrayList<CTHDonBan> danhSachChiTietHD;
    private ThanhToan thanhToan;

    public Hoadonban() {
        super();
        this.maKH = "";
        this.danhSachChiTietHD = new ArrayList<>();
        this.thanhToan = null;
    }

    public Hoadonban(String maHD, String maNV, String maKH) {
        super(maHD, maNV);
        setMaKH(maKH);
        this.danhSachChiTietHD = new ArrayList<>();
        this.thanhToan = null;
    }

    // Nhap thong tin hoa don ban
    public static Hoadonban Nhap(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner khong hop le!");
        }

        String maHD = HoaDon.nhapMaHD(sc);
        String maNV = HoaDon.nhapMaNV(sc);

        String maKH = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma khach hang: ",
                ".+",
                "Ma khach hang khong duoc de trong!"
        );

        return new Hoadonban(maHD, maNV, maKH);
    }

    // ================= GETTER =================

    public String getMaKH() {
        return maKH;
    }

    public ArrayList<CTHDonBan> getDanhSachChiTietHD() {
        return danhSachChiTietHD;
    }

    public ThanhToan getThanhToan() {
        return thanhToan;
    }

    // ================= SETTER =================

    public void setMaKH(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma khach hang khong hop le!");
        }
        this.maKH = maKH.trim();
    }

    public void setThanhToan(ThanhToan thanhToan) {
        this.thanhToan = thanhToan;
    }

    // ================= CHI TIET HOA DON =================

    public void themChiTietHoaDon(CTHDonBan ct) {
        if (ct == null) {
            throw new IllegalArgumentException("Chi tiet hoa don khong hop le!");
        }

        ct.setMaHD(maHD);
        danhSachChiTietHD.add(ct);
    }

    public void xoaChiTietHoaDon(int viTri) {
        if (viTri >= 0 && viTri < danhSachChiTietHD.size()) {
            danhSachChiTietHD.remove(viTri);
        } else {
            System.out.println("Vi tri khong hop le!");
        }
    }

    // ================= KHUYEN MAI =================

    public void ApMaGiamGia(Khuyenmai km) {
        for (CTHDonBan ct : danhSachChiTietHD) {
            ct.Apmagiamgia(km);
        }
    }

    // ================= TINH TIEN =================

    public double tinhTongTienHang() {
        double tong = 0;

        for (CTHDonBan ct : danhSachChiTietHD) {
            tong += ct.tinhTienHang();
        }

        return tong;
    }

    public double tongtienhoadon() {
        double tong = 0;

        for (CTHDonBan ct : danhSachChiTietHD) {
            tong += ct.ThanhTienSauKhuyenMai();
        }

        return tong;
    }

    @Override
    public double Tongtienhoadon() {
        return tongtienhoadon();
    }

    // ================= THANH TOAN =================

    public boolean thanhToanHoaDon() {
        if (thanhToan == null) {
            System.out.println("Chua chon phuong thuc thanh toan!");
            return false;
        }

        if (isDaThanhToan()) {
            System.out.println("Hoa don da duoc thanh toan!");
            return false;
        }

        thanhToan.setSoTien(tongtienhoadon());
        thanhToan.setMaNV(maNV);
        thanhToan.nhap();

        boolean ketQua = thanhToan.qTrinhThanhToan();

        if (ketQua) {
            trangThai = "Da thanh toan";
        } else {
            trangThai = "Chua thanh toan";
        }

        return ketQua;
    }

    // ================= IN HOA DON =================

    @Override
    public void inhoadon() {
        System.out.println("\n========== HOA DON BAN ==========");
        System.out.println("Ma hoa don: " + maHD);
        System.out.println("Ma khach hang: " + maKH);
        System.out.println("Ma nhan vien: " + maNV);
        System.out.println("Thoi gian in: " + thoigianin);

        System.out.println("\n--- CHI TIET HOA DON ---");

        for (CTHDonBan ct : danhSachChiTietHD) {
            System.out.println(ct);
            System.out.println("------------------------");
        }

        System.out.printf("Tong tien hang: %,.0f VND%n",
                tinhTongTienHang());

        System.out.printf("Tong tien giam: %,.0f VND%n",
                tinhTongTienHang() - tongtienhoadon());

        System.out.printf("Tong thanh toan: %,.0f VND%n",
                tongtienhoadon());

        System.out.println("Trang thai: " + trangThai);

        if (thanhToan != null) {
            System.out.println("Thanh toan: " + thanhToan);
        }

        System.out.println("=================================");
    }

    @Override
    public String toString() {
        return "MaHD: " + maHD
                + " | MaKH: " + maKH
                + " | MaNV: " + maNV
                + " | Thoi gian: " + thoigianin
                + " | Tong tien: "
                + String.format("%,.0f", tongtienhoadon())
                + " VND | Trang thai: " + trangThai;
    }
}
