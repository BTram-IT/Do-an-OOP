import java.util.Scanner;

public class CTHoaDon {
    private SanPham sanPham;
    private int soLuong;
    private double donGia;
    private double thueDonVi;
    private double thanhTien;

    // Constructor mac dinh
    public CTHoaDon() {
        this.sanPham = null;
        this.soLuong = 0;
        this.donGia = 0;
        this.thueDonVi = 0;
        this.thanhTien = 0;
    }

    // Tao chi tiet hoa don tu chi tiet don hang
    public CTHoaDon(ChiTietDonHang ctdh) {
        this();

        if (ctdh != null) {
            this.sanPham = ctdh.getSanPham();
            this.soLuong = ctdh.getSoLuong();
            capNhatThanhTien();
        }
    }

    // Constructor tu san pham va so luong
    public CTHoaDon(SanPham sanPham, int soLuong) {
        this();

        if (sanPham != null && soLuong > 0) {
            this.sanPham = sanPham;
            this.soLuong = soLuong;
            capNhatThanhTien();
        }
    }

    // Constructor sao chep
    public CTHoaDon(CTHoaDon other) {
        this();

        if (other != null) {
            this.sanPham = other.sanPham;
            this.soLuong = other.soLuong;
            this.donGia = other.donGia;
            this.thueDonVi = other.thueDonVi;
            this.thanhTien = other.thanhTien;
        }
    }

    // Cap nhat don gia, thue va thanh tien
    private void capNhatThanhTien() {
        if (sanPham == null) {
            donGia = 0;
            thueDonVi = 0;
            thanhTien = 0;
            return;
        }

        donGia = sanPham.getGiaBan();
        thueDonVi = sanPham.tinhThue();

        // Gia sau thue cua mot san pham nhan voi so luong
        thanhTien = (donGia + thueDonVi) * soLuong;
    }

    // Getter va setter
    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
        capNhatThanhTien();
    }

    public String getMaSP() {
        return sanPham != null ? sanPham.getMaSP() : "";
    }

    public String getTenSP() {
        return sanPham != null ? sanPham.getTenSP() : "";
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong >= 0) {
            this.soLuong = soLuong;
            capNhatThanhTien();
        }
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (donGia >= 0) {
            this.donGia = donGia;
            this.thanhTien = (this.donGia + this.thueDonVi) * this.soLuong;
        }
    }

    public double getThueDonVi() {
        return thueDonVi;
    }

    public void setThueDonVi(double thueDonVi) {
        if (thueDonVi >= 0) {
            this.thueDonVi = thueDonVi;
            this.thanhTien = (this.donGia + this.thueDonVi) * this.soLuong;
        }
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        if (thanhTien >= 0) {
            this.thanhTien = thanhTien;
        }
    }

    // Nhap chi tiet hoa don bang ma san pham
    // Khong tu dong tru kho tai day
    public void nhap(Scanner sc) {
        System.out.print("Nhap ma SP: ");
        String ma = sc.nextLine().trim();

        if (QuanLyCuaHang.dsSanPham == null) {
            System.out.println("Danh sach san pham chua duoc khoi tao!");
            return;
        }

        SanPham spTimThay =
                QuanLyCuaHang.dsSanPham.timTheoMa(ma);

        if (spTimThay == null) {
            System.out.println("Khong tim thay san pham trong kho!");
            return;
        }

        this.sanPham = spTimThay;

        System.out.println("Da tim thay san pham: "
                + sanPham.getTenSP());

        System.out.println("Don gia: " + sanPham.getGiaBan());
        System.out.println("Thue don vi: " + sanPham.tinhThue());

        System.out.print("Nhap so luong mua: ");
        try {
            int sl = Integer.parseInt(sc.nextLine().trim());

            if (sl <= 0) {
                System.out.println("So luong phai lon hon 0!");
                this.sanPham = null;
                return;
            }

            this.soLuong = sl;
            capNhatThanhTien();

        } catch (NumberFormatException e) {
            System.out.println("So luong khong hop le!");
            this.sanPham = null;
            this.soLuong = 0;
            capNhatThanhTien();
        }
    }

    // Xuat chi tiet hoa don
    public void xuat() {
        System.out.printf(
                "   * %-5s | %-26s | SL: %-2d | Gia: %,10.0f"
                        + " | Thue: %,8.0f | Thanh tien: %,12.0f VND%n",
                getMaSP(),
                getTenSP(),
                soLuong,
                donGia,
                thueDonVi,
                thanhTien
        );
    }
}