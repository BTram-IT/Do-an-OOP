import java.util.ArrayList;
import java.util.Scanner;

public class Hoadonban extends HoaDon {

    private String MaKH;
    private ArrayList<CTHoaDon> CTHoaDon;
    private ThanhToan thanhtoan;
    private ArrayList<LoXuat> cacLoDaXuat;

    private DSSanPham dsSanPham;
    private DSSanPhamKhuyenMai dsSPKM;
    private DSKhuyenMai dsKM;

    // Constructor mặc định
    public Hoadonban() {
        MaKH = "";
        CTHoaDon = new ArrayList<>();
        cacLoDaXuat = new ArrayList<>();
        thanhtoan = null;

        dsSanPham = null;
        dsSPKM = null;
        dsKM = null;
    }

    // Constructor đầy đủ theo cấu trúc cũ
    public Hoadonban(String maKH, ArrayList<CTHoaDon> CTHoaDon,
                     ThanhToan thanhtoan) {

        this();

        setMaKH(maKH);
        setCTHoaDon(CTHoaDon);
        this.thanhtoan = thanhtoan;
    }

    // Constructor có đầy đủ dữ liệu để tính tiền
    public Hoadonban(String maKH, ArrayList<CTHoaDon> CTHoaDon,
                     ThanhToan thanhtoan, DSSanPham dsSanPham,
                     DSSanPhamKhuyenMai dsSPKM, DSKhuyenMai dsKM) {

        this(maKH, CTHoaDon, thanhtoan);

        this.dsSanPham = dsSanPham;
        this.dsSPKM = dsSPKM;
        this.dsKM = dsKM;
    }

    // Nhập thông tin hóa đơn
    public void Nhap(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner không hợp lệ!");
        }

        MaKH = Ktradulieu.nhapChuoi(
                sc,
                "Nhập mã khách hàng: ",
                ".+",
                "Mã khách hàng không được để trống!"
        );
    }

    // Tính tổng tiền hàng trước khuyến mãi
    public double tinhTongTienTruocKM() {

        double tongTien = 0;

        if (CTHoaDon == null) {
            return 0;
        }

        for (CTHoaDon ct : CTHoaDon) {
            if (ct != null) {
                tongTien += ct.tinhTienHang();
            }
        }

        return tongTien;
    }

    // Tính tổng tiền hàng sau khuyến mãi
    public double tinhTongTienSauKM() {

        if (CTHoaDon == null) {
            return 0;
        }

        double tongTienSauKM = 0;

        // Tổng tiền trước khuyến mãi dùng để xét mức chi tiêu tối thiểu
        double tongTienTruocKM = tinhTongTienTruocKM();

        for (CTHoaDon ct : CTHoaDon) {

            if (ct == null) {
                continue;
            }

            tongTienSauKM += ct.tinhTienSauKM(
                    dsSPKM,
                    dsKM,
                    tongTienTruocKM
            );
        }

        return tongTienSauKM;
    }

    // Tính tổng thuế của hóa đơn
    public double Tongthuedonhang(DSSanPham dssanpham) {

        if (CTHoaDon == null || dssanpham == null) {
            return 0;
        }

        double tongThue = 0;

        double tongTienTruocKM = tinhTongTienTruocKM();

        for (CTHoaDon ct : CTHoaDon) {

            if (ct == null) {
                continue;
            }

            // Tìm sản phẩm theo mã trong chi tiết hóa đơn
            SanPham sp = dssanpham.timTheoMa(ct.getMaSP());

            if (sp == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy sản phẩm có mã: " + ct.getMaSP()
                );
            }

            // Tính thuế trên giá sau khuyến mãi
            tongThue += ct.tinhTienThue(
                    sp,
                    dsSPKM,
                    dsKM,
                    tongTienTruocKM
            );
        }

        return tongThue;
    }

    // Tính tổng tiền hóa đơn
    @Override
    public double Tongtienhoadon() {

        if (dsSanPham == null) {
            return 0;
        }

        // Tiền hàng sau khuyến mãi + tổng thuế
        return tinhTongTienSauKM()
                + Tongthuedonhang(dsSanPham);
    }

    // Nhập phương thức thanh toán
    public void nhapThanhToan(Scanner sc, double tongTien) {

        if (sc == null) {
            throw new IllegalArgumentException("Scanner không hợp lệ!");
        }

        if (tongTien < 0 || Double.isNaN(tongTien)
                || Double.isInfinite(tongTien)) {
            throw new IllegalArgumentException("Tổng tiền không hợp lệ!");
        }

        // Chưa thanh toán thì không giữ thông tin thanh toán cũ
        thanhtoan = null;

        while (thanhtoan == null) {

            System.out.println("\n===== PHƯƠNG THỨC THANH TOÁN =====");
            System.out.println("Tổng tiền cần thanh toán: " + tongTien);
            System.out.println("1. Tiền mặt");
            System.out.println("2. Chuyển khoản");

            int chon = Ktradulieu.luaChon(
                    sc,
                    "Chọn phương thức thanh toán: ",
                    1,
                    2
            );

            if (chon == 1) {

                TienMat tienMat = new TienMat();

                tienMat.nhap(sc);

                if (tienMat.thanhToan(tongTien)) {

                    thanhtoan = tienMat;

                    System.out.println("Thanh toán thành công!");
                    System.out.println(
                            "Tiền thừa: " + tienMat.tienThua(tongTien)
                    );

                } else {

                    System.out.println("Khách đưa không đủ tiền!");
                    System.out.println("Vui lòng nhập lại!");
                }

            } else if (chon == 2) {

                String maGiaoDich = Ktradulieu.nhapChuoi(
                        sc,
                        "Nhập mã giao dịch: ",
                        ".+",
                        "Mã giao dịch không được để trống!"
                );

                ChuyenKhoan chuyenKhoan =
                        new ChuyenKhoan(maGiaoDich);

                if (chuyenKhoan.thanhToan(tongTien)) {

                    thanhtoan = chuyenKhoan;

                    System.out.println("Thanh toán chuyển khoản thành công!");

                } else {

                    System.out.println("Thanh toán chuyển khoản thất bại!");
                }
            }
        }
    }

    // Lưu các lô hàng đã xuất
    public void setCacLoDaXuat(ArrayList<LoXuat> cacLoDaXuat) {

        if (cacLoDaXuat == null) {
            this.cacLoDaXuat = new ArrayList<>();
        } else {
            this.cacLoDaXuat = new ArrayList<>(cacLoDaXuat);
        }
    }

    public ArrayList<LoXuat> getCacLoDaXuat() {
        return new ArrayList<>(cacLoDaXuat);
    }

    // Getter và setter
    public String getMaKH() {
        return MaKH;
    }

    public void setMaKH(String maKH) {

        if (maKH == null || maKH.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã khách hàng không được để trống!"
            );
        }

        MaKH = maKH.trim();
    }

    public ArrayList<CTHoaDon> getCTHoaDon() {
        return new ArrayList<>(CTHoaDon);
    }

    public void setCTHoaDon(ArrayList<CTHoaDon> CTHoaDon) {

        if (CTHoaDon == null) {
            this.CTHoaDon = new ArrayList<>();
        } else {
            this.CTHoaDon = new ArrayList<>(CTHoaDon);
        }
    }

    public ThanhToan getThanhtoan() {
        return thanhtoan;
    }

    public void setThanhtoan(ThanhToan thanhtoan) {
        this.thanhtoan = thanhtoan;
    }

    public DSSanPham getDsSanPham() {
        return dsSanPham;
    }

    public void setDsSanPham(DSSanPham dsSanPham) {
        this.dsSanPham = dsSanPham;
    }

    public DSSanPhamKhuyenMai getDsSPKM() {
        return dsSPKM;
    }

    public void setDsSPKM(DSSanPhamKhuyenMai dsSPKM) {
        this.dsSPKM = dsSPKM;
    }

    public DSKhuyenMai getDsKM() {
        return dsKM;
    }

    public void setDsKM(DSKhuyenMai dsKM) {
        this.dsKM = dsKM;
    }

    // In hóa đơn
    @Override
    public void inhoadon() {

        System.out.println("\n========== HÓA ĐƠN BÁN ==========");

        System.out.println("Mã khách hàng: " + MaKH);

        if (CTHoaDon == null || CTHoaDon.isEmpty()) {
            System.out.println("Hóa đơn chưa có sản phẩm!");
            return;
        }

        for (CTHoaDon ct : CTHoaDon) {

            if (ct != null) {
                System.out.println("---------------------------------");
                System.out.println(ct);

                System.out.println(
                        "Tiền sau khuyến mãi: "
                                + ct.tinhTienSauKM(
                                dsSPKM,
                                dsKM,
                                tinhTongTienTruocKM()
                        )
                );
            }
        }

        System.out.println("---------------------------------");

        System.out.println(
                "Tổng tiền hàng trước KM: "
                        + tinhTongTienTruocKM()
        );

        System.out.println(
                "Tổng tiền hàng sau KM: "
                        + tinhTongTienSauKM()
        );

        System.out.println(
                "Tổng thuế: "
                        + Tongthuedonhang(dsSanPham)
        );

        System.out.println("---------------------------------");

        System.out.println(
                "Tổng tiền hóa đơn: "
                        + Tongtienhoadon()
        );

        if (thanhtoan != null) {
            System.out.println("Phương thức thanh toán: "
                    + thanhtoan.getPhuongThuc());

            System.out.println("Thông tin thanh toán:");
            System.out.println(thanhtoan);
        } else {
            System.out.println("Hóa đơn chưa thanh toán!");
        }

        System.out.println("=================================");
    }
}