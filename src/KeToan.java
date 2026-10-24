import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.Scanner;

public class KeToan extends Nhanvien{
    private String BangCap;
    private String Chungchi;

    public KeToan(String maNV, LocalDate ngaybatdaulam, String chucvu, String Hoten, String SDT, String Email, String Diachi, String bangCap, String chungchi) {
        super(maNV, ngaybatdaulam, chucvu, Hoten, SDT, Email, Diachi);
        BangCap = bangCap;
        Chungchi = chungchi;
    }

    public KeToan(String bangCap, String chungchi) {
        BangCap = bangCap;
        Chungchi = chungchi;
    }

    public KeToan() {
        super();
        BangCap = "";
        Chungchi = "";
    }

    public String getBangCap() {
        return BangCap;
    }

    public void setBangCap(String bangCap) {
        BangCap = bangCap;
    }

    public String getChungchi() {
        return Chungchi;
    }

    public void setChungchi(String chungchi) {
        Chungchi = chungchi;
    }

    Scanner sc = new Scanner(System.in);
    public void Nhap() {
        super.Nhap();
        System.out.println("Nhập bằng cấp: ");
        BangCap = sc.nextLine();
        System.out.println("Nhập chứng chỉ: ");
        Chungchi = sc.nextLine();
    }
    public void Xuat() {
        super.Xuat();
    }

    @Override
    public String toString() {
        return "KeToan{" +
                "BangCap='" + BangCap + '\'' +
                ", Chungchi='" + Chungchi + '\'' +
                ", sc=" + sc +
                '}';
    }

}
