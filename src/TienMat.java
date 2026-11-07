import java.util.Scanner;

public class TienMat extends ThanhToan {

    private double tienKhachDua;
    private double tienThoi;

    public TienMat() {
        this("");
    }

    public TienMat(String maGD) {
        super(maGD, "Tiền mặt");
        this.tienKhachDua = 0;
        this.tienThoi = 0;
    }

    @Override
    public void nhap() {
        Scanner sc = new Scanner(System.in);
        nhap(sc);
    }

    @Override
    public void nhap(Scanner sc) {
        if (sc == null) sc = new Scanner(System.in);
        this.tienKhachDua = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập số tiền khách đưa: ");
    }

    @Override
    public boolean qTrinhThanhToan() {
        boolean duTien = tienKhachDua >= soTien;
        tienThoi = duTien ? (tienKhachDua - soTien) : 0;
        return ketThuc(duTien);
    }

    // Phương thức thanh toán tương thích với Hoadonban
    public boolean thanhToan(Scanner sc, long tongTien) {
        this.soTien = (double) tongTien;
        System.out.println("Tổng tiền cần trả: " + Ktradulieu.dinhDangTien(tongTien));

        while (true) {
            nhap(sc);
            if (qTrinhThanhToan()) {
                return true;
            }
            System.out.printf("Khách đưa thiếu %,.0f VND! Vui lòng đưa thêm tiền hoặc nhập lại!%n", (soTien - tienKhachDua));
            System.out.print("Bạn có muốn nhập lại số tiền khách đưa? (y/n): ");
            String retry = sc.nextLine().trim().toLowerCase();
            if (!retry.equals("y") && !retry.equals("yes") && !retry.equals("c")) {
                return false;
            }
        }
    }

    public boolean thanhToan(long tongTien) {
        return thanhToan(new Scanner(System.in), tongTien);
    }

    public long tienThua(long tongTien) {
        return (long) Math.max(0, tienKhachDua - tongTien);
    }

    public double getTienKhachDua() { return tienKhachDua; }
    public void setTienKhachDua(double tienKhachDua) { this.tienKhachDua = tienKhachDua; }

    public double getTienThoi() { return tienThoi; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Khách đưa: %,.0f VND | Tiền thối: %,.0f VND", tienKhachDua, tienThoi);
    }
}