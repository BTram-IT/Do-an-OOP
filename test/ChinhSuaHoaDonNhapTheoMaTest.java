import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

import doitac.DSNhaCungCap;
import doitac.NhaCungCap;
import hoadon.CTHDonNhap;
import hoadon.DSHoadon;
import hoadon.HoadonNhap;
import hoadon.Hoadonban;
import person.Admin;
import person.Nguoidung;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.ThucAn;

public class ChinhSuaHoaDonNhapTheoMaTest {
    private static int soKiemTra;
    private static final LocalDate HOM_NAY = LocalDate.now();
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
        final NhanVien nv = nhanVien("NV001");
        final NhanVien nvKhac = nhanVien("NV002");
        final HoadonNhap hd = taoPhieu("HDN001", nv);
        DuLieu() {
            ncc.them(new NhaCungCap("NCC001", "Nhà cung cấp một", "0912345678", "TP HCM", "mot@example.com", false));
            ncc.them(new NhaCungCap("NCC002", "Nhà cung cấp hai", "0912345679", "TP HCM", "hai@example.com", false));
            kho.themSP(sp("TA001", "NCC001"));
            kho.themSP(sp("TA002", "NCC001"));
            kho.themSP(sp("TA003", "NCC002"));
            phieu.themHD(hd);
        }
    }

    private static NhanVien nhanVien(String ma) {
        return new NhanVien(ma, "Nhân viên thử", ma.toLowerCase(), "123456", "0912345678", HOM_NAY, false);
    }
    private static ThucAn sp(String ma, String ncc) {
        return new ThucAn(ma, "Bánh " + ma, 5000, 10000, 20, "gói", HOM_NAY.minusDays(3),
                HOM_NAY.plusDays(90), 0, "Bột", "Khô", "Việt Nam", false, ncc, false);
    }
    private static HoadonNhap taoPhieu(String ma, Nguoidung nguoiLap) {
        HoadonNhap hd = new HoadonNhap(ma, nguoiLap, HOM_NAY, "DangXuLy", "Phiếu cần sửa", "NCC001");
        hd.themCTHD(new CTHDonNhap(ma, "TA001", "Bánh TA001", 2, 5000, 0, HOM_NAY.plusDays(90)));
        return hd;
    }
    private static Scanner phim(String... dong) { return new Scanner(String.join("\n", dong) + "\n"); }
    private static void dung(boolean dieuKien, String thongBao) {
        soKiemTra++;
        if (!dieuKien) throw new AssertionError(thongBao);
    }
    private static String log() { return NHAT_KY.toString(StandardCharsets.UTF_8); }
    private static boolean sua(DuLieu d, Nguoidung nguoi, String... dong) {
        NHAT_KY.reset();
        return d.phieu.chinhSuaHoaDonNhapTheoMa(phim(dong), nguoi, d.ncc, d.kho);
    }

    private static void nhapMaTruocKhiHienChiTiet() {
        DuLieu d = new DuLieu();
        NHAT_KY.reset();
        d.phieu.nhapThemHang(phim("3", "0", "4"), d.nv, d.ncc, d.kho);
        dung(log().contains("Nhập mã hóa đơn nhập cần chỉnh sửa"), "Mục 3 yêu cầu mã hóa đơn");
        dung(!log().contains("DANH SÁCH HÀNG CẦN NHẬP") && !log().contains("Tổng tiền hàng:"),
                "Không in hóa đơn trống trước khi chọn mã");
        dung(!sua(d, d.nv, "KHONG_CO", "0"), "Mã không tồn tại không mở chỉnh sửa");
        dung(!log().contains("1. Thêm mặt hàng") && d.phieu.soLanLuu == 0, "Mã sai không hiện thao tác hoặc ghi file");
        d.phieu.themHD(new Hoadonban("HDB001", d.nv, HOM_NAY, "DangXuLy", "", ""));
        dung(sua(d, d.nv, "HDB001", "hdn001", "0"), "Từ chối mã bán rồi cho nhập lại mã nhập hợp lệ");
        dung(log().contains("Không tìm thấy hóa đơn nhập có mã: HDB001"), "Kiểm tra đúng loại hóa đơn");
        dung(log().indexOf("Mã hóa đơn nhập: HDN001") < log().indexOf("1. Thêm mặt hàng"),
                "Hiện đúng phiếu đã chọn trước danh sách thao tác");
        dung(d.phieu.getSoLuong() == 2, "Tra cứu không tạo thêm hóa đơn tạm");
        NHAT_KY.reset();
        d.phieu.nhapThemHang(phim("5", "0", "4"), d.nv, d.ncc, d.kho);
        dung(log().contains("Chưa có mặt hàng.") && !log().contains("Tổng tiền hàng:"),
                "Danh sách đang nhập rỗng có thông báo ngắn, không in bảng và tổng 0");
    }

    private static void kiemTraTrangThaiVaQuyen() {
        DuLieu d = new DuLieu();
        HoadonNhap traTien = taoPhieu("HDN002", d.nv);
        traTien.khoiPhucThanhToanTuFile(traTien.tinhThanhTien(), "TienMat");
        HoadonNhap daDuyet = taoPhieu("HDN003", d.nv);
        daDuyet.xacNhanXuLy();
        HoadonNhap daHuy = taoPhieu("HDN004", d.nv);
        daHuy.huyHoaDon();
        d.phieu.themHD(traTien);
        d.phieu.themHD(daDuyet);
        d.phieu.themHD(daHuy);
        dung(!sua(d, new Admin(), "HDN002", "HDN003", "HDN004", "0"), "Khóa cả ba trạng thái cho Admin");
        dung(!log().contains("1. Thêm mặt hàng") && !log().contains("Bánh TA001"),
                "Phiếu khóa không hiện chi tiết hoặc thao tác sửa");
        dung(!sua(d, d.nvKhac, "HDN001", "0") && log().contains("do mình lập"), "Nhân viên không sửa phiếu của người khác");
        dung(sua(d, new QuanLyCuaHang(), "HDN001", "0"), "Quản lý sửa được phiếu nháp của nhân viên");
        dung(sua(d, new Admin(), "HDN001", "0"), "Admin sửa được phiếu nháp của nhân viên");
        NhanVien biKhoa = nhanVien("NV003");
        biKhoa.setDaXoa(true);
        dung(!sua(d, biKhoa), "Tài khoản bị khóa bị chặn trước khi nhập mã");
        dung(d.kho.get(0).getSoLuongTon() == 20 && d.hd.getSoLuongCT() == 1, "Tra cứu không đổi kho hoặc dòng hàng");
    }

    private static void themSuaXoaVaLuu() {
        DuLieu d = new DuLieu();
        dung(sua(d, d.nv, "HDN001", "1", "TA003", "TA002", "3", "", "2", "1", "4", "6000", "8",
                "3", "2", "1", "4", "NCC002", "0", "5", "Ghi chú đã sửa", "0"), "Hoàn thành chỉnh sửa phiếu đã chọn");
        dung(log().contains("không thuộc phiếu này"), "Không thêm hàng của nhà cung cấp khác");
        dung(d.hd.getSoLuongCT() == 1 && d.hd.getDsChiTiet()[0].getSoLuongNhap() == 4,
                "Thêm, sửa và xóa đúng dòng của phiếu");
        dung(d.hd.getDsChiTiet()[0].getDonGiaNhap() == 6000 && d.hd.getDsChiTiet()[0].getThueSuatNhap() == 8,
                "Cập nhật đơn giá và VAT");
        dung(d.hd.getMaNCC().equals("NCC001"), "Không đổi sang NCC không khớp mặt hàng đang có");
        dung(d.hd.getGhiChu().equals("Ghi chú đã sửa") && d.phieu.soLanLuu >= 5, "Lưu thay đổi vào danh sách hóa đơn");
        dung(d.kho.get(0).getSoLuongTon() == 20 && d.kho.get(0).getNsx().equals(HOM_NAY.minusDays(3)),
                "Sửa phiếu chưa thanh toán không cộng kho hoặc thay ngày sản phẩm");
        dung(!log().contains("===== HÓA ĐƠN NHẬP HÀNG ====="), "Chỉ hiện chi tiết, chưa in biên nhận thanh toán");
    }

    private static void thanhToanVaHuy() {
        DuLieu d = new DuLieu();
        dung(sua(d, d.nv, "HDN001", "6", "1", "10000"), "Thanh toán từ phiếu đã chọn");
        dung(d.hd.isDaThanhToan() && !d.hd.isDaXuLyThanhCong(), "Đã trả tiền nhưng vẫn chờ duyệt kho");
        dung(log().indexOf("===== HÓA ĐƠN NHẬP HÀNG =====") > log().indexOf("ĐÃ HOÀN TẤT THANH TOÁN"),
                "In hóa đơn sau thanh toán thành công");
        dung(d.kho.get(0).getSoLuongTon() == 20, "Thanh toán không tự ý duyệt nhập kho");
        dung(!sua(d, d.nv, "HDN001", "0"), "Không sửa tiếp hóa đơn đã trả tiền");
        DuLieu huy = new DuLieu();
        dung(sua(huy, huy.nv, "HDN001", "7", "n", "0") && !huy.hd.isDaHuy(), "Từ chối xác nhận hủy giữ nguyên phiếu");
        dung(sua(huy, huy.nv, "HDN001", "7", "y") && huy.hd.isDaHuy(), "Xác nhận hủy mới chuyển trạng thái");
        dung(huy.kho.get(0).getSoLuongTon() == 20, "Hủy phiếu không làm đổi tồn kho");
    }

    public static void main(String[] args) throws Exception {
        PrintStream manHinh = System.out;
        try (PrintStream tam = new PrintStream(NHAT_KY, true, StandardCharsets.UTF_8)) {
            System.setOut(tam);
            nhapMaTruocKhiHienChiTiet();
            kiemTraTrangThaiVaQuyen();
            themSuaXoaVaLuu();
            thanhToanVaHuy();
        } catch (Throwable e) {
            manHinh.println(log());
            throw e;
        } finally { System.setOut(manHinh); }
        System.out.println("PASS: " + soKiemTra + " kiểm tra nhập mã, phân quyền và chỉnh sửa hóa đơn nhập.");
    }
}
