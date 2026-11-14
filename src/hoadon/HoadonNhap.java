package hoadon;


import java.util.Locale;
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

    private boolean kiemTraDuocChinhSua() {
        if (this.isDaXuLyThanhCong()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất, không thể chỉnh sửa!");
            return false;
        }

        if (this.isDaHuy()) {
            System.out.println("[Lỗi] Hóa đơn đã hủy, không thể chỉnh sửa!");
            return false;
        }

        return true;
    }

    public boolean themCTHD(CTHDonNhap ct) {
        if (!kiemTraDuocChinhSua()) {
            return false;
        }

        if (ct == null
                || ct.getMaSP() == null
                || ct.getMaSP().trim().isEmpty()
                || ct.getSoLuongNhap() <= 0
                || ct.getDonGiaNhap() <= 0) {
            System.out.println("[Lỗi] Chi tiết nhập hàng không hợp lệ!");
            return false;
        }

        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDonNhap ctCu = this.dsChiTiet[i];

            if (ctCu.getMaSP().equalsIgnoreCase(ct.getMaSP())) {
                System.out.println(
                        "[Lỗi] Sản phẩm này đã có trong hóa đơn. "
                                + "Bạn hãy sửa số lượng ở dòng sản phẩm hiện có!"
                );
                return false;
            }
        }

        if (this.soLuongCT >= this.dsChiTiet.length) {
            moRongBoNho();
        }

        this.dsChiTiet[this.soLuongCT++] = new CTHDonNhap(ct);

        System.out.println("[Thành công] Đã thêm sản phẩm vào hóa đơn!");
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

    public boolean xoaCTHD(int stt) {
        if (!kiemTraDuocChinhSua()) {
            return false;
        }

        if (stt < 1 || stt > this.soLuongCT) {
            System.out.println("[Lỗi] STT sản phẩm không hợp lệ!");
            return false;
        }

        if (this.soLuongCT <= 1) {
            System.out.println("[Lỗi] Hóa đơn nhập phải có ít nhất 1 sản phẩm!");
            return false;
        }

        int viTri = stt - 1;

        for (int i = viTri; i < this.soLuongCT - 1; i++) {
            this.dsChiTiet[i] = this.dsChiTiet[i + 1];
        }

        this.dsChiTiet[this.soLuongCT - 1] = null;
        this.soLuongCT--;

        System.out.println("[Thành công] Đã xóa sản phẩm khỏi hóa đơn!");
        return true;
    }

    public boolean suaCTHD(int stt, CTHDonNhap ctMoi) {
        if (!kiemTraDuocChinhSua()) {
            return false;
        }

        if (stt < 1 || stt > this.soLuongCT) {
            System.out.println("[Lỗi] STT sản phẩm không hợp lệ!");
            return false;
        }

        if (ctMoi == null
                || ctMoi.getMaSP() == null
                || ctMoi.getMaSP().trim().isEmpty()
                || ctMoi.getSoLuongNhap() <= 0
                || ctMoi.getDonGiaNhap() <= 0) {

            System.out.println("[Lỗi] Thông tin sản phẩm không hợp lệ!");
            return false;
        }

        for (int i = 0; i < this.soLuongCT; i++) {
            if (i == stt - 1) {
                continue;
            }

            if (this.dsChiTiet[i].getMaSP()
                    .equalsIgnoreCase(ctMoi.getMaSP())) {

                System.out.println("[Lỗi] Mã sản phẩm đã tồn tại ở dòng khác!");
                return false;
            }
        }

        this.dsChiTiet[stt - 1] = new CTHDonNhap(ctMoi);

        System.out.println("[Thành công] Đã sửa chi tiết hóa đơn!");
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

    public boolean nhapThongTin(Scanner sc, NhanVien nhanVienDangNhap, DSHoadon dsHD, DSNhaCungCap dsNCC, DSSanPham dsSP, Nguoidung nguoiDungDangNhap) {
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

            if (!ct.nhapThongTin(sc, this.maHD, dsSP, nguoiDungDangNhap)) {
                System.out.println("[Thông báo] Đã hủy thao tác lập phiếu nhập!");
                return false;
            }

            if (this.themCTHD(ct)) {
                System.out.println("[Thành công] Đã thêm mặt hàng vào phiếu nhập.");
            }

            System.out.print("Bạn có muốn nhập tiếp không? (1: Có / 0: Hoàn tất): ");

            String tiepTuc = sc.nextLine().trim();

            if (tiepTuc.equals("1")) {
                continue;
            }

            if (this.soLuongCT == 0) {
                System.out.println("[Lỗi] Hóa đơn nhập phải có ít nhất 1 mặt hàng!");
                continue;
            }

            break;
        }
        this.phuongThucThanhToan = null;

        this.setTrangThai("DangXuLy");

        System.out.println("\n========== LẬP HÓA ĐƠN NHẬP ==========");
        System.out.println("• Mã hóa đơn nhập: [" + this.maHD + "]");
        System.out.println("• Mã nhà cung cấp: [" + this.maNCC + "]");
        System.out.println("• Nhân viên phụ trách: ["
                + nhanVienDangNhap.getMaND() + "] "
                + nhanVienDangNhap.getTenND());

        System.out.println("• Tổng tiền hàng: "
                + Ktradulieu.dinhDangTien(tinhTongTien()));

        System.out.println("• Tiền thuế: "
                + Ktradulieu.dinhDangTien(tinhTienThue()));

        System.out.println("• Tổng tiền phải trả: "
                + Ktradulieu.dinhDangTien(tinhThanhTien()));

        System.out.println("\n[Thành công] Đã lập hóa đơn nhập ["
                + this.maHD + "].");

        System.out.println("Trạng thái: Đang chờ quản lý duyệt.");
        System.out.println("Chưa thực hiện thanh toán.");

        return true;
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

    private boolean thucHienThanhToan(
            Scanner sc,
            Nguoidung nguoiDuyet
    ) {
        if (!(nguoiDuyet instanceof NhanVien)) {
            System.out.println("[Lỗi] Người duyệt không hợp lệ!");
            return false;
        }

        if (this.phuongThucThanhToan != null
                && this.phuongThucThanhToan.isThanhCong()) {
            System.out.println("[Thông báo] Hóa đơn đã được thanh toán!");
            return false;
        }

        double tongTienCanThanhToan = tinhThanhTien();

        if (tongTienCanThanhToan <= 0) {
            System.out.println("[Lỗi] Tổng tiền hóa đơn không hợp lệ!");
            return false;
        }

        System.out.println("\n========== THANH TOÁN HÓA ĐƠN NHẬP ==========");
        System.out.println("Mã hóa đơn: " + getMaHD());
        System.out.println("Tiền hàng: "
                + util.Ktradulieu.dinhDangTien(tinhTongTien()));
        System.out.println("Tiền thuế: "
                + util.Ktradulieu.dinhDangTien(tinhTienThue()));
        System.out.println("Tổng thanh toán: "
                + util.Ktradulieu.dinhDangTien(tongTienCanThanhToan));

        System.out.println("\n1. Thanh toán tiền mặt");
        System.out.println("2. Thanh toán chuyển khoản");
        System.out.println("0. Hủy thanh toán");

        int chon = util.Ktradulieu.nhapSoNguyen(
                sc, "Chọn phương thức thanh toán: ", 0, 2
        );

        if (chon == 0) {
            System.out.println("Đã hủy thanh toán. Hóa đơn vẫn chờ duyệt.");
            return false;
        }

        ThanhToan thanhToan;

        NhanVien nhanVien = (NhanVien) nguoiDuyet;

        if (chon == 1) {
            thanhToan = new TienMat(
                    getMaHD(),
                    tongTienCanThanhToan,
                    nhanVien
            );
        } else {
            thanhToan = new ChuyenKhoan(
                    getMaHD(),
                    tongTienCanThanhToan,
                    nhanVien
            );
        }

        thanhToan.nhap(sc);

        if (!thanhToan.isThanhCong()) {
            System.out.println("[Lỗi] Thanh toán thất bại!");
            System.out.println("Hóa đơn chưa được duyệt và chưa cập nhật kho.");
            return false;
        }

        this.phuongThucThanhToan = thanhToan;

        System.out.println("[Thành công] Thanh toán hóa đơn nhập thành công!");
        return true;
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

        // 3. Kiểm tra trạng thái hóa đơn
        if (this.isDaXuLyThanhCong()) {
            System.out.println("[Thông báo] Hóa đơn nhập [" + this.maHD + "] đã được duyệt trước đó!");
            return false;
        }

        if (this.isDaHuy()) {
            System.out.println("[Lỗi] Hóa đơn đã bị hủy, không thể duyệt!");
            return false;
        }

        if (this.soLuongCT <= 0) {
            System.out.println("[Lỗi] Hóa đơn chưa có sản phẩm!");
            return false;
        }

        hienThiChiTietChoDuyet(dsSP);

        // 4. Kiểm tra toàn bộ chi tiết hóa đơn
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

            // Hàng nhập đã quá hạn ngay tại thời điểm duyệt thì không được nhập kho
            if (ct.getHanSuDung() != null && ct.getHanSuDung().isBefore(java.time.LocalDate.now())) {
                System.out.println("[Lỗi] Sản phẩm " + ct.getMaSP() + " có hạn sử dụng ("
                        + ct.getHanSuDung().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        + ") đã qua, không thể duyệt nhập kho!");
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

            // Kiểm tra tổng số lượng để tránh vượt giới hạn int
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

        // 7. Kiểm tra lại danh sách sản phẩm trước khi thanh toán
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

        // =====================================================
        // 8. THANH TOÁN CHO NHÀ CUNG CẤP
        // =====================================================

        double tongTienThanhToan = this.tinhThanhTien();

        if (tongTienThanhToan <= 0) {
            System.out.println("[Lỗi] Tổng tiền thanh toán không hợp lệ!");
            return false;
        }

        System.out.println("\n========== THANH TOÁN HÓA ĐƠN NHẬP ==========");
        System.out.println("Mã hóa đơn: " + this.maHD);
        System.out.println("Mã nhà cung cấp: " + this.maNCC);
        System.out.println("Tổng tiền hàng: "
                + Ktradulieu.dinhDangTien(this.tinhTongTien()));
        System.out.println("Tiền thuế: "
                + Ktradulieu.dinhDangTien(this.tinhTienThue()));
        System.out.println("Tổng tiền phải trả: "
                + Ktradulieu.dinhDangTien(tongTienThanhToan));

        System.out.println("\nChọn hình thức thanh toán:");
        System.out.println("1. Tiền mặt");
        System.out.println("2. Chuyển khoản");
        System.out.println("0. Hủy thanh toán");

        int chonTT;

        while (true) {
            chonTT = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập lựa chọn (0-2): ");

            if (chonTT >= 0 && chonTT <= 2) {
                break;
            }

            System.out.println("[Lỗi] Chỉ được chọn từ 0 đến 2!");
        }

        // Nếu quản lý hủy thanh toán
        if (chonTT == 0) {
            System.out.println("[Thông báo] Đã hủy thanh toán.");
            System.out.println("[Thông báo] Hóa đơn vẫn đang chờ duyệt, kho chưa thay đổi.");
            return false;
        }

        ThanhToan thanhToan;

        // Thanh toán tiền mặt
        if (chonTT == 1) {

            System.out.println("\n========== CHI TIỀN MẶT ==========");
            System.out.println("Cửa hàng chi tiền cho nhà cung cấp.");
            System.out.println("Số tiền chi: "
                    + Ktradulieu.dinhDangTien(tongTienThanhToan));

            int xacNhan;

            while (true) {
                xacNhan = (int) Ktradulieu.nhapSoNguyen(
                        sc, "Xác nhận đã chi tiền? (1: Có / 0: Không): ");

                if (xacNhan == 0 || xacNhan == 1) {
                    break;
                }

                System.out.println("[Lỗi] Chỉ được chọn 1 hoặc 0!");
            }

            if (xacNhan == 0) {
                System.out.println("[Thông báo] Chưa xác nhận thanh toán.");
                System.out.println("[Thông báo] Hóa đơn vẫn đang chờ duyệt, kho chưa thay đổi.");
                return false;
            }

            TienMat tm = new TienMat(
                    this.maHD,
                    tongTienThanhToan,
                    nguoiDuyet
            );

            tm.setTienKhachDua(tongTienThanhToan);

            if (!tm.qTrinhThanhToan()) {
                System.out.println("[Lỗi] Thanh toán tiền mặt thất bại!");
                return false;
            }

            thanhToan = tm;

        } else {

            // Thanh toán chuyển khoản
            System.out.println("\n========== CHUYỂN KHOẢN CHO NHÀ CUNG CẤP ==========");

            ChuyenKhoan ck = new ChuyenKhoan(
                    this.maHD,
                    tongTienThanhToan,
                    nguoiDuyet
            );

            ck.nhap(sc);

            if (!ck.isThanhCong()) {
                System.out.println("[Lỗi] Thanh toán chuyển khoản thất bại!");
                System.out.println("[Thông báo] Hóa đơn vẫn đang chờ duyệt, kho chưa thay đổi.");
                return false;
            }

            thanhToan = ck;
        }

        // Chỉ ghi nhận phương thức thanh toán sau khi thanh toán thành công
        this.phuongThucThanhToan = thanhToan;

        System.out.println("[Thành công] Thanh toán hóa đơn nhập thành công!");

        // =====================================================
        // 9. THANH TOÁN THÀNH CÔNG - THÊM SẢN PHẨM MỚI
        // =====================================================

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

        // =====================================================
        // 10. CẬP NHẬT SỐ LƯỢNG TỒN KHO VÀ GIÁ NHẬP
        // =====================================================

        for (int i = 0; i < this.soLuongCT; i++) {

            CTHDonNhap ct = this.dsChiTiet[i];
            SanPham sp = dsSP.timKiem(ct.getMaSP());

            if (sp == null) {
                System.out.println("[Lỗi] Không tìm thấy sản phẩm "
                        + ct.getMaSP() + " trong danh sách!");
                return false;
            }

            // Sản phẩm đã có trong kho: cập nhật HSD theo lần nhập này TRƯỚC khi cộng tồn,
            // để hàng mới nhập không bị đánh giá theo HSD cũ (sản phẩm mới đã mang sẵn HSD từ hồ sơ).
            if (ct.getSanPhamMoi() == null) {
                String thongBaoHSD = sp.capNhatHanSuDungKhiNhap(ct.getHanSuDung());
                if (thongBaoHSD != null) {
                    System.out.println("[HSD] " + thongBaoHSD);
                }
            }

            sp.congTonKho(ct.getSoLuongNhap());
            sp.setDonGiaNhap(ct.getDonGiaNhap());
        }

        // =====================================================
        // 11. XÁC NHẬN HÓA ĐƠN ĐÃ XỬ LÝ
        // =====================================================

        this.xacNhanXuLy();
        return true;
    }



    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) {
        if (!kiemTraDuocChinhSua()) {
            return;
        }

        if (maNCC == null || maNCC.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "[Lỗi] Mã nhà cung cấp không được để trống!"
            );
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
        return String.format(Locale.ROOT, "HDNHAP;%s;%s;%s;%s;%s;%.0f;%.0f;%.0f;%s;%s",
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