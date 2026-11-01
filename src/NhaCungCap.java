import java.util.Scanner;
import java.util.ArrayList;

public class NhaCungCap extends DoiTac {
    private static final Scanner sc = TienIch.sc;


    private String nganhHangCungCap;
    private double hanMucNoCong;
    private double congNoHienTai;        
    private ArrayList<String> dsSP;       
    
    public NhaCungCap() {
        super("Nha cung cap");
        this.nganhHangCungCap = "";
        this.dsSP = new ArrayList<>();
    }

    public NhaCungCap(String maDT, String tenDT, String diaChi, String email, String maSoThue,
                      String nganhHangCungCap, double hanMucNoCong, ArrayList<String> dsSP) {
        super(maDT, tenDT, diaChi, email, maSoThue, "Nha cung cap");
        this.nganhHangCungCap = nganhHangCungCap;
        this.hanMucNoCong = hanMucNoCong;
        this.dsSP = dsSP;
    }

    @Override
    public void nhap() {
        super.nhap();
        nganhHangCungCap = Ktradulieu.nhapChuoi(sc, "Nganh hang cung cap: ", TienIch.REGEX_VAN_BAN, TienIch.LOI_VAN_BAN);
        hanMucNoCong = TienIch.nhapSoThucKhongAm(sc, "Han muc no cong: ");
        dsSP = new ArrayList<>();
        String ds = TienIch.nhapChuoiTuyChon(sc, "Ma san pham cung cap, cach nhau dau phay (de trong neu chua co): ", TienIch.REGEX_DS_MA_SP, TienIch.LOI_DS_MA_SP);
        for (String ma : ds.split(",")) {
            if (!ma.trim().isEmpty()) {
                dsSP.add(ma.trim().toUpperCase());
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
        for (String ma : dsSP) {
            if (ma.equalsIgnoreCase(maSP)) {
                return true;
            }
        }
        return false;
    }

    public String getNganhHangCungCap() {
        return nganhHangCungCap;
    }

    public double getHanMucNoCong() {
        return hanMucNoCong;
    }

    public double getCongNoHienTai() {
        return congNoHienTai;
    }

    public ArrayList<String> getDsSP() {
        return dsSP;
    }

    @Override
    public String toString() {
        return super.toString() + String.format("%n       Nganh: %s | Han muc no: %,.0f | Dang no: %,.0f | SP: %s",
                nganhHangCungCap, hanMucNoCong, congNoHienTai, String.join(",", dsSP));
    }
}
