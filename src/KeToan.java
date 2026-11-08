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

    // Constructor mac dinh
    public KeToan() {
        super();
        this.NhanVien = new DSNhanVien();
        this.DSSanPham = new DSSanPham();
        this.HoaDon = new DSHoadon();
        this.quanLyKho = null;
    }

    @Override
    public String getVaiTro() {
        return "";
    }

    // Constructor ket noi cac danh sach
    public KeToan(DSNhanVien nhanVien,
                  DSSanPham DSSanPham,
                  DSHoadon hoadon) {
        super();
        this.NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        this.HoaDon = hoadon;
        this.quanLyKho = null;
    }

    // Constructor ket noi day du ca QuanlyKho
    public KeToan(DSNhanVien nhanVien,
                  DSSanPham DSSanPham,
                  DSHoadon hoadon,
                  QuanlyKho quanLyKho) {
        super();
        this.NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        this.HoaDon = hoadon;
        this.quanLyKho = quanLyKho;
    }

    // Constructor day du thong tin nhan vien KeToan
    public KeToan(String ten, String sdt, String diaChi,
                  String email, LocalDate ngaySinh,
                  String maNV, double luongCoBan,
                  LocalDate ngayVaoLam, String chucVu,
                  DSNhanVien nhanVien,
                  DSSanPham DSSanPham,
                  DSHoadon hoadon) {

        super(ten, sdt, diaChi, email, ngaySinh,
                maNV, luongCoBan, ngayVaoLam, chucVu);

        this.NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        this.HoaDon = hoadon;
        this.quanLyKho = null;
    }

    // Getter va Setter
    public DSNhanVien getNhanVien() {
        return NhanVien;
    }

    public void setNhanVien(DSNhanVien nhanVien) {
        this.NhanVien = nhanVien;
    }

    public DSSanPham getDSSanPham() {
        return DSSanPham;
    }

    public void setDSSanPham(DSSanPham DSSanPham) {
        this.DSSanPham = DSSanPham;
    }

    public DSHoadon getHoadon() {
        return HoaDon;
    }

    public void setHoadon(DSHoadon hoadon) {
        this.HoaDon = hoadon;
    }

    public DSHoadon getHoaDon() {
        return HoaDon;
    }

    public void setHoaDon(DSHoadon hoaDon) {
        this.HoaDon = hoaDon;
    }

    public QuanlyKho getQuanLyKho() {
        return quanLyKho;
    }

    public void setQuanLyKho(QuanlyKho quanLyKho) {
        this.quanLyKho = quanLyKho;
    }

    public Scanner getSc() {
        return sc;
    }

    public void setSc(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner khong hop le!");
        }
        this.sc = sc;
    }

    // Kiem tra du lieu ket noi
    private void kiemTraDuLieu() {
        if (HoaDon == null || HoaDon.getDs() == null) {
            throw new IllegalStateException("Chua ket noi danh sach hoa don!");
        }

        if (DSSanPham == null) {
            throw new IllegalStateException("Chua ket noi danh sach san pham!");
        }

        if (NhanVien == null) {
            throw new IllegalStateException("Chua ket noi danh sach nhan vien!");
        }
    }

    // =====================================================
    // 1. THONG KE DOANH THU (DOANH THU THUAN - CHUA BAO GOM THUE VAT)
    // =====================================================

    @Override
    public double tinhDoanhThuTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) {
            return 0;
        }

        kiemTraDuLieu();

        double doanhThu = 0;

        for (HoaDon hd : HoaDon.getDs()) {
            // Chi tinh doanh thu tren Hoa don ban (khong cong nham Hoa don nhap)
            if (!(hd instanceof Hoadonban)) {
                continue;
            }

            if (hd.getThoigianin() == null) {
                continue;
            }

            if (hd.getThoigianin().getMonthValue() == thang
                    && hd.getThoigianin().getYear() == nam) {

                Hoadonban hb = (Hoadonban) hd;
                if (hb.getDsSanPham() == null && this.DSSanPham != null) {
                    hb.setDsSanPham(this.DSSanPham);
                }

                // Doanh thu ban hang = Tien hang sau khuyen mai (CHUA bao gom thue VAT)
                doanhThu += hb.tinhTongTienSauKM();
            }
        }

        return doanhThu;
    }

    @Override
    public double tinhDoanhThuTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) {
            return 0;
        }

        int thangBatDau = (quy - 1) * 3 + 1;
        double doanhThu = 0;

        for (int thang = thangBatDau; thang < thangBatDau + 3; thang++) {
            doanhThu += tinhDoanhThuTheoThang(thang, nam);
        }

        return doanhThu;
    }

    @Override
    public double tinhDoanhThuTheoNam(int nam) {
        if (nam <= 0) {
            return 0;
        }

        double doanhThu = 0;

        for (int thang = 1; thang <= 12; thang++) {
            doanhThu += tinhDoanhThuTheoThang(thang, nam);
        }

        return doanhThu;
    }

    // =====================================================
    // 2. THONG KE GIA VON
    // =====================================================

    public double TongGiavonHangdabantheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) {
            return 0;
        }

        kiemTraDuLieu();

        double tongVon = 0;

        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) {
                continue;
            }

            if (hd.getThoigianin() == null) {
                continue;
            }

            if (hd.getThoigianin().getMonthValue() == thang
                    && hd.getThoigianin().getYear() == nam) {

                Hoadonban hb = (Hoadonban) hd;

                if (quanLyKho != null && hb.getCacLoDaXuat() != null && !hb.getCacLoDaXuat().isEmpty()) {
                    tongVon += quanLyKho.tinhGiaVon(hb.getCacLoDaXuat());
                } else {
                    // Du phong neu chua ket noi quanLyKho hoac hoa don chua luu cacLoDaXuat
                    for (CTHoaDon ct : hb.getCTHoaDon()) {
                        if (ct == null) {
                            continue;
                        }
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
        if (quy < 1 || quy > 4 || nam <= 0) {
            return 0;
        }

        double tongVon = 0;
        int thangBatDau = (quy - 1) * 3 + 1;

        for (int thang = thangBatDau; thang < thangBatDau + 3; thang++) {
            tongVon += TongGiavonHangdabantheoThang(thang, nam);
        }

        return tongVon;
    }

    public double TongGiavonHangdabantheoNam(int nam) {
        if (nam <= 0) {
            return 0;
        }

        double tongVon = 0;

        for (int thang = 1; thang <= 12; thang++) {
            tongVon += TongGiavonHangdabantheoThang(thang, nam);
        }

        return tongVon;
    }

    // =====================================================
    // 3. THONG KE LOI NHUAN
    // =====================================================

    /*
       Vi Doanh thu (tinhDoanhThuTheoThang) da la doanh thu thuan
       (tien hang sau KM, CHUA bao gom thue VAT),
       nen Loi nhuan = Doanh thu (chua thue) - Gia von - Luong
       (khong tru thue VAT lan nua de tranh bi tru kep 2 lan).
    */

    @Override
    public double tinhLoiNhuanTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) {
            return 0;
        }
        return tinhDoanhThuTheoThang(thang, nam)
                - TongGiavonHangdabantheoThang(thang, nam)
                - tinhTongLuongTheoThang(thang, nam);
    }

    @Override
    public double tinhLoiNhuanTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) {
            return 0;
        }
        return tinhDoanhThuTheoQuy(quy, nam)
                - TongGiavonTheoQuy(quy, nam)
                - tinhTongLuongTheoQuy(quy, nam);
    }

    @Override
    public double tinhLoiNhuanTheoNam(int nam) {
        if (nam <= 0) {
            return 0;
        }
        return tinhDoanhThuTheoNam(nam)
                - TongGiavonHangdabantheoNam(nam)
                - tinhTongLuongTheoNam(nam);
    }

    // =====================================================
    // 4. THONG KE THUE
    // =====================================================

    @Override
    public double thongKeThueTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) {
            return 0;
        }

        kiemTraDuLieu();

        double tongThue = 0;

        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) {
                continue;
            }

            if (hd.getThoigianin() == null) {
                continue;
            }

            if (hd.getThoigianin().getMonthValue() == thang
                    && hd.getThoigianin().getYear() == nam) {

                Hoadonban hb = (Hoadonban) hd;
                tongThue += hb.Tongthuedonhang(DSSanPham);
            }
        }

        return tongThue;
    }

    // Thong ke thue theo quy
    public double thongKeThueTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) {
            return 0;
        }

        double tongThue = 0;
        int thangBatDau = (quy - 1) * 3 + 1;

        for (int thang = thangBatDau; thang < thangBatDau + 3; thang++) {
            tongThue += thongKeThueTheoThang(thang, nam);
        }

        return tongThue;
    }

    @Override
    public double thongKeThueTheoNam(int nam) {
        if (nam <= 0) {
            return 0;
        }

        double tongThue = 0;

        for (int thang = 1; thang <= 12; thang++) {
            tongThue += thongKeThueTheoThang(thang, nam);
        }

        return tongThue;
    }

    // =====================================================
    // 5. THONG KE LUONG
    // =====================================================

    public double tinhTongLuongTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) {
            return 0;
        }

        kiemTraDuLieu();

        if (NhanVien.getDsNhanVien() == null) {
            return 0;
        }

        LocalDate ngayDauThang = LocalDate.of(nam, thang, 1);
        LocalDate ngayCuoiThang = ngayDauThang.withDayOfMonth(
                ngayDauThang.lengthOfMonth()
        );

        LocalDate homNay = LocalDate.now();

        // Khong tinh luong cho thang tuong lai
        if (ngayDauThang.isAfter(homNay)) {
            return 0;
        }

        // Neu la thang hien tai, chi tinh den hom nay
        if (ngayCuoiThang.isAfter(homNay)) {
            ngayCuoiThang = homNay;
        }

        int soNgayTrongThang = ngayDauThang.lengthOfMonth();

        double tongLuong = 0;

        for (NhanVien nv : NhanVien.getDsNhanVien()) {
            if (nv == null) {
                continue;
            }

            LocalDate ngayVaoLam = nv.getNgayVaoLam();

            if (ngayVaoLam == null || ngayVaoLam.isAfter(ngayCuoiThang)) {
                continue;
            }

            // Neu vao lam giua thang thi tinh tu ngay vao lam
            LocalDate ngayBatDauTinh = ngayVaoLam.isAfter(ngayDauThang)
                    ? ngayVaoLam : ngayDauThang;

            long soNgayLam = ChronoUnit.DAYS.between(
                    ngayBatDauTinh, ngayCuoiThang
            ) + 1;

            if (soNgayLam <= 0) {
                continue;
            }

            // Luong thang theo kinh nghiem tai ngay ket thuc
            double luongThang = nv.Tinhluong(ngayCuoiThang);

            // Tinh luong theo so ngay lam trong thang
            double luongThucTe = luongThang
                    * soNgayLam / soNgayTrongThang;

            tongLuong += luongThucTe;
        }

        return tongLuong;
    }

    public double tinhTongLuongTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) {
            return 0;
        }

        double tongLuong = 0;
        int thangBatDau = (quy - 1) * 3 + 1;

        for (int thang = thangBatDau; thang < thangBatDau + 3; thang++) {
            tongLuong += tinhTongLuongTheoThang(thang, nam);
        }

        return tongLuong;
    }

    public double tinhTongLuongTheoNam(int nam) {
        if (nam <= 0) {
            return 0;
        }

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

        System.out.println("===== THONG KE LUONG THANG "
                + homNay.getMonthValue() + "/" + homNay.getYear()
                + " =====");

        if (NhanVien.getDsNhanVien() != null) {
            for (NhanVien nv : NhanVien.getDsNhanVien()) {
                if (nv == null) {
                    continue;
                }

                System.out.println("Ma nhan vien: " + nv.getMaNV());
                System.out.println("Ho ten: " + nv.getTen());
                System.out.println("Chuc vu: " + nv.getChucVu());
                System.out.println("Luong hien tai: "
                        + String.format("%,.0f", nv.Tinhluong()));
                System.out.println("-----------------------------");
            }
        }

        System.out.println("Tong luong thang: "
                + String.format("%,.0f",
                tinhTongLuongTheoThang(
                        homNay.getMonthValue(),
                        homNay.getYear()
                )));
    }

    // =====================================================
    // 6. THONG KE SAN PHAM BAN DUOC
    // =====================================================

    private boolean thuocKy(HoaDon hd, int ky, int nam, int loai) {
        if (hd == null || hd.getThoigianin() == null || nam <= 0) {
            return false;
        }

        int thang = hd.getThoigianin().getMonthValue();
        int namHD = hd.getThoigianin().getYear();

        if (namHD != nam) {
            return false;
        }

        if (loai == 1) {
            return thang == ky;
        }

        if (loai == 2) {
            int quyHD = (thang - 1) / 3 + 1;
            return quyHD == ky;
        }

        if (loai == 3) {
            return true;
        }

        return false;
    }

    private void thongKeSoLuongSanPham(int ky, int nam, int loai) {
        kiemTraDuLieu();

        ArrayList<String> dsMaSP = new ArrayList<>();
        ArrayList<String> dsTenSP = new ArrayList<>();
        ArrayList<Integer> dsSoLuong = new ArrayList<>();

        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) {
                continue;
            }

            if (!thuocKy(hd, ky, nam, loai)) {
                continue;
            }

            Hoadonban hdb = (Hoadonban) hd;

            for (CTHoaDon ct : hdb.getCTHoaDon()) {
                if (ct == null || ct.getMaSP() == null) {
                    continue;
                }

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

        System.out.println("===== THONG KE SAN PHAM BAN DUOC =====");

        for (int i = 0; i < dsMaSP.size(); i++) {
            System.out.println(
                    "Ma SP: " + dsMaSP.get(i)
                            + " | Ten: " + dsTenSP.get(i)
                            + " | So luong ban: " + dsSoLuong.get(i)
            );
        }

        if (dsMaSP.isEmpty()) {
            System.out.println("Khong co san pham ban trong ky nay.");
        }
    }

    @Override
    public void thongKeSanPhamBanDuocTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) {
            System.out.println("Thang hoac nam khong hop le!");
            return;
        }

        thongKeSoLuongSanPham(thang, nam, 1);
    }

    @Override
    public void thongKeSanPhamBanDuocTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) {
            System.out.println("Quy hoac nam khong hop le!");
            return;
        }

        thongKeSoLuongSanPham(quy, nam, 2);
    }

    @Override
    public void thongKeSanPhamBanDuocTheoNam(int nam) {
        if (nam <= 0) {
            System.out.println("Nam khong hop le!");
            return;
        }

        thongKeSoLuongSanPham(0, nam, 3);
    }

    // =====================================================
    // 7. THONG KE CHI TIET SAN PHAM BAN RA
    // =====================================================

    private void thongKeSanPhamBanRa(int ky, int nam, int loai) {
        kiemTraDuLieu();

        boolean coSanPham = false;

        System.out.println("===== CHI TIET SAN PHAM BAN RA =====");

        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) {
                continue;
            }

            if (!thuocKy(hd, ky, nam, loai)) {
                continue;
            }

            Hoadonban hdb = (Hoadonban) hd;

            for (CTHoaDon ct : hdb.getCTHoaDon()) {
                if (ct == null) {
                    continue;
                }

                coSanPham = true;

                System.out.println(
                        "Ma HD: " + hd.getMaHD()
                                + " | Ma SP: " + ct.getMaSP()
                                + " | Ten SP: " + ct.getTenSP()
                                + " | So luong: " + ct.getSoluong()
                                + " | Gia ban: " + ct.getGiaban()
                );
            }
        }

        if (!coSanPham) {
            System.out.println("Khong co san pham ban trong ky nay.");
        }
    }

    @Override
    public void thongKeSanPhamBanRaTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12 || nam <= 0) {
            System.out.println("Thang hoac nam khong hop le!");
            return;
        }

        thongKeSanPhamBanRa(thang, nam, 1);
    }

    @Override
    public void thongKeSanPhamBanRaTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4 || nam <= 0) {
            System.out.println("Quy hoac nam khong hop le!");
            return;
        }

        thongKeSanPhamBanRa(quy, nam, 2);
    }

    @Override
    public void thongKeSanPhamBanRaTheoNam(int nam) {
        if (nam <= 0) {
            System.out.println("Nam khong hop le!");
            return;
        }

        thongKeSanPhamBanRa(0, nam, 3);
    }

    // =====================================================
    // 8. HAM NHAP SO NGUYEN CHO MENU
    // =====================================================

    private int nhapSoNguyen(String thongBao) {
        while (true) {
            try {
                System.out.print(thongBao);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui long nhap so nguyen!");
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