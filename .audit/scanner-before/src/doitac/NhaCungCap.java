package doitac;

import java.util.Scanner;
import util.Ktradulieu;

public class NhaCungCap {
    private String maNCC;
    private String tenNCC;
    private String sdt;
    private String diaChi;
    private String email;
    private boolean daXoa;


    public NhaCungCap() {
        this.maNCC = "";
        this.tenNCC = "";
        this.sdt = "";
        this.diaChi = "";
        this.email = "";
        this.daXoa = false;
    }


    public NhaCungCap(String maNCC, String tenNCC, String sdt, String diaChi, String email, boolean daXoa) {
        this.maNCC = maNCC;
        this.tenNCC = tenNCC;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.email = email;
        this.daXoa = daXoa;
    }

    public void nhapThongTin(Scanner sc, DSNhaCungCap ds) {
        System.out.println("\n--- NHẬP THÔNG TIN NHÀ CUNG CẤP MỚI ---");
        String maGoiY = (ds != null) ? ds.sinhMaTuDong("NCC") : "NCC001";

        while (true) {
            System.out.print("Nhập mã nhà cung cấp (Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                this.setMaNCC(maGoiY);
                System.out.println("-> Mã tự tạo: " + this.maNCC);
                break;
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);
                if (!maChuan.startsWith("NCC")) {
                    System.out.println("Lỗi: Mã nhà cung cấp phải bắt đầu bằng 'NCC' (Ví dụ: NCC001, NCC012)! Hoặc nhấn / để tự tạo mã.");
                    continue;
                }
                if (ds != null && ds.timKiem(maChuan) != null) {
                    System.out.println("Lỗi: Mã '" + maChuan + "' đã tồn tại! Vui lòng nhập lại hoặc nhấn / để tự tạo mã.");
                    continue;
                }
                this.setMaNCC(maChuan);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        this.setTenNCC(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên nhà cung cấp: "));
        while (true) {
            try {
                System.out.print("Nhập số điện thoại nhà cung cấp: ");
                String sdtInput = sc.nextLine().trim();
                this.setSdt(sdtInput);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage());
            }
        }

        this.setDiaChi(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập địa chỉ nhà cung cấp: "));
        String emailInput = Ktradulieu.nhapChuoi(sc, "Nhập email liên hệ: ",
                Ktradulieu.emailRegex, "Email không đúng định dạng (VD: contact@vinamilk.com)!");
        this.setEmail(emailInput);
        this.daXoa = false;
        System.out.println("Đã thêm xong nhà cung cấp: [" + this.maNCC + "] " + this.tenNCC);
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã nhà cung cấp không được để trống!");
        }
        String maChuan = Ktradulieu.chuanHoaMa(maNCC);
        if (!maChuan.startsWith("NCC")) {
            throw new IllegalArgumentException("[Lỗi] Mã nhà cung cấp phải bắt đầu bằng 'NCC' (Nhận: " + maNCC + ")!");
        }
        this.maNCC = maChuan;
    }

    public String getTenNCC() {
        return tenNCC;
    }

    public void setTenNCC(String tenNCC) {
        if (tenNCC == null || tenNCC.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Tên nhà cung cấp không được để trống!");
        }
        this.tenNCC = Ktradulieu.chuanHoaChuoi(tenNCC);
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Số điện thoại nhà cung cấp không được để trống!");
        }
        String sdtChuan = sdt.trim().replaceAll("\\s+", "");
        if (!sdtChuan.matches(Ktradulieu.phoneRegex) && !sdtChuan.matches("^(03|05|07|08|09)\\d{8}$|^02\\d{8,9}$")) {
            throw new IllegalArgumentException("[Lỗi] Số điện thoại '" + sdt + "' không đúng định dạng (VD: 02854155555 hoặc 0912345678)!");
        }
        this.sdt = sdtChuan;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Địa chỉ nhà cung cấp không được để trống!");
        }
        this.diaChi = diaChi.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Email nhà cung cấp không được để trống!");
        }
        // Dùng biến regex emailRegex có sẵn trong Ktradulieu của bạn
        if (!email.trim().matches(Ktradulieu.emailRegex)) {
            throw new IllegalArgumentException("[Lỗi] Email '" + email + "' không đúng định dạng!");
        }
        this.email = email.trim();
    }

    public boolean isDaXoa() {
        return daXoa;
    }

    public void setDaXoa(boolean daXoa) {
        this.daXoa = daXoa;
    }

    public String toFileString() {
        return String.format("%s;%s;%s;%s;%s;%b",
                this.maNCC, this.tenNCC, this.sdt, this.diaChi, this.email, this.daXoa);
    }

    @Override
    public String toString() {
        return "[" + maNCC + "] " + tenNCC + " - SĐT: " + sdt + " - Email: " + email + " - ĐC: " + diaChi
                + (daXoa ? " [NGỪNG HỢP TÁC]" : " [ĐANG GIAO DỊCH]");
    }
}