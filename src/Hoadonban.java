import java.util.ArrayList;

public class Hoadonban extends HoaDon {
    private String MaKH;
    private ArrayList<CTHoaDon> CTHoaDon;
    ThanhToan thanhtoan;


    @Override
    public void inhoadon() {

    }

    public double Tongthuedonhang(DSSanPham dssanpham) {
        return 0;
    }

    @Override
    public double Tongtienhoadon() {
        return 0;
    }

    public Hoadonban(String maKH, ArrayList<CTHoaDon> CTHoaDon, ThanhToan thanhtoan) {
        MaKH = maKH;
        this.CTHoaDon = CTHoaDon;
        this.thanhtoan = thanhtoan;
    }

    public ArrayList<CTHoaDon> getCTHoaDon() {
        return CTHoaDon;
    }
}
