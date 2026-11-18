package hoadon;

import util.BangConsole;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Comparator;
import util.HienThiBang;
import java.util.Scanner;
import doitac.DSNhaCungCap;
import doitac.NhaCungCap;
import person.DsKhachHang;
import person.DSNhanVien;
import person.Nguoidung;
import sanpham.DSSanPham;
import sanpham.SanPham;
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

    // Chọn hàng trước, sau đó tách phiếu nhập theo nhà cung cấp của từng sản phẩm.
    public int nhapThemHang(Scanner sc, Nguoidung nguoiLap, DSNhaCungCap dsNCC, DSSanPham dsSP) {
        if (sc == null || nguoiLap == null || !nguoiLap.coQuyen("QUYEN_NHAP_KHO")
                || dsSP == null || dsNCC == null || !dsNCC.coNhaCungCapHoatDong()) {
            System.out.println("[Thông báo] Cần tài khoản nhập kho và nhà cung cấp đang hợp tác!");
            return 0;
        }
        HoadonNhap danhSachNhap = new HoadonNhap("HDN_TAM", nguoiLap, LocalDate.now(), "DangXuLy", "", "NCC_TAM");
        int soDaLuu = 0;
        try {
            boolean hoanTat = false;
            while (!hoanTat) {
                System.out.println("\n========== NHẬP THÊM HÀNG ==========");
                System.out.println("1. Chọn hàng cần nhập theo đề xuất");
                System.out.println("2. Thêm sản phẩm mới và nhập hàng");
                System.out.println("3. Xem / chỉnh danh sách hàng đang nhập");
                System.out.println("0. Hoàn tất, nhập mã hóa đơn và gửi duyệt");
                System.out.println("4. Hủy nhập hàng");
                int chon = Ktradulieu.luaChon(sc, "Chọn (0-4): ", 0, 4);
                switch (chon) {
                    case 1: chonHangDeXuat(sc, danhSachNhap, dsSP, dsNCC); break;
                    case 2: {
                        SanPham sp = dsSP.themSanPhamTuBanPhim(sc, dsNCC);
                        if (sp != null) nhapMatHang(sc, danhSachNhap, sp, dsSP, dsNCC, -1);
                        break;
                    }
                    case 3: suaDanhSachNhap(sc, danhSachNhap, dsSP, dsNCC); break;
                    case 0:
                        if (danhSachNhap.getSoLuongCT() == 0) {
                            System.out.println("[Thông báo] Chưa có mặt hàng để tạo phiếu nhập.");
                        } else hoanTat = true;
                        break;
                    case 4:
                        System.out.println("Đã hủy nhập hàng. Các danh mục mới vẫn có tồn kho 0.");
                        return 0;
                }
            }

            ArrayList<HoadonNhap> cacPhieu = tachPhieuTheoNhaCungCap(danhSachNhap, nguoiLap, dsSP, dsNCC);
            for (HoadonNhap phieu : cacPhieu) {
                NhaCungCap ncc = dsNCC.timKiemTheoMa(phieu.getMaNCC());
                System.out.println("\nTạo hóa đơn nhập cho [" + ncc.getMaNCC() + "] " + ncc.getTenNCC());
                if (!nhapMaHoaDonNhap(sc, phieu)) {
                    System.out.println("[Thông báo] Đã hủy tạo hóa đơn của " + ncc.getMaNCC() + ".");
                    continue;
                }
                phieu.setGhiChu(Ktradulieu.nhapChuoiTuyChon(sc, "Ghi chú (Enter để bỏ qua): ", ".*", ""));
                if (phieu.chinhSuaVaHoanTat(sc, nguoiLap, dsNCC, dsSP, nguoiLap) && themHD(phieu)) {
                    ghiFile("data/hoadon.txt");
                    soDaLuu++;
                } else {
                    System.out.println("[Thông báo] Phiếu của " + ncc.getMaNCC() + " chưa hoàn tất, không lưu hóa đơn.");
                }
            }
        } catch (java.util.NoSuchElementException e) {
            System.out.println("[Thông báo] Đã dừng nhập hàng. Các phiếu đã hoàn tất trước đó được giữ lại.");
        }
        System.out.println("Đã lưu " + soDaLuu + " phiếu nhập chờ Admin/Quản lý thanh toán và duyệt nhập kho.");
        return soDaLuu;
    }

    private void chonHangDeXuat(Scanner sc, HoadonNhap danhSachNhap, DSSanPham dsSP, DSNhaCungCap dsNCC) {
        while (true) {
            System.out.println("\n1. Hàng đã hết\n2. Hàng tồn kho thấp (1-10)\n3. Hàng sắp hết hạn (30 ngày)\n4. Hàng hết hạn\n0. Quay lại");
            int loai = Ktradulieu.luaChon(sc, "Chọn đề xuất (0-4): ", 0, 4);
            if (loai == 0) return;
            SanPham[] deXuat = dsSP.locHangCanNhap(loai);
            if (deXuat.length == 0) {
                System.out.println("Không có mặt hàng phù hợp đề xuất này.");
                continue;
            }
            BangConsole bang = new BangConsole("STT", "MÃ SP", "TÊN SẢN PHẨM", "MÃ NCC", "TỒN", "HSD");
            for (int i = 0; i < deXuat.length; i++) {
                SanPham sp = deXuat[i];
                bang.themDong(i + 1, sp.getMaSP(), sp.getTenSP(), sp.getMaNCC(), sp.getSoLuongTon(), Ktradulieu.dinhDangNgay(sp.getHsd()));
            }
            System.out.println(bang);
            System.out.println("1. Nhập tất cả mặt hàng trong đề xuất\n2. Chọn từng mặt hàng\n0. Quay lại");
            int cach = Ktradulieu.luaChon(sc, "Chọn (0-2): ", 0, 2);
            if (cach == 1) {
                for (SanPham sp : deXuat) nhapMatHang(sc, danhSachNhap, sp, dsSP, dsNCC, -1);
            } else if (cach == 2) {
                int stt = Ktradulieu.luaChon(sc, "Chọn STT sản phẩm (0: Quay lại): ", 0, deXuat.length);
                if (stt != 0) nhapMatHang(sc, danhSachNhap, deXuat[stt - 1], dsSP, dsNCC, -1);
            }
        }
    }

    public boolean chinhSuaHoaDonNhapTheoMa(Scanner sc, Nguoidung nguoiThucHien,
                                          DSNhaCungCap dsNCC, DSSanPham dsSP) {
        if (sc == null || nguoiThucHien == null || !nguoiThucHien.coQuyen("QUYEN_NHAP_KHO")
                || dsNCC == null || dsSP == null) {
            System.out.println("[Từ chối] Tài khoản hoặc dữ liệu chỉnh sửa hóa đơn không hợp lệ!");
            return false;
        }
        while (true) {
            String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hóa đơn nhập cần chỉnh sửa (0: Quay lại): ");
            if (ma.equals("0")) return false;
            HoaDon hd = timKiem(ma);
            if (!(hd instanceof HoadonNhap)) {
                System.out.println("[Thông báo] Không tìm thấy hóa đơn nhập có mã: " + ma);
                continue;
            }
            if (!hd.coTheChinhSua()) {
                System.out.println("[Khóa chỉnh sửa] Hóa đơn đã thanh toán, đã nhập kho hoặc đã hủy: " + hd.getMaHD());
                continue;
            }
            if (!nguoiThucHien.coQuyen("SUA_HOA_DON") && (hd.getNguoiLap() == null
                    || !nguoiThucHien.getMaND().equalsIgnoreCase(hd.getNguoiLap().getMaND()))) {
                System.out.println("[Từ chối] Nhân viên chỉ được sửa hóa đơn nhập chưa thanh toán do mình lập!");
                continue;
            }
            HoadonNhap phieu = (HoadonNhap) hd;
            while (phieu.coTheChinhSua()) {
                System.out.println("\n========== CHỈNH SỬA HÓA ĐƠN NHẬP " + phieu.getMaHD() + " ==========");
                System.out.println("Ngày lập: " + Ktradulieu.dinhDangNgay(phieu.getNgayLap())
                        + " | Người lập: " + phieu.getMaNV());
                phieu.kiemTraHoaDon();
                System.out.println("1. Thêm mặt hàng\n2. Sửa số lượng / đơn giá / VAT\n3. Xóa mặt hàng");
                System.out.println("4. Đổi nhà cung cấp\n5. Sửa ghi chú\n6. Hoàn tất chỉnh sửa và chờ duyệt\n7. Hủy hóa đơn nhập\n0. Lưu và quay lại");
                int chon = Ktradulieu.luaChon(sc, "Chọn (0-7): ", 0, 7);
                switch (chon) {
                    case 1: phieu.themSanPham(sc, dsSP, nguoiThucHien); break;
                    case 2: phieu.suaSanPham(sc); break;
                    case 3: phieu.xoaSanPham(sc); break;
                    case 4: phieu.chonNhaCungCap(sc, dsNCC, dsSP); break;
                    case 5: phieu.suaGhiChu(sc); break;
                    case 6:
                        if (phieu.hoanTatHoaDon(dsSP, dsNCC)) {
                            ghiFile("data/hoadon.txt");
                            return true;
                        }
                        break;
                    case 7:
                        if (Ktradulieu.nhapXacNhan(sc, "Xác nhận hủy hóa đơn nhập [" + phieu.getMaHD() + "]?")) {
                            phieu.huyHoaDon();
                            ghiFile("data/hoadon.txt");
                            return true;
                        }
                        break;
                    case 0:
                        ghiFile("data/hoadon.txt");
                        return true;
                }
                ghiFile("data/hoadon.txt");
            }
            return true;
        }
    }

    private boolean nhapMatHang(Scanner sc, HoadonNhap danhSachNhap, SanPham sp,
                               DSSanPham dsSP, DSNhaCungCap dsNCC, int viTriThay) {
        if (sp == null) {
            String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã sản phẩm (0: Hủy): ");
            if (ma.equals("0")) return false;
            sp = dsSP.timKiem(ma);
        }
        if (sp == null || sp.isDaXoa()) {
            System.out.println("[Thông báo] Sản phẩm không tồn tại hoặc đã bị xóa.");
            return false;
        }
        NhaCungCap ncc = dsNCC.timKiemTheoMa(sp.getMaNCC());
        if (ncc == null || ncc.isDaXoa()) {
            System.out.println("[Thông báo] Mặt hàng " + sp.getMaSP()
                    + " chưa có nhà cung cấp đang hợp tác. Hãy cập nhật NCC trong danh mục sản phẩm.");
            return false;
        }
        System.out.println("Nhập hàng cho [" + sp.getMaSP() + "] " + sp.getTenSP() + " - " + sp.getMaNCC());
        CTHDonNhap ct = new CTHDonNhap();
        if (!ct.nhapThongTin(sc, danhSachNhap.getMaHD(), sp)) return false;
        boolean daThem = viTriThay < 0 ? danhSachNhap.themCTHD(ct) : danhSachNhap.thayChiTiet(viTriThay, ct);
        System.out.println(daThem ? "Đã cập nhật danh sách cần nhập." : "[Lỗi] Dòng nhập không hợp lệ hoặc bị trùng mã sản phẩm!");
        return daThem;
    }

    private void suaDanhSachNhap(Scanner sc, HoadonNhap danhSachNhap, DSSanPham dsSP, DSNhaCungCap dsNCC) {
        while (true) {
            danhSachNhap.kiemTraHoaDon();
            System.out.println("1. Thêm mặt hàng\n2. Xóa dòng nhập\n3. Thay mặt hàng / sửa số lượng, giá\n0. Quay lại");
            int chon = Ktradulieu.luaChon(sc, "Chọn (0-3): ", 0, 3);
            if (chon == 0) return;
            if (chon == 1) { nhapMatHang(sc, danhSachNhap, null, dsSP, dsNCC, -1); continue; }
            if (danhSachNhap.getSoLuongCT() == 0) continue;
            int stt = Ktradulieu.luaChon(sc, "Chọn STT dòng nhập (0: Hủy): ", 0, danhSachNhap.getSoLuongCT());
            if (stt == 0) continue;
            if (chon == 2) danhSachNhap.xoaChiTiet(stt - 1);
            else nhapMatHang(sc, danhSachNhap, null, dsSP, dsNCC, stt - 1);
        }
    }

    private boolean nhapMaHoaDonNhap(Scanner sc, HoadonNhap phieu) {
        while (true) {
            String ma = Ktradulieu.chuanHoaMa(Ktradulieu.nhapChuoiKhongRong(sc,
                    "Nhập mã hóa đơn nhập (VD: HDN027, 0: Hủy phiếu): "));
            if (ma.equals("0")) return false;
            if (!ma.matches("HDN[0-9]+")) {
                System.out.println("[Lỗi] Mã hóa đơn nhập phải có dạng HDN và các chữ số, ví dụ HDN027.");
                continue;
            }
            if (timKiem(ma) != null) {
                System.out.println("[Lỗi] Mã hóa đơn đã tồn tại: " + ma);
                continue;
            }
            phieu.setMaHD(ma);
            System.out.println("Mã hóa đơn nhập: " + phieu.getMaHD());
            return true;
        }
    }

    // Chỉ gom các dòng tạm; người dùng nhập mã chính thức cho từng phiếu khi hoàn tất.
    public ArrayList<HoadonNhap> tachPhieuTheoNhaCungCap(HoadonNhap danhSachNhap, Nguoidung nguoiLap,
                                                      DSSanPham dsSP, DSNhaCungCap dsNCC) {
        if (danhSachNhap == null || nguoiLap == null || !nguoiLap.coQuyen("QUYEN_NHAP_KHO")
                || dsSP == null || dsNCC == null) {
            throw new IllegalArgumentException("Dữ liệu tách phiếu nhập không hợp lệ!");
        }
        ArrayList<HoadonNhap> cacPhieu = new ArrayList<>();
        for (CTHDonNhap ct : danhSachNhap.getDsChiTiet()) {
            SanPham sp = dsSP.timKiem(ct.getMaSP());
            NhaCungCap ncc = sp == null ? null : dsNCC.timKiemTheoMa(sp.getMaNCC());
            if (sp == null || sp.isDaXoa() || ncc == null || ncc.isDaXoa()) {
                throw new IllegalArgumentException("Nhà cung cấp không hợp lệ cho mặt hàng " + ct.getMaSP());
            }
            HoadonNhap phieu = null;
            for (HoadonNhap p : cacPhieu) if (p.getMaNCC().equalsIgnoreCase(sp.getMaNCC())) { phieu = p; break; }
            if (phieu == null) {
                phieu = new HoadonNhap("HDN_TAM", nguoiLap,
                        LocalDate.now(), "DangXuLy", "", ncc.getMaNCC());
                cacPhieu.add(phieu);
            }
            CTHDonNhap dong = new CTHDonNhap(ct);
            dong.setMaHD(phieu.getMaHD());
            if (!phieu.themCTHD(dong)) throw new IllegalArgumentException("Không thể gộp dòng nhập " + ct.getMaSP());
        }
        return cacPhieu;
    }

    public ArrayList<HoaDon> getDanhSach() {
        return new ArrayList<>(this.danhSach);
    }

    public HoaDon get(int index) {
        if (index < 0 || index >= this.danhSach.size()) return null;
        return this.danhSach.get(index);
    }

    public String sinhMaTuDong(String tienTo) {
        if (tienTo == null || tienTo.trim().isEmpty()) tienTo = "HD";
        tienTo = Ktradulieu.chuanHoaMa(tienTo);
        String mauMa = tienTo + "%03d";
        int maxSo = 0;

        for (HoaDon hd : this.danhSach) {
            if (hd != null && hd.getMaHD() != null && hd.getMaHD().startsWith(tienTo)) {
                int so = Ktradulieu.laySoTuMa(hd.getMaHD(), tienTo);
                if (so > maxSo) maxSo = so;
            }
        }
        if (maxSo == Integer.MAX_VALUE) throw new IllegalStateException("Không thể sinh thêm mã hóa đơn!");
        return Ktradulieu.taoMa(mauMa, maxSo + 1);
    }

    public int timViTri(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) return -1;
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

    private boolean dungLoai(HoaDon hd, String loai) {
        if (loai == null || loai.isBlank()) return true;
        if ("HDBAN".equalsIgnoreCase(loai)) return hd instanceof Hoadonban;
        if ("HDNHAP".equalsIgnoreCase(loai)) return hd instanceof HoadonNhap;
        throw new IllegalArgumentException("Loại hóa đơn không hợp lệ!");
    }

    public ArrayList<HoaDon> traCuuTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay, String loai) {
        if (tuNgay == null || denNgay == null || tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Khoảng ngày tra cứu không hợp lệ!");
        }
        ArrayList<HoaDon> ketQua = new ArrayList<>();
        for (HoaDon hd : danhSach) {
            if (dungLoai(hd, loai) && !hd.getNgayLap().isBefore(tuNgay)
                    && !hd.getNgayLap().isAfter(denNgay)) ketQua.add(hd);
        }
        return ketQua;
    }

    public ArrayList<HoaDon> timTheoDoiTac(String maDoiTac, String loai) {
        ArrayList<HoaDon> ketQua = new ArrayList<>();
        if (maDoiTac == null || maDoiTac.isBlank()) return ketQua;
        String ma = Ktradulieu.chuanHoaMa(maDoiTac);
        for (HoaDon hd : danhSach) {
            if (dungLoai(hd, loai) && ma.equalsIgnoreCase(hd.layMaDoiTac())) ketQua.add(hd);
        }
        return ketQua;
    }

    public ArrayList<HoaDon> locTheoTongTien(double toiThieu, double toiDa, String loai) {
        if (!Double.isFinite(toiThieu) || !Double.isFinite(toiDa) || toiThieu < 0 || toiDa < toiThieu) {
            throw new IllegalArgumentException("Khoảng tổng tiền không hợp lệ!");
        }
        ArrayList<HoaDon> ketQua = new ArrayList<>();
        for (HoaDon hd : danhSach) {
            double tong = hd.tinhThanhTien();
            if (dungLoai(hd, loai) && tong >= toiThieu && tong <= toiDa) ketQua.add(hd);
        }
        return ketQua;
    }

    public ArrayList<HoaDon> locChuaXuLy(String loai) {
        ArrayList<HoaDon> ketQua = new ArrayList<>();
        for (HoaDon hd : danhSach) {
            if (dungLoai(hd, loai) && !hd.isDaXuLyThanhCong() && !hd.isDaHuy()) ketQua.add(hd);
        }
        return ketQua;
    }

    public void traCuuHoaDon(Scanner sc, String loai) {
        System.out.println("\n===== TRA CỨU HÓA ĐƠN =====");
        int chon = Ktradulieu.luaChon(sc, "1. Khoảng ngày | 2. Đối tác | 3. Tổng tiền | 4. Chưa xử lý | 0. Quay lại: ", 0, 4);
        if (chon == 0) return;
        try {
            ArrayList<HoaDon> ketQua;
            switch (chon) {
                case 1:
                    LocalDate tu = Ktradulieu.nhapNgay(sc, "Từ ngày (dd/MM/yyyy): ");
                    LocalDate den = Ktradulieu.nhapNgay(sc, "Đến ngày (dd/MM/yyyy): ");
                    ketQua = traCuuTheoKhoangNgay(tu, den, loai);
                    break;
                case 2:
                    ketQua = timTheoDoiTac(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã khách hàng hoặc nhà cung cấp: "), loai);
                    break;
                case 3:
                    double min = Ktradulieu.nhapSoThucKhongAm(sc, "Tổng tiền tối thiểu: ");
                    double max = Ktradulieu.nhapSoThucKhongAm(sc, "Tổng tiền tối đa: ");
                    ketQua = locTheoTongTien(min, max, loai);
                    break;
                default:
                    ketQua = locChuaXuLy(loai);
            }
            System.out.println(BangConsole.tuDanhSach(ketQua));
            System.out.println("Tổng số hóa đơn: " + ketQua.size());
        } catch (IllegalArgumentException e) {
            System.out.println("[Lỗi] " + e.getMessage());
        }
    }

    public static class SanPhamBanChay implements HienThiBang {
        private final String maSP;
        private final String tenSP;
        private long soLuongBan;
        private long soLuongTang;
        private double tienHangSauGiam;

        private SanPhamBanChay(String maSP, String tenSP) {
            this.maSP = maSP;
            this.tenSP = tenSP;
        }

        public String getMaSP() { return maSP; }
        public long getSoLuongBan() { return soLuongBan; }
        public long getSoLuongTang() { return soLuongTang; }
        public double getTienHangSauGiam() { return tienHangSauGiam; }

        @Override
        public String[] layTieuDeBang() {
            return new String[]{"MÃ SP", "TÊN SẢN PHẨM", "SL BÁN", "SL TẶNG", "TIỀN HÀNG SAU GIẢM"};
        }

        @Override
        public Object[] layDongBang() {
            return new Object[]{maSP, tenSP, soLuongBan, soLuongTang, Ktradulieu.dinhDangTien(tienHangSauGiam)};
        }

        @Override
        public String toString() { return BangConsole.tuDoiTuong(this); }
    }

    /** Xếp theo số lượng bán, rồi tiền hàng sau giảm; không tính quà tặng vào lượng bán. */
    public ArrayList<SanPhamBanChay> topNSanPham(int n, LocalDate tuNgay, LocalDate denNgay) {
        if (n <= 0) throw new IllegalArgumentException("Số lượng N phải lớn hơn 0!");
        Map<String, SanPhamBanChay> thongKe = new LinkedHashMap<>();
        for (HoaDon hd : traCuuTheoKhoangNgay(tuNgay, denNgay, "HDBAN")) {
            if (!hd.isDaXuLyThanhCong()) continue;
            for (CTHDBan ct : ((Hoadonban) hd).getDsChiTiet()) {
                String ma = ct.getMaSP().toUpperCase(Locale.ROOT);
                SanPhamBanChay dong = thongKe.computeIfAbsent(ma, key -> new SanPhamBanChay(key, ct.getTenSP()));
                dong.soLuongBan = Math.addExact(dong.soLuongBan, ct.getSoLuongBan());
                dong.soLuongTang = Math.addExact(dong.soLuongTang, ct.getSoLuongTang());
                dong.tienHangSauGiam += ct.tinhTienHang() - ct.tinhTienGiamHieuLuc();
                if (!Double.isFinite(dong.tienHangSauGiam)) throw new IllegalArgumentException("Tổng tiền thống kê vượt giới hạn!");
            }
        }
        ArrayList<SanPhamBanChay> ketQua = new ArrayList<>(thongKe.values());
        ketQua.sort(Comparator.comparingLong(SanPhamBanChay::getSoLuongBan).reversed()
                .thenComparing(Comparator.comparingDouble(SanPhamBanChay::getTienHangSauGiam).reversed())
                .thenComparing(SanPhamBanChay::getMaSP));
        return new ArrayList<>(ketQua.subList(0, Math.min(n, ketQua.size())));
    }

    public void thongKeTopNSanPham(Scanner sc) {
        long n = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng sản phẩm bán chạy cần xem: ");
        if (n <= 0 || n > Integer.MAX_VALUE) {
            System.out.println("[Lỗi] Số lượng N không hợp lệ!");
            return;
        }
        System.out.println("\n===== TOP SẢN PHẨM BÁN CHẠY =====");
        System.out.println(BangConsole.tuDanhSach(topNSanPham((int) n, LocalDate.MIN, LocalDate.MAX)));
        System.out.println("Tiền hàng sau giảm chưa gồm VAT và điểm thưởng; chỉ tính hóa đơn đã hoàn tất.");
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

    public boolean lapHoaDonMoi(
            Scanner sc,
            Nguoidung nvDangNhap,
            DsKhachHang dsKH,
            DSNhaCungCap dsNCC,
            DSSanPham dsSP,
            DSKhuyenMai dsKM,
            DSSanPhamKhuyenMai dsSPKM) {

        if (sc == null || nvDangNhap == null) {
            System.out.println("[Lỗi] Thông tin nhân viên đăng nhập không hợp lệ!");
            return false;
        }

        System.out.println("\n========== CHỌN LOẠI HÓA ĐƠN CẦN LẬP ==========");
        System.out.println("1. Lập Hóa đơn bán lẻ");
        System.out.println("2. Lập Hóa đơn nhập hàng");
        System.out.println("0. Hủy bỏ");

        int chon = Ktradulieu.luaChon(sc, "Nhập lựa chọn (0-2): ", 0, 2);

        switch (chon) {
            case 1: {
                Hoadonban hdb = new Hoadonban();

                return hdb.lapHoaDon(sc, nvDangNhap, this, dsKH, dsSP, dsKM, dsSPKM)
                        && themHD(hdb);
            }

            case 2: {
                HoadonNhap hdn = new HoadonNhap();

                Nguoidung nguoiDungDangNhap = nvDangNhap;

                return hdn.lapHoaDon(sc, nvDangNhap, this, dsNCC, dsSP, nguoiDungDangNhap)
                        && themHD(hdn);
            }

            case 0:
                System.out.println("[Thông báo] Đã hủy thao tác lập hóa đơn.");
                return false;

            default:
                System.out.println("[Lỗi] Lựa chọn không hợp lệ!");
                return false;
        }
    }

    public void xuatDanhSachHoaDonChoDuyet() {
        ArrayList<HoaDon> dsChoDuyet = new ArrayList<>();
        System.out.println("\n========== HÓA ĐƠN NHẬP CHỜ DUYỆT ==========");

        for (HoaDon hd : this.danhSach) {
            if (hd instanceof HoadonNhap && !hd.isDaXuLyThanhCong() && !hd.isDaHuy()) {
                dsChoDuyet.add(hd);
            }
        }
        if (dsChoDuyet.isEmpty()) {
            System.out.println("[Thông báo] Không có hóa đơn nhập chờ duyệt!");
        } else {
            System.out.println(BangConsole.tuDanhSach(dsChoDuyet));
        }
    }

    public void xuatDanhSachHoaDonNhap() {
        ArrayList<HoaDon> dsNhap = new ArrayList<>();
        System.out.println("\n====================== DANH SÁCH HÓA ĐƠN NHẬP ======================");
        for (HoaDon hd : this.danhSach) {
            if (hd instanceof HoadonNhap) {
                dsNhap.add(hd);
            }
        }
        if (dsNhap.isEmpty()) {
            System.out.println("[Thông báo] Chưa có hóa đơn nhập nào trong hệ thống!");
        } else {
            System.out.println(BangConsole.tuDanhSach(dsNhap));
            System.out.println("Tổng số hóa đơn nhập: " + dsNhap.size());
        }
    }

    private boolean coQuyenDuyetHoaDon(Nguoidung nguoiDuyet) {
        return nguoiDuyet != null && nguoiDuyet.coQuyen("DUYET_HOA_DON");
    }

    public boolean duyetHoaDon(Scanner sc, Nguoidung nguoiDuyet, DSSanPham dsSP, DSNhaCungCap dsNCC) {
        // 1. Kiểm tra dữ liệu đầu vào
        if (sc == null || dsSP == null || dsNCC == null) {
            System.out.println("[Lỗi] Scanner, danh sách sản phẩm hoặc nhà cung cấp không hợp lệ!");
            return false;
        }

        // 2. Kiểm tra quyền duyệt
        if (!coQuyenDuyetHoaDon(nguoiDuyet)) {
            System.out.println("[Lỗi] Bạn không có quyền duyệt hóa đơn!");
            return false;
        }

        // 3. Lọc hóa đơn nhập kho chưa xử lý thành công
        ArrayList<HoaDon> dsChoDuyet = new ArrayList<>();

        for (HoaDon hd : this.danhSach) {
            if (hd instanceof HoadonNhap && !hd.isDaXuLyThanhCong() && !hd.isDaHuy()) {
                dsChoDuyet.add(hd);
            }
        }

        // 4. Kiểm tra danh sách chờ duyệt
        if (dsChoDuyet.isEmpty()) {
            System.out.println("\n[Thông báo] Hiện không có hóa đơn nhập kho chờ duyệt!");
            return false;
        }

        // 5. Hiển thị danh sách hóa đơn chờ duyệt
        System.out.println("\n========== DANH SÁCH HÓA ĐƠN NHẬP CHỜ DUYỆT ==========");
        System.out.println(BangConsole.tuDanhSach(dsChoDuyet));
        System.out.println("0. Quay lại");

        // 6. Quản lý chọn hóa đơn bằng STT
        int chon;

        while (true) {
            chon = Ktradulieu.luaChon(
                    sc, "Chọn hóa đơn cần duyệt (0-" + dsChoDuyet.size() + "): ", 0, dsChoDuyet.size());

            if (chon >= 0 && chon <= dsChoDuyet.size()) {
                break;
            }

            System.out.println("[Lỗi] Số thứ tự không hợp lệ! Vui lòng chọn lại.");
        }

        // 7. Nếu chọn 0 thì quay lại
        if (chon == 0) {
            System.out.println("[Thông báo] Đã hủy thao tác duyệt hóa đơn.");
            return false;
        }

        HoaDon hd = dsChoDuyet.get(chon - 1);

        HoadonNhap hdNhap = (HoadonNhap) hd;

        System.out.println("\n========== THÔNG TIN HÓA ĐƠN NHẬP ==========");
        System.out.println("Ngày lập: " + Ktradulieu.dinhDangNgay(hdNhap.getNgayLap())
                + " | Người lập: " + hdNhap.getMaNV());
        hdNhap.kiemTraHoaDon();
        System.out.println("Thanh toán: " + (hdNhap.isDaThanhToan() ? "Đã thanh toán" : "Chưa thanh toán"));

        System.out.println("\n========== CHI TIẾT DUYỆT HÓA ĐƠN ==========");

        System.out.println("\n============================================");
        System.out.println("Bạn có chắc chắn muốn duyệt hóa đơn này không?");
        System.out.println("1. Có, thanh toán và duyệt nhập kho");
        System.out.println("0. Không, hủy thao tác");

        int xacNhan;

        while (true) {
            xacNhan = Ktradulieu.luaChon(
                    sc, "Nhập lựa chọn (0-1): ", 0, 1);

            if (xacNhan == 0 || xacNhan == 1) {
                break;
            }

            System.out.println("[Lỗi] Vui lòng chỉ chọn 0 hoặc 1!");
        }

        if (xacNhan == 0) {
            System.out.println("[Thông báo] Đã hủy duyệt hóa đơn. Hóa đơn vẫn đang chờ duyệt!");
            return false;
        }

        NhaCungCap ncc = dsNCC.timKiemTheoMa(hdNhap.getMaNCC());
        if (ncc == null || ncc.isDaXoa()) {
            System.out.println("[Lỗi] Nhà cung cấp không còn hợp lệ để duyệt nhập kho!");
            return false;
        }
        if (!hdNhap.isDaThanhToan() && !hdNhap.thanhToanHoaDon(sc, nguoiDuyet, dsSP, dsNCC)) {
            System.out.println("[Thông báo] Chưa thanh toán thành công. Hóa đơn vẫn chờ duyệt, chưa nhập kho.");
            return false;
        }

        boolean ketQua = hdNhap.duyetNhapKho(sc, dsSP, nguoiDuyet);
        // Giữ thông tin đã chi tiền cả khi nhập kho chưa hoàn tất, tránh thanh toán lại khi thử duyệt tiếp.
        ghiFile("data/hoadon.txt");
        if (ketQua) {
            System.out.println(hdNhap.inHoaDon());
        }

        return ketQua;
    }

    public boolean suaHD(Scanner sc, Nguoidung nguoiThucHien) {
        if (nguoiThucHien == null || !nguoiThucHien.coQuyen("SUA_HOA_DON")) {
            System.out.println("[Từ chối] Chỉ Quản lý được sửa hóa đơn.");
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
        System.out.println("Loại: " + hd.getLoaiHD() + " | Ngày lập: " + Ktradulieu.dinhDangNgay(hd.getNgayLap()) + " | Trạng thái: " + hd.getTrangThai());
        System.out.println("1. Sửa ghi chú hóa đơn\n2. Sửa ngày lập hóa đơn\n0. Hủy bỏ");
        int chon = Ktradulieu.luaChon(sc, "Nhập lựa chọn (0-2): ", 0, 2);

        try {
            switch (chon) {
                case 1:
                    hd.setGhiChu(Ktradulieu.nhapChuoiTuyChon(sc, "Nhập ghi chú mới: ", ".+", ""));
                    System.out.println("[Thành công] Đã cập nhật ghi chú mới.");
                    break;
                case 2:
                    hd.setNgayLap(Ktradulieu.nhapNgay(sc, "Nhập ngày lập mới (dd/MM/yyyy): "));
                    System.out.println("[Thành công] Đã cập nhật ngày lập hóa đơn.");
                    break;
                case 0:
                    System.out.println("[Thông báo] Đã hủy thao tác sửa.");
                    return false;
                default:
                    System.out.println("[Lỗi] Lựa chọn không hợp lệ!");
                    return false;
            }
            ghiFile("data/hoadon.txt");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean xoaHD(Scanner sc, Nguoidung nguoiThucHien) {
        if (nguoiThucHien == null || !nguoiThucHien.coQuyen("HUY_HOA_DON")) {
            System.out.println(
                    "[Từ chối] Chỉ Quản lý được hủy hóa đơn.");
            return false;
        }

        String maHD = Ktradulieu.nhapChuoi(
                sc, "Nhập mã hóa đơn cần hủy: ", ".+",
                "Mã không được để trống!");

        int viTri = timViTri(maHD);

        if (viTri == -1) {
            System.out.println(
                    "[Lỗi] Không tìm thấy hóa đơn có mã: " + maHD);
            return false;
        }

        HoaDon hd = this.danhSach.get(viTri);

        if (hd.isDaXuLyThanhCong()) {
            System.out.println(
                    "[Lỗi] Không thể hủy! Hóa đơn [" + maHD
                            + "] đã xử lý thành công.");
            return false;
        }

        if (hd.isDaHuy()) {
            System.out.println(
                    "[Lỗi] Hóa đơn [" + maHD + "] đã được hủy trước đó.");
            return false;
        }
        hd.huyHoaDon();
        ghiFile("data/hoadon.txt");
        System.out.println(
                "[Thành công] Đã hủy hóa đơn: " + maHD);

        return true;
    }

    public void xuatDanhSach() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        if (this.danhSach.isEmpty()) {
            return "[Thông báo] Danh sách hóa đơn hiện đang trống!";
        }
        String xuongDong = System.lineSeparator();
        return xuongDong + "===== DANH SÁCH TOÀN BỘ HÓA ĐƠN =====" + xuongDong
                + BangConsole.tuDanhSach(this.danhSach) + xuongDong
                + "Tổng số hóa đơn lưu trữ: " + this.danhSach.size();
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
        if (filePath == null || filePath.isBlank()) {
            System.out.println("[Lỗi] Đường dẫn file không hợp lệ!");
            return;
        }
        String dich = Ktradulieu.duongDanGhi(filePath);
        File thuMuc = Ktradulieu.chuanBiFileTam(dich).getAbsoluteFile().getParentFile();
        File tam = null;
        try {
            // Không ghi đè file .tmp còn sót từ phiên cũ (có thể chứa giao dịch chưa lưu).
            tam = Files.createTempFile(thuMuc.toPath(), "hoadon-" + new File(dich).getName() + ".", ".tmp").toFile();
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(tam), StandardCharsets.UTF_8))) {
                Set<String> cacMa = new HashSet<>();
                for (HoaDon hd : this.danhSach) {
                    if (hd == null || !cacMa.add(hd.getMaHD().toUpperCase(Locale.ROOT))) {
                        throw new IllegalArgumentException("Hóa đơn rỗng hoặc trùng mã!");
                    }
                    bw.write(hd.toFileString());
                    bw.newLine();
                    CTHoaDon[] chiTiet;
                    String loaiChiTiet;
                    if (hd instanceof HoadonNhap) {
                        chiTiet = ((HoadonNhap) hd).getDsChiTiet();
                        loaiChiTiet = "CTNHAP;";
                    } else if (hd instanceof Hoadonban) {
                        chiTiet = ((Hoadonban) hd).getDsChiTiet();
                        loaiChiTiet = "CTBAN;";
                    } else {
                        throw new IllegalArgumentException("Loại hóa đơn chưa được hỗ trợ!");
                    }
                    for (CTHoaDon ct : chiTiet) {
                        if (ct == null || !hd.getMaHD().equalsIgnoreCase(ct.getMaHD())) {
                            throw new IllegalArgumentException("Chi tiết không thuộc hóa đơn đang lưu!");
                        }
                        bw.write(loaiChiTiet + ct.toDongFile());
                        bw.newLine();
                    }
                }
            }
            Ktradulieu.hoanTatGhi(tam, dich);
            System.out.println("[Thành công] Đã lưu " + this.danhSach.size() + " hóa đơn vào file: " + dich);
        } catch (IOException | RuntimeException e) {
            Ktradulieu.huyFileTam(tam);
            System.out.println("[Lỗi] Ghi file hóa đơn: " + e.getMessage());
        }
    }

    public void docFile(String filePath, DSNhanVien dsNV) {
        if (filePath == null || filePath.isBlank()) {
            System.out.println("[Lỗi] Đường dẫn file không hợp lệ!");
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[Thông báo] Chưa có file dữ liệu hóa đơn tại: " + filePath);
            return;
        }
        ArrayList<HoaDon> danhSachTam = new ArrayList<>();
        Map<String, HoaDon> theoMa = new LinkedHashMap<>();
        Map<String, Double> tongTienDaLuu = new LinkedHashMap<>();
        int soDong = 0;
        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8))) {
                String dong;
                while ((dong = br.readLine()) != null) {
                    soDong++;
                    dong = dong.trim();
                    if (dong.isEmpty() || dong.startsWith("#")) continue;
                    if (dong.startsWith("CTNHAP;") || dong.startsWith("CTBAN;")) {
                        boolean laNhap = dong.startsWith("CTNHAP;");
                        String duLieu = dong.substring(laNhap ? 7 : 6);
                        CTHoaDon ct = laNhap ? CTHDonNhap.tuDongFile(duLieu) : CTHDBan.tuDongFile(duLieu);
                        if (ct == null) throw new IllegalArgumentException("Chi tiết hóa đơn không hợp lệ!");
                        HoaDon chu = theoMa.get(ct.getMaHD().toUpperCase(Locale.ROOT));
                        boolean daThem = laNhap && chu instanceof HoadonNhap
                                ? ((HoadonNhap) chu).themCTHDTuFile((CTHDonNhap) ct)
                                : !laNhap && chu instanceof Hoadonban
                                && ((Hoadonban) chu).themCTHDTuFile((CTHDBan) ct);
                        if (!daThem) throw new IllegalArgumentException("Chi tiết trùng hoặc không có hóa đơn tương ứng!");
                    } else {
                        HoaDon hd = HoaDon.tuDongFile(dong, dsNV);
                        String ma = hd.getMaHD().toUpperCase(Locale.ROOT);
                        if (theoMa.putIfAbsent(ma, hd) != null) {
                            throw new IllegalArgumentException("Mã hóa đơn bị trùng: " + hd.getMaHD());
                        }
                        String[] p = dong.split(";", -1);
                        tongTienDaLuu.put(ma, Double.parseDouble(p[hd instanceof Hoadonban ? 10 : 8]));
                        danhSachTam.add(hd);
                    }
                }
            }
            for (HoaDon hd : danhSachTam) {
                hd.kiemTraDuLieuFile();
                // Dòng tổng kết lưu VND làm tròn, cho phép chênh tối đa 1 đồng.
                if (Math.abs(hd.tinhThanhTien() - tongTienDaLuu.get(hd.getMaHD().toUpperCase(Locale.ROOT))) > 1) {
                    throw new IllegalArgumentException("Tổng tiền không khớp chi tiết của hóa đơn " + hd.getMaHD());
                }
            }
            this.danhSach.clear();
            this.danhSach.addAll(danhSachTam);
            System.out.println("[Thành công] Đã nạp " + danhSachTam.size() + " hóa đơn từ file.");
        } catch (IOException | RuntimeException e) {
            Ktradulieu.danhDauFileLoi(filePath, "Dòng " + soDong + ": " + e.getMessage());
        }
    }
}
