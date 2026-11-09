package khuyenmai;

import java.time.LocalDate;
import util.Ktradulieu;

public class KhuyenmaiTructiep extends Khuyenmai {
    private long soTienGiam;

    public KhuyenmaiTructiep() {
        super();
        this.soTienGiam = 0;
        setLoaiKM("Giảm giá trực tiếp");
    }

    public KhuyenmaiTructiep(
            String maKM,
            String loaiKM,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            long mucChiToiThieu,
            long soTienGiam) {
        super(maKM,
                (loaiKM == null || loaiKM.trim().isEmpty()) ? "Giảm giá trực tiếp" : loaiKM,
                ngayBatDau, ngayKetThuc, mucChiToiThieu);
        setSoTienGiam(soTienGiam);
    }

    @Override
    public void setKM() {
        super.setKM();

        do {
            soTienGiam = Ktradulieu.nhapSoNguyen(
                    sc, "Nhập số tiền giảm: ");

            if (soTienGiam <= 0) {
                System.out.println("Số tiền giảm phải lớn hơn 0!");
            }
        } while (soTienGiam <= 0);
    }

    @Override
    public long tinhTienGiam(long giaBan) {
        if (giaBan <= 0 || soTienGiam <= 0) {
            return 0;
        }
        return Math.min(giaBan, soTienGiam);
    }

    @Override
    public Khuyenmai taoKhuyenMaiRong() {
        return new KhuyenmaiTructiep();
    }

    public long getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(long soTienGiam) {
        if (soTienGiam <= 0) {
            throw new IllegalArgumentException(
                    "Số tiền giảm phải lớn hơn 0!");
        }
        this.soTienGiam = soTienGiam;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nSố tiền giảm: " + soTienGiam;
    }
}
