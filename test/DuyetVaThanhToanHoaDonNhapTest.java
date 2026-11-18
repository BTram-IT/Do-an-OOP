import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

import doitac.DSNhaCungCap;
import doitac.NhaCungCap;
import hoadon.CTHDonNhap;
import hoadon.DSHoadon;
import hoadon.HoaDon;
import hoadon.HoadonNhap;
import person.Admin;
import person.Nguoidung;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.ThucAn;

/** Người lập chỉ gửi phiếu; Admin/Quản lý thanh toán lúc duyệt rồi mới cộng kho. */
public class DuyetVaThanhToanHoaDonNhapTest {
    private static final LocalDate HOM_NAY = LocalDate.now();
    private static final ByteArrayOutputStream NHAT_KY = new ByteArrayOutputStream();
    private static int soKiemTra;

    private static class KhoThu extends DSSanPham {
        @Override public void ghiFile(String path) { }
    }
    private static class NccThu extends DSNhaCungCap {
        @Override public void ghiFile(String path) { }
    }
    private static class PhieuThu extends DSHoadon {
        int soLanLuu;
        String daLuu;
        @Override public void ghiFile(String path) {
            soLanLuu++;
            daLuu = get(0).toFileString();
        }
    }
    private static class DuLieu {
        final KhoThu kho = new KhoThu();
        final NccThu ncc = new NccThu();
        final PhieuThu phieu = new PhieuThu();
        final NhanVien nv = new NhanVien("NV001", "Nhân viên thử", "nv", "123456", "0912345678", HOM_NAY, false);
        final HoadonNhap hd = new HoadonNhap("HDN100", nv, HOM_NAY, "DangXuLy", "Chờ duyệt", "NCC001");
        DuLieu() {
            ncc.them(new NhaCungCap("NCC001", "Nhà cung cấp thử", "0912345678", "TP HCM", "ncc@example.com", false));
            kho.themSP(new ThucAn("TA001", "Bánh thử", 5000, 10000, 20, "gói", HOM_NAY.minusDays(1),
                    HOM_NAY.plusDays(89), 10, "Bột", "Khô", "Việt Nam", false, "NCC001", false));
            hd.themCTHD(new CTHDonNhap("HDN100", "TA001", "Bánh thử", 2, 5000, 10, HOM_NAY.plusDays(89)));
            phieu.themHD(hd);
        }
    }

    private static QuanLyCuaHang quanLy() {
        return new QuanLyCuaHang("QL001", "Quản lý thử", "ql", "123456", "0912345678", "Quản lý", false, "Cửa hàng", 1.5);
    }
    private static Admin admin() {
        return new Admin("AD001", "Admin thử", "ad", "123456", "0912345678", false);
    }
    private static Scanner phim(String... keys) { return new Scanner(String.join("\n", keys) + "\n"); }
    private static String log() { return NHAT_KY.toString(StandardCharsets.UTF_8); }
    private static void dung(boolean ok, String message) {
        soKiemTra++;
        if (!ok) throw new AssertionError(message);
    }
    private static boolean duyet(DuLieu d, Nguoidung actor, String... keys) {
        NHAT_KY.reset();
        return d.phieu.duyetHoaDon(phim(keys), actor, d.kho, d.ncc);
    }

    private static void nguoiLapChiGuiDuyet() {
        for (Nguoidung actor : new Nguoidung[]{new DuLieu().nv, quanLy(), admin()}) {
            DuLieu d = new DuLieu();
            NHAT_KY.reset();
            int count = d.phieu.nhapThemHang(phim("3", "1", "TA001", "3", "", "0", "0", "HDN321", "Phiếu mới", "6"),
                    actor, d.ncc, d.kho);
            HoaDon moi = d.phieu.timKiem("HDN321");
            dung(count == 1 && moi != null && !moi.isDaThanhToan() && !moi.isDaXuLyThanhCong(),
                    "Mọi vai trò lập phiếu đều chỉ lưu phiếu chưa thanh toán chờ duyệt");
            dung(moi.getMaNV().equals(actor.getMaND()) && d.kho.get(0).getSoLuongTon() == 20,
                    "Giữ người lập và tồn kho trước khi duyệt");
            dung(!log().contains("Chọn thanh toán") && !log().contains("6. Thanh toán"),
                    "Không có bước thanh toán trong màn hình lập phiếu");
            HoadonNhap doc = (HoadonNhap) HoaDon.tuDongFile(moi.toFileString(), null);
            dung(!doc.isDaThanhToan() && doc.coTheChinhSua(), "Đọc lại phiếu chờ duyệt vẫn chưa thanh toán và được sửa");
        }
    }

    private static void phanQuyenVaHuyThanhToan() {
        DuLieu d = new DuLieu();
        dung(!d.hd.thanhToanHoaDon(phim(), d.nv, d.kho, d.ncc), "Nhân viên không được gọi thanh toán nhập trực tiếp");
        dung(!duyet(d, d.nv), "Nhân viên không được mở duyệt/thanh toán");
        dung(!d.hd.duyetNhapKho(phim(), d.kho, quanLy()), "Không gọi tắt nhập kho cho phiếu chưa thanh toán");
        for (Nguoidung actor : new Nguoidung[]{quanLy(), admin()}) {
            actor.setDaXoa(true);
            dung(!duyet(d, actor) && !d.hd.thanhToanHoaDon(phim(), actor, d.kho, d.ncc),
                    "Quản lý/Admin bị khóa không được duyệt hoặc chi tiền");
        }
        String[][] huy = {{"1", "0"}, {"1", "1", "0"}, {"1", "1", "1", "0"},
                {"1", "1", "2", "Ngân hàng thử", "0123456789", "NCC thử", "GD100", "0"}, {"1", "1", "1"}};
        for (String[] keys : huy) {
            dung(!duyet(d, quanLy(), keys), "Hủy xác nhận, hủy tiền mặt/chuyển khoản hoặc hết đầu vào không duyệt phiếu");
            dung(!d.hd.isDaThanhToan() && !d.hd.isDaXuLyThanhCong() && d.hd.coTheChinhSua(), "Phiếu vẫn chờ duyệt sau lần thanh toán không thành công");
            dung(d.kho.get(0).getSoLuongTon() == 20 && d.phieu.soLanLuu == 0
                    && !log().contains("===== HÓA ĐƠN NHẬP HÀNG ====="), "Không cộng kho, lưu thanh toán hoặc in biên nhận khi hủy");
        }
        d.kho.get(0).setSoLuongTon(Integer.MAX_VALUE);
        dung(!duyet(d, quanLy(), "1", "1") && !log().contains("Chọn thanh toán") && !d.hd.isDaThanhToan(),
                "Phiếu không thể nhập kho bị chặn trước khi chi tiền");
    }

    private static void thanhToanVaNhapKho(Path folder) throws Exception {
        for (Nguoidung actor : new Nguoidung[]{quanLy(), admin()}) {
            DuLieu d = new DuLieu();
            String[] keys = actor instanceof Admin
                    ? new String[]{"1", "1", "2", "Ngân hàng thử", "0123456789", "NCC thử", "GD101", "1"}
                    : new String[]{"1", "1", "1", "15000"};
            dung(duyet(d, actor, keys) && d.hd.isDaThanhToan() && d.hd.isDaXuLyThanhCong(), "Người duyệt thanh toán và nhập kho thành công");
            dung(d.kho.get(0).getSoLuongTon() == 22 && d.phieu.soLanLuu == 1, "Cộng đúng số lượng và lưu trạng thái sau duyệt");
            dung(d.hd.getMaNV().equals("NV001") && d.hd.getPhuongThucThanhToan().getMaNV().equals(actor.getMaND()),
                    "Nhân viên vẫn là người lập, Quản lý/Admin là người thanh toán");
            dung(log().indexOf("===== HÓA ĐƠN NHẬP HÀNG =====") > log().indexOf("Đã duyệt hóa đơn"),
                    "In hóa đơn có trạng thái đã thanh toán và nhập kho sau khi cộng kho");
            dung(log().contains("Người thanh toán: " + actor.getMaND()), "Hiển thị đúng người chi tiền");
            Path path = folder.resolve(actor.getMaND() + ".txt");
            DSHoadon save = new DSHoadon();
            save.themHD(d.hd);
            save.ghiFile(path.toString());
            DSHoadon restored = new DSHoadon();
            restored.docFile(path.toString(), null);
            HoadonNhap copy = (HoadonNhap) restored.timKiem("HDN100");
            dung(copy != null && copy.isDaThanhToan() && copy.isDaXuLyThanhCong()
                    && copy.getMaNV().equals("NV001") && copy.getPhuongThucThanhToan().getMaNV().equals(actor.getMaND()),
                    "Lưu/đọc lại giữ trạng thái và người thanh toán riêng");
            dung(!duyet(d, actor) && d.kho.get(0).getSoLuongTon() == 22 && d.phieu.soLanLuu == 1,
                    "Không duyệt, trả tiền hoặc cộng kho lần hai");
        }
    }

    private static void phieuCuDaTraTienVaThuLai() {
        DuLieu d = new DuLieu();
        d.hd.khoiPhucThanhToanTuFile(d.hd.tinhThanhTien(), "TienMat");
        String old17 = String.join(";", java.util.Arrays.copyOf(d.hd.toFileString().split(";", -1), 17));
        dung(((HoadonNhap) HoaDon.tuDongFile(old17, null)).isDaThanhToan(), "Đọc được phiếu 17 cột đã thanh toán trước khi đổi luồng");
        dung(duyet(d, quanLy(), "1", "1") && !log().contains("Chọn thanh toán"), "Phiếu cũ đã trả tiền không bị chi tiền lại");

        class PhieuNhapLoiMotLan extends HoadonNhap {
            boolean loi = true;
            PhieuNhapLoiMotLan() { super("HDN200", d.nv, HOM_NAY, "DangXuLy", "", "NCC001"); }
            @Override public boolean duyetNhapKho(Scanner sc, DSSanPham kho, Nguoidung actor) {
                if (loi) { loi = false; return false; }
                return super.duyetNhapKho(sc, kho, actor);
            }
        }
        PhieuNhapLoiMotLan hd = new PhieuNhapLoiMotLan();
        hd.themCTHD(new CTHDonNhap("HDN200", "TA001", "Bánh thử", 2, 5000, 10, HOM_NAY.plusDays(89)));
        PhieuThu phieu = new PhieuThu();
        phieu.themHD(hd);
        dung(!phieu.duyetHoaDon(phim("1", "1", "1", "15000"), quanLy(), d.kho, d.ncc)
                && hd.isDaThanhToan() && !hd.isDaXuLyThanhCong(), "Lỗi nhập kho sau khi trả tiền vẫn giữ giao dịch đã thành công");
        dung(phieu.soLanLuu == 1 && phieu.daLuu.contains(";TienMat;") && phieu.daLuu.endsWith(";QL001"),
                "Lưu giao dịch đã trả tiền dù nhập kho chưa hoàn tất");
        NHAT_KY.reset();
        dung(phieu.duyetHoaDon(phim("1", "1"), quanLy(), d.kho, d.ncc)
                && !log().contains("Chọn thanh toán") && d.kho.get(0).getSoLuongTon() == 24,
                "Thử duyệt lại chỉ nhập kho, không thanh toán thêm");
    }

    public static void main(String[] args) throws Exception {
        Path folder = Files.createTempDirectory("oop-duyet-thanh-toan-");
        PrintStream output = System.out;
        try (PrintStream log = new PrintStream(NHAT_KY, true, StandardCharsets.UTF_8)) {
            System.setOut(log);
            nguoiLapChiGuiDuyet();
            phanQuyenVaHuyThanhToan();
            thanhToanVaNhapKho(folder);
            phieuCuDaTraTienVaThuLai();
        } catch (Throwable e) {
            output.println(log());
            throw e;
        } finally {
            System.setOut(output);
            try (var files = Files.list(folder)) { files.forEach(p -> p.toFile().deleteOnExit()); }
            folder.toFile().deleteOnExit();
        }
        System.out.println("PASS: " + soKiemTra + " kiểm tra lập phiếu chờ duyệt, quyền chi tiền, hủy/thử lại và lưu người thanh toán.");
    }
}
