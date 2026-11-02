import java.util.Scanner;
import java.util.ArrayList;

public class DonViVanChuyen extends DoiTac {
    private static final Scanner sc = new Scanner(System.in);
    private ArrayList<PhuongTien> dsXe;

    public DonViVanChuyen() {
        super("Van chuyen");
        this.dsXe = new ArrayList<>();
    }

    public DonViVanChuyen(String maDT, String tenDT, String diaChi, String email, String maSoThue,
                          ArrayList<PhuongTien> dsXe) {
        super(maDT, tenDT, diaChi, email, maSoThue, "Van chuyen");
        this.dsXe = dsXe;
    }

    @Override
    public void nhap() {
        super.nhap();
        taoDSXe();
    }

    public void taoDSXe() {
        System.out.println("Nhap danh sach xe (de trong ma xe de ket thuc):");
        while (true) {
            String ma = TienIch.nhapChuoiTuyChon(sc, "Ma xe: ", TienIch.REGEX_MA_XE, TienIch.LOI_MA_XE);
            if (ma.isEmpty()) {
                return;
            }
            if (traCuuXe(ma) != null) {
                System.out.println("Ma xe da ton tai!");
                continue;
            }
            PhuongTien xe = new PhuongTien();
            xe.setMaXe(ma);
            xe.nhap();
            dsXe.add(xe);
        }
    }

    public void themXe(PhuongTien xe) {
        if (xe != null && traCuuXe(xe.getMaXe()) == null) {
            dsXe.add(xe);
        }
    }

    /** Tra cuu xe theo ma. */
    public PhuongTien traCuuXe(String maXe) {
        for (PhuongTien xe : dsXe) {
            if (xe.getMaXe().equalsIgnoreCase(maXe)) {
                return xe;
            }
        }
        return null;
    }

    public void inBangVe(double soKm, double taiTrongKg) {
        System.out.printf("--- BANG GIA VAN CHUYEN - %s (%,.0f km, %,.0f kg) ---%n", tenDT, soKm, taiTrongKg);
        boolean co = false;
        for (PhuongTien xe : dsXe) {
            if (xe.getSucChuaToiDa() >= taiTrongKg) {
                System.out.printf("  %-6s %-18s %-12s %,12.0f VND%n",
                        xe.getMaXe(), xe.getTenXe(), xe.getLoaiXe(), xe.tinhPhi(soKm));
                co = true;
            }
        }
        if (!co) {
            System.out.println("  (Khong co xe nao du suc chua)");
        }
    }

    public ArrayList<PhuongTien> getDsXe() {
        return dsXe;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        for (PhuongTien xe : dsXe) {
            sb.append(System.lineSeparator()).append("       Xe: ").append(xe);
        }
        return sb.toString();
    }
}
