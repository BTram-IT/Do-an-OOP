import java.time.LocalDate;
import java.util.ArrayList;

public class KhuyenmaiPhantram extends Khuyenmai {
    private double phanTramGiamGia;
    private long mucGiamToiDa;

    public KhuyenmaiPhantram() {
        phanTramGiamGia = 0;
        mucGiamToiDa = 0;
        setLoaiKM("Giảm theo phần trăm");
    }

    public KhuyenmaiPhantram(String maKM, String loaiKM,
                             LocalDate ngayBatDau, LocalDate ngayKetThuc,
                             long mucChiToiThieu, ArrayList<SanPham> danhSachKM,
                             double phanTramGiamGia, long mucGiamToiDa) {
        super(maKM, loaiKM, ngayBatDau, ngayKetThuc,
                mucChiToiThieu, danhSachKM);
        this.phanTramGiamGia = phanTramGiamGia;
        this.mucGiamToiDa = mucGiamToiDa;
    }

    public void setKM() {
        super.setKM();

        do {
            phanTramGiamGia = Ktradulieu.nhapSoThuc(
                    sc, "Nhập phần trăm giảm giá: ");

            if (phanTramGiamGia > 100) {
                System.out.println("Phần trăm giảm giá không được lớn hơn 100!");
            }
        } while (phanTramGiamGia > 100);

        mucGiamToiDa = Ktradulieu.nhapSoNguyen(
                sc, "Nhập số tiền được giảm tối đa: ");

        setLoaiKM("Giảm " + phanTramGiamGia + "%");
    }

    public double getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    public long getMucGiamToiDa() {
        return mucGiamToiDa;
    }

    public void setPhanTramGiamGia(double phanTramGiamGia) {
        this.phanTramGiamGia = phanTramGiamGia;
    }

    public void setMucGiamToiDa(long mucGiamToiDa) {
        this.mucGiamToiDa = mucGiamToiDa;
    }

    @Override
    public double traCuuGiaSanPhamSauKM(String maSP) {
        int viTri = timKiemSP(maSP);

        if (viTri == -1) {
            return -1;
        }

        SanPham sp = getDanhSachKM().get(viTri);
        double giaBan = sp.getGiaBan();

        double tienGiam = giaBan * phanTramGiamGia / 100;

        if (tienGiam > mucGiamToiDa) {
            tienGiam = mucGiamToiDa;
        }

        return giaBan - tienGiam;
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Phần trăm giảm giá: " + phanTramGiamGia
                + "% | Mức giảm tối đa: " + mucGiamToiDa;
    }
}