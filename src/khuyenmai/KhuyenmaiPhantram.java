package khuyenmai;

import java.time.LocalDate;
import java.util.Scanner;
import java.math.BigDecimal;
import java.math.RoundingMode;
import util.Ktradulieu;

public class KhuyenmaiPhantram extends Khuyenmai {

    private double phanTramGiamGia;
    private long mucGiamToiDa; // 0: không giới hạn, tương thích các chương trình cũ.

    // Constructor mặc định
    public KhuyenmaiPhantram() {
        super();
        this.phanTramGiamGia = 0;
        setLoaiKM("Giảm theo phần trăm");
    }

    // Constructor đầy đủ
    public KhuyenmaiPhantram(
            String maKM,
            String loaiKM,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            double phanTramGiamGia) {
        super(
                maKM,
                (loaiKM == null || loaiKM.trim().isEmpty()) ? "Giảm theo phần trăm" : loaiKM,
                ngayBatDau,
                ngayKetThuc
        );
        setPhanTramGiamGia(phanTramGiamGia);
    }

    // Kiểm tra phần trăm giảm giá
    private void kiemTraPhanTram(double phanTram) {
        if (!Double.isFinite(phanTram) || phanTram <= 0 || phanTram > 100) {
            throw new IllegalArgumentException(
                    "[Lỗi] Phần trăm giảm phải lớn hơn 0 và không vượt quá 100%!"
            );
        }
    }

    // Hiển thị phần trăm gọn: 10.0 -> "10", 12.5 -> "12.5"
    private String dinhDangPhanTram() {
        return BigDecimal.valueOf(phanTramGiamGia).stripTrailingZeros().toPlainString();
    }

    // Cập nhật mô tả loại khuyến mãi
    private void capNhatLoaiKM() {
        if (phanTramGiamGia > 0) {
            setLoaiKM("Giảm " + dinhDangPhanTram() + "%");
        } else {
            setLoaiKM("Giảm theo phần trăm");
        }
    }

    // Nhập thông tin khuyến mãi
    @Override
    public void nhapThongTin(Scanner sc, DSKhuyenMai dsKM) {
        super.nhapThongTin(sc, dsKM);

        while (true) {
            try {
                double pt = Ktradulieu.nhapSoThucKhongAm(
                        sc, "Nhập phần trăm giảm giá (0 < % <= 100): "
                );

                setPhanTramGiamGia(pt);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println(
                "Đã thiết lập xong chương trình: [" + this.maKM + "] " + this.loaiKM
        );
    }

    // Tính tiền giảm theo phần trăm trên một sản phẩm
    private long tinhTienGiamTheoPhanTram(long giaBan) {
        if (giaBan <= 0 || phanTramGiamGia <= 0) {
            return 0;
        }

        BigDecimal tien = BigDecimal.valueOf(giaBan);
        BigDecimal phanTram = BigDecimal.valueOf(phanTramGiamGia);

        try {
            return tien.multiply(phanTram)
                    .divide(
                            BigDecimal.valueOf(100),
                            0,
                            RoundingMode.HALF_UP
                    )
                    .longValueExact();

        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "[Lỗi] Tiền giảm vượt giới hạn!", e
            );
        }
    }

    // Tính tiền giảm trên một đơn vị sản phẩm
    @Override
    public long tinhTienGiam(long giaBan) {
        if (giaBan <= 0) {
            return 0;
        }

        return Math.min(
                tinhTienGiamTheoPhanTram(giaBan),
                giaBan
        );
    }

    // Mức giảm tối đa áp dụng cho cả dòng sản phẩm.
    @Override
    public long tinhTongTienGiam(long giaBan, int soLuong) {
        long tongGiam = super.tinhTongTienGiam(giaBan, soLuong);
        return mucGiamToiDa > 0 ? Math.min(tongGiam, mucGiamToiDa) : tongGiam;
    }

    public long getMucGiamToiDa() {
        return mucGiamToiDa;
    }

    public void setMucGiamToiDa(long mucGiamToiDa) {
        if (mucGiamToiDa < 0) {
            throw new IllegalArgumentException("[Lỗi] Mức giảm tối đa không được âm!");
        }
        this.mucGiamToiDa = mucGiamToiDa;
    }

    // Cập nhật phần trăm giảm giá
    public void setPhanTramGiamGia(double phanTramGiamGia) {
        kiemTraPhanTram(phanTramGiamGia);
        this.phanTramGiamGia = phanTramGiamGia;
        capNhatLoaiKM();
    }

    // Getter
    public double getPhanTramGiamGia() {
        return phanTramGiamGia;
    }

    // Tạo đối tượng khuyến mãi rỗng
    @Override
    public Khuyenmai taoKhuyenMaiRong() {
        return new KhuyenmaiPhantram();
    }

    // Ghi dữ liệu ra file
    @Override
    public String toDongFile() {
        kiemTraPhanTram(phanTramGiamGia);

        return "PHANTRAM;"
                + super.toDongFile()
                + ";" + phanTramGiamGia
                + (mucChiToiThieu > 0 || mucGiamToiDa > 0 ? ";" + mucChiToiThieu + ";" + mucGiamToiDa : "");
    }

    @Override
    protected String layChiTietBang() {
        return "Giảm: " + dinhDangPhanTram() + "%"
                + (mucGiamToiDa > 0 ? System.lineSeparator() + "Giảm tối đa: " + Ktradulieu.dinhDangTien(mucGiamToiDa) : "");
    }
}
