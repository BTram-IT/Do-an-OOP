package doitac;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import util.Ktradulieu;

public class DSDoiTac {
    private ArrayList<DoiTac> dsDoiTac;

    public DSDoiTac() {
        dsDoiTac = new ArrayList<>();
    }

    public ArrayList<DoiTac> getDsDoiTac() {
        return new ArrayList<>(dsDoiTac);
    }

    public int getSoLuong() {
        return dsDoiTac.size();
    }

    // Tìm đối tác theo mã
    public DoiTac timTheoMa(String maDT) {
        String ma = Ktradulieu.chuanHoaMa(maDT);
        if (ma.isEmpty()) return null;

        for (DoiTac dt : dsDoiTac) {
            if (ma.equals(dt.getMaDT())) return dt;
        }
        return null;
    }

    // Thêm đối tác
    public void them(DoiTac dt) {
        if (dt == null || dt.getMaDT() == null || dt.getMaDT().trim().isEmpty())
            throw new IllegalArgumentException("Đối tác không hợp lệ!");

        if (timTheoMa(dt.getMaDT()) != null)
            throw new IllegalArgumentException("Mã đối tác đã tồn tại!");

        dsDoiTac.add(dt);
    }

    // Xóa đối tác
    public boolean xoa(String maDT) {
        DoiTac dt = timTheoMa(maDT);
        return dt != null && dsDoiTac.remove(dt);
    }

    // Sửa đối tác
    public boolean sua(String maDT, DoiTac moi) {
        if (moi == null)
            throw new IllegalArgumentException("Đối tác mới không hợp lệ!");

        DoiTac cu = timTheoMa(maDT);
        if (cu == null) return false;

        if (!cu.getLoaiDT().equals(moi.getLoaiDT()))
            throw new IllegalArgumentException("Không được thay đổi loại đối tác!");

        if (!cu.getMaDT().equals(moi.getMaDT()))
            throw new IllegalArgumentException("Không được thay đổi mã đối tác!");

        dsDoiTac.set(dsDoiTac.indexOf(cu), moi);
        return true;
    }

    // Xuất danh sách
    public void xuatDanhSach() {
        if (dsDoiTac.isEmpty()) {
            System.out.println("Danh sách đối tác trống!");
            return;
        }

        for (DoiTac dt : dsDoiTac) {
            System.out.println(dt);
            System.out.println("-------------------------");
        }
    }

    // Ghi file
    public void ghiFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            for (DoiTac dt : dsDoiTac) {
                bw.write(dt.toDongFile());
                bw.newLine();
            }

            System.out.println("Lưu danh sách đối tác thành công!");

        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
        }
    }

    // Đọc file
    public void docFile(String filePath) {
        ArrayList<DoiTac> dsTam = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            String dong;

            while ((dong = br.readLine()) != null) {
                if (dong.trim().isEmpty()) continue;

                try {
                    String loai = dong.split("\\|", 2)[0];
                    DoiTac dt;

                    switch (loai) {
                        case "NCC":
                            dt = NhaCungCap.tuDongFile(dong);
                            break;
                        case "VC":
                            dt = DonViVanChuyen.tuDongFile(dong);
                            break;
                        default:
                            System.out.println("Loại đối tác không hợp lệ: " + loai);
                            continue;
                    }

                    if (dt != null) {
                        boolean trung = false;

                        for (DoiTac item : dsTam) {
                            if (item.getMaDT().equals(dt.getMaDT())) {
                                trung = true;
                                break;
                            }
                        }

                        if (!trung) dsTam.add(dt);
                    }

                } catch (RuntimeException e) {
                    System.out.println("Bỏ qua dòng dữ liệu lỗi: " + dong);
                }
            }

            dsDoiTac = dsTam;
            System.out.println("Đọc danh sách đối tác thành công!");

        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
        }
    }
}
