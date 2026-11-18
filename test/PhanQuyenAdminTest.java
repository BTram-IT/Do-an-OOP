import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import doitac.DSNhaCungCap;
import doitac.NhaCungCap;
import giaodien.Giaodien;
import hoadon.CTHDBan;
import hoadon.CTHDonNhap;
import hoadon.DSHoadon;
import hoadon.HoaDon;
import hoadon.HoadonNhap;
import hoadon.Hoadonban;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import person.Admin;
import person.DSNhanVien;
import person.DsKhachHang;
import person.Nguoidung;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.ThucAn;
import thanhtoan.TienMat;

/** Kiểm tra phân quyền bằng dữ liệu riêng, không ghi vào data của dự án. */
public class PhanQuyenAdminTest {
    private static int soKiemTra;
    private static final LocalDate HOM_NAY = LocalDate.now();

    private static class NhanSuThu extends DSNhanVien {
        @Override public boolean ghiFile(String duongDan) { return true; }
    }

    private static class KhoThu extends DSSanPham {
        @Override public void ghiFile(String duongDan) { }
    }

    private static class NhaCungCapThu extends DSNhaCungCap {
        @Override public void ghiFile(String duongDan) { }
    }

    private static Scanner phim(String... dong) {
        return new Scanner(String.join("\n", dong) + "\n");
    }

    private static void dung(boolean dieuKien, String thongBao) {
        soKiemTra++;
        if (!dieuKien) throw new AssertionError(thongBao);
    }

    private static Admin admin() {
        return new Admin("AD001", "Admin thử", "admin", "123456", "0912345678", false);
    }

    private static QuanLyCuaHang quanLy() {
        return new QuanLyCuaHang("QL001", "Quản lý thử", "quanly", "123456", "0912345678",
                "Quản lý", false, "Toàn cửa hàng", 1.5);
    }

    private static NhanVien nhanVien() {
        return new NhanVien("NV001", "Nhân viên thử", "nhanvien", "123456", "0912345678", HOM_NAY, false);
    }

    private static void quyenVaNhanSu() {
        Admin ad = admin();
        QuanLyCuaHang ql = quanLy();
        NhanVien nv = nhanVien();
        NhanSuThu ds = new NhanSuThu();
        dung(ds.them(ad), "Nạp Admin vào dữ liệu thử");
        dung(ds.them(ad, ql) && ds.them(ql, nv), "Admin tạo Quản lý, Quản lý tạo nhân viên");
        for (String quyen : ql.getQuyenHan()) dung(ad.coQuyen(quyen), "Admin có mọi quyền của Quản lý: " + quyen);
        dung(ad.coQuyen("QUAN_TRI_TAI_KHOAN") && ad.coQuyen("SA_THAI_NHAN_VIEN"), "Admin có toàn quyền");
        dung(!ql.coQuyen("QUAN_TRI_TAI_KHOAN") && !ql.coQuyen("SA_THAI_NHAN_VIEN"), "Quản lý không có quyền quản trị");
        dung(!nv.coQuyen("DUYET_HOA_DON") && !nv.coQuyen("QUAN_LY_NHAN_VIEN"), "Nhân viên không có quyền quản lý");
        dung(!ds.them(ql, new Admin("AD002", "Admin hai", "admin2", "123456", "0912345678", false)), "Chặn Quản lý tạo Admin");
        dung(!ds.them(ql, new QuanLyCuaHang("QL002", "Quản lý hai", "quanly2", "123456", "0912345678",
                "Quản lý", false, "Kho", 1.5)), "Chặn Quản lý tạo Quản lý khác");
        dung(!ds.them(nv, new NhanVien("NV002", "Nhân viên hai", "nv2", "123456", "0912345678", HOM_NAY, false)), "Chặn nhân viên tạo tài khoản");

        ds.sua(phim("1", "Tên bị đổi", "0"), ql, ad.getMaND());
        dung(ad.getTenND().equals("Admin Thử"), "Quản lý không được sửa Admin");
        ds.sua(phim("3", "0"), ql, nv.getMaND());
        dung(nv.getMatKhau().equals("123456"), "Quản lý không được đặt lại mật khẩu");
        ds.sua(phim("1", "Nhân viên mới", "4", "6000000", "0"), ql, nv.getMaND());
        dung(nv.getTenND().equals("Nhân Viên Mới") && nv.getLuongCoBan() == 6000000, "Quản lý cập nhật hồ sơ và lương nhân viên");
        ds.sua(phim("3", "matkhau-moi", "6", "Kho mới", "7", "2", "8", "quanly-moi", "0"), ad, ql.getMaND());
        dung(ql.getMatKhau().equals("matkhau-moi") && ql.getKhuVucQuanLy().equals("Kho mới")
                && ql.getHeSoTrachNhiem() == 2 && ql.getTaiKhoan().equals("quanly-moi"), "Admin cập nhật tài khoản Quản lý");
        ds.sua(phim("8", ql.getTaiKhoan(), "0"), ad, ad.getMaND());
        dung(ad.getTaiKhoan().equals("admin"), "Chặn đổi sang tên đăng nhập đã tồn tại");

        dung(!ds.xoa(phim("Vi phạm nội quy"), ql, nv.getMaND()) && !nv.isDaXoa()
                && nv.getYeuCauSaThai().equals("Vi phạm nội quy"), "Quản lý chỉ gửi đề xuất sa thải");
        dung(!ds.xuLyYeuCauSaThai(ql, nv.getMaND(), true) && !nv.isDaXoa(), "Quản lý không được tự duyệt đề xuất");
        dung(ds.xuLyYeuCauSaThai(ad, nv.getMaND(), false) && !nv.isDaXoa()
                && nv.getYeuCauSaThai() == null, "Admin từ chối đề xuất");
        ds.xoa(phim("Vi phạm lần hai"), ql, nv.getMaND());
        dung(ds.xuLyYeuCauSaThai(ad, nv.getMaND(), true) && nv.isDaXoa()
                && nv.getYeuCauSaThai() == null, "Admin duyệt sa thải");
        dung(ds.doiTrangThaiTaiKhoan(ad, nv.getMaND(), false) && !nv.isDaXoa(), "Admin khôi phục nhân viên");
        dung(ad.saThaiNhanVien(ds, nv.getMaND()) && nv.isDaXoa(), "Admin được sa thải trực tiếp");
        dung(!ds.doiTrangThaiTaiKhoan(ql, ad.getMaND(), true) && !ad.isDaXoa(), "Quản lý không được khóa Admin");
        dung(!ds.doiTrangThaiTaiKhoan(ad, ad.getMaND(), true), "Bảo vệ tài khoản Admin đang đăng nhập");
        dung(ds.doiTrangThaiTaiKhoan(ad, ql.getMaND(), true) && !ql.coQuyen("DUYET_HOA_DON"), "Admin khóa Quản lý");
        dung(!ds.xoa(phim("Không được phép"), ql, nv.getMaND()), "Quản lý bị khóa không thể thao tác");
        dung(ds.doiTrangThaiTaiKhoan(ad, ql.getMaND(), false) && ql.coQuyen("DUYET_HOA_DON"), "Admin mở khóa Quản lý");
        ad.setDaXoa(true);
        dung(!ad.coQuyen("QUAN_TRI_TAI_KHOAN") && !ad.coQuyen("DUYET_HOA_DON"), "Admin bị khóa mất toàn bộ quyền");
        dung(!ds.doiTrangThaiTaiKhoan(ad, ql.getMaND(), true), "Admin bị khóa không được quản trị tài khoản");
    }

    private static void dinhDangVaTuongThich(Path thuMuc) throws Exception {
        Path tep = thuMuc.resolve("tai-khoan-cu.txt");
        Files.write(tep, List.of(
                "GIAMDOC;GD001;Admin cũ;giamdoc;123456;0912345678;Giám đốc;false;15000000;35.00",
                "GD002;Admin cũ hai;giamdoc2;123456;0912345678;Giám đốc;true;10000000;10.00"), StandardCharsets.UTF_8);
        DSNhanVien ds = new DSNhanVien();
        dung(ds.docFile(tep.toString()), "Đọc được cả hai định dạng tài khoản cũ");
        Nguoidung ad = ds.timKiemTheoMa("GD001");
        dung(ad instanceof Admin && ad.getVaiTro().equals("Admin") && ad.getTaiKhoan().equals("giamdoc")
                && ad.coQuyen("QUAN_TRI_TAI_KHOAN"), "Chuyển vai trò, giữ nguyên mã và tài khoản cũ");
        dung(ds.timKiemTheoMa("GD002").isDaXoa(), "Giữ nguyên trạng thái tài khoản cũ đã bị khóa");
        Path moi = thuMuc.resolve("admin.txt");
        dung(ds.ghiFile(moi.toString()) && Files.readAllLines(moi).stream().allMatch(d -> d.startsWith("ADMIN;")), "Lưu tài khoản cũ theo định dạng ADMIN");
        DSNhanVien docLai = new DSNhanVien();
        dung(docLai.docFile(moi.toString()) && docLai.timKiemTheoMa("GD001") instanceof Admin
                && docLai.timKiemTheoMa("GD002").isDaXoa(), "Đọc lại ADMIN không mất quyền hoặc trạng thái");
        NhanSuThu dsMoi = new NhanSuThu();
        Admin nhap = new Admin();
        nhap.nhapThongTin(phim("/", "Admin mới", "0912345678", "admin-moi", "123456"), dsMoi);
        dung(nhap.getMaND().equals("AD001") && nhap.getVaiTro().equals("Admin"), "Tạo Admin với mã AD, không nhập phụ cấp/cổ phần/khu vực");
        dsMoi.them(nhap);
        dung(dsMoi.sinhMaTuDong("AD").equals("AD002"), "Sinh mã Admin tiếp theo");
    }

    private static void hoaDonVaKhoaQuyen() {
        KhoThu kho = new KhoThu();
        NhaCungCapThu ncc = new NhaCungCapThu();
        ncc.them(new NhaCungCap("NCC001", "Nhà cung cấp thử", "0912345678", "TP HCM", "thu@example.com", false));
        dung(kho.themSP(new ThucAn("TA001", "Bánh thử", 5000, 10000, 10, "gói", HOM_NAY.minusDays(1),
                HOM_NAY.plusDays(120), 0, "Bột", "Nơi khô", "Việt Nam", false, false)), "Tạo sản phẩm thử");
        for (Nguoidung nd : new Nguoidung[]{admin(), quanLy()}) {
            int tonCu = kho.timKiem("TA001").getSoLuongTon();
            Hoadonban ban = new Hoadonban("HDB001", nd, HOM_NAY, "DangXuLy", "", "KHACH_LE");
            dung(ban.themCTHD(new CTHDBan("HDB001", "TA001", "Bánh thử", 1, 10000, 0, 0, 0, "KHONG"),
                    kho, new DSKhuyenMai(), new DSSanPhamKhuyenMai()), "Thêm chi tiết bán thử");
            dung(ban.thanhToanHoaDon(phim("1", "10000"), nd, new DsKhachHang(), kho), nd.getVaiTro() + " thanh toán hóa đơn bán");
            dung(ban.getNguoiLap() == nd && ban.getPhuongThucThanhToan().getMaNV().equals(nd.getMaND()), "Lưu đúng người lập và người thanh toán");
            NhanSuThu dsNV = new NhanSuThu();
            dsNV.them(nd);
            HoaDon docLai = HoaDon.tuDongFile(ban.toFileString(), dsNV);
            dung(docLai.getNguoiLap() == nd && docLai.getNguoiLap().getVaiTro().equals(nd.getVaiTro()), "Khôi phục đúng vai trò người lập hóa đơn");

            HoadonNhap nhap = new HoadonNhap("HDN001", nd, HOM_NAY, "DangXuLy", "", "NCC001");
            dung(nhap.themCTHD(new CTHDonNhap("HDN001", "TA001", "Bánh thử", 2, 5000, 0, HOM_NAY.plusDays(90))), "Thêm chi tiết nhập thử");
            dung(nhap.thanhToanHoaDon(phim("1", "10000"), nd, kho, ncc), nd.getVaiTro() + " thanh toán hóa đơn nhập");
            dung(!nhap.duyetNhapKho(phim("1"), kho, nhanVien()), "Nhân viên không được duyệt nhập");
            dung(nhap.duyetNhapKho(phim("1"), kho, nd) && nhap.isDaXuLyThanhCong(), nd.getVaiTro() + " được duyệt nhập");
            dung(kho.timKiem("TA001").getSoLuongTon() == tonCu + 1, "Trừ kho khi bán, cộng kho khi duyệt nhập đúng một lần");
            nd.setDaXoa(true);
            dung(!kho.xoaSP("TA001", nd) && !kho.suaSP(phim(), "TA001", nd), "Tài khoản bị khóa không được sửa/xóa sản phẩm");
            dung(!ncc.xoa(nd, "NCC001") && !ncc.sua(phim(), nd, "NCC001"), "Tài khoản bị khóa không được sửa/xóa NCC");
            DSHoadon hd = new DSHoadon();
            dung(!hd.suaHD(phim(), nd) && !hd.xoaHD(phim(), nd), "Tài khoản bị khóa không được sửa/hủy hóa đơn");
            dung(!hd.duyetHoaDon(phim(), nd, kho, ncc), "Tài khoản bị khóa không được duyệt hóa đơn");
        }
        Admin ad = admin();
        ad.thongKeTonKho(kho);
        ad.thongKeDoanhThuLoiNhuan(new DSHoadon(), HOM_NAY, HOM_NAY);
        dung(ad instanceof person.Thongke, "Admin tự thực hiện báo cáo, không cần tài khoản Quản lý khác");
        dung(new TienMat("HDB002", 10000, ad).getMaNV().equals("AD001"), "Thanh toán nhận trực tiếp tài khoản Admin");
    }

    private static void chanMenuChuaDangNhap() {
        Giaodien gd = new Giaodien();
        // Scanner rỗng: menu phải từ chối trước khi đọc lựa chọn, không ném lỗi hết đầu vào.
        gd.menuAdmin(phim());
        gd.menuQuanLyNhanSuAdmin(phim());
        gd.menuQuanLyKhuyenMai(phim());
        gd.menuQuanLyNhanVien(phim());
        gd.menuQuanLySanPham(phim());
        gd.menuThongKeBaoCao(phim());
        gd.taoHoaDonBan(phim());
        gd.taoHoaDonNhap(phim());
        dung(true, "Chặn gọi trực tiếp menu quản trị/nghiệp vụ khi chưa đăng nhập");
    }

    public static void main(String[] args) throws Exception {
        Path thuMuc = Files.createTempDirectory("oop-phan-quyen-admin-");
        PrintStream manHinh = System.out;
        ByteArrayOutputStream nhatKy = new ByteArrayOutputStream();
        try (PrintStream tam = new PrintStream(nhatKy, true, StandardCharsets.UTF_8)) {
            System.setOut(tam);
            quyenVaNhanSu();
            dinhDangVaTuongThich(thuMuc);
            hoaDonVaKhoaQuyen();
            chanMenuChuaDangNhap();
        } catch (Throwable e) {
            manHinh.println(nhatKy.toString(StandardCharsets.UTF_8));
            throw e;
        } finally {
            System.setOut(manHinh);
            thuMuc.toFile().deleteOnExit();
            try (var tep = Files.list(thuMuc)) { tep.forEach(p -> p.toFile().deleteOnExit()); }
        }
        System.out.println("PASS: " + soKiemTra + " kiểm tra phân quyền Admin/Quản lý, chuyển đổi tài khoản và hóa đơn.");
    }
}
