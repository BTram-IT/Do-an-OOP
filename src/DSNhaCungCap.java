
import java.util.ArrayList;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class DSNhaCungCap {
    private ArrayList<NhaCungCap> danhSachNhaCungCap = new ArrayList<>();

    public ArrayList<NhaCungCap> getDanhSachNhaCungCap() {
        return new ArrayList<>(danhSachNhaCungCap);
    }

    // Thêm nhà cung cấp
    public void them(NhaCungCap ncc) {
        if (ncc == null)
            throw new IllegalArgumentException("Nhà cung cấp không hợp lệ!");

        String ma = Ktradulieu.chuanHoaMa(ncc.getMaDT());

        if (ma.isEmpty())
            throw new IllegalArgumentException("Mã không được rỗng!");

        if (tonTai(ma))
            throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại!");

        danhSachNhaCungCap.add(ncc);
    }

    // Tìm theo mã
    public NhaCungCap timTheoMa(String ma) {
        String maChuan = Ktradulieu.chuanHoaMa(ma);

        if (maChuan.isEmpty()) return null;

        for (NhaCungCap ncc : danhSachNhaCungCap) {
            if (maChuan.equals(Ktradulieu.chuanHoaMa(ncc.getMaDT())))
                return ncc;
        }

        return null;
    }

    // Tìm theo tên
    public ArrayList<NhaCungCap> timTheoTen(String ten) {
        ArrayList<NhaCungCap> kq = new ArrayList<>();

        if (ten == null || ten.trim().isEmpty()) return kq;

        String tuKhoa = ten.trim().toLowerCase();

        for (NhaCungCap ncc : danhSachNhaCungCap) {
            if (ncc.getTenDT().toLowerCase().contains(tuKhoa))
                kq.add(ncc);
        }

        return kq;
    }

    // Kiểm tra tồn tại
    public boolean tonTai(String ma) {
        return timTheoMa(ma) != null;
    }

    // Sửa nhà cung cấp
    public boolean sua(String ma, NhaCungCap moi) {
        if (moi == null || ma == null) return false;

        NhaCungCap cu = timTheoMa(ma);

        if (cu == null) return false;

        String maMoi = Ktradulieu.chuanHoaMa(moi.getMaDT());
        String maCu = Ktradulieu.chuanHoaMa(cu.getMaDT());

        if (!maCu.equals(maMoi))
            throw new IllegalArgumentException("Không được thay đổi mã nhà cung cấp!");

        danhSachNhaCungCap.set(danhSachNhaCungCap.indexOf(cu), moi);
        return true;
    }

    // Xóa nhà cung cấp
    public boolean xoa(String ma) {
        NhaCungCap ncc = timTheoMa(ma);

        if (ncc == null) return false;

        danhSachNhaCungCap.remove(ncc);
        return true;
    }

    // Xuất danh sách
    public void xuat() {
        if (danhSachNhaCungCap.isEmpty()) {
            System.out.println("(Danh sách nhà cung cấp trống)");
            return;
        }

        System.out.println("===== DANH SÁCH NHÀ CUNG CẤP =====");

        for (NhaCungCap ncc : danhSachNhaCungCap)
            System.out.println(ncc);
    }

    // Ghi file
    public void ghiFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            for (NhaCungCap ncc : danhSachNhaCungCap) {
                bw.write(ncc.toDongFile());
                bw.newLine();
            }

            System.out.println("Lưu danh sách nhà cung cấp thành công!");

        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
        }
    }

    // Đọc file
    public void docFile(String filePath) {
        ArrayList<NhaCungCap> dsTam = new ArrayList<>();
        int thanhCong = 0;
        int soDong = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String dong;

            while ((dong = br.readLine()) != null) {
                soDong++;

                if (dong.trim().isEmpty()) continue;

                try {
                    NhaCungCap ncc = NhaCungCap.tuDongFile(dong);

                    boolean trung = false;

                    for (NhaCungCap item : dsTam) {
                        if (Ktradulieu.chuanHoaMa(item.getMaDT())
                                .equals(Ktradulieu.chuanHoaMa(ncc.getMaDT()))) {
                            trung = true;
                            break;
                        }
                    }

                    if (trung) {
                        System.out.println("Bỏ qua dòng " + soDong + ": Mã bị trùng!");
                        continue;
                    }

                    dsTam.add(ncc);
                    thanhCong++;

                } catch (RuntimeException e) {
                    System.out.println("Lỗi dòng " + soDong + ": " + e.getMessage());
                }
            }

            danhSachNhaCungCap = dsTam;

            System.out.println("Đọc file hoàn tất. Đã thêm: "
                    + thanhCong + " nhà cung cấp.");

        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }
    }
}
