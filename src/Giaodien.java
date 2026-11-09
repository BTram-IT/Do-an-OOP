import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

public class Giaodien {

    private static Scanner sc = new Scanner(System.in);

    private KeToan keToan;
    private ThuNgan thuNgan;
    private QuanlyKho quanLyKho;
    private QuanLyCuaHang quanLyCuaHang;


    private DSSanPhamKhuyenMai dsSanPhamKhuyenMai;
    private DSKhuyenMai dsKM;
    private DSSanPham dsSanPham;
    private DSDoiTac dsDoiTac;
    private DSHoadon dsHoaDon;
    private DSNhaCungCap dsNhaCungCap; // (Hoặc gộp chung với DSDoiTac tùy thiết kế của bạn)
    private DSNhanVien dsNhanVien;
    private DsKhachHang dsKhachHang;

    public Giaodien(KeToan keToan, ThuNgan thuNgan, QuanlyKho quanLyKho, QuanLyCuaHang quanLyCuaHang) {
        this.keToan = keToan;
        this.thuNgan = thuNgan;
        this.quanLyKho = quanLyKho;
        this.quanLyCuaHang = quanLyCuaHang;

        // Tự động khởi tạo và đọc file luôn trong này cho gọn
        khoiTaoVaDocFile();
    }

    public void khoiTaoVaDocFile(){
        System.out.println("Đang tải dữ liệu hệ thống...");

        try {
            dsSanPham = new DSSanPham();
            dsSanPham.docFile("data/sanpham.txt");

            dsDoiTac = new DSDoiTac();
            dsDoiTac.docFile("data/doitac.txt");

            dsKhachHang=new DsKhachHang();
            dsKhachHang.docFile("data/khachhang.txt");

            dsNhanVien=new DSNhanVien();
            dsNhanVien.docFile("data/nhanvien.txt");

            dsKM=new DSKhuyenMai();
            dsKM.docFile("data/khuyenmai.txt");

            dsNhaCungCap=new DSNhaCungCap();
            dsNhaCungCap.docFile("data/nhaCungCap.txt");

            dsHoaDon=new DSHoadon();
            dsHoaDon.docFile("data/hoadon.txt");

            dsSanPhamKhuyenMai=new DSSanPhamKhuyenMai();
            dsSanPhamKhuyenMai.docFile("data/sanphamKhuyenMai.txt");

        } catch (IOException e) {
            System.out.println("Lỗi đọc file dữ liệu: " + e.getMessage());
        }

        System.out.println("-> Hoàn tất tải dữ liệu!\n");
    }

    public static Scanner getSc() {
        return sc;
    }

    public KeToan getKeToan() {
        return keToan;
    }

    public ThuNgan getThuNgan() {
        return thuNgan;
    }

    public QuanlyKho getQuanLyKho() {
        return quanLyKho;
    }

    public QuanLyCuaHang getQuanLyCuaHang() {
        return quanLyCuaHang;
    }

    public DSSanPhamKhuyenMai getDsSanPhamKhuyenMai() {
        return dsSanPhamKhuyenMai;
    }

    public DSNhaCungCap getDsNhaCungCap() {
        return dsNhaCungCap;
    }

    public static void setSc(Scanner sc) {
        Giaodien.sc = sc;
    }

    public void setKeToan(KeToan keToan) {
        this.keToan = keToan;
    }

    public void setQuanLyKho(QuanlyKho quanLyKho) {
        this.quanLyKho = quanLyKho;
    }

    public void setThuNgan(ThuNgan thuNgan) {
        this.thuNgan = thuNgan;
    }

    public void setQuanLyCuaHang(QuanLyCuaHang quanLyCuaHang) {
        this.quanLyCuaHang = quanLyCuaHang;
    }
    // --- CÁC GETTER ĐỂ CÁC PHÂN HỆ KHÁC DỄ DÀNG TRUY XUẤT ---

    public DSSanPham getDsSanPham() { return dsSanPham; }
    public DSDoiTac getDsDoiTac() { return dsDoiTac; }
    public DSHoadon getDsHoaDon() { return dsHoaDon; }
    public DSKhuyenMai getDsKM() { return dsKM; }
    public DSSanPhamKhuyenMai getDsSPKM() { return dsSanPhamKhuyenMai; }
    public DSNhanVien getDsNhanVien() { return dsNhanVien; }
    public DsKhachHang getDsKhachHang() { return dsKhachHang; }




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
                    "1. Lập hóa đơn",
                    "2. Tra cứu hóa đơn",
                    "3. In hóa đơn theo khách hàng",
                    "4. Hiển thị danh sách khách hàng",
                    "5. Tìm kiếm khách hàng",
                    "6. Chọn ca làm việc",
                    "0. Quay lại");

            chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng: ", 0, 6
            );

            switch (chon) {
                case 1:
                    thuNgan.taoHoaDonMoi(sc, quanLyKho.getDsSanPham(), dsSanPhamKhuyenMai, dsKM);
                    break;
                case 2:
                    String maHD = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hóa đơn: ");
                    thuNgan.traCuuHoaDon(maHD);
                    break;
                case 3: {
                    String maKH = Ktradulieu.nhapChuoiKhongRong(
                            sc, "Nhập mã khách hàng: "
                    );
                    thuNgan.inDanhSachHoaDonTheoKhachHang(maKH);
                    break;
                }
                case 4:
                    thuNgan.hienThiDanhSachKhachHang();
                    break;
                case 5: {
                    System.out.println("\n===== TÌM KIẾM KHÁCH HÀNG =====");
                    System.out.println("1. Tìm theo mã khách hàng");
                    System.out.println("2. Tìm theo tên khách hàng");
                    System.out.println("0. Quay lại");

                    int chonTimKiem = Ktradulieu.luaChon(sc, "Chọn cách tìm kiếm: ", 0, 2);
                    switch (chonTimKiem) {
                        case 1: {
                            String maKH = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã khách hàng: ");
                            thuNgan.timKhachHangTheoMa(maKH);
                            break;
                        }
                        case 2: {
                            String tenKH = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên khách hàng: ");
                            thuNgan.timKhachHangTheoTen(tenKH);
                            break;
                        }
                        case 0:
                            break;
                    }
                    break;
                }
                case 6:
                    thuNgan.chonCaLamViec(sc);
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
    private void menuQuanLyKho() {
        int chon;

        do {
            hienThiMenu("MENU QUẢN LÝ KHO",
                    "1. Quản lý nhập kho",
                    "2. Thống kê tồn kho",
                    "3. Tìm kiếm sản phẩm",
                    "4. Cảnh báo hàng tồn kho",
                    "5. Cảnh báo hạn sử dụng",
                    "0. Quay lại");

            chon = Ktradulieu.luaChon(sc, "Chọn chức năng: ", 0, 5);

            switch (chon) {
                case 1:
                    int chonNhapKho;
                    do {
                        hienThiMenu("MENU QUẢN LÝ NHẬP KHO",
                                "1. Tạo hóa đơn nhập hàng",
                                "2. Thêm sản phẩm vào hóa đơn",
                                "3. Xác nhận hóa đơn nhập",
                                "4. Hủy hóa đơn nhập",
                                "5. Xem danh sách hóa đơn nhập",
                                "6. Tra cứu hóa đơn nhập",
                                "7. Báo cáo nhập kho",
                                "0. Quay lại");

                        chonNhapKho = Ktradulieu.luaChon(
                                sc, "Chọn chức năng: ", 0, 7
                        );

                        switch (chonNhapKho) {
                            case 1:
                                try {
                                    String maNhanVien = Ktradulieu.nhapChuoi(
                                            sc, "Nhập mã nhân viên: ", ".+", "Không được để trống!");
                                    String maNhaCungCap = Ktradulieu.nhapChuoi(
                                            sc, "Nhập mã nhà cung cấp: ", ".+", "Không được để trống!");
                                    LocalDate ngayNhap = Ktradulieu.nhapNgay(
                                            sc, "Nhập ngày nhập (dd/MM/yyyy): ", true);
                                    if (ngayNhap == null) ngayNhap = LocalDate.now();
                                    HoadonNhap phieu = quanLyKho.taoHoaDonNhap(maNhanVien, maNhaCungCap, ngayNhap);
                                    System.out.println("Tạo hóa đơn nhập thành công! Mã hóa đơn: " + phieu.getMaHoaDon());
                                } catch (Exception e) {
                                    System.out.println("Lỗi: " + e.getMessage());
                                }
                                break;

                            case 2:
                                try {
                                    String maPhieu = Ktradulieu.nhapChuoi(sc, "Nhập mã hóa đơn nhập: ", ".+", "Không được để trống!");
                                    String maSP = Ktradulieu.nhapChuoi(sc, "Nhập mã sản phẩm: ", ".+", "Không được để trống!");
                                    int soLuong = Ktradulieu.luaChon(sc, "Số lượng: ", 1, Integer.MAX_VALUE);
                                    long giaNhap = Long.parseLong(Ktradulieu.nhapChuoi(sc, "Giá nhập: ", "\\d{1,18}", "Giá nhập không hợp lệ!"));
                                    double thue = Double.parseDouble(Ktradulieu.nhapChuoi(sc, "Thuế suất nhập (%): ", "\\d{1,9}(\\.\\d{1,4})?", "Thuế suất không hợp lệ!"));
                                    LocalDate hsd = Ktradulieu.nhapNgay(sc, "Hạn sử dụng (dd/MM/yyyy, Enter = không hạn): ", false);

                                    quanLyKho.themChiTietPhieu(maPhieu, maSP, soLuong, giaNhap, thue, hsd);
                                    System.out.println("Đã thêm sản phẩm vào hóa đơn nhập.");
                                } catch (Exception e) {
                                    System.out.println("Lỗi: " + e.getMessage());
                                }
                                break;

                            case 3:
                                try {
                                    String maXacNhan = Ktradulieu.nhapChuoi(
                                            sc, "Nhập mã hóa đơn cần xác nhận: ",
                                            ".+", "Không được để trống!");
                                    quanLyKho.xacNhanHoaDonNhap(maXacNhan);
                                    System.out.println("Đã xác nhận hóa đơn nhập thành công!");
                                } catch (Exception e) {
                                    System.out.println("Lỗi: " + e.getMessage());
                                }
                                break;

                            case 4:
                                try {
                                    String maHuy = Ktradulieu.nhapChuoi(
                                            sc, "Nhập mã hóa đơn cần hủy: ",
                                            ".+", "Không được để trống!");
                                    quanLyKho.huyHoaDonNhap(maHuy);
                                    System.out.println("Đã hủy hóa đơn nhập.");
                                } catch (Exception e) {
                                    System.out.println("Lỗi: " + e.getMessage());
                                }
                                break;

                            case 5:
                                quanLyKho.inTatCaPhieuNhap();
                                break;

                            case 6:
                                String maTraCuu = Ktradulieu.nhapChuoi(
                                        sc, "Nhập mã hóa đơn cần tra cứu: ",
                                        ".+", "Không được để trống!");
                                quanLyKho.inPhieuNhap(maTraCuu);
                                break;

                            case 7:
                                LocalDate tuNgay = Ktradulieu.nhapNgay(
                                        sc, "Từ ngày (dd/MM/yyyy): ", true);
                                LocalDate denNgay = Ktradulieu.nhapNgay(
                                        sc, "Đến ngày (dd/MM/yyyy): ", true);
                                System.out.println(
                                        quanLyKho.baoCaoNhapKho(tuNgay, denNgay));
                                break;

                            case 0:
                                break;
                        }

                        if (chonNhapKho != 0) {
                            tamDung();
                        }

                    } while (chonNhapKho != 0);
                    break;

                case 2:
                    quanLyKho.inBaoCaoTonKho();
                    break;

                case 3:
                    quanLyKho.timKiemTuBanPhim(sc);
                    break;

                case 4:
                    quanLyKho.inDanhSachSapHet();
                    quanLyKho.inDanhSachHetHang();
                    break;

                case 5:
                    quanLyKho.inBaoCaoHanSuDung();
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

    private void menuQuanLyCuaHang() {
        int chon;
        do {
            hienThiMenu("MENU QUẢN LÝ CỬA HÀNG",
                    "1. Quản lý nhân viên",
                    "2. Quản lý khách hàng",
                    "3. Quản lý đối tác",
                    "4. Quản lý sản phẩm",
                    "5. Quản lý sản phẩm khuyến mãi",
                    "6. Quản lý khuyến mãi",
                    "7. Quản lý hóa đơn",
                    "8. Thống kê kinh doanh",
                    "0. Quay lại");

            chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng: ", 0, 8
            );
            switch (chon) {
                case 1:
                    quanLyCuaHang.menuQuanLyNhanVien();
                    break;

                case 2:
                    quanLyCuaHang.menuQuanLyKhachHang();
                    break;

                case 3:
                    quanLyCuaHang.menuQuanLyDoiTac();
                    break;

                case 4:
                    quanLyCuaHang.menuQuanLySanPham();
                    break;

                case 5:
                    quanLyCuaHang.menuQuanLySPKhuyenMai();
                    break;

                case 6:
                    quanLyCuaHang.menuQuanLyKhuyenMai();
                    break;

                case 7:
                    quanLyCuaHang.menuQuanLyHoaDon();
                    break;

                case 8:
                    quanLyCuaHang.menuThongKeKinhDoanh();
                    break; // Đã sửa thêm break để tránh trôi code

                case 0:
                    System.out.println("Quay lại menu chính.");
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