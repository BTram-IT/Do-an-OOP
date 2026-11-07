
import java.util.ArrayList;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class DSNhaCungCap {
    private ArrayList<NhaCungCap> danhSachNhaCungCap = new ArrayList<>();

    public ArrayList<NhaCungCap> getDanhSachNhaCungCap() {
        return new ArrayList<>(danhSachNhaCungCap);
    }

    public void them(NhaCungCap ncc) {
        if (ncc == null) throw new IllegalArgumentException("Nhà cung cấp không hợp lệ!");
        String ma = SanPham.chuanHoaMa(ncc.getMaDT());
        if (ma == null || ma.isEmpty()) throw new IllegalArgumentException("Mã không được rỗng!");
        if (tonTai(ma)) throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại!");
        danhSachNhaCungCap.add(ncc);
    }

    public NhaCungCap timTheoMa(String ma) {
        if (ma == null || ma.trim().isEmpty()) return null;
        String maChuan = SanPham.chuanHoaMa(ma);
        for (NhaCungCap ncc : danhSachNhaCungCap)
            if (maChuan.equals(SanPham.chuanHoaMa(ncc.getMaDT()))) return ncc;
        return null;
    }

    public ArrayList<NhaCungCap> timTheoTen(String ten) {
        ArrayList<NhaCungCap> kq = new ArrayList<>();
        if (ten == null || ten.trim().isEmpty()) return kq;
        for (NhaCungCap ncc : danhSachNhaCungCap)
            if (ncc.getTenDT().toLowerCase().contains(ten.trim().toLowerCase()))
                kq.add(ncc);
        return kq;
    }

    public boolean tonTai(String ma) {
        return timTheoMa(ma) != null;
    }

    public boolean sua(String ma, NhaCungCap moi) {
        if (moi == null || ma == null) return false;
        NhaCungCap cu = timTheoMa(ma);
        if (cu == null) return false;

        String maMoi = SanPham.chuanHoaMa(moi.getMaDT());
        if (!SanPham.chuanHoaMa(cu.getMaDT()).equals(maMoi))
            throw new IllegalArgumentException("Không được thay đổi mã nhà cung cấp!");

        danhSachNhaCungCap.set(danhSachNhaCungCap.indexOf(cu), moi);
        return true;
    }

    public boolean xoa(String ma) {
        NhaCungCap ncc = timTheoMa(ma);
        if (ncc == null) return false;
        danhSachNhaCungCap.remove(ncc);
        return true;
    }

    public void xuat() {
        if (danhSachNhaCungCap.isEmpty()) {
            System.out.println("(Danh sách nhà cung cấp trống)");
            return;
        }
        System.out.println("===== DANH SÁCH NHÀ CUNG CẤP =====");
        for (NhaCungCap ncc : danhSachNhaCungCap)
            System.out.println(ncc);
    }

    public void ghiFile(String filePath) {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            for (NhaCungCap ncc : danhSachNhaCungCap)
                pw.println(ncc.toDongFile());

            if (pw.checkError()) throw new IOException("Không thể ghi file!");
            System.out.println("Lưu danh sách nhà cung cấp thành công!");
        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
        }
    }

    public void docFile(String filePath) {
        int thanhCong = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String dong;
            int soDong = 0;
            while ((dong = br.readLine()) != null) {
                soDong++;
                if (dong.trim().isEmpty()) continue;
                try {
                    NhaCungCap ncc = NhaCungCap.tuDongFile(dong);
                    if (ncc == null || tonTai(ncc.getMaDT())) {
                        System.out.println("Bỏ qua dòng " + soDong + ": Mã trùng hoặc dữ liệu không hợp lệ!");
                        continue;
                    }
                    them(ncc);
                    thanhCong++;
                } catch (RuntimeException e) {
                    System.out.println("Lỗi dòng " + soDong + ": " + e.getMessage());
                }
            }
            System.out.println("Đọc file hoàn tất. Đã thêm: " + thanhCong + " nhà cung cấp.");
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }
    }
}
