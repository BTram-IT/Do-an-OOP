public class TienMat implements ThanhToan {

    private double tienKhachDua;

    public TienMat() {
        this(0);
    }

    public TienMat(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    @Override
    public boolean thanhToan(double soTien) {
        return tienKhachDua >= soTien;
    }

    @Override
    public String getPhuongThuc() {
        return "Tien mat";
    }

    public double tienThua(double soTien) {
        return Math.max(0, tienKhachDua - soTien);
    }
}
