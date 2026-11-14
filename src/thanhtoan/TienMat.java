package thanhtoan;

import java.util.Scanner;
import util.Ktradulieu;
import person.Nguoidung;

public class TienMat extends ThanhToan {

    private double tienKhachDua;
    private double tienThoi;

    public TienMat() {
        super();
        this.loaiThanhToan = "TienMat";
        this.tienKhachDua = 0.0;
        this.tienThoi = 0.0;
    }

    public TienMat(String maHD, double soTien, Nguoidung nvDangDangNhap) {
        super(maHD, "TienMat", soTien, nvDangDangNhap);
        this.tienKhachDua = 0.0;
        this.tienThoi = 0.0;
    }

    @Override
    public void nhap() {
        Scanner sc = new Scanner(System.in);
        nhap(sc);
    }

    @Override
    public void nhap(Scanner sc) {
        if (sc == null) sc = new Scanner(System.in);

        System.out.println("\n========== THANH TOÁN TIỀN MẶT ==========");
        System.out.println("• Mã giao dịch       : [" + this.maGD + "]");
        System.out.println("• Thu ngân phụ trách : [" + this.maNV + "]");
        System.out.println("• Tổng tiền cần thu  : " + Ktradulieu.dinhDangTien(this.soTien));

        while (true) {
            try {
                double tienDua = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập số tiền khách đưa: ");
                setTienKhachDua(tienDua);

                if (this.tienKhachDua < this.soTien) {
                    double thieu = this.soTien - this.tienKhachDua;
                    System.out.println("Khách đưa thiếu: " + Ktradulieu.dinhDangTien(thieu) + "! Vui lòng nhận thêm tiền hoặc nhập lại.");
                    continue;
                }

                this.tienThoi = this.tienKhachDua - this.soTien;
                System.out.println("Tiền thừa thối lại khách: " + Ktradulieu.dinhDangTien(this.tienThoi));

                qTrinhThanhToan();
                break;
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage());
            }
        }
    }
    @Override
    public boolean qTrinhThanhToan() {
        if (this.tienKhachDua >= this.soTien && this.soTien > 0) {
            ketThuc(true);
            System.out.println("ĐÃ HOÀN TẤT THANH TOÁN TIỀN MẶT THÀNH CÔNG.");
            return true;
        } else {
            ketThuc(false);
            return false;
        }
    }

    public boolean thanhToan(Scanner sc, double tongTien) {
        this.soTien = tongTien;
        nhap(sc);
        return isThanhCong();
    }

    public boolean thanhToan(double tongTien) {
        return thanhToan(new Scanner(System.in), tongTien);
    }

    public double getTienKhachDua() {
        return tienKhachDua;
    }

    public void setTienKhachDua(double tienKhachDua) {
        if (tienKhachDua < 0) {
            throw new IllegalArgumentException("[Lỗi] Số tiền khách đưa không được âm! Nhận: " + tienKhachDua);
        }
        this.tienKhachDua = tienKhachDua;
    }

    public double getTienThoi() {
        return tienThoi;
    }

    public void setTienThoi(double tienThoi) {
        if (tienThoi < 0) {
            throw new IllegalArgumentException("[Lỗi] Tiền thối không được âm!");
        }
        this.tienThoi = tienThoi;
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Khách đưa: " + Ktradulieu.dinhDangTien(tienKhachDua)
                + " | Tiền thối: " + Ktradulieu.dinhDangTien(tienThoi);
    }
}