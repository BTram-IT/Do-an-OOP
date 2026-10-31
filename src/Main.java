private static Scanner sc = new Scanner(System.in);

public static void main(String[] args) {
    while (true) {
        hienThiTrangChu();

        System.out.print("  Mời bạn chọn chức năng: ");

        int chon;
        try {
            chon = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("  Vui lòng nhập số hợp lệ!");
            tamDung();
            continue;
        }

        if (chon == 0) {
            System.out.println("\n  Cảm ơn bạn đã sử dụng chương trình!");
            break;
        }

        String chucVu;

        switch (chon) {
            case 1:
                chucVu = "QuanLy";
                break;
            case 2:
                chucVu = "KeToan";
                break;
            case 3:
                chucVu = "ThuNgan";
                break;
            case 4:
                chucVu = "QuanLyKho";
                break;
            default:
                System.out.println("  Lựa chọn không hợp lệ!");
                tamDung();
                continue;
        }

        if (dangNhap(chucVu)) {
            System.out.println("\n  Đăng nhập thành công!");

            switch (chon) {
                case 1:
                    menuQuanLyCuaHang();
                    break;
                case 2:
                    menuKeToan();
                    break;
                case 3:
                    menuThuNgan();
                    break;
                case 4:
                    menuQuanLyKho();
                    break;
            }
        } else {
            System.out.println("\n  Đăng nhập thất bại!");
            System.out.println("  Sai tên tài khoản hoặc mật khẩu.");
        }

        tamDung();
    }

    sc.close();
}

// Hiển thị trang chủ
public static void hienThiTrangChu() {
    System.out.println();
    System.out.println("  ============================================================");
    System.out.println("  ||                                                        ||");
    System.out.println("  ||                 QUẢN LÝ CỬA HÀNG                       ||");
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

// Kiểm tra tài khoản đăng nhập
public static boolean dangNhap(String chucVu) {
    System.out.println("\n  ===== ĐĂNG NHẬP =====");

    System.out.print("  Tên tài khoản: ");
    String tenTaiKhoan = sc.nextLine();

    System.out.print("  Mật khẩu: ");
    String matKhau = sc.nextLine();

    switch (chucVu) {
        case "QuanLy":
            return tenTaiKhoan.equals("quanly")
                    && matKhau.equals("123");

        case "KeToan":
            return tenTaiKhoan.equals("ketoan")
                    && matKhau.equals("123");

        case "ThuNgan":
            return tenTaiKhoan.equals("thungan")
                    && matKhau.equals("123");

        case "QuanLyKho":
            return tenTaiKhoan.equals("kho")
                    && matKhau.equals("123");

        default:
            return false;
    }
}

// Menu quản lý cửa hàng
public static void menuQuanLyCuaHang() {
    System.out.println("\n  Bạn đã vào chức năng Quản lý cửa hàng.");
    // Gọi menu quản lý cửa hàng tại đây
}

// Menu kế toán
public static void menuKeToan() {
    System.out.println("\n  Bạn đã vào chức năng Kế toán.");
    KeToan keToan = new KeToan();

}

// Menu thu ngân
public static void menuThuNgan() {
    System.out.println("\n  Bạn đã vào chức năng Thu ngân.");
    // Gọi menu thu ngân tại đây
}

// Menu quản lý kho
public static void menuQuanLyKho() {
    System.out.println("\n  Bạn đã vào chức năng Quản lý kho.");
    // Gọi menu quản lý kho tại đây
}

// Tạm dừng để người dùng đọc thông báo
public static void tamDung() {
    System.out.println("\n  Nhấn Enter để tiếp tục...");
    sc.nextLine();
}