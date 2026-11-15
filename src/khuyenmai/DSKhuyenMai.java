package khuyenmai;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import util.Ktradulieu;

public class DSKhuyenMai {

    private Khuyenmai[] danhSach;
    private int soLuong;

    // Số thứ tự mã KM lớn nhất từng xuất hiện (kể cả mã đã xóa) -> không bao giờ cấp lại mã cũ trong cùng phiên chạy
    private int boDemMa;

    private static final int SUC_CHUA_MAC_DINH = 50;
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    // =========================================================
    // 1. CONSTRUCTOR
    // =========================================================
    public DSKhuyenMai() {
        this(SUC_CHUA_MAC_DINH);
    }

    public DSKhuyenMai(int sucChuaBanDau) {
        if (sucChuaBanDau <= 0) {
            sucChuaBanDau = SUC_CHUA_MAC_DINH;
        }
        this.danhSach = new Khuyenmai[sucChuaBanDau];
        this.soLuong = 0;
        this.boDemMa = 0;
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

    // =========================================================
    // 2. QUẢN LÝ KÍCH THƯỚC MẢNG
    // =========================================================
    private void moRongBoNho() {
        if (this.danhSach.length == Integer.MAX_VALUE) {
            throw new IllegalStateException("Danh sách khuyến mãi đã đạt giới hạn!");
        }

        int sucChuaMoi;
        if (this.danhSach.length > Integer.MAX_VALUE / 2) {
            sucChuaMoi = Integer.MAX_VALUE;
        } else {
            sucChuaMoi = this.danhSach.length * 2;
        }

        Khuyenmai[] mangMoi = new Khuyenmai[sucChuaMoi];
        for (int i = 0; i < this.soLuong; i++) {
            mangMoi[i] = this.danhSach[i];
        }
        this.danhSach = mangMoi;
    }

    // =========================================================
    // 3. SINH MÃ KHUYẾN MÃI
    // =========================================================

    // Ghi nhận số thứ tự của một mã KM để lần sinh mã sau không dùng lại
    private void ghiNhanSoMa(String maKM) {
        if (maKM == null) {
            return;
        }
        int so = Ktradulieu.laySoTuMa(maKM, "KM");
        if (so > this.boDemMa) {
            this.boDemMa = so;
        }
    }

    public String sinhMaTuDong(String tienTo) {
        if (tienTo == null || tienTo.trim().isEmpty()) {
            tienTo = "KM";
        }
        tienTo = Ktradulieu.chuanHoaMa(tienTo);
        int maxSo = 0;

        for (int i = 0; i < this.soLuong; i++) {
            if (this.danhSach[i] != null) {
                String ma = this.danhSach[i].getMaKM();
                int so = Ktradulieu.laySoTuMa(ma, tienTo);
                if (so > maxSo) {
                    maxSo = so;
                }
            }
        }

        // Mã đã từng cấp nhưng đã bị xóa vẫn được tính, tránh cấp lại mã cũ
        if (tienTo.equals("KM") && this.boDemMa > maxSo) {
            maxSo = this.boDemMa;
        }

        if (maxSo == Integer.MAX_VALUE) {
            throw new IllegalStateException("Không thể sinh thêm mã khuyến mãi!");
        }

        return Ktradulieu.taoMa(tienTo + "%03d", maxSo + 1);
    }

    // =========================================================
    // 4. TÌM KIẾM
    // =========================================================
    public int timViTri(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return -1;
        }
        String maChuan = Ktradulieu.chuanHoaMa(maKM);
        for (int i = 0; i < this.soLuong; i++) {
            if (this.danhSach[i] != null
                    && this.danhSach[i].getMaKM() != null
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
            if (i != viTriBoQua
                    && this.danhSach[i] != null
                    && this.danhSach[i].getMaKM() != null
                    && this.danhSach[i].getMaKM().equalsIgnoreCase(maChuan)) {
                return true;
            }
        }
        return false;
    }

    public void timKiemKhuyenMai(Scanner sc) {
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần tìm: ", ".+", "Mã khuyến mãi không được để trống!");
        Khuyenmai km = timKiem(maKM);

        if (km == null) {
            System.out.println("Không tìm thấy chương trình khuyến mãi: " + maKM);
            return;
        }

        System.out.println("\n--- THÔNG TIN CHƯƠNG TRÌNH KHUYẾN MÃI ---");
        System.out.println(km);
    }

    // =========================================================
    // 5. KIỂM TRA ĐỐI TƯỢNG TRƯỚC KHI THÊM
    // =========================================================
    private boolean kiemTraKhuyenMai(Khuyenmai km) {
        if (km == null) return false;
        if (km.getMaKM() == null || km.getMaKM().trim().isEmpty()) return false;
        if (km.getLoaiKM() == null || km.getLoaiKM().trim().isEmpty()) return false;

        try {
            km.toDongFile();
            return true;
        } catch (RuntimeException e) {
            System.out.println("Dữ liệu khuyến mãi không hợp lệ: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // 6. THÊM KHUYẾN MÃI
    // =========================================================
    public boolean themKhuyenMai(Khuyenmai km) {
        if (!kiemTraKhuyenMai(km)) {
            System.out.println("Lỗi: Khuyến mãi không hợp lệ!");
            return false;
        }
        if (timKiem(km.getMaKM()) != null) {
            System.out.println("Lỗi: Mã khuyến mãi '" + km.getMaKM() + "' đã tồn tại!");
            return false;
        }
        if (this.soLuong >= this.danhSach.length) {
            moRongBoNho();
        }

        this.danhSach[this.soLuong] = km;
        this.soLuong++;
        ghiNhanSoMa(km.getMaKM());
        System.out.println("Đã thêm chương trình khuyến mãi [" + km.getMaKM() + "] thành công!");
        return true;
    }

    // =========================================================
    // 7. THÊM KHUYẾN MÃI TỪ BÀN PHÍM
    // =========================================================
    public boolean themTuBanPhim(Scanner sc) {
        System.out.println("\n========== CHỌN LOẠI CHƯƠNG TRÌNH KHUYẾN MÃI ==========");
        System.out.println("1. Khuyến mãi mua tặng (Mua X tặng Y)");
        System.out.println("2. Khuyến mãi theo phần trăm");
        System.out.println("3. Khuyến mãi giảm tiền mặt trực tiếp");
        System.out.println("0. Hủy bỏ");

        int chon = Ktradulieu.luaChon(sc, "Chọn loại khuyến mãi (0-3): ", 0, 3);
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
                return false;
        }

        try {
            kmMoi.nhapThongTin(sc, this);

            if (themKhuyenMai(kmMoi)) {
                ghiFile("data/khuyenmai.txt");
                return true;
            }

            return false;

        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi nhập khuyến mãi: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // 8. NHẬP SỐ TIỀN NGUYÊN KHÔNG ÂM
    // =========================================================
    private long nhapSoTienKhongAm(Scanner sc, String thongBao) {
        while (true) {
            System.out.print(thongBao);
            String input = sc.nextLine().trim();
            try {
                long giaTri = new BigDecimal(input).longValueExact();
                if (giaTri >= 0) return giaTri;
                System.out.println("Số tiền không được âm!");
            } catch (NumberFormatException | ArithmeticException e) {
                System.out.println("Vui lòng nhập số nguyên không âm hợp lệ!");
            }
        }
    }

    // =========================================================
    // 9. SỬA KHUYẾN MÃI
    // =========================================================
    public boolean suaKhuyenMai(Scanner sc) {
        String ma = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần sửa: ", ".+",
                "Mã khuyến mãi không được để trống!");

        Khuyenmai km = timKiem(ma);

        if (km == null) {
            System.out.println("Không tìm thấy chương trình khuyến mãi: " + ma);
            return false;
        }

        System.out.println("\n========== SỬA THÔNG TIN KHUYẾN MÃI [" + km.getMaKM() + "] ==========");
        System.out.println("Thông tin hiện tại: " + km);
        System.out.println("1. Sửa ngày bắt đầu");
        System.out.println("2. Sửa ngày kết thúc");
        System.out.println("3. Sửa giá trị khuyến mãi chi tiết");
        System.out.println("0. Hủy bỏ");

        int chon = Ktradulieu.luaChon(sc, "Chọn mục cần sửa (0-3): ", 0, 3);

        try {
            switch (chon) {
                case 1: {
                    LocalDate ngayBD = Ktradulieu.nhapNgay(
                            sc, "Nhập ngày bắt đầu mới (dd/MM/yyyy): ");

                    km.setNgayBatDau(ngayBD);
                    System.out.println("Đã cập nhật ngày bắt đầu.");
                    break;
                }

                case 2: {
                    LocalDate ngayKT = Ktradulieu.nhapNgay(
                            sc, "Nhập ngày kết thúc mới (dd/MM/yyyy): ");

                    if (ngayKT.isBefore(LocalDate.now())) {
                        throw new IllegalArgumentException(
                                "[Lỗi] Ngày kết thúc mới không được ở trong quá khứ!");
                    }

                    km.setNgayKetThuc(ngayKT);
                    System.out.println("Đã cập nhật ngày kết thúc.");
                    break;
                }

                case 3: {
                    if (km instanceof KhuyenmaiMuatang) {
                        KhuyenmaiMuatang kmmt = (KhuyenmaiMuatang) km;

                        int mua = (int) Ktradulieu.nhapSoNguyen(
                                sc, "Nhập số lượng mua tối thiểu mới: ");

                        int tang = (int) Ktradulieu.nhapSoNguyen(
                                sc, "Nhập số lượng tặng mới: ");

                        kmmt.setDieuKienMuaTang(mua, tang);

                    } else if (km instanceof KhuyenmaiPhantram) {
                        KhuyenmaiPhantram kmpt = (KhuyenmaiPhantram) km;

                        double pt = Ktradulieu.nhapSoThucKhongAm(
                                sc, "Nhập phần trăm giảm mới (0 < % <= 100): ");

                        kmpt.setPhanTramGiamGia(pt);

                    } else if (km instanceof KhuyenmaiTructiep) {
                        KhuyenmaiTructiep kmtt = (KhuyenmaiTructiep) km;

                        long giam = nhapSoTienKhongAm(
                                sc, "Nhập số tiền giảm trực tiếp mới (VND): ");

                        kmtt.setSoTienGiam(giam);
                    }

                    System.out.println("Đã cập nhật thông tin chi tiết khuyến mãi.");
                    break;
                }

                case 0:
                    System.out.println("Đã hủy thao tác sửa.");
                    return false;

                default:
                    return false;
            }

            ghiFile("data/khuyenmai.txt");
            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi dữ liệu cập nhật: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // 10. XÓA KHUYẾN MÃI
    //   Luôn phải truyền DSSanPhamKhuyenMai để xóa cả các liên kết sản phẩm - khuyến mãi,
    //   nếu không mã KM bị xóa có thể bị cấp lại và thừa hưởng liên kết cũ.
    // =========================================================
    private void xoaTaiViTri(int viTri) {
        for (int i = viTri; i < this.soLuong - 1; i++) {
            this.danhSach[i] = this.danhSach[i + 1];
        }

        this.danhSach[this.soLuong - 1] = null;
        this.soLuong--;
    }

    public boolean xoa(String maKM, DSSanPhamKhuyenMai dsSPKM) {
        if (dsSPKM == null) {
            System.out.println("Lỗi: Cần truyền danh sách liên kết sản phẩm - khuyến mãi để xóa khuyến mãi an toàn!");
            return false;
        }

        int viTri = timViTri(maKM);
        if (viTri == -1) {
            return false;
        }

        int soLienKet = dsSPKM.xoaTheoMaKM(maKM);
        System.out.println("Đã xóa " + soLienKet + " liên kết sản phẩm - khuyến mãi.");

        xoaTaiViTri(viTri);

        ghiFile("data/khuyenmai.txt");
        dsSPKM.ghiFile("data/sanphamkhuyenmai.txt");

        return true;
    }

    public boolean xoaKhuyenMai(String maKM, DSSanPhamKhuyenMai dsSPKM) {
        return xoa(maKM, dsSPKM);
    }

    public boolean xoaKhuyenMai(Scanner sc, DSSanPhamKhuyenMai dsSPKM) {
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần xóa: ", ".+", "Mã khuyến mãi không được để trống!");
        if (xoa(maKM, dsSPKM)) {
            System.out.println("Đã xóa chương trình khuyến mãi: " + maKM);
            return true;
        }
        System.out.println("Không tìm thấy khuyến mãi có mã: " + maKM);
        return false;
    }

    // =========================================================
    // 11. XUẤT DANH SÁCH
    // =========================================================
    public void xuatDanhSach() {
        if (this.soLuong == 0) {
            System.out.println("Danh sách khuyến mãi hiện đang trống!");
            return;
        }

        System.out.println("\n========================== DANH SÁCH CHƯƠNG TRÌNH KHUYẾN MÃI ==========================");
        for (int i = 0; i < this.soLuong; i++) {
            System.out.printf("%-3d. %s%n", i + 1, this.danhSach[i]);
            System.out.println("--------------------------------------------------------------------------------------");
        }
        System.out.println("Tổng số chương trình khuyến mãi: " + this.soLuong);
    }

    // =========================================================
    // 12. GHI FILE
    // =========================================================
    public void ghiFile(String filePath) {
        String dich = Ktradulieu.duongDanGhi(filePath);
        File tam = Ktradulieu.chuanBiFileTam(dich);
        try {
            try (BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(tam), StandardCharsets.UTF_8))) {
                for (int i = 0; i < this.soLuong; i++) {
                    if (this.danhSach[i] != null) {
                        bw.write(this.danhSach[i].toDongFile());
                        bw.newLine();
                    }
                }
            }
            Ktradulieu.hoanTatGhi(tam, dich);
            System.out.println("Đã lưu thành công " + this.soLuong + " chương trình khuyến mãi vào file: " + dich);
        } catch (IOException | RuntimeException e) {
            Ktradulieu.huyFileTam(tam);
            System.out.println("[Lỗi] Ghi file khuyến mãi: " + e.getMessage());
        }
    }

    // =========================================================
    // 13. KIỂM TRA MÃ TRÙNG TRONG MẢNG TẠM
    // =========================================================
    private boolean trungMaTrongMang(Khuyenmai[] mang, int soLuongMang, String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return true;
        }
        for (int i = 0; i < soLuongMang; i++) {
            if (mang[i] != null && mang[i].getMaKM().equalsIgnoreCase(maKM)) {
                return true;
            }
        }
        return false;
    }

    // =========================================================
    // 14. ĐỌC FILE
    // =========================================================
    public void docFile(String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Chưa có file dữ liệu khuyến mãi tại: "
                    + filePath + " (sẽ khởi tạo khi lưu).");
            return;
        }

        Khuyenmai[] danhSachTam = new Khuyenmai[SUC_CHUA_MAC_DINH];
        int soLuongTam = 0;
        int soDong = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {

            String dong;

            while ((dong = br.readLine()) != null) {
                soDong++;
                dong = dong.trim();

                if (dong.isEmpty() || dong.startsWith("#")) {
                    continue;
                }

                String[] p = dong.split(";", -1);

                String loai = p[0].trim();
                String ma;
                String tenLoai;

                if (p.length < 6) {
                    throw new IllegalArgumentException(
                            "Dòng " + soDong + ": Không đủ trường dữ liệu!");
                }

                ma = Ktradulieu.chuanHoaMa(p[1]);
                tenLoai = p[2].trim();

                if (ma.isEmpty() || tenLoai.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Dòng " + soDong + ": Mã hoặc loại khuyến mãi bị trống!");
                }

                if (trungMaTrongMang(danhSachTam, soLuongTam, ma)) {
                    throw new IllegalArgumentException(
                            "Dòng " + soDong + ": Mã khuyến mãi bị trùng: " + ma);
                }

                LocalDate ngayBD;
                LocalDate ngayKT;

                try {
                    ngayBD = LocalDate.parse(p[3].trim(), DINH_DANG_NGAY);
                    ngayKT = LocalDate.parse(p[4].trim(), DINH_DANG_NGAY);

                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException(
                            "Dòng " + soDong + ": Ngày không đúng định dạng dd/MM/yyyy!");
                }

                Khuyenmai km;

                try {
                    if ("MUATANG".equalsIgnoreCase(loai)) {

                        if (p.length != 7) {
                            throw new IllegalArgumentException(
                                    "Khuyến mãi mua tặng phải có 7 trường!");
                        }

                        int mua = Integer.parseInt(p[5].trim());
                        int tang = Integer.parseInt(p[6].trim());

                        km = new KhuyenmaiMuatang(
                                ma, tenLoai, ngayBD, ngayKT, mua, tang);

                    } else if ("PHANTRAM".equalsIgnoreCase(loai)) {

                        if (p.length != 6) {
                            throw new IllegalArgumentException(
                                    "Khuyến mãi phần trăm phải có 6 trường!");
                        }

                        double pt = Double.parseDouble(p[5].trim());

                        km = new KhuyenmaiPhantram(
                                ma, tenLoai, ngayBD, ngayKT, pt);

                    } else if ("TRUCTIEP".equalsIgnoreCase(loai)) {

                        if (p.length != 6) {
                            throw new IllegalArgumentException(
                                    "Khuyến mãi trực tiếp phải có 6 trường!");
                        }

                        long giam = Long.parseLong(p[5].trim());

                        km = new KhuyenmaiTructiep(
                                ma, tenLoai, ngayBD, ngayKT, giam);

                    } else {
                        throw new IllegalArgumentException(
                                "Loại khuyến mãi không xác định: " + loai);
                    }

                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Dòng " + soDong + ": Giá trị số không hợp lệ!");

                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Dòng " + soDong + ": " + e.getMessage());
                }

                if (!kiemTraKhuyenMai(km)) {
                    throw new IllegalArgumentException(
                            "Dòng " + soDong + ": Dữ liệu khuyến mãi không hợp lệ!");
                }

                if (soLuongTam >= danhSachTam.length) {
                    int sucChuaMoi = danhSachTam.length * 2;
                    Khuyenmai[] mangMoi = new Khuyenmai[sucChuaMoi];

                    for (int i = 0; i < soLuongTam; i++) {
                        mangMoi[i] = danhSachTam[i];
                    }

                    danhSachTam = mangMoi;
                }

                danhSachTam[soLuongTam] = km;
                soLuongTam++;
            }

            this.danhSach = danhSachTam;
            this.soLuong = soLuongTam;

            // Cập nhật bộ đếm mã theo dữ liệu vừa nạp
            for (int i = 0; i < this.soLuong; i++) {
                ghiNhanSoMa(this.danhSach[i].getMaKM());
            }

            System.out.println("Đã nạp thành công " + soLuongTam
                    + " chương trình khuyến mãi từ file: " + filePath);

        } catch (IOException | RuntimeException e) {
            Ktradulieu.danhDauFileLoi(filePath, "Dòng " + soDong + ": " + e.getMessage());
        }
    }
}