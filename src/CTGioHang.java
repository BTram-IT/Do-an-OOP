import java.util.Scanner;

public class CTGioHang {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private long DonGia;

    // Constructor mặc định
    public CTGioHang() {
        MaSP = "";
        TenSP = "";
        Soluong = 0;
        DonGia = 0;
    }

    // Constructor có tham số
    public CTGioHang(String maSP, String tenSP, int soluong, long donGia) {
        setMaSP(maSP);
        setTenSP(tenSP);
        setSoluong(soluong);
        setDonGia(donGia);
    }

    // Getter
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

    // Setter
    public void setMaSP(String maSP) {
        if (maSP == null || maSP.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã sản phẩm không được để trống."
            );
        }

        this.MaSP = SanPham.chuanHoaMa(maSP);
    }

    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Tên sản phẩm không được để trống."
            );
        }

        this.TenSP = tenSP.trim();
    }

    public void setSoluong(int soluong) {
        if (soluong <= 0) {
            throw new IllegalArgumentException(
                    "Số lượng phải lớn hơn 0."
            );
        }

        this.Soluong = soluong;
    }

    public void setDonGia(long donGia) {
        if (donGia <= 0 || donGia > SanPham.GIA_TOI_DA) {
            throw new IllegalArgumentException(
                    "Đơn giá phải lớn hơn 0 và không vượt quá "
                            + SanPham.GIA_TOI_DA + " đồng."
            );
        }

        this.DonGia = donGia;
    }

    // Tính tổng tiền sản phẩm (chưa thuế)
    public long TongTien() {
        return Math.multiplyExact((long) Soluong, DonGia);
    }

    // Nhập chi tiết giỏ hàng
    public void Nhap(Scanner sc, DSSanPham dsSP) {
        if (sc == null || dsSP == null) {
            throw new IllegalArgumentException(
                    "Scanner và danh sách sản phẩm không được null."
            );
        }

        SanPham sp;

        // Nhập và tìm sản phẩm
        while (true) {
            String ma = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhập mã sản phẩm: ",
                    "^SP\\d{3}$",
                    "Mã sản phẩm không đúng định dạng!"
            );

            ma = SanPham.chuanHoaMa(ma);

            sp = dsSP.timTheoMa(ma);

            if (sp != null) {
                break;
            }

            System.out.println(
                    "Không tìm thấy sản phẩm! Vui lòng nhập lại."
            );
        }

        // Lấy thông tin sản phẩm
        setMaSP(sp.getMaSP());
        setTenSP(sp.getTenSP());
        setDonGia(sp.getGiaBan());

        // Nhập số lượng và kiểm tra tồn kho
        while (true) {
            int soLuong = (int) Ktradulieu.nhapSoNguyen(
                    sc,
                    "Nhập số lượng: "
            );

            if (sp.kiemTraTonKho(soLuong)) {
                setSoluong(soLuong);
                break;
            }

            System.out.println(
                    "Số lượng không hợp lệ hoặc không đủ hàng còn hạn!"
            );

            System.out.println(
                    "Số lượng hiện có thể bán: "
                            + sp.getSoLuongBanDuoc(java.time.LocalDate.now())
            );
        }
    }

    // Hiển thị chi tiết giỏ hàng
    public void Xuat() {
        System.out.println("Mã sản phẩm: " + MaSP);
        System.out.println("Tên sản phẩm: " + TenSP);
        System.out.println("Số lượng: " + Soluong);
        System.out.println("Đơn giá: " + TienIch.dinhDangTien(DonGia));
        System.out.println("Thành tiền: " + TienIch.dinhDangTien(TongTien()));
    }

    @Override
    public String toString() {
        return MaSP + " | " + TenSP
                + " | Số lượng: " + Soluong
                + " | Đơn giá: " + TienIch.dinhDangTien(DonGia)
                + " | Thành tiền: " + TienIch.dinhDangTien(TongTien());
    }
}