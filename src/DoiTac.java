public class DoiTac {
     protected String ma;
    protected String ten;
    protected String sdt;

    public DoiTac() {
        this("", "", "");
    }

    public DoiTac(String ma, String ten, String sdt) {
        this.ma = ma;
        this.ten = ten;
        this.sdt = sdt;
    }

    public abstract String getLoai();

    public String getMa() {
        return ma;
    }

    public String getTen() {
        return ten;
    }

    public String getSdt() {
        return sdt;
    }

    @Override
    public String toString() {
        return String.format("%-8s %-25s %-14s %-15s", ma, ten, sdt, getLoai());
    }
}
