package hoadon;

import java.util.Scanner;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import person.DsKhachHang;
import person.Khachhang;
import person.NhanVien;
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

    public Hoadonban(String maHD, NhanVien nguoiLap, String ngayLap, String trangThai, String ghiChu, String maKH) {
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

        if (this.isDaXuLyThanhCong()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất, không thể thêm chi tiết!");
            return false;
        }

        if (ct == null || ct.getMaSP() == null
                || ct.getMaSP().trim().isEmpty()
                || ct.getSoLuongBan() <= 0) {
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

        if (!sp.kiemTraHanSuDung()) {
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

                ctCu.setSoLuongBan((int) slMoi);

                // Tính lại khuyến mãi dựa trên tổng số lượng đã cộng dồn.
                ctCu.apDungKhuyenMai(dsKM, dsSPKM, sp);

                System.out.println("[Thông báo] Đã cộng dồn sản phẩm. Số lượng mua: " + slMoi);
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
            if (sp == null || !sp.kiemTraHanSuDung()) {
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
            if (tongSoLuong > sp.getSoLuongTon()) {
                System.out.println("[Lỗi] Không đủ tồn kho cho sản phẩm " + ct.getMaSP() + " (Tồn: " + sp.getSoLuongTon() + ")");
                return false;
            }
        }
        return true;
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
            tongGiam += this.dsChiTiet[i].getTienGiam();
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

    public void nhapThongTin(Scanner sc, NhanVien nhanVienDangNhap, DSHoadon dsHD, DsKhachHang dsKH, DSSanPham dsSP, DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {
        if (sc == null) sc = new Scanner(System.in);

        super.nhapThongTin(sc, nhanVienDangNhap, dsHD);

        // Nhận diện khách hàng
        System.out.println("\n--- THÔNG TIN KHÁCH HÀNG ---");
        Khachhang khachHang = null;
        System.out.print("Khách có thẻ thành viên không? (1: Có | 0: Khách lẻ): ");
        String coThe = sc.nextLine().trim();

        if (coThe.equals("1")) {
            while (true) {
                System.out.print("Nhập mã khách hàng (Ví dụ: KH001): ");
                String maChuan = Ktradulieu.chuanHoaMa(sc.nextLine().trim());
                if (dsKH != null) {
                    khachHang = dsKH.timKiem(maChuan);
                    if (khachHang == null) {
                        System.out.println("[Lỗi] Không tìm thấy khách hàng! Vui lòng nhập lại.");
                        continue;
                    }
                    setMaKH(khachHang.getMaKH());
                    System.out.println("-> Khách: [" + khachHang.getMaKH() + "] " + khachHang.getTenKH() + " | Điểm: " + (int) khachHang.getDiemTichLuy());
                    break;
                } else {
                    setMaKH(maChuan);
                    break;
                }
            }
        } else {
            setMaKH("KHACH_LE");
            System.out.println("-> Khách hàng: Khách lẻ vãng lai.");
        }

        // Nhập danh sách sản phẩm
        System.out.println("\n--- BẮT ĐẦU QUÉT MẶT HÀNG BÁN ---");
        while (true) {
            CTHDBan ct = new CTHDBan();
            if (ct.nhapThongTin(sc, this.maHD, dsSP, dsKM, dsSPKM)) {
                if (this.themCTHD(ct, dsSP, dsKM, dsSPKM)) {
                    System.out.println("[Thành công] Đã thêm sản phẩm vào đơn hàng.");
                }
            }
            System.out.print("Quét tiếp sản phẩm khác? (1: Có | 0: Hoàn tất): ");
            if (!sc.nextLine().trim().equals("1")) {
                if (this.soLuongCT == 0) {
                    System.out.println("[Lỗi] Hóa đơn bán phải có ít nhất 1 mặt hàng!");
                    continue;
                }
                break;
            }
        }

        // Sử dụng điểm thưởng
        this.diemDung = 0;
        this.tienDungDiem = 0;
        if (khachHang != null && khachHang.getDiemTichLuy() > 0) {

            System.out.println("\nKhách có " + (int) khachHang.getDiemTichLuy()
                    + " điểm (" + Ktradulieu.dinhDangTien(
                    khachHang.getDiemTichLuy() * 1000) + ").");

            System.out.print("Dùng điểm thưởng trừ tiền? (1: Có | 0: Không): ");

            if (sc.nextLine().trim().equals("1")) {

                // Số tiền còn phải trả trước khi sử dụng điểm.
                double tienTruocKhiDungDiem =
                        this.tinhTongTien()
                                - this.tinhTienGiamKhuyenMai()
                                + this.tinhTienThue();

                // Không cho phép dùng điểm vượt quá số tiền cần thanh toán.
                int diemToiDaDuocDung = (int) Math.min(
                        khachHang.getDiemTichLuy(),
                        Math.floor(Math.max(0, tienTruocKhiDungDiem) / 1000)
                );

                if (diemToiDaDuocDung <= 0) {
                    System.out.println("[Thông báo] Hóa đơn không còn số tiền phù hợp để sử dụng điểm.");
                } else {
                    while (true) {
                        int diemMuonDung = (int) Ktradulieu.nhapSoNguyen(
                                sc, "Nhập số điểm muốn sử dụng: ");

                        if (diemMuonDung <= 0 || diemMuonDung > diemToiDaDuocDung) {
                            System.out.println("[Lỗi] Chỉ được sử dụng từ 1 đến "
                                    + diemToiDaDuocDung + " điểm!");
                            continue;
                        }

                        this.diemDung = diemMuonDung;
                        this.tienDungDiem = this.diemDung * 1000;

                        System.out.println("-> Đã trừ: "
                                + Ktradulieu.dinhDangTien(this.tienDungDiem));
                        break;
                    }
                }
            }
        }
        if (!kiemTraTonKhoTruocThanhToan(dsSP)) {
            System.out.println("[Thông báo] Hủy thanh toán do tồn kho không hợp lệ.");
            return;
        }

        // Tổng kết & Thanh toán
        double phaiTra = this.tinhThanhTien();
        System.out.println("\n================ TỔNG KẾT HÓA ĐƠN BÁN ================");
        System.out.println("Tổng tiền hàng  : " + Ktradulieu.dinhDangTien(this.tinhTongTien()));
        if (this.tinhTienGiamKhuyenMai() > 0)
            System.out.println("Giảm khuyến mãi : -" + Ktradulieu.dinhDangTien(this.tinhTienGiamKhuyenMai()));
        System.out.println("Thuế VAT        : +" + Ktradulieu.dinhDangTien(this.tinhTienThue()));
        if (this.tienDungDiem > 0)
            System.out.println("Trừ điểm thưởng : -" + Ktradulieu.dinhDangTien(this.tienDungDiem));
        System.out.println("TỔNG PHẢI TRẢ   : " + Ktradulieu.dinhDangTien(phaiTra));
        System.out.println("-----------------------------------------------------");

        System.out.print("Chọn phương thức thanh toán (1. Tiền mặt | 2. Chuyển khoản): ");
        int chonTT = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập lựa chọn (1-2): ");

        if (chonTT == 2) {
            ChuyenKhoan ck = new ChuyenKhoan(this.maHD, phaiTra, nhanVienDangNhap);
            ck.nhap(sc);
            ck.qTrinhThanhToan();
            this.phuongThucThanhToan = ck;
        } else {
            TienMat tm = new TienMat(this.maHD, phaiTra, nhanVienDangNhap);
            tm.nhap(sc);
            tm.qTrinhThanhToan();
            this.phuongThucThanhToan = tm;
        }

        hoanTatDonHang(dsSP, khachHang);
    }

    private void hoanTatDonHang(DSSanPham dsSP, Khachhang khachHang) {
        if (this.isDaXuLyThanhCong()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất, không thể trừ kho lần nữa!");
            return;
        }

        if (!kiemTraTonKhoTruocThanhToan(dsSP)) {
            throw new IllegalStateException("[Lỗi] Tồn kho không hợp lệ khi hoàn tất!");
        }

        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDBan ct = this.dsChiTiet[i];
            SanPham sp = dsSP.timKiem(ct.getMaSP());
            int tongSoLuong = ct.getTongSoLuongXuatKho();
            sp.truTonKho(tongSoLuong);
            System.out.println("[Trừ kho] " + ct.getMaSP() + " | Mua: " + ct.getSoLuongBan() + " | Tặng: " + ct.getSoLuongTang());
        }

        if (khachHang != null) {
            if (this.diemDung > 0) khachHang.truDiem((int) this.diemDung);
            int diemCongMoi = (int) (this.tinhThanhTien() / 100000);
            if (diemCongMoi > 0) {
                khachHang.congDiem(diemCongMoi);
                System.out.println("[Tích điểm] Cộng thêm: +" + diemCongMoi + " điểm.");
            }
            System.out.println("[Tài khoản KH] Điểm hiện tại: " + (int) khachHang.getDiemTichLuy());
        }

        this.xacNhanXuLy();
        System.out.println("[Thành công] ĐÃ HOÀN TẤT ĐƠN HÀNG BÁN LẺ [" + this.maHD + "]");
        System.out.println(inHoaDon());
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) {
        kiemTraQuyenChinhSua();
        this.maKH = (maKH == null || maKH.trim().isEmpty()) ? "KHACH_LE" : Ktradulieu.chuanHoaMa(maKH);
    }

    public double getDiemDung() { return diemDung; }
    public double getTienDungDiem() { return tienDungDiem; }
    public ThanhToan getPhuongThucThanhToan() { return phuongThucThanhToan; }
    public void setPhuongThucThanhToan(ThanhToan phuongThucThanhToan) {
        kiemTraQuyenChinhSua();
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    @Override
    public String toFileString() {
        String pt = (this.phuongThucThanhToan != null) ? this.phuongThucThanhToan.getLoaiThanhToan() : "CHUA_TT";
        return String.format("HDBAN;%s;%s;%s;%s;%s;%.0f;%.0f;%.0f;%.0f;%.0f;%s;%s",
                this.maHD, this.ngayLap, this.getMaNV(), this.maKH, this.trangThai,
                this.tinhTongTien(), this.tinhTienGiamKhuyenMai(), this.tinhTienThue(),
                this.tienDungDiem, this.tinhThanhTien(), pt, (this.ghiChu != null ? this.ghiChu : ""));
    }

    @Override
    public String inHoaDon() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================================ PHIẾU THANH TOÁN ================================\n");
        sb.append("CỬA HÀNG TIỆN LỢI 24/7\n");
        sb.append("Mã hóa đơn : ").append(this.maHD).append("\n");
        sb.append("Ngày lập   : ").append(this.ngayLap).append("\n");
        sb.append("Thu ngân   : [").append(this.getMaNV()).append("] ").append(this.nguoiLap != null ? this.nguoiLap.getTenND() : "").append("\n");
        sb.append("Khách hàng : ").append(this.maKH).append("\n");
        sb.append("Trạng thái : ").append(this.trangThai).append("\n");
        sb.append("----------------------------------------------------------------------------------\n");
        sb.append(String.format("%-4s | %-8s | %-20s | %-5s | %-12s | %-10s | %-12s\n", "STT", "MÃ SP", "TÊN MẶT HÀNG", "SL", "ĐƠN GIÁ", "GIẢM GIÁ", "THÀNH TIỀN"));
        sb.append("----------------------------------------------------------------------------------\n");

        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDBan ct = this.dsChiTiet[i];
            String giamStr = (ct.getTienGiam() > 0) ? Ktradulieu.dinhDangTien(ct.getTienGiam()) : "0";
            sb.append(String.format("%-4d | %-8s | %-20s | %-5d | %-12s | %-10s | %-12s\n",
                    (i + 1), ct.getMaSP(), ct.getTenSP(), ct.getSoLuongBan(),
                    Ktradulieu.dinhDangTien(ct.getDonGiaBan()), giamStr, Ktradulieu.dinhDangTien(ct.tinhThanhTien())));
            if (ct.getSoLuongTang() > 0) {
                sb.append("    * Quà tặng khuyến mãi kèm theo: ").append(ct.getSoLuongTang()).append(" sản phẩm miễn phí\n");
            }
        }

        sb.append("----------------------------------------------------------------------------------\n");
        sb.append(String.format("Tổng tiền hàng  : %s\n", Ktradulieu.dinhDangTien(this.tinhTongTien())));
        if (this.tinhTienGiamKhuyenMai() > 0) {
            sb.append(String.format("Giảm khuyến mãi : -%s\n", Ktradulieu.dinhDangTien(this.tinhTienGiamKhuyenMai())));
        }
        sb.append(String.format("Tiền thuế VAT   : +%s\n", Ktradulieu.dinhDangTien(this.tinhTienThue())));
        if (this.tienDungDiem > 0) {
            sb.append(String.format("Trừ điểm thưởng : -%s (%d điểm)\n", Ktradulieu.dinhDangTien(this.tienDungDiem), (int) this.diemDung));
        }
        sb.append("----------------------------------------------------------------------------------\n");
        sb.append(String.format("TỔNG THANH TOÁN : %s\n", Ktradulieu.dinhDangTien(this.tinhThanhTien())));
        if (this.phuongThucThanhToan != null) {
            sb.append(String.format("Phương thức TT  : %s (%s)\n", this.phuongThucThanhToan.getLoaiThanhToan(), this.phuongThucThanhToan.getTrangThai()));
        }
        sb.append("=================== CẢM ƠN QUÝ KHÁCH VÀ HẸN GẶP LẠI ===================\n");
        return sb.toString();
    }
}