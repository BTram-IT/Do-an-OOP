
package khuyenmai;

import java.util.Scanner;
import util.Ktradulieu;

public class SPKhMai {

    private String maSP;
    private String maKM;

    public SPKhMai() {
        this.maSP = "";
        this.maKM = "";
    }

    public SPKhMai(String maSP, String maKM) {
        setLienKet(maSP, maKM);
    }

    // Kiểm tra mã sản phẩm
    private String kiemTraMaSP(String maSP) {

        if (maSP == null || maSP.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã sản phẩm không được để trống!");
        }

        String maChuan = Ktradulieu.chuanHoaMa(maSP);

        if (!maChuan.startsWith("TA")
                && !maChuan.startsWith("DU")
                && !maChuan.startsWith("SP")) {

            throw new IllegalArgumentException(
                    "Mã sản phẩm phải bắt đầu bằng TA, DU hoặc SP!");
        }

        return maChuan;
    }

    // Kiểm tra mã khuyến mãi
    private String kiemTraMaKM(String maKM) {

        if (maKM == null || maKM.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Mã khuyến mãi không được để trống!");
        }

        String maChuan = Ktradulieu.chuanHoaMa(maKM);

        if (!maChuan.startsWith("KM")) {
            throw new IllegalArgumentException(
                    "Mã khuyến mãi phải bắt đầu bằng KM!");
        }

        return maChuan;
    }

    // Cập nhật đồng thời hai mã
    public void setLienKet(String maSP, String maKM) {

        String spMoi = kiemTraMaSP(maSP);
        String kmMoi = kiemTraMaKM(maKM);

        this.maSP = spMoi;
        this.maKM = kmMoi;
    }

    public void nhap(Scanner sc) {

        if (sc == null) {
            throw new IllegalArgumentException(
                    "Scanner không được null!");
        }

        String maSPMoi;
        String maKMMoi;

        while (true) {

            System.out.print("Nhập mã sản phẩm: ");
            maSPMoi = sc.nextLine().trim();

            try {
                maSPMoi = kiemTraMaSP(maSPMoi);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {

            System.out.print("Nhập mã khuyến mãi: ");
            maKMMoi = sc.nextLine().trim();

            try {
                maKMMoi = kiemTraMaKM(maKMMoi);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setLienKet(maSPMoi, maKMMoi);
    }

    public String toDongFile() {

        // Không cho ghi liên kết rỗng
        String sp = kiemTraMaSP(this.maSP);
        String km = kiemTraMaKM(this.maKM);

        return sp + ";" + km;
    }

    public static SPKhMai tuDongFile(String dong) {

        if (dong == null || dong.trim().isEmpty()) {
            return null;
        }

        String[] parts = dong.split(";", -1);

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Dòng liên kết phải có đúng 2 trường!");
        }

        return new SPKhMai(
                parts[0].trim(),
                parts[1].trim());
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = kiemTraMaSP(maSP);
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = kiemTraMaKM(maKM);
    }

    @Override
    public String toString() {
        return "Sản phẩm: " + maSP
                + " <--> Khuyến mãi: " + maKM;
    }
}
