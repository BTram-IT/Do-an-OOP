package hoadon;

import java.util.Scanner;
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

    public CTHDBan() {
        super();
        this.phanTramThue = 0.0;
        this.tienGiam = 0.0;
        this.soLuongTang = 0;
        this.maKM = "KHONG";
    }

    public CTHDBan(String maHD, String maSP, String tenSP, int soLuongBan,
                   double donGiaBan, double phanTramThue, double tienGiam, int soLuongTang, String maKM) {
        super(maHD, maSP, tenSP, soLuongBan, donGiaBan);
        setPhanTramThue(phanTramThue);
        setTienGiam(tienGiam);
        setSoLuongTang(soLuongTang);
        setMaKM(maKM);
    }


    public int getSoLuongBan() { return getSoLuong(); }
    public void setSoLuongBan(int sl) { setSoLuong(sl); }

    public double getDonGiaBan() { return getDonGia(); }
    public void setDonGiaBan(double dg) { setDonGia(dg); }

    public boolean nhapThongTin(Scanner sc, String maHoaDon, DSSanPham dsSP,
                                DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM, double tongTienHienTai) {
        if (sc == null) sc = new Scanner(System.in);
        setMaHD(maHoaDon);
        SanPham spDuocChon = null;

        while (true) {
            System.out.print("Nhập mã sản phẩm cần bán (Ví dụ: TA001, DU001): ");
            String input = sc.nextLine().trim();

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);
                if (dsSP == null) {
                    System.out.println("[Lỗi] Danh mục sản phẩm kho hàng không tồn tại!");
                    return false;
                }

                spDuocChon = dsSP.timKiem(maChuan);
                if (spDuocChon == null) {
                    System.out.println("[Lỗi Logic] Mã sản phẩm '" + maChuan + "' không có trong kho hàng!");
                    continue;
                }

                if (!spDuocChon.kiemTraHanSuDung()) {
                    System.out.println("[Lỗi] Sản phẩm [" + spDuocChon.getTenSP() + "] ĐÃ HẾT HẠN SỬ DỤNG, không được phép bán!");
                    continue;
                }

                if (spDuocChon.getSoLuongTon() <= 0) {
                    System.out.println("[Lỗi Kho] Sản phẩm [" + spDuocChon.getTenSP() + "] ĐÃ HẾT HÀNG trong kho (Tồn: 0)!");
                    continue;
                }

                setMaSP(spDuocChon.getMaSP());
                setTenSP(spDuocChon.getTenSP());
                setDonGia(spDuocChon.getDonGiaBan());
                setPhanTramThue(spDuocChon.getPhanTramThue());
                System.out.println("-> Đã chọn: " + this.tenSP + " | Giá bán: " + Ktradulieu.dinhDangTien(this.donGia)
                        + " | Tồn kho hiện có: " + spDuocChon.getSoLuongTon());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                int sl = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng khách mua: ");
                if (sl > spDuocChon.getSoLuongTon()) {
                    System.out.println("[Lỗi Kho] Kho chỉ còn " + spDuocChon.getSoLuongTon() + " sản phẩm! Không đủ số lượng yêu cầu.");
                    continue;
                }
                setSoLuong(sl);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Tự động kiểm tra và áp dụng khuyến mãi
        this.tienGiam = 0;
        this.soLuongTang = 0;
        this.maKM = "KHONG";

        if (dsKM != null && dsSPKM != null) {
            long thanhTienTam = (long) (this.soLuong * this.donGia);
            long tongDonHangUocTinh = (long) (tongTienHienTai + thanhTienTam);

            for (int i = 0; i < dsKM.getSoLuong(); i++) {
                Khuyenmai km = dsKM.getDanhSach()[i];
                if (km != null && km.kiemTraApDung(this.maSP, tongDonHangUocTinh, dsSPKM)) {
                    this.maKM = km.getMaKM();
                    System.out.println("[Khuyến mãi] Sản phẩm được áp dụng: [" + km.getMaKM() + "] " + km.getLoaiKM());

                    if (km instanceof KhuyenmaiPhantram) {
                        this.tienGiam = km.tinhTongTienGiam((long) this.donGia, this.soLuong);
                        System.out.println("-> Được giảm giá: " + Ktradulieu.dinhDangTien(this.tienGiam));
                    } else if (km instanceof KhuyenmaiTructiep) {
                        this.tienGiam = km.tinhTongTienGiam((long) this.donGia, this.soLuong);
                        System.out.println("-> Được giảm trực tiếp: " + Ktradulieu.dinhDangTien(this.tienGiam));
                    } else if (km instanceof KhuyenmaiMuatang) {
                        this.soLuongTang = km.tinhSoLuongTang(this.soLuong);
                        if (this.soLuongTang > 0) {
                            System.out.println("-> Được tặng kèm: " + this.soLuongTang + " sản phẩm miễn phí!");
                        }
                    }
                    break;
                }
            }
        }

        return true;
    }


    @Override
    public double tinhTienHang() {
        return this.soLuong * this.donGia;
    }

    @Override
    public double tinhTienThue() {
        double tienSauGiam = Math.max(0, tinhTienHang() - this.tienGiam);
        return tienSauGiam * (this.phanTramThue / 100.0);
    }

    @Override
    public double tinhThanhTien() {
        return tinhTienHang() - this.tienGiam + tinhTienThue();
    }

    @Override
    public String toDongFile() {
        return String.format("%s;%s;%s;%d;%.0f;%.2f;%.0f;%d;%s",
                maHD, maSP, tenSP, soLuong, donGia, phanTramThue, tienGiam, soLuongTang, maKM);
    }

    public static CTHDBan tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty()) return null;
        String[] p = dong.split(";");
        if (p.length >= 9) {
            String maHD = p[0].trim();
            String maSP = p[1].trim();
            String tenSP = p[2].trim();
            int sl = Integer.parseInt(p[3].trim());
            double dg = Double.parseDouble(p[4].trim());
            double thue = Double.parseDouble(p[5].trim());
            double giam = Double.parseDouble(p[6].trim());
            int tang = Integer.parseInt(p[7].trim());
            String km = p[8].trim();
            return new CTHDBan(maHD, maSP, tenSP, sl, dg, thue, giam, tang, km);
        }
        return null;
    }

    public double getPhanTramThue() { return phanTramThue; }
    public void setPhanTramThue(double phanTramThue) {
        if (phanTramThue < 0 || phanTramThue > 100) throw new IllegalArgumentException("[Lỗi] Thuế phải từ 0% đến 100%!");
        this.phanTramThue = phanTramThue;
    }

    public double getTienGiam() { return tienGiam; }
    public void setTienGiam(double tienGiam) {
        if (tienGiam < 0) throw new IllegalArgumentException("[Lỗi] Tiền giảm không được âm!");
        this.tienGiam = tienGiam;
    }

    public int getSoLuongTang() { return soLuongTang; }
    public void setSoLuongTang(int soLuongTang) {
        if (soLuongTang < 0) throw new IllegalArgumentException("[Lỗi] Số lượng tặng không được âm!");
        this.soLuongTang = soLuongTang;
    }

    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) {
        this.maKM = (maKM != null && !maKM.trim().isEmpty()) ? Ktradulieu.chuanHoaMa(maKM) : "KHONG";
    }

    @Override
    public String toString() {
        String kmStr = !maKM.equals("KHONG") ? (" [KM: " + maKM + " - Giảm: " + Ktradulieu.dinhDangTien(tienGiam) + "]") : "";
        String tangStr = (soLuongTang > 0) ? (" [Tặng: " + soLuongTang + "]") : "";
        return String.format("%-8s | %-20s | SL: %-4d | Giá: %-12s | Thuế: %-4.1f%%%s%s | Thành tiền: %s",
                maSP, tenSP, soLuong, Ktradulieu.dinhDangTien(donGia), phanTramThue, kmStr, tangStr,
                Ktradulieu.dinhDangTien(tinhThanhTien()));
    }
}