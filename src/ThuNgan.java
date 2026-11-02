public class ThuNgan {
    private String maTN;
    private String tenThuNgan;
    private String chucVu;

    private String[] caLamViec = {
        "Ca Sang", "Ca Chieu", "Ca Toi"
    };

    public ThuNgan(String maTN, String tenThuNgan, String chucVu) {
        this.maTN = maTN;
        this.tenThuNgan = tenThuNgan;
        this.chucVu = chucVu;
    }

    public void hienThiThongTin() {
        System.out.println("Ma thu ngan: " + maTN);
        System.out.println("Thu ngan: " + tenThuNgan);
        System.out.println("Chuc vu: " + chucVu);
    }

    public void hienThiCaLamViec() {
        System.out.println("Danh sach ca lam viec:");

        for (int i = 0; i < caLamViec.length; i++) {
            System.out.println((i + 1) + ". " + caLamViec[i]);
        }
    }

    public String getMaTN() {
        return maTN;
    }

    public String getTenThuNgan() {
        return tenThuNgan;
    }

    public String getChucVu() {
        return chucVu;
    }
}
