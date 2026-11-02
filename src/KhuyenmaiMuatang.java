
import java.time.LocalDateTime;
import java.util.ArrayList;

public class KhuyenmaiMuatang extends Khuyenmai {
    private int soLuongMuaToiThieu;
    private int soLuongTang;

    public KhuyenmaiMuatang() {
        super();
        loaiKM = "Mua tang";
        soLuongMuaToiThieu = 0;
        soLuongTang = 0;
    }

    public KhuyenmaiMuatang(
            String maKM, String tenKM,
            LocalDateTime ngayBatDau,
            LocalDateTime ngayKetThuc,
            double mucChiToiThieu,
            ArrayList<SanPham> danhSachKM,
            int soLuongMuaToiThieu,
            int soLuongTang) {

        super(maKM, tenKM, "Mua tang",
                ngayBatDau, ngayKetThuc,
                mucChiToiThieu, danhSachKM);

        if (soLuongMuaToiThieu <= 0 || soLuongTang <= 0) {
            throw new IllegalArgumentException("So luong mua va so luong tang phai lon hon 0!");
        }

        this.soLuongMuaToiThieu = soLuongMuaToiThieu;
        this.soLuongTang = soLuongTang;
    }

    public void thietLapMuaTang() {
        super.thietLapKhuyenMai();

        loaiKM = "Mua tang";

        long soLuongMua = Ktradulieu.nhapSoNguyen(
                sc,
                "Nhap so luong mua toi thieu: "
        );

        while (soLuongMua > Integer.MAX_VALUE) {
            System.out.println("So luong vuot qua gioi han cho phep!");
            soLuongMua = Ktradulieu.nhapSoNguyen(
                    sc,
                    "Nhap lai so luong mua toi thieu: "
            );
        }

        soLuongMuaToiThieu = (int) soLuongMua;

        long soLuongDuocTang = Ktradulieu.nhapSoNguyen(
                sc,
                "Nhap so luong tang: "
        );

        while (soLuongDuocTang > Integer.MAX_VALUE) {
            System.out.println("So luong vuot qua gioi han cho phep!");
            soLuongDuocTang = Ktradulieu.nhapSoNguyen(
                    sc,
                    "Nhap lai so luong tang: "
            );
        }

        soLuongTang = (int) soLuongDuocTang;
    }

    @Override
    public double traCuuGiaSanPhamSauKM(String maSP) {
        int viTri = timKiemSP(maSP);

        if (viTri == -1 || !kiemTraThoiHan()) {
            return -1;
        }

        // Khuyen mai mua tang khong lam thay doi don gia.
        return danhSachKM.get(viTri).getGiaBan();
    }

    public int tinhSoLuongDuocTang(int soLuongMua) {
        if (!kiemTraThoiHan()
                || soLuongMua < soLuongMuaToiThieu
                || soLuongMuaToiThieu <= 0) {
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

    @Override
    public String toString() {
        return super.toString()
                + "\nSo luong mua toi thieu: " + soLuongMuaToiThieu
                + "\nSo luong tang: " + soLuongTang;
    }
}
