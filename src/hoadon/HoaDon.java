package hoadon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import person.NhanVien;
import util.Ktradulieu;

public abstract class HoaDon implements IHoaDon {
    protected String maHD;
    protected String ngayLap;
    protected NhanVien nguoiLap; // Tự động lấy từ nhân viên đang đăng nhập hệ thống
    protected String trangThai;  // "DangXuLy" hoặc "DaXuLyThanhCong"
    protected String ghiChu;


    public HoaDon() {
        this.maHD = "";
        this.ngayLap = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        this.trangThai = "DangXuLy";
        this.ghiChu = "";
    }


    public HoaDon(String maHD, NhanVien nguoiLap, String ngayLap, String trangThai, String ghiChu) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.nguoiLap = nguoiLap;
    }


    public void nhapThongTin(Scanner sc, NhanVien nhanVienDangNhap, DSHoadon dsHD) {
        System.out.println("\n--- TẠO HÓA ĐƠN MỚI ---");
        String tienTo = (this instanceof Hoadonban) ? "HDB" : ((this instanceof HoadonNhap) ? "HDN" : "HD");
        String maGoiY = (dsHD != null) ? dsHD.sinhMaTuDong(tienTo) : (tienTo + "001");

        while (true) {
            System.out.print("Nhập mã hóa đơn (Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                this.setMaHD(maGoiY);
                System.out.println("-> Mã tự tạo: " + this.maHD);
                break;
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);
                if (!maChuan.startsWith(tienTo)) {
                    System.out.println("Lỗi: Mã hóa đơn phải bắt đầu bằng '" + tienTo + "' (Ví dụ: " + tienTo + "001)! Hoặc nhấn / để tự tạo mã.");
                    continue;
                }
                if (dsHD != null && dsHD.timKiem(maChuan) != null) {
                    System.out.println("Lỗi: Mã '" + maChuan + "' đã tồn tại! Vui lòng nhập lại hoặc nhấn / để tự tạo mã.");
                    continue;
                }
                this.setMaHD(maChuan);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // TỰ ĐỘNG GẮN NHÂN VIÊN ĐANG ĐĂNG NHẬP (KHÔNG CẦN GÕ LẠI MÃ)
        this.setNguoiLap(nhanVienDangNhap);
        System.out.println("• Nhân viên lập đơn : [" + this.nguoiLap.getMaND() + "] " + this.nguoiLap.getTenND());

        LocalDate homNay = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.setNgayLap(homNay.format(dtf));
        System.out.println("• Ngày lập hóa đơn: " + this.ngayLap);
        this.trangThai = "DangXuLy";
        this.ghiChu = Ktradulieu.nhapChuoiTuyChon(sc, "Nhập ghi chú hóa đơn (Enter để bỏ qua): ", ".+", "");
    }


    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        kiemTraQuyenChinhSua(); // Khóa đơn nếu đã xử lý thành công
        if (maHD == null || maHD.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã hóa đơn không được để trống!");
        }
        this.maHD = Ktradulieu.chuanHoaMa(maHD);
    }

    public NhanVien getNguoiLap() {
        return nguoiLap;
    }

    public void setNguoiLap(NhanVien nguoiLap) {
        kiemTraQuyenChinhSua();
        if (nguoiLap == null) {
            throw new IllegalArgumentException("[Lỗi] Nhân viên lập hóa đơn không được để trống (null)!");
        }
        this.nguoiLap = nguoiLap;
    }

    public String getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(String ngayLap) {
        kiemTraQuyenChinhSua();
        if (ngayLap == null || ngayLap.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Ngày lập hóa đơn không được để trống!");
        }
        // Dùng biến regex ngayRegex có sẵn trong Ktradulieu của bạn (dd/MM/yyyy)
        if (!ngayLap.trim().matches(Ktradulieu.ngayRegex)) {
            throw new IllegalArgumentException("[Lỗi] Ngày lập phải đúng định dạng dd/MM/yyyy (VD: 25/10/2026)!");
        }
        this.ngayLap = ngayLap.trim();
    }

    @Override
    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Trạng thái không được để trống!");
        }
        this.trangThai = trangThai.trim();
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        kiemTraQuyenChinhSua();
        this.ghiChu = (ghiChu != null) ? ghiChu.trim() : "";
    }

    protected void kiemTraQuyenChinhSua() {
        if (isDaXuLyThanhCong()) {
            throw new IllegalStateException("[Khóa Hóa Đơn] Hóa đơn [" + maHD + "] đã XỬ LÝ THÀNH CÔNG, không được phép sửa xóa!");
        }
    }

    public boolean isDaXuLyThanhCong() {
        return "DaXuLyThanhCong".equalsIgnoreCase(this.trangThai);
    }

    @Override
    public void xacNhanXuLy() {
        this.trangThai = "DaXuLyThanhCong";
    }

    public String getMaNV() {
        return (this.nguoiLap != null) ? this.nguoiLap.getMaND() : "KHONG_RO";
    }

    public abstract String toFileString();

    @Override
    public abstract double tinhTongTien();

    @Override
    public abstract String inHoaDon();

    public abstract double tinhThanhTien();

    public abstract String getLoaiHD();
}