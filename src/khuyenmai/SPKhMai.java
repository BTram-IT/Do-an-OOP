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
        this.maSP = maSP;
        this.maKM = maKM;
    }


    public void nhap(Scanner sc) {
        if (sc == null) sc = new Scanner(System.in);

        while (true) {
            System.out.print("Nhập mã sản phẩm (Ví dụ: TA001, DU001 - Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                this.setMaSP("TA001");
                System.out.println("-> Ma san pham tu dong tao: " + this.maSP);
                break;
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);
                if (!maChuan.startsWith("TA") && !maChuan.startsWith("DU") && !maChuan.startsWith("SP")) {
                    System.out.println("[Lỗi Định Dạng] Mã sản phẩm phải bắt đầu bằng 'TA' (Thức ăn) hoặc 'DU' (Đồ uống)! Hoặc nhấn / để tự tạo.");
                    continue;
                }
                this.setMaSP(maChuan);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }


        while (true) {
            System.out.print("Nhập mã khuyến mãi (Ví dụ: KM001 - Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                this.setMaKM("KM001");
                System.out.println("-> Ma khuyen mai tu dong tao: " + this.maKM);
                break;
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);
                if (!maChuan.startsWith("KM")) {
                    System.out.println("[Lỗi Định Dạng] Mã khuyến mãi phải bắt đầu bằng 'KM' (Ví dụ: KM001, KM_TET)! Hoặc nhấn / để tự tạo.");
                    continue;
                }
                this.setMaKM(maChuan);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public String toDongFile() {
        return this.maSP + ";" + this.maKM;
    }

    public static SPKhMai tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty()) {
            return null;
        }

        String[] parts = dong.split("[;|]");
        if (parts.length >= 2) {
            String sp = parts[0].trim();
            String km = parts[1].trim();
            if (!sp.isEmpty() && !km.isEmpty()) {
                return new SPKhMai(sp, km);
            }
        }
        return null;
    }


    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        if (maSP == null || maSP.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã sản phẩm không được để trống!");
        }
        this.maSP = Ktradulieu.chuanHoaMa(maSP);
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã khuyến mãi không được để trống!");
        }
        this.maKM = Ktradulieu.chuanHoaMa(maKM);
    }

    @Override
    public String toString() {
        return "San pham: " + maSP + " <---> Khuyen mai: " + maKM;
    }
}