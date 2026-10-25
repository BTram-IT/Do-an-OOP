import java.util.Scanner;

public class Khachhang extends Nguoidung {
    private String maKH;
    private int diemTichLuy;
    private String hangThanhVien;

    public Khachhang() {
        super();
        this.maKH = "";
        this.diemTichLuy = 0;
        this.hangThanhVien = "Thuong";
    }

    public Khachhang(String maKH, String hoten, String sdt, String email, String diachi) {
        super(hoten, sdt, email, diachi);
        this.maKH = maKH;
        this.diemTichLuy = 0;
        this.hangThanhVien = "Thuong";
    }

    public Khachhang(String maKH, String hoten, String sdt, String email, String diachi, int diemTichLuy) {
        super(hoten, sdt, email, diachi);
        this.maKH = maKH;
        this.diemTichLuy = Math.max(0, diemTichLuy);
        capNhatHangThanhVien();
    }

    public void congDiem(int diem) {
        if (diem > 0) {
            this.diemTichLuy += diem;
            capNhatHangThanhVien();
        }
    }

    private void capNhatHangThanhVien() {
        if (diemTichLuy >= 1000) {
            hangThanhVien = "Kim Cuong";
        } else if (diemTichLuy >= 500) {
            hangThanhVien = "Vang";
        } else if (diemTichLuy >= 200) {
            hangThanhVien = "Bac";
        } else {
            hangThanhVien = "Thuong";
        }
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = Math.max(0, diemTichLuy);
        capNhatHangThanhVien();
    }

    public String getHangThanhVien() {
        return hangThanhVien;
    }

    @Override
    public void Nhap() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap ma khach hang: ");
        this.maKH = sc.nextLine();
        super.Nhap();
        System.out.print("Nhap diem tich luy: ");
        try {
            this.diemTichLuy = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            this.diemTichLuy = 0;
        }
        capNhatHangThanhVien();
    }

    @Override
    public void Xuat() {
        System.out.printf("Ma KH: %-8s | Hang: %-10s | Diem: %-6d | ", maKH, hangThanhVien, diemTichLuy);
        super.Xuat();
    }

    @Override
    public String toString() {
        return String.format("Khachhang[MaKH=%s, HoTen=%s, SDT=%s, Hang=%s, Diem=%d]",
                maKH, hoten, sdt, hangThanhVien, diemTichLuy);
    }
}
