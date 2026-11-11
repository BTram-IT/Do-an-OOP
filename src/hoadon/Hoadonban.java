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
        setMaKH(maKH);
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

    public boolean themCTHD(CTHDBan ct) {
        if (ct == null) {
            System.out.println("[Lỗi] Chi tiết bán hàng không hợp lệ!");
            return false;
        }

        for (int i = 0; i < this.soLuongCT; i++) {
            if (this.dsChiTiet[i].getMaSP().equalsIgnoreCase(ct.getMaSP())) {
                int slMoi = this.dsChiTiet[i].getSoLuongBan() + ct.getSoLuongBan();
                this.dsChiTiet[i].setSoLuongBan(slMoi);
                this.dsChiTiet[i].setTienGiam(this.dsChiTiet[i].getTienGiam() + ct.getTienGiam());
                this.dsChiTiet[i].setSoLuongTang(this.dsChiTiet[i].getSoLuongTang() + ct.getSoLuongTang());
                System.out.println("[Thông báo] Sản phẩm [" + ct.getMaSP() + "] đã có trong đơn. Đã cộng dồn thành: " + slMoi);
                return true;
            }
        }

        if (this.soLuongCT >= this.dsChiTiet.length) {
            moRongBoNho();
        }

        this.dsChiTiet[this.soLuongCT++] = ct;
        return true;
    }

    public CTHDBan[] getDsChiTiet() {
        CTHDBan[] ketQua = new CTHDBan[this.soLuongCT];
        for (int i = 0; i < this.soLuongCT; i++) {
            ketQua[i] = this.dsChiTiet[i];
        }
        return ketQua;
    }

    public int getSoLuongCT() {
        return this.soLuongCT;
    }


    @Override
    public String getmaHoaDon() {
        return this.maHD;
    }

    @Override
    public void setmaHoaDon() {
    }

    @Override
    public void thanhTien() {
        System.out.println("Tổng thành tiền thanh toán: " + Ktradulieu.dinhDangTien(tinhThanhTien()));
    }

    @Override
    public void hienThiHoaDon() {
        System.out.println(inHoaDon());
    }

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

    public double getThanhTien() {
        return this.tinhThanhTien();
    }
    @Override
    public double tinhThanhTien() {
        double tong = tinhTongTien() - tinhTienGiamKhuyenMai() + tinhTienThue() - this.tienDungDiem;
        return Math.max(0, tong);
    }

    @Override
    public String getLoaiHD() {
        return "Hóa đơn bán";
    }

    // =========================================================================
    // NHẬP THÔNG TIN VÀ THỰC HIỆN BÁN HÀNG TẠI QUẦY
    // =========================================================================
    public void nhapThongTin(Scanner sc, NhanVien nhanVienDangNhap, DSHoadon dsHD,
                             DsKhachHang dsKH, DSSanPham dsSP, DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {
        if (sc == null) sc = new Scanner(System.in);

        // 1. Gọi hàm lớp cha: tự tạo mã HD (phím /), tự gắn nhanVienDangNhap vào nguoiLap, gán ngày lập
        super.nhapThongTin(sc, nhanVienDangNhap, dsHD);

        // 2. Nhận diện Khách hàng & Tích điểm
        System.out.println("\n--- THÔNG TIN KHÁCH HÀNG ---");
        Khachhang khachHang = null;
        System.out.print("Khách có thẻ thành viên không? (1: Có thẻ  |  0: Khách lẻ vãng lai): ");
        String coThe = sc.nextLine().trim();

        if (coThe.equals("1")) {
            while (true) {
                System.out.print("Nhập mã khách hàng (Ví dụ: KH001): ");
                String inputKH = sc.nextLine().trim();
                String maChuan = Ktradulieu.chuanHoaMa(inputKH);

                if (dsKH != null) {
                    khachHang = dsKH.timKiem(maChuan);
                    if (khachHang == null) {
                        System.out.println("[Lỗi] Không tìm thấy khách hàng mã '" + maChuan + "'! Vui lòng nhập lại.");
                        continue;
                    }
                    setMaKH(khachHang.getMaKH());
                    System.out.println("-> Khách hàng: [" + khachHang.getMaKH() + "] " + khachHang.getTenKH()
                            + " | Điểm tích lũy hiện có: " + (int) khachHang.getDiemTichLuy() + " điểm");
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

        // 3. Nhập danh sách các mặt hàng khách mua
        System.out.println("\n--- BẮT ĐẦU QUÉT MẶT HÀNG BÁN ---");
        while (true) {
            CTHDBan ct = new CTHDBan();
            boolean thanhCong = ct.nhapThongTin(sc, this.maHD, dsSP, dsKM, dsSPKM, this.tinhTongTien());

            if (thanhCong) {
                this.themCTHD(ct);
                System.out.println("[Thành công] Đã thêm sản phẩm vào đơn hàng.");
            }

            System.out.print("Quét tiếp sản phẩm khác? (1: Có  |  0: Hoàn tất quét hàng): ");
            String tiep = sc.nextLine().trim();
            if (!tiep.equals("1")) {
                if (this.soLuongCT == 0) {
                    System.out.println("[Lỗi] Hóa đơn bán phải có ít nhất 1 mặt hàng!");
                    continue;
                }
                break;
            }
        }


        this.diemDung = 0;
        this.tienDungDiem = 0;
        if (khachHang != null && khachHang.getDiemTichLuy() > 0) {
            System.out.println("\nKhách có " + (int) khachHang.getDiemTichLuy() + " điểm thưởng (Tương đương: "
                    + Ktradulieu.dinhDangTien(khachHang.getDiemTichLuy() * 1000) + ").");
            System.out.print("Khách có muốn dùng điểm thưởng trừ tiền không? (1: Có  |  0: Không): ");
            String dungDiemOpt = sc.nextLine().trim();

            if (dungDiemOpt.equals("1")) {
                while (true) {
                    int diemMuonDung = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập số điểm muốn sử dụng: ");
                    if (diemMuonDung < 0 || diemMuonDung > khachHang.getDiemTichLuy()) {
                        System.out.println("[Lỗi] Số điểm nhập không hợp lệ hoặc vượt quá số điểm hiện có!");
                        continue;
                    }
                    this.diemDung = diemMuonDung;
                    this.tienDungDiem = this.diemDung * 1000;
                    System.out.println("-> Đã trừ: " + Ktradulieu.dinhDangTien(this.tienDungDiem) + " từ điểm thưởng.");
                    break;
                }
            }
        }


        double phaiTra = this.tinhThanhTien();
        System.out.println("\n================ TỔNG KẾT HÓA ĐƠN BÁN ================");
        System.out.println("Tổng tiền hàng  : " + Ktradulieu.dinhDangTien(this.tinhTongTien()));
        if (this.tinhTienGiamKhuyenMai() > 0) {
            System.out.println("Giảm khuyến mãi : -" + Ktradulieu.dinhDangTien(this.tinhTienGiamKhuyenMai()));
        }
        System.out.println("Thuế VAT        : +" + Ktradulieu.dinhDangTien(this.tinhTienThue()));
        if (this.tienDungDiem > 0) {
            System.out.println("Trừ điểm thưởng : -" + Ktradulieu.dinhDangTien(this.tienDungDiem));
        }
        System.out.println("TỔNG PHẢI TRẢ   : " + Ktradulieu.dinhDangTien(phaiTra));
        System.out.println("-----------------------------------------------------");

        System.out.println("Chọn phương thức thanh toán: 1. Tiền mặt  |  2. Chuyển khoản");
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
        // Trừ tồn kho thực tế của các sản phẩm đã bán
        if (dsSP != null) {
            for (int i = 0; i < this.soLuongCT; i++) {
                CTHDBan ct = this.dsChiTiet[i];
                SanPham sp = dsSP.timKiem(ct.getMaSP());
                if (sp != null) {
                    sp.truTonKho(ct.getSoLuongBan());
                }
            }
            System.out.println("[Hệ thống] Đã trừ số lượng sản phẩm xuất bán khỏi tồn kho.");
        }

        // Cập nhật điểm thưởng cho khách hàng thành viên
        if (khachHang != null) {
            if (this.diemDung > 0) {
                khachHang.truDiem((int) this.diemDung);
            }
            // Tích điểm mới: Cứ 100,000 VND thanh toán tích 1 điểm
            int diemCongMoi = (int) (this.tinhThanhTien() / 100000);
            if (diemCongMoi > 0) {
                khachHang.congDiem(diemCongMoi);
                System.out.println("[Tích điểm] Khách hàng được tích lũy thêm: +" + diemCongMoi + " điểm.");
            }
            System.out.println("[Tài khoản KH] Điểm thưởng hiện tại: " + (int) khachHang.getDiemTichLuy() + " điểm.");
        }

        //  Chuyển trạng thái sang DaXuLyThanhCong và khóa đơn hàng
        this.xacNhanXuLy();
        System.out.println("[Thành công] ĐÃ HOÀN TẤT ĐƠN HÀNG BÁN LẺ [" + this.maHD + "] (Trạng thái: DaXuLyThanhCong).");

        // In hóa đơn bán lẻ cho khách
        System.out.println(inHoaDon());
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        kiemTraQuyenChinhSua();
        if (maKH == null || maKH.trim().isEmpty()) {
            this.maKH = "KHACH_LE";
        } else {
            this.maKH = Ktradulieu.chuanHoaMa(maKH);
        }
    }

    public double getDiemDung() {
        return diemDung;
    }

    public double getTienDungDiem() {
        return tienDungDiem;
    }

    public ThanhToan getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(ThanhToan phuongThucThanhToan) {
        kiemTraQuyenChinhSua();
        this.phuongThucThanhToan = phuongThucThanhToan;
    }


    @Override
    public String toFileString() {
        String pt = (this.phuongThucThanhToan != null) ? this.phuongThucThanhToan.getLoaiThanhToan() : "CHUA_TT";
        return String.format("HDBAN;%s;%s;%s;%s;%s;%.0f;%.0f;%.0f;%.0f;%.0f;%s;%s",
                this.maHD, this.ngayLap, this.getMaNV(), this.maKH,
                this.trangThai, this.tinhTongTien(), this.tinhTienGiamKhuyenMai(),
                this.tinhTienThue(), this.tienDungDiem, this.tinhThanhTien(), pt, (this.ghiChu != null ? this.ghiChu : ""));
    }

    @Override
    public String inHoaDon() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================================ PHIẾU THANH TOÁN ================================\n");
        sb.append("CỬA HÀNG TIỆN LỢI 24/7\n");
        sb.append("Mã hóa đơn : ").append(this.maHD).append("\n");
        sb.append("Ngày lập   : ").append(this.ngayLap).append("\n");
        sb.append("Thu ngân   : [").append(this.getMaNV()).append("] ")
                .append(this.nguoiLap != null ? this.nguoiLap.getTenND() : "").append("\n");
        sb.append("Khách hàng : ").append(this.maKH).append("\n");
        sb.append("Trạng thái : ").append(this.trangThai).append("\n");
        sb.append("----------------------------------------------------------------------------------\n");
        sb.append(String.format("%-4s | %-8s | %-20s | %-5s | %-12s | %-10s | %-12s\n",
                "STT", "MÃ SP", "TÊN MẶT HÀNG", "SL", "ĐƠN GIÁ", "GIẢM GIÁ", "THÀNH TIỀN"));
        sb.append("----------------------------------------------------------------------------------\n");
        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDBan ct = this.dsChiTiet[i];
            String giamStr = (ct.getTienGiam() > 0) ? Ktradulieu.dinhDangTien(ct.getTienGiam()) : "0";
            sb.append(String.format("%-4d | %-8s | %-20s | %-5d | %-12s | %-10s | %-12s\n",
                    (i + 1), ct.getMaSP(), ct.getTenSP(), ct.getSoLuongBan(),
                    Ktradulieu.dinhDangTien(ct.getDonGiaBan()),
                    giamStr,
                    Ktradulieu.dinhDangTien(ct.tinhThanhTien())));
            if (ct.getSoLuongTang() > 0) {
                sb.append("     * Quà tặng khuyến mãi kèm theo: ").append(ct.getSoLuongTang()).append(" sản phẩm miễn phí\n");
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
            sb.append(String.format("Phương thức TT  : %s (%s)\n",
                    this.phuongThucThanhToan.getLoaiThanhToan(), this.phuongThucThanhToan.getTrangThai()));
        }
        sb.append("=================== CẢM ƠN QUÝ KHÁCH VÀ HẸN GẶP LẠI ===================\n");
        return sb.toString();
    }
}