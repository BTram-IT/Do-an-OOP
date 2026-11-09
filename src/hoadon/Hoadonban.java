package hoadon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import lohang.LoXuat;
import sanpham.DSSanPham;
import sanpham.SanPham;
import thanhtoan.ChuyenKhoan;
import thanhtoan.ThanhToan;
import thanhtoan.TienMat;
import util.Ktradulieu;

public class Hoadonban extends HoaDon {
    private String MaKH;
    private ArrayList<CTHoaDon> dsChiTietHoaDon;
    private ThanhToan thanhtoan;
    private ArrayList<LoXuat> cacLoDaXuat;
    private DSSanPham dsSanPham;
    private DSSanPhamKhuyenMai dsSPKM;
    private DSKhuyenMai dsKM;
    private boolean daThanhToanNapTuFile;

    public Hoadonban() {
        super();
        MaKH = "";
        dsChiTietHoaDon = new ArrayList<>();
        cacLoDaXuat = new ArrayList<>();
        thanhtoan = null;
        dsSanPham = null;
        dsSPKM = null;
        dsKM = null;
        daThanhToanNapTuFile = false;
    }

    public Hoadonban(String maKH, ArrayList<CTHoaDon> ds, ThanhToan tt) {
        this();
        setMaKH(maKH);
        setCTHoaDon(ds);
        thanhtoan = tt;
    }

    public Hoadonban(String maKH, ArrayList<CTHoaDon> ds, ThanhToan tt,
                     DSSanPham sp, DSSanPhamKhuyenMai spkm, DSKhuyenMai km) {
        this(maKH, ds, tt);
        dsSanPham = sp;
        dsSPKM = spkm;
        dsKM = km;
    }

    public Hoadonban(String maHD, String maNV, LocalDateTime thoiGian,
                     String maKH, ArrayList<CTHoaDon> ds, ThanhToan tt) {
        this();
        setMaHD(maHD);
        setMaNV(maNV);
        setThoigianin(thoiGian);
        setMaKH(maKH);
        setCTHoaDon(ds);
        thanhtoan = tt;
    }

    public Hoadonban(String maHD, String maNV, LocalDateTime thoiGian,
                     String maKH, ArrayList<CTHoaDon> ds, ThanhToan tt,
                     DSSanPham sp, DSSanPhamKhuyenMai spkm, DSKhuyenMai km) {
        this(maHD, maNV, thoiGian, maKH, ds, tt);
        dsSanPham = sp;
        dsSPKM = spkm;
        dsKM = km;
    }

    @Override
    public void setMaHD(String maHD) {
        super.setMaHD(maHD);
        if (dsChiTietHoaDon != null) {
            for (CTHoaDon ct : dsChiTietHoaDon) {
                if (ct != null) ct.setMaHD(getMaHD());
            }
        }
    }

    public void Nhap(Scanner sc) {
        if (sc == null)
            throw new IllegalArgumentException("Scanner không hợp lệ!");

        if (getMaHD() == null || getMaHD().trim().isEmpty()) {
            setMaHD(Ktradulieu.nhapChuoi(
                    sc, "Nhập mã hóa đơn: ", ".+",
                    "Mã hóa đơn không được để trống!"));
        }

        if (getMaNV() == null || getMaNV().trim().isEmpty()) {
            setMaNV(Ktradulieu.nhapChuoi(
                    sc, "Nhập mã nhân viên lập hóa đơn: ", ".+",
                    "Mã nhân viên không được để trống!"));
        }

        setMaKH(Ktradulieu.nhapChuoi(
                sc, "Nhập mã khách hàng: ", ".+",
                "Mã khách hàng không được để trống!"));
    }

    private boolean daChotTatCaChiTiet() {
        if (dsChiTietHoaDon == null || dsChiTietHoaDon.isEmpty())
            return false;

        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (ct == null || !ct.isDaChotThanhToan())
                return false;
        }
        return true;
    }

    public long tinhTongTienTruocKM() {
        long tong = 0;
        if (dsChiTietHoaDon == null) return 0;

        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (ct != null)
                tong = Math.addExact(tong, ct.TinhTienHang1SP());
        }
        return tong;
    }

    public long tinhTongTienSauKM() {
        long tong = 0;
        if (dsChiTietHoaDon == null) return 0;

        long truocKM = tinhTongTienTruocKM();

        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (ct == null) continue;

            long tien;
            if (ct.isDaChotThanhToan()) {
                tien = Math.subtractExact(
                        ct.TinhTienHang1SP(),
                        ct.getTienGiamThucTe());
            } else {
                tien = ct.tinhTienSauKM(dsSPKM, dsKM, truocKM);
            }

            tong = Math.addExact(tong, tien);
        }
        return tong;
    }

    public long Tongthuedonhang(DSSanPham dssanpham) {
        if (dsChiTietHoaDon == null) return 0;

        if (daChotTatCaChiTiet()) {
            long tong = 0;
            for (CTHoaDon ct : dsChiTietHoaDon)
                tong = Math.addExact(tong, ct.getTienThueThucTe());
            return tong;
        }

        DSSanPham nguonSP = dssanpham != null ? dssanpham : dsSanPham;
        if (nguonSP == null)
            throw new IllegalStateException("Chưa có danh sách sản phẩm!");

        long tong = 0;
        long truocKM = tinhTongTienTruocKM();

        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (ct == null) continue;

            SanPham sp = nguonSP.timTheoMa(ct.getMaSP());
            if (sp == null)
                throw new IllegalStateException(
                        "Không tìm thấy sản phẩm: " + ct.getMaSP());

            tong = Math.addExact(
                    tong,
                    ct.tinhTienThue(sp, dsSPKM, dsKM, truocKM));
        }
        return tong;
    }

    @Override
    public long Tongtienhoadon() {
        if (daChotTatCaChiTiet()) {
            long tong = 0;
            for (CTHoaDon ct : dsChiTietHoaDon)
                tong = Math.addExact(tong, ct.getTongTienSauThue());
            return tong;
        }

        if (dsSanPham == null)
            throw new IllegalStateException("Chưa có danh sách sản phẩm!");

        return Math.addExact(
                tinhTongTienSauKM(),
                Tongthuedonhang(dsSanPham));
    }

    @Override
    public long tinhTongTien() {
        return Tongtienhoadon();
    }

    public double getTongTien() {
        return (double) Tongtienhoadon();
    }

    public void themChiTiet(CTHoaDon ct) {
        if (ct == null)
            throw new IllegalArgumentException("Chi tiết hóa đơn không hợp lệ!");

        if (dsChiTietHoaDon == null)
            dsChiTietHoaDon = new ArrayList<>();

        if (getMaHD() != null && !getMaHD().isEmpty())
            ct.setMaHD(getMaHD());

        dsChiTietHoaDon.add(ct);
    }

    public void themGioHang(CTGioHang gh) {
        if (gh == null)
            throw new IllegalArgumentException("Giỏ hàng không hợp lệ!");

        themChiTiet(new CTHoaDon(
                gh.getMaSP(),
                gh.getTenSP(),
                gh.getSoluong(),
                gh.getDonGia(),
                getMaHD()));
    }

    public void nhapThanhToan(Scanner sc, long tongTien) {
        if (sc == null)
            throw new IllegalArgumentException("Scanner không hợp lệ!");

        if (tongTien < 0)
            throw new IllegalArgumentException("Tổng tiền không hợp lệ!");

        if (dsChiTietHoaDon == null || dsChiTietHoaDon.isEmpty())
            throw new IllegalStateException("Hóa đơn chưa có sản phẩm!");

        if (isDaThanhToan())
            throw new IllegalStateException("Hóa đơn đã thanh toán!");

        if (dsSanPham == null)
            throw new IllegalStateException("Chưa có danh sách sản phẩm!");

        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (ct == null)
                throw new IllegalStateException("Chi tiết hóa đơn không hợp lệ!");

            if (dsSanPham.timTheoMa(ct.getMaSP()) == null)
                throw new IllegalStateException(
                        "Không tìm thấy sản phẩm: " + ct.getMaSP());
        }

        long tongTienThucTe = Tongtienhoadon();
        if (tongTien != tongTienThucTe)
            throw new IllegalArgumentException(
                    "Số tiền thanh toán không khớp với tổng tiền hóa đơn!");

        thanhtoan = null;

        while (thanhtoan == null) {
            System.out.println("\n===== PHƯƠNG THỨC THANH TOÁN =====");
            System.out.println("Số tiền cần thanh toán: "
                    + Ktradulieu.dinhDangTien(tongTien));
            System.out.println("1. Tiền mặt");
            System.out.println("2. Chuyển khoản");

            int chon = Ktradulieu.luaChon(
                    sc, "Chọn phương thức (1-2): ", 1, 2);

            if (chon == 1) {
                ThanhToan tm = new TienMat(getMaHD());
                tm.setMaNV(getMaNV());
                if (tm.thanhToan(sc, tongTien)) {
                    thanhtoan = tm;
                    System.out.println("Thanh toán thành công!");
                    System.out.println("Tiền thối: "
                            + Ktradulieu.dinhDangTien(tm.getTienThoi()));
                } else {
                    System.out.println("Khách đưa chưa đủ tiền!");
                }
            } else {
                ChuyenKhoan ck = new ChuyenKhoan(getMaHD());
                ck.setMaNV(getMaNV());

                if (ck.thanhToan(sc, tongTien)) {
                    thanhtoan = ck;
                    System.out.println("Thanh toán chuyển khoản thành công!");
                } else {
                    System.out.println("Thanh toán chuyển khoản không hợp lệ!");
                }
            }
        }

        long truocKM = tinhTongTienTruocKM();

        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (!ct.isDaChotThanhToan()) {
                SanPham sp = dsSanPham.timTheoMa(ct.getMaSP());
                ct.chotThanhToan(sp, dsSPKM, dsKM, truocKM);
            }
        }
    }

    public void setCacLoDaXuat(ArrayList<LoXuat> ds) {
        cacLoDaXuat = ds == null
                ? new ArrayList<>()
                : new ArrayList<>(ds);
    }

    public ArrayList<LoXuat> getCacLoDaXuat() {
        return new ArrayList<>(cacLoDaXuat);
    }

    public String getMaKH() {
        return MaKH;
    }

    public void setMaKH(String maKH) {
        if (maKH == null || maKH.trim().isEmpty())
            throw new IllegalArgumentException(
                    "Mã khách hàng không được để trống!");

        MaKH = Ktradulieu.chuanHoaMa(maKH);
    }

    public ArrayList<CTHoaDon> getCTHoaDon() {
        return new ArrayList<>(dsChiTietHoaDon);
    }

    public void setCTHoaDon(ArrayList<CTHoaDon> ds) {
        dsChiTietHoaDon = ds == null
                ? new ArrayList<>()
                : new ArrayList<>(ds);

        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (ct != null && getMaHD() != null && !getMaHD().isEmpty())
                ct.setMaHD(getMaHD());
        }
    }

    public ThanhToan getThanhtoan() {
        return thanhtoan;
    }

    public void setThanhtoan(ThanhToan tt) {
        thanhtoan = tt;
        daThanhToanNapTuFile = false;
    }

    public DSSanPham getDsSanPham() {
        return dsSanPham;
    }

    public void setDsSanPham(DSSanPham ds) {
        dsSanPham = ds;
    }

    public DSSanPhamKhuyenMai getDsSPKM() {
        return dsSPKM;
    }

    public void setDsSPKM(DSSanPhamKhuyenMai ds) {
        dsSPKM = ds;
    }

    public DSKhuyenMai getDsKM() {
        return dsKM;
    }

    public void setDsKM(DSKhuyenMai ds) {
        dsKM = ds;
    }

    @Override
    public boolean isDaThanhToan() {
        return daThanhToanNapTuFile
                || (thanhtoan != null && thanhtoan.isThanhCong());
    }

    @Override
    public void inhoadon() {
        System.out.println("\n========== HÓA ĐƠN BÁN HÀNG ==========");
        System.out.println("Mã hóa đơn: " + getMaHD());
        System.out.println("Mã nhân viên: " + getMaNV());
        System.out.println("Thời gian: " + getThoigianin());
        System.out.println("Mã khách hàng: " + MaKH);

        if (dsChiTietHoaDon == null || dsChiTietHoaDon.isEmpty()) {
            System.out.println("Hóa đơn chưa có sản phẩm!");
            return;
        }

        for (CTHoaDon ct : dsChiTietHoaDon)
            if (ct != null) System.out.println(ct);

        System.out.println("--------------------------------------");
        System.out.println("Tiền hàng trước KM: "
                + Ktradulieu.dinhDangTien(tinhTongTienTruocKM()));
        System.out.println("Tiền hàng sau KM: "
                + Ktradulieu.dinhDangTien(tinhTongTienSauKM()));

        if (daChotTatCaChiTiet() || dsSanPham != null) {
            System.out.println("Thuế VAT: "
                    + Ktradulieu.dinhDangTien(Tongthuedonhang(dsSanPham)));
            System.out.println("TỔNG THANH TOÁN: "
                    + Ktradulieu.dinhDangTien(Tongtienhoadon()));
        }

        if (thanhtoan != null) {
            System.out.println("Phương thức: " + thanhtoan.getPhuongThuc());
            System.out.println("Chi tiết: " + thanhtoan);
        } else if (daThanhToanNapTuFile) {
            System.out.println("Trạng thái: ĐÃ THANH TOÁN (đọc từ file)");
        } else {
            System.out.println("Trạng thái: CHƯA THANH TOÁN");
        }

        System.out.println("======================================\n");
    }

    public String toDongFile() {
        StringBuilder sb = new StringBuilder();

        sb.append("HDB|")
                .append(getMaHD()).append("|")
                .append(getMaNV()).append("|")
                .append(getThoigianin()).append("|")
                .append(MaKH).append("|")
                .append(isDaThanhToan() ? "DA_TT" : "CHUA_TT")
                .append(";");

        for (int i = 0; i < dsChiTietHoaDon.size(); i++) {
            CTHoaDon ct = dsChiTietHoaDon.get(i);
            if (ct != null)
                sb.append(ct.toDongFile());

            if (i < dsChiTietHoaDon.size() - 1)
                sb.append("~");
        }

        return sb.toString();
    }

    public static Hoadonban tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty())
            return null;

        String[] parts = dong.split(";", -1);
        String[] meta = parts[0].split("\\|", -1);

        if (meta.length != 6 || !meta[0].equals("HDB"))
            throw new IllegalArgumentException(
                    "Sai định dạng hóa đơn bán!");

        if (!meta[5].equals("DA_TT") && !meta[5].equals("CHUA_TT"))
            throw new IllegalArgumentException(
                    "Trạng thái thanh toán không hợp lệ!");

        Hoadonban hdb = new Hoadonban();
        hdb.setMaHD(meta[1]);
        hdb.setMaNV(meta[2]);
        hdb.setThoigianin(LocalDateTime.parse(meta[3]));
        hdb.setMaKH(meta[4]);

        if (parts.length > 2)
            throw new IllegalArgumentException(
                    "Dữ liệu chi tiết hóa đơn không hợp lệ!");

        if (parts.length == 2 && !parts[1].trim().isEmpty()) {
            for (String item : parts[1].split("~", -1)) {
                if (item.trim().isEmpty()) continue;

                CTHoaDon ct = CTHoaDon.tuDongFile(item);
                if (ct == null)
                    throw new IllegalArgumentException(
                            "Không thể khôi phục chi tiết hóa đơn!");

                hdb.themChiTiet(ct);
            }
        }

        hdb.daThanhToanNapTuFile = "DA_TT".equals(meta[5]);
        return hdb;
    }
}