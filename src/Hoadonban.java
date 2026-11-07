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
        super();
        this.MaKH = "";
        this.CTHoaDon = new ArrayList<>();
        this.cacLoDaXuat = new ArrayList<>();
        this.thanhtoan = null;

        this.dsSanPham = null;
        this.dsSPKM = null;
        this.dsKM = null;
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

    @Override
    public void setMaHD(String maHD) {
        super.setMaHD(maHD);
        if (this.CTHoaDon != null && getMaHD() != null && !getMaHD().isEmpty()) {
            for (CTHoaDon ct : this.CTHoaDon) {
                if (ct != null) {
                    ct.setMaHD(getMaHD());
                }
            }
        }
    }

    // Nhập thông tin hóa đơn
    public void Nhap(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner không hợp lệ!");
        }

        if (getMaHD() == null || getMaHD().isEmpty()) {
            String maHD = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhập mã hóa đơn: ",
                    ".+",
                    "Mã hóa đơn không được để trống!"
            );
            setMaHD(maHD);
        }

        if (getMaNV() == null || getMaNV().isEmpty()) {
            String maNV = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhập mã nhân viên lập hóa đơn: ",
                    ".+",
                    "Mã nhân viên không được để trống!"
            );
            setMaNV(maNV);
        }

        String maKH = Ktradulieu.nhapChuoi(
                sc,
                "Nhập mã khách hàng: ",
                ".+",
                "Mã khách hàng không được để trống!"
        );
        setMaKH(maKH);
    }

    // Kiểm tra tất cả chi tiết đã chốt thanh toán chưa
    private boolean daChotTatCaChiTiet() {
        if (CTHoaDon == null || CTHoaDon.isEmpty()) {
            return false;
        }

        for (CTHoaDon ct : CTHoaDon) {
            if (ct == null || !ct.isDaChotThanhToan()) {
                return false;
            }
        }

        return true;
    }

    // Tổng tiền toàn bộ sản phẩm trước khuyến mãi
    public long tinhTongTienTruocKM() {
        long TongTienTruocKM = 0;

        for (CTHoaDon ct : CTHoaDon) {
            if (ct != null) {
                TongTienTruocKM = Math.addExact(
                        TongTienTruocKM,
                        ct.TinhTienHang1SP()
                );
            }
        }

        return TongTienTruocKM;
    }

    // Tổng tiền hàng sau khuyến mãi
    public long tinhTongTienSauKM() {
        if (daChotTatCaChiTiet()) {
            long tongSauKM = 0;
            for (CTHoaDon ct : CTHoaDon) {
                tongSauKM = Math.addExact(
                        tongSauKM,
                        ct.TinhTienHang1SP() - ct.getTienGiamThucTe()
                );
            }
            return tongSauKM;
        }

        long tongTienSauKM = 0;
        long TongTienTruocKM = tinhTongTienTruocKM();

        for (CTHoaDon ct : CTHoaDon) {
            if (ct != null) {
                tongTienSauKM = Math.addExact(
                        tongTienSauKM,
                        ct.tinhTienSauKM(
                                dsSPKM,
                                dsKM,
                                TongTienTruocKM
                        )
                );
            }
        }

        return tongTienSauKM;
    }

    // Tổng thuế hóa đơn
    public long Tongthuedonhang(DSSanPham dssanpham) {
        // Nếu hóa đơn đã chốt thì lấy thuế đã lưu (không phụ thuộc dssanpham hiện tại)
        if (daChotTatCaChiTiet()) {
            long tongThue = 0;
            for (CTHoaDon ct : CTHoaDon) {
                tongThue = Math.addExact(
                        tongThue,
                        ct.getTienThueThucTe()
                );
            }
            return tongThue;
        }

        DSSanPham nguonSP = (dssanpham != null) ? dssanpham : this.dsSanPham;
        if (nguonSP == null) {
            throw new IllegalArgumentException(
                    "Danh sách sản phẩm không được null!"
            );
        }

        long tongThue = 0;
        long TongTienTruocKM = tinhTongTienTruocKM();

        for (CTHoaDon ct : CTHoaDon) {
            if (ct == null) {
                continue;
            }

            SanPham sp = nguonSP.timTheoMa(ct.getMaSP());

            if (sp == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy sản phẩm: " + ct.getMaSP()
                );
            }

            tongThue = Math.addExact(
                    tongThue,
                    ct.tinhTienThue(
                            sp,
                            dsSPKM,
                            dsKM,
                            TongTienTruocKM
                    )
            );
        }

        return tongThue;
    }

    // Tổng tiền hóa đơn
    @Override
    public long Tongtienhoadon() {
        // Hóa đơn đã thanh toán: lấy số liệu đã lưu
        if (daChotTatCaChiTiet()) {
            long tongTien = 0;
            for (CTHoaDon ct : CTHoaDon) {
                tongTien = Math.addExact(
                        tongTien,
                        ct.getTongTienSauThue()
                );
            }
            return tongTien;
        }

        if (dsSanPham == null) {
            throw new IllegalStateException(
                    "Chưa có danh sách sản phẩm!"
            );
        }

        // Hóa đơn chưa thanh toán: tính tạm thời
        return Math.addExact(tinhTongTienSauKM(), Tongthuedonhang(dsSanPham));
    }

    // Nhập phương thức thanh toán
    public void nhapThanhToan(Scanner sc, long tongTien) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner không hợp lệ!");
        }

        if (tongTien < 0) {
            throw new IllegalArgumentException("Tổng tiền không hợp lệ!");
        }

        if (dsSanPham == null) {
            throw new IllegalStateException("Chưa có danh sách sản phẩm!");
        }

        if (CTHoaDon == null || CTHoaDon.isEmpty()) {
            throw new IllegalStateException("Hóa đơn chưa có sản phẩm!");
        }

        // Kiểm tra sản phẩm trước khi thu tiền
        for (CTHoaDon ct : CTHoaDon) {
            if (ct == null) {
                throw new IllegalStateException("Chi tiết hóa đơn không hợp lệ!");
            }

            SanPham sp = dsSanPham.timTheoMa(ct.getMaSP());
            if (sp == null) {
                throw new IllegalArgumentException("Không tìm thấy sản phẩm: " + ct.getMaSP());
            }
        }

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
                    System.out.println("Tiền thừa: " + tienMat.tienThua(tongTien));
                } else {
                    System.out.println("Khách đưa không đủ tiền! Vui lòng nhập lại!");
                }

            } else {
                String maGiaoDich = Ktradulieu.nhapChuoi(
                        sc,
                        "Nhập mã giao dịch: ",
                        ".+",
                        "Mã giao dịch không được để trống!"
                );

                ChuyenKhoan chuyenKhoan = new ChuyenKhoan(maGiaoDich);

                if (chuyenKhoan.thanhToan(tongTien)) {
                    thanhtoan = chuyenKhoan;
                    System.out.println("Thanh toán chuyển khoản thành công!");
                } else {
                    System.out.println("Thanh toán chuyển khoản thất bại!");
                }
            }
        }

        // Chỉ chốt dữ liệu sau khi thanh toán thành công
        long TongTienTruocKM = tinhTongTienTruocKM();

        for (CTHoaDon ct : CTHoaDon) {
            if (!ct.isDaChotThanhToan()) {
                SanPham sp = dsSanPham.timTheoMa(ct.getMaSP());
                ct.chotThanhToan(
                        sp,
                        dsSPKM,
                        dsKM,
                        TongTienTruocKM
                );
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
        this.MaKH = SanPham.chuanHoaMa(maKH);
    }

    public ArrayList<CTHoaDon> getCTHoaDon() {
        return new ArrayList<>(CTHoaDon);
    }

    public void setCTHoaDon(ArrayList<CTHoaDon> CTHoaDon) {
        if (CTHoaDon == null) {
            this.CTHoaDon = new ArrayList<>();
        } else {
            this.CTHoaDon = new ArrayList<>(CTHoaDon);
            if (getMaHD() != null && !getMaHD().isEmpty()) {
                for (CTHoaDon ct : this.CTHoaDon) {
                    if (ct != null && (ct.getMaHD() == null || ct.getMaHD().isEmpty())) {
                        ct.setMaHD(getMaHD());
                    }
                }
            }
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

    public boolean isDaThanhToan() {
        return thanhtoan != null || daChotTatCaChiTiet();
    }
    // In hóa đơn
    @Override
    public void inhoadon() {
        System.out.println("\n========== HÓA ĐƠN BÁN ==========");
        if (getMaHD() != null && !getMaHD().isEmpty()) {
            System.out.println("Mã hóa đơn: " + getMaHD());
        }
        if (getMaNV() != null && !getMaNV().isEmpty()) {
            System.out.println("Mã nhân viên: " + getMaNV());
        }
        if (getThoigianin() != null) {
            System.out.println("Thời gian in: " + getThoigianin());
        }
        System.out.println("Mã khách hàng: " + MaKH);

        if (CTHoaDon == null || CTHoaDon.isEmpty()) {
            System.out.println("Hóa đơn chưa có sản phẩm!");
            return;
        }

        long tongTruocKM = tinhTongTienTruocKM();

        for (CTHoaDon ct : CTHoaDon) {
            if (ct != null) {
                System.out.println("---------------------------------");
                System.out.println(ct);

                if (ct.isDaChotThanhToan()) {
                    System.out.println(
                            "Tiền sau khuyến mãi: "
                                    + (ct.TinhTienHang1SP() - ct.getTienGiamThucTe())
                    );
                } else {
                    System.out.println(
                            "Tiền sau khuyến mãi (tạm tính): "
                                    + ct.tinhTienSauKM(dsSPKM, dsKM, tongTruocKM)
                    );
                }
            }
        }

        System.out.println("---------------------------------");
        System.out.println("Tổng tiền hàng trước KM: " + tongTruocKM);
        System.out.println("Tổng tiền hàng sau KM: " + tinhTongTienSauKM());

        if (daChotTatCaChiTiet() || dsSanPham != null) {
            System.out.println("Tổng thuế: " + Tongthuedonhang(dsSanPham));
            System.out.println("---------------------------------");
            System.out.println("Tổng tiền hóa đơn: " + Tongtienhoadon());
        }

        if (thanhtoan != null) {
            System.out.println("Phương thức thanh toán: " + thanhtoan.getPhuongThuc());
            System.out.println("Thông tin thanh toán:");
            System.out.println(thanhtoan);
        } else {
            System.out.println("Hóa đơn chưa thanh toán!");
        }

        System.out.println("=================================");
    }
}
