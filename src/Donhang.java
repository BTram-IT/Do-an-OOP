import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Donhang {

    private String maDonHang;
    private LocalDateTime ngayTao;
    private ThuNgan thuNgan;
    private Khachhang khachHang;
    private List<ChiTietDonHang> danhSachChiTiet;
    private Khuyenmai khuyenMai;
    private boolean daXacNhan;
    private boolean daHuy;

    // Constructor mac dinh
    public Donhang() {
        this.maDonHang = "DH00";
        this.ngayTao = LocalDateTime.now();
        this.thuNgan = null;
        this.khachHang = null;
        this.danhSachChiTiet = new ArrayList<>();
        this.khuyenMai = null;
        this.daXacNhan = false;
        this.daHuy = false;
    }

    // Constructor day du
    public Donhang(String maDonHang, ThuNgan thuNgan,
                   Khachhang khachHang, Khuyenmai khuyenMai) {
        this.maDonHang = maDonHang;
        this.ngayTao = LocalDateTime.now();
        this.thuNgan = thuNgan;
        this.khachHang = khachHang;
        this.danhSachChiTiet = new ArrayList<>();
        this.khuyenMai = khuyenMai;
        this.daXacNhan = false;
        this.daHuy = false;
    }

    // Getter
    public String getMaDonHang() {
        return maDonHang;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public ThuNgan getThuNgan() {
        return thuNgan;
    }

    public Khachhang getKhachHang() {
        return khachHang;
    }

    public List<ChiTietDonHang> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public Khuyenmai getKhuyenMai() {
        return khuyenMai;
    }

    public boolean isDaXacNhan() {
        return daXacNhan;
    }

    public boolean isDaHuy() {
        return daHuy;
    }

    // Setter
    public void setMaDonHang(String maDonHang) {
        if (!daXacNhan && !daHuy) {
            this.maDonHang = maDonHang;
        }
    }

    public void setThuNgan(ThuNgan thuNgan) {
        if (!daXacNhan && !daHuy) {
            this.thuNgan = thuNgan;
        }
    }

    public void setKhachHang(Khachhang khachHang) {
        if (!daXacNhan && !daHuy) {
            this.khachHang = khachHang;
        }
    }

    public void setKhuyenMai(Khuyenmai khuyenMai) {
        if (!daXacNhan && !daHuy) {
            this.khuyenMai = khuyenMai;
        } else {
            System.out.println(
                    "Khong the thay doi khuyen mai cua don hang nay!"
            );
        }
    }

    // Them mon vao don hang, chua tru kho
    public boolean themMon(SanPham sp, int soLuong) {
        if (daXacNhan || daHuy) {
            System.out.println(
                    "Don hang khong con o trang thai tao moi!"
            );
            return false;
        }

        if (sp == null || soLuong <= 0) {
            System.out.println(
                    "San pham hoac so luong khong hop le!"
            );
            return false;
        }

        danhSachChiTiet.add(new ChiTietDonHang(sp, soLuong));

        System.out.println("Da them san pham: " + sp.getTenSP());
        return true;
    }

    // Tinh tong tien hang truoc thue
    public double getTongTienHang() {
        double tong = 0;

        for (ChiTietDonHang ct : danhSachChiTiet) {
            tong += ct.tinhTienTruocThue();
        }

        return tong;
    }

    // Tinh tong tien thue
    public double getTongTienThue() {
        double tong = 0;

        for (ChiTietDonHang ct : danhSachChiTiet) {
            tong += ct.tinhTienThue();
        }

        return tong;
    }

    // Tong tien truoc khi tru khuyen mai
    public double getTongTienTruocKhuyenMai() {
        return getTongTienHang() + getTongTienThue();
    }

    // Tinh tien giam khuyen mai
    public double tinhTienGiamKM() {
        if (khuyenMai == null) {
            return 0;
        }

        double tongTien = getTongTienTruocKhuyenMai();
        double tienGiam = khuyenMai.tinhTienGiam(tongTien);

        // Tien giam khong am va khong vuot tong tien
        return Math.max(0, Math.min(tienGiam, tongTien));
    }

    // Tinh tong tien khach phai thanh toan
    public double getTongThanhToan() {
        return Math.max(
                0,
                getTongTienTruocKhuyenMai() - tinhTienGiamKM()
        );
    }

    // Tinh tong so luong cua cung mot doi tuong san pham
    private int tinhTongSoLuongSanPham(SanPham sp) {
        int tong = 0;

        for (ChiTietDonHang ct : danhSachChiTiet) {
            if (ct.getSanPham() == sp) {
                tong += ct.getSoLuong();
            }
        }

        return tong;
    }

    // Kiem tra san pham da xuat hien truoc vi tri hien tai chua
    private boolean daKiemTraSanPham(SanPham sp, int viTri) {
        for (int i = 0; i < viTri; i++) {
            if (danhSachChiTiet.get(i).getSanPham() == sp) {
                return true;
            }
        }

        return false;
    }

    // Xac nhan don hang va tru kho
    public boolean xacNhanDonHang() {
        if (daHuy || daXacNhan || danhSachChiTiet.isEmpty()) {
            System.out.println("Khong the xac nhan don hang!");
            return false;
        }

        // Phai co thu ngan lap don
        if (thuNgan == null) {
            System.out.println(
                    "Vui long chon thu ngan truoc khi xac nhan don!"
            );
            return false;
        }

        // Buoc 1: Kiem tra ton kho cua tat ca san pham
        for (int i = 0; i < danhSachChiTiet.size(); i++) {
            ChiTietDonHang ct = danhSachChiTiet.get(i);
            SanPham sp = ct.getSanPham();

            if (daKiemTraSanPham(sp, i)) {
                continue;
            }

            int soLuongCanBan = tinhTongSoLuongSanPham(sp);

            if (!sp.kiemTraTonKho(soLuongCanBan)) {
                System.out.println(
                        "Ton kho khong du cho san pham: "
                                + sp.getTenSP()
                );
                return false;
            }
        }

        // Buoc 2: Chi tru kho sau khi tat ca san pham du hang
        for (int i = 0; i < danhSachChiTiet.size(); i++) {
            ChiTietDonHang ct = danhSachChiTiet.get(i);
            SanPham sp = ct.getSanPham();

            if (daKiemTraSanPham(sp, i)) {
                continue;
            }

            int soLuongCanBan = tinhTongSoLuongSanPham(sp);
            sp.giamSoLuong(soLuongCanBan);
        }

        daXacNhan = true;

        System.out.println("Xac nhan don hang thanh cong!");
        return true;
    }

    // Huy don hang, hoan kho neu da xac nhan
    public void huyDonHang() {
        if (daHuy) {
            System.out.println("Don hang da huy truoc do!");
            return;
        }

        if (daXacNhan) {
            for (int i = 0; i < danhSachChiTiet.size(); i++) {
                ChiTietDonHang ct = danhSachChiTiet.get(i);
                SanPham sp = ct.getSanPham();

                if (daKiemTraSanPham(sp, i)) {
                    continue;
                }

                int soLuongHoan = tinhTongSoLuongSanPham(sp);
                sp.tangSoLuong(soLuongHoan);
            }
        }

        daHuy = true;
        System.out.println("Da huy don hang!");
    }

    // In don hang
    public void inDonHang() {
        System.out.println("========================================");
        System.out.println("                 DON HANG");
        System.out.println("========================================");

        System.out.println("Ma don hang: " + maDonHang);
        System.out.println("Ngay tao: " + ngayTao);

        System.out.println("Thu ngan: "
                + (thuNgan != null
                ? thuNgan.getHoten()
                : "Chua co"));

        System.out.println("Khach hang: "
                + (khachHang != null
                ? khachHang.getHoten()
                : "Khach le"));

        System.out.println("----------------------------------------");

        for (ChiTietDonHang ct : danhSachChiTiet) {
            ct.hienThi();
            System.out.println("----------------------------------------");
        }

        System.out.println(
                "Tong tien hang: " + getTongTienHang() + " VND"
        );

        System.out.println(
                "Tong tien thue: " + getTongTienThue() + " VND"
        );

        System.out.println("Khuyen mai: "
                + (khuyenMai != null
                ? khuyenMai.getTenKM()
                : "Khong co"));

        System.out.println(
                "Tien giam khuyen mai: " + tinhTienGiamKM() + " VND"
        );

        System.out.println(
                "Tong thanh toan: " + getTongThanhToan() + " VND"
        );

        System.out.println("Trang thai: "
                + (daHuy
                ? "Da huy"
                : daXacNhan
                ? "Da xac nhan"
                : "Chua xac nhan"));

        System.out.println("========================================");
    }
}