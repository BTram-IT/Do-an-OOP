
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

    // Nhập danh sách xe
    public void taoDSXe(Scanner sc) {
        if (sc == null)
            throw new IllegalArgumentException("Scanner không hợp lệ!");

        System.out.println("Nhập danh sách xe (để trống mã xe để kết thúc):");

        while (true) {
            System.out.print("Mã xe: ");
            String ma = Ktradulieu.chuanHoaMa(sc.nextLine());

            if (ma.isEmpty()) break;

            if (!ma.matches("[A-Z0-9]+")) {
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

    // Tra cứu xe theo mã
    public PhuongTien traCuuXe(String maXe) {
        String ma = Ktradulieu.chuanHoaMa(maXe);

        if (ma.isEmpty()) return null;

        for (PhuongTien xe : dsXe) {
            if (Ktradulieu.chuanHoaMa(xe.getMaXe()).equals(ma))
                return xe;
        }

        return null;
    }

    // Thêm xe
    public boolean themXe(PhuongTien xe) {
        if (xe == null) return false;

        String ma = Ktradulieu.chuanHoaMa(xe.getMaXe());

        if (ma.isEmpty() || traCuuXe(ma) != null)
            return false;

        dsXe.add(xe);
        return true;
    }

    // Xóa xe
    public boolean xoaXe(String maXe) {
        PhuongTien xe = traCuuXe(maXe);
        return xe != null && dsXe.remove(xe);
    }

    // Sửa xe
    public boolean suaXe(String maXe, PhuongTien xeMoi) {
        if (xeMoi == null) return false;

        String maCu = Ktradulieu.chuanHoaMa(maXe);
        String maMoi = Ktradulieu.chuanHoaMa(xeMoi.getMaXe());

        if (maCu.isEmpty() || maMoi.isEmpty()) return false;

        for (int i = 0; i < dsXe.size(); i++) {
            String maHienTai = Ktradulieu.chuanHoaMa(dsXe.get(i).getMaXe());

            if (maHienTai.equals(maCu)) {
                PhuongTien trung = traCuuXe(maMoi);

                if (trung != null && trung != dsXe.get(i))
                    return false;

                dsXe.set(i, xeMoi);
                return true;
            }
        }

        return false;
    }

    // Hiển thị danh sách xe
    public void hienThiDSXe() {
        if (dsXe.isEmpty()) {
            System.out.println("Chưa có phương tiện nào!");
            return;
        }

        for (PhuongTien xe : dsXe)
            System.out.println(xe);
    }

    public ArrayList<PhuongTien> getDsXe() {
        return new ArrayList<>(dsXe);
    }

    @Override
    public String toDongFile() {
        return "VC|" + maDT + "|" + tenDT + "|" + diaChi + "|"
                + email + "|" + maSoThue + "|" + dsXe.size();
    }

    public static DonViVanChuyen tuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);

        if (t.length != 7 || !t[0].equals("VC"))
            throw new IllegalArgumentException("Dòng dữ liệu vận chuyển không hợp lệ!");

        return new DonViVanChuyen(
                t[1], t[2], t[3], t[4], t[5], new ArrayList<>()
        );
    }

    @Override
    public String toString() {
        return super.toString() + " | Số lượng xe: " + dsXe.size();
    }
}
