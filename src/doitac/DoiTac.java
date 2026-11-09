package doitac;

import java.util.Locale;
import java.util.Scanner;
import util.Ktradulieu;

public abstract class DoiTac {
    protected String maDT, tenDT, diaChi, email, maSoThue;
    protected final String loaiDT;

    public DoiTac(String loaiDT) {
        this.loaiDT = loaiDT == null ? "" : loaiDT.trim();
    }

    public DoiTac(String maDT, String tenDT, String diaChi,
                  String email, String maSoThue, String loaiDT) {
        this(loaiDT);

        if (maDT != null && !maDT.trim().isEmpty()) setMaDT(maDT);
        if (tenDT != null && !tenDT.trim().isEmpty()) setTenDT(tenDT);
        if (diaChi != null && !diaChi.trim().isEmpty()) setDiaChi(diaChi);
        if (email != null && !email.trim().isEmpty()) setEmail(email);
        if (maSoThue != null && !maSoThue.trim().isEmpty()) setMaSoThue(maSoThue);
    }

    public String getMaDT() { return maDT; }
    public String getMa() { return maDT; }
    public String getTenDT() { return tenDT; }
    public String getDiaChi() { return diaChi; }
    public String getEmail() { return email; }
    public String getMaSoThue() { return maSoThue; }
    public String getLoaiDT() { return loaiDT; }

    public void setMaDT(String maDT) {
        String ma = Ktradulieu.chuanHoaMa(maDT);
        if (!ma.matches("[A-Z0-9]+"))
            throw new IllegalArgumentException("Mã đối tác không hợp lệ!");
        this.maDT = ma;
    }

    public void setTenDT(String tenDT) {
        if (tenDT == null || tenDT.trim().isEmpty())
            throw new IllegalArgumentException("Tên đối tác không được để trống!");
        this.tenDT = Ktradulieu.chuanHoaChuoi(tenDT);
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty())
            throw new IllegalArgumentException("Địa chỉ không được để trống!");
        this.diaChi = diaChi.trim();
    }

    public void setEmail(String email) {
        String mail = email == null ? "" : email.trim();
        if (!mail.matches(Ktradulieu.emailRegex))
            throw new IllegalArgumentException("Email không hợp lệ!");
        this.email = mail;
    }

    public void setMaSoThue(String maSoThue) {
        String mst = maSoThue == null ? "" : maSoThue.trim();
        if (!mst.matches("\\d{10}(-\\d{3})?"))
            throw new IllegalArgumentException("Mã số thuế không hợp lệ!");
        this.maSoThue = mst;
    }

    public static final String TIEN_TO_MA = "DT";
    private static int boDem = 0;

    public static synchronized String maKeTiep() {
        boDem++;
        return String.format(Locale.ROOT, "%s%03d", TIEN_TO_MA, boDem);
    }

    public static void capNhatBoDem(String maDT) {
        // Gọi thẳng từ Ktradulieu, không cần viết lại hàm phụ trợ nữa!
        int so = Ktradulieu.laySoTuMa(Ktradulieu.chuanHoaMa(maDT), TIEN_TO_MA);
        if (so > boDem) {
            boDem = so;
        }
    }

    public static void resetBoDem() {
        boDem = 0;
    }
    public void nhap(Scanner sc) {
        if (sc == null) sc = new Scanner(System.in);

        System.out.println("\n--- NHẬP THÔNG TIN ĐỐI TÁC ---");

        // 1. Nhập mã đối tác (Enter = tự lấy mã tiếp theo)
        String maGoiY = maKeTiep();
        String thongBaoMa = String.format("Nhập mã đối tác (Enter = tự tạo %s): ", maGoiY);

        String nhapMa = Ktradulieu.nhapChuoiTuyChon(sc, thongBaoMa, "^[a-zA-Z0-9]+$", "Mã không hợp lệ!");

        this.maDT = nhapMa.isEmpty() ? maGoiY : Ktradulieu.chuanHoaMa(nhapMa);
        capNhatBoDem(this.maDT); // Đảm bảo bộ đếm luôn được cập nhật chính xác

        // 2. Nhập tên đối tác (Có gợi ý ví dụ mẫu)
        String tenNhap = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên đối tác (VD: Công Ty TNHH Thực Phẩm Sạch): ");
        this.tenDT = Ktradulieu.chuanHoaChuoi(tenNhap);

        // 3. Nhập địa chỉ
        String diaChiNhap = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập địa chỉ (VD: 123 Nguyễn Văn Linh, Q.7, TP.HCM): ");
        this.diaChi = Ktradulieu.chuanHoaChuoi(diaChiNhap);

        // 4. Nhập email (Sử dụng regex chuẩn có sẵn trong Ktradulieu)
        this.email = Ktradulieu.nhapChuoi(sc, "Nhập email (VD: lienhe@thucphamsach.vn): ",
                Ktradulieu.emailRegex, "Email không hợp lệ! (Phải đúng định dạng như abc@gmail.com)");

        // 5. Nhập mã số thuế (Sử dụng regex kiểm tra 10 số hoặc kèm nhánh -xxx)
        this.maSoThue = Ktradulieu.nhapChuoi(sc, "Nhập mã số thuế (VD: 0312345678 hoặc 0312345678-001): ",
                "\\d{10}(-\\d{3})?", "Mã số thuế không hợp lệ! (Phải gồm 10 chữ số hoặc kèm nhánh -xxx)");

        System.out.println("-> Thêm đối tác [" + this.maDT + "] thành công!");
    }

    public abstract String toDongFile();

    @Override
    public String toString() {
        return String.format(
                "[%s] %s | Loại: %s | MST: %s | Email: %s | ĐC: %s",
                maDT, tenDT, loaiDT, maSoThue, email, diaChi);
    }
}
