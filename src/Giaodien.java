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
            chon = nhapSo("Mời bạn chọn chức năng: ");

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
                    System.out.println("Cảm ơn bạn đã sử dụng chương trình!");
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }

            if (chon != 0) {
                tamDung();
            }

        } while (chon != 0);
    }

    public void hienThiTrangChu() {
        System.out.println();
        System.out.println("  ============================================================");
        System.out.println("  ||                                                        ||");
        System.out.println("  ||                  QUẢN LÝ CỬA HÀNG                       ||");
        System.out.println("  ||                                                        ||");
        System.out.println("  ||                THỨC ĂN VÀ NƯỚC UỐNG                    ||");
        System.out.println("  ||                                                        ||");
        System.out.println("  ============================================================");
        System.out.println();
        System.out.println("                 ===== TRANG ĐĂNG NHẬP =====");
        System.out.println();

        System.out.println("  +----------------------------------------------------------+");
        System.out.println("  |  [1]  ĐĂNG NHẬP QUẢN LÝ CỬA HÀNG                         |");
        System.out.println("  +----------------------------------------------------------+");
        System.out.println("  |  [2]  ĐĂNG NHẬP KẾ TOÁN                                  |");
        System.out.println("  +----------------------------------------------------------+");
        System.out.println("  |  [3]  ĐĂNG NHẬP THU NGÂN                                 |");
        System.out.println("  +----------------------------------------------------------+");
        System.out.println("  |  [4]  ĐĂNG NHẬP QUẢN LÝ KHO                              |");
        System.out.println("  +----------------------------------------------------------+");
        System.out.println("  |  [0]  THOÁT CHƯƠNG TRÌNH                                 |");
        System.out.println("  +----------------------------------------------------------+");

        System.out.println();
        System.out.println("       CHÚC BẠN MỘT NGÀY LÀM VIỆC HIỆU QUẢ!");
        System.out.println();
    }

    // ================= ĐĂNG NHẬP =================

    public boolean dangNhap(String chucVu) {
        System.out.println("\n===== ĐĂNG NHẬP =====");

        System.out.print("Tên tài khoản: ");
        String tenTaiKhoan = sc.nextLine();

        System.out.print("Mật khẩu: ");
        String matKhau = sc.nextLine();

        boolean hopLe = false;

        switch (chucVu) {
            case "QuanLy":
                hopLe = tenTaiKhoan.equals("quanly")
                        && matKhau.equals("123");
                break;

            case "KeToan":
                hopLe = tenTaiKhoan.equals("ketoan")
                        && matKhau.equals("123");
                break;

            case "ThuNgan":
                hopLe = tenTaiKhoan.equals("thungan")
                        && matKhau.equals("123");
                break;

            case "QuanLyKho":
                hopLe = tenTaiKhoan.equals("kho")
                        && matKhau.equals("123");
                break;
        }

        if (hopLe) {
            System.out.println("Đăng nhập thành công!");
        } else {
            System.out.println("Sai tên tài khoản hoặc mật khẩu!");
        }

        return hopLe;
    }

    // ================= MENU KẾ TOÁN =================

    public void menuKeToan() {
        int chon;

        do {
            System.out.println("\n===== MENU KẾ TOÁN =====");
            System.out.println("1. Thống kê doanh thu");
            System.out.println("2. Thống kê lợi nhuận");
            System.out.println("3. Thống kê thuế");
            System.out.println("4. Thống kê sản phẩm bán được");
            System.out.println("5. Thống kê lương nhân viên");
            System.out.println("0. Quay lại");

            chon = nhapSo("Chọn chức năng: ");

            switch (chon) {
                case 1:
                    System.out.println("Chức năng thống kê doanh thu");
                    keToan.menuDoanhThu();
                    break;

                case 2:
                    System.out.println("Chức năng thống kê lợi nhuận");
                    keToan.menuLoiNhuan();
                    break;

                case 3:
                    System.out.println("Chức năng thống kê thuế");
                    keToan.menuThue();
                    break;

                case 4:
                    System.out.println("Chức năng thống kê sản phẩm bán được");
                    keToan.menuThongKeSanPham();
                    break;

                case 5:
                    System.out.println("Chức năng thống kê lương nhân viên");
                    keToan.menuLuong();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }

    // ================= MENU THU NGÂN =================

    public void menuThuNgan() {
        int chon;

        do {
            System.out.println("\n===== MENU THU NGÂN =====");
            System.out.println("1. Bán hàng");
            System.out.println("2. Quản lý giỏ hàng");
            System.out.println("3. Thanh toán");
            System.out.println("4. Xem hóa đơn");
            System.out.println("0. Quay lại");

            chon = nhapSo("Chọn chức năng: ");

            switch (chon) {
                case 1:
                    // Gọi phương thức bán hàng của ThuNgan
                    System.out.println("Chức năng bán hàng");
                    break;

                case 2:
                    System.out.println("Chức năng quản lý giỏ hàng");
                    break;

                case 3:
                    System.out.println("Chức năng thanh toán");
                    break;

                case 4:
                    System.out.println("Chức năng xem hóa đơn");
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }

    // ================= MENU QUẢN LÝ KHO =================

    public void menuQuanLyKho() {
        int chon;

        do {
            System.out.println("\n===== MENU QUẢN LÝ KHO =====");
            System.out.println("1. Quản lý sản phẩm");
            System.out.println("2. Lập phiếu nhập");
            System.out.println("3. Xem danh sách phiếu nhập");
            System.out.println("4. Kiểm tra tồn kho");
            System.out.println("0. Quay lại");

            chon = nhapSo("Chọn chức năng: ");

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

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }

    // ================= MENU QUẢN LÝ CỬA HÀNG =================

    public void menuQuanLyCuaHang() {
        int chon;

        do {
            System.out.println("\n===== MENU QUẢN LÝ CỬA HÀNG =====");
            System.out.println("1. Quản lý nhân viên");
            System.out.println("2. Quản lý tài khoản");
            System.out.println("3. Xem thông tin cửa hàng");
            System.out.println("0. Quay lại");

            chon = nhapSo("Chọn chức năng: ");

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

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }

    // ================= HỖ TRỢ =================

    public int nhapSo(String thongBao) {
        while (true) {
            System.out.print(thongBao);

            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số nguyên hợp lệ!");
            }
        }
    }

    public void tamDung() {
        System.out.println("\nNhấn Enter để tiếp tục...");
        sc.nextLine();
    }
}