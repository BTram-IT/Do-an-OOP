package hoadon;

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
import doitac.DSNhaCungCap;
import person.DsKhachHang;
import person.DSNhanVien;
import person.Giamdoc;
import person.Nguoidung;
import person.NhanVien;
import person.QuanLyCuaHang;
import sanpham.DSSanPham;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import util.Ktradulieu;

public class DSHoadon {
    private ArrayList<HoaDon> danhSach;

    public DSHoadon() {
        this.danhSach = new ArrayList<>();
    }

    public int getSoLuong() {
        return this.danhSach.size();
    }

    public ArrayList<HoaDon> getDanhSach() {
        return this.danhSach;
    }

    public HoaDon get(int index) {
        if (index < 0 || index >= this.danhSach.size()) {
            return null;
        }
        return this.danhSach.get(index);
    }

    public String sinhMaTuDong(String tienTo) {
        if (tienTo == null || tienTo.trim().isEmpty()) {
            tienTo = "HD";
        }
        int maxSo = 0;
        for (HoaDon hd : this.danhSach) {
            if (hd != null && hd.getMaHD() != null && hd.getMaHD().startsWith(tienTo)) {
                int so = Ktradulieu.laySoTuMa(hd.getMaHD(), tienTo);
                if (so > maxSo) {
                    maxSo = so;
                }
            }
        }
        return Ktradulieu.taoMa(tienTo, maxSo + 1);
    }

    public int timViTri(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) {
            return -1;
        }
        String maChuan = Ktradulieu.chuanHoaMa(maHD);
        for (int i = 0; i < this.danhSach.size(); i++) {
            if (this.danhSach.get(i) != null && this.danhSach.get(i).getMaHD() != null
                    && this.danhSach.get(i).getMaHD().equalsIgnoreCase(maChuan)) {
                return i;
            }
        }
        return -1;
    }

    public HoaDon timKiem(String maHD) {
        int viTri = timViTri(maHD);
        return (viTri == -1) ? null : this.danhSach.get(viTri);
    }

    public boolean themHD(HoaDon hd) {
        if (hd == null || hd.getMaHD() == null || hd.getMaHD().trim().isEmpty()) {
            System.out.println("[Lỗi] Hóa đơn không hợp lệ!");
            return false;
        }

        if (timKiem(hd.getMaHD()) != null) {
            System.out.println("[Lỗi] Mã hóa đơn '" + hd.getMaHD() + "' đã tồn tại trong hệ thống!");
            return false;
        }

        this.danhSach.add(hd);
        System.out.println("[Thành công] Đã lưu hóa đơn [" + hd.getMaHD() + "] (" + hd.getLoaiHD() + ") vào hệ thống.");
        return true;
    }

    public boolean lapHoaDonMoi(Scanner sc, NhanVien nvDangNhap, DsKhachHang dsKH,
                                DSNhaCungCap dsNCC, DSSanPham dsSP, DSKhuyenMai dsKM, DSSanPhamKhuyenMai dsSPKM) {
        System.out.println("\n========== CHỌN LOẠI HÓA ĐƠN CẦN LẬP ==========");
        System.out.println("1. Lập Hóa đơn bán lẻ (Bán hàng tại quầy)");
        System.out.println("2. Lập Hóa đơn nhập hàng (Nhập kho từ Nhà cung cấp)");
        System.out.println("0. Hủy bỏ");

        int chon = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập lựa chọn (0-2): ");

        if (chon == 1) {
            Hoadonban hdb = new Hoadonban();
            hdb.nhapThongTin(sc, nvDangNhap, this, dsKH, dsSP, dsKM, dsSPKM);
            return themHD(hdb);
        } else if (chon == 2) {
            HoadonNhap hdn = new HoadonNhap();
            hdn.nhapThongTin(sc, nvDangNhap, this, dsNCC, dsSP);
            return themHD(hdn);
        } else {
            System.out.println("[Thông báo] Đã hủy thao tác lập hóa đơn.");
            return false;
        }
    }

    public boolean duyetHoaDon(Scanner sc, Nguoidung nguoiDuyet, DSSanPham dsSP) {
        if (!nguoiDuyet.getVaiTro().equalsIgnoreCase("QuanLy") && !nguoiDuyet.getVaiTro().equalsIgnoreCase("GiamDoc")) {
            System.out.println("[Lỗi] BẠN KHÔNG CÓ QUYỀN! Chỉ Quản lý hoặc Giám đốc mới được thực hiện thao tác này.");
            return false;
        }

        String maHD = Ktradulieu.nhapChuoi(sc, "Nhập mã hóa đơn cần duyệt: ", ".+", "Mã không được để trống!");
        HoaDon hd = timKiem(maHD);

        if (hd == null) {
            System.out.println("[Lỗi] Không tìm thấy hóa đơn có mã: " + maHD);
            return false;
        }

        if (hd.isDaXuLyThanhCong()) {
            System.out.println("[Thông báo] Hóa đơn [" + maHD + "] vốn đã ở trạng thái 'DaXuLyThanhCong' trước đó.");
            return false;
        }

        if (hd instanceof HoadonNhap) {
            return ((HoadonNhap) hd).duyetNhapKho(dsSP, nguoiDuyet);
        } else {
            hd.xacNhanXuLy();
            System.out.println("[Thành công] Đã duyệt hóa đơn bán [" + maHD + "] thành 'DaXuLyThanhCong'. Dữ liệu đã được khóa.");
            return true;
        }
    }


    public boolean suaHD(Scanner sc, Nguoidung nguoiThucHien) {
        if (!(nguoiThucHien instanceof QuanLyCuaHang) && !(nguoiThucHien instanceof Giamdoc)) {
            System.out.println("[Lỗi] BẠN KHÔNG CÓ QUYỀN! Chỉ Quản lý hoặc Giám đốc mới được sửa hóa đơn.");
            return false;
        }

        String maHD = Ktradulieu.nhapChuoi(sc, "Nhập mã hóa đơn cần sửa: ", ".+", "Mã không được để trống!");
        HoaDon hd = timKiem(maHD);

        if (hd == null) {
            System.out.println("[Lỗi] Không tìm thấy hóa đơn: " + maHD);
            return false;
        }

        if (hd.isDaXuLyThanhCong()) {
            System.out.println("[Khóa Hóa Đơn] Hóa đơn [" + maHD + "] đã XỬ LÝ THÀNH CÔNG, không được phép sửa!");
            return false;
        }

        System.out.println("\n========== SỬA THÔNG TIN HÓA ĐƠN [" + hd.getMaHD() + "] ==========");
        System.out.println("Loại: " + hd.getLoaiHD() + " | Ngày lập: " + hd.getNgayLap() + " | Trạng thái: " + hd.getTrangThai());
        System.out.println("1. Sửa ghi chú hóa đơn");
        System.out.println("2. Sửa ngày lập hóa đơn");
        System.out.println("0. Hủy bỏ");

        int chon = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập lựa chọn (0-2): ");

        try {
            switch (chon) {
                case 1:
                    String gcMoi = Ktradulieu.nhapChuoiTuyChon(sc, "Nhập ghi chú mới: ", ".+", "");
                    hd.setGhiChu(gcMoi);
                    System.out.println("[Thành công] Đã cập nhật ghi chú mới.");
                    break;
                case 2:
                    String ngayMoi = Ktradulieu.nhapChuoi(sc, "Nhập ngày lập mới (dd/MM/yyyy): ", Ktradulieu.ngayRegex, "Định dạng ngày phải là dd/MM/yyyy!");
                    hd.setNgayLap(ngayMoi);
                    System.out.println("[Thành công] Đã cập nhật ngày lập hóa đơn.");
                    break;
                case 0:
                    System.out.println("[Thông báo] Đã hủy thao tác sửa.");
                    return false;
                default:
                    System.out.println("[Lỗi] Lựa chọn không hợp lệ!");
                    return false;
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean xoaHD(Scanner sc, Nguoidung nguoiThucHien) {
        if (!(nguoiThucHien instanceof QuanLyCuaHang) && !(nguoiThucHien instanceof Giamdoc)) {
            System.out.println("[Lỗi] BẠN KHÔNG CÓ QUYỀN! Chỉ Quản lý hoặc Giám đốc mới được xóa hóa đơn.");
            return false;
        }

        String maHD = Ktradulieu.nhapChuoi(sc, "Nhập mã hóa đơn cần xóa: ", ".+", "Mã không được để trống!");
        int viTri = timViTri(maHD);

        if (viTri == -1) {
            System.out.println("[Lỗi] Không tìm thấy hóa đơn có mã: " + maHD);
            return false;
        }

        HoaDon hd = this.danhSach.get(viTri);
        if (hd.isDaXuLyThanhCong()) {
            System.out.println("[Khóa Hóa Đơn] KHÔNG THỂ XÓA! Hóa đơn [" + maHD + "] đã XỬ LÝ THÀNH CÔNG.");
            return false;
        }

        this.danhSach.remove(viTri);
        System.out.println("[Thành công] Đã xóa hóa đơn: " + maHD);
        return true;
    }


    public void xuatDanhSach() {
        if (this.danhSach.isEmpty()) {
            System.out.println("[Thông báo] Danh sách hóa đơn hiện đang trống!");
            return;
        }

        System.out.println("\n=================================== DANH SÁCH TOÀN BỘ HÓA ĐƠN ===================================");
        System.out.printf("%-10s | %-14s | %-12s | %-10s | %-15s | %-16s | %-15s\n",
                "MÃ HĐ", "LOẠI", "NGÀY LẬP", "NV LẬP", "ĐỐI TÁC", "TỔNG THANH TOÁN", "TRẠNG THÁI");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (HoaDon hd : this.danhSach) {
            String doiTac = (hd instanceof Hoadonban) ? ((Hoadonban) hd).getMaKH() : ((HoadonNhap) hd).getMaNCC();
            System.out.printf("%-10s | %-14s | %-12s | %-10s | %-15s | %-16s | %-15s\n",
                    hd.getMaHD(), hd.getLoaiHD(), hd.getNgayLap(), hd.getMaNV(),
                    doiTac, Ktradulieu.dinhDangTien(hd.tinhThanhTien()), hd.getTrangThai());
        }
        System.out.println("Tổng số hóa đơn lưu trữ: " + this.danhSach.size());
    }

    public void xemChiTietHoaDon(Scanner sc) {
        String maHD = Ktradulieu.nhapChuoi(sc, "Nhập mã hóa đơn cần xem chi tiết: ", ".+", "Mã không được để trống!");
        HoaDon hd = timKiem(maHD);
        if (hd == null) {
            System.out.println("[Lỗi] Không tìm thấy hóa đơn: " + maHD);
            return;
        }
        System.out.println(hd.inHoaDon());
    }

    public void ghiFile(String filePath) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            for (HoaDon hd : this.danhSach) {
                if (hd != null) {
                    bw.write(hd.toFileString());
                    bw.newLine();
                }
            }
            System.out.println("[Thành công] Đã lưu " + this.danhSach.size() + " hóa đơn vào file: " + filePath);
        } catch (IOException e) {
            System.out.println("[Lỗi] Ghi file hóa đơn: " + e.getMessage());
        }
    }

    public void docFile(String filePath, DSNhanVien dsNV) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[Thông báo] Chưa có file dữ liệu hóa đơn tại: " + filePath);
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String dong;
            this.danhSach.clear();
            int dem = 0;

            while ((dong = br.readLine()) != null) {
                dong = dong.trim();
                if (dong.isEmpty() || dong.startsWith("#")) continue;

                // Format: LOAI_HD;maHD;ngayLap;maNV;maDoiTac;trangThai;tongTien;...
                String[] p = dong.split(";");
                if (p.length >= 6) {
                    String loai = p[0].trim();
                    String maHD = p[1].trim();
                    String ngayLap = p[2].trim();
                    String maNV = p[3].trim();
                    String maDoiTac = p[4].trim();
                    String trangThai = p[5].trim();
                    String ghiChu = (p.length >= 11) ? p[10].trim() : "";

                    NhanVien nv = (dsNV != null) ? dsNV.timKiem(maNV) : null;
                    if (nv == null) {
                        nv = new NhanVien();
                        nv.setMaND(maNV);
                        nv.setTenND("Nhân viên " + maNV);
                    }

                    HoaDon hd;
                    if ("HDBAN".equalsIgnoreCase(loai)) {
                        hd = new Hoadonban(maHD, nv, ngayLap, trangThai, ghiChu, maDoiTac);
                    } else {
                        hd = new HoadonNhap(maHD, nv, ngayLap, trangThai, ghiChu, maDoiTac);
                    }

                    this.danhSach.add(hd);
                    dem++;
                }
            }
            System.out.println("[Thành công] Đã nạp " + dem + " hóa đơn từ file: " + filePath);
        } catch (Exception e) {
            System.out.println("[Lỗi] Đọc file hóa đơn: " + e.getMessage());
        }
    }
}