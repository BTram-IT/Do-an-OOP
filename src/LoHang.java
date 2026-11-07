import java.time.LocalDate;
import java.util.Locale;

public class LoHang {

    public static final String TIEN_TO_MA = "LO";
    private static int boDem = 0;

    private final String maLo;
    private final String maSP;
    private final String maHoaDonNhap;
    private final LocalDate ngayNhap;
    private final LocalDate hanSuDung;
    private final int soLuongNhap;
    private int soLuongCon;
    private final long giaNhap;

    public LoHang(String maLo, String maSP, String maHoaDonNhap, LocalDate ngayNhap,
                  LocalDate hanSuDung, int soLuongNhap, int soLuongCon, long giaNhap) {
        this.maLo = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maLo, "Mã lô"));
        this.maSP = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maSP, "Mã sản phẩm"));
        this.maHoaDonNhap = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maHoaDonNhap, "Mã hóa đơn nhập"));
        if (ngayNhap == null) {
            throw new IllegalArgumentException("Ngày nhập không được null.");
        }
        if (soLuongNhap <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
        }
        if (soLuongCon < 0 || soLuongCon > soLuongNhap) {
            throw new IllegalArgumentException("Số lượng còn phải từ 0 đến số lượng nhập (" + soLuongNhap + ").");
        }
        if (hanSuDung != null && hanSuDung.isBefore(ngayNhap)) {
            throw new IllegalArgumentException("Hạn sử dụng không được trước ngày nhập.");
        }
        this.ngayNhap = ngayNhap;
        this.hanSuDung = hanSuDung;
        this.soLuongNhap = soLuongNhap;
        this.soLuongCon = soLuongCon;
        this.giaNhap = SanPham.kiemTraGiaNhap(giaNhap);

        boDem = Math.max(boDem, SanPham.laySoTuMa(this.maLo, TIEN_TO_MA));
    }

    public LoHang(String maLo, String maSP, String maHoaDonNhap, LocalDate ngayNhap,
                  LocalDate hanSuDung, int soLuongNhap, long giaNhap) {
        this(maLo, maSP, maHoaDonNhap, ngayNhap, hanSuDung, soLuongNhap, soLuongNhap, giaNhap);
    }

    public static String maKeTiep() {
        return String.format(Locale.ROOT, "%s%03d", TIEN_TO_MA, boDem + 1);
    }

    public boolean isHetHan(LocalDate ngay) {
        if (hanSuDung == null) {
            return false;
        }
        return ngay != null && !ngay.isBefore(hanSuDung);
    }

    public boolean sapHetHan(LocalDate ngay, int soNgay) {
        if (hanSuDung == null || ngay == null || soNgay < 0 || isHetHan(ngay)) {
            return false;
        }
        return !hanSuDung.isAfter(ngay.plusDays(soNgay));
    }

    public static boolean uuTienHon(LoHang a, LoHang b) {
        if (a == null) return false;
        if (b == null) return true;

        if (a.hanSuDung != null && b.hanSuDung == null) return true;
        if (a.hanSuDung == null && b.hanSuDung != null) return false;

        if (a.hanSuDung != null && !a.hanSuDung.equals(b.hanSuDung)) {
            return a.hanSuDung.isBefore(b.hanSuDung);
        }

        return a.ngayNhap.isBefore(b.ngayNhap);
    }

    public void giam(int soLuong) {
        if (soLuong <= 0 || soLuong > soLuongCon) {
            throw new IllegalArgumentException("Số lượng giảm không hợp lệ.");
        }
        soLuongCon -= soLuong;
    }

    public void tang(int soLuong) {
        if (soLuong <= 0 || (long) soLuongCon + soLuong > soLuongNhap) {
            throw new IllegalArgumentException("Số lượng tăng không hợp lệ.");
        }
        soLuongCon += soLuong;
    }

    public String toDongFile() {
        return "LO|" + maLo + "|" + maSP + "|" + maHoaDonNhap + "|" + ngayNhap + "|"
                + (hanSuDung == null ? "" : hanSuDung) + "|" + soLuongNhap + "|" + soLuongCon + "|" + giaNhap;
    }

    public static LoHang tuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);
        if (t.length != 9 || !t[0].equals("LO")) {
            throw new IllegalArgumentException("Dòng lô hàng không đúng định dạng.");
        }
        LocalDate hsd = t[5].isEmpty() ? null : LocalDate.parse(t[5]);
        return new LoHang(t[1], t[2], t[3], LocalDate.parse(t[4]), hsd,
                Integer.parseInt(t[6]), Integer.parseInt(t[7]), Long.parseLong(t[8]));
    }

    public String getMaLo() { return maLo; }
    public String getMaSP() { return maSP; }
    public String getMaHoaDonNhap() { return maHoaDonNhap; }
    public LocalDate getNgayNhap() { return ngayNhap; }
    public LocalDate getHanSuDung() { return hanSuDung; }
    public int getSoLuongNhap() { return soLuongNhap; }
    public int getSoLuongCon() { return soLuongCon; }
    public long getGiaNhap() { return giaNhap; }

    @Override
    public String toString() {
        return "Lô " + maLo + " (nhập: " + SanPham.dinhDangNgay(ngayNhap)
                + ", HSD: " + SanPham.dinhDangNgay(hanSuDung)
                + ", còn: " + soLuongCon + "/" + soLuongNhap
                + ", giá nhập: " + SanPham.dinhDangTien(giaNhap) + ")";
    }
}