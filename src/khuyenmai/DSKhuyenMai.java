package khuyenmai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import sanpham.SanPham;
import util.Ktradulieu;

public class DSKhuyenMai {
    private Khuyenmai[] danhSach;
    private int soLuong;
    private static final int MAX = 100;
    private static final Scanner sc = new Scanner(System.in);

    // Constructor
    public DSKhuyenMai() {
        danhSach = new Khuyenmai[MAX];
        soLuong = 0;
    }

    // Getter
    public Khuyenmai[] getDanhSach() {
        return danhSach;
    }

    public int getSoLuong() {
        return soLuong;
    }

    // Tìm vị trí khuyến mãi theo mã
    public int timKhuyenMai(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return -1;
        }
        String maChuan = SanPham.chuanHoaMa(maKM);
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getMaKM() != null
                    && danhSach[i].getMaKM().equalsIgnoreCase(maChuan)) {
                return i;
            }
        }
        return -1;
    }

    // Tìm đối tượng khuyến mãi theo mã
    public Khuyenmai timTheoMa(String maKM) {
        int viTri = timKhuyenMai(maKM);
        return viTri == -1 ? null : danhSach[viTri];
    }

    // Thêm khuyến mãi
    public void themKhuyenMai(Khuyenmai km) {
        if (km == null || km.getMaKM() == null || km.getMaKM().trim().isEmpty()) {
            System.out.println("Khuyến mãi không hợp lệ!");
            return;
        }

        if (soLuong >= MAX) {
            System.out.println("Danh sách khuyến mãi đã đầy!");
            return;
        }

        if (timKhuyenMai(km.getMaKM()) != -1) {
            System.out.println("Mã khuyến mãi đã tồn tại!");
            return;
        }

        danhSach[soLuong] = km;
        soLuong++;

        System.out.println("Thêm khuyến mãi thành công!");
    }

    // Tìm kiếm khuyến mãi
    public void timKiemKhuyenMai() {
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần tìm: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKhuyenMai(maKM);

        if (viTri == -1) {
            System.out.println("Không tìm thấy khuyến mãi!");
            return;
        }

        System.out.println("Thông tin khuyến mãi:");
        System.out.println(danhSach[viTri]);
    }

    // Sửa khuyến mãi
    public void suaKhuyenMai() {
        String ma = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần sửa: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKhuyenMai(ma);

        if (viTri == -1) {
            System.out.println("Không tìm thấy khuyến mãi!");
            return;
        }

        Khuyenmai tam = danhSach[viTri].taoKhuyenMaiRong();
        tam.setKM();

        if (trungMa(tam.getMaKM(), viTri)) {
            System.out.println("Mã khuyến mãi đã tồn tại! Không thể sửa.");
            return;
        }

        danhSach[viTri] = tam;
        System.out.println("Sửa khuyến mãi thành công!");
    }

    // Kiểm tra trùng mã
    public boolean trungMa(String maKM, int viTriBoQua) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return false;
        }
        String maChuan = SanPham.chuanHoaMa(maKM);
        for (int i = 0; i < soLuong; i++) {
            if (i != viTriBoQua && danhSach[i] != null
                    && danhSach[i].getMaKM() != null
                    && danhSach[i].getMaKM().equalsIgnoreCase(maChuan)) {
                return true;
            }
        }
        return false;
    }

    // Xóa khuyến mãi theo mã
    public boolean xoaKhuyenMai(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return false;
        }
        int viTri = timKhuyenMai(maKM);
        if (viTri == -1) {
            return false;
        }
        for (int i = viTri; i < soLuong - 1; i++) {
            danhSach[i] = danhSach[i + 1];
        }
        danhSach[soLuong - 1] = null;
        soLuong--;
        return true;
    }

    public boolean xoa(String maKM) {
        return xoaKhuyenMai(maKM);
    }

    // Xóa khuyến mãi (Giao diện nhập)
    public boolean xoaKhuyenMai() {
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần xóa: ", ".+",
                "Mã khuyến mãi không được để trống!");
        boolean kq = xoaKhuyenMai(maKM);
        if (kq) {
            System.out.println("Xóa khuyến mãi thành công!");
        } else {
            System.out.println("Không tìm thấy khuyến mãi!");
        }
        return kq;
    }

    // Xuất danh sách khuyến mãi
    public void xuatDanhSach() {
        if (soLuong == 0) {
            System.out.println("Danh sách khuyến mãi đang trống!");
            return;
        }

        System.out.println("========== DANH SÁCH KHUYẾN MÃI ==========");

        for (int i = 0; i < soLuong; i++) {
            System.out.println("Khuyến mãi thứ " + (i + 1));
            System.out.println(danhSach[i]);
            System.out.println("----------------------------------------");
        }

        System.out.println("Tổng số chương trình khuyến mãi: " + soLuong);
    }

    // =========================================================================
    // PHƯƠNG THỨC XỬ LÝ FILE (ĐỌC & GHI)
    // =========================================================================

    // Ghi danh sách khuyến mãi xuống file
    public void ghiFile(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Tự động tạo thư mục data nếu chưa có
        }

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            for (int i = 0; i < soLuong; i++) {
                if (danhSach[i] != null) {
                    // Gọi phương thức chuyển đối tượng thành dòng văn bản (giả sử Khuyenmai có toDongFile())
                    bw.write(danhSach[i].toDongFile());
                    bw.newLine();
                }
            }
            System.out.println("-> Lưu file khuyến mãi thành công tại: " + filePath);
        } catch (IOException e) {
            System.out.println("Lỗi ghi file khuyến mãi: " + e.getMessage());
        }
    }

    // Đọc danh sách khuyến mãi từ file
    public void docFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Chưa có file dữ liệu khuyến mãi cũ, hệ thống sẽ tạo mới khi lưu.");
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String dong;
            soLuong = 0; // Reset số lượng hiện tại trước khi load
            while ((dong = br.readLine()) != null) {
                dong = dong.trim();
                if (dong.isEmpty()) continue;

                // Phân tích dòng file thành đối tượng Khuyenmai
                // (Giả sử lớp Khuyenmai hoặc lớp con có phương thức tuDongFile(dong))
                Khuyenmai km = Khuyenmai.tuDongFile(dong);
                if (km != null && soLuong < MAX) {
                    danhSach[soLuong] = km;
                    soLuong++;
                    // Đồng thời cập nhật bộ đếm mã nếu có cơ chế tự tăng
                    // Khuyenmai.capNhatBoDem(km.getMaKM());
                }
            }
            System.out.println("-> Đọc file khuyến mãi thành công! Tổng số: " + soLuong);
        } catch (IOException e) {
            System.out.println("Lỗi đọc file khuyến mãi: " + e.getMessage());
        }
    }
}