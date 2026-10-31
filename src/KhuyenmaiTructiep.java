import java.time.LocalDate;
import java.util.ArrayList;

public class KhuyenmaiTructiep extends Khuyenmai {
    private long soTienGiam;

    public KhuyenmaiTructiep() {
        soTienGiam = 0;
        setLoaiKM("Giảm giá trực tiếp");
    }

    public KhuyenmaiTructiep(String maKM, String loaiKM,
                             LocalDate ngayBatDau, LocalDate ngayKetThuc,
                             long mucChiToiThieu, ArrayList<SanPham> danhSachKM,
                             long soTienGiam) {
        super(maKM, loaiKM, ngayBatDau, ngayKetThuc,
                mucChiToiThieu, danhSachKM);
        this.soTienGiam = soTienGiam;
    }

    public void setKM() {
        super.setKM();

        soTienGiam = Ktradulieu.nhapSoNguyen(
                sc, "Nhập số tiền giảm giá trực tiếp: ");

        setLoaiKM("Giảm trực tiếp " + soTienGiam + " đồng");
    }

    public long getSoTienGiam() {
        return soTienGiam;
    }

    public void setSoTienGiam(long soTienGiam) {
        this.soTienGiam = soTienGiam;
    }

    @Override
    public double traCuuGiaSanPhamSauKM(String maSP) {
        int viTri = timKiemSP(maSP);

        if (viTri == -1) {
            return -1;
        }

        double giaBan = getDanhSachKM().get(viTri).getGiaBan();

        // Không giảm quá giá bán sản phẩm
        return Math.max(0, giaBan - soTienGiam);
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Số tiền giảm trực tiếp: " + soTienGiam;
    }
}