import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Nhanvien extends Nguoidung {
    private String MaNV;
    private LocalDate Ngaybatdaulam;
    private double Luong=6000000;
    private String Vitri;


    public Nhanvien(String maNV, LocalDate ngaybatdaulam, String Vitri, String Hoten, String SDT, String Email, String Diachi) {
        super(Hoten,SDT,Email,Diachi);
        MaNV = maNV;
        Ngaybatdaulam = ngaybatdaulam;
        Vitri = Vitri;
    }

    public Nhanvien() {
        super();
        MaNV = "";
        Ngaybatdaulam = null;
        Vitri = "";
    }

    public String getMaNV() {
        return MaNV;
    }

    public void setMaNV(String maNV) {
        MaNV = maNV;
    }

    public LocalDate getNgaybatdaulam() {
        return Ngaybatdaulam;
    }

    public void setNgaybatdaulam(LocalDate ngaybatdaulam) {
        Ngaybatdaulam = ngaybatdaulam;
    }

    public void setLuong(float luong) {
        Luong = luong;
    }

    public String getVitri() {
        return Vitri;
    }

    public void setVitri(String Vitri) {
        this.Vitri = Vitri;
    }

    Scanner sc = new Scanner(System.in);
    // Nhập nhân viên
    public void Nhap() {
        super.Nhap();
        System.out.println("Nhập mã nhân viên: ");
        MaNV = sc.nextLine();
        System.out.println("Nhập ngày vào làm: ");
        String ngay = sc.nextLine();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Ngaybatdaulam=LocalDate.parse(ngay,dtf);
        System.out.println("Nhập chức vụ: ");
        Vitri = sc.nextLine();
    }

    public void Xuat() {
        super.Xuat();
    }

    @Override
    public String toString() {
        return "Nhanvien{" +
                "MaNV='" + MaNV + '\'' +
                ", Ngaybatdaulam=" + Ngaybatdaulam +
                ", Luong=" + Luong +
                ", Chucvu='" + Vitri + '\'' +
                ", sc=" + sc +
                '}';
    }

    public double Tinhluong(LocalDate ngayTinh) {
        return Luong;
    }

    public double Tinhluong() {
        return Luong;
    }
}
