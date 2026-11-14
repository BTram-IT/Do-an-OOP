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
        return new ArrayList<>(this.danhSach);
    }

    public HoaDon get(int index) {
        if (index < 0 || index >= this.danhSach.size()) return null;
        return this.danhSach.get(index);
    }

    public String sinhMaTuDong(String tienTo) {
        if (tienTo == null || tienTo.trim().isEmpty()) tienTo = "HD";
        String mauMa = tienTo + "%03d";
        int maxSo = 0;

        for (HoaDon hd : this.danhSach) {
            if (hd != null && hd.getMaHD() != null && hd.getMaHD().startsWith(tienTo)) {
                int so = Ktradulieu.laySoTuMa(hd.getMaHD(), tienTo);
                if (so > maxSo) maxSo = so;
            }
        }
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
        return true;
    }
    public boolean lapHoaDonMoi(
            Scanner sc,
            NhanVien nvDangNhap,
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

        int chon = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập lựa chọn (0-2): ");

        switch (chon) {
            case 1: {
                Hoadonban hdb = new Hoadonban();

                boolean ketQua = hdb.nhapThongTin(
                        sc, nvDangNhap, this, dsKH, dsSP, dsKM, dsSPKM
                );

                if (!ketQua) {
                    System.out.println("[Thông báo] Không thể lập hóa đơn bán!");
                    return false;
                }

                return themHD(hdb);
            }

            case 2: {
                HoadonNhap hdn = new HoadonNhap();

                Nguoidung nguoiDungDangNhap = nvDangNhap;

                boolean ketQua = hdn.nhapThongTin(
                        sc, nvDangNhap, this, dsNCC, dsSP, nguoiDungDangNhap
                );

                if (!ketQua) {
                    System.out.println("[Thông báo] Không thể lập hóa đơn nhập!");
                    return false;
                }

                return themHD(hdn);
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
        boolean coHoaDon = false;

        System.out.println("\n========== HÓA ĐƠN NHẬP CHỜ DUYỆT ==========");

        for (HoaDon hd : this.danhSach) {

            if (hd instanceof HoadonNhap
                    && !hd.isDaXuLyThanhCong()
                    && !hd.isDaHuy()) {

                System.out.println("----------------------------------------");
                System.out.println("Mã hóa đơn: " + hd.getMaHD());
                System.out.println("Nhân viên lập: " + hd.getMaNV());
                System.out.println("Ngày lập: " + hd.getNgayLap());
                System.out.println("Trạng thái: " + hd.getTrangThai());

                coHoaDon = true;
            }
        }

        if (!coHoaDon) {
            System.out.println("[Thông báo] Không có hóa đơn nhập chờ duyệt!");
        }
    }

    public void xuatDanhSachHoaDonNhap() {

        boolean coHoaDon = false;

        System.out.println("\n====================== DANH SÁCH HÓA ĐƠN NHẬP ======================");

        System.out.printf("%-5s | %-12s | %-12s | %-12s | %-18s | %-15s%n",
                "STT", "MÃ HÓA ĐƠN", "NGÀY LẬP", "MÃ NV", "TỔNG THANH TOÁN", "TRẠNG THÁI");

        System.out.println("------------------------------------------------------------------------");

        int stt = 1;

        for (HoaDon hd : this.danhSach) {

            if (hd instanceof HoadonNhap) {

                HoadonNhap hdn = (HoadonNhap) hd;

                System.out.printf("%-5d | %-12s | %-12s | %-12s | %-18s | %-15s%n",
                        stt++,
                        hdn.getMaHD(),
                        hdn.getNgayLap(),
                        hdn.getMaNV(),
                        Ktradulieu.dinhDangTien(hdn.tinhThanhTien()),
                        hdn.getTrangThai());

                coHoaDon = true;
            }
        }

        System.out.println("------------------------------------------------------------------------");

        if (!coHoaDon) {
            System.out.println("[Thông báo] Chưa có hóa đơn nhập nào trong hệ thống!");
        } else {
            System.out.println("Tổng số hóa đơn nhập: " + (stt - 1));
        }
    }

    private boolean coQuyenDuyetHoaDon(Nguoidung nguoiDuyet) {
        if (nguoiDuyet == null || nguoiDuyet.isDaXoa()) return false;
        if (!(nguoiDuyet instanceof QuanLyCuaHang) && !(nguoiDuyet instanceof Giamdoc)) return false;

        String[] quyenHan = nguoiDuyet.getQuyenHan();
        if (quyenHan == null) return false;

        for (String quyen : quyenHan) {
            if (quyen != null && "DUYET_HOA_DON".equalsIgnoreCase(quyen.trim())) return true;
        }
        return false;
    }

    public boolean duyetHoaDon(Scanner sc, Nguoidung nguoiDuyet, DSSanPham dsSP) {

        // 1. Kiểm tra dữ liệu đầu vào
        if (sc == null || dsSP == null) {
            System.out.println("[Lỗi] Scanner hoặc danh sách sản phẩm không hợp lệ!");
            return false;
        }

        // 2. Kiểm tra quyền duyệt
        if (!coQuyenDuyetHoaDon(nguoiDuyet)) {
            System.out.println("[Lỗi] Bạn không có quyền duyệt hóa đơn!");
            return false;
        }

        // 3. Lọc hóa đơn nhập đang chờ duyệt
        ArrayList<HoaDon> dsChoDuyet = new ArrayList<>();

        for (HoaDon hd : this.danhSach) {

            if (hd instanceof HoadonNhap
                    && !hd.isDaXuLyThanhCong()
                    && !hd.isDaHuy()) {

                dsChoDuyet.add(hd);
            }
        }

        // 4. Kiểm tra danh sách chờ duyệt
        if (dsChoDuyet.isEmpty()) {
            System.out.println("\n[Thông báo] Hiện không có hóa đơn nhập kho chờ duyệt!");
            return false;
        }

        // 5. Hiển thị danh sách hóa đơn
        System.out.println("\n========== DANH SÁCH HÓA ĐƠN NHẬP CHỜ DUYỆT ==========");

        System.out.printf("%-5s | %-12s | %-12s | %-12s | %-18s%n",
                "STT", "MÃ HÓA ĐƠN", "NGÀY LẬP", "MÃ NV", "TỔNG TIỀN");

        System.out.println("---------------------------------------------------------------");

        for (int i = 0; i < dsChoDuyet.size(); i++) {

            HoaDon hd = dsChoDuyet.get(i);

            System.out.printf("%-5d | %-12s | %-12s | %-12s | %-18s%n",
                    i + 1,
                    hd.getMaHD(),
                    hd.getNgayLap(),
                    hd.getMaNV(),
                    Ktradulieu.dinhDangTien(hd.tinhThanhTien()));
        }

        System.out.println("---------------------------------------------------------------");
        System.out.println("0. Quay lại");

        // 6. Chọn hóa đơn
        int chon;

        while (true) {

            chon = (int) Ktradulieu.nhapSoNguyen(
                    sc, "Chọn hóa đơn cần duyệt (0-" + dsChoDuyet.size() + "): ");

            if (chon >= 0 && chon <= dsChoDuyet.size()) {
                break;
            }

            System.out.println("[Lỗi] Số thứ tự không hợp lệ! Vui lòng chọn lại.");
        }

        if (chon == 0) {
            System.out.println("[Thông báo] Đã quay lại.");
            return false;
        }

        // 7. Lấy hóa đơn được chọn
        HoaDon hd = dsChoDuyet.get(chon - 1);

        HoadonNhap hdNhap = (HoadonNhap) hd;

        System.out.println("\n========== THÔNG TIN HÓA ĐƠN NHẬP ==========");
        System.out.println(hd.inHoaDon());

        // 8. Xác nhận duyệt
        System.out.println("\nBạn có chắc chắn muốn duyệt hóa đơn này không?");
        System.out.println("1. Có, xác nhận duyệt");
        System.out.println("0. Không, hủy thao tác");

        int xacNhan;

        while (true) {

            xacNhan = (int) Ktradulieu.nhapSoNguyen(
                    sc, "Nhập lựa chọn (0-1): ");

            if (xacNhan == 0 || xacNhan == 1) {
                break;
            }

            System.out.println("[Lỗi] Vui lòng chỉ chọn 0 hoặc 1!");
        }

        if (xacNhan == 0) {
            System.out.println("[Thông báo] Hóa đơn vẫn đang chờ duyệt!");
            return false;
        }

        // 9. Thực hiện duyệt và thanh toán
        boolean ketQua = hdNhap.duyetNhapKho(sc, dsSP, nguoiDuyet);

        // 10. Chỉ ghi file khi duyệt và thanh toán thành công
        if (ketQua) {

            ghiFile("data/hoadon.txt");
            dsSP.ghiFile("data/sanpham.txt");
        }

        return ketQua;
    }
    public boolean suaHD(Scanner sc, Nguoidung nguoiThucHien) {
        return suaHD(sc, nguoiThucHien, null);
    }

    // Sửa hóa đơn
    public boolean suaHD(Scanner sc, Nguoidung nguoiThucHien, DSSanPham dsSP) {

        if (sc == null || nguoiThucHien == null
                || nguoiThucHien.isDaXoa()) {
            System.out.println("[Lỗi] Người thực hiện không hợp lệ!");
            return false;
        }

        String maHD = Ktradulieu.nhapChuoi(
                sc,
                "Nhập mã hóa đơn cần sửa: ",
                ".+",
                "Mã không được để trống!"
        );

        HoaDon hd = timKiem(maHD);

        if (hd == null) {
            System.out.println("[Lỗi] Không tìm thấy hóa đơn: " + maHD);
            return false;
        }

        // Kiểm tra trạng thái hóa đơn
        if (hd.isDaXuLyThanhCong() || hd.isDaHuy()) {
            System.out.println("[Lỗi] Hóa đơn đã hoàn tất hoặc đã hủy, không thể sửa!");
            return false;
        }

        // Kiểm tra quyền sửa
        if (hd instanceof HoadonNhap) {
            if (!(nguoiThucHien instanceof NhanVien)
                    && !(nguoiThucHien instanceof QuanLyCuaHang)) {
                System.out.println("[Lỗi] Bạn không có quyền sửa hóa đơn nhập!");
                return false;
            }
        } else {
            if (!(nguoiThucHien instanceof QuanLyCuaHang)
                    && !(nguoiThucHien instanceof Giamdoc)) {
                System.out.println("[Lỗi] Chỉ Quản lý hoặc Giám đốc mới được sửa hóa đơn bán!");
                return false;
            }
        }

        System.out.println("\n========== SỬA HÓA ĐƠN ==========");
        System.out.println("Mã hóa đơn: " + hd.getMaHD());
        System.out.println("Loại hóa đơn: " + hd.getLoaiHD());
        System.out.println("Ngày lập: " + hd.getNgayLap());
        System.out.println("Trạng thái: " + hd.getTrangThai());

        if (hd instanceof HoadonNhap) {

            HoadonNhap hdNhap = (HoadonNhap) hd;

            System.out.println("\n1. Sửa ghi chú hóa đơn");
            System.out.println("2. Sửa ngày lập hóa đơn");
            System.out.println("3. Thêm sản phẩm vào hóa đơn");
            System.out.println("4. Sửa sản phẩm trong hóa đơn");
            System.out.println("5. Xóa sản phẩm khỏi hóa đơn");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(
                    sc, "Nhập lựa chọn (0-5): ", 0, 5
            );

            try {
                switch (chon) {

                    case 1:
                        hd.setGhiChu(Ktradulieu.nhapChuoiTuyChon(
                                sc, "Nhập ghi chú mới: ", ".+", ""
                        ));
                        break;

                    case 2:
                        hd.setNgayLap(Ktradulieu.nhapChuoi(
                                sc,
                                "Nhập ngày lập mới (dd/MM/yyyy): ",
                                Ktradulieu.ngayRegex,
                                "Định dạng ngày phải là dd/MM/yyyy!"
                        ));
                        break;

                    case 3: {
                        if (dsSP == null) {
                            System.out.println("[Lỗi] Danh sách sản phẩm không hợp lệ!");
                            return false;
                        }

                        CTHDonNhap ctMoi = new CTHDonNhap();

                        if (!ctMoi.nhapThongTin(
                                sc, hdNhap.getMaHD(), dsSP, nguoiThucHien)) {
                            System.out.println("[Thông báo] Đã hủy thêm sản phẩm.");
                            return false;
                        }

                        if (!hdNhap.themCTHD(ctMoi)) {
                            return false;
                        }

                        break;
                    }

                    case 4: {
                        if (dsSP == null) {
                            System.out.println("[Lỗi] Danh sách sản phẩm không hợp lệ!");
                            return false;
                        }

                        if (hdNhap.getSoLuongCT() == 0) {
                            System.out.println("[Lỗi] Hóa đơn chưa có sản phẩm!");
                            return false;
                        }

                        System.out.println(hdNhap.inHoaDon());

                        int stt = (int) Ktradulieu.nhapSoNguyen(
                                sc, "Nhập STT sản phẩm cần sửa: "
                        );

                        if (stt < 1 || stt > hdNhap.getSoLuongCT()) {
                            System.out.println("[Lỗi] STT sản phẩm không hợp lệ!");
                            return false;
                        }

                        CTHDonNhap ctMoi = new CTHDonNhap();

                        if (!ctMoi.nhapThongTin(
                                sc, hdNhap.getMaHD(), dsSP, nguoiThucHien)) {
                            System.out.println("[Thông báo] Đã hủy sửa sản phẩm.");
                            return false;
                        }

                        if (!hdNhap.suaCTHD(stt, ctMoi)) {
                            return false;
                        }

                        break;
                    }

                    case 5: {
                        if (hdNhap.getSoLuongCT() == 0) {
                            System.out.println("[Lỗi] Hóa đơn chưa có sản phẩm!");
                            return false;
                        }

                        System.out.println(hdNhap.inHoaDon());

                        int stt = (int) Ktradulieu.nhapSoNguyen(
                                sc, "Nhập STT sản phẩm cần xóa: "
                        );

                        if (!hdNhap.xoaCTHD(stt)) {
                            return false;
                        }

                        break;
                    }

                    case 0:
                        System.out.println("[Thông báo] Đã hủy thao tác sửa.");
                        return false;
                }

                ghiFile("data/hoadon.txt");

                System.out.println("[Thành công] Đã cập nhật hóa đơn!");
                System.out.println(hd.inHoaDon());

                return true;

            } catch (Exception e) {
                System.out.println("[Lỗi] " + e.getMessage());
                return false;
            }

        } else {

            // Chức năng sửa hóa đơn bán
            System.out.println("\n1. Sửa ghi chú hóa đơn");
            System.out.println("2. Sửa ngày lập hóa đơn");
            System.out.println("0. Quay lại");

            int chon = Ktradulieu.luaChon(
                    sc, "Nhập lựa chọn (0-2): ", 0, 2
            );

            try {
                switch (chon) {

                    case 1:
                        hd.setGhiChu(Ktradulieu.nhapChuoiTuyChon(
                                sc, "Nhập ghi chú mới: ", ".+", ""
                        ));
                        break;

                    case 2:
                        hd.setNgayLap(Ktradulieu.nhapChuoi(
                                sc,
                                "Nhập ngày lập mới (dd/MM/yyyy): ",
                                Ktradulieu.ngayRegex,
                                "Định dạng ngày phải là dd/MM/yyyy!"
                        ));
                        break;

                    case 0:
                        System.out.println("[Thông báo] Đã hủy thao tác sửa.");
                        return false;
                }

                ghiFile("data/hoadon.txt");

                System.out.println("[Thành công] Đã cập nhật hóa đơn!");
                System.out.println(hd.inHoaDon());

                return true;

            } catch (Exception e) {
                System.out.println("[Lỗi] " + e.getMessage());
                return false;
            }
        }
    }

    public boolean xoaHD(Scanner sc, Nguoidung nguoiThucHien) {

        String maHD = Ktradulieu.nhapChuoi(
                sc, "Nhập mã hóa đơn cần hủy: ",
                ".+", "Mã không được để trống!");

        int viTri = timViTri(maHD);

        if (viTri == -1) {
            System.out.println(
                    "[Lỗi] Không tìm thấy hóa đơn có mã: " + maHD);
            return false;
        }

        HoaDon hd = this.danhSach.get(viTri);

        // Kiểm tra trạng thái và quyền theo loại hóa đơn
        if (hd instanceof HoadonNhap) {

            // Chỉ được hủy hóa đơn nhập đang xử lý
            if (!"DangXuLy".equals(hd.getTrangThai())) {
                System.out.println(
                        "[Lỗi] Chỉ được hủy hóa đơn nhập đang trong quá trình xử lý!");
                return false;
            }

            // Nhân viên kho hoặc Quản lý cửa hàng
            if (!(nguoiThucHien instanceof NhanVien)
                    && !(nguoiThucHien instanceof QuanLyCuaHang)) {

                System.out.println("[Lỗi] Bạn không có quyền hủy hóa đơn nhập!");
                return false;
            }

        } else {

            // Hóa đơn bán giữ nguyên quyền cũ
            if (hd.isDaXuLyThanhCong()) {
                System.out.println(
                        "[Lỗi] Không thể hủy hóa đơn đã xử lý thành công!");
                return false;
            }

            if (hd.isDaHuy()) {
                System.out.println(
                        "[Lỗi] Hóa đơn đã được hủy trước đó!");
                return false;
            }

            if (!(nguoiThucHien instanceof QuanLyCuaHang)
                    && !(nguoiThucHien instanceof Giamdoc)) {

                System.out.println(
                        "[Lỗi] Chỉ Quản lý hoặc Giám đốc mới được hủy hóa đơn bán!");
                return false;
            }
        }

        hd.huyHoaDon();
        ghiFile("data/hoadon.txt");

        System.out.println(
                "[Thành công] Đã hủy hóa đơn: " + maHD);

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
                new OutputStreamWriter(
                        new FileOutputStream(filePath),
                        StandardCharsets.UTF_8))) {

            for (HoaDon hd : this.danhSach) {
                if (hd == null) {
                    continue;
                }

                // Lưu thông tin chung của hóa đơn
                bw.write(hd.toFileString());
                bw.newLine();

                // Lưu chi tiết hóa đơn nhập
                if (hd instanceof HoadonNhap) {
                    HoadonNhap hdn = (HoadonNhap) hd;

                    for (CTHDonNhap ct : hdn.getDsChiTiet()) {
                        if (ct != null) {
                            bw.write("CTNHAP;" + ct.toDongFile());
                            bw.newLine();
                        }
                    }
                } else if (hd instanceof Hoadonban) {
                    Hoadonban hdb = (Hoadonban) hd;

                    for (CTHDBan ct : hdb.getDsChiTiet()) {
                        if (ct != null) {
                            bw.write("CTBAN;" + ct.toDongFile());
                            bw.newLine();
                        }
                    }
                }
            }

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

        ArrayList<HoaDon> danhSachTam = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {

            String dong;
            int dem = 0;
            int loi = 0;

            while ((dong = br.readLine()) != null) {
                dong = dong.trim();

                if (dong.isEmpty() || dong.startsWith("#")) {
                    continue;
                }

                try {
                    // 1. ĐỌC CHI TIẾT HÓA ĐƠN NHẬP
                    if (dong.startsWith("CTNHAP;")) {

                        String duLieuChiTiet =
                                dong.substring("CTNHAP;".length());

                        CTHDonNhap ct =
                                CTHDonNhap.tuDongFile(duLieuChiTiet);

                        if (ct == null) {
                            loi++;
                            continue;
                        }

                        HoadonNhap hdnTimDuoc = null;

                        for (HoaDon hdCu : danhSachTam) {
                            if (hdCu instanceof HoadonNhap
                                    && hdCu.getMaHD().equalsIgnoreCase(ct.getMaHD())) {

                                hdnTimDuoc = (HoadonNhap) hdCu;
                                break;
                            }
                        }

                        if (hdnTimDuoc == null
                                || !hdnTimDuoc.themCTHDTuFile(ct)) {
                            loi++;
                            continue;
                        }

                        continue;
                    }

                    // 2. ĐỌC CHI TIẾT HÓA ĐƠN BÁN
                    if (dong.startsWith("CTBAN;")) {

                        String duLieuChiTiet =
                                dong.substring("CTBAN;".length());

                        CTHDBan ct =
                                CTHDBan.tuDongFile(duLieuChiTiet);

                        if (ct == null) {
                            loi++;
                            continue;
                        }

                        Hoadonban hdbTimDuoc = null;

                        for (HoaDon hdCu : danhSachTam) {
                            if (hdCu instanceof Hoadonban
                                    && hdCu.getMaHD().equalsIgnoreCase(ct.getMaHD())) {

                                hdbTimDuoc = (Hoadonban) hdCu;
                                break;
                            }
                        }

                        if (hdbTimDuoc == null
                                || !hdbTimDuoc.themCTHDTuFile(ct)) {
                            loi++;
                            continue;
                        }

                        continue;
                    }

                    // 3. ĐỌC THÔNG TIN HÓA ĐƠN CHÍNH
                    String[] p = dong.split(";", -1);

                    if (p.length < 6) {
                        loi++;
                        continue;
                    }

                    String loai = p[0].trim();
                    String maHD = p[1].trim();
                    String ngayLap = p[2].trim();
                    String maNV = p[3].trim();
                    String maDoiTac = p[4].trim();
                    String trangThai = p[5].trim();

                    if (maHD.isEmpty() || maNV.isEmpty()) {
                        loi++;
                        continue;
                    }

                    boolean trungMa = false;

                    for (HoaDon hdCu : danhSachTam) {
                        if (hdCu.getMaHD().equalsIgnoreCase(maHD)) {
                            trungMa = true;
                            break;
                        }
                    }

                    if (trungMa) {
                        loi++;
                        continue;
                    }

                    String ghiChu = "";

                    if ("HDBAN".equalsIgnoreCase(loai)) {
                        ghiChu = (p.length > 12) ? p[12].trim() : "";
                    } else if ("HDNHAP".equalsIgnoreCase(loai)) {
                        ghiChu = (p.length > 10) ? p[10].trim() : "";
                    } else {
                        loi++;
                        continue;
                    }

                    NhanVien nv = (dsNV != null)
                            ? dsNV.timNhanVienTheoMa(maNV) : null;

                    if (nv == null) {
                        nv = new NhanVien();
                        nv.setMaND(maNV);
                        nv.setTenND("Nhân viên " + maNV);
                    }

                    HoaDon hd;

                    if ("HDBAN".equalsIgnoreCase(loai)) {
                        hd = new Hoadonban(
                                maHD, nv, ngayLap, trangThai, ghiChu, maDoiTac);
                    } else {
                        hd = new HoadonNhap(
                                maHD, nv, ngayLap, trangThai, ghiChu, maDoiTac);
                    }

                    danhSachTam.add(hd);
                    dem++;

                } catch (Exception e) {
                    loi++;
                    System.out.println(
                            "[Cảnh báo] Bỏ qua một dòng hóa đơn không hợp lệ: "
                                    + e.getMessage());
                }
            }

            this.danhSach.clear();
            this.danhSach.addAll(danhSachTam);

            System.out.println("[Thành công] Đã nạp " + dem + " hóa đơn từ file.");

            if (loi > 0) {
                System.out.println(
                        "[Cảnh báo] Có " + loi + " dòng hóa đơn không đọc được.");
            }

        } catch (IOException e) {
            System.out.println("[Lỗi] Đọc file hóa đơn: " + e.getMessage());
        }
    }
}