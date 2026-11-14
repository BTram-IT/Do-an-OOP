package thanhtoan;

import java.util.Scanner;
import person.Nguoidung;
import util.Ktradulieu;

public class ChuyenKhoan extends ThanhToan {

    private String maGDNganHang;
    private String tenNguoiChuyen;
    private String soTKChuyen;
    private String tenNganHang;


    public ChuyenKhoan() {
        super();
        this.loaiThanhToan = "ChuyenKhoan";
        this.maGDNganHang = "";
        this.tenNguoiChuyen = "";
        this.soTKChuyen = "";
        this.tenNganHang = "";
    }


    public ChuyenKhoan(String maHD, double soTien, Nguoidung nvDangDangNhap) {
        super(maHD, "ChuyenKhoan", soTien, nvDangDangNhap);
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

        System.out.println("\n========== THANH TOÁN CHUYỂN KHOẢN ==========");
        System.out.println("• Mã giao dịch       : [" + this.maGD + "]");
        System.out.println("• Người thực hiện  : [" + this.maNV + "]");
        System.out.println("• Số tiền cần chuyển : " + Ktradulieu.dinhDangTien(this.soTien));

        while (true) {
            try {
                String nh = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên ngân hàng (VD: Vietcombank, MB Bank, Techcombank): ");
                setTenNganHang(nh);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                String stk = Ktradulieu.nhapChuoi(sc, "Nhập số tài khoản người chuyển: ", "^\\d{6,20}$",
                        "Số tài khoản phải là chữ số và có độ dài từ 6 đến 20 số!");
                setSoTKChuyen(stk);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage());
            }
        }
        while (true) {
            try {
                String ten = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên người chuyển tiền: ");
                setTenNguoiChuyen(ten);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                String magd = Ktradulieu.nhapChuoi(sc, "Nhập mã giao dịch ngân hàng (Mã FT/GD trên app): ",
                        "^[a-zA-Z0-9]{4,30}$", "Mã giao dịch phải từ 4-30 ký tự chữ và số!");
                setMaGDNganHang(magd);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage());
            }
        }
        qTrinhThanhToan();
    }

    @Override
    public boolean qTrinhThanhToan() {
        // Hợp lệ khi đã nhập đầy đủ mã giao dịch ngân hàng và số tài khoản
        boolean hopLe = (this.maGDNganHang != null && !this.maGDNganHang.isEmpty())
                && (this.soTKChuyen != null && !this.soTKChuyen.isEmpty())
                && (this.soTien > 0);

        if (hopLe) {
            ketThuc(true);
            System.out.println("GIAO DỊCH CHUYỂN KHOẢN THÀNH CÔNG: " + Ktradulieu.dinhDangTien(this.soTien));
            return true;
        } else {
            ketThuc(false);
            System.out.println("GIAO DỊCH CHUYỂN KHOẢN THẤT BẠI: Thiếu thông tin xác thực!");
            return false;
        }
    }

    public boolean thanhToan(Scanner sc, double tongTien) {
        this.soTien = tongTien;
        nhap(sc);
        return isThanhCong();
    }

    public boolean thanhToan(double tongTien) {
        return thanhToan(new Scanner(System.in), tongTien);
    }


    public String getMaGDNganHang() {
        return maGDNganHang;
    }

    public void setMaGDNganHang(String maGDNganHang) {
        if (maGDNganHang == null || maGDNganHang.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã giao dịch ngân hàng không được để trống!");
        }
        this.maGDNganHang = Ktradulieu.chuanHoaMa(maGDNganHang);
    }

    public String getTenNguoiChuyen() {
        return tenNguoiChuyen;
    }

    public void setTenNguoiChuyen(String tenNguoiChuyen) {
        if (tenNguoiChuyen == null || tenNguoiChuyen.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Tên người chuyển khoản không được để trống!");
        }
        this.tenNguoiChuyen = Ktradulieu.chuanHoaChuoi(tenNguoiChuyen);
    }

    public String getSoTKChuyen() {
        return soTKChuyen;
    }

    public void setSoTKChuyen(String soTKChuyen) {
        if (soTKChuyen == null || soTKChuyen.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Số tài khoản không được để trống!");
        }
        String stk = soTKChuyen.trim().replaceAll("\\s+", "");
        if (!stk.matches("^\\d{6,20}$")) {
            throw new IllegalArgumentException("[Lỗi] Số tài khoản phải là chữ số từ 6 đến 20 số!");
        }
        this.soTKChuyen = stk;
    }

    public String getTenNganHang() {
        return tenNganHang;
    }

    public void setTenNganHang(String tenNganHang) {
        if (tenNganHang == null || tenNganHang.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Tên ngân hàng không được để trống!");
        }
        this.tenNganHang = Ktradulieu.chuanHoaChuoi(tenNganHang);
    }

    @Override
    public String toString() {
        return super.toString() + " | Ngân hàng: " + tenNganHang + " (STK: " + soTKChuyen + " - " + tenNguoiChuyen + ")"
                + " | Mã GD NH: " + maGDNganHang;
    }
}