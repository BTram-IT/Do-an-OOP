import java.util.Scanner;
public class TienMat extends ThanhToan {
    private static final Scanner sc = new Scanner(System.in);

    private double tienKhachDua;
    private double tienThoi;

    public TienMat() {
        this("");
    }

    public TienMat(String maGD) {
        super(maGD, "Tien mat");
    }

    @Override
    public void nhap() {
        tienKhachDua = TienIch.nhapSoThucKhongAm(sc, "Khach dua: ");
    }

    @Override
    public boolean qTrinhThanhToan() {
        boolean duTien = tienKhachDua >= soTien;
        tienThoi = duTien ? tienKhachDua - soTien : 0;
        return ketThuc(duTien);
    }

    public double getTienKhachDua() {
        return tienKhachDua;
    }

    public void setTienKhachDua(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    public double getTienThoi() {
        return tienThoi;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Khach dua: %,.0f | Tien thoi: %,.0f", tienKhachDua, tienThoi);
    }
}
