package person;

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

/**
 * Danh sách tài khoản của hệ thống. Chứa MỌI vai trò (NhanVien, QuanLyCuaHang, Giamdoc)
 * nên mã tự sinh, kiểm tra trùng mã / trùng tài khoản và việc lưu - đọc file đều áp dụng chung.
 */
public class DSNhanVien {
    private List<Nguoidung> danhSach;

    public DSNhanVien() {
        this.danhSach = new ArrayList<>();
    }

    /** Danh sách chỉ đọc của tất cả tài khoản. Muốn thêm / xóa phải đi qua them(), xoa()... */
    public List<Nguoidung> getDanhSach() {
        return Collections.unmodifiableList(danhSach);
    }

    /** Bản sao chỉ gồm các tài khoản nhân viên (NhanVien). */
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

    private boolean laQuanLyHoacGiamDoc(Nguoidung nd) {
        return nd instanceof QuanLyCuaHang || nd instanceof Giamdoc;
    }

    // =========================================================
    // XÓA / SA THẢI
    // =========================================================
    public boolean xoa(Scanner sc, Nguoidung nguoiThucHien, String maNV) {
        if (!taiKhoanConHoatDong(nguoiThucHien)) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Tài khoản không hợp lệ hoặc đã bị vô hiệu hóa!");
            return false;
        }
        if (!laQuanLyHoacGiamDoc(nguoiThucHien)) {
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

        if (nguoiThucHien instanceof Giamdoc) {
            nv.setDaXoa(true);
            nv.setYeuCauSaThai(null);
            ghiFile("data/nhanvien.txt");

            System.out.println("[GIÁM ĐỐC PHÊ DUYỆT] Đã sa thải nhân viên [" + maNV + "] " + nv.getTenND() + ". (Dữ liệu cũ vẫn được bảo lưu).");
            return true;
        }

        // Quản lý: chỉ được gửi đề xuất
        System.out.println("QUẢN LÝ KHÔNG CÓ QUYỀN SA THẢI! Bạn chỉ có thể gửi đề xuất lên Giám Đốc.");
        if (sc == null) {
            System.out.println("[Lỗi] Không có nguồn nhập liệu để ghi lý do đề xuất!");
            return false;
        }
        String lyDo = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập lý do đề xuất sa thải: ");
        nv.setYeuCauSaThai(lyDo);
        ghiFile("data/nhanvien.txt");

        System.out.println(" Đã gửi đề xuất sa thải nhân viên [" + maNV + "] lên Giám Đốc phê duyệt.");
        return false;
    }

    // =========================================================
    // SỬA
    // =========================================================
    public void sua(Scanner sc, Nguoidung nguoiThucHien, String maNV) {
        if (!taiKhoanConHoatDong(nguoiThucHien)) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Tài khoản không hợp lệ hoặc đã bị vô hiệu hóa!");
            return;
        }
        if (!laQuanLyHoacGiamDoc(nguoiThucHien)) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Chỉ Quản Lý và Giám Đốc mới có quyền chỉnh sửa nhân viên!");
            return;
        }
        if (sc == null) {
            System.out.println("[Lỗi] Không có nguồn nhập liệu!");
            return;
        }

        NhanVien nv = timNhanVienTheoMa(maNV);
        if (nv == null) {
            System.out.println("Không tìm thấy nhân viên có mã: " + maNV);
            return;
        }
        if (nv.isDaXoa()) {
            System.out.println("Nhân viên [" + maNV + "] đã nghỉ việc, không thể chỉnh sửa!");
            return;
        }

        while (true) {
            System.out.println("\n--- CHỌN TRƯỜNG CẦN SỬA CHO NHÂN VIÊN [" + nv.getMaND() + "] ---");
            System.out.println("1. Sửa Họ và Tên");
            System.out.println("2. Sửa Số điện thoại");
            System.out.println("3. Sửa Mật khẩu");
            System.out.println("4. Sửa Lương cơ bản");
            System.out.println("5. Sửa Ngày vào làm (dd/MM/yyyy)");
            System.out.println("0. Hoàn tất sửa");
            int chon = Ktradulieu.luaChon(sc, "Nhập lựa chọn của bạn: ", 0, 5);

            try {
                switch (chon) {
                    case 1:
                        String tenMoi = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập họ tên mới: ");
                        nv.setTenND(tenMoi);
                        System.out.println("Đã cập nhật họ tên thành công!");
                        break;
                    case 2:
                        String sdtMoi = Ktradulieu.nhapChuoi(sc, "Nhập số điện thoại mới: ", Ktradulieu.phoneRegex, "SĐT không hợp lệ!");
                        nv.setSoDienThoai(sdtMoi);
                        System.out.println("Đã cập nhật SĐT!");
                        break;
                    case 3:
                        String mkMoi = Ktradulieu.nhapChuoi(sc, "Nhập mật khẩu mới: ", "[^;]{4,}", "Mật khẩu tối thiểu 4 ký tự và không chứa dấu ';'!");
                        nv.setMatKhau(mkMoi);
                        System.out.println("Đã cập nhật mật khẩu!");
                        break;
                    case 4:
                        double luongMoi = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập mức lương cơ bản mới: ");
                        nv.setLuongCoBan(luongMoi);
                        System.out.println("Đã cập nhật mức lương cơ bản!");
                        break;
                    case 5:
                        LocalDate ngayMoi = Ktradulieu.nhapNgay(sc, "Nhập ngày vào làm mới (dd/MM/yyyy): ");
                        nv.setNgayVaoLam(ngayMoi);
                        System.out.println("Đã cập nhật ngày vào làm!");
                        break;
                    case 0:
                        ghiFile("data/nhanvien.txt");
                        return;
                }
            } catch (Exception e) {
                System.out.println("Lỗi cập nhật: " + e.getMessage());
            }
        }
    }

    public void xuatDanhSach() {
        System.out.println("\n================ DANH SÁCH TÀI KHOẢN CỬA HÀNG ================");
        for (Nguoidung nd : danhSach) {
            System.out.println(nd.toString());
        }
        System.out.println("Tổng số tài khoản: " + danhSach.size());
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
            Ktradulieu.hoanTatGhi(tam, dichGhi);
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

    // Định dạng: NHANVIEN;... | QUANLY;... | GIAMDOC;...  (dòng cũ không có tiền tố vẫn đọc được)
    private Nguoidung phanTichDong(String dong) {
        String[] p = dong.split(";", -1);
        String loai = p[0].trim().toUpperCase();

        switch (loai) {
            case "NHANVIEN":
                return phanTichNhanVien(p, 1);
            case "QUANLY":
                return phanTichQuanLy(p, 1);
            case "GIAMDOC":
                return phanTichGiamDoc(p, 1);
            default:
                // Định dạng cũ: dòng bắt đầu bằng mã -> đoán vai trò theo tiền tố mã
                if (loai.startsWith("GD")) return phanTichGiamDoc(p, 0);
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

    private Giamdoc phanTichGiamDoc(String[] p, int o) {
        // maND;tenND;taiKhoan;matKhau;sdt;vaiTro;daXoa;phuCap;tyLeCoPhan
        if (p.length < o + 9) {
            throw new IllegalArgumentException("Dòng giám đốc thiếu trường dữ liệu!");
        }
        return new Giamdoc(p[o].trim(), p[o + 1].trim(), p[o + 2].trim(), p[o + 3].trim(),
                p[o + 4].trim(), p[o + 5].trim(), Boolean.parseBoolean(p[o + 6].trim()),
                docSo(p[o + 7]), docSo(p[o + 8]));
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
// GIÁM ĐỐC PHÊ DUYỆT / TỪ CHỐI YÊU CẦU SA THẢI
// =========================================================
    public boolean xuLyYeuCauSaThai(
            Nguoidung nguoiThucHien,
            String maNV,
            boolean chapNhan) {

        if (!(nguoiThucHien instanceof Giamdoc)
                || !taiKhoanConHoatDong(nguoiThucHien)) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Chỉ Giám đốc đang hoạt động mới được xử lý yêu cầu!");
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
            System.out.println("[GIÁM ĐỐC ĐÃ PHÊ DUYỆT] Nhân viên "
                    + nv.getTenND() + " [" + nv.getMaND()
                    + "] đã được sa thải.");
        } else {
            System.out.println("[GIÁM ĐỐC ĐÃ TỪ CHỐI] Yêu cầu sa thải nhân viên "
                    + nv.getTenND() + " [" + nv.getMaND()
                    + "] không được chấp thuận.");
        }

        nv.setYeuCauSaThai(null);
        ghiFile("data/nhanvien.txt");
        return true;
    }
}