import java.util.Scanner;

public class SPKhMai {
    private String maSP;
    private String maKM;

    // Constructor mặc định
    public SPKhMai() {
    }

    // Constructor đầy đủ
    public SPKhMai(String maSP, String maKM) {
        this.maSP = maSP;
        this.maKM = maKM;
    }

    // Nhập thông tin
    public void nhap(Scanner sc) {
        maSP = Ktradulieu.nhapChuoi(
                sc,
                "Nhập mã sản phẩm: ",
                ".+",
                "Mã sản phẩm không được để trống!"
        );

        maKM = Ktradulieu.nhapChuoi(
                sc,
                "Nhập mã khuyến mãi: ",
                ".+",
                "Mã khuyến mãi không được để trống!"
        );
    }

    // Getter và Setter
    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    @Override
    public String toString() {
        return "Mã sản phẩm: " + maSP
                + "\nMã khuyến mãi: " + maKM;
    }
}