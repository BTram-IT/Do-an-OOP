import java.time.LocalDate;
import java.util.ArrayList;

public class KhuyenmaiMuatang extends Khuyenmai {
    private int soLuongMuaToiThieu;
    private int soLuongTang;

    public KhuyenmaiMuatang() {
        soLuongMuaToiThieu = 0;
        soLuongTang = 0;
        setLoaiKM("Mua tặng");
    }

    public KhuyenmaiMuatang(String maKM, String loaiKM,
                            LocalDate ngayBatDau, LocalDate ngayKetThuc,
                            long mucChiToiThieu, ArrayList<SanPham> danhSachKM,
                            int soLuongMuaToiThieu, int soLuongTang) {
        super(maKM, loaiKM, ngayBatDau, ngayKetThuc,
                mucChiToiThieu, danhSachKM);
        this.soLuongMuaToiThieu = soLuongMuaToiThieu;
        this.soLuongTang = soLuongTang;
    }

    public void setKM() {
        super.setKM();

        soLuongMuaToiThieu = (int) Ktradulieu.nhapSoNguyen(
                sc, "Nhập số lượng sản phẩm mua tối thiểu: ");

        soLuongTang = (int) Ktradulieu.nhapSoNguyen(
                sc, "Nhập số lượng sản phẩm tặng: ");

        setLoaiKM("Mua " + soLuongMuaToiThieu
                + " tặng " + soLuongTang);
    }

    public int tinhSoLuongTang(int soLuongMua) {
        if (soLuongMuaToiThieu <= 0 || soLuongMua < soLuongMuaToiThieu) {
            return 0;
        }

        return (soLuongMua / soLuongMuaToiThieu) * soLuongTang;
    }

    public int getSoLuongMuaToiThieu() {
        return soLuongMuaToiThieu;
    }

    public int getSoLuongTang() {
        return soLuongTang;
    }

    public void setSoLuongMuaToiThieu(int soLuongMuaToiThieu) {
        this.soLuongMuaToiThieu = soLuongMuaToiThieu;
    }

    public void setSoLuongTang(int soLuongTang) {
        this.soLuongTang = soLuongTang;
    }

    @Override
    public double traCuuGiaSanPhamSauKM(String maSP) {
        int viTri = timKiemSP(maSP);

        if (viTri == -1) {
            return -1;
        }

        // Mua tặng không làm thay đổi giá bán của sản phẩm
        return getDanhSachKM().get(viTri).getGiaBan();
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Số lượng mua tối thiểu: " + soLuongMuaToiThieu
                + " | Số lượng tặng: " + soLuongTang;
    }
}