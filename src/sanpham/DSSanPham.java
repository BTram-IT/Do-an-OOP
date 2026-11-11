package sanpham;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
        System.out.println("Đã lưu sản phẩm: [" + sp.getMaSP() + "] " + sp.getTenSP() + " vào kho.");
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

    // 3. XÓA SẢN PHẨM (Dồn mảng tĩnh, chỉ Quản lý hoặc Giám đốc mới xóa được)
    public boolean xoaSP(String maSP, Nguoidung nguoiThucHien) {
        if (!nguoiThucHien.getVaiTro().equalsIgnoreCase("QuanLy") && !nguoiThucHien.getVaiTro().equalsIgnoreCase("GiamDoc")) {
            System.out.println("[Lỗi] BẠN KHÔNG CÓ QUYỀN! Chỉ Quản lý hoặc Giám đốc mới được thực hiện thao tác này.");
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
            System.out.println(" Không tìm thấy mã SP: " + maSP);
            return false;
        }

        for (int i = viTri; i < this.soLuong - 1; i++) {
            this.danhSach[i] = this.danhSach[i + 1];
        }
        this.danhSach[--this.soLuong] = null;
        System.out.println(" Đã xóa thành công sản phẩm: " + maSP);
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
        return true;
    }

    public void kiemTraVaCapNhatHanSuDung() {
        int hetHan = 0;
        for (int i = 0; i < this.soLuong; i++) {
            // Hàm kiemTraHanSuDung của SanPham tự so sánh với LocalDate.now()
            if (!this.danhSach[i].kiemTraHanSuDung()) {
                hetHan++;
            }
        }
        if (hetHan == 0) {
            System.out.println("Tất cả sản phẩm trong kho đều còn hạn sử dụng.");
        } else {
            System.out.println("Có " + hetHan + " sản phẩm hết hạn đã được cập nhật đưa về tồn kho = 0!");
        }
    }

    public void xuatDanhSach() {
        System.out.println("\n================ DANH SÁCH SẢN PHẨM TRONG KHO (" + this.soLuong + " sản phẩm) ================");
        for (int i = 0; i < this.soLuong; i++) {
            System.out.println((i + 1) + ". " + this.danhSach[i].toString());
        }
        System.out.println("==================================================================================");
    }

    // đọc file
    public void docFile(String duongDan) {
        File file = new File(duongDan);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] p = line.split(";");
                if (p.length >= 15) { // Đảm bảo đủ các cột dữ liệu
                    String loai = p[0].trim();
                    String ma = p[1].trim();
                    String ten = p[2].trim();
                    double giaNhap = Double.parseDouble(p[3].trim());
                    double giaBan = Double.parseDouble(p[4].trim());
                    int ton = Integer.parseInt(p[5].trim());
                    String dvt = p[6].trim();
                    String nsx = p[7].trim();
                    String hsd = p[8].trim();
                    double thue = Double.parseDouble(p[9].trim());

                    SanPham sp;
                    if ("DOUONG".equalsIgnoreCase(loai)) {
                        double dungTich = Double.parseDouble(p[10].trim());
                        String thuongHieu = p[11].trim();
                        String huongVi = p[12].trim();
                        String loaiDoUong = p[13].trim();
                        String trangThai = p[14].trim();
                        boolean daXoa = (p.length > 15) && Boolean.parseBoolean(p[15].trim());
                        sp = new DoUong(ma, ten, giaNhap, giaBan, ton, dvt, nsx, hsd, thue,
                                dungTich, thuongHieu, huongVi, loaiDoUong, trangThai, daXoa);
                    } else {
                        String thanhPhan = p[10].trim();
                        String baoQuan = p[11].trim();
                        String xuatXu = p[12].trim();
                        boolean laDoChay = Boolean.parseBoolean(p[13].trim());
                        String trangThai = p[14].trim();
                        boolean daXoa = (p.length > 15) && Boolean.parseBoolean(p[15].trim());
                        sp = new ThucAn(ma, ten, giaNhap, giaBan, ton, dvt, nsx, hsd, thue,
                                thanhPhan, baoQuan, xuatXu, laDoChay, trangThai, daXoa);
                    }

                    if (this.soLuong >= this.danhSach.length) {
                        moRongBoNho();
                    }
                    this.danhSach[this.soLuong++] = sp;
                }
            }
            System.out.println("Đã đọc thành công " + this.soLuong + " sản phẩm từ file: " + duongDan);
        } catch (Exception e) {
            System.out.println("Lỗi đọc file SP: " + e.getMessage());
        }
    }

    // ghi file
    public void ghiFile(String duongDan) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(duongDan))) {
            for (int i = 0; i < this.soLuong; i++) {
                SanPham sp = this.danhSach[i];
                if (sp instanceof ThucAn) {
                    bw.write(((ThucAn) sp).toFileString());
                } else if (sp instanceof DoUong) {
                    bw.write(((DoUong) sp).toFileString());
                }
                bw.newLine();
            }
            System.out.println("Đã lưu danh sách " + this.soLuong + " sản phẩm vào file: " + duongDan);
        } catch (IOException e) {
            System.out.println("Lỗi ghi file SP: " + e.getMessage());
        }
    }
}