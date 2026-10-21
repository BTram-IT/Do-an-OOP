
import java.util.Scanner;

public class Sanpham {
    private String maSP;
    private String tenSP;
    private double giaNhap;
    private double giaBan;
    private int soLuongTon;
    private String donViTinh;
    private String hanSuDung;

    // Constructor mac dinh
    public Sanpham() {
        maSP = "";
        tenSP = "";
        giaNhap = 0;
        giaBan = 0;
        soLuongTon = 0;
        donViTinh = "";
        hanSuDung = "";
    }

    // Constructor co tham so
    public Sanpham(String maSP, String tenSP,
                   double giaNhap, double giaBan,
                   int soLuongTon, String donViTinh,
                   String hanSuDung) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.giaNhap = giaNhap;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.donViTinh = donViTinh;
        this.hanSuDung = hanSuDung;
    }

    // Getter va Setter
    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public String getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(String hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    // Nhap thong tin san pham
    public void nhap() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhap ma san pham: ");
        maSP = sc.nextLine();

        System.out.print("Nhap ten san pham: ");
        tenSP = sc.nextLine();

        System.out.print("Nhap gia nhap: ");
        giaNhap = sc.nextDouble();

        System.out.print("Nhap gia ban: ");
        giaBan = sc.nextDouble();

        System.out.print("Nhap so luong ton: ");
        soLuongTon = sc.nextInt();
        sc.nextLine();

        System.out.print("Nhap don vi tinh: ");
        donViTinh = sc.nextLine();

        System.out.print("Nhap han su dung: ");
        hanSuDung = sc.nextLine();
    }

    // Xuat thong tin san pham
    public void xuat() {
        System.out.println("Ma san pham: " + maSP);
        System.out.println("Ten san pham: " + tenSP);
        System.out.println("Gia nhap: " + giaNhap);
        System.out.println("Gia ban: " + giaBan);
        System.out.println("So luong ton: " + soLuongTon);
        System.out.println("Don vi tinh: " + donViTinh);
        System.out.println("Han su dung: " + hanSuDung);
    }

    // Tinh loi nhuan tren mot don vi san pham
    public double tinhLoiNhuanDonVi() {
        return giaBan - giaNhap;
    }
}
