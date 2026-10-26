import java.util.Scanner;

public abstract class Khuyenmai {
    private String maKM;
    private String tenKM;

    public Khuyenmai() {
        this.maKM = "KM00";
        this.tenKM = "Khong KM";
    }

    public Khuyenmai(String maKM, String tenKM) {
        this.maKM = maKM;
        this.tenKM = tenKM;
    }

    public Khuyenmai(Khuyenmai other) {
        this.maKM = other.maKM;
        this.tenKM = other.tenKM;
    }

    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }
    public String getTenKM() { return tenKM; }
    public void setTenKM(String tenKM) { this.tenKM = tenKM; }

    public void nhap() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap ma KM: ");
        this.maKM = sc.nextLine();
        System.out.print("Nhap ten chuong trinh KM: ");
        this.tenKM = sc.nextLine();
    }

    public void xuat() {
        System.out.printf("KM: [%s] %s", maKM, tenKM);
    }

    public abstract double tinhTienGiam(double tongTienHang);
}

class TheoPhanTram extends Khuyenmai {
    private double phanTram;

    public TheoPhanTram() {
        super();
        this.phanTram = 10;
    }

    public TheoPhanTram(String maKM, String tenKM, double phanTram) {
        super(maKM, tenKM);
        this.phanTram = phanTram;
    }

    public TheoPhanTram(TheoPhanTram other) {
        super(other);
        this.phanTram = other.phanTram;
    }

    public double getPhanTram() { return phanTram; }
    public void setPhanTram(double phanTram) { this.phanTram = phanTram; }

    @Override
    public void nhap() {
        Scanner sc = new Scanner(System.in);
        super.nhap();
        System.out.print("Nhap phan tram giam (%): ");
        this.phanTram = Double.parseDouble(sc.nextLine());
    }

    @Override
    public double tinhTienGiam(double tongTienHang) {
        return tongTienHang * (phanTram / 100.0);
    }
}

class GiamTrucTiep extends Khuyenmai {
    private double soTienGiam;

    public GiamTrucTiep() {
        super();
        this.soTienGiam = 10000;
    }

    public GiamTrucTiep(String maKM, String tenKM, double soTienGiam) {
        super(maKM, tenKM);
        this.soTienGiam = soTienGiam;
    }

    public GiamTrucTiep(GiamTrucTiep other) {
        super(other);
        this.soTienGiam = other.soTienGiam;
    }

    public double getSoTienGiam() { return soTienGiam; }
    public void setSoTienGiam(double soTienGiam) { this.soTienGiam = soTienGiam; }

    @Override
    public void nhap() {
        Scanner sc = new Scanner(System.in);
        super.nhap();
        System.out.print("Nhap so tien giam truc tiep: ");
        this.soTienGiam = Double.parseDouble(sc.nextLine());
    }

    @Override
    public double tinhTienGiam(double tongTienHang) {
        return Math.min(soTienGiam, tongTienHang);
    }
}

class MuaTang extends Khuyenmai {
    private double giaTriQuaTang;

    public MuaTang() {
        super();
        this.giaTriQuaTang = 15000;
    }

    public MuaTang(String maKM, String tenKM, double giaTriQuaTang) {
        super(maKM, tenKM);
        this.giaTriQuaTang = giaTriQuaTang;
    }

    public MuaTang(MuaTang other) {
        super(other);
        this.giaTriQuaTang = other.giaTriQuaTang;
    }

    public double getGiaTriQuaTang() { return giaTriQuaTang; }
    public void setGiaTriQuaTang(double giaTriQuaTang) { this.giaTriQuaTang = giaTriQuaTang; }

    @Override
    public void nhap() {
        Scanner sc = new Scanner(System.in);
        super.nhap();
        System.out.print("Nhap gia tri san pham tang kem: ");
        this.giaTriQuaTang = Double.parseDouble(sc.nextLine());
    }

    @Override
    public double tinhTienGiam(double tongTienHang) {
        return tongTienHang >= 40000 ? giaTriQuaTang : 0;
    }
}