import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public class DsKhachHang implements IQuanLy<Khachhang> {
    private ArrayList<Khachhang> dsKhachHang;
    private Khachhang[] dsKhachHangVip;
    private int soLuongVip;
    private static final int MAX_VIP = 50;

    public DsKhachHang() {
        dsKhachHang = new ArrayList<>();
        dsKhachHangVip = new Khachhang[MAX_VIP];
        soLuongVip = 0;
    }

    public ArrayList<Khachhang> getDsKhachHang() {
        return new ArrayList<>(dsKhachHang);
    }

    @Override
    public void them(Khachhang kh) {
        if (kh == null) throw new IllegalArgumentException("Khách hàng không hợp lệ!");
        if (timKiemTheoMa(kh.getMaKH()) != null)
            throw new IllegalArgumentException("Mã khách hàng đã tồn tại!");
        dsKhachHang.add(kh);
        capNhatDanhSachVip();
    }

    @Override
    public boolean sua(String maKH, Khachhang itemMoi) {
        Khachhang cu = timKiemTheoMa(maKH);
        if (cu == null || itemMoi == null) return false;
        if (!cu.getMaKH().equalsIgnoreCase(itemMoi.getMaKH()))
            throw new IllegalArgumentException("Không được đổi mã khách hàng!");
        dsKhachHang.set(dsKhachHang.indexOf(cu), itemMoi);
        capNhatDanhSachVip();
        return true;
    }

    @Override
    public boolean xoa(String maKH) {
        Khachhang kh = timKiemTheoMa(maKH);
        if (kh == null) return false;
        dsKhachHang.remove(kh);
        capNhatDanhSachVip();
        return true;
    }

    @Override
    public Khachhang timKiemTheoMa(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) return null;
        maKH = Ktradulieu.chuanHoaMa(maKH);
        for (Khachhang kh : dsKhachHang)
            if (kh.getMaKH().equalsIgnoreCase(maKH)) return kh;
        return null;
    }

    public ArrayList<Khachhang> timKiemTheoTen(String ten) {
        ArrayList<Khachhang> kq = new ArrayList<>();
        if (ten == null || ten.trim().isEmpty()) return kq;
        for (Khachhang kh : dsKhachHang)
            if (kh.getTen().toLowerCase(Locale.ROOT).contains(ten.trim().toLowerCase(Locale.ROOT)))
                kq.add(kh);
        return kq;
    }

    public void capNhatDanhSachVip() {
        soLuongVip = 0;
        for (int i = 0; i < MAX_VIP; i++) dsKhachHangVip[i] = null;
        for (Khachhang kh : dsKhachHang) {
            String hang = kh.getThuHang();
            if (hang.equalsIgnoreCase("Vang") || hang.equalsIgnoreCase("Kim Cuong")) {
                if (soLuongVip < MAX_VIP) dsKhachHangVip[soLuongVip++] = kh;
            }
        }
    }

    public void xuatDanhSachVip() {
        System.out.println("=== DANH SACH KHACH HANG VIP ===");
        if (soLuongVip == 0) {
            System.out.println("Chua co khach hang VIP.");
            return;
        }
        for (int i = 0; i < soLuongVip; i++) System.out.println(dsKhachHangVip[i]);
    }

    @Override
    public void xuatDanhSach() {
        System.out.println("=========================================================================================");
        System.out.printf("%-8s | %-20s | %-12s | %-12s | %-15s%n",
                "Ma KH", "Ho Ten", "SDT", "Thu Hang", "Tong Chi");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Khachhang kh : dsKhachHang)
            System.out.printf("%-8s | %-20s | %-12s | %-12s | %,13.0f VND%n",
                    kh.getMaKH(), kh.getTen(), kh.getSdt(), kh.getThuHang(), kh.getTongTienMua());
        System.out.println("=========================================================================================");
    }

    private String csv(String s) {
        if (s == null) s = "";
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private ArrayList<String> tachCSV(String line) {
        ArrayList<String> kq = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean trongNgoac = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (trongNgoac && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    sb.append('"');
                    i++;
                } else trongNgoac = !trongNgoac;
            } else if (c == ',' && !trongNgoac) {
                kq.add(sb.toString());
                sb.setLength(0);
            } else sb.append(c);
        }
        if (trongNgoac) throw new IllegalArgumentException("Lỗi định dạng CSV!");
        kq.add(sb.toString());
        return kq;
    }

    public void ghiFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) return;
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            for (Khachhang kh : dsKhachHang)
                pw.println(csv(kh.getMaKH()) + "," + csv(kh.getTen()) + "," + csv(kh.getSdt())
                        + "," + csv(kh.getDiaChi()) + "," + csv(kh.getEmail())
                        + "," + csv(kh.getNgaySinh().toString()) + "," + csv(kh.getNgayDangKy().toString())
                        + "," + csv(String.format(Locale.ROOT, "%.0f", kh.getTongTienMua())));
            if (pw.checkError()) throw new IOException("Không thể ghi file!");
            System.out.println("Luu file thanh cong!");
        } catch (IOException e) {
            System.out.println("Loi ghi file: " + e.getMessage());
        }
    }

    public void docFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) return;
        File file = new File(filePath);
        if (!file.exists()) return;

        int dong = 0, thanhCong = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                dong++;
                if (line.trim().isEmpty()) continue;
                try {
                    ArrayList<String> p = tachCSV(line);
                    if (p.size() != 8) throw new IllegalArgumentException("Sai số cột!");
                    Khachhang kh = new Khachhang(p.get(1), p.get(2), p.get(3), p.get(4),
                            LocalDate.parse(p.get(5)), p.get(0), LocalDate.parse(p.get(6)),
                            Double.parseDouble(p.get(7)));
                    if (timKiemTheoMa(kh.getMaKH()) != null) continue;
                    them(kh);
                    thanhCong++;
                } catch (RuntimeException e) {
                    System.out.println("Loi dong " + dong + ": " + e.getMessage());
                }
            }
            System.out.println("Doc file hoan tat. Da them: " + thanhCong);
        } catch (IOException e) {
            System.out.println("Loi doc file: " + e.getMessage());
        }
    }
}
