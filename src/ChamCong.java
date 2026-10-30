import java.time.LocalDate;
import java.util.Scanner;

public class ChamCong {
    private String maChamCong;
    private String maNV;
    private String maCa;
    private LocalDate ngayChamCong;
    private boolean coMat;
    private String ghiChu;

    public ChamCong() {
        this.maChamCong = "";
        this.maNV = "";
        this.maCa = "";
        this.ngayChamCong = LocalDate.now();
        this.coMat = true;
        this.ghiChu = "Dung gio";
    }

    public ChamCong(String maChamCong, String maNV, String maCa, LocalDate ngayChamCong, boolean coMat, String ghiChu) {
        this.maChamCong = maChamCong;
        this.maNV = maNV;
        this.maCa = maCa;
        this.ngayChamCong = ngayChamCong;
        this.coMat = coMat;
        this.ghiChu = ghiChu;
    }

    public String getMaChamCong() { return maChamCong; }
    public String getMaNV() { return maNV; }
    public String getMaCa() { return maCa; }
    public LocalDate getNgayChamCong() { return ngayChamCong; }
    public boolean isCoMat() { return coMat; }

    public void nhap(Scanner sc) {
        System.out.print("Nhap Ma Cham Cong: ");
        this.maChamCong = sc.nextLine();
        System.out.print("Nhap Ma NV: ");
        this.maNV = sc.nextLine();
        System.out.print("Nhap Ma Ca lam: ");
        this.maCa = sc.nextLine();
        this.ngayChamCong = LocalDate.now();
        System.out.print("Co mat hay khong (true/false): ");
        this.coMat = Boolean.parseBoolean(sc.nextLine());
        System.out.print("Ghi chu: ");
        this.ghiChu = sc.nextLine();
    }

    @Override
    public String toString() {
        return String.format("[%s] Ngay: %s | NV: %-6s | Ca: %-5s | Trang thai: %s | Ghi chu: %s",
                maChamCong, ngayChamCong, maNV, maCa, (coMat ? "Co mat" : "Vang mat"), ghiChu);
    }
}
