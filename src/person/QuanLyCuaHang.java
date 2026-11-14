package person;


import java.util.Locale;
import java.time.DateTimeException;
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
import java.time.format.DateTimeFormatter;
import hoadon.CTHDBan;
import java.util.LinkedHashMap;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class QuanLyCuaHang extends Nguoidung implements Thongke {
    private String khuVucQuanLy;
    private double heSoTrachNhiem;

    public QuanLyCuaHang() {
        super();
        this.vaiTro = "Quản lý";
        this.khuVucQuanLy = "Toàn bộ cửa hàng";
        this.heSoTrachNhiem = 1.5;
    }

    public QuanLyCuaHang(String maND, String tenND, String taiKhoan, String matKhau, String soDienThoai, String vaiTro, boolean daXoa, String khuVucQuanLy, double heSoTrachNhiem) {
        super(maND, tenND, taiKhoan, matKhau, soDienThoai, vaiTro, daXoa);
        setHeSoTrachNhiem(heSoTrachNhiem);
        setKhuVucQuanLy(khuVucQuanLy);
    }

    public String getKhuVucQuanLy() { return khuVucQuanLy; }
    public void setKhuVucQuanLy(String khuVucQuanLy) {
        if (khuVucQuanLy == null || khuVucQuanLy.trim().isEmpty()) { throw new IllegalArgumentException("[Lỗi] Khu vực quản lý không được để trống!"); }
        this.khuVucQuanLy = khuVucQuanLy.trim();
    }

    public double getHeSoTrachNhiem() { return heSoTrachNhiem; }
    public void setHeSoTrachNhiem(double heSoTrachNhiem) {
        if (heSoTrachNhiem <= 0) { throw new IllegalArgumentException("[Lỗi] Hệ số trách nhiệm quản lý phải lớn hơn 0!"); }
        this.heSoTrachNhiem = heSoTrachNhiem;
    }

    @Override
    public String[] getQuyenHan() {
        return new String[] { "QUAN_LY_KHO", "DUYET_HOA_DON", "DUYET_SAN_PHAM", "TINH_LUONG_NHAN_VIEN", "XEM_THONG_KE_DOANH_THU" };
    }

    @Override
    public String thucHienCongViec() {
        return "Quản lý cửa hàng [" + this.tenND + "] đang kiểm tra doanh thu ca trực, duyệt đơn và kiểm soát hàng tồn kho khu vực: " + this.khuVucQuanLy + ".";
    }

    @Override
    public void nhapThongTin(Scanner sc, DSNhanVien ds) {
        super.nhapThongTin(sc, ds);
        System.out.println("--- Nhập thông tin bổ sung cho Quản lý ---");
        this.khuVucQuanLy = Ktradulieu.nhapChuoi(sc, "Nhập khu vực quản lý (VD: Tầng 1, Quầy thực phẩm, Toàn cửa hàng): ", ".+", "Khu vực không được để trống!");
        this.heSoTrachNhiem = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập hệ số trách nhiệm (Mặc định 1.5): ");
        if (this.heSoTrachNhiem <= 0) { this.heSoTrachNhiem = 1.5; }
    }

    public double tinhLuongNhanVien(NhanVien nv) {
        if (nv == null) { throw new IllegalArgumentException("[Lỗi] Nhân viên không được để trống khi tính lương!"); }
        double luongCoBan = nv.getLuongCoBan();
        long soThangLamViec = (nv.getNgayVaoLam() != null) ? ChronoUnit.MONTHS.between(nv.getNgayVaoLam(), LocalDate.now()) : 0;
        if (soThangLamViec < 0) { soThangLamViec = 0; }
        long soLanTangLuong = soThangLamViec / 6;
        double tienThuongThamNien = soLanTangLuong * 500000.0;
        double tongLuong = luongCoBan + tienThuongThamNien;
        System.out.println("\n--------------------------------------------------");
        System.out.println("[BẢNG TÍNH LƯƠNG NHÂN VIÊN] " + nv.getTenND() + " [" + nv.getMaND() + "]");
        System.out.println("- Ngày vào làm: " + nv.getNgayVaoLam() + " (" + soThangLamViec + " tháng cống hiến)");
        System.out.printf("- Lương cơ bản: %,.0f VND\n", luongCoBan);
        System.out.println("- Số lần tăng thâm niên (mỗi 6 tháng): " + soLanTangLuong + " lần (+ " + String.format("%,.0f", tienThuongThamNien) + " VND)");
        System.out.println("[Tổng lĩnh] " + String.format("%,.0f VND", tongLuong));
        System.out.println("--------------------------------------------------");
        return tongLuong;
    }

    public boolean saThaiNhanVien(DSNhanVien dsNV, String maNV) {
        System.out.println("[Lỗi] BẠN KHÔNG CÓ QUYỀN! Quản lý không có quyền sa thải nhân viên. Quyền hạn thuộc về GIÁM ĐỐC.");
        return false;
    }

    public boolean duyetSanPhamMoi(DSSanPham dsSP, SanPham spMoi) {
        if (this.isDaXoa()) { throw new IllegalStateException("[TỪ CHỐI TRUY CẬP] Tài khoản quản lý đã bị vô hiệu hóa!"); }
        if (dsSP == null || spMoi == null) { throw new IllegalArgumentException("[Lỗi] Dữ liệu sản phẩm duyệt không được null!"); }

        String trangThaiCu = spMoi.getTrangThaiDuyet();
        spMoi.setTrangThaiDuyet("Đã duyệt");

        boolean themDuoc;
        try {
            themDuoc = dsSP.themSP(spMoi);
        } catch (RuntimeException e) {
            System.out.println("[Lỗi] " + e.getMessage());
            themDuoc = false;
        }

        if (!themDuoc) {
            if (trangThaiCu != null && !trangThaiCu.trim().isEmpty()) {
                spMoi.setTrangThaiDuyet(trangThaiCu);
            }
            System.out.println("[Thất bại] Không thể thêm sản phẩm [" + spMoi.getMaSP() + "] vào danh mục, sản phẩm chưa được duyệt.");
            return false;
        }

        System.out.println("[Thành công] Quản lý [" + this.tenND + "] đã duyệt thêm sản phẩm mới vào danh mục: "
                + spMoi.getTenSP() + " [" + spMoi.getMaSP() + "].");
        return true;
    }

    public boolean duyetHoaDon(Scanner sc, DSHoadon dsHD, DSSanPham dsSP) {
        if (this.isDaXoa()) { System.out.println("[TỪ CHỐI TRUY CẬP] Tài khoản quản lý đã bị vô hiệu hóa!"); return false; }
        if (dsHD == null) { System.out.println("[Lỗi] Danh sách hóa đơn không hợp lệ!"); return false; }
        return dsHD.duyetHoaDon(sc, this, dsSP);
    }

    public void thongKeDoanhThuLoiNhuan(DSHoadon dsHD, LocalDate tuNgay, LocalDate denNgay) {
        if (dsHD == null || tuNgay == null || denNgay == null) { System.out.println("[Lỗi] Dữ liệu thống kê không hợp lệ!"); return; }
        if (tuNgay.isAfter(denNgay)) { System.out.println("[Lỗi] Ngày bắt đầu không được sau ngày kết thúc!"); return; }
        tinhToanBaoCao(dsHD, tuNgay, denNgay);
    }

    @Override
    public void thongKeThue(DSHoadon dsHD, LocalDate tuNgay, LocalDate denNgay) {
        if (dsHD == null || tuNgay == null || denNgay == null
                || tuNgay.isAfter(denNgay)) {
            System.out.println("[Lỗi] Dữ liệu thống kê không hợp lệ!");
            return;
        }

        double tongThueBan = 0;
        double tongThueNhap = 0;
        int soHDBan = 0;
        int soHDNhap = 0;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < dsHD.getSoLuong(); i++) {
            HoaDon hd = dsHD.get(i);

            if (hd == null || !hd.isDaXuLyThanhCong()
                    || hd.getNgayLap() == null) {
                continue;
            }

            LocalDate ngayLap;
            try {
                ngayLap = LocalDate.parse(hd.getNgayLap(), dtf);
            } catch (DateTimeParseException e) {
                continue;
            }

            if (ngayLap.isBefore(tuNgay) || ngayLap.isAfter(denNgay)) {
                continue;
            }

            if (hd instanceof Hoadonban) {
                tongThueBan += ((Hoadonban) hd).tinhTienThue();
                soHDBan++;
            } else if (hd instanceof HoadonNhap) {
                tongThueNhap += ((HoadonNhap) hd).tinhTienThue();
                soHDNhap++;
            }
        }

        System.out.println("\n========== BÁO CÁO THỐNG KÊ THUẾ VAT ==========");
        System.out.println("Thời gian: " + tuNgay.format(dtf)
                + " - " + denNgay.format(dtf));
        System.out.printf("- Thuế VAT bán hàng (%d hóa đơn): %,.0f VND%n",
                soHDBan, tongThueBan);
        System.out.printf("- Thuế VAT nhập hàng (%d hóa đơn): %,.0f VND%n",
                soHDNhap, tongThueNhap);
        System.out.printf("- Tổng thuế VAT: %,.0f VND%n",
                tongThueBan + tongThueNhap);
        System.out.println("================================================");
    }

    @Override
    public void thongKeSanPhamBanDuoc(DSHoadon dsHD, DSSanPham dsSP, LocalDate tuNgay, LocalDate denNgay) {
        if (dsHD == null || dsSP == null || tuNgay == null || denNgay == null) {
            System.out.println("[Lỗi] Dữ liệu thống kê không hợp lệ!");
            return;
        }
        if (tuNgay.isAfter(denNgay)) {
            System.out.println("[Lỗi] Ngày bắt đầu không được sau ngày kết thúc!");
            return;
        }

        DateTimeFormatter dtfIn = DateTimeFormatter.ofPattern("dd/MM/uuuu")
                .withResolverStyle(java.time.format.ResolverStyle.STRICT);
        DateTimeFormatter dtfOut = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Map<String, Integer> soLuongBan = new HashMap<>();
        Map<String, Integer> soLuongTang = new HashMap<>();
        Map<String, Double> tienHang = new HashMap<>();
        int tongSoHoaDon = 0;

        // 1. Cộng dồn từ hóa đơn bán thành công trong kỳ
        for (int i = 0; i < dsHD.getSoLuong(); i++) {
            HoaDon hd = dsHD.get(i);
            if (!(hd instanceof Hoadonban) || !hd.isDaXuLyThanhCong() || hd.getNgayLap() == null) {
                continue;
            }

            LocalDate ngayLap;
            try {
                ngayLap = LocalDate.parse(hd.getNgayLap().trim(), dtfIn);
            } catch (DateTimeParseException e) {
                continue;
            }
            if (ngayLap.isBefore(tuNgay) || ngayLap.isAfter(denNgay)) {
                continue;
            }

            CTHDBan[] dsCT = ((Hoadonban) hd).getDsChiTiet();
            if (dsCT == null || dsCT.length == 0) {
                continue;
            }
            tongSoHoaDon++;

            for (CTHDBan ct : dsCT) {
                if (ct == null || ct.getMaSP() == null || ct.getMaSP().trim().isEmpty()) {
                    continue;
                }
                String ma = ct.getMaSP().trim().toUpperCase();
                soLuongBan.merge(ma, ct.getSoLuongBan(), Integer::sum);
                soLuongTang.merge(ma, ct.getSoLuongTang(), Integer::sum);
                tienHang.merge(ma, ct.tinhTienHang(), Double::sum);
            }
        }

        System.out.println("\n========== BÁO CÁO SẢN PHẨM BÁN ĐƯỢC VÀ KHÔNG BÁN ĐƯỢC ==========");
        System.out.println("Thời gian: " + tuNgay.format(dtfOut) + " - " + denNgay.format(dtfOut));

        // 2. Sản phẩm bán được: duyệt theo danh mục sản phẩm
        System.out.println("\n--- SẢN PHẨM BÁN ĐƯỢC ---");
        System.out.printf("%-5s | %-12s | %-25s | %-10s | %-10s%n",
                "STT", "MÃ SP", "TÊN SẢN PHẨM", "SL BÁN", "SL TẶNG");
        System.out.println("--------------------------------------------------------------------");

        int sttBan = 0, tongSLBan = 0, tongSLTang = 0;
        double tongTienHang = 0;

        for (int i = 0; i < dsSP.getSoLuong(); i++) {
            SanPham sp = dsSP.get(i);
            if (sp == null) continue;

            String ma = sp.getMaSP().trim().toUpperCase();
            int slBan = soLuongBan.getOrDefault(ma, 0);
            if (slBan <= 0) continue;

            int slTang = soLuongTang.getOrDefault(ma, 0);
            sttBan++;
            tongSLBan += slBan;
            tongSLTang += slTang;
            tongTienHang += tienHang.getOrDefault(ma, 0.0);

            System.out.printf("%-5d | %-12s | %-25s | %-10d | %-10d%n",
                    sttBan, sp.getMaSP(), sp.getTenSP(), slBan, slTang);
        }
        if (sttBan == 0) {
            System.out.println("Không có sản phẩm nào được bán trong kỳ này.");
        }

        // 3. Sản phẩm không bán được: còn trong danh mục, không bị xóa, không có giao dịch trong kỳ
        System.out.println("\n--- SẢN PHẨM KHÔNG BÁN ĐƯỢC ---");
        System.out.printf("%-5s | %-12s | %-25s | %-10s%n", "STT", "MÃ SP", "TÊN SẢN PHẨM", "TỒN KHO");
        System.out.println("--------------------------------------------------------------------");

        int sttKhongBan = 0;
        for (int i = 0; i < dsSP.getSoLuong(); i++) {
            SanPham sp = dsSP.get(i);
            if (sp == null || sp.isDaXoa()) continue;

            String ma = sp.getMaSP().trim().toUpperCase();
            if (soLuongBan.getOrDefault(ma, 0) > 0) continue;

            sttKhongBan++;
            System.out.printf("%-5d | %-12s | %-25s | %-10d%n",
                    sttKhongBan, sp.getMaSP(), sp.getTenSP(), sp.getSoLuongTon());
        }
        if (sttKhongBan == 0) {
            System.out.println("Tất cả sản phẩm đều có bán trong kỳ này.");
        }

        // 4. Tổng kết
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("- Số hóa đơn bán thành công trong kỳ: " + tongSoHoaDon);
        System.out.println("- Số loại sản phẩm bán được: " + sttBan);
        System.out.println("- Số loại sản phẩm không bán được: " + sttKhongBan);
        System.out.println("- Tổng số lượng bán: " + tongSLBan);
        System.out.println("- Tổng số lượng tặng: " + tongSLTang);
        System.out.printf("- Tổng tiền hàng trước giảm giá và thuế: %,.0f VND%n", tongTienHang);
        System.out.println("====================================================================");
    }

    public void thongKeKhachHang(DSHoadon dsHD, DsKhachHang dsKH, LocalDate tuNgay, LocalDate denNgay) {
        if (dsHD == null || dsKH == null || tuNgay == null || denNgay == null || tuNgay.isAfter(denNgay)) {
            System.out.println("[Lỗi] Dữ liệu thống kê không hợp lệ!"); return;
        }
        DateTimeFormatter dinhDangNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<String, Integer> soLanMua = new HashMap<>();
        Map<String, Double> chiTieu = new HashMap<>();
        Map<String, LocalDate> lanMuaDauTien = new HashMap<>();
        int tongKhachHang = 0;

        for (int i = 0; i < dsKH.getSoLuong(); i++) {
            Khachhang kh = dsKH.get(i);
            if (kh != null && !kh.isDaXoa()) { tongKhachHang++; }
        }

        for (int i = 0; i < dsHD.getSoLuong(); i++) {
            HoaDon hoaDon = dsHD.get(i);
            if (!(hoaDon instanceof Hoadonban)) { continue; }
            Hoadonban hd = (Hoadonban) hoaDon;
            if (!hd.isDaXuLyThanhCong()) { continue; }
            String maKH = hd.getMaKH();
            if (maKH == null || maKH.trim().isEmpty() || maKH.equalsIgnoreCase("KHACH_LE")) { continue; }
            Khachhang kh = dsKH.timKiem(maKH);
            if (kh == null || kh.isDaXoa()) { continue; }

            LocalDate ngayLap;
            try { ngayLap = LocalDate.parse(hd.getNgayLap(), dinhDangNgay); } catch (DateTimeParseException e) { continue; }
            String maChuan = kh.getMaKH();
            if (!lanMuaDauTien.containsKey(maChuan) || ngayLap.isBefore(lanMuaDauTien.get(maChuan))) {
                lanMuaDauTien.put(maChuan, ngayLap);
            }
            if (ngayLap.isBefore(tuNgay) || ngayLap.isAfter(denNgay)) { continue; }
            soLanMua.put(maChuan, soLanMua.getOrDefault(maChuan, 0) + 1);
            chiTieu.put(maChuan, chiTieu.getOrDefault(maChuan, 0.0) + hd.tinhThanhTien());
        }

        int soKhachHangMoi = 0;
        for (LocalDate ngayMua : lanMuaDauTien.values()) {
            if (!ngayMua.isBefore(tuNgay) && !ngayMua.isAfter(denNgay)) { soKhachHangMoi++; }
        }

        String maMuaNhieuNhat = null;
        int soLanMuaNhieuNhat = 0;
        for (Map.Entry<String, Integer> entry : soLanMua.entrySet()) {
            if (entry.getValue() > soLanMuaNhieuNhat) { soLanMuaNhieuNhat = entry.getValue(); maMuaNhieuNhat = entry.getKey(); }
        }

        String maChiTieuCaoNhat = null;
        double mucChiTieuCaoNhat = 0, tongDoanhThuKhachHang = 0;
        for (Map.Entry<String, Double> entry : chiTieu.entrySet()) {
            double tien = entry.getValue();
            tongDoanhThuKhachHang += tien;
            if (maChiTieuCaoNhat == null || tien > mucChiTieuCaoNhat) { mucChiTieuCaoNhat = tien; maChiTieuCaoNhat = entry.getKey(); }
        }

        System.out.println("\n========== BÁO CÁO KHÁCH HÀNG ==========");
        System.out.println("Thời gian: " + tuNgay.format(dinhDangNgay) + " - " + denNgay.format(dinhDangNgay));
        System.out.println("----------------------------------------");
        System.out.println("Tổng số khách hàng: " + tongKhachHang);
        System.out.println("Số khách hàng đã mua hàng: " + soLanMua.size());
        System.out.println("Số khách hàng mới trong kỳ: " + soKhachHangMoi);
        System.out.println("----------------------------------------");

        if (maMuaNhieuNhat != null) {
            Khachhang kh = dsKH.timKiem(maMuaNhieuNhat);
            System.out.println("Khách hàng mua nhiều lần nhất: " + kh.getTenKH() + " [" + maMuaNhieuNhat + "] - " + soLanMuaNhieuNhat + " hóa đơn");
        } else { System.out.println("Khách hàng mua nhiều lần nhất: Chưa có"); }

        if (maChiTieuCaoNhat != null) {
            Khachhang kh = dsKH.timKiem(maChiTieuCaoNhat);
            System.out.println("Khách hàng có tổng chi tiêu cao nhất: " + kh.getTenKH() + " [" + maChiTieuCaoNhat + "] - " + Ktradulieu.dinhDangTien(mucChiTieuCaoNhat));
        } else { System.out.println("Khách hàng có tổng chi tiêu cao nhất: Chưa có"); }

        System.out.println("----------------------------------------");
        System.out.println("Tổng doanh thu từ khách hàng: " + Ktradulieu.dinhDangTien(tongDoanhThuKhachHang));
        System.out.println("========================================");
    }

    @Override
    public void thongKeNhanVien(DSNhanVien dsNV) {
        if (dsNV == null) {
            System.out.println("[Lỗi] Danh sách nhân viên không hợp lệ!");
            return;
        }

        int tongNhanVien = 0;

        System.out.println("\n========== BÁO CÁO NHÂN VIÊN ==========");

        for (int i = 0; i < dsNV.getSoLuong(); i++) {

            Object obj = dsNV.get(i);

            if (!(obj instanceof NhanVien)) {
                continue;
            }

            NhanVien nv = (NhanVien) obj;
            if (nv.isDaXoa()) continue;

            tongNhanVien++;

            System.out.println("--------------------------------------");
            System.out.println("Mã nhân viên: " + nv.getMaND());
            System.out.println("Họ tên: " + nv.getTenND());
            System.out.println("Ngày vào làm: " + nv.getNgayVaoLam());
            System.out.printf("Lương cơ bản: %,.0f VND%n", nv.getLuongCoBan());
        }

        System.out.println("--------------------------------------");
        System.out.println("Tổng số nhân viên: " + tongNhanVien);
        System.out.println("======================================");
    }
    @Override
    public void thongKeBangLuong(DSNhanVien dsNV) {
        if (dsNV == null) {
            System.out.println("[Lỗi] Danh sách nhân viên không hợp lệ!");
            return;
        }

        double tongQuyLuong = 0;
        int soNhanVien = 0;

        System.out.println("\n========== BÁO CÁO BẢNG LƯƠNG ==========");

        for (int i = 0; i < dsNV.getSoLuong(); i++) {

            Object obj = dsNV.get(i);

            if (!(obj instanceof NhanVien)) {
                continue;
            }

            NhanVien nv = (NhanVien) obj;
            if (nv.isDaXoa()) continue;

            soNhanVien++;
            tongQuyLuong += tinhLuongNhanVien(nv);
        }

        System.out.println("\n========== TỔNG KẾT BẢNG LƯƠNG ==========");
        System.out.println("Số nhân viên tính lương: " + soNhanVien);
        System.out.printf("Tổng quỹ lương: %,.0f VND%n", tongQuyLuong);
        System.out.println("=========================================");
    }

    @Override
    public void thongKeNhanVienVaBangLuong(DSNhanVien dsNV) {
        thongKeNhanVien(dsNV);
        thongKeBangLuong(dsNV);
    }

    private void tinhToanBaoCao(DSHoadon dsHD, LocalDate tuNgay, LocalDate denNgay) {
        double tongDoanhThuBan = 0, tongChiPhiNhap = 0;
        int donBanThanhCong = 0, donNhapThanhCong = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < dsHD.getSoLuong(); i++) {
            HoaDon hd = dsHD.get(i);
            if (hd == null || !hd.isDaXuLyThanhCong()) { continue; }
            if (hd.getNgayLap() == null || hd.getNgayLap().trim().isEmpty()) { continue; }
            LocalDate ngayLap;
            try { ngayLap = LocalDate.parse(hd.getNgayLap(), dtf); } catch (DateTimeParseException e) { continue; }
            if (ngayLap.isBefore(tuNgay) || ngayLap.isAfter(denNgay)) { continue; }

            if (hd instanceof Hoadonban) {
                tongDoanhThuBan += ((Hoadonban) hd).tinhDoanhThuThuan();
                donBanThanhCong++;
            } else if (hd instanceof HoadonNhap) {
                tongChiPhiNhap += hd.tinhTongTien();
                donNhapThanhCong++;
            }
        }

        double loiNhuan = tongDoanhThuBan - tongChiPhiNhap;

        System.out.println("\n========== BÁO CÁO DOANH THU & CHI PHÍ ==========");
        System.out.println("Thời gian: " + tuNgay.format(dtf) + " - " + denNgay.format(dtf));
        System.out.printf("- Doanh thu thuần bán hàng (%d đơn, sau giảm giá, chưa VAT): %,.0f VND%n",
                donBanThanhCong, tongDoanhThuBan);
        System.out.printf("- Chi phí nhập hàng (%d đơn thành công): %,.0f VND%n",
                donNhapThanhCong, tongChiPhiNhap);
        System.out.println("--------------------------------------------------");
        if (loiNhuan >= 0) {
            System.out.printf("- Chênh lệch thu - chi: +%,.0f VND (có lãi)%n", loiNhuan);
        } else {
            System.out.printf("- Chênh lệch thu - chi: -%,.0f VND (đang lỗ)%n", Math.abs(loiNhuan));
        }
        System.out.println("======================================================");
    }

    public void thongKeTongQuanHoaDon(DSHoadon dsHD, LocalDate tuNgay, LocalDate denNgay) {
        if (dsHD == null || tuNgay == null || denNgay == null) { System.out.println("[Lỗi] Dữ liệu báo cáo không hợp lệ!"); return; }
        if (tuNgay.isAfter(denNgay)) { System.out.println("[Lỗi] Ngày bắt đầu không được sau ngày kết thúc!"); return; }
        int tongHDBan = 0, tongHDNhap = 0, tongHDHuy = 0, tongHD = 0;
        DateTimeFormatter dinhDangNgay = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (HoaDon hd : dsHD.getDanhSach()) {
            if (hd == null || hd.getNgayLap() == null) { continue; }
            LocalDate ngayLap;
            try { ngayLap = LocalDate.parse(hd.getNgayLap(), dinhDangNgay); } catch (DateTimeParseException e) { continue; }
            if (ngayLap.isBefore(tuNgay) || ngayLap.isAfter(denNgay)) { continue; }
            tongHD++;
            if (hd.isDaHuy()) { tongHDHuy++; continue; }
            if (hd.isDaXuLyThanhCong()) {
                if (hd instanceof Hoadonban) { tongHDBan++; }
                else if (hd instanceof HoadonNhap) { tongHDNhap++; }
            }
        }

        System.out.println("\n========== BÁO CÁO TỔNG QUAN HÓA ĐƠN ==========");
        System.out.println("Từ ngày: " + tuNgay.format(dinhDangNgay));
        System.out.println("Đến ngày: " + denNgay.format(dinhDangNgay));
        System.out.println("------------------------------------------------");
        System.out.println("Số hóa đơn bán thành công: " + tongHDBan);
        System.out.println("Số hóa đơn nhập thành công: " + tongHDNhap);
        System.out.println("Số hóa đơn bị hủy: " + tongHDHuy);
        System.out.println("Tổng số hóa đơn: " + tongHD);
        System.out.println("================================================");
    }

    public void thongKeTonKho(DSSanPham dsSP) {
        if (dsSP == null) {
            System.out.println("[Lỗi] Danh sách sản phẩm không hợp lệ!");
            return;
        }

        int sapHet = 0;
        int anToan = 0;
        int tongMatHang = 0;

        System.out.println("\n---------------- BÁO CÁO TỒN KHO & CẢNH BÁO HÀNG HÓA ----------------");

        // 1. Danh sách sản phẩm an toàn
        System.out.println("\n========== DANH SÁCH SẢN PHẨM AN TOÀN ==========");
        System.out.printf("%-5s | %-12s | %-25s | %-12s | %-12s%n",
                "STT", "MÃ SP", "TÊN SẢN PHẨM", "TỒN KHO", "ĐƠN VỊ");

        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < dsSP.getSoLuong(); i++) {
            SanPham sp = dsSP.get(i);

            if (sp == null || sp.isDaXoa()) {
                continue;
            }

            if (sp.getSoLuongTon() > 10) {
                anToan++;
                tongMatHang++;

                System.out.printf("%-5d | %-12s | %-25s | %-12d | %-12s%n",
                        anToan,
                        sp.getMaSP(),
                        sp.getTenSP(),
                        sp.getSoLuongTon(),
                        sp.getDonViTinh());
            }
        }

        if (anToan == 0) {
            System.out.println("Không có sản phẩm nào đang ở mức tồn kho an toàn.");
        }

        // 2. Danh sách sản phẩm cần nhập bổ sung
        System.out.println("\n========== DANH SÁCH SẢN PHẨM CẦN NHẬP BỔ SUNG ==========");
        System.out.printf("%-5s | %-12s | %-25s | %-12s | %-12s%n",
                "STT", "MÃ SP", "TÊN SẢN PHẨM", "TỒN KHO", "ĐƠN VỊ");

        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < dsSP.getSoLuong(); i++) {
            SanPham sp = dsSP.get(i);

            if (sp == null || sp.isDaXoa()) {
                continue;
            }

            if (sp.getSoLuongTon() <= 10) {
                sapHet++;
                tongMatHang++;

                System.out.printf("%-5d | %-12s | %-25s | %-12d | %-12s%n",
                        sapHet,
                        sp.getMaSP(),
                        sp.getTenSP(),
                        sp.getSoLuongTon(),
                        sp.getDonViTinh());
            }
        }

        if (sapHet == 0) {
            System.out.println("Không có sản phẩm nào cần nhập bổ sung.");
        }

        // 3. Tổng kết
        System.out.println("\n======================= TỔNG KẾT =======================");
        System.out.println("- Tổng số mặt hàng quản lý: " + tongMatHang);
        System.out.println("- Số mặt hàng an toàn: " + anToan);
        System.out.println("- Số mặt hàng cần nhập bổ sung ngay (<= 10): " + sapHet);
        System.out.println("========================================================");
    }

    @Override
    public void baoCaoTheoThang(DSHoadon dsHD, int thang, int nam) {
        if (dsHD == null) { System.out.println("[Lỗi] Danh sách hóa đơn không hợp lệ!"); return; }
        if (nam <= 0 || thang < 1 || thang > 12) { System.out.println("[Lỗi] Tháng hoặc năm không hợp lệ!"); return; }
        try {
            LocalDate tuNgay = LocalDate.of(nam, thang, 1);
            LocalDate denNgay = tuNgay.withDayOfMonth(tuNgay.lengthOfMonth());
            System.out.println("\n========== BÁO CÁO THEO THÁNG ==========\nTháng " + thang + "/" + nam);
            tinhToanBaoCao(dsHD, tuNgay, denNgay);
            thongKeTongQuanHoaDon(dsHD, tuNgay, denNgay);
        } catch (DateTimeException e) { System.out.println("[Lỗi] Không thể tạo kỳ báo cáo: " + e.getMessage()); }
    }

    @Override
    public void baoCaoTheoQuy(DSHoadon dsHD, int quy, int nam) {
        if (dsHD == null) { System.out.println("[Lỗi] Danh sách hóa đơn không hợp lệ!"); return; }
        if (nam <= 0 || quy < 1 || quy > 4) { System.out.println("[Lỗi] Quý hoặc năm không hợp lệ!"); return; }
        try {
            int thangBatDau = (quy - 1) * 3 + 1;
            LocalDate tuNgay = LocalDate.of(nam, thangBatDau, 1);
            LocalDate denNgay = tuNgay.plusMonths(2).withDayOfMonth(tuNgay.plusMonths(2).lengthOfMonth());
            System.out.println("\n========== BÁO CÁO THEO QUÝ ==========\nQuý " + quy + " năm " + nam);
            tinhToanBaoCao(dsHD, tuNgay, denNgay);
            thongKeTongQuanHoaDon(dsHD, tuNgay, denNgay);
        } catch (DateTimeException e) { System.out.println("[Lỗi] Không thể tạo kỳ báo cáo: " + e.getMessage()); }
    }

    @Override
    public void baoCaoTheoNam(DSHoadon dsHD, int nam) {
        if (dsHD == null) { System.out.println("[Lỗi] Danh sách hóa đơn không hợp lệ!"); return; }
        if (nam <= 0) { System.out.println("[Lỗi] Năm báo cáo không hợp lệ!"); return; }
        try {
            LocalDate tuNgay = LocalDate.of(nam, 1, 1);
            LocalDate denNgay = LocalDate.of(nam, 12, 31);
            System.out.println("\n========== BÁO CÁO THEO NĂM ==========\nNăm " + nam);
            tinhToanBaoCao(dsHD, tuNgay, denNgay);
            thongKeTongQuanHoaDon(dsHD, tuNgay, denNgay);
        } catch (DateTimeException e) { System.out.println("[Lỗi] Không thể tạo kỳ báo cáo: " + e.getMessage()); }
    }

    public void xuatThongTin() {
        System.out.printf("%-10s | %-22s | %-12s | %-15s | Khu vực: %s | Hệ số: %.2f | Trạng thái: %s\n",
                getMaND(), getTenND(), getSoDienThoai(), getVaiTro(), khuVucQuanLy, heSoTrachNhiem, (daXoa ? "ĐÃ NGHỈ VIỆC" : "HOẠT ĐỘNG"));
    }

    public String toFileString() {
        return String.format(Locale.ROOT, "QUANLY;%s;%s;%s;%s;%s;%s;%b;%s;%.2f",
                getMaND(), getTenND(), getTaiKhoan(), getMatKhau(), getSoDienThoai(), getVaiTro(), isDaXoa(), khuVucQuanLy, heSoTrachNhiem);
    }
}