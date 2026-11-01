import java.util.Scanner;
public class ChuyenKhoan extends ThanhToan {
    private static final Scanner sc = TienIch.sc;

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
        maGDNganHang = Ktradulieu.nhapChuoi(sc, "Ma giao dich ngan hang: ", TienIch.REGEX_MA_GD_NGAN_HANG, TienIch.LOI_MA_GD_NGAN_HANG);
        tenNguoiChuyen = Ktradulieu.nhapChuoi(sc, "Ten nguoi chuyen: ", TienIch.REGEX_TEN, TienIch.LOI_TEN);
        soTKChuyen = Ktradulieu.nhapChuoi(sc, "So tai khoan chuyen: ", TienIch.REGEX_SO_TAI_KHOAN, TienIch.LOI_SO_TAI_KHOAN);
        tenNganHang = Ktradulieu.nhapChuoi(sc, "Ten ngan hang: ", TienIch.REGEX_VAN_BAN, TienIch.LOI_VAN_BAN);
    }

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
