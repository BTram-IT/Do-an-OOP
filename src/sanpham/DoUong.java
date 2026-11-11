package sanpham;

import util.Ktradulieu;

import java.util.Scanner;

public class DoUong extends SanPham {
    private double dungTich;
    private String thuongHieu;
    private String huongVi;
    private String loaiDoUong;
    Scanner sc = new Scanner(System.in);

    // Constructor đầy đủ
    public DoUong(String maSP, String tenSP, double donGiaNhap, double donGiaBan, int soLuongTon,
                  String donViTinh, String nsx, String hsd, double phanTramThue,
                  double dungTich, String thuongHieu, String huongVi, String loaiDoUong,
                  String trangThaiDuyet, boolean daXoa) {
        super(maSP, tenSP, donGiaNhap, donGiaBan, soLuongTon, donViTinh, nsx, hsd, phanTramThue, trangThaiDuyet, daXoa);
        this.dungTich = dungTich;
        this.thuongHieu = thuongHieu;
        this.huongVi = huongVi;
        this.loaiDoUong = loaiDoUong;
        this.trangThaiDuyet = trangThaiDuyet;
        this.daXoa = daXoa;
    }

    public DoUong() {
        super();
        this.dungTich = 0;
        this.thuongHieu = "";
        this.huongVi = "";
        this.loaiDoUong = "";
        this.trangThaiDuyet = "";
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
        System.out.println("Đã thêm xong đồ uống: [" + this.maSP + "] " + this.tenSP);
    }

    public String toFileString() {
        return String.format("DOUONG;%s;%s;%.0f;%.0f;%d;%s;%s;%s;%.1f;%.1f;%s;%s;%s;%s;%b",
                this.maSP, this.tenSP, this.donGiaNhap, this.donGiaBan,
                this.soLuongTon, this.donViTinh, this.nsx, this.hsd,
                this.phanTramThue, this.dungTich, this.thuongHieu,
                this.huongVi, this.loaiDoUong, this.trangThaiDuyet, this.daXoa);
    }
    @Override
    public String toString() {
        return super.toString() +
                "\n   ↳ [Chi tiết]: Thương hiệu: " + thuongHieu +
                " | Loại: " + loaiDoUong +
                " | Hương vị: " + huongVi +
                " | Dung tích: " + (int)dungTich + " ml";
    }
}