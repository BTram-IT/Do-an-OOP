import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;

public class KeToan extends NhanVien implements Thongke {

    private DSNhanVien NhanVien;
    private DSSanPham DSSanPham;
    private DSHoadon HoaDon;
    private QuanlyKho quanLyKho;

    private Scanner sc = new Scanner(System.in);

    public KeToan() {
        super();
        this.NhanVien = new DSNhanVien();
        this.DSSanPham = new DSSanPham();
        this.HoaDon = new DSHoadon();
        this.quanLyKho = null;
    }

    @Override
    public String getVaiTro() {
        return "KeToan";
    }

    public KeToan(DSNhanVien nhanVien, DSSanPham DSSanPham, DSHoadon hoadon) {
        super();
        this.NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        this.HoaDon = hoadon;
        this.quanLyKho = null;
    }

    public KeToan(DSNhanVien nhanVien, DSSanPham DSSanPham, DSHoadon hoadon, QuanlyKho quanLyKho) {
        super();
        this.NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        this.HoaDon = hoadon;
        this.quanLyKho = quanLyKho;
    }

    private void kiemTraDuLieu() {
        if (HoaDon == null || HoaDon.getDs() == null) {
            throw new IllegalStateException("Chưa kết nối danh sách hóa đơn!");
        }
        if (DSSanPham == null) {
            throw new IllegalStateException("Chưa kết nối danh sách sản phẩm!");
        }
        if (NhanVien == null) {
            throw new IllegalStateException("Chưa kết nối danh sách nhân viên!");
        }
    }

    @Override
    public double tinhDoanhThuTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) return 0;
        kiemTraDuLieu();

        double doanhThu = 0;
        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) continue;

            Hoadonban hb = (Hoadonban) hd;

            // CHỈ TÍNH DOANH THU ĐỐI VỚI HÓA ĐƠN ĐÃ THANH TOÁN THÀNH CÔNG
            if (!hb.isDaThanhToan()) continue;

            if (hb.getThoigianin() == null) continue;

            if (hb.getThoigianin().getMonthValue() == thang
                    && hb.getThoigianin().getYear() == nam) {

                if (hb.getDsSanPham() == null && this.DSSanPham != null) {
                    hb.setDsSanPham(this.DSSanPham);
                }
                doanhThu += hb.tinhTongTienSauKM();
            }
        }
        return doanhThu;
    }

    @Override
    public double tinhDoanhThuTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) return 0;
        int thangBatDau = (quy - 1) * 3 + 1;
        double doanhThu = 0;
        for (int thang = thangBatDau; thang < thangBatDau + 3; thang++) {
            doanhThu += tinhDoanhThuTheoThang(thang, nam);
        }
        return doanhThu;
    }

    @Override
    public double tinhDoanhThuTheoNam(int nam) {
        if (nam <= 0) return 0;
        double doanhThu = 0;
        for (int thang = 1; thang <= 12; thang++) {
            doanhThu += tinhDoanhThuTheoThang(thang, nam);
        }
        return doanhThu;
    }

    public double TongGiavonHangdabantheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) return 0;
        kiemTraDuLieu();

        double tongVon = 0;
        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) continue;

            Hoadonban hb = (Hoadonban) hd;
            if (!hb.isDaThanhToan()) continue;

            if (hb.getThoigianin() == null) continue;

            if (hb.getThoigianin().getMonthValue() == thang
                    && hb.getThoigianin().getYear() == nam) {

                if (quanLyKho != null && hb.getCacLoDaXuat() != null && !hb.getCacLoDaXuat().isEmpty()) {
                    tongVon += quanLyKho.tinhGiaVon(hb.getCacLoDaXuat());
                } else {
                    for (CTHoaDon ct : hb.getCTHoaDon()) {
                        if (ct == null) continue;
                        SanPham sp = DSSanPham.timTheoMa(ct.getMaSP());
                        if (sp != null) {
                            long giaVonDonVi = sp.tinhGiaVonBinhQuanTon() > 0
                                    ? sp.tinhGiaVonBinhQuanTon()
                                    : sp.getGiaNhap();
                            tongVon += (double) giaVonDonVi * ct.getSoluong();
                        }
                    }
                }
            }
        }
        return tongVon;
    }

    public double TongGiavonTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) return 0;
        double tongVon = 0;
        int thangBatDau = (quy - 1) * 3 + 1;
        for (int thang = thangBatDau; thang < thangBatDau + 3; thang++) {
            tongVon += TongGiavonHangdabantheoThang(thang, nam);
        }
        return tongVon;
    }

    public double TongGiavonHangdabantheoNam(int nam) {
        if (nam <= 0) return 0;
        double tongVon = 0;
        for (int thang = 1; thang <= 12; thang++) {
            tongVon += TongGiavonHangdabantheoThang(thang, nam);
        }
        return tongVon;
    }

    @Override
    public double tinhLoiNhuanTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) return 0;
        return tinhDoanhThuTheoThang(thang, nam)
                - TongGiavonHangdabantheoThang(thang, nam)
                - tinhTongLuongTheoThang(thang, nam);
    }

    @Override
    public double tinhLoiNhuanTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) return 0;
        return tinhDoanhThuTheoQuy(quy, nam)
                - TongGiavonTheoQuy(quy, nam)
                - tinhTongLuongTheoQuy(quy, nam);
    }

    @Override
    public double tinhLoiNhuanTheoNam(int nam) {
        if (nam <= 0) return 0;
        return tinhDoanhThuTheoNam(nam)
                - TongGiavonHangdabantheoNam(nam)
                - tinhTongLuongTheoNam(nam);
    }

    @Override
    public double thongKeThueTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) return 0;
        kiemTraDuLieu();

        double tongThue = 0;
        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) continue;
            Hoadonban hb = (Hoadonban) hd;
            if (!hb.isDaThanhToan()) continue;

            if (hb.getThoigianin() == null) continue;

            if (hb.getThoigianin().getMonthValue() == thang
                    && hb.getThoigianin().getYear() == nam) {
                tongThue += hb.Tongthuedonhang(DSSanPham);
            }
        }
        return tongThue;
    }

    public double thongKeThueTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) return 0;
        double tongThue = 0;
        int thangBatDau = (quy - 1) * 3 + 1;
        for (int thang = thangBatDau; thang < thangBatDau + 3; thang++) {
            tongThue += thongKeThueTheoThang(thang, nam);
        }
        return tongThue;
    }

    @Override
    public double thongKeThueTheoNam(int nam) {
        if (nam <= 0) return 0;
        double tongThue = 0;
        for (int thang = 1; thang <= 12; thang++) {
            tongThue += thongKeThueTheoThang(thang, nam);
        }
        return tongThue;
    }

    public double tinhTongLuongTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) return 0;
        kiemTraDuLieu();
        if (NhanVien.getDsNhanVien() == null) return 0;

        LocalDate ngayDauThang = LocalDate.of(nam, thang, 1);
        LocalDate ngayCuoiThang = ngayDauThang.withDayOfMonth(ngayDauThang.lengthOfMonth());
        LocalDate homNay = LocalDate.now();

        if (ngayDauThang.isAfter(homNay)) return 0;
        if (ngayCuoiThang.isAfter(homNay)) ngayCuoiThang = homNay;

        double soNgayTrongThang = ngayDauThang.lengthOfMonth();
        double tongLuong = 0;

        for (NhanVien nv : NhanVien.getDsNhanVien()) {
            if (nv == null) continue;
            LocalDate ngayVaoLam = nv.getNgayVaoLam();
            if (ngayVaoLam == null || ngayVaoLam.isAfter(ngayCuoiThang)) continue;

            LocalDate ngayBatDauTinh = ngayVaoLam.isAfter(ngayDauThang) ? ngayVaoLam : ngayDauThang;
            double soNgayLam = ChronoUnit.DAYS.between(ngayBatDauTinh, ngayCuoiThang) + 1;
            if (soNgayLam <= 0) continue;

            double luongThang = nv.Tinhluong(ngayCuoiThang);
            tongLuong += luongThang * (soNgayLam / soNgayTrongThang);
        }
        return tongLuong;
    }

    public double tinhTongLuongTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) return 0;
        double tongLuong = 0;
        int thangBatDau = (quy - 1) * 3 + 1;
        for (int thang = thangBatDau; thang < thangBatDau + 3; thang++) {
            tongLuong += tinhTongLuongTheoThang(thang, nam);
        }
        return tongLuong;
    }

    public double tinhTongLuongTheoNam(int nam) {
        if (nam <= 0) return 0;
        double tongLuong = 0;
        for (int thang = 1; thang <= 12; thang++) {
            tongLuong += tinhTongLuongTheoThang(thang, nam);
        }
        return tongLuong;
    }

    @Override
    public void thongKeLuong() {
        kiemTraDuLieu();
        LocalDate homNay = LocalDate.now();
        System.out.println("===== THỐNG KÊ LƯƠNG THÁNG " + homNay.getMonthValue() + "/" + homNay.getYear() + " =====");
        if (NhanVien.getDsNhanVien() != null) {
            for (NhanVien nv : NhanVien.getDsNhanVien()) {
                if (nv == null) continue;
                System.out.println("Mã nhân viên: " + nv.getMaNV() + " | Họ tên: " + nv.getTen() + " | Lương: " + String.format("%,.0f", nv.Tinhluong()));
            }
        }
        System.out.println("Tổng lương thực tế: " + String.format("%,.0f", tinhTongLuongTheoThang(homNay.getMonthValue(), homNay.getYear())));
    }

    private boolean thuocKy(HoaDon hd, int ky, int nam, int loai) {
        if (hd == null || hd.getThoigianin() == null || nam <= 0) return false;
        int thang = hd.getThoigianin().getMonthValue();
        int namHD = hd.getThoigianin().getYear();
        if (namHD != nam) return false;

        if (loai == 1) return thang == ky;
        if (loai == 2) return ((thang - 1) / 3 + 1) == ky;
        return loai == 3;
    }

    private void thongKeSoLuongSanPham(int ky, int nam, int loai) {
        kiemTraDuLieu();
        ArrayList<String> dsMaSP = new ArrayList<>();
        ArrayList<String> dsTenSP = new ArrayList<>();
        ArrayList<Integer> dsSoLuong = new ArrayList<>();

        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) continue;
            Hoadonban hb = (Hoadonban) hd;
            if (!hb.isDaThanhToan()) continue;
            if (!thuocKy(hd, ky, nam, loai)) continue;

            for (CTHoaDon ct : hb.getCTHoaDon()) {
                if (ct == null || ct.getMaSP() == null) continue;
                int viTri = -1;
                for (int i = 0; i < dsMaSP.size(); i++) {
                    if (dsMaSP.get(i).equalsIgnoreCase(ct.getMaSP())) {
                        viTri = i;
                        break;
                    }
                }
                if (viTri != -1) {
                    dsSoLuong.set(viTri, dsSoLuong.get(viTri) + ct.getSoluong());
                } else {
                    dsMaSP.add(ct.getMaSP());
                    dsTenSP.add(ct.getTenSP());
                    dsSoLuong.add(ct.getSoluong());
                }
            }
        }

        System.out.println("===== THỐNG KÊ SẢN PHẨM BÁN ĐƯỢC =====");
        for (int i = 0; i < dsMaSP.size(); i++) {
            System.out.println("Mã SP: " + dsMaSP.get(i) + " | Tên: " + dsTenSP.get(i) + " | Số lượng bán: " + dsSoLuong.get(i));
        }
        if (dsMaSP.isEmpty()) System.out.println("Không có sản phẩm bán trong kỳ này.");
    }

    @Override
    public void thongKeSanPhamBanDuocTheoThang(int thang, int nam) {
        thongKeSoLuongSanPham(thang, nam, 1);
    }

    @Override
    public void thongKeSanPhamBanDuocTheoQuy(int quy, int nam) {
        thongKeSoLuongSanPham(quy, nam, 2);
    }

    @Override
    public void thongKeSanPhamBanDuocTheoNam(int nam) {
        thongKeSoLuongSanPham(0, nam, 3);
    }

    private void thongKeSanPhamBanRa(int ky, int nam, int loai) {
        kiemTraDuLieu();
        boolean coSanPham = false;
        System.out.println("===== CHI TIẾT SẢN PHẨM BÁN RA =====");
        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) continue;
            Hoadonban hb = (Hoadonban) hd;
            if (!hb.isDaThanhToan()) continue;
            if (!thuocKy(hd, ky, nam, loai)) continue;

            for (CTHoaDon ct : hb.getCTHoaDon()) {
                if (ct == null) continue;
                coSanPham = true;
                System.out.println("Mã HD: " + hd.getMaHD() + " | Mã SP: " + ct.getMaSP() + " | Tên SP: " + ct.getTenSP() + " | SL: " + ct.getSoluong() + " | Giá: " + ct.getGiaban());
            }
        }
        if (!coSanPham) System.out.println("Không có sản phẩm bán trong kỳ này.");
    }

    @Override
    public void thongKeSanPhamBanRaTheoThang(int thang, int nam) { thongKeSanPhamBanRa(thang, nam, 1); }
    @Override
    public void thongKeSanPhamBanRaTheoQuy(int quy, int nam) { thongKeSanPhamBanRa(quy, nam, 2); }
    @Override
    public void thongKeSanPhamBanRaTheoNam(int nam) { thongKeSanPhamBanRa(0, nam, 3); }

    private int nhapSoNguyen(String thongBao) {
        while (true) {
            try {
                System.out.print(thongBao);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập số nguyên!");
            }
        }
    }

    // =====================================================
    // 9. MENU DOANH THU
    // =====================================================

    public void menuDoanhThu() {
        int chon;

        do {
            System.out.println("\n===== THONG KE DOANH THU =====");
            System.out.println("1. Doanh thu theo thang");
            System.out.println("2. Doanh thu theo quy");
            System.out.println("3. Doanh thu theo nam");
            System.out.println("0. Quay lai");

            chon = nhapSoNguyen("Chon: ");

            switch (chon) {
                case 1: {
                    int thang = nhapSoNguyen("Nhap thang (1-12): ");
                    int nam = nhapSoNguyen("Nhap nam: ");

                    if (thang < 1 || thang > 12 || nam <= 0) {
                        System.out.println("Thang hoac nam khong hop le!");
                        break;
                    }

                    System.out.println("Doanh thu: "
                            + String.format("%,.0f", tinhDoanhThuTheoThang(thang, nam)));
                    break;
                }

                case 2: {
                    int quy = nhapSoNguyen("Nhap quy (1-4): ");
                    int nam = nhapSoNguyen("Nhap nam: ");

                    if (quy < 1 || quy > 4 || nam <= 0) {
                        System.out.println("Quy hoac nam khong hop le!");
                        break;
                    }

                    System.out.println("Doanh thu: "
                            + String.format("%,.0f", tinhDoanhThuTheoQuy(quy, nam)));
                    break;
                }

                case 3: {
                    int nam = nhapSoNguyen("Nhap nam: ");
                    if (nam <= 0) {
                        System.out.println("Nam khong hop le!");
                        break;
                    }

                    System.out.println("Doanh thu: "
                            + String.format("%,.0f", tinhDoanhThuTheoNam(nam)));
                    break;
                }

                case 0:
                    break;

                default:
                    System.out.println("Lua chon khong hop le!");
            }

        } while (chon != 0);
    }

    // =====================================================
    // 10. MENU LOI NHUAN
    // =====================================================

    public void menuLoiNhuan() {
        int chon;

        do {
            System.out.println("\n===== THONG KE LOI NHUAN =====");
            System.out.println("1. Loi nhuan theo thang");
            System.out.println("2. Loi nhuan theo quy");
            System.out.println("3. Loi nhuan theo nam");
            System.out.println("0. Quay lai");

            chon = nhapSoNguyen("Chon: ");

            switch (chon) {
                case 1: {
                    int thang = nhapSoNguyen("Nhap thang (1-12): ");
                    int nam = nhapSoNguyen("Nhap nam: ");

                    if (thang < 1 || thang > 12 || nam <= 0) {
                        System.out.println("Thang hoac nam khong hop le!");
                        break;
                    }

                    System.out.println("Loi nhuan: "
                            + String.format("%,.0f", tinhLoiNhuanTheoThang(thang, nam)));
                    break;
                }

                case 2: {
                    int quy = nhapSoNguyen("Nhap quy (1-4): ");
                    int nam = nhapSoNguyen("Nhap nam: ");

                    if (quy < 1 || quy > 4 || nam <= 0) {
                        System.out.println("Quy hoac nam khong hop le!");
                        break;
                    }

                    System.out.println("Loi nhuan: "
                            + String.format("%,.0f", tinhLoiNhuanTheoQuy(quy, nam)));
                    break;
                }

                case 3: {
                    int nam = nhapSoNguyen("Nhap nam: ");
                    if (nam <= 0) {
                        System.out.println("Nam khong hop le!");
                        break;
                    }

                    System.out.println("Loi nhuan: "
                            + String.format("%,.0f", tinhLoiNhuanTheoNam(nam)));
                    break;
                }

                case 0:
                    break;

                default:
                    System.out.println("Lua chon khong hop le!");
            }

        } while (chon != 0);
    }

    // =====================================================
    // 11. MENU THUE
    // =====================================================

    public void menuThue() {
        int chon;

        do {
            System.out.println("\n===== THONG KE THUE =====");
            System.out.println("1. Thue theo thang");
            System.out.println("2. Thue theo quy");
            System.out.println("3. Thue theo nam");
            System.out.println("0. Quay lai");

            chon = nhapSoNguyen("Chon: ");

            switch (chon) {
                case 1: {
                    int thang = nhapSoNguyen("Nhap thang (1-12): ");
                    int nam = nhapSoNguyen("Nhap nam: ");

                    if (thang < 1 || thang > 12 || nam <= 0) {
                        System.out.println("Thang hoac nam khong hop le!");
                        break;
                    }

                    System.out.println("Tong thue: "
                            + String.format("%,.0f", thongKeThueTheoThang(thang, nam)));
                    break;
                }

                case 2: {
                    int quy = nhapSoNguyen("Nhap quy (1-4): ");
                    int nam = nhapSoNguyen("Nhap nam: ");

                    if (quy < 1 || quy > 4 || nam <= 0) {
                        System.out.println("Quy hoac nam khong hop le!");
                        break;
                    }

                    System.out.println("Tong thue: "
                            + String.format("%,.0f", thongKeThueTheoQuy(quy, nam)));
                    break;
                }

                case 3: {
                    int nam = nhapSoNguyen("Nhap nam: ");
                    if (nam <= 0) {
                        System.out.println("Nam khong hop le!");
                        break;
                    }

                    System.out.println("Tong thue: "
                            + String.format("%,.0f", thongKeThueTheoNam(nam)));
                    break;
                }

                case 0:
                    break;

                default:
                    System.out.println("Lua chon khong hop le!");
            }

        } while (chon != 0);
    }

    // =====================================================
    // 12. MENU LUONG
    // =====================================================

    public void menuLuong() {
        int chon;

        do {
            System.out.println("\n===== THONG KE LUONG =====");
            System.out.println("1. Luong theo thang");
            System.out.println("2. Luong theo quy");
            System.out.println("3. Luong theo nam");
            System.out.println("4. Bang luong chi tiet thang hien tai");
            System.out.println("0. Quay lai");

            chon = nhapSoNguyen("Chon: ");

            switch (chon) {
                case 1: {
                    int thang = nhapSoNguyen("Nhap thang (1-12): ");
                    int nam = nhapSoNguyen("Nhap nam: ");

                    if (thang < 1 || thang > 12 || nam <= 0) {
                        System.out.println("Thang hoac nam khong hop le!");
                        break;
                    }

                    System.out.println("Tong luong: "
                            + String.format("%,.0f", tinhTongLuongTheoThang(thang, nam)));
                    break;
                }

                case 2: {
                    int quy = nhapSoNguyen("Nhap quy (1-4): ");
                    int nam = nhapSoNguyen("Nhap nam: ");

                    if (quy < 1 || quy > 4 || nam <= 0) {
                        System.out.println("Quy hoac nam khong hop le!");
                        break;
                    }

                    System.out.println("Tong luong: "
                            + String.format("%,.0f", tinhTongLuongTheoQuy(quy, nam)));
                    break;
                }

                case 3: {
                    int nam = nhapSoNguyen("Nhap nam: ");
                    if (nam <= 0) {
                        System.out.println("Nam khong hop le!");
                        break;
                    }

                    System.out.println("Tong luong: "
                            + String.format("%,.0f", tinhTongLuongTheoNam(nam)));
                    break;
                }

                case 4:
                    thongKeLuong();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Lua chon khong hop le!");
            }

        } while (chon != 0);
    }

    // =====================================================
    // 13. MENU SAN PHAM BAN DUOC
    // =====================================================

    public void menuSanPhamBanDuoc() {
        int chon;

        do {
            System.out.println("\n===== SAN PHAM BAN DUOC =====");
            System.out.println("1. Theo thang");
            System.out.println("2. Theo quy");
            System.out.println("3. Theo nam");
            System.out.println("0. Quay lai");

            chon = nhapSoNguyen("Chon: ");

            switch (chon) {
                case 1: {
                    int thang = nhapSoNguyen("Nhap thang: ");
                    int nam = nhapSoNguyen("Nhap nam: ");
                    thongKeSanPhamBanDuocTheoThang(thang, nam);
                    break;
                }

                case 2: {
                    int quy = nhapSoNguyen("Nhap quy: ");
                    int nam = nhapSoNguyen("Nhap nam: ");
                    thongKeSanPhamBanDuocTheoQuy(quy, nam);
                    break;
                }

                case 3: {
                    int nam = nhapSoNguyen("Nhap nam: ");
                    thongKeSanPhamBanDuocTheoNam(nam);
                    break;
                }

                case 0:
                    break;

                default:
                    System.out.println("Lua chon khong hop le!");
            }

        } while (chon != 0);
    }

    // =====================================================
    // 14. MENU SAN PHAM BAN RA
    // =====================================================

    public void menuSanPhamBanRa() {
        int chon;

        do {
            System.out.println("\n===== CHI TIET SAN PHAM BAN RA =====");
            System.out.println("1. Theo thang");
            System.out.println("2. Theo quy");
            System.out.println("3. Theo nam");
            System.out.println("0. Quay lai");

            chon = nhapSoNguyen("Chon: ");

            switch (chon) {
                case 1: {
                    int thang = nhapSoNguyen("Nhap thang: ");
                    int nam = nhapSoNguyen("Nhap nam: ");
                    thongKeSanPhamBanRaTheoThang(thang, nam);
                    break;
                }

                case 2: {
                    int quy = nhapSoNguyen("Nhap quy: ");
                    int nam = nhapSoNguyen("Nhap nam: ");
                    thongKeSanPhamBanRaTheoQuy(quy, nam);
                    break;
                }

                case 3: {
                    int nam = nhapSoNguyen("Nhap nam: ");
                    thongKeSanPhamBanRaTheoNam(nam);
                    break;
                }

                case 0:
                    break;

                default:
                    System.out.println("Lua chon khong hop le!");
            }

        } while (chon != 0);
    }

    // =====================================================
    // 15. MENU THONG KE SAN PHAM
    // =====================================================

    public void menuThongKeSanPham() {
        int chon;

        do {
            System.out.println("\n===== THONG KE SAN PHAM =====");
            System.out.println("1. So luong san pham ban duoc");
            System.out.println("2. Chi tiet san pham ban ra");
            System.out.println("0. Quay lai");

            chon = nhapSoNguyen("Chon: ");

            switch (chon) {
                case 1:
                    menuSanPhamBanDuoc();
                    break;

                case 2:
                    menuSanPhamBanRa();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Lua chon khong hop le!");
            }

        } while (chon != 0);
    }

    // =====================================================
    // 16. MENU TONG HOP KE TOAN
    // =====================================================

    public void menuKeToan() {
        int chon;

        do {
            System.out.println("\n========== MENU NGHIEP VU KE TOAN ==========");
            System.out.println("1. Thong ke doanh thu");
            System.out.println("2. Thong ke loi nhuan");
            System.out.println("3. Thong ke thue");
            System.out.println("4. Thong ke luong nhan vien");
            System.out.println("5. Thong ke san pham ban ra");
            System.out.println("0. Thoat");

            chon = nhapSoNguyen("Chon: ");

            switch (chon) {
                case 1:
                    menuDoanhThu();
                    break;
                case 2:
                    menuLoiNhuan();
                    break;
                case 3:
                    menuThue();
                    break;
                case 4:
                    menuLuong();
                    break;
                case 5:
                    menuThongKeSanPham();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Lua chon khong hop le!");
            }
        } while (chon != 0);
    }
}