package person;

import java.time.LocalDate;
import java.util.Scanner;
import util.Ktradulieu;

public class NhanVien extends Nguoidung {
    private double luongCoBan;
    private LocalDate ngayVaoLam;
    private String yeuCauSaThai; // Quản lý gửi đề xuất lý do sa thải, Giám đốc duyệt sa thải

    // Constructor rỗng
    public NhanVien() {
        super();
        this.vaiTro = "NhanVien";
        this.luongCoBan = 4000000.0; // Lương khởi điểm cố định 4 triệu
        this.ngayVaoLam = LocalDate.now();
        this.yeuCauSaThai = null;
    }

    // Constructor đầy đủ tham số
    public NhanVien(String maND, String tenND, String taiKhoan, String matKhau, String soDienThoai,
                    LocalDate ngayVaoLam, boolean daXoa) {
        super(maND, tenND, taiKhoan, matKhau, soDienThoai, "NhanVien", daXoa);
        this.luongCoBan = 4000000.0;
        setNgayVaoLam((ngayVaoLam != null) ? ngayVaoLam : LocalDate.now());
        this.yeuCauSaThai = null;
    }

    public void nhapThongTin(Scanner sc, DSNhanVien ds) {
        super.nhapThongTin(sc, ds);

        System.out.println("--- THÔNG TIN LÀM VIỆC CỦA NHÂN VIÊN ---");
        this.vaiTro = "NhanVien";
        this.luongCoBan = 4000000.0; // Mặc định 4 triệu
        System.out.println("• Lương khởi điểm: " + Ktradulieu.dinhDangTien(this.luongCoBan));

        while (true) {
            try {
                LocalDate ngay = Ktradulieu.nhapNgay(sc, "Nhập ngày vào làm (dd/MM/yyyy): ");
                setNgayVaoLam(ngay);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage());
            }
        }

        this.yeuCauSaThai = null;
        System.out.println("Đã tạo thành công tài khoản Nhân viên đa nhiệm: [" + this.maND + "] " + this.tenND);
    }


    public double getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(double luongCoBan) {
        if (luongCoBan < 0) {
            throw new IllegalArgumentException("[Lỗi] Lương cơ bản không được âm! Nhận: " + luongCoBan);
        }
        this.luongCoBan = luongCoBan;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        if (ngayVaoLam == null) {
            throw new IllegalArgumentException("[Lỗi] Ngày vào làm không được để trống!");
        }
        if (ngayVaoLam.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("[Lỗi] Ngày vào làm không thể ở tương lai!");
        }
        this.ngayVaoLam = ngayVaoLam;
    }

    public String getYeuCauSaThai() {
        return yeuCauSaThai;
    }

    public void setYeuCauSaThai(String yeuCauSaThai) {
        this.yeuCauSaThai = yeuCauSaThai;
    }


    @Override
    public String[] getQuyenHan() {
        return new String[]{"QUYEN_NHAP_KHO", "QUYEN_BAN_HANG", "QUYEN_KIEM_KE_KHO", "QUYEN_TRA_CUU"};
    }

    @Override
    public String thucHienCongViec() {
        return "Nhân viên [" + tenND + "] phụ trách đa nhiệm: Quản lý kho, tạo đơn nhập và thanh toán thu ngân.";
    }



    public String toFileString() {
        String ngayStr = (this.ngayVaoLam != null) ? this.ngayVaoLam.toString() : LocalDate.now().toString();
        String saThaiStr = (this.yeuCauSaThai != null && !this.yeuCauSaThai.trim().isEmpty()) ? this.yeuCauSaThai : "KHONG";
        return String.format("NHANVIEN;%s;%s;%s;%s;%s;%s;%s;%.0f;%s;%b",
                this.maND, this.tenND, this.taiKhoan, this.matKhau, this.soDienThoai,
                this.vaiTro, ngayStr, this.luongCoBan, saThaiStr, this.daXoa);
    }

    @Override
    public String toString() {
        return super.toString() +
                " | Ngày vào làm: " + ngayVaoLam +
                " | Lương khởi điểm: " + Ktradulieu.dinhDangTien(luongCoBan) +
                (yeuCauSaThai != null ? " [ĐỀ XUẤT SA THẢI: " + yeuCauSaThai + "]" : "");
    }
}