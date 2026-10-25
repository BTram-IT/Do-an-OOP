import java.time.LocalDate;
import java.util.Locale;

public class ThucAn extends SanPham {

    public static final String TIEN_TO_MA = "TA";
    private static int boDem = 0;

    private String nhaSanXuat;
    private LocalDate hanSuDung;

    public ThucAn(String maSP, String tenSP, long giaBan, int soLuongTon, String donViTinh,
                  double thueSuat, String nhaSanXuat, LocalDate hanSuDung) {
        super(maSP, tenSP, giaBan, soLuongTon, donViTinh, thueSuat);
        this.nhaSanXuat = kiemTraVanBan(nhaSanXuat, "Nhà sản xuất");
        this.hanSuDung = kiemTraHan(hanSuDung);

        boDem = Math.max(boDem, laySoTuMa(chuanHoaMa(maSP), TIEN_TO_MA));
    }

    public static String maKeTiep() {
        return String.format(Locale.ROOT, "%s%03d", TIEN_TO_MA, boDem + 1);
    }

    private static LocalDate kiemTraHan(LocalDate han) {
        if (han == null) {
            throw new IllegalArgumentException("Thức ăn phải có hạn sử dụng.");
        }
        return han;
    }

    public String getNhaSanXuat() {
        return nhaSanXuat;
    }

    public void setNhaSanXuat(String nhaSanXuat) {
        this.nhaSanXuat = kiemTraVanBan(nhaSanXuat, "Nhà sản xuất");
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = kiemTraHan(hanSuDung);
    }

    @Override
    public String getLoai() {
        return "Thức ăn";
    }

    @Override
    public boolean isHetHan(LocalDate ngay) {
        return hanSuDung.isBefore(ngay);
    }

    @Override
    public String toDongFile() {
        return "THUCAN|" + phanChungDeGhi() + "|" + nhaSanXuat + "|" + hanSuDung;
    }

    @Override
    protected void capNhatThongTinRieng(SanPham nguon) {
        ThucAn moi = (ThucAn) nguon;
        this.nhaSanXuat = moi.nhaSanXuat;
        this.hanSuDung = moi.hanSuDung;
    }

    @Override
    public String toString() {
        return thongTinChung() + " | NSX: " + nhaSanXuat
                + " | HSD: " + TienIch.dinhDangNgay(hanSuDung) + (isHetHan() ? " (HẾT HẠN)" : "");
    }
}
