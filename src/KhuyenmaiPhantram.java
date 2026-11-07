import java.time.LocalDate;

public class KhuyenmaiPhantram extends Khuyenmai {
    private double phanTramGiamGia;
    private long mucGiamToiDa;

    public KhuyenmaiPhantram() {
        super();
        this.phanTramGiamGia = 0;
        this.mucGiamToiDa = 0;
        setLoaiKM("Giảm theo phần trăm");
    }

    public KhuyenmaiPhantram(
            String maKM,
            String loaiKM,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            long mucChiToiThieu,
            double phanTramGiamGia,
            long mucGiamToiDa) {
        super(maKM,
                (loaiKM == null || loaiKM.trim().isEmpty()) ? "Giảm theo phần trăm" : loaiKM,
                ngayBatDau, ngayKetThuc, mucChiToiThieu);
        setPhanTramGiamGia(phanTramGiamGia);
        setMucGiamToiDa(mucGiamToiDa);
    }

    @Override
    public void setKM() {
        super.setKM();

        do {
            phanTramGiamGia = Ktradulieu.nhapSoThuc(
                    sc, "Nhập phần trăm giảm (0 < % <= 100): ");

            if (phanTramGiamGia <= 0 || phanTramGiamGia > 100) {
                System.out.println("Phần trăm phải lớn hơn 0 và không quá 100!");
            }
        } while (phanTramGiamGia <= 0 || phanTramGiamGia > 100);

        do {
            mucGiamToiDa = Ktradulieu.nhapSoNguyen(
                    sc, "Nhập mức giảm tối đa (0 = không giới hạn): ");

            if (mucGiamToiDa < 0) {
                System.out.println("Mức giảm tối đa không được âm!");
            }
        } while (mucGiamToiDa < 0);

        setLoaiKM("Giảm " + phanTramGiamGia + "%");
    }

    @Override
    public long tinhTienGiam(long giaBan) {
        if (giaBan <= 0 || phanTramGiamGia <= 0 || mucGiamToiDa < 0) {
            return 0;
        }

        long tienGiam = Math.round(giaBan * phanTramGiamGia / 100.0);

        // Nếu mucGiamToiDa > 0 thì áp trần, còn = 0 nghĩa là không giới hạn mức giảm tối đa
        if (mucGiamToiDa > 0) {
            tienGiam = Math.min(tienGiam, mucGiamToiDa);
        }

        return Math.min(tienGiam, giaBan);
    }

    @Override
    public long tinhTongTienGiam(long giaBan, int soLuong) {
        if (giaBan <= 0 || soLuong <= 0 || phanTramGiamGia <= 0 || mucGiamToiDa < 0) {
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

    public double getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    public void setPhanTramGiamGia(double phanTramGiamGia) {
        if (!Double.isFinite(phanTramGiamGia) || phanTramGiamGia <= 0 || phanTramGiamGia > 100) {
            throw new IllegalArgumentException(
                    "Phần trăm giảm phải lớn hơn 0 và không quá 100!");
        }
        this.phanTramGiamGia = phanTramGiamGia;
    }

    public long getMucGiamToiDa() {
        return mucGiamToiDa;
    }

    public void setMucGiamToiDa(long mucGiamToiDa) {
        if (mucGiamToiDa < 0) {
            throw new IllegalArgumentException(
                    "Mức giảm tối đa không được âm!");
        }
        this.mucGiamToiDa = mucGiamToiDa;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nPhần trăm giảm: " + phanTramGiamGia + "%"
                + "\nMức giảm tối đa: " + (mucGiamToiDa == 0 ? "Không giới hạn" : mucGiamToiDa);
    }
}
