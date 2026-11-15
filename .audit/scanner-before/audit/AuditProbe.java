import giaodien.Giaodien;
import hoadon.*;
import sanpham.*;
import person.*;
import thanhtoan.*;
import util.Ktradulieu;
import khuyenmai.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

public class AuditProbe {
    static PrintStream report = System.out;
    static int checks, failures;
    interface Task { boolean run() throws Exception; }
    static void check(String name, Task task) {
        checks++;
        try {
            boolean ok = task.run();
            if (!ok) failures++;
            report.println((ok ? "PASS " : "FAIL ") + name);
        } catch (Throwable e) {
            failures++;
            report.println("FAIL " + name + " " + e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }
    static boolean rejects(Task task) throws Exception {
        try { task.run(); return false; }
        catch (IllegalArgumentException | IllegalStateException e) { return true; }
    }
    static ThucAn product() {
        ThucAn p = new ThucAn(); p.setMaSP("TA999"); p.setTenSP("Audit");
        p.setDonGiaNhap(100); p.setDonGiaBan(200); p.setSoLuongTon(100);
        return p;
    }
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("menu")) {
            Giaodien g = new Giaodien();
            Field f = Giaodien.class.getDeclaredField("nguoiDangNhap"); f.setAccessible(true);
            f.set(g, args[2].equals("director") ? new Giamdoc() : args[2].equals("manager") ? new QuanLyCuaHang() : new NhanVien());
            try {
                Giaodien.class.getMethod(args[1]).invoke(g);
                System.out.println("AUDIT_RETURN");
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof NoSuchElementException) System.out.println("AUDIT_EOF");
                else { System.out.println("AUDIT_EXCEPTION " + e.getCause()); e.getCause().printStackTrace(); }
            }
            return;
        }
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        check("integer invalid retry", () -> Ktradulieu.nhapSoNguyen(new Scanner("abc\n2147483648\n-1\n0\n2\n"), "") == 2);
        check("double nonfinite retry", () -> Ktradulieu.nhapSoThuc(new Scanner("NaN\nInfinity\n1e309\n12.5\n"), "") == 12.5);
        check("strict shared date", () -> Ktradulieu.nhapNgay(new Scanner("31/02/2026\n29/02/2025\n28/02/2026\n"), "").equals(LocalDate.of(2026,2,28)));
        check("cash insufficient retry", () -> new TienMat().thanhToan(new Scanner("1\n100\n"), 100));
        check("loyalty points overflow rejected without mutation", () -> {
            Khachhang k = new Khachhang(); k.setDiemTichLuy(Integer.MAX_VALUE);
            boolean rejected=rejects(() -> { k.congDiem(1); return true; });
            return rejected && k.getDiemTichLuy()==Integer.MAX_VALUE;
        });
        check("percentage discount boundary", () -> {
            KhuyenmaiPhantram k = new KhuyenmaiPhantram(); k.setPhanTramGiamGia(100);
            return k.tinhTienGiam(1000)==1000 && rejects(() -> { k.setPhanTramGiamGia(Double.NaN); return true; });
        });
        check("direct discount clamped", () -> {
            KhuyenmaiTructiep k = new KhuyenmaiTructiep(); k.setSoTienGiam(2000);
            return k.tinhTongTienGiam(1000,2)==2000;
        });
        check("buy gift normal and overflow", () -> {
            KhuyenmaiMuatang k = new KhuyenmaiMuatang(); k.setDieuKienMuaTang(2,1);
            boolean normal=k.tinhSoLuongTang(5)==2 && k.tinhTongSoLuongXuatKho(5)==7;
            k.setDieuKienMuaTang(1,Integer.MAX_VALUE);
            return normal && rejects(() -> { k.tinhSoLuongTang(2); return true; });
        });
        check("sales cash checkout and no double debit", () -> {
            DSSanPham ds = new DSSanPham(); ThucAn p=product(); ds.themSP(p);
            NhanVien nv=new NhanVien(); nv.setMaND("NV999"); nv.setTenND("Audit");
            Hoadonban h=new Hoadonban("HDB999",nv,LocalDate.of(2026,11,14),"DangXuLy","","KHACH_LE");
            h.themCTHD(new CTHDBan("HDB999","TA999","Audit",2,200,5,0,0,"KHONG"),ds,new DSKhuyenMai(),new DSSanPhamKhuyenMai());
            return h.thanhToanHoaDon(new Scanner("1\n1000\n"),nv,new DsKhachHang(),ds)
                && p.getSoLuongTon()==98 && !h.thanhToanHoaDon(new Scanner(""),nv,new DsKhachHang(),ds) && p.getSoLuongTon()==98;
        });
        check("product stock overflow rejected without mutation", () -> {
            ThucAn p = product(); p.setSoLuongTon(Integer.MAX_VALUE);
            boolean ok = rejects(() -> { p.congTonKho(1); return true; });
            return ok && p.getSoLuongTon() == Integer.MAX_VALUE;
        });
        for (double v : new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            check("product reject price " + v, () -> rejects(() -> { product().setDonGiaBan(v); return true; }));
            check("product reject tax " + v, () -> rejects(() -> { product().setPhanTramThue(v); return true; }));
        }
        check("product impossible date rejected", () -> {
            try {
                product().setHSD(LocalDate.of(2026,2,1), Ktradulieu.chuyenChuoiThanhNgay("31/02/2026"));
                return false;
            } catch (java.time.format.DateTimeParseException e) { return true; }
        });
        check("invalid expiry fails closed", () -> {
            try {
                new ThucAn("TA999", "Audit", 100, 200, 100, "box", LocalDate.of(2026,1,1), Ktradulieu.chuyenChuoiThanhNgay("garbage"), 5, "a", "b", "c", false, "Da duyet", false);
                return false;
            } catch (java.time.format.DateTimeParseException e) { return true; }
        });
        check("import parser rejects negative quantity", () -> CTHDonNhap.tuDongFile("HDN999;TA999;Audit;-1;100;5;01/01/2099") == null);
        check("import parser rejects NaN price", () -> CTHDonNhap.tuDongFile("HDN999;TA999;Audit;1;NaN;5;01/01/2099") == null);
        check("import parser rejects invalid tax", () -> CTHDonNhap.tuDongFile("HDN999;TA999;Audit;1;100;101;01/01/2099") == null);
        check("import parser rejects impossible date", () -> CTHDonNhap.tuDongFile("HDN999;TA999;Audit;1;100;5;31/02/2099") == null);
        check("import detail locale roundtrip", () -> {
            Locale old = Locale.getDefault();
            try { Locale.setDefault(Locale.GERMANY);
                CTHDonNhap c = new CTHDonNhap("HDN999", "TA999", "Audit", 1, 100, 5, LocalDate.of(2099,1,1));
                return CTHDonNhap.tuDongFile(c.toDongFile()) != null;
            } finally { Locale.setDefault(old); }
        });
        check("sales total finite for finite input", () -> Double.isFinite(new CTHDBan("HDB999", "TA999", "Audit", 2, Double.MAX_VALUE, 5, 0, 0, "KHONG").tinhThanhTien()));
        check("sales detail overflow guarded", () -> {
            try { new CTHDBan("HDB999", "TA999", "Audit", Integer.MAX_VALUE, 100, 5, 0, 1, "KHONG").getTongSoLuongXuatKho(); return false; }
            catch (ArithmeticException e) { return true; }
        });
        check("invoice payment persistence", () -> {
            NhanVien nv = new NhanVien(); nv.setMaND("NV999"); nv.setTenND("Audit");
            Hoadonban h = new Hoadonban("HDB999", nv, LocalDate.of(2026,11,14), "DaXuLyThanhCong", "", "KHACH_LE");
            h.themCTHDTuFile(new CTHDBan("HDB999", "TA999", "Audit", 2, 10000, 5, 0, 0, "KHONG"));
            h.khoiPhucThanhToanTuFile(1000, 20000, "TienMat");
            DSHoadon ds = new DSHoadon(); ds.themHD(h); ds.ghiFile("data/audit-payment.txt");
            DSHoadon loaded = new DSHoadon(); loaded.docFile("data/audit-payment.txt", null);
            Hoadonban x = (Hoadonban) loaded.timKiem("HDB999");
            return x != null && x.getTienDungDiem() == 1000 && x.getPhuongThucThanhToan() != null;
        });
        check("corrupt invoice file preserved", () -> {
            Path p = Paths.get("data/audit-corrupt.txt"); String original = "BROKEN;RECORD\n";
            Files.writeString(p, original); DSHoadon ds = new DSHoadon(); ds.docFile(p.toString(), null); ds.ghiFile(p.toString());
            return Files.readString(p).equals(original);
        });
        check("report impossible date rejected", () -> {
            Giaodien g = new Giaodien(); Method m = Giaodien.class.getDeclaredMethod("nhapNgay", Scanner.class, String.class); m.setAccessible(true);
            return m.invoke(g, new Scanner("31/02/2026\n01/03/2026\n"), "").equals(LocalDate.of(2026,3,1));
        });
        Random random = new Random(20261114);
        for (int i=0;i<10000;i++) {
            int n = random.nextInt(100000)+1; double price = random.nextInt(1000000)+1; double tax = random.nextInt(101);
            CTHDBan c = new CTHDBan("HDB999", "TA999", "Audit", n, price, tax, 0, 0, "KHONG");
            final double expected = n*price*(1+tax/100);
            final int j = i;
            check("sales arithmetic " + j, () -> Math.abs(c.tinhThanhTien()-expected) <= Math.max(0.01, expected*1e-12));
        }
        for (int n : new int[]{100,1000,2000}) {
            DSSanPham ds = new DSSanPham(); long start = System.nanoTime();
            for(int i=0;i<n;i++) { ThucAn p=product(); p.setMaSP("TA"+i); ds.themSP(p); }
            long insert = System.nanoTime()-start; start = System.nanoTime();
            for(int i=0;i<10000;i++) ds.timKiem("TA"+(i%n));
            report.printf(Locale.US,"BENCH products=%d insert_ms=%.3f lookup_10000_ms=%.3f%n",n,insert/1e6,(System.nanoTime()-start)/1e6);
        }
        report.println("SUMMARY checks="+checks+" failures="+failures);
    }
}
