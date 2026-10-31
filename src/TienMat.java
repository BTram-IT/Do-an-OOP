import java.util.Scanner;

public class TienMat implements ThanhToan {
    Scanner sc = new Scanner(System.in);
    private double tienKhachDua;

    public TienMat() {
        this(0);
    }

    public TienMat(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    public void nhap(Scanner sc) {
        tienKhachDua = Ktradulieu.nhapSoThuc(
                sc, "Nhập tiền khách đưa: "
        );
    }

    @Override
    public boolean thanhToan(double soTien) {
        return tienKhachDua >= soTien;
    }

    @Override
    public String getPhuongThuc() {
        return "Tiền mặt";
    }

    public double tienThua(double soTien) {
        return Math.max(0, tienKhachDua - soTien);
    }
}