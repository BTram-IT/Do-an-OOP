
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {

    private String maHD;
    private String maNV;
    private LocalDateTime thoiGianIn;
    private double tongTien;
    private String trangThai;

    private List<CTHoaDon> danhSachChiTietHD = new ArrayList<>();
    private Khuyenmai khuyenMai;
    private List<String> lichSuMaGiam = new ArrayList<>();

    // Constructor mac dinh
    public HoaDon() {
        this.maHD = "";
        this.maNV = "";
        this.thoiGianIn = LocalDateTime.now();
        this.tongTien = 0;
        this.trangThai = "Chua thanh toan";
    }

    // Constructor tuong thich voi Main cu
    public HoaDon(String maHD, double tongTien, String trangThai) {
        this.maHD = maHD;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.maNV = "";
        this.thoiGianIn = LocalDateTime.now();
    }

    // Constructor day du
    public HoaDon(String maHD, String maNV) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.thoiGianIn = LocalDateTime.now();
        this.tongTien = 0;
        this.trangThai = "Chua thanh toan";
    }

    // Getter
    public String getMaHD() {
        return maHD;
    }

    public String getMaNV() {
        return maNV;
    }

    public LocalDateTime getThoiGianIn() {
        return thoiGianIn;
    }

    public double getTongTien() {
        return tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public List<CTHoaDon> getDanhSachChiTietHD() {
        return danhSachChiTietHD;
    }

    public Khuyenmai getKhuyenMai() {
        return khuyenMai;
    }

    // Setter
    public void setMaHD(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma hoa don khong hop le!");
        }
        this.maHD = maHD.trim();
    }

    public void setMaNV(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Ma nhan vien khong hop le!");
        }
        this.maNV = maNV.trim();
    }

    public void setThoiGianIn(LocalDateTime thoiGianIn) {
        if (thoiGianIn == null) {
            throw new IllegalArgumentException("Thoi gian in khong hop le!");
        }
        this.thoiGianIn = thoiGianIn;
    }

    // Them chi tiet hoa don
    public void themChiTietHoaDon(CTHoaDon ct) {
        if (ct == null) {
            throw new IllegalArgumentException("Chi tiet hoa don khong hop le!");
        }

        danhSachChiTietHD.add(ct);
    }

    // Xoa chi tiet hoa don
    public void xoaChiTietHoaDon(int viTri) {
        if (viTri < 0 || viTri >= danhSachChiTietHD.size()) {
            System.out.println("Vi tri khong hop le!");
            return;
        }

        danhSachChiTietHD.remove(viTri);
    }

    // Ap dung khuyen mai
    public void apDungKhuyenMai(Khuyenmai khuyenMai) {
        this.khuyenMai = khuyenMai;

        if (khuyenMai != null) {
            lichSuMaGiam.add(
                "Da ap dung: " + khuyenMai.getClass().getSimpleName()
            );
        }
    }

    // Huy khuyen mai
    public void huyKhuyenMai() {
        this.khuyenMai = null;
    }

    // Tinh tong tien hang truoc khuyen mai
    public double tinhTongTienHang() {
        double tong = 0;

        for (CTHoaDon ct : danhSachChiTietHD) {
            tong += ct.tinhTienHang();
        }

        return tong;
    }

    // Tinh tien giam
    public double tinhTienGiam() {
        if (khuyenMai == null) {
            return 0;
        }

        return khuyenMai.tinhTienGiamHopLe(tinhTongTienHang());
    }

    // Tinh tong tien phai thanh toan
    public double tinhTongTienPhaiThanhToan() {
        tongTien = Math.max(
            0,
            tinhTongTienHang() - tinhTienGiam()
        );

        return tongTien;
    }

    // Hien thi hoa don
    public void inhoadon() {
        System.out.println("\n========== HOA DON ==========");
        System.out.println("Ma hoa don: " + maHD);
        System.out.println("Ma nhan vien: " + maNV);
        System.out.println("Thoi gian in: " + thoiGianIn);

        System.out.println("\n--- CHI TIET HOA DON ---");

        for (CTHoaDon ct : danhSachChiTietHD) {
            System.out.println(ct);
        }

        System.out.println("Tong tien hang: " + tinhTongTienHang());
        System.out.println("Tien giam: " + tinhTienGiam());
        System.out.println("Tong thanh toan: "
                + tinhTongTienPhaiThanhToan());

        System.out.println("Trang thai: " + trangThai);
        System.out.println("=============================");
    }

    // Tinh tong tien hoa don
    public double Tongtienhoadon() {
        return tinhTongTienPhaiThanhToan();
    }

    // Cap nhat trang thai thanh toan
    public void capNhatTrangThaiThanhToan(boolean daThanhToan) {
        if (daThanhToan) {
            trangThai = "Da thanh toan";
            tinhTongTienPhaiThanhToan();
        } else {
            trangThai = "Chua thanh toan";
        }
    }

    // Lich su khuyen mai
    public List<String> getLichSuMaGiam() {
        return lichSuMaGiam;
    }

    // Hien thi hoa don theo ten phuong thuc Main cu
    public void hienThiHoaDon() {
        inhoadon();
    }
}
