import java.time.LocalDate;

// Một dòng của hóa đơn nhập (HoadonNhap): nhập sản phẩm nào, bao nhiêu, GIÁ NHẬP BAO NHIÊU, HẠN SỬ DỤNG nào ở lần nhập này.
// Khi phiếu được xác nhận, mỗi dòng sinh ra đúng MỘT lô hàng (LoHang) mang hạn sử dụng và số lượng của dòng đó.
// Không có setter: dòng đã tạo thì không sửa được, muốn đổi thì xóa dòng và thêm lại (khi phiếu chưa xác nhận).
// - giaNhap: đơn giá trên hóa đơn nhà cung cấp, CHƯA thuế (đồng).
// - thueSuatNhap: phần trăm thuế của lần nhập này, độc lập với thuế bán của SanPham.
// - tenSP: tên sản phẩm tại lúc nhập, để sau này đổi tên sản phẩm không làm sai phiếu cũ.
// - hanSuDung: hạn của lô này; null = hàng không có hạn (chỉ cho phép với loại sản phẩm không bắt buộc hạn).
public class CTHDonNhap {

    private String maSP;
    private String tenSP;
    private int soLuongNhap;
    private long giaNhap;
    private double thueSuatNhap;
    private LocalDate hanSuDung;

    public CTHDonNhap(String maSP, String tenSP, int soLuongNhap, long giaNhap, double thueSuatNhap,
                      LocalDate hanSuDung) {
        this.maSP = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maSP, "Mã sản phẩm"));
        this.tenSP = SanPham.kiemTraVanBan(tenSP, "Tên sản phẩm");
        if (soLuongNhap <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
        }
        if (giaNhap < 0 || giaNhap > SanPham.GIA_TOI_DA) {
            throw new IllegalArgumentException("Giá nhập phải từ 0 đến " + SanPham.GIA_TOI_DA + " đồng.");
        }
        try {
            Math.multiplyExact((long) soLuongNhap, giaNhap); // thành tiền không được tràn long
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Thành tiền (số lượng x giá nhập) quá lớn.");
        }
        this.soLuongNhap = soLuongNhap;
        this.giaNhap = giaNhap;
        this.thueSuatNhap = SanPham.kiemTraThueSuat(thueSuatNhap);
        this.hanSuDung = hanSuDung;
    }

    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public long getGiaNhap() {
        return giaNhap;
    }

    public double getThueSuatNhap() {
        return thueSuatNhap;
    }

    // null = không có hạn sử dụng
    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    // Thành tiền = số lượng x giá nhập (chưa thuế) - Tương ứng yêu cầu Tongtien()
    public long Tongtien() {
        return soLuongNhap * giaNhap; // đã kiểm tra không tràn ở constructor
    }

    // Đổi tên phụ trợ để tương thích ngược nếu các file khác đang gọi getThanhTien()
    public long getThanhTien() {
        return Tongtien();
    }

    // Tiền thuế nhập của dòng = thành tiền x thueSuatNhap%, làm tròn đến đồng
    public long tienThueNhap() {
        return Math.round(Tongtien() * thueSuatNhap / 100.0);
    }

    // Dòng ghi file: CT|maSP|tenSP|soLuongNhap|giaNhap|thueSuatNhap|hanSuDung
    public String toDongFile() {
        String hanChuoi = "";
        if (hanSuDung != null) {
            hanChuoi = hanSuDung.toString();
        }
        return "CT|" + maSP + "|" + tenSP + "|" + soLuongNhap + "|" + giaNhap + "|" + thueSuatNhap + "|" + hanChuoi;
    }

    // Đọc lại từ dòng file, sai định dạng thì ném IllegalArgumentException
    public static CTHDonNhap tuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);
        if (t.length != 7 || !t[0].equals("CT")) {
            throw new IllegalArgumentException("Dòng chi tiết cần dạng CT|maSP|tenSP|soLuong|giaNhap|thueSuat|hanSuDung.");
        }
        LocalDate han = null;
        if (!t[6].isEmpty()) {
            han = LocalDate.parse(t[6]);
        }
        return new CTHDonNhap(t[1], t[2], Integer.parseInt(t[3]), Long.parseLong(t[4]), Double.parseDouble(t[5]), han);
    }

    @Override
    public String toString() {
        String han = "không HSD";
        if (hanSuDung != null) {
            han = "HSD " + TienIch.dinhDangNgay(hanSuDung);
        }
        return maSP + " - " + tenSP + " | SL: " + soLuongNhap
                + " | Giá nhập: " + TienIch.dinhDangTien(giaNhap)
                + " | Thành tiền: " + TienIch.dinhDangTien(Tongtien())
                + " | Thuế nhập: " + thueSuatNhap + "%"
                + " | " + han;
    }
}