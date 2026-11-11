package person;

import java.time.LocalDate;
import java.util.Scanner;

import hoadon.DSHoadon;
import hoadon.HoaDon;
import hoadon.Hoadonban;
import hoadon.HoadonNhap;
import sanpham.DSSanPham;
import sanpham.SanPham;
import util.Ktradulieu;

public class Giamdoc extends Nguoidung {

    private double phuCapChucVu;
    private double tyLeCoPhan;

    public Giamdoc() {
        super();
        this.vaiTro = "Giám đốc";
        this.phuCapChucVu = 15000000.0;
        this.tyLeCoPhan = 35.0;
    }

    public Giamdoc(String maND, String tenND, String taiKhoan, String matKhau, String soDienThoai,
                   String vaiTro, boolean daXoa,
                   double phuCapChucVu, double tyLeCoPhan) {
        super(maND, tenND, taiKhoan, matKhau, soDienThoai, vaiTro, daXoa);
       this.phuCapChucVu = phuCapChucVu;
       this.tyLeCoPhan = tyLeCoPhan;
    }

    public double getPhuCapChucVu() {
        return phuCapChucVu;
    }

    public void setPhuCapChucVu(double phuCapChucVu) {
        if (phuCapChucVu < 0) {
            throw new IllegalArgumentException("[Lỗi] Phụ cấp chức vụ giám đốc không được âm!");
        }
        this.phuCapChucVu = phuCapChucVu;
    }

    public double getTyLeCoPhan() {
        return tyLeCoPhan;
    }

    public void setTyLeCoPhan(double tyLeCoPhan) {
        if (tyLeCoPhan < 0 || tyLeCoPhan > 100) {
            throw new IllegalArgumentException("[Lỗi] Tỷ lệ cổ phần phải nằm trong khoảng từ 0% đến 100%!");
        }
        this.tyLeCoPhan = tyLeCoPhan;
    }


    @Override
    public String[] getQuyenHan() {
        return new String[] {
                "TOAN_QUYEN_GIAM_DOC",
                "SA_THAI_NHAN_VIEN",
                "BO_NHIEM_QUAN_LY",
                "DUYET_HOA_DON",
                "DUYET_SAN_PHAM",
                "XEM_TOAN_BO_BAO_CAO"
        };
    }

    @Override
    public String thucHienCongViec() {
        return "Giám đốc [" + this.tenND + "] đang điều hành chiến lược kinh doanh toàn diện của chuỗi cửa hàng.";
    }

    @Override
    public void nhapThongTin(Scanner sc, DSNhanVien ds) {
        // Nhập các trường cơ bản từ lớp cha Nguoidung (tiền tố tự động nhận diện là GD)
        super.nhapThongTin(sc, ds);

        System.out.println("--- Nhập thông tin bổ sung cho Giám đốc ---");
        this.phuCapChucVu = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập phụ cấp chức vụ (VND, mặc định 15.000.000): ");
        if (this.phuCapChucVu <= 0) {
            this.phuCapChucVu = 15000000.0;
        }

        while (true) {
            this.tyLeCoPhan = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập tỷ lệ cổ phần sở hữu (0 - 100%): ");
            if (this.tyLeCoPhan >= 0 && this.tyLeCoPhan <= 100) {
                break;
            }
            System.out.println("[Lỗi] Tỷ lệ cổ phần phải nằm trong khoảng 0% đến 100%!");
        }
    }

    public boolean saThaiNhanVien(DSNhanVien dsNV, String maNV) {
        if (dsNV == null || maNV == null || maNV.trim().isEmpty()) {
            System.out.println("[Lỗi] Danh sách hoặc mã nhân viên không hợp lệ!");
            return false;
        }
        Scanner sc = new Scanner(System.in);
        boolean thanhCong = dsNV.xoa(sc, this, maNV.trim());

        if (thanhCong) {
            System.out.println("[Thành công] Giám đốc [" + this.tenND + "] đã phê duyệt quyết định sa thải nhân viên [" + maNV + "].");
        } else {
            System.out.println("[Lỗi] Không thể hoàn tất sa thải nhân viên mã " + maNV + ".");
        }
        return thanhCong;
    }


    public void duyetSanPhamMoi(DSSanPham dsSP, SanPham spMoi) {
        if (dsSP == null || spMoi == null) {
            throw new IllegalArgumentException("[Lỗi] Dữ liệu sản phẩm duyệt không được để trống!");
        }
        spMoi.setTrangThaiDuyet("Đã duyệt");
        dsSP.themSP(spMoi);
        System.out.println("[Thành công] Giám đốc [" + this.tenND + "] đã chính thức phê duyệt sản phẩm mới: " + spMoi.getTenSP() + " [" + spMoi.getMaSP() + "].");
    }

    //  Phê duyệt hóa đơn
    public boolean duyetHoaDon(Scanner sc, DSHoadon dsHD, DSSanPham dsSP) {
        if (dsHD == null) {
            System.out.println("[Lỗi] Danh sách hóa đơn không hợp lệ!");
            return false;
        }
        return dsHD.duyetHoaDon(sc, this, dsSP);
    }


    public void thongKeDoanhThuLoiNhuan(DSHoadon dsHD) {
        double tongDoanhThu = 0;
        double tongChiPhiNhap = 0;
        int donBanThanhCong = 0;
        int donNhapThanhCong = 0;


        double loiNhuan = tongDoanhThu - tongChiPhiNhap;

        System.out.println("\n---------------- BÁO CÁO DOANH THU & LỢI NHUẬN (GIÁM ĐỐC) ----------------");
        System.out.printf("- Tổng doanh thu bán hàng (%d đơn thành công): %,.0f VND\n", donBanThanhCong, tongDoanhThu);
        System.out.printf("- Tổng chi phí nhập hàng (%d đơn thành công): %,.0f VND\n", donNhapThanhCong, tongChiPhiNhap);
        System.out.println("--------------------------------------------------------------------------");
        if (loiNhuan >= 0) {
            System.out.printf("[Kết quả] LỢI NHUẬN RÒNG: + %,.0f VND (Kinh doanh có lãi)\n", loiNhuan);
        } else {
            System.out.printf("[Cảnh báo] LỢI NHUẬN RÒNG: - %,.0f VND (Đang bù lỗ)\n", Math.abs(loiNhuan));
        }
        System.out.println("--------------------------------------------------------------------------");
    }

    public void thongKeThue(DSHoadon dsHD) {
        double thueDauRa = 0;
        double thueDauVao = 0;

        for (int i = 0; i < dsHD.getSoLuong(); i++) {
            HoaDon hd = dsHD.get(i);
            if (hd != null && hd.isDaXuLyThanhCong()) {
                if (hd instanceof Hoadonban) {
                    thueDauRa += ((Hoadonban) hd).tinhTienThue();
                } else if (hd instanceof HoadonNhap) {
                    thueDauVao += ((HoadonNhap) hd).tinhTienThue();
                }
            }
        }

        double thuePhaiNop = thueDauRa - thueDauVao;

        System.out.println("\n---------------- BÁO CÁO NGHĨA VỤ THUẾ CỬA HÀNG ----------------");
        System.out.printf("- Thuế VAT đầu ra thu từ khách hàng: %,.0f VND\n", thueDauRa);
        System.out.printf("- Thuế VAT đầu vào đã trả cho đối tác NCC: %,.0f VND\n", thueDauVao);
        System.out.println("----------------------------------------------------------------");
        if (thuePhaiNop > 0) {
            System.out.printf("[Thông báo] Thuế phải nộp vào ngân sách Nhà nước: %,.0f VND\n", thuePhaiNop);
        } else {
            System.out.printf("[Thông báo] Thuế còn được khấu trừ chuyển kỳ sau: %,.0f VND\n", Math.abs(thuePhaiNop));
        }
        System.out.println("----------------------------------------------------------------");
    }

    public void xuatThongTin() {
        System.out.printf("%-10s | %-22s | %-12s | %-15s | Phụ cấp: %,.0f VND | Cổ phần: %.1f%% | Trạng thái: %s\n",
                getMaND(), getTenND(), getSoDienThoai(), getVaiTro(), phuCapChucVu, tyLeCoPhan,
                (daXoa ? "ĐÃ NGHỈ VIỆC" : "HOẠT ĐỘNG"));
    }

    public String toFileString() {
        return String.format("%s;%s;%s;%s;%s;%s;%b;%.0f;%.2f",
                getMaND(), getTenND(), getTaiKhoan(), getMatKhau(), getSoDienThoai(), getVaiTro(), isDaXoa(),
                phuCapChucVu, tyLeCoPhan);
    }
}