package khuyenmai;

import java.time.LocalDate;
import java.util.Scanner;
import util.Ktradulieu;

public class KhuyenmaiPhantram extends Khuyenmai {

    private double phanTramGiamGia;
    private long mucGiamToiDa; // 0 = Không giới hạn trần


    public KhuyenmaiPhantram() {
        super();
        this.phanTramGiamGia = 0;
        this.mucGiamToiDa = 0;
        setLoaiKM("Giảm theo phần trăm");
    }


    public KhuyenmaiPhantram(String maKM, String loaiKM, LocalDate ngayBatDau, LocalDate ngayKetThuc, long mucChiToiThieu, double phanTramGiamGia, long mucGiamToiDa) {
        super(maKM, loaiKM, ngayBatDau, ngayKetThuc, mucChiToiThieu);
        this.phanTramGiamGia = phanTramGiamGia;
        this.mucGiamToiDa = mucGiamToiDa;
        if (loaiKM == null || loaiKM.trim().isEmpty()) {
            setLoaiKM("Giảm " + phanTramGiamGia + "%");
        }
    }


    @Override
    public void nhapThongTin(Scanner sc, DSKhuyenMai dsKM) {
        super.nhapThongTin(sc, dsKM);

        while (true) {
            try {
                double pt = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập phần trăm giảm giá (0 < % <= 100): ");
                setPhanTramGiamGia(pt);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                long trantoDa = (long) Ktradulieu.nhapSoThucKhongAm(sc, "Nhập mức giảm tối đa (0 = Không giới hạn): ");
                setMucGiamToiDa(trantoDa);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setLoaiKM("Giảm " + this.phanTramGiamGia + "%");
    }

    @Override
    public long tinhTienGiam(long giaBan) {
        if (giaBan <= 0 || phanTramGiamGia <= 0) {
            return 0;
        }

        long tienGiam = Math.round(giaBan * phanTramGiamGia / 100.0);
        if (mucGiamToiDa > 0) {
            tienGiam = Math.min(tienGiam, mucGiamToiDa);
        }
        return Math.min(tienGiam, giaBan);
    }

    @Override
    public long tinhTongTienGiam(long giaBan, int soLuong) {
        if (giaBan <= 0 || soLuong <= 0 || phanTramGiamGia <= 0) {
            return 0;
        }

        long thanhTien = Math.multiplyExact(giaBan, (long) soLuong);
        long tongGiam = Math.round(thanhTien * phanTramGiamGia / 100.0);

        if (mucGiamToiDa > 0) {
            tongGiam = Math.min(tongGiam, mucGiamToiDa);
        }
        return Math.min(tongGiam, thanhTien);
    }

    @Override
    public Khuyenmai taoKhuyenMaiRong() {
        return new KhuyenmaiPhantram();
    }

    @Override
    public String toDongFile() {
        return "PHANTRAM;" + super.toDongFile() + ";" + phanTramGiamGia + ";" + mucGiamToiDa;
    }

    public double getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    public void setPhanTramGiamGia(double phanTramGiamGia) {
        if (!Double.isFinite(phanTramGiamGia) || phanTramGiamGia <= 0 || phanTramGiamGia > 100) {
            throw new IllegalArgumentException("[Lỗi] Phần trăm giảm phải > 0 và <= 100! Nhận: " + phanTramGiamGia);
        }
        this.phanTramGiamGia = phanTramGiamGia;
    }

    public long getMucGiamToiDa() {
        return mucGiamToiDa;
    }

    public void setMucGiamToiDa(long mucGiamToiDa) {
        if (mucGiamToiDa < 0) {
            throw new IllegalArgumentException("[Lỗi] Mức giảm tối đa không được âm! Nhận: " + mucGiamToiDa);
        }
        this.mucGiamToiDa = mucGiamToiDa;
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Giảm: " + phanTramGiamGia + "%"
                + " | Tối đa: " + (mucGiamToiDa == 0 ? "Không giới hạn" : Ktradulieu.dinhDangTien(mucGiamToiDa));
    }
}