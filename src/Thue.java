import jdk.swing.interop.SwingInterOpUtils;

import java.util.Scanner;
import java.util.List;

public class Thue {
    private String Mathue;
    private String Tendvthue;

    private static final double thueSuatmacdinh = 0.08;

    public Thue() {
        Mathue = "";
        Tendvthue = "";
    }

    public Thue(String mathue, String tenthue) {
        Mathue = mathue;
        Tendvthue = tenthue;
    }

    public String getMathue() {
        return Mathue;
    }

    public void setMathue(String mathue) {
        Mathue = mathue;
    }

    public String getTenthue() {
        return Tendvthue;
    }

    public void setTenthue(String tenthue) {
        Tendvthue = tenthue;
    }

    public double getThueSuat() {
        return thueSuatmacdinh;
    }

    Scanner sc = new Scanner(System.in);

    public void Nhap() {
        System.out.println("Nhập mã thuế: ");
        Mathue = sc.nextLine();

        System.out.println("Nhập tên thuế: ");
        Tendvthue = sc.nextLine();
    }

    public void inBangKeThue(List<HoaDon> hoaDonList) {
        System.out.println("============================================================");
        System.out.println("    BẢNG KÊ THUẾ GIÁ TRỊ GIA TĂNG (GTGT) ĐẦU RA BÁN LẺ");
        System.out.println("Đơn vị nộp thuế : "+Tendvthue);
        System.out.println("Mã số thuế : "+Mathue);
        System.out.println("Mã HĐ       Ngày lập      Doanh thu       Thuế suất      Tiền thuế       Tổng tiền");
        System.out.println("============================================================");

        double TongDoanhthuChuaThue=0;
        double TongTienThuePhaiNop=0;
        double TongDoanhThuSauThue=0;

        for(HoaDon hd : hoaDonList) {
            double truocThue=hd.getTongtienTruocThue();// Tiền chưa tính thuế khi có giảm giá
            double vat=hd.getTienThueVAT(); // Tiền thuế
            double sauThue=hd.TinhTongThanhToan(); // Tổng tiền đã bao gồm thuế

            TongDoanhThuSauThue+=truocThue;
            TongTienThuePhaiNop+=vat;
            TongDoanhthuChuaThue+=sauThue;

            System.out.println(hd.getMaHD() + "     " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(hd.getThoiGianTao()) + "     " + String.format("%,.0f", truocThue) + "     " + String.format("%.0f%%", hd.getThueSuatVAT() * 100) + "     " + String.format("%,.0f", vat) + "     " + String.format("%,.0f", sauThue) + " VNĐ");
        }
        System.out.println("=========================================================");
        System.out.println("TỔNG DOANH SỐ CHƯA THUẾ : "+TongDoanhthuChuaThue);
        System.out.println("TỔNG THUẾ GTGT BÁN RA PHẢI KÊ NỘP : "+TongTienThuePhaiNop);
        System.out.println("TỔNG GIÁ TRỊ THANH TOÁN ( BAO GỒM VAT ) : "+TongDoanhThuSauThue);
        System.out.println("=========================================================");
    }
}