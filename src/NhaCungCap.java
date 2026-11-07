import java.util.ArrayList;
import java.util.Scanner;

public class NhaCungCap extends DoiTac {
    private static final Scanner sc = new Scanner(System.in);

    private String nganhHangCungCap;
    private double hanMucNoCong;
    private double congNoHienTai;
    private ArrayList<String> dsSP;

    public NhaCungCap() {
        super("Nhà cung cấp");
        this.nganhHangCungCap = "";
        this.hanMucNoCong = 100_000_000;
        this.congNoHienTai = 0;
        this.dsSP = new ArrayList<>();
    }

    public NhaCungCap(String maDT, String tenDT, String diaChi, String email, String maSoThue,
                      String nganhHangCungCap, double hanMucNoCong, ArrayList<String> dsSP) {
        super(maDT, tenDT, diaChi, email, maSoThue, "Nhà cung cấp");
        this.nganhHangCungCap = nganhHangCungCap != null ? nganhHangCungCap.trim() : "";
        this.hanMucNoCong = Math.max(0, hanMucNoCong);
        this.congNoHienTai = 0;
        this.dsSP = dsSP != null ? dsSP : new ArrayList<>();
    }

    @Override
    public void nhap() {
        super.nhap(sc);
        this.nganhHangCungCap = Ktradulieu.nhapChuoi(sc, "Ngành hàng cung cấp: ", ".+", "Ngành hàng không được để trống!");
        this.hanMucNoCong = Ktradulieu.nhapSoThuc(sc, "Hạn mức nợ công: ");
        this.dsSP = new ArrayList<>();
        System.out.println("Mã sản phẩm cung cấp (cách nhau dấu phẩy, Enter = bỏ qua): ");
        String ds = sc.nextLine().trim();
        if (!ds.isEmpty()) {
            for (String ma : ds.split(",")) {
                if (!ma.trim().isEmpty()) {
                    dsSP.add(ma.trim().toUpperCase());
                }
            }
        }
    }

    public boolean kiemTraHanMucNoCong(double soTienMoi) {
        return congNoHienTai + soTienMoi <= hanMucNoCong;
    }

    public void ghiNo(double soTien) {
        congNoHienTai += Math.max(0, soTien);
    }

    public void traNo(double soTien) {
        congNoHienTai = Math.max(0, congNoHienTai - Math.max(0, soTien));
    }

    public boolean cungCap(String maSP) {
        if (maSP == null) return false;
        for (String ma : dsSP) {
            if (ma.equalsIgnoreCase(maSP)) return true;
        }
        return false;
    }

    public String getNganhHangCungCap() { return nganhHangCungCap; }
    public void setNganhHangCungCap(String nganhHangCungCap) { this.nganhHangCungCap = nganhHangCungCap != null ? nganhHangCungCap.trim() : ""; }
    public double getHanMucNoCong() { return hanMucNoCong; }
    public void setHanMucNoCong(double hanMucNoCong) { this.hanMucNoCong = Math.max(0, hanMucNoCong); }
    public double getCongNoHienTai() { return congNoHienTai; }
    public ArrayList<String> getDsSP() { return dsSP; }

    public boolean themSanPhamCungCap(String maSP) {
        if (maSP == null || cungCap(maSP)) return false;
        dsSP.add(maSP.trim().toUpperCase());
        return true;
    }

    public boolean xoaSanPhamCungCap(String maSP) {
        if (maSP == null) return false;
        return dsSP.removeIf(m -> m.equalsIgnoreCase(maSP.trim()));
    }

    @Override
    public String toDongFile() {
        return "NCC|" + maDT + "|" + tenDT + "|" + diaChi + "|" + email + "|" + maSoThue + "|"
                + nganhHangCungCap + "|" + hanMucNoCong + "|" + congNoHienTai + "|" + String.join(",", dsSP);
    }

    public static NhaCungCap tuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);
        ArrayList<String> spList = new ArrayList<>();
        if (t.length >= 10 && !t[9].isEmpty()) {
            for (String s : t[9].split(",")) spList.add(s.trim());
        }
        NhaCungCap ncc = new NhaCungCap(t[1], t[2], t[3], t[4], t[5], t[6], Double.parseDouble(t[7]), spList);
        ncc.congNoHienTai = Double.parseDouble(t[8]);
        return ncc;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("%n       Ngành: %s | Hạn mức: %,.0f | Đang nợ: %,.0f | SP: %s",
                nganhHangCungCap, hanMucNoCong, congNoHienTai, String.join(", ", dsSP));
    }
}