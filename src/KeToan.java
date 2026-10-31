import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.Scanner;




public class KeToan extends NhanVien implements Thongke {
    private DSNhanVien NhanVien;
    private DSSanPham DSSanPham;
    private DSHoadon HoaDon;


    public KeToan(DSNhanVien nhanVien, DSSanPham DSSanPham, DSHoadon hoadon) {
        NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        HoaDon = hoadon;
    }

    public KeToan(String maNV, LocalDate ngaybatdaulam, String Vitri, String Hoten, String SDT, String Email, String Diachi, DSNhanVien nhanVien, DSSanPham DSSanPham, DSHoadon hoadon) {
        super(maNV, ngaybatdaulam, Vitri, Hoten, SDT, Email, Diachi);
        NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        HoaDon = hoadon;
    }

    public KeToan() {
        super();
        NhanVien = new DSNhanVien();
        DSSanPham = new DSSanPham();
        HoaDon = new DSHoadon();
    }

    public DSNhanVien getNhanVien() {
        return NhanVien;
    }

    public void setNhanVien(DSNhanVien nhanVien) {
        NhanVien = nhanVien;
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
        HoaDon = hoadon;
    }


    private Scanner sc = new Scanner(System.in);


    @Override
    public double tinhDoanhThuTheoThang(int thang, int nam) {
        double doanthu = 0;
        for (HoaDon hd : HoaDon.getDs()) {
            if (hd.getThoigianin() != null
                    && hd.getThoigianin().getMonthValue() == thang
                    && hd.getThoigianin().getYear() == nam) {
                doanthu += hd.Tongtienhoadon();
            }
        }
        return doanthu;
    }

    @Override
    public double tinhDoanhThuTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4) {
            return 0;
        }

        double doanhthu = 0;
        int thangbatdau = (quy - 1) * 3 + 1;
        for (HoaDon hd : HoaDon.getDs()) {
            if (hd.getThoigianin() != null) {
                int thang = hd.getThoigianin().getMonthValue();
                int namHD = hd.getThoigianin().getYear();

                if (namHD == nam
                        && thang >= thangbatdau && thang < thangbatdau + 3) {
                    doanhthu += hd.Tongtienhoadon();
                }
            }
        }
        return doanhthu;
    }


    @Override
    public double tinhDoanhThuTheoNam(int nam) {
        double doanthu = 0;
        for (HoaDon hd : HoaDon.getDs()) {
            if (hd.getThoigianin() != null && hd.getThoigianin().getYear() == nam) {
                doanthu += hd.Tongtienhoadon();
            }
        }
        return doanthu;
    }

    public double TongGiavonHangdabantheoThang(int thang, int nam) {
        double tongvon = 0;
        for (HoaDon hd : HoaDon.getDs()) {
            if (hd instanceof Hoadonban) {
                Hoadonban hb = (Hoadonban) hd;

                if (hd.getThoigianin() != null
                        && hd.getThoigianin().getMonthValue() == thang
                        && hd.getThoigianin().getYear() == nam) {
                    for (CTHoaDon ct : hb.getCTHoaDon()) {
                        SanPham sp = DSSanPham.timTheoMa(ct.getMaSP());

                        if (sp != null) {
                            tongvon += sp.getGiaNhap() * ct.getSoluong();
                        }
                    }
                }
            }
        }
        return tongvon;
    }

    public double TongGiavonTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4) {
            return 0;
        }

        double tongvon = 0;
        int thangbatdau = (quy - 1) * 3 + 1;

        for (int thang = thangbatdau; thang < thangbatdau + 3; thang++) {
            tongvon += TongGiavonHangdabantheoThang(thang, nam);
        }

        return tongvon;
    }

    public double TongGiavonHangdabantheoNam(int nam) {
        double tongvon = 0;

        for (int thang = 1; thang <= 12; thang++) {
            tongvon += TongGiavonHangdabantheoThang(thang, nam);
        }

        return tongvon;
    }

    @Override
    public double tinhLoiNhuanTheoThang(int thang, int nam) {
        double loinhuan = tinhDoanhThuTheoThang(thang, nam) - TongGiavonHangdabantheoThang(thang, nam) - tinhTongLuongTheoThang(thang, nam);
        return loinhuan;
    }

    @Override
    public double tinhLoiNhuanTheoQuy(int quy, int nam) {
        double loinhuan = tinhDoanhThuTheoQuy(quy, nam) - TongGiavonTheoQuy(quy, nam) - tinhTongLuongTheoQuy(quy, nam);
        return loinhuan;
    }

    @Override
    public double tinhLoiNhuanTheoNam(int nam) {
        double loinhuan = tinhDoanhThuTheoNam(nam) - TongGiavonHangdabantheoNam(nam) - tinhTongLuongTheoNam(nam);
        return loinhuan;
    }

    @Override
    public double thongKeThueTheoThang(int thang, int nam) {
        double tongthue = 0;
        for (HoaDon hd : HoaDon.getDs()) {
            if (hd instanceof Hoadonban) {
                Hoadonban hb = (Hoadonban) hd;
                if (hd.getThoigianin() != null
                        && hd.getThoigianin().getMonthValue() == thang
                        && hd.getThoigianin().getYear() == nam) {
                    tongthue += hb.Tongthuedonhang(DSSanPham);
                }
            }
        }
        return tongthue;
    }

    @Override
    public double thongKeThueTheoNam(int nam) {
        double tongthue = 0;

        for (HoaDon hd : HoaDon.getDs()) {
            if (hd instanceof Hoadonban) {
                Hoadonban hb = (Hoadonban) hd;

                if (hd.getThoigianin() != null && hd.getThoigianin().getYear() == nam) {
                    tongthue += hb.Tongthuedonhang(DSSanPham);
                }
            }
        }
        return tongthue;
    }

    public double tinhTongLuongTheoThang(int thang, int nam) {
        if (thang < 1 || thang > 12) {
            return 0;
        }

        LocalDate ngayDauThang = LocalDate.of(nam, thang, 1);
        LocalDate ngayCuoiThang = ngayDauThang
                .withDayOfMonth(ngayDauThang.lengthOfMonth());

        // Không tính lương tháng trong tương lai
        if (ngayDauThang.isAfter(LocalDate.now())) {
            return 0;
        }

        // Nếu là tháng hiện tại, chỉ tính đến hôm nay
        LocalDate ngayKetThuc = ngayCuoiThang;
        if (ngayKetThuc.isAfter(LocalDate.now())) {
            ngayKetThuc = LocalDate.now();
        }

        int soNgayTrongThang = ngayDauThang.lengthOfMonth();
        int ngayCongChuan = soNgayTrongThang - 3;

        double tongLuong = 0;

        for (NhanVien nv : NhanVien.getDsNhanVien()) {
            LocalDate ngayBatDau = nv.();

            if (ngayBatDau == null || ngayBatDau.isAfter(ngayKetThuc)) {
                continue;
            }

            // Ngày bắt đầu được tính lương
            LocalDate ngayBatDauTinh = ngayBatDau.isAfter(ngayDauThang)
                    ? ngayBatDau : ngayDauThang;

            // Số ngày từ ngày bắt đầu tính lương đến ngày kết thúc
            long soNgayLich = java.time.temporal.ChronoUnit.DAYS.between(
                    ngayBatDauTinh, ngayKetThuc
            ) + 1;

            // Ước tính số ngày nghỉ theo tỷ lệ thời gian làm việc
            double soNgayOff = 3.0 * soNgayLich / soNgayTrongThang;
            double ngayCongThucTe = soNgayLich - soNgayOff;

            // Lương tháng theo mức kinh nghiệm tại ngày kết thúc
            double luongThang = nv.Tinhluong(ngayKetThuc);

            // Lương thực tế
            double luongThucTe = luongThang
                    * ngayCongThucTe / ngayCongChuan;

            tongLuong += luongThucTe;
        }

        return tongLuong;
    }

    public double tinhTongLuongTheoQuy(int quy, int nam) {
        if (quy < 1 || quy > 4) {
            System.out.println("Quy khong hop le!");
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
        double tongLuong = 0;

        for (int thang = 1; thang <= 12; thang++) {
            tongLuong += tinhTongLuongTheoThang(thang, nam);
        }

        return tongLuong;
    }

    @Override
    public void thongKeLuong() {
        LocalDate homNay = LocalDate.now();
        int thang = homNay.getMonthValue();
        int nam = homNay.getYear();

        System.out.println("===== THONG KE LUONG THANG "
                + thang + "/" + nam + " =====");

        for (NhanVien nv : NhanVien.getDsNhanVien()) {
            System.out.println("Ma nhan vien: " + nv.getMaNV());
            System.out.println("Chuc vu: " + nv.getChucVu());
            System.out.println("Luong: " + nv.tinhLuong(homNay));
            System.out.println("-----------------------------");
        }

        System.out.println("Tong luong thang: "
                + tinhTongLuongTheoThang(thang, nam));
    }

    private boolean thuocKy(HoaDon hd, int ky, int nam, int loai) {
        if (hd.getThoigianin() == null) {
            return false;
        }
        int thang = hd.getThoigianin().getMonthValue();
        int namHD = hd.getThoigianin().getYear();

        if (namHD != nam) {
            return false;
        }

        if (loai == 1) { // Thống kê theo tháng
            return thang == ky;
        }

        if (loai == 2) { // Thống kê theo quý
            int quyHD = (thang - 1) / 3 + 1;
            return quyHD == ky;
        }

        if (loai == 3) { // Thống kê theo năm
            return true;
        }

        return false;
    }

    private void thongKeSoLuongSanPham(int ky, int nam, int loai) {
        String[] maSP = new String[500];
        String[] tenSP = new String[500];
        int[] soLuong = new int[500];

        int n = 0;

        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) {
                continue;
            }

            if (!thuocKy(hd, ky, nam, loai)) {
                continue;
            }

            Hoadonban hdb = (Hoadonban) hd;

            for (CTHoaDon ct : hdb.getCTHoaDon()) {
                int viTri = -1;

                // Tìm sản phẩm đã có trong mảng chưa
                for (int i = 0; i < n; i++) {
                    if (maSP[i].equals(ct.getMaSP())) {
                        viTri = i;
                        break;
                    }
                }

                if (viTri != -1) {
                    soLuong[viTri] += ct.getSoluong();
                } else {
                    if (n >= maSP.length) {
                        System.out.println("Danh sách sản phẩm đã đầy!");
                        return;
                    }

                    maSP[n] = ct.getMaSP();
                    tenSP[n] = ct.getTenSP();
                    soLuong[n] = ct.getSoluong();
                    n++;
                }
            }
        }

        System.out.println("===== THỐNG KÊ SẢN PHẨM =====");

        for (int i = 0; i < n; i++) {
            System.out.println(
                    "Mã SP: " + maSP[i]
                            + " | Tên: " + tenSP[i]
                            + " | Số lượng bán: " + soLuong[i]
            );
        }

        if (n == 0) {
            System.out.println("Không có sản phẩm bán trong kỳ này.");
        }
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
        boolean coSanPham = false;

        System.out.println("===== CHI TIẾT SẢN PHẨM BÁN RA =====");

        for (HoaDon hd : HoaDon.getDs()) {
            if (!(hd instanceof Hoadonban)) {
                continue;
            }

            if (!thuocKy(hd, ky, nam, loai)) {
                continue;
            }

            Hoadonban hdb = (Hoadonban) hd;

            for (CTHoaDon ct : hdb.getCTHoaDon()) {
                coSanPham = true;

                System.out.println("Mã HĐ: " + hd.getMaHD()
                        + " | Mã SP: " + ct.getMaSP()
                        + " | Tên SP: " + ct.getTenSP()
                        + " | Số lượng: " + ct.getSoluong()
                        + " | Giá bán: " + ct.getGiaban());
            }
        }

        if (!coSanPham) {
            System.out.println("Không có sản phẩm bán trong kỳ này.");
        }
    }
    @Override
    public void thongKeSanPhamBanRaTheoThang(int thang, int nam) {
        thongKeSanPhamBanRa(thang, nam, 1);
    }

    @Override
    public void thongKeSanPhamBanRaTheoQuy(int quy, int nam) {
        thongKeSanPhamBanRa(quy, nam, 2);
    }

    @Override
    public void thongKeSanPhamBanRaTheoNam(int nam) {
        thongKeSanPhamBanRa(0, nam, 3);
    }


    //======================================================================================

    //Menu doanh thu
    public void menuDoanhThu() {
        int chon;

        do {
            System.out.println("\n===== THỐNG KÊ DOANH THU =====");
            System.out.println("1. Doanh thu theo tháng");
            System.out.println("2. Doanh thu theo quý");
            System.out.println("3. Doanh thu theo năm");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            chon = sc.nextInt();

            switch (chon) {
                case 1:
                    System.out.print("Nhập tháng: ");
                    int thang = sc.nextInt();
                    System.out.print("Nhập năm: ");
                    int namThang = sc.nextInt();

                    if (thang < 1 || thang > 12) {
                        System.out.println("Tháng không hợp lệ!");
                        break;
                    }

                    System.out.println("Doanh thu tháng " + thang
                            + "/" + namThang + ": "
                            + tinhDoanhThuTheoThang(thang, namThang));
                    break;

                case 2:
                    System.out.print("Nhập quý (1-4): ");
                    int quy = sc.nextInt();
                    System.out.print("Nhập năm: ");
                    int namQuy = sc.nextInt();

                    if (quy < 1 || quy > 4) {
                        System.out.println("Quý không hợp lệ!");
                        break;
                    }

                    System.out.println("Doanh thu quý " + quy
                            + "/" + namQuy + ": "
                            + tinhDoanhThuTheoQuy(quy, namQuy));
                    break;

                case 3:
                    System.out.print("Nhập năm: ");
                    int nam = sc.nextInt();

                    System.out.println("Doanh thu năm " + nam + ": "
                            + tinhDoanhThuTheoNam(nam));
                    break;

                case 0:
                    System.out.println("Quay lại menu kế toán.");
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
    // Menu lợi nhuận

    public void menuLoiNhuan() {
        int chon;

        do {
            System.out.println("\n===== THỐNG KÊ LỢI NHUẬN =====");
            System.out.println("1. Lợi nhuận theo tháng");
            System.out.println("2. Lợi nhuận theo quý");
            System.out.println("3. Lợi nhuận theo năm");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            chon = sc.nextInt();

            switch (chon) {
                case 1:
                    System.out.print("Nhập tháng: ");
                    int thang = sc.nextInt();
                    System.out.print("Nhập năm: ");
                    int namThang = sc.nextInt();

                    if (thang < 1 || thang > 12) {
                        System.out.println("Tháng không hợp lệ!");
                        break;
                    }

                    System.out.println("Lợi nhuận tháng " + thang
                            + "/" + namThang + ": "
                            + tinhLoiNhuanTheoThang(thang, namThang));
                    break;

                case 2:
                    System.out.print("Nhập quý (1-4): ");
                    int quy = sc.nextInt();
                    System.out.print("Nhập năm: ");
                    int namQuy = sc.nextInt();

                    if (quy < 1 || quy > 4) {
                        System.out.println("Quý không hợp lệ!");
                        break;
                    }

                    System.out.println("Lợi nhuận quý " + quy
                            + "/" + namQuy + ": "
                            + tinhLoiNhuanTheoQuy(quy, namQuy));
                    break;

                case 3:
                    System.out.print("Nhập năm: ");
                    int nam = sc.nextInt();

                    System.out.println("Lợi nhuận năm " + nam + ": "
                            + tinhLoiNhuanTheoNam(nam));
                    break;

                case 0:
                    System.out.println("Quay lại menu kế toán.");
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }

    //Menu thuế

    public void menuThue() {
        int chon;

        do {
            System.out.println("\n===== THỐNG KÊ THUẾ =====");
            System.out.println("1. Thống kê thuế theo tháng");
            System.out.println("2. Thống kê thuế theo năm");
            System.out.println("0. Quay lại");
            System.out.print("Chọn chức năng: ");

            chon = Integer.parseInt(sc.nextLine());

            switch (chon) {
                case 1:
                    System.out.print("Nhập tháng: ");
                    int thang = Integer.parseInt(sc.nextLine());

                    System.out.print("Nhập năm: ");
                    int namThang = Integer.parseInt(sc.nextLine());

                    if (thang < 1 || thang > 12) {
                        System.out.println("Tháng không hợp lệ!");
                        break;
                    }

                    System.out.println("Tổng thuế tháng "
                            + thang + "/" + namThang + ": "
                            + thongKeThueTheoThang(thang, namThang));
                    break;

                case 2:
                    System.out.print("Nhập năm: ");
                    int nam = Integer.parseInt(sc.nextLine());

                    System.out.println("Tổng thuế năm "
                            + nam + ": "
                            + thongKeThueTheoNam(nam));
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }

        } while (chon != 0);
    }

    // menu thống kê sản phẩm

    // Spham bán được
    public void menuSanPhamBanDuoc() {
        int chon;

        do {
            System.out.println("\n===== SỐ LƯỢNG SẢN PHẨM BÁN ĐƯỢC =====");
            System.out.println("1. Theo tháng");
            System.out.println("2. Theo quý");
            System.out.println("3. Theo năm");
            System.out.println("0. Quay lại");
            System.out.print("Chọn chức năng: ");

            chon = Integer.parseInt(sc.nextLine());

            switch (chon) {
                case 1: {
                    System.out.print("Nhập tháng: ");
                    int thang = Integer.parseInt(sc.nextLine());

                    System.out.print("Nhập năm: ");
                    int nam = Integer.parseInt(sc.nextLine());

                    if (thang < 1 || thang > 12) {
                        System.out.println("Tháng không hợp lệ!");
                        break;
                    }

                    thongKeSanPhamBanDuocTheoThang(thang, nam);
                    break;
                }

                case 2: {
                    System.out.print("Nhập quý (1-4): ");
                    int quy = Integer.parseInt(sc.nextLine());

                    System.out.print("Nhập năm: ");
                    int nam = Integer.parseInt(sc.nextLine());

                    if (quy < 1 || quy > 4) {
                        System.out.println("Quý không hợp lệ!");
                        break;
                    }

                    thongKeSanPhamBanDuocTheoQuy(quy, nam);
                    break;
                }

                case 3: {
                    System.out.print("Nhập năm: ");
                    int nam = Integer.parseInt(sc.nextLine());

                    thongKeSanPhamBanDuocTheoNam(nam);
                    break;
                }

                case 0:
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }

        } while (chon != 0);
    }
    // Spham bán ra

    public void menuSanPhamBanRa() {
        int chon;

        do {
            System.out.println("\n===== CHI TIẾT SẢN PHẨM BÁN RA =====");
            System.out.println("1. Theo tháng");
            System.out.println("2. Theo quý");
            System.out.println("3. Theo năm");
            System.out.println("0. Quay lại");
            System.out.print("Chọn chức năng: ");

            chon = Integer.parseInt(sc.nextLine());

            switch (chon) {
                case 1: {
                    System.out.print("Nhập tháng: ");
                    int thang = Integer.parseInt(sc.nextLine());

                    System.out.print("Nhập năm: ");
                    int nam = Integer.parseInt(sc.nextLine());

                    if (thang < 1 || thang > 12) {
                        System.out.println("Tháng không hợp lệ!");
                        break;
                    }

                    thongKeSanPhamBanRaTheoThang(thang, nam);
                    break;
                }

                case 2: {
                    System.out.print("Nhập quý (1-4): ");
                    int quy = Integer.parseInt(sc.nextLine());

                    System.out.print("Nhập năm: ");
                    int nam = Integer.parseInt(sc.nextLine());

                    if (quy < 1 || quy > 4) {
                        System.out.println("Quý không hợp lệ!");
                        break;
                    }

                    thongKeSanPhamBanRaTheoQuy(quy, nam);
                    break;
                }

                case 3: {
                    System.out.print("Nhập năm: ");
                    int nam = Integer.parseInt(sc.nextLine());

                    thongKeSanPhamBanRaTheoNam(nam);
                    break;
                }

                case 0:
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }

        } while (chon != 0);
    }


    //
    public void menuThongKeSanPham() {
        int chon;

        do {
            System.out.println("\n===== THỐNG KÊ SẢN PHẨM =====");
            System.out.println("1. Thống kê số lượng sản phẩm bán được");
            System.out.println("2. Thống kê chi tiết sản phẩm bán ra");
            System.out.println("0. Quay lại");
            System.out.print("Chọn chức năng: ");

            chon = Integer.parseInt(sc.nextLine());

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
                    System.out.println("Lựa chọn không hợp lệ!");
            }

        } while (chon != 0);
    }

    //Menu lương
    public void menuLuong() {
        int chon;

        do {
            System.out.println("\n===== THỐNG KÊ LƯƠNG =====");
            System.out.println("1. Thống kê lương theo tháng");
            System.out.println("2. Thống kê lương theo quý");
            System.out.println("3. Thống kê lương theo năm");
            System.out.println("0. Quay lại");
            System.out.print("Chọn: ");
            chon = sc.nextInt();

            switch (chon) {
                case 1:
                    System.out.print("Nhập tháng: ");
                    int thang = sc.nextInt();

                    System.out.print("Nhập năm: ");
                    int namThang = sc.nextInt();

                    if (thang < 1 || thang > 12) {
                        System.out.println("Tháng không hợp lệ!");
                        break;
                    }

                    System.out.println("Tổng lương tháng " + thang
                            + "/" + namThang + ": "
                            + tinhTongLuongTheoThang(thang, namThang));
                    break;

                case 2:
                    System.out.print("Nhập quý (1-4): ");
                    int quy = sc.nextInt();

                    System.out.print("Nhập năm: ");
                    int namQuy = sc.nextInt();

                    if (quy < 1 || quy > 4) {
                        System.out.println("Quý không hợp lệ!");
                        break;
                    }

                    System.out.println("Tổng lương quý " + quy
                            + "/" + namQuy + ": "
                            + tinhTongLuongTheoQuy(quy, namQuy));
                    break;

                case 3:
                    System.out.print("Nhập năm: ");
                    int nam = sc.nextInt();

                    System.out.println("Tổng lương năm " + nam + ": "
                            + tinhTongLuongTheoNam(nam));
                    break;

                case 0:
                    System.out.println("Quay lại menu kế toán.");
                    break;

                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }

//    //MENU CHÍNH
//    public void menu() {
//        Scanner sc = new Scanner(System.in);
//        int chon;
//
//        do {
//            System.out.println("\n===== MENU KẾ TOÁN =====");
//            System.out.println("1. Thống kê doanh thu");
//            System.out.println("2. Thống kê lợi nhuận");
//            System.out.println("3. Thống kê thuế");
//            System.out.println("4. Thống kê sản phẩm bán được");
//            System.out.println("5. Thống kê lương nhân viên");
//            System.out.println("0. Quay lại");
//            System.out.print("Chọn chức năng: ");
//
//            chon = Integer.parseInt(sc.nextLine());
//
//            switch (chon) {
//                case 1:
//                    menuDoanhThu();
//                    break;
//
//                case 2:
//                    menuLoiNhuan();
//                    break;
//
//                case 3:
//                    menuThue();
//                    break;
//
//                case 4:
//                    menuSanPhamBanDuoc();
//                    break;
//
//                case 5:
//                    menuLuong();
//                    break;
//
//                case 0:
//                    System.out.println("Quay lại màn hình đăng nhập!");
//                    break;
//
//                default:
//                    System.out.println("Lựa chọn không hợp lệ!");
//            }
//
//        } while (chon != 0);
//    }
}


