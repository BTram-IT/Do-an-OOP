package person;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

import hoadon.DSHoadon;
import hoadon.HoaDon;
import hoadon.Hoadonban;
import hoadon.HoadonNhap;
import sanpham.DSSanPham;
import sanpham.SanPham;
import util.Ktradulieu;

public class QuanLyCuaHang extends Nguoidung {

    private String khuVucQuanLy;
    private double heSoTrachNhiem;

    public QuanLyCuaHang() {
        super();
        this.vaiTro = "Quản lý";
        this.khuVucQuanLy = "Toàn bộ cửa hàng";
        this.heSoTrachNhiem = 1.5;
    }

    public QuanLyCuaHang(String maND, String tenND, String taiKhoan, String matKhau, String soDienThoai,
                         String vaiTro, boolean daXoa,
                         String khuVucQuanLy, double heSoTrachNhiem) {
        super(maND, tenND, taiKhoan, matKhau, soDienThoai, vaiTro, daXoa);
        this.heSoTrachNhiem=heSoTrachNhiem;
        this.khuVucQuanLy = khuVucQuanLy;
    }

    public String getKhuVucQuanLy() {
        return khuVucQuanLy;
    }

    public void setKhuVucQuanLy(String khuVucQuanLy) {
        if (khuVucQuanLy == null || khuVucQuanLy.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Khu vực quản lý không được để trống!");
        }
        this.khuVucQuanLy = khuVucQuanLy.trim();
    }

    public double getHeSoTrachNhiem() {
        return heSoTrachNhiem;
    }

    public void setHeSoTrachNhiem(double heSoTrachNhiem) {
        if (heSoTrachNhiem <= 0) {
            throw new IllegalArgumentException("[Lỗi] Hệ số trách nhiệm quản lý phải lớn hơn 0!");
        }
        this.heSoTrachNhiem = heSoTrachNhiem;
    }

    @Override
    public String[] getQuyenHan() {
        return new String[] {
                "QUAN_LY_KHO",
                "DUYET_HOA_DON",
                "DUYET_SAN_PHAM",
                "TINH_LUONG_NHAN_VIEN",
                "XEM_THONG_KE_DOANH_THU"
        };
    }

    @Override
    public String thucHienCongViec() {
        return "Quản lý cửa hàng [" + this.tenND + "] đang kiểm tra doanh thu ca trực, duyệt đơn và kiểm soát hàng tồn kho khu vực: " + this.khuVucQuanLy + ".";
    }

    @Override
    public void nhapThongTin(Scanner sc, DSNhanVien ds) {
        super.nhapThongTin(sc, ds);

        System.out.println("--- Nhập thông tin bổ sung cho Quản lý ---");
        this.khuVucQuanLy = Ktradulieu.nhapChuoi(sc, "Nhập khu vực quản lý (VD: Tầng 1, Quầy thực phẩm, Toàn cửa hàng): ",
                ".+", "Khu vực không được để trống!");
        this.heSoTrachNhiem = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập hệ số trách nhiệm (Mặc định 1.5): ");
        if (this.heSoTrachNhiem <= 0) {
            this.heSoTrachNhiem = 1.5;
        }
    }


    public double tinhLuongNhanVien(NhanVien nv) {
        if (nv == null) {
            throw new IllegalArgumentException("[Lỗi] Nhân viên không được để trống khi tính lương!");
        }
        double luongCoBan = 4000000.0;
        long soThangLamViec = 0;
        if (nv.getNgayVaoLam() != null) {
            soThangLamViec = ChronoUnit.MONTHS.between(nv.getNgayVaoLam(), LocalDate.now());
        }
        if (soThangLamViec < 0) {
            soThangLamViec = 0;
        }

        long soLanTangLuong = soThangLamViec / 6;
        double tienThuongThamNien = soLanTangLuong * 500000.0;
        double tongLuong = luongCoBan + tienThuongThamNien;

        System.out.println("\n--------------------------------------------------");
        System.out.println("[BẢNG TÍNH LƯƠNG NHÂN VIÊN] " + nv.getTenND() + " [" + nv.getMaND() + "]");
        System.out.println("- Ngày vào làm: " + nv.getNgayVaoLam() + " (" + soThangLamViec + " tháng cống hiến)");
        System.out.println("- Lương khởi điểm: 4,000,000 VND");
        System.out.println("- Số lần tăng thâm niên (mỗi 6 tháng): " + soLanTangLuong + " lần (+ " + String.format("%,.0f", tienThuongThamNien) + " VND)");
        System.out.println("[Tổng lĩnh] " + String.format("%,.0f VND", tongLuong));
        System.out.println("--------------------------------------------------");

        return tongLuong;
    }

    // sa thải nhân viên
    public boolean saThaiNhanVien(DSNhanVien dsNV, String maNV) {
        System.out.println("[Lỗi] BẠN KHÔNG CÓ QUYỀN! Quản lý không có quyền sa thải nhân viên. Quyền hạn thuộc về GIÁM ĐỐC.");
        return false;
    }

    // phê duyệt sp mới
    public void duyetSanPhamMoi(DSSanPham dsSP, SanPham spMoi) {
        if (dsSP == null || spMoi == null) {
            throw new IllegalArgumentException("[Lỗi] Dữ liệu sản phẩm duyệt không được null!");
        }
        spMoi.setTrangThaiDuyet("Đã duyệt");
        dsSP.themSP(spMoi);
        System.out.println("[Thành công] Quản lý [" + this.tenND + "] đã xem xét và duyệt thêm sản phẩm mới vào danh mục: " + spMoi.getTenSP() + " [" + spMoi.getMaSP() + "].");
    }

    public boolean duyetHoaDon(Scanner sc, DSHoadon dsHD, DSSanPham dsSP) {
        if (dsHD == null) {
            System.out.println("[Lỗi] Danh sách hóa đơn không hợp lệ!");
            return false;
        }
        return dsHD.duyetHoaDon(sc, this, dsSP);
    }

   // doanh thu và tồn kho
    public void thongKeDoanhThuLoiNhuan(DSHoadon dsHD) {
        double tongDoanhThuBan = 0;
        double tongChiPhiNhap = 0;
        int donBanThanhCong = 0;
        int donNhapThanhCong = 0;

        for (int i = 0; i < dsHD.getSoLuong(); i++) {
            HoaDon hd = dsHD.get(i);
            if (hd != null && hd.isDaXuLyThanhCong()) {
                if (hd instanceof Hoadonban) {
                    tongDoanhThuBan += ((Hoadonban) hd).tinhTongTien();
                    donBanThanhCong++;
                } else if (hd instanceof HoadonNhap) {
                    tongChiPhiNhap += hd.tinhTongTien();
                    donNhapThanhCong++;
                }
            }
        }

        double loiNhuanGop = tongDoanhThuBan - tongChiPhiNhap;

        System.out.println("\n---------------- BÁO CÁO DOANH THU & CHI PHÍ CỬA HÀNG ----------------");
        System.out.printf("- Tổng doanh thu bán lẻ (%d đơn thành công): %,.0f VND\n", donBanThanhCong, tongDoanhThuBan);
        System.out.printf("- Tổng chi phí nhập hàng (%d đơn thành công): %,.0f VND\n", donNhapThanhCong, tongChiPhiNhap);
        System.out.println("----------------------------------------------------------------------");
        if (loiNhuanGop >= 0) {
            System.out.printf("[Kết quả] LỢI NHUẬN GỘP: + %,.0f VND (Kinh doanh có lãi)\n", loiNhuanGop);
        } else {
            System.out.printf("[Cảnh báo] LỢI NHUẬN GỘP: - %,.0f VND (Đang bù lỗ)\n", Math.abs(loiNhuanGop));
        }
        System.out.println("----------------------------------------------------------------------");
    }

    public void thongKeTonKho(DSSanPham dsSP) {
        System.out.println("\n---------------- BÁO CÁO TỒN KHO & CẢNH BÁO HÀNG HÓA ----------------");
        int sapHet = 0;
        int anToan = 0;

        for (int i = 0; i < dsSP.getSoLuong(); i++) {
            SanPham sp = dsSP.get(i);
            if (sp != null) {
                if (sp.getSoLuongTon() <= 10) {
                    System.out.printf("[Cảnh báo sắp hết] %-8s | %-25s | Tồn kho: %d %s\n",
                            sp.getMaSP(), sp.getTenSP(), sp.getSoLuongTon(), sp.getDonViTinh());
                    sapHet++;
                } else {
                    anToan++;
                }
            }
        }
        System.out.println("---------------------------------------------------------------------");
        System.out.println("- Tổng số mặt hàng quản lý: " + dsSP.getSoLuong());
        System.out.println("- Số mặt hàng an toàn: " + anToan);
        System.out.println("- Số mặt hàng cần nhập bổ sung ngay (<= 10): " + sapHet);
    }

    public void xuatThongTin() {
        System.out.printf("%-10s | %-22s | %-12s | %-15s | Khu vực: %s | Hệ số: %.2f | Trạng thái: %s\n",
                getMaND(), getTenND(), getSoDienThoai(), getVaiTro(), khuVucQuanLy, heSoTrachNhiem,
                (daXoa ? "ĐÃ NGHỈ VIỆC" : "HOẠT ĐỘNG"));
    }

    public String toFileString() {
        return String.format("%s;%s;%s;%s;%s;%s;%b;%s;%.2f",
                getMaND(), getTenND(), getTaiKhoan(), getMatKhau(), getSoDienThoai(), getVaiTro(), isDaXoa(),
                khuVucQuanLy, heSoTrachNhiem);
    }
}