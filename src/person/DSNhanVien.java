package person;

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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import util.Ktradulieu;

public class DSNhanVien {
    private List<Nguoidung> danhSach;

    public DSNhanVien() {
        this.danhSach = new ArrayList<>();
    }

    public List<Nguoidung> getDanhSach() {
        return Collections.unmodifiableList(danhSach);
    }

    public List<NhanVien> getDanhSachNhanVien() {
        List<NhanVien> ketQua = new ArrayList<>();
        for (Nguoidung nd : danhSach) {
            if (nd instanceof NhanVien) {
                ketQua.add((NhanVien) nd);
            }
        }
        return Collections.unmodifiableList(ketQua);
    }

    public int getSoLuong() {
        return danhSach.size();
    }

    public Nguoidung get(int index) {
        if (index < 0 || index >= danhSach.size()) return null;
        return danhSach.get(index);
    }

    public String sinhMaTuDong(String tienTo) {
        int maxSo = 0;
        for (Nguoidung nd : danhSach) {
            int so = Ktradulieu.laySoTuMa(nd.getMaND(), tienTo);
            if (so > maxSo) {
                maxSo = so;
            }
        }
        return Ktradulieu.taoMa(tienTo + "%03d", maxSo + 1);
    }

    // =========================================================
    // THÊM
    // =========================================================
    public boolean them(Nguoidung nd) {
        if (nd == null) return false;
        if (nd.getMaND() == null || nd.getMaND().trim().isEmpty()
                || nd.getTaiKhoan() == null || nd.getTaiKhoan().trim().isEmpty()) {
            System.out.println("Tài khoản chưa đủ thông tin (thiếu mã hoặc tên đăng nhập)!");
            return false;
        }
        if (timKiemTheoMa(nd.getMaND()) != null) {
            System.out.println("Mã người dùng " + nd.getMaND() + " đã tồn tại!");
            return false;
        }
        if (timKiemTheoTaiKhoan(nd.getTaiKhoan()) != null) {
            System.out.println("Tên đăng nhập '" + nd.getTaiKhoan() + "' đã có người sử dụng!");
            return false;
        }
        danhSach.add(nd);
        ghiFile("data/nhanvien.txt");

        System.out.println("Đã thêm [" + nd.getVaiTro() + "] [" + nd.getMaND() + "] " + nd.getTenND() + " vào hệ thống.");
        return true;
    }

    public boolean themNV(NhanVien nv) {
        return them(nv);
    }

    /** Thêm qua nghiệp vụ: Quản lý chỉ được tạo nhân viên thường. */
    public boolean them(Nguoidung nguoiThucHien, Nguoidung taiKhoanMoi) {
        if (nguoiThucHien == null || taiKhoanMoi == null
                || !nguoiThucHien.coQuyen("QUAN_TRI_TAI_KHOAN")) {
            System.out.println("[Từ chối] Chỉ Admin được thêm nhân sự!");
            return false;
        }
        return them(taiKhoanMoi);
    }

    public boolean doiTrangThaiTaiKhoan(Nguoidung nguoiThucHien, String maND, boolean khoa) {
        if (nguoiThucHien == null || !nguoiThucHien.coQuyen("QUAN_TRI_TAI_KHOAN")) {
            System.out.println("[Từ chối] Chỉ Admin đang hoạt động được khóa/mở khóa tài khoản!");
            return false;
        }
        Nguoidung nd = timKiemTheoMa(maND);
        if (nd == null) {
            System.out.println("[Lỗi] Không tìm thấy tài khoản: " + maND);
            return false;
        }
        if (khoa && nd.getMaND().equalsIgnoreCase(nguoiThucHien.getMaND())) {
            System.out.println("[Từ chối] Không thể tự khóa tài khoản đang đăng nhập!");
            return false;
        }
        nd.setDaXoa(khoa);
        if (nd instanceof NhanVien) ((NhanVien) nd).setYeuCauSaThai(null);
        ghiFile("data/nhanvien.txt");
        System.out.println("Đã " + (khoa ? "khóa" : "mở khóa") + " tài khoản [" + nd.getMaND() + "].");
        return true;
    }

    // =========================================================
    // TÌM KIẾM
    // =========================================================

    /** Tìm CHÍNH XÁC theo mã (không phân biệt hoa thường), mọi vai trò. */
    public Nguoidung timKiemTheoMa(String ma) {
        if (ma == null || ma.trim().isEmpty()) return null;
        String key = ma.trim();
        for (Nguoidung nd : danhSach) {
            if (nd.getMaND() != null && nd.getMaND().equalsIgnoreCase(key)) {
                return nd;
            }
        }
        return null;
    }

    /** Tìm CHÍNH XÁC theo mã, chỉ trả về nếu tài khoản đó là nhân viên (NhanVien). */
    public NhanVien timNhanVienTheoMa(String ma) {
        Nguoidung nd = timKiemTheoMa(ma);
        return (nd instanceof NhanVien) ? (NhanVien) nd : null;
    }

    /** Tìm để tra cứu: ưu tiên trùng mã chính xác, nếu không có mới tìm tên chứa từ khóa. */
    public Nguoidung timKiem(String maHoacTen) {
        if (maHoacTen == null || maHoacTen.trim().isEmpty()) return null;
        Nguoidung theoMa = timKiemTheoMa(maHoacTen);
        if (theoMa != null) return theoMa;

        String key = maHoacTen.trim().toLowerCase();
        for (Nguoidung nd : danhSach) {
            if (nd.getTenND() != null && nd.getTenND().toLowerCase().contains(key)) {
                return nd;
            }
        }
        return null;
    }

    public Nguoidung timKiemTheoTaiKhoan(String taiKhoan) {
        if (taiKhoan == null || taiKhoan.trim().isEmpty()) return null;
        for (Nguoidung nd : danhSach) {
            if (nd.getTaiKhoan() != null && nd.getTaiKhoan().equalsIgnoreCase(taiKhoan.trim())) {
                return nd;
            }
        }
        return null;
    }

    // =========================================================
    // KIỂM TRA QUYỀN NGƯỜI THỰC HIỆN
    // =========================================================
    private boolean taiKhoanConHoatDong(Nguoidung nd) {
        return nd != null && !nd.isDaXoa();
    }

    private boolean laQuanLyHoacAdmin(Nguoidung nd) {
        return nd != null && nd.coQuyen("QUAN_LY_NHAN_VIEN");
    }

    // =========================================================
    // XÓA / SA THẢI
    // =========================================================
    public boolean xoa(Scanner sc, Nguoidung nguoiThucHien, String maNV) {
        if (!taiKhoanConHoatDong(nguoiThucHien)) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Tài khoản không hợp lệ hoặc đã bị vô hiệu hóa!");
            return false;
        }
        if (!nguoiThucHien.coQuyen("QUAN_TRI_TAI_KHOAN")) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Bạn không có quyền hạn đối với tác vụ này!");
            return false;
        }

        NhanVien nv = timNhanVienTheoMa(maNV);
        if (nv == null) {
            if (timKiemTheoMa(maNV) != null) {
                System.out.println("Mã [" + maNV + "] không phải tài khoản nhân viên, không thể sa thải theo thủ tục này!");
            } else {
                System.out.println("Không tìm thấy nhân viên có mã: " + maNV);
            }
            return false;
        }
        if (nv.isDaXoa()) {
            System.out.println("Nhân viên [" + maNV + "] đã nghỉ việc từ trước!");
            return false;
        }

        nv.setDaXoa(true);
        nv.setYeuCauSaThai(null);
        ghiFile("data/nhanvien.txt");
        System.out.println("[Thành công] Đã xóa nhân viên [" + maNV + "] " + nv.getTenND()
                + ". Lịch sử hóa đơn vẫn được giữ lại.");
        return true;
    }

    // =========================================================
    // SỬA
    // =========================================================
    public void sua(Scanner sc, Nguoidung nguoiThucHien, String maNV) {
        if (!taiKhoanConHoatDong(nguoiThucHien) || !nguoiThucHien.coQuyen("QUAN_TRI_TAI_KHOAN")) {
            System.out.println("[Từ chối] Chỉ Admin đang hoạt động được cập nhật nhân sự!");
            return;
        }
        Nguoidung nd = timKiemTheoMa(maNV);
        boolean laAdmin = nguoiThucHien.coQuyen("QUAN_TRI_TAI_KHOAN");
        if (nd == null || (!laAdmin && (!(nd instanceof NhanVien) || nd.isDaXoa()))) {
            System.out.println("[Từ chối] Quản lý chỉ được sửa nhân viên thường đang làm việc; kiểm tra lại mã tài khoản!");
            return;
        }
        if (sc == null) return;
        while (true) {
            System.out.println("\n--- CẬP NHẬT TÀI KHOẢN [" + nd.getMaND() + "] ---");
            System.out.println("1. Sửa họ tên\n2. Sửa số điện thoại");
            if (laAdmin) System.out.println("3. Đặt lại mật khẩu\n8. Sửa tên đăng nhập");
            if (nd instanceof NhanVien) {
                System.out.println("4. Sửa lương cơ bản\n5. Sửa ngày vào làm");
            } else if (nd instanceof QuanLyCuaHang && !(nd instanceof Admin)) {
                System.out.println("6. Sửa khu vực quản lý\n7. Sửa hệ số trách nhiệm");
            }
            System.out.println("0. Hoàn tất");
            int chon = Ktradulieu.luaChon(sc, "Chọn (0-8): ", 0, 8);
            try {
                switch (chon) {
                    case 1:
                        nd.setTenND(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập họ tên mới: "));
                        break;
                    case 2:
                        nd.setSoDienThoai(Ktradulieu.nhapChuoi(sc, "Nhập số điện thoại mới: ",
                                Ktradulieu.phoneRegex, "Số điện thoại không hợp lệ!"));
                        break;
                    case 3:
                        if (!laAdmin) { System.out.println("[Từ chối] Chỉ Admin được đặt lại mật khẩu!"); break; }
                        nd.setMatKhau(Ktradulieu.nhapChuoi(sc, "Nhập mật khẩu mới: ",
                                "[^;]{4,}", "Mật khẩu tối thiểu 4 ký tự và không chứa ';'!"));
                        break;
                    case 4:
                        if (!(nd instanceof NhanVien)) { System.out.println("[Lỗi] Chỉ áp dụng cho nhân viên!"); break; }
                        ((NhanVien) nd).setLuongCoBan(Ktradulieu.nhapSoThucKhongAm(sc, "Nhập lương cơ bản: "));
                        break;
                    case 5:
                        if (!(nd instanceof NhanVien)) { System.out.println("[Lỗi] Chỉ áp dụng cho nhân viên!"); break; }
                        ((NhanVien) nd).setNgayVaoLam(Ktradulieu.nhapNgay(sc, "Nhập ngày vào làm (dd/MM/yyyy): "));
                        break;
                    case 6:
                    case 7:
                        if (!laAdmin || !(nd instanceof QuanLyCuaHang) || nd instanceof Admin) {
                            System.out.println("[Từ chối] Chỉ Admin được cập nhật thông tin Quản lý!");
                            break;
                        }
                        QuanLyCuaHang ql = (QuanLyCuaHang) nd;
                        if (chon == 6) ql.setKhuVucQuanLy(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập khu vực quản lý: "));
                        else ql.setHeSoTrachNhiem(Ktradulieu.nhapSoThucKhongAm(sc, "Nhập hệ số trách nhiệm (> 0): "));
                        break;
                    case 8:
                        if (!laAdmin) { System.out.println("[Từ chối] Chỉ Admin được đổi tên đăng nhập!"); break; }
                        String tk = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên đăng nhập mới: ");
                        Nguoidung trung = timKiemTheoTaiKhoan(tk);
                        if (trung != null && trung != nd) { System.out.println("[Lỗi] Tên đăng nhập đã tồn tại!"); break; }
                        nd.setTaiKhoan(tk);
                        break;
                    case 0:
                        ghiFile("data/nhanvien.txt");
                        return;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("[Lỗi] " + e.getMessage());
            }
        }
    }

    public void xuatDanhSach() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        String xuongDong = System.lineSeparator();
        return xuongDong + "===== DANH SÁCH TÀI KHOẢN CỬA HÀNG =====" + xuongDong
                + BangConsole.tuDanhSach(danhSach) + xuongDong
                + "Tổng số tài khoản: " + danhSach.size();
    }

    // =========================================================
    // GHI FILE (UTF-8, ghi ra file tạm rồi mới thay file thật để không bị cụt dữ liệu khi lỗi)
    // =========================================================
    public boolean ghiFile(String duongDan) {
        String dichGhi = Ktradulieu.duongDanGhi(duongDan);
        File file = new File(dichGhi);
        File tam = Ktradulieu.chuanBiFileTam(dichGhi);

        try {
            try (BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(tam), StandardCharsets.UTF_8))) {
                for (Nguoidung nd : danhSach) {
                    bw.write(nd.toFileString());
                    bw.newLine();
                }
            }
            Files.move(tam.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Đã lưu danh sách tài khoản vào file: " + dichGhi);
            return true;
        } catch (IOException | RuntimeException e) {
            System.out.println("Lỗi ghi file tài khoản: " + e.getMessage());
            tam.delete();
            return false;
        }
    }

    // =========================================================
    // ĐỌC FILE (đọc vào danh sách tạm, đọc xong toàn bộ mới thay thế)
    // =========================================================
    public boolean docFile(String duongDan) {
        File file = new File(duongDan);
        if (!file.exists()) {
            System.out.println("Chưa có file dữ liệu tài khoản tại: " + duongDan);
            return false;
        }

        List<Nguoidung> tam = new ArrayList<>();
        int soDong = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String dong;
            while ((dong = br.readLine()) != null) {
                soDong++;
                dong = dong.trim();
                if (dong.isEmpty() || dong.startsWith("#")) continue;

                Nguoidung nd = phanTichDong(dong);

                for (Nguoidung da : tam) {
                    if (da.getMaND().equalsIgnoreCase(nd.getMaND())) {
                        throw new IllegalArgumentException("Mã người dùng bị trùng: " + nd.getMaND());
                    }
                    if (da.getTaiKhoan().equalsIgnoreCase(nd.getTaiKhoan())) {
                        throw new IllegalArgumentException("Tên đăng nhập bị trùng: " + nd.getTaiKhoan());
                    }
                }
                tam.add(nd);
            }
        } catch (Exception e) {
            System.out.println("Lỗi đọc file tài khoản tại dòng " + soDong + ": " + e.getMessage());
            System.out.println("Danh sách hiện tại được giữ nguyên, không có dữ liệu nào bị thay thế.");
            Ktradulieu.danhDauFileLoi(duongDan, "Dòng " + soDong + ": " + e.getMessage());
            return false;
        }

        this.danhSach.clear();
        this.danhSach.addAll(tam);
        System.out.println("Đã đọc thành công " + danhSach.size() + " tài khoản từ file: " + duongDan);
        return true;
    }

    // Sao lưu file bị lỗi để dù có lưu đè sau đó vẫn còn bản gốc để sửa tay
    private void saoLuuFileLoi(File file) {
        try {
            File dich = new File(file.getPath() + ".loi.bak");
            Files.copy(file.toPath(), Paths.get(dich.getPath()), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Đã sao lưu file lỗi sang: " + dich.getPath());
        } catch (IOException e) {
            System.out.println("Không thể sao lưu file lỗi: " + e.getMessage());
        }
    }

    // Định dạng: NHANVIEN;... | QUANLY;... | ADMIN;... (vẫn đọc tài khoản cũ).
    private Nguoidung phanTichDong(String dong) {
        String[] p = dong.split(";", -1);
        String loai = p[0].trim().toUpperCase();

        switch (loai) {
            case "NHANVIEN":
                return phanTichNhanVien(p, 1);
            case "QUANLY":
                return phanTichQuanLy(p, 1);
            case "ADMIN":
            case "GIAMDOC": // Chuyển vai trò cũ sang Admin, giữ nguyên mã và thông tin đăng nhập.
                return phanTichAdmin(p, 1);
            default:
                // Định dạng cũ: dòng bắt đầu bằng mã -> đoán vai trò theo tiền tố mã
                if (loai.startsWith("AD") || loai.startsWith("GD")) return phanTichAdmin(p, 0);
                if (loai.startsWith("QL")) return phanTichQuanLy(p, 0);
                return phanTichNhanVien(p, 0);
        }
    }

    private static double docSo(String s) {
        return Double.parseDouble(s.trim().replace(',', '.'));
    }

    private NhanVien phanTichNhanVien(String[] p, int o) {
        // maND;tenND;taiKhoan;matKhau;sdt;vaiTro;ngayVaoLam;[luong];[yeuCauSaThai];daXoa
        if (p.length < o + 8) {
            throw new IllegalArgumentException("Dòng nhân viên thiếu trường dữ liệu!");
        }
        int cuoi = p.length - 1;
        LocalDate ngayVao = LocalDate.parse(p[o + 6].trim());
        boolean daXoa = Boolean.parseBoolean(p[cuoi].trim());

        NhanVien nv = new NhanVien(p[o].trim(), p[o + 1].trim(), p[o + 2].trim(), p[o + 3].trim(),
                p[o + 4].trim(), ngayVao, daXoa);

        if (o + 7 < cuoi) {
            nv.setLuongCoBan(docSo(p[o + 7]));
        }
        if (o + 8 < cuoi && !p[o + 8].trim().equalsIgnoreCase("KHONG") && !p[o + 8].trim().isEmpty()) {
            nv.setYeuCauSaThai(p[o + 8].trim());
        }
        return nv;
    }

    private QuanLyCuaHang phanTichQuanLy(String[] p, int o) {
        // maND;tenND;taiKhoan;matKhau;sdt;vaiTro;daXoa;khuVuc;heSo
        if (p.length < o + 9) {
            throw new IllegalArgumentException("Dòng quản lý thiếu trường dữ liệu!");
        }
        return new QuanLyCuaHang(p[o].trim(), p[o + 1].trim(), p[o + 2].trim(), p[o + 3].trim(),
                p[o + 4].trim(), p[o + 5].trim(), Boolean.parseBoolean(p[o + 6].trim()),
                p[o + 7].trim(), docSo(p[o + 8]));
    }

    private Admin phanTichAdmin(String[] p, int o) {
        // maND;tenND;taiKhoan;matKhau;sdt;vaiTro;daXoa
        // Hai trường phụ cấp/cổ phần ở định dạng cũ không còn dùng cho Admin.
        if (p.length < o + 7) {
            throw new IllegalArgumentException("Dòng Admin thiếu trường dữ liệu!");
        }
        return new Admin(p[o].trim(), p[o + 1].trim(), p[o + 2].trim(), p[o + 3].trim(),
                p[o + 4].trim(), Boolean.parseBoolean(p[o + 6].trim()));
    }
    // =========================================================
    public List<NhanVien> getDanhSachYeuCauSaThai() {
        List<NhanVien> ketQua = new ArrayList<>();

        for (Nguoidung nd : danhSach) {
            if (nd instanceof NhanVien) {
                NhanVien nv = (NhanVien) nd;

                if (!nv.isDaXoa()
                        && nv.getYeuCauSaThai() != null
                        && !nv.getYeuCauSaThai().trim().isEmpty()) {
                    ketQua.add(nv);
                }
            }
        }

        return ketQua;
    }

    // =========================================================
// Admin PHÊ DUYỆT / TỪ CHỐI YÊU CẦU SA THẢI
// =========================================================
    public boolean xuLyYeuCauSaThai(
            Nguoidung nguoiThucHien,
            String maNV,
            boolean chapNhan) {

        if (!(nguoiThucHien instanceof Admin)
                || !taiKhoanConHoatDong(nguoiThucHien)) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Chỉ Admin đang hoạt động mới được xử lý yêu cầu!");
            return false;
        }

        NhanVien nv = timNhanVienTheoMa(maNV);

        if (nv == null || nv.isDaXoa()) {
            System.out.println("[Lỗi] Không tìm thấy nhân viên hợp lệ!");
            return false;
        }

        String lyDo = nv.getYeuCauSaThai();

        if (lyDo == null || lyDo.trim().isEmpty()) {
            System.out.println("[Thông báo] Nhân viên này không có yêu cầu sa thải đang chờ!");
            return false;
        }

        if (chapNhan) {
            nv.setDaXoa(true);
            System.out.println("[Admin ĐÃ PHÊ DUYỆT] Nhân viên "
                    + nv.getTenND() + " [" + nv.getMaND()
                    + "] đã được sa thải.");
        } else {
            System.out.println("[Admin ĐÃ TỪ CHỐI] Yêu cầu sa thải nhân viên "
                    + nv.getTenND() + " [" + nv.getMaND()
                    + "] không được chấp thuận.");
        }

        nv.setYeuCauSaThai(null);
        ghiFile("data/nhanvien.txt");
        return true;
    }
}
