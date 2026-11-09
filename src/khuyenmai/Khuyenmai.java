package khuyenmai;

import java.time.LocalDate;
import java.util.Scanner;
import sanpham.SanPham;
import util.Ktradulieu;

public abstract class Khuyenmai {
    protected static final Scanner sc = new Scanner(System.in);

    private String maKM;
    private String loaiKM;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private long mucChiToiThieu;

    public Khuyenmai() {
        this.maKM = "";
        this.loaiKM = "";
        this.ngayBatDau = LocalDate.now();
        this.ngayKetThuc = LocalDate.now();
        this.mucChiToiThieu = 0;
    }

    public Khuyenmai(String maKM, String loaiKM, LocalDate ngayBatDau, LocalDate ngayKetThuc, long mucChiToiThieu) {
        setMaKM(maKM);
        setLoaiKM(loaiKM);
        if (ngayBatDau == null || ngayKetThuc == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được null!");
        }
        if (ngayKetThuc.isBefore(ngayBatDau)) {
            throw new IllegalArgumentException("Ngày kết thúc không được trước ngày bắt đầu!");
        }
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        setMucChiToiThieu(mucChiToiThieu);
    }

    public void setKM() {
        String maNhap = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi: ", ".+",
                "Mã khuyến mãi không được để trống!");
        setMaKM(maNhap);

        ngayBatDau = Ktradulieu.nhapNgaySinh(
                sc, "Nhập ngày bắt đầu (yyyy-MM-dd): ");

        do {
            ngayKetThuc = Ktradulieu.nhapNgaySinh(
                    sc, "Nhập ngày kết thúc (yyyy-MM-dd): ");

            if (ngayKetThuc.isBefore(ngayBatDau)) {
                System.out.println("Ngày kết thúc không được nhỏ hơn ngày bắt đầu!");
            }
        } while (ngayKetThuc.isBefore(ngayBatDau));

        do {
            mucChiToiThieu = Ktradulieu.nhapSoNguyen(
                    sc, "Nhập mức chi tiêu tối thiểu: ");

            if (mucChiToiThieu < 0) {
                System.out.println("Mức chi tiêu không được âm!");
            }
        } while (mucChiToiThieu < 0);
    }

    public boolean kiemTraThoiHan() {
        return kiemTraThoiHan(LocalDate.now());
    }

    public boolean kiemTraThoiHan(LocalDate ngayKiemTra) {
        if (ngayKiemTra == null || ngayBatDau == null || ngayKetThuc == null) {
            return false;
        }
        return !ngayKiemTra.isBefore(ngayBatDau)
                && !ngayKiemTra.isAfter(ngayKetThuc);
    }

    public boolean kiemTraApDung(String maSP, long TongTienTruocKM, DSSanPhamKhuyenMai dsSPKM) {
        if (maSP == null || maSP.trim().isEmpty()) {
            return false;
        }
        if (!kiemTraThoiHan()) {
            return false;
        }
        if (TongTienTruocKM < mucChiToiThieu) {
            return false;
        }
        if (dsSPKM == null) {
            return false;
        }
        return dsSPKM.kiemTraLienKet(maSP, maKM);
    }

    // Trả về số tiền được giảm trên 1 đơn vị sản phẩm
    public abstract long tinhTienGiam(long giaBan);

    // Trả về tổng số tiền được giảm trên cả dòng sản phẩm (soLuong sản phẩm)
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

    // Số lượng sản phẩm tặng kèm (mặc định = 0, lớp KhuyenmaiMuatang sẽ ghi đè)
    public int tinhSoLuongTang(int soLuongMua) {
        return 0;
    }

    // Tạo đối tượng rỗng cùng loại để sửa khuyến mãi
    public abstract Khuyenmai taoKhuyenMaiRong();

    // =========================================================================
    // XỬ LÝ FILE (TO DÒNG & TỪ ĐÒNG FILE)
    // =========================================================================

    // Chuyển đối tượng thành định dạng chuỗi ngăn cách bằng dấu "|" để ghi file
    public String toDongFile() {
        return maKM + "|" + loaiKM + "|" + ngayBatDau + "|" + ngayKetThuc + "|" + mucChiToiThieu;
    }

    // Hàm tĩnh đọc dòng từ file và trả về đối tượng Khuyến Mãi phù hợp
    public static Khuyenmai tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty()) {
            return null;
        }
        String[] parts = dong.split("\\|");
        if (parts.length < 5) {
            return null;
        }

        String maKM = parts[0];
        String loaiKM = parts[1];
        LocalDate ngayBatDau = LocalDate.parse(parts[2]);
        LocalDate ngayKetThuc = LocalDate.parse(parts[3]);
        long mucChiToiThieu = Long.parseLong(parts[4]);


        return null; // Thay thế bằng logic trả về lớp con tương ứng của bạn
    }

    // =========================================================================
    // GETTER & SETTER
    // =========================================================================

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã khuyến mãi không được để trống!");
        }
        this.maKM = SanPham.chuanHoaMa(maKM);
    }

    public String getLoaiKM() {
        return loaiKM;
    }

    public void setLoaiKM(String loaiKM) {
        this.loaiKM = (loaiKM == null) ? "" : loaiKM.trim();
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        if (ngayBatDau == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được null!");
        }
        if (this.ngayKetThuc != null && this.ngayKetThuc.isBefore(ngayBatDau)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc!");
        }
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        if (ngayKetThuc == null) {
            throw new IllegalArgumentException("Ngày kết thúc không được null!");
        }
        if (this.ngayBatDau != null && ngayKetThuc.isBefore(this.ngayBatDau)) {
            throw new IllegalArgumentException("Ngày kết thúc không được trước ngày bắt đầu!");
        }
        this.ngayKetThuc = ngayKetThuc;
    }

    public long getMucChiToiThieu() {
        return mucChiToiThieu;
    }

    public void setMucChiToiThieu(long mucChiToiThieu) {
        if (mucChiToiThieu < 0) {
            throw new IllegalArgumentException("Mức chi tối thiểu không được âm!");
        }
        this.mucChiToiThieu = mucChiToiThieu;
    }

    @Override
    public String toString() {
        return "Mã KM: " + maKM
                + "\nLoại KM: " + loaiKM
                + "\nNgày bắt đầu: " + ngayBatDau
                + "\nNgày kết thúc: " + ngayKetThuc
                + "\nMức chi tối thiểu: " + mucChiToiThieu;
    }
}