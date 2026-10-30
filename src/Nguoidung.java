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
        this.ngaySinh = LocalDate.now();
    }

    public Nguoidung(String ten, String sdt, String diaChi, String email, LocalDate ngaySinh) {
        this.ten = ten;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.email = email;
        this.ngaySinh = ngaySinh;
    }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public void nhap(Scanner sc) {
        this.ten = Ktradulieu.nhapChuoi(sc, "Nhap ho ten: ");
        this.sdt = Ktradulieu.nhapSDT(sc, "Nhap SDT: ");
        this.diaChi = Ktradulieu.nhapChuoi(sc, "Nhap dia chi: ");
        this.email = Ktradulieu.nhapEmail(sc, "Nhap email: ");
        this.ngaySinh = Ktradulieu.nhapNgay(sc, "Nhap ngay sinh (yyyy-MM-dd): ");
    }

    @Override
    public String toString() {
        return "Ten: " + ten + " | SDT: " + sdt + " | Dia Chi: " + diaChi + " | Email: " + email + " | Ngay sinh: " + ngaySinh;
    }
}
