public class NhaCungCap {
  private String diaChi;

    public NhaCungCap() {
        this("", "", "", "");
    }

    public NhaCungCap(String ma, String ten, String sdt, String diaChi) {
        super(ma, ten, sdt);
        this.diaChi = diaChi;
    }

    @Override
    public String getLoai() {
        return "NhaCungCap";
    }

    public String getDiaChi() {
        return diaChi;
    }

    @Override
    public String toString() {
        return super.toString() + " Dia chi: " + diaChi;
    }
}
