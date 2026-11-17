package hoadon;

import util.BangConsole;
import util.HienThiBang;

import util.Ktradulieu;

public abstract class CTHoaDon implements HienThiBang {

    protected String maHD;
    protected String maSP;
    protected String tenSP;
    protected int soLuong;
    protected double donGia;

    public CTHoaDon() {
        this.maHD = "";
        this.maSP = "";
        this.tenSP = "";
        this.soLuong = 0;
        this.donGia = 0.0;
    }


    public CTHoaDon(String maHD, String maSP, String tenSP, int soLuong, double donGia) {
        setMaHD(maHD);
        setMaSP(maSP);
        setTenSP(tenSP);
        setSoLuong(soLuong);
        setDonGia(donGia);
    }

    public abstract double tinhTienHang();
    public abstract double tinhTienThue();
    public abstract double tinhThanhTien();
    public abstract String toDongFile();


    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã hóa đơn không được để trống!");
        }
        this.maHD = Ktradulieu.chuanHoaMa(maHD);
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        if (maSP == null || maSP.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã sản phẩm không được để trống!");
        }
        this.maSP = Ktradulieu.chuanHoaMa(maSP);
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Tên sản phẩm không được để trống!");
        }
        this.tenSP = Ktradulieu.chuanHoaChuoi(tenSP);
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng phải > 0! Nhận: " + soLuong);
        }
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (!Double.isFinite(donGia) || donGia <= 0) {
            throw new IllegalArgumentException("[Lỗi] Đơn giá phải > 0! Nhận: " + donGia);
        }
        this.donGia = donGia;
    }

    public void kiemTraDuLieuFile() {
        for (String truong : new String[]{maHD, maSP, tenSP}) {
            if (truong == null || truong.isBlank() || truong.contains(";")
                    || truong.contains("\n") || truong.contains("\r")) {
                throw new IllegalArgumentException("Thông tin chi tiết hóa đơn không hợp lệ để lưu file!");
            }
        }
        if (soLuong <= 0 || !Double.isFinite(donGia) || donGia <= 0
                || !Double.isFinite(tinhThanhTien()) || tinhThanhTien() < 0) {
            throw new IllegalArgumentException("Số lượng, đơn giá hoặc thành tiền không hợp lệ!");
        }
    }

    @Override
    public String toString() {
        return BangConsole.tuDoiTuong(this);
    }
}
