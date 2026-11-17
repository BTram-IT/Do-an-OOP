import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import doitac.DSNhaCungCap;
import doitac.NhaCungCap;
import hoadon.CTHDBan;
import hoadon.CTHDonNhap;
import hoadon.DSHoadon;
import hoadon.HoadonNhap;
import hoadon.Hoadonban;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import person.DsKhachHang;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.DoUong;
import sanpham.SanPham;
import sanpham.ThucAn;
import util.Ktradulieu;

/** Kiểm tra danh mục tồn 0, nhập/bán thực tế và tương thích file cũ bằng dữ liệu riêng. */
public class QuanLySanPhamTonKhoTest {
    private static final LocalDate HOM_NAY = LocalDate.now();
    private static int soKiemTra;
    private static final ByteArrayOutputStream NHAT_KY = new ByteArrayOutputStream();

    private static class KhoThu extends DSSanPham {
        @Override public void ghiFile(String duongDan) { }
    }

    private static class NccThu extends DSNhaCungCap {
        @Override public void ghiFile(String duongDan) { }
    }

    private static class HoaDonThu extends DSHoadon {
        @Override public void ghiFile(String duongDan) { }
    }

    private static Scanner phim(String... dong) {
        return new Scanner(String.join("\n", dong) + "\n");
    }

    private static void dung(boolean dieuKien, String thongBao) {
        soKiemTra++;
        if (!dieuKien) throw new AssertionError(thongBao);
    }

    private static ThucAn thucAn(String ma, int ton, LocalDate hsd) {
        return new ThucAn(ma, "Bánh thử", 5000, 10000, ton, "gói", HOM_NAY.minusDays(10), hsd,
                0, "Bột", "Nơi khô", "Việt Nam", false, false);
    }

    private static void danhMucVaNhapBan() {
        KhoThu kho = new KhoThu();
        String nsx = Ktradulieu.dinhDangNgay(HOM_NAY);
        String hsd = Ktradulieu.dinhDangNgay(HOM_NAY.plusDays(30));
        NccThu nguonNCC = new NccThu();
        nguonNCC.them(new NhaCungCap("NCC001", "Nhà cung cấp thử", "0912345678", "TP HCM", "thu@example.com", false));
        // Không có bước nhập số lượng khi tạo danh mục thức ăn/đồ uống.
        kho.themSanPhamTuBanPhim(phim("1", "/", "Bánh mới", "5000", "10000", "gói", "0", nsx, hsd,
                "Việt Nam", "Nơi khô", "Bột", "n", "1"), nguonNCC);
        kho.themSanPhamTuBanPhim(phim("2", "/", "Trà mới", "5000", "10000", "chai", "0", nsx, hsd,
                "Nhãn thử", "Trà", "Đào", "330", "1"), nguonNCC);
        dung(kho.getSoLuong() == 2 && kho.timKiem("TA001") instanceof ThucAn
                && kho.timKiem("DU001") instanceof DoUong, "Tạo đủ hai loại sản phẩm trong danh mục");
        SanPham sp = kho.timKiem("TA001");
        dung(sp.getSoLuongTon() == 0 && kho.timKiem("DU001").getSoLuongTon() == 0, "Mặt hàng mới luôn có tồn kho 0");

        NhanVien nv = new NhanVien("NV001", "Nhân viên thử", "nv", "123456", "0912345678", HOM_NAY, false);
        DSKhuyenMai km = new DSKhuyenMai();
        DSSanPhamKhuyenMai lienKet = new DSSanPhamKhuyenMai();
        Hoadonban ban = new Hoadonban("HDB001", nv, HOM_NAY, "DangXuLy", "", "KHACH_LE");
        CTHDBan ctBan = new CTHDBan("HDB001", "TA001", sp.getTenSP(), 1, 10000, 0, 0, 0, "KHONG");
        dung(!ban.themCTHD(ctBan, kho, km, lienKet), "Không bán được mặt hàng chỉ có danh mục, chưa nhập kho");
        dung(lienKet.tuDongGiamGiaCanHSD(kho, km, HOM_NAY) == 0, "Không giảm giá tự động cho mặt hàng chưa có tồn");

        CTHDonNhap ct = new CTHDonNhap();
        dung(!ct.nhapThongTin(phim("TA999", "0"), "HDN001", kho, nv) && kho.getSoLuong() == 2,
                "Nhân viên chọn mã chưa có phải yêu cầu tạo danh mục trước");
        dung(ct.nhapThongTin(phim("TA001", "3", ""), "HDN001", kho, nv), "Nhân viên nhập lô cho mặt hàng tồn 0");
        dung(ct.getSanPhamMoi() == null && ct.toDongFile().split(";", -1).length == 7,
                "Phiếu mới chỉ tham chiếu mã, không kèm hồ sơ sản phẩm hoặc bước duyệt riêng");
        HoadonNhap nhap = new HoadonNhap("HDN001", nv, HOM_NAY, "DangXuLy", "", "NCC001");
        dung(nhap.themCTHD(ct) && sp.getSoLuongTon() == 0, "Tạo chi tiết nhập chưa làm tăng kho");
        NccThu ncc = new NccThu();
        ncc.them(new NhaCungCap("NCC001", "Nhà cung cấp thử", "0912345678", "TP HCM", "thu@example.com", false));
        dung(nhap.thanhToanHoaDon(phim("1", "15000"), nv, kho, ncc) && sp.getSoLuongTon() == 0,
                "Thanh toán phiếu nhập vẫn giữ tồn 0 đến khi duyệt");
        HoaDonThu ds = new HoaDonThu();
        dung(ds.themHD(nhap), "Lưu phiếu nhập chờ duyệt");
        dung(!ds.duyetHoaDon(phim(), nv, kho) && sp.getSoLuongTon() == 0, "Nhân viên không được tự duyệt phiếu");
        dung(!ds.duyetHoaDon(phim("1", "0"), new QuanLyCuaHang(), kho) && sp.getSoLuongTon() == 0,
                "Hủy duyệt phiếu không làm tăng kho");
        // Chỉ hai lựa chọn: chọn phiếu và xác nhận; không hỏi duyệt sản phẩm.
        dung(ds.duyetHoaDon(phim("1", "1"), new QuanLyCuaHang(), kho) && sp.getSoLuongTon() == 3,
                "Một lần duyệt phiếu cộng đúng số lượng thực nhập");
        dung(!nhap.duyetNhapKho(phim(), kho, new QuanLyCuaHang()) && sp.getSoLuongTon() == 3, "Không cộng kho lần hai");
        dung(ban.themCTHD(ctBan, kho, km, lienKet), "Có tồn kho là bán được, không cần trạng thái duyệt sản phẩm");
        dung(ban.thanhToanHoaDon(phim("1", "10000"), nv, new DsKhachHang(), kho) && sp.getSoLuongTon() == 2,
                "Bán thành công trừ đúng hàng thực xuất");
    }

    private static void hetHanVaKiemTraTruocKhiCongKho() {
        KhoThu kho = new KhoThu();
        ThucAn hetHan = thucAn("TA001", 7, HOM_NAY.minusDays(1));
        kho.themSP(hetHan);
        dung(!hetHan.kiemTraHanSuDung() && hetHan.getSoLuongTon() == 0, "Hàng hết hạn bị loại khỏi tồn, không cần bộ đếm riêng");
        dung(!hetHan.kiemTraHanSuDung() && hetHan.getSoLuongTon() == 0, "Kiểm tra hết hạn nhiều lần không trừ lại");
        hetHan.setSoLuongTon(7);
        NhanVien nv = new NhanVien("NV001", "Nhân viên thử", "nv", "123456", "0912345678", HOM_NAY, false);
        HoadonNhap moi = new HoadonNhap("HDN002", nv, HOM_NAY, "DangXuLy", "", "NCC001");
        moi.themCTHD(new CTHDonNhap("HDN002", "TA001", "Bánh thử", 4, 5000, 0, HOM_NAY.plusDays(30)));
        dung(moi.duyetNhapKho(phim(), kho, new QuanLyCuaHang()) && hetHan.getSoLuongTon() == 4
                && hetHan.getNsx().equals(HOM_NAY) && hetHan.getHsd().equals(HOM_NAY.plusDays(9)), "Nhập bổ sung loại tồn hết hạn, tự cập nhật NSX/HSD theo thời hạn 9 ngày");

        ThucAn ganDay = thucAn("TA002", Integer.MAX_VALUE, HOM_NAY.plusDays(30));
        kho.themSP(ganDay);
        HoadonNhap loi = new HoadonNhap("HDN003", nv, HOM_NAY, "DangXuLy", "", "NCC001");
        loi.themCTHD(new CTHDonNhap("HDN003", "TA001", "Bánh thử", 1, 6000, 0, HOM_NAY.plusDays(20)));
        loi.themCTHD(new CTHDonNhap("HDN003", "TA002", "Bánh thử", 1, 5000, 0, HOM_NAY.plusDays(20)));
        dung(!loi.duyetNhapKho(phim(), kho, new QuanLyCuaHang()) && hetHan.getSoLuongTon() == 4
                && hetHan.getDonGiaNhap() == 5000 && ganDay.getSoLuongTon() == Integer.MAX_VALUE,
                "Một dòng tràn tồn phải chặn cả phiếu trước khi sửa kho hoặc giá");
        HoadonNhap thieu = new HoadonNhap("HDN004", nv, HOM_NAY, "DangXuLy", "", "NCC001");
        thieu.themCTHD(new CTHDonNhap("HDN004", "TA999", "Chưa có", 1, 5000, 0, HOM_NAY.plusDays(20)));
        dung(!thieu.duyetNhapKho(phim(), kho, new QuanLyCuaHang()) && kho.getSoLuong() == 2,
                "Không duyệt phiếu tham chiếu mặt hàng chưa có danh mục");
    }

    private static void docGhiFile(Path thuMuc) throws Exception {
        ThucAn ta = thucAn("TA001", 6, HOM_NAY.plusDays(30));
        DoUong du = new DoUong("DU001", "Trà thử", 5000, 10000, 8, "chai", HOM_NAY, HOM_NAY.plusDays(60),
                5, 330, "Nhãn thử", "Đào", "Trà", true);
        String[] dongMoi = {ta.toFileString(), du.toFileString()};
        for (String dong : dongMoi) {
            dung(dong.split(";", -1).length == 16, "File sản phẩm mới có 16 cột gồm mã NCC");
            SanPham sp = SanPham.tuDongFile(dong);
            String[] cot = dong.split(";", -1);
            String cu = String.join(";", Arrays.copyOf(cot, 14)) + ";Chờ duyệt;" + cot[cot.length - 1];
            SanPham spCu = SanPham.tuDongFile(cu);
            dung(sp.getMaSP().equals(spCu.getMaSP()) && sp.getSoLuongTon() == spCu.getSoLuongTon()
                    && sp.isDaXoa() == spCu.isDaXoa(), "Đọc file cũ giữ nguyên tồn và cờ xóa, bỏ cột duyệt");
        }
        Path tep = thuMuc.resolve("sanpham.txt");
        Files.write(tep, List.of(dongMoi), StandardCharsets.UTF_8);
        DSSanPham doc = new DSSanPham();
        doc.docFile(tep.toString());
        dung(doc.getSoLuong() == 2 && doc.timKiem("TA001").getSoLuongTon() == 6
                && doc.timKiem("DU001").isDaXoa(), "Nạp lại đầy đủ hai loại, tồn kho và cờ xóa");
        Path ghi = thuMuc.resolve("sanpham-ghi.txt");
        doc.ghiFile(ghi.toString());
        dung(Files.readAllLines(ghi).equals(List.of(dongMoi)), "Đọc/lưu không làm thay đổi thông tin sản phẩm");
        String hoSoCu = String.join(";", Arrays.copyOf(dongMoi[0].split(";", -1), 14)) + ";Chờ duyệt;false";
        String ctCu = "HDN005;TA001;Bánh thử;3;5000;0;" + Ktradulieu.dinhDangNgay(HOM_NAY.plusDays(30)) + ";SPMOI;" + hoSoCu;
        CTHDonNhap ct = CTHDonNhap.tuDongFile(ctCu);
        dung(ct != null && ct.getSanPhamMoi().getMaSP().equals("TA001"), "Giữ khả năng đọc hồ sơ kèm hóa đơn cũ");
        HoadonNhap nhap = new HoadonNhap("HDN005", new NhanVien(), HOM_NAY, "DangXuLy", "", "NCC001");
        nhap.themCTHD(ct);
        KhoThu kho = new KhoThu();
        dung(nhap.duyetNhapKho(phim(), kho, new QuanLyCuaHang()) && kho.timKiem("TA001").getSoLuongTon() == 3,
                "Hồ sơ cũ vào danh mục tồn 0 rồi cộng đúng lượng nhập, không dùng tồn có sẵn trong hồ sơ");
        dung(CTHDonNhap.tuDongFile(ct.toDongFile()) != null, "Đọc lại hồ sơ cũ sau khi ghi bằng định dạng sản phẩm mới");
    }

    public static void main(String[] args) throws Exception {
        Path thuMuc = Files.createTempDirectory("oop-san-pham-ton-kho-");
        PrintStream manHinh = System.out;
        try (PrintStream tam = new PrintStream(NHAT_KY, true, StandardCharsets.UTF_8)) {
            System.setOut(tam);
            danhMucVaNhapBan();
            hetHanVaKiemTraTruocKhiCongKho();
            docGhiFile(thuMuc);
        } catch (Throwable e) {
            manHinh.println(NHAT_KY.toString(StandardCharsets.UTF_8));
            throw e;
        } finally {
            System.setOut(manHinh);
            thuMuc.toFile().deleteOnExit();
            try (var tep = Files.list(thuMuc)) { tep.forEach(p -> p.toFile().deleteOnExit()); }
        }
        System.out.println("PASS: " + soKiemTra + " kiểm tra danh mục sản phẩm, nhập/bán, hết hạn và file cũ/mới.");
    }
}
