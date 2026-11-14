package khuyenmai;


import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import util.Ktradulieu;

public abstract class Khuyenmai {

    protected String maKM;
    protected String loaiKM;
    protected LocalDate ngayBatDau;
    protected LocalDate ngayKetThuc;

    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Constructor mặc định
    public Khuyenmai() {
        this.maKM = "";
        this.loaiKM = "";
        this.ngayBatDau = null;
        this.ngayKetThuc = null;
    }

    // Constructor đầy đủ
    public Khuyenmai(String maKM, String loaiKM, LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        String maChuan = chuanHoaMaKM(maKM);

        if (loaiKM == null || loaiKM.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Loại khuyến mãi không được để trống!");
        }

        kiemTraKhoangNgay(ngayBatDau, ngayKetThuc);

        this.maKM = maChuan;
        this.loaiKM = loaiKM.trim();
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    // Chuẩn hóa và kiểm tra mã khuyến mãi (KM001, KM002, ... KM1000 trở lên vẫn hợp lệ)
    private String chuanHoaMaKM(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã khuyến mãi không được để trống!");
        }

        String maChuan = Ktradulieu.chuanHoaMa(maKM);

        if (!maChuan.matches("^KM\\d{3,}$")) {
            throw new IllegalArgumentException("[Lỗi] Mã khuyến mãi phải có dạng KM001, KM002,...!");
        }

        return maChuan;
    }

    // Kiểm tra khoảng ngày
    private static void kiemTraKhoangNgay(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        if (ngayBatDau == null || ngayKetThuc == null) {
            throw new IllegalArgumentException("[Lỗi] Ngày bắt đầu và ngày kết thúc không được để trống!");
        }

        if (ngayKetThuc.isBefore(ngayBatDau)) {
            throw new IllegalArgumentException("[Lỗi] Ngày kết thúc không thể trước ngày bắt đầu!");
        }
    }

    // Cập nhật khoảng ngày
    public void setKhoangNgay(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        kiemTraKhoangNgay(ngayBatDau, ngayKetThuc);
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }

    // Nhập thông tin khuyến mãi
    public void nhapThongTin(Scanner sc, DSKhuyenMai dsKM) {
        System.out.println("\n--- THIẾT LẬP CHƯƠNG TRÌNH KHUYẾN MÃI ---");

        // 1. Nhập mã khuyến mãi
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
                String maChuan = chuanHoaMaKM(input);

                if (dsKM != null && dsKM.timKiem(maChuan) != null) {
                    System.out.println("Lỗi: Mã " + maChuan + " đã tồn tại! Vui lòng nhập lại.");
                    continue;
                }

                this.setMaKM(maChuan);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // 2. Nhập ngày bắt đầu và ngày kết thúc
        while (true) {
            try {
                LocalDate batDau = Ktradulieu.nhapNgay(sc, "Nhập ngày bắt đầu khuyến mãi (dd/MM/yyyy): ");
                LocalDate ketThuc = Ktradulieu.nhapNgay(sc, "Nhập ngày kết thúc khuyến mãi (dd/MM/yyyy): ");

                // Chỉ chặn khi tạo mới. Khi đọc file, khuyến mãi cũ đã hết hạn vẫn được nạp bình thường.
                if (ketThuc != null && ketThuc.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException(
                            "[Lỗi] Ngày kết thúc không được ở trong quá khứ (khuyến mãi sẽ không bao giờ có hiệu lực)!");
                }

                setKhoangNgay(batDau, ketThuc);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("Vui lòng nhập lại cả hai ngày!");
            }
        }
    }

    // Kiểm tra khuyến mãi còn trong thời hạn không
    public boolean kiemTraThoiHan() {
        return kiemTraThoiHan(LocalDate.now());
    }

    public boolean kiemTraThoiHan(LocalDate ngayKiemTra) {
        if (ngayKiemTra == null || ngayBatDau == null || ngayKetThuc == null) {
            return false;
        }

        return !ngayKiemTra.isBefore(ngayBatDau) && !ngayKiemTra.isAfter(ngayKetThuc);
    }

    // Kiểm tra điều kiện áp dụng khuyến mãi cho sản phẩm
    public boolean kiemTraApDung(String maSP, DSSanPhamKhuyenMai dsSPKM) {
        if (maSP == null || maSP.trim().isEmpty()) {
            return false;
        }

        if (!kiemTraThoiHan()) {
            return false;
        }

        if (dsSPKM == null) {
            return false;
        }

        return dsSPKM.kiemTraLienKet(maSP, maKM);
    }

    // Tính tiền giảm trên một sản phẩm
    public abstract long tinhTienGiam(long giaBan);

    // Tính tổng tiền giảm của một dòng sản phẩm
    public long tinhTongTienGiam(long giaBan, int soLuong) {
        if (giaBan <= 0 || soLuong <= 0) {
            return 0;
        }

        long giamMotSP = tinhTienGiam(giaBan);

        if (giamMotSP <= 0) {
            return 0;
        }

        if (giamMotSP > giaBan) {
            giamMotSP = giaBan;
        }

        try {
            long tongGiam = Math.multiplyExact(giamMotSP, (long) soLuong);
            long thanhTien = Math.multiplyExact(giaBan, (long) soLuong);

            return Math.min(tongGiam, thanhTien);

        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("[Lỗi] Số tiền quá lớn, không thể tính khuyến mãi!", e);
        }
    }

    // Mặc định không có sản phẩm tặng
    public int tinhSoLuongTang(int soLuongMua) {
        return 0;
    }

    /**
     * Quy đổi ưu đãi của một dòng hàng ra tiền (tiền giảm + giá trị hàng tặng).
     * Dùng để so sánh các khuyến mãi cùng áp dụng cho một sản phẩm và chọn cái có lợi nhất cho khách.
     */
    public long tinhGiaTriUuDai(long giaBan, int soLuong) {
        if (giaBan <= 0 || soLuong <= 0) {
            return 0;
        }

        long giam = tinhTongTienGiam(giaBan, soLuong);

        try {
            long tienHangTang = Math.multiplyExact((long) tinhSoLuongTang(soLuong), giaBan);
            return Math.addExact(giam, tienHangTang);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

    // Tạo đối tượng khuyến mãi rỗng theo từng loại
    public abstract Khuyenmai taoKhuyenMaiRong();

    // Getter và Setter
    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = chuanHoaMaKM(maKM);
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

        if (ngayKetThuc != null && ngayKetThuc.isBefore(ngayBatDau)) {
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

        if (ngayBatDau != null && ngayKetThuc.isBefore(ngayBatDau)) {
            throw new IllegalArgumentException("[Lỗi] Ngày kết thúc không thể trước ngày bắt đầu!");
        }

        this.ngayKetThuc = ngayKetThuc;
    }

    // Kiểm tra dữ liệu trước khi ghi file
    private void kiemTraDuLieuGhiFile() {
        if (maKM == null || maKM.trim().isEmpty() || loaiKM == null || loaiKM.trim().isEmpty()) {
            throw new IllegalStateException("[Lỗi] Không thể ghi khuyến mãi chưa đủ thông tin!");
        }

        kiemTraKhoangNgay(ngayBatDau, ngayKetThuc);
    }

    // Chuyển thành một dòng dữ liệu để ghi file
    public String toDongFile() {
        kiemTraDuLieuGhiFile();

        return String.format(Locale.ROOT, "%s;%s;%s;%s",
                maKM,
                loaiKM,
                ngayBatDau.format(DINH_DANG_NGAY),
                ngayKetThuc.format(DINH_DANG_NGAY));
    }

    @Override
    public String toString() {
        String thoiGian;

        if (ngayBatDau == null || ngayKetThuc == null) {
            thoiGian = "Chưa thiết lập thời hạn";
        } else {
            thoiGian = ngayBatDau.format(DINH_DANG_NGAY) + " -> " + ngayKetThuc.format(DINH_DANG_NGAY);
        }

        return "[" + maKM + "] " + loaiKM + " | Thời gian: " + thoiGian;
    }
}