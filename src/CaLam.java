package src;

import java.time.LocalTime;
import java.util.Scanner;

public class CaLam {
    private String maCa;
    private String tenCa;       // Ca Sang, Ca Chieu, Ca Toi
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private double heSoLuongCa; // vd ca toi he so 1.2

    public CaLam() {
        this.maCa = "";
        this.tenCa = "";
        this.gioBatDau = LocalTime.of(6, 0);
        this.gioKetThuc = LocalTime.of(14, 0);
        this.heSoLuongCa = 1.0;
    }

    public CaLam(String maCa, String tenCa, LocalTime gioBatDau, LocalTime gioKetThuc, double heSoLuongCa) {
        this.maCa = maCa;
        this.tenCa = tenCa;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.heSoLuongCa = heSoLuongCa;
    }

    public String getMaCa() { return maCa; }
    public void setMaCa(String maCa) { this.maCa = maCa; }

    public String getTenCa() { return tenCa; }
    public void setTenCa(String tenCa) { this.tenCa = tenCa; }

    public LocalTime getGioBatDau() { return gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public double getHeSoLuongCa() { return heSoLuongCa; }

    public void nhap(Scanner sc) {
        System.out.print("Nhap Ma Ca: ");
        this.maCa = sc.nextLine();
        System.out.print("Nhap Ten Ca (Sang/Chieu/Toi): ");
        this.tenCa = sc.nextLine();
        System.out.print("Nhap Gio bat dau (HH:mm, vd 06:00): ");
        this.gioBatDau = LocalTime.parse(sc.nextLine());
        System.out.print("Nhap Gio ket thuc (HH:mm, vd 14:00): ");
        this.gioKetThuc = LocalTime.parse(sc.nextLine());
        System.out.print("Nhap He so luong ca (vd 1.0 hoac 1.2): ");
        this.heSoLuongCa = Double.parseDouble(sc.nextLine());
    }

    @Override
    public String toString() {
        return String.format("[%s] %-10s | %s -> %s | He so: %.1f",
                maCa, tenCa, gioBatDau, gioKetThuc, heSoLuongCa);
    }
}
