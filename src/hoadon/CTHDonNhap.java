package hoadon;

import java.util.Arrays;

import java.time.LocalDate;
import java.util.Scanner;
import sanpham.ThucAn;
import sanpham.DoUong;

import person.Nguoidung;
import sanpham.DSSanPham;
import sanpham.SanPham;
import util.Ktradulieu;

public class CTHDonNhap extends CTHoaDon {

    private double thueSuatNhap;
    private LocalDate hanSuDung; // Giữ thông tin ngày ở file hóa đơn cũ, không chia hàng theo lô.
    private SanPham sanPhamMoi; // Chỉ giữ hồ sơ đi kèm hóa đơn ở định dạng file cũ.

    // ==================== CONSTRUCTOR ====================

    public CTHDonNhap() {
        super();
        this.thueSuatNhap = 0;
        this.hanSuDung = null;
        this.sanPhamMoi = null;
    }

    public CTHDonNhap(CTHDonNhap ct) {
        super(ct != null ? ct.getMaHD() : "", ct != null ? ct.getMaSP() : "", ct != null ? ct.getTenSP() : "", ct != null ? ct.getSoLuongNhap() : 0, ct != null ? ct.getDonGiaNhap() : 0);
        if (ct == null) {
            throw new IllegalArgumentException("[Lỗi] Chi tiết hóa đơn không hợp lệ!");
        }
        this.thueSuatNhap = ct.thueSuatNhap;
        this.hanSuDung = ct.hanSuDung;
        this.sanPhamMoi = saoChepSanPhamMoi(ct.sanPhamMoi);
    }

    private static SanPham saoChepSanPhamMoi(SanPham sp) {
        if (sp == null) return null;
        if (sp instanceof ThucAn) {
            ThucAn ta = (ThucAn) sp;
            return new ThucAn(ta.getMaSP(), ta.getTenSP(), ta.getDonGiaNhap(), ta.getDonGiaBan(),
                    ta.getSoLuongTon(), ta.getDonViTinh(), ta.getNsx(), ta.getHsd(), ta.getPhanTramThue(),
                    ta.getThanhPhan(), ta.getBaoQuan(), ta.getXuatXu(), ta.isLaDoChay(),
                    ta.getMaNCC(), ta.isDaXoa());
        }
        if (sp instanceof DoUong) {
            DoUong du = (DoUong) sp;
            return new DoUong(du.getMaSP(), du.getTenSP(), du.getDonGiaNhap(), du.getDonGiaBan(),
                    du.getSoLuongTon(), du.getDonViTinh(), du.getNsx(), du.getHsd(), du.getPhanTramThue(),
                    du.getDungTich(), du.getThuongHieu(), du.getHuongVi(), du.getLoaiDoUong(),
                    du.getMaNCC(), du.isDaXoa());
        }
        throw new IllegalArgumentException("Loại sản phẩm mới không hợp lệ!");
    }

    public CTHDonNhap(String maHD, String maSP, String tenSP, int soLuongNhap, double donGiaNhap, double thueSuatNhap, LocalDate hanSuDung) {
        super(maHD, maSP, tenSP, soLuongNhap, donGiaNhap);
        setThueSuatNhap(thueSuatNhap);
        setHanSuDung(hanSuDung);
    }

    public CTHDonNhap(String maHD, SanPham sp, int soLuongNhap, double donGiaNhap, LocalDate hanSuDung) {
        super(maHD, sp.getMaSP(), sp.getTenSP(), soLuongNhap, donGiaNhap > 0 ? donGiaNhap : sp.getDonGiaNhap());
        setThueSuatNhap(sp.getPhanTramThue());
        setHanSuDung(hanSuDung);
    }

    // ==================== GETTER / SETTER ====================

    public int getSoLuongNhap() { return getSoLuong(); }
    public void setSoLuongNhap(int soLuongNhap) {
        if (soLuongNhap <= 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng nhập phải lớn hơn 0!");
        }
        setSoLuong(soLuongNhap);
    }

    public double getDonGiaNhap() { return getDonGia(); }
    public void setDonGiaNhap(double donGiaNhap) {
        if (donGiaNhap < 0 || Double.isNaN(donGiaNhap) || Double.isInfinite(donGiaNhap)) {
            throw new IllegalArgumentException("[Lỗi] Giá nhập không hợp lệ!");
        }
        setDonGia(donGiaNhap);
    }

    public double getThueSuatNhap() { return thueSuatNhap; }
    public void setThueSuatNhap(double thueSuatNhap) {
        if (thueSuatNhap < 0 || thueSuatNhap > 100 || Double.isNaN(thueSuatNhap) || Double.isInfinite(thueSuatNhap)) {
            throw new IllegalArgumentException("[Lỗi] Thuế suất nhập phải nằm trong khoảng 0-100%!");
        }
        this.thueSuatNhap = thueSuatNhap;
    }

    public LocalDate getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(LocalDate hanSuDung) {
        if (hanSuDung == null) {
            throw new IllegalArgumentException("[Lỗi] Hạn sử dụng không được để trống!");
        }
        if (hanSuDung.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("[Lỗi] Hạn sử dụng không được trước ngày hiện tại!");
        }
        this.hanSuDung = hanSuDung;
    }

    public SanPham getSanPhamMoi() { return sanPhamMoi; }
    public void setSanPhamMoi(SanPham sanPhamMoi) { this.sanPhamMoi = sanPhamMoi; }


    // ==================== NHẬP THÔNG TIN ====================

    public boolean nhapThongTin(Scanner sc, String maHoaDon, DSSanPham dsSP) {
        return nhapThongTin(sc, maHoaDon, dsSP, null);
    }

    public boolean nhapThongTin(Scanner sc, String maHoaDon, DSSanPham dsSP, Nguoidung nguoiDungDangNhap) {
        return nhapThongTin(sc, maHoaDon, dsSP, nguoiDungDangNhap, "");
    }

    public boolean nhapThongTin(Scanner sc, String maHoaDon, DSSanPham dsSP,
                                Nguoidung nguoiDungDangNhap, String maNCC) {
        if (sc == null) throw new IllegalArgumentException("Scanner không được null!");
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            System.out.println("[Lỗi] Dữ liệu nhập hàng không hợp lệ!");
            return false;
        }

        System.out.println("\n========== NHẬP CHI TIẾT HÀNG NHẬP ==========");
        this.sanPhamMoi = null;

        while (true) {
            System.out.print("Nhập mã sản phẩm (Ví dụ: TA001, DU001 - Nhập 0 để hủy): ");
            String input = sc.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("[Thông báo] Đã hủy thao tác nhập chi tiết hàng nhập!");
                return false;
            }

            try {
                String maSP = Ktradulieu.chuanHoaMa(input);
                if (!maSP.startsWith("TA") && !maSP.startsWith("DU") && !maSP.startsWith("SP")) {
                    System.out.println("[Lỗi] Mã sản phẩm phải bắt đầu bằng TA, DU hoặc SP!");
                    continue;
                }

                SanPham sp = (dsSP != null) ? dsSP.timKiem(maSP) : null;
                if (sp == null || sp.isDaXoa()) {
                    System.out.println("[Lỗi] Sản phẩm chưa có trong danh mục hoặc đã bị xóa.");
                    System.out.println("Admin/Quản lý cần thêm sản phẩm với tồn kho 0 trước khi lập phiếu nhập.");
                    continue;
                }
                if (maNCC != null && !maNCC.isEmpty() && !sp.getMaNCC().isEmpty()
                        && !maNCC.equalsIgnoreCase(sp.getMaNCC())) {
                    System.out.println("[Lỗi] Sản phẩm thuộc nhà cung cấp " + sp.getMaNCC() + ", không thuộc phiếu này!");
                    continue;
                }
                setMaHD(maHoaDon);
                setMaSP(sp.getMaSP());
                setTenSP(sp.getTenSP());
                setDonGiaNhap(sp.getDonGiaNhap());
                setThueSuatNhap(sp.getPhanTramThue());
                this.hanSuDung = sp.tinhHsdKhiNhapHang(LocalDate.now());
                System.out.println("-> Đã nhận diện sản phẩm: [" + sp.getMaSP() + "] " + sp.getTenSP());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        return nhapSoLuongVaGia(sc);
    }

    public boolean nhapThongTin(Scanner sc, String maHoaDon, SanPham sp) {
        if (sc == null || sp == null || sp.isDaXoa()) return false;
        setMaHD(maHoaDon);
        setMaSP(sp.getMaSP());
        setTenSP(sp.getTenSP());
        setDonGiaNhap(sp.getDonGiaNhap());
        setThueSuatNhap(sp.getPhanTramThue());
        this.hanSuDung = sp.tinhHsdKhiNhapHang(LocalDate.now());
        sanPhamMoi = null;
        return nhapSoLuongVaGia(sc);
    }

    private boolean nhapSoLuongVaGia(Scanner sc) {
        // Nhập số lượng
        long soLuong;
        while (true) {
            soLuong = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng nhập kho: ");
            if (soLuong > 0 && soLuong <= Integer.MAX_VALUE) break;
            System.out.println("[Lỗi] Số lượng phải lớn hơn 0 và không vượt giới hạn!");
        }
        setSoLuongNhap((int) soLuong);

        // Giá nhập
        while (true) {
            try {
                if (getDonGiaNhap() > 0) {
                    System.out.print("Đơn giá nhập hiện tại là: " + Ktradulieu.dinhDangTien(getDonGiaNhap()) + ". Nhấn ENTER để giữ nguyên, hoặc nhập giá mới: ");
                } else {
                    System.out.print("Nhập giá nhập mới: ");
                }
                String dgInput = sc.nextLine().trim();
                if (dgInput.isEmpty() && getDonGiaNhap() > 0) {
                    break;
                }
                double giaMoi = Double.parseDouble(dgInput);
                if (giaMoi < 0 || Double.isNaN(giaMoi) || Double.isInfinite(giaMoi)) {
                    System.out.println("[Lỗi] Giá nhập không hợp lệ!");
                    continue;
                }
                setDonGiaNhap(giaMoi);
                break;
            } catch (NumberFormatException e) {
                System.out.println("[Lỗi] Đơn giá phải là số hợp lệ!");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        return true;
    }


    // ==================== TÍNH TIỀN ====================

    @Override
    public double tinhTienHang() { return getSoLuongNhap() * getDonGiaNhap(); }

    @Override
    public double tinhTienThue() { return tinhTienHang() * this.thueSuatNhap / 100.0; }

    @Override
    public double tinhThanhTien() { return tinhTienHang() + tinhTienThue(); }

    // ==================== LƯU / ĐỌC FILE ====================

    @Override
    public void kiemTraDuLieuFile() {
        super.kiemTraDuLieuFile();
        if (sanPhamMoi != null && (!maSP.equalsIgnoreCase(sanPhamMoi.getMaSP())
                || !Double.isFinite(sanPhamMoi.getDonGiaNhap()) || sanPhamMoi.getDonGiaNhap() <= 0
                || !Double.isFinite(sanPhamMoi.getDonGiaBan()) || sanPhamMoi.getDonGiaBan() <= 0
                || !Double.isFinite(sanPhamMoi.getPhanTramThue()) || sanPhamMoi.getPhanTramThue() < 0
                || sanPhamMoi.getPhanTramThue() > 100 || sanPhamMoi.getSoLuongTon() < 0)) {
            throw new IllegalArgumentException("Hồ sơ sản phẩm mới trong hóa đơn không hợp lệ!");
        }
    }

    @Override
    public String toDongFile() {
        kiemTraDuLieuFile();
        String hsd = Ktradulieu.dinhDangNgay(this.hanSuDung);

        String dong = String.format(java.util.Locale.US,
                "%s;%s;%s;%d;%.0f;%.2f;%s",
                maHD, maSP, tenSP, getSoLuongNhap(),
                getDonGiaNhap(), this.thueSuatNhap, hsd
        );

        if (this.sanPhamMoi != null) {

            String dongSanPham;

            if (this.sanPhamMoi instanceof ThucAn) {
                dongSanPham = ((ThucAn) this.sanPhamMoi).toFileString();

            } else if (this.sanPhamMoi instanceof DoUong) {
                dongSanPham = ((DoUong) this.sanPhamMoi).toFileString();

            } else {
                throw new IllegalStateException("Loại sản phẩm không hợp lệ!");
            }

            dong += ";SPMOI;" + dongSanPham;
        }

        return dong;
    }

    public static CTHDonNhap tuDongFile(String dong) {

        if (dong == null || dong.trim().isEmpty()) {
            return null;
        }

        try {
            String[] p = dong.split(";", -1);

            if (p.length != 7 && (p.length < 23 || p.length > 24 || !"SPMOI".equalsIgnoreCase(p[7]))) {
                return null;
            }

            int soLuong = Integer.parseInt(p[3].trim());
            double donGia = Double.parseDouble(p[4].trim());
            double vat = Double.parseDouble(p[5].trim());

            LocalDate hsd = null;

            if (!p[6].trim().isEmpty()) {
                hsd = Ktradulieu.chuyenChuoiThanhNgay(p[6]);
            }

            CTHDonNhap ct = new CTHDonNhap();

            ct.setMaHD(p[0]);
            ct.setMaSP(p[1]);
            ct.setTenSP(p[2]);

            ct.setSoLuongNhap(soLuong);
            ct.setDonGiaNhap(donGia);
            ct.setThueSuatNhap(vat);
            ct.hanSuDung = hsd;

            ct.sanPhamMoi = null;

            // Khôi phục hồ sơ sản phẩm mới
            if (p.length > 8 && p[7].equalsIgnoreCase("SPMOI")) {

                String[] sp = Arrays.copyOfRange(p, 8, p.length);

                ct.sanPhamMoi = SanPham.tuDongFile(String.join(";", sp));

            }

            if (ct.sanPhamMoi != null && !ct.maSP.equalsIgnoreCase(ct.sanPhamMoi.getMaSP())) {
                throw new IllegalArgumentException("Hồ sơ sản phẩm mới không khớp mã trong chi tiết!");
            }
            ct.kiemTraDuLieuFile();
            return ct;

        } catch (Exception e) {
            System.out.println(
                    "[Lỗi] Đọc chi tiết hóa đơn nhập: " + e.getMessage()
            );
            return null;
        }
    }

    // ==================== HIỂN THỊ ====================

    @Override
    public String[] layTieuDeBang() {
        return new String[]{"MÃ SP", "TÊN SẢN PHẨM", "SL", "ĐƠN GIÁ", "THUẾ", "THÀNH TIỀN"};
    }

    @Override
    public Object[] layDongBang() {
        return new Object[]{maSP, tenSP, getSoLuongNhap(), Ktradulieu.dinhDangTien(getDonGiaNhap()),
                thueSuatNhap + "%",
                Ktradulieu.dinhDangTien(tinhThanhTien())};
    }
}
