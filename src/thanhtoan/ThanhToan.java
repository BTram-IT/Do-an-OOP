package thanhtoan;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import person.NhanVien;
import util.Ktradulieu;

public abstract class ThanhToan {

    public static final String CHO_THANH_TOAN = "Cho thanh toan";
    public static final String THANH_CONG = "Thanh cong";
    public static final String THAT_BAI = "That bai";
    private static final DateTimeFormatter DINH_DANG_NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    protected String maGD;
    protected String loaiThanhToan;
    protected double soTien;        // Lấy tự động từ tổng tiền hóa đơn
    protected String maNV;          // Lấy tự động từ nhân viên đang đăng nhập
    protected String trangThai;
    protected LocalDateTime ngayThanhToan;


    public ThanhToan() {
        this.maGD = "";
        this.loaiThanhToan = "";
        this.soTien = 0.0;
        this.maNV = "";
        this.trangThai = CHO_THANH_TOAN;
        this.ngayThanhToan = null;
    }


    public ThanhToan(String maHD, String loaiThanhToan, double soTien, NhanVien nvDangDangNhap) {
        setMaGD("TT_" + ((maHD != null && !maHD.isEmpty()) ? maHD : "GD001"));
        setLoaiThanhToan(loaiThanhToan);
        setSoTien(soTien);

        if (nvDangDangNhap != null) {
            setMaNV(nvDangDangNhap.getMaND());
        } else {
            this.maNV = "NV_MACDINH";
        }
        this.trangThai = CHO_THANH_TOAN;
        this.ngayThanhToan = null;
    }

    public abstract void nhap();

    public abstract void nhap(Scanner sc);

    public abstract boolean qTrinhThanhToan();

    protected boolean ketThuc(boolean thanhCong) {
        this.trangThai = thanhCong ? THANH_CONG : THAT_BAI;
        if (thanhCong) {
            this.ngayThanhToan = LocalDateTime.now();
        }
        return thanhCong;
    }

    public boolean isThanhCong() {
        return THANH_CONG.equalsIgnoreCase(this.trangThai);
    }


    public String getMaGD() {
        return maGD;
    }

    public void setMaGD(String maGD) {
        if (maGD == null || maGD.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã giao dịch thanh toán không được để trống!");
        }
        this.maGD = Ktradulieu.chuanHoaMa(maGD);
    }

    public String getLoaiThanhToan() {
        return loaiThanhToan;
    }

    public void setLoaiThanhToan(String loaiThanhToan) {
        if (loaiThanhToan == null || loaiThanhToan.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Loại thanh toán không được để trống!");
        }
        this.loaiThanhToan = loaiThanhToan.trim();
    }

    public double getSoTien() {
        return soTien;
    }

    public void setSoTien(double soTien) {
        if (soTien < 0) {
            throw new IllegalArgumentException("[Lỗi] Số tiền thanh toán không được âm! Nhận: " + soTien);
        }
        this.soTien = soTien;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã nhân viên thu ngân không được để trống!");
        }
        this.maNV = Ktradulieu.chuanHoaMa(maNV);
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    @Override
    public String toString() {
        String ngay = (ngayThanhToan == null) ? "Chưa thanh toán" : ngayThanhToan.format(DINH_DANG_NGAY_GIO);
        return String.format("[%s] Mã GD: %s | Số tiền: %s | Trạng thái: %s | Thu ngân: %s | Lúc: %s",
                loaiThanhToan, maGD, Ktradulieu.dinhDangTien(soTien), trangThai, maNV, ngay);
    }
}