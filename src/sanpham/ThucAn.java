package sanpham;

import util.BangConsole;

import util.Ktradulieu;

import java.time.LocalDate;
import java.util.Scanner;

public class ThucAn extends SanPham {
    private String thanhPhan;
    private String baoQuan;
    private String xuatXu;
    private boolean laDoChay;

    // Constructor đầy đủ
    public ThucAn(String maSP, String tenSP, double donGiaNhap, double donGiaBan, int soLuongTon,
                  String donViTinh, LocalDate nsx, LocalDate hsd, double phanTramThue,
                  String thanhPhan, String baoQuan, String xuatXu, boolean laDoChay,
                  boolean daXoa) {
        this(maSP, tenSP, donGiaNhap, donGiaBan, soLuongTon, donViTinh, nsx, hsd, phanTramThue,
                thanhPhan, baoQuan, xuatXu, laDoChay, "", daXoa);
    }

    public ThucAn(String maSP, String tenSP, double donGiaNhap, double donGiaBan, int soLuongTon,
                  String donViTinh, LocalDate nsx, LocalDate hsd, double phanTramThue,
                  String thanhPhan, String baoQuan, String xuatXu, boolean laDoChay,
                  String maNCC, boolean daXoa) {
        super(maSP, tenSP, donGiaNhap, donGiaBan, soLuongTon, donViTinh, nsx, hsd, phanTramThue, maNCC, daXoa);
        this.thanhPhan = thanhPhan;
        this.baoQuan = baoQuan;
        this.xuatXu = xuatXu;
        this.laDoChay = laDoChay;
    }

    public ThucAn(){
        super();
        this.thanhPhan = "";
        this.baoQuan = "";
        this.xuatXu = "";
        this.laDoChay = false;
    }
    public String getThanhPhan() {
        return thanhPhan;
    }

    public void setThanhPhan(String thanhPhan) {
        if (thanhPhan == null || thanhPhan.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi Thức Ăn] Thành phần không được để trống!");
        }
        this.thanhPhan = thanhPhan.trim();
    }

    public String getBaoQuan() {
        return baoQuan;
    }

    public void setBaoQuan(String baoQuan) {
        if (baoQuan == null || baoQuan.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi Thức Ăn] Hướng dẫn bảo quản không được để trống!");
        }
        this.baoQuan = baoQuan.trim();
    }

    public String getXuatXu() {
        return xuatXu;
    }

    public void setXuatXu(String xuatXu) {
        if (xuatXu == null || xuatXu.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi Thức Ăn] Xuất xứ không được để trống!");
        }
        this.xuatXu = Ktradulieu.chuanHoaChuoi(xuatXu);
    }

    public boolean isLaDoChay() {
        return laDoChay;
    }

    public void setLaDoChay(boolean laDoChay) {
        this.laDoChay = laDoChay;
    }

    @Override
    public double tinhGiaBan() {
        return this.donGiaBan;
    }

    @Override
    public String getLoaiSanPham() {
        return "ThucAn";
    }

    public void nhapThongTin(Scanner sc, DSSanPham ds) {
        super.nhapThongTin(sc, ds);
        this.setXuatXu(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập xuất xứ: "));
        this.setBaoQuan(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập hướng dẫn bảo quản: "));
        this.setThanhPhan(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập thành phần: "));
        this.laDoChay = Ktradulieu.nhapXacNhan(sc, "Có phải đồ chay không?");
        System.out.println("Đã nhập thông tin thức ăn: [" + this.maSP + "] " + this.tenSP);
    }

    public String toFileString() {
        return String.format(java.util.Locale.US, "THUCAN;%s;%s;%.0f;%.0f;%d;%s;%s;%s;%.1f;%s;%s;%s;%b;%s;%b",
                this.maSP, this.tenSP, this.donGiaNhap, this.donGiaBan,
                this.soLuongTon, this.donViTinh, Ktradulieu.dinhDangNgay(this.nsx), Ktradulieu.dinhDangNgay(this.hsd),
                this.phanTramThue, this.thanhPhan, this.baoQuan,
                this.xuatXu, this.laDoChay, this.maNCC, this.daXoa);
    }

    @Override
    protected String layChiTietBang() {
        return BangConsole.thongTin(new String[]{"Xuất xứ", "Bảo quản", "Đồ chay", "Thành phần"},
                xuatXu, baoQuan, laDoChay ? "Có" : "Không", thanhPhan);
    }
}
