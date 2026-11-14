
package hoadon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import person.NhanVien;
import util.Ktradulieu;

public abstract class HoaDon implements IHoaDon {

    protected String maHD;
    protected String ngayLap;
    protected NhanVien nguoiLap;
    protected String trangThai;
    protected String ghiChu;

    public HoaDon() {
        this.maHD = "";
        this.ngayLap = LocalDate.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.trangThai = "DangXuLy";
        this.ghiChu = "";
        this.nguoiLap = null;
    }

    public HoaDon(String maHD, NhanVien nguoiLap,
                  String ngayLap, String trangThai, String ghiChu) {

        if (maHD == null || maHD.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống!");
        }

        if (!Ktradulieu.laNgayHopLe(ngayLap)) {
            throw new IllegalArgumentException("Ngày lập không hợp lệ: " + ngayLap);
        }

        if (trangThai == null || trangThai.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống!");
        }

        // Gán trực tiếp để khôi phục được hóa đơn đã hoàn tất.
        this.maHD = Ktradulieu.chuanHoaMa(maHD);
        this.nguoiLap = nguoiLap;
        this.ngayLap = ngayLap.trim();
        this.trangThai = trangThai.trim();
        this.ghiChu = ghiChu != null ? ghiChu.trim() : "";
    }

    public void nhapThongTin(Scanner sc, NhanVien nhanVienDangNhap,
                             DSHoadon dsHD) {

        if (sc == null) {
            throw new IllegalArgumentException("Scanner không hợp lệ!");
        }

        if (nhanVienDangNhap == null) {
            throw new IllegalArgumentException("Nhân viên đăng nhập không hợp lệ!");
        }

        System.out.println("\n--- TẠO HÓA ĐƠN MỚI ---");

        String tienTo = (this instanceof Hoadonban)
                ? "HDB"
                : (this instanceof HoadonNhap ? "HDN" : "HD");

        while (true) {
            System.out.print("Nhập mã hóa đơn (Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                String maMoi = dsHD != null
                        ? dsHD.sinhMaTuDong(tienTo)
                        : tienTo + "001";

                this.setMaHD(maMoi);
                System.out.println("-> Mã tự tạo: " + this.maHD);
                break;
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);

                if (!maChuan.startsWith(tienTo)) {
                    System.out.println("Mã hóa đơn phải bắt đầu bằng "
                            + tienTo + "!");
                    continue;
                }

                if (dsHD != null && dsHD.timKiem(maChuan) != null) {
                    System.out.println("Mã hóa đơn đã tồn tại!");
                    continue;
                }

                this.setMaHD(maChuan);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        this.setNguoiLap(nhanVienDangNhap);

        System.out.println("Nhân viên lập đơn: ["
                + this.nguoiLap.getMaND() + "] "
                + this.nguoiLap.getTenND());

        if (!Ktradulieu.laNgayHopLe(ngayLap)) {
            throw new IllegalArgumentException(
                    "Ngày lập không tồn tại hoặc sai định dạng dd/MM/yyyy!");
        }

        System.out.println("Ngày lập hóa đơn: " + this.ngayLap);

        this.setTrangThai("DangXuLy");

        this.setGhiChu(Ktradulieu.nhapChuoiTuyChon(
                sc, "Nhập ghi chú hóa đơn (Enter để bỏ qua): ",
                ".+", ""));
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        kiemTraQuyenChinhSua();

        if (maHD == null || maHD.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống!");
        }

        this.maHD = Ktradulieu.chuanHoaMa(maHD);
    }

    public NhanVien getNguoiLap() {
        return nguoiLap;
    }

    public void setNguoiLap(NhanVien nguoiLap) {
        kiemTraQuyenChinhSua();

        if (nguoiLap == null) {
            throw new IllegalArgumentException(
                    "Nhân viên lập hóa đơn không được để trống!");
        }

        this.nguoiLap = nguoiLap;
    }

    public String getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(String ngayLap) {
        kiemTraQuyenChinhSua();

        if (ngayLap == null
                || !ngayLap.trim().matches(Ktradulieu.ngayRegex)) {
            throw new IllegalArgumentException(
                    "Ngày lập phải đúng định dạng dd/MM/yyyy!");
        }

        this.ngayLap = ngayLap.trim();
    }

    // Kiểm tra hóa đơn đã bị hủy chưa
    public boolean isDaHuy() {
        return "DaHuy".equalsIgnoreCase(trangThai);
    }

    // Thực hiện hủy hóa đơn
    public void huyHoaDon() {
        if (isDaXuLyThanhCong()) {
            throw new IllegalStateException(
                    "Hóa đơn đã hoàn tất, không thể hủy!");
        }

        if (isDaHuy()) {
            throw new IllegalStateException(
                    "Hóa đơn này đã được hủy trước đó!");
        }

        setTrangThai("DaHuy");
    }
    @Override
    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Trạng thái không được để trống!");
        }

        String trangThaiMoi = trangThai.trim();

        // Hóa đơn đã hoàn tất không được đổi sang trạng thái khác.
        if (isDaXuLyThanhCong()
                && !"DaXuLyThanhCong".equalsIgnoreCase(trangThaiMoi)) {
            throw new IllegalStateException(
                    "Hóa đơn đã hoàn tất, không thể thay đổi trạng thái!");
        }

        // Hóa đơn đã hủy không được chuyển lại trạng thái khác.
        if (isDaHuy()
                && !"DaHuy".equalsIgnoreCase(trangThaiMoi)) {
            throw new IllegalStateException(
                    "Hóa đơn đã hủy, không thể thay đổi trạng thái!");
        }

        this.trangThai = trangThaiMoi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        kiemTraQuyenChinhSua();
        this.ghiChu = ghiChu != null ? ghiChu.trim() : "";
    }

    protected void kiemTraQuyenChinhSua() {
        if (isDaXuLyThanhCong()) {
            throw new IllegalStateException(
                    "Hóa đơn [" + maHD
                            + "] đã hoàn tất, không được phép chỉnh sửa!");
        }
    }

    public boolean isDaXuLyThanhCong() {
        return "DaXuLyThanhCong".equalsIgnoreCase(this.trangThai);
    }

    @Override
    public void xacNhanXuLy() {
        if (isDaXuLyThanhCong()) {
            return;
        }

        this.trangThai = "DaXuLyThanhCong";
    }

    public String getMaNV() {
        return this.nguoiLap != null
                ? this.nguoiLap.getMaND()
                : "KHONG_RO";
    }

    @Override
    public abstract double tinhTongTien();

    @Override
    public abstract String inHoaDon();

    public abstract double tinhThanhTien();

    public abstract String getLoaiHD();
}
