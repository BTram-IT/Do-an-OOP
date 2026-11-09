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
import java.util.ArrayList;
import java.util.Scanner;
import sanpham.SanPham;
import util.Ktradulieu;

public class DSSanPhamKhuyenMai {
    private ArrayList<SPKhMai> danhSach;
    private static final Scanner sc = new Scanner(System.in);

    // Constructor
    public DSSanPhamKhuyenMai() {
        danhSach = new ArrayList<>();
    }

    // Getter
    public SPKhMai[] getDanhSach() {
        return danhSach.toArray(new SPKhMai[0]);
    }

    public int getSoLuong() {
        return danhSach.size();
    }

    // Tìm kiếm sản phẩm khuyến mãi
    public int timKiem(String maSP, String maKM) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaSP().equalsIgnoreCase(maSP)
                    && danhSach.get(i).getMaKM().equalsIgnoreCase(maKM)) {
                return i;
            }
        }
        return -1;
    }

    // Thêm sản phẩm khuyến mãi
    public void themSP() {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm: ", ".+",
                "Mã sản phẩm không được để trống!");

        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi: ", ".+",
                "Mã khuyến mãi không được để trống!");

        if (timKiem(maSP, maKM) != -1) {
            System.out.println("Sản phẩm đã thuộc chương trình khuyến mãi này!");
            return;
        }

        danhSach.add(new SPKhMai(maSP, maKM));
        System.out.println("Thêm sản phẩm khuyến mãi thành công!");
    }

    // Sửa sản phẩm khuyến mãi
    public void suaSP() {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm cần sửa: ", ".+",
                "Mã sản phẩm không được để trống!");

        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần sửa: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKiem(maSP, maKM);

        if (viTri == -1) {
            System.out.println("Không tìm thấy sản phẩm khuyến mãi!");
            return;
        }

        System.out.println("\n===== CHỌN THÔNG TIN CẦN SỬA =====");
        System.out.println("1. Sửa mã sản phẩm");
        System.out.println("2. Sửa mã khuyến mãi");
        System.out.println("3. Sửa cả hai");
        System.out.println("0. Thoát");

        int chon = Ktradulieu.luaChon(sc, "Nhập lựa chọn: ", 0, 3);

        String maSPMoi = maSP;
        String maKMMoi = maKM;

        switch (chon) {
            case 1:
                maSPMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã sản phẩm mới: ", ".+",
                        "Mã sản phẩm không được để trống!");
                break;

            case 2:
                maKMMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã khuyến mãi mới: ", ".+",
                        "Mã khuyến mãi không được để trống!");
                break;

            case 3:
                maSPMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã sản phẩm mới: ", ".+",
                        "Mã sản phẩm không được để trống!");

                maKMMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã khuyến mãi mới: ", ".+",
                        "Mã khuyến mãi không được để trống!");
                break;

            case 0:
                System.out.println("Đã hủy sửa!");
                return;
        }

        // Kiểm tra trùng dữ liệu
        int viTriMoi = timKiem(maSPMoi, maKMMoi);

        if (viTriMoi != -1 && viTriMoi != viTri) {
            System.out.println("Sản phẩm khuyến mãi này đã tồn tại!");
            return;
        }

        danhSach.set(viTri, new SPKhMai(maSPMoi, maKMMoi));

        System.out.println("Sửa sản phẩm khuyến mãi thành công!");
    }

    // Xóa sản phẩm khuyến mãi
    public void xoaSP() {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm cần xóa: ", ".+",
                "Mã sản phẩm không được để trống!");

        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKiem(maSP, maKM);

        if (viTri == -1) {
            System.out.println("Không tìm thấy sản phẩm khuyến mãi!");
            return;
        }

        danhSach.remove(viTri);
        System.out.println("Xóa sản phẩm khuyến mãi thành công!");
    }

    public boolean kiemTraLienKet(String maSP, String maKM) {
        if (maSP == null || maKM == null) return false;

        String sp = SanPham.chuanHoaMa(maSP);
        String km = SanPham.chuanHoaMa(maKM);

        for (SPKhMai item : danhSach) {
            if (item != null
                    && sp.equals(SanPham.chuanHoaMa(item.getMaSP()))
                    && km.equals(SanPham.chuanHoaMa(item.getMaKM()))) {
                return true;
            }
        }
        return false;
    }

    // Xuất danh sách sản phẩm khuyến mãi
    public void xuatDanhSach() {
        if (danhSach.isEmpty()) {
            System.out.println("Danh sách sản phẩm khuyến mãi đang trống!");
            return;
        }

        System.out.println("\n===== DANH SÁCH SẢN PHẨM KHUYẾN MÃI =====");

        for (int i = 0; i < danhSach.size(); i++) {
            System.out.println("Sản phẩm khuyến mãi thứ " + (i + 1));
            System.out.println(danhSach.get(i));
            System.out.println("----------------------------------------");
        }

        System.out.println("Tổng số sản phẩm khuyến mãi: " + danhSach.size());
    }

    // =========================================================================
    // XỬ LÝ FILE (ĐỌC & GHI)
    // =========================================================================

    // Ghi danh sách liên kết sản phẩm - khuyến mãi xuống file
    public void ghiFile(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs(); // Tự động tạo thư mục data nếu chưa có
        }

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            for (SPKhMai item : danhSach) {
                if (item != null) {
                    // Gọi hàm toDongFile() của đối tượng SPKhMai
                    bw.write(item.toDongFile());
                    bw.newLine();
                }
            }
            System.out.println("-> Lưu file sản phẩm khuyến mãi thành công tại: " + filePath);
        } catch (IOException e) {
            System.out.println("Lỗi ghi file sản phẩm khuyến mãi: " + e.getMessage());
        }
    }

    // Đọc danh sách liên kết sản phẩm - khuyến mãi từ file
    public void docFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Chưa có file dữ liệu sản phẩm khuyến mãi cũ, hệ thống sẽ tạo mới khi lưu.");
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String dong;
            danhSach.clear(); // Làm sạch danh sách hiện tại trước khi load
            while ((dong = br.readLine()) != null) {
                dong = dong.trim();
                if (dong.isEmpty()) continue;

                // Gọi hàm tĩnh tuDongFile(String) của lớp SPKhMai để khôi phục đối tượng
                SPKhMai item = SPKhMai.tuDongFile(dong);
                if (item != null) {
                    danhSach.add(item);
                }
            }
            System.out.println("-> Đọc file sản phẩm khuyến mãi thành công! Tổng số: " + danhSach.size());
        } catch (IOException e) {
            System.out.println("Lỗi đọc file sản phẩm khuyến mãi: " + e.getMessage());
        }
    }
}