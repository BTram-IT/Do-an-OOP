import java.util.Locale;

public class DoUong extends SanPham {

    public static final String TIEN_TO_MA = "DU";
    public static final int DUNG_TICH_TOI_DA_ML = 100_000;

    private static int boDem = 0;

    private int dungTichMl;

    // Constructor
    public DoUong(String maSP, String tenSP, long giaNhap, long giaBan,
                  String donViTinh, double thueSuat, int dungTichMl) {

        super(maSP, tenSP, giaNhap, giaBan, donViTinh, thueSuat);

        this.dungTichMl = kiemTraDungTich(dungTichMl);

        // Cập nhật bộ đếm theo mã sản phẩm đã tạo hoặc đọc từ file
        boDem = Math.max(
                boDem,
                laySoTuMa(chuanHoaMa(maSP), TIEN_TO_MA)
        );
    }

    // Kiểm tra dung tích
    private static int kiemTraDungTich(int dungTichMl) {

        if (dungTichMl <= 0 || dungTichMl > DUNG_TICH_TOI_DA_ML) {
            throw new IllegalArgumentException(
                    "Dung tích phải lớn hơn 0 và không vượt quá "
                            + DUNG_TICH_TOI_DA_ML + " ml."
            );
        }

        return dungTichMl;
    }

    // Sinh mã sản phẩm tiếp theo
    public static synchronized String maKeTiep() {

        boDem++;

        return String.format(
                Locale.ROOT,
                "%s%03d",
                TIEN_TO_MA,
                boDem
        );
    }

    // Getter
    public int getDungTichMl() {
        return dungTichMl;
    }

    // Setter
    public void setDungTichMl(int dungTichMl) {
        this.dungTichMl = kiemTraDungTich(dungTichMl);
    }

    // Xác định loại sản phẩm
    @Override
    public String getLoai() {
        return "Đồ uống";
    }

    // Đồ uống không bắt buộc phải có hạn sử dụng
    @Override
    public boolean batBuocCoHanSuDung() {
        return false;
    }

    // Chuyển thông tin sản phẩm thành một dòng để lưu file
    @Override
    public String toDongFile() {
        return "DOUONG|" + phanChungDeGhi() + "|" + dungTichMl;
    }

    // Cập nhật thông tin riêng của đồ uống
    @Override
    protected void capNhatThongTinRieng(SanPham nguon) {

        if (!(nguon instanceof DoUong)) {
            throw new IllegalArgumentException(
                    "Chỉ được cập nhật thông tin từ sản phẩm đồ uống."
            );
        }

        DoUong moi = (DoUong) nguon;

        this.dungTichMl = kiemTraDungTich(moi.dungTichMl);
    }

    // Hiển thị thông tin sản phẩm
    @Override
    public String toString() {
        return thongTinChung() + " | Dung tích: " + dungTichMl + " ml";
    }
}
