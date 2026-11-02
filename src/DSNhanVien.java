
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DSNhanVien implements IQuanLy<NhanVien> {

    // Danh sach nhan vien
    private ArrayList<NhanVien> dsNhanVien;

    // Constructor
    public DSNhanVien() {
        this.dsNhanVien = new ArrayList<>();
    }

    // Getter
    public ArrayList<NhanVien> getDsNhanVien() {
        return dsNhanVien;
    }

    // Them nhan vien
    @Override
    public void them(NhanVien nv) {
        if (nv == null) {
            System.out.println("Nhan vien khong hop le!");
            return;
        }

        if (timKiemTheoMa(nv.getMaNV()) != null) {
            System.out.println("Ma nhan vien da ton tai!");
            return;
        }

        dsNhanVien.add(nv);
        System.out.println("Them nhan vien thanh cong!");
    }

    // Sua nhan vien
    @Override
    public boolean sua(String maNV, NhanVien itemMoi) {
        if (maNV == null || itemMoi == null) {
            return false;
        }

        for (int i = 0; i < dsNhanVien.size(); i++) {
            NhanVien nv = dsNhanVien.get(i);

            if (nv.getMaNV().equalsIgnoreCase(maNV)) {

                // Kiem tra ma moi co bi trung voi nhan vien khac khong
                NhanVien nvTrung = timKiemTheoMa(itemMoi.getMaNV());

                if (nvTrung != null && nvTrung != nv) {
                    System.out.println("Ma nhan vien moi da ton tai!");
                    return false;
                }

                dsNhanVien.set(i, itemMoi);
                return true;
            }
        }

        return false;
    }

    // Xoa nhan vien
    @Override
    public boolean xoa(String maNV) {
        if (maNV == null) {
            return false;
        }

        for (int i = 0; i < dsNhanVien.size(); i++) {
            if (dsNhanVien.get(i).getMaNV().equalsIgnoreCase(maNV)) {
                dsNhanVien.remove(i);
                return true;
            }
        }

        return false;
    }

    // Tim nhan vien theo ma
    @Override
    public NhanVien timKiemTheoMa(String maNV) {
        if (maNV == null) {
            return null;
        }

        for (NhanVien nv : dsNhanVien) {
            if (nv.getMaNV().equalsIgnoreCase(maNV)) {
                return nv;
            }
        }

        return null;
    }

    // Tim nhan vien theo ten
    public ArrayList<NhanVien> timKiemTheoTen(String ten) {
        ArrayList<NhanVien> ketQua = new ArrayList<>();

        if (ten == null) {
            return ketQua;
        }

        for (NhanVien nv : dsNhanVien) {
            if (nv.getTen().toLowerCase().contains(ten.trim().toLowerCase())) {
                ketQua.add(nv);
            }
        }

        return ketQua;
    }

    // Xuat danh sach nhan vien
    @Override
    public void xuatDanhSach() {
        System.out.println("==========================================================================");
        System.out.println("Ma NV | Ho Ten | Chuc Vu | Luong Co Ban | Luong Thuc Nhan");
        System.out.println("--------------------------------------------------------------------------");

        for (NhanVien nv : dsNhanVien) {
            System.out.println(
                    nv.getMaNV() + " | "
                            + nv.getTen() + " | "
                            + nv.getChucVu() + " | "
                            + String.format("%,.0f", nv.getLuongCoBan()) + " VND | "
                            + String.format("%,.0f", nv.Tinhluong()) + " VND"
            );
        }

        System.out.println("==========================================================================");
    }

    // Ghi danh sach nhan vien vao file
    public void ghiFile(String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {

            for (NhanVien nv : dsNhanVien) {
                // maNV,ten,sdt,diaChi,email,ngaySinh,luongCoBan,chucVu,ngayVaoLam
                pw.println(
                        nv.getMaNV() + ","
                                + nv.getTen() + ","
                                + nv.getSdt() + ","
                                + nv.getDiaChi() + ","
                                + nv.getEmail() + ","
                                + nv.getNgaySinh() + ","
                                + nv.getLuongCoBan() + ","
                                + nv.getChucVu() + ","
                                + nv.getNgayVaoLam()
                );
            }

            System.out.println("Luu danh sach nhan vien vao file thanh cong!");

        } catch (IOException e) {
            System.out.println("Loi ghi file nhan vien: " + e.getMessage());
        }
    }

    // Doc danh sach nhan vien tu file
    public void docFile(String filePath) {
        File f = new File(filePath);

        if (!f.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);

                if (p.length >= 9) {
                    try {
                        NhanVien nv = new NhanVien(
                                p[1],
                                p[2],
                                p[3],
                                p[4],
                                LocalDate.parse(p[5]),
                                p[0],
                                Double.parseDouble(p[6]),
                                LocalDate.parse(p[8]),
                                p[7]
                        );

                        // Khong them trung ma nhan vien
                        if (timKiemTheoMa(nv.getMaNV()) == null) {
                            dsNhanVien.add(nv);
                        }

                    } catch (Exception e) {
                        System.out.println("Bo qua dong du lieu khong hop le: " + line);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Loi doc file nhan vien: " + e.getMessage());
        }
    }
}