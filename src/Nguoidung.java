
import java.time.LocalDate;
import java.util.Scanner;

public abstract class Nguoidung {
    protected String ten;
    protected String sdt;
    protected String diaChi;
    protected String email;
    protected LocalDate ngaySinh;

    public Nguoidung() {
        this.ten = "";
        this.sdt = "";
        this.diaChi = "";
        this.email = "";
        this.ngaySinh = null;
    }

    public Nguoidung(String ten, String sdt, String diaChi, String email, LocalDate ngaySinh) {
        setTen(ten);
        setSdt(sdt);
        setDiaChi(diaChi);
        setEmail(email);
        setNgaySinh(ngaySinh);
    }

    public String getTen() { return ten; }
    public void setTen(String ten) {
        if (ten == null || !ten.trim().matches("^[\\p{L}]+(?:[ '-][\\p{L}]+)*$"))
            throw new IllegalArgumentException("Họ tên không hợp lệ!");
        this.ten = Ktradulieu.chuanHoaChuoi(ten);
    }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) {
        sdt = Ktradulieu.chuanHoaMa(sdt);
        if (!sdt.matches(Ktradulieu.phoneRegex))
            throw new IllegalArgumentException("Số điện thoại không hợp lệ!");
        this.sdt = sdt;
    }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) {
        if (diaChi == null || !diaChi.trim().matches(Ktradulieu.diaChiRegex))
            throw new IllegalArgumentException("Địa chỉ không được để trống!");
        this.diaChi = diaChi.trim();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !email.trim().matches(Ktradulieu.emailRegex))
            throw new IllegalArgumentException("Email không hợp lệ!");
        this.email = email.trim();
    }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) {
        if (ngaySinh == null || ngaySinh.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày sinh không hợp lệ!");
        this.ngaySinh = ngaySinh;
    }

    public void nhap(Scanner sc) {
        this.ten = Ktradulieu.chuanHoaChuoi(
                Ktradulieu.nhapChuoi(sc, "Nhập họ tên: ",
                        "^[\\p{L}]+(?:[ '-][\\p{L}]+)*$",
                        "Họ tên không hợp lệ, vui lòng nhập lại!")
        );

        this.sdt = Ktradulieu.nhapChuoi(sc, "Nhập SDT: ",
                Ktradulieu.phoneRegex,
                "Số điện thoại không hợp lệ, vui lòng nhập lại!");

        this.diaChi = Ktradulieu.nhapChuoi(sc, "Nhập địa chỉ: ",
                Ktradulieu.diaChiRegex,
                "Địa chỉ không được để trống, vui lòng nhập lại!");

        this.email = Ktradulieu.nhapChuoi(sc, "Nhập email: ",
                Ktradulieu.emailRegex,
                "Email không hợp lệ, vui lòng nhập lại!");

        this.ngaySinh = Ktradulieu.nhapNgaySinh(sc,
                "Nhập ngày sinh (yyyy-MM-dd): ");
    }

    @Override
    public String toString() {
        return "Tên: " + ten + " | SDT: " + sdt
                + " | Địa chỉ: " + diaChi + " | Email: " + email
                + " | Ngày sinh: " + ngaySinh;
    }
}
