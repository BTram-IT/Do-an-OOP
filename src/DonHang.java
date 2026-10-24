import java.util.ArrayList;
import java.util.List;

public class DonHang {
    private String maDH;
    private String ngayLap;
    private Khachhang khachhang;

    private List<String> danhSachSanPham = new ArrayList<>();

    public Khachhang getKhachhang() {
        return khachhang;
    }

    public void setKhachhang(Khachhang khachhang) {
        this.khachhang = khachhang;
    }

    public DonHang(String maDH, String ngayLap) {
        this.maDH = maDH;
        this.ngayLap = ngayLap;
    }

    public void themSanPham(String tenSP) {
        danhSachSanPham.add(tenSP);
        System.out.println("-> Da them san pham: \"" + tenSP + "\" vao don hang.");
    }

    public void xoaSanPham(int index) {
        if (index >= 0 && index < danhSachSanPham.size()) {
            String spBiXoa = danhSachSanPham.remove(index);
            System.out.println("-> Da xoa san pham: \"" + spBiXoa + "\" khoi don hang.");
        } else {
            System.out.println("-> Vi tri san pham khong hop le de xoa!");
        }
    }

    public void suaSanPham(int index, String tenMoi) {
        if (index >= 0 && index < danhSachSanPham.size()) {
            String spCu = danhSachSanPham.set(index, tenMoi);
            System.out.println("-> Da sua san pham \"" + spCu + "\" thanh \"" + tenMoi + "\".");
        } else {
            System.out.println("-> Vi tri san pham khong hop le de sua!");
        }
    }

    public void hienThiDanhSachSanPham() {
        System.out.println("\n--- DANH SACH SAN PHAM TRONG DON: " + maDH + " ---");
        if (danhSachSanPham.isEmpty()) {
            System.out.println("(Don hang dang trong)");
        } else {
            for (int i = 0; i < danhSachSanPham.size(); i++) {
                System.out.println((i + 1) + ". " + danhSachSanPham.get(i));
            }
        }
    }

    public String getMaDH() {
        return maDH;
    }

    public String getNgayLap() {
        return ngayLap;
    }
}
