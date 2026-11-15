package khuyenmai;

import java.time.LocalDate;
import java.util.Scanner;
import java.math.BigDecimal;
import util.Ktradulieu;

public class KhuyenmaiTructiep extends Khuyenmai {

    private long soTienGiam;

    // Constructor mặc định
    public KhuyenmaiTructiep() {
        super();
        this.soTienGiam = 0;
        setLoaiKM("Giảm giá trực tiếp");
    }

    // Constructor đầy đủ
    public KhuyenmaiTructiep(
            String maKM,
            String loaiKM,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            long soTienGiam) {
        super(
                maKM,
                (loaiKM == null || loaiKM.trim().isEmpty()) ? "Giảm giá trực tiếp" : loaiKM,
                ngayBatDau,
                ngayKetThuc
        );
        setSoTienGiam(soTienGiam);
    }

    // Cập nhật mô tả khuyến mãi
    private void capNhatLoaiKM() {
        if (soTienGiam > 0) {
            setLoaiKM("Giảm " + Ktradulieu.dinhDangTien(soTienGiam));
        } else {
            setLoaiKM("Giảm giá trực tiếp");
        }
    }

    // Nhập thông tin khuyến mãi
    @Override
    public void nhapThongTin(Scanner sc, DSKhuyenMai dsKM) {
        super.nhapThongTin(sc, dsKM);

        while (true) {
            try {
                double giaTri = Ktradulieu.nhapSoThucKhongAm(
                        sc, "Nhập số tiền giảm trực tiếp cho mỗi sản phẩm (VND): "
                );

                // Không cho phép số tiền có phần thập phân
                long giam = BigDecimal.valueOf(giaTri).longValueExact();
                setSoTienGiam(giam);
                break;

            } catch (IllegalArgumentException | ArithmeticException e) {
                System.out.println("[Lỗi] Số tiền giảm phải là số nguyên lớn hơn 0!");
            }
        }

        System.out.println("Đã thiết lập xong chương trình: [" + this.maKM + "] " + this.loaiKM);
    }

    // Tính tiền giảm cho một sản phẩm
    @Override
    public long tinhTienGiam(long giaBan) {
        if (giaBan <= 0 || soTienGiam <= 0) {
            return 0;
        }
        // Không giảm vượt quá giá bán sản phẩm
        return Math.min(giaBan, soTienGiam);
    }

    // Tính tổng tiền giảm cho một dòng sản phẩm
    @Override
    public long tinhTongTienGiam(long giaBan, int soLuong) {
        if (giaBan <= 0 || soLuong <= 0 || soTienGiam <= 0) {
            return 0;
        }

        try {
            long thanhTien = Math.multiplyExact(giaBan, (long) soLuong);
            long giamMotSP = tinhTienGiam(giaBan);
            long tongGiam = Math.multiplyExact(giamMotSP, (long) soLuong);

            return Math.min(tongGiam, thanhTien);

        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("[Lỗi] Thành tiền hoặc tổng tiền giảm vượt giới hạn!", e);
        }
    }

    // Tạo đối tượng khuyến mãi rỗng
    @Override
    public Khuyenmai taoKhuyenMaiRong() {
        return new KhuyenmaiTructiep();
    }

    // Ghi dữ liệu ra file
    @Override
    public String toDongFile() {
        if (soTienGiam <= 0) {
            throw new IllegalStateException("[Lỗi] Số tiền giảm trực tiếp chưa hợp lệ!");
        }

        return "TRUCTIEP;" + super.toDongFile() + ";" + soTienGiam;
    }

    // Getter
    public long getSoTienGiam() {
        return soTienGiam;
    }

    // Setter
    public void setSoTienGiam(long soTienGiam) {
        if (soTienGiam <= 0) {
            throw new IllegalArgumentException("[Lỗi] Số tiền giảm phải lớn hơn 0! Nhận: " + soTienGiam);
        }

        this.soTienGiam = soTienGiam;
        capNhatLoaiKM();
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Tiền giảm mỗi sản phẩm: "
                + Ktradulieu.dinhDangTien(soTienGiam);
    }
}