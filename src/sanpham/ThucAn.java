package sanpham;

import java.util.Locale;

public class ThucAn extends SanPham {

    public static final String TIEN_TO_MA = "TA";
    private static int boDem = 0;

    private String nhaSanXuat;

    // Constructor
    public ThucAn(String maSP, String tenSP, long giaNhap, long giaBan,
                  String donViTinh, double thueSuat, String nhaSanXuat) {

        super(maSP, tenSP, giaNhap, giaBan, donViTinh, thueSuat);

        this.nhaSanXuat = kiemTraVanBan(nhaSanXuat, "Nhà sản xuất");

        // Cập nhật bộ đếm theo mã sản phẩm đã tạo hoặc đọc từ file
        boDem = Math.max(
                boDem,
                laySoTuMa(chuanHoaMa(maSP), TIEN_TO_MA)
        );
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
    public String getNhaSanXuat() {
        return nhaSanXuat;
    }

    // Setter
    public void setNhaSanXuat(String nhaSanXuat) {
        this.nhaSanXuat = kiemTraVanBan(nhaSanXuat, "Nhà sản xuất");
    }

    // Xác định loại sản phẩm
    @Override
    public String getLoai() {
        return "Thức ăn";
    }

    // Thức ăn bắt buộc phải có hạn sử dụng
    @Override
    public boolean batBuocCoHanSuDung() {
        return true;
    }

    // Chuyển thông tin sản phẩm thành một dòng để lưu file
    @Override
    public String toDongFile() {
        return "THUCAN|" + phanChungDeGhi() + "|" + nhaSanXuat;
    }

    // Cập nhật thông tin riêng của thức ăn
    @Override
    protected void capNhatThongTinRieng(SanPham nguon) {

        if (!(nguon instanceof ThucAn)) {
            throw new IllegalArgumentException(
                    "Chỉ được cập nhật thông tin từ sản phẩm thức ăn."
            );
        }

        ThucAn moi = (ThucAn) nguon;

        this.nhaSanXuat = kiemTraVanBan(
                moi.nhaSanXuat,
                "Nhà sản xuất"
        );
    }

    // Hiển thị thông tin sản phẩm
    @Override
    public String toString() {
        return thongTinChung() + " | NSX: " + nhaSanXuat;
    }
}
