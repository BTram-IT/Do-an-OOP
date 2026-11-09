import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Khachhang extends Nguoidung {
    private static int boDem = 0;
    private static final String TIEN_TO_MA = "KH";

    private String maKH;
    private String thuHang;
    private LocalDate ngayDangKy;
    private double tongTienMua;
    private ArrayList<String> lichSuMuaHang;

    public Khachhang() {
        super();
        this.maKH = maKeTiep();
        this.thuHang = "Dong";
        this.ngayDangKy = LocalDate.now();
        this.tongTienMua = 0;
        this.lichSuMuaHang = new ArrayList<>();
    }

    public Khachhang(String ten, String sdt, String diaChi, String email,
                     LocalDate ngaySinh, String maKH, LocalDate ngayDangKy,
                     double tongTienMua) {
        super(ten, sdt, diaChi, email, ngaySinh);
        setMaKH(maKH);
        setNgayDangKy(ngayDangKy);
        setTongTienMua(tongTienMua);
        this.lichSuMuaHang = new ArrayList<>();
        capNhatThuHang();
    }

    public static synchronized String maKeTiep() {
        boDem++;
        return Ktradulieu.taoMa("KH%03d", boDem);
    }

    private static void capNhatBoDem(String maKH) {
        if (maKH != null && maKH.matches("^KH\\d+$")) {
            int so = Integer.parseInt(maKH.substring(2));
            boDem = Math.max(boDem, so);
        }
    }

    public String getMaKH() { return maKH; }

    public void setMaKH(String maKH) {
        maKH = Ktradulieu.chuanHoaMa(maKH);
        if (!maKH.matches("^KH\\d{3,}$"))
            throw new IllegalArgumentException("Mã khách hàng không hợp lệ!");
        this.maKH = maKH;
        capNhatBoDem(maKH);
    }

    public String getThuHang() { return thuHang; }
    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public double getTongTienMua() { return tongTienMua; }

    public ArrayList<String> getLichSuMuaHang() {
        return new ArrayList<>(lichSuMuaHang);
    }

    public void setNgayDangKy(LocalDate ngayDangKy) {
        if (ngayDangKy == null || ngayDangKy.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày đăng ký không hợp lệ!");
        this.ngayDangKy = ngayDangKy;
    }

    public void setTongTienMua(double tongTienMua) {
        if (!Double.isFinite(tongTienMua) || tongTienMua < 0)
            throw new IllegalArgumentException("Tổng tiền mua không hợp lệ!");
        this.tongTienMua = tongTienMua;
        capNhatThuHang();
    }

    public void capNhatThuHang() {
        if (tongTienMua >= 10000000) {
            this.thuHang = "Kim Cuong";
        } else if (tongTienMua >= 5000000) {
            this.thuHang = "Vang";
        } else if (tongTienMua >= 2000000) {
            this.thuHang = "Bac";
        } else {
            this.thuHang = "Dong";
        }
    }

    public void themLichSu(String maHD, double tien) {
        if (maHD == null || maHD.trim().isEmpty())
            throw new IllegalArgumentException("Mã hóa đơn không được để trống!");

        if (!Double.isFinite(tien) || tien < 0)
            throw new IllegalArgumentException("Số tiền mua không hợp lệ!");

        maHD = maHD.trim();

        for (String item : lichSuMuaHang) {
            if (item.startsWith(maHD + " (Tong: "))
                throw new IllegalArgumentException("Hóa đơn này đã được ghi nhận!");
        }

        double tongMoi = tongTienMua + tien;
        if (!Double.isFinite(tongMoi))
            throw new IllegalArgumentException("Tổng tiền mua vượt giới hạn!");

        lichSuMuaHang.add(maHD + " (Tong: " + tien + " VND)");
        this.tongTienMua = tongMoi;
        capNhatThuHang();
    }

    public void xemLichSuMuaHang() {
        System.out.println("=== LICH SU MUA HANG CUA KH: " + maKH + " - " + ten + " ===");

        if (lichSuMuaHang.isEmpty()) {
            System.out.println("Chua co giao dich nao.");
            return;
        }

        for (String item : lichSuMuaHang) {
            System.out.println("- " + item);
        }
    }

    public void xemHanThanhVien() {
        LocalDate han = ngayDangKy.plusYears(1);
        System.out.println("Han the thanh vien: Den ngay " + han);
    }

    @Override
    public void nhap(Scanner sc) {
        super.nhap(sc);
        this.maKH = maKeTiep();
        this.ngayDangKy = LocalDate.now();
        this.tongTienMua = 0;
        this.thuHang = "Dong";
        this.lichSuMuaHang.clear();
    }

    @Override
    public String toString() {
        return super.toString()
                + " | MaKH: " + maKH
                + " | Hang: " + thuHang
                + " | Ngay dang ky: " + ngayDangKy
                + " | Tong chi: " + String.format("%,.0f", tongTienMua) + " VND";
    }
}
