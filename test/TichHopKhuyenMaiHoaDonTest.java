import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hoadon.CTHDBan;
import hoadon.CTHDonNhap;
import hoadon.DSHoadon;
import hoadon.HoaDon;
import hoadon.HoadonNhap;
import hoadon.Hoadonban;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import khuyenmai.Khuyenmai;
import khuyenmai.KhuyenmaiMuatang;
import khuyenmai.KhuyenmaiPhantram;
import khuyenmai.KhuyenmaiTructiep;
import khuyenmai.SPKhMai;
import person.NhanVien;
import sanpham.DSSanPham;
import sanpham.ThucAn;
import thanhtoan.ChuyenKhoan;
import thanhtoan.TienMat;

/** Chạy độc lập bằng javac/java; mọi file thử nghiệm đều nằm trong thư mục tạm. */
public class TichHopKhuyenMaiHoaDonTest {
    private static int soKiemTra;
    private static final LocalDate NGAY = LocalDate.of(2026, 11, 16);

    private static void dung(boolean dieuKien, String thongBao) {
        soKiemTra++;
        if (!dieuKien) throw new AssertionError(thongBao);
    }

    private static void phaiLoi(Runnable ham, String thongBao) {
        boolean coLoi = false;
        try { ham.run(); } catch (IllegalArgumentException | IllegalStateException | java.time.DateTimeException e) { coLoi = true; }
        dung(coLoi, thongBao);
    }

    private static NhanVien nhanVien() {
        return new NhanVien("NV001", "Nguyễn Văn An", "nv001", "123456", "0912345678",
                LocalDate.of(2020, 1, 1), false);
    }

    private static ThucAn thucAn(String ma, int ton, LocalDate hsd) {
        return new ThucAn(ma, "Bánh kiểm tra " + ma, 10000, 20000, ton, "gói",
                LocalDate.of(2020, 1, 1), hsd, 10, "Bột", "Nơi khô", "Việt Nam", false, false);
    }

    private static void kiemTraKhuyenMai() {
        LocalDate homNay = LocalDate.now();
        KhuyenmaiPhantram pt = new KhuyenmaiPhantram("KM001", "Phần trăm", homNay, homNay, 20);
        dung(pt.kiemTraThoiHan(homNay), "Ngày bắt đầu/kết thúc phải được áp dụng");
        dung(!pt.kiemTraThoiHan(homNay.minusDays(1)) && !pt.kiemTraThoiHan(homNay.plusDays(1)), "Sai biên ngày");
        dung(pt.toDongFile().split(";", -1).length == 6, "Giữ định dạng cũ khi chưa có điều kiện mới");
        pt.setMucChiToiThieu(100000);
        pt.setMucGiamToiDa(15000);
        dung(pt.tinhTongTienGiam(20000, 4) == 0, "Chưa đủ mức chi tối thiểu");
        dung(pt.tinhTongTienGiam(20000, 5) == 15000, "Giới hạn giảm trên cả dòng, không nhân theo số lượng");
        KhuyenmaiPhantram docLai = (KhuyenmaiPhantram) Khuyenmai.tuDongFile(pt.toDongFile());
        dung(docLai.getMucChiToiThieu() == 100000 && docLai.getMucGiamToiDa() == 15000, "Mất điều kiện sau khi đọc lại");
        docLai.setMucGiamToiDa(0);
        dung(docLai.tinhTongTienGiam(20000, 5) == 20000, "0 phải có nghĩa là không giới hạn");
        phaiLoi(() -> pt.setMucChiToiThieu(-1), "Chặn mức chi âm");
        phaiLoi(() -> pt.setMucGiamToiDa(-1), "Chặn mức giảm âm");
        phaiLoi(() -> pt.tinhTongTienGiam(Long.MAX_VALUE, 2), "Chặn tràn số tiền hàng");
        phaiLoi(() -> Khuyenmai.tuDongFile("PHANTRAM;KM001;Giảm;31/02/2026;01/03/2026;20"), "Ngày không tồn tại");
        phaiLoi(() -> Khuyenmai.tuDongFile("PHANTRAM;KM001;Giảm;01/01/2026;31/12/2026;NaN"), "Chặn NaN");
        phaiLoi(() -> new SPKhMai("SP001;BAD", "KM001"), "Mã liên kết không được phá định dạng file");

        DSKhuyenMai ds = new DSKhuyenMai();
        DSSanPhamKhuyenMai lienKet = new DSSanPhamKhuyenMai();
        KhuyenmaiTructiep tt = new KhuyenmaiTructiep("KM002", "Trực tiếp", homNay, homNay, 3000);
        KhuyenmaiMuatang tang = new KhuyenmaiMuatang("KM003", "Mua tặng", homNay, homNay, 2, 1);
        tang.setMucChiToiThieu(100000);
        for (Khuyenmai km : new Khuyenmai[]{pt, tt, tang}) {
            dung(ds.themKhuyenMai(km), "Thêm khuyến mãi");
            dung(lienKet.them(new SPKhMai("TA001", km.getMaKM())), "Thêm liên kết");
        }
        dung(tt.tinhTongTienGiam(20000, 5) == 15000, "Giữ giảm trực tiếp theo mỗi sản phẩm");
        dung(tang.tinhSoLuongTang(5) == 2, "Giữ số lượng mua trả tiền tách riêng số lượng tặng");
        ThucAn sp = thucAn("TA001", 10, homNay.plusDays(20));
        CTHDBan ct = new CTHDBan("HDB001", "TA001", "Bánh", 5, 20000, 10, 0, 0, "KHONG");
        dung(ct.apDungKhuyenMai(ds, lienKet, sp) && "KM003".equals(ct.getMaKM()) && ct.getSoLuongTang() == 2,
                "Chọn quà tặng có lợi nhất khi kho đủ");
        sp.setSoLuongTon(5);
        dung(ct.apDungKhuyenMai(ds, lienKet, sp) && "KM001".equals(ct.getMaKM()) && ct.getTienGiam() == 15000,
                "Chọn ưu đãi tiền khi không còn hàng tặng");
        ct.setSoLuongBan(4);
        dung(ct.apDungKhuyenMai(ds, lienKet, sp) && "KM002".equals(ct.getMaKM()), "Tính lại điều kiện khi sửa số lượng");
        dung(Khuyenmai.tuDongFile(tang.toDongFile()).getMucChiToiThieu() == 100000, "Lưu mức chi mua tặng");
        tt.setMucChiToiThieu(50000);
        dung(Khuyenmai.tuDongFile(tt.toDongFile()).getMucChiToiThieu() == 50000, "Lưu mức chi giảm trực tiếp");

        ds.themKhuyenMai(new KhuyenmaiPhantram("KM004", "Cũ", homNay.minusDays(2), homNay.minusDays(1), 10));
        ds.themKhuyenMai(new KhuyenmaiPhantram("KM005", "Tương lai", homNay.plusDays(1), homNay.plusDays(2), 10));
        dung(ds.locTheoTrangThai(1, homNay).length == 3 && ds.locTheoTrangThai(2, homNay).length == 1
                && ds.locTheoTrangThai(3, homNay).length == 1, "Lọc đúng ba trạng thái");
        dung(ds.timKiemTheoTuKhoa("km00").length == 5 && ds.timKiemTheoTuKhoa("MUA").length == 1, "Tìm mã và loại, không phân biệt hoa thường");
    }

    private static void kiemTraCanHan(Path thuMuc) throws Exception {
        DSSanPham sp = new DSSanPham();
        DSKhuyenMai km = new DSKhuyenMai();
        DSSanPhamKhuyenMai lienKet = new DSSanPhamKhuyenMai();
        int[] soNgay = {0, 3, 4, 7, 8, 15, 16, -1};
        List<String> duLieuSP = new ArrayList<>();
        for (int i = 0; i < soNgay.length; i++) duLieuSP.add(thucAn("TA00" + (i + 1), 10, NGAY.plusDays(soNgay[i])).toFileString());
        ThucAn daXoa = thucAn("TA009", 10, NGAY.plusDays(1));
        daXoa.setDaXoa(true);
        duLieuSP.add(daXoa.toFileString());
        duLieuSP.add(thucAn("TA010", 0, NGAY.plusDays(1)).toFileString());
        Path tepSP = thuMuc.resolve("sanpham-thu.txt");
        Files.write(tepSP, duLieuSP, StandardCharsets.UTF_8);
        sp.docFile(tepSP.toString());
        km.themKhuyenMai(new KhuyenmaiPhantram("KM001", "Thủ công", NGAY.plusDays(1), NGAY.plusDays(10), 5));
        lienKet.them(new SPKhMai("TA002", "KM001"));
        dung(lienKet.tuDongGiamGiaCanHSD(sp, km, NGAY) == 5, "Chỉ tạo cho thực phẩm còn hạn, còn tồn và chưa bị xóa");
        dung(lienKet.tuDongGiamGiaCanHSD(sp, km, NGAY) == 0 && km.getSoLuong() == 6, "Gọi lặp không tạo mã trùng");
        dung(lienKet.layDanhSachMaSPTheoKM("KM001").equals(List.of("TA002")), "Giữ chương trình thủ công chưa bắt đầu");
        for (SPKhMai lk : lienKet.getDanhSach()) {
            if (lk.getMaKM().equals("KM001")) continue;
            ThucAn hang = (ThucAn) sp.timKiem(lk.getMaSP());
            long con = java.time.temporal.ChronoUnit.DAYS.between(NGAY, hang.getHsd());
            double mongDoi = con <= 3 ? 50 : (con <= 7 ? 30 : 15);
            dung(((KhuyenmaiPhantram) km.timKiem(lk.getMaKM())).getPhanTramGiamGia() == mongDoi, "Đúng mức giảm theo hạn");
            dung(hang.getSoLuongTon() == 10 && hang.getDonGiaBan() == 20000, "Không sửa kho hoặc giá gốc");
        }
        dung(lienKet.tuDongGiamGiaCanHSD(sp, km, NGAY.plusDays(1)) == 3, "Cập nhật hai mức giảm và thêm sản phẩm vừa tới mốc 15 ngày");
        Path tepKM = thuMuc.resolve("khuyenmai.txt");
        Path tepLK = thuMuc.resolve("lienket.txt");
        km.ghiFile(tepKM.toString());
        lienKet.ghiFile(tepLK.toString());
        DSKhuyenMai docKM = new DSKhuyenMai();
        DSSanPhamKhuyenMai docLK = new DSSanPhamKhuyenMai();
        docKM.docFile(tepKM.toString());
        docLK.docFile(tepLK.toString());
        dung(docKM.getSoLuong() == km.getSoLuong() && docLK.getSoLuong() == lienKet.getSoLuong(), "Đọc lại khuyến mãi cận hạn");
        dung(docLK.tuDongGiamGiaCanHSD(sp, docKM, NGAY.plusDays(1)) == 0, "Giữ nhận diện mã tự động qua lần chạy sau");
        String truoc = docKM.toString();
        Path tepLoi = thuMuc.resolve("khuyenmai-loi.txt");
        Files.writeString(tepLoi, km.getDanhSach()[0].toDongFile() + "\nDONG_LOI", StandardCharsets.UTF_8);
        docKM.docFile(tepLoi.toString());
        dung(truoc.equals(docKM.toString()), "Đọc lỗi phải giữ toàn bộ danh sách khuyến mãi cũ");
    }

    private static Hoadonban hoaDonBan(String ma, String maSP, int sl, int tang, double gia, String trangThai) {
        Hoadonban hd = new Hoadonban(ma, nhanVien(), NGAY, "DangXuLy", "Kiểm tra", "KH001");
        dung(hd.themCTHDTuFile(new CTHDBan(ma, maSP, "Bánh " + maSP, sl, gia, 10, 0, tang, "KHONG")), "Gắn chi tiết đúng hóa đơn");
        if (trangThai.equals("DaXuLyThanhCong")) hd.xacNhanXuLy();
        if (trangThai.equals("DaHuy")) hd.huyHoaDon();
        return hd;
    }

    private static List<String> cacDong(DSHoadon ds) {
        ArrayList<String> ketQua = new ArrayList<>();
        for (HoaDon hd : ds.getDanhSach()) {
            ketQua.add(hd.toFileString());
            if (hd instanceof Hoadonban ban) for (CTHDBan ct : ban.getDsChiTiet()) ketQua.add("CTBAN;" + ct.toDongFile());
            if (hd instanceof HoadonNhap nhap) for (CTHDonNhap ct : nhap.getDsChiTiet()) ketQua.add("CTNHAP;" + ct.toDongFile());
        }
        return ketQua;
    }

    private static void kiemTraHoaDon(Path thuMuc) throws Exception {
        DSHoadon ds = new DSHoadon();
        Hoadonban tienMat = hoaDonBan("HDB001", "TA001", 5, 2, 20000, "DaXuLyThanhCong");
        tienMat.khoiPhucThanhToanTuFile(0, tienMat.tinhThanhTien(), "TienMat");
        tienMat.khoiPhucChiTietThanhToanTuFile(120000, "", "", "", "");
        Hoadonban chuyenKhoan = hoaDonBan("HDB002", "TA002", 8, 1, 10000, "DaXuLyThanhCong");
        chuyenKhoan.khoiPhucThanhToanTuFile(0, chuyenKhoan.tinhThanhTien(), "ChuyenKhoan");
        chuyenKhoan.khoiPhucChiTietThanhToanTuFile(0, "GD123", "Nguyễn Văn An", "0123456789", "Ngân hàng thử");
        ds.themHD(tienMat);
        ds.themHD(chuyenKhoan);
        ds.themHD(hoaDonBan("HDB003", "TA001", 3, 0, 20000, "DaXuLyThanhCong"));
        ds.themHD(hoaDonBan("HDB004", "TA002", 999, 0, 10000, "DangXuLy"));
        Hoadonban daHuy = hoaDonBan("HDB005", "TA002", 888, 0, 10000, "DaHuy");
        ds.themHD(daHuy);
        HoadonNhap nhap = new HoadonNhap("HDN001", nhanVien(), NGAY.plusDays(1), "DangXuLy", "Nhập bổ sung cùng sản phẩm", "NCC001");
        dung(nhap.themCTHD(new CTHDonNhap("HDN001", "TA001", "Bánh", 2, 10000, 5, NGAY.plusDays(10))), "Thêm sản phẩm nhập");
        dung(nhap.themCTHD(new CTHDonNhap("HDN001", "TA001", "Bánh", 4, 10000, 5, NGAY.plusDays(20))), "Cộng dồn cùng mã không chia theo lô");
        ds.themHD(nhap);
        dung(!nhap.themCTHD(new CTHDonNhap("HDN999", "TA002", "Sai hóa đơn", 1, 10000, 5, NGAY.plusDays(10))), "Không nhận chi tiết sai hóa đơn");
        ThucAn sanPhamMoi = thucAn("TA999", 0, NGAY.plusDays(20));
        CTHDonNhap chiTietMoi = new CTHDonNhap("HDN999", sanPhamMoi, 5, 10000, NGAY.plusDays(20));
        chiTietMoi.setSanPhamMoi(sanPhamMoi);
        String dongMoi = chiTietMoi.toDongFile();
        dung(CTHDonNhap.tuDongFile(dongMoi) != null, "Khôi phục hồ sơ sản phẩm mới");
        dung(CTHDonNhap.tuDongFile(dongMoi + ";COT_THUA") == null, "Chặn cột thừa trong hồ sơ mới");
        dung(CTHDonNhap.tuDongFile(dongMoi.replace(";10000;20000;", ";10000;NaN;")) == null, "Chặn giá bán NaN trong hồ sơ mới");
        phaiLoi(() -> daHuy.setGhiChu("Sửa sai"), "Không sửa hóa đơn đã hủy");
        phaiLoi(daHuy::xacNhanXuLy, "Không xác nhận hóa đơn đã hủy");
        phaiLoi(() -> nhap.setTrangThai("TRANG_THAI_LA"), "Không nhận trạng thái lạ");
        dung(ds.locChuaXuLy("HDBAN").size() == 1, "Loại hóa đơn đã hủy khỏi danh sách chưa xử lý");
        dung(ds.traCuuTheoKhoangNgay(NGAY, NGAY, "HDBAN").size() == 5, "Tra cứu ngày bao gồm hai biên");
        dung(ds.timTheoDoiTac(" kh001 ", "HDBAN").size() == 5 && ds.timTheoDoiTac("ncc001", "HDNHAP").size() == 1, "Tra cứu đối tác");
        dung(ds.locTheoTongTien(88000, 88000, "HDBAN").size() == 1, "Lọc đúng biên tiền thanh toán, có VAT");
        phaiLoi(() -> ds.traCuuTheoKhoangNgay(NGAY.plusDays(1), NGAY, null), "Chặn khoảng ngày ngược");
        phaiLoi(() -> ds.locTheoTongTien(Double.NaN, 1000, null), "Chặn khoảng tiền NaN");
        List<DSHoadon.SanPhamBanChay> top = ds.topNSanPham(10, NGAY, NGAY);
        dung(top.size() == 2 && top.get(0).getMaSP().equals("TA001"), "Xếp theo số bán, rồi tiền hàng khi bằng số lượng");
        dung(top.get(0).getSoLuongBan() == 8 && top.get(0).getSoLuongTang() == 2
                && top.get(0).getTienHangSauGiam() == 160000, "Tách hàng bán, quà tặng và VAT; bỏ đơn nháp/hủy");
        dung(ds.topNSanPham(1, NGAY, NGAY).size() == 1, "Giới hạn Top N");

        Path tep = thuMuc.resolve("hoadon.txt");
        ds.ghiFile(tep.toString());
        dung(Files.exists(tep) && !Files.exists(Path.of(tep + ".tmp")), "Ghi file chính sau khi đóng file tạm");
        Path phienCu = Path.of(tep + ".tmp");
        Files.writeString(phienCu, "Giao dịch chưa lưu từ phiên cũ", StandardCharsets.UTF_8);
        ds.ghiFile(tep.toString());
        dung(Files.readString(phienCu).equals("Giao dịch chưa lưu từ phiên cũ"), "Giữ file .tmp cũ khi ghi phiên mới");
        DSHoadon doc = new DSHoadon();
        doc.docFile(tep.toString(), null);
        dung(cacDong(ds).equals(cacDong(doc)), "Đọc lại giữ đủ tổng tiền, chi tiết, trạng thái, thông tin thanh toán");
        TienMat tm = (TienMat) ((Hoadonban) doc.timKiem("HDB001")).getPhuongThucThanhToan();
        dung(tm.getTienKhachDua() == 120000 && tm.getTienThoi() == 10000, "Khôi phục tiền nhận và tiền thối");
        ChuyenKhoan ck = (ChuyenKhoan) ((Hoadonban) doc.timKiem("HDB002")).getPhuongThucThanhToan();
        dung(ck.getMaGDNganHang().equals("GD123") && ck.getSoTKChuyen().equals("0123456789"), "Khôi phục mã giao dịch và tài khoản");
        String[] cu = tienMat.toFileString().split(";", -1);
        String dongCu = String.join(";", java.util.Arrays.copyOf(cu, 13));
        dung(HoaDon.tuDongFile(dongCu, null) instanceof Hoadonban, "Đọc được hóa đơn cũ 13 trường");
        phaiLoi(() -> HoaDon.tuDongFile(tienMat.toFileString().replace(";TienMat;", ";KhongHopLe;"), null), "Chặn loại thanh toán lạ");
        phaiLoi(() -> new TienMat().setTienKhachDua(Double.NaN), "Chặn tiền khách đưa NaN");

        List<String> truoc = cacDong(doc);
        String valid = String.join("\n", cacDong(ds));
        String chiTiet = "CTBAN;" + tienMat.getDsChiTiet()[0].toDongFile();
        String[] cacLoi = {valid + "\n" + tienMat.toFileString(), valid + "\n" + chiTiet,
                "CTBAN;HDB999;TA001;Bánh;1;10000;0;0;0;KHONG", "HDBAN;THIEU_COT",
                valid.replace(";120000;;;;", ";NaN;;;;")};
        for (int i = 0; i < cacLoi.length; i++) {
            Path tepLoi = thuMuc.resolve("hoadon-loi-" + i + ".txt");
            Files.writeString(tepLoi, cacLoi[i], StandardCharsets.UTF_8);
            doc.docFile(tepLoi.toString(), null);
            dung(truoc.equals(cacDong(doc)), "Không nạp một phần khi gặp dữ liệu lỗi " + i);
            doc.ghiFile(tepLoi.toString());
            dung(Files.readString(tepLoi).equals(cacLoi[i]) && Files.exists(Path.of(tepLoi + ".moi")), "Bảo vệ file lỗi và ghi phiên mới riêng");
        }

        Path tepAnToan = thuMuc.resolve("ghi-an-toan.txt");
        ds.ghiFile(tepAnToan.toString());
        String duLieuCu = Files.readString(tepAnToan);
        ds.timKiem("HDB004").setGhiChu("Ghi chú;sai định dạng");
        ds.ghiFile(tepAnToan.toString());
        dung(duLieuCu.equals(Files.readString(tepAnToan)) && !Files.exists(Path.of(tepAnToan + ".tmp")), "Ghi thất bại không làm mất file cũ");
        DSHoadon dayMa = new DSHoadon();
        dayMa.themHD(hoaDonBan("HDB2147483647", "TA001", 1, 0, 20000, "DangXuLy"));
        phaiLoi(() -> dayMa.sinhMaTuDong("hdb"), "Không sinh mã âm khi bộ đếm đầy");
    }

    public static void main(String[] args) throws Exception {
        Path thuMuc = Files.createTempDirectory("oop-km-hoadon-test-");
        thuMuc.toFile().deleteOnExit();
        PrintStream manHinh = System.out;
        ByteArrayOutputStream nhatKy = new ByteArrayOutputStream();
        try (PrintStream tam = new PrintStream(nhatKy, true, StandardCharsets.UTF_8)) {
            System.setOut(tam);
            kiemTraKhuyenMai();
            kiemTraCanHan(thuMuc);
            kiemTraHoaDon(thuMuc);
        } catch (Throwable e) {
            manHinh.println(nhatKy.toString(StandardCharsets.UTF_8));
            throw e;
        } finally {
            System.setOut(manHinh);
            try (var tep = Files.list(thuMuc)) { tep.forEach(p -> p.toFile().deleteOnExit()); }
        }
        System.out.println("PASS: " + soKiemTra + " kiểm tra tích hợp khuyến mãi và hóa đơn. Dữ liệu dự án không bị thay đổi.");
    }
}
