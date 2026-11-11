package person; // hoặc package khachhang tùy dự án của bạn

import java.util.Scanner;
import util.Ktradulieu;

public class Khachhang {
    private String maKH;
    private String tenKH;
    private String sdt;
    private String diaChi;
    private int diemTichLuy;
    private boolean daXoa;

    // Constructor rỗng
    public Khachhang() {
        this.maKH = "";
        this.tenKH = "";
        this.sdt = "";
        this.diaChi = "";
        this.diemTichLuy = 0;
        this.daXoa = false;
    }

    // Constructor đầy đủ tham số
    public Khachhang(String maKH, String tenKH, String sdt, String diaChi, int diemTichLuy, boolean daXoa) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.diemTichLuy = diemTichLuy;
        this.daXoa = daXoa;
    }

    public void nhapThongTin(Scanner sc, DsKhachHang ds) {
        System.out.println("\n--- NHẬP THÔNG TIN KHÁCH HÀNG ---");
        String maGoiY = (ds != null) ? ds.sinhMaTuDong("KH") : "KH001";
        while (true) {
            System.out.print("Nhập mã khách hàng (Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                this.setMaKH(maGoiY);
                System.out.println("-> Mã tự tạo: " + this.maKH);
                break;
            }
            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);
                if (!maChuan.startsWith("KH")) {
                    System.out.println("Lỗi: Mã khách hàng phải bắt đầu bằng 'KH' (Ví dụ: KH001, KH002)! Hoặc nhấn / để tự tạo mã.");
                    continue;
                }
                if (ds != null && ds.timKiem(maChuan) != null) {
                    System.out.println("Lỗi: Mã '" + maChuan + "' đã tồn tại! Vui lòng nhập lại hoặc nhấn / để tự tạo mã.");
                    continue;
                }
                this.setMaKH(maChuan);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        this.setTenKH(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập họ và tên: "));
        String sdtInput = Ktradulieu.nhapChuoi(sc, "Nhập số điện thoại: ",
                Ktradulieu.phoneRegex, "Số điện thoại không đúng định dạng (03/05/07/08/09)!");
        this.setSdt(sdtInput);
        this.setDiaChi(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập địa chỉ: "));
        this.diemTichLuy = 0;
        this.daXoa = false;
        System.out.println("✅ Đã thêm xong khách hàng: [" + this.maKH + "] " + this.tenKH);
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã khách hàng không được để trống!");
        }
        String maChuan = Ktradulieu.chuanHoaMa(maKH);
        if (!maChuan.startsWith("KH")) {
            throw new IllegalArgumentException("[Lỗi] Mã khách hàng phải bắt đầu bằng 'KH' (Nhận: " + maKH + ")!");
        }
        this.maKH = maChuan;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        if (tenKH == null || tenKH.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Tên khách hàng không được để trống!");
        }
        this.tenKH = Ktradulieu.chuanHoaChuoi(tenKH);
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Số điện thoại không được để trống!");
        }
        String sdtChuan = sdt.trim().replaceAll("\\s+", "");
        if (!sdtChuan.matches(Ktradulieu.phoneRegex)) {
            throw new IllegalArgumentException("[Lỗi] Số điện thoại '" + sdt + "' không đúng định dạng!");
        }
        this.sdt = sdtChuan;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        if (diaChi == null || diaChi.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Địa chỉ không được để trống!");
        }
        this.diaChi = diaChi.trim();
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) {
            throw new IllegalArgumentException("[Lỗi] Điểm tích lũy phải >= 0! Nhận: " + diemTichLuy);
        }
        this.diemTichLuy = diemTichLuy;
    }

    public boolean isDaXoa() {
        return daXoa;
    }

    public void setDaXoa(boolean daXoa) {
        this.daXoa = daXoa;
    }

    public void congDiem(int diem) {
        if (diem > 0) this.diemTichLuy += diem;
    }

    public boolean truDiem(int diem) {
        if (diem <= 0) return false;
        if (this.diemTichLuy >= diem) {
            this.diemTichLuy -= diem;
            return true;
        }
        return false;
    }

    // Hàm xuất định dạng lưu file
    public String toFileString() {
        return String.format("%s;%s;%s;%s;%d;%b",
                this.maKH, this.tenKH, this.sdt, this.diaChi, this.diemTichLuy, this.daXoa);
    }

    @Override
    public String toString() {
        return "[" + maKH + "] " + tenKH + " - SĐT: " + sdt + " - ĐC: " + diaChi + " - Điểm: " + diemTichLuy
                + (daXoa ? " [ĐÃ XÓA]" : " [HOẠT ĐỘNG]");
    }
}