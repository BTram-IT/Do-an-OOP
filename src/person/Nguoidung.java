package person;

import java.util.Scanner;
import util.Ktradulieu;

public abstract class Nguoidung {
    protected String maND;
    protected String tenND;
    protected String taiKhoan;
    protected String matKhau;
    protected String soDienThoai;
    protected String vaiTro;
    protected boolean daXoa; // Soft delete


    public Nguoidung() {
        this.maND = "";
        this.tenND = "";
        this.taiKhoan = "";
        this.matKhau = "";
        this.soDienThoai = "";
        this.vaiTro = "";
        this.daXoa = false;
    }

    // Constructor đầy đủ tham số
    public Nguoidung(String maND, String tenND, String taiKhoan, String matKhau, String soDienThoai, String vaiTro, boolean daXoa) {
        this.maND = maND;
        this.tenND = tenND;
        this.taiKhoan = taiKhoan;
        this.matKhau = matKhau;
        this.soDienThoai = soDienThoai;
        this.vaiTro = vaiTro;
        this.daXoa = daXoa;
    }

    public void nhapThongTin(Scanner sc, DSNhanVien ds) {
        String tienTo = "NV"; // Mặc định là Nhân Viên
        if (this instanceof QuanLyCuaHang) {
            tienTo = "QL";
        } else if (this instanceof Giamdoc) {
            tienTo = "GD";
        }

        String maGoiY = (ds != null) ? ds.sinhMaTuDong(tienTo) : (tienTo + "001");

        while (true) {
            System.out.print("Nhập mã người dùng (Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                this.setMaND(maGoiY);
                System.out.println("-> Mã tự tạo: " + this.maND);
                break;
            }

            // Nếu tự gõ -> Kiểm tra đúng tiền tố (NV001, QL001...)
            String maChuan = Ktradulieu.chuanHoaMa(input);
            String regex = "^" + tienTo + "\\d+$";

            if (!maChuan.matches(regex)) {
                System.out.println("Lỗi: Mã phải bắt đầu bằng '" + tienTo + "' (Ví dụ: " + tienTo + "001)! Hoặc nhấn / để tự tạo mã.");
                continue;
            }

            // Kiểm tra trùng mã trong danh sách
            if (ds != null && ds.timKiem(maChuan) != null) {
                System.out.println("Lỗi: Mã '" + maChuan + "' đã tồn tại! Vui lòng nhập lại hoặc nhấn / để tự tạo mã.");
                continue;
            }

            this.setMaND(maChuan);
            break;
        }
        this.setTenND(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập họ và tên: "));

        String sdt = Ktradulieu.nhapChuoi(sc, "Nhập số điện thoại (10 số, VD: 0912345678): ",
                Ktradulieu.phoneRegex, "Số điện thoại không đúng định dạng (03/05/07/08/09)!");
        this.setSoDienThoai(sdt);

        while (true) {
            String tk = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên tài khoản đăng nhập: ");
            if (tk.contains(" ")) {
                System.out.println("Tài khoản không được chứa dấu cách! Vui lòng nhập lại.");
                continue;
            }
            // Kiểm tra trùng tài khoản
            if (ds != null && ds.timKiemTheoTaiKhoan(tk) != null) {
                System.out.println("Tài khoản '" + tk + "' đã có người sử dụng! Vui lòng chọn tên khác.");
                continue;
            }
            this.setTaiKhoan(tk);
            break;
        }

        // Nhập mật khẩu tối thiểu 4 ký tự
        String mk = Ktradulieu.nhapChuoi(sc, "Nhập mật khẩu (tối thiểu 4 ký tự): ",
                ".{4,}", "Mật khẩu phải có từ 4 ký tự trở lên!");
        this.setMatKhau(mk);

        this.daXoa = false;
    }


    public String getMaND() {
        return maND;
    }

    public void setMaND(String maND) {
        if (maND == null || maND.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã người dùng không được để trống!");
        }
        this.maND = Ktradulieu.chuanHoaMa(maND);
    }

    public String getTenND() {
        return tenND;
    }

    public void setTenND(String tenND) {
        if (tenND == null || tenND.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Tên người dùng không được để trống!");
        }
        this.tenND = Ktradulieu.chuanHoaChuoi(tenND);
    }

    public String getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(String taiKhoan) {
        if (taiKhoan == null || taiKhoan.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Tài khoản đăng nhập không được để trống!");
        }
        if (taiKhoan.trim().contains(" ")) {
            throw new IllegalArgumentException("[Lỗi] Tài khoản không được chứa khoảng trắng!");
        }
        this.taiKhoan = taiKhoan.trim();
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        if (matKhau == null || matKhau.trim().length() < 4) {
            throw new IllegalArgumentException("[Lỗi] Mật khẩu tối thiểu phải từ 4 ký tự trở lên!");
        }
        this.matKhau = matKhau.trim();
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Số điện thoại không được để trống!");
        }
        String sdt = soDienThoai.trim().replaceAll("\\s+", "");
        if (!sdt.matches(Ktradulieu.phoneRegex) && !sdt.matches("^(03|05|07|08|09)\\d{8}$|^02\\d{8,9}$")) {
            throw new IllegalArgumentException("[Lỗi] Số điện thoại không đúng định dạng!");
        }
        this.soDienThoai = sdt;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        if (vaiTro == null || vaiTro.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Vai trò người dùng không được để trống!");
        }
        this.vaiTro = vaiTro.trim();
    }

    public boolean isDaXoa() {
        return daXoa;
    }

    public void setDaXoa(boolean daXoa) {
        this.daXoa = daXoa;
    }

    public abstract String[] getQuyenHan();
    public abstract String thucHienCongViec();

    @Override
    public String toString() {
        return "[" + vaiTro + "] " + maND + " - " + tenND + " (TK: " + taiKhoan + " | SĐT: " + soDienThoai + ")"
                + (daXoa ? " [ĐÃ NGHỈ VIỆC]" : " [HOẠT ĐỘNG]");
    }
}