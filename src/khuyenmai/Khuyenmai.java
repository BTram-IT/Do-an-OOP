package khuyenmai;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import util.Ktradulieu;

public abstract class Khuyenmai {

    protected String maKM;
    protected String loaiKM;
    protected LocalDate ngayBatDau;
    protected LocalDate ngayKetThuc;
    protected long mucChiToiThieu;
    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public Khuyenmai() {
        this.maKM = "";
        this.loaiKM = "";
        this.ngayBatDau = LocalDate.now();
        this.ngayKetThuc = LocalDate.now().plusMonths(1);
        this.mucChiToiThieu = 0;
    }


    public Khuyenmai(String maKM, String loaiKM, LocalDate ngayBatDau, LocalDate ngayKetThuc, long mucChiToiThieu) {
        this.maKM = maKM;
        this.loaiKM = loaiKM;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.mucChiToiThieu = mucChiToiThieu;
    }

    public void nhapThongTin(Scanner sc, DSKhuyenMai dsKM) {
        System.out.println("\n--- THIẾT LẬP CHƯƠNG TRÌNH KHUYẾN MÃI ---");
        String maGoiY = (dsKM != null) ? dsKM.sinhMaTuDong("KM") : "KM001";
        while (true) {
            System.out.print("Nhập mã khuyến mãi (Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                this.setMaKM(maGoiY);
                System.out.println("-> Mã khuyến mãi tự tạo: " + this.maKM);
                break;
            }

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);
                if (!maChuan.startsWith("KM")) {
                    System.out.println("Lỗi: Mã khuyến mãi phải bắt đầu bằng 'KM' (Ví dụ: KM001, KM_TET)! Hoặc nhấn / để tự tạo mã.");
                    continue;
                }
                if (dsKM != null && dsKM.timKiem(maChuan) != null) {
                    System.out.println("Lỗi: Mã '" + maChuan + "' đã tồn tại! Vui lòng nhập lại hoặc nhấn / để tự tạo.");
                    continue;
                }
                this.setMaKM(maChuan);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                LocalDate batDau = Ktradulieu.nhapNgay(sc, "Nhập ngày bắt đầu khuyến mãi (dd/MM/yyyy): ");
                setNgayBatDau(batDau);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage());
            }
        }

        while (true) {
            try {
                LocalDate ketThuc = Ktradulieu.nhapNgay(sc, "Nhập ngày kết thúc khuyến mãi (dd/MM/yyyy): ");
                setNgayKetThuc(ketThuc);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                long mucChi = (long) Ktradulieu.nhapSoThucKhongAm(sc, "Nhập mức chi tiêu tối thiểu của đơn hàng (đ): ");
                setMucChiToiThieu(mucChi);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean kiemTraThoiHan() {
        return kiemTraThoiHan(LocalDate.now());
    }

    public boolean kiemTraThoiHan(LocalDate ngayKiemTra) {
        if (ngayKiemTra == null || ngayBatDau == null || ngayKetThuc == null) {
            return false;
        }
        return !ngayKiemTra.isBefore(ngayBatDau) && !ngayKiemTra.isAfter(ngayKetThuc);
    }

    public boolean kiemTraApDung(String maSP, long tongTienTruocKM, DSSanPhamKhuyenMai dsSPKM) {
        if (maSP == null || maSP.trim().isEmpty()) {
            return false;
        }
        if (!kiemTraThoiHan()) {
            return false;
        }
        if (tongTienTruocKM < mucChiToiThieu) {
            return false;
        }
        if (dsSPKM == null) {
            return false;
        }
        return dsSPKM.kiemTraLienKet(maSP, maKM);
    }


    public abstract long tinhTienGiam(long giaBan);

    // Trả về tổng tiền giảm trên toàn bộ số lượng sản phẩm
    public long tinhTongTienGiam(long giaBan, int soLuong) {
        if (giaBan <= 0 || soLuong <= 0) {
            return 0;
        }
        long giamMotSP = tinhTienGiam(giaBan);
        if (giamMotSP <= 0) {
            return 0;
        }
        long thanhTien = Math.multiplyExact(giaBan, (long) soLuong);
        long tongGiam = Math.multiplyExact(giamMotSP, (long) soLuong);
        return Math.min(tongGiam, thanhTien);
    }

    public int tinhSoLuongTang(int soLuongMua) {
        return 0;
    }

    public abstract Khuyenmai taoKhuyenMaiRong();

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã khuyến mãi không được để trống!");
        }
        this.maKM = Ktradulieu.chuanHoaMa(maKM);
    }

    public String getLoaiKM() {
        return loaiKM;
    }

    public void setLoaiKM(String loaiKM) {
        if (loaiKM == null || loaiKM.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Loại khuyến mãi không được để trống!");
        }
        this.loaiKM = loaiKM.trim();
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        if (ngayBatDau == null) {
            throw new IllegalArgumentException("[Lỗi] Ngày bắt đầu không được để trống!");
        }
        if (this.ngayKetThuc != null && ngayBatDau.isAfter(this.ngayKetThuc)) {
            throw new IllegalArgumentException("[Lỗi] Ngày bắt đầu không thể sau ngày kết thúc!");
        }
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        if (ngayKetThuc == null) {
            throw new IllegalArgumentException("[Lỗi] Ngày kết thúc không được để trống!");
        }
        if (this.ngayBatDau != null && ngayKetThuc.isBefore(this.ngayBatDau)) {
            throw new IllegalArgumentException("[Lỗi] Ngày kết thúc không thể trước ngày bắt đầu!");
        }
        this.ngayKetThuc = ngayKetThuc;
    }

    public long getMucChiToiThieu() {
        return mucChiToiThieu;
    }

    public void setMucChiToiThieu(long mucChiToiThieu) {
        if (mucChiToiThieu < 0) {
            throw new IllegalArgumentException("[Lỗi] Mức chi tối thiểu không được âm! Nhận: " + mucChiToiThieu);
        }
        this.mucChiToiThieu = mucChiToiThieu;
    }

    public String toDongFile() {
        return String.format("%s;%s;%s;%s;%d",
                maKM, loaiKM, ngayBatDau.format(DINH_DANG_NGAY), ngayKetThuc.format(DINH_DANG_NGAY), mucChiToiThieu);
    }

    @Override
    public String toString() {
        return "[" + maKM + "] " + loaiKM
                + " | Thời gian: " + ngayBatDau.format(DINH_DANG_NGAY) + " -> " + ngayKetThuc.format(DINH_DANG_NGAY)
                + " | Đơn tối thiểu: " + Ktradulieu.dinhDangTien(mucChiToiThieu);
    }
}