package hoadon;

import java.util.Scanner;
import java.util.Locale;

import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import khuyenmai.Khuyenmai;
import khuyenmai.KhuyenmaiMuatang;
import khuyenmai.KhuyenmaiPhantram;
import khuyenmai.KhuyenmaiTructiep;

import sanpham.DSSanPham;
import sanpham.SanPham;
import util.Ktradulieu;

public class CTHDBan extends CTHoaDon {

    private double phanTramThue;
    private double tienGiam;
    private int soLuongTang;
    private String maKM;

    // ================= CONSTRUCTOR =================

    public CTHDBan() {
        super();
        this.phanTramThue = 0.0;
        this.tienGiam = 0.0;
        this.soLuongTang = 0;
        this.maKM = "KHONG";
    }

    public CTHDBan(String maHD, String maSP, String tenSP, int soLuongBan, double donGiaBan, double phanTramThue, double tienGiam, int soLuongTang, String maKM) {
        super(maHD, maSP, tenSP, soLuongBan, donGiaBan);
        setPhanTramThue(phanTramThue);
        setTienGiam(tienGiam);
        setSoLuongTang(soLuongTang);
        setMaKM(maKM);
    }

    public CTHDBan(CTHDBan ct) {
        this(ct.getMaHD(), ct.getMaSP(), ct.getTenSP(), ct.getSoLuongBan(), ct.getDonGiaBan(), ct.getPhanTramThue(), ct.getTienGiam(), ct.getSoLuongTang(), ct.getMaKM());
    }

    // ================= GETTER / SETTER SỐ LƯỢNG VÀ ĐƠN GIÁ =================

    public int getSoLuongBan() { return getSoLuong(); }
    public void setSoLuongBan(int sl) { setSoLuong(sl); }

    public double getDonGiaBan() { return getDonGia(); }
    public void setDonGiaBan(double dg) { setDonGia(dg); }

    // ================= NHẬP THÔNG TIN =================

    public boolean nhapThongTin(Scanner sc, String maHoaDon, DSSanPham dsSP, DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {
        if (sc == null) sc = new Scanner(System.in);
        if (dsSP == null) {
            System.out.println("[Lỗi] Danh mục sản phẩm không tồn tại!");
            return false;
        }

        setMaHD(maHoaDon);
        SanPham spDuocChon = null;

        // BƯỚC 1: CHỌN SẢN PHẨM
        while (true) {
            System.out.print("Nhập mã sản phẩm cần bán (TA001, DU001 - Nhập 0 để hủy): ");
            String input = sc.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("Đã hủy chọn sản phẩm.");
                return false;
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);
                spDuocChon = dsSP.timKiem(maChuan);
                if (spDuocChon == null) {
                    System.out.println("[Lỗi] Không tìm thấy sản phẩm!");
                    continue;
                }
                if (!spDuocChon.kiemTraHanSuDung()) {
                    System.out.println("[Lỗi] Sản phẩm [" + spDuocChon.getTenSP() + "] đã hết hạn sử dụng!");
                    continue;
                }
                if (spDuocChon.getSoLuongTon() <= 0) {
                    System.out.println("[Lỗi] Sản phẩm đã hết hàng trong kho!");
                    continue;
                }

                setMaSP(spDuocChon.getMaSP());
                setTenSP(spDuocChon.getTenSP());
                setDonGia(spDuocChon.getDonGiaBan());
                setPhanTramThue(spDuocChon.getPhanTramThue());

                System.out.println("-> Đã chọn: " + getTenSP() + " | Giá bán: " + Ktradulieu.dinhDangTien(getDonGiaBan()) + " | Tồn kho: " + spDuocChon.getSoLuongTon());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // BƯỚC 2: NHẬP SỐ LƯỢNG MUA
        while (true) {
            try {
                long slNhap = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng khách mua: ");
                if (slNhap <= 0 || slNhap > Integer.MAX_VALUE) {
                    System.out.println("[Lỗi] Số lượng không hợp lệ!");
                    continue;
                }
                if (slNhap > spDuocChon.getSoLuongTon()) {
                    System.out.println("[Lỗi Kho] Kho chỉ còn " + spDuocChon.getSoLuongTon() + " sản phẩm!");
                    continue;
                }
                setSoLuongBan((int) slNhap);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // BƯỚC 3: RESET KHUYẾN MÃI. Việc áp dụng khuyến mãi do Hoadonban.themCTHD thực hiện
        // (một lần duy nhất, tránh in thông báo trùng lặp).
        this.tienGiam = 0;
        this.soLuongTang = 0;
        this.maKM = "KHONG";

        return true;
    }

    // ================= ÁP DỤNG KHUYẾN MÃI =================

    public boolean apDungKhuyenMai(DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM, SanPham sp) {
        this.tienGiam = 0;
        this.soLuongTang = 0;
        this.maKM = "KHONG";

        if (dsKM == null || dsSPKM == null || sp == null) return false;
        Khuyenmai[] danhSachKM = dsKM.getDanhSach();
        if (danhSachKM == null) return false;

        int soLuongMua = getSoLuongBan();
        if (soLuongMua <= 0) return false;

        long giaBan = Math.round(getDonGiaBan());
        if (giaBan <= 0) return false;

        int conLai = sp.getSoLuongTon() - soLuongMua;
        if (conLai < 0) return false;

        Khuyenmai kmTotNhat = null;
        long giaTriTotNhat = 0;
        long tienGiamTotNhat = 0;
        int soLuongTangTotNhat = 0;

        for (Khuyenmai km : danhSachKM) {
            if (km == null || !km.kiemTraApDung(getMaSP(), dsSPKM)) continue;

            long giaTriUuDai = 0;
            long tienGiamUngVien = 0;
            int soLuongTangUngVien = 0;

            if (km instanceof KhuyenmaiMuatang) {
                KhuyenmaiMuatang kmMuaTang = (KhuyenmaiMuatang) km;
                int soLuongTangLyThuyet = Math.max(0, kmMuaTang.tinhSoLuongTang(soLuongMua));
                soLuongTangUngVien = Math.min(soLuongTangLyThuyet, conLai);
                try {
                    giaTriUuDai = Math.multiplyExact(giaBan, (long) soLuongTangUngVien);
                } catch (ArithmeticException e) {
                    giaTriUuDai = Long.MAX_VALUE;
                }
            } else if (km instanceof KhuyenmaiPhantram || km instanceof KhuyenmaiTructiep) {
                tienGiamUngVien = km.tinhTongTienGiam(giaBan, soLuongMua);
                giaTriUuDai = tienGiamUngVien;
            } else {
                continue;
            }

            if (giaTriUuDai > giaTriTotNhat) {
                giaTriTotNhat = giaTriUuDai;
                kmTotNhat = km;
                tienGiamTotNhat = tienGiamUngVien;
                soLuongTangTotNhat = soLuongTangUngVien;
            }
        }

        if (kmTotNhat == null || giaTriTotNhat <= 0) {
            return false;
        }

        this.maKM = kmTotNhat.getMaKM();
        this.tienGiam = tienGiamTotNhat;
        this.soLuongTang = soLuongTangTotNhat;

        System.out.println("\n[Khuyến mãi được áp dụng]");
        System.out.println("Mã khuyến mãi: " + kmTotNhat.getMaKM());
        System.out.println("Loại khuyến mãi: " + kmTotNhat.getLoaiKM());
        if (soLuongTangTotNhat > 0) {
            System.out.println("Số lượng mua: " + soLuongMua);
            System.out.println("Số lượng được tặng: " + soLuongTangTotNhat);
            if (soLuongTangTotNhat < ((KhuyenmaiMuatang) kmTotNhat).tinhSoLuongTang(soLuongMua)) {
                System.out.println("[Lưu ý] Số lượng hàng tặng đã được giới hạn theo tồn kho thực tế.");
            }
        } else {
            System.out.println("Tiền được giảm: " + Ktradulieu.dinhDangTien(this.tienGiam));
        }

        return true;
    }

    // ================= SỐ LƯỢNG XUẤT KHO & TÍNH TIỀN =================

    public int getTongSoLuongXuatKho() {
        return Math.addExact(getSoLuongBan(), getSoLuongTang());
    }

    @Override
    public double tinhTienHang() {
        return getSoLuongBan() * getDonGiaBan();
    }

    public double tinhTienGiamHieuLuc() {
        return Math.min(Math.max(0, tienGiam), tinhTienHang());
    }

    @Override
    public double tinhTienThue() {
        double tienSauGiam = Math.max(0, tinhTienHang() - tinhTienGiamHieuLuc());
        return tienSauGiam * (phanTramThue / 100.0);
    }

    @Override
    public double tinhThanhTien() {
        return tinhTienHang() - tinhTienGiamHieuLuc() + tinhTienThue();
    }

    // ================= GHI & ĐỌC FILE =================

    @Override
    public String toDongFile() {
        return String.format(Locale.US, "%s;%s;%s;%d;%.0f;%.2f;%.0f;%d;%s",
                maHD, maSP, tenSP, getSoLuongBan(), getDonGiaBan(), phanTramThue, tienGiam, soLuongTang, maKM);
    }

    public static CTHDBan tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty()) return null;
        try {
            String[] p = dong.split(";", -1);
            if (p.length != 9) return null;
            return new CTHDBan(
                    p[0].trim(), p[1].trim(), p[2].trim(),
                    Integer.parseInt(p[3].trim()),
                    Ktradulieu.docSoThuc(p[4]),
                    Ktradulieu.docSoThuc(p[5]),
                    Ktradulieu.docSoThuc(p[6]),
                    Integer.parseInt(p[7].trim()),
                    p[8].trim()
            );
        } catch (RuntimeException e) {
            System.out.println("[Lỗi] Không thể đọc chi tiết hóa đơn bán: " + e.getMessage());
            return null;
        }
    }

    // ================= GETTER / SETTER =================

    public double getPhanTramThue() { return phanTramThue; }
    public void setPhanTramThue(double phanTramThue) {
        if (!Double.isFinite(phanTramThue) || phanTramThue < 0 || phanTramThue > 100) {
            throw new IllegalArgumentException("[Lỗi] Thuế phải từ 0% đến 100%!");
        }
        this.phanTramThue = phanTramThue;
    }

    public double getTienGiam() { return tienGiam; }
    public void setTienGiam(double tienGiam) {
        if (!Double.isFinite(tienGiam) || tienGiam < 0) {
            throw new IllegalArgumentException("[Lỗi] Tiền giảm không được âm hoặc không hợp lệ!");
        }
        this.tienGiam = tienGiam;
    }

    public int getSoLuongTang() { return soLuongTang; }
    public void setSoLuongTang(int soLuongTang) {
        if (soLuongTang < 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng tặng không được âm!");
        }
        this.soLuongTang = soLuongTang;
    }

    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) {
        this.maKM = (maKM != null && !maKM.trim().isEmpty()) ? Ktradulieu.chuanHoaMa(maKM) : "KHONG";
    }

    // ================= TOSTRING =================

    @Override
    public String toString() {
        String kmStr = !maKM.equals("KHONG") ? " [KM: " + maKM + " - Giảm: " + Ktradulieu.dinhDangTien(tienGiam) + "]" : "";
        String tangStr = soLuongTang > 0 ? " [Tặng: " + soLuongTang + "]" : "";
        return String.format(Locale.US, "%-8s | %-20s | SL: %-4d | Giá: %-12s | Thuế: %-4.1f%%%s%s | Thành tiền: %s",
                maSP, tenSP, getSoLuongBan(), Ktradulieu.dinhDangTien(getDonGiaBan()),
                phanTramThue, kmStr, tangStr, Ktradulieu.dinhDangTien(tinhThanhTien()));
    }
}