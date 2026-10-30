import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class DSNhanVien implements IQuanLy<NhanVien> {
    // 1. Mảng động lưu toàn bộ nhân viên
    private ArrayList<NhanVien> dsNhanVien;

    // 2. Mảng động lưu dữ liệu chấm công
    private ArrayList<ChamCong> dsChamCong;

    // 3. Mảng tĩnh lưu danh mục các Ca Làm việc cố định trong ngày (Thỏa mãn yêu cầu mảng tĩnh)
    private CaLam[] danhSachCaLam;
    private int soLuongCaLam;
    private static final int MAX_CA = 5;

    public DSNhanVien() {
        this.dsNhanVien = new ArrayList<>();
        this.dsChamCong = new ArrayList<>();
        this.danhSachCaLam = new CaLam[MAX_CA];
        this.soLuongCaLam = 0;

        // Khởi tạo các ca làm mặc định
        khoiTaoCaLamMacDinh();
    }

    private void khoiTaoCaLamMacDinh() {
        danhSachCaLam[soLuongCaLam++] = new CaLam("CA01", "Ca Sang", LocalTime.of(6, 0), LocalTime.of(12, 0), 1.0);
        danhSachCaLam[soLuongCaLam++] = new CaLam("CA02", "Ca Chieu", LocalTime.of(12, 0), LocalTime.of(18, 0), 1.0);
        danhSachCaLam[soLuongCaLam++] = new CaLam("CA03", "Ca Toi", LocalTime.of(18, 0), LocalTime.of(23, 0), 1.2);
    }

    public ArrayList<NhanVien> getDsNhanVien() { return dsNhanVien; }
    public ArrayList<ChamCong> getDsChamCong() { return dsChamCong; }

    @Override
    public void them(NhanVien nv) {
        dsNhanVien.add(nv);
    }

    @Override
    public boolean sua(String maNV, NhanVien itemMoi) {
        for (int i = 0; i < dsNhanVien.size(); i++) {
            if (dsNhanVien.get(i).getMaNV().equalsIgnoreCase(maNV)) {
                dsNhanVien.set(i, itemMoi);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean xoa(String maNV) {
        for (int i = 0; i < dsNhanVien.size(); i++) {
            if (dsNhanVien.get(i).getMaNV().equalsIgnoreCase(maNV)) {
                dsNhanVien.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public NhanVien timKiemTheoMa(String maNV) {
        for (NhanVien nv : dsNhanVien) {
            if (nv.getMaNV().equalsIgnoreCase(maNV)) return nv;
        }
        return null;
    }

    public ArrayList<NhanVien> timKiemTheoTen(String ten) {
        ArrayList<NhanVien> res = new ArrayList<>();
        for (NhanVien nv : dsNhanVien) {
            if (nv.getTen().toLowerCase().contains(ten.toLowerCase())) {
                res.add(nv);
            }
        }
        return res;
    }

    // Nghiệp vụ ca làm & chấm công
    public void xemDanhSachCaLam() {
        System.out.println("=== DANH MỤC CA LÀM VIỆC (MẢNG TĨNH) ===");
        for (int i = 0; i < soLuongCaLam; i++) {
            System.out.println(danhSachCaLam[i]);
        }
    }

    public void chamCongNhanVien(Scanner sc) {
        System.out.println("=== CHẤM CÔNG NHÂN VIÊN ===");
        ChamCong cc = new ChamCong();
        cc.nhap(sc);
        dsChamCong.add(cc);
        System.out.println("Đã ghi nhận chấm công thành công!");
    }

    public void xemNhatKyChamCong() {
        System.out.println("=== NHẬT KÝ CHẤM CÔNG ===");
        if (dsChamCong.isEmpty()) {
            System.out.println("Chưa có bản ghi chấm công nào.");
            return;
        }
        for (ChamCong cc : dsChamCong) {
            System.out.println(cc);
        }
    }

    @Override
    public void xuatDanhSach() {
        System.out.println("=========================================================================================");
        System.out.printf("%-8s | %-20s | %-12s | %-12s | %-15s\n", "Ma NV", "Ho Ten", "Chuc Vu", "So Ca Lam", "Luong Thuc Nhan");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (NhanVien nv : dsNhanVien) {
            System.out.printf("%-8s | %-20s | %-12s | %8d ca | %,13.0f VND\n",
                    nv.getMaNV(), nv.getTen(), nv.getChucVu(), nv.getSoCaLam(), nv.tinhLuong());
        }
        System.out.println("=========================================================================================");
    }

    // Đọc & Ghi file text cho Nhân Viên
    public void ghiFile(String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (NhanVien nv : dsNhanVien) {
                // maNV,ten,sdt,diaChi,email,ngaySinh,luongCoBan,chucVu,soCaLam
                pw.println(String.format("%s,%s,%s,%s,%s,%s,%.0f,%s,%d",
                        nv.getMaNV(), nv.getTen(), nv.getSdt(), nv.getDiaChi(),
                        nv.getEmail(), nv.getNgaySinh(), nv.getLuongCoBan(), nv.getChucVu(), nv.getSoCaLam()));
            }
            System.out.println("Luu danh sach nhan vien vao file thanh cong!");
        } catch (IOException e) {
            System.out.println("Loi ghi file nhan vien: " + e.getMessage());
        }
    }

    public void docFile(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 9) {
                    NhanVien nv = new NhanVien(p[1], p[2], p[3], p[4], LocalDate.parse(p[5]),
                            p[0], Double.parseDouble(p[6]), LocalDate.now(), 2, p[7]);
                    nv.setSoCaLam(Integer.parseInt(p[8]));
                    them(nv);
                }
            }
        } catch (Exception e) {
            System.out.println("Loi doc file nhan vien: " + e.getMessage());
        }
    }
}
