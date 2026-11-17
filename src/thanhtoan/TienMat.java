package thanhtoan;

import util.BangConsole;

import java.util.Scanner;
import person.Nguoidung;
import util.Ktradulieu;

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
    public void nhap(Scanner sc) {
        if (sc == null) throw new IllegalArgumentException("Scanner không được null!");
        batDauThanhToan();
        this.tienKhachDua = 0;
        this.tienThoi = 0;

        System.out.println("\n========== THANH TOÁN TIỀN MẶT ==========");
        System.out.println("• Mã giao dịch       : [" + this.maGD + "]");
        System.out.println("• Thu ngân phụ trách : [" + this.maNV + "]");
        System.out.println((chiTien ? "• Tổng tiền cần chi  : " : "• Tổng tiền cần thu  : ") + Ktradulieu.dinhDangTien(this.soTien));

        if (this.soTien == 0) {
            int xacNhan = Ktradulieu.luaChon(sc, "Hóa đơn 0 đồng. Xác nhận thanh toán? (1: Có / 0: Hủy): ", 0, 1);
            if (xacNhan == 1) qTrinhThanhToan();
            else ketThuc(false);
            return;
        }

        while (true) {
            try {
                double tienDua = Ktradulieu.nhapSoThucKhongAm(sc, chiTien
                        ? "Nhập số tiền cửa hàng chi (0 để hủy thanh toán): "
                        : "Nhập số tiền khách đưa (0 để hủy thanh toán): ");
                if (tienDua == 0) {
                    ketThuc(false);
                    System.out.println("[Thông báo] Đã hủy lần thanh toán tiền mặt.");
                    return;
                }
                setTienKhachDua(tienDua);

                if (this.tienKhachDua < this.soTien) {
                    double thieu = this.soTien - this.tienKhachDua;
                    System.out.println((chiTien ? "Cửa hàng chi thiếu: " : "Khách đưa thiếu: ")
                            + Ktradulieu.dinhDangTien(thieu) + "! Vui lòng nhập lại tổng số tiền.");
                    continue;
                }

                this.tienThoi = this.tienKhachDua - this.soTien;
                System.out.println((chiTien ? "Tiền nhà cung cấp trả lại: " : "Tiền thừa thối lại khách: ")
                        + Ktradulieu.dinhDangTien(this.tienThoi));

                qTrinhThanhToan();
                break;
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage());
            }
        }
    }
    @Override
    public boolean qTrinhThanhToan() {
        if (Double.isFinite(this.soTien) && this.tienKhachDua >= this.soTien && this.soTien >= 0) {
            this.tienThoi = this.tienKhachDua - this.soTien;
            ketThuc(true);
            System.out.println("ĐÃ HOÀN TẤT THANH TOÁN TIỀN MẶT THÀNH CÔNG.");
            return true;
        } else {
            ketThuc(false);
            return false;
        }
    }

    public boolean thanhToan(Scanner sc, double tongTien) {
        setSoTien(tongTien);
        return thucHienThanhToan(sc);
    }

    public double getTienKhachDua() {
        return tienKhachDua;
    }

    public void setTienKhachDua(double tienKhachDua) {
        if (!Double.isFinite(tienKhachDua) || tienKhachDua < 0) {
            throw new IllegalArgumentException("[Lỗi] Số tiền khách đưa không được âm! Nhận: " + tienKhachDua);
        }
        this.tienKhachDua = tienKhachDua;
    }

    public double getTienThoi() {
        return tienThoi;
    }

    public void setTienThoi(double tienThoi) {
        if (!Double.isFinite(tienThoi) || tienThoi < 0) {
            throw new IllegalArgumentException("[Lỗi] Tiền thối không được âm!");
        }
        this.tienThoi = tienThoi;
    }

    @Override
    protected String layChiTietBang() {
        return BangConsole.thongTin(chiTien ? new String[]{"Cửa hàng chi", "Nhận lại"} : new String[]{"Khách đưa", "Tiền thối"},
                Ktradulieu.dinhDangTien(tienKhachDua), Ktradulieu.dinhDangTien(tienThoi));
    }
}
