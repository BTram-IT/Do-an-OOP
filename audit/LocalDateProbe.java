import hoadon.CTHDonNhap;
import hoadon.DSHoadon;
import hoadon.HoaDon;
import hoadon.HoadonNhap;
import hoadon.Hoadonban;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import sanpham.DoUong;
import sanpham.SanPham;
import sanpham.ThucAn;
import util.Ktradulieu;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Scanner;

/** Chạy trong thư mục kiểm thử riêng vì một số hàm nghiệp vụ ghi vào data/. */
public class LocalDateProbe {
    private static int checks;

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void rejects(Runnable action, String message) {
        try {
            action.run();
        } catch (DateTimeException | IllegalArgumentException | IllegalStateException expected) {
            checks++;
            return;
        }
        throw new AssertionError(message);
    }

    private static String capture(Runnable action) {
        PrintStream previous = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream captured = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(captured);
            action.run();
        } finally {
            System.setOut(previous);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        PrintStream report = System.out;
        try (PrintStream quiet = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(quiet);
            LocalDate today = LocalDate.now();
            LocalDate leapDay = LocalDate.of(2024, 2, 29);
            check(Ktradulieu.chuyenChuoiThanhNgay("29/02/2024").equals(leapDay), "Leap day must parse");
            check(Ktradulieu.dinhDangNgay(leapDay).equals("29/02/2024"), "Keep dd/MM/yyyy format");
            for (String invalid : new String[]{"31/02/2026", "29/02/2025", "31/04/2026", "garbage", ""}) {
                rejects(() -> Ktradulieu.chuyenChuoiThanhNgay(invalid), "Reject invalid date: " + invalid);
            }
            rejects(() -> Ktradulieu.chuyenChuoiThanhNgay(null), "Reject missing date");
            check(Ktradulieu.nhapNgay(new Scanner("31/02/2026\n29/02/2024\n"), "").equals(leapDay),
                    "Date input must retry after impossible date");

            ThucAn food = new ThucAn("TA901", "Audit", 100, 200, 3, "box",
                    today.minusDays(5), today, 5, "a", "b", "c", false, "Đã duyệt", false);
            rejects(() -> food.setHSD(today, today.minusDays(1)), "Expiry cannot precede production");
            rejects(() -> food.setHSD(null, today), "Production date is required");
            rejects(() -> food.setHSD(today, null), "Expiry date is required");
            check(food.getNsx().equals(today.minusDays(5)) && food.getHsd().equals(today),
                    "Rejected updates must preserve both dates");
            check(food.kiemTraHanSuDung() && food.getSoLuongTon() == 3, "Valid on expiry day");
            food.setHSD(today.minusDays(5), today.minusDays(1));
            check(!food.kiemTraHanSuDung() && food.getSoLuongTon() == 0 && food.getSoLuongHetHan() == 3,
                    "Expired stock must be removed");
            check(!food.kiemTraHanSuDung() && food.getSoLuongHetHan() == 3, "Do not remove stock twice");

            DoUong drink = new DoUong("DU901", "Audit", 100, 200, 2, "bottle",
                    today, today.plusDays(10), 5, 500, "a", "b", "c", "Đã duyệt", false);
            Path scratch = Files.createTempDirectory(Path.of("."), "localdate-");
            Path productFile = scratch.resolve("products.txt");
            Files.writeString(productFile, food.toFileString() + "\n" + drink.toFileString() + "\n", StandardCharsets.UTF_8);
            DSSanPham products = new DSSanPham();
            products.docFile(productFile.toString());
            check(products.getSoLuong() == 2, "Load both product types, including expired history");
            check(products.timKiem("TA901").getHsd().equals(food.getHsd()), "Restore food expiry");
            check(products.timKiem("DU901").getNsx().equals(today), "Restore drink production date");
            Path productCopy = scratch.resolve("products-copy.txt");
            products.ghiFile(productCopy.toString());
            check(Files.readAllLines(productCopy).equals(Files.readAllLines(productFile)), "Product file round trip");

            for (SanPham product : new SanPham[]{food, drink}) {
                String row = "HDN901;" + product.getMaSP() + ";Audit;1;100;5;"
                        + Ktradulieu.dinhDangNgay(product.getHsd()) + ";SPMOI;"
                        + (product instanceof ThucAn ? ((ThucAn) product).toFileString() : ((DoUong) product).toFileString());
                CTHDonNhap detail = CTHDonNhap.tuDongFile(row);
                check(detail != null && detail.getHanSuDung().equals(product.getHsd()), "Restore historical import expiry");
                check(detail.getSanPhamMoi().getNsx().equals(product.getNsx())
                                && detail.getSanPhamMoi().getHsd().equals(product.getHsd()),
                        "Restore nested product dates");
                check(CTHDonNhap.tuDongFile(detail.toDongFile()) != null, "Nested product round trip");
            }
            check(CTHDonNhap.tuDongFile("HDN901;TA901;Audit;1;100;5;31/02/2026") == null,
                    "Reject impossible import expiry");

            NhanVien employee = new NhanVien();
            employee.setMaND("NV901");
            employee.setTenND("Audit");
            Hoadonban sale = new Hoadonban("HDB901", employee, leapDay, "DangXuLy", "", "KHACH_LE");
            HoadonNhap purchase = new HoadonNhap("HDN901", employee, leapDay, "DaXuLyThanhCong", "", "NCC901");
            rejects(() -> sale.setNgayLap(null), "Invoice date is required");
            rejects(() -> purchase.setNgayLap(today), "Completed invoice date remains locked");
            check(new Hoadonban().getNgayLap().equals(today), "New invoice defaults to today");
            DSHoadon invoices = new DSHoadon();
            invoices.themHD(sale);
            invoices.themHD(purchase);
            Path invoiceFile = scratch.resolve("invoices.txt");
            invoices.ghiFile(invoiceFile.toString());
            DSHoadon loaded = new DSHoadon();
            loaded.docFile(invoiceFile.toString(), null);
            check(loaded.getSoLuong() == 2 && loaded.get(0).getNgayLap().equals(leapDay)
                            && loaded.get(1).getNgayLap().equals(leapDay),
                    "Restore both invoice dates");
            check(sale.inHoaDon().contains("29/02/2024") && purchase.inHoaDon().contains("29/02/2024"),
                    "Keep invoice display format");
            check(capture(loaded::xuatDanhSach).contains("29/02/2024"), "Keep list display format");
            check(loaded.suaHD(new Scanner("HDB901\n2\n31/02/2026\n01/03/2024\n"), new QuanLyCuaHang())
                            && loaded.timKiem("HDB901").getNgayLap().equals(LocalDate.of(2024, 3, 1)),
                    "Invoice editing uses strict date input");

            DSHoadon reportInvoices = new DSHoadon();
            for (int offset = -1; offset <= 2; offset++) {
                reportInvoices.themHD(new Hoadonban("HDB" + (950 + offset), employee, leapDay.plusDays(offset),
                        "DaXuLyThanhCong", "", "KHACH_LE"));
            }
            String statistics = capture(() -> new QuanLyCuaHang().thongKeTongQuanHoaDon(
                    reportInvoices, leapDay, leapDay.plusDays(1)));
            check(statistics.contains("Số hóa đơn bán thành công: 2"), "Report includes both date boundaries only");

            if (args.length > 0) {
                Path repository = Path.of(args[0]);
                Path existingProducts = repository.resolve("data/sanpham.txt");
                Path existingInvoices = repository.resolve("data/hoadon.txt");
                DSSanPham currentProducts = new DSSanPham();
                currentProducts.docFile(existingProducts.toString());
                long productCount = Files.readAllLines(existingProducts).stream()
                        .filter(line -> line.startsWith("THUCAN;") || line.startsWith("DOUONG;")).count();
                check(currentProducts.getSoLuong() == productCount, "Load existing product file");
                DSHoadon currentInvoices = new DSHoadon();
                currentInvoices.docFile(existingInvoices.toString(), null);
                long invoiceCount = Files.readAllLines(existingInvoices).stream()
                        .filter(line -> line.startsWith("HDBAN;") || line.startsWith("HDNHAP;")).count();
                check(currentInvoices.getSoLuong() == invoiceCount, "Load existing invoice file");
                for (HoaDon invoice : currentInvoices.getDanhSach()) {
                    check(invoice.getNgayLap() != null, "Existing invoice has a typed date");
                }
            }
        } finally {
            System.setOut(report);
        }
        report.println("PASS LocalDate migration: " + checks + " checks");
    }
}
