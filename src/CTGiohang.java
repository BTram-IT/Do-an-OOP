
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CTGiohang {

    // Thong tin mot san pham
    private SanPham sanPham;
    private int soLuongSP;

    // Danh sach cac san pham trong gio hang
    private static List<CTGiohang> danhSachGioHang = new ArrayList<>();

    // Constructor
    public CTGiohang(SanPham sanPham, int soLuongSP) {
        if (sanPham == null) {
            throw new IllegalArgumentException("San pham khong hop le");
        }

        if (soLuongSP <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0");
        }

        this.sanPham = sanPham;
        this.soLuongSP = soLuongSP;
    }

    // Getter
    public String getMaSP() {
        return sanPham.getMaSP();
    }

    public String getTenSP() {
        return sanPham.getTenSP();
    }

    public int getSoLuongSP() {
        return soLuongSP;
    }

    public double getDonGia() {
        return sanPham.getDonGia();
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    // Setter
    public void setSoLuongSP(int soLuongSP) {
        if (soLuongSP <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0");
        }

        this.soLuongSP = soLuongSP;
    }

    // Tinh tien mot san pham
    public double tongTien() {
        return sanPham.getDonGia() * soLuongSP;
    }

    // Nhap san pham vao gio hang
    public static void Nhap(SanPham sanPham, int soLuong) {
        if (sanPham == null || soLuong <= 0) {
            System.out.println("Thong tin khong hop le!");
            return;
        }

        // Kiem tra san pham da co trong gio chua
        for (CTGiohang ct : danhSachGioHang) {
            if (ct.getMaSP().equals(sanPham.getMaSP())) {
                int soLuongMoi = ct.getSoLuongSP() + soLuong;

                if (soLuongMoi > sanPham.getSoLuongTon()) {
                    System.out.println("Khong du hang trong kho!");
                    return;
                }

                ct.setSoLuongSP(soLuongMoi);
                System.out.println("Da cap nhat so luong!");
                return;
            }
        }

        // Kiem tra ton kho
        if (soLuong > sanPham.getSoLuongTon()) {
            System.out.println("Khong du hang trong kho!");
            return;
        }

        danhSachGioHang.add(new CTGiohang(sanPham, soLuong));
        System.out.println("Da them san pham vao gio hang!");
    }

    // Xoa san pham theo vi tri
    public static void xoaSanPham(int viTri) {
        if (viTri < 0 || viTri >= danhSachGioHang.size()) {
            System.out.println("Vi tri khong hop le!");
            return;
        }

        danhSachGioHang.remove(viTri);
        System.out.println("Da xoa san pham!");
    }

    // Sua so luong san pham
    public static void suaSoLuong(int viTri, int soLuongMoi) {
        if (viTri < 0 || viTri >= danhSachGioHang.size()) {
            System.out.println("Vi tri khong hop le!");
            return;
        }

        CTGiohang ct = danhSachGioHang.get(viTri);

        if (soLuongMoi <= 0 ||
            soLuongMoi > ct.getSanPham().getSoLuongTon()) {
            System.out.println("So luong khong hop le hoac vuot ton kho!");
            return;
        }

        ct.setSoLuongSP(soLuongMoi);
        System.out.println("Da cap nhat so luong!");
    }

    // Kiem tra ton kho
    public static boolean kiemTraTonKho() {
        for (CTGiohang ct : danhSachGioHang) {
            if (ct.getSoLuongSP() > ct.getSanPham().getSoLuongTon()) {
                System.out.println(
                    "San pham " + ct.getTenSP() + " khong du hang!"
                );
                return false;
            }
        }

        return true;
    }

    // Tinh tong tien toan bo gio hang
    public static double tinhTongTienGioHang() {
        double tong = 0;

        for (CTGiohang ct : danhSachGioHang) {
            tong += ct.tongTien();
        }

        return tong;
    }

    // Hien thi gio hang
    public static void hienThiGioHang() {
        if (danhSachGioHang.isEmpty()) {
            System.out.println("Gio hang dang trong!");
            return;
        }

        System.out.println("===== GIO HANG =====");

        for (int i = 0; i < danhSachGioHang.size(); i++) {
            System.out.println((i + 1) + ". " + danhSachGioHang.get(i));
        }

        System.out.println("Tong tien: " + tinhTongTienGioHang() + " VND");
    }

    // toString
    @Override
    public String toString() {
        return String.format(
            "%-10s | %-20s | %-10d | %-15.0f | %-15.0f",
            getMaSP(),
            getTenSP(),
            soLuongSP,
            getDonGia(),
            tongTien()
        );
    }
}
