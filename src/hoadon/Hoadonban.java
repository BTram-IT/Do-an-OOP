package hoadon;

import util.BangConsole;
import java.util.Arrays;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.NoSuchElementException;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import person.DsKhachHang;
import person.Khachhang;
import person.Nguoidung;
import sanpham.DSSanPham;
import sanpham.SanPham;
import thanhtoan.ChuyenKhoan;
import thanhtoan.ThanhToan;
import thanhtoan.TienMat;
import util.Ktradulieu;

public class Hoadonban extends HoaDon implements IHoaDon {

    private String maKH;
    private double diemDung;
    private double tienDungDiem;
    private CTHDBan[] dsChiTiet;
    private int soLuongCT;
    private ThanhToan phuongThucThanhToan;

    private static final int SUC_CHUA_MAC_DINH = 20;

    public Hoadonban() {
        super();
        this.maKH = "KHACH_LE";
        this.diemDung = 0;
        this.tienDungDiem = 0;
        this.dsChiTiet = new CTHDBan[SUC_CHUA_MAC_DINH];
        this.soLuongCT = 0;
        this.phuongThucThanhToan = null;
    }

    public Hoadonban(String maHD, Nguoidung nguoiLap, LocalDate ngayLap, String trangThai, String ghiChu, String maKH) {
        super(maHD, nguoiLap, ngayLap, trangThai, ghiChu);
        this.maKH = (maKH == null || maKH.trim().isEmpty()) ? "KHACH_LE" : Ktradulieu.chuanHoaMa(maKH);
        this.diemDung = 0;
        this.tienDungDiem = 0;
        this.dsChiTiet = new CTHDBan[SUC_CHUA_MAC_DINH];
        this.soLuongCT = 0;
        this.phuongThucThanhToan = null;
    }

    private void moRongBoNho() {
        int sucChuaMoi = this.dsChiTiet.length * 2;
        CTHDBan[] mangMoi = new CTHDBan[sucChuaMoi];
        for (int i = 0; i < this.soLuongCT; i++) {
            mangMoi[i] = this.dsChiTiet[i];
        }
        this.dsChiTiet = mangMoi;
    }

    public boolean themCTHD(CTHDBan ct, DSSanPham dsSP,
                            DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {

        if (!coTheChinhSua()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất, không thể thêm chi tiết!");
            return false;
        }

        if (ct == null || ct.getMaSP() == null
                || ct.getMaSP().trim().isEmpty()
                || ct.getSoLuongBan() <= 0 || !this.maHD.equalsIgnoreCase(ct.getMaHD())) {
            System.out.println("[Lỗi] Chi tiết bán hàng không hợp lệ!");
            return false;
        }

        if (dsSP == null) {
            System.out.println("[Lỗi] Danh sách sản phẩm không tồn tại!");
            return false;
        }

        SanPham sp = dsSP.timKiem(ct.getMaSP());

        if (sp == null) {
            System.out.println("[Lỗi] Không tìm thấy sản phẩm trong kho!");
            return false;
        }

        if (sp.isDaXoa()
                || sp.getHsd() == null || sp.getHsd().isBefore(LocalDate.now())) {
            System.out.println("[Lỗi] Sản phẩm đã hết hạn sử dụng!");
            return false;
        }

        // Nếu sản phẩm đã có trong hóa đơn thì cộng dồn số lượng mua.
        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDBan ctCu = this.dsChiTiet[i];

            if (ctCu.getMaSP().equalsIgnoreCase(ct.getMaSP())) {

                long slMoi = (long) ctCu.getSoLuongBan() + ct.getSoLuongBan();

                if (slMoi > Integer.MAX_VALUE) {
                    System.out.println("[Lỗi] Tổng số lượng vượt giới hạn!");
                    return false;
                }

                if (slMoi > sp.getSoLuongTon()) {
                    System.out.println("[Lỗi] Tổng số lượng mua vượt tồn kho!");
                    return false;
                }

                CTHDBan capNhat = new CTHDBan(ctCu);
                capNhat.setSoLuongBan((int) slMoi);

                // Tính lại khuyến mãi dựa trên tổng số lượng đã cộng dồn.
                capNhat.apDungKhuyenMai(dsKM, dsSPKM, sp);
                this.dsChiTiet[i] = capNhat;
                return true;
            }
        }

        // Sản phẩm chưa có trong hóa đơn.
        if (ct.getSoLuongBan() > sp.getSoLuongTon()) {
            System.out.println("[Lỗi] Số lượng bán vượt quá tồn kho!");
            return false;
        }

        if (this.soLuongCT >= this.dsChiTiet.length) {
            moRongBoNho();
        }

        // Tạo bản sao để tránh việc bên ngoài sửa trực tiếp chi tiết trong hóa đơn.
        CTHDBan ctMoi = new CTHDBan(ct);

        // Tính khuyến mãi cho dòng sản phẩm mới.
        ctMoi.apDungKhuyenMai(dsKM, dsSPKM, sp);

        this.dsChiTiet[this.soLuongCT++] = ctMoi;

        return true;
    }



    private boolean kiemTraTonKhoTruocThanhToan(DSSanPham dsSP) {
        if (dsSP == null) {
            System.out.println("[Lỗi] Danh sách sản phẩm không tồn tại!");
            return false;
        }
        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDBan ct = this.dsChiTiet[i];
            SanPham sp = dsSP.timKiem(ct.getMaSP());
            if (sp == null || sp.isDaXoa()
                    || sp.getHsd() == null || sp.getHsd().isBefore(LocalDate.now())) {
                System.out.println("[Lỗi] Sản phẩm không tồn tại hoặc đã hết hạn!");
                return false;
            }
            int tongSoLuong;
            try {
                tongSoLuong = ct.getTongSoLuongXuatKho();
            } catch (ArithmeticException e) {
                System.out.println("[Lỗi] Tổng số lượng xuất kho vượt giới hạn!");
                return false;
            }
            long total = 0;
            for (int j = 0; j < soLuongCT; j++) {
                if (dsChiTiet[j].getMaSP().equalsIgnoreCase(ct.getMaSP()))
                    total += (long) dsChiTiet[j].getSoLuongBan() + dsChiTiet[j].getSoLuongTang();
            }
            if (total > sp.getSoLuongTon()) {
                System.out.println("[Lỗi] Không đủ tồn kho cho sản phẩm " + ct.getMaSP() + " (Tồn: " + sp.getSoLuongTon() + ")");
                return false;
            }
        }
        return true;
    }

    public void suaSoLuongSanPham(Scanner sc, DSSanPham dsSP,
                                  DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {
        if (sc == null) { return; }

        if (!coTheChinhSua()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất, không thể chỉnh sửa!");
            return;
        }

        if (this.soLuongCT == 0) {
            System.out.println("[Thông báo] Hóa đơn chưa có sản phẩm!");
            return;
        }

        // Hiển thị sản phẩm hiện có
        kiemTraHoaDon();

        System.out.print("\nNhập mã sản phẩm cần sửa số lượng: ");
        String maSP = Ktradulieu.chuanHoaMa(sc.nextLine().trim());

        int viTri = -1;

        for (int i = 0; i < this.soLuongCT; i++) {
            if (this.dsChiTiet[i].getMaSP().equalsIgnoreCase(maSP)) {
                viTri = i;
                break;
            }
        }

        if (viTri == -1) {
            System.out.println("[Lỗi] Sản phẩm không có trong hóa đơn!");
            return;
        }

        CTHDBan ct = this.dsChiTiet[viTri];
        if (dsSP == null) return;
        SanPham sp = dsSP.timKiem(maSP);

        if (sp == null) {
            System.out.println("[Lỗi] Không tìm thấy sản phẩm trong kho!");
            return;
        }

        System.out.println("Sản phẩm: " + ct.getTenSP());
        System.out.println("Số lượng hiện tại: " + ct.getSoLuongBan());
        System.out.println("Tồn kho hiện tại: " + sp.getSoLuongTon());

        while (true) {
            long slMoi = Ktradulieu.nhapSoNguyen(
                    sc, "Nhập số lượng mới: ");

            if (slMoi <= 0 || slMoi > Integer.MAX_VALUE) {
                System.out.println("[Lỗi] Số lượng phải lớn hơn 0!");
                continue;
            }

            if (slMoi > sp.getSoLuongTon()) {
                System.out.println("[Lỗi] Kho chỉ còn "
                        + sp.getSoLuongTon() + " sản phẩm!");
                continue;
            }

            // Cập nhật số lượng mới
            CTHDBan capNhat = new CTHDBan(ct);
            capNhat.setSoLuongBan((int) slMoi);

            // Tính lại khuyến mãi theo số lượng mới
            capNhat.apDungKhuyenMai(dsKM, dsSPKM, sp);
            this.dsChiTiet[viTri] = capNhat;

            System.out.println("[Thành công] Đã cập nhật số lượng sản phẩm!");
            break;
        }
    }

    public void xoaSanPham(Scanner sc) {
        if (sc == null) { return; }

        if (!coTheChinhSua()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất, không thể chỉnh sửa!");
            return;
        }

        if (this.soLuongCT == 0) {
            System.out.println("[Thông báo] Hóa đơn chưa có sản phẩm!");
            return;
        }

        kiemTraHoaDon();

        System.out.print("\nNhập mã sản phẩm cần xóa: ");
        String maSP = Ktradulieu.chuanHoaMa(sc.nextLine().trim());

        int viTri = -1;

        for (int i = 0; i < this.soLuongCT; i++) {
            if (this.dsChiTiet[i].getMaSP().equalsIgnoreCase(maSP)) {
                viTri = i;
                break;
            }
        }

        if (viTri == -1) {
            System.out.println("[Lỗi] Không tìm thấy sản phẩm trong hóa đơn!");
            return;
        }

        System.out.print("Bạn có chắc muốn xóa sản phẩm này? (1: Có | 0: Không): ");
        String xacNhan = sc.nextLine().trim();

        if (!xacNhan.equals("1")) {
            System.out.println("Đã hủy thao tác xóa.");
            return;
        }

        // Dồn các phần tử phía sau lên trước
        for (int i = viTri; i < this.soLuongCT - 1; i++) {
            this.dsChiTiet[i] = this.dsChiTiet[i + 1];
        }

        this.dsChiTiet[this.soLuongCT - 1] = null;
        this.soLuongCT--;

        System.out.println("[Thành công] Đã xóa sản phẩm khỏi hóa đơn!");
    }

    public void kiemTraHoaDon() {

        System.out.println("\n========== GIỎ HÀNG ĐANG CHỌN ==========");
        System.out.println("Mã hóa đơn: " + this.maHD);
        System.out.println("Mã khách hàng: " + this.maKH);
        System.out.println("Ghi chú: " + this.ghiChu);

        if (this.soLuongCT == 0) {
            System.out.println("[Thông báo] Hóa đơn chưa có sản phẩm.");
            System.out.println("==========================================");
            return;
        }

        System.out.println("\nDANH SÁCH SẢN PHẨM:");

        System.out.println(BangConsole.tuDanhSach(Arrays.asList(this.dsChiTiet).subList(0, this.soLuongCT)));

        System.out.println("------------------------------------------");
        System.out.println("Số loại sản phẩm: " + this.soLuongCT);

        System.out.println("Tổng tiền hàng: "
                + Ktradulieu.dinhDangTien(this.tinhTongTien()));

        System.out.println("Tiền giảm khuyến mãi: -"
                + Ktradulieu.dinhDangTien(this.tinhTienGiamKhuyenMai()));

        System.out.println("Thuế VAT: +"
                + Ktradulieu.dinhDangTien(this.tinhTienThue()));

        System.out.println("Điểm thưởng sử dụng: -"
                + Ktradulieu.dinhDangTien(this.tienDungDiem));

        System.out.println("------------------------------------------");

        System.out.println("TỔNG TIỀN: "
                + Ktradulieu.dinhDangTien(this.tinhThanhTien()));

        System.out.println("==========================================");
    }
    public CTHDBan[] getDsChiTiet() {
        CTHDBan[] ketQua = new CTHDBan[this.soLuongCT];
        for (int i = 0; i < this.soLuongCT; i++) {
            ketQua[i] = new CTHDBan(this.dsChiTiet[i]);
        }
        return ketQua;
    }

    public int getSoLuongCT() { return this.soLuongCT; }

    @Override
    public String getmaHoaDon() { return this.maHD; }

    @Override
    public void setmaHoaDon() {}

    @Override
    public void thanhTien() {
        System.out.println("Tổng thành tiền thanh toán: " + Ktradulieu.dinhDangTien(tinhThanhTien()));
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

    public double tinhTienGiamKhuyenMai() {
        double tongGiam = 0;
        for (int i = 0; i < this.soLuongCT; i++) {
            tongGiam += this.dsChiTiet[i].tinhTienGiamHieuLuc();
        }
        return tongGiam;
    }

    public double tinhTienThue() {
        double tongThue = 0;
        for (int i = 0; i < this.soLuongCT; i++) {
            tongThue += this.dsChiTiet[i].tinhTienThue();
        }
        return tongThue;
    }

    public double getThanhTien() { return this.tinhThanhTien(); }

    @Override
    public double tinhThanhTien() {
        double tong = tinhTongTien() - tinhTienGiamKhuyenMai() + tinhTienThue() - this.tienDungDiem;
        return Math.max(0, tong);
    }

    @Override
    public String getLoaiHD() { return "Hóa đơn bán"; }

    @Override
    protected String layMaDoiTac() { return this.maKH; }

    public boolean nhapThongTin(Scanner sc, Nguoidung nhanVienDangNhap,
                                DSHoadon dsHD, DsKhachHang dsKH, DSSanPham dsSP,
                                DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {

        if (sc == null) throw new IllegalArgumentException("Scanner không được null!");

        // Chỉ khởi tạo thông tin hóa đơn
        super.nhapThongTin(sc, nhanVienDangNhap, dsHD);

        // Mặc định là khách lẻ
        setMaKH("KHACH_LE");

        this.diemDung = 0;
        this.tienDungDiem = 0;
        this.soLuongCT = 0;
        this.phuongThucThanhToan = null;

        return true;
    }

    /** Nhập hàng, xem lại nhiều lần và chỉ trả về true sau khi thanh toán xong. */
    public boolean lapHoaDon(Scanner sc, Nguoidung nv, DSHoadon dsHD, DsKhachHang dsKH,
                             DSSanPham dsSP, DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {
        if (sc == null || nv == null || !coTheChinhSua()) return false;
        try {
            if (!nhapThongTin(sc, nv, dsHD, dsKH, dsSP, dsKM, dsSPKM)
                    || !tiepNhanKhachHang(sc, dsKH)) return false;
            do {
                themSanPham(sc, dsSP, dsKM, dsSPKM);
            } while (Ktradulieu.luaChon(sc, "Thêm sản phẩm tiếp? (1: Có / 0: Xem lại hóa đơn): ", 0, 1) == 1);
            return chinhSuaVaThanhToan(sc, nv, dsKH, dsSP, dsKM, dsSPKM);
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("[Thông báo] Đã dừng lập hóa đơn bán, chưa lưu hóa đơn.");
            return false;
        }
    }

    public boolean chinhSuaVaThanhToan(Scanner sc, Nguoidung nv, DsKhachHang dsKH,
                                      DSSanPham dsSP, DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {
        if (sc == null || !coTheChinhSua()) return false;
        try {
            while (coTheChinhSua()) {
                System.out.println("\nSố dòng hàng: " + soLuongCT + " | Tạm tính: " + Ktradulieu.dinhDangTien(tinhThanhTien()));
                System.out.println("1. Thêm sản phẩm");
                System.out.println("2. Sửa số lượng sản phẩm");
                System.out.println("3. Xóa sản phẩm");
                System.out.println("4. Đổi khách hàng");
                System.out.println("5. Sửa ghi chú");
                System.out.println("6. Thanh toán và in hóa đơn");
                System.out.println("7. Xem danh sách mặt hàng");
                System.out.println("0. Hủy lập hóa đơn");
                int chon = Ktradulieu.luaChon(sc, "Chọn thao tác (0-7): ", 0, 7);
                switch (chon) {
                    case 1: themSanPham(sc, dsSP, dsKM, dsSPKM); break;
                    case 2: suaSoLuongSanPham(sc, dsSP, dsKM, dsSPKM); break;
                    case 3: xoaSanPham(sc); break;
                    case 4: tiepNhanKhachHang(sc, dsKH); break;
                    case 5: suaGhiChu(sc); break;
                    case 6:
                        if (thanhToanHoaDon(sc, nv, dsKH, dsSP)) return true;
                        break;
                    case 7: kiemTraHoaDon(); break;
                    case 0:
                        huyHoaDon();
                        System.out.println("[Thông báo] Đã hủy lập hóa đơn bán " + maHD + ".");
                        return false;
                }
            }
        } catch (NoSuchElementException | IllegalStateException e) {
            System.out.println("[Thông báo] Đã dừng chỉnh sửa, hóa đơn chưa được thanh toán.");
        }
        return false;
    }

    public boolean tiepNhanKhachHang(Scanner sc, DsKhachHang dsKH) {
        if (sc == null) { return false; }
        if (!coTheChinhSua()) return false;


        System.out.println("\n--- TIẾP NHẬN KHÁCH HÀNG ---");
        System.out.println("1. Khách hàng thành viên");
        System.out.println("0. Khách lẻ");

        int chon = Ktradulieu.luaChon(sc, "Chọn loại khách hàng: ", 0, 1);

        if (chon == 0) {
            setMaKH("KHACH_LE");
            System.out.println("-> Đã chọn khách hàng lẻ.");
            return true;
        }

        if (dsKH == null) {
            System.out.println("[Lỗi] Danh sách khách hàng chưa được khởi tạo!");
            return false;
        }

        while (true) {
            System.out.print("Nhập mã khách hàng: ");

            String maChuan = Ktradulieu.chuanHoaMa(sc.nextLine().trim());

            Khachhang khachHang = dsKH.timKiem(maChuan);

            if (khachHang == null || khachHang.isDaXoa()) {
                System.out.println("[Lỗi] Không tìm thấy khách hàng! Vui lòng nhập lại.");
                continue;
            }

            setMaKH(khachHang.getMaKH());

            System.out.println("-> Khách hàng: " + khachHang.getMaKH()
                    + " - " + khachHang.getTenKH());

            System.out.println("-> Điểm tích lũy: "
                    + (int) khachHang.getDiemTichLuy());

            return true;
        }
    }

    public boolean themSanPham(Scanner sc, DSSanPham dsSP,
                               DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {
        if (sc == null) { return false; }


        if (!coTheChinhSua()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất!");
            return false;
        }

        System.out.println("\n--- THÊM SẢN PHẨM VÀO HÓA ĐƠN ---");

        CTHDBan ct = new CTHDBan();

        if (!ct.nhapThongTin(sc, this.maHD, dsSP, dsKM, dsSPKM)) {
            System.out.println("[Thông báo] Không thêm được sản phẩm.");
            return false;
        }

        if (this.themCTHD(ct, dsSP, dsKM, dsSPKM)) {
            System.out.println("[Thành công] Đã thêm sản phẩm vào hóa đơn.");
            return true;
        }

        return false;
    }

    public boolean thanhToanHoaDon(Scanner sc, Nguoidung nhanVienDangNhap,
                                   DsKhachHang dsKH, DSSanPham dsSP) {
        if (sc == null) { return false; }

        if (!coTheChinhSua()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất!");
            return false;
        }

        if (this.soLuongCT == 0) {
            System.out.println("[Lỗi] Hóa đơn phải có ít nhất một sản phẩm!");
            return false;
        }

        try {
            kiemTraDuLieuFile();
            for (int i = 0; i < soLuongCT; i++) dsChiTiet[i].kiemTraDuLieuFile();
        } catch (IllegalArgumentException e) {
            System.out.println("[Lỗi] Hãy sửa hóa đơn trước khi thanh toán: " + e.getMessage());
            return false;
        }

        // Kiểm tra tồn kho trước khi thanh toán
        if (!kiemTraTonKhoTruocThanhToan(dsSP)) {
            System.out.println("[Lỗi] Tồn kho không hợp lệ!");
            return false;
        }

        if (nhanVienDangNhap == null || !nhanVienDangNhap.coQuyen("QUYEN_BAN_HANG")
                || nhanVienDangNhap.getMaND() == null || nhanVienDangNhap.getMaND().isBlank()) return false;
        Khachhang khachHang = null;

        if (!"KHACH_LE".equals(this.maKH) && dsKH != null) {
            khachHang = dsKH.timKiem(this.maKH);
        }

        // Sử dụng điểm thưởng
        this.diemDung = 0;
        this.tienDungDiem = 0;

        if (!"KHACH_LE".equals(maKH) && (khachHang == null || khachHang.isDaXoa())) return false;
        if (khachHang != null && khachHang.getDiemTichLuy() > 0) {

            System.out.println("\nKhách có "
                    + (int) khachHang.getDiemTichLuy()
                    + " điểm.");

            System.out.print("Dùng điểm thưởng trừ tiền? (1: Có | 0: Không): ");

            if (sc.nextLine().trim().equals("1")) {

                double tienTruocKhiDungDiem =
                        this.tinhTongTien()
                                - this.tinhTienGiamKhuyenMai()
                                + this.tinhTienThue();

                int diemToiDaDuocDung = (int) Math.min(
                        khachHang.getDiemTichLuy(),
                        Math.floor(Math.max(0, tienTruocKhiDungDiem) / 1000)
                );

                if (diemToiDaDuocDung > 0) {

                    while (true) {
                        int diemMuonDung = (int) Ktradulieu.nhapSoNguyen(
                                sc, "Nhập số điểm muốn sử dụng: ");

                        if (diemMuonDung <= 0
                                || diemMuonDung > diemToiDaDuocDung) {

                            System.out.println("[Lỗi] Chỉ được sử dụng từ 1 đến "
                                    + diemToiDaDuocDung + " điểm!");
                            continue;
                        }

                        this.diemDung = diemMuonDung;
                        this.tienDungDiem = diemMuonDung * 1000.0;

                        break;
                    }
                } else {
                    System.out.println("[Thông báo] Không thể sử dụng điểm cho hóa đơn này.");
                }
            }
        }

        // Tổng kết hóa đơn
        double phaiTra = this.tinhThanhTien();
        if (!Double.isFinite(phaiTra)) return false;
        if (khachHang != null) {
            long remaining = (long) khachHang.getDiemTichLuy() - (long) diemDung;
            if (remaining < 0 || Math.floor(phaiTra / 100000) > Integer.MAX_VALUE - remaining) return false;
        }

        System.out.println("\n========== THÔNG TIN THANH TOÁN ==========");
        System.out.println("Tổng tiền hàng  : "
                + Ktradulieu.dinhDangTien(this.tinhTongTien()));

        System.out.println("Giảm khuyến mãi : -"
                + Ktradulieu.dinhDangTien(this.tinhTienGiamKhuyenMai()));

        System.out.println("Thuế VAT        : +"
                + Ktradulieu.dinhDangTien(this.tinhTienThue()));

        System.out.println("Trừ điểm thưởng : -"
                + Ktradulieu.dinhDangTien(this.tienDungDiem));

        System.out.println("------------------------------------------");
        System.out.println("TỔNG PHẢI TRẢ   : "
                + Ktradulieu.dinhDangTien(phaiTra));

        ThanhToan tt = chonVaThanhToan(sc, nhanVienDangNhap, false);
        if (tt == null) {
            this.diemDung = 0;
            this.tienDungDiem = 0;
            System.out.println("[Thông báo] Chưa thanh toán. Bạn có thể tiếp tục sửa hóa đơn.");
            return false;
        }
        this.phuongThucThanhToan = tt;

        // Thanh toán thành công mới hoàn tất hóa đơn
        return hoanTatDonHang(dsSP, khachHang);
    }

    private boolean hoanTatDonHang(DSSanPham dsSP, Khachhang khachHang) {

        if (this.isDaXuLyThanhCong() || isDaHuy()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất!");
            return false;
        }

        if (!kiemTraTonKhoTruocThanhToan(dsSP)) {
            System.out.println("[Lỗi] Tồn kho không hợp lệ khi hoàn tất!");
            return false;
        }

        // Trừ tồn kho
        for (int i = 0; i < this.soLuongCT; i++) {

            CTHDBan ct = this.dsChiTiet[i];

            SanPham sp = dsSP.timKiem(ct.getMaSP());

            int tongSoLuong = ct.getTongSoLuongXuatKho();

            sp.truTonKho(tongSoLuong);

            System.out.println("[Trừ kho] " + ct.getMaSP()
                    + " | Mua: " + ct.getSoLuongBan()
                    + " | Tặng: " + ct.getSoLuongTang());
        }

        // Cập nhật điểm khách hàng
        if (khachHang != null) {

            if (this.diemDung > 0) {
                if (!khachHang.truDiem((int) this.diemDung)) throw new IllegalStateException("Insufficient loyalty points");
            }

            int diemCongMoi = (int) (this.tinhThanhTien() / 100000);

            if (diemCongMoi > 0) {
                khachHang.congDiem(diemCongMoi);

                System.out.println("[Tích điểm] Cộng thêm: +"
                        + diemCongMoi + " điểm.");
            }

            System.out.println("[Tài khoản KH] Điểm hiện tại: "
                    + (int) khachHang.getDiemTichLuy());
        }

        this.xacNhanXuLy();

        System.out.println("[Thành công] ĐÃ HOÀN TẤT ĐƠN HÀNG BÁN LẺ ["
                + this.maHD + "]");

        // In hóa đơn cho khách hàng
        System.out.println(inHoaDon());

        return true;
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) {
        kiemTraQuyenChinhSua();
        this.maKH = (maKH == null || maKH.trim().isEmpty()) ? "KHACH_LE" : Ktradulieu.chuanHoaMa(maKH);
        this.diemDung = 0;
        this.tienDungDiem = 0;
    }

    /**
     * Dùng khi nạp hóa đơn từ file: thêm chi tiết đã lưu, không tính lại khuyến mãi
     * và không trừ tồn kho (tồn kho đã được trừ và lưu từ lúc bán).
     */
    public boolean themCTHDTuFile(CTHDBan ct) {
        if (ct == null || !this.maHD.equalsIgnoreCase(ct.getMaHD())) {
            return false;
        }
        for (int i = 0; i < this.soLuongCT; i++) {
            if (dsChiTiet[i].getMaSP().equalsIgnoreCase(ct.getMaSP())) return false;
        }
        if (this.soLuongCT >= this.dsChiTiet.length) {
            moRongBoNho();
        }
        CTHDBan banSao = new CTHDBan(ct);
        this.dsChiTiet[this.soLuongCT++] = banSao;
        return true;
    }

    /**
     * Dùng khi nạp hóa đơn từ file: khôi phục điểm đã dùng và phương thức thanh toán.
     * Các cột tiền khách đưa và thông tin ngân hàng (nếu có) được khôi phục riêng sau bước này.
     */
    public void khoiPhucThanhToanTuFile(double tienDungDiem, double soTienThanhToan, String loaiThanhToan) {
        if (!Double.isFinite(tienDungDiem) || tienDungDiem < 0 || !Double.isFinite(soTienThanhToan) || soTienThanhToan < 0) throw new IllegalArgumentException("Invalid payment values");
        if (loaiThanhToan == null || !("TienMat".equalsIgnoreCase(loaiThanhToan)
                || "ChuyenKhoan".equalsIgnoreCase(loaiThanhToan) || "CHUA_TT".equalsIgnoreCase(loaiThanhToan))) {
            throw new IllegalArgumentException("Phương thức thanh toán trong file không hợp lệ!");
        }
        this.tienDungDiem = tienDungDiem;
        this.diemDung = this.tienDungDiem / 1000;

        ThanhToan tt = null;
        if ("TienMat".equalsIgnoreCase(loaiThanhToan)) {
            tt = new TienMat(this.maHD, soTienThanhToan, this.nguoiLap);
        } else if ("ChuyenKhoan".equalsIgnoreCase(loaiThanhToan)) {
            tt = new ChuyenKhoan(this.maHD, soTienThanhToan, this.nguoiLap);
        }
        if (tt != null && this.isDaXuLyThanhCong()) {
            tt.setTrangThai(ThanhToan.THANH_CONG);
        }
        this.phuongThucThanhToan = tt;
    }

    public double getDiemDung() { return diemDung; }
    public double getTienDungDiem() { return tienDungDiem; }
    public ThanhToan getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(ThanhToan phuongThucThanhToan) {
        kiemTraQuyenChinhSua();
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public boolean themCTHDBanTuFile(CTHDBan ct) {
        return themCTHDTuFile(ct);
    }
    @Override
    public String toFileString() {
        kiemTraDuLieuFile();
        String pt = (this.phuongThucThanhToan != null) ? this.phuongThucThanhToan.getLoaiThanhToan() : "CHUA_TT";
        // Các trường mở rộng (thêm vào CUỐI dòng để file cũ 13 trường vẫn đọc được):
        // tiền khách đưa; mã GD ngân hàng; tên người chuyển; số TK; tên ngân hàng
        String moRong = ";0;;;;";
        if (this.phuongThucThanhToan instanceof TienMat) {
            moRong = String.format(";%.0f;;;;", ((TienMat) this.phuongThucThanhToan).getTienKhachDua());
        } else if (this.phuongThucThanhToan instanceof ChuyenKhoan) {
            ChuyenKhoan ck = (ChuyenKhoan) this.phuongThucThanhToan;
            moRong = ";0;" + lamSachTruongFile(ck.getMaGDNganHang()) + ";" + lamSachTruongFile(ck.getTenNguoiChuyen())
                    + ";" + lamSachTruongFile(ck.getSoTKChuyen()) + ";" + lamSachTruongFile(ck.getTenNganHang());
        }
        return String.format("HDBAN;%s;%s;%s;%s;%s;%.0f;%.0f;%.0f;%.0f;%.0f;%s;%s",
                this.maHD, Ktradulieu.dinhDangNgay(this.ngayLap), this.getMaNV(), this.maKH, this.trangThai,
                this.tinhTongTien(), this.tinhTienGiamKhuyenMai(), this.tinhTienThue(),
                this.tienDungDiem, this.tinhThanhTien(), pt, (this.ghiChu != null ? this.ghiChu : "")) + moRong;
    }

    private static String lamSachTruongFile(String giaTri) {
        return giaTri == null ? "" : giaTri.replace(';', ',').trim();
    }

    @Override
    public String inHoaDon() {
        String xuongDong = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append(xuongDong).append("===== PHIẾU THANH TOÁN =====").append(xuongDong);
        sb.append("CỬA HÀNG TIỆN LỢI 24/7").append(xuongDong);
        sb.append(BangConsole.thongTin(
                new String[]{"Mã hóa đơn", "Ngày lập", "Thu ngân", "Khách hàng", "Trạng thái", "Ghi chú"},
                this.maHD, Ktradulieu.dinhDangNgay(this.ngayLap),
                "[" + this.getMaNV() + "] " + (this.nguoiLap != null ? this.nguoiLap.getTenND() : ""),
                this.maKH, this.trangThai, this.ghiChu)).append(xuongDong);
        sb.append(BangConsole.tuDanhSach(
                Arrays.asList(this.dsChiTiet).subList(0, this.soLuongCT))).append(xuongDong);

        BangConsole tongKet = new BangConsole("KHOẢN MỤC", "SỐ TIỀN").canPhai(1);
        tongKet.themDong("Tổng tiền hàng", Ktradulieu.dinhDangTien(this.tinhTongTien()));
        if (this.tinhTienGiamKhuyenMai() > 0) {
            tongKet.themDong("Giảm khuyến mãi", "-" + Ktradulieu.dinhDangTien(this.tinhTienGiamKhuyenMai()));
        }
        tongKet.themDong("Tiền thuế VAT", "+" + Ktradulieu.dinhDangTien(this.tinhTienThue()));
        if (this.tienDungDiem > 0) {
            tongKet.themDong("Trừ điểm thưởng", "-" + Ktradulieu.dinhDangTien(this.tienDungDiem)
                    + " (" + (int) this.diemDung + " điểm)");
        }
        tongKet.themDong("TỔNG THANH TOÁN", Ktradulieu.dinhDangTien(this.tinhThanhTien()));
        sb.append(tongKet).append(xuongDong);
        if (this.phuongThucThanhToan != null) {
            sb.append("Phương thức TT: ").append(this.phuongThucThanhToan.getLoaiThanhToan())
                    .append(" (").append(this.phuongThucThanhToan.getTrangThai()).append(')').append(xuongDong);
            if (this.phuongThucThanhToan instanceof TienMat) {
                TienMat tm = (TienMat) this.phuongThucThanhToan;
                if (tm.getTienKhachDua() > 0) {
                    sb.append(BangConsole.thongTin(new String[]{"Khách đưa", "Tiền thối"},
                            Ktradulieu.dinhDangTien(tm.getTienKhachDua()), Ktradulieu.dinhDangTien(tm.getTienThoi())))
                            .append(xuongDong);
                }
            } else if (this.phuongThucThanhToan instanceof ChuyenKhoan) {
                ChuyenKhoan ck = (ChuyenKhoan) this.phuongThucThanhToan;
                sb.append(BangConsole.thongTin(new String[]{"Ngân hàng", "Mã GD ngân hàng"},
                        ck.getTenNganHang(), ck.getMaGDNganHang())).append(xuongDong);
            }
        }
        sb.append("===== CẢM ƠN QUÝ KHÁCH VÀ HẸN GẶP LẠI =====").append(xuongDong);
        return sb.toString();
    }
}
