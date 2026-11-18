package giaodien;

import util.BangConsole;
import java.util.ArrayList;

import java.io.File;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
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
import person.Admin;
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

        Admin admin = new Admin(
                "AD001", "Lê Hoàng Long", "admin",
                "123456", "0909123456", false
        );
        dsNhanVien.them(admin);

        dsNhanVien.ghiFile("data/nhanvien.txt");

        System.out.println("Đã tạo và lưu 3 tài khoản ban đầu!");
    }
    // HÀM NHẬP NGÀY DÙNG CHUNG
    private LocalDate nhapNgay(Scanner sc, String thongBao) {
        return Ktradulieu.nhapNgay(sc, thongBao);
    }
    // HÀM CHỌN KỲ BÁO CÁO DÙNG CHUNG
    private LocalDate[] chonKyBaoCao(Scanner sc) {

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

    public void batDau(Scanner sc) {
        if (sc == null) throw new IllegalArgumentException("Scanner không được null!");
        while (true) {
            try {
                hienThiManHinhDangNhap(sc);
                if (this.nguoiDangNhap == null) {
                    System.out.println("\nCảm ơn bạn đã sử dụng chương trình. Tạm biệt!");
                    break;
                }

                if (this.nguoiDangNhap instanceof Admin) {
                    menuAdmin(sc);
                } else if (this.nguoiDangNhap instanceof QuanLyCuaHang) {
                    menuQuanLyCuaHang(sc);
                } else if (this.nguoiDangNhap instanceof NhanVien) {
                    menuNhanVien(sc);
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

      private static final int WIDTH = 56;

    private static String vienTren() {
        return "╔" + "═".repeat(WIDTH - 2) + "╗";
    }

    private static String vienDuoi() {
        return "╚" + "═".repeat(WIDTH - 2) + "╝";
    }

    private static String ngan() {
        return "╟" + "─".repeat(WIDTH - 2) + "╢";
    }

     public void hienThiManHinhDangNhap(Scanner sc) {

        System.out.println();
        System.out.println(vienTren());
        System.out.println("║" + " ".repeat(14) + "HỆ THỐNG QUẢN LÝ CỬA HÀNG" + " ".repeat(15) + "║");
        System.out.println("║" + " ".repeat(17) + "THỨC ĂN & NƯỚC UỐNG" + " ".repeat(18) + "║");
        System.out.println(ngan());
        System.out.println("║" + " ".repeat(22) + "ĐĂNG NHẬP" + " ".repeat(23) + "║");
        System.out.println(vienDuoi());
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
                            "[Từ chối] Tài khoản " + nd.getVaiTro() + " đã bị xóa hoặc khóa. "
                            + "Cần thêm nhân sự mới cho chức vụ này để thực hiện công việc.\n");
                    continue;
                }

                this.nguoiDangNhap = nd;

                System.out.println();
                System.out.println(vienTren());
                System.out.println("║" + " ".repeat(16) + "ĐĂNG NHẬP THÀNH CÔNG!" + " ".repeat(17) + "║");
                System.out.println(vienDuoi());
                System.out.println("  Xin chào: " + nd.getVaiTro()
                        + " - " + nd.getTenND());

                return;
            }

            soLanSai++;

            System.out.println("\n[Lỗi] Sai tên đăng nhập hoặc mật khẩu!");
            System.out.println("Số lần đăng nhập sai: " + soLanSai);
            System.out.println("Vui lòng thử lại.\n");
        }
    }

    // 1. MENU NHÂN VIÊN
    public void menuNhanVien(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_BAN_HANG")) return;
        while (true) {
            System.out.println("\n========== MENU NHÂN VIÊN ==========");
            System.out.println("1. Quản lý kho hàng");
            System.out.println("2. Thu ngân - Tiếp nhận khách hàng");
            System.out.println("3. Đăng xuất");
            System.out.println("===================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (1-3): ", 1, 3);
            switch (chon) {
                case 1: menuQuanLyKhoHang(sc); break;
                case 2: menuThuNganTiepNhanKhachHang(sc); break;
                case 3:
                    System.out.println(">> Đã đăng xuất khỏi tài khoản nhân viên.");
                    this.nguoiDangNhap = null;
                    return;
            }
        }
    }

    public void menuQuanLyKhoHang(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_NHAP_KHO")) return;
        while (true) {
            System.out.println("\n========== QUẢN LÝ KHO HÀNG ==========");
            System.out.println("1. Nhập thêm hàng - lập phiếu theo nhà cung cấp");
            System.out.println("2. Xem danh sách hóa đơn nhập");
            System.out.println("3. Tra cứu hóa đơn nhập");
            System.out.println("4. Xem danh sách sản phẩm");
            System.out.println("5. Tìm kiếm sản phẩm");
            System.out.println("6. Thống kê sản phẩm hết hạn còn tồn");
            System.out.println("7. Thống kê sản phẩm sắp hết hạn (30 ngày)");
            System.out.println("8. Xử lý hàng sắp hết hạn (Admin/Quản lý)");
            System.out.println("9. Loại bỏ số lượng hàng hết hạn");
            System.out.println("10. Nhập mã hóa đơn nhập để chỉnh sửa");
            System.out.println("0. Quay lại");
            System.out.println("=====================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (0-10): ", 0, 10);
            switch (chon) {
                case 1: taoHoaDonNhap(sc); break;
                case 2:
                    dsHoaDon.xuatDanhSachHoaDonNhap();
                    break;
                case 3: traCuuHoaDonNhap(sc); break;
                case 4: dsSanPham.xuatDanhSach(); break;
                case 5: timKiemSanPham(sc); break;
                case 6: dsSanPham.thongKeSanPhamHetHan(); break;
                case 7: dsSanPham.thongKeSanPhamSapHetHan(); break;
                case 8: menuXuLyHangSapHetHan(sc); break;
                case 9: loaiBoHangHetHan(sc); break;
                case 10: dsHoaDon.chinhSuaHoaDonNhapTheoMa(sc, nguoiDangNhap, dsNhaCungCap, dsSanPham); break;
                case 0: return;
                default:
                    System.out.println("Lựa chọn không hợp lệ");
            }
        }
    }

    public void menuXuLyHangSapHetHan(Scanner sc) {
        if (!yeuCauQuyen("QUAN_LY_KHUYEN_MAI")) return;
        while (true) {
            dsSanPham.thongKeSanPhamSapHetHan();
            System.out.println("Giảm 50% khi còn 0-3 ngày; 30% khi còn 4-7 ngày; 15% khi còn 8-15 ngày; 10% khi còn 16-30 ngày.");
            System.out.println("Áp dụng cho thức ăn và đồ uống còn tồn; giữ nguyên khuyến mãi thủ công.");
            System.out.println("1. Tạo/cập nhật giảm giá cận hạn\n0. Quay lại");
            int chon = Ktradulieu.luaChon(sc, "Chọn (0-1): ", 0, 1);
            if (chon == 0) return;
            int soCapNhat = dsSPKM.xuLyHangSapHetHan(dsSanPham, dsKhuyenMai, nguoiDangNhap);
            dsKhuyenMai.ghiFile("data/khuyenmai.txt");
            dsSPKM.ghiFile("data/sanphamkhuyenmai.txt");
            System.out.println("Đã tạo/cập nhật giảm giá cận hạn cho " + soCapNhat + " sản phẩm.");
        }
    }

    public void loaiBoHangHetHan(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_KIEM_KE_KHO")) return;
        dsSanPham.thongKeSanPhamHetHan();
        if (dsSanPham.locSanPhamHetHan(LocalDate.now()).length == 0) return;
        if (Ktradulieu.nhapXacNhan(sc, "Xác nhận đưa số lượng hàng hết hạn về 0 (giữ danh mục sản phẩm)?")) {
            dsSanPham.loaiBoSoLuongHetHan(nguoiDangNhap);
            if (dsSPKM.goGiamGiaCanHanKhongConPhuHop(dsSanPham, dsKhuyenMai, LocalDate.now()) > 0) {
                dsSPKM.ghiFile("data/sanphamkhuyenmai.txt");
            }
        } else System.out.println("Đã hủy loại bỏ hàng hết hạn.");
    }

    public void menuThuNganTiepNhanKhachHang(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_BAN_HANG")) return;
        while (true) {
            System.out.println("\n========== THU NGÂN ==========");
            System.out.println("1. Tiếp nhận khách hàng");
            System.out.println("2. Lập hóa đơn bán");
            System.out.println("3. Quản lý hóa đơn bán");
            System.out.println("4. Tra cứu khuyến mãi");
            System.out.println("0. Quay lại");
            System.out.println("==============================");

            int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (0-4): ", 0, 4);

            switch (chon) {
                case 1:
                    menuTiepNhanKhachHang(sc);
                    break;

                case 2:
                    taoHoaDonBan(sc);
                    break;

                case 3:
                    menuQuanLyHoaDonBan(sc);
                    break;

                case 4:
                    menuXemVaTimKiemKhuyenMai(sc);
                    break;

                case 0:
                    return;
            }
        }
    }

    public void menuKiemTraHoaDonBan(Scanner sc, Hoadonban hdb) {
        if (!yeuCauQuyen("QUYEN_BAN_HANG")) return;
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

    public void menuTiepNhanKhachHang(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_BAN_HANG")) return;
        while (true) {
            System.out.println("\n--- TIẾP NHẬN KHÁCH HÀNG ---");
            System.out.println("1. Tìm kiếm khách hàng");
            System.out.println("2. Xem danh sách khách hàng");
            System.out.println("3. Thêm khách hàng mới");
            System.out.println("4. Xem lịch sử mua hàng của khách");
            System.out.println("5. Sửa thông tin khách hàng");
            System.out.println("6. Xóa khách hàng (chỉ Quản lý/Admin)");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-6): ", 0, 6);
            switch (chon) {
                case 1: timKiemKhachHang(sc); break;
                case 2: dsKhachHang.xuatDanhSach(); break;
                case 3: dsKhachHang.themTuBanPhim(sc); break;
                case 4: xemLichSuMuaHangKhach(sc); break;
                case 5: {
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc SĐT khách hàng cần sửa: ");
                    dsKhachHang.sua(sc, nguoiDangNhap, ma);
                    break;
                }
                case 6: {
                    if (!yeuCauQuyen("QUAN_LY_KHACH_HANG")) {
                        System.out.println("[Từ chối] Chỉ Quản lý hoặc Admin mới được xóa khách hàng.");
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

    public void menuXemVaTimKiemKhuyenMai(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_TRA_CUU")) return;
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

    public void menuQuanLyHoaDonBan(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_TRA_CUU")) return;
        while (true) {
            System.out.println("\n--- QUẢN LÝ HÓA ĐƠN BÁN ---");
            System.out.println("1. Xem danh sách hóa đơn bán");
            System.out.println("2. Tìm kiếm và xem chi tiết hóa đơn bán");
            System.out.println("3. Tra cứu theo ngày, khách hàng, tổng tiền hoặc trạng thái");
            System.out.println("4. Thống kê Top N sản phẩm bán chạy");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-4): ", 0, 4);
            switch (chon) {
                case 1: {
                    List<HoaDon> dsBan = new ArrayList<>();
                    for (int i = 0; i < dsHoaDon.getSoLuong(); i++) {
                        HoaDon hd = dsHoaDon.get(i);
                        if (hd instanceof Hoadonban) dsBan.add(hd);
                    }
                    System.out.println(BangConsole.tuDanhSach(dsBan));
                    break;
                }
                case 2: xemChiTietHoaDonBan(sc); break;
                case 3: dsHoaDon.traCuuHoaDon(sc, "HDBAN"); break;
                case 4: dsHoaDon.thongKeTopNSanPham(sc); break;
                case 0: return;
            }
        }
    }

    // 2. MENU QUẢN LÝ CỬA HÀNG
    public void menuQuanLyCuaHang(Scanner sc) {
        if (!yeuCauQuyen("QUAN_LY_KHO")) return;
        menuDieuHanh(sc);
    }

    public void menuQuanLySanPham(Scanner sc) {
        if (!yeuCauQuyen("QUAN_LY_SAN_PHAM")) return;
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
                case 1: dsSanPham.themSanPhamTuBanPhim(sc, dsNhaCungCap); break;
                case 2: dsSanPham.xuatDanhSach(); break;
                case 3: timKiemSanPham(sc); break;
                case 4: {
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã sản phẩm cần sửa: ");
                    dsSanPham.suaSP(sc, ma, nguoiDangNhap, dsNhaCungCap);
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
                    break;
                case 0: return;
            }
        }
    }

    public void menuQuanLyKhuyenMai(Scanner sc) {
        if (!yeuCauQuyen("QUAN_LY_KHUYEN_MAI")) return;
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
            System.out.println("9. Thống kê khuyến mãi đang áp dụng, hết hạn hoặc chưa bắt đầu");
            System.out.println("10. Tạo/cập nhật giảm giá thực phẩm cận hạn");
            System.out.println("0. Quay lại ");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-10): ", 0, 10);
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
                case 9: dsKhuyenMai.thongKeTrangThai(sc); break;
                case 10: {
                    System.out.println("Giảm 50% nếu còn 0-3 ngày, 30% nếu còn 4-7 ngày, 15% nếu còn 8-15 ngày.");
                    int soCapNhat = dsSPKM.tuDongGiamGiaCanHSD(dsSanPham, dsKhuyenMai);
                    if (soCapNhat > 0) {
                        dsKhuyenMai.ghiFile("data/khuyenmai.txt");
                        dsSPKM.ghiFile("data/sanphamkhuyenmai.txt");
                    }
                    System.out.println("Đã tạo/cập nhật khuyến mãi cận hạn cho " + soCapNhat + " sản phẩm.");
                    break;
                }
                case 0: return;
            }
        }
    }

    public void menuQuanLyNhapKhoQuyTrinh(Scanner sc) {
        if (!yeuCauQuyen("DUYET_HOA_DON")) return;
        while (true) {
            System.out.println("\n--- 3. QUẢN LÝ NHẬP KHO ---");
            System.out.println("1. Xem danh sách hóa đơn nhập kho chờ duyệt");
            System.out.println("2. Thanh toán và duyệt hóa đơn nhập kho");
            System.out.println("3. Xem lịch sử hóa đơn nhập kho");
            System.out.println("4. Tìm kiếm hóa đơn nhập kho");
            System.out.println("5. Tra cứu theo ngày, nhà cung cấp, tổng tiền hoặc trạng thái");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-5): ", 0, 5);
            switch (chon) {
                case 1:
                    dsHoaDon.xuatDanhSachHoaDonChoDuyet();
                    break;
                case 2:
                    if (dsHoaDon.duyetHoaDon(sc, nguoiDangNhap, dsSanPham, dsNhaCungCap)) {
                        dsHoaDon.ghiFile("data/hoadon.txt");
                        dsSanPham.ghiFile("data/sanpham.txt");
                        if (dsSPKM.goGiamGiaCanHanKhongConPhuHop(dsSanPham, dsKhuyenMai, LocalDate.now()) > 0) {
                            dsSPKM.ghiFile("data/sanphamkhuyenmai.txt");
                        }
                    }
                    break;
                case 3:
                    dsHoaDon.xuatDanhSachHoaDonNhap();
                    break;
                case 4: traCuuHoaDonNhap(sc); break;
                case 5: dsHoaDon.traCuuHoaDon(sc, "HDNHAP"); break;
                case 0: return;
            }
        }
    }

    public void menuQuanLyNhaCungCap(Scanner sc) {
        if (!yeuCauQuyen("QUAN_LY_NHA_CUNG_CAP")) return;
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
                case 3: timKiemNhaCungCap(sc); break;
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

    public void menuQuanLyNhanVien(Scanner sc) {
        if (!yeuCauQuyen("QUAN_LY_NHAN_VIEN")) return;
        while (true) {
            System.out.println("\n--- 5. QUẢN LÝ NHÂN VIÊN ---");
            System.out.println("1. Xem danh sách nhân viên");
            System.out.println("2. Tìm kiếm nhân viên");
            System.out.println("3. Thêm nhân viên");
            System.out.println("4. Cập nhật thông tin nhân viên");
            System.out.println("5. Xóa nhân viên");
            System.out.println("6. Tính lương nhân viên");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-6): ", 0, 6);

            switch (chon) {
                case 1:
                    System.out.println("\n============ DANH SÁCH NHÂN VIÊN ============");
                    System.out.println(BangConsole.tuDanhSach(dsNhanVien.getDanhSachNhanVien()));
                    break;

                case 2: {
                    String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc tên: ");
                    Nguoidung nd = dsNhanVien.timKiem(key);

                    if (nd instanceof NhanVien) {
                        System.out.println("Kết quả:\n" + nd);
                    } else {
                        System.out.println("Không tìm thấy nhân viên phù hợp!");
                    }
                    break;
                }

                case 3:
                    themNhanVien(sc);
                    break;

                case 4: {
                    String maNV = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã nhân viên: ");
                    dsNhanVien.sua(sc, nguoiDangNhap, maNV);
                    dsNhanVien.ghiFile("data/nhanvien.txt");
                    break;
                }

                case 5: {
                    String maNV = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã nhân viên cần xóa: ");
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

    public void menuThongKeBaoCao(Scanner sc) {
        if (!yeuCauQuyen("XEM_THONG_KE_DOANH_THU")) return;
        while (true) {
            System.out.println("\n================ MENU BÁO CÁO ================");
            System.out.println("1. Báo cáo tổng quan hóa đơn");
            System.out.println("2. Báo cáo doanh thu và chi phí");
            System.out.println("3. Báo cáo thuế VAT");
            System.out.println("4. Báo cáo sản phẩm bán được và không bán được");
            System.out.println("5. Báo cáo nhân viên và bảng lương");
            System.out.println("6. Báo cáo tồn kho");
            System.out.println("7. Thống kê Top N sản phẩm bán chạy");
            System.out.println("0. Quay lại");
            System.out.println("===============================================");

            int chon = Ktradulieu.luaChon(sc, "Chọn (0-7): ", 0, 7);
            switch (chon) {
                case 1: {
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        LocalDate[] ky = chonKyBaoCao(sc);
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
                        LocalDate[] ky = chonKyBaoCao(sc);

                        if (ky != null) {
                            ((QuanLyCuaHang) nguoiDangNhap).thongKeDoanhThuLoiNhuan(dsHoaDon, ky[0], ky[1]);
                        }
                    }
                    break;
                }
                case 3: {
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        LocalDate[] ky = chonKyBaoCao(sc);

                        if (ky != null) {
                            ((QuanLyCuaHang) nguoiDangNhap)
                                    .thongKeThue(dsHoaDon, ky[0], ky[1]);
                        }
                    }
                    break;
                }
                case 4: {
                    if (nguoiDangNhap instanceof QuanLyCuaHang) {
                        LocalDate[] ky = chonKyBaoCao(sc);

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
                case 7:
                    if (nguoiDangNhap instanceof QuanLyCuaHang) dsHoaDon.thongKeTopNSanPham(sc);
                    break;
                default:
                    System.out.println("[Lỗi] Vui lòng chọn từ 0 đến 7!");
                    break;
            }
        }
    }
    public void menuQuanLyNhanSuAdmin(Scanner sc) {
        if (!yeuCauQuyen("QUAN_TRI_TAI_KHOAN")) return;
        while (true) {
            System.out.println("\n========== QUẢN TRỊ NHÂN SỰ VÀ TÀI KHOẢN ==========");
            System.out.println("1. Xem toàn bộ tài khoản");
            System.out.println("2. Xóa nhân viên");
            System.out.println("3. Thêm nhân viên");
            System.out.println("4. Thêm quản lý cửa hàng");
            System.out.println("5. Thêm Admin");
            System.out.println("6. Cập nhật thông tin / đặt lại mật khẩu tài khoản");
            System.out.println("7. Khóa tài khoản / cho nghỉ việc");
            System.out.println("8. Mở khóa tài khoản / khôi phục nhân sự");
            System.out.println("9. Quản lý nhân viên và tính lương");
            System.out.println("0. Quay lại");
            int chon = Ktradulieu.luaChon(sc, "Chọn (0-9): ", 0, 9);
            switch (chon) {
                case 1: dsNhanVien.xuatDanhSach(); break;
                case 2: dsNhanVien.xoa(sc, nguoiDangNhap,
                        Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã nhân viên cần xóa: ")); break;
                case 3: themNhanVien(sc); break;
                case 4: themQuanLy(sc); break;
                case 5: {
                    Admin admin = new Admin();
                    admin.nhapThongTin(sc, dsNhanVien);
                    dsNhanVien.them(nguoiDangNhap, admin);
                    break;
                }
                case 6: {
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã tài khoản cần cập nhật: ");
                    dsNhanVien.sua(sc, nguoiDangNhap, ma);
                    break;
                }
                case 7:
                case 8: {
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã tài khoản: ");
                    if (Ktradulieu.nhapXacNhan(sc, "Xác nhận " + (chon == 7 ? "khóa" : "mở khóa")
                            + " tài khoản [" + ma + "]?")) {
                        dsNhanVien.doiTrangThaiTaiKhoan(nguoiDangNhap, ma, chon == 7);
                    }
                    break;
                }
                case 9: menuQuanLyNhanVien(sc); break;
                case 0: return;
            }
        }
    }

    // 3. MENU Admin
    public void menuAdmin(Scanner sc) {
        if (!yeuCauQuyen("QUAN_TRI_TAI_KHOAN")) return;
        menuDieuHanh(sc);
    }

    private void menuDieuHanh(Scanner sc) {
        boolean laAdmin = nguoiDangNhap instanceof Admin;
        while (true) {
            System.out.println("\n========== MENU " + (laAdmin ? "ADMIN" : "QUẢN LÝ CỬA HÀNG") + " ==========");
            System.out.println("1. Quản lý sản phẩm");
            System.out.println("2. Quản lý khuyến mãi");
            System.out.println(laAdmin ? "3. Xem hóa đơn" : "3. Quản lý và duyệt nhập kho");
            System.out.println("4. Quản lý nhà cung cấp");
            if (laAdmin) System.out.println("5. Quản trị nhân sự và tài khoản");
            System.out.println("6. Báo cáo - thống kê");
            System.out.println("7. Quản lý khách hàng");
            if (!laAdmin) {
                System.out.println("8. Nghiệp vụ kho - lập hóa đơn nhập");
                System.out.println("9. Thu ngân - lập hóa đơn bán");
                System.out.println("10. Sửa / hủy hóa đơn chưa hoàn tất");
            }
            System.out.println("0. Đăng xuất");
            int chon = Ktradulieu.luaChon(sc, "Chọn (0-10): ", 0, 10);
            switch (chon) {
                case 1: menuQuanLySanPham(sc); break;
                case 2: menuQuanLyKhuyenMai(sc); break;
                case 3:
                    if (laAdmin) menuXemHoaDon(sc);
                    else menuQuanLyNhapKhoQuyTrinh(sc);
                    break;
                case 4: menuQuanLyNhaCungCap(sc); break;
                case 5:
                    if (laAdmin) menuQuanLyNhanSuAdmin(sc);
                    else System.out.println("[Từ chối] Chỉ Admin được quản lý nhân sự.");
                    break;
                case 6: menuThongKeBaoCao(sc); break;
                case 7: menuTiepNhanKhachHang(sc); break;
                case 8: menuQuanLyKhoHang(sc); break;
                case 9: menuThuNganTiepNhanKhachHang(sc); break;
                case 10: menuDieuChinhHoaDon(sc); break;
                case 0:
                    ghiDuLieu();
                    nguoiDangNhap = null;
                    System.out.println(">> Đã đăng xuất.");
                    return;
            }
        }
    }

    public void menuXemHoaDon(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_TRA_CUU")) return;
        while (true) {
            System.out.println("\n1. Xem danh sách hóa đơn\n2. Xem chi tiết hóa đơn\n0. Quay lại");
            switch (Ktradulieu.luaChon(sc, "Chọn (0-2): ", 0, 2)) {
                case 1: dsHoaDon.xuatDanhSach(); break;
                case 2: dsHoaDon.xemChiTietHoaDon(sc); break;
                case 0: return;
            }
        }
    }

    public void menuDieuChinhHoaDon(Scanner sc) {
        if (!yeuCauQuyen("SUA_HOA_DON")) return;
        while (true) {
            System.out.println("\n========== ĐIỀU CHỈNH HÓA ĐƠN ==========");
            System.out.println("1. Sửa thông tin hóa đơn chưa hoàn tất");
            System.out.println("2. Hủy hóa đơn chưa hoàn tất");
            System.out.println("0. Quay lại");
            int chon = Ktradulieu.luaChon(sc, "Chọn (0-2): ", 0, 2);
            switch (chon) {
                case 1: dsHoaDon.suaHD(sc, nguoiDangNhap); break;
                case 2: dsHoaDon.xoaHD(sc, nguoiDangNhap); break;
                case 0: return;
            }
        }
    }

    private boolean yeuCauQuyen(String maQuyen) {
        if (nguoiDangNhap != null && nguoiDangNhap.coQuyen(maQuyen)) return true;
        System.out.println("[Từ chối] Tài khoản không có quyền truy cập chức năng này hoặc đã bị khóa!");
        return false;
    }

    // =========================================================
    // HỖ TRỢ NGHIỆP VỤ - GỌI ĐÚNG HÀM TRONG CÁC CLASS CỦA BẠN
    // =========================================================
    private Nguoidung layNguoiThaoTac() {
        return nguoiDangNhap != null && !nguoiDangNhap.isDaXoa() ? nguoiDangNhap : null;
    }

    public void taoHoaDonNhap(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_NHAP_KHO")) return;
        dsHoaDon.nhapThemHang(sc, nguoiDangNhap, dsNhaCungCap, dsSanPham);
    }

    public void taoHoaDonBan(Scanner sc) {
        if (!yeuCauQuyen("QUYEN_BAN_HANG")) return;
        Nguoidung nvLap = layNguoiThaoTac();

        if (nvLap == null) {
            System.out.println("[Lỗi] Không tìm thấy tài khoản hợp lệ để lập hóa đơn!");
            return;
        }

        Hoadonban hdb = new Hoadonban();

        if (hdb.lapHoaDon(sc, nvLap, dsHoaDon, dsKhachHang, dsSanPham, dsKhuyenMai, dsSPKM)
                && dsHoaDon.themHD(hdb)) {
            dsHoaDon.ghiFile("data/hoadon.txt");
            dsSanPham.ghiFile("data/sanpham.txt");
            dsKhachHang.ghiFile("data/khachhang.txt");
            System.out.println("[Thành công] Đã hoàn tất và lưu hóa đơn bán!");
        }
    }

    public void timKiemSanPham(Scanner sc) {
        String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc tên sản phẩm: ").toLowerCase();
        List<SanPham> ketQua = new ArrayList<>();
        System.out.println("\n============ KẾT QUẢ TÌM KIẾM SẢN PHẨM ============");
        for (int i = 0; i < dsSanPham.getSoLuong(); i++) {
            SanPham sp = dsSanPham.get(i);
            if (sp != null && (sp.getMaSP().toLowerCase().contains(key) || sp.getTenSP().toLowerCase().contains(key))) {
                ketQua.add(sp);
            }
        }
        if (ketQua.isEmpty()) {
            System.out.println("Không tìm thấy sản phẩm phù hợp.");
        } else {
            System.out.println(BangConsole.tuDanhSach(ketQua));
        }
    }

    public void timKiemNhaCungCap(Scanner sc) {
        String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc tên NCC: ");
        NhaCungCap ncc = dsNhaCungCap.timKiem(key);
        System.out.println(ncc != null ? "\nKết quả:\n" + ncc : "Không tìm thấy nhà cung cấp: " + key);
    }

    public void timKiemKhachHang(Scanner sc) {
        String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc SĐT: ");
        Khachhang kh = dsKhachHang.timKiem(key);
        System.out.println(kh != null ? "\nThông tin:\n" + kh : "Không tìm thấy khách hàng: " + key);
    }

    public void xemLichSuMuaHangKhach(Scanner sc) {
        String key = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hoặc SĐT: ");
        Khachhang kh = dsKhachHang.timKiem(key);
        if (kh == null) {
            System.out.println("Không tìm thấy khách hàng: " + key);
            return;
        }
        System.out.println("\nLỊCH SỬ MUA HÀNG CỦA: " + kh.getTenKH() + " [" + kh.getMaKH() + "]");
        int dem = 0;
        BangConsole bang = new BangConsole("STT", "MÃ HĐ", "NGÀY LẬP", "TỔNG TIỀN HÀNG", "TRẠNG THÁI").canPhai(3);
        for (int i = 0; i < dsHoaDon.getSoLuong(); i++) {
            HoaDon hd = dsHoaDon.get(i);
            if (hd instanceof Hoadonban && ((Hoadonban) hd).getMaKH().equalsIgnoreCase(kh.getMaKH())) {
                dem++;
                bang.themDong(dem, hd.getMaHD(), Ktradulieu.dinhDangNgay(hd.getNgayLap()),
                        Ktradulieu.dinhDangTien(hd.tinhTongTien()), hd.getTrangThai());
            }
        }
        if (dem == 0) {
            System.out.println("Khách hàng này chưa có đơn mua hàng nào.");
        } else {
            System.out.println(bang);
        }
    }

    public void traCuuHoaDonNhap(Scanner sc) {
        String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hóa đơn nhập: ");
        HoaDon hd = dsHoaDon.timKiem(ma);
        if (hd instanceof HoadonNhap) {
            System.out.println("\n--- CHI TIẾT HÓA ĐƠN NHẬP ---");
            hd.hienThiHoaDon();
        } else {
            System.out.println("Không tìm thấy hóa đơn nhập mã: " + ma);
        }
    }

    public void xemChiTietHoaDonBan(Scanner sc) {
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

        BangConsole bang = new BangConsole("STT", "MÃ KM", "LOẠI KHUYẾN MÃI", "NGÀY BẮT ĐẦU",
                "NGÀY KẾT THÚC", "CHI TIẾT", "SẢN PHẨM ÁP DỤNG");
        int stt = 0;
        for (Khuyenmai km : listKM) {
            if (km == null) {
                continue;
            }

            List<String> spList =
                    dsSPKM.layDanhSachMaSPTheoKM(km.getMaKM());
            Object[] duLieu = km.layDongBang();
            Object[] dong = new Object[duLieu.length + 2];
            dong[0] = ++stt;
            System.arraycopy(duLieu, 0, dong, 1, duLieu.length);
            dong[dong.length - 1] = spList == null || spList.isEmpty()
                    ? "Chưa gán" : String.join(System.lineSeparator(), spList);
            bang.themDong(dong);
        }
        System.out.println(bang);
    }
    private void themNhanVien(Scanner sc) {

        if (!yeuCauQuyen("QUAN_LY_NHAN_VIEN")) {
            System.out.println("[Lỗi] Bạn không có quyền thêm nhân viên!");
            return;
        }

        NhanVien nvMoi = new NhanVien();
        nvMoi.nhapThongTin(sc, dsNhanVien);

        if (dsNhanVien.them(nguoiDangNhap, nvMoi)) {
            System.out.println("Đã thêm và lưu nhân viên thành công!");
        } else {
            System.out.println("[Lỗi] Không thể thêm nhân viên (dữ liệu bị trùng hoặc không hợp lệ). Dữ liệu không thay đổi.");
        }
    }

    // Thêm quản lý cửa hàng - Chỉ Admin được phép
    private void themQuanLy(Scanner sc) {

        if (!yeuCauQuyen("QUAN_TRI_TAI_KHOAN")) {
            System.out.println("[Lỗi] Chỉ Admin mới có quyền thêm quản lý!");
            return;
        }

        QuanLyCuaHang qlMoi = new QuanLyCuaHang();
        qlMoi.nhapThongTin(sc, dsNhanVien);

        if (!dsNhanVien.them(nguoiDangNhap, qlMoi)) {
            System.out.println("[Lỗi] Không thể tạo tài khoản quản lý!");
        }
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
