package giaodien;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
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
            try {
                int chon = Ktradulieu.luaChon(sc, "Chọn (0-5): ", 0, 5);

                LocalDate tuNgay;
                LocalDate denNgay;

                switch (chon) {
                    case 1:
                        tuNgay = nhapNgay(sc,
                                "Nhập ngày (dd/MM/yyyy): ");
                        denNgay = tuNgay;
                        break;
                    case 2:
                        int thang = Ktradulieu.luaChon(sc, "Nhập tháng (1-12): ", 1, 12);
                        int namThang = Ktradulieu.luaChon(sc, "Nhập năm (1900-9999): ", 1900, 9999);
                        YearMonth ym = YearMonth.of(namThang, thang);
                        tuNgay = ym.atDay(1);
                        denNgay = ym.atEndOfMonth();
                        break;
                    case 3:
                        int quy = Ktradulieu.luaChon(sc, "Nhập quý (1-4): ", 1, 4);
                        int namQuy = Ktradulieu.luaChon(sc, "Nhập năm (1900-9999): ", 1900, 9999);
                        int thangDau = (quy - 1) * 3 + 1;
                        tuNgay = LocalDate.of(namQuy, thangDau, 1);
                        denNgay = tuNgay.plusMonths(2).withDayOfMonth(tuNgay.plusMonths(2).lengthOfMonth());
                        break;

                    case 4:
                        int nam = Ktradulieu.luaChon(sc, "Nhập năm (1900-9999): ", 1900, 9999);

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

            } catch (DateTimeException e) {
                System.out.println(
                        "[Lỗi] Dữ liệu nhập không hợp lệ!");
            }
        }
    }

    public void batDau() {
        while (true) {
            try {
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
            } catch (NoSuchElementException e) {
                // Luồng nhập bị đóng (Ctrl+D / Ctrl+Z): lưu dữ liệu rồi thoát êm
                System.out.println("\n[Thông báo] Luồng nhập dữ liệu đã đóng. Đang lưu dữ liệu và thoát...");
                ghiDuLieu();
                break;
            } catch (RuntimeException e) {
                System.out.println("\n[Lỗi] Đã xảy ra lỗi không mong muốn: " + e);
                System.out.println("[Thông báo] Quay lại màn hình đăng nhập. Các thay đổi đã lưu trước đó không bị ảnh hưởng.");
                this.nguoiDangNhap = null;
            }
        }
    }

    public void hienThiManHinhDangNhap() {

        System.out.println();
        System.out.println("==============================================");
        System.out.println("=       HỆ THỐNG QUẢN LÝ CỬA HÀNG            =");
        System.out.println("=          THỨC ĂN & NƯỚC UỐNG               =");
        System.out.println("----------------------------------------------");
        System.out.println("=                 ĐĂNG NHẬP                  =");
        System.out.println("==============================================");
        System.out.println();

        int soLanSai = 0;

        while (true) {

            String tk = Ktradulieu.nhapChuoiKhongRong(
                    sc, "TÊN ĐĂNG NHẬP : ");

            if (tk.equals("0")) {
                this.nguoiDangNhap = null;
                ghiDuLieu();
                return;
            }

            String mk = Ktradulieu.nhapChuoiKhongRong(
                    sc, "MẬT KHẨU       : ");

            Nguoidung nd = dsNhanVien.timKiemTheoTaiKhoan(tk);

            if (nd != null && nd.getMatKhau().equals(mk)) {

                if (nd.isDaXoa()) {
                    System.out.println(
                            "[Từ chối] Tài khoản đã nghỉ việc hoặc bị khóa!\n");
                    continue;
                }

                this.nguoiDangNhap = nd;

                System.out.println("\n----------------------------------------------");
                System.out.println("=           ĐĂNG NHẬP THÀNH CÔNG!              =");
                System.out.println("  Xin chào: " + nd.getVaiTro()
                        + " - " + nd.getTenND());
                System.out.println("----------------------------------------------");

                return;
            }

            soLanSai++;

            System.out.println("\n[Lỗi] Sai tên đăng nhập hoặc mật khẩu!");
            System.out.println("Số lần đăng nhập sai: " + soLanSai);
            System.out.println("Vui lòng thử lại.\n");
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
            System.out.println("\n================================================");
            System.out.println("                 QUẢN LÝ KHO HÀNG");
            System.out.println("================================================");
            System.out.println("1. Tạo hóa đơn nhập");
            System.out.println("2. Xem danh sách hóa đơn nhập");
            System.out.println("3. Tra cứu hóa đơn nhập");
            System.out.println("4. Sửa hóa đơn nhập đang chờ duyệt");
            System.out.println("5. Hủy hóa đơn nhập đang chờ duyệt");
            System.out.println("6. Xem danh sách sản phẩm");
            System.out.println("7. Tìm kiếm sản phẩm");
            System.out.println("8. Xem danh sách nhà cung cấp");
            System.out.println("9. Thêm nhà cung cấp");
            System.out.println("10. Tìm kiếm nhà cung cấp");
            System.out.println("0. Quay lại menu nhân viên");
            System.out.println("================================================");

            int chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng (0-10): ", 0, 10);

            switch (chon) {
                case 1:
                    taoHoaDonNhap();
                    break;

                case 2:
                    dsHoaDon.xuatDanhSachHoaDonNhap();
                    break;

                case 3:
                    traCuuHoaDonNhap();
                    break;

                case 4:
                    dsHoaDon.suaHD(sc, nguoiDangNhap, dsSanPham);
                    break;

                case 5:
                    dsHoaDon.xoaHD(sc, nguoiDangNhap);
                    break;

                case 6:
                    dsSanPham.xuatDanhSach();
                    break;

                case 7:
                    timKiemSanPham();
                    break;

                case 8:
                    dsNhaCungCap.xuatDanhSach();
                    break;

                case 9: {
                    NhaCungCap ncc = new NhaCungCap();
                    ncc.nhapThongTin(sc, dsNhaCungCap);
                    dsNhaCungCap.them(ncc);
                    break;
                }

                case 10:
                    timKiemNhaCungCap();
                    break;

                case 0:
                    return;
            }
        }
    }

    public void menuThuNganTiepNhanKhachHang() {
        while (true) {
            System.out.println("\n========== THU NGÂN ==========");
            System.out.println("1. Tiếp nhận khách hàng");
            System.out.println("2. Lập hóa đơn bán");
            System.out.println("3. Quản lý hóa đơn bán");
            System.out.println("4. Tra cứu khuyến mãi");
            System.out.println("0. Quay lại menu nhân viên");
            System.out.println("==============================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (1-5): ", 0, 4);

            switch (chon) {
                case 1:
                    menuTiepNhanKhachHang();
                    break;

                case 2:
                    taoHoaDonBan();
                    break;

                case 3:
                    menuQuanLyHoaDonBan();
                    break;

                case 4:
                    menuXemVaTimKiemKhuyenMai();
                    break;

                case 0:
                    return;
            }
        }
    }

    public void menuKiemTraHoaDonBan(Hoadonban hdb) {
        while (true) {
            System.out.println("\n========== KIỂM TRA HÓA ĐƠN ==========");
            System.out.println("1. Thêm sản phẩm");
            System.out.println("2. Sửa số lượng sản phẩm");
            System.out.println("3. Xóa sản phẩm");
            System.out.println("4. Xem lại tổng tiền");
            System.out.println("0. Quay lại");
            System.out.println("=====================================");

            int chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng (0-4): ", 0, 4);

            switch (chon) {
                case 1:
                    hdb.themSanPham(
                            sc, dsSanPham, dsKhuyenMai, dsSPKM);
                    break;

                case 2:
                    hdb.suaSoLuongSanPham(
                            sc, dsSanPham, dsKhuyenMai, dsSPKM);
                    break;

                case 3:
                    hdb.xoaSanPham(sc);
                    break;

                case 4:
                    hdb.kiemTraHoaDon();
                    break;

                case 0:
                    return;
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
            System.out.println("5. Sửa thông tin khách hàng");
            System.out.println("6. Xóa khách hàng (chỉ Quản lý/Giám đốc)");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-6): ", 0, 6);
            switch (chon) {
                case 1: timKiemKhachHang(); break;
                case 2: dsKhachHang.xuatDanhSach(); break;
                case 3: dsKhachHang.themTuBanPhim(sc); break;
                case 4: xemLichSuMuaHangKhach(); break;
                case 5: {
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc SĐT khách hàng cần sửa: ");
                    dsKhachHang.sua(sc, nguoiDangNhap, ma);
                    break;
                }
                case 6: {
                    if (!(nguoiDangNhap instanceof QuanLyCuaHang) && !(nguoiDangNhap instanceof Giamdoc)) {
                        System.out.println("[Từ chối] Chỉ Quản lý hoặc Giám đốc mới được xóa khách hàng.");
                        break;
                    }
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc SĐT khách hàng cần xóa: ");
                    Khachhang khXoa = dsKhachHang.timKiem(ma);
                    if (khXoa == null) {
                        System.out.println("[Lỗi] Không tìm thấy khách hàng: " + ma);
                    } else if (Ktradulieu.nhapXacNhan(sc, "Xác nhận xóa khách hàng [" + khXoa.getMaKH()
                            + "] " + khXoa.getTenKH() + "?")) {
                        dsKhachHang.xoa(khXoa.getMaKH());
                    } else {
                        System.out.println("[Thông báo] Đã hủy thao tác xóa, dữ liệu không thay đổi.");
                    }
                    break;
                }
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
            System.out.println("7. Quản lý khách hàng");
            System.out.println("0. Đăng xuất");
            System.out.println("===========================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn phân hệ quản lý (0-7): ", 0, 7);
            switch (chon) {
                case 1: menuQuanLySanPham(); break;
                case 2: menuQuanLyKhuyenMai(); break;
                case 3: menuQuanLyNhapKhoQuyTrinh(); break;
                case 4: menuQuanLyNhaCungCap(); break;
                case 5: menuQuanLyNhanVien(); break;
                case 6: menuThongKeBaoCao(); break;
                case 7: menuTiepNhanKhachHang(); break;
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
                    SanPham spXoa = dsSanPham.timKiem(ma);
                    if (spXoa == null) {
                        System.out.println("[Lỗi] Không tìm thấy sản phẩm: " + ma);
                    } else if (!Ktradulieu.nhapXacNhan(sc, "Xác nhận xóa sản phẩm [" + spXoa.getMaSP()
                            + "] " + spXoa.getTenSP() + "?")) {
                        System.out.println("[Thông báo] Đã hủy thao tác xóa, dữ liệu không thay đổi.");
                    } else if (dsSanPham.xoaSP(ma, nguoiDangNhap)) {
                        // Dọn các liên kết khuyến mãi của sản phẩm vừa xóa để không còn dữ liệu mồ côi
                        if (dsSPKM.xoaLienKetMoCoi(dsSanPham, dsKhuyenMai) > 0) {
                            dsSPKM.ghiFile("data/sanphamkhuyenmai.txt");
                        }
                    }
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
                    if (dsKhuyenMai.timKiem(maKM) == null) {
                        System.out.println("[Lỗi] Không tìm thấy khuyến mãi: " + maKM);
                    } else if (Ktradulieu.nhapXacNhan(sc, "Xác nhận xóa khuyến mãi [" + maKM
                            + "] (các liên kết sản phẩm sẽ bị gỡ)?")) {
                        dsKhuyenMai.xoaKhuyenMai(maKM, dsSPKM);
                    } else {
                        System.out.println("[Thông báo] Đã hủy thao tác xóa, dữ liệu không thay đổi.");
                    }
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
                        try {
                            if (dsSPKM.them(new SPKhMai(maSP, maKM), dsSanPham, dsKhuyenMai)) {
                                dsSPKM.ghiFile("data/sanphamkhuyenmai.txt");
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("[Lỗi] " + e.getMessage());
                        }
                    }
                    break;
                }
                case 7: {
                    String maSP = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã sản phẩm: ");
                    String maKM = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã khuyến mãi: ");
                    if (dsSPKM.xoa(maSP, maKM)) {
                        System.out.println("[Thành công] Đã hủy áp dụng khuyến mãi cho sản phẩm.");
                    } else {
                        System.out.println("[Lỗi] Không tìm thấy liên kết sản phẩm - khuyến mãi này!");
                    }
                    break;
                }
                case 8: dsSPKM.xuatDanhSach(); break;
                case 0: return;
            }
        }
    }

    public void menuQuanLyNhapKhoQuyTrinh() {
        while (true) {
            System.out.println("\n================================================");
            System.out.println("           QUẢN LÝ NHẬP KHO");
            System.out.println("================================================");
            System.out.println("1. Xem danh sách hóa đơn nhập kho chờ duyệt");
            System.out.println("2. Duyệt hóa đơn nhập kho");
            System.out.println("3. Xem lịch sử hóa đơn nhập kho");
            System.out.println("4. Tìm kiếm hóa đơn nhập kho");
            System.out.println("5. Sửa hóa đơn nhập kho");
            System.out.println("6. Hủy hóa đơn nhập kho");
            System.out.println("0. Quay lại");
            System.out.println("================================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (0-6): ", 0, 6);

            switch (chon) {
                case 1:
                    dsHoaDon.xuatDanhSachHoaDonChoDuyet();
                    break;

                case 2:
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        ((QuanLyCuaHang) nguoiDangNhap)
                                .duyetHoaDon(sc, dsHoaDon, dsSanPham);
                    } else {
                        dsHoaDon.duyetHoaDon(sc, nguoiDangNhap, dsSanPham);
                    }

                    dsHoaDon.ghiFile("data/hoadon.txt");
                    dsSanPham.ghiFile("data/sanpham.txt");
                    break;

                case 3:
                    dsHoaDon.xuatDanhSachHoaDonNhap();
                    break;

                case 4:
                    traCuuHoaDonNhap();
                    break;

                case 5:
                    dsHoaDon.suaHD(sc, nguoiDangNhap,dsSanPham);
                    break;

                case 6:
                    dsHoaDon.xoaHD(sc, nguoiDangNhap);
                    break;

                case 0:
                    return;
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
                    NhaCungCap nccXoa = dsNhaCungCap.timKiem(ma);
                    if (nccXoa == null) {
                        System.out.println("[Lỗi] Không tìm thấy nhà cung cấp: " + ma);
                    } else if (Ktradulieu.nhapXacNhan(sc, "Xác nhận ngừng hợp tác với nhà cung cấp ["
                            + nccXoa.getMaNCC() + "] " + nccXoa.getTenNCC() + "?")) {
                        dsNhaCungCap.xoa(nguoiDangNhap, nccXoa.getMaNCC());
                    } else {
                        System.out.println("[Thông báo] Đã hủy thao tác xóa, dữ liệu không thay đổi.");
                    }
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

        // Khởi tạo thông tin hóa đơn
        hdb.nhapThongTin(sc, nvLap, dsHoaDon,
                dsKhachHang, dsSanPham, dsKhuyenMai, dsSPKM);

        while (true) {
            System.out.println("\n========== LẬP HÓA ĐƠN BÁN ==========");
            System.out.println("Mã hóa đơn: " + hdb.getMaHD());
            System.out.println("Nhân viên: " + nvLap.getMaND()
                    + " - " + nvLap.getTenND());

            String tenKhachHang = "Khách lẻ";

            if (!"KHACH_LE".equalsIgnoreCase(hdb.getMaKH())) {
                // Tìm thông tin khách hàng theo mã
                for (Khachhang kh : dsKhachHang.getDanhSach()) {
                    if (kh != null && kh.getMaKH() != null
                            && kh.getMaKH().equalsIgnoreCase(hdb.getMaKH())) {
                        tenKhachHang = kh.getTenKH();
                        break;
                    }
                }
            }

            System.out.println("Khách hàng: " + tenKhachHang);
            System.out.println("Số sản phẩm: " + hdb.getSoLuongCT());
            System.out.println("Tổng tiền: "
                    + Ktradulieu.dinhDangTien(hdb.tinhThanhTien()));

            System.out.println("------------------------------------");
            System.out.println("1. Tiếp nhận khách hàng");
            System.out.println("2. Kiểm tra và điều chỉnh hóa đơn");
            System.out.println("3. Thanh toán và hoàn tất");
            System.out.println("0. Hủy lập hóa đơn");
            System.out.println("====================================");

            int chon = Ktradulieu.luaChon(
                    sc, "Chọn chức năng (0-3): ", 0, 3);

            switch (chon) {
                case 1:
                    hdb.tiepNhanKhachHang(sc, dsKhachHang);
                    break;

                case 2:
                    menuKiemTraHoaDonBan(hdb);
                    break;

                case 3:
                    boolean thanhCong = hdb.thanhToanHoaDon(
                            sc, nvLap, dsKhachHang, dsSanPham);

                    if (thanhCong) {
                        if (dsHoaDon.themHD(hdb)) {
                            dsHoaDon.ghiFile("data/hoadon.txt");
                            dsSanPham.ghiFile("data/sanpham.txt");
                            dsKhachHang.ghiFile("data/khachhang.txt");

                            System.out.println(
                                    "[Thành công] Đã hoàn tất và lưu hóa đơn bán!");
                        }
                        return;
                    }
                    break;

                case 0:
                    System.out.println(
                            "[Thông báo] Đã hủy lập hóa đơn: " + hdb.getMaHD());
                    return;
            }
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

        if (dsNhanVien.them(nvMoi)) {
            System.out.println("Đã thêm và lưu nhân viên thành công!");
        } else {
            System.out.println("[Lỗi] Không thể thêm nhân viên (dữ liệu bị trùng hoặc không hợp lệ). Dữ liệu không thay đổi.");
        }
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
        dsSPKM.docFile("data/sanphamkhuyenmai.txt");
        dsHoaDon.docFile("data/hoadon.txt", dsNhanVien);
    }
    private void ghiDuLieu() {
        dsNhanVien.ghiFile("data/nhanvien.txt");
        dsKhachHang.ghiFile("data/khachhang.txt");
        dsNhaCungCap.ghiFile("data/nhacungcap.txt");
        dsSanPham.ghiFile("data/sanpham.txt");
        dsKhuyenMai.ghiFile("data/khuyenmai.txt");
        dsSPKM.ghiFile("data/sanphamkhuyenmai.txt");
        dsHoaDon.ghiFile("data/hoadon.txt");
    }
}