package sanpham;

import util.BangConsole;

import util.Ktradulieu;

import java.time.LocalDate;
import java.util.Scanner;

public class DoUong extends SanPham {
    private double dungTich;
    private String thuongHieu;
    private String huongVi;
    private String loaiDoUong;

    // Constructor đầy đủ
    public DoUong(String maSP, String tenSP, double donGiaNhap, double donGiaBan, int soLuongTon,
                  String donViTinh, LocalDate nsx, LocalDate hsd, double phanTramThue,
                  double dungTich, String thuongHieu, String huongVi, String loaiDoUong,
                  boolean daXoa) {
        this(maSP, tenSP, donGiaNhap, donGiaBan, soLuongTon, donViTinh, nsx, hsd, phanTramThue,
                dungTich, thuongHieu, huongVi, loaiDoUong, "", daXoa);
    }

    public DoUong(String maSP, String tenSP, double donGiaNhap, double donGiaBan, int soLuongTon,
                  String donViTinh, LocalDate nsx, LocalDate hsd, double phanTramThue,
                  double dungTich, String thuongHieu, String huongVi, String loaiDoUong,
                  String maNCC, boolean daXoa) {
        super(maSP, tenSP, donGiaNhap, donGiaBan, soLuongTon, donViTinh, nsx, hsd, phanTramThue, maNCC, daXoa);
        this.dungTich = dungTich;
        this.thuongHieu = thuongHieu;
        this.huongVi = huongVi;
        this.loaiDoUong = loaiDoUong;
        this.daXoa = daXoa;
    }

    public DoUong() {
        super();
        this.dungTich = 0;
        this.thuongHieu = "";
        this.huongVi = "";
        this.loaiDoUong = "";
        this.daXoa = false;
    }
    public double getDungTich() {
        return dungTich;
    }

    public void setDungTich(double dungTich) {
        if (dungTich <= 0) {
            throw new IllegalArgumentException("[Lỗi Đồ Uống] Dung tích phải lớn hơn 0 ml! Nhận: " + dungTich);
        }
        this.dungTich = dungTich;
    }

    public String getThuongHieu() {
        return thuongHieu;
    }

    public void setThuongHieu(String thuongHieu) {
        if (thuongHieu == null || thuongHieu.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi Đồ Uống] Thương hiệu không được để trống!");
        }
        this.thuongHieu = Ktradulieu.chuanHoaChuoi(thuongHieu);
    }

    public String getHuongVi() {
        return huongVi;
    }

    public void setHuongVi(String huongVi) {
        if (huongVi == null || huongVi.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi Đồ Uống] Hương vị không được để trống!");
        }
        this.huongVi = huongVi.trim();
    }

    public String getLoaiDoUong() {
        return loaiDoUong;
    }

    public void setLoaiDoUong(String loaiDoUong) {
        if (loaiDoUong == null || loaiDoUong.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi Đồ Uống] Loại đồ uống không được để trống!");
        }
        this.loaiDoUong = Ktradulieu.chuanHoaChuoi(loaiDoUong);
    }

    @Override
    public double tinhGiaBan() {
        return this.donGiaBan;
    }

    @Override
    public String getLoaiSanPham() {
        return "DoUong";
    }

    public void nhapThongTin(Scanner sc, DSSanPham ds) {
        super.nhapThongTin(sc, ds);

        this.setThuongHieu(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập thương hiệu: "));
        this.setLoaiDoUong(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập loại đồ uống: "));
        this.setHuongVi(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập hương vị: "));
        this.setDungTich(Ktradulieu.nhapSoThuc(sc, "Nhập dung tích (ml): "));
        System.out.println("Đã nhập thông tin đồ uống: [" + this.maSP + "] " + this.tenSP);
    }

    public String toFileString() {
        return String.format(java.util.Locale.US, "DOUONG;%s;%s;%.0f;%.0f;%d;%s;%s;%s;%.1f;%.1f;%s;%s;%s;%s;%b",
                this.maSP, this.tenSP, this.donGiaNhap, this.donGiaBan,
                this.soLuongTon, this.donViTinh, Ktradulieu.dinhDangNgay(this.nsx), Ktradulieu.dinhDangNgay(this.hsd),
                this.phanTramThue, this.dungTich, this.thuongHieu,
                this.huongVi, this.loaiDoUong, this.maNCC, this.daXoa);
    }
    @Override
    protected String layChiTietBang() {
        return BangConsole.thongTin(new String[]{"Thương hiệu", "Loại", "Hương vị", "Dung tích"},
                thuongHieu, loaiDoUong, huongVi, (int) dungTich + " ml");
    }
}
