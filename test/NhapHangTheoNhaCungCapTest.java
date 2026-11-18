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
        dung(cacPhieu.stream().allMatch(hd -> hd.getMaHD().equals("HDN_TAM")),
                "Tách danh sách chưa tự sinh mã hóa đơn chính thức");
        for (HoadonNhap hd : cacPhieu) {
            for (CTHDonNhap ct : hd.getDsChiTiet()) {
                dung(ct.getMaHD().equals(hd.getMaHD()) && d.kho.timKiem(ct.getMaSP()).getMaNCC().equals(hd.getMaNCC()),
                        "Chi tiết thuộc đúng phiếu và đúng NCC");
            }
        }
        HoadonNhap sai = new HoadonNhap("HDN099", d.nv, HOM_NAY, "DangXuLy", "", "NCC001");
        sai.themCTHD(new CTHDonNhap("HDN099", d.kho.timKiem("TA003"), 1, 5000, HOM_NAY.plusDays(90)));
        dung(!sai.thanhToanHoaDon(phim(), new QuanLyCuaHang(), d.kho, d.ncc) && !sai.isDaThanhToan(), "Chặn chi tiền cho mặt hàng khác NCC");
        sai.khoiPhucThanhToanTuFile(sai.tinhThanhTien(), "TienMat");
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
        String[] dau = {"3", "1", "TA001", "2", "", "1", "TA003", "3", "",
                "1", "TA002", "1", "", "0", "0", "hdn101", "", "6", "HDN202", ""};
        String[] cuoi = huyPhieuHai ? new String[]{"0"} : new String[]{"6"};
        String[] keys = java.util.Arrays.copyOf(dau, dau.length + cuoi.length);
        System.arraycopy(cuoi, 0, keys, dau.length, cuoi.length);
        NHAT_KY.reset();
        int so = d.phieu.nhapThemHang(phim(keys), d.nv, d.ncc, d.kho);
        dung(so == (huyPhieuHai ? 1 : 2) && d.phieu.getSoLuong() == so && d.phieu.soLanLuu == so, "Lưu từng phiếu đã hoàn tất để chờ duyệt");
        dung(d.phieu.get(0).getMaHD().equals("HDN101") && (huyPhieuHai || d.phieu.get(1).getMaHD().equals("HDN202")),
                "Giữ đúng mã tự nhập cho từng NCC, không sinh mã kế tiếp");
        for (var hd : d.phieu.getDanhSach()) {
            for (CTHDonNhap ct : ((HoadonNhap) hd).getDsChiTiet()) {
                dung(ct.getMaHD().equals(hd.getMaHD()), "Chi tiết được chuyển sang mã hóa đơn người dùng nhập");
            }
        }
        dung(d.kho.timKiem("TA001").getSoLuongTon() == 0 && d.kho.timKiem("TA003").getSoLuongTon() == 20,
                "Hoàn tất phiếu nhập chưa cộng tồn");
        dung(dem("===== HÓA ĐƠN NHẬP HÀNG =====") == 0, "Chưa in hóa đơn thanh toán khi chỉ gửi duyệt");
        String log = NHAT_KY.toString(StandardCharsets.UTF_8);
        dung(d.phieu.getDanhSach().stream().noneMatch(hd -> hd.isDaThanhToan() || hd.isDaXuLyThanhCong())
                && !log.contains("Chọn thanh toán"), "Người lập chỉ gửi duyệt, không trả tiền hoặc nhập kho");
        dung(!log.contains("NCC_TAM") && !log.contains("HDN_TAM"), "Không hiển thị mã tạm của danh sách nháp");
        dung(dem("Nhập mã hóa đơn nhập (") == 2 && !log.contains("Chọn nhà cung cấp theo STT"),
                "Hỏi mã riêng cho mỗi NCC, không hỏi NCC khi chọn hàng có sẵn");
    }

    private static void deXuatVaSuaDanhSach() {
        DuLieu d = new DuLieu();
        dung(d.kho.locHangCanNhap(1).length == 1, "Đề xuất hàng hết chỉ có mặt hàng tồn 0");
        NHAT_KY.reset();
        // Chọn hết hàng theo đề xuất; sau đó thay TA001 bằng TA003 và gửi phiếu NCC002.
        int so = d.phieu.nhapThemHang(phim("1", "1", "1", "2", "", "0",
                "3", "3", "1", "TA003", "4", "", "0", "0", "HDN045", "", "6"), d.nv, d.ncc, d.kho);
        dung(so == 1 && ((HoadonNhap) d.phieu.get(0)).getMaNCC().equals("NCC002"), "Thay sản phẩm cập nhật nhóm NCC cuối cùng");
        dung(((HoadonNhap) d.phieu.get(0)).getDsChiTiet()[0].getSoLuongNhap() == 4, "Giữ số lượng vừa sửa");
        DuLieu xoa = new DuLieu();
        dung(xoa.phieu.nhapThemHang(phim("3", "1", "TA001", "2", "", "2", "1", "0", "4"),
                xoa.nv, xoa.ncc, xoa.kho) == 0 && xoa.phieu.getSoLuong() == 0, "Xóa dòng rồi hủy không tạo phiếu hoặc cộng kho");
    }

    private static void sanPhamMoi() {
        DuLieu d = new DuLieu();
        String nsx = Ktradulieu.dinhDangNgay(HOM_NAY);
        int so = d.phieu.nhapThemHang(phim("2", "1", "/", "Bánh mới", "5000", "10000", "gói", "0", nsx, HSD,
                "Việt Nam", "Khô", "Bột", "n", "2", "4", "", "0", "HDN500", "", "6"), d.nv, d.ncc, d.kho);
        dung(so == 1 && d.kho.timKiem("TA004").getMaNCC().equals("NCC002"), "Nhân viên thêm sản phẩm mới và chọn đúng NCC");
        dung(d.kho.timKiem("TA004").getSoLuongTon() == 0, "Sản phẩm mới tồn 0 sau khi hoàn tất phiếu");
        HoadonNhap hd = (HoadonNhap) d.phieu.get(0);
        QuanLyCuaHang ql = new QuanLyCuaHang();
        ql.setMaND("QL001");
        dung(d.phieu.duyetHoaDon(phim("1", "1", "1", "20000"), ql, d.kho, d.ncc)
                && d.kho.timKiem("TA004").getSoLuongTon() == 4 && hd.isDaThanhToan(),
                "Quản lý thanh toán lúc duyệt rồi mới cộng lượng nhập cho sản phẩm mới");
        dung(!hd.duyetNhapKho(phim(), d.kho, new QuanLyCuaHang()), "Không duyệt hai lần");
    }

    private static void kiemTraMaTuNhap() {
        DuLieu d = new DuLieu();
        d.phieu.themHD(new HoadonNhap("HDN007", d.nv, HOM_NAY, "DangXuLy", "Phiếu cũ", "NCC001"));
        NHAT_KY.reset();
        int so = d.phieu.nhapThemHang(phim("3", "1", "TA001", "1", "", "0", "0",
                "", "/", "HDB008", "HDN", "HDN12A", "HDN007", " hdn078 ", "Mã tự nhập",
                "7", "6"), d.nv, d.ncc, d.kho);
        dung(so == 1 && d.phieu.getSoLuong() == 2 && d.phieu.timKiem("HDN078") != null,
                "Yêu cầu nhập lại mã trống, sai loại, sai định dạng và trùng; giữ mã hợp lệ");
        HoadonNhap hd = (HoadonNhap) d.phieu.timKiem("HDN078");
        dung(hd.getDsChiTiet()[0].getMaHD().equals("HDN078") && hd.getGhiChu().equals("Mã tự nhập"),
                "Mã nhập được chuẩn hóa và gắn đúng với dòng hàng, ghi chú");
        String log = NHAT_KY.toString(StandardCharsets.UTF_8);
        dung(log.contains("Mã hóa đơn đã tồn tại: HDN007") && !log.contains("Mã tự tạo"),
                "Không tự sinh mã ngay cả khi nhập dấu gạch chéo");
        dung(log.indexOf("Nhập mã hóa đơn nhập (") > log.indexOf("Đã cập nhật danh sách cần nhập."),
                "Chỉ hỏi mã khi hoàn tất, sau khi đã chọn hàng");
        dung(log.contains("Mã hóa đơn nhập: HDN078") && !log.contains("Nhập mã hóa đơn nhập cần chỉnh sửa"),
                "Mục 3 sửa danh sách hiện tại, không mở phiếu cũ cùng lúc");

        DuLieu huy = new DuLieu();
        NHAT_KY.reset();
        dung(huy.phieu.nhapThemHang(phim("3", "1", "TA001", "1", "", "0", "0", "0"),
                huy.nv, huy.ncc, huy.kho) == 0 && huy.phieu.getSoLuong() == 0 && huy.phieu.soLanLuu == 0,
                "Hủy tại bước nhập mã không tạo hoặc lưu hóa đơn");
    }

    private static void khongHoiNCCKhiChonHang() {
        DuLieu d = new DuLieu();
        d.kho.themSP(sp("TA099", "", 3));
        d.kho.themSP(sp("TA004", "NCC009", 3));
        d.kho.themSP(sp("TA005", "NCC999", 3));
        NHAT_KY.reset();
        int so = d.phieu.nhapThemHang(phim("3", "1", "TA099", "1", "TA004", "1", "TA005",
                "1", "TA001", "2", "", "0", "4"), d.nv, d.ncc, d.kho);
        String log = NHAT_KY.toString(StandardCharsets.UTF_8);
        dung(so == 0 && !log.contains("Chọn nhà cung cấp theo STT") && !log.contains("Chọn nhà cung cấp cho"),
                "Hàng thiếu NCC, NCC ngừng hợp tác hoặc không tồn tại không mở bước chọn NCC");
        dung(d.kho.timKiem("TA099").getMaNCC().isEmpty() && d.kho.timKiem("TA099").getSoLuongTon() == 3,
                "Không tự đoán NCC hoặc thay tồn của dữ liệu thiếu");
        dung(dem("Hãy cập nhật NCC trong danh mục sản phẩm.") == 3 && log.contains("Đã cập nhật danh sách cần nhập."),
                "Báo dữ liệu cần sửa rồi vẫn cho chọn mặt hàng hợp lệ khác");
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
            kiemTraMaTuNhap();
            khongHoiNCCKhiChonHang();
        } catch (Throwable e) {
            manHinh.println(NHAT_KY.toString(StandardCharsets.UTF_8));
            throw e;
        } finally { System.setOut(manHinh); }
        System.out.println("PASS: " + soKiemTra + " kiểm tra NCC sản phẩm, gom phiếu nhập, thanh toán và tồn kho.");
    }
}
