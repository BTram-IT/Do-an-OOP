
import java.time.LocalDateTime;
import java.util.ArrayList;

public class KhuyenmaiTructiep extends Khuyenmai {
    private double soTienGiam;

    // Constructor mac dinh
    public KhuyenmaiTructiep() {
        super();
        loaiKM = "Giam truc tiep";
        soTienGiam = 0;
    }

    // Constructor day du
    public KhuyenmaiTructiep(
            String maKM, String tenKM,
            LocalDateTime ngayBatDau,
            LocalDateTime ngayKetThuc,
            double mucChiToiThieu,
            ArrayList<SanPham> danhSachKM,
            double soTienGiam) {

        super(maKM, tenKM, "Giam truc tiep",
                ngayBatDau, ngayKetThuc,
                mucChiToiThieu, danhSachKM);

        this.soTienGiam = soTienGiam;
    }

    // Nhap thong tin khuyen mai
    public void thietLapKhuyenMai() {
        super.thietLapKhuyenMai();

        loaiKM = "Giam truc tiep";

        soTienGiam = Ktradulieu.nhapSoThuc(
                sc,
                "Nhap so tien giam truc tiep: "
        );
    }

    // Tra cuu gia san pham sau khuyen mai
    @Override
    public double traCuuGiaSanPhamSauKM(String maSP) {
        int viTri = timKiemSP(maSP);

        if (viTri == -1 || !kiemTraThoiHan()) {
            return -1;
        }

        double giaGoc = danhSachKM.get(viTri).getGiaBan();

        return Math.max(0, giaGoc - soTienGiam);
    }

    // Getter
    public double getSoTienGiam() {
        return soTienGiam;
    }

    // Setter
    public void setSoTienGiam(double soTienGiam) {
        if (!Double.isFinite(soTienGiam) || soTienGiam < 0) {
            throw new IllegalArgumentException(
                    "So tien giam khong hop le!");
        }

        this.soTienGiam = soTienGiam;
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nSo tien giam truc tiep: "
                + String.format("%,.0f VND", soTienGiam);
    }
}
