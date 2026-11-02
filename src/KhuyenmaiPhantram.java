
import java.time.LocalDateTime;
import java.util.ArrayList;

public class KhuyenmaiPhantram extends Khuyenmai {
    private double phanTramGiamGia;
    private double mucGiamGiaToiDa;

    public KhuyenmaiPhantram() {
        super();
        loaiKM = "Giam theo phan tram";
        phanTramGiamGia = 0;
        mucGiamGiaToiDa = 0;
    }

    public KhuyenmaiPhantram(
            String maKM, String tenKM,
            LocalDateTime ngayBatDau,
            LocalDateTime ngayKetThuc,
            double mucChiToiThieu,
            ArrayList<SanPham> danhSachKM,
            double phanTramGiamGia,
            double mucGiamGiaToiDa) {

        super(maKM, tenKM, "Giam theo phan tram",
                ngayBatDau, ngayKetThuc,
                mucChiToiThieu, danhSachKM);

        this.phanTramGiamGia = phanTramGiamGia;
        this.mucGiamGiaToiDa = mucGiamGiaToiDa;
    }

    public void thietLapKhuyenMai() {
        super.thietLapKhuyenMai();

        loaiKM = "Giam theo phan tram";

        do {
            phanTramGiamGia = Ktradulieu.nhapSoThuc(
                    sc,
                    "Nhap phan tram giam gia (toi da 100%): "
            );

            if (phanTramGiamGia > 100) {
                System.out.println("Phan tram giam gia khong duoc vuot qua 100%!");
            }
        } while (phanTramGiamGia > 100);

        mucGiamGiaToiDa = Ktradulieu.nhapSoThuc(
                sc,
                "Nhap muc giam gia toi da: "
        );
    }

    @Override
    public double traCuuGiaSanPhamSauKM(String maSP) {
        int viTri = timKiemSP(maSP);

        if (viTri == -1 || !kiemTraThoiHan()) {
            return -1;
        }

        double giaGoc = danhSachKM.get(viTri).getGiaBan();

        double tienGiam = giaGoc * phanTramGiamGia / 100;

        tienGiam = Math.min(tienGiam, mucGiamGiaToiDa);

        return Math.max(0, giaGoc - tienGiam);
    }

    public double getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    public double getMucGiamGiaToiDa() {
        return mucGiamGiaToiDa;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nPhan tram giam: " + phanTramGiamGia + "%"
                + "\nMuc giam toi da: " + mucGiamGiaToiDa;
    }
}
