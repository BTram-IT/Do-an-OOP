import java.util.ArrayList;
import java.io.*;
import java.nio.charset.StandardCharsets;

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

    public DoiTac timTheoMa(String maDT) {
        if (maDT == null || maDT.trim().isEmpty()) return null;

        String ma = Ktradulieu.chuanHoaMa(maDT);

        for (DoiTac dt : dsDoiTac) {
            if (dt != null && ma.equals(Ktradulieu.chuanHoaMa(dt.getMaDT()))) {
                return dt;
            }
        }
        return null;
    }

    public void them(DoiTac dt) {
        if (dt == null) throw new IllegalArgumentException("Đối tác không hợp lệ!");
        if (dt.getMaDT() == null || dt.getMaDT().trim().isEmpty())
            throw new IllegalArgumentException("Mã đối tác không được rỗng!");
        if (timTheoMa(dt.getMaDT()) != null)
            throw new IllegalArgumentException("Mã đối tác đã tồn tại!");

        dsDoiTac.add(dt);
    }

    public boolean xoa(String maDT) {
        DoiTac dt = timTheoMa(maDT);
        if (dt == null) return false;

        dsDoiTac.remove(dt);
        return true;
    }

    public boolean sua(String maDT, DoiTac moi) {
        if (moi == null) throw new IllegalArgumentException("Đối tác mới không hợp lệ!");

        DoiTac cu = timTheoMa(maDT);
        if (cu == null) return false;

        String maCu = Ktradulieu.chuanHoaMa(maDT);
        String maMoi = Ktradulieu.chuanHoaMa(moi.getMaDT());

        if (!maCu.equals(maMoi))
            throw new IllegalArgumentException("Không được thay đổi mã đối tác!");

        dsDoiTac.set(dsDoiTac.indexOf(cu), moi);
        return true;
    }

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

    public void ghiFile(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath),
                        StandardCharsets.UTF_8))) {

            for (DoiTac dt : dsDoiTac) {
                bw.write(dt.toDongFile());
                bw.newLine();
            }

            System.out.println("Lưu danh sách đối tác thành công!");

        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e.getMessage());
        }
    }

    public void docFile(String filePath) {
        ArrayList<DoiTac> dsTam = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath),
                        StandardCharsets.UTF_8))) {

            String dong;

            while ((dong = br.readLine()) != null) {
                if (dong.trim().isEmpty()) continue;

                try {
                    String loai = dong.split("\\|", 2)[0];

                    DoiTac dt = null;

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
                            if (item.getMaDT().equalsIgnoreCase(dt.getMaDT())) {
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