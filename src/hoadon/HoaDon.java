package hoadon;

import java.time.LocalDateTime;
import java.util.Scanner;
import util.Ktradulieu;

public abstract class HoaDon implements IHoaDon{
    private String maHD;
    private String maNV;
    private LocalDateTime thoigianin;

    public HoaDon() {
        this.maHD = "";
        this.maNV = "";
        this.thoigianin = LocalDateTime.now();
    }

    public HoaDon(String maHD, String maNV, LocalDateTime thoigianin) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.thoigianin = thoigianin;
    }

    public String getmaHoaDon() {
        return maHD;
    }
    public void setMaHoaDon() {
        if (maHD == null || maHD.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống!");
        }
        this.maHD = maHD;
    }

    public void setMaNV(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống!");
        }
        this.maNV = maNV.trim();
    }

    public LocalDateTime getThoigianin() {
        return thoigianin;
    }

    public void setThoigianin(LocalDateTime thoigianin) {
        if (thoigianin == null) {
            throw new IllegalArgumentException("Thời gian in không được để trống!");
        }
        this.thoigianin = thoigianin;
    }

    public void thanhTien() {
    }
    public boolean isDaThanhToan() {
        return false;
    }

    public static String nhapMaHD(Scanner sc) {
        return Ktradulieu.nhapChuoi(sc, "Nhập mã hóa đơn: ", "", "Mã hóa đơn không được để trống!").toUpperCase();
    }

    public static String nhapMaNV(Scanner sc) {
        return Ktradulieu.nhapChuoi(sc, "Nhập mã nhân viên: ", ".+", "Mã nhân viên không được để trống!").toUpperCase();
    }

    public abstract void inhoadon() ;

    public abstract long Tongtienhoadon();

    public long tinhTongTien() {
        return Tongtienhoadon();
    }
}
