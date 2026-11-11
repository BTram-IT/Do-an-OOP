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
import util.Ktradulieu;

public class DSSanPhamKhuyenMai {

    private SPKhMai[] danhSach;
    private int soLuong;
    private static final int SUC_CHUA_MAC_DINH = 50;

    public DSSanPhamKhuyenMai() {
        this(SUC_CHUA_MAC_DINH);
    }

    public DSSanPhamKhuyenMai(int sucChuaBanDau) {
        if (sucChuaBanDau <= 0) {
            sucChuaBanDau = SUC_CHUA_MAC_DINH;
        }
        this.danhSach = new SPKhMai[sucChuaBanDau];
        this.soLuong = 0;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public SPKhMai[] getDanhSach() {
        SPKhMai[] ketQua = new SPKhMai[this.soLuong];
        for (int i = 0; i < this.soLuong; i++) {
            ketQua[i] = this.danhSach[i];
        }
        return ketQua;
    }

    private void moRongBoNho() {
        int sucChuaMoi = this.danhSach.length * 2;
        SPKhMai[] mangMoi = new SPKhMai[sucChuaMoi];
        for (int i = 0; i < this.soLuong; i++) {
            mangMoi[i] = this.danhSach[i];
        }
        this.danhSach = mangMoi;
    }

    public int timKiem(String maSP, String maKM) {
        if (maSP == null || maKM == null) {
            return -1;
        }

        String spChuan = Ktradulieu.chuanHoaMa(maSP);
        String kmChuan = Ktradulieu.chuanHoaMa(maKM);

        for (int i = 0; i < this.soLuong; i++) {
            if (this.danhSach[i] != null
                    && this.danhSach[i].getMaSP() != null
                    && this.danhSach[i].getMaKM() != null
                    && this.danhSach[i].getMaSP().equalsIgnoreCase(spChuan)
                    && this.danhSach[i].getMaKM().equalsIgnoreCase(kmChuan)) {
                return i;
            }
        }
        return -1;
    }

    public boolean kiemTraLienKet(String maSP, String maKM) {
        return timKiem(maSP, maKM) != -1;
    }

    public boolean them(SPKhMai item) {
        if (item == null) {
            System.out.println("[Lỗi] Dữ liệu liên kết không hợp lệ!");
            return false;
        }

        if (timKiem(item.getMaSP(), item.getMaKM()) != -1) {
            System.out.println("[Lỗi Logic] Sản phẩm [" + item.getMaSP() + "] đã thuộc chương trình khuyến mãi [" + item.getMaKM() + "] rồi!");
            return false;
        }

        if (this.soLuong >= this.danhSach.length) {
            moRongBoNho();
        }

        this.danhSach[this.soLuong++] = item;
        System.out.println("[Thành công] Đã thêm liên kết sản phẩm [" + item.getMaSP() + "] với khuyến mãi [" + item.getMaKM() + "].");
        return true;
    }

    public boolean themSP(Scanner sc) {
        System.out.println("\n--- THÊM SẢN PHẨM VÀO CHƯƠNG TRÌNH KHUYẾN MÃI ---");
        SPKhMai item = new SPKhMai();
        item.nhap(sc);
        return them(item);
    }


    public boolean suaSP(Scanner sc) {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm cần sửa liên kết: ", ".+",
                "Mã sản phẩm không được để trống!");

        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần sửa liên kết: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKiem(maSP, maKM);
        if (viTri == -1) {
            System.out.println("[Lỗi] Không tìm thấy liên kết giữa SP [" + maSP + "] và KM [" + maKM + "]!");
            return false;
        }

        System.out.println("\n===== CHỌN THÔNG TIN CẦN SỬA =====");
        System.out.println("1. Sửa mã sản phẩm");
        System.out.println("2. Sửa mã khuyến mãi");
        System.out.println("3. Sửa cả mã SP và mã KM");
        System.out.println("0. Hủy bỏ");

        int chon = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập lựa chọn (0-3): ");

        String maSPMoi = this.danhSach[viTri].getMaSP();
        String maKMMoi = this.danhSach[viTri].getMaKM();

        switch (chon) {
            case 1:
                maSPMoi = Ktradulieu.nhapChuoi(sc, "Nhập mã sản phẩm mới: ", ".+", "Mã sản phẩm không được để trống!");
                break;
            case 2:
                maKMMoi = Ktradulieu.nhapChuoi(sc, "Nhập mã khuyến mãi mới: ", ".+", "Mã khuyến mãi không được để trống!");
                break;
            case 3:
                maSPMoi = Ktradulieu.nhapChuoi(sc, "Nhập mã sản phẩm mới: ", ".+", "Mã sản phẩm không được để trống!");
                maKMMoi = Ktradulieu.nhapChuoi(sc, "Nhập mã khuyến mãi mới: ", ".+", "Mã khuyến mãi không được để trống!");
                break;
            case 0:
                System.out.println("[Thông báo] Đã hủy thao tác sửa.");
                return false;
            default:
                System.out.println("[Lỗi] Lựa chọn không hợp lệ!");
                return false;
        }

        int viTriTrung = timKiem(maSPMoi, maKMMoi);
        if (viTriTrung != -1 && viTriTrung != viTri) {
            System.out.println("[Lỗi Logic] Liên kết SP [" + maSPMoi + "] - KM [" + maKMMoi + "] đã tồn tại trong danh sách!");
            return false;
        }

        try {
            this.danhSach[viTri].setMaSP(maSPMoi);
            this.danhSach[viTri].setMaKM(maKMMoi);
            System.out.println("[Thành công] Sửa sản phẩm khuyến mãi thành công!");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    public boolean xoa(String maSP, String maKM) {
        int viTri = timKiem(maSP, maKM);
        if (viTri == -1) {
            return false;
        }

        for (int i = viTri; i < this.soLuong - 1; i++) {
            this.danhSach[i] = this.danhSach[i + 1];
        }
        this.danhSach[this.soLuong - 1] = null;
        this.soLuong--;
        return true;
    }

    public boolean xoaSP(Scanner sc) {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm cần xóa liên kết: ", ".+",
                "Mã sản phẩm không được để trống!");

        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần xóa liên kết: ", ".+",
                "Mã khuyến mãi không được để trống!");

        if (xoa(maSP, maKM)) {
            System.out.println("[Thành công] Xóa sản phẩm khuyến mãi thành công!");
            return true;
        } else {
            System.out.println("[Lỗi] Không tìm thấy sản phẩm khuyến mãi cần xóa!");
            return false;
        }
    }

    public void xuatDanhSach() {
        if (this.soLuong == 0) {
            System.out.println("[Thông báo] Danh sách sản phẩm khuyến mãi đang trống!");
            return;
        }

        System.out.println("\n===== DANH SÁCH SẢN PHẨM KHUYẾN MÃI =====");
        System.out.printf("%-6s | %-15s | %-15s\n", "STT", "MÃ SẢN PHẨM", "MÃ KHUYẾN MÃI");
        System.out.println("------------------------------------------------");
        for (int i = 0; i < this.soLuong; i++) {
            System.out.printf("%-6d | %-15s | %-15s\n", (i + 1), this.danhSach[i].getMaSP(), this.danhSach[i].getMaKM());
        }
        System.out.println("Tổng số sản phẩm khuyến mãi: " + this.soLuong);
    }


    public void ghiFile(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            for (int i = 0; i < this.soLuong; i++) {
                if (this.danhSach[i] != null) {
                    bw.write(this.danhSach[i].toDongFile());
                    bw.newLine();
                }
            }
            System.out.println("[Thành công] Lưu file sản phẩm khuyến mãi tại: " + filePath);
        } catch (IOException e) {
            System.out.println("[Lỗi] Ghi file sản phẩm khuyến mãi: " + e.getMessage());
        }
    }

    public void docFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[Thông báo] Chưa có file dữ liệu sản phẩm khuyến mãi tại: " + filePath);
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String dong;
            this.soLuong = 0;
            int dem = 0;

            while ((dong = br.readLine()) != null) {
                dong = dong.trim();
                if (dong.isEmpty() || dong.startsWith("#")) continue;

                SPKhMai item = SPKhMai.tuDongFile(dong);
                if (item != null) {
                    if (this.soLuong >= this.danhSach.length) {
                        moRongBoNho();
                    }
                    this.danhSach[this.soLuong++] = item;
                    dem++;
                }
            }
            System.out.println("[Thành công] Đọc file sản phẩm khuyến mãi! Tổng số: " + dem);
        } catch (IOException e) {
            System.out.println("[Lỗi] Đọc file sản phẩm khuyến mãi: " + e.getMessage());
        }
    }
}