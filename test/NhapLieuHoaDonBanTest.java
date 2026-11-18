import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;
import hoadon.*;
import sanpham.*;
import person.NhanVien;
import khuyenmai.*;

public class NhapLieuHoaDonBanTest {
    static class Kho extends DSSanPham {
        @Override public void ghiFile(String path) { }
    }
    static Scanner sauLoiNhac(ByteArrayOutputStream log, String prompt, String keys) {
        return new Scanner(new ByteArrayInputStream(keys.getBytes(StandardCharsets.UTF_8)) {
            @Override public synchronized int read(byte[] b, int offset, int length) {
                if (!log.toString(StandardCharsets.UTF_8).contains(prompt))
                    throw new AssertionError("Đọc bàn phím trước khi hiện lời nhắc: " + prompt);
                return super.read(b, offset, length);
            }
        }, StandardCharsets.UTF_8);
    }
    public static void main(String[] args) {
        PrintStream original = System.out;
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(log, true, StandardCharsets.UTF_8)) {
            System.setOut(output);
            Kho kho = new Kho();
            LocalDate today = LocalDate.now();
            kho.themSP(new ThucAn("TA001", "Bánh thử", 5000, 10000, 10, "gói", today,
                    today.plusDays(90), 0, "Bột", "Khô", "Việt Nam", false, false));
            NhanVien nv = new NhanVien("NV001", "Nhân viên", "nv", "123456", "0912345678", today, false);
            Hoadonban hd = new Hoadonban("HDB001", nv, today, "DangXuLy", "", "KHACH_LE");
            DSKhuyenMai km = new DSKhuyenMai();
            DSSanPhamKhuyenMai links = new DSSanPhamKhuyenMai();
            hd.themCTHD(new CTHDBan("HDB001", "TA001", "Bánh thử", 1, 10000, 0, 0, 0, "KHONG"), kho, km, links);
            log.reset();
            hd.suaSoLuongSanPham(sauLoiNhac(log, "Nhập mã sản phẩm cần sửa số lượng:", "TA001\n2\n"), kho, km, links);
            if (hd.getDsChiTiet()[0].getSoLuong() != 2) throw new AssertionError("Sửa số lượng thất bại");
            log.reset();
            hd.xoaSanPham(sauLoiNhac(log, "Nhập mã sản phẩm cần xóa:", "TA001\n1\n"));
            if (hd.getDsChiTiet().length != 0) throw new AssertionError("Xóa thất bại");
        } finally { System.setOut(original); }
        System.out.println("PASS: sửa/xóa hiện lời nhắc trước khi đọc bàn phím, không cần nhập thêm dòng.");
    }
}
