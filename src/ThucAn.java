import java.util.Locale;

// Thức ăn: có nhà sản xuất. Hạn sử dụng KHÔNG nằm ở đây mà ở từng lô hàng (LoHang); thức ăn bắt buộc mỗi lô phải có hạn.
public class ThucAn extends SanPham {

    public static final String TIEN_TO_MA = "TA";
    private static int boDem = 0;

    private String nhaSanXuat;

    // Sản phẩm mới luôn có tồn 0, hàng vào kho qua phiếu nhập (mỗi lần nhập tạo một lô có hạn sử dụng)
    public ThucAn(String maSP, String tenSP, long giaNhap, long giaBan, String donViTinh,
                  double thueSuat, String nhaSanXuat) {
        super(maSP, tenSP, giaNhap, giaBan, donViTinh, thueSuat); // giaNhap do SanPham quản lý
        this.nhaSanXuat = kiemTraVanBan(nhaSanXuat, "Nhà sản xuất");

        boDem = Math.max(boDem, laySoTuMa(chuanHoaMa(maSP), TIEN_TO_MA));
    }

    public static String maKeTiep() {
        return String.format(Locale.ROOT, "%s%03d", TIEN_TO_MA, boDem + 1);
    }

    public String getNhaSanXuat() {
        return nhaSanXuat;
    }

    public void setNhaSanXuat(String nhaSanXuat) {
        this.nhaSanXuat = kiemTraVanBan(nhaSanXuat, "Nhà sản xuất");
    }

    @Override
    public String getLoai() {
        return "Thức ăn";
    }

    @Override
    public double tinhThue() {
        return tinhThueTheoSuat();
    }

    @Override
    public boolean batBuocCoHanSuDung() {
        return true;
    }

    @Override
    public String toDongFile() {
        return "THUCAN|" + phanChungDeGhi() + "|" + nhaSanXuat;
    }

    @Override
    protected void capNhatThongTinRieng(SanPham nguon) {
        ThucAn moi = (ThucAn) nguon;
        this.nhaSanXuat = moi.nhaSanXuat;
    }

    @Override
    public String toString() {
        return thongTinChung() + " | NSX: " + nhaSanXuat;
    }
}
