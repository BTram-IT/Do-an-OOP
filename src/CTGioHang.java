import java.util.Scanner;

public class CTGioHang {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private double DonGia;

    // Constructor mặc định
    public CTGioHang() {
        MaSP = "";
        TenSP = "";
        Soluong = 0;
        DonGia = 0;
    }

    // Constructor có tham số
    public CTGioHang(String maSP, String tenSP, int soluong, double donGia) {
        MaSP = maSP;
        TenSP = tenSP;
        Soluong = soluong;
        DonGia = donGia;
    }

    // Getter và Setter
    public String getMaSP() {
        return MaSP;
    }

    public void setMaSP(String maSP) {
        MaSP = maSP;
    }

    public String getTenSP() {
        return TenSP;
    }

    public void setTenSP(String tenSP) {
        TenSP = tenSP;
    }

    public int getSoluong() {
        return Soluong;
    }

    public void setSoluong(int soluong) {
        Soluong = soluong;
    }

    public double getDonGia() {
        return DonGia;
    }

    public void setDonGia(double donGia) {
        DonGia = donGia;
    }

    // Tính tổng tiền của sản phẩm
    public double TongTien() {
        return Soluong * DonGia;
    }

    // Nhập chi tiết giỏ hàng
    public void Nhap(Scanner sc, DSSanPham dsSP) {
        SanPham sp;

        while (true) {
            MaSP = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhập mã sản phẩm: ",
                    "^SP\\d{3}$",
                    "Mã sản phẩm không đúng định dạng!"
            );

            sp = dsSP.timTheoMa(MaSP);

            if (sp != null) {
                break;
            }

            System.out.println("Không tìm thấy sản phẩm! Vui lòng nhập lại.");
        }

        // Lấy thông tin từ sản phẩm
        TenSP = sp.getTenSP();
        DonGia = sp.getGiaBan();

        // Nhập số lượng
        Soluong = (int) Ktradulieu.nhapSoNguyen(
                sc, "Nhập số lượng: "
        );
    }

    // Hiển thị chi tiết giỏ hàng
    public void Xuat() {
        System.out.println("Mã sản phẩm: " + MaSP);
        System.out.println("Tên sản phẩm: " + TenSP);
        System.out.println("Số lượng: " + Soluong);
        System.out.println("Đơn giá: " + DonGia);
        System.out.println("Thành tiền: " + TongTien());
    }
}