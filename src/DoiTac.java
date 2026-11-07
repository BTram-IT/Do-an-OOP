
import java.util.Scanner;

public abstract class DoiTac {
    protected String maDT, tenDT, diaChi, email, maSoThue;
    protected final String loaiDT;

    public DoiTac(String loaiDT) {
        this("", "", "", "", "", loaiDT);
    }

    public DoiTac(String maDT, String tenDT, String diaChi,
                  String email, String maSoThue, String loaiDT) {
        this.loaiDT = loaiDT == null ? "" : loaiDT.trim();
        this.maDT = "";
        this.tenDT = "";
        this.diaChi = "";
        this.email = "";
        this.maSoThue = "";

        if (maDT != null && !maDT.trim().isEmpty()) setMaDT(maDT);
        if (tenDT != null && !tenDT.trim().isEmpty()) setTenDT(tenDT);
        if (diaChi != null && !diaChi.trim().isEmpty()) setDiaChi(diaChi);
        if (email != null && !email.trim().isEmpty()) setEmail(email);
        if (maSoThue != null && !maSoThue.trim().isEmpty()) setMaSoThue(maSoThue);
    }

    public String getMaDT() { return maDT; }
    public String getTenDT() { return tenDT; }
    public String getDiaChi() { return diaChi; }
    public String getEmail() { return email; }
    public String getMaSoThue() { return maSoThue; }
    public String getLoaiDT() { return loaiDT; }

    public void setMaDT(String maDT) {
        String ma = Ktradulieu.chuanHoaMa(maDT);
        if (!ma.isEmpty() && !ma.matches("[A-Z0-9]+"))
            throw new IllegalArgumentException("Mã đối tác không hợp lệ!");
        this.maDT = ma;
    }

    public void setTenDT(String tenDT) {
        if (tenDT == null || tenDT.trim().isEmpty())
            throw new IllegalArgumentException("Tên đối tác không được để trống!");
        this.tenDT = tenDT.trim();
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty())
            throw new IllegalArgumentException("Địa chỉ không được để trống!");
        this.diaChi = diaChi.trim();
    }

    public void setEmail(String email) {
        if (email == null || !email.trim().matches(Ktradulieu.emailRegex))
            throw new IllegalArgumentException("Email không hợp lệ!");
        this.email = email.trim();
    }

    public void setMaSoThue(String maSoThue) {
        if (maSoThue == null || !maSoThue.trim().matches("\\d{10}(-\\d{3})?"))
            throw new IllegalArgumentException("Mã số thuế không hợp lệ!");
        this.maSoThue = maSoThue.trim();
    }

    public void nhap(Scanner sc) {
        if (sc == null)
            throw new IllegalArgumentException("Scanner không hợp lệ!");

        setMaDT(Ktradulieu.nhapChuoi(
                sc, "Nhập mã đối tác: ", "[a-zA-Z0-9]+", "Mã không hợp lệ!"));

        setTenDT(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên đối tác: "));
        setDiaChi(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập địa chỉ: "));

        setEmail(Ktradulieu.nhapChuoi(
                sc, "Nhập email: ", Ktradulieu.emailRegex, "Email không hợp lệ!"));

        setMaSoThue(Ktradulieu.nhapChuoi(
                sc, "Nhập mã số thuế: ", "\\d{10}(-\\d{3})?",
                "Mã số thuế không hợp lệ!"));
    }

    public abstract void nhap();

    public abstract String toDongFile();

    @Override
    public String toString() {
        return String.format(
                "[%s] %s | Loại: %s | MST: %s | Email: %s | ĐC: %s",
                maDT, tenDT, loaiDT, maSoThue, email, diaChi);
    }
}
