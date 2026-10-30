import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class DSKhachHang implements IQuanLy<KhachHang> {
    // 1. Mảng động lưu toàn bộ khách hàng
    private ArrayList<KhachHang> dsKhachHang;

    // 2. Mảng tĩnh lưu danh sách khách hàng VIP / Kim cương (Thỏa mãn yêu cầu mảng tĩnh)
    private KhachHang[] dsKhachHangVip;
    private int soLuongVip;
    private static final int MAX_VIP = 50;

    public DSKhachHang() {
        this.dsKhachHang = new ArrayList<>();
        this.dsKhachHangVip = new KhachHang[MAX_VIP];
        this.soLuongVip = 0;
    }

    public ArrayList<KhachHang> getDsKhachHang() { return dsKhachHang; }

    @Override
    public void them(KhachHang kh) {
        dsKhachHang.add(kh);
        capNhatDanhSachVip();
    }

    @Override
    public boolean sua(String maKH, KhachHang itemMoi) {
        for (int i = 0; i < dsKhachHang.size(); i++) {
            if (dsKhachHang.get(i).getMaKH().equalsIgnoreCase(maKH)) {
                dsKhachHang.set(i, itemMoi);
                capNhatDanhSachVip();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean xoa(String maKH) {
        for (int i = 0; i < dsKhachHang.size(); i++) {
            if (dsKhachHang.get(i).getMaKH().equalsIgnoreCase(maKH)) {
                dsKhachHang.remove(i);
                capNhatDanhSachVip();
                return true;
            }
        }
        return false;
    }

    @Override
    public KhachHang timKiemTheoMa(String maKH) {
        for (KhachHang kh : dsKhachHang) {
            if (kh.getMaKH().equalsIgnoreCase(maKH)) return kh;
        }
        return null;
    }

    // Tìm kiếm khách hàng theo tên (gần đúng)
    public ArrayList<KhachHang> timKiemTheoTen(String ten) {
        ArrayList<KhachHang> ketQua = new ArrayList<>();
        for (KhachHang kh : dsKhachHang) {
            if (kh.getTen().toLowerCase().contains(ten.toLowerCase())) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    // Cập nhật danh sách mảng tĩnh VIP (Vàng & Kim Cương)
    public void capNhatDanhSachVip() {
        soLuongVip = 0;
        for (KhachHang kh : dsKhachHang) {
            if (kh.getThuHang().equalsIgnoreCase("Vang") || kh.getThuHang().equalsIgnoreCase("Kim Cuong")) {
                if (soLuongVip < MAX_VIP) {
                    dsKhachHangVip[soLuongVip++] = kh;
                }
            }
        }
    }

    public void xuatDanhSachVip() {
        System.out.println("=== DANH SACH KHACH HANG VIP (MANG TINH) ===");
        if (soLuongVip == 0) {
            System.out.println("Chua co khach hang VIP dat hang Vang tro len.");
            return;
        }
        for (int i = 0; i < soLuongVip; i++) {
            System.out.println(dsKhachHangVip[i]);
        }
    }

    @Override
    public void xuatDanhSach() {
        System.out.println("=========================================================================================");
        System.out.printf("%-8s | %-20s | %-12s | %-12s | %-15s\n", "Ma KH", "Ho Ten", "SDT", "Thu Hang", "Tong Chi");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (KhachHang kh : dsKhachHang) {
            System.out.printf("%-8s | %-20s | %-12s | %-12s | %,13.0f VND\n",
                    kh.getMaKH(), kh.getTen(), kh.getSdt(), kh.getThuHang(), kh.getTongTienMua());
        }
        System.out.println("=========================================================================================");
    }

    // Đọc & Ghi file text cho Khách Hàng
    public void ghiFile(String filePath) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
            for (KhachHang kh : dsKhachHang) {
                // maKH,ten,sdt,diaChi,email,ngaySinh,ngayDangKy,tongTienMua
                pw.println(String.format("%s,%s,%s,%s,%s,%s,%s,%.0f",
                        kh.getMaKH(), kh.getTen(), kh.getSdt(), kh.getDiaChi(),
                        kh.getEmail(), kh.getNgaySinh(), kh.getNgayDangKy(), kh.getTongTienMua()));
            }
            System.out.println("Luu danh sach khach hang vao file thanh cong!");
        } catch (IOException e) {
            System.out.println("Loi ghi file khach hang: " + e.getMessage());
        }
    }

    public void docFile(String filePath) {
        File f = new File(filePath);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 8) {
                    KhachHang kh = new KhachHang(p[1], p[2], p[3], p[4],
                            LocalDate.parse(p[5]), p[0], LocalDate.parse(p[6]), Double.parseDouble(p[7]));
                    them(kh);
                }
            }
        } catch (Exception e) {
            System.out.println("Loi doc file khach hang: " + e.getMessage());
        }
    }
}
