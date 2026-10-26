public class DonViVanChuyen extends DoiTac {

    private double phiGiaoHang;

    public DonViVanChuyen() {
        this("", "", "", 0);
    }

    public DonViVanChuyen(String ma, String ten, String sdt, double phiGiaoHang) {
        super(ma, ten, sdt);
        this.phiGiaoHang = phiGiaoHang;
    }

    @Override
    public String getLoai() {
        return "DonViVanChuyen";
    }

    public double getPhiGiaoHang() {
        return phiGiaoHang;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" Phi giao: %,.0f", phiGiaoHang);
    }
}
