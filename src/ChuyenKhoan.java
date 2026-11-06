import java.util.Scanner;
public class ChuyenKhoan extends ThanhToan {
    private static final Scanner sc = Main.sc;


    private String maGDNganHang;
    private String tenNguoiChuyen;
    private String soTKChuyen;
    private String tenNganHang;

    public ChuyenKhoan() {
        this("");
    }

    public ChuyenKhoan(String maGD) {
        super(maGD, "Chuyen khoan");
        this.maGDNganHang = "";
        this.tenNguoiChuyen = "";
        this.soTKChuyen = "";
        this.tenNganHang = "";
    }

    @Override
    public void nhap() {
        maGDNganHang = Ktradulieu.nhapChuoi(sc, "Ma giao dich ngan hang: ", "^\\w{4,30}$", "Ma giao dich gom 4-30 chu cai / chu so, vui long nhap lai!");
        tenNguoiChuyen = Ktradulieu.nhapChuoi(sc, "Ten nguoi chuyen: ", "^[\\p{L}][\\p{L} .'-]{1,49}$", "Ten chi gom chu cai va khoang trang (2-50 ky tu), vui long nhap lai!");
        soTKChuyen = Ktradulieu.nhapChuoi(sc, "So tai khoan chuyen: ", "^\\d{6,20}$", "So tai khoan gom 6-20 chu so, vui long nhap lai!");
        tenNganHang = Ktradulieu.nhapChuoi(sc, "Ten ngan hang: ", "^[^|]{1,200}$", "Khong duoc de trong, khong qua 200 ky tu va khong chua ky tu '|', vui long nhap lai!");
    }

    /** Thanh cong khi da co ma giao dich ngan hang va so tai khoan chuyen. */
    @Override
    public boolean qTrinhThanhToan() {
        return ketThuc(!maGDNganHang.isEmpty() && !soTKChuyen.isEmpty());
    }

    public String getMaGDNganHang() {
        return maGDNganHang;
    }

    public String getTenNguoiChuyen() {
        return tenNguoiChuyen;
    }

    public String getSoTKChuyen() {
        return soTKChuyen;
    }

    public String getTenNganHang() {
        return tenNganHang;
    }

    @Override
    public String toString() {
        return super.toString() + " | GD NH: " + maGDNganHang + " | " + tenNguoiChuyen
                + " | STK: " + soTKChuyen + " | " + tenNganHang;
    }
}
