import java.util.Scanner;

public class ChuyenKhoan extends ThanhToan {

    private String maGDNganHang;
    private String tenNguoiChuyen;
    private String soTKChuyen;
    private String tenNganHang;

    public ChuyenKhoan() {
        this("");
    }

    public ChuyenKhoan(String maGD) {
        super(maGD, "Chuyển khoản");
        this.maGDNganHang = "";
        this.tenNguoiChuyen = "";
        this.soTKChuyen = "";
        this.tenNganHang = "";
    }

    @Override
    public void nhap() {
        Scanner sc = new Scanner(System.in);
        nhap(sc);
    }

    @Override
    public void nhap(Scanner sc) {
        if (sc == null) sc = new Scanner(System.in);

        maGDNganHang = Ktradulieu.nhapChuoi(
                sc,
                "Mã giao dịch ngân hàng: ",
                "^[\\w]{4,30}$",
                "Mã giao dịch gồm 4-30 ký tự chữ hoặc số, vui lòng nhập lại!"
        );

        tenNguoiChuyen = Ktradulieu.nhapChuoi(
                sc,
                "Tên người chuyển khoản: ",
                "^[\\p{L}][\\p{L} .'-]{1,49}$",
                "Tên chỉ gồm chữ cái và khoảng trắng (2-50 ký tự), vui lòng nhập lại!"
        );

        soTKChuyen = Ktradulieu.nhapChuoi(
                sc,
                "Số tài khoản chuyển: ",
                "^\\d{6,20}$",
                "Số tài khoản gồm 6-20 chữ số, vui lòng nhập lại!"
        );

        tenNganHang = Ktradulieu.nhapChuoi(
                sc,
                "Tên ngân hàng: ",
                "^[^|]{1,200}$",
                "Không được để trống và không chứa ký tự '|', vui lòng nhập lại!"
        );
    }


    @Override
    public boolean qTrinhThanhToan() {
        boolean hopLe = (maGDNganHang != null && !maGDNganHang.isEmpty())
                && (soTKChuyen != null && !soTKChuyen.isEmpty());
        return ketThuc(hopLe);
    }

    public boolean thanhToan(Scanner sc, long tongTien) {
        this.soTien = (double) tongTien;
        System.out.println("Tổng tiền cần chuyển: " + Ktradulieu.dinhDangTien(tongTien));
        nhap(sc);
        return qTrinhThanhToan();
    }

    public boolean thanhToan(long tongTien) {
        return thanhToan(new Scanner(System.in), tongTien);
    }

    public String getMaGDNganHang() { return maGDNganHang; }
    public String getTenNguoiChuyen() { return tenNguoiChuyen; }
    public String getSoTKChuyen() { return soTKChuyen; }
    public String getTenNganHang() { return tenNganHang; }

    @Override
    public String toString() {
        return super.toString() + " | GD NH: " + maGDNganHang + " | " + tenNguoiChuyen
                + " | STK: " + soTKChuyen + " | " + tenNganHang;
    }
}