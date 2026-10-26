import java.util.Scanner;

public class CaLam {
    private String maCa;
    private String tenCa;
    private String gioBatDau;
    private String gioKetThuc;
    private double luongTheoCa;

    public CaLam() {
    }

    public CaLam(String maCa, String tenCa, String gioBatDau, String gioKetThuc, double luongTheoCa) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.luongTheoCa = luongTheoCa;
    }

    // Getters và Setters
    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }

    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }

    public String getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(String gioBatDau) { this.gioBatDau = gioBatDau; }

    public String getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(String gioKetThuc) { this.gioKetThuc = gioKetThuc; }

    public double getLuongTheoCa() { return luongTheoCa; }
    public void setLuongTheoCa(double luongTheoCa) { this.luongTheoCa = luongTheoCa; }

    public void nhap() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap ma ca (VD: CA1, CA2): ");
        this.maCa = sc.nextLine();
        System.out.print("Nhap ten ca (Ca sang / Ca chieu / Ca toi): ");
        this.tenCa = sc.nextLine();
        System.out.print("Nhap gio bat dau (VD: 07:00): ");
        this.gioBatDau = sc.nextLine();
        System.out.print("Nhap gio ket thuc (VD: 12:00): ");
        this.gioKetThuc = sc.nextLine();
        System.out.print("Nhap muc luong cua ca: ");
        this.luongTheoCa = Double.parseDouble(sc.nextLine());
    }

    public void xuat() {
        System.out.printf("| %-8s | %-15s | %-10s - %-10s | %,.0f VND |\n", 
                maCa, tenCa, gioBatDau, gioKetThuc, luongTheoCa);
    }
}
