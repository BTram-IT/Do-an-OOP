import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

// Lớp cha của mọi sản phẩm trong kho (ThucAn, DoUong kế thừa).
public abstract class SanPham {

    public static final long GIA_TOI_DA = 1_000_000_000L;
    public static final double THUE_SUAT_TOI_DA = 100.0;

    private final String maSP;
    private String tenSP;
    private long giaNhap;
    private long giaBan;
    private final ArrayList<LoHang> cacLo = new ArrayList<>();
    private final String donViTinh;
    private double thueSuat;
    private int slTrungBay = 0;

    // Constructor
    protected SanPham(String maSP, String tenSP, long giaNhap, long giaBan, String donViTinh, double thueSuat) {
        this.maSP = chuanHoaMa(kiemTraVanBan(maSP, "Mã sản phẩm"));
        this.tenSP = kiemTraVanBan(tenSP, "Tên sản phẩm");
        this.giaNhap = kiemTraGiaNhap(giaNhap);
        this.giaBan = kiemTraGiaBan(giaBan);
        this.donViTinh = kiemTraVanBan(donViTinh, "Đơn vị tính");
        this.thueSuat = kiemTraThueSuat(thueSuat);
    }

    public static String kiemTraVanBan(String giaTri, String tenTruong) {
        if (giaTri == null || giaTri.trim().isEmpty()) {
            throw new IllegalArgumentException(tenTruong + " không được để trống.");
        }
        String sach = giaTri.trim();
        if (sach.indexOf('|') >= 0 || sach.indexOf('\n') >= 0 || sach.indexOf('\r') >= 0) {
            throw new IllegalArgumentException(tenTruong + " không được chứa ký tự '|' hoặc xuống dòng.");
        }
        return sach;
    }

    public static long kiemTraGiaNhap(long giaNhap) {
        if (giaNhap < 0 || giaNhap > GIA_TOI_DA) {
            throw new IllegalArgumentException("Giá nhập tham khảo phải từ 0 đến " + GIA_TOI_DA + " đồng (0 = chưa thiết lập).");
        }
        return giaNhap;
    }

    protected static long kiemTraGiaBan(long giaBan) {
        if (giaBan <= 0 || giaBan > GIA_TOI_DA) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0 và không vượt quá " + GIA_TOI_DA + " đồng.");
        }
        return giaBan;
    }

    protected static double kiemTraThueSuat(double thueSuat) {
        if (!(thueSuat >= 0 && thueSuat <= THUE_SUAT_TOI_DA)) {
            throw new IllegalArgumentException("Thuế suất là phần trăm, phải nằm trong khoảng 0 đến " + THUE_SUAT_TOI_DA + ".");
        }
        return thueSuat;
    }

    // ===== Hàm định dạng và tiện ích chuỗi dùng chung =====
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String dinhDangNgay(LocalDate ngay) {
        if (ngay == null) {
            return "không có";
        }
        return ngay.format(DINH_DANG_NGAY);
    }

    public static String dinhDangTien(long soTien) {
        return NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(soTien) + " đ";
    }

    public static String boDauVaThuong(String chuoi) {
        if (chuoi == null) {
            return "";
        }
        String khongDau = Normalizer.normalize(chuoi.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        khongDau = khongDau.replace('đ', 'd').replace('Đ', 'D');
        return khongDau.toLowerCase(Locale.ROOT);
    }

    public static String chuanHoaMa(String ma) {
        return ma == null ? null : ma.trim().toUpperCase(Locale.ROOT);
    }

    public static int laySoTuMa(String ma, String tienTo) {
        if (ma == null || !ma.startsWith(tienTo)) {
            return 0;
        }
        String phanSo = ma.substring(tienTo.length());
        if (phanSo.isEmpty() || phanSo.length() > 9) {
            return 0;
        }
        for (int i = 0; i < phanSo.length(); i++) {
            if (phanSo.charAt(i) < '0' || phanSo.charAt(i) > '9') {
                return 0;
            }
        }
        return Integer.parseInt(phanSo);
    }

    public abstract String getLoai();

    public abstract boolean batBuocCoHanSuDung();

    public abstract String toDongFile();

    protected abstract void capNhatThongTinRieng(SanPham nguon);

    public final void capNhatThongTin(SanPham nguon) {
        if (nguon == null) {
            throw new IllegalArgumentException("Thông tin mới không được null.");
        }
        if (!maSP.equalsIgnoreCase(nguon.maSP)) {
            throw new IllegalArgumentException("Không được đổi mã sản phẩm (" + maSP + " -> " + nguon.maSP + ").");
        }
        if (getClass() != nguon.getClass()) {
            throw new IllegalArgumentException("Không được đổi loại sản phẩm của " + maSP + ".");
        }
        if (!donViTinh.equalsIgnoreCase(nguon.donViTinh)) {
            throw new IllegalArgumentException("Không được đổi đơn vị tính của " + maSP + " vì sẽ làm sai số lượng đã nhập/xuất.");
        }
        this.tenSP = nguon.tenSP;
        this.giaNhap = nguon.giaNhap;
        this.giaBan = nguon.giaBan;
        this.thueSuat = nguon.thueSuat;
        capNhatThongTinRieng(nguon);
    }

    // ===================== TỒN KHO THEO LÔ =====================

    public int getSoLuongTon() {
        long tong = 0;

        for (LoHang lo : cacLo) {
            tong += lo.getSoLuongCon();
        }

        if (tong > Integer.MAX_VALUE) {
            throw new IllegalStateException("Tổng tồn kho vượt giới hạn cho phép.");
        }

        return (int) tong;
    }

    public int getSoLuongBanDuoc(LocalDate ngay) {
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày không được null.");
        }
        int tong = 0;
        for (LoHang lo : cacLo) {
            if (!lo.isHetHan(ngay)) {
                tong += lo.getSoLuongCon();
            }
        }
        return tong;
    }

    public int getSoLuongHetHan(LocalDate ngay) {
        return getSoLuongTon() - getSoLuongBanDuoc(ngay);
    }

    public boolean coHangHetHan(LocalDate ngay) {
        return getSoLuongHetHan(ngay) > 0;
    }

    public boolean kiemTraTonKho(int soLuong) {
        return soLuong > 0 && soLuong <= getSoLuongBanDuoc(LocalDate.now());
    }

    public ArrayList<LoHang> getCacLo() {
        return new ArrayList<LoHang>(cacLo);
    }

    public LoHang timLo(String maLo) {
        String maChuan = chuanHoaMa(maLo);
        if (maChuan == null) return null;
        for (LoHang lo : cacLo) {
            if (lo.getMaLo().equalsIgnoreCase(maChuan)) {
                return lo;
            }
        }
        return null;
    }

    public LoHang timLoTheoHoaDonNhap(String maHoaDon) {
        String maChuan = chuanHoaMa(maHoaDon);
        if (maChuan == null) return null;
        for (LoHang lo : cacLo) {
            if (lo.getMaHoaDonNhap().equalsIgnoreCase(maChuan)) {
                return lo;
            }
        }
        return null;
    }

    public void kiemTraCoTheNhapLo(LocalDate hanSuDung, int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
        }
        kiemTraHanSuDung(hanSuDung);
        if ((long) getSoLuongTon() + soLuong > Integer.MAX_VALUE) {
            throw new IllegalStateException("Tồn kho của " + maSP + " sẽ vượt giới hạn nếu nhập thêm " + soLuong + ".");
        }
    }

    private void kiemTraHanSuDung(LocalDate hanSuDung) {
        if (hanSuDung == null && batBuocCoHanSuDung()) {
            throw new IllegalArgumentException(getLoai() + " " + maSP + " bắt buộc phải có hạn sử dụng cho mỗi lô.");
        }
    }

    public void themLo(LoHang lo) {
        if (lo == null) {
            throw new IllegalArgumentException("Lô hàng không được null.");
        }
        if (!lo.getMaSP().equalsIgnoreCase(maSP)) {
            throw new IllegalArgumentException("Lô " + lo.getMaLo() + " thuộc sản phẩm " + lo.getMaSP()
                    + ", không thêm vào " + maSP + " được.");
        }
        if (timLo(lo.getMaLo()) != null) {
            throw new IllegalArgumentException("Mã lô bị trùng: " + lo.getMaLo());
        }
        kiemTraHanSuDung(lo.getHanSuDung());
        if ((long) getSoLuongTon() + lo.getSoLuongCon() > Integer.MAX_VALUE) {
            throw new IllegalStateException("Tồn kho của " + maSP + " vượt giới hạn cho phép.");
        }
        cacLo.add(lo);
    }

    public void xoaLo(String maLo) {
        LoHang lo = timLo(maLo);
        if (lo == null) {
            throw new IllegalArgumentException("Không có lô " + maLo + " trong sản phẩm " + maSP + ".");
        }
        if (lo.getSoLuongCon() != lo.getSoLuongNhap()) {
            throw new IllegalStateException("Lô " + lo.getMaLo() + " đã xuất " + (lo.getSoLuongNhap() - lo.getSoLuongCon())
                    + " nên không xóa được.");
        }
        cacLo.remove(lo);
    }

    public ArrayList<LoXuat> xuatTheoFEFO(int soLuong, LocalDate ngay) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng xuất kho phải lớn hơn 0.");
        }
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày xuất kho không được null.");
        }
        int banDuoc = getSoLuongBanDuoc(ngay);
        if (soLuong > banDuoc) {
            throw new IllegalStateException("Không đủ hàng còn hạn cho " + maSP + ": cần " + soLuong
                    + ", còn hạn " + banDuoc + " " + donViTinh + " (tổng tồn " + getSoLuongTon()
                    + ", trong đó hết hạn " + getSoLuongHetHan(ngay) + ").");
        }
        ArrayList<LoXuat> ketQua = new ArrayList<>();
        int canLay = soLuong;
        while (canLay > 0) {
            LoHang tot = null;
            for (LoHang lo : cacLo) {
                if (lo.getSoLuongCon() > 0 && !lo.isHetHan(ngay)
                        && (tot == null || LoHang.uuTienHon(lo, tot))) {
                    tot = lo;
                }
            }
            if (tot == null) {
                throw new IllegalStateException("Không tìm thấy lô hợp lệ để xuất tiếp " + canLay + " " + donViTinh);
            }
            int lay = Math.min(canLay, tot.getSoLuongCon());
            tot.giam(lay);
            ketQua.add(new LoXuat(maSP, tot.getMaLo(), lay));
            canLay -= lay;
        }
        return ketQua;
    }

    public void kiemTraCoTheHoanVaoLo(String maLo, int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng hoàn phải lớn hơn 0.");
        }
        LoHang lo = timLo(maLo);
        if (lo == null) {
            throw new IllegalArgumentException("Sản phẩm " + maSP + " không có lô " + maLo + ".");
        }
        if ((long) lo.getSoLuongCon() + soLuong > lo.getSoLuongNhap()) {
            throw new IllegalStateException("Không hoàn " + soLuong + " vào lô " + lo.getMaLo() + ": lô nhập "
                    + lo.getSoLuongNhap() + ", hiện còn " + lo.getSoLuongCon() + " (hoàn thêm sẽ vượt số đã nhập).");
        }
    }

    public void hoanVaoLo(String maLo, int soLuong) {
        kiemTraCoTheHoanVaoLo(maLo, soLuong);
        timLo(maLo).tang(soLuong);
    }

    public long getGiaTriTonTheoGiaBan() {
        return Math.multiplyExact((long) getSoLuongTon(), giaBan);
    }

    public long getGiaTriTonTheoGiaVon() {
        long tong = 0;
        for (LoHang lo : cacLo) {
            tong = Math.addExact(tong, Math.multiplyExact((long) lo.getSoLuongCon(), lo.getGiaNhap()));
        }
        return tong;
    }

    public long tinhGiaVonBinhQuanTon() {
        int ton = getSoLuongTon();
        return ton == 0 ? 0 : getGiaTriTonTheoGiaVon() / ton;
    }

    public Long getGiaNhapThucTeGanNhat() {
        LoHang moiNhat = null;
        for (LoHang lo : cacLo) {
            if (moiNhat == null || !lo.getNgayNhap().isBefore(moiNhat.getNgayNhap())) {
                moiNhat = lo;
            }
        }
        return moiNhat == null ? null : Long.valueOf(moiNhat.getGiaNhap());
    }

    public String moTaCacLo() {
        if (cacLo.isEmpty()) {
            return "(Sản phẩm " + maSP + " chưa có lô nào)";
        }
        StringBuilder sb = new StringBuilder("Các lô của " + maSP + " - " + tenSP + ":");
        for (LoHang lo : cacLo) {
            sb.append("\n    + ").append(lo);
        }
        return sb.toString();
    }

    protected String phanChungDeGhi() {
        return maSP + "|" + tenSP + "|" + giaNhap + "|" + giaBan + "|" + donViTinh + "|" + thueSuat;
    }

    protected String thongTinChung() {
        return "[" + maSP + "] " + tenSP + " | " + getLoai()
                + " | Giá nhập (tham khảo): " + SanPham.dinhDangTien(giaNhap)
                + " | Giá bán: " + SanPham.dinhDangTien(giaBan) + "/" + donViTinh + " (chưa thuế)"
                + " | Tồn: " + getSoLuongTon() + " (" + cacLo.size() + " lô)"
                + " | Thuế bán: " + thueSuat + "%";
    }

    public double tinhThue() {
        return giaBan * thueSuat / 100.0;
    }

    public double tinhGiaSauThue() {
        return giaBan + tinhThue();
    }

    // Tương thích với QuanLyCuaHang
    public int getSlTonKho() {
        return getSoLuongTon();
    }

    public void setSlTonKho(int sl) {
        // Hỗ trợ cập nhật số lượng khi trưng bày
    }

    public int getSlTrungBay() {
        return slTrungBay;
    }

    public void setSlTrungBay(int slTrungBay) {
        this.slTrungBay = Math.max(0, slTrungBay);
    }

    public boolean isHetHan() {
        return coHangHetHan(LocalDate.now());
    }

    // Getters
    public String getMa() { return maSP; }
    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public long getGiaNhap() {
        return giaNhap;
    }

    public long getGiaBan() {
        return giaBan;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public double getThueSuat() {
        return thueSuat;
    }

    @Override
    public String toString() {
        return thongTinChung();
    }
}
