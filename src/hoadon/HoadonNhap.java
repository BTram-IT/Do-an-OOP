package hoadon;

import util.BangConsole;
import java.util.Arrays;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.Objects;

import doitac.DSNhaCungCap;
import person.Nguoidung;
import sanpham.DSSanPham;
import sanpham.SanPham;
import thanhtoan.ThanhToan;
import thanhtoan.TienMat;
import thanhtoan.ChuyenKhoan;
import util.Ktradulieu;

public class HoadonNhap extends HoaDon implements IHoaDon {

    private String maNCC;
    private CTHDonNhap[] dsChiTiet;
    private int soLuongCT;
    private ThanhToan phuongThucThanhToan;

    private static final int SUC_CHUA_MAC_DINH = 20;

    public HoadonNhap() {
        super();
        this.maNCC = "";
        this.dsChiTiet = new CTHDonNhap[SUC_CHUA_MAC_DINH];
        this.soLuongCT = 0;
        this.phuongThucThanhToan = null;
    }

    public HoadonNhap(String maHD, Nguoidung nguoiLap, LocalDate ngayLap, String trangThai, String ghiChu, String maNCC) {
        super(maHD, nguoiLap, ngayLap, trangThai, ghiChu);
        if (maNCC == null || maNCC.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã nhà cung cấp không được để trống!");
        }
        this.maNCC = Ktradulieu.chuanHoaMa(maNCC);
        this.dsChiTiet = new CTHDonNhap[SUC_CHUA_MAC_DINH];
        this.soLuongCT = 0;
        this.phuongThucThanhToan = null;
    }

    private void moRongBoNho() {
        int sucChuaMoi = this.dsChiTiet.length * 2;
        CTHDonNhap[] mangMoi = new CTHDonNhap[sucChuaMoi];
        for (int i = 0; i < this.soLuongCT; i++) {
            mangMoi[i] = this.dsChiTiet[i];
        }
        this.dsChiTiet = mangMoi;
    }

    public boolean themCTHD(CTHDonNhap ct) {
        if (!coTheChinhSua() || ct == null || !maHD.equalsIgnoreCase(ct.getMaHD())
                || ct.getSoLuongNhap() <= 0 || !Double.isFinite(ct.getDonGiaNhap()) || ct.getDonGiaNhap() <= 0) {
            System.out.println("[Lỗi] Chi tiết nhập không hợp lệ hoặc hóa đơn đã hoàn tất!");
            return false;
        }
        try { ct.kiemTraDuLieuFile(); }
        catch (IllegalArgumentException e) { System.out.println(e.getMessage()); return false; }
        for (int i = 0; i < soLuongCT; i++) {
            CTHDonNhap cu = dsChiTiet[i];
            if (!cu.getMaSP().equalsIgnoreCase(ct.getMaSP())) continue;
            long tong = (long) cu.getSoLuongNhap() + ct.getSoLuongNhap();
            if (tong > Integer.MAX_VALUE) {
                System.out.println("[Lỗi] Tổng số lượng nhập vượt giới hạn!");
                return false;
            }
            CTHDonNhap moi = new CTHDonNhap(ct);
            moi.setSoLuongNhap((int) tong);
            if (moi.getSanPhamMoi() == null && cu.getSanPhamMoi() != null) {
                moi.setSanPhamMoi(new CTHDonNhap(cu).getSanPhamMoi());
            }
            dsChiTiet[i] = moi;
            System.out.println("Đã cộng dồn [" + ct.getMaSP() + "]. Đơn giá và VAT áp dụng theo lần nhập cuối.");
            return true;
        }
        if (soLuongCT >= dsChiTiet.length) moRongBoNho();
        dsChiTiet[soLuongCT++] = new CTHDonNhap(ct);
        return true;
    }

    public CTHDonNhap[] getDsChiTiet() {
        CTHDonNhap[] ketQua = new CTHDonNhap[this.soLuongCT];
        for (int i = 0; i < this.soLuongCT; i++) {
            ketQua[i] = new CTHDonNhap(this.dsChiTiet[i]);
        }
        return ketQua;
    }

    public int getSoLuongCT() { return this.soLuongCT; }

    public boolean themCTHDTuFile(CTHDonNhap ct) {
        if (ct == null || !this.maHD.equalsIgnoreCase(ct.getMaHD())) {
            return false;
        }
        for (int i = 0; i < soLuongCT; i++) {
            CTHDonNhap daCo = dsChiTiet[i];
            if (daCo.getMaSP().equalsIgnoreCase(ct.getMaSP())
                    && Double.compare(daCo.getDonGiaNhap(), ct.getDonGiaNhap()) == 0
                    && Double.compare(daCo.getThueSuatNhap(), ct.getThueSuatNhap()) == 0
                    && Objects.equals(daCo.getHanSuDung(), ct.getHanSuDung())) return false;
        }

        if (this.soLuongCT >= this.dsChiTiet.length) {
            moRongBoNho();
        }

        this.dsChiTiet[this.soLuongCT++] = new CTHDonNhap(ct);
        return true;
    }
    @Override
    public String getmaHoaDon() { return this.maHD; }

    @Override
    public void setmaHoaDon() {}

    @Override
    public void thanhTien() {
        System.out.println("Tổng tiền hàng: " + Ktradulieu.dinhDangTien(this.tinhTongTien()));
        System.out.println("Tiền thuế VAT: " + Ktradulieu.dinhDangTien(this.tinhTienThue()));
        System.out.println("Tổng thanh toán: " + Ktradulieu.dinhDangTien(this.tinhThanhTien()));
    }

    @Override
    public void hienThiHoaDon() { System.out.println(inHoaDon()); }

    @Override
    public double tinhTongTien() {
        double tong = 0;
        for (int i = 0; i < this.soLuongCT; i++) {
            tong += this.dsChiTiet[i].tinhTienHang();
        }
        return tong;
    }

    public double tinhTienThue() {
        double tongThue = 0;
        for (int i = 0; i < this.soLuongCT; i++) {
            tongThue += this.dsChiTiet[i].tinhTienThue();
        }
        return tongThue;
    }

    @Override
    public double tinhThanhTien() { return tinhTongTien() + tinhTienThue(); }

    @Override
    public String getLoaiHD() { return "Hóa đơn nhập"; }

    @Override
    protected String layMaDoiTac() { return this.maNCC; }

    public boolean nhapThongTin(Scanner sc, Nguoidung nhanVienDangNhap, DSHoadon dsHD, DSNhaCungCap dsNCC, DSSanPham dsSP, Nguoidung nguoiDungDangNhap) {
        if (sc == null) throw new IllegalArgumentException("Scanner không được null!");
        super.nhapThongTin(sc, nhanVienDangNhap, dsHD);
        if (!chonNhaCungCap(sc, dsNCC)) return false;
        do {
            themSanPham(sc, dsSP, nguoiDungDangNhap);
        } while (Ktradulieu.luaChon(sc, "Nhập thêm mặt hàng? (1: Có / 0: Xem lại hóa đơn): ", 0, 1) == 1);
        return true;
    }

    public boolean chonNhaCungCap(Scanner sc, DSNhaCungCap dsNCC) {
        return chonNhaCungCap(sc, dsNCC, null);
    }

    public boolean chonNhaCungCap(Scanner sc, DSNhaCungCap dsNCC, DSSanPham dsSP) {
        if (sc == null || !coTheChinhSua()) return false;
        boolean coNhaCungCap = false;
        if (dsNCC != null) {
            for (doitac.NhaCungCap ncc : dsNCC.getDanhSach()) {
                if (ncc != null && !ncc.isDaXoa()) { coNhaCungCap = true; break; }
            }
        }
        if (!coNhaCungCap) {
            System.out.println("[Lỗi] Chưa có nhà cung cấp đang hợp tác trong hệ thống!");
            return false;
        }

        System.out.println("\n--- THÔNG TIN ĐỐI TÁC NHÀ CUNG CẤP ---");
        while (true) {
            System.out.print("Nhập mã nhà cung cấp (/: NCC đầu tiên | 0: Quay lại): ");
            String inputNCC = sc.nextLine().trim();
            if (inputNCC.equals("0")) return false;

            if (inputNCC.equals("/")) {
                String maNCCDauTien = null;
                if (dsNCC != null) {
                    for (doitac.NhaCungCap n : dsNCC.getDanhSach()) {
                        if (n != null && !n.isDaXoa()) {
                            maNCCDauTien = n.getMaNCC();
                            break;
                        }
                    }
                }
                if (maNCCDauTien != null) {
                    if (!kiemTraNhaCungCap(dsSP, maNCCDauTien)) continue;
                    this.setMaNCC(maNCCDauTien);
                    System.out.println("-> Đã chọn NCC: " + this.maNCC);
                    break;
                } else {
                    System.out.println("[Lỗi] Chưa có nhà cung cấp trong hệ thống!");
                }
                continue;
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(inputNCC);
                if (!maChuan.startsWith("NCC")) {
                    System.out.println("[Lỗi] Mã nhà cung cấp phải bắt đầu bằng NCC!");
                    continue;
                }
                if (dsNCC != null) {
                    doitac.NhaCungCap nccChon = dsNCC.timKiemTheoMa(maChuan);
                    if (nccChon == null) {
                        System.out.println("[Lỗi] Nhà cung cấp không tồn tại!");
                        continue;
                    }
                    if (nccChon.isDaXoa()) {
                        System.out.println("[Lỗi] Nhà cung cấp này đã ngừng hợp tác, không thể lập hóa đơn nhập!");
                        continue;
                    }
                }
                if (!kiemTraNhaCungCap(dsSP, maChuan)) continue;
                this.setMaNCC(maChuan);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return true;
    }

    public boolean themSanPham(Scanner sc, DSSanPham dsSP, Nguoidung nguoiDung) {
        if (sc == null || !coTheChinhSua()) return false;
        CTHDonNhap ct = new CTHDonNhap();
        if (!ct.nhapThongTin(sc, maHD, dsSP, nguoiDung, maNCC) || !themCTHD(ct)) return false;
        System.out.println("[Thành công] Đã thêm mặt hàng vào hóa đơn nhập.");
        return true;
    }

    /** Mỗi mã sản phẩm là một dòng nhập, chỉ sửa số lượng, giá và VAT. */
    public boolean capNhatChiTiet(int viTri, int soLuong, double donGia, double vat) {
        if (!coTheChinhSua() || viTri < 0 || viTri >= soLuongCT || donGia <= 0) return false;
        try {
            CTHDonNhap moi = new CTHDonNhap(dsChiTiet[viTri]);
            moi.setSoLuongNhap(soLuong);
            moi.setDonGiaNhap(donGia);
            moi.setThueSuatNhap(vat);
            if (moi.getSanPhamMoi() != null) {
                moi.getSanPhamMoi().setDonGiaNhap(donGia);
                moi.getSanPhamMoi().setPhanTramThue(vat);
            }
            dsChiTiet[viTri] = moi;
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean xoaChiTiet(int viTri) {
        if (!coTheChinhSua() || viTri < 0 || viTri >= soLuongCT) return false;
        System.arraycopy(dsChiTiet, viTri + 1, dsChiTiet, viTri, soLuongCT - viTri - 1);
        dsChiTiet[--soLuongCT] = null;
        return true;
    }

    public void kiemTraHoaDon() {
        System.out.println("\n========== DANH SÁCH HÀNG CẦN NHẬP ==========");
        if (!"HDN_TAM".equals(maHD)) System.out.println("Mã hóa đơn nhập: " + maHD);
        System.out.println(("NCC_TAM".equals(maNCC) ? "Nhập hàng từ nhiều nhà cung cấp" : "Nhà cung cấp: " + maNCC)
                + " | Ghi chú: " + ghiChu);
        if (soLuongCT == 0) {
            System.out.println("Chưa có mặt hàng. Chọn thêm mặt hàng để tiếp tục.");
            return;
        }
        String[] cot = new CTHDonNhap().layTieuDeBang();
        String[] tieuDe = new String[cot.length + 1];
        tieuDe[0] = "STT";
        System.arraycopy(cot, 0, tieuDe, 1, cot.length);
        BangConsole bang = new BangConsole(tieuDe);
        for (int i = 0; i < soLuongCT; i++) {
            Object[] duLieu = dsChiTiet[i].layDongBang();
            Object[] dong = new Object[duLieu.length + 1];
            dong[0] = i + 1;
            System.arraycopy(duLieu, 0, dong, 1, duLieu.length);
            bang.themDong(dong);
        }
        System.out.println(bang);
        thanhTien();
    }

    public void suaSanPham(Scanner sc) {
        if (sc == null || !coTheChinhSua() || soLuongCT == 0) return;
        kiemTraHoaDon();
        int stt = Ktradulieu.luaChon(sc, "Chọn STT sản phẩm nhập cần sửa (0: Quay lại): ", 0, soLuongCT);
        if (stt == 0) return;
        CTHDonNhap cu = dsChiTiet[stt - 1];
        System.out.println("Enter để giữ giá trị hiện tại.");
        while (true) {
            try {
                System.out.print("Số lượng [" + cu.getSoLuongNhap() + "]: ");
                String sl = sc.nextLine().trim();
                System.out.print("Đơn giá [" + cu.getDonGiaNhap() + "]: ");
                String gia = sc.nextLine().trim();
                System.out.print("VAT (%) [" + cu.getThueSuatNhap() + "]: ");
                String vat = sc.nextLine().trim();
                if (capNhatChiTiet(stt - 1, sl.isEmpty() ? cu.getSoLuongNhap() : Integer.parseInt(sl),
                        gia.isEmpty() ? cu.getDonGiaNhap() : Double.parseDouble(gia),
                        vat.isEmpty() ? cu.getThueSuatNhap() : Double.parseDouble(vat))) {
                    System.out.println("[Thành công] Đã cập nhật sản phẩm nhập.");
                }
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("[Lỗi] Giá trị chưa hợp lệ: " + e.getMessage());
            }
        }
    }

    public void xoaSanPham(Scanner sc) {
        if (sc == null || !coTheChinhSua() || soLuongCT == 0) return;
        kiemTraHoaDon();
        int stt = Ktradulieu.luaChon(sc, "Chọn STT sản phẩm nhập cần xóa (0: Quay lại): ", 0, soLuongCT);
        if (stt != 0 && Ktradulieu.luaChon(sc, "Xác nhận xóa? (1: Có / 0: Không): ", 0, 1) == 1) {
            xoaChiTiet(stt - 1);
            System.out.println("[Thành công] Đã xóa sản phẩm nhập.");
        }
    }

    public boolean lapHoaDon(Scanner sc, Nguoidung nv, DSHoadon dsHD, DSNhaCungCap dsNCC,
                             DSSanPham dsSP, Nguoidung nguoiDung) {
        if (sc == null || nv == null || !coTheChinhSua()) return false;
        try {
            return nhapThongTin(sc, nv, dsHD, dsNCC, dsSP, nguoiDung)
                    && chinhSuaVaThanhToan(sc, nv, dsNCC, dsSP, nguoiDung);
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("[Thông báo] Đã dừng lập hóa đơn nhập, chưa lưu hóa đơn.");
            return false;
        }
    }

    public boolean chinhSuaVaThanhToan(Scanner sc, Nguoidung nv, DSNhaCungCap dsNCC,
                                      DSSanPham dsSP, Nguoidung nguoiDung) {
        if (sc == null || !coTheChinhSua()) return false;
        try {
            while (coTheChinhSua()) {
                System.out.println("\nSố dòng hàng: " + soLuongCT + " | Tạm tính: " + Ktradulieu.dinhDangTien(tinhThanhTien()));
                System.out.println("1. Thêm mặt hàng");
                System.out.println("2. Sửa số lượng, đơn giá, VAT");
                System.out.println("3. Xóa mặt hàng");
                System.out.println("4. Đổi nhà cung cấp");
                System.out.println("5. Sửa ghi chú");
                System.out.println("6. Thanh toán và in hóa đơn");
                System.out.println("7. Xem danh sách mặt hàng");
                System.out.println("0. Hủy lập hóa đơn");
                switch (Ktradulieu.luaChon(sc, "Chọn thao tác (0-7): ", 0, 7)) {
                    case 1: themSanPham(sc, dsSP, nguoiDung); break;
                    case 2: suaSanPham(sc); break;
                    case 3: xoaSanPham(sc); break;
                    case 4: chonNhaCungCap(sc, dsNCC, dsSP); break;
                    case 5: suaGhiChu(sc); break;
                    case 6:
                        if (thanhToanHoaDon(sc, nv, dsSP, dsNCC)) return true;
                        break;
                    case 7: kiemTraHoaDon(); break;
                    case 0:
                        huyHoaDon();
                        System.out.println("[Thông báo] Đã hủy lập hóa đơn nhập " + maHD + ".");
                        return false;
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("[Thông báo] Đã dừng chỉnh sửa, hóa đơn chưa được thanh toán.");
        }
        return false;
    }

    public boolean thanhToanHoaDon(Scanner sc, Nguoidung nv, DSSanPham dsSP, DSNhaCungCap dsNCC) {
        if (sc == null || !coTheChinhSua() || nv == null || !nv.coQuyen("QUYEN_NHAP_KHO")) return false;
        if (soLuongCT == 0) {
            System.out.println("[Lỗi] Hóa đơn nhập phải có ít nhất một mặt hàng!");
            return false;
        }
        if (dsNCC == null || dsNCC.timKiemTheoMa(maNCC) == null || dsNCC.timKiemTheoMa(maNCC).isDaXoa()) {
            System.out.println("[Lỗi] Nhà cung cấp không còn hợp lệ!");
            return false;
        }
        if (dsSP == null || !Double.isFinite(tinhThanhTien()) || !kiemTraNhaCungCap(dsSP, maNCC)) return false;
        for (int i = 0; i < soLuongCT; i++) {
            CTHDonNhap ct = dsChiTiet[i];
            SanPham sp = dsSP.timKiem(ct.getMaSP());
            if (sp == null) sp = ct.getSanPhamMoi();
            if (sp == null || sp.isDaXoa() || sp.getNsx() == null || sp.getHsd() == null) {
                System.out.println("[Lỗi] Mặt hàng " + ct.getMaSP() + " không hợp lệ. Hãy sửa trước khi thanh toán.");
                return false;
            }
            try { sp.tinhHsdKhiNhapHang(LocalDate.now()); }
            catch (IllegalArgumentException e) { System.out.println(e.getMessage()); return false; }
            long tongNhap = 0;
            for (int j = 0; j < soLuongCT; j++) {
                if (dsChiTiet[j].getMaSP().equalsIgnoreCase(ct.getMaSP())) tongNhap += dsChiTiet[j].getSoLuongNhap();
            }
            if (tongNhap + (sp.daHetHan(LocalDate.now()) ? 0 : sp.getSoLuongTon()) > Integer.MAX_VALUE) {
                System.out.println("[Lỗi] Tổng số lượng nhập vượt giới hạn kho cho " + ct.getMaSP() + ".");
                return false;
            }
        }
        try {
            kiemTraDuLieuFile();
            for (int i = 0; i < soLuongCT; i++) dsChiTiet[i].kiemTraDuLieuFile();
        } catch (IllegalArgumentException e) {
            System.out.println("[Lỗi] Hãy sửa hóa đơn trước khi thanh toán: " + e.getMessage());
            return false;
        }
        ThanhToan tt = chonVaThanhToan(sc, nv, true);
        if (tt == null) {
            System.out.println("[Thông báo] Chưa thanh toán. Bạn có thể tiếp tục sửa hóa đơn.");
            return false;
        }
        phuongThucThanhToan = tt;
        System.out.println("[Thành công] Đã thanh toán hóa đơn nhập [" + maHD + "]. Chờ người có quyền duyệt nhập kho.");
        System.out.println(inHoaDon());
        return true;
    }


    private void hienThiChiTietChoDuyet(DSSanPham dsSP) {
        System.out.println("\n========== CHI TIẾT HÓA ĐƠN " + this.maHD + " ==========");
        System.out.println("Nhà cung cấp: " + this.maNCC);
        System.out.println("Ngày lập: " + Ktradulieu.dinhDangNgay(this.ngayLap));
        BangConsole bang = new BangConsole("STT", "MÃ SP", "TÊN SP", "SL NHẬP", "TỒN HIỆN TẠI");
        for (int i = 0; i < soLuongCT; i++) {
            CTHDonNhap ct = dsChiTiet[i];
            SanPham sp = dsSP.timKiem(ct.getMaSP());
            bang.themDong(i + 1, ct.getMaSP(), ct.getTenSP(), ct.getSoLuongNhap(),
                    sp != null ? sp.getSoLuongTon() : 0);
        }
        System.out.println(bang);
    }

    public boolean duyetNhapKho(Scanner sc, DSSanPham dsSP, Nguoidung nguoiDuyet) {
        if (sc == null || dsSP == null) {
            System.out.println("[Lỗi] Scanner hoặc danh sách sản phẩm không hợp lệ!");
            return false;
        }
        if (nguoiDuyet == null || !nguoiDuyet.coQuyen("DUYET_HOA_DON")) {
            System.out.println("[Lỗi] Chỉ Quản lý hoặc Admin mới được duyệt hóa đơn nhập!");
            return false;
        }
        if (maNCC == null || !maNCC.matches("NCC\\d+")) {
            System.out.println("[Lỗi] Mã nhà cung cấp của phiếu không hợp lệ!");
            return false;
        }
        if (isDaXuLyThanhCong() || isDaHuy()) {
            System.out.println("[Thông báo] Hóa đơn nhập đã được duyệt hoặc đã hủy!");
            return false;
        }
        if (!kiemTraNhaCungCap(dsSP, maNCC)) return false;
        if (soLuongCT == 0) {
            System.out.println("[Lỗi] Hóa đơn chưa có sản phẩm!");
            return false;
        }

        LocalDate ngayNhap = LocalDate.now();
        // Kiểm tra toàn bộ phiếu trước khi thay đổi bất kỳ tồn kho nào.
        for (int i = 0; i < soLuongCT; i++) {
            CTHDonNhap ct = dsChiTiet[i];
            try {
                ct.kiemTraDuLieuFile();
            } catch (IllegalArgumentException e) {
                System.out.println("[Lỗi] " + e.getMessage());
                return false;
            }
            SanPham sp = dsSP.timKiem(ct.getMaSP());
            if (sp == null) sp = ct.getSanPhamMoi(); // Hồ sơ đi kèm ở file hóa đơn cũ.
            if (sp == null || sp.isDaXoa() || !ct.getMaSP().equalsIgnoreCase(sp.getMaSP())
                    || sp.getNsx() == null || sp.getHsd() == null) {
                System.out.println("[Lỗi] Sản phẩm hoặc hạn sử dụng của sản phẩm nhập không hợp lệ: " + ct.getMaSP());
                return false;
            }
            try { sp.tinhHsdKhiNhapHang(ngayNhap); }
            catch (IllegalArgumentException e) { System.out.println(e.getMessage()); return false; }
            long tongNhap = 0;
            for (int j = 0; j < soLuongCT; j++) {
                if (dsChiTiet[j].getMaSP().equalsIgnoreCase(ct.getMaSP())) {
                    tongNhap += dsChiTiet[j].getSoLuongNhap();
                }
            }
            SanPham trongKho = dsSP.timKiem(ct.getMaSP());
            int tonCu = trongKho != null && !trongKho.daHetHan(ngayNhap)
                    ? trongKho.getSoLuongTon() : 0;
            if (tongNhap + tonCu > Integer.MAX_VALUE) {
                System.out.println("[Lỗi] Tổng tồn kho vượt giới hạn: " + ct.getMaSP());
                return false;
            }
        }
        hienThiChiTietChoDuyet(dsSP);

        // Tương thích hồ sơ cũ: đưa vào danh mục với tồn 0, không duyệt riêng sản phẩm.
        for (int i = 0; i < soLuongCT; i++) {
            CTHDonNhap ct = dsChiTiet[i];
            if (dsSP.timKiem(ct.getMaSP()) == null) {
                SanPham sp = new CTHDonNhap(ct).getSanPhamMoi();
                sp.setSoLuongTon(0);
                if (!dsSP.themSP(sp)) return false;
            }
        }

        for (int i = 0; i < soLuongCT; i++) {
            CTHDonNhap ct = dsChiTiet[i];
            SanPham sp = dsSP.timKiem(ct.getMaSP());
            if (sp.getMaNCC().isEmpty()) sp.setMaNCC(maNCC);
            sp.nhapThemHang(ct.getSoLuongNhap(), ngayNhap);
            sp.setDonGiaNhap(ct.getDonGiaNhap());
        }
        xacNhanXuLy();
        System.out.println("[Thành công] Đã duyệt hóa đơn [" + maHD + "] và cộng hàng vào kho.");
        return true;
    }


    private boolean kiemTraNhaCungCap(DSSanPham dsSP, String ma) {
        for (int i = 0; i < soLuongCT; i++) {
            CTHDonNhap ct = dsChiTiet[i];
            SanPham sp = dsSP == null ? null : dsSP.timKiem(ct.getMaSP());
            if (sp == null) sp = ct.getSanPhamMoi();
            if (sp != null && !sp.getMaNCC().isEmpty() && !sp.getMaNCC().equalsIgnoreCase(ma)) {
                System.out.println("[Lỗi] Mặt hàng " + ct.getMaSP() + " thuộc nhà cung cấp " + sp.getMaNCC()
                        + ", không thể nhập từ " + ma + ".");
                return false;
            }
        }
        return true;
    }

    public boolean thayChiTiet(int viTri, CTHDonNhap moi) {
        if (!coTheChinhSua() || moi == null || viTri < 0 || viTri >= soLuongCT
                || !maHD.equalsIgnoreCase(moi.getMaHD())) return false;
        try { moi.kiemTraDuLieuFile(); }
        catch (IllegalArgumentException e) { return false; }
        for (int i = 0; i < soLuongCT; i++) {
            CTHDonNhap ct = dsChiTiet[i];
            if (i != viTri && ct.getMaSP().equalsIgnoreCase(moi.getMaSP())) return false;
        }
        dsChiTiet[viTri] = new CTHDonNhap(moi);
        return true;
    }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) {
        kiemTraQuyenChinhSua();
        if (maNCC == null || maNCC.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã nhà cung cấp không được để trống!");
        }
        this.maNCC = Ktradulieu.chuanHoaMa(maNCC);
    }

    /**
     * Dùng khi nạp hóa đơn từ file: khôi phục hình thức thanh toán đã lưu.
     * File cũ ghi phương thức khi đã trả tiền, kể cả khi còn chờ duyệt kho.
     */
    public void khoiPhucThanhToanTuFile(double soTienThanhToan, String loaiThanhToan) {
        if (!Double.isFinite(soTienThanhToan) || soTienThanhToan < 0 || loaiThanhToan == null
                || !("TienMat".equalsIgnoreCase(loaiThanhToan) || "ChuyenKhoan".equalsIgnoreCase(loaiThanhToan)
                || "CHUA_TT".equalsIgnoreCase(loaiThanhToan))) {
            throw new IllegalArgumentException("Dữ liệu thanh toán hóa đơn nhập không hợp lệ!");
        }
        ThanhToan tt = null;
        if ("TienMat".equalsIgnoreCase(loaiThanhToan)) {
            tt = new TienMat(this.maHD, soTienThanhToan, this.nguoiLap);
        } else if ("ChuyenKhoan".equalsIgnoreCase(loaiThanhToan)) {
            tt = new ChuyenKhoan(this.maHD, soTienThanhToan, this.nguoiLap);
        }
        if (tt != null) {
            tt.setChiTien(true);
            tt.setTrangThai(ThanhToan.THANH_CONG);
        }
        this.phuongThucThanhToan = tt;
    }

    public ThanhToan getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(ThanhToan phuongThucThanhToan) {
        kiemTraQuyenChinhSua();
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    @Override
    public String toFileString() {
        kiemTraDuLieuFile();
        String pt = (this.phuongThucThanhToan != null) ? this.phuongThucThanhToan.getLoaiThanhToan() : "CHUA_TT";
        String trangThaiTT = phuongThucThanhToan == null ? ThanhToan.CHO_THANH_TOAN : phuongThucThanhToan.getTrangThai();
        // Giữ 11 cột cũ, nối trạng thái thanh toán và 5 cột chi tiết ở cuối.
        return String.format(java.util.Locale.US, "HDNHAP;%s;%s;%s;%s;%s;%.0f;%.0f;%.0f;%s;%s;%s",
                this.maHD, Ktradulieu.dinhDangNgay(this.ngayLap), this.getMaNV(), this.maNCC, this.trangThai,
                this.tinhTongTien(), this.tinhTienThue(), this.tinhThanhTien(), pt,
                (this.ghiChu != null ? this.ghiChu : ""), trangThaiTT) + chiTietThanhToanToFile();
    }

    @Override
    public String inHoaDon() {
        String xuongDong = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append(xuongDong).append("===== HÓA ĐƠN NHẬP HÀNG =====").append(xuongDong);
        sb.append(BangConsole.thongTin(
                new String[]{"Mã hóa đơn nhập", "Ngày lập", "Nhân viên lập", "Nhà cung cấp", "Nhập kho", "Thanh toán", "Ghi chú"},
                this.maHD, Ktradulieu.dinhDangNgay(this.ngayLap),
                "[" + this.getMaNV() + "] " + (this.nguoiLap != null ? this.nguoiLap.getTenND() : ""),
                this.maNCC, isDaXuLyThanhCong() ? "Đã nhập kho" : (isDaHuy() ? "Đã hủy" : "Chờ duyệt nhập kho"),
                isDaThanhToan() ? "Đã thanh toán" : "Chưa thanh toán", this.ghiChu)).append(xuongDong);
        sb.append(BangConsole.tuDanhSach(
                Arrays.asList(this.dsChiTiet).subList(0, this.soLuongCT))).append(xuongDong);
        sb.append(new BangConsole("KHOẢN MỤC", "SỐ TIỀN").canPhai(1)
                .themDong("Tổng tiền hàng", Ktradulieu.dinhDangTien(this.tinhTongTien()))
                .themDong("Tiền thuế VAT", Ktradulieu.dinhDangTien(this.tinhTienThue()))
                .themDong("TỔNG THANH TOÁN", Ktradulieu.dinhDangTien(this.tinhThanhTien()))).append(xuongDong);
        if (this.phuongThucThanhToan != null) {
            sb.append("Hình thức TT: ").append(this.phuongThucThanhToan.getLoaiThanhToan())
                    .append(" (").append(this.phuongThucThanhToan.getTrangThai()).append(')').append(xuongDong);
            if (phuongThucThanhToan instanceof TienMat) {
                TienMat tm = (TienMat) phuongThucThanhToan;
                sb.append(BangConsole.thongTin(new String[]{"Cửa hàng chi", "NCC trả lại"},
                        Ktradulieu.dinhDangTien(tm.getTienKhachDua()), Ktradulieu.dinhDangTien(tm.getTienThoi())))
                        .append(xuongDong);
            } else if (phuongThucThanhToan instanceof ChuyenKhoan) {
                ChuyenKhoan ck = (ChuyenKhoan) phuongThucThanhToan;
                sb.append(BangConsole.thongTin(new String[]{"Ngân hàng", "Tài khoản nhận", "Người nhận", "Mã GD ngân hàng"},
                        ck.getTenNganHang(), ck.getSoTKChuyen(), ck.getTenNguoiChuyen(), ck.getMaGDNganHang()))
                        .append(xuongDong);
            }
        }
        return sb.toString();
    }
}
