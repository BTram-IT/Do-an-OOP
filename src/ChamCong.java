import java.util.Scanner;

public class ChamCong {
    private String maChamCong;
    private String maNV; // Liên kết với Nhanvien.java
    private CaLam caLam; // Liên kết với CaLam.java
    private String ngayChamCong;
    private boolean coMat; // true: Có mặt, false: Vắng
    private double soGioTangCa;

    public ChamCong() {
        this.caLam = new CaLam();
    }

    public ChamCong(String maChamCong, String maNV, CaLam caLam, String ngayChamCong, boolean coMat, double soGioTangCa) {
        this.maChamCong = maChamCong;
        this.maNV = maNV;
        this.caLam = caLam;
        this.ngayChamCong = ngayChamCong;
        this.coMat = coMat;
        this.soGioTangCa = soGioTangCa;
    }

    // Getters và Setters
    public String getMaChamCong() { return maChamCong; }
    public void setMaChamCong(String maChamCong) { this.maChamCong = maChamCong; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public CaLam getCaLam() { return caLam; }
    public void setCaLam(CaLam caLam) { this.caLam = caLam; }

    public String getNgayChamCong() { return ngayChamCong; }
    public void setNgayChamCong(String ngayChamCong) { this.ngayChamCong = ngayChamCong; }

    public boolean isCoMat() { return coMat; }
    public void setCoMat(boolean coMat) { this.coMat = coMat; }

    public double getSoGioTangCa() { return soGioTangCa; }
    public void setSoGioTangCa(double soGioTangCa) { this.soGioTangCa = soGioTangCa; }

    // Tính tiền công của buổi chấm công này (giả sử mỗi giờ tăng ca = 30,000đ)
    public double tinhTienCongNgay() {
        if (!coMat) return 0;
        return caLam.getLuongTheoCa() + (soGioTangCa * 30000);
    }

    public void nhap() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap ma cham cong: ");
        this.maChamCong = sc.nextLine();
        System.out.print("Nhap ma nhan vien: ");
        this.maNV = sc.nextLine();
        System.out.print("Nhap ngay cham cong (dd/mm/yyyy): ");
        this.ngayChamCong = sc.nextLine();
        
        System.out.println("--- Nhap thong tin ca lam ---");
        this.caLam.nhap();

        System.out.print("Nhan vien co mat khong? (1: Co, 0: Vang): ");
        int status = Integer.parseInt(sc.nextLine());
        this.coMat = (status == 1);

        if (this.coMat) {
            System.out.print("Nhap so gio tang ca: ");
            this.soGioTangCa = Double.parseDouble(sc.nextLine());
        } else {
            this.soGioTangCa = 0;
        }
    }

    public void xuat() {
        String trangThai = coMat ? "Co mat" : "Vang";
        System.out.printf("| %-10s | %-8s | %-12s | %-10s | %-8s | Tang ca: %-4.1fh | Luong ngay: %,.0f VND |\n",
                maChamCong, maNV, ngayChamCong, caLam.getTenCa(), trangThai, soGioTangCa, tinhTienCongNgay());
    }
}
