package sanpham;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import person.Nguoidung;
import person.QuanLyCuaHang;
import person.Giamdoc;
import util.Ktradulieu;

public class DSSanPham {

    private SanPham[] danhSach;
    private int soLuong;
    private static final int SUC_CHUA_MAC_DINH = 50;

    public DSSanPham() {
        this.danhSach = new SanPham[SUC_CHUA_MAC_DINH];
        this.soLuong = 0;


    }

    public DSSanPham(int sucChuaBanDau) {
        if (sucChuaBanDau <= 0) sucChuaBanDau = SUC_CHUA_MAC_DINH;
        this.danhSach = new SanPham[sucChuaBanDau];
        this.soLuong = 0;
    }


    public int getSoLuong() {
        return this.soLuong;
    }

    public SanPham get(int index) {
        if (index < 0 || index >= this.soLuong) return null;
        return this.danhSach[index];
    }

    public SanPham[] getDanhSach() {
        SanPham[] ketQua = new SanPham[this.soLuong];
        for (int i = 0; i < this.soLuong; i++) {
            ketQua[i] = this.danhSach[i];
        }
        return ketQua;
    }


    private void moRongBoNho() {
        int sucChuaMoi = this.danhSach.length * 2;
        SanPham[] mangMoi = new SanPham[sucChuaMoi];
        for (int i = 0; i < this.soLuong; i++) {
            mangMoi[i] = this.danhSach[i];
        }
        this.danhSach = mangMoi;
    }

    public String sinhMaTuDong(String tienTo) {
        int maxSo = 0;

        for (int i = 0; i < this.soLuong; i++) {
            String maHienTai = this.danhSach[i].getMaSP();
            int soHienTai = Ktradulieu.laySoTuMa(maHienTai, tienTo);
            if (soHienTai > maxSo) {
                maxSo = soHienTai;
            }
        }
        int soTiepTheo = maxSo + 1;
        return Ktradulieu.taoMa(tienTo + "%03d", soTiepTheo);
    }

    public boolean themSP(SanPham sp) {
        if (sp == null) {
            throw new IllegalArgumentException("Không thể thêm sản phẩm null!");
        }
        if (timKiem(sp.getMaSP()) != null) {
            System.out.println("Mã SP " + sp.getMaSP() + " đã tồn tại trong kho!");
            return false;
        }
        if (this.soLuong >= this.danhSach.length) {
            moRongBoNho();
        }
        this.danhSach[this.soLuong++] = sp;
        ghiFile("data/sanpham.txt");
        return true;
    }

    // thêm sp
    public void themSanPhamTuBanPhim(Scanner sc) {
        System.out.println("\n--- THÊM SẢN PHẨM MỚI VÀO KHO ---");
        System.out.println("1. Thêm Thức Ăn");
        System.out.println("2. Thêm Đồ Uống");
        int chon = Ktradulieu.nhapSoNguyen(sc, "Chọn loại (1-2): ", 1, 2);

        SanPham sp;
        if (chon == 1) {
            sp = new ThucAn();
        } else {
            sp = new DoUong();
        }
        sp.nhapThongTin(sc, this);
        this.themSP(sp);
    }

    public SanPham timKiem(String maSP) {
        if (maSP == null || maSP.trim().isEmpty()) return null;
        for (int i = 0; i < this.soLuong; i++) {
            if (this.danhSach[i].getMaSP().equalsIgnoreCase(maSP.trim())) {
                return this.danhSach[i];
            }
        }
        return null;
    }

    public boolean xoaSP(String maSP, Nguoidung nguoiThucHien) {

        // Kiểm tra quyền
        if (!(nguoiThucHien instanceof QuanLyCuaHang)
                && !(nguoiThucHien instanceof Giamdoc)) {

            System.out.println(
                    "[Lỗi] BẠN KHÔNG CÓ QUYỀN! Chỉ Quản lý hoặc Giám đốc mới được thực hiện thao tác này."
            );
            return false;
        }

        // Kiểm tra mã sản phẩm
        if (maSP == null || maSP.trim().isEmpty()) {
            System.out.println("Mã sản phẩm không được để trống!");
            return false;
        }

        int viTri = -1;

        for (int i = 0; i < this.soLuong; i++) {
            if (this.danhSach[i].getMaSP().equalsIgnoreCase(maSP.trim())) {
                viTri = i;
                break;
            }
        }

        if (viTri == -1) {
            System.out.println("Không tìm thấy mã SP: " + maSP);
            return false;
        }

        // Dồn mảng sau khi xóa
        for (int i = viTri; i < this.soLuong - 1; i++) {
            this.danhSach[i] = this.danhSach[i + 1];
        }
        this.danhSach[--this.soLuong] = null;
        ghiFile("data/sanpham.txt");

        System.out.println("Đã xóa thành công sản phẩm: " + maSP);

        return true;
    }

    // Sửa sp
    public boolean suaSP(Scanner sc, String maSP, Nguoidung nguoiThucHien) {
        if (!(nguoiThucHien instanceof QuanLyCuaHang) && !(nguoiThucHien instanceof Giamdoc)) {
            System.out.println("BẠN KHÔNG CÓ QUYỀN! Chỉ Quản lý hoặc Giám đốc mới được sửa sản phẩm.");
            return false;
        }

        SanPham sp = timKiem(maSP);
        if (sp == null) {
            System.out.println(" Không tìm thấy sản phẩm: " + maSP);
            return false;
        }

        System.out.println("\n--- SỬA THÔNG TIN SẢN PHẨM: " + sp.getTenSP() + " [" + sp.getMaSP() + "] ---");
        System.out.println("1. Sửa tên sản phẩm");
        System.out.println("2. Sửa đơn giá nhập");
        System.out.println("3. Sửa đơn giá bán");
        System.out.println("4. Sửa số lượng tồn kho");
        System.out.println("5. Sửa thuế VAT (%)");
        System.out.println("6. Sửa ngày sản xuất & hạn sử dụng");
        System.out.println("0. Hủy bỏ");


        int chon = Ktradulieu.nhapSoNguyen(sc, " Chọn trường cần sửa (0-6): ", 0, 6);
        try {
            switch (chon) {
                case 1:
                    String tenMoi = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên mới: ");
                    sp.setTenSP(tenMoi);
                    break;
                case 2:
                    double giaNhap = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập đơn giá nhập mới (đ): ");
                    sp.setDonGiaNhap(giaNhap);
                    break;
                case 3:
                    double giaBan = Ktradulieu.nhapSoThuc(sc, "Nhập đơn giá bán mới (đ): ");
                    sp.setDonGiaBan(giaBan);
                    break;
                case 4:
                    int tonMoi = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng tồn kho mới: ");
                    sp.setSoLuongTon(tonMoi);
                    break;
                case 5:
                    double thueMoi = Ktradulieu.nhapSoThuc(sc, "Nhập % thuế VAT mới (VD: 8 hoặc 10): ");
                    sp.setPhanTramThue(thueMoi);
                    break;
                case 6:
                    LocalDate nsxDate = Ktradulieu.nhapNgay(sc, "Nhập NSX mới (dd/MM/yyyy): ");
                    LocalDate hsdDate = Ktradulieu.nhapNgay(sc, "Nhập HSD mới (dd/MM/yyyy): ");
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    sp.setHSD(nsxDate.format(dtf), hsdDate.format(dtf));
                    break;
                case 0:
                    System.out.println("Đã hủy thao tác sửa.");
                    return false;
            }
            ghiFile("data/sanpham.txt");
            System.out.println("Đã cập nhật thông tin sản phẩm thành công!");
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Lỗi dữ liệu: " + e.getMessage());
            return false;
        }
    }

    // Cập nhật tồn kho và hạn sử dụng
    public boolean capNhatSoLuong(String maSP, int soLuongThayDoi) {
        SanPham sp = timKiem(maSP);
        if (sp == null) return false;
        int tonMoi = sp.getSoLuongTon() + soLuongThayDoi;
        if (tonMoi < 0) return false;
        sp.setSoLuongTon(tonMoi);
        ghiFile("data/sanpham.txt");

        return true;
    }

    // Thao tác nghiệp vụ có chủ đích: chuyển tồn của sản phẩm quá hạn sang "số lượng hết hạn" rồi lưu file.
    public void kiemTraVaCapNhatHanSuDung() {
        int hetHan = 0;

        for (int i = 0; i < this.soLuong; i++) {
            SanPham sp = this.danhSach[i];
            int daChuyen = sp.xuLyHetHan();
            if (daChuyen > 0) {
                hetHan++;
                System.out.println("Sản phẩm [" + sp.getMaSP() + "] " + sp.getTenSP()
                        + " ĐÃ HẾT HẠN (HSD: " + sp.getHsd() + ")! Đã chuyển "
                        + daChuyen + " " + sp.getDonViTinh() + " vào số lượng hết hạn (Tồn kho = 0).");
            }
        }

        if (hetHan == 0) {
            System.out.println("Không có sản phẩm hết hạn nào còn tồn trong kho.");
        } else {
            ghiFile("data/sanpham.txt");
            System.out.println("Có " + hetHan + " sản phẩm hết hạn đã được cập nhật đưa về tồn kho = 0!");
        }
    }

    public void xuatDanhSach() {
        System.out.println("\n                          DANH SÁCH SẢN PHẨM TRONG KHO");
        System.out.println("                       Tổng số: " + this.soLuong + " sản phẩm");

        if (this.soLuong == 0) {
            System.out.println("\n                 Kho hiện chưa có sản phẩm.");
            return;
        }

        for (int i = 0; i < this.soLuong; i++) {
            System.out.printf("\n  %02d. %s%n", i + 1, this.danhSach[i]);

            if (i < this.soLuong - 1) {
                System.out.println("  " + "─".repeat(60));
            }
        }

        System.out.println("\n                             KẾT THÚC DANH SÁCH SẢN PHẨM");
    }

    // đọc file: đọc toàn bộ vào danh sách tạm; chỉ thay thế danh sách hiện tại khi mọi dòng đều hợp lệ.
    // Trả về true nếu đọc thành công (hoặc chưa có file), false nếu file có lỗi / không đọc được
    // (khi đó danh sách hiện tại giữ nguyên và file gốc được bảo vệ, không bị ghi đè).
    public boolean docFile(String duongDan) {
        File file = new File(duongDan);

        if (!file.exists()) {
            System.out.println("[Thông báo] Chưa tìm thấy file sản phẩm: " + duongDan);
            return true;
        }

        java.util.ArrayList<SanPham> tam = new java.util.ArrayList<>();
        int soDong = 0;

        try (BufferedReader br = new BufferedReader(
                new java.io.InputStreamReader(
                        new java.io.FileInputStream(file),
                        java.nio.charset.StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                soDong++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                SanPham sp;
                try {
                    sp = phanTichDong(line);
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException("Dòng " + soDong + ": " + e.getMessage());
                }

                for (SanPham da : tam) {
                    if (da.getMaSP().equalsIgnoreCase(sp.getMaSP())) {
                        throw new IllegalArgumentException(
                                "Dòng " + soDong + ": Mã sản phẩm bị trùng: " + sp.getMaSP());
                    }
                }
                tam.add(sp);
            }

        } catch (IOException | RuntimeException e) {
            System.out.println("[Lỗi] Đọc file sản phẩm thất bại: " + e.getMessage());
            System.out.println("[Thông báo] Danh sách sản phẩm hiện tại được giữ nguyên, không có dữ liệu nào bị thay thế.");
            Ktradulieu.danhDauFileLoi(duongDan, e.getMessage());
            return false;
        }

        this.danhSach = new SanPham[Math.max(SUC_CHUA_MAC_DINH, tam.size() * 2)];
        this.soLuong = 0;
        for (SanPham sp : tam) {
            this.danhSach[this.soLuong++] = sp;
        }

        System.out.println("[Thành công] Đã đọc " + this.soLuong + " sản phẩm từ file: " + duongDan);
        return true;
    }

    private static boolean docBoolean(String s, String tenTruong) {
        String t = s.trim();
        if (t.equalsIgnoreCase("true")) return true;
        if (t.equalsIgnoreCase("false")) return false;
        throw new IllegalArgumentException(tenTruong + " phải là true/false: '" + s + "'");
    }

    // Phân tích một dòng dữ liệu sản phẩm; ném IllegalArgumentException nếu dòng không hợp lệ
    private SanPham phanTichDong(String line) {
        String[] p = line.split(";", -1);

        if (p.length < 15) {
            throw new IllegalArgumentException("Thiếu trường dữ liệu (cần ít nhất 15, có " + p.length + ")");
        }

        String loai = p[0].trim();
        String ma = p[1].trim();
        String ten = p[2].trim();

        double giaNhap = Ktradulieu.docSoThuc(p[3]);
        double giaBan = Ktradulieu.docSoThuc(p[4]);
        int ton = Integer.parseInt(p[5].trim());

        String dvt = p[6].trim();
        String nsx = p[7].trim();
        String hsd = p[8].trim();

        double thue = Ktradulieu.docSoThuc(p[9]);

        boolean daXoa = p.length > 15 && docBoolean(p[15], "Trạng thái xóa");

        // Trường tùy chọn (file cũ không có): số lượng hết hạn
        int hetHan = 0;
        if (p.length > 16 && !p[16].trim().isEmpty()) {
            hetHan = Integer.parseInt(p[16].trim());
            if (hetHan < 0) {
                throw new IllegalArgumentException("Số lượng hết hạn không được âm: " + hetHan);
            }
        }

        SanPham ketQua;

        if ("DOUONG".equalsIgnoreCase(loai)) {
            double dungTich = Ktradulieu.docSoThuc(p[10]);
            ketQua = new DoUong(
                    ma, ten, giaNhap, giaBan, ton,
                    dvt, nsx, hsd, thue,
                    dungTich, p[11].trim(), p[12].trim(),
                    p[13].trim(), p[14].trim(), daXoa);
        } else if ("THUCAN".equalsIgnoreCase(loai)) {
            ketQua = new ThucAn(
                    ma, ten, giaNhap, giaBan, ton,
                    dvt, nsx, hsd, thue,
                    p[10].trim(), p[11].trim(), p[12].trim(),
                    docBoolean(p[13], "Đồ chay"), p[14].trim(), daXoa);
        } else {
            throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: '" + loai + "'");
        }
        ketQua.setSoLuongHetHan(hetHan);
        return ketQua;
    }

    // ghi file: ghi ra file tạm (UTF-8) rồi mới thay file thật; file đang bị bảo vệ do đọc lỗi sẽ không bị ghi đè
    public boolean ghiFile(String duongDan) {
        java.util.ArrayList<String> cacDong = new java.util.ArrayList<>();
        for (int i = 0; i < this.soLuong; i++) {
            SanPham sp = this.danhSach[i];
            if (sp instanceof ThucAn) {
                cacDong.add(((ThucAn) sp).toFileString());
            } else if (sp instanceof DoUong) {
                cacDong.add(((DoUong) sp).toFileString());
            } else {
                System.out.println("[Lỗi] Ghi file SP bị hủy: loại sản phẩm không xác định tại vị trí " + i);
                return false;
            }
        }

        try {
            Ktradulieu.ghiDongAnToan(duongDan, cacDong);
            return true;
        } catch (IOException | RuntimeException e) {
            System.out.println("Lỗi ghi file SP: " + e.getMessage());
            return false;
        }
    }
}
