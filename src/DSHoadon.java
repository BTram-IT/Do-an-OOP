import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class DSHoadon {
    private ArrayList<HoaDon> ds;
    public static final String FILE_MAC_DINH = "hoadon.txt";

<<<<<<< HEAD
=======
    // Constructor mặc định
>>>>>>> origin/main
    public DSHoadon() {
        this.ds = new ArrayList<>();
    }

<<<<<<< HEAD
=======
    // Lấy số lượng hóa đơn
>>>>>>> origin/main
    public int getSoLuong() {
        return ds.size();
    }

<<<<<<< HEAD
    public int size() {
        return ds.size();
    }

=======
    // Lấy bản sao danh sách hóa đơn
>>>>>>> origin/main
    public ArrayList<HoaDon> getDanhSach() {
        return new ArrayList<>(ds);
    }

    // Lấy danh sách gốc (tương thích với KeToan.java)
    public ArrayList<HoaDon> getDs() {
        return new ArrayList<>(ds);
    }

    public ArrayList<Hoadonban> getDanhSachHoaDonBan() {
        ArrayList<Hoadonban> ketQua = new ArrayList<>();
        for (HoaDon hd : ds)
            if (hd instanceof Hoadonban) ketQua.add((Hoadonban) hd);
        return ketQua;
    }

    public ArrayList<Hoadonban> layHoaDonBan() {
        return getDanhSachHoaDonBan();
    }

    public ArrayList<HoadonNhap> layHoaDonNhap() {
        ArrayList<HoadonNhap> ketQua = new ArrayList<>();
        for (HoaDon hd : ds)
            if (hd instanceof HoadonNhap) ketQua.add((HoadonNhap) hd);
        return ketQua;
    }

<<<<<<< HEAD
    public HoaDon timTheoMa(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) return null;
        String maChuan = Ktradulieu.chuanHoaMa(maHD);
        for (HoaDon hd : ds) {
            if (hd != null && hd.getMaHD().equalsIgnoreCase(maChuan))
=======
    // Lấy riêng danh sách các Hóa đơn bán
    public ArrayList<Hoadonban> getDanhSachHoaDonBan() {
        ArrayList<Hoadonban> ketQua = new ArrayList<>();
        for (HoaDon hd : ds) {
            if (hd instanceof Hoadonban) {
                ketQua.add((Hoadonban) hd);
            }
        }
        return ketQua;
    }

    // Tìm hóa đơn theo mã
    public HoaDon timTheoMa(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) {
            return null;
        }

        String maChuan = SanPham.chuanHoaMa(maHD);

        for (HoaDon hd : ds) {
            if (hd != null && hd.getMaHD() != null
                    && hd.getMaHD().equalsIgnoreCase(maChuan)) {
>>>>>>> origin/main
                return hd;
        }
        return null;
    }

<<<<<<< HEAD
    public HoaDon timKiemTheoMa(String maHD) {
        return timTheoMa(maHD);
    }

=======
    // Kiểm tra mã hóa đơn đã tồn tại chưa
>>>>>>> origin/main
    public boolean daTonTaiMa(String maHD) {
        return timTheoMa(maHD) != null;
    }

<<<<<<< HEAD
    public boolean them(HoaDon hd) {
        if (hd == null || hd.getMaHD() == null || hd.getMaHD().trim().isEmpty()) {
            System.out.println("Hóa đơn hoặc mã hóa đơn không hợp lệ!");
=======
    // Thêm hóa đơn
    public boolean them(HoaDon hd) {
        if (hd == null) {
            System.out.println("Hóa đơn không hợp lệ!");
            return false;
        }

        if (hd.getMaHD() == null || hd.getMaHD().trim().isEmpty()) {
            System.out.println("Mã hóa đơn không được để trống!");
>>>>>>> origin/main
            return false;
        }

        hd.setMaHD(SanPham.chuanHoaMa(hd.getMaHD()));

        if (daTonTaiMa(hd.getMaHD())) {
            System.out.println("Mã hóa đơn đã tồn tại: " + hd.getMaHD());
            return false;
        }

        ds.add(hd);
<<<<<<< HEAD
        return true;
    }

=======
        System.out.println("Thêm hóa đơn thành công!");
        return true;
    }

    // Xuất toàn bộ danh sách hóa đơn
>>>>>>> origin/main
    public void xuatDS() {
        if (ds.isEmpty()) {
            System.out.println("Danh sách hóa đơn đang trống!");
            return;
        }

        System.out.println("\n========== DANH SÁCH HÓA ĐƠN (" + ds.size() + ") ==========");
<<<<<<< HEAD
        for (HoaDon hd : ds)
            if (hd != null) hd.inhoadon();
    }

    public void xuatDanhSach() {
        xuatDS();
    }

=======
        for (HoaDon hd : ds) {
            if (hd != null) {
                hd.inhoadon();
            }
        }
    }

    // Xuất hóa đơn theo mã
>>>>>>> origin/main
    public boolean xuatTheoMa(String maHD) {
        HoaDon hd = timTheoMa(maHD);
        if (hd == null) {
<<<<<<< HEAD
            System.out.println("Không tìm thấy hóa đơn: " + maHD);
=======
            System.out.println("Không tìm thấy hóa đơn có mã: " + maHD);
>>>>>>> origin/main
            return false;
        }
        hd.inhoadon();
        return true;
    }

<<<<<<< HEAD
    // Chỉ hóa đơn bán chưa thanh toán và hóa đơn nhập đang chờ xác nhận mới được sửa/xóa.
    private boolean coTheSua(HoaDon hd) {
        if (hd instanceof HoadonNhap) {
            return ((HoadonNhap) hd).getTrangThai()
                    == HoadonNhap.TrangThai.CHO_XAC_NHAN;
        }
        return !hd.isDaThanhToan();
    }

=======
    // Xóa hóa đơn theo mã (chỉ cho phép xóa hóa đơn chưa thanh toán)
>>>>>>> origin/main
    public boolean xoa(String maHD) {
        HoaDon hd = timTheoMa(maHD);
        if (hd == null) {
            System.out.println("Không tìm thấy hóa đơn!");
            return false;
        }

<<<<<<< HEAD
        if (!coTheSua(hd)) {
            System.out.println("Không thể xóa hóa đơn đã thanh toán, đã xác nhận hoặc đã hủy!");
=======
        if (hd.isDaThanhToan()) {
            System.out.println("Không thể xóa hóa đơn đã thanh toán!");
>>>>>>> origin/main
            return false;
        }

        ds.remove(hd);
        System.out.println("Xóa hóa đơn thành công!");
        return true;
    }

<<<<<<< HEAD
    public boolean sua(String maHD, HoaDon hdMoi) {
        HoaDon hdCu = timTheoMa(maHD);

        if (hdCu == null || hdMoi == null) {
            System.out.println("Hóa đơn cần sửa không tồn tại hoặc không hợp lệ!");
            return false;
        }

        if (!coTheSua(hdCu)) {
            System.out.println("Không thể sửa hóa đơn đã thanh toán, đã xác nhận hoặc đã hủy!");
=======
    // Sửa hóa đơn theo mã (chỉ cho phép sửa hóa đơn chưa thanh toán)
    public boolean sua(String maHD, HoaDon hdMoi) {
        if (hdMoi == null) {
            System.out.println("Hóa đơn mới không hợp lệ!");
            return false;
        }

        HoaDon hdCu = timTheoMa(maHD);

        if (hdCu == null) {
            System.out.println("Không tìm thấy hóa đơn cần sửa!");
            return false;
        }

        if (hdCu.isDaThanhToan()) {
            System.out.println("Không thể sửa hóa đơn đã thanh toán!");
>>>>>>> origin/main
            return false;
        }

        if (hdCu.getClass() != hdMoi.getClass()) {
            System.out.println("Không thể đổi loại hóa đơn khi sửa!");
            return false;
        }

        if (hdMoi.getMaHD() == null || hdMoi.getMaHD().trim().isEmpty()) {
<<<<<<< HEAD
            System.out.println("Mã hóa đơn không được để trống!");
=======
            System.out.println("Mã hóa đơn mới không được để trống!");
            return false;
        }

        hdMoi.setMaHD(SanPham.chuanHoaMa(hdMoi.getMaHD()));

        // Kiểm tra mã mới có bị trùng với hóa đơn khác không
        if (!hdCu.getMaHD().equalsIgnoreCase(hdMoi.getMaHD())
                && daTonTaiMa(hdMoi.getMaHD())) {
            System.out.println("Mã hóa đơn mới đã tồn tại!");
>>>>>>> origin/main
            return false;
        }

        HoaDon trung = timTheoMa(hdMoi.getMaHD());
        if (trung != null && trung != hdCu) {
            System.out.println("Mã hóa đơn mới đã tồn tại!");
            return false;
        }

<<<<<<< HEAD
        ds.set(ds.indexOf(hdCu), hdMoi);
=======
>>>>>>> origin/main
        System.out.println("Sửa hóa đơn thành công!");
        return true;
    }

<<<<<<< HEAD
    public void timKiem(Scanner sc) {
        if (sc == null) sc = new Scanner(System.in);
        String maHD = Ktradulieu.nhapChuoi(sc,
                "Nhập mã hóa đơn cần tìm: ", ".+",
                "Mã hóa đơn không được để trống!");
        xuatTheoMa(maHD);
    }

    // ===================== GHI FILE =====================

    public boolean ghiFile(String duongDan) {
        if (duongDan == null || duongDan.trim().isEmpty()) {
            System.out.println("Đường dẫn file không hợp lệ!");
            return false;
        }

        // Kiểm tra loại hóa đơn trước khi ghi để tránh lưu thiếu dữ liệu.
        for (HoaDon hd : ds) {
            if (!(hd instanceof Hoadonban) && !(hd instanceof HoadonNhap)) {
                System.out.println("Có loại hóa đơn chưa hỗ trợ lưu file!");
                return false;
            }
        }

        int soDaGhi = 0;

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(duongDan), StandardCharsets.UTF_8))) {

            for (HoaDon hd : ds) {
                if (hd instanceof Hoadonban) {
                    bw.write(((Hoadonban) hd).toDongFile());
                    bw.newLine();
                    soDaGhi++;
                } else if (hd instanceof HoadonNhap) {
                    ArrayList<String> cacDong = ((HoadonNhap) hd).toCacDongFile();
                    for (String dong : cacDong) {
                        bw.write(dong);
                        bw.newLine();
                    }
                    soDaGhi++;
                }
            }

            System.out.println("Đã lưu " + soDaGhi + " hóa đơn vào file: " + duongDan);
            return true;

        } catch (IOException e) {
            System.out.println("Lỗi ghi file hóa đơn: " + e.getMessage());
            return false;
        }
    }

    public boolean ghiFile() {
        return ghiFile(FILE_MAC_DINH);
    }

    // ===================== ĐỌC FILE =====================

    public boolean docFile(String duongDan) {
        if (duongDan == null || duongDan.trim().isEmpty()) {
            System.out.println("Đường dẫn file không hợp lệ!");
            return false;
        }

        File file = new File(duongDan);
        if (!file.exists()) {
            System.out.println("File chưa tồn tại: " + duongDan);
            return false;
        }

        ArrayList<String> cacDongFile = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null)
                cacDongFile.add(line.trim());
        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
            return false;
        }

        ArrayList<HoaDon> dsMoi = new ArrayList<>();
        int soLoi = 0;

        for (int i = 0; i < cacDongFile.size(); i++) {
            String line = cacDongFile.get(i);

            if (line.isEmpty() || line.startsWith("#")) continue;

            try {
                HoaDon hd = null;

                // Hóa đơn bán: một dòng dữ liệu.
                if (line.startsWith("HDB|")) {
                    hd = Hoadonban.tuDongFile(line);
                }

                // Hóa đơn nhập: một dòng đầu phiếu + nhiều dòng CT.
                else if (line.startsWith("HDN|")) {
                    String[] t = line.split("\\|", -1);
                    if (t.length != 8)
                        throw new IllegalArgumentException("Sai định dạng đầu hóa đơn nhập!");

                    ArrayList<CTHDonNhap> chiTiet = new ArrayList<>();
                    boolean loiChiTiet = false;

                    while (i + 1 < cacDongFile.size()
                            && cacDongFile.get(i + 1).startsWith("CT|")) {
                        i++;
                        try {
                            chiTiet.add(CTHDonNhap.tuDongFile(cacDongFile.get(i)));
                        } catch (RuntimeException e) {
                            loiChiTiet = true;
                            break;
                        }
                    }

                    if (loiChiTiet)
                        throw new IllegalArgumentException("Dòng chi tiết hóa đơn nhập không hợp lệ!");

                    LocalDateTime thoiGianLap = LocalDateTime.parse(t[3]);
                    LocalDate ngayNhap = LocalDate.parse(t[4]);
                    HoadonNhap.TrangThai trangThai =
                            HoadonNhap.TrangThai.valueOf(t[6]);

                    hd = new HoadonNhap(
                            t[1], t[2], thoiGianLap, ngayNhap,
                            t[5], t[7], trangThai, chiTiet
                    );
                }

                if (hd == null)
                    throw new IllegalArgumentException("Không thể khôi phục hóa đơn!");

                boolean trungMa = false;
                for (HoaDon daCo : dsMoi) {
                    if (daCo.getMaHD().equalsIgnoreCase(hd.getMaHD())) {
                        trungMa = true;
                        break;
                    }
                }

                if (trungMa)
                    throw new IllegalArgumentException("Mã hóa đơn bị trùng!");

                dsMoi.add(hd);

            } catch (RuntimeException e) {
                soLoi++;
                System.out.println("Bỏ qua dữ liệu lỗi: " + e.getMessage());
            }
        }

        // Chỉ thay danh sách sau khi đã đọc xong file.
        this.ds = dsMoi;

        System.out.println("Đã đọc " + ds.size() + " hóa đơn từ file.");
        if (soLoi > 0)
            System.out.println("Có " + soLoi + " hóa đơn hoặc dòng dữ liệu bị lỗi.");

        return true;
    }

    public boolean docFile() {
        return docFile(FILE_MAC_DINH);
    }
}
=======
    // Tìm kiếm hóa đơn từ bàn phím
    public void timKiem(Scanner sc) {
        if (sc == null) {
            return;
        }
        String maHD = Ktradulieu.nhapChuoi(
                sc, "Nhập mã hóa đơn cần tìm: ", ".+", "Mã hóa đơn không được để trống!");
        xuatTheoMa(maHD);
    }
}
>>>>>>> origin/main
