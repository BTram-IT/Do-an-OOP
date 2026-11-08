
import java.util.Scanner;

public class Giaodien {

    private static Scanner sc = new Scanner(System.in);

    private KeToan keToan;
    private ThuNgan thuNgan;
    private QuanlyKho quanLyKho;
    private QuanLyCuaHang quanLyCuaHang;

    public Giaodien(KeToan keToan, ThuNgan thuNgan,
                    QuanlyKho quanLyKho,
                    QuanLyCuaHang quanLyCuaHang) {
        this.keToan = keToan;
        this.thuNgan = thuNgan;
        this.quanLyKho = quanLyKho;
        this.quanLyCuaHang = quanLyCuaHang;
    }

    // ================= TRANG CHỦ =================

    public void menu() {
        int chon;

        do {
            hienThiTrangChu();
            chon = Ktradulieu.luaChon(
                    sc, "Mời bạn chọn chức năng: ", 0, 4
            );
            switch (chon) {
                case 1:
                    if (dangNhap("QuanLy")) {
                        menuQuanLyCuaHang();
                    }
                    break;
                case 2:
                    if (dangNhap("KeToan")) {
                        menuKeToan();
                    }
                    break;
                case 3:
                    if (dangNhap("ThuNgan")) {
                        menuThuNgan();
                    }
                    break;
                case 4:
                    if (dangNhap("QuanLyKho")) {
                        menuQuanLyKho();
                    }
                    break;
                case 0:
                    System.out.println(
                            "Cảm ơn bạn đã sử dụng chương trình!"
                    );
                    break;
            }
            if (chon != 0) {
                tamDung();
            }

        } while (chon != 0);
    }

    public void hienThiTrangChu() {
        System.out.println("\n============================================================");
        System.out.println("                  QUẢN LÝ CỬA HÀNG");
        System.out.println("                 THỨC ĂN VÀ NƯỚC UỐNG");
        System.out.println("============================================================");
        System.out.println("                    TRANG ĐĂNG NHẬP");
        System.out.println("------------------------------------------------------------");
        System.out.println("  1. Đăng nhập quản lý cửa hàng");
        System.out.println("  2. Đăng nhập kế toán");
        System.out.println("  3. Đăng nhập thu ngân");
        System.out.println("  4. Đăng nhập quản lý kho");
        System.out.println("  0. Thoát chương trình");
        System.out.println("------------------------------------------------------------");
        System.out.println("       CHÚC BẠN MỘT NGÀY LÀM VIỆC HIỆU QUẢ!");
    }

    // ================= ĐĂNG NHẬP =================

    public boolean dangNhap(String chucVu) {
        System.out.println("\n===== ĐĂNG NHẬP =====");
        String tenTaiKhoan = Ktradulieu.nhapChuoiKhongRong(
                sc, "Tên tài khoản: "
        );

        String matKhau = Ktradulieu.nhapChuoiKhongRong(
                sc, "Mật khẩu: "
        );

        String taiKhoan = "";

        switch (chucVu) {
            case "QuanLy":
                taiKhoan = "quanly";
                break;

            case "KeToan":
                taiKhoan = "ketoan";
                break;

            case "ThuNgan":
                taiKhoan = "thungan";
                break;

            case "QuanLyKho":
                taiKhoan = "kho";
                break;

            default:
                System.out.println("Chức vụ không hợp lệ!");
                return false;
        }

        boolean hopLe = tenTaiKhoan.equals(taiKhoan)
                && matKhau.equals("123");

        System.out.println(hopLe
                ? "Đăng nhập thành công!"
                : "Sai tên tài khoản hoặc mật khẩu!");

        return hopLe;
    }

    // ================= MENU KẾ TOÁN =================

    public void menuKeToan() {
        int chon;

        do {
            hienThiMenu("MENU KẾ TOÁN",
                    "1. Thống kê doanh thu",
                    "2. Thống kê lợi nhuận",
                    "3. Thống kê thuế",
                    "4. Thống kê sản phẩm bán được",
                    "5. Thống kê lương nhân viên",
                    "0. Quay lại");

            chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng: ", 0, 5
            );

            switch (chon) {
                case 1:
                    keToan.menuDoanhThu();
                    break;

                case 2:
                    keToan.menuLoiNhuan();
                    break;

                case 3:
                    keToan.menuThue();
                    break;

                case 4:
                    keToan.menuThongKeSanPham();
                    break;

                case 5:
                    keToan.menuLuong();
                    break;

                case 0:
                    break;
            }

            if (chon != 0) {
                tamDung();
            }

        } while (chon != 0);
    }

    // ================= MENU THU NGÂN =================

    public void menuThuNgan() {
        int chon;

        do {
            hienThiMenu("MENU THU NGÂN",
                    "1. Hiển thị thông tin thu ngân",
                    "2. Chọn ca làm việc",
                    "3. Quản lý khách hàng",
                    "4. Tra cứu hóa đơn",
                    "5. In hóa đơn theo khách hàng",
                    "0. Quay lại");

            chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng: ", 0, 5
            );

            switch (chon) {
                case 1:
                    thuNgan.hienThiThongTin();
                    break;

                case 2:
                    thuNgan.chonCaLamViec(sc);
                    break;

                case 3:
                    menuQuanLyKhachHang();
                    break;

                case 4:
                    String maHD = Ktradulieu.nhapChuoiKhongRong(
                            sc, "Nhập mã hóa đơn: "
                    );
                    thuNgan.traCuuHoaDon(maHD);
                    break;

                case 5:
                    String maKH = Ktradulieu.nhapChuoiKhongRong(
                            sc, "Nhập mã khách hàng: "
                    );
                    thuNgan.inDanhSachHoaDonTheoKhachHang(maKH);
                    break;

                case 0:
                    break;
            }

            if (chon != 0) {
                tamDung();
            }

        } while (chon != 0);
    }

    // ================= QUẢN LÝ KHÁCH HÀNG =================

    public void menuQuanLyKhachHang() {
        int chon;

        do {
            hienThiMenu("QUẢN LÝ KHÁCH HÀNG",
                    "1. Hiển thị danh sách khách hàng",
                    "2. Hiển thị danh sách khách hàng VIP",
                    "0. Quay lại");

            chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng: ", 0, 2
            );

            switch (chon) {
                case 1:
                    thuNgan.hienThiDanhSachKhachHang();
                    break;

                case 2:
                    thuNgan.hienThiDanhSachKhachHangVIP();
                    break;

                case 0:
                    break;
            }

            if (chon != 0) {
                tamDung();
            }

        } while (chon != 0);
    }

    // ================= MENU QUẢN LÝ KHO =================

    public void menuQuanLyKho() {
        int chon;

        do {
            hienThiMenu("MENU QUẢN LÝ KHO",
                    "1. Quản lý sản phẩm",
                    "2. Lập phiếu nhập",
                    "3. Xem danh sách phiếu nhập",
                    "4. Kiểm tra tồn kho",
                    "0. Quay lại");

            chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng: ", 0, 4
            );

            switch (chon) {
                case 1:
                    System.out.println("Chức năng quản lý sản phẩm");
                    break;

                case 2:
                    System.out.println("Chức năng lập phiếu nhập");
                    break;

                case 3:
                    System.out.println("Chức năng xem phiếu nhập");
                    break;

                case 4:
                    System.out.println("Chức năng kiểm tra tồn kho");
                    break;

                case 0:
                    break;
            }

            if (chon != 0) {
                tamDung();
            }

        } while (chon != 0);
    }

    // ================= MENU QUẢN LÝ CỬA HÀNG =================

    public void menuQuanLyCuaHang() {
        int chon;

        do {
            hienThiMenu("MENU QUẢN LÝ CỬA HÀNG",
                    "1. Quản lý nhân viên",
                    "2. Quản lý tài khoản",
                    "3. Xem thông tin cửa hàng",
                    "0. Quay lại");

            chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng: ", 0, 3
            );

            switch (chon) {
                case 1:
                    System.out.println("Chức năng quản lý nhân viên");
                    break;

                case 2:
                    System.out.println("Chức năng quản lý tài khoản");
                    break;

                case 3:
                    System.out.println("Chức năng xem thông tin cửa hàng");
                    break;

                case 0:
                    break;
            }

            if (chon != 0) {
                tamDung();
            }

        } while (chon != 0);
    }

    // ================= HỖ TRỢ =================

    private void hienThiMenu(String tieuDe, String... chucNang) {
        System.out.println("\n========== " + tieuDe + " ==========");

        for (String muc : chucNang) {
            System.out.println(muc);
        }
    }

    public void tamDung() {
        System.out.println("\nNhấn Enter để tiếp tục...");
        sc.nextLine();
    }
}
