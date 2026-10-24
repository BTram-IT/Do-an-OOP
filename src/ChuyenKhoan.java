public class ChuyenKhoan implements ThanhToan {

    private String maGiaoDich;

    public ChuyenKhoan() {
        this("");
    }

    public ChuyenKhoan(String maGiaoDich) {
        this.maGiaoDich = (maGiaoDich == null) ? "" : maGiaoDich.trim();
    }

    @Override
    public boolean thanhToan(double soTien) {
        return soTien >= 0 && !maGiaoDich.isEmpty();
    }

    @Override
    public String getPhuongThuc() {
        return "Chuyen khoan";
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }
}
