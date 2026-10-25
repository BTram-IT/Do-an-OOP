import java.util.Scanner;

public class Nguoidung {
    protected String hoten;
    protected String sdt;
    protected String email;
    protected String diachi;

    public Nguoidung() {
        this.hoten = "";
        this.sdt = "";
        this.email = "";
        this.diachi = "";
    }

    public Nguoidung(String hoten, String sdt, String email, String diachi) {
        this.hoten = hoten;
        this.sdt = sdt;
        this.email = email;
        this.diachi = diachi;
    }

    public String getHoten() {
        return hoten;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiachi() {
        return diachi;
    }

    public void setDiachi(String diachi) {
        this.diachi = diachi;
    }

    public void Nhap() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap ho ten: ");
        this.hoten = sc.nextLine();
        System.out.print("Nhap so dien thoai: ");
        this.sdt = sc.nextLine();
        System.out.print("Nhap email: ");
        this.email = sc.nextLine();
        System.out.print("Nhap dia chi: ");
        this.diachi = sc.nextLine();
    }

    public void Xuat() {
        System.out.printf("Ho ten: %-20s | SDT: %-12s | Email: %-22s | Dia chi: %s%n",
                hoten, sdt, email, diachi);
    }

    @Override
    public String toString() {
        return "Nguoidung{" +
                "hoten='" + hoten + '\'' +
                ", sdt='" + sdt + '\'' +
                ", email='" + email + '\'' +
                ", diachi='" + diachi + '\'' +
                '}';
    }
}
