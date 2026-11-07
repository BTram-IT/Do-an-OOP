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

    // Constructor tương thích 2 tham số
    public CTGioHang(String maSP, int soluong) {
        this();
        setMaSP(maSP);
        setSoluong(soluong);
    }

    // Constructor đầy đủ
    public CTGioHang(String maSP, String tenSP, int soluong, long donGia) {
        this();
        setMaSP(maSP);
        setTenSP(tenSP);
        setSoluong(soluong);
        setDonGia(donGia);
    }

    // Getter
    public String getMaSP() { return MaSP; }
    public String getTenSP() { return TenSP; }
    public int getSoluong() { return Soluong; }
    public int getSoLuong() { return Soluong; }
    public long getDonGia() { return DonGia; }

    // Setter
    public void setMaSP(String maSP) {
        if (maSP == null || maSP.trim().isEmpty())
            throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
        this.MaSP = SanPham.chuanHoaMa(maSP);
    }

    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.trim().isEmpty())
            throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
        this.TenSP = tenSP.trim();
    }

    public void setSoluong(int soluong) {
        if (soluong <= 0)
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        this.Soluong = soluong;
    }

    public void setSoLuong(int soluong) {
        setSoluong(soluong);
    }

    public void setDonGia(long donGia) {
        if (donGia <= 0 || donGia > SanPham.GIA_TOI_DA)
            throw new IllegalArgumentException(
                    "Đơn giá phải lớn hơn 0 và không vượt quá "
                            + SanPham.GIA_TOI_DA + " đồng."
            );
        this.DonGia = donGia;
    }

    // Tính thành tiền
    public long TongTien() {
        return Math.multiplyExact((long) Soluong, DonGia);
    }

    // Nhập chi tiết giỏ hàng
    public void Nhap(Scanner sc, DSSanPham dsSP) {
        if (sc == null || dsSP == null)
            throw new IllegalArgumentException("Scanner và danh sách sản phẩm không được null.");

        SanPham sp;
        LocalDate ngayBan = LocalDate.now();

        // Tìm sản phẩm còn hàng bán được
        while (true) {
            String ma = Ktradulieu.nhapChuoi(
                    sc, "Nhập mã sản phẩm: ",
                    ".+", "Mã sản phẩm không được để trống!"
            );

            ma = SanPham.chuanHoaMa(ma);
            sp = dsSP.timTheoMa(ma);

            if (sp == null) {
                System.out.println("Không tìm thấy sản phẩm! Vui lòng nhập lại.");
                continue;
            }

            int coTheBan = sp.getSoLuongBanDuoc(ngayBan);

            if (coTheBan <= 0) {
                System.out.println("Sản phẩm " + sp.getTenSP()
                        + " hiện không còn hàng bán được!");
                continue;
            }
            break;
        }

        // Lưu thông tin sản phẩm
        setMaSP(sp.getMaSP());
        setTenSP(sp.getTenSP());
        setDonGia(sp.getGiaBan());

        // Nhập số lượng theo tồn kho còn hạn
        while (true) {
            long soLuongNhap = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng: ");
            int soLuongConBan = sp.getSoLuongBanDuoc(ngayBan);

            if (soLuongNhap > Integer.MAX_VALUE) {
                System.out.println("Số lượng vượt quá giới hạn cho phép!");
                continue;
            }

            if (soLuongNhap > soLuongConBan) {
                System.out.println("Số lượng vượt quá hàng còn tồn!");
                System.out.println("Số lượng hiện có thể bán: " + soLuongConBan);
                continue;
            }

            setSoluong((int) soLuongNhap);
            break;
        }
    }

    public void nhap(Scanner sc, DSSanPham dsSP) {
        Nhap(sc, dsSP);
    }

    // Xuất chi tiết giỏ hàng
    public void Xuat() {
        System.out.println("Mã sản phẩm: " + MaSP);
        System.out.println("Tên sản phẩm: " + TenSP);
        System.out.println("Số lượng: " + Soluong);
        System.out.println("Đơn giá: " + Ktradulieu.dinhDangTien(DonGia));
        System.out.println("Thành tiền: " + Ktradulieu.dinhDangTien(TongTien()));
    }

    public void xuat(DSSanPham dsSP) {
        Xuat();
    }

    @Override
    public String toString() {
        return MaSP + " | " + TenSP
                + " | Số lượng: " + Soluong
                + " | Đơn giá: " + Ktradulieu.dinhDangTien(DonGia)
                + " | Thành tiền: " + Ktradulieu.dinhDangTien(TongTien());
    }
}