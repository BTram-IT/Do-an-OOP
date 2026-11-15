import giaodien.Giaodien;
import hoadon.CTHDBan;
import hoadon.Hoadonban;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import person.DsKhachHang;
import person.NhanVien;
import sanpham.DSSanPham;
import sanpham.ThucAn;
import thanhtoan.ChuyenKhoan;
import thanhtoan.TienMat;
import util.Ktradulieu;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

/** Chạy trong thư mục riêng để các dữ liệu kiểm thử không ghi vào data/ của đồ án. */
public class ScannerProbe {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    public static void main(String[] args) {
        String input = String.join("\n",
                "nhanvien", "123456", "1", "0", "3",
                "quanly", "123456", "6", "0", "0",
                "giamdoc", "123456", "1", "0", "0",
                "0", "UI_DONE",
                "0", "INVOICE_MENU_DONE",
                "1", "1000", "CASH_DONE",
                "Vietcombank", "123456789", "Audit", "FT1234", "BANK_DONE", "");
        PrintStream report = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (Scanner sc = new Scanner(input);
             PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            Giaodien screen = new Giaodien();
            screen.batDau(sc);
            check(sc.nextLine().equals("UI_DONE"), "Login, nested menus and logout must share the supplied Scanner");
            check(!output.toString(StandardCharsets.UTF_8).contains("lỗi không mong muốn"), "Menus must complete without runtime errors");

            NhanVien employee = new NhanVien();
            employee.setMaND("NV901");
            employee.setTenND("Audit");
            Hoadonban invoice = new Hoadonban("HDB901", employee, LocalDate.now(), "DangXuLy", "", "KHACH_LE");
            screen.menuKiemTraHoaDonBan(sc, invoice);
            check(sc.nextLine().equals("INVOICE_MENU_DONE"), "Invoice menu must leave the shared Scanner open");

            ThucAn product = new ThucAn();
            product.setMaSP("TA901");
            product.setTenSP("Audit");
            product.setDonGiaNhap(100);
            product.setDonGiaBan(200);
            product.setSoLuongTon(5);
            product.setHSD(LocalDate.now(), LocalDate.now().plusDays(10));
            DSSanPham products = new DSSanPham();
            products.themSP(product);
            check(invoice.themCTHD(new CTHDBan("HDB901", "TA901", "Audit", 2, 200, 5, 0, 0, "KHONG"),
                    products, new DSKhuyenMai(), new DSSanPhamKhuyenMai()), "Prepare checkout");
            check(invoice.thanhToanHoaDon(sc, employee, new DsKhachHang(), products), "Checkout must forward the Scanner to cash payment");
            check(product.getSoLuongTon() == 3 && sc.nextLine().equals("CASH_DONE"), "Checkout must update stock and preserve remaining input");

            ChuyenKhoan transfer = new ChuyenKhoan();
            check(transfer.thanhToan(sc, 100), "Bank payment must consume the same Scanner");
            check(sc.nextLine().equals("BANK_DONE"), "Bank payment must not close the Scanner");

            try {
                Ktradulieu.luaChon(null, "", 0, 1);
                throw new AssertionError("Missing Scanner must be rejected");
            } catch (IllegalArgumentException expected) {
                checks++;
            }
            try {
                new TienMat().nhap(null);
                throw new AssertionError("Payment must not create a fallback Scanner");
            } catch (IllegalArgumentException expected) {
                checks++;
            }
        } finally {
            System.setOut(report);
        }
        report.println("PASS shared Scanner: " + checks + " checks");
    }
}
