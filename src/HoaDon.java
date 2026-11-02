import java.util.ArrayList;
import java.util.List;

public class HoaDon {
    private String maHD;
    private double tongTien;
    private String trangThai;

    // Chi tiet hoa don duoc tao tu chi tiet don hang
    private List<CTHoaDon> danhSachChiTietHD = new ArrayList<>();

    // Lien ket don hang va khuyen mai
    private DonHang donHang;
    private Khuyenmai khuyenMai;

    private List<String> lichSuMaGiam = new ArrayList<>();

    // Dung cho cach khoi tao hoa don cu
    public HoaDon(String maHD, double tongTien, String trangThai) {
        this.maHD = maHD;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
    }

    // Tao hoa don tu don hang
    public HoaDon(String maHD, DonHang donHang) {
        this.maHD = maHD;
        this.donHang = donHang;
        this.tongTien = 0;
        this.trangThai = "Chua thanh toan";

        capNhatChiTietHoaDon();
    }

    // Lay cac chi tiet tu don hang
    private void capNhatChiTietHoaDon() {
        danhSachChiTietHD.clear();

        if (donHang == null) {
            return;
        }

        List<CTDonhang> ds =
            donHang.getDanhSachChiTiet();

        for (int i = 0; i < ds.size(); i++) {
            CTHoaDon ctHD = new CTHoaDon(
                maHD + "-CTHD" + (i + 1),
                ds.get(i),
                ""
            );

            danhSachChiTietHD.add(ctHD);
        }
    }

    // Gan khuyen mai cho hoa don
    public void apDungKhuyenMai(Khuyenmai khuyenMai) {
        this.khuyenMai = khuyenMai;

        if (khuyenMai != null) {
            lichSuMaGiam.add("Da ap dung: "
                + khuyenMai.getClass().getSimpleName());
        }
    }

    // Huy ap dung khuyen mai
    public void huyKhuyenMai() {
        this.khuyenMai = null;
    }

    // Tong tien hang truoc khuyen mai va thue
    public double tinhTongTienHang() {
        if (donHang == null) {
            return 0;
        }

        return donHang.tinhTongTienHang();
    }

    // Tien duoc giam, khong vuot qua tien hang
    public double tinhTienGiam() {
        if (khuyenMai == null) {
            return 0;
        }

        double tienGiam =
            khuyenMai.tinhTienGiamHopLe(tinhTongTienHang());

        if (tienGiam < 0) {
            return 0;
        }

        return Math.min(tienGiam, tinhTongTienHang());
    }

    // Tien hang sau khuyen mai
    public double tinhTienHangSauKhuyenMai() {
        return Math.max(0, tinhTongTienHang() - tinhTienGiam());
    }

    // Tong thue sau khi phan bo tien giam vao cac san pham
    public double tinhTongTienThue() {
        if (donHang == null) {
            return 0;
        }

        return donHang.tinhTongTienThueSauGiam(tinhTienGiam());
    }

    // Tong tien cuoi cung khach phai tra
    public double tinhTongTienPhaiThanhToan() {
        tongTien = tinhTienHangSauKhuyenMai()
                 + tinhTongTienThue();

        return tongTien;
    }

    // Hien thi hoa don va cac khoan tien
    public void hienThiHoaDon() {
        System.out.println("\n========== HOA DON ==========");
        System.out.println("Ma hoa don: " + maHD);

        if (donHang != null) {
            System.out.println("Ma don hang: " + donHang.getMaDH());

            Nhanvien nv = donHang.getNhanVien();
            Khachhang kh = donHang.getKhachHang();

            if (nv != null) {
                System.out.println("Nhan vien lap: " + nv);
            }

            if (kh != null) {
                System.out.println("Khach hang: " + kh);
            }

            // Dong bo chi tiet hoa don voi don hang
            capNhatChiTietHoaDon();

            System.out.println("\n--- CHI TIET HOA DON ---");
            for (CTHoaDon ct : danhSachChiTietHD) {
                ct.hienThiThongTinChiTiet();
                System.out.println("-------------------------");
            }

            System.out.println("Tong tien hang: "
                + tinhTongTienHang());

            System.out.println("Tien giam khuyen mai: "
                + tinhTienGiam());

            System.out.println("Tien hang sau khuyen mai: "
                + tinhTienHangSauKhuyenMai());

            System.out.println("Tong tien thue sau giam: "
                + tinhTongTienThue());

            System.out.println("Tong tien phai thanh toan: "
                + tinhTongTienPhaiThanhToan());
        } else {
            // Hoa don khoi tao theo constructor cu
            System.out.println("Tong tien: " + tongTien);
        }

        System.out.println("Trang thai thanh toan: " + trangThai);

        System.out.println("Lich su khuyen mai:");
        if (lichSuMaGiam.isEmpty()) {
            System.out.println("Chua ap dung khuyen mai.");
        } else {
            for (String ma : lichSuMaGiam) {
                System.out.println("- " + ma);
            }
        }

        System.out.println("==============================");
    }

    // Cap nhat trang thai thanh toan
    public void capNhatTrangThaiThanhToan(boolean daThanhToan) {
        if (daThanhToan) {
            if (donHang != null) {
                if (donHang.isDaHuy()) {
                    System.out.println(
                        "Don hang da huy, khong the thanh toan.");
                    return;
                }

                // Chi xac nhan don khi chua xac nhan truoc do
                if (!donHang.isDaXacNhan()) {
                    if (!donHang.xacNhanDonHang()) {
                        System.out.println(
                            "Khong the thanh toan don hang nay.");
                        return;
                    }
                }
            }

            trangThai = "Da thanh toan";
            tongTien = tinhTongTienPhaiThanhToan();
        } else {
            trangThai = "Chua thanh toan";
        }
    }

    public String getMaHD() {
        return maHD;
    }

    public double getTongTien() {
        if (donHang != null) {
            return tinhTongTienPhaiThanhToan();
        }

        return tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public Khuyenmai getKhuyenMai() {
        return khuyenMai;
    }

    public List<CTHoaDon> getDanhSachChiTietHD() {
        return danhSachChiTietHD;
    }
}
