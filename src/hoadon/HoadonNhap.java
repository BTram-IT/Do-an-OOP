package hoadon;

import java.util.Scanner;
import java.util.Objects;

import doitac.DSNhaCungCap;
import person.NhanVien;
import person.Nguoidung;
import person.QuanLyCuaHang;
import person.Giamdoc;
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

    public HoadonNhap(String maHD, NhanVien nguoiLap, String ngayLap, String trangThai, String ghiChu, String maNCC) {
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
        if (this.isDaXuLyThanhCong()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất, không thể thêm chi tiết!");
            return false;
        }
        if (ct == null || ct.getMaSP() == null || ct.getMaSP().trim().isEmpty() || ct.getSoLuongNhap() <= 0 || ct.getDonGiaNhap() <= 0) {
            System.out.println("[Lỗi] Chi tiết nhập hàng không hợp lệ!");
            return false;
        }

        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDonNhap ctCu = this.dsChiTiet[i];
            boolean cungMaSP = ctCu.getMaSP().equalsIgnoreCase(ct.getMaSP());
            boolean cungDonGia = Double.compare(ctCu.getDonGiaNhap(), ct.getDonGiaNhap()) == 0;
            boolean cungHSD = Objects.equals(ctCu.getHanSuDung(), ct.getHanSuDung());

            // Nếu cùng mã sản phẩm
            if (cungMaSP) {

                SanPham spCu = ctCu.getSanPhamMoi();
                SanPham spMoi = ct.getSanPhamMoi();

                // Không cho hai hồ sơ sản phẩm mới trùng mã
                if (spCu != null && spMoi != null) {
                    System.out.println(
                            "[Lỗi] Sản phẩm mới đã tồn tại trong phiếu nhập!"
                    );
                    return false;
                }

                // Chỉ gộp khi đơn giá và hạn sử dụng giống nhau
                if (cungDonGia && cungHSD) {

                    // Không gộp sản phẩm đã tồn tại với sản phẩm mới
                    if ((spCu == null) != (spMoi == null)) {
                        System.out.println(
                                "[Lỗi] Không thể gộp sản phẩm đã tồn tại với hồ sơ sản phẩm mới!"
                        );
                        return false;
                    }
                    // Kiểm tra mã hồ sơ sản phẩm mới
                    if (spCu != null && spMoi != null
                            && !spCu.getMaSP().equalsIgnoreCase(spMoi.getMaSP())) {
                        System.out.println("[Lỗi] Hồ sơ sản phẩm mới không khớp!");
                        return false;
                    }

                    long slMoi = (long) ctCu.getSoLuongNhap()
                            + ct.getSoLuongNhap();

                    if (slMoi > Integer.MAX_VALUE) {
                        System.out.println(
                                "[Lỗi] Tổng số lượng nhập vượt giới hạn!"
                        );
                        return false;
                    }

                    ctCu.setSoLuongNhap((int) slMoi);

                    System.out.println(
                            "[Thông báo] Đã cộng dồn sản phẩm ["
                                    + ct.getMaSP() + "]. Số lượng mới: " + slMoi
                    );

                    return true;
                }
            }
        }

        if (this.soLuongCT >= this.dsChiTiet.length) {
            moRongBoNho();
        }
        this.dsChiTiet[this.soLuongCT++] = new CTHDonNhap(ct);
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
        if (ct == null) {
            return false;
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

    public void nhapThongTin(Scanner sc, NhanVien nhanVienDangNhap, DSHoadon dsHD, DSNhaCungCap dsNCC, DSSanPham dsSP, Nguoidung nguoiDungDangNhap) {
        if (sc == null) sc = new Scanner(System.in);
        super.nhapThongTin(sc, nhanVienDangNhap, dsHD);

        System.out.println("\n--- THÔNG TIN ĐỐI TÁC NHÀ CUNG CẤP ---");
        while (true) {
            System.out.print("Nhập mã nhà cung cấp (Ví dụ NCC001): ");
            String inputNCC = sc.nextLine().trim();

            if (inputNCC.equals("/")) {
                String maNCCDauTien = null;
                if (dsNCC != null) {
                    for (doitac.NhaCungCap n : dsNCC.getDanhSach()) {
                        if (!n.isDaXoa()) {
                            maNCCDauTien = n.getMaNCC();
                            break;
                        }
                    }
                }
                if (maNCCDauTien != null) {
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
                    doitac.NhaCungCap nccChon = dsNCC.timKiem(maChuan);
                    if (nccChon == null) {
                        System.out.println("[Lỗi] Nhà cung cấp không tồn tại!");
                        continue;
                    }
                    if (nccChon.isDaXoa()) {
                        System.out.println("[Lỗi] Nhà cung cấp này đã ngừng hợp tác, không thể lập hóa đơn nhập!");
                        continue;
                    }
                }
                this.setMaNCC(maChuan);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        while (true) {
            CTHDonNhap ct = new CTHDonNhap();
            if (ct.nhapThongTin(sc, this.maHD, dsSP, nguoiDungDangNhap)) {
                if (this.themCTHD(ct)) {
                    System.out.println("[Thành công] Đã thêm mặt hàng vào phiếu nhập.");
                }
            }
            System.out.print("Bạn có muốn nhập tiếp không? (1: Có / 0: Hoàn tất): ");
            if (!sc.nextLine().trim().equals("1")) {
                if (this.soLuongCT == 0) {
                    System.out.println("[Lỗi] Hóa đơn nhập phải có ít nhất 1 mặt hàng!");
                    continue;
                }
                break;
            }
        }

        double tongTien = this.tinhThanhTien();

        System.out.println("\n========== THANH TOÁN CHO NHÀ CUNG CẤP ==========");
        System.out.println("• Mã hóa đơn nhập       : [" + this.maHD + "]");
        System.out.println("• Mã nhà cung cấp       : [" + this.maNCC + "]");
        System.out.println("• Nhân viên phụ trách   : [" + nhanVienDangNhap.getMaND() + "]" +nhanVienDangNhap.getTenND());
        System.out.println("• Tổng tiền cửa hàng phải trả: "
                + Ktradulieu.dinhDangTien(tongTien));

        System.out.println("\nChọn hình thức thanh toán:");
        System.out.println("1. Tiền mặt");
        System.out.println("2. Chuyển khoản");

        int chonTT;

        while (true) {
            chonTT = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập lựa chọn (1-2): ");

            if (chonTT == 1 || chonTT == 2) {
                break;
            }

            System.out.println("[Lỗi] Chỉ được chọn 1 hoặc 2!");
        }

        if (chonTT == 1) {

            // Cửa hàng chi tiền mặt cho nhà cung cấp
            System.out.println("\n========== CHI TIỀN MẶT ==========");
            System.out.println("Cửa hàng chi tiền cho nhà cung cấp.");
            System.out.println("Số tiền chi: "
                    + Ktradulieu.dinhDangTien(tongTien));

            System.out.print("Xác nhận đã chi tiền? (1: Có / 0: Không): ");
            int xacNhan = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập lựa chọn: ");

            if (xacNhan != 1) {
                System.out.println("[Thông báo] Chưa xác nhận thanh toán.");
                return;
            }

            TienMat tm = new TienMat(this.maHD, tongTien, nhanVienDangNhap);

            // Ghi nhận số tiền cửa hàng đã chi
            tm.setTienKhachDua(tongTien);

            if (tm.qTrinhThanhToan()) {
                this.phuongThucThanhToan = tm;
            } else {
                System.out.println("[Lỗi] Không thể ghi nhận thanh toán!");
                return;
            }

        } else {

            // Cửa hàng chuyển khoản cho nhà cung cấp
            System.out.println("\n========== CHUYỂN KHOẢN CHO NHÀ CUNG CẤP ==========");

            ChuyenKhoan ck = new ChuyenKhoan(
                    this.maHD,
                    tongTien,
                    nhanVienDangNhap
            );

            ck.nhap(sc);

            this.phuongThucThanhToan = ck;
        }

        this.setTrangThai("DangXuLy");

        System.out.println("[Thành công] Đã lập phiếu nhập kho [" + this.maHD
                + "] (Trạng thái: Đang xử lý).");

    }

    private boolean coQuyen(Nguoidung nguoiDung, String maQuyen) {
        if (nguoiDung == null || nguoiDung.isDaXoa()) return false;
        String[] quyenHan = nguoiDung.getQuyenHan();
        if (quyenHan == null) return false;

        for (String quyen : quyenHan) {
            if (quyen != null && maQuyen.equalsIgnoreCase(quyen.trim())) return true;
        }
        return false;
    }

    private void hienThiChiTietChoDuyet(DSSanPham dsSP) {
        System.out.println("\n========== CHI TIẾT HÓA ĐƠN " + this.maHD + " ==========");
        System.out.println("Nhà cung cấp: " + this.maNCC);
        System.out.println("Ngày lập: " + this.ngayLap);
        System.out.println("----------------------------------------");

        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDonNhap ct = this.dsChiTiet[i];
            SanPham spTrongKho = dsSP.timKiem(ct.getMaSP());

            System.out.println("\nMã sản phẩm: " + ct.getMaSP());
            System.out.println("Tên sản phẩm: " + ct.getTenSP());
            System.out.println("Số lượng nhập: " + ct.getSoLuongNhap());

            if (spTrongKho != null && ct.getSanPhamMoi() == null) {
                System.out.println("Trạng thái: Đã tồn tại");
            } else if (ct.getSanPhamMoi() != null) {
                System.out.println("Trạng thái: Sản phẩm mới - Chờ duyệt");
            } else {
                System.out.println("Trạng thái: Không tìm thấy sản phẩm");
            }
        }
        System.out.println("\n========== KẾT THÚC CHI TIẾT HÓA ĐƠN ==========");
    }

    public boolean duyetNhapKho(Scanner sc, DSSanPham dsSP, Nguoidung nguoiDuyet) {

        // 1. Kiểm tra dữ liệu đầu vào
        if (sc == null || dsSP == null) {
            System.out.println("[Lỗi] Scanner hoặc danh sách sản phẩm không hợp lệ!");
            return false;
        }

        // 2. Kiểm tra người duyệt
        if (nguoiDuyet == null || nguoiDuyet.isDaXoa()
                || (!(nguoiDuyet instanceof QuanLyCuaHang)
                && !(nguoiDuyet instanceof Giamdoc))) {
            System.out.println("[Lỗi] Chỉ Quản lý cửa hàng hoặc Giám đốc mới được duyệt hóa đơn nhập!");
            return false;
        }

        if (!coQuyen(nguoiDuyet, "DUYET_HOA_DON")) {
            System.out.println("[Lỗi] Bạn không có quyền duyệt hóa đơn!");
            return false;
        }

        // 3. Không cho duyệt hóa đơn đã hoàn tất
        if (this.isDaXuLyThanhCong()) {
            System.out.println("[Thông báo] Hóa đơn nhập [" + this.maHD + "] đã được duyệt trước đó!");
            return false;
        }

        if (this.soLuongCT <= 0) {
            System.out.println("[Lỗi] Hóa đơn chưa có sản phẩm!");
            return false;
        }

        hienThiChiTietChoDuyet(dsSP);

        // 4. Kiểm tra toàn bộ chi tiết trước khi cập nhật kho
        boolean coSanPhamMoi = false;

        for (int i = 0; i < this.soLuongCT; i++) {

            CTHDonNhap ct = this.dsChiTiet[i];

            if (ct == null
                    || ct.getMaSP() == null
                    || ct.getMaSP().trim().isEmpty()
                    || ct.getSoLuongNhap() <= 0
                    || ct.getDonGiaNhap() <= 0) {

                System.out.println("[Lỗi] Chi tiết nhập hàng thứ " + (i + 1) + " không hợp lệ!");
                return false;
            }

            SanPham spTrongKho = dsSP.timKiem(ct.getMaSP());
            SanPham spMoi = ct.getSanPhamMoi();

            if (spMoi != null) {

                coSanPhamMoi = true;

                // Sản phẩm mới không được trùng mã trong kho
                if (spTrongKho != null) {
                    System.out.println("[Lỗi] Mã sản phẩm " + ct.getMaSP() + " đã tồn tại trong kho!");
                    return false;
                }

                // Kiểm tra mã trong hồ sơ sản phẩm mới
                if (spMoi.getMaSP() == null
                        || !spMoi.getMaSP().equalsIgnoreCase(ct.getMaSP())) {

                    System.out.println("[Lỗi] Mã sản phẩm trong hồ sơ không khớp chi tiết hóa đơn!");
                    return false;
                }

                // Không cho nhiều hồ sơ sản phẩm mới trùng mã
                for (int j = 0; j < i; j++) {

                    SanPham spTruoc = this.dsChiTiet[j].getSanPhamMoi();

                    if (spTruoc != null
                            && spTruoc.getMaSP().equalsIgnoreCase(spMoi.getMaSP())) {

                        System.out.println("[Lỗi] Hóa đơn có nhiều hồ sơ trùng mã sản phẩm "
                                + spMoi.getMaSP());

                        return false;
                    }
                }

            } else {

                // Sản phẩm cũ phải tồn tại trong kho
                if (spTrongKho == null) {
                    System.out.println("[Lỗi] Không tìm thấy sản phẩm có mã: " + ct.getMaSP());
                    return false;
                }
            }

            /*
             * Kiểm tra tổng số lượng của các dòng cùng mã sản phẩm.
             * Tránh trường hợp cộng tồn kho vượt giới hạn int.
             */
            long tongSoLuong = 0;

            for (int j = 0; j < this.soLuongCT; j++) {

                if (this.dsChiTiet[j].getMaSP().equalsIgnoreCase(ct.getMaSP())) {
                    tongSoLuong += this.dsChiTiet[j].getSoLuongNhap();
                }
            }

            int tonHienTai = (spTrongKho != null)
                    ? spTrongKho.getSoLuongTon()
                    : 0;

            if (tongSoLuong + tonHienTai > Integer.MAX_VALUE) {
                System.out.println("[Lỗi] Tổng tồn kho của sản phẩm "
                        + ct.getMaSP() + " vượt giới hạn!");
                return false;
            }
        }

        // 5. Kiểm tra quyền duyệt sản phẩm mới
        if (coSanPhamMoi && !coQuyen(nguoiDuyet, "DUYET_SAN_PHAM")) {
            System.out.println("[Lỗi] Bạn không có quyền duyệt sản phẩm mới!");
            return false;
        }

        // 6. Yêu cầu duyệt từng sản phẩm mới
        for (int i = 0; i < this.soLuongCT; i++) {

            CTHDonNhap ct = this.dsChiTiet[i];
            SanPham spMoi = ct.getSanPhamMoi();

            if (spMoi == null) {
                continue;
            }

            System.out.println("\n========== DUYỆT SẢN PHẨM MỚI ==========");
            System.out.println("Mã sản phẩm: " + spMoi.getMaSP());
            System.out.println("Tên sản phẩm: " + spMoi.getTenSP());
            System.out.println("Giá nhập: " + Ktradulieu.dinhDangTien(ct.getDonGiaNhap()));
            System.out.println("Giá bán: " + Ktradulieu.dinhDangTien(spMoi.getDonGiaBan()));
            System.out.println("Đơn vị tính: " + spMoi.getDonViTinh());
            System.out.println("Nhà sản xuất: " + spMoi.getNsx());
            System.out.println("Hạn sử dụng: " + spMoi.getHsd());
            System.out.println("Thuế VAT: " + spMoi.getPhanTramThue() + "%");
            System.out.println("Số lượng nhập: " + ct.getSoLuongNhap());

            System.out.println("1. Duyệt sản phẩm");
            System.out.println("0. Từ chối sản phẩm");

            int chon = (int) Ktradulieu.nhapSoNguyen(sc, "Lựa chọn: ");

            if (chon != 1) {
                System.out.println("[Từ chối] Sản phẩm "
                        + spMoi.getMaSP() + " không được duyệt.");

                System.out.println("[Thông báo] Không cập nhật tồn kho.");
                return false;
            }
        }

        // 7. Kiểm tra lại danh sách trước khi thêm sản phẩm mới
        for (int i = 0; i < this.soLuongCT; i++) {

            CTHDonNhap ct = this.dsChiTiet[i];

            SanPham spTrongKho = dsSP.timKiem(ct.getMaSP());

            if (ct.getSanPhamMoi() == null && spTrongKho == null) {
                System.out.println("[Lỗi] Sản phẩm "
                        + ct.getMaSP() + " không còn tồn tại trong danh sách!");

                return false;
            }

            if (ct.getSanPhamMoi() != null && spTrongKho != null) {
                System.out.println("[Lỗi] Mã sản phẩm mới "
                        + ct.getMaSP() + " vừa bị trùng trong danh sách!");

                return false;
            }
        }

        // 8. Thêm toàn bộ sản phẩm mới vào danh sách
        for (int i = 0; i < this.soLuongCT; i++) {

            CTHDonNhap ct = this.dsChiTiet[i];
            SanPham spMoi = ct.getSanPhamMoi();

            if (spMoi == null) {
                continue;
            }

            spMoi.setTrangThaiDuyet("Đã duyệt");

            if (!dsSP.themSP(spMoi)) {
                System.out.println("[Lỗi] Không thể thêm sản phẩm "
                        + spMoi.getMaSP() + " vào danh sách!");

                return false;
            }

            System.out.println("[Thành công] Đã thêm sản phẩm "
                    + spMoi.getMaSP() + " vào danh sách sản phẩm.");
        }

        // 9. Chỉ sau khi thêm sản phẩm mới thành công mới cập nhật tồn kho
        for (int i = 0; i < this.soLuongCT; i++) {

            CTHDonNhap ct = this.dsChiTiet[i];

            SanPham sp = dsSP.timKiem(ct.getMaSP());

            if (sp == null) {
                System.out.println("[Lỗi] Không tìm thấy sản phẩm "
                        + ct.getMaSP() + " trong danh sách!");

                return false;
            }
        }

        // 10. Cộng tồn kho và cập nhật giá nhập
        for (int i = 0; i < this.soLuongCT; i++) {

            CTHDonNhap ct = this.dsChiTiet[i];

            SanPham sp = dsSP.timKiem(ct.getMaSP());

            sp.congTonKho(ct.getSoLuongNhap());
            sp.setDonGiaNhap(ct.getDonGiaNhap());
        }

        // 11. Xác nhận hóa đơn đã xử lý
        this.xacNhanXuLy();

        System.out.println("\n[Thành công] Đã duyệt nhập kho hóa đơn ["
                + this.maHD + "]!");

        System.out.println("[Thông báo] Tất cả sản phẩm đã được cập nhật vào kho.");

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
     * File chỉ lưu loại thanh toán nên chi tiết chuyển khoản không được khôi phục.
     */
    public void khoiPhucThanhToanTuFile(double soTienThanhToan, String loaiThanhToan) {
        ThanhToan tt = null;
        if ("TienMat".equalsIgnoreCase(loaiThanhToan)) {
            tt = new TienMat(this.maHD, soTienThanhToan, this.nguoiLap);
        } else if ("ChuyenKhoan".equalsIgnoreCase(loaiThanhToan)) {
            tt = new ChuyenKhoan(this.maHD, soTienThanhToan, this.nguoiLap);
        }
        if (tt != null) {
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
        String pt = (this.phuongThucThanhToan != null) ? this.phuongThucThanhToan.getLoaiThanhToan() : "CHUA_TT";
        return String.format("HDNHAP;%s;%s;%s;%s;%s;%.0f;%.0f;%.0f;%s;%s",
                this.maHD, this.ngayLap, this.getMaNV(), this.maNCC, this.trangThai,
                this.tinhTongTien(), this.tinhTienThue(), this.tinhThanhTien(), pt, (this.ghiChu != null ? this.ghiChu : ""));
    }

    @Override
    public String inHoaDon() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================== PHIẾU NHẬP KHO ==========================\n");
        sb.append("Mã hóa đơn nhập : ").append(this.maHD).append("\n");
        sb.append("Ngày lập        : ").append(this.ngayLap).append("\n");
        sb.append("Nhân viên lập   : [").append(this.getMaNV()).append("] ").append(this.nguoiLap != null ? this.nguoiLap.getTenND() : "").append("\n");
        sb.append("Nhà cung cấp    : ").append(this.maNCC).append("\n");
        sb.append("Trạng thái      : ").append(this.trangThai).append("\n");
        if (this.ghiChu != null && !this.ghiChu.isEmpty()) {
            sb.append("Ghi chú         : ").append(this.ghiChu).append("\n");
        }
        sb.append("--------------------------------------------------------------------\n");
        sb.append(String.format("%-4s | %-8s | %-20s | %-6s | %-12s | %-12s\n", "STT", "MÃ SP", "TÊN SẢN PHẨM", "SL", "ĐƠN GIÁ", "THÀNH TIỀN"));
        sb.append("--------------------------------------------------------------------\n");

        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDonNhap ct = this.dsChiTiet[i];
            sb.append(String.format("%-4d | %-8s | %-20s | %-6d | %-12s | %-12s\n",
                    i + 1, ct.getMaSP(), ct.getTenSP(), ct.getSoLuongNhap(),
                    Ktradulieu.dinhDangTien(ct.getDonGiaNhap()), Ktradulieu.dinhDangTien(ct.tinhThanhTien())));
        }

        sb.append("--------------------------------------------------------------------\n");
        sb.append("Tổng tiền hàng  : ").append(Ktradulieu.dinhDangTien(this.tinhTongTien())).append("\n");
        sb.append("Tiền thuế VAT   : ").append(Ktradulieu.dinhDangTien(this.tinhTienThue())).append("\n");
        sb.append("TỔNG THANH TOÁN : ").append(Ktradulieu.dinhDangTien(this.tinhThanhTien())).append("\n");
        if (this.phuongThucThanhToan != null) {
            sb.append("Hình thức TT    : ").append(this.phuongThucThanhToan.getLoaiThanhToan()).append(" (").append(this.phuongThucThanhToan.getTrangThai()).append(")\n");
        }
        sb.append("====================================================================\n");
        return sb.toString();
    }
}