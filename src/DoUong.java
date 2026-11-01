import java.util.Locale;

// Đồ uống: có dung tích (ml). Hạn sử dụng ở từng lô; đồ uống KHÔNG bắt buộc có hạn (nước suối có thể nhập lô không hạn).
public class DoUong extends SanPham {

    public static final String TIEN_TO_MA = "DU";
    public static final int DUNG_TICH_TOI_DA_ML = 100_000;
    private static int boDem = 0;

    // Mức thuế mặc định của đồ uống (số giả định, nhóm/Kế toán chỉnh khi có quy định)
    public static final double THUE_SUAT_MAC_DINH = 8.0;

    private int dungTichMl;

    public DoUong(String maSP, String tenSP, long giaNhap, long giaBan, String donViTinh,
                  double thueSuat, int dungTichMl) {
        super(maSP, tenSP, giaNhap, giaBan, donViTinh, thueSuat); // giaNhap do SanPham quản lý
        this.dungTichMl = kiemTraDungTich(dungTichMl);

        boDem = Math.max(boDem, laySoTuMa(chuanHoaMa(maSP), TIEN_TO_MA));
    }

    // Không truyền thuế: thuế do lớp DoUong quyết định
    public DoUong(String maSP, String tenSP, long giaNhap, long giaBan, String donViTinh, int dungTichMl) {
        this(maSP, tenSP, giaNhap, giaBan, donViTinh, THUE_SUAT_MAC_DINH, dungTichMl);
    }

    public static String maKeTiep() {
        return String.format(Locale.ROOT, "%s%03d", TIEN_TO_MA, boDem + 1);
    }

    private static int kiemTraDungTich(int dungTichMl) {
        if (dungTichMl <= 0 || dungTichMl > DUNG_TICH_TOI_DA_ML) {
            throw new IllegalArgumentException("Dung tích phải từ 1 đến " + DUNG_TICH_TOI_DA_ML + " ml.");
        }
        return dungTichMl;
    }

    public int getDungTichMl() {
        return dungTichMl;
    }

    // Không public: sửa thông tin sản phẩm đi qua DSSanPham.sua (quy ước, cùng default package)
    void setDungTichMl(int dungTichMl) {
        this.dungTichMl = kiemTraDungTich(dungTichMl);
    }

    @Override
    public String getLoai() {
        return "Đồ uống";
    }

    @Override
    public boolean batBuocCoHanSuDung() {
        return false;
    }

    @Override
    public String toDongFile() {
        return "DOUONG|" + phanChungDeGhi() + "|" + dungTichMl;
    }

    @Override
    protected void capNhatThongTinRieng(SanPham nguon) {
        DoUong moi = (DoUong) nguon;
        this.dungTichMl = moi.dungTichMl;
    }

    @Override
    public String toString() {
        return thongTinChung() + " | Dung tích: " + dungTichMl + "ml";
    }
}
