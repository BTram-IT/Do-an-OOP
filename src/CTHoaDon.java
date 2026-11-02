
public class CTHoaDon {
    private String maChiTietHD;
    private String noiDungGhiChu;

    private String[] hinhThucThanhToan = {
        "Tien mat", "Chuyen khoan", "Quet the"
    };

    private CTDonhang chiTietDonHang;

    public CTHoaDon(String maChiTietHD, String noiDungGhiChu) {
        this.maChiTietHD = maChiTietHD;
        this.noiDungGhiChu = noiDungGhiChu;
    }

    public CTHoaDon(String maChiTietHD,
                         CTDonhang chiTietDonHang,
                         String noiDungGhiChu) {
        this.maChiTietHD = maChiTietHD;
        this.chiTietDonHang = chiTietDonHang;
        this.noiDungGhiChu = noiDungGhiChu;
    }

    public void hienThiHinhThucThanhToan() {
        System.out.println("Cac hinh thuc thanh toan ho tro:");

        for (int i = 0; i < hinhThucThanhToan.length; i++) {
            System.out.println((i + 1) + ". " + hinhThucThanhToan[i]);
        }
    }

    public double getThanhTien() {
        if (chiTietDonHang == null) {
            return 0;
        }

        return chiTietDonHang.tinhThanhTien();
    }

    public void hienThiThongTinChiTiet() {
        System.out.println("Ma chi tiet HD: " + maChiTietHD);

        if (chiTietDonHang != null) {
            chiTietDonHang.hienThiThongTin();
        }

        if (noiDungGhiChu != null && !noiDungGhiChu.isEmpty()) {
            System.out.println("Ghi chu: " + noiDungGhiChu);
        }
    }

    public String getMaChiTietHD() {
        return maChiTietHD;
    }

    public String getNoiDungGhiChu() {
        return noiDungGhiChu;
    }

    public CTDonhang getChiTietDonHang() {
        return chiTietDonHang;
    }
}
