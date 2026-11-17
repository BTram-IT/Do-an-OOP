
package khuyenmai;

import util.BangConsole;
import util.HienThiBang;

import java.util.Scanner;
import util.Ktradulieu;

public class SPKhMai implements HienThiBang {

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

        if (!maChuan.matches("^(TA|DU|SP)\\d+$")) {

            throw new IllegalArgumentException(
                    "Mã sản phẩm phải có tiền tố TA, DU hoặc SP và theo sau là chữ số!");
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

        if (!maChuan.matches("^KM\\d+$")) {
            throw new IllegalArgumentException(
                    "Mã khuyến mãi phải có tiền tố KM và theo sau là chữ số!");
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
    public String[] layTieuDeBang() {
        return new String[]{"MÃ SẢN PHẨM", "MÃ KHUYẾN MÃI"};
    }

    @Override
    public Object[] layDongBang() {
        return new Object[]{maSP, maKM};
    }

    @Override
    public String toString() {
        return BangConsole.tuDoiTuong(this);
    }
}
