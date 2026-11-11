package hoadon;

import java.util.Scanner;
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
        setMaNCC(maNCC);
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
        if (ct == null) {
            System.out.println("[Lỗi] Chi tiết nhập hàng không hợp lệ!");
            return false;
        }

        // Nếu sản phẩm đã có trong hóa đơn thì cộng dồn số lượng
        for (int i = 0; i < this.soLuongCT; i++) {
            if (this.dsChiTiet[i].getMaSP().equalsIgnoreCase(ct.getMaSP())) {
                int slMoi = this.dsChiTiet[i].getSoLuongNhap() + ct.getSoLuongNhap();
                this.dsChiTiet[i].setSoLuongNhap(slMoi);
                System.out.println("[Thông báo] Sản phẩm [" + ct.getMaSP() + "] đã có trong đơn. Đã cộng dồn số lượng thành: " + slMoi);
                return true;
            }
        }

        if (this.soLuongCT >= this.dsChiTiet.length) {
            moRongBoNho();
        }

        this.dsChiTiet[this.soLuongCT++] = ct;
        return true;
    }

    public CTHDonNhap[] getDsChiTiet() {
        CTHDonNhap[] ketQua = new CTHDonNhap[this.soLuongCT];
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
        return "";
    }

    @Override
    public void setmaHoaDon() {

    }

    @Override
    public void thanhTien() {
        return this.tinhThanhTien();
    }

    @Override
    public void hienThiHoaDon() {

    }

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
    public double tinhThanhTien() {
        return tinhTongTien() + tinhTienThue();
    }

    @Override
    public String getLoaiHD() {
        return "Hóa đơn nhập";
    }

    public void nhapThongTin(Scanner sc, NhanVien nhanVienDangNhap, DSHoadon dsHD, DSNhaCungCap dsNCC, DSSanPham dsSP) {
        if (sc == null) sc = new Scanner(System.in);

        super.nhapThongTin(sc, nhanVienDangNhap, dsHD);

        System.out.println("\n--- THÔNG TIN ĐỐI TÁC NHÀ CUNG CẤP ---");
        while (true) {
            System.out.print("Nhập mã nhà cung cấp (Ví dụ: NCC001 - Nhấn / để chọn NCC có sẵn): ");
            String inputNCC = sc.nextLine().trim();

            if (inputNCC.equals("/")) {
                if (dsNCC != null && dsNCC.getSoLuong() > 0) {
                    this.setMaNCC(dsNCC.getDanhSach().get(0).getMaNCC());
                    System.out.println("-> Tự động chọn NCC: [" + this.maNCC + "] " + dsNCC.getDanhSach().get(0).getTenNCC());
                    break;
                } else {
                    this.setMaNCC("NCC001");
                    System.out.println("-> Tự động gán mã NCC mặc định: " + this.maNCC);
                    break;
                }
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(inputNCC);
                if (!maChuan.startsWith("NCC")) {
                    System.out.println("[Lỗi] Mã nhà cung cấp phải bắt đầu bằng 'NCC' (Ví dụ: NCC001)!");
                    continue;
                }
                if (dsNCC != null && dsNCC.timKiem(maChuan) == null) {
                    System.out.println("[Lỗi Logic] Mã nhà cung cấp '" + maChuan + "' không tồn tại trong hệ thống!");
                    continue;
                }
                this.setMaNCC(maChuan);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // 3. Nhập danh sách các mặt hàng nhập kho
        System.out.println("\n--- BẮT ĐẦU NHẬP CÁC DÒNG HÀNG VÀO PHIẾU NHẬP ---");
        while (true) {
            CTHDonNhap ct = new CTHDonNhap();
            boolean thanhCong = ct.nhapThongTin(sc, this.maHD, dsSP);

            if (thanhCong) {
                this.themCTHD(ct);
                System.out.println("[Thành công] Đã thêm mặt hàng vào phiếu nhập.");
            }

            System.out.print("Bạn có muốn nhập tiếp mặt hàng khác không? (1: Có / 0: Hoàn tất danh sách hàng): ");
            String tiep = sc.nextLine().trim();
            if (!tiep.equals("1")) {
                if (this.soLuongCT == 0) {
                    System.out.println("[Lỗi] Hóa đơn nhập phải có ít nhất 1 mặt hàng!");
                    continue;
                }
                break;
            }
        }

        // 4. Chọn hình thức thanh toán chi tiền cho Nhà cung cấp
        System.out.println("\nTổng tiền cần thanh toán cho nhà cung cấp: " + Ktradulieu.dinhDangTien(this.tinhThanhTien()));
        System.out.println("Chọn hình thức chi tiền: 1. Tiền mặt  |  2. Chuyển khoản ngân hàng");
        int chonTT = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập lựa chọn (1-2): ");

        if (chonTT == 2) {
            ChuyenKhoan ck = new ChuyenKhoan(this.maHD, this.tinhThanhTien(), nhanVienDangNhap);
            ck.nhap(sc);
            ck.qTrinhThanhToan();
            this.phuongThucThanhToan = ck;
        } else {
            TienMat tm = new TienMat(this.maHD, this.tinhThanhTien(), nhanVienDangNhap);
            tm.nhap(sc);
            tm.qTrinhThanhToan();
            this.phuongThucThanhToan = tm;
        }

        this.setTrangThai("DangXuLy");
        System.out.println("[Thành công] Đã lập phiếu nhập kho [" + this.maHD + "] (Trạng thái: Đang xử lý).");
    }

    public boolean duyetNhapKho(DSSanPham dsSP, Nguoidung nguoiDuyet) {
        if (!(nguoiDuyet instanceof QuanLyCuaHang) && !(nguoiDuyet instanceof Giamdoc)) {
            System.out.println("[Lỗi] BẠN KHÔNG CÓ QUYỀN! Chỉ Quản lý hoặc Giám đốc mới được duyệt nhập kho.");
            return false;
        }

        if (this.isDaXuLyThanhCong()) {
            System.out.println("[Thông báo] Hóa đơn nhập [" + this.maHD + "] vốn đã được duyệt và cộng kho trước đó.");
            return false;
        }

        if (dsSP == null) {
            System.out.println("[Lỗi] Không tìm thấy danh sách sản phẩm để cập nhật kho!");
            return false;
        }

        // Tiến hành cộng tồn kho cho từng sản phẩm trong phiếu nhập
        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDonNhap ct = this.dsChiTiet[i];
            SanPham sp = dsSP.timKiem(ct.getMaSP());
            if (sp != null) {
                sp.congTonKho(ct.getSoLuongNhap());
                sp.setDonGiaNhap(ct.getDonGiaNhap()); // Cập nhật đơn giá nhập mới nhất
            }
        }

        this.xacNhanXuLy(); // Chuyển trạng thái sang "DaXuLyThanhCong"
        System.out.println("[Thành công] ĐÃ DUYỆT NHẬP KHO! Hóa đơn [" + this.maHD + "] đã chuyển sang 'DaXuLyThanhCong'.");
        System.out.println("[Thông báo] Hàng hóa đã được cộng chính thức vào kho.");
        return true;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        kiemTraQuyenChinhSua();
        if (maNCC == null || maNCC.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã nhà cung cấp không được để trống!");
        }
        this.maNCC = Ktradulieu.chuanHoaMa(maNCC);
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
        return String.format("HDNHAP;%s;%s;%s;%s;%s;%.0f;%.0f;%.0f;%s;%s",
                this.maHD, this.ngayLap, this.getMaNV(), this.maNCC,
                this.trangThai, this.tinhTongTien(), this.tinhTienThue(),
                this.tinhThanhTien(), pt, (this.ghiChu != null ? this.ghiChu : ""));
    }

    // =========================================================================
    // IN HÓA ĐƠN NHẬP KHO CHI TIẾT (Triển khai abstract method inHoaDon)
    // =========================================================================
    @Override
    public String inHoaDon() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================== PHIẾU NHẬP KHO ==========================\n");
        sb.append("Mã hóa đơn nhập : ").append(this.maHD).append("\n");
        sb.append("Ngày lập        : ").append(this.ngayLap).append("\n");
        sb.append("Nhân viên lập   : [").append(this.getMaNV()).append("] ")
                .append(this.nguoiLap != null ? this.nguoiLap.getTenND() : "").append("\n");
        sb.append("Nhà cung cấp    : ").append(this.maNCC).append("\n");
        sb.append("Trạng thái      : ").append(this.trangThai).append("\n");
        if (this.ghiChu != null && !this.ghiChu.isEmpty()) {
            sb.append("Ghi chú         : ").append(this.ghiChu).append("\n");
        }
        sb.append("--------------------------------------------------------------------\n");
        sb.append(String.format("%-4s | %-8s | %-20s | %-6s | %-12s | %-12s\n",
                "STT", "MÃ SP", "TÊN SẢN PHẨM", "SL", "ĐƠN GIÁ", "THÀNH TIỀN"));
        sb.append("--------------------------------------------------------------------\n");
        for (int i = 0; i < this.soLuongCT; i++) {
            CTHDonNhap ct = this.dsChiTiet[i];
            sb.append(String.format("%-4d | %-8s | %-20s | %-6d | %-12s | %-12s\n",
                    (i + 1), ct.getMaSP(), ct.getTenSP(), ct.getSoLuongNhap(),
                    Ktradulieu.dinhDangTien(ct.getDonGiaNhap()),
                    Ktradulieu.dinhDangTien(ct.tinhThanhTien())));
        }
        sb.append("--------------------------------------------------------------------\n");
        sb.append("Tổng tiền hàng  : ").append(Ktradulieu.dinhDangTien(this.tinhTongTien())).append("\n");
        sb.append("Tiền thuế VAT   : ").append(Ktradulieu.dinhDangTien(this.tinhTienThue())).append("\n");
        sb.append("TỔNG THANH TOÁN : ").append(Ktradulieu.dinhDangTien(this.tinhThanhTien())).append("\n");
        if (this.phuongThucThanhToan != null) {
            sb.append("Hình thức TT    : ").append(this.phuongThucThanhToan.getLoaiThanhToan())
                    .append(" (").append(this.phuongThucThanhToan.getTrangThai()).append(")\n");
        }
        sb.append("====================================================================\n");
        return sb.toString();
    }
}