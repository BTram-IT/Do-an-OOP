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
import hoadon.CTHDonNhap;
import hoadon.DSHoadon;
import hoadon.HoadonNhap;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.SanPham;
import sanpham.ThucAn;
import util.Ktradulieu;

/** Dữ liệu riêng: một mã/một dòng nhập, tự cập nhật ngày ở sản phẩm khi duyệt kho. */
public class NhapHangKhongTheoLoTest {
    private static final LocalDate HOM_NAY = LocalDate.now();
    private static int soKiemTra;

    private static class KhoThu extends DSSanPham {
        @Override public void ghiFile(String duongDan) { }
    }
    private static class NccThu extends DSNhaCungCap {
        @Override public void ghiFile(String duongDan) { }
    }
    private static final NhanVien NV = new NhanVien("NV001", "Nhân viên thử", "nv", "123456", "0912345678", HOM_NAY, false);
    private static final QuanLyCuaHang QL = new QuanLyCuaHang("QL001", "Quản lý thử", "ql", "123456",
            "0912345678", "Quản lý", false, "Cửa hàng", 1.5);

    private static ThucAn sp(String ma, int ton, LocalDate nsx, LocalDate hsd) {
        return new ThucAn(ma, "Bánh thử", 5000, 10000, ton, "gói", nsx, hsd,
                0, "Bột", "Khô", "Việt Nam", false, "NCC001", false);
    }
    private static Scanner phim(String... dong) { return new Scanner(String.join("\n", dong) + "\n"); }
    private static void dung(boolean dieuKien, String thongBao) {
        soKiemTra++;
        if (!dieuKien) throw new AssertionError(thongBao);
    }

    private static void nhapVaCapNhatNgay(Path thuMuc) throws Exception {
        KhoThu kho = new KhoThu();
        ThucAn sp = sp("TA001", 10, HOM_NAY.minusDays(5), HOM_NAY.plusDays(25));
        kho.themSP(sp);
        NccThu ncc = new NccThu();
        ncc.them(new NhaCungCap("NCC001", "Nhà cung cấp thử", "0912345678", "TP HCM", "thu@example.com", false));
        CTHDonNhap ct = new CTHDonNhap();
        try (Scanner sc = phim("2", "", "TIẾP THEO")) {
            dung(ct.nhapThongTin(sc, "HDN001", sp), "Nhập mặt hàng chỉ cần số lượng và giá");
            dung(sc.nextLine().equals("TIẾP THEO"), "Không đọc thêm NSX/HSD của từng lô");
        }
        HoadonNhap hd = new HoadonNhap("HDN001", NV, HOM_NAY, "DangXuLy", "", "NCC001");
        dung(hd.themCTHD(ct), "Thêm mặt hàng lần đầu");
        dung(hd.themCTHD(new CTHDonNhap("HDN001", "TA001", "Bánh thử", 3, 6000, 5, HOM_NAY.plusDays(100))),
                "Thêm cùng mã khác giá/VAT/ngày vẫn gộp một dòng");
        dung(hd.getSoLuongCT() == 1 && hd.getDsChiTiet()[0].getSoLuongNhap() == 5, "Một mã/một dòng, cộng dồn số lượng");
        dung(hd.getDsChiTiet()[0].getDonGiaNhap() == 6000 && hd.tinhThanhTien() == 31500, "Áp dụng giá và VAT lần nhập cuối");
        dung(!hd.capNhatChiTiet(0, 0, 6000, 5) && hd.getDsChiTiet()[0].getSoLuongNhap() == 5, "Sửa không hợp lệ giữ nguyên chi tiết");
        dung(sp.getNsx().equals(HOM_NAY.minusDays(5)) && sp.getHsd().equals(HOM_NAY.plusDays(25))
                && sp.getSoLuongTon() == 10, "Tạo phiếu chưa sửa ngày hoặc tồn kho");
        dung(!hd.thanhToanHoaDon(phim("0"), QL, kho, ncc), "Hủy thanh toán được phép quay lại sửa");
        dung(sp.getNsx().equals(HOM_NAY.minusDays(5)) && sp.getHsd().equals(HOM_NAY.plusDays(25)), "Hủy thanh toán không đổi ngày");
        dung(hd.thanhToanHoaDon(phim("1", "40000"), QL, kho, ncc), "Quản lý thanh toán phiếu nhập");
        dung(sp.getNsx().equals(HOM_NAY.minusDays(5)) && sp.getSoLuongTon() == 10, "Đã trả tiền nhưng chưa duyệt vẫn giữ ngày/tồn cũ");
        dung(hd.duyetNhapKho(phim(), kho, new QuanLyCuaHang()), "Duyệt nhập thành công");
        dung(sp.getNsx().equals(HOM_NAY) && sp.getHsd().equals(HOM_NAY.plusDays(30)), "NSX bằng ngày nhập, HSD giữ thời hạn 30 ngày");
        dung(sp.getSoLuongTon() == 15 && sp.getDonGiaNhap() == 6000, "Cộng kho và cập nhật giá trên sản phẩm hiện có");
        dung(!hd.duyetNhapKho(phim(), kho, new QuanLyCuaHang()) && sp.getSoLuongTon() == 15
                && sp.getHsd().equals(HOM_NAY.plusDays(30)), "Duyệt lần hai không cộng kho hoặc kéo dài HSD");

        String[] cot = sp.layTieuDeBang();
        Object[] dong = sp.layDongBang();
        int cotNSX = Arrays.asList(cot).indexOf("NGÀY SẢN XUẤT");
        int cotHSD = Arrays.asList(cot).indexOf("HẠN SỬ DỤNG");
        dung(cotNSX >= 0 && cotHSD >= 0 && dong[cotNSX].equals(Ktradulieu.dinhDangNgay(HOM_NAY))
                && dong[cotHSD].equals(Ktradulieu.dinhDangNgay(HOM_NAY.plusDays(30))), "Danh sách sản phẩm hiện đúng hai cột ngày mới");
        Path tep = thuMuc.resolve("sanpham.txt");
        Files.writeString(tep, sp.toFileString(), StandardCharsets.UTF_8);
        DSSanPham doc = new DSSanPham();
        doc.docFile(tep.toString());
        dung(doc.getSoLuong() == 1 && doc.get(0).getNsx().equals(HOM_NAY)
                && doc.get(0).getHsd().equals(HOM_NAY.plusDays(30)) && doc.get(0).getSoLuongTon() == 15,
                "Đọc lại giữ nguyên ngày và tồn kho đã cập nhật");
    }

    private static void hetHanVaKhongCapNhatMotPhan() {
        KhoThu kho = new KhoThu();
        ThucAn cu = sp("TA001", 7, HOM_NAY.minusDays(30), HOM_NAY.minusDays(10));
        kho.themSP(cu);
        CTHDonNhap ct = new CTHDonNhap();
        dung(ct.nhapThongTin(phim("4", ""), "HDN002", cu), "Nhập lại sản phẩm cũ hết hạn không phải gõ ngày mới");
        HoadonNhap hd = new HoadonNhap("HDN002", NV, HOM_NAY, "DangXuLy", "", "NCC001");
        hd.themCTHD(ct);
        hd.khoiPhucThanhToanTuFile(hd.tinhThanhTien(), "TienMat");
        dung(hd.duyetNhapKho(phim(), kho, new QuanLyCuaHang()) && cu.getSoLuongTon() == 4, "Loại tồn hết hạn trước khi nhận hàng mới");
        dung(cu.getNsx().equals(HOM_NAY) && cu.getHsd().equals(HOM_NAY.plusDays(20)), "Giữ thời hạn riêng 20 ngày của mặt hàng");
        ThucAn day = sp("TA002", Integer.MAX_VALUE, HOM_NAY.minusDays(2), HOM_NAY.plusDays(8));
        kho.themSP(day);
        HoadonNhap sai = new HoadonNhap("HDN003", NV, HOM_NAY, "DangXuLy", "", "NCC001");
        sai.themCTHD(new CTHDonNhap("HDN003", cu, 1, 8000, HOM_NAY.plusDays(1)));
        sai.themCTHD(new CTHDonNhap("HDN003", day, 1, 5000, HOM_NAY.plusDays(1)));
        sai.khoiPhucThanhToanTuFile(sai.tinhThanhTien(), "TienMat");
        dung(!sai.duyetNhapKho(phim(), kho, new QuanLyCuaHang()), "Một dòng tràn tồn chặn cả phiếu");
        dung(cu.getSoLuongTon() == 4 && cu.getDonGiaNhap() == 5000 && day.getNsx().equals(HOM_NAY.minusDays(2))
                && day.getHsd().equals(HOM_NAY.plusDays(8)), "Phiếu sai không sửa một phần số lượng, giá hoặc ngày");
    }

    private static void giuLichSuCu(Path thuMuc) throws Exception {
        HoadonNhap hd = new HoadonNhap("HDN004", NV, HOM_NAY, "DangXuLy", "Lịch sử cũ", "NCC001");
        dung(hd.themCTHDTuFile(new CTHDonNhap("HDN004", "TA001", "Bánh thử", 2, 5000, 0, HOM_NAY.plusDays(10))), "Nạp dòng lịch sử thứ nhất");
        dung(hd.themCTHDTuFile(new CTHDonNhap("HDN004", "TA001", "Bánh thử", 3, 6000, 0, HOM_NAY.plusDays(20))), "Nạp dòng lịch sử cùng mã khác giá");
        Path tep = thuMuc.resolve("hoadon-cu.txt");
        Files.write(tep, List.of(hd.toFileString(), "CTNHAP;" + hd.getDsChiTiet()[0].toDongFile(),
                "CTNHAP;" + hd.getDsChiTiet()[1].toDongFile()), StandardCharsets.UTF_8);
        DSHoadon doc = new DSHoadon();
        doc.docFile(tep.toString(), null);
        dung(doc.getSoLuong() == 1 && doc.get(0).tinhThanhTien() == 28000, "Đọc dữ liệu cũ không đổi lịch sử tổng tiền");
        dung(((HoadonNhap) doc.get(0)).getSoLuongCT() == 2, "Không gộp lại giá của những dòng lịch sử đã lưu");
    }

    public static void main(String[] args) throws Exception {
        Path thuMuc = Files.createTempDirectory("oop-nhap-khong-theo-lo-");
        PrintStream manHinh = System.out;
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        try (PrintStream tam = new PrintStream(log, true, StandardCharsets.UTF_8)) {
            System.setOut(tam);
            nhapVaCapNhatNgay(thuMuc);
            hetHanVaKhongCapNhatMotPhan();
            giuLichSuCu(thuMuc);
        } catch (Throwable e) {
            manHinh.println(log.toString(StandardCharsets.UTF_8));
            throw e;
        } finally {
            System.setOut(manHinh);
            thuMuc.toFile().deleteOnExit();
            try (var tep = Files.list(thuMuc)) { tep.forEach(p -> p.toFile().deleteOnExit()); }
        }
        System.out.println("PASS: " + soKiemTra + " kiểm tra nhập gộp sản phẩm và tự cập nhật NSX/HSD.");
    }
}
