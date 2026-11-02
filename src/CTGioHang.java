
import java.time.LocalDate;
import java.util.Scanner;

public class CTGioHang {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private long DonGia;

    public CTGioHang() {
        MaSP = "";
        TenSP = "";
        Soluong = 0;
        DonGia = 0;
    }

    public CTGioHang(String maSP, String tenSP, int soluong, long donGia) {
        setMaSP(maSP);
        setTenSP(tenSP);
        setSoluong(soluong);
        setDonGia(donGia);
    }

    public String getMaSP() {
        return MaSP;
    }

    public String getTenSP() {
        return TenSP;
    }

    public int getSoluong() {
        return Soluong;
    }

    public long getDonGia() {
        return DonGia;
    }

    public void setMaSP(String maSP) {
        if (maSP == null || !maSP.trim().matches("(?i)(TA|DU)\\d{3}")) {
            throw new IllegalArgumentException("Ma san pham khong hop le!");
        }

        this.MaSP = SanPham.chuanHoaMa(maSP);
    }

    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten san pham khong duoc de trong!");
        }

        this.TenSP = tenSP.trim();
    }

    public void setSoluong(int soluong) {
        if (soluong <= 0) {
            throw new IllegalArgumentException("So luong phai lon hon 0!");
        }

        this.Soluong = soluong;
    }

    public void setDonGia(long donGia) {
        if (donGia <= 0) {
            throw new IllegalArgumentException("Don gia phai lon hon 0!");
        }

        this.DonGia = donGia;
    }

    public long tongtien() {
        return Math.multiplyExact((long) Soluong, DonGia);
    }

    // Giu ten cu de khong anh huong code dang goi TongTien()
    public long TongTien() {
        return tongtien();
    }

    // Nhap san pham vao gio hang
    public void Nhap(Scanner sc, DSSanPham dsSP) {
        if (sc == null || dsSP == null) {
            throw new IllegalArgumentException("Du lieu nhap khong hop le!");
        }

        // Nhap ma san pham bang Ktradulieu
        String ma = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma san pham (TA### hoac DU###): ",
                "(?i)(TA|DU)\\d{3}",
                "Ma san pham khong hop le!"
        );

        // Tim san pham trong danh sach
        SanPham sp = dsSP.timTheoMa(ma);

        while (sp == null) {
            System.out.println("Khong tim thay san pham!");

            ma = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhap lai ma san pham: ",
                    "(?i)(TA|DU)\\d{3}",
                    "Ma san pham khong hop le!"
            );

            sp = dsSP.timTheoMa(ma);
        }

        // Kiem tra so luong ton kho
        long tonKho = dsSP.layTonBanDuoc(ma, LocalDate.now());

        long soLuong;

        do {
            soLuong = Ktradulieu.nhapSoNguyen(
                    sc,
                    "Nhap so luong: "
            );

            if (soLuong > Integer.MAX_VALUE) {
                System.out.println("So luong qua lon! Vui long nhap lai.");
            } else if (soLuong > tonKho) {
                System.out.println("So luong ton kho khong du! Ton kho hien tai: " + tonKho);
            }

        } while (soLuong > Integer.MAX_VALUE || soLuong > tonKho);

        // Gan thong tin san pham vao gio hang
        this.MaSP = sp.getMaSP();
        this.TenSP = sp.getTenSP();
        this.Soluong = (int) soLuong;
        this.DonGia = sp.getGiaBan();
    }

    // Giu lai phuong thuc Nhap() cu de tuong thich code
    public void Nhap() {
        System.out.println("Hay goi Nhap(Scanner, DSSanPham) de nhap san pham.");
    }

    @Override
    public String toString() {
        return "MaSP: " + MaSP
                + " | TenSP: " + TenSP
                + " | So luong: " + Soluong
                + " | Don gia: " + String.format("%,d", DonGia)
                + " VND | Thanh tien: " + String.format("%,d", tongtien())
                + " VND";
    }
}
