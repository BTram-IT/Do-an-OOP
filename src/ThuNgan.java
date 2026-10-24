public class ThuNgan {
    private String maTN;
    private String tenThuNgan;
    private String chucVu;

    private String[] caLamViec = {"Ca Sang", "Ca Chieu", "Ca Toi"};

    public ThuNgan(String maTN, String tenThuNgan, String chucVu) {
        this.maTN = maTN;
        this.tenThuNgan = tenThuNgan;
        this.chucVu = chucVu;
    }

    public void hienThiThongTin() {
        System.out.println("Thu ngan: " + tenThuNgan + " - Chuc vu: " + chucVu);
    }

    public void hienThiCaLamViec() {
        System.out.println("Danh sach ca lam việc:");
        for (int i = 0; i < caLamViec.length; i++) {
            System.out.println((i + 1) + ". " + caLamViec[i]);
        }
    }

    public String getTenThuNgan() {
        return tenThuNgan;
    }
}
