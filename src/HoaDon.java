import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HoaDon  {

    private static int demSoHoaDon = 0;

    private String maHD;
    private int thang;
    private int nam;

    // Hoa don chi giu lien ket den don hang goc
    private Donhang donHangGoc;

    private CTHoaDon[] dsCT;
    private int soCT;

    private double tongTruocThue;
    private double tongThue;
    private double tienGiamKM;
    private double tongThanhToan;

    private ThanhToan phuongThucTT;

    private boolean daThanhToan;
    private boolean daCongDiem;

    // ==================== CONSTRUCTOR ====================

    public HoaDon() {
        this.maHD = taoMaHoaDon();
        this.thang = 10;
        this.nam = 2026;

        this.donHangGoc = null;

        this.dsCT = new CTHoaDon[0];
        this.soCT = 0;

        this.tongTruocThue = 0;
        this.tongThue = 0;
        this.tienGiamKM = 0;
        this.tongThanhToan = 0;

        this.phuongThucTT = null;

        this.daThanhToan = false;
        this.daCongDiem = false;

        demSoHoaDon++;
    }

    // Tao hoa don tu don hang da xac nhan
    public HoaDon(String maHD, int thang, int nam,
                  Donhang dh, ThanhToan phuongThucTT) {

        if (dh == null) {
            throw new IllegalArgumentException(
                    "Don hang khong duoc de trong!"
            );
        }

        if (dh.isDaHuy()) {
            throw new IllegalArgumentException(
                    "Don hang da huy, khong the tao hoa don!"
            );
        }

        if (!dh.isDaXacNhan()) {
            throw new IllegalArgumentException(
                    "Can xac nhan don hang truoc khi tao hoa don!"
            );
        }

        this.maHD = maHD;
        this.thang = thang;
        this.nam = nam;

        this.donHangGoc = dh;
        this.phuongThucTT = phuongThucTT;

        // Sao chep chi tiet don hang sang chi tiet hoa don
        // Khong tru ton kho tai day
        List<ChiTietDonHang> dsDonHang =
                dh.getDanhSachChiTiet();

        this.soCT = dsDonHang.size();
        this.dsCT = new CTHoaDon[this.soCT];

        for (int i = 0; i < this.soCT; i++) {
            this.dsCT[i] =
                    new CTHoaDon(dsDonHang.get(i));
        }

        // Lay tong tien tu don hang goc
        this.tongTruocThue = dh.getTongTienHang();
        this.tongThue = dh.getTongTienThue();
        this.tienGiamKM = dh.tinhTienGiamKM();
        this.tongThanhToan = dh.getTongThanhToan();

        this.daThanhToan = false;
        this.daCongDiem = false;

        demSoHoaDon++;
    }

    // Constructor sao chep
    public HoaDon(HoaDon other) {
        if (other == null) {
            throw new IllegalArgumentException(
                    "Hoa don sao chep khong duoc null!"
            );
        }

        this.maHD = other.maHD;
        this.thang = other.thang;
        this.nam = other.nam;

        // Dung chung don hang goc
        this.donHangGoc = other.donHangGoc;

        this.soCT = other.soCT;
        this.dsCT = new CTHoaDon[this.soCT];

        for (int i = 0; i < this.soCT; i++) {
            this.dsCT[i] =
                    new CTHoaDon(other.dsCT[i]);
        }

        this.tongTruocThue = other.tongTruocThue;
        this.tongThue = other.tongThue;
        this.tienGiamKM = other.tienGiamKM;
        this.tongThanhToan = other.tongThanhToan;

        this.phuongThucTT = other.phuongThucTT;

        this.daThanhToan = other.daThanhToan;
        this.daCongDiem = other.daCongDiem;

        demSoHoaDon++;
    }

    private static String taoMaHoaDon() {
        return String.format("HD%02d", demSoHoaDon + 1);
    }

    // ==================== GETTER ====================

    public static int getDemSoHoaDon() {
        return demSoHoaDon;
    }

    public String getMaHD() {
        return maHD;
    }

    public int getThang() {
        return thang;
    }

    public int getNam() {
        return nam;
    }

    public int getQuy() {
        return (thang - 1) / 3 + 1;
    }

    public Donhang getDonHangGoc() {
        return donHangGoc;
    }

    // Lay thu ngan tu don hang
    public ThuNgan getThuNgan() {
        return donHangGoc != null
                ? donHangGoc.getThuNgan()
                : null;
    }

    // Lay khach hang tu don hang
    public Khachhang getKhachHang() {
        return donHangGoc != null
                ? donHangGoc.getKhachHang()
                : null;
    }

    public CTHoaDon[] getDsCT() {
        return Arrays.copyOf(dsCT, soCT);
    }

    public int getSoCT() {
        return soCT;
    }

    public double getTongTruocThue() {
        return tongTruocThue;
    }

    public double getTongThue() {
        return tongThue;
    }

    public double getTienGiamKM() {
        return tienGiamKM;
    }

    public double getTongThanhToan() {
        return tongThanhToan;
    }

    public ThanhToan getPhuongThucTT() {
        return phuongThucTT;
    }

    public boolean isDaThanhToan() {
        return daThanhToan;
    }

    public boolean isDaCongDiem() {
        return daCongDiem;
    }

    public String getTenThuNgan() {
        ThuNgan tn = getThuNgan();

        return tn != null
                ? tn.getHoten()
                : "Chua co thu ngan";
    }

    public String getTenKhachHang() {
        Khachhang kh = getKhachHang();

        return kh != null
                ? kh.getHoten()
                : "Khach vang lai";
    }

    // ==================== SETTER ====================

    public void setMaHD(String maHD) {
        if (!daThanhToan && maHD != null
                && !maHD.trim().isEmpty()) {
            this.maHD = maHD;
        }
    }

    public void setThang(int thang) {
        if (!daThanhToan && thang >= 1 && thang <= 12) {
            this.thang = thang;
        }
    }

    public void setNam(int nam) {
        if (!daThanhToan && nam > 0) {
            this.nam = nam;
        }
    }

    public void setPhuongThucTT(ThanhToan phuongThucTT) {
        if (!daThanhToan) {
            this.phuongThucTT = phuongThucTT;
        }
    }

    // ==================== THANH TOAN ====================

    /*
     * Goi ham nay sau khi phuong thuc thanh toan
     * da xu ly va tra ve ket qua thanh cong that su.
     */
    public boolean xacNhanThanhToan(boolean thanhCong) {
        if (daThanhToan) {
            System.out.println("Hoa don da thanh toan!");
            return true;
        }

        if (donHangGoc == null
                || donHangGoc.isDaHuy()
                || !donHangGoc.isDaXacNhan()) {
            System.out.println(
                    "Don hang khong hop le de thanh toan!"
            );
            return false;
        }

        if (phuongThucTT == null) {
            System.out.println(
                    "Vui long chon phuong thuc thanh toan!"
            );
            return false;
        }

        if (!thanhCong) {
            System.out.println("Thanh toan that bai!");
            return false;
        }

        daThanhToan = true;

        // Cong diem khach hang sau khi thanh toan thanh cong
        Khachhang kh = getKhachHang();

//        if (kh != null && !daCongDiem) {
//            kh.congDiem(tongThanhToan);
//            daCongDiem = true;
//        }

        System.out.println("Thanh toan hoa don thanh cong!");
        return true;
    }

    // ==================== NHAP ====================


    public void nhap(Scanner sc) {
        System.out.println(
                "Hoa don duoc tao tu don hang da xac nhan."
        );
        System.out.println(
                "Khong nhap lai khach hang, thu ngan va san pham."
        );

        if (daThanhToan) {
            System.out.println(
                    "Hoa don da thanh toan, khong the sua."
            );
            return;
        }

        System.out.print("Nhap ma hoa don: ");
        maHD = sc.nextLine().trim();

        while (maHD.isEmpty()) {
            System.out.print(
                    "Ma hoa don khong duoc de trong. Nhap lai: "
            );
            maHD = sc.nextLine().trim();
        }

        System.out.print("Nhap thang: ");
        while (!sc.hasNextInt()) {
            System.out.print("Thang phai la so. Nhap lai: ");
            sc.next();
        }
        int thangNhap = sc.nextInt();

        while (thangNhap < 1 || thangNhap > 12) {
            System.out.print("Thang tu 1 den 12. Nhap lai: ");

            while (!sc.hasNextInt()) {
                System.out.print("Thang phai la so. Nhap lai: ");
                sc.next();
            }

            thangNhap = sc.nextInt();
        }

        this.thang = thangNhap;

        System.out.print("Nhap nam: ");
        while (!sc.hasNextInt()) {
            System.out.print("Nam phai la so. Nhap lai: ");
            sc.next();
        }

        int namNhap = sc.nextInt();

        while (namNhap <= 0) {
            System.out.print("Nam khong hop le. Nhap lai: ");

            while (!sc.hasNextInt()) {
                System.out.print("Nam phai la so. Nhap lai: ");
                sc.next();
            }

            namNhap = sc.nextInt();
        }

        this.nam = namNhap;
        sc.nextLine();

        System.out.println(
                "Da cap nhat thong tin co ban cua hoa don."
        );
        System.out.println(
                "Don hang, chi tiet va tong tien khong bi thay doi."
        );
    }

    // ==================== XUAT ====================

    public void xuat() {
        System.out.println(
                "============================================================"
        );
        System.out.println("                    HOA DON BAN HANG");
        System.out.println(
                "============================================================"
        );

        System.out.println("Ma hoa don: " + maHD);
        System.out.println(
                "Thoi gian : " + thang + "/" + nam
        );
        System.out.println(
                "Ma don hang: "
                        + (donHangGoc != null
                        ? donHangGoc.getMaDonHang()
                        : "Chua co")
        );

        System.out.println("Thu ngan  : " + getTenThuNgan());
        System.out.println("Khach hang: " + getTenKhachHang());

        System.out.println(
                "------------------------------------------------------------"
        );
        System.out.println("CHI TIET HOA DON:");

        if (soCT == 0) {
            System.out.println("Hoa don chua co chi tiet.");
        } else {
            for (int i = 0; i < soCT; i++) {
                System.out.print((i + 1) + ". ");
                dsCT[i].xuat();
            }
        }

        System.out.println(
                "------------------------------------------------------------"
        );

        System.out.printf(
                "Tong tien truoc thue : %,.0f VND%n",
                tongTruocThue
        );

        System.out.printf(
                "Tong tien thue       : %,.0f VND%n",
                tongThue
        );

        System.out.printf(
                "Tien giam khuyen mai : %,.0f VND%n",
                tienGiamKM
        );

        System.out.printf(
                "TONG THANH TOAN      : %,.0f VND%n",
                tongThanhToan
        );

        System.out.println(
                "Phuong thuc thanh toan: "
                        + (phuongThucTT != null
                        ? phuongThucTT.getPhuongThuc()
                        : "Chua chon")
        );

        System.out.println(
                "Trang thai: "
                        + (daThanhToan ? "Da thanh toan" : "Chua thanh toan")
        );

//        if (phuongThucTT != null) {
//            phuongThucTT.xuat();
//        }

        System.out.println(
                "============================================================"
        );
    }
    public void inhoadon() {

    }
}