
import java.util.ArrayList;
import java.util.Scanner;

public class DonViVanChuyen extends DoiTac {
    private ArrayList<PhuongTien> dsXe;

    public DonViVanChuyen() {
        super("Vận chuyển");
        dsXe = new ArrayList<>();
    }

    public DonViVanChuyen(String maDT, String tenDT, String diaChi, String email,
                          String maSoThue, ArrayList<PhuongTien> dsXe) {
        super(maDT, tenDT, diaChi, email, maSoThue, "Vận chuyển");
        this.dsXe = dsXe == null ? new ArrayList<>() : new ArrayList<>(dsXe);
    }

    @Override
    public void nhap(Scanner sc) {
        super.nhap(sc);
        taoDSXe(sc);
    }


    public void taoDSXe(Scanner sc) {
        System.out.println("Nhập danh sách xe (để trống mã xe để kết thúc):");
        while (true) {
            System.out.print("Mã xe: ");
            String ma = sc.nextLine().trim();
            if (ma.isEmpty()) break;
            if (!ma.matches("[a-zA-Z0-9]+")) {
                System.out.println("Mã xe không hợp lệ!");
                continue;
            }
            if (traCuuXe(ma) != null) {
                System.out.println("Mã xe đã tồn tại!");
                continue;
            }
            PhuongTien xe = new PhuongTien();
            xe.setMaXe(ma);
            xe.nhap(sc);
            dsXe.add(xe);
        }
    }

    public PhuongTien traCuuXe(String maXe) {
        if (maXe == null || maXe.trim().isEmpty()) return null;
        for (PhuongTien xe : dsXe)
            if (xe.getMaXe().equalsIgnoreCase(maXe.trim())) return xe;
        return null;
    }

    public boolean themXe(PhuongTien xe) {
        if (xe == null || traCuuXe(xe.getMaXe()) != null) return false;
        dsXe.add(xe);
        return true;
    }

    public boolean xoaXe(String maXe) {
        if (maXe == null) return false;
        return dsXe.removeIf(x -> x.getMaXe().equalsIgnoreCase(maXe.trim()));
    }

    public boolean suaXe(String maXe, PhuongTien xeMoi) {
        if (maXe == null || xeMoi == null) return false;
        for (int i = 0; i < dsXe.size(); i++) {
            if (dsXe.get(i).getMaXe().equalsIgnoreCase(maXe.trim())) {
                PhuongTien trung = traCuuXe(xeMoi.getMaXe());
                if (trung != null && trung != dsXe.get(i)) return false;
                dsXe.set(i, xeMoi);
                return true;
            }
        }
        return false;
    }

    public void hienThiDSXe() {
        if (dsXe.isEmpty()) {
            System.out.println("Chưa có phương tiện nào!");
            return;
        }
        for (PhuongTien xe : dsXe) System.out.println(xe);
    }

    public ArrayList<PhuongTien> getDsXe() {
        return new ArrayList<>(dsXe);
    }

    @Override
    public String toDongFile() {
        return "VC|" + maDT + "|" + tenDT + "|" + diaChi + "|" + email
                + "|" + maSoThue + "|" + dsXe.size();
    }

    public static DonViVanChuyen tuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);
        if (t.length != 7 || !t[0].equals("VC"))
            throw new IllegalArgumentException("Dòng dữ liệu vận chuyển không hợp lệ!");
        return new DonViVanChuyen(t[1], t[2], t[3], t[4], t[5], new ArrayList<>());
    }

    @Override
    public String toString() {
        return super.toString() + " | Số lượng xe: " + dsXe.size();
    }
}
