package sanpham;

import util.BangConsole;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

import person.Nguoidung;
import doitac.DSNhaCungCap;
import doitac.NhaCungCap;
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

        System.out.println("Đã lưu sản phẩm: [" + sp.getMaSP() + "] " + sp.getTenSP() + " vào kho.");
        return true;
    }

    // thêm sp
    public SanPham themSanPhamTuBanPhim(Scanner sc, DSNhaCungCap dsNCC) {
        if (dsNCC == null || !dsNCC.coNhaCungCapHoatDong()) {
            System.out.println("[Thông báo] Vui lòng thêm nhà cung cấp đang hợp tác trước!");
            return null;
        }
        System.out.println("\n--- THÊM SẢN PHẨM MỚI VÀO DANH MỤC ---");
        System.out.println("1. Thức ăn\n2. Đồ uống\n0. Quay lại");
        int chon = Ktradulieu.luaChon(sc, "Chọn loại (0-2): ", 0, 2);
        if (chon == 0) return null;
        SanPham sp = chon == 1 ? new ThucAn() : new DoUong();
        sp.nhapThongTin(sc, this);
        NhaCungCap ncc = dsNCC.chonNhaCungCap(sc);
        if (ncc == null) return null;
        sp.setMaNCC(ncc.getMaNCC());
        return themSP(sp) ? sp : null;
    }

    // Mặt hàng ở file cũ được chọn NCC một lần khi nhập hàng lần đầu.
    public boolean boSungNhaCungCap(Scanner sc, SanPham sp, DSNhaCungCap dsNCC) {
        if (sp == null || sp.isDaXoa() || dsNCC == null) return false;
        if (sp.getMaNCC().isEmpty()) {
            System.out.println("Chọn nhà cung cấp cho [" + sp.getMaSP() + "] " + sp.getTenSP());
            NhaCungCap ncc = dsNCC.chonNhaCungCap(sc);
            if (ncc == null) return false;
            sp.setMaNCC(ncc.getMaNCC());
            ghiFile("data/sanpham.txt");
        }
        NhaCungCap ncc = dsNCC.timKiemTheoMa(sp.getMaNCC());
        if (ncc == null || ncc.isDaXoa()) {
            System.out.println("[Lỗi] Nhà cung cấp của mặt hàng không còn hợp tác: " + sp.getMaSP());
            return false;
        }
        return true;
    }

    public SanPham[] locHangCanNhap(int loai) {
        SanPham[] tam = new SanPham[soLuong];
        int dem = 0;
        LocalDate homNay = LocalDate.now();
        for (int i = 0; i < soLuong; i++) {
            SanPham sp = danhSach[i];
            if (sp.isDaXoa() || sp.getHsd() == null) continue;
            int ton = sp.getSoLuongTon();
            long soNgay = java.time.temporal.ChronoUnit.DAYS.between(homNay, sp.getHsd());
            if ((loai == 1 && ton == 0) || (loai == 2 && ton > 0 && ton <= 10 && soNgay >= 0)
                    || (loai == 3 && ton > 0 && soNgay >= 0 && soNgay <= 30)
                    || (loai == 4 && ton > 0 && soNgay < 0)) tam[dem++] = sp;
        }
        return Arrays.copyOf(tam, dem);
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
        if (nguoiThucHien == null || !nguoiThucHien.coQuyen("QUAN_LY_SAN_PHAM")) {

            System.out.println(
                    "[Lỗi] BẠN KHÔNG CÓ QUYỀN! Chỉ Quản lý hoặc Admin mới được thực hiện thao tác này."
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
        return suaSP(sc, maSP, nguoiThucHien, null);
    }

    public boolean suaSP(Scanner sc, String maSP, Nguoidung nguoiThucHien, DSNhaCungCap dsNCC) {
        if (nguoiThucHien == null || !nguoiThucHien.coQuyen("QUAN_LY_SAN_PHAM")) {
            System.out.println("BẠN KHÔNG CÓ QUYỀN! Chỉ Quản lý hoặc Admin mới được sửa sản phẩm.");
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
        System.out.println("4. Sửa thuế VAT (%)");
        System.out.println("5. Sửa ngày sản xuất & hạn sử dụng");
        System.out.println("6. Đổi nhà cung cấp");
        System.out.println("0. Hủy bỏ");


        int chon = Ktradulieu.luaChon(sc, " Chọn trường cần sửa (0-6): ", 0, 6);
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
                    double thueMoi = Ktradulieu.nhapSoThuc(sc, "Nhập % thuế VAT mới (VD: 8 hoặc 10): ");
                    sp.setPhanTramThue(thueMoi);
                    break;
                case 5:
                    LocalDate nsxDate = Ktradulieu.nhapNgay(sc, "Nhập NSX mới (dd/MM/yyyy): ");
                    LocalDate hsdDate = Ktradulieu.nhapNgay(sc, "Nhập HSD mới (dd/MM/yyyy): ");
                    sp.setHSD(nsxDate, hsdDate);
                    break;
                case 6:
                    NhaCungCap ncc = dsNCC == null ? null : dsNCC.chonNhaCungCap(sc);
                    if (ncc == null) return false;
                    sp.setMaNCC(ncc.getMaNCC());
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

    public void kiemTraVaCapNhatHanSuDung() {
        SanPham[] hetHan = locSanPhamHetHan(LocalDate.now());
        for (SanPham sp : hetHan) sp.setSoLuongTon(0);
        if (hetHan.length > 0) ghiFile("data/sanpham.txt");
        System.out.println("Đã loại tồn hết hạn của " + hetHan.length + " mặt hàng về 0.");
    }

    public SanPham[] locSanPhamHetHan(LocalDate ngayKiemTra) {
        return locTheoHanSuDung(ngayKiemTra, true, 0);
    }

    public SanPham[] locSanPhamSapHetHan(int soNgayCanhBao, LocalDate ngayKiemTra) {
        return locTheoHanSuDung(ngayKiemTra, false, soNgayCanhBao);
    }

    private SanPham[] locTheoHanSuDung(LocalDate ngayKiemTra, boolean hetHan, int soNgayCanhBao) {
        if (ngayKiemTra == null || soNgayCanhBao < 0) {
            throw new IllegalArgumentException("Ngày kiểm tra hoặc số ngày cảnh báo không hợp lệ!");
        }
        SanPham[] tam = new SanPham[soLuong];
        int dem = 0;
        for (int i = 0; i < soLuong; i++) {
            SanPham sp = danhSach[i];
            if (sp.isDaXoa() || sp.getSoLuongTon() <= 0) continue;
            if (hetHan ? sp.daHetHan(ngayKiemTra) : sp.sapHetHan(ngayKiemTra, soNgayCanhBao)) {
                tam[dem++] = sp;
            }
        }
        return Arrays.copyOf(tam, dem);
    }

    public void thongKeSanPhamHetHan() {
        thongKeSanPhamHetHan(LocalDate.now());
    }

    public void thongKeSanPhamHetHan(LocalDate ngayKiemTra) {
        xuatThongKeHanSuDung("SẢN PHẨM HẾT HẠN CÒN TỒN", locSanPhamHetHan(ngayKiemTra), ngayKiemTra);
    }

    public void thongKeSanPhamSapHetHan() {
        thongKeSanPhamSapHetHan(30, LocalDate.now());
    }

    public void thongKeSanPhamSapHetHan(int soNgayCanhBao, LocalDate ngayKiemTra) {
        xuatThongKeHanSuDung("SẢN PHẨM SẮP HẾT HẠN (0-" + soNgayCanhBao + " NGÀY)",
                locSanPhamSapHetHan(soNgayCanhBao, ngayKiemTra), ngayKiemTra);
    }

    private void xuatThongKeHanSuDung(String tieuDe, SanPham[] ds, LocalDate ngayKiemTra) {
        System.out.println("\n========== " + tieuDe + " ==========");
        System.out.println("Ngày kiểm tra: " + Ktradulieu.dinhDangNgay(ngayKiemTra));
        BangConsole bang = new BangConsole("STT", "MÃ SP", "LOẠI", "TÊN SẢN PHẨM", "MÃ NCC", "TỒN", "ĐƠN VỊ", "NSX", "HSD", "TÌNH TRẠNG");
        long tongSoLuong = 0;
        for (int i = 0; i < ds.length; i++) {
            SanPham sp = ds[i];
            long soNgay = java.time.temporal.ChronoUnit.DAYS.between(ngayKiemTra, sp.getHsd());
            bang.themDong(i + 1, sp.getMaSP(), sp.getLoaiSanPham(), sp.getTenSP(),
                    sp.getMaNCC().isEmpty() ? "Chưa gán" : sp.getMaNCC(), sp.getSoLuongTon(), sp.getDonViTinh(),
                    Ktradulieu.dinhDangNgay(sp.getNsx()), Ktradulieu.dinhDangNgay(sp.getHsd()),
                    soNgay < 0 ? "Quá hạn " + (-soNgay) + " ngày" : "Còn " + soNgay + " ngày");
            tongSoLuong += sp.getSoLuongTon();
        }
        if (ds.length == 0) System.out.println("Không có sản phẩm phù hợp còn tồn trong kho.");
        else System.out.println(bang);
        System.out.println("Số mặt hàng: " + ds.length);
        System.out.println("Tổng số đơn vị còn tồn: " + tongSoLuong);
    }

    public long loaiBoSoLuongHetHan(Nguoidung nguoiThucHien) {
        return loaiBoSoLuongHetHan(nguoiThucHien, LocalDate.now());
    }

    public long loaiBoSoLuongHetHan(Nguoidung nguoiThucHien, LocalDate ngayKiemTra) {
        if (nguoiThucHien == null || !nguoiThucHien.coQuyen("QUYEN_KIEM_KE_KHO")) {
            System.out.println("[Từ chối] Tài khoản không có quyền kiểm kê hoặc đã bị khóa!");
            return 0;
        }
        SanPham[] hetHan = locSanPhamHetHan(ngayKiemTra);
        long tongLoai = 0;
        for (SanPham sp : hetHan) {
            tongLoai += sp.getSoLuongTon();
            sp.setSoLuongTon(0);
        }
        if (tongLoai > 0) ghiFile("data/sanpham.txt");
        System.out.println("Đã loại " + tongLoai + " đơn vị hết hạn của " + hetHan.length + " mặt hàng khỏi tồn kho.");
        return tongLoai;
    }

    public void xuatDanhSach() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        if (this.soLuong == 0) {
            return "[Thông báo] Kho hiện chưa có sản phẩm.";
        }
        String xuongDong = System.lineSeparator();
        return xuongDong + "===== DANH SÁCH SẢN PHẨM TRONG KHO =====" + xuongDong
                + BangConsole.tuDanhSach(Arrays.asList(getDanhSach())) + xuongDong
                + "Tổng số sản phẩm: " + this.soLuong;
    }

    // đọc file
    public void docFile(String duongDan) {
        File file = new File(duongDan);

        if (!file.exists()) {
            System.out.println("[Thông báo] Chưa tìm thấy file sản phẩm: " + duongDan);
            return;
        }

        // Xóa dữ liệu cũ trước khi đọc lại file
        this.soLuong = 0;

        try (BufferedReader br = new BufferedReader(
                new java.io.InputStreamReader(
                        new java.io.FileInputStream(file),
                        java.nio.charset.StandardCharsets.UTF_8))) {

            String line;
            int dem = 0;
            int loi = 0;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                try {
                    SanPham sp = SanPham.tuDongFile(line);
                    String ma = sp.getMaSP();

                    if (timKiem(ma) != null) {
                        loi++;
                        continue;
                    }

                    if (this.soLuong >= this.danhSach.length) {
                        moRongBoNho();
                    }

                    this.danhSach[this.soLuong++] = sp;
                    dem++;

                } catch (Exception e) {
                    loi++;
                    System.out.println(
                            "[Cảnh báo] Dòng sản phẩm không hợp lệ: "
                                    + e.getMessage()
                    );
                }
            }

            System.out.println(
                    "[Thành công] Đã đọc " + dem
                            + " sản phẩm từ file: " + duongDan
            );

            if (loi > 0) {
                System.out.println(
                        "[Cảnh báo] Có " + loi
                                + " dòng sản phẩm không đọc được."
                );
            }

        } catch (IOException e) {
            System.out.println("[Lỗi] Đọc file sản phẩm: " + e.getMessage());
        }
    }

    // ghi file
    public void ghiFile(String duongDan) {
        duongDan = Ktradulieu.duongDanGhi(duongDan);
        File file = new File(duongDan);

        // Tự tạo thư mục chứa file nếu chưa tồn tại
        File thuMucCha = file.getAbsoluteFile().getParentFile();

        if (thuMucCha != null && !thuMucCha.exists()) {
            thuMucCha.mkdirs();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(duongDan + ".tmp"))) {
            for (int i = 0; i < this.soLuong; i++) {
                SanPham sp = this.danhSach[i];

                if (sp instanceof ThucAn) {
                    bw.write(((ThucAn) sp).toFileString());
                } else if (sp instanceof DoUong) {
                    bw.write(((DoUong) sp).toFileString());
                }

                bw.newLine();
            }

            bw.close();
            Ktradulieu.hoanTatGhi(new File(duongDan + ".tmp"), duongDan);
            System.out.println("Đã lưu danh sách " + this.soLuong + " sản phẩm vào file: " + duongDan);

        } catch (IOException e) {
            System.out.println("Lỗi ghi file SP: " + e.getMessage());
        }
    }
}
