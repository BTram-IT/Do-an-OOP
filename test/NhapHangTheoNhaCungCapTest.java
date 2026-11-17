import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import doitac.DSNhaCungCap;
import doitac.NhaCungCap;
import hoadon.CTHDonNhap;
import hoadon.DSHoadon;
import hoadon.HoadonNhap;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.SanPham;
import sanpham.ThucAn;
import util.Ktradulieu;

public class NhapHangTheoNhaCungCapTest {
    private static int soKiemTra;
    private static final LocalDate HOM_NAY = LocalDate.now();
    private static final String HSD = Ktradulieu.dinhDangNgay(HOM_NAY.plusDays(90));
    private static final ByteArrayOutputStream NHAT_KY = new ByteArrayOutputStream();

    private static class KhoThu extends DSSanPham {
        @Override public void ghiFile(String duongDan) { }
    }
    private static class NccThu extends DSNhaCungCap {
        @Override public void ghiFile(String duongDan) { }
    }
    private static class PhieuThu extends DSHoadon {
        int soLanLuu;
        @Override public void ghiFile(String duongDan) { soLanLuu++; }
    }
    private static class DuLieu {
        final KhoThu kho = new KhoThu();
        final NccThu ncc = new NccThu();
        final PhieuThu phieu = new PhieuThu();
        final NhanVien nv = new NhanVien("NV001", "Nhân viên thử", "nv", "123456", "0912345678", HOM_NAY, false);
        DuLieu() {
            ncc.them(new NhaCungCap("NCC001", "NCC một", "0912345678", "TP HCM", "mot@example.com", false));
            ncc.them(new NhaCungCap("NCC009", "NCC đã nghỉ", "0912345678", "TP HCM", "cu@example.com", true));
            ncc.them(new NhaCungCap("NCC002", "NCC hai", "0912345678", "TP HCM", "hai@example.com", false));
            kho.themSP(sp("TA001", "NCC001", 0));
            kho.themSP(sp("TA002", "NCC001", 20));
            kho.themSP(sp("TA003", "NCC002", 20));
        }
    }

    private static ThucAn sp(String ma, String ncc, int ton) {
        return new ThucAn(ma, "Bánh " + ma, 5000, 10000, ton, "gói", HOM_NAY, HOM_NAY.plusDays(90),
                0, "Bột", "Khô", "Việt Nam", false, ncc, false);
    }
    private static Scanner phim(String... dong) { return new Scanner(String.join("\n", dong) + "\n"); }
    private static void dung(boolean dieuKien, String thongBao) {
        soKiemTra++;
        if (!dieuKien) throw new AssertionError(thongBao);
    }
    private static int dem(String mau) { return NHAT_KY.toString(StandardCharsets.UTF_8).split(java.util.regex.Pattern.quote(mau), -1).length - 1; }
    private static void chonVaLuuNCC() {
        DuLieu d = new DuLieu();
        dung(d.ncc.chonNhaCungCap(phim("2")).getMaNCC().equals("NCC002"), "Chọn NCC theo STT, bỏ NCC đã ngừng hợp tác");
        dung(d.ncc.timKiemTheoMa("ncc001") != null && d.ncc.timKiemTheoMa("NCC một") == null, "Tra cứu mã NCC chính xác");
        String dong = d.kho.get(0).toFileString();
        SanPham doc = SanPham.tuDongFile(dong);
        dung(dong.split(";", -1).length == 16 && doc.getMaNCC().equals("NCC001"), "Đọc/lưu mã NCC trong sản phẩm");
        dung(doc.layTieuDeBang()[3].equals("MÃ NCC") && doc.layDongBang()[3].equals("NCC001"), "Hiển thị mã NCC trong bảng");
        boolean loi = false;
        try { doc.setMaNCC("ABC001"); } catch (IllegalArgumentException e) { loi = true; }
        dung(loi && doc.getMaNCC().equals("NCC001"), "Chặn mã sai định dạng, giữ dữ liệu cũ");
        ThucAn cu = new ThucAn("TA099", "Hàng cũ", 5000, 10000, 3, "gói", HOM_NAY, HOM_NAY.plusDays(90),
                0, "Bột", "Khô", "Việt Nam", false, false);
        d.kho.themSP(cu);
        dung(d.kho.boSungNhaCungCap(phim("2"), cu, d.ncc) && cu.getMaNCC().equals("NCC002")
                && cu.getSoLuongTon() == 3, "Bổ sung NCC cho dữ liệu cũ, không thay đổi tồn");
        d.kho.suaSP(phim("6", "1"), "TA099", new QuanLyCuaHang(), d.ncc);
        dung(cu.getMaNCC().equals("NCC001"), "Quản lý đổi NCC qua danh sách đang hợp tác");
        dung(d.kho.themSanPhamTuBanPhim(phim(), new NccThu()) == null, "Không có NCC phải dừng trước khi nhập sản phẩm");
    }

    private static void gomPhieuVaChanNhapSai() {
        DuLieu d = new DuLieu();
        HoadonNhap tam = new HoadonNhap("HDN_TAM", d.nv, HOM_NAY, "DangXuLy", "", "NCC_TAM");
        for (SanPham sp : d.kho.getDanhSach()) {
            tam.themCTHD(new CTHDonNhap("HDN_TAM", sp, 2, 5000, HOM_NAY.plusDays(90)));
        }
        ArrayList<HoadonNhap> cacPhieu = d.phieu.tachPhieuTheoNhaCungCap(tam, d.nv, d.kho, d.ncc);
        dung(cacPhieu.size() == 2 && cacPhieu.get(0).getSoLuongCT() == 2 && cacPhieu.get(1).getSoLuongCT() == 1,
                "Ba mặt hàng, hai NCC thành hai phiếu đúng nhóm");
        dung(!cacPhieu.get(0).getMaHD().equals(cacPhieu.get(1).getMaHD()), "Các phiếu mới có mã riêng");
        for (HoadonNhap hd : cacPhieu) {
            for (CTHDonNhap ct : hd.getDsChiTiet()) {
                dung(ct.getMaHD().equals(hd.getMaHD()) && d.kho.timKiem(ct.getMaSP()).getMaNCC().equals(hd.getMaNCC()),
                        "Chi tiết thuộc đúng phiếu và đúng NCC");
            }
        }
        HoadonNhap sai = new HoadonNhap("HDN099", d.nv, HOM_NAY, "DangXuLy", "", "NCC001");
        sai.themCTHD(new CTHDonNhap("HDN099", d.kho.timKiem("TA003"), 1, 5000, HOM_NAY.plusDays(90)));
        dung(!sai.thanhToanHoaDon(phim(), d.nv, d.kho, d.ncc) && !sai.isDaThanhToan(), "Chặn chi tiền cho mặt hàng khác NCC");
        dung(!sai.duyetNhapKho(phim(), d.kho, new QuanLyCuaHang()) && d.kho.timKiem("TA003").getSoLuongTon() == 20,
                "Chặn cộng kho nếu phiếu sai NCC");
        CTHDonNhap ct = new CTHDonNhap();
        dung(!ct.nhapThongTin(phim("TA003", "0"), "HDN099", d.kho, d.nv, "NCC001"), "Chọn dòng nhập cũng chặn khác NCC");
        ThucAn hoSo = sp("TA100", "NCC002", 0);
        CTHDonNhap cu = new CTHDonNhap("HDN100", hoSo, 2, 5000, HOM_NAY.plusDays(90));
        cu.setSanPhamMoi(hoSo);
        dung(new CTHDonNhap(cu).getSanPhamMoi().getMaNCC().equals("NCC002")
                && CTHDonNhap.tuDongFile(cu.toDongFile()).getSanPhamMoi().getMaNCC().equals("NCC002"), "Sao chép/lưu hồ sơ cũ giữ mã NCC");
    }

    private static void nhapNhieuNCC(boolean huyPhieuHai) {
        DuLieu d = new DuLieu();
        String[] dau = {"5", "1", "TA001", "2", "", "1", "TA003", "3", "",
                "1", "TA002", "1", "", "0", "0", "", "6", "1", "15000", ""};
        String[] cuoi = huyPhieuHai ? new String[]{"0"} : new String[]{"6", "1", "15000"};
        String[] keys = java.util.Arrays.copyOf(dau, dau.length + cuoi.length);
        System.arraycopy(cuoi, 0, keys, dau.length, cuoi.length);
        NHAT_KY.reset();
        int so = d.phieu.nhapThemHang(phim(keys), d.nv, d.ncc, d.kho);
        dung(so == (huyPhieuHai ? 1 : 2) && d.phieu.getSoLuong() == so && d.phieu.soLanLuu == so, "Chỉ lưu từng phiếu thanh toán thành công");
        dung(d.kho.timKiem("TA001").getSoLuongTon() == 0 && d.kho.timKiem("TA003").getSoLuongTon() == 20,
                "Tạo và trả tiền cho phiếu nhập chưa cộng tồn");
        dung(dem("===== HÓA ĐƠN NHẬP HÀNG =====") == so, "Chỉ in hóa đơn cho các phiếu đã trả tiền");
        String log = NHAT_KY.toString(StandardCharsets.UTF_8);
        dung(log.indexOf("===== HÓA ĐƠN NHẬP HÀNG =====") > log.indexOf("ĐÃ HOÀN TẤT THANH TOÁN TIỀN MẶT THÀNH CÔNG."),
                "Bước thanh toán xuất hiện trước hóa đơn đầu tiên");
        dung(!log.contains("NCC_TAM") && !log.contains("HDN_TAM"), "Không hiển thị mã tạm của danh sách nháp");
    }

    private static void deXuatVaSuaDanhSach() {
        DuLieu d = new DuLieu();
        dung(d.kho.locHangCanNhap(1).length == 1, "Đề xuất hàng hết chỉ có mặt hàng tồn 0");
        NHAT_KY.reset();
        // Chọn hết hàng theo đề xuất; sau đó thay TA001 bằng TA003 và thanh toán cho NCC002.
        int so = d.phieu.nhapThemHang(phim("1", "1", "1", "2", "", "0",
                "5", "3", "1", "TA003", "4", "", "0", "0", "", "6", "1", "20000"), d.nv, d.ncc, d.kho);
        dung(so == 1 && ((HoadonNhap) d.phieu.get(0)).getMaNCC().equals("NCC002"), "Thay sản phẩm cập nhật nhóm NCC cuối cùng");
        dung(((HoadonNhap) d.phieu.get(0)).getDsChiTiet()[0].getSoLuongNhap() == 4, "Giữ số lượng vừa sửa");
        DuLieu xoa = new DuLieu();
        dung(xoa.phieu.nhapThemHang(phim("5", "1", "TA001", "2", "", "2", "1", "0", "4"),
                xoa.nv, xoa.ncc, xoa.kho) == 0 && xoa.phieu.getSoLuong() == 0, "Xóa dòng rồi hủy không tạo phiếu hoặc cộng kho");
    }

    private static void sanPhamMoi() {
        DuLieu d = new DuLieu();
        String nsx = Ktradulieu.dinhDangNgay(HOM_NAY);
        int so = d.phieu.nhapThemHang(phim("2", "1", "/", "Bánh mới", "5000", "10000", "gói", "0", nsx, HSD,
                "Việt Nam", "Khô", "Bột", "n", "2", "4", "", "0", "", "6", "1", "20000"), d.nv, d.ncc, d.kho);
        dung(so == 1 && d.kho.timKiem("TA004").getMaNCC().equals("NCC002"), "Nhân viên thêm sản phẩm mới và chọn đúng NCC");
        dung(d.kho.timKiem("TA004").getSoLuongTon() == 0, "Sản phẩm mới tồn 0 sau khi tạo/thanh toán phiếu");
        HoadonNhap hd = (HoadonNhap) d.phieu.get(0);
        dung(hd.duyetNhapKho(phim(), d.kho, new QuanLyCuaHang()) && d.kho.timKiem("TA004").getSoLuongTon() == 4,
                "Duyệt mới cộng lượng nhập cho sản phẩm mới");
        dung(!hd.duyetNhapKho(phim(), d.kho, new QuanLyCuaHang()), "Không duyệt hai lần");
    }

    public static void main(String[] args) throws Exception {
        PrintStream manHinh = System.out;
        try (PrintStream tam = new PrintStream(NHAT_KY, true, StandardCharsets.UTF_8)) {
            System.setOut(tam);
            chonVaLuuNCC();
            gomPhieuVaChanNhapSai();
            nhapNhieuNCC(false);
            nhapNhieuNCC(true);
            deXuatVaSuaDanhSach();
            sanPhamMoi();
        } catch (Throwable e) {
            manHinh.println(NHAT_KY.toString(StandardCharsets.UTF_8));
            throw e;
        } finally { System.setOut(manHinh); }
        System.out.println("PASS: " + soKiemTra + " kiểm tra NCC sản phẩm, gom phiếu nhập, thanh toán và tồn kho.");
    }
}
