package giaodien;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import doitac.DSNhaCungCap;
import doitac.NhaCungCap;
import hoadon.DSHoadon;
import hoadon.HoaDon;
import hoadon.Hoadonban;
import hoadon.HoadonNhap;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import khuyenmai.Khuyenmai;
import khuyenmai.SPKhMai;
import person.DsKhachHang;
import person.DSNhanVien;
import person.Giamdoc;
import person.Khachhang;
import person.Nguoidung;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.DoUong;
import sanpham.SanPham;
import sanpham.ThucAn;
import util.Ktradulieu;

public class Giaodien {

    // =========================================================
    // CÁC KHO DỮ LIỆU CỦA HỆ THỐNG
    // =========================================================
    private DSNhanVien dsNhanVien;
    private DsKhachHang dsKhachHang;
    private DSNhaCungCap dsNhaCungCap;
    private DSSanPham dsSanPham;
    private DSKhuyenMai dsKhuyenMai;
    private DSSanPhamKhuyenMai dsSPKM;
    private DSHoadon dsHoaDon;

    private Nguoidung nguoiDangNhap;
    private Scanner sc;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================
    public Giaodien() {
        this.dsNhanVien = new DSNhanVien();
        this.dsKhachHang = new DsKhachHang();
        this.dsNhaCungCap = new DSNhaCungCap();
        this.dsSanPham = new DSSanPham();
        this.dsKhuyenMai = new DSKhuyenMai();
        this.dsSPKM = new DSSanPhamKhuyenMai();
        this.dsHoaDon = new DSHoadon();

        this.nguoiDangNhap = null;
        this.sc = new Scanner(System.in);

        docDuLieu();
    }

    private void khoiTaoTaiKhoanMacDinh() {
        NhanVien nv = new NhanVien(
                "NV001", "Nguyễn Văn An", "nhanvien",
                "123456", "0912345678",
                LocalDate.of(2023, 1, 15), false
        );
        nv.setLuongCoBan(5000000.0);
        dsNhanVien.them(nv);

        QuanLyCuaHang ql = new QuanLyCuaHang(
                "QL001", "Trần Thị Mai", "quanly",
                "123456", "0987654321", "Quản lý",
                false, "Toàn cửa hàng", 1.8
        );
        dsNhanVien.them(ql);

        Giamdoc gd = new Giamdoc(
                "GD001", "Lê Hoàng Long", "giamdoc",
                "123456", "0909123456", "Giám đốc",
                false, 15000000.0, 45.0
        );
        dsNhanVien.them(gd);

        dsNhanVien.ghiFile("data/nhanvien.txt");

        System.out.println("Đã tạo và lưu 3 tài khoản ban đầu!");
    }
    // HÀM NHẬP NGÀY DÙNG CHUNG
    private LocalDate nhapNgay(Scanner sc, String thongBao) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/uuuu");
        while (true) {
            try {
                String ngay = Ktradulieu.nhapChuoi(sc, thongBao, "\\d{2}/\\d{2}/\\d{4}", "Ngày phải có định dạng dd/MM/yyyy!");
                return LocalDate.parse(ngay, fmt);
            } catch (DateTimeParseException e) {
                System.out.println(
                        "[Lỗi] Ngày không tồn tại hoặc không hợp lệ!");
            }
        }
    }
    // HÀM CHỌN KỲ BÁO CÁO DÙNG CHUNG
    private LocalDate[] chonKyBaoCao() {

        while (true) {
            System.out.println("\n========== CHỌN KỲ BÁO CÁO ==========");
            System.out.println("1. Theo ngày");
            System.out.println("2. Theo tháng");
            System.out.println("3. Theo quý");
            System.out.println("4. Theo năm");
            System.out.println("5. Theo khoảng thời gian");
            System.out.println("0. Quay lại");
            System.out.println("====================================");
            System.out.print("Chọn: ");

            try {
                int chon = sc.nextInt();
                sc.nextLine();

                LocalDate tuNgay;
                LocalDate denNgay;

                switch (chon) {
                    case 1:
                        tuNgay = nhapNgay(sc,
                                "Nhập ngày (dd/MM/yyyy): ");
                        denNgay = tuNgay;
                        break;
                    case 2:
                        System.out.print("Nhập tháng (1-12): ");
                        int thang = sc.nextInt();
                        System.out.print("Nhập năm: ");
                        int namThang = sc.nextInt();
                        sc.nextLine();
                        YearMonth ym = YearMonth.of(namThang, thang);
                        tuNgay = ym.atDay(1);
                        denNgay = ym.atEndOfMonth();
                        break;
                    case 3:
                        System.out.print("Nhập quý (1-4): ");
                        int quy = sc.nextInt();
                        System.out.print("Nhập năm: ");
                        int namQuy = sc.nextInt();
                        sc.nextLine();
                        if (quy < 1 || quy > 4) {
                            System.out.println(
                                    "[Lỗi] Quý phải từ 1 đến 4!");
                            continue;
                        }
                        int thangDau = (quy - 1) * 3 + 1;
                        tuNgay = LocalDate.of(namQuy, thangDau, 1);
                        denNgay = tuNgay.plusMonths(2).withDayOfMonth(tuNgay.plusMonths(2).lengthOfMonth());
                        break;

                    case 4:
                        System.out.print("Nhập năm: ");
                        int nam = sc.nextInt();
                        sc.nextLine();

                        tuNgay = LocalDate.of(nam, 1, 1);
                        denNgay = LocalDate.of(nam, 12, 31);
                        break;

                    case 5:
                        tuNgay = nhapNgay(sc,
                                "Nhập từ ngày (dd/MM/yyyy): ");

                        denNgay = nhapNgay(sc,
                                "Nhập đến ngày (dd/MM/yyyy): ");
                        break;

                    case 0:
                        return null;

                    default:
                        System.out.println(
                                "[Lỗi] Lựa chọn không hợp lệ!");
                        continue;
                }

                if (tuNgay.isAfter(denNgay)) {
                    System.out.println(
                            "[Lỗi] Từ ngày không được sau đến ngày!");
                    continue;
                }

                return new LocalDate[]{tuNgay, denNgay};

            } catch (Exception e) {
                System.out.println(
                        "[Lỗi] Dữ liệu nhập không hợp lệ!");
                sc.nextLine();
            }
        }
    }

    public void batDau() {
        while (true) {
            hienThiManHinhDangNhap();
            if (this.nguoiDangNhap == null) {
                System.out.println("\nCảm ơn bạn đã sử dụng chương trình. Tạm biệt!");
                break;
            }

            if (this.nguoiDangNhap instanceof Giamdoc) {
                menuGiamDoc();
            } else if (this.nguoiDangNhap instanceof QuanLyCuaHang) {
                menuQuanLyCuaHang();
            } else if (this.nguoiDangNhap instanceof NhanVien) {
                menuNhanVien();
            }
        }
    }

    public void hienThiManHinhDangNhap() {
        System.out.println("\n=======================================================");
        System.out.println("  HỆ THỐNG QUẢN LÝ CỬA HÀNG THỨC ĂN VÀ NƯỚC UỐNG");
        System.out.println("                   ĐĂNG NHẬP");
        System.out.println("=======================================================");
        System.out.println("Gợi ý 3 tài khoản demo hệ thống:");
        System.out.println("1. Nhân viên : nhanvien  | Mật khẩu: 123456");
        System.out.println("2. Quản lý   : quanly    | Mật khẩu: 123456");
        System.out.println("3. Giám đốc  : giamdoc   | Mật khẩu: 123456");
        System.out.println("Nhập '0' tại tên đăng nhập để thoát chương trình.");
        System.out.println("-------------------------------------------------------");

        while (true) {
            String tk = Ktradulieu.nhapChuoiKhongRong(sc, "Tên đăng nhập: ");
            if (tk.equals("0")) {
                this.nguoiDangNhap = null;
                ghiDuLieu();
                return;
            }
            String mk = Ktradulieu.nhapChuoiKhongRong(sc, "Mật khẩu: ");

            Nguoidung nd = dsNhanVien.timKiemTheoTaiKhoan(tk);
            if (nd != null && nd.getMatKhau().equals(mk)) {
                if (nd.isDaXoa()) {
                    System.out.println("[TỪ CHỐI TRUY CẬP] Tài khoản đã nghỉ việc hoặc bị khóa!");
                    continue;
                }
                this.nguoiDangNhap = nd;
                System.out.println("\n>> Đăng nhập thành công! Xin chào " + nd.getVaiTro() + ": " + nd.getTenND() + " <<");
                return;
            } else {
                System.out.println("Sai tên đăng nhập hoặc mật khẩu! Vui lòng thử lại.");
            }
        }
    }

    // 1. MENU NHÂN VIÊN
    public void menuNhanVien() {
        while (true) {
            System.out.println("\n========== MENU NHÂN VIÊN ==========");
            System.out.println("1. Quản lý kho hàng");
            System.out.println("2. Thu ngân - Tiếp nhận khách hàng");
            System.out.println("3. Đăng xuất");
            System.out.println("===================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (1-3): ", 1, 3);
            switch (chon) {
                case 1: menuQuanLyKhoHang(); break;
                case 2: menuThuNganTiepNhanKhachHang(); break;
                case 3:
                    System.out.println(">> Đã đăng xuất khỏi tài khoản nhân viên.");
                    this.nguoiDangNhap = null;
                    return;
            }
        }
    }

    public void menuQuanLyKhoHang() {
        while (true) {
            System.out.println("\n========== QUẢN LÝ KHO HÀNG ==========");
            System.out.println("1. Tạo hóa đơn nhập");
            System.out.println("2. Xem danh sách hóa đơn nhập");
            System.out.println("3. Tra cứu hóa đơn nhập");
            System.out.println("4. Xem danh sách sản phẩm");
            System.out.println("5. Tìm kiếm sản phẩm");
            System.out.println("6. Xem danh sách nhà cung cấp");
            System.out.println("7. Thêm nhà cung cấp");
            System.out.println("8. Tìm kiếm nhà cung cấp");
            System.out.println("9. Quay lại menu nhân viên");
            System.out.println("=====================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (1-9): ", 1, 9);
            switch (chon) {
                case 1: taoHoaDonNhap(); break;
                case 2:
                    dsHoaDon.xuatDanhSachHoaDonNhap();
                    break;
                case 3: traCuuHoaDonNhap(); break;
                case 4: dsSanPham.xuatDanhSach(); break;
                case 5: timKiemSanPham(); break;
                case 6: dsNhaCungCap.xuatDanhSach(); break;
                case 7: {
                    NhaCungCap ncc = new NhaCungCap();
                    ncc.nhapThongTin(sc, dsNhaCungCap);
                    dsNhaCungCap.them(ncc);
                    break;
                }
                case 8: timKiemNhaCungCap(); break;
                case 9: return;
            }
        }
    }

    public void menuThuNganTiepNhanKhachHang() {
        while (true) {
            System.out.println("\n========== THU NGÂN - TIẾP NHẬN KHÁCH HÀNG ==========");
            System.out.println("1. Tiếp nhận khách hàng");
            System.out.println("2. Tạo hóa đơn bán");
            System.out.println("3. Xem và tìm kiếm khuyến mãi");
            System.out.println("4. Quản lý hóa đơn bán");
            System.out.println("5. Quay lại menu nhân viên");
            System.out.println("====================================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (1-5): ", 1, 5);
            switch (chon) {
                case 1: menuTiepNhanKhachHang(); break;
                case 2: taoHoaDonBan(); break;
                case 3: menuXemVaTimKiemKhuyenMai(); break;
                case 4: menuQuanLyHoaDonBan(); break;
                case 5: return;
            }
        }
    }

    public void menuTiepNhanKhachHang() {
        while (true) {
            System.out.println("\n--- TIẾP NHẬN KHÁCH HÀNG ---");
            System.out.println("1. Tìm kiếm khách hàng");
            System.out.println("2. Xem danh sách khách hàng");
            System.out.println("3. Thêm khách hàng mới");
            System.out.println("4. Xem lịch sử mua hàng của khách");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-4): ", 0, 4);
            switch (chon) {
                case 1: timKiemKhachHang(); break;
                case 2: dsKhachHang.xuatDanhSach(); break;
                case 3: dsKhachHang.themTuBanPhim(sc); break;
                case 4: xemLichSuMuaHangKhach(); break;
                case 0: return;
            }
        }
    }

    public void menuXemVaTimKiemKhuyenMai() {
        while (true) {
            System.out.println("\n--- XEM & TÌM KIẾM KHUYẾN MÃI ---");
            System.out.println("1. Xem danh sách khuyến mãi");
            System.out.println("2. Tìm kiếm khuyến mãi");
            System.out.println("3. Xem thời gian áp dụng và sản phẩm được khuyến mãi");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-3): ", 0, 3);
            switch (chon) {
                case 1: dsKhuyenMai.xuatDanhSach(); break;
                case 2: dsKhuyenMai.timKiemKhuyenMai(sc); break;
                case 3: xemThoiGianVaSanPhamKhuyenMai(); break;
                case 0: return;
            }
        }
    }

    public void menuQuanLyHoaDonBan() {
        while (true) {
            System.out.println("\n--- QUẢN LÝ HÓA ĐƠN BÁN ---");
            System.out.println("1. Xem danh sách hóa đơn bán");
            System.out.println("2. Tìm kiếm và xem chi tiết hóa đơn bán");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-2): ", 0, 2);
            switch (chon) {
                case 1:
                    for (int i = 0; i < dsHoaDon.getSoLuong(); i++) {
                        HoaDon hd = dsHoaDon.get(i);
                        if (hd instanceof Hoadonban) System.out.println(hd);
                    }
                    break;
                case 2: xemChiTietHoaDonBan(); break;
                case 0: return;
            }
        }
    }

    // 2. MENU QUẢN LÝ CỬA HÀNG
    public void menuQuanLyCuaHang() {
        while (true) {
            System.out.println("\n========== MENU QUẢN LÝ CỬA HÀNG ==========");
            System.out.println("1. Quản lý sản phẩm");
            System.out.println("2. Quản lý khuyến mãi");
            System.out.println("3. Quản lý nhập kho ");
            System.out.println("4. Quản lý nhà cung cấp");
            System.out.println("5. Quản lý nhân viên");
            System.out.println("6. Báo cáo ");
            System.out.println("0. Đăng xuất");
            System.out.println("===========================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn phân hệ quản lý (0-6): ", 0, 6);
            switch (chon) {
                case 1: menuQuanLySanPham(); break;
                case 2: menuQuanLyKhuyenMai(); break;
                case 3: menuQuanLyNhapKhoQuyTrinh(); break;
                case 4: menuQuanLyNhaCungCap(); break;
                case 5: menuQuanLyNhanVien(); break;
                case 6: menuThongKeBaoCao(); break;
                case 0:
                    System.out.println("Đã đăng xuất.");
                    this.nguoiDangNhap = null;
                    return;
            }
        }
    }

    public void menuQuanLySanPham() {
        while (true) {
            System.out.println("\n--- 2.1. QUẢN LÝ SẢN PHẨM ---");
            System.out.println("1. Thêm sản phẩm");
            System.out.println("2. Xem danh sách sản phẩm");
            System.out.println("3. Tìm kiếm sản phẩm");
            System.out.println("4. Cập nhật thông tin sản phẩm");
            System.out.println("5. Xóa sản phẩm");
            System.out.println("6. Xem số lượng tồn kho");
            System.out.println("0. Quay lại ");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-6): ", 0, 6);
            switch (chon) {
                case 1: dsSanPham.themSanPhamTuBanPhim(sc); break;
                case 2: dsSanPham.xuatDanhSach(); break;
                case 3: timKiemSanPham(); break;
                case 4: {
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã sản phẩm cần sửa: ");
                    dsSanPham.suaSP(sc, ma, nguoiDangNhap);
                    break;
                }
                case 5: {
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã sản phẩm cần xóa: ");
                    dsSanPham.xoaSP(ma, nguoiDangNhap);
                    break;
                }
                case 6:
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        ((QuanLyCuaHang) nguoiDangNhap).thongKeTonKho(dsSanPham);
                    }
                    dsSanPham.kiemTraVaCapNhatHanSuDung();
                    break;
                case 0: return;
            }
        }
    }

    public void menuQuanLyKhuyenMai() {
        while (true) {
            System.out.println("\n--- 2.2. QUẢN LÝ KHUYẾN MÃI ---");
            System.out.println("1. Thêm chương trình khuyến mãi");
            System.out.println("2. Xem danh sách khuyến mãi");
            System.out.println("3. Tìm kiếm khuyến mãi");
            System.out.println("4. Cập nhật khuyến mãi");
            System.out.println("5. Xóa khuyến mãi");
            System.out.println("6. Áp dụng khuyến mãi cho sản phẩm");
            System.out.println("7. Hủy áp dụng khuyến mãi cho sản phẩm");
            System.out.println("8. Xem danh sách sản phẩm áp dụng khuyến mãi");
            System.out.println("0. Quay lại ");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-8): ", 0, 8);
            switch (chon) {
                case 1: dsKhuyenMai.themTuBanPhim(sc); break;
                case 2: dsKhuyenMai.xuatDanhSach(); break;
                case 3: dsKhuyenMai.timKiemKhuyenMai(sc); break;
                case 4: dsKhuyenMai.suaKhuyenMai(sc); break;
                case 5: {
                    String maKM = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã khuyến mãi cần xóa: ");
                    dsKhuyenMai.xoaKhuyenMai(maKM, dsSPKM);
                    break;
                }
                case 6: {
                    String maSP = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã sản phẩm: ");
                    String maKM = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã khuyến mãi: ");
                    if (dsSanPham.timKiem(maSP) == null) {
                        System.out.println("[Lỗi] Không tìm thấy sản phẩm: " + maSP);
                    } else if (dsKhuyenMai.timKiem(maKM) == null) {
                        System.out.println("[Lỗi] Không tìm thấy khuyến mãi: " + maKM);
                    } else {
                        dsSPKM.them(new SPKhMai(maSP, maKM));
                    }
                    break;
                }
                case 7: {
                    String maSP = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã sản phẩm: ");
                    String maKM = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã khuyến mãi: ");
                    dsSPKM.xoa(maSP, maKM);
                    break;
                }
                case 8: dsSPKM.xuatDanhSach(); break;
                case 0: return;
            }
        }
    }

    public void menuQuanLyNhapKhoQuyTrinh() {
        while (true) {
            System.out.println("\n--- 3. QUẢN LÝ NHẬP KHO ---");
            System.out.println("1. Xem danh sách hóa đơn nhập kho chờ duyệt");
            System.out.println("2. Duyệt hóa đơn nhập kho ");
            System.out.println("3. Xem lịch sử hóa đơn nhập kho");
            System.out.println("4. Tìm kiếm hóa đơn nhập kho");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-4): ", 0, 4);
            switch (chon) {
                case 1:
                    System.out.println("\n--- HÓA ĐƠN NHẬP KHO CHỜ PHÊ DUYỆT ---");
                    int dem = 0;
                    for (int i = 0; i < dsHoaDon.getSoLuong(); i++) {
                        HoaDon hd = dsHoaDon.get(i);
                        if (hd instanceof HoadonNhap && !hd.isDaXuLyThanhCong() && !hd.getTrangThai().equalsIgnoreCase("Da huy")) {
                            dem++;
                            System.out.println(dem + ". " + hd.toString());
                        }
                    }
                    if (dem == 0) System.out.println("Hiện không có hóa đơn nhập nào đang chờ duyệt.");
                    break;
                case 2:
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        ((QuanLyCuaHang) nguoiDangNhap).duyetHoaDon(sc, dsHoaDon, dsSanPham);
                    } else {
                        dsHoaDon.duyetHoaDon(sc, nguoiDangNhap, dsSanPham);
                    }
                    dsHoaDon.ghiFile("data/hoadon.txt");
                    dsSanPham.ghiFile("data/sanpham.txt");
                    break;
                case 3:
                    dsHoaDon.xuatDanhSachHoaDonNhap();
                    break;
                case 4: traCuuHoaDonNhap(); break;
                case 0: return;
            }
        }
    }

    public void menuQuanLyNhaCungCap() {
        while (true) {
            System.out.println("\n--- 4. QUẢN LÝ NHÀ CUNG CẤP ---");
            System.out.println("1. Thêm nhà cung cấp");
            System.out.println("2. Xem danh sách nhà cung cấp");
            System.out.println("3. Tìm kiếm nhà cung cấp");
            System.out.println("4. Cập nhật thông tin nhà cung cấp");
            System.out.println("5. Xóa nhà cung cấp");
            System.out.println("0. Quay lại menu quản lý");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-5): ", 0, 5);
            switch (chon) {
                case 1: {
                    NhaCungCap ncc = new NhaCungCap();
                    ncc.nhapThongTin(sc, dsNhaCungCap);
                    dsNhaCungCap.them(ncc);
                    break;
                }
                case 2: dsNhaCungCap.xuatDanhSach(); break;
                case 3: timKiemNhaCungCap(); break;
                case 4: {
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã NCC: ");
                    dsNhaCungCap.sua(sc, nguoiDangNhap, ma);
                    break;
                }
                case 5: {
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã NCC cần xóa: ");
                    dsNhaCungCap.xoa(nguoiDangNhap, ma);
                    break;
                }
                case 0: return;
            }
        }
    }

    public void menuQuanLyNhanVien() {
        while (true) {
            System.out.println("\n--- 5. QUẢN LÝ NHÂN VIÊN ---");
            System.out.println("1. Xem danh sách nhân viên");
            System.out.println("2. Tìm kiếm nhân viên");
            System.out.println("3. Thêm nhân viên");
            System.out.println("4. Cập nhật thông tin nhân viên");
            System.out.println("5. Đề xuất sa thải nhân viên (Gửi đề xuất lên Giám Đốc)");
            System.out.println("6. Tính lương nhân viên");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-6): ", 0, 6);

            switch (chon) {
                case 1:
                    System.out.println("\n============ DANH SÁCH NHÂN VIÊN ============");
                    for (NhanVien nv : dsNhanVien.getDanhSachNhanVien()) {
                        System.out.println(nv.toString());
                    }
                    break;

                case 2: {
                    String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc tên: ");
                    Nguoidung nd = dsNhanVien.timKiem(key);

                    if (nd instanceof NhanVien) {
                        System.out.println("Kết quả: " + nd.toString());
                    } else {
                        System.out.println("Không tìm thấy nhân viên phù hợp!");
                    }
                    break;
                }

                case 3:
                    themNhanVien();
                    break;

                case 4: {
                    String maNV = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã nhân viên: ");
                    dsNhanVien.sua(sc, nguoiDangNhap, maNV);
                    dsNhanVien.ghiFile("data/nhanvien.txt");
                    break;
                }

                case 5: {
                    String maNV = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã nhân viên đề xuất: ");
                    dsNhanVien.xoa(sc, nguoiDangNhap, maNV);
                    dsNhanVien.ghiFile("data/nhanvien.txt");
                    break;
                }

                case 6: {
                    String maNV = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã nhân viên tính lương: ");
                    NhanVien nv = dsNhanVien.timNhanVienTheoMa(maNV);

                    if (nv != null && nguoiDangNhap instanceof QuanLyCuaHang) {
                        ((QuanLyCuaHang) nguoiDangNhap).tinhLuongNhanVien(nv);
                    } else if (nv == null) {
                        System.out.println("Không tìm thấy nhân viên: " + maNV);
                    }
                    break;
                }

                case 0:
                    return;
            }
        }
    }

    public void menuThongKeBaoCao() {
        while (true) {
            System.out.println("\n================ MENU BÁO CÁO ================");
            System.out.println("1. Báo cáo tổng quan hóa đơn");
            System.out.println("2. Báo cáo doanh thu và chi phí");
            System.out.println("3. Báo cáo thuế VAT");
            System.out.println("4. Báo cáo sản phẩm bán được và không bán được");
            System.out.println("5. Báo cáo nhân viên và bảng lương");
            System.out.println("6. Báo cáo tồn kho");
            System.out.println("0. Quay lại");
            System.out.println("===============================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-6): ", 0, 6);
            switch (chon) {
                case 1: {
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        LocalDate[] ky = chonKyBaoCao();
                        if (ky != null) {
                            ((QuanLyCuaHang) nguoiDangNhap)
                                    .thongKeTongQuanHoaDon(
                                            dsHoaDon, ky[0], ky[1]);
                        }
                    }
                    break;
                }
                case 2: {
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        LocalDate[] ky = chonKyBaoCao();

                        if (ky != null) {
                            ((QuanLyCuaHang) nguoiDangNhap).thongKeDoanhThuLoiNhuan(dsHoaDon, ky[0], ky[1]);
                        }
                    }
                    break;
                }
                case 3: {
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        LocalDate[] ky = chonKyBaoCao();

                        if (ky != null) {
                            ((QuanLyCuaHang) nguoiDangNhap)
                                    .thongKeThue(dsHoaDon, ky[0], ky[1]);
                        }
                    }
                    break;
                }
                case 4: {
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        LocalDate[] ky = chonKyBaoCao();

                        if (ky != null) {
                            ((QuanLyCuaHang) nguoiDangNhap)
                                    .thongKeSanPhamBanDuoc(
                                            dsHoaDon,
                                            dsSanPham,
                                            ky[0],
                                            ky[1]
                                    );
                        }
                    }
                    break;
                }
                case 5: {
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        ((QuanLyCuaHang) nguoiDangNhap)
                                .thongKeNhanVienVaBangLuong(dsNhanVien);
                    }
                    break;
                }
                case 6: {
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        ((QuanLyCuaHang) nguoiDangNhap).thongKeTonKho(dsSanPham);
                    }
                    break;
                }
                case 0: {
                    System.out.println("Đang quay lại menu quản lý cửa hàng...");
                    return;
                }
                default:
                    System.out.println("[Lỗi] Vui lòng chọn từ 0 đến 6!");
                    break;
            }
        }
    }
    public void menuQuanLyNhanSuGiamDoc() {
        while (true) {
            System.out.println("\n========== QUẢN LÝ NHÂN SỰ ==========");
            System.out.println("1. Xem danh sách nhân sự");
            System.out.println("2. Xem và xử lý yêu cầu sa thải");
            System.out.println("3. Thêm nhân viên");
            System.out.println("4. Thêm quản lý cửa hàng");
            System.out.println("0. Quay lại");
            System.out.println("=====================================");

            if (!(nguoiDangNhap instanceof Giamdoc)) {
                System.out.println("[Lỗi] Bạn không có quyền truy cập!");
                return;
            }

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (0-4): ", 0, 4);

            switch (chon) {
                case 1:
                    dsNhanVien.xuatDanhSach();
                    break;

                case 2:
                    ((Giamdoc) nguoiDangNhap)
                            .duyetYeuCauSaThai(sc, dsNhanVien);
                    dsNhanVien.ghiFile("data/nhanvien.txt");
                    break;

                case 3:
                    themNhanVien();
                    break;

                case 4:
                    themQuanLy();
                    break;

                case 0:
                    return;
            }
        }
    }

    // MENU BÁO CÁO - THỐNG KÊ CỦA GIÁM ĐỐC
    public void menuBaoCaoThongKeGiamDoc() {
        while (true) {
            System.out.println("\n========== BÁO CÁO - THỐNG KÊ ==========");
            System.out.println("1. Báo cáo tổng quan hóa đơn");
            System.out.println("2. Thống kê doanh thu và chi phí nhập hàng");
            System.out.println("3. Báo cáo thuế VAT");
            System.out.println("4. Thống kê sản phẩm bán được và không bán được");
            System.out.println("5. Báo cáo nhân viên và bảng lương");
            System.out.println("6. Báo cáo tồn kho");
            System.out.println("0. Quay lại");
            System.out.println("========================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (0-6): ", 0, 6);

            if (!(nguoiDangNhap instanceof Giamdoc)) {
                System.out.println("[Lỗi] Bạn không có quyền truy cập!");
                return;
            }

            // Tìm đối tượng quản lý để sử dụng các hàm thống kê đã viết
            QuanLyCuaHang baoCao = null;

            if (dsNhanVien != null) {
                for (int i = 0; i < dsNhanVien.getSoLuong(); i++) {
                    Object obj = dsNhanVien.get(i);

                    if (obj instanceof QuanLyCuaHang) {
                        baoCao = (QuanLyCuaHang) obj;
                        break;
                    }
                }
            }

            if (chon != 0 && baoCao == null) {
                System.out.println("[Lỗi] Không tìm thấy đối tượng quản lý để thực hiện báo cáo!");
                continue;
            }

            switch (chon) {
                case 1: {
                    LocalDate[] ky = chonKyBaoCao();

                    if (ky != null) {
                        baoCao.thongKeTongQuanHoaDon(
                                dsHoaDon, ky[0], ky[1]);
                    }
                    break;
                }

                case 2: {
                    LocalDate[] ky = chonKyBaoCao();

                    if (ky != null) {
                        baoCao.thongKeDoanhThuLoiNhuan(
                                dsHoaDon, ky[0], ky[1]);
                    }
                    break;
                }

                case 3: {
                    LocalDate[] ky = chonKyBaoCao();

                    if (ky != null) {
                        baoCao.thongKeThue(
                                dsHoaDon, ky[0], ky[1]);
                    }
                    break;
                }

                case 4: {
                    LocalDate[] ky = chonKyBaoCao();

                    if (ky != null) {
                        baoCao.thongKeSanPhamBanDuoc(
                                dsHoaDon, dsSanPham, ky[0], ky[1]);
                    }
                    break;
                }

                case 5:
                    baoCao.thongKeNhanVienVaBangLuong(dsNhanVien);
                    break;

                case 6:
                    baoCao.thongKeTonKho(dsSanPham);
                    break;

                case 0:
                    return;
            }
        }
    }

    // 3. MENU GIÁM ĐỐC
    public void menuGiamDoc() {
        while (true) {
            System.out.println("\n========== MENU GIÁM ĐỐC ==========");
            System.out.println("1. Quản lý nhân sự");
            System.out.println("2. Báo cáo - thống kê");
            System.out.println("3. Phê duyệt hóa đơn nhập kho");
            System.out.println("0. Đăng xuất");
            System.out.println("===================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (0-3): ", 0, 3);

            switch (chon) {
                case 1:
                    menuQuanLyNhanSuGiamDoc();
                    break;

                case 2:
                    menuBaoCaoThongKeGiamDoc();
                    break;
                case 3:
                    if (nguoiDangNhap instanceof Giamdoc) {
                        ((Giamdoc) nguoiDangNhap).duyetHoaDon(sc, dsHoaDon, dsSanPham);

                        dsHoaDon.ghiFile("data/hoadon.txt");
                        dsSanPham.ghiFile("data/sanpham.txt");
                    }
                    break;

                case 0:
                    System.out.println(">> Đã đăng xuất.");
                    this.nguoiDangNhap = null;
                    return;
            }
        }
    }

    // =========================================================
    // HỖ TRỢ NGHIỆP VỤ - GỌI ĐÚNG HÀM TRONG CÁC CLASS CỦA BẠN
    // =========================================================
    private NhanVien layNhanVienThaoTac() {
        if (nguoiDangNhap instanceof NhanVien) {
            return (NhanVien) nguoiDangNhap;
        }
        return null;
    }

    public void taoHoaDonNhap() {
        NhanVien nvLap = layNhanVienThaoTac();

        if (nvLap == null) {
            System.out.println("[Lỗi] Không tìm thấy nhân viên lập hóa đơn!");
            return;
        }

        HoadonNhap hdn = new HoadonNhap();

        hdn.nhapThongTin(sc, nvLap, dsHoaDon,
                dsNhaCungCap, dsSanPham, nguoiDangNhap);
        if (dsHoaDon.themHD(hdn)) {
            dsHoaDon.ghiFile("data/hoadon.txt");
            dsSanPham.ghiFile("data/sanpham.txt");
            dsNhaCungCap.ghiFile("data/nhacungcap.txt");

            System.out.println("[Thành công] Đã lưu hóa đơn nhập!");
        }
    }

    public void taoHoaDonBan() {
        NhanVien nvLap = layNhanVienThaoTac();

        if (nvLap == null) {
            System.out.println("[Lỗi] Không tìm thấy nhân viên lập hóa đơn!");
            return;
        }

        Hoadonban hdb = new Hoadonban();

        hdb.nhapThongTin(sc, nvLap, dsHoaDon,
                dsKhachHang, dsSanPham, dsKhuyenMai, dsSPKM);

        if (dsHoaDon.themHD(hdb)) {
            // Lưu hóa đơn
            dsHoaDon.ghiFile("data/hoadon.txt");

            // Lưu sản phẩm sau khi cập nhật tồn kho
            dsSanPham.ghiFile("data/sanpham.txt");

            // Lưu khách hàng sau khi cập nhật điểm tích lũy
            dsKhachHang.ghiFile("data/khachhang.txt");

            System.out.println("[Thành công] Đã lưu hóa đơn bán!");
        }
    }

    public void timKiemSanPham() {
        String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc tên sản phẩm: ").toLowerCase();
        int dem = 0;
        System.out.println("\n============ KẾT QUẢ TÌM KIẾM SẢN PHẨM ============");
        for (int i = 0; i < dsSanPham.getSoLuong(); i++) {
            SanPham sp = dsSanPham.get(i);
            if (sp != null && (sp.getMaSP().toLowerCase().contains(key) || sp.getTenSP().toLowerCase().contains(key))) {
                dem++;
                System.out.println(dem + ". " + sp.toString());
            }
        }
        if (dem == 0) System.out.println("Không tìm thấy sản phẩm phù hợp.");
        System.out.println("==================================================");
    }

    public void timKiemNhaCungCap() {
        String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc tên NCC: ");
        NhaCungCap ncc = dsNhaCungCap.timKiem(key);
        System.out.println(ncc != null ? "\nKết quả: " + ncc.toString() : "Không tìm thấy nhà cung cấp: " + key);
    }

    public void timKiemKhachHang() {
        String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc SĐT: ");
        Khachhang kh = dsKhachHang.timKiem(key);
        System.out.println(kh != null ? "\nThông tin: " + kh.toString() : "Không tìm thấy khách hàng: " + key);
    }

    public void xemLichSuMuaHangKhach() {
        String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc SĐT: ");
        Khachhang kh = dsKhachHang.timKiem(key);
        if (kh == null) {
            System.out.println("Không tìm thấy khách hàng: " + key);
            return;
        }
        System.out.println("\nLỊCH SỬ MUA HÀNG CỦA: " + kh.getTenKH() + " [" + kh.getMaKH() + "]");
        int dem = 0;
        for (int i = 0; i < dsHoaDon.getSoLuong(); i++) {
            HoaDon hd = dsHoaDon.get(i);
            if (hd instanceof Hoadonban && ((Hoadonban) hd).getMaKH().equalsIgnoreCase(kh.getMaKH())) {
                dem++;
                System.out.printf("%d. [%s] Ngày: %s | Tổng tiền: %,.0f VND | Trạng thái: %s\n",
                        dem, hd.getMaHD(), hd.getNgayLap(), hd.tinhTongTien(), hd.getTrangThai());
            }
        }
        if (dem == 0) System.out.println("Khách hàng này chưa có đơn mua hàng nào.");
    }

    public void traCuuHoaDonNhap() {
        String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hóa đơn nhập: ");
        HoaDon hd = dsHoaDon.timKiem(ma);
        if (hd instanceof HoadonNhap) {
            System.out.println("\n--- CHI TIẾT HÓA ĐƠN NHẬP ---");
            hd.hienThiHoaDon();
        } else {
            System.out.println("Không tìm thấy hóa đơn nhập mã: " + ma);
        }
    }

    public void xemChiTietHoaDonBan() {
        String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hóa đơn bán: ");
        HoaDon hd = dsHoaDon.timKiem(ma);
        if (hd instanceof Hoadonban) {
            System.out.println("\n--- CHI TIẾT HÓA ĐƠN BÁN ---");
            hd.hienThiHoaDon();
        } else {
            System.out.println("Không tìm thấy hóa đơn bán: " + ma);
        }
    }

    public void xemThoiGianVaSanPhamKhuyenMai() {
        System.out.println("\n--- THỜI GIAN ÁP DỤNG & SẢN PHẨM KHUYẾN MÃI ---");

        Khuyenmai[] listKM = dsKhuyenMai.getDanhSach();

        if (listKM == null || listKM.length == 0) {
            System.out.println("Chưa có chương trình khuyến mãi nào!");
            return;
        }

        for (Khuyenmai km : listKM) {
            if (km == null) {
                continue;
            }

            System.out.println("\n" + km);

            List<String> spList =
                    dsSPKM.layDanhSachMaSPTheoKM(km.getMaKM());

            if (spList == null || spList.isEmpty()) {
                System.out.println("   Sản phẩm: (Chưa gán)");
            } else {
                System.out.println("   Sản phẩm: " + String.join(", ", spList));
            }
        }
    }
    private void themNhanVien() {

        if (!(nguoiDangNhap instanceof Giamdoc)
                && !(nguoiDangNhap instanceof QuanLyCuaHang)) {
            System.out.println("[Lỗi] Bạn không có quyền thêm nhân viên!");
            return;
        }

        NhanVien nvMoi = new NhanVien();
        nvMoi.nhapThongTin(sc, dsNhanVien);

        dsNhanVien.them(nvMoi);
        dsNhanVien.ghiFile("data/nhanvien.txt");

        System.out.println("Đã thêm và lưu nhân viên thành công!");
    }

    // Thêm quản lý cửa hàng - Chỉ Giám đốc được phép
    private void themQuanLy() {

        if (!(nguoiDangNhap instanceof Giamdoc)) {
            System.out.println("[Lỗi] Chỉ Giám đốc mới có quyền thêm quản lý!");
            return;
        }

        QuanLyCuaHang qlMoi = new QuanLyCuaHang();
        qlMoi.nhapThongTin(sc, dsNhanVien);

        dsNhanVien.them(qlMoi);
        dsNhanVien.ghiFile("data/nhanvien.txt");

        System.out.println("Đã thêm và lưu quản lý cửa hàng thành công!");
    }

    private void docDuLieu() {

        File fileNhanVien = new File("data/nhanvien.txt");

        if (!fileNhanVien.exists() || fileNhanVien.length() == 0) {
            khoiTaoTaiKhoanMacDinh();
        } else {
            dsNhanVien.docFile("data/nhanvien.txt");
        }

        dsKhachHang.docFile("data/khachhang.txt");
        dsNhaCungCap.docFile("data/nhacungcap.txt");
        dsSanPham.docFile("data/sanpham.txt");
        dsKhuyenMai.docFile("data/khuyenmai.txt");
        dsSPKM.docFile("data/sanpham_khuyenmai.txt");
        dsHoaDon.docFile("data/hoadon.txt", dsNhanVien);
    }
    private void ghiDuLieu() {
        dsNhanVien.ghiFile("data/nhanvien.txt");
        dsKhachHang.ghiFile("data/khachhang.txt");
        dsNhaCungCap.ghiFile("data/nhacungcap.txt");
        dsSanPham.ghiFile("data/sanpham.txt");
        dsKhuyenMai.ghiFile("data/khuyenmai.txt");
        dsSPKM.ghiFile("data/sanpham_khuyenmai.txt");
        dsHoaDon.ghiFile("data/hoadon.txt");
    }
}