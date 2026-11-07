import java.time.LocalDate;
import java.util.Scanner;

public class CTGioHang {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private long DonGia;

    // Constructor mặc định
    public CTGioHang() {
        this.MaSP = "";
        this.TenSP = "";
        this.Soluong = 0;
        this.DonGia = 0;
    }

<<<<<<< HEAD
    // Constructor tương thích 2 tham số
    public CTGioHang(String maSP, int soluong) {
        this();
        setMaSP(maSP);
        setSoluong(soluong);
    }

    // Constructor đầy đủ tham số
=======
    // Constructor có tham số
>>>>>>> origin/main
    public CTGioHang(String maSP, String tenSP, int soluong, long donGia) {
        this();
        setMaSP(maSP);
        setTenSP(tenSP);
        setSoluong(soluong);
        setDonGia(donGia);
    }

    // Getter
<<<<<<< HEAD
    public String getMaSP() { return MaSP; }
    public String getTenSP() { return TenSP; }
    public int getSoluong() { return Soluong; }
    public int getSoLuong() { return Soluong; }
    public long getDonGia() { return DonGia; }
=======
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
>>>>>>> origin/main

    // Setter
    public void setMaSP(String maSP) {
        if (maSP == null || maSP.trim().isEmpty()) {
<<<<<<< HEAD
            throw new IllegalArgumentException("Mã sản phẩm không được để trống.");
=======
            throw new IllegalArgumentException(
                    "Mã sản phẩm không được để trống."
            );
>>>>>>> origin/main
        }
        this.MaSP = SanPham.chuanHoaMa(maSP);
    }

    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.trim().isEmpty()) {
<<<<<<< HEAD
            throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
=======
            throw new IllegalArgumentException(
                    "Tên sản phẩm không được để trống."
            );
>>>>>>> origin/main
        }
        this.TenSP = tenSP.trim();
    }

    public void setSoluong(int soluong) {
        if (soluong <= 0) {
<<<<<<< HEAD
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
=======
            throw new IllegalArgumentException(
                    "Số lượng phải lớn hơn 0."
            );
>>>>>>> origin/main
        }
        this.Soluong = soluong;
    }

<<<<<<< HEAD
    public void setSoLuong(int soluong) {
        setSoluong(soluong);
    }

    public void setDonGia(long donGia) {
        if (donGia <= 0 || donGia > SanPham.GIA_TOI_DA) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0 và không vượt quá " + SanPham.GIA_TOI_DA + " đồng.");
=======
    public void setDonGia(long donGia) {
        if (donGia <= 0 || donGia > SanPham.GIA_TOI_DA) {
            throw new IllegalArgumentException(
                    "Đơn giá phải lớn hơn 0 và không vượt quá "
                            + SanPham.GIA_TOI_DA + " đồng."
            );
>>>>>>> origin/main
        }
        this.DonGia = donGia;
    }

<<<<<<< HEAD
    // Tính thành tiền chưa thuế
=======
    // Tính tổng tiền sản phẩm (chưa thuế)
>>>>>>> origin/main
    public long TongTien() {
        return Math.multiplyExact((long) Soluong, DonGia);
    }

<<<<<<< HEAD
    // Nhập chi tiết giỏ hàng - Sử dụng Ktradulieu
    public void Nhap(Scanner sc, DSSanPham dsSP) {
        if (sc == null || dsSP == null) {
            throw new IllegalArgumentException("Scanner và danh sách sản phẩm không được null.");
        }

        SanPham sp;
        LocalDate ngayBan = LocalDate.now();

        // Tìm sản phẩm còn hàng bán được
=======
    // Nhập chi tiết giỏ hàng
    public void Nhap(Scanner sc, DSSanPham dsSP) {
        if (sc == null || dsSP == null) {
            throw new IllegalArgumentException(
                    "Scanner và danh sách sản phẩm không được null."
            );
        }

        SanPham sp;

        // Nhập và tìm sản phẩm còn hàng bán được
>>>>>>> origin/main
        while (true) {
            String ma = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhập mã sản phẩm: ",
                    ".+",
                    "Mã sản phẩm không được để trống!"
            );

            ma = SanPham.chuanHoaMa(ma);
            sp = dsSP.timTheoMa(ma);

            if (sp == null) {
                System.out.println("Không tìm thấy sản phẩm! Vui lòng nhập lại.");
                continue;
            }

<<<<<<< HEAD
            int coTheBan = sp.getSoLuongBanDuoc(ngayBan);
            if (coTheBan <= 0) {
                System.out.println("Sản phẩm " + sp.getTenSP() + " hiện không còn hàng bán được!");
                continue;
            }
            break;
        }

        // Lưu thông tin sản phẩm vào chi tiết giỏ hàng
=======
            int coTheBan = sp.getSoLuongBanDuoc(LocalDate.now());
            if (coTheBan <= 0) {
                System.out.println("Sản phẩm " + sp.getTenSP() + " hiện đã hết hàng còn hạn! Vui lòng chọn sản phẩm khác.");
                continue;
            }

            break;
        }

        // Lấy thông tin sản phẩm
>>>>>>> origin/main
        setMaSP(sp.getMaSP());
        setTenSP(sp.getTenSP());
        setDonGia(sp.getGiaBan());

<<<<<<< HEAD
        // Nhập số lượng và kiểm tra hàng còn bán được qua Ktradulieu
        while (true) {
            long soLuongNhap = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng: ");
            int soLuongConBan = sp.getSoLuongBanDuoc(ngayBan);

            if (soLuongNhap > Integer.MAX_VALUE) {
                System.out.println("Số lượng vượt quá giới hạn cho phép!");
                continue;
            }

            if (soLuongNhap <= 0) {
                System.out.println("Số lượng phải lớn hơn 0!");
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

=======
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

            System.out.println("Số lượng không hợp lệ hoặc không đủ hàng còn hạn!");
            System.out.println("Số lượng hiện có thể bán: " + sp.getSoLuongBanDuoc(LocalDate.now()));
        }
    }

>>>>>>> origin/main
    // Hiển thị chi tiết giỏ hàng
    public void Xuat() {
        System.out.println("Mã sản phẩm: " + MaSP);
        System.out.println("Tên sản phẩm: " + TenSP);
        System.out.println("Số lượng: " + Soluong);
<<<<<<< HEAD
        System.out.println("Đơn giá: " + Ktradulieu.dinhDangTien(DonGia));
        System.out.println("Thành tiền: " + Ktradulieu.dinhDangTien(TongTien()));
    }

    public void xuat(DSSanPham dsSP) {
        Xuat();
=======
        System.out.println("Đơn giá: " + DonGia + " đ");
        System.out.println("Thành tiền: " + TongTien() + " đ");
>>>>>>> origin/main
    }

    @Override
    public String toString() {
        return MaSP + " | " + TenSP
                + " | Số lượng: " + Soluong
<<<<<<< HEAD
                + " | Đơn giá: " + Ktradulieu.dinhDangTien(DonGia)
                + " | Thành tiền: " + Ktradulieu.dinhDangTien(TongTien());
=======
                + " | Đơn giá: " + DonGia + " đ"
                + " | Thành tiền: " + TongTien() + " đ";
>>>>>>> origin/main
    }
}