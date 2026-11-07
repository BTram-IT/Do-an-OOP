<<<<<<< HEAD

import java.time.LocalDateTime;
=======
>>>>>>> origin/main
import java.util.ArrayList;
import java.util.Scanner;

public class Hoadonban extends HoaDon {
<<<<<<< HEAD
    private String MaKH;
    private ArrayList<CTHoaDon> dsChiTietHoaDon;
    private ThanhToan thanhtoan;
    private ArrayList<LoXuat> cacLoDaXuat;
    private DSSanPham dsSanPham;
    private DSSanPhamKhuyenMai dsSPKM;
    private DSKhuyenMai dsKM;
    private boolean daThanhToanNapTuFile;
=======
>>>>>>> origin/main

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
<<<<<<< HEAD
        MaKH = "";
        dsChiTietHoaDon = new ArrayList<>();
        cacLoDaXuat = new ArrayList<>();
        thanhtoan = null;
        dsSanPham = null;
        dsSPKM = null;
        dsKM = null;
        daThanhToanNapTuFile = false;
    }

    public Hoadonban(String maKH, ArrayList<CTHoaDon> ds, ThanhToan tt) {
        this();
        setMaKH(maKH);
        setCTHoaDon(ds);
        thanhtoan = tt;
    }

    public Hoadonban(String maKH, ArrayList<CTHoaDon> ds, ThanhToan tt,
                     DSSanPham sp, DSSanPhamKhuyenMai spkm, DSKhuyenMai km) {
        this(maKH, ds, tt);
        dsSanPham = sp;
        dsSPKM = spkm;
        dsKM = km;
    }

    public Hoadonban(String maHD, String maNV, LocalDateTime thoiGian,
                     String maKH, ArrayList<CTHoaDon> ds, ThanhToan tt) {
        this();
        setMaHD(maHD);
        setMaNV(maNV);
        setThoigianin(thoiGian);
        setMaKH(maKH);
        setCTHoaDon(ds);
        thanhtoan = tt;
    }

    @Override
    public void setMaHD(String maHD) {
        super.setMaHD(maHD);
        if (dsChiTietHoaDon != null) {
            for (CTHoaDon ct : dsChiTietHoaDon) {
                if (ct != null) ct.setMaHD(getMaHD());
            }
        }
    }

    public void Nhap(Scanner sc) {
        if (sc == null) sc = new Scanner(System.in);

        if (getMaHD() == null || getMaHD().trim().isEmpty())
            setMaHD(Ktradulieu.nhapChuoi(sc, "Nhập mã hóa đơn: ", ".+", "Mã hóa đơn không được để trống!"));

        if (getMaNV() == null || getMaNV().trim().isEmpty())
            setMaNV(Ktradulieu.nhapChuoi(sc, "Nhập mã nhân viên lập hóa đơn: ", ".+", "Mã nhân viên không được để trống!"));

        setMaKH(Ktradulieu.nhapChuoi(sc, "Nhập mã khách hàng: ", ".+", "Mã khách hàng không được để trống!"));
    }

    private boolean daChotTatCaChiTiet() {
        if (dsChiTietHoaDon == null || dsChiTietHoaDon.isEmpty()) return false;
        for (CTHoaDon ct : dsChiTietHoaDon)
            if (ct == null || !ct.isDaChotThanhToan()) return false;
        return true;
    }

    public long tinhTongTienTruocKM() {
        long tong = 0;
        if (dsChiTietHoaDon == null) return 0;
        for (CTHoaDon ct : dsChiTietHoaDon)
            if (ct != null) tong = Math.addExact(tong, ct.TinhTienHang1SP());
        return tong;
    }

    public long tinhTongTienSauKM() {
        long tong = 0;
        if (dsChiTietHoaDon == null) return 0;

        long truocKM = tinhTongTienTruocKM();
        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (ct == null) continue;
            long tien = ct.isDaChotThanhToan()
                    ? ct.TinhTienHang1SP() - ct.getTienGiamThucTe()
                    : ct.tinhTienSauKM(dsSPKM, dsKM, truocKM);
            tong = Math.addExact(tong, tien);
        }
        return tong;
    }

    public long Tongthuedonhang(DSSanPham dssanpham) {
        if (dsChiTietHoaDon == null) return 0;

        if (daChotTatCaChiTiet()) {
            long tong = 0;
            for (CTHoaDon ct : dsChiTietHoaDon)
                tong = Math.addExact(tong, ct.getTienThueThucTe());
            return tong;
        }

        DSSanPham nguonSP = dssanpham != null ? dssanpham : dsSanPham;
        if (nguonSP == null) return 0;

        long tong = 0, truocKM = tinhTongTienTruocKM();
        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (ct == null) continue;
            SanPham sp = nguonSP.timTheoMa(ct.getMaSP());
            if (sp == null) throw new IllegalStateException("Không tìm thấy sản phẩm: " + ct.getMaSP());
            tong = Math.addExact(tong, ct.tinhTienThue(sp, dsSPKM, dsKM, truocKM));
        }
        return tong;
    }

    @Override
    public long Tongtienhoadon() {
        if (daChotTatCaChiTiet()) {
            long tong = 0;
            for (CTHoaDon ct : dsChiTietHoaDon)
                tong = Math.addExact(tong, ct.getTongTienSauThue());
            return tong;
        }
        return Math.addExact(tinhTongTienSauKM(), Tongthuedonhang(dsSanPham));
    }

    @Override
    public long tinhTongTien() {
        return Tongtienhoadon();
    }

    public double getTongTien() {
        return (double) Tongtienhoadon();
    }

    public void themChiTiet(CTHoaDon ct) {
        if (ct == null) return;
        if (dsChiTietHoaDon == null) dsChiTietHoaDon = new ArrayList<>();
        if (getMaHD() != null && !getMaHD().isEmpty()) ct.setMaHD(getMaHD());
        dsChiTietHoaDon.add(ct);
    }

    public void themGioHang(CTGioHang gh) {
        if (gh == null) return;
        themChiTiet(new CTHoaDon(gh.getMaSP(), gh.getTenSP(), gh.getSoluong(), gh.getDonGia(), getMaHD()));
    }

    public void nhapThanhToan(Scanner sc, long tongTien) {
        if (sc == null) sc = new Scanner(System.in);
        if (tongTien < 0) throw new IllegalArgumentException("Tổng tiền không hợp lệ!");
        if (dsChiTietHoaDon == null || dsChiTietHoaDon.isEmpty())
            throw new IllegalStateException("Hóa đơn chưa có sản phẩm!");
        if (isDaThanhToan()) throw new IllegalStateException("Hóa đơn đã thanh toán!");

        // Kiểm tra sản phẩm và dữ liệu trước khi nhận thanh toán.
        if (dsSanPham == null) throw new IllegalStateException("Chưa có danh sách sản phẩm!");
        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (ct == null) throw new IllegalStateException("Chi tiết hóa đơn không hợp lệ!");
            if (dsSanPham.timTheoMa(ct.getMaSP()) == null)
                throw new IllegalStateException("Không tìm thấy sản phẩm: " + ct.getMaSP());
        }

        int chon;
        do {
            System.out.println("\n===== PHƯƠNG THỨC THANH TOÁN =====");
            System.out.println("Số tiền cần thanh toán: " + Ktradulieu.dinhDangTien(tongTien));
            System.out.println("1. Tiền mặt\n2. Chuyển khoản");
            chon = Ktradulieu.luaChon(sc, "Chọn phương thức (1-2): ", 1, 2);

            if (chon == 1) {
                TienMat tm = new TienMat(getMaHD());
                tm.setMaNV(getMaNV());
                if (tm.thanhToan(sc, tongTien)) {
                    thanhtoan = tm;
                    System.out.println("Thanh toán thành công! Tiền thối: " + Ktradulieu.dinhDangTien(tm.getTienThoi()));
                } else System.out.println("Khách đưa chưa đủ tiền!");
            } else {
                ChuyenKhoan ck = new ChuyenKhoan(getMaHD());
                ck.setMaNV(getMaNV());
                if (ck.thanhToan(sc, tongTien)) {
                    thanhtoan = ck;
                    System.out.println("Thanh toán chuyển khoản thành công!");
                } else System.out.println("Thanh toán chuyển khoản không hợp lệ!");
            }
        } while (thanhtoan == null);

        long truocKM = tinhTongTienTruocKM();
        for (CTHoaDon ct : dsChiTietHoaDon) {
            if (!ct.isDaChotThanhToan()) {
                SanPham sp = dsSanPham.timTheoMa(ct.getMaSP());
                ct.chotThanhToan(sp, dsSPKM, dsKM, truocKM);
            }
        }
    }

    public void setCacLoDaXuat(ArrayList<LoXuat> ds) {
        cacLoDaXuat = ds == null ? new ArrayList<>() : new ArrayList<>(ds);
    }

    public ArrayList<LoXuat> getCacLoDaXuat() {
        return new ArrayList<>(cacLoDaXuat);
    }

    public String getMaKH() {
        return MaKH;
    }

    public void setMaKH(String maKH) {
        if (maKH == null || maKH.trim().isEmpty())
            throw new IllegalArgumentException("Mã khách hàng không được để trống!");
        MaKH = Ktradulieu.chuanHoaMa(maKH);
    }

    public ArrayList<CTHoaDon> getCTHoaDon() {
        return new ArrayList<>(dsChiTietHoaDon);
    }

    public void setCTHoaDon(ArrayList<CTHoaDon> ds) {
        dsChiTietHoaDon = ds == null ? new ArrayList<>() : new ArrayList<>(ds);
        for (CTHoaDon ct : dsChiTietHoaDon)
            if (ct != null && getMaHD() != null && !getMaHD().isEmpty()) ct.setMaHD(getMaHD());
    }

    public ThanhToan getThanhtoan() {
        return thanhtoan;
    }

    public void setThanhtoan(ThanhToan tt) {
        thanhtoan = tt;
        daThanhToanNapTuFile = false;
    }

    public DSSanPham getDsSanPham() {
        return dsSanPham;
    }

    public void setDsSanPham(DSSanPham ds) {
        dsSanPham = ds;
    }

    public DSSanPhamKhuyenMai getDsSPKM() {
        return dsSPKM;
    }

    public void setDsSPKM(DSSanPhamKhuyenMai ds) {
        dsSPKM = ds;
    }

    public DSKhuyenMai getDsKM() {
        return dsKM;
    }

    public void setDsKM(DSKhuyenMai ds) {
        dsKM = ds;
    }

    @Override
    public boolean isDaThanhToan() {
        return daThanhToanNapTuFile || (thanhtoan != null && thanhtoan.isThanhCong());
    }

    @Override
    public void inhoadon() {
        System.out.println("\n========== HÓA ĐƠN BÁN HÀNG ==========");
        System.out.println("Mã hóa đơn: " + getMaHD());
        System.out.println("Mã nhân viên: " + getMaNV());
        System.out.println("Thời gian: " + getThoigianin());
        System.out.println("Mã khách: " + MaKH);

        if (dsChiTietHoaDon == null || dsChiTietHoaDon.isEmpty()) {
            System.out.println("Hóa đơn chưa có sản phẩm!");
            return;
        }

        for (CTHoaDon ct : dsChiTietHoaDon)
            if (ct != null) System.out.println(ct);

        System.out.println("--------------------------------------");
        System.out.println("Tiền hàng trước KM: " + Ktradulieu.dinhDangTien(tinhTongTienTruocKM()));
        System.out.println("Tiền hàng sau KM: " + Ktradulieu.dinhDangTien(tinhTongTienSauKM()));
        System.out.println("Thuế VAT: " + Ktradulieu.dinhDangTien(Tongthuedonhang(dsSanPham)));
        System.out.println("TỔNG THANH TOÁN: " + Ktradulieu.dinhDangTien(Tongtienhoadon()));

        if (thanhtoan != null) {
            System.out.println("Phương thức: " + thanhtoan.getPhuongThuc());
            System.out.println("Chi tiết: " + thanhtoan);
        } else if (daThanhToanNapTuFile) {
            System.out.println("Trạng thái: ĐÃ THANH TOÁN (đọc từ file)");
        } else System.out.println("Trạng thái: CHƯA THANH TOÁN");

        System.out.println("======================================\n");
    }

    public String toDongFile() {
        StringBuilder sb = new StringBuilder();
        sb.append("HDB|").append(getMaHD()).append("|").append(getMaNV()).append("|")
                .append(getThoigianin()).append("|").append(MaKH).append("|")
                .append(isDaThanhToan() ? "DA_TT" : "CHUA_TT").append(";");

        for (int i = 0; i < dsChiTietHoaDon.size(); i++) {
            if (dsChiTietHoaDon.get(i) != null) sb.append(dsChiTietHoaDon.get(i).toDongFile());
            if (i < dsChiTietHoaDon.size() - 1) sb.append("~");
        }
        return sb.toString();
    }

    public static Hoadonban tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty()) return null;

        String[] parts = dong.split(";", -1);
        String[] meta = parts[0].split("\\|", -1);
        if (meta.length < 6 || !meta[0].equals("HDB")) return null;

        Hoadonban hdb = new Hoadonban();
        hdb.setMaHD(meta[1]);
        hdb.setMaNV(meta[2]);

        try {
            hdb.setThoigianin(LocalDateTime.parse(meta[3]));
        } catch (Exception e) {
            hdb.setThoigianin(LocalDateTime.now());
        }

        hdb.setMaKH(meta[4]);

        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            for (String item : parts[1].split("~", -1)) {
                if (item.trim().isEmpty()) continue;
                CTHoaDon ct = CTHoaDon.tuDongFile(item);
                if (ct != null) hdb.themChiTiet(ct);
            }
        }

        if ("DA_TT".equals(meta[5])) hdb.daThanhToanNapTuFile = true;
        return hdb;
    }
=======
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
>>>>>>> origin/main
}
