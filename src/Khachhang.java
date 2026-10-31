import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class KhachHang extends Nguoidung {
    private String maKH;
    private String thuHang; // Dong, Bac, Vang, Kim Cuong
    private LocalDate ngayDangKy;
    private double tongTienMua;
    private ArrayList<String> lichSuMuaHang; // Lưu danh sách mã đơn / hóa đơn đã mua (mảng động)

    public KhachHang() {
        super();
        this.maKH = "";
        this.thuHang = "Dong";
        this.ngayDangKy = LocalDate.now();
        this.tongTienMua = 0;
        this.lichSuMuaHang = new ArrayList<>();
    }

    public KhachHang(String ten, String sdt, String diaChi, String email, LocalDate ngaySinh,
                     String maKH, LocalDate ngayDangKy, double tongTienMua) {
        super(ten, sdt, diaChi, email, ngaySinh);
        this.maKH = maKH;
        this.ngayDangKy = ngayDangKy;
        this.tongTienMua = tongTienMua;
        this.lichSuMuaHang = new ArrayList<>();
        capNhatThuHang();
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getThuHang() { return thuHang; }
    public LocalDate getNgayDangKy() { return ngayDangKy; }
    public double getTongTienMua() { return tongTienMua; }

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
        lichSuMuaHang.add(maHD + " (Tong: " + tien + " VND)");
        this.tongTienMua += tien;
        capNhatThuHang();
    }

    public void xemLichSuMuaHang() {
        System.out.println("=== LICH SU MUA HANG CUA KH: " + maKH + " - " + ten + " ===");
        if (lichSuMuaHang.isEmpty()) {
            System.out.println("Chua co giao dich nao.");
        } else {
            for (String item : lichSuMuaHang) {
                System.out.println("- " + item);
            }
        }
    }

    public void xemHanThanhVien() {
        LocalDate han = ngayDangKy.plusYears(1);
        System.out.println("Han the thanh vien: Den ngay " + han);
    }

    @Override
    public void nhap(Scanner sc) {
        this.maKH = Ktradulieu.taoMa("KH%03d", 1);
        super.nhap(sc);
        this.ngayDangKy = LocalDate.now();
        this.tongTienMua = 0;
        this.thuHang = "Dong";
    }

    @Override
    public String toString() {
        return super.toString() + " | MaKH: " + maKH + " | Hang: " + thuHang + " | Tong chi: " + String.format("%,.0f", tongTienMua) + " VND";
    }
}
