package khuyenmai;

import java.time.LocalDate;
import java.util.Scanner;
import util.Ktradulieu;

public class KhuyenmaiTructiep extends Khuyenmai {

    private long soTienGiam;

    public KhuyenmaiTructiep() {
        super();
        this.soTienGiam = 0;
        setLoaiKM("Giảm giá trực tiếp");
    }

    public KhuyenmaiTructiep(String maKM, String loaiKM, LocalDate ngayBatDau, LocalDate ngayKetThuc, long mucChiToiThieu, long soTienGiam) {
        super(maKM, loaiKM, ngayBatDau, ngayKetThuc, mucChiToiThieu);
        this.soTienGiam = soTienGiam;
        if (loaiKM == null || loaiKM.trim().isEmpty()) {
            setLoaiKM("Giảm " + Ktradulieu.dinhDangTien(soTienGiam));
        }
    }

    @Override
    public void nhapThongTin(Scanner sc, DSKhuyenMai dsKM) {
        super.nhapThongTin(sc, dsKM);

        while (true) {
            try {
                long giam = (long) Ktradulieu.nhapSoThucKhongAm(sc, "Nhập số tiền giảm trực tiếp (VND): ");
                setSoTienGiam(giam);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setLoaiKM("Giảm " + Ktradulieu.dinhDangTien(this.soTienGiam));
    }

    @Override
    public long tinhTienGiam(long giaBan) {
        if (giaBan <= 0 || soTienGiam <= 0) {
            return 0;
        }
        return Math.min(giaBan, soTienGiam);
    }

    @Override
    public long tinhTongTienGiam(long giaBan, int soLuong) {
        if (giaBan <= 0 || soLuong <= 0 || soTienGiam <= 0) {
            return 0;
        }
        long giamMotSP = tinhTienGiam(giaBan);
        long thanhTien = Math.multiplyExact(giaBan, (long) soLuong);
        long tongGiam = Math.multiplyExact(giamMotSP, (long) soLuong);
        return Math.min(tongGiam, thanhTien);
    }

    @Override
    public Khuyenmai taoKhuyenMaiRong() {
        return new KhuyenmaiTructiep();
    }

    @Override
    public String toDongFile() {
        return "TRUCTIEP;" + super.toDongFile() + ";" + soTienGiam;
    }

    public long getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(long soTienGiam) {
        if (soTienGiam <= 0) {
            throw new IllegalArgumentException("[Lỗi] Số tiền giảm phải > 0! Nhận: " + soTienGiam);
        }
        this.soTienGiam = soTienGiam;
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Tiền giảm: " + Ktradulieu.dinhDangTien(soTienGiam);
    }
}