public class CTHoaDon {
    private String maChiTietHD;
    private String noiDungGhiChu;

    private String[] hinhThucThanhToan = {"Tien mat", "Chuyen khoan", "Quet the"};

    public CTHoaDon(String maChiTietHD, String noiDungGhiChu) {
        this.maChiTietHD = maChiTietHD;
        this.noiDungGhiChu = noiDungGhiChu;
    }

    public void hienThiHinhThucThanhToan() {
        System.out.println("Cac hinh thuc thanh toan ho tro:");
        for (int i = 0; i < hinhThucThanhToan.length; i++) {
            System.out.println((i + 1) + ". " + hinhThucThanhToan[i]);
        }
    }

    public void hienThiThongTinChiTiet() {
        System.out.println("Ma chi tiet HD: " + maChiTietHD + " | Ghi chu: " + noiDungGhiChu);
    }

    public String getMaChiTietHD() {
        return maChiTietHD;
    }
}
