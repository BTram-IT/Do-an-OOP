package khuyenmai;

import java.util.Scanner;
import util.Ktradulieu;

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

    // =========================================================================
    // XỬ LÝ FILE (TO DÒNG & TỪ ĐÒNG FILE)
    // =========================================================================

    // Chuyển đối tượng thành chuỗi ngăn cách bằng dấu "|" để lưu file
    public String toDongFile() {
        return maSP + "|" + maKM;
    }

    // Hàm tĩnh đọc dòng từ file và chuyển đổi thành đối tượng SPKhMai
    public static SPKhMai tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty()) {
            return null;
        }
        String[] parts = dong.split("\\|");
        if (parts.length >= 2) {
            String maSP = parts[0].trim();
            String maKM = parts[1].trim();
            return new SPKhMai(maSP, maKM);
        }
        return null;
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