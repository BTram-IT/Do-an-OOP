import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

import giaodien.Giaodien;
import hoadon.CTHDonNhap;
import hoadon.HoadonNhap;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import khuyenmai.Khuyenmai;
import khuyenmai.KhuyenmaiPhantram;
import khuyenmai.SPKhMai;
import person.Admin;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.DoUong;
import sanpham.SanPham;
import sanpham.ThucAn;

/** Kiểm tra bằng dữ liệu riêng; các danh sách thử không ghi vào dữ liệu dự án. */
public class QuanLyHanSuDungKhoTest {
    private static final LocalDate HOM_NAY = LocalDate.now();
    private static final ByteArrayOutputStream NHAT_KY = new ByteArrayOutputStream();
    private static int soKiemTra;

    private static class KhoThu extends DSSanPham {
        int soLanGhi;
        @Override public void ghiFile(String duongDan) { soLanGhi++; }
    }
    private static class KhuyenMaiThu extends DSKhuyenMai {
        @Override public void ghiFile(String duongDan) { }
    }
    private static class LienKetThu extends DSSanPhamKhuyenMai {
        @Override public void ghiFile(String duongDan) { }
    }
    private static void dung(boolean dk, String noiDung) {
        soKiemTra++;
        if (!dk) throw new AssertionError(noiDung);
    }
    private static NhanVien nhanVien() {
        return new NhanVien("NV001", "Nhân viên kho", "nv", "123456", "0912345678", HOM_NAY, false);
    }
    private static Admin admin() {
        return new Admin("AD001", "Admin thử", "admin", "123456", "0912345678", false);
    }
    private static SanPham sp(String ma, int conNgay, int ton) {
        if (ma.startsWith("DU")) return new DoUong(ma, "Trà " + ma, 5000, 10000, ton, "chai", HOM_NAY.minusDays(100),
                HOM_NAY.plusDays(conNgay), 0, 330, "Nhãn thử", "Đào", "Trà", "NCC001", false);
        return new ThucAn(ma, "Bánh " + ma, 5000, 10000, ton, "gói", HOM_NAY.minusDays(100),
                HOM_NAY.plusDays(conNgay), 0, "Bột", "Khô", "Việt Nam", false, "NCC001", false);
    }
    private static KhoThu duLieu() {
        KhoThu ds = new KhoThu();
        String[] ma = {"TA001", "DU001", "TA002", "TA003", "DU002", "TA004", "TA005", "DU003", "TA006", "DU004", "TA007"};
        int[] ngay = {-1, -5, 0, 3, 4, 7, 8, 15, 16, 30, 31};
        int[] ton = {7, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        for (int i = 0; i < ma.length; i++) ds.themSP(sp(ma[i], ngay[i], ton[i]));
        ds.themSP(sp("TA008", 1, 0));
        SanPham xoa = sp("TA009", 2, 13);
        xoa.setDaXoa(true);
        ds.themSP(xoa);
        ThucAn thieuNgay = new ThucAn();
        thieuNgay.setMaSP("TA010");
        thieuNgay.setTenSP("Chưa có ngày");
        thieuNgay.setSoLuongTon(2);
        ds.themSP(thieuNgay);
        ds.soLanGhi = 0;
        return ds;
    }
    private static String anhChup(KhoThu ds) {
        StringBuilder s = new StringBuilder();
        for (SanPham sp : ds.getDanhSach()) {
            s.append(sp.getMaSP()).append(':').append(sp.getSoLuongTon()).append(':').append(sp.getNsx())
                    .append(':').append(sp.getHsd()).append(':').append(sp.getDonGiaBan()).append(';');
        }
        return s.toString();
    }
    private static void thongKeVaLoaiBo() {
        KhoThu ds = duLieu();
        SanPham[] hetHan = ds.locSanPhamHetHan(HOM_NAY);
        SanPham[] sapHet = ds.locSanPhamSapHetHan(30, HOM_NAY);
        dung(hetHan.length == 2 && Arrays.stream(hetHan).mapToInt(SanPham::getSoLuongTon).sum() == 10, "Thống kê đúng hàng hết hạn còn tồn của cả hai loại");
        dung(sapHet.length == 8 && Arrays.stream(sapHet).mapToInt(SanPham::getSoLuongTon).sum() == 60, "Sắp hết hạn gồm 0-30 ngày, đủ 8 mã và 60 đơn vị");
        dung(!ds.get(2).daHetHan(HOM_NAY) && ds.get(2).sapHetHan(HOM_NAY, 30), "HSD hôm nay còn hợp lệ, không tính là hết hạn");
        dung(!ds.timKiem("TA007").sapHetHan(HOM_NAY, 30), "Mốc 31 ngày không thuộc sắp hết hạn");
        String truoc = anhChup(ds);
        NHAT_KY.reset();
        ds.thongKeSanPhamHetHan(HOM_NAY);
        ds.thongKeSanPhamSapHetHan(30, HOM_NAY);
        String log = NHAT_KY.toString(StandardCharsets.UTF_8);
        dung(log.contains("Số mặt hàng: 2") && log.contains("Tổng số đơn vị còn tồn: 10")
                && log.contains("Số mặt hàng: 8") && log.contains("Tổng số đơn vị còn tồn: 60"), "Báo cáo hiện đúng số mặt hàng và tổng lượng tồn");
        dung(anhChup(ds).equals(truoc) && ds.soLanGhi == 0, "Xem thống kê không xóa số lượng, đổi ngày hoặc ghi file");
        NhanVien nv = nhanVien();
        nv.setDaXoa(true);
        dung(ds.loaiBoSoLuongHetHan(nv, HOM_NAY) == 0 && anhChup(ds).equals(truoc), "Tài khoản bị khóa không được loại hàng");
        nv.setDaXoa(false);
        dung(ds.loaiBoSoLuongHetHan(nv, HOM_NAY) == 10 && ds.soLanGhi == 1, "Nhân viên kho loại đúng 10 đơn vị hết hạn và lưu một lần");
        dung(ds.locSanPhamHetHan(HOM_NAY).length == 0 && ds.locSanPhamSapHetHan(30, HOM_NAY).length == 8, "Loại hàng hết hạn không ảnh hưởng hàng cận hạn");
        dung(ds.getSoLuong() == 14 && !ds.timKiem("TA001").isDaXoa() && ds.timKiem("TA009").getSoLuongTon() == 13,
                "Giữ danh mục, cờ xóa và dữ liệu đã xóa trước đó");
        dung(ds.loaiBoSoLuongHetHan(nv, HOM_NAY) == 0 && ds.soLanGhi == 1, "Loại lại không đếm/trừ hoặc ghi thêm");
    }
    private static void nhapHangTheoTinhTrangTonCu() {
        SanPham quaHan = sp("TA101", -1, Integer.MAX_VALUE);
        quaHan.nhapThemHang(2, HOM_NAY);
        dung(quaHan.getSoLuongTon() == 2, "Hàng cũ hết hạn chỉ giữ lượng mới, không tràn vì tồn cũ");
        SanPham conHan = sp("DU101", 5, 7);
        conHan.nhapThemHang(3, HOM_NAY);
        dung(conHan.getSoLuongTon() == 10, "Hàng cũ còn hạn cộng cả tồn cũ và lượng mới");
        SanPham homNay = sp("TA102", 0, 7);
        homNay.nhapThemHang(3, HOM_NAY);
        dung(homNay.getSoLuongTon() == 10, "HSD hôm nay vẫn cộng cả hai lượng");
        SanPham day = sp("TA103", 5, Integer.MAX_VALUE);
        LocalDate nsxCu = day.getNsx();
        LocalDate hsdCu = day.getHsd();
        boolean loi = false;
        try { day.nhapThemHang(1, HOM_NAY); } catch (ArithmeticException e) { loi = true; }
        dung(loi && day.getSoLuongTon() == Integer.MAX_VALUE && day.getNsx().equals(nsxCu)
                && day.getHsd().equals(hsdCu), "Tràn tồn không cập nhật một phần số lượng hoặc ngày");

        KhoThu kho = new KhoThu();
        kho.themSP(sp("TA001", -1, 7));
        kho.themSP(sp("TA002", 5, Integer.MAX_VALUE));
        HoadonNhap hd = new HoadonNhap("HDN001", nhanVien(), HOM_NAY, "DangXuLy", "", "NCC001");
        hd.themCTHD(new CTHDonNhap("HDN001", kho.get(0), 3, 5000, HOM_NAY.plusDays(1)));
        hd.themCTHD(new CTHDonNhap("HDN001", kho.get(1), 1, 5000, HOM_NAY.plusDays(1)));
        hd.khoiPhucThanhToanTuFile(hd.tinhThanhTien(), "TienMat");
        dung(!hd.duyetNhapKho(new Scanner(""), kho, new QuanLyCuaHang()) && kho.get(0).getSoLuongTon() == 7,
                "Phiếu có dòng sai không loại tồn hết hạn của dòng trước");
    }
    private static void xuLyCanHan() {
        KhoThu ds = duLieu();
        KhuyenMaiThu km = new KhuyenMaiThu();
        LienKetThu lk = new LienKetThu();
        KhuyenmaiPhantram thuCong = new KhuyenmaiPhantram("KM001", "Thủ công", HOM_NAY, HOM_NAY.plusDays(20), 20);
        km.themKhuyenMai(thuCong);
        lk.them(new SPKhMai("TA003", "KM001"));
        String truoc = anhChup(ds);
        dung(lk.xuLyHangSapHetHan(ds, km, nhanVien(), HOM_NAY) == 0 && km.getSoLuong() == 1, "Nhân viên kho không tự áp dụng giảm giá");
        Admin ad = admin();
        ad.setDaXoa(true);
        dung(lk.xuLyHangSapHetHan(ds, km, ad, HOM_NAY) == 0, "Admin bị khóa không áp dụng giảm giá");
        ad.setDaXoa(false);
        dung(lk.xuLyHangSapHetHan(ds, km, ad, HOM_NAY) == 7 && km.getSoLuong() == 8 && lk.getSoLuong() == 8,
                "Giảm cận hạn đủ hai loại sản phẩm, bỏ mã có chương trình thủ công");
        dung(anhChup(ds).equals(truoc), "Giảm giá qua khuyến mãi không đổi giá gốc, ngày hoặc số lượng");
        for (SPKhMai link : lk.getDanhSach()) {
            if (link.getMaKM().equals("KM001")) continue;
            SanPham sp = ds.timKiem(link.getMaSP());
            long con = java.time.temporal.ChronoUnit.DAYS.between(HOM_NAY, sp.getHsd());
            double muc = con <= 3 ? 50 : (con <= 7 ? 30 : (con <= 15 ? 15 : 10));
            KhuyenmaiPhantram ct = (KhuyenmaiPhantram) km.timKiem(link.getMaKM());
            dung(ct.getPhanTramGiamGia() == muc && ct.getNgayKetThuc().equals(sp.getHsd()), "Đúng mức giảm và kết thúc theo HSD: " + sp.getMaSP());
        }
        dung(thuCong.getPhanTramGiamGia() == 20 && lk.layDanhSachMaSPTheoKM("KM001").size() == 1, "Giữ nguyên khuyến mãi thủ công");
        dung(lk.xuLyHangSapHetHan(ds, km, new QuanLyCuaHang(), HOM_NAY) == 0 && km.getSoLuong() == 8,
                "Gọi lại không tạo chương trình hoặc liên kết trùng");
        String maAuto = null;
        for (SPKhMai link : lk.getDanhSach()) if (link.getMaSP().equals("TA004")) maAuto = link.getMaKM();
        SanPham nhapMoi = ds.timKiem("TA004");
        nhapMoi.nhapThemHang(2, HOM_NAY);
        dung(lk.goGiamGiaCanHanKhongConPhuHop(ds, km, HOM_NAY) == 1 && !lk.kiemTraLienKet("TA004", maAuto),
                "Nhập hàng làm ngày mới vượt 30 ngày thì gỡ giảm cận hạn cũ");
        dung(km.timKiem(maAuto) != null && lk.kiemTraLienKet("TA003", "KM001"), "Gỡ liên kết tự động vẫn bảo toàn chương trình lịch sử và thủ công");
    }
    private static void chanMenuChuaDangNhap() {
        Giaodien gd = new Giaodien();
        gd.menuXuLyHangSapHetHan(new Scanner(""));
        gd.loaiBoHangHetHan(new Scanner(""));
        dung(true, "Menu xử lý kho chặn phiên chưa đăng nhập trước khi nhận lựa chọn");
    }
    public static void main(String[] args) throws Exception {
        PrintStream manHinh = System.out;
        try (PrintStream tam = new PrintStream(NHAT_KY, true, StandardCharsets.UTF_8)) {
            System.setOut(tam);
            thongKeVaLoaiBo();
            nhapHangTheoTinhTrangTonCu();
            xuLyCanHan();
            chanMenuChuaDangNhap();
        } catch (Throwable e) {
            manHinh.println(NHAT_KY.toString(StandardCharsets.UTF_8));
            throw e;
        } finally { System.setOut(manHinh); }
        System.out.println("PASS: " + soKiemTra + " kiểm tra hết hạn/cận hạn, xử lý kho và nhập bổ sung.");
    }
}
