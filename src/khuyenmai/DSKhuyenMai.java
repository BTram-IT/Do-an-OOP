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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import util.Ktradulieu;

public class DSKhuyenMai {

    private Khuyenmai[] danhSach;
    private int soLuong;
    private static final int SUC_CHUA_MAC_DINH = 50;
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public DSKhuyenMai() {
        this(SUC_CHUA_MAC_DINH);
    }

    public DSKhuyenMai(int sucChuaBanDau) {
        if (sucChuaBanDau <= 0) {
            sucChuaBanDau = SUC_CHUA_MAC_DINH;
        }
        this.danhSach = new Khuyenmai[sucChuaBanDau];
        this.soLuong = 0;
    }

    public int getSoLuong() {
        return this.soLuong;
    }

    public Khuyenmai[] getDanhSach() {
        Khuyenmai[] ketQua = new Khuyenmai[this.soLuong];
        for (int i = 0; i < this.soLuong; i++) {
            ketQua[i] = this.danhSach[i];
        }
        return ketQua;
    }

    private void moRongBoNho() {
        int sucChuaMoi = this.danhSach.length * 2;
        Khuyenmai[] mangMoi = new Khuyenmai[sucChuaMoi];
        for (int i = 0; i < this.soLuong; i++) {
            mangMoi[i] = this.danhSach[i];
        }
        this.danhSach = mangMoi;
    }


    public String sinhMaTuDong(String tienTo) {
        if (tienTo == null || tienTo.trim().isEmpty()) {
            tienTo = "KM";
        }
        int maxSo = 0;
        for (int i = 0; i < this.soLuong; i++) {
            if (this.danhSach[i] != null && this.danhSach[i].getMaKM() != null) {
                String ma = this.danhSach[i].getMaKM();
                if (ma.startsWith(tienTo)) {
                    int so = Ktradulieu.laySoTuMa(ma);
                    if (so > maxSo) {
                        maxSo = so;
                    }
                }
            }
        }
        return Ktradulieu.taoMa(tienTo, maxSo + 1);
    }


    public int timViTri(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return -1;
        }
        String maChuan = Ktradulieu.chuanHoaMa(maKM);
        for (int i = 0; i < this.soLuong; i++) {
            if (this.danhSach[i] != null && this.danhSach[i].getMaKM() != null
                    && this.danhSach[i].getMaKM().equalsIgnoreCase(maChuan)) {
                return i;
            }
        }
        return -1;
    }

    public Khuyenmai timKiem(String maKM) {
        int viTri = timViTri(maKM);
        return (viTri == -1) ? null : this.danhSach[viTri];
    }

    public Khuyenmai timTheoMa(String maKM) {
        return timKiem(maKM);
    }

    public boolean trungMa(String maKM, int viTriBoQua) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return false;
        }
        String maChuan = Ktradulieu.chuanHoaMa(maKM);
        for (int i = 0; i < this.soLuong; i++) {
            if (i != viTriBoQua && this.danhSach[i] != null
                    && this.danhSach[i].getMaKM() != null
                    && this.danhSach[i].getMaKM().equalsIgnoreCase(maChuan)) {
                return true;
            }
        }
        return false;
    }


    public void timKiemKhuyenMai(Scanner sc) {
        String maKM = Ktradulieu.nhapChuoi(sc, "Nhập mã khuyến mãi cần tìm: ", ".+",  "Mã khuyến mãi không được để trống!");

        Khuyenmai km = timKiem(maKM);
        if (km == null) {
            System.out.println("Không tìm thấy chương trình khuyến mãi: " + maKM);
            return;
        }

        System.out.println("\n--- THÔNG TIN CHƯƠNG TRÌNH KHUYẾN MÃI ---");
        System.out.println(km);
    }


    public boolean themKhuyenMai(Khuyenmai km) {
        if (km == null || km.getMaKM() == null || km.getMaKM().trim().isEmpty()) {
            System.out.println("Lỗi: Khuyến mãi không hợp lệ!");
            return false;
        }

        if (timKiem(km.getMaKM()) != null) {
            System.out.println(" Lỗi: Mã khuyến mãi '" + km.getMaKM() + "' đã tồn tại!");
            return false;
        }

        if (this.soLuong >= this.danhSach.length) {
            moRongBoNho();
        }

        this.danhSach[this.soLuong++] = km;
        System.out.println("Đã thêm chương trình khuyến mãi [" + km.getMaKM() + "] thành công!");
        return true;
    }

    public boolean themTuBanPhim(Scanner sc) {
        System.out.println("\n========== CHỌN LOẠI CHƯƠNG TRÌNH KHUYẾN MÃI ==========");
        System.out.println("1. Khuyến mãi mua tặng (Mua X tặng Y)");
        System.out.println("2. Khuyến mãi theo phần trăm (Giảm % có mức trần)");
        System.out.println("3. Khuyến mãi giảm tiền mặt trực tiếp");
        System.out.println("0. Hủy bỏ");

        int chon = (int) Ktradulieu.nhapSoNguyen(sc, "Chọn loại khuyến mãi (0-3): ");
        Khuyenmai kmMoi;

        switch (chon) {
            case 1:
                kmMoi = new KhuyenmaiMuatang();
                break;
            case 2:
                kmMoi = new KhuyenmaiPhantram();
                break;
            case 3:
                kmMoi = new KhuyenmaiTructiep();
                break;
            case 0:
                System.out.println("Đã hủy thao tác thêm.");
                return false;
            default:
                System.out.println("Lựa chọn không hợp lệ!");
                return false;
        }

        kmMoi.nhapThongTin(sc, this);
        return themKhuyenMai(kmMoi);
    }

    public boolean suaKhuyenMai(Scanner sc) {
        String ma = Ktradulieu.nhapChuoi(sc, "Nhập mã khuyến mãi cần sửa: ", ".+", "Mã khuyến mãi không được để trống!");
        Khuyenmai km = timKiem(ma);
        if (km == null) {
            System.out.println(" Không tìm thấy chương trình khuyến mãi: " + ma);
            return false;
        }

        System.out.println("\n========== SỬA THÔNG TIN KHUYẾN MÃI [" + km.getMaKM() + "] ==========");
        System.out.println("Thông tin hiện tại: " + km);
        System.out.println("1. Sửa ngày bắt đầu");
        System.out.println("2. Sửa ngày kết thúc");
        System.out.println("3. Sửa mức chi tiêu tối thiểu");
        System.out.println("4. Sửa giá trị khuyến mãi chi tiết");
        System.out.println("0. Hủy bỏ (Thoát)");

        int chon = (int) Ktradulieu.nhapSoNguyen(sc, " Chọn mục cần sửa (0-4): ");

        try {
            switch (chon) {
                case 1:
                    LocalDate ngayBD = Ktradulieu.nhapNgay(sc, "Nhập ngày bắt đầu mới (dd/MM/yyyy): ");
                    km.setNgayBatDau(ngayBD);
                    System.out.println("Đã cập nhật ngày bắt đầu mới.");
                    break;
                case 2:
                    LocalDate ngayKT = Ktradulieu.nhapNgay(sc, "Nhập ngày kết thúc mới (dd/MM/yyyy): ");
                    km.setNgayKetThuc(ngayKT);
                    System.out.println("Đã cập nhật ngày kết thúc mới.");
                    break;
                case 3:
                    long mucChi = (long) Ktradulieu.nhapSoThucKhongAm(sc, "Nhập mức chi tiêu tối thiểu mới (VND): ");
                    km.setMucChiToiThieu(mucChi);
                    System.out.println("Đã cập nhật mức chi tối thiểu mới.");
                    break;
                case 4:
                    if (km instanceof KhuyenmaiMuatang) {
                        KhuyenmaiMuatang kmmt = (KhuyenmaiMuatang) km;
                        int mua = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng mua tối thiểu mới: ");
                        int tang = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng tặng mới: ");
                        kmmt.setSoLuongMuaToiThieu(mua);
                        kmmt.setSoLuongTang(tang);
                        kmmt.setLoaiKM("Mua " + mua + " tặng " + tang);
                    } else if (km instanceof KhuyenmaiPhantram) {
                        KhuyenmaiPhantram kmpt = (KhuyenmaiPhantram) km;
                        double pt = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập % giảm mới (0 < % <= 100): ");
                        long tran = (long) Ktradulieu.nhapSoThucKhongAm(sc, "Nhập mức giảm tối đa mới (0 = không giới hạn): ");
                        kmpt.setPhanTramGiamGia(pt);
                        kmpt.setMucGiamToiDa(tran);
                        kmpt.setLoaiKM("Giảm " + pt + "%");
                    } else if (km instanceof KhuyenmaiTructiep) {
                        KhuyenmaiTructiep kmtt = (KhuyenmaiTructiep) km;
                        long giam = (long) Ktradulieu.nhapSoThucKhongAm(sc, "Nhập số tiền giảm trực tiếp mới (VND): ");
                        kmtt.setSoTienGiam(giam);
                        kmtt.setLoaiKM("Giảm " + Ktradulieu.dinhDangTien(giam));
                    }
                    System.out.println("Đã cập nhật thông tin chi tiết khuyến mãi.");
                    break;
                case 0:
                    System.out.println("Đã hủy thao tác sửa.");
                    return false;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
                    return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi dữ liệu cập nhật: " + e.getMessage());
            return false;
        }
    }


    public boolean xoa(String maKM) {
        int viTri = timViTri(maKM);
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

    public boolean xoaKhuyenMai(String maKM) {
        return xoa(maKM);
    }

    public boolean xoaKhuyenMai(Scanner sc) {
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần xóa: ", ".+",
                "Mã khuyến mãi không được để trống!");

        if (xoa(maKM)) {
            System.out.println("Đã xóa thành công chương trình khuyến mãi: " + maKM);
            return true;
        } else {
            System.out.println("Không tìm thấy khuyến mãi có mã: " + maKM);
            return false;
        }
    }

    public void xuatDanhSach() {
        if (this.soLuong == 0) {
            System.out.println("Danh sách khuyến mãi hiện đang trống!");
            return;
        }

        System.out.println("\n========================== DANH SÁCH CHƯƠNG TRÌNH KHUYẾN MÃI ==========================");
        for (int i = 0; i < this.soLuong; i++) {
            System.out.printf("%-3d. %s\n", (i + 1), this.danhSach[i]);
            System.out.println("--------------------------------------------------------------------------------------");
        }
        System.out.println("Tổng số chương trình khuyến mãi: " + this.soLuong);
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
            System.out.println("Đã lưu thành công " + this.soLuong + " chương trình khuyến mãi vào file: " + filePath);
        } catch (IOException e) {
            System.out.println("Lỗi ghi file khuyến mãi: " + e.getMessage());
        }
    }

    public void docFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Chưa có file dữ liệu khuyến mãi tại: " + filePath + " (sẽ khởi tạo khi lưu).");
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

                // Format: LOAI_KM;maKM;loaiKM;ngayBatDau;ngayKetThuc;mucChiToiThieu;...
                String[] p = dong.split(";");
                if (p.length >= 6) {
                    String loai = p[0].trim();
                    String ma = p[1].trim();
                    String tenLoai = p[2].trim();
                    LocalDate ngayBD = LocalDate.parse(p[3].trim(), DINH_DANG_NGAY);
                    LocalDate ngayKT = LocalDate.parse(p[4].trim(), DINH_DANG_NGAY);
                    long mucChi = Long.parseLong(p[5].trim());

                    Khuyenmai km = null;
                    if ("MUATANG".equalsIgnoreCase(loai) && p.length >= 8) {
                        int mua = Integer.parseInt(p[6].trim());
                        int tang = Integer.parseInt(p[7].trim());
                        km = new KhuyenmaiMuatang(ma, tenLoai, ngayBD, ngayKT, mucChi, mua, tang);
                    } else if ("PHANTRAM".equalsIgnoreCase(loai) && p.length >= 8) {
                        double pt = Double.parseDouble(p[6].trim());
                        long trantoDa = Long.parseLong(p[7].trim());
                        km = new KhuyenmaiPhantram(ma, tenLoai, ngayBD, ngayKT, mucChi, pt, trantoDa);
                    } else if ("TRUCTIEP".equalsIgnoreCase(loai) && p.length >= 7) {
                        long giam = Long.parseLong(p[6].trim());
                        km = new KhuyenmaiTructiep(ma, tenLoai, ngayBD, ngayKT, mucChi, giam);
                    }

                    if (km != null) {
                        if (this.soLuong >= this.danhSach.length) {
                            moRongBoNho();
                        }
                        this.danhSach[this.soLuong++] = km;
                        dem++;
                    }
                }
            }
            System.out.println("Đã nạp thành công " + dem + " chương trình khuyến mãi từ file: " + filePath);
        } catch (Exception e) {
            System.out.println("Lỗi khi đọc file khuyến mãi: " + e.getMessage());
        }
    }
}