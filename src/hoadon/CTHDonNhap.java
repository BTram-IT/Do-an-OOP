package hoadon;


import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import sanpham.ThucAn;
import sanpham.DoUong;

import person.Nguoidung;
import sanpham.DSSanPham;
import sanpham.SanPham;
import sanpham.ThucAn;
import sanpham.DoUong;
import util.Ktradulieu;

public class CTHDonNhap extends CTHoaDon {

    private double thueSuatNhap;
    private LocalDate hanSuDung;
    private SanPham sanPhamMoi;

    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ==================== CONSTRUCTOR ====================

    public CTHDonNhap() {
        super();
        this.thueSuatNhap = 0;
        this.hanSuDung = null;
        this.sanPhamMoi = null;
    }

    public CTHDonNhap(CTHDonNhap ct) {
        super(ct != null ? ct.getMaHD() : "", ct != null ? ct.getMaSP() : "", ct != null ? ct.getTenSP() : "", ct != null ? ct.getSoLuongNhap() : 0, ct != null ? ct.getDonGiaNhap() : 0);
        if (ct == null) {
            throw new IllegalArgumentException("[Lỗi] Chi tiết hóa đơn không hợp lệ!");
        }
        this.thueSuatNhap = ct.thueSuatNhap;
        this.hanSuDung = ct.hanSuDung;
        this.sanPhamMoi = ct.sanPhamMoi;
    }

    public CTHDonNhap(String maHD, String maSP, String tenSP, int soLuongNhap, double donGiaNhap, double thueSuatNhap, LocalDate hanSuDung) {
        super(maHD, maSP, tenSP, soLuongNhap, donGiaNhap);
        setThueSuatNhap(thueSuatNhap);
        setHanSuDung(hanSuDung);
    }

    public CTHDonNhap(String maHD, SanPham sp, int soLuongNhap, double donGiaNhap, LocalDate hanSuDung) {
        super(maHD, sp.getMaSP(), sp.getTenSP(), soLuongNhap, donGiaNhap > 0 ? donGiaNhap : sp.getDonGiaNhap());
        setThueSuatNhap(sp.getPhanTramThue());
        setHanSuDung(hanSuDung);
    }

    // ==================== GETTER / SETTER ====================

    public int getSoLuongNhap() { return getSoLuong(); }
    public void setSoLuongNhap(int soLuongNhap) {
        if (soLuongNhap <= 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng nhập phải lớn hơn 0!");
        }
        setSoLuong(soLuongNhap);
    }

    public double getDonGiaNhap() { return getDonGia(); }
    public void setDonGiaNhap(double donGiaNhap) {
        if (donGiaNhap < 0 || Double.isNaN(donGiaNhap) || Double.isInfinite(donGiaNhap)) {
            throw new IllegalArgumentException("[Lỗi] Giá nhập không hợp lệ!");
        }
        setDonGia(donGiaNhap);
    }

    public double getThueSuatNhap() { return thueSuatNhap; }
    public void setThueSuatNhap(double thueSuatNhap) {
        if (thueSuatNhap < 0 || thueSuatNhap > 100 || Double.isNaN(thueSuatNhap) || Double.isInfinite(thueSuatNhap)) {
            throw new IllegalArgumentException("[Lỗi] Thuế suất nhập phải nằm trong khoảng 0-100%!");
        }
        this.thueSuatNhap = thueSuatNhap;
    }

    public LocalDate getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(LocalDate hanSuDung) {
        if (hanSuDung == null) {
            throw new IllegalArgumentException("[Lỗi] Hạn sử dụng không được để trống!");
        }
        if (hanSuDung.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("[Lỗi] Hạn sử dụng không được trước ngày hiện tại!");
        }
        this.hanSuDung = hanSuDung;
    }

    public SanPham getSanPhamMoi() { return sanPhamMoi; }
    public void setSanPhamMoi(SanPham sanPhamMoi) { this.sanPhamMoi = sanPhamMoi; }

    public boolean coSanPhamMoiChoDuyet() {
        return sanPhamMoi != null && "Chờ duyệt".equalsIgnoreCase(sanPhamMoi.getTrangThaiDuyet());
    }

    // ==================== NHẬP THÔNG TIN ====================

    public boolean nhapThongTin(Scanner sc, String maHoaDon, DSSanPham dsSP) {
        return nhapThongTin(sc, maHoaDon, dsSP, null);
    }

    public boolean nhapThongTin(Scanner sc, String maHoaDon, DSSanPham dsSP, Nguoidung nguoiDungDangNhap) {
        if (sc == null) sc = new Scanner(System.in);
        if (maHoaDon == null || maHoaDon.trim().isEmpty()) {
            System.out.println("[Lỗi] Dữ liệu nhập hàng không hợp lệ!");
            return false;
        }

        System.out.println("\n========== NHẬP CHI TIẾT HÀNG NHẬP ==========");
        this.sanPhamMoi = null;

        while (true) {
            System.out.print("Nhập mã sản phẩm (Ví dụ: TA001, DU001 - Nhập 0 để hủy): ");
            String input = sc.nextLine().trim();
            if (input.equals("0")) {
                System.out.println("[Thông báo] Đã hủy thao tác nhập chi tiết hàng nhập!");
                return false;
            }

            try {
                String maSP = Ktradulieu.chuanHoaMa(input);
                if (!maSP.startsWith("TA") && !maSP.startsWith("DU") && !maSP.startsWith("SP")) {
                    System.out.println("[Lỗi] Mã sản phẩm phải bắt đầu bằng TA, DU hoặc SP!");
                    continue;
                }

                SanPham sp = (dsSP != null) ? dsSP.timKiem(maSP) : null;
                if (sp != null) {
                    setMaHD(maHoaDon);
                    setMaSP(sp.getMaSP());
                    setTenSP(sp.getTenSP());
                    if (sp.getDonGiaNhap() > 0) {
                        setDonGiaNhap(sp.getDonGiaNhap());
                    }
                    setThueSuatNhap(sp.getPhanTramThue());
                    this.sanPhamMoi = null;

                    System.out.println("-> Đã nhận diện sản phẩm: [" + sp.getMaSP() + "] " + sp.getTenSP());
                    break;
                }

                System.out.println("[Thông báo] Sản phẩm chưa có trong danh sách kho.");
                System.out.print("Bạn có muốn tạo hồ sơ sản phẩm mới không? (1: Có / 0: Không): ");
                if (!sc.nextLine().trim().equals("1")) return false;

                SanPham spMoi = taoHoSoSanPhamMoi(sc, maSP);
                if (spMoi == null) return false;

                this.sanPhamMoi = spMoi;
                setMaHD(maHoaDon);
                setMaSP(spMoi.getMaSP());
                setTenSP(spMoi.getTenSP());
                setDonGiaNhap(spMoi.getDonGiaNhap());
                setThueSuatNhap(spMoi.getPhanTramThue());

                System.out.println("[Thông báo] Hồ sơ sản phẩm mới đang chờ người có quyền duyệt.");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Nhập số lượng
        long soLuong;
        while (true) {
            soLuong = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng nhập kho: ");
            if (soLuong > 0 && soLuong <= Integer.MAX_VALUE) break;
            System.out.println("[Lỗi] Số lượng phải lớn hơn 0 và không vượt giới hạn!");
        }
        setSoLuongNhap((int) soLuong);

        // Giá nhập
        while (true) {
            try {
                if (getDonGiaNhap() > 0) {
                    System.out.print("Đơn giá nhập hiện tại là: " + Ktradulieu.dinhDangTien(getDonGiaNhap()) + ". Nhấn ENTER để giữ nguyên, hoặc nhập giá mới: ");
                } else {
                    System.out.print("Nhập giá nhập mới: ");
                }
                String dgInput = sc.nextLine().trim();
                if (dgInput.isEmpty() && getDonGiaNhap() > 0) {
                    break;
                }
                double giaMoi = Double.parseDouble(dgInput);
                if (giaMoi < 0 || Double.isNaN(giaMoi) || Double.isInfinite(giaMoi)) {
                    System.out.println("[Lỗi] Giá nhập không hợp lệ!");
                    continue;
                }
                setDonGiaNhap(giaMoi);
                if (this.sanPhamMoi != null) {
                    this.sanPhamMoi.setDonGiaNhap(giaMoi);
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("[Lỗi] Đơn giá phải là số hợp lệ!");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Hạn sử dụng (Nếu có sản phẩm mới thì HSD đã lấy từ hồ sơ, nếu không thì nhập mới)
        if (this.sanPhamMoi == null) {
            while (true) {
                try {
                    LocalDate hsd = Ktradulieu.nhapNgay(sc, "Nhập hạn sử dụng (dd/MM/yyyy): ");
                    setHanSuDung(hsd);
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        } else {
            System.out.println("-> Hạn sử dụng theo hồ sơ sản phẩm mới: " + this.hanSuDung.format(DINH_DANG_NGAY));
        }

        return true;
    }

    // ==================== TẠO HỒ SƠ SẢN PHẨM MỚI ====================

    private SanPham taoHoSoSanPhamMoi(Scanner sc, String maSP) {
        System.out.println("\n========== TẠO HỒ SƠ SẢN PHẨM MỚI ==========");

        try {
            if (sc == null) {
                sc = new Scanner(System.in);
            }

            // 1. Xác định loại sản phẩm
            String loaiSP;

            if (maSP.startsWith("TA")) {
                loaiSP = "TA";
            } else if (maSP.startsWith("DU")) {
                loaiSP = "DU";
            } else {
                System.out.println("Mã SP chưa xác định được loại sản phẩm.");
                System.out.println("1. Thức ăn");
                System.out.println("2. Đồ uống");

                while (true) {
                    System.out.print("Chọn loại sản phẩm: ");
                    String chon = sc.nextLine().trim();

                    if (chon.equals("1")) {
                        loaiSP = "TA";
                        break;
                    } else if (chon.equals("2")) {
                        loaiSP = "DU";
                        break;
                    }

                    System.out.println("[Lỗi] Chỉ được chọn 1 hoặc 2!");
                }
            }

            // 2. Nhập thông tin chung
            String tenSP = Ktradulieu.nhapChuoiKhongRong(
                    sc, "Nhập tên sản phẩm: "
            ).replace(';', ',');

            double giaNhap;
            while (true) {
                giaNhap = Ktradulieu.nhapSoThuc(sc, "Nhập giá nhập: ");

                if (giaNhap > 0 && Double.isFinite(giaNhap)) {
                    break;
                }

                System.out.println("[Lỗi] Giá nhập phải lớn hơn 0!");
            }

            double giaBan;
            while (true) {
                giaBan = Ktradulieu.nhapSoThuc(sc, "Nhập giá bán: ");

                if (giaBan > 0 && Double.isFinite(giaBan)) {
                    break;
                }

                System.out.println("[Lỗi] Giá bán phải lớn hơn 0!");
            }

            String donVi = Ktradulieu.nhapChuoiKhongRong(
                    sc, "Nhập đơn vị tính: "
            ).replace(';', ',');

            double vat;
            while (true) {
                vat = Ktradulieu.nhapSoThuc(sc, "Nhập thuế VAT (%): ");

                if (vat >= 0 && vat <= 100 && Double.isFinite(vat)) {
                    break;
                }

                System.out.println("[Lỗi] Thuế VAT phải từ 0 đến 100%!");
            }

            // 3. Nhập ngày sản xuất
            LocalDate nsx;

            while (true) {
                try {
                    nsx = Ktradulieu.nhapNgay(
                            sc, "Nhập ngày sản xuất (dd/MM/yyyy): "
                    );

                    if (nsx.isAfter(LocalDate.now())) {
                        System.out.println(
                                "[Lỗi] Ngày sản xuất không được ở tương lai!"
                        );
                        continue;
                    }

                    break;

                } catch (IllegalArgumentException e) {
                    System.out.println("[Lỗi] " + e.getMessage());
                }
            }

            // 4. Nhập hạn sử dụng
            LocalDate hsd;

            while (true) {
                try {
                    hsd = Ktradulieu.nhapNgay(
                            sc, "Nhập hạn sử dụng (dd/MM/yyyy): "
                    );

                    if (hsd.isBefore(LocalDate.now())) {
                        System.out.println(
                                "[Lỗi] Hạn sử dụng không được trước ngày hiện tại!"
                        );
                        continue;
                    }

                    if (hsd.isBefore(nsx)) {
                        System.out.println(
                                "[Lỗi] Hạn sử dụng không được trước ngày sản xuất!"
                        );
                        continue;
                    }

                    break;

                } catch (IllegalArgumentException e) {
                    System.out.println("[Lỗi] " + e.getMessage());
                }
            }

            // 5. Tạo đối tượng theo loại sản phẩm
            SanPham spMoi;

            if (loaiSP.equals("TA")) {

                System.out.println("\n========== THÔNG TIN RIÊNG CỦA THỨC ĂN ==========");

                ThucAn thucAn = new ThucAn();

                thucAn.setThanhPhan(
                        Ktradulieu.nhapChuoiKhongRong(
                                sc, "Nhập thành phần: "
                        ).replace(';', ',')
                );

                thucAn.setBaoQuan(
                        Ktradulieu.nhapChuoiKhongRong(
                                sc, "Nhập hướng dẫn bảo quản: "
                        ).replace(';', ',')
                );

                thucAn.setXuatXu(
                        Ktradulieu.nhapChuoiKhongRong(
                                sc, "Nhập xuất xứ: "
                        ).replace(';', ',')
                );

                thucAn.setLaDoChay(
                        Ktradulieu.nhapXacNhan(
                                sc, "Có phải đồ chay không?"
                        )
                );

                spMoi = thucAn;

            } else {

                System.out.println("\n========== THÔNG TIN RIÊNG CỦA ĐỒ UỐNG ==========");

                DoUong doUong = new DoUong();

                doUong.setThuongHieu(
                        Ktradulieu.nhapChuoiKhongRong(
                                sc, "Nhập thương hiệu: "
                        ).replace(';', ',')
                );

                doUong.setLoaiDoUong(
                        Ktradulieu.nhapChuoiKhongRong(
                                sc, "Nhập loại đồ uống: "
                        ).replace(';', ',')
                );

                doUong.setHuongVi(
                        Ktradulieu.nhapChuoiKhongRong(
                                sc, "Nhập hương vị: "
                        ).replace(';', ',')
                );

                double dungTich;

                while (true) {
                    dungTich = Ktradulieu.nhapSoThuc(
                            sc, "Nhập dung tích (ml): "
                    );

                    if (dungTich > 0 && Double.isFinite(dungTich)) {
                        break;
                    }

                    System.out.println("[Lỗi] Dung tích phải lớn hơn 0!");
                }

                doUong.setDungTich(dungTich);

                spMoi = doUong;
            }

            // 6. Gán thông tin chung cho sản phẩm
            spMoi.setMaSP(maSP);
            spMoi.setTenSP(tenSP);
            spMoi.setDonGiaNhap(giaNhap);
            spMoi.setDonGiaBan(giaBan);
            spMoi.setDonViTinh(donVi);
            spMoi.setSoLuongTon(0);

            spMoi.setHSD(
                    nsx.format(DINH_DANG_NGAY),
                    hsd.format(DINH_DANG_NGAY)
            );

            spMoi.setPhanTramThue(vat);
            spMoi.setTrangThaiDuyet("Chờ duyệt");
            spMoi.setDaXoa(false);

            // Lưu hạn sử dụng vào chi tiết hóa đơn
            setHanSuDung(hsd);

            System.out.println("\n[Thành công] Đã tạo hồ sơ sản phẩm mới!");
            System.out.println("Mã sản phẩm: " + maSP);
            System.out.println("Tên sản phẩm: " + tenSP);
            System.out.println("Loại sản phẩm: "
                    + (spMoi instanceof ThucAn ? "Thức ăn" : "Đồ uống"));
            System.out.println("Trạng thái: Chờ duyệt");

            return spMoi;

        } catch (IllegalArgumentException e) {
            System.out.println("[Lỗi] " + e.getMessage());
            return null;
        }
    }

    // ==================== TÍNH TIỀN ====================

    @Override
    public double tinhTienHang() { return getSoLuongNhap() * getDonGiaNhap(); }

    @Override
    public double tinhTienThue() { return tinhTienHang() * this.thueSuatNhap / 100.0; }

    @Override
    public double tinhThanhTien() { return tinhTienHang() + tinhTienThue(); }

    // ==================== LƯU / ĐỌC FILE ====================

    @Override
    public String toDongFile() {

        String hsd = (this.hanSuDung != null)
                ? this.hanSuDung.format(DINH_DANG_NGAY)
                : "";

        String dong = String.format(
                Locale.ROOT,
                "%s;%s;%s;%d;%.0f;%.2f;%s",
                maHD, maSP, tenSP, getSoLuongNhap(),
                getDonGiaNhap(), this.thueSuatNhap, hsd
        );

        if (this.sanPhamMoi != null) {

            String dongSanPham;

            if (this.sanPhamMoi instanceof ThucAn) {
                dongSanPham = ((ThucAn) this.sanPhamMoi).toFileString();

            } else if (this.sanPhamMoi instanceof DoUong) {
                dongSanPham = ((DoUong) this.sanPhamMoi).toFileString();

            } else {
                throw new IllegalStateException("Loại sản phẩm không hợp lệ!");
            }

            dong += ";SPMOI;" + dongSanPham;
        }

        return dong;
    }

    public static CTHDonNhap tuDongFile(String dong) {

        if (dong == null || dong.trim().isEmpty()) {
            return null;
        }

        try {
            String[] p = dong.split(";", -1);

            if (p.length < 7) {
                return null;
            }

            int soLuong = Integer.parseInt(p[3].trim());
            double donGia = Ktradulieu.docSoThuc(p[4]);
            double vat = Ktradulieu.docSoThuc(p[5]);

            LocalDate hsd = null;

            if (!p[6].trim().isEmpty()) {
                hsd = LocalDate.parse(p[6].trim(), DINH_DANG_NGAY);
            }

            CTHDonNhap ct = new CTHDonNhap();

            ct.maHD = Ktradulieu.chuanHoaMa(p[0].trim());
            ct.maSP = Ktradulieu.chuanHoaMa(p[1].trim());
            ct.tenSP = Ktradulieu.chuanHoaChuoi(p[2].trim());

            ct.soLuong = soLuong;
            ct.donGia = donGia;
            ct.thueSuatNhap = vat;
            ct.hanSuDung = hsd;

            ct.sanPhamMoi = null;

            // Khôi phục hồ sơ sản phẩm mới
            if (p.length > 8 && p[7].equalsIgnoreCase("SPMOI")) {

                String[] sp = java.util.Arrays.copyOfRange(p, 8, p.length);

                String loai = sp[0].trim();

                if ("THUCAN".equalsIgnoreCase(loai)) {

                    if (sp.length < 15) {
                        return null;
                    }

                    ct.sanPhamMoi = new ThucAn(
                            sp[1], sp[2],
                            Ktradulieu.docSoThuc(sp[3]),
                            Ktradulieu.docSoThuc(sp[4]),
                            Integer.parseInt(sp[5]),
                            sp[6], sp[7], sp[8],
                            Ktradulieu.docSoThuc(sp[9]),
                            sp[10], sp[11], sp[12],
                            Boolean.parseBoolean(sp[13]),
                            sp[14],
                            sp.length > 15 && Boolean.parseBoolean(sp[15])
                    );

                } else if ("DOUONG".equalsIgnoreCase(loai)) {

                    if (sp.length < 16) {
                        return null;
                    }

                    ct.sanPhamMoi = new DoUong(
                            sp[1], sp[2],
                            Ktradulieu.docSoThuc(sp[3]),
                            Ktradulieu.docSoThuc(sp[4]),
                            Integer.parseInt(sp[5]),
                            sp[6], sp[7], sp[8],
                            Ktradulieu.docSoThuc(sp[9]),
                            Ktradulieu.docSoThuc(sp[10]),
                            sp[11], sp[12], sp[13],
                            sp[14],
                            sp.length > 15 && Boolean.parseBoolean(sp[15])
                    );

                } else {
                    return null;
                }
            }

            return ct;

        } catch (Exception e) {
            System.out.println(
                    "[Lỗi] Đọc chi tiết hóa đơn nhập: " + e.getMessage()
            );
            return null;
        }
    }

    // ==================== HIỂN THỊ ====================

    @Override
    public String toString() {
        String hsdStr = (this.hanSuDung != null) ? this.hanSuDung.format(DINH_DANG_NGAY) : "N/A";
        return String.format("%-8s | %-20s | SL: %-4d | Đơn giá: %-12s | Thuế: %-4.1f%% | HSD: %-10s | Thành tiền: %s",
                maSP, tenSP, getSoLuongNhap(), Ktradulieu.dinhDangTien(getDonGiaNhap()),
                thueSuatNhap, hsdStr, Ktradulieu.dinhDangTien(tinhThanhTien()));
    }
}