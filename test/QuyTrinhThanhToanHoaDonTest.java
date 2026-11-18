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
import hoadon.CTHDBan;
import hoadon.CTHDonNhap;
import hoadon.DSHoadon;
import hoadon.HoaDon;
import hoadon.HoadonNhap;
import hoadon.Hoadonban;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import khuyenmai.KhuyenmaiMuatang;
import khuyenmai.SPKhMai;
import person.DsKhachHang;
import person.Khachhang;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.ThucAn;
import thanhtoan.ChuyenKhoan;
import thanhtoan.ThanhToan;
import thanhtoan.TienMat;
import util.Ktradulieu;

/** Các kịch bản bàn phím dùng dữ liệu riêng; không ghi vào data của dự án. */
public class QuyTrinhThanhToanHoaDonTest {
    private static int soKiemTra;
    private static final ByteArrayOutputStream NHAT_KY = new ByteArrayOutputStream();
    private static final LocalDate HOM_NAY = LocalDate.now();
    private static final String HSD = Ktradulieu.dinhDangNgay(HOM_NAY.plusDays(90));

    // Duyệt hồ sơ sản phẩm mới có gọi ghiFile tự động; kho thử tuyệt đối không ghi file này.
    private static class KhoThu extends DSSanPham {
        @Override public void ghiFile(String duongDan) { }
    }

    private static class DuLieu {
        final KhoThu kho = new KhoThu();
        final DsKhachHang khach = new DsKhachHang();
        final DSNhaCungCap ncc = new DSNhaCungCap();
        final DSKhuyenMai km = new DSKhuyenMai();
        final DSSanPhamKhuyenMai lienKet = new DSSanPhamKhuyenMai();
        final NhanVien nv = new NhanVien("NV001", "Nhân viên thử", "nv001", "123456", "0912345678", HOM_NAY, false);
        final DSHoadon hoaDon = new DSHoadon();

        DuLieu(Path thuMuc) throws Exception {
            Path sp = thuMuc.resolve("sanpham.txt");
            Files.write(sp, List.of(sanPham("TA001", 50, 5000, 20000, 10).toFileString(),
                    sanPham("TA002", 20, 10000, 30000, 0).toFileString()), StandardCharsets.UTF_8);
            kho.docFile(sp.toString());
            Path kh = thuMuc.resolve("khachhang.txt");
            Files.write(kh, List.of(new Khachhang("KH001", "Khách thử", "0912345678", "TP HCM", 10, false).toFileString()), StandardCharsets.UTF_8);
            khach.docFile(kh.toString());
            Path nc = thuMuc.resolve("nhacungcap.txt");
            Files.write(nc, List.of(new NhaCungCap("NCC001", "Nhà cung cấp một", "0912345678", "TP HCM", "mot@example.com", false).toFileString(),
                    new NhaCungCap("NCC002", "Nhà cung cấp hai", "0922345678", "TP HCM", "hai@example.com", false).toFileString()), StandardCharsets.UTF_8);
            ncc.docFile(nc.toString());
            km.themKhuyenMai(new KhuyenmaiMuatang("KM001", "Mua tặng", HOM_NAY, HOM_NAY.plusDays(10), 2, 1));
            lienKet.them(new SPKhMai("TA001", "KM001"));
        }

        Hoadonban ban(String ma) {
            Hoadonban hd = new Hoadonban(ma, nv, HOM_NAY, "DangXuLy", "", "KHACH_LE");
            dung(hd.themCTHD(new CTHDBan(ma, "TA001", "Bánh TA001", 4, 20000, 10, 0, 0, "KHONG"), kho, km, lienKet), "Tạo giỏ bán thử");
            return hd;
        }

        HoadonNhap nhap(String ma) {
            HoadonNhap hd = new HoadonNhap(ma, nv, HOM_NAY, "DangXuLy", "", "NCC001");
            dung(hd.themCTHD(new CTHDonNhap(ma, "TA001", "Bánh TA001", 2, 5000, 10, HOM_NAY.plusDays(90))), "Tạo giỏ nhập thử");
            return hd;
        }
    }

    private static ThucAn sanPham(String ma, int ton, double nhap, double ban, double vat) {
        return new ThucAn(ma, "Bánh " + ma, nhap, ban, ton, "gói", HOM_NAY.minusDays(1), HOM_NAY.plusDays(120),
                vat, "Bột", "Nơi khô", "Việt Nam", false, false);
    }

    private static Scanner phim(String... dong) { return new Scanner(String.join("\n", dong) + "\n"); }
    private static QuanLyCuaHang quanLy() {
        return new QuanLyCuaHang("QL001", "Quản lý thử", "ql", "123456", "0912345678",
                "Quản lý", false, "Cửa hàng", 1.5);
    }
    private static String manHinh() { return NHAT_KY.toString(StandardCharsets.UTF_8); }
    private static int soLan(String chuoi, String mau) { return chuoi.split(java.util.regex.Pattern.quote(mau), -1).length - 1; }

    private static void dung(boolean dieuKien, String thongBao) {
        soKiemTra++;
        if (!dieuKien) throw new AssertionError(thongBao);
    }

    private static void biChan(Runnable ham, String thongBao) {
        boolean loi = false;
        try { ham.run(); } catch (IllegalStateException | IllegalArgumentException e) { loi = true; }
        dung(loi, thongBao);
    }

    private static void banDayDu(DuLieu d) {
        Hoadonban hd = new Hoadonban();
        NHAT_KY.reset();
        boolean xong = hd.lapHoaDon(phim("/", "", "0", "TA001", "2", "0",
                "2", "TA001", "4", "1", "TA002", "1", "3", "TA002", "1",
                "4", "1", "KH001", "5", "Giao nhanh",
                "6", "1", "2", "0", // Dùng điểm nhưng hủy chọn phương thức: quay lại sửa.
                "2", "TA001", "5", "6", "1", "2", "1", "100000", "120000"),
                d.nv, d.hoaDon, d.khach, d.kho, d.km, d.lienKet);
        dung(xong && hd.isDaThanhToan() && hd.isDaXuLyThanhCong(), "Hoàn tất luồng bán sau khi chỉnh sửa");
        dung(hd.getSoLuongCT() == 1 && hd.getDsChiTiet()[0].getSoLuongBan() == 5, "Giữ đúng mặt hàng sau thêm, sửa, xóa");
        dung(hd.getDsChiTiet()[0].getSoLuongTang() == 2, "Tính lại khuyến mãi theo số lượng cuối");
        dung(hd.getMaKH().equals("KH001") && hd.getGhiChu().equals("Giao nhanh"), "Đổi khách hàng và ghi chú");
        dung(hd.getTienDungDiem() == 2000 && hd.tinhThanhTien() == 108000, "Tính lại VAT và điểm sau lần hủy thanh toán");
        TienMat tm = (TienMat) hd.getPhuongThucThanhToan();
        dung(tm.getTienKhachDua() == 120000 && tm.getTienThoi() == 12000, "Nhập thiếu tiền phải thử lại và tính tiền thối");
        dung(d.kho.timKiem("TA001").getSoLuongTon() == 43 && d.kho.timKiem("TA002").getSoLuongTon() == 20, "Chỉ trừ hàng mua và tặng ở lần thành công");
        dung(d.khach.timKiem("KH001").getDiemTichLuy() == 9, "Chỉ trừ/cộng điểm một lần");
        dung(soLan(manHinh(), "===== PHIẾU THANH TOÁN =====") == 1 && manHinh().contains(hd.getMaHD()), "In ngay đúng một hóa đơn bán");
        dung(!hd.coTheChinhSua() && !hd.thanhToanHoaDon(phim(), d.nv, d.khach, d.kho), "Không thanh toán lại hóa đơn bán");
        String truoc = hd.toFileString();
        dung(!hd.themSanPham(phim("TA002", "1"), d.kho, d.km, d.lienKet), "Khóa thêm sau thanh toán");
        hd.suaSoLuongSanPham(phim("TA001", "1"), d.kho, d.km, d.lienKet);
        hd.xoaSanPham(phim("TA001", "1"));
        biChan(() -> hd.setMaKH("KHACH_LE"), "Khóa đổi khách hàng");
        biChan(() -> hd.setGhiChu("Đổi"), "Khóa ghi chú");
        biChan(hd::huyHoaDon, "Khóa hủy hóa đơn đã thu tiền");
        dung(truoc.equals(hd.toFileString()) && d.kho.timKiem("TA001").getSoLuongTon() == 43, "Không thay đổi dữ liệu sau khi khóa");
    }

    private static void banChuyenKhoan(DuLieu d) {
        Hoadonban hd = d.ban("HDB010");
        hd.setMaKH("KH001");
        NHAT_KY.reset();
        dung(!hd.thanhToanHoaDon(phim("1", "2", "2", "Ngân hàng thử", "0123456789", "Khách thử", "GD0010", "0"), d.nv, d.khach, d.kho), "Chuyển khoản chưa xác nhận phải thất bại");
        dung(hd.coTheChinhSua() && !hd.isDaThanhToan() && hd.getPhuongThucThanhToan() == null, "Trở về hóa đơn chưa thanh toán");
        dung(hd.getTienDungDiem() == 0 && d.khach.timKiem("KH001").getDiemTichLuy() == 10, "Hủy chuyển khoản không trừ hoặc giữ điểm tạm");
        dung(d.kho.timKiem("TA001").getSoLuongTon() == 50 && !manHinh().contains("===== PHIẾU THANH TOÁN ====="), "Chưa xuất kho hay in biên lai khi chưa xác nhận");
        dung(hd.chinhSuaVaThanhToan(phim("5", "Đã kiểm tra", "6", "0", "2", "Ngân hàng thử", "0123456789", "Khách thử", "GD0011", "1"),
                d.nv, d.khach, d.kho, d.km, d.lienKet), "Sửa tiếp rồi thanh toán chuyển khoản thành công");
        dung(hd.getPhuongThucThanhToan() instanceof ChuyenKhoan && d.kho.timKiem("TA001").getSoLuongTon() == 44, "Chỉ xuất kho khi xác nhận chuyển khoản");
        dung(hd.inHoaDon().contains("GD0011") && soLan(manHinh(), "===== PHIẾU THANH TOÁN =====") == 1, "In đúng giao dịch chuyển khoản thành công");
    }

    private static void nhapDayDu(DuLieu d, Path thuMuc) throws Exception {
        HoadonNhap hd = new HoadonNhap();
        NHAT_KY.reset();
        boolean xong = hd.lapHoaDon(phim("/", "Ghi chú đầu", "NCC001", "TA001", "2", "", "0",
                "2", "1", "4", "6000", "5",
                "1", "TA002", "2", "", "3", "2", "1", "4", "NCC002", "5", "Nhập hàng sửa",
                "2", "1", "5", "", "",
                "5", "Đã kiểm tra", "7", "6"), d.nv, d.hoaDon, d.ncc, d.kho, d.nv);
        dung(xong && !hd.isDaThanhToan() && !hd.isDaXuLyThanhCong(), "Lập hóa đơn nhập chỉ hoàn tất để chờ duyệt");
        dung(!manHinh().contains("Chọn thanh toán") && d.kho.timKiem("TA001").getSoLuongTon() == 50,
                "Không hỏi tiền hoặc cộng kho ở bước lập phiếu");
        dung(hd.getSoLuongCT() == 1 && hd.getDsChiTiet()[0].getSoLuongNhap() == 5, "Thêm, sửa, xóa đúng sản phẩm nhập");
        dung(hd.getMaNCC().equals("NCC002") && hd.getGhiChu().equals("Đã kiểm tra"), "Đổi nhà cung cấp, ghi chú trước khi chi tiền");
        dung(hd.tinhTongTien() == 30000 && hd.tinhTienThue() == 1500 && hd.tinhThanhTien() == 31500, "Tính lại giá nhập và VAT");
        dung(!hd.thanhToanHoaDon(phim(), d.nv, d.kho, d.ncc), "Nhân viên không được thanh toán hóa đơn nhập");
        dung(!hd.thanhToanHoaDon(phim("1", "0"), quanLy(), d.kho, d.ncc) && hd.coTheChinhSua(),
                "Quản lý hủy thanh toán giữ phiếu chưa thanh toán");
        dung(!hd.thanhToanHoaDon(phim("2", "Ngân hàng thử", "0123456789", "Nhà cung cấp hai", "GD0020", "0"),
                quanLy(), d.kho, d.ncc), "Chưa xác nhận chuyển khoản không ghi đã trả tiền");
        dung(hd.thanhToanHoaDon(phim("1", "40000"), quanLy(), d.kho, d.ncc), "Quản lý thực hiện thanh toán nhập");
        dung(hd.getMaNV().equals("NV001") && hd.getPhuongThucThanhToan().getMaNV().equals("QL001"),
                "Giữ người lập phiếu và ghi người duyệt thanh toán riêng");
        dung(((TienMat) hd.getPhuongThucThanhToan()).getTienThoi() == 8500, "Ghi tiền cửa hàng chi và tiền NCC trả lại");
        dung(d.kho.timKiem("TA001").getSoLuongTon() == 50 && d.kho.timKiem("TA001").getDonGiaNhap() == 5000, "Thanh toán nhập chưa tự ý cập nhật kho/giá");
        dung(soLan(manHinh(), "===== HÓA ĐƠN NHẬP HÀNG =====") == 0, "Chưa in hóa đơn nhập hoàn tất trước bước cộng kho");
        dung(hd.inHoaDon().contains("Chờ duyệt nhập kho") && hd.inHoaDon().contains("NCC002"), "Tách trạng thái kho với thanh toán trên hóa đơn");
        dung(!hd.coTheChinhSua() && !hd.capNhatChiTiet(0, 1, 7000, 10) && !hd.xoaChiTiet(0), "Khóa sửa và xóa khi đã trả tiền nhưng chưa nhập kho");
        dung(!hd.themCTHD(new CTHDonNhap(hd.getMaHD(), "TA002", "Bánh", 1, 10000, 0, HOM_NAY.plusDays(90))), "Khóa thêm lô sau chi tiền");
        biChan(() -> hd.setMaNCC("NCC001"), "Khóa đổi nhà cung cấp");
        biChan(hd::huyHoaDon, "Không hủy hóa đơn nhập đã chi tiền");
        dung(!hd.thanhToanHoaDon(phim(), d.nv, d.kho, d.ncc), "Không thanh toán nhập hai lần");

        dung(hd.toFileString().split(";", -1).length == 18, "Ghi đủ trạng thái, chi tiết và người thanh toán nhập");
        d.hoaDon.themHD(hd);
        Path tep = thuMuc.resolve("hoa-don-nhap-da-tra.txt");
        d.hoaDon.ghiFile(tep.toString());
        DSHoadon doc = new DSHoadon();
        doc.docFile(tep.toString(), null);
        HoadonNhap lai = (HoadonNhap) doc.timKiem(hd.getMaHD());
        dung(lai != null && lai.isDaThanhToan() && !lai.isDaXuLyThanhCong() && !lai.coTheChinhSua(), "Mở lại vẫn khóa hóa đơn nhập đã trả tiền");
        dung(lai.toFileString().equals(hd.toFileString()), "Không mất số tiền, tiền thối, trạng thái khi lưu/đọc");
        dung(lai.getMaNV().equals("NV001") && lai.getPhuongThucThanhToan().getMaNV().equals("QL001"),
                "Đọc lại vẫn phân biệt người lập với người thanh toán");
        TienMat tm = (TienMat) lai.getPhuongThucThanhToan();
        dung(tm.getTienKhachDua() == 40000 && tm.getTienThoi() == 8500, "Khôi phục đúng chi tiết chi tiền mặt");
        dung(lai.duyetNhapKho(phim(), d.kho, new QuanLyCuaHang()), "Người có quyền vẫn duyệt được hóa đơn đã trả tiền");
        dung(d.kho.timKiem("TA001").getSoLuongTon() == 55 && d.kho.timKiem("TA001").getDonGiaNhap() == 6000, "Chỉ duyệt kho mới cộng tồn và cập nhật giá");
        dung(!lai.duyetNhapKho(phim(), d.kho, new QuanLyCuaHang()) && d.kho.timKiem("TA001").getSoLuongTon() == 55, "Không nhập kho hai lần");
    }

    private static void huyVaHetDauVao(DuLieu d) {
        Hoadonban ban = d.ban("HDB020");
        ban.setMaKH("KH001");
        NHAT_KY.reset();
        dung(!ban.chinhSuaVaThanhToan(phim("6", "1", "2", "1", "0", "0"), d.nv, d.khach, d.kho, d.km, d.lienKet) && ban.isDaHuy(), "Hủy thanh toán tiền mặt rồi hủy cả hóa đơn bán");
        dung(d.kho.timKiem("TA001").getSoLuongTon() == 50 && d.khach.timKiem("KH001").getDiemTichLuy() == 10, "Hủy không động tới tồn kho và điểm");
        dung(!manHinh().contains("===== PHIẾU THANH TOÁN ====="), "Không in biên lai thành công cho hóa đơn hủy");
        HoadonNhap nhap = d.nhap("HDN020");
        dung(!nhap.chinhSuaVaHoanTat(phim("3", "1", "1", "6", "0"), d.nv, d.ncc, d.kho, d.nv) && nhap.isDaHuy(), "Không thể gửi duyệt hóa đơn trống, vẫn hủy được");
        dung(d.hoaDon.getSoLuong() == 0, "Không tự lưu bản nháp/hóa đơn đã hủy");
        Hoadonban banEOF = d.ban("HDB021");
        dung(!banEOF.chinhSuaVaThanhToan(phim("6", "1"), d.nv, d.khach, d.kho, d.km, d.lienKet), "Hết đầu vào lúc thanh toán không làm chương trình văng lỗi");
        dung(!banEOF.isDaThanhToan() && d.kho.timKiem("TA001").getSoLuongTon() == 50, "Hết đầu vào không hoàn tất giao dịch");
        HoadonNhap nhapEOF = d.nhap("HDN021");
        dung(!nhapEOF.chinhSuaVaHoanTat(new Scanner(""), d.nv, d.ncc, d.kho, d.nv) && !nhapEOF.isDaThanhToan(), "Hết đầu vào lúc xem lại hóa đơn nhập");
    }

    private static void loNhapVaHoSoMoi(DuLieu d) {
        HoadonNhap hd = d.nhap("HDN030");
        dung(hd.themCTHD(new CTHDonNhap("HDN030", "TA001", "Bánh TA001", 3, 6000, 5, HOM_NAY.plusDays(100))), "Cộng dồn cùng mã dù giá/VAT/HSD cũ khác nhau");
        dung(hd.getSoLuongCT() == 1 && hd.getDsChiTiet()[0].getSoLuongNhap() == 5
                && hd.getDsChiTiet()[0].getDonGiaNhap() == 6000, "Mỗi mã chỉ có một dòng, dùng giá nhập cuối");
        dung(!hd.capNhatChiTiet(0, 0, 5000, 10), "Chặn số lượng 0");
        dung(!hd.capNhatChiTiet(0, 2, Double.NaN, 10), "Chặn giá NaN");
        dung(!hd.capNhatChiTiet(0, 2, 5000, 101), "Chặn VAT vượt 100");
        dung(!hd.capNhatChiTiet(0, 2, 0, 10), "Chặn giá nhập 0");
        dung(hd.capNhatChiTiet(0, 4, 7000, 0), "Sửa số lượng, giá, VAT của sản phẩm");
        dung(hd.tinhThanhTien() == 28000, "Tổng tiền phản ánh dòng sản phẩm đã sửa");
        dung(hd.xoaChiTiet(0) && hd.getSoLuongCT() == 0, "Xóa dòng sản phẩm không quản lý theo lô");

        HoadonNhap moi = new HoadonNhap("HDN031", d.nv, HOM_NAY, "DangXuLy", "", "NCC001");
        ThucAn hoSo = sanPham("TA999", 0, 5000, 20000, 10);
        CTHDonNhap ct = new CTHDonNhap("HDN031", hoSo, 3, 5000, HOM_NAY.plusDays(120));
        ct.setSanPhamMoi(hoSo);
        dung(moi.themCTHD(ct), "Thêm hồ sơ sản phẩm mới vào bản nháp");
        hoSo.setDonGiaNhap(9000);
        dung(moi.getDsChiTiet()[0].getSanPhamMoi().getDonGiaNhap() == 5000, "Sao chép hồ sơ, không giữ tham chiếu ngoài");
        dung(moi.capNhatChiTiet(0, 4, 6000, 5), "Sửa đồng bộ giá và VAT của hồ sơ cũ");
        CTHDonNhap sao = moi.getDsChiTiet()[0];
        dung(sao.getSanPhamMoi().getDonGiaNhap() == 6000 && sao.getSanPhamMoi().getPhanTramThue() == 5 && sao.getSanPhamMoi().getHsd().equals(HOM_NAY.plusDays(120)), "Sửa giá/VAT không nhập lại HSD của hồ sơ sản phẩm");
        sao.getSanPhamMoi().setDonGiaNhap(1000);
        dung(moi.getDsChiTiet()[0].getSanPhamMoi().getDonGiaNhap() == 6000, "Không sửa ngược hóa đơn qua getter bản sao");
        dung(moi.thanhToanHoaDon(phim("2", "Ngân hàng thử", "0123456789", "NCC một", "GD0031", "1"), quanLy(), d.kho, d.ncc), "Quản lý thanh toán hồ sơ mới bằng chuyển khoản");
        dung(d.kho.timKiem("TA999") == null, "Hồ sơ từ file cũ chưa làm tăng kho trước khi duyệt phiếu");
        dung(moi.duyetNhapKho(phim("1"), d.kho, new QuanLyCuaHang()), "Duyệt một phiếu nhập có hồ sơ cũ, không duyệt riêng từng sản phẩm");
        dung(d.kho.timKiem("TA999").getSoLuongTon() == 4 && d.kho.timKiem("TA999").getDonGiaNhap() == 6000, "Kho nhận đúng số lượng/giá đã chỉnh");
        String dong = moi.toFileString();
        HoadonNhap lai = (HoadonNhap) HoaDon.tuDongFile(dong, null);
        dung(lai.isDaThanhToan() && ((ChuyenKhoan) lai.getPhuongThucThanhToan()).getMaGDNganHang().equals("GD0031"), "Khôi phục xác nhận và mã giao dịch chuyển khoản nhập");
        dung(lai.inHoaDon().contains("Tài khoản nhận") && lai.inHoaDon().contains("0123456789"), "In thông tin người nhận cho chi chuyển khoản");
        String cu = String.join(";", java.util.Arrays.copyOf(dong.split(";", -1), 11));
        dung(((HoadonNhap) HoaDon.tuDongFile(cu.replace("DaXuLyThanhCong", "DangXuLy"), null)).isDaThanhToan(), "Đọc định dạng nhập 11 cột cũ vẫn nhận biết đã trả tiền");
    }

    private static void thanhToanCoBan(DuLieu d) {
        TienMat khong = new TienMat("HDB040", 0, d.nv);
        dung(!khong.thucHienThanhToan(phim("0")) && !khong.isThanhCong(), "Hóa đơn 0 đồng vẫn được hủy");
        dung(khong.thucHienThanhToan(phim("1")) && khong.isThanhCong() && khong.getTienThoi() == 0, "Hóa đơn 0 đồng cần xác nhận rõ ràng");
        TienMat tm = new TienMat("HDB041", 10000, d.nv);
        dung(!tm.thucHienThanhToan(phim("5000", "0")), "Thiếu tiền rồi hủy không thành công");
        dung(tm.getNgayThanhToan() == null, "Thanh toán hủy không ghi thời điểm thành công");
        dung(tm.thucHienThanhToan(phim("12000")) && tm.getTienThoi() == 2000, "Thử lại phương thức tiền mặt sau hủy");
        dung(!tm.thucHienThanhToan(new Scanner("")) && tm.getNgayThanhToan() == null, "Lần thử mới không giữ trạng thái thành công cũ khi hết đầu vào");
        ChuyenKhoan ck = new ChuyenKhoan("HDB042", 10000, d.nv);
        ck.setTenNganHang("Ngân hàng thử"); ck.setTenNguoiChuyen("Khách"); ck.setSoTKChuyen("0123456789"); ck.setMaGDNganHang("GD0042");
        dung(!ck.qTrinhThanhToan(), "Nhập đủ trường qua setter vẫn cần xác nhận thanh toán");
        dung(!ck.thucHienThanhToan(phim("0")), "Hủy chuyển khoản trước khi nhập thông tin");
        dung(ck.thucHienThanhToan(phim("Ngân hàng thử", "0123456789", "Khách", "GD0043", "1")), "Chuyển khoản chỉ thành công khi xác nhận");
        dung(!ck.thucHienThanhToan(phim("0")) && ck.getTrangThai().equals(ThanhToan.THAT_BAI), "Thử lại không giữ xác nhận của giao dịch trước");
    }

    private static void diemVaoDanhSach(DuLieu d) {
        NHAT_KY.reset();
        dung(!d.hoaDon.lapHoaDonMoi(phim("1", "/", "", "0", "TA001", "1", "0", "0"), d.nv, d.khach, d.ncc, d.kho, d.km, d.lienKet), "Điểm vào danh sách không lưu hóa đơn bán đã hủy");
        dung(d.hoaDon.getSoLuong() == 0 && d.kho.timKiem("TA001").getSoLuongTon() == 50, "Bán hủy từ danh sách không ảnh hưởng dữ liệu");
        dung(!d.hoaDon.lapHoaDonMoi(phim("2", "/", "", "NCC001", "TA001", "1", "", "0", "0"), d.nv, d.khach, d.ncc, d.kho, d.km, d.lienKet), "Điểm vào danh sách không lưu hóa đơn nhập đã hủy");
        dung(d.hoaDon.getSoLuong() == 0, "Không lưu hóa đơn nhập dở dang");
        dung(d.hoaDon.lapHoaDonMoi(phim("1", "/", "", "0", "TA001", "1", "0", "6", "1", "22000"), d.nv, d.khach, d.ncc, d.kho, d.km, d.lienKet), "Danh sách dùng trọn quy trình bán và thanh toán");
        dung(d.hoaDon.getSoLuong() == 1 && d.hoaDon.get(0).isDaThanhToan(), "Chỉ thêm hóa đơn đã thanh toán vào danh sách");
    }

    private static void kiemTraTruocKhiThuChi(DuLieu d) {
        Hoadonban ban = d.ban("HDB050");
        ban.setGhiChu("Ghi chú;không lưu được");
        dung(!ban.thanhToanHoaDon(phim("1", "100000"), d.nv, d.khach, d.kho)
                && !ban.isDaThanhToan() && d.kho.timKiem("TA001").getSoLuongTon() == 50,
                "Chặn thu tiền nếu hóa đơn bán chưa hợp lệ để lưu");
        HoadonNhap nhap = d.nhap("HDN050");
        dung(nhap.capNhatChiTiet(0, Integer.MAX_VALUE, 5000, 10), "Nhập số lượng lớn hợp lệ ở bản nháp");
        dung(!nhap.thanhToanHoaDon(phim("1", "99999999999999"), quanLy(), d.kho, d.ncc) && !nhap.isDaThanhToan(),
                "Chặn chi tiền trước khi biết hóa đơn không thể nhập kho vì tràn số lượng");
        dung(nhap.capNhatChiTiet(0, 2, 5000, 10), "Vẫn sửa được sau khi kiểm tra không đạt");
        nhap.setGhiChu("Ghi chú;sai định dạng");
        dung(!nhap.thanhToanHoaDon(phim("1", "20000"), quanLy(), d.kho, d.ncc) && !nhap.isDaThanhToan(),
                "Chặn chi tiền nếu hóa đơn nhập chưa hợp lệ để lưu");
    }

    public static void main(String[] args) throws Exception {
        Path thuMuc = Files.createTempDirectory("oop-quy-trinh-thanh-toan-");
        PrintStream manHinh = System.out;
        try (PrintStream tam = new PrintStream(NHAT_KY, true, StandardCharsets.UTF_8)) {
            System.setOut(tam);
            banDayDu(new DuLieu(thuMuc));
            banChuyenKhoan(new DuLieu(thuMuc));
            nhapDayDu(new DuLieu(thuMuc), thuMuc);
            huyVaHetDauVao(new DuLieu(thuMuc));
            loNhapVaHoSoMoi(new DuLieu(thuMuc));
            thanhToanCoBan(new DuLieu(thuMuc));
            diemVaoDanhSach(new DuLieu(thuMuc));
            kiemTraTruocKhiThuChi(new DuLieu(thuMuc));
        } catch (Throwable e) {
            manHinh.println(manHinh());
            throw e;
        } finally {
            System.setOut(manHinh);
            thuMuc.toFile().deleteOnExit();
            try (var tep = Files.list(thuMuc)) { tep.forEach(p -> p.toFile().deleteOnExit()); }
        }
        System.out.println("PASS: " + soKiemTra + " kiểm tra quy trình chỉnh sửa và thanh toán hóa đơn.");
    }
}
