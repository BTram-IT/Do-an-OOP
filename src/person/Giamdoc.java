package person;


import java.util.Locale;
import java.time.LocalDate;
import java.util.List;
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
        setPhuCapChucVu(phuCapChucVu);
        setTyLeCoPhan(tyLeCoPhan);
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

    public void duyetYeuCauSaThai(Scanner sc, DSNhanVien dsNV) {
        if (this.isDaXoa()) {
            System.out.println("[Lỗi] Tài khoản giám đốc đã bị vô hiệu hóa!");
            return;
        }

        if (sc == null || dsNV == null) {
            System.out.println("[Lỗi] Dữ liệu không hợp lệ!");
            return;
        }

        // Lấy danh sách nhân viên đang có yêu cầu sa thải
        List<NhanVien> danhSach = dsNV.getDanhSachYeuCauSaThai();

        System.out.println("\n========== DANH SÁCH YÊU CẦU SA THẢI ==========");

        if (danhSach.isEmpty()) {
            System.out.println("[Thông báo] Hiện không có yêu cầu sa thải nào.");
            return;
        }

        for (int i = 0; i < danhSach.size(); i++) {
            NhanVien nv = danhSach.get(i);

            System.out.println("--------------------------------------------");
            System.out.println("STT: " + (i + 1));
            System.out.println("Mã nhân viên: " + nv.getMaND());
            System.out.println("Họ tên: " + nv.getTenND());
            System.out.println("Số điện thoại: " + nv.getSoDienThoai());
            System.out.println("Lý do đề xuất: " + nv.getYeuCauSaThai());
        }

        System.out.println("--------------------------------------------");

        // Chọn nhân viên cần xử lý
        String maNV = Ktradulieu.nhapChuoiKhongRong(
                sc, "Nhập mã nhân viên cần xử lý yêu cầu: ");

        NhanVien nv = dsNV.timNhanVienTheoMa(maNV);

        if (nv == null || nv.getYeuCauSaThai() == null
                || nv.getYeuCauSaThai().trim().isEmpty()) {
            System.out.println("[Lỗi] Nhân viên không tồn tại hoặc không có yêu cầu sa thải!");
            return;
        }

        System.out.println("\n========== XỬ LÝ YÊU CẦU SA THẢI ==========");
        System.out.println("Mã nhân viên: " + nv.getMaND());
        System.out.println("Họ tên: " + nv.getTenND());
        System.out.println("Lý do: " + nv.getYeuCauSaThai());

        System.out.println("1. Chấp nhận sa thải");
        System.out.println("2. Từ chối sa thải");
        System.out.println("0. Quay lại");

        int chon = Ktradulieu.luaChon(sc, "Chọn quyết định (0-2): ", 0, 2);

        switch (chon) {
            case 1:
                // DSNhanVien.xoa() sẽ kiểm tra quyền giám đốc,
                // đánh dấu nhân viên đã nghỉ và xóa yêu cầu.
                dsNV.xoa(sc, this, maNV);
                break;

            case 2:
                // Từ chối yêu cầu, nhân viên vẫn hoạt động.
                nv.setYeuCauSaThai(null);
                System.out.println("[Thành công] Đã từ chối yêu cầu sa thải!");
                break;

            case 0:
                System.out.println("[Thông báo] Đã hủy thao tác.");
                return;
        }
    }
    public void duyetSanPhamMoi(DSSanPham dsSP, SanPham spMoi) {
        if (this.isDaXoa()) {
            throw new IllegalStateException("[TỪ CHỐI TRUY CẬP] Tài khoản giám đốc đã bị vô hiệu hóa!");
        }
        if (dsSP == null || spMoi == null) {
            throw new IllegalArgumentException("[Lỗi] Dữ liệu sản phẩm duyệt không được để trống!");
        }
        spMoi.setTrangThaiDuyet("Đã duyệt");
        dsSP.themSP(spMoi);
        System.out.println("[Thành công] Giám đốc [" + this.tenND + "] đã chính thức phê duyệt sản phẩm mới: " + spMoi.getTenSP() + " [" + spMoi.getMaSP() + "].");
    }

    // Phê duyệt hóa đơn
    public boolean duyetHoaDon(Scanner sc, DSHoadon dsHD, DSSanPham dsSP) {
        if (this.isDaXoa()) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Tài khoản giám đốc đã bị vô hiệu hóa!");
            return false;
        }
        if (dsHD == null) {
            System.out.println("[Lỗi] Danh sách hóa đơn không hợp lệ!");
            return false;
        }
        return dsHD.duyetHoaDon(sc, this, dsSP);
    }

    // Đã sửa: duyệt danh sách hóa đơn để tính toán doanh thu và chi phí thực tế
    public void thongKeDoanhThuLoiNhuan(DSHoadon dsHD) {
        if (dsHD == null) {
            System.out.println("[Lỗi] Danh sách hóa đơn không hợp lệ!");
            return;
        }

        double tongDoanhThu = 0;
        double tongChiPhiNhap = 0;
        int donBanThanhCong = 0;
        int donNhapThanhCong = 0;

        for (int i = 0; i < dsHD.getSoLuong(); i++) {
            HoaDon hd = dsHD.get(i);
            if (hd != null && hd.isDaXuLyThanhCong()) {
                if (hd instanceof Hoadonban) {
                    tongDoanhThu += ((Hoadonban) hd).tinhTongTien();
                    donBanThanhCong++;
                } else if (hd instanceof HoadonNhap) {
                    tongChiPhiNhap += hd.tinhTongTien();
                    donNhapThanhCong++;
                }
            }
        }

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

    // 1. Báo cáo tổng quan hóa đơn
    public void thongKeTongQuanHoaDon(
            QuanLyCuaHang baoCao, DSHoadon dsHD,
            LocalDate tuNgay, LocalDate denNgay) {

        if (this.isDaXoa() || baoCao == null) {
            System.out.println("[Lỗi] Không thể thực hiện báo cáo!");
            return;
        }

        baoCao.thongKeTongQuanHoaDon(dsHD, tuNgay, denNgay);
    }

    // 2. Báo cáo doanh thu và chi phí nhập hàng
    public void thongKeDoanhThuLoiNhuan(
            QuanLyCuaHang baoCao, DSHoadon dsHD,
            LocalDate tuNgay, LocalDate denNgay) {

        if (this.isDaXoa() || baoCao == null) {
            System.out.println("[Lỗi] Không thể thực hiện báo cáo!");
            return;
        }

        baoCao.thongKeDoanhThuLoiNhuan(dsHD, tuNgay, denNgay);
    }

    // 3. Báo cáo thuế
    public void thongKeThue(
            QuanLyCuaHang baoCao, DSHoadon dsHD,
            LocalDate tuNgay, LocalDate denNgay) {

        if (this.isDaXoa() || baoCao == null) {
            System.out.println("[Lỗi] Không thể thực hiện báo cáo!");
            return;
        }

        baoCao.thongKeThue(dsHD, tuNgay, denNgay);
    }

    // 4. Báo cáo sản phẩm bán được và không bán được
    public void thongKeSanPhamBanDuoc(
            QuanLyCuaHang baoCao, DSHoadon dsHD, DSSanPham dsSP,
            LocalDate tuNgay, LocalDate denNgay) {

        if (this.isDaXoa() || baoCao == null) {
            System.out.println("[Lỗi] Không thể thực hiện báo cáo!");
            return;
        }

        baoCao.thongKeSanPhamBanDuoc(dsHD, dsSP, tuNgay, denNgay);
    }

    // 5. Báo cáo nhân viên và bảng lương
    public void thongKeNhanVienVaBangLuong(
            QuanLyCuaHang baoCao, DSNhanVien dsNV) {

        if (this.isDaXoa() || baoCao == null) {
            System.out.println("[Lỗi] Không thể thực hiện báo cáo!");
            return;
        }

        baoCao.thongKeNhanVienVaBangLuong(dsNV);
    }

    // 6. Báo cáo tồn kho
    public void thongKeTonKho(
            QuanLyCuaHang baoCao, DSSanPham dsSP) {

        if (this.isDaXoa() || baoCao == null) {
            System.out.println("[Lỗi] Không thể thực hiện báo cáo!");
            return;
        }

        baoCao.thongKeTonKho(dsSP);
    }

    public void thongKeThue(DSHoadon dsHD) {
        if (dsHD == null) {
            System.out.println("[Lỗi] Danh sách hóa đơn không hợp lệ!");
            return;
        }
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
        return String.format(Locale.ROOT, "GIAMDOC;%s;%s;%s;%s;%s;%s;%b;%.0f;%.2f",
                getMaND(), getTenND(), getTaiKhoan(), getMatKhau(), getSoDienThoai(), getVaiTro(), isDaXoa(),
                phuCapChucVu, tyLeCoPhan);
    }
}