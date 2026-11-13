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
import java.util.List;
import java.util.Scanner;

import sanpham.DSSanPham;
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

    // =====================================================
    // 1. MỞ RỘNG MẢNG
    // =====================================================
    private int tinhSucChuaMoi(int sucChuaHienTai) {
        if (sucChuaHienTai >= Integer.MAX_VALUE) {
            throw new IllegalStateException("Danh sách liên kết đã đạt giới hạn!");
        }

        long sucChuaMoi = (long) sucChuaHienTai * 2;
        if (sucChuaMoi > Integer.MAX_VALUE) {
            sucChuaMoi = Integer.MAX_VALUE;
        }
        return (int) sucChuaMoi;
    }

    private void moRongBoNho() {
        int sucChuaMoi = tinhSucChuaMoi(this.danhSach.length);
        SPKhMai[] mangMoi = new SPKhMai[sucChuaMoi];

        for (int i = 0; i < this.soLuong; i++) {
            mangMoi[i] = this.danhSach[i];
        }
        this.danhSach = mangMoi;
    }

    // =====================================================
    // 2. TÌM KIẾM LIÊN KẾT
    // =====================================================
    public int timKiem(String maSP, String maKM) {
        if (maSP == null || maKM == null || maSP.trim().isEmpty() || maKM.trim().isEmpty()) {
            return -1;
        }

        String spChuan = Ktradulieu.chuanHoaMa(maSP);
        String kmChuan = Ktradulieu.chuanHoaMa(maKM);

        for (int i = 0; i < this.soLuong; i++) {
            SPKhMai item = this.danhSach[i];
            if (item != null
                    && item.getMaSP().equalsIgnoreCase(spChuan)
                    && item.getMaKM().equalsIgnoreCase(kmChuan)) {
                return i;
            }
        }
        return -1;
    }

    public boolean kiemTraLienKet(String maSP, String maKM) {
        return timKiem(maSP, maKM) != -1;
    }

    // =====================================================
    // 3. THÊM LIÊN KẾT
    // =====================================================
    public boolean them(SPKhMai item) {
        if (item == null) {
            System.out.println("[Lỗi] Dữ liệu liên kết không hợp lệ!");
            return false;
        }

        try {
            item.toDongFile();

            String maSP = Ktradulieu.chuanHoaMa(item.getMaSP());
            String maKM = Ktradulieu.chuanHoaMa(item.getMaKM());

            if (timKiem(maSP, maKM) != -1) {
                System.out.println("[Lỗi] Liên kết sản phẩm [" + maSP + "] - khuyến mãi [" + maKM + "] đã tồn tại!");
                return false;
            }

            if (this.soLuong >= this.danhSach.length) {
                moRongBoNho();
            }

            this.danhSach[this.soLuong] = item;
            this.soLuong++;

            System.out.println("[Thành công] Đã thêm liên kết sản phẩm [" + maSP + "] với khuyến mãi [" + maKM + "].");
            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("[Lỗi] Liên kết không hợp lệ: " + e.getMessage());
            return false;
        }
    }

    /**
     * Thêm liên kết nhưng kiểm tra trước: sản phẩm phải có trong kho và khuyến mãi phải tồn tại.
     * Truyền null cho danh sách nào thì bỏ qua việc kiểm tra danh sách đó.
     */
    public boolean them(SPKhMai item, DSSanPham dsSP, DSKhuyenMai dsKM) {
        if (item == null) {
            System.out.println("[Lỗi] Dữ liệu liên kết không hợp lệ!");
            return false;
        }

        try {
            String maSP = Ktradulieu.chuanHoaMa(item.getMaSP());
            String maKM = Ktradulieu.chuanHoaMa(item.getMaKM());

            if (dsSP != null && dsSP.timKiem(maSP) == null) {
                System.out.println("[Lỗi] Sản phẩm [" + maSP + "] không tồn tại trong kho!");
                return false;
            }

            if (dsKM != null && dsKM.timKiem(maKM) == null) {
                System.out.println("[Lỗi] Khuyến mãi [" + maKM + "] không tồn tại!");
                return false;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[Lỗi] Liên kết không hợp lệ: " + e.getMessage());
            return false;
        }

        return them(item);
    }

    public boolean themSP(Scanner sc) {
        System.out.println("\n--- THÊM SẢN PHẨM VÀO CHƯƠNG TRÌNH KHUYẾN MÃI ---");

        SPKhMai item = new SPKhMai();
        item.nhap(sc);

        if (them(item)) {
            ghiFile("data/sanphamkhuyenmai.txt");
            return true;
        }

        return false;
    }

    public boolean themSP(Scanner sc, DSSanPham dsSP, DSKhuyenMai dsKM) {
        System.out.println("\n--- THÊM SẢN PHẨM VÀO CHƯƠNG TRÌNH KHUYẾN MÃI ---");

        SPKhMai item = new SPKhMai();
        item.nhap(sc);

        if (them(item, dsSP, dsKM)) {
            ghiFile("data/sanphamkhuyenmai.txt");
            return true;
        }

        return false;
    }

    // lấy
    public List<String> layDanhSachMaSPTheoKM(String maKM) {
        List<String> ketQua = new ArrayList<>();

        if (maKM == null || maKM.trim().isEmpty()) {
            return ketQua;
        }

        String maChuan = Ktradulieu.chuanHoaMa(maKM);

        for (int i = 0; i < this.soLuong; i++) {
            SPKhMai item = this.danhSach[i];

            if (item != null && item.getMaKM().equalsIgnoreCase(maChuan)) {
                ketQua.add(item.getMaSP());
            }
        }

        return ketQua;
    }

    // =====================================================
    // 4. SỬA LIÊN KẾT
    // =====================================================
    public boolean suaSP(Scanner sc) {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm cần sửa liên kết: ", ".+", "Mã sản phẩm không được để trống!");
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần sửa liên kết: ", ".+", "Mã khuyến mãi không được để trống!");

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

        int chon = Ktradulieu.luaChon(sc, "Nhập lựa chọn (0-3): ", 0, 3);

        String maSPMoi = this.danhSach[viTri].getMaSP();
        String maKMMoi = this.danhSach[viTri].getMaKM();

        switch (chon) {
            case 1:
                maSPMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã sản phẩm mới: ", ".+", "Mã sản phẩm không được để trống!");
                break;
            case 2:
                maKMMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã khuyến mãi mới: ", ".+", "Mã khuyến mãi không được để trống!");
                break;
            case 3:
                maSPMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã sản phẩm mới: ", ".+", "Mã sản phẩm không được để trống!");
                maKMMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã khuyến mãi mới: ", ".+", "Mã khuyến mãi không được để trống!");
                break;
            case 0:
                System.out.println("[Thông báo] Đã hủy thao tác sửa.");
                return false;
            default:
                return false;
        }

        try {
            maSPMoi = Ktradulieu.chuanHoaMa(maSPMoi);
            maKMMoi = Ktradulieu.chuanHoaMa(maKMMoi);

            SPKhMai lienKetTam = new SPKhMai(maSPMoi, maKMMoi);
            int viTriTrung = timKiem(lienKetTam.getMaSP(), lienKetTam.getMaKM());

            if (viTriTrung != -1 && viTriTrung != viTri) {
                System.out.println("[Lỗi] Liên kết SP [" + maSPMoi + "] - KM [" + maKMMoi + "] đã tồn tại!");
                return false;
            }

            this.danhSach[viTri].setLienKet(lienKetTam.getMaSP(), lienKetTam.getMaKM());
            ghiFile("data/sanphamkhuyenmai.txt");

            System.out.println("[Thành công] Sửa liên kết thành công!");
            return true;

        } catch (IllegalArgumentException e) {
            System.out.println("[Lỗi] " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // 5. XÓA MỘT LIÊN KẾT
    // =====================================================
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
        ghiFile("data/sanphamkhuyenmai.txt");

        return true;
    }

    // =====================================================
    // 6. XÓA TẤT CẢ LIÊN KẾT THEO MÃ KHUYẾN MÃI
    // =====================================================
    public int xoaTheoMaKM(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return 0;
        }

        String maChuan = Ktradulieu.chuanHoaMa(maKM);
        int soLienKetDaXoa = 0;

        for (int i = 0; i < this.soLuong;) {
            if (this.danhSach[i].getMaKM().equalsIgnoreCase(maChuan)) {
                for (int j = i; j < this.soLuong - 1; j++) {
                    this.danhSach[j] = this.danhSach[j + 1];
                }

                this.danhSach[this.soLuong - 1] = null;
                this.soLuong--;
                soLienKetDaXoa++;
            } else {
                i++;
            }
        }
        if (soLienKetDaXoa > 0) {
            ghiFile("data/sanphamkhuyenmai.txt");
        }

        return soLienKetDaXoa;
    }

    /**
     * Dọn các liên kết "mồ côi": trỏ tới khuyến mãi đã bị xóa hoặc sản phẩm không còn trong kho.
     * Nên gọi sau khi nạp file để loại dữ liệu cũ bị sót.
     * Truyền null cho danh sách nào thì không kiểm tra theo danh sách đó.
     * @return số liên kết đã xóa
     */
    public int xoaLienKetMoCoi(DSSanPham dsSP, DSKhuyenMai dsKM) {
        int soDaXoa = 0;

        for (int i = 0; i < this.soLuong;) {
            SPKhMai item = this.danhSach[i];
            boolean moCoi = false;

            try {
                if (dsKM != null && dsKM.timKiem(item.getMaKM()) == null) {
                    moCoi = true;
                }
                if (!moCoi && dsSP != null && dsSP.timKiem(item.getMaSP()) == null) {
                    moCoi = true;
                }
            } catch (IllegalArgumentException e) {
                moCoi = true; // mã sai định dạng cũng coi là liên kết hỏng
            }

            if (moCoi) {
                for (int j = i; j < this.soLuong - 1; j++) {
                    this.danhSach[j] = this.danhSach[j + 1];
                }
                this.danhSach[this.soLuong - 1] = null;
                this.soLuong--;
                soDaXoa++;
            } else {
                i++;
            }
        }

        if (soDaXoa > 0) {
            System.out.println("[Thông báo] Đã dọn " + soDaXoa + " liên kết sản phẩm - khuyến mãi không còn hợp lệ.");
        }
        return soDaXoa;
    }

    // =====================================================
    // 7. XÓA LIÊN KẾT TỪ BÀN PHÍM
    // =====================================================
    public boolean xoaSP(Scanner sc) {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm cần xóa liên kết: ", ".+", "Mã sản phẩm không được để trống!");
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần xóa liên kết: ", ".+", "Mã khuyến mãi không được để trống!");

        if (xoa(maSP, maKM)) {
            System.out.println("[Thành công] Xóa liên kết thành công!");
            return true;
        }

        System.out.println("[Lỗi] Không tìm thấy liên kết cần xóa!");
        return false;
    }

    // =====================================================
    // 8. XUẤT DANH SÁCH
    // =====================================================
    public void xuatDanhSach() {
        if (this.soLuong == 0) {
            System.out.println("[Thông báo] Danh sách sản phẩm khuyến mãi đang trống!");
            return;
        }

        System.out.println("\n===== DANH SÁCH SẢN PHẨM KHUYẾN MÃI =====");
        System.out.printf("%-6s | %-15s | %-15s%n", "STT", "MÃ SẢN PHẨM", "MÃ KHUYẾN MÃI");
        System.out.println("------------------------------------------------");

        for (int i = 0; i < this.soLuong; i++) {
            System.out.printf(
                    "%-6d | %-15s | %-15s%n",
                    i + 1,
                    this.danhSach[i].getMaSP(),
                    this.danhSach[i].getMaKM());
        }

        System.out.println("Tổng số liên kết: " + this.soLuong);
    }

    // =====================================================
    // 9. GHI FILE
    // =====================================================
    public void ghiFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            System.out.println("[Lỗi] Đường dẫn file không hợp lệ!");
            return;
        }

        String dich = Ktradulieu.duongDanGhi(filePath);
        File tam = Ktradulieu.chuanBiFileTam(dich);
        try {
            try (BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(tam), StandardCharsets.UTF_8))) {
                for (int i = 0; i < this.soLuong; i++) {
                    if (this.danhSach[i] == null) {
                        throw new IllegalStateException("Danh sách chứa liên kết rỗng tại vị trí " + i);
                    }
                    bw.write(this.danhSach[i].toDongFile());
                    bw.newLine();
                }
            }
            Ktradulieu.hoanTatGhi(tam, dich);
            System.out.println("[Thành công] Lưu file sản phẩm khuyến mãi tại: " + dich);
        } catch (IOException | RuntimeException e) {
            Ktradulieu.huyFileTam(tam);
            System.out.println("[Lỗi] Ghi file sản phẩm khuyến mãi: " + e.getMessage());
        }
    }

    // =====================================================
    // 10. ĐỌC FILE AN TOÀN
    // =====================================================
    public void docFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            System.out.println("[Lỗi] Đường dẫn file không hợp lệ!");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[Thông báo] Chưa có file dữ liệu sản phẩm khuyến mãi tại: " + filePath);
            return;
        }

        SPKhMai[] danhSachTam = new SPKhMai[SUC_CHUA_MAC_DINH];
        int soLuongTam = 0;
        int soDong = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String dong;

            while ((dong = br.readLine()) != null) {
                soDong++;
                dong = dong.trim();

                if (dong.isEmpty() || dong.startsWith("#")) {
                    continue;
                }

                SPKhMai item;
                try {
                    item = SPKhMai.tuDongFile(dong);
                    if (item == null) {
                        throw new IllegalArgumentException("Dữ liệu liên kết rỗng!");
                    }
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException("Dòng " + soDong + ": " + e.getMessage());
                }

                for (int i = 0; i < soLuongTam; i++) {
                    if (danhSachTam[i].getMaSP().equalsIgnoreCase(item.getMaSP())
                            && danhSachTam[i].getMaKM().equalsIgnoreCase(item.getMaKM())) {
                        throw new IllegalArgumentException(
                                "Dòng " + soDong + ": Liên kết sản phẩm - khuyến mãi bị trùng!");
                    }
                }

                if (soLuongTam >= danhSachTam.length) {
                    int sucChuaMoi = tinhSucChuaMoi(danhSachTam.length);
                    SPKhMai[] mangMoi = new SPKhMai[sucChuaMoi];

                    for (int i = 0; i < soLuongTam; i++) {
                        mangMoi[i] = danhSachTam[i];
                    }
                    danhSachTam = mangMoi;
                }

                danhSachTam[soLuongTam] = item;
                soLuongTam++;
            }

            this.danhSach = danhSachTam;
            this.soLuong = soLuongTam;

            System.out.println("[Thành công] Đọc file sản phẩm khuyến mãi! Tổng số: " + soLuongTam);

        } catch (IOException | RuntimeException e) {
            Ktradulieu.danhDauFileLoi(filePath, e.getMessage());
        }
    }
}