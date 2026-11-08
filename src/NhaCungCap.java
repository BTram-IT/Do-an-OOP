
import java.util.ArrayList;
import java.util.Scanner;

public class NhaCungCap extends DoiTac {
    private String nganhHangCungCap;
    private double hanMucNoCong, congNoHienTai;
    private ArrayList<String> dsSP;

    public NhaCungCap() {
        super("Nhà cung cấp");
        nganhHangCungCap = "";
        hanMucNoCong = 100_000_000;
        congNoHienTai = 0;
        dsSP = new ArrayList<>();
    }

    public NhaCungCap(String maDT, String tenDT, String diaChi,
                      String email, String maSoThue, String nganhHangCungCap,
                      double hanMucNoCong, ArrayList<String> dsSP) {
        super(maDT, tenDT, diaChi, email, maSoThue, "Nhà cung cấp");

        setNganhHangCungCap(nganhHangCungCap);
        setHanMucNoCong(hanMucNoCong);
        congNoHienTai = 0;
        this.dsSP = new ArrayList<>();

        if (dsSP != null) {
            for (String ma : dsSP) themSanPhamCungCap(ma);
        }
    }

    @Override
    public void nhap(Scanner sc) {
        super.nhap(sc);

        setNganhHangCungCap(Ktradulieu.nhapChuoiKhongRong(
                sc, "Ngành hàng cung cấp: "));

        setHanMucNoCong(Ktradulieu.nhapSoThucKhongAm(
                sc, "Hạn mức nợ công: "));

        dsSP.clear();
        System.out.println("Nhập mã sản phẩm cung cấp (cách nhau dấu phẩy, Enter = bỏ qua): ");

        String ds = sc.nextLine().trim();

        if (!ds.isEmpty()) {
            for (String ma : ds.split(",")) {
                if (!themSanPhamCungCap(ma))
                    System.out.println("Mã sản phẩm không hợp lệ hoặc bị trùng: " + ma);
            }
        }
    }

    // Kiểm tra hạn mức công nợ
    public boolean kiemTraHanMucNoCong(double soTienMoi) {
        return Double.isFinite(soTienMoi) && soTienMoi >= 0
                && congNoHienTai + soTienMoi <= hanMucNoCong;
    }

    // Ghi tăng công nợ
    public void ghiNo(double soTien) {
        if (!kiemTraHanMucNoCong(soTien))
            throw new IllegalArgumentException("Số tiền vượt hạn mức nợ công!");

        congNoHienTai += soTien;
    }

    // Thanh toán công nợ
    public void traNo(double soTien) {
        if (!Double.isFinite(soTien) || soTien < 0)
            throw new IllegalArgumentException("Số tiền trả nợ không hợp lệ!");

        congNoHienTai = Math.max(0, congNoHienTai - soTien);
    }

    // Kiểm tra sản phẩm có được cung cấp không
    public boolean cungCap(String maSP) {
        if (maSP == null) return false;

        String ma = Ktradulieu.chuanHoaMa(maSP);
        return dsSP.contains(ma);
    }

    public String getNganhHangCungCap() {
        return nganhHangCungCap;
    }

    public void setNganhHangCungCap(String nganhHangCungCap) {
        if (nganhHangCungCap == null || nganhHangCungCap.trim().isEmpty())
            throw new IllegalArgumentException("Ngành hàng không được để trống!");

        this.nganhHangCungCap = nganhHangCungCap.trim();
    }

    public double getHanMucNoCong() {
        return hanMucNoCong;
    }

    public void setHanMucNoCong(double hanMucNoCong) {
        if (!Double.isFinite(hanMucNoCong) || hanMucNoCong < 0)
            throw new IllegalArgumentException("Hạn mức nợ không hợp lệ!");

        this.hanMucNoCong = hanMucNoCong;
    }

    public double getCongNoHienTai() {
        return congNoHienTai;
    }

    public ArrayList<String> getDsSP() {
        return new ArrayList<>(dsSP);
    }

    // Thêm sản phẩm cung cấp
    public boolean themSanPhamCungCap(String maSP) {
        String ma = Ktradulieu.chuanHoaMa(maSP);

        if (!ma.matches("[A-Z0-9]+") || cungCap(ma))
            return false;

        dsSP.add(ma);
        return true;
    }

    // Xóa sản phẩm cung cấp
    public boolean xoaSanPhamCungCap(String maSP) {
        if (maSP == null) return false;
        return dsSP.remove(Ktradulieu.chuanHoaMa(maSP));
    }

    @Override
    public String toDongFile() {
        return "NCC|" + maDT + "|" + tenDT + "|" + diaChi + "|"
                + email + "|" + maSoThue + "|" + nganhHangCungCap + "|"
                + hanMucNoCong + "|" + congNoHienTai + "|"
                + String.join(",", dsSP);
    }

    public static NhaCungCap tuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);

        if (t.length != 10 || !t[0].equals("NCC"))
            throw new IllegalArgumentException("Dữ liệu nhà cung cấp không hợp lệ!");

        ArrayList<String> ds = new ArrayList<>();

        if (!t[9].isEmpty()) {
            for (String ma : t[9].split(",")) {
                ds.add(ma.trim());
            }
        }

        NhaCungCap ncc = new NhaCungCap(
                t[1], t[2], t[3], t[4], t[5], t[6],
                Double.parseDouble(t[7]), ds
        );

        double congNo = Double.parseDouble(t[8]);

        if (!Double.isFinite(congNo) || congNo < 0)
            throw new IllegalArgumentException("Công nợ không hợp lệ!");

        ncc.congNoHienTai = congNo;
        return ncc;
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format("%n       Ngành: %s | Hạn mức: %,.0f"
                        + " | Đang nợ: %,.0f | SP: %s",
                nganhHangCungCap, hanMucNoCong,
                congNoHienTai, String.join(", ", dsSP));
    }
}
