public class CTDonhang {
    private String maChiTiet;
    private double donGia;
    private int soLuong;

    private String[] danhSachDonVi = {"Cai", "Hop", "Ly", "Phan"};

    public CTDonhang(String maChiTiet, double donGia, int soLuong) {
        this.maChiTiet = maChiTiet;
        this.donGia = donGia;
        this.soLuong = soLuong;
    }

    public double tinhThanhTien() {
        return donGia * soLuong;
    }

    public void hienThiThongTin() {
        System.out.println("Ma chi tiet: " + maChiTiet + " | Don gia: " + donGia + " | So luong: " + soLuong + " | Thanh tien: " + tinhThanhTien());
    }

    public void hienThiDanhSachDonVi() {
        System.out.println("Danh sach don vi tinh:");
        for (int i = 0; i < danhSachDonVi.length; i++) {
            System.out.println((i + 1) + ". " + danhSachDonVi[i]);
        }
    }

    public String getMaChiTiet() {
        return maChiTiet;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }
}
