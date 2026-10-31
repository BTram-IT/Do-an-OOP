import java.time.LocalDate;
import java.util.Scanner;

public class NhanVien extends Nguoidung {
    private String maNV;
    private double luongCoBan;
    private LocalDate ngayVaoLam;
    private int kinhNghiem; // tính theo năm hoặc số tháng
    private String chucVu; // Thu ngan, Quan ly kho, Bep, Phuc vu
    private int soCaLam; // Số ca làm trong tháng

    public NhanVien() {
        super();
        this.maNV = "";
        this.luongCoBan = 5000000;
        this.ngayVaoLam = LocalDate.now();
        this.kinhNghiem = 0;
        this.chucVu = "Nhan vien";
        this.soCaLam = 26;
    }

    public NhanVien(String ten, String sdt, String diaChi, String email, LocalDate ngaySinh,
                    String maNV, double luongCoBan, LocalDate ngayVaoLam, int kinhNghiem, String chucVu) {
        super(ten, sdt, diaChi, email, ngaySinh);
        this.maNV = maNV;
        this.luongCoBan = luongCoBan;
        this.ngayVaoLam = ngayVaoLam;
        this.kinhNghiem = kinhNghiem;
        this.chucVu = chucVu;
        this.soCaLam = 26;
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(double luongCoBan) { this.luongCoBan = luongCoBan; }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) { this.chucVu = chucVu; }

    public int getSoCaLam() { return soCaLam; }
    public void setSoCaLam(int soCaLam) { this.soCaLam = soCaLam; }

    public double tinhLuong() {
        // Lương = lương cơ bản + phụ cấp kinh nghiệm (500k/năm) + lương ca làm
        double phuCap = kinhNghiem * 500000;
        return luongCoBan + phuCap + (soCaLam * 150000);
    }

    @Override
    public void nhap(Scanner sc) {
        this.maNV = Ktradulieu.nhapChuoi(sc, "Nhap Ma NV: ",Ktradulieu.taoMa("NV%03d", 1),"Mã nhân viên không hợp lệ! Ví dụ: NV001");
        super.nhap(sc);
        this.luongCoBan = Ktradulieu.nhapSoNguyen(sc, "Nhap Luong Co Ban: ");
        this.kinhNghiem = Math.toIntExact(Ktradulieu.nhapSoNguyen(sc, "Nhap Kinh Nghiem (nam): "));
        this.chucVu = Ktradulieu.nhapChuoi(sc, "Nhap Chuc Vu: ","^[\\p{L}]+(?:\\s[\\p{L}]+)*$","Nhập chức vụ không hợp lệ, vui lòng nhập lại!");
        this.ngayVaoLam = LocalDate.now();
    }

    @Override
    public String toString() {
        return super.toString() + " | MaNV: " + maNV + " | Chuc vu: " + chucVu + " | Luong thuc nhan: " + String.format("%,.0f", tinhLuong()) + " VND";
    }
}
