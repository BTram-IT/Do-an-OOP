
package hoadon;

import util.BangConsole;
import util.HienThiBang;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Scanner;

import person.Nguoidung;
import person.NhanVien;
import person.DSNhanVien;
import util.Ktradulieu;
import thanhtoan.ThanhToan;
import thanhtoan.TienMat;
import thanhtoan.ChuyenKhoan;

public abstract class HoaDon implements IHoaDon, HienThiBang {

    protected String maHD;
    protected LocalDate ngayLap;
    protected Nguoidung nguoiLap;
    protected String trangThai;
    protected String ghiChu;

    public HoaDon() {
        this.maHD = "";
        this.ngayLap = LocalDate.now();
        this.trangThai = "DangXuLy";
        this.ghiChu = "";
        this.nguoiLap = null;
    }

    public HoaDon(String maHD, Nguoidung nguoiLap,
                  LocalDate ngayLap, String trangThai, String ghiChu) {

        if (maHD == null || maHD.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống!");
        }

        if (ngayLap == null) {
            throw new IllegalArgumentException("Ngày lập không hợp lệ!");
        }

        if (trangThai == null || trangThai.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống!");
        }

        // Gán trực tiếp để khôi phục được hóa đơn đã hoàn tất.
        this.maHD = Ktradulieu.chuanHoaMa(maHD);
        this.nguoiLap = nguoiLap;
        this.ngayLap = ngayLap;
        this.trangThai = chuanHoaTrangThai(trangThai);
        this.ghiChu = ghiChu != null ? ghiChu.trim() : "";
    }

    public void nhapThongTin(Scanner sc, Nguoidung nhanVienDangNhap,
                             DSHoadon dsHD) {

        if (sc == null) {
            throw new IllegalArgumentException("Scanner không hợp lệ!");
        }

        if (nhanVienDangNhap == null || !nhanVienDangNhap.coQuyen(
                this instanceof Hoadonban ? "QUYEN_BAN_HANG" : "QUYEN_NHAP_KHO")) {
            throw new IllegalArgumentException("Tài khoản không có quyền lập hóa đơn hoặc đã bị khóa!");
        }

        System.out.println("\n--- TẠO HÓA ĐƠN MỚI ---");

        String tienTo = (this instanceof Hoadonban)
                ? "HDB"
                : (this instanceof HoadonNhap ? "HDN" : "HD");

        while (true) {
            System.out.print("Nhập mã hóa đơn (Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                String maMoi = dsHD != null
                        ? dsHD.sinhMaTuDong(tienTo)
                        : tienTo + "001";

                this.setMaHD(maMoi);
                System.out.println("-> Mã tự tạo: " + this.maHD);
                break;
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);

                if (!maChuan.startsWith(tienTo)) {
                    System.out.println("Mã hóa đơn phải bắt đầu bằng "
                            + tienTo + "!");
                    continue;
                }

                if (dsHD != null && dsHD.timKiem(maChuan) != null) {
                    System.out.println("Mã hóa đơn đã tồn tại!");
                    continue;
                }

                this.setMaHD(maChuan);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        this.setNguoiLap(nhanVienDangNhap);

        System.out.println("Nhân viên lập đơn: ["
                + this.nguoiLap.getMaND() + "] "
                + this.nguoiLap.getTenND());

        this.setNgayLap(LocalDate.now());

        System.out.println("Ngày lập hóa đơn: " + Ktradulieu.dinhDangNgay(this.ngayLap));

        this.setTrangThai("DangXuLy");

        this.setGhiChu(Ktradulieu.nhapChuoiTuyChon(
                sc, "Nhập ghi chú hóa đơn (Enter để bỏ qua): ",
                ".+", ""));
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        kiemTraQuyenChinhSua();

        if (maHD == null || maHD.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống!");
        }

        this.maHD = Ktradulieu.chuanHoaMa(maHD);
    }

    public Nguoidung getNguoiLap() {
        return nguoiLap;
    }

    public void setNguoiLap(Nguoidung nguoiLap) {
        kiemTraQuyenChinhSua();

        if (nguoiLap == null) {
            throw new IllegalArgumentException(
                    "Nhân viên lập hóa đơn không được để trống!");
        }

        this.nguoiLap = nguoiLap;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        kiemTraQuyenChinhSua();

        if (ngayLap == null) {
            throw new IllegalArgumentException(
                    "Ngày lập không được để trống!");
        }

        this.ngayLap = ngayLap;
    }

    // Kiểm tra hóa đơn đã bị hủy chưa
    public boolean isDaHuy() {
        return "DaHuy".equalsIgnoreCase(trangThai);
    }

    // Thực hiện hủy hóa đơn
    public void huyHoaDon() {
        if (isDaXuLyThanhCong() || isDaThanhToan()) {
            throw new IllegalStateException(
                    "Hóa đơn đã hoàn tất, không thể hủy!");
        }

        if (isDaHuy()) {
            throw new IllegalStateException(
                    "Hóa đơn này đã được hủy trước đó!");
        }

        setTrangThai("DaHuy");
    }
    @Override
    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        if (trangThai == null || trangThai.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Trạng thái không được để trống!");
        }

        String trangThaiMoi = chuanHoaTrangThai(trangThai);
        if (isDaThanhToan() && "DaHuy".equals(trangThaiMoi)) {
            throw new IllegalStateException("Hóa đơn đã thanh toán, không thể hủy!");
        }

        // Hóa đơn đã hoàn tất không được đổi sang trạng thái khác.
        if (isDaXuLyThanhCong()
                && !"DaXuLyThanhCong".equalsIgnoreCase(trangThaiMoi)) {
            throw new IllegalStateException(
                    "Hóa đơn đã hoàn tất, không thể thay đổi trạng thái!");
        }

        // Hóa đơn đã hủy không được chuyển lại trạng thái khác.
        if (isDaHuy()
                && !"DaHuy".equalsIgnoreCase(trangThaiMoi)) {
            throw new IllegalStateException(
                    "Hóa đơn đã hủy, không thể thay đổi trạng thái!");
        }

        this.trangThai = trangThaiMoi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        kiemTraQuyenChinhSua();
        this.ghiChu = ghiChu != null ? ghiChu.trim() : "";
    }

    protected void kiemTraQuyenChinhSua() {
        if (!coTheChinhSua()) {
            throw new IllegalStateException(
                    "Hóa đơn [" + maHD
                            + "] đã thanh toán, hoàn tất hoặc đã hủy, không được phép chỉnh sửa!");
        }
    }

    public abstract ThanhToan getPhuongThucThanhToan();

    public boolean isDaThanhToan() {
        ThanhToan tt = getPhuongThucThanhToan();
        return tt != null && tt.isThanhCong();
    }

    public boolean coTheChinhSua() {
        return !isDaThanhToan() && !isDaXuLyThanhCong() && !isDaHuy();
    }

    public void suaGhiChu(Scanner sc) {
        kiemTraQuyenChinhSua();
        setGhiChu(Ktradulieu.nhapChuoiTuyChon(sc, "Nhập ghi chú mới (Enter để bỏ trống): ", ".*", ""));
    }

    protected ThanhToan chonVaThanhToan(Scanner sc, Nguoidung nv, boolean chiTien) {
        int chon = Ktradulieu.luaChon(sc, "Chọn thanh toán (1. Tiền mặt | 2. Chuyển khoản | 0. "
                + (chiTien ? "Hủy thanh toán" : "Quay lại sửa") + "): ", 0, 2);
        if (chon == 0) return null;
        ThanhToan tt = chon == 1 ? new TienMat(maHD, tinhThanhTien(), nv)
                : new ChuyenKhoan(maHD, tinhThanhTien(), nv);
        tt.setChiTien(chiTien);
        return tt.thucHienThanhToan(sc) ? tt : null;
    }

    /** Khôi phục chi tiết thanh toán của cả hóa đơn bán và nhập, không phát sinh giao dịch. */
    public void khoiPhucChiTietThanhToanTuFile(double tienDua, String maGD, String tenNguoi,
                                             String soTK, String tenNH) {
        if (!Double.isFinite(tienDua) || tienDua < 0) {
            throw new IllegalArgumentException("Số tiền đã chi/nhận trong file không hợp lệ!");
        }
        ThanhToan tt = getPhuongThucThanhToan();
        if (tt instanceof TienMat) {
            TienMat tm = (TienMat) tt;
            tm.setTienKhachDua(tienDua);
            tm.setTienThoi(Math.max(0, tienDua - tm.getSoTien()));
        } else if (tt instanceof ChuyenKhoan) {
            ChuyenKhoan ck = (ChuyenKhoan) tt;
            if (maGD != null && !maGD.isEmpty()) ck.setMaGDNganHang(maGD);
            if (tenNguoi != null && !tenNguoi.isEmpty()) ck.setTenNguoiChuyen(tenNguoi);
            if (soTK != null && !soTK.isEmpty()) ck.setSoTKChuyen(soTK);
            if (tenNH != null && !tenNH.isEmpty()) ck.setTenNganHang(tenNH);
        }
    }

    protected String chiTietThanhToanToFile() {
        ThanhToan tt = getPhuongThucThanhToan();
        if (tt instanceof TienMat) {
            return String.format(java.util.Locale.US, ";%.0f;;;;", ((TienMat) tt).getTienKhachDua());
        }
        if (tt instanceof ChuyenKhoan) {
            ChuyenKhoan ck = (ChuyenKhoan) tt;
            return ";0;" + lamSachTruongFile(ck.getMaGDNganHang()) + ";" + lamSachTruongFile(ck.getTenNguoiChuyen())
                    + ";" + lamSachTruongFile(ck.getSoTKChuyen()) + ";" + lamSachTruongFile(ck.getTenNganHang());
        }
        return ";0;;;;";
    }

    private static String lamSachTruongFile(String value) {
        return value == null ? "" : value.replace(';', ',').replace('\n', ' ').replace('\r', ' ').trim();
    }

    public boolean isDaXuLyThanhCong() {
        return "DaXuLyThanhCong".equalsIgnoreCase(this.trangThai);
    }

    @Override
    public void xacNhanXuLy() {
        if (isDaXuLyThanhCong()) {
            return;
        }

        if (isDaHuy()) throw new IllegalStateException("Hóa đơn đã hủy, không thể xác nhận xử lý!");
        this.trangThai = "DaXuLyThanhCong";
    }

    public String getMaNV() {
        return this.nguoiLap != null
                ? this.nguoiLap.getMaND()
                : "KHONG_RO";
    }

    @Override
    public abstract double tinhTongTien();

    @Override
    public abstract String inHoaDon();

    public abstract double tinhThanhTien();

    public abstract String getLoaiHD();

    private static String chuanHoaTrangThai(String trangThai) {
        if (trangThai != null) {
            for (String hopLe : new String[]{"DangXuLy", "DaXuLyThanhCong", "DaHuy"}) {
                if (hopLe.equalsIgnoreCase(trangThai.trim())) return hopLe;
            }
        }
        throw new IllegalArgumentException("Trạng thái hóa đơn không hợp lệ!");
    }

    public void kiemTraDuLieuFile() {
        if (maHD == null || !maHD.matches("(?i)HD(B|N)?\\d+") || ngayLap == null
                || nguoiLap == null || getMaNV().isBlank()) {
            throw new IllegalArgumentException("Thông tin hóa đơn chưa hợp lệ để lưu file!");
        }
        chuanHoaTrangThai(trangThai);
        if (ghiChu != null && (ghiChu.contains(";") || ghiChu.contains("\n") || ghiChu.contains("\r"))) {
            throw new IllegalArgumentException("Ghi chú không được chứa dấu ';' hoặc xuống dòng!");
        }
        if (!Double.isFinite(tinhThanhTien()) || tinhThanhTien() < 0) {
            throw new IllegalArgumentException("Tổng thanh toán không hợp lệ!");
        }
    }

    /** Khôi phục hóa đơn theo định dạng hiện tại, giữ nguyên ưu đãi lịch sử. */
    public static HoaDon tuDongFile(String dong, DSNhanVien dsNV) {
        if (dong == null || dong.isBlank()) throw new IllegalArgumentException("Dòng hóa đơn rỗng!");
        String[] p = dong.split(";", -1);
        boolean laBan = "HDBAN".equalsIgnoreCase(p[0].trim());
        boolean laNhap = "HDNHAP".equalsIgnoreCase(p[0].trim());
        if ((!laBan && !laNhap) || (laBan && p.length != 13 && p.length != 18)
                || (laNhap && p.length != 11 && p.length != 17 && p.length != 18)) {
            throw new IllegalArgumentException("Loại hoặc số trường hóa đơn không hợp lệ!");
        }
        DateTimeFormatter dinhDang = DateTimeFormatter.ofPattern("dd/MM/uuuu")
                .withResolverStyle(ResolverStyle.STRICT);
        LocalDate ngay = LocalDate.parse(p[2].trim(), dinhDang);
        Nguoidung nv = dsNV == null ? null : dsNV.timKiemTheoMa(p[3].trim());
        if (nv == null) {
            nv = new NhanVien();
            nv.setMaND(p[3]);
            nv.setTenND("Nhân viên " + p[3].trim());
        }
        int cotTienCuoi = laBan ? 10 : 8;
        for (int i = 6; i <= cotTienCuoi; i++) {
            double giaTri = Double.parseDouble(p[i].trim());
            if (!Double.isFinite(giaTri) || giaTri < 0) {
                throw new IllegalArgumentException("Số tiền trong hóa đơn không hợp lệ!");
            }
        }
        HoaDon hd;
        if (laBan) {
            Hoadonban hdb = new Hoadonban(p[1], nv, ngay, p[5], p[12], p[4]);
            hdb.khoiPhucThanhToanTuFile(Double.parseDouble(p[9]), Double.parseDouble(p[10]), p[11]);
            if (p.length == 18) {
                hdb.khoiPhucChiTietThanhToanTuFile(Double.parseDouble(p[13]), p[14], p[15], p[16], p[17]);
            }
            hd = hdb;
        } else {
            HoadonNhap hdn = new HoadonNhap(p[1], nv, ngay, p[5], p[10], p[4]);
            hdn.khoiPhucThanhToanTuFile(Double.parseDouble(p[8]), p[9]);
            if (p.length >= 17) {
                String trangThaiTT = p[11].trim();
                if (!ThanhToan.THANH_CONG.equals(trangThaiTT) && !ThanhToan.CHO_THANH_TOAN.equals(trangThaiTT)
                        && !ThanhToan.THAT_BAI.equals(trangThaiTT)) {
                    throw new IllegalArgumentException("Trạng thái thanh toán trong file không hợp lệ!");
                }
                ThanhToan tt = hdn.getPhuongThucThanhToan();
                if (tt == null && !ThanhToan.CHO_THANH_TOAN.equals(trangThaiTT)) {
                    throw new IllegalArgumentException("Thiếu phương thức thanh toán của hóa đơn nhập!");
                }
                if (tt != null) tt.setTrangThai(trangThaiTT);
                hdn.khoiPhucChiTietThanhToanTuFile(Double.parseDouble(p[12]), p[13], p[14], p[15], p[16]);
                if (p.length == 18) {
                    if (tt != null) {
                        if (p[17].isBlank()) throw new IllegalArgumentException("Thiếu mã người thanh toán hóa đơn nhập!");
                        tt.setMaNV(p[17]);
                    } else if (!p[17].isBlank()) {
                        throw new IllegalArgumentException("Hóa đơn chưa có thanh toán không được có mã người thanh toán!");
                    }
                }
            }
            hd = hdn;
        }
        hd.kiemTraDuLieuFile();
        return hd;
    }

    protected abstract String layMaDoiTac();

    @Override
    public String[] layTieuDeBang() {
        return new String[]{"MÃ HĐ", "LOẠI", "NGÀY LẬP", "NV LẬP", "ĐỐI TÁC", "TỔNG THANH TOÁN", "TRẠNG THÁI", "GHI CHÚ"};
    }

    @Override
    public Object[] layDongBang() {
        return new Object[]{maHD, getLoaiHD(), Ktradulieu.dinhDangNgay(ngayLap), getMaNV(),
                layMaDoiTac(), Ktradulieu.dinhDangTien(tinhThanhTien()), trangThai, ghiChu};
    }

    @Override
    public String toString() {
        return BangConsole.tuDoiTuong(this);
    }
}
