package thanhtoan;

import util.BangConsole;

import java.util.Scanner;
import person.Nguoidung;
import util.Ktradulieu;

public class ChuyenKhoan extends ThanhToan {

    private String maGDNganHang;
    private String tenNguoiChuyen;
    private String soTKChuyen;
    private String tenNganHang;
    private boolean daXacNhan;


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
    public void nhap(Scanner sc) {
        if (sc == null) throw new IllegalArgumentException("Scanner không được null!");
        batDauThanhToan();
        this.daXacNhan = false;

        System.out.println("\n========== THANH TOÁN CHUYỂN KHOẢN ==========");
        System.out.println("• Mã giao dịch       : [" + this.maGD + "]");
        System.out.println((chiTien ? "• Người chi tiền     : [" : "• Thu ngân phụ trách : [") + this.maNV + "]");
        System.out.println("• Số tiền cần chuyển : " + Ktradulieu.dinhDangTien(this.soTien));

        while (true) {
            try {
                String nh = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên ngân hàng (0 để hủy thanh toán): ");
                if (nh.equals("0")) {
                    ketThuc(false);
                    return;
                }
                setTenNganHang(nh);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                String stk = Ktradulieu.nhapChuoi(sc, chiTien ? "Nhập tài khoản nhà cung cấp nhận tiền: " : "Nhập số tài khoản người chuyển: ", "^\\d{6,20}$",
                        "Số tài khoản phải là chữ số và có độ dài từ 6 đến 20 số!");
                setSoTKChuyen(stk);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage());
            }
        }
        while (true) {
            try {
                String ten = Ktradulieu.nhapChuoiKhongRong(sc, chiTien ? "Nhập tên người nhận tiền: " : "Nhập tên người chuyển tiền: ");
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
        System.out.println("Nội dung chuyển khoản: " + getMaGD() + " | Số tiền: " + Ktradulieu.dinhDangTien(soTien));
        this.daXacNhan = Ktradulieu.luaChon(sc, chiTien
                ? "Xác nhận cửa hàng đã chuyển tiền? (1: Có / 0: Chưa, quay lại sửa): "
                : "Xác nhận đã nhận tiền chuyển khoản? (1: Có / 0: Chưa, quay lại sửa): ", 0, 1) == 1;
        qTrinhThanhToan();
    }

    @Override
    public boolean qTrinhThanhToan() {
        // Hợp lệ khi đã nhập đầy đủ mã giao dịch ngân hàng và số tài khoản
        boolean hopLe = daXacNhan && (this.maGDNganHang != null && !this.maGDNganHang.isEmpty())
                && (this.soTKChuyen != null && !this.soTKChuyen.isEmpty())
                && (this.tenNguoiChuyen != null && !this.tenNguoiChuyen.isEmpty())
                && (this.tenNganHang != null && !this.tenNganHang.isEmpty())
                && Double.isFinite(this.soTien) && (this.soTien >= 0);

        if (hopLe) {
            ketThuc(true);
            System.out.println("GIAO DỊCH CHUYỂN KHOẢN THÀNH CÔNG: " + Ktradulieu.dinhDangTien(this.soTien));
            return true;
        } else {
            ketThuc(false);
            System.out.println("[Thông báo] Chuyển khoản chưa được xác nhận thành công.");
            return false;
        }
    }

    public boolean thanhToan(Scanner sc, double tongTien) {
        setSoTien(tongTien);
        return thucHienThanhToan(sc);
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
    protected String layChiTietBang() {
        return BangConsole.thongTin(new String[]{"Ngân hàng", "Số tài khoản", chiTien ? "Người nhận" : "Người chuyển", "Mã GD ngân hàng"},
                tenNganHang, soTKChuyen, tenNguoiChuyen, maGDNganHang);
    }
}
