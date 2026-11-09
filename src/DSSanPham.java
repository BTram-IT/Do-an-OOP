import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class DSSanPham {

    public static final String DONG_PHIEN_BAN = "#PHIENBAN|2";
    public static final String REGEX_KHONG_RONG = ".+";
    public static final String LOI_KHONG_RONG = "Không được để trống!";
    public static final String REGEX_SO_KHONG_AM = "\\d{1,18}";
    public static final String LOI_SO_KHONG_AM = "Phải nhập số nguyên không âm (tối đa 18 chữ số)!";
    public static final String REGEX_SO_THUC_KHONG_AM = "\\d{1,9}(\\.\\d{1,4})?";
    public static final String LOI_SO_THUC_KHONG_AM = "Phải nhập số không âm (ví dụ 8 hoặc 8.5)!";

    private static final int THEO_MA = 1;
    private static final int THEO_TEN = 2;
    private static final int THEO_GIA = 3;
    private static final int THEO_TON = 4;

    private ArrayList<SanPham> ds;

    public DSSanPham() {
        ds = new ArrayList<>();
    }

    // ==================== QUẢN LÝ DANH SÁCH ====================

    public int soLuong() {
        return ds.size();
    }

    public int size() {
        return ds.size();
    }

    public SanPham get(int viTri) {
        return layTheoViTri(viTri);
    }

    public SanPham layTheoViTri(int viTri) {
        if (viTri < 0 || viTri >= ds.size())
            throw new IndexOutOfBoundsException("Vị trí nằm ngoài danh sách.");
        return ds.get(viTri);
    }

    public void them(SanPham sp) {
        if (sp == null)
            throw new IllegalArgumentException("Sản phẩm không được null.");
        if (tonTaiMa(sp.getMaSP()))
            throw new IllegalArgumentException("Mã sản phẩm đã tồn tại: " + sp.getMaSP());
        ds.add(sp);
    }

    public int themNhieu(SanPham[] dsMoi) {
        if (dsMoi == null || dsMoi.length == 0)
            throw new IllegalArgumentException("Không có sản phẩm nào để thêm.");

        Set<String> cacMa = new HashSet<>();

        for (SanPham sp : dsMoi) {
            if (sp == null)
                throw new IllegalArgumentException("Có sản phẩm null trong danh sách.");

            String ma = SanPham.chuanHoaMa(sp.getMaSP());

            if (ma == null || ma.isEmpty())
                throw new IllegalArgumentException("Mã sản phẩm không hợp lệ.");

            if (tonTaiMa(ma) || !cacMa.add(ma))
                throw new IllegalArgumentException("Mã sản phẩm bị trùng: " + ma);
        }

        for (SanPham sp : dsMoi) ds.add(sp);
        return dsMoi.length;
    }

    public void nhapN(int n, Scanner sc) {
        if (n <= 0) throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");

        for (int i = 1; i <= n; i++) {
            System.out.println("--- Nhập sản phẩm thứ " + i + "/" + n + " ---");
            boolean xong = false;

            while (!xong) {
                try {
                    them(nhapSanPhamTuBanPhim(sc));
                    xong = true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Lỗi: " + e.getMessage() + " -> Nhập lại.");
                }
            }
        }
    }

    public static SanPham nhapSanPhamTuBanPhim(Scanner sc) {
        int loai = Ktradulieu.luaChon(sc, "Loại sản phẩm (1 = Thức ăn, 2 = Đồ uống): ", 1, 2);

        String maGoiY = loai == 1 ? ThucAn.maKeTiep() : DoUong.maKeTiep();
        String tienTo = loai == 1 ? ThucAn.TIEN_TO_MA : DoUong.TIEN_TO_MA;

        String ma = Ktradulieu.nhapChuoi(sc,
                "Mã sản phẩm (Enter = tự tạo " + maGoiY + "): ",
                "(?i)(" + tienTo + "\\d{3,})?",
                "Mã phải có dạng " + tienTo + " kèm ít nhất 3 chữ số!");

        if (ma.isEmpty()) ma = maGoiY;

        String ten = Ktradulieu.nhapChuoi(sc, "Tên sản phẩm: ", REGEX_KHONG_RONG, LOI_KHONG_RONG);
        long giaNhap = Long.parseLong(Ktradulieu.nhapChuoi(sc, "Giá nhập tham khảo (0 = chưa có): ",
                REGEX_SO_KHONG_AM, LOI_SO_KHONG_AM));
        long giaBan = Ktradulieu.nhapSoNguyen(sc, "Giá bán: ");
        String donVi = Ktradulieu.nhapChuoi(sc, "Đơn vị tính: ", REGEX_KHONG_RONG, LOI_KHONG_RONG);
        double thue = Double.parseDouble(Ktradulieu.nhapChuoi(sc, "Thuế suất (%): ",
                REGEX_SO_THUC_KHONG_AM, LOI_SO_THUC_KHONG_AM));

        if (loai == 1) {
            String nsx = Ktradulieu.nhapChuoi(sc, "Nhà sản xuất: ", REGEX_KHONG_RONG, LOI_KHONG_RONG);
            return new ThucAn(ma, ten, giaNhap, giaBan, donVi, thue, nsx);
        }

        int dungTich = Ktradulieu.luaChon(sc, "Dung tích (ml): ", 1, DoUong.DUNG_TICH_TOI_DA_ML);
        return new DoUong(ma, ten, giaNhap, giaBan, donVi, thue, dungTich);
    }

    public void sua(String ma, SanPham thongTinMoi) {
        layBatBuoc(ma).capNhatThongTin(thongTinMoi);
    }

    public void xoaTheoMa(String ma) {
        int idx = timViTriTheoMa(ma);

        if (idx < 0)
            throw new IllegalArgumentException("Không tìm thấy sản phẩm: " + ma);

        if (ds.get(idx).getSoLuongTon() > 0)
            throw new IllegalStateException("Không thể xóa " + ma + " vì còn tồn kho.");

        ds.remove(idx);
    }

    private int timViTriTheoMa(String ma) {
        if (ma == null || ma.trim().isEmpty()) return -1;

        String maChuan = SanPham.chuanHoaMa(ma);

        for (int i = 0; i < ds.size(); i++) {
            if (ds.get(i).getMaSP().equalsIgnoreCase(maChuan)) return i;
        }
        return -1;
    }

    public SanPham layBatBuoc(String ma) {
        SanPham sp = timTheoMa(ma);
        if (sp == null)
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + ma);
        return sp;
    }

    public boolean tonTaiMa(String ma) {
        return timViTriTheoMa(ma) >= 0;
    }

    public SanPham timTheoMa(String ma) {
        int idx = timViTriTheoMa(ma);
        return idx < 0 ? null : ds.get(idx);
    }

    // ==================== QUẢN LÝ LÔ HÀNG ====================

    public void themLo(LoHang lo) {
        if (lo == null) throw new IllegalArgumentException("Lô không được null.");
        layBatBuoc(lo.getMaSP()).themLo(lo);
    }

    public void xoaLo(String maSP, String maLo) {
        layBatBuoc(maSP).xoaLo(maLo);
    }

    public ArrayList<LoXuat> xuatFEFO(String ma, int soLuong, LocalDate ngay) {
        return layBatBuoc(ma).xuatTheoFEFO(soLuong, ngay);
    }

    public void hoanLo(String ma, String maLo, int soLuong) {
        layBatBuoc(ma).hoanVaoLo(maLo, soLuong);
    }

    public int layTon(String ma) {
        return layBatBuoc(ma).getSoLuongTon();
    }

    public int layTonBanDuoc(String ma, LocalDate ngay) {
        return layBatBuoc(ma).getSoLuongBanDuoc(ngay);
    }

    public boolean duTon(String ma, int soLuong) {
        return duTon(ma, soLuong, LocalDate.now());
    }

    public boolean duTon(String ma, int soLuong, LocalDate ngay) {
        return soLuong > 0 && soLuong <= layTonBanDuoc(ma, ngay);
    }

    // ==================== TÌM KIẾM ====================

    public ArrayList<SanPham> timTheoMaGanDung(String tuKhoa) {
        ArrayList<SanPham> kq = new ArrayList<>();
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) return kq;

        String khoa = SanPham.chuanHoaMa(tuKhoa.trim());

        for (SanPham sp : ds) {
            if (sp.getMaSP().contains(khoa)) kq.add(sp);
        }
        return kq;
    }

    public ArrayList<SanPham> timTheoTenChinhXac(String ten) {
        ArrayList<SanPham> kq = new ArrayList<>();
        if (ten == null || ten.trim().isEmpty()) return kq;

        for (SanPham sp : ds) {
            if (sp.getTenSP().equalsIgnoreCase(ten.trim())) kq.add(sp);
        }
        return kq;
    }

    public ArrayList<SanPham> timTheoTenGanDung(String tuKhoa) {
        ArrayList<SanPham> kq = new ArrayList<>();
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) return kq;

        String khoa = SanPham.boDauVaThuong(tuKhoa.trim());

        for (SanPham sp : ds) {
            if (SanPham.boDauVaThuong(sp.getTenSP()).contains(khoa)) kq.add(sp);
        }
        return kq;
    }

    public ArrayList<SanPham> timTheoLoai(String loai) {
        ArrayList<SanPham> kq = new ArrayList<>();
        if (loai == null || loai.trim().isEmpty()) return kq;

        String khoa = SanPham.boDauVaThuong(loai.trim());

        for (SanPham sp : ds) {
            if (SanPham.boDauVaThuong(sp.getLoai()).equals(khoa)) kq.add(sp);
        }
        return kq;
    }

    public ArrayList<SanPham> timTheoKhoangGia(long giaTu, long giaDen) {
        kiemTraKhoangGia(giaTu, giaDen);
        ArrayList<SanPham> kq = new ArrayList<>();

        for (SanPham sp : ds) {
            if (sp.getGiaBan() >= giaTu && sp.getGiaBan() <= giaDen) kq.add(sp);
        }
        return kq;
    }

    public ArrayList<SanPham> timNhieuTieuChi(String tuKhoaTen, String loai, long giaTu, long giaDen) {
        kiemTraKhoangGia(giaTu, giaDen);

        String kt = SanPham.boDauVaThuong(tuKhoaTen == null ? "" : tuKhoaTen.trim());
        String kl = SanPham.boDauVaThuong(loai == null ? "" : loai.trim());

        ArrayList<SanPham> kq = new ArrayList<>();

        for (SanPham sp : ds) {
            String ten = SanPham.boDauVaThuong(sp.getTenSP());
            String loaiSP = SanPham.boDauVaThuong(sp.getLoai());

            boolean hopLe = (kt.isEmpty() || ten.contains(kt))
                    && (kl.isEmpty() || loaiSP.equals(kl))
                    && sp.getGiaBan() >= giaTu
                    && sp.getGiaBan() <= giaDen;

            if (hopLe) kq.add(sp);
        }
        return kq;
    }

    private void kiemTraKhoangGia(long giaTu, long giaDen) {
        if (giaTu < 0 || giaDen < 0 || giaTu > giaDen)
            throw new IllegalArgumentException("Khoảng giá không hợp lệ.");
    }

    // ==================== SẮP XẾP ====================

    public void sapXepTheoMa() {
        ds.sort((a, b) -> a.getMaSP().compareTo(b.getMaSP()));
    }

    public void sapXepTheoTen() {
        ds.sort((a, b) -> SanPham.boDauVaThuong(a.getTenSP())
                .compareTo(SanPham.boDauVaThuong(b.getTenSP())));
    }

    public void sapXepTheoGiaBan(boolean tangDan) {
        ds.sort((a, b) -> tangDan
                ? Long.compare(a.getGiaBan(), b.getGiaBan())
                : Long.compare(b.getGiaBan(), a.getGiaBan()));
    }

    public void sapXepTheoTon(boolean tangDan) {
        ds.sort((a, b) -> tangDan
                ? Integer.compare(a.getSoLuongTon(), b.getSoLuongTon())
                : Integer.compare(b.getSoLuongTon(), a.getSoLuongTon()));
    }

    // ==================== THỐNG KÊ KHO ====================

    public long tongSoLuongTon() {
        long tong = 0;
        for (SanPham sp : ds) tong = Math.addExact(tong, sp.getSoLuongTon());
        return tong;
    }

    public long tongGiaTriTon() {
        long tong = 0;
        for (SanPham sp : ds)
            tong = Math.addExact(tong, sp.getGiaTriTonTheoGiaBan());
        return tong;
    }

    public ArrayList<SanPham> layHangSapHet(int nguong) {
        if (nguong < 0)
            throw new IllegalArgumentException("Ngưỡng tồn kho không được âm.");

        ArrayList<SanPham> kq = new ArrayList<>();

        for (SanPham sp : ds) {
            int tonBanDuoc = sp.getSoLuongBanDuoc(LocalDate.now());
            if (tonBanDuoc > 0 && tonBanDuoc <= nguong) kq.add(sp);
        }
        return kq;
    }

    public ArrayList<SanPham> layHangHetHang() {
        ArrayList<SanPham> kq = new ArrayList<>();
        for (SanPham sp : ds) {
            if (sp.getSoLuongTon() == 0) kq.add(sp);
        }
        return kq;
    }

    public ArrayList<SanPham> layHangHetHan(LocalDate ngay) {
        if (ngay == null) throw new IllegalArgumentException("Ngày kiểm tra không được null.");

        ArrayList<SanPham> kq = new ArrayList<>();
        for (SanPham sp : ds) {
            if (sp.coHangHetHan(ngay)) kq.add(sp);
        }
        return kq;
    }

    public ArrayList<LoHang> layLoHetHan(LocalDate ngay) {
        if (ngay == null) throw new IllegalArgumentException("Ngày kiểm tra không được null.");

        ArrayList<LoHang> kq = new ArrayList<>();

        for (SanPham sp : ds) {
            for (LoHang lo : sp.getCacLo()) {
                if (lo.getSoLuongCon() > 0 && lo.isHetHan(ngay)) kq.add(lo);
            }
        }
        return kq;
    }

    public ArrayList<LoHang> layLoSapHetHan(LocalDate ngay, int soNgay) {
        if (ngay == null || soNgay < 0)
            throw new IllegalArgumentException("Ngày hoặc số ngày kiểm tra không hợp lệ.");

        ArrayList<LoHang> kq = new ArrayList<>();

        for (SanPham sp : ds) {
            for (LoHang lo : sp.getCacLo()) {
                if (lo.getSoLuongCon() > 0 && lo.sapHetHan(ngay, soNgay)) kq.add(lo);
            }
        }
        return kq;
    }

    public String baoCaoThongKe() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== THỐNG KÊ KHO =====\n");
        sb.append("Tổng số mặt hàng : ").append(soLuong()).append("\n");
        sb.append("Tổng số lượng tồn: ").append(tongSoLuongTon()).append("\n");
        sb.append("Giá trị tồn (theo giá bán): ")
                .append(SanPham.dinhDangTien(tongGiaTriTon())).append("\n");
        return sb.toString();
    }

    public void xuat() {
        if (ds.isEmpty()) {
            System.out.println("(Danh sách sản phẩm đang rỗng)");
            return;
        }

        System.out.println("Danh sách sản phẩm (" + ds.size() + "):");
        for (int i = 0; i < ds.size(); i++)
            System.out.println((i + 1) + ". " + ds.get(i));
    }

    // ==================== GHI FILE ====================

    public void ghiFile(String duongDan) throws IOException {
        if (duongDan == null || duongDan.trim().isEmpty())
            throw new IllegalArgumentException("Đường dẫn file không được để trống.");

        Path dich = Paths.get(duongDan);
        Path tam = Paths.get(duongDan + ".tmp");

        // Tự động tạo thư mục chứa file nếu chưa tồn tại
        Path thuMuc = dich.getParent();

        if (thuMuc != null && !Files.exists(thuMuc)) {
            Files.createDirectories(thuMuc);
        }

        try {
            ghiRaFile(tam.toString());
            Files.move(tam, dich, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(tam);
            } catch (IOException loiXoa) {
                e.addSuppressed(loiXoa);
            }
            throw e;
        }
    }

    public void ghiRaFile(String duongDan) throws IOException {
        try (BufferedWriter ghi = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(duongDan),
                        StandardCharsets.UTF_8))) {

            ghi.write(DONG_PHIEN_BAN);
            ghi.newLine();

            for (SanPham sp : ds) {
                ghi.write(sp.toDongFile());
                ghi.newLine();

                for (LoHang lo : sp.getCacLo()) {
                    ghi.write(lo.toDongFile());
                    ghi.newLine();
                }
            }
        }
    }

    // ==================== ĐỌC FILE ====================

    public void docFile(String duongDan) throws IOException {
        ArrayList<SanPham> dsMoi = docSanPhamTuFile(duongDan);
        thayTheBang(dsMoi);
    }

    public ArrayList<SanPham> docSanPhamTuFile(String duongDan) throws IOException {
        ArrayList<SanPham> dsMoi = new ArrayList<>();
        Set<String> cacMaSP = new HashSet<>();
        Set<String> cacMaLo = new HashSet<>();
        boolean dungPhienBan = false;

        File file = new File(duongDan);

        // Kiểm tra file có tồn tại không
        if (!file.exists()) {
            System.out.println("Chưa có file sản phẩm. Khởi tạo danh sách rỗng!");
            return dsMoi;
        }

        try (BufferedReader doc = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {

            String dong;
            int soDong = 0;

            while ((dong = doc.readLine()) != null) {
                soDong++;
                dong = dong.trim();

                if (soDong == 1 && dong.startsWith("\uFEFF"))
                    dong = dong.substring(1).trim();

                if (dong.equals(DONG_PHIEN_BAN)) {
                    if (dungPhienBan)
                        throw new IOException("Dòng " + soDong + ": khai báo phiên bản bị trùng.");
                    dungPhienBan = true;
                    continue;
                }

                if (dong.isEmpty() || dong.startsWith("#")) continue;

                if (!dungPhienBan)
                    throw new IOException("Dòng " + soDong + ": thiếu dòng " + DONG_PHIEN_BAN);

                try {
                    if (dong.startsWith("LO|")) {
                        LoHang lo = LoHang.tuDongFile(dong);
                        String maLo = SanPham.chuanHoaMa(lo.getMaLo());
                        String maSP = SanPham.chuanHoaMa(lo.getMaSP());

                        if (!cacMaLo.add(maLo))
                            throw new IllegalArgumentException("Mã lô bị trùng: " + maLo);

                        SanPham chu = timTrongDanhSach(dsMoi, maSP);

                        if (chu == null)
                            throw new IllegalArgumentException("Lô " + maLo
                                    + " không tìm thấy sản phẩm " + maSP);

                        chu.themLo(lo);

                    } else {
                        SanPham sp = taoSanPhamTuDongFile(dong);
                        String maSP = SanPham.chuanHoaMa(sp.getMaSP());

                        if (!cacMaSP.add(maSP))
                            throw new IllegalArgumentException("Mã sản phẩm bị trùng: " + maSP);

                        dsMoi.add(sp);
                    }

                } catch (RuntimeException e) {
                    throw new IOException(
                            duongDan + ", dòng " + soDong + ": " + e.getMessage(), e
                    );
                }
            }

            if (!dungPhienBan)
                throw new IOException("File không có dòng phiên bản " + DONG_PHIEN_BAN);
        }

        return dsMoi;
    }

    public void thayTheBang(ArrayList<SanPham> dsMoi) {
        if (dsMoi == null)
            throw new IllegalArgumentException("Danh sách mới không được null.");

        Set<String> cacMa = new HashSet<>();

        for (SanPham sp : dsMoi) {
            if (sp == null)
                throw new IllegalArgumentException("Danh sách chứa sản phẩm null.");

            String ma = SanPham.chuanHoaMa(sp.getMaSP());

            if (ma == null || ma.isEmpty() || !cacMa.add(ma))
                throw new IllegalArgumentException("Mã sản phẩm không hợp lệ hoặc bị trùng: " + ma);
        }

        ds.clear();
        ds.addAll(dsMoi);
    }

    private static SanPham timTrongDanhSach(ArrayList<SanPham> danhSach, String ma) {
        if (ma == null) return null;

        String maChuan = SanPham.chuanHoaMa(ma);

        for (SanPham sp : danhSach) {
            if (sp.getMaSP().equalsIgnoreCase(maChuan)) return sp;
        }
        return null;
    }

    private static SanPham taoSanPhamTuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty())
            throw new IllegalArgumentException("Dòng sản phẩm không được để trống.");

        String[] t = dong.split("\\|", -1);

        if (t.length != 8)
            throw new IllegalArgumentException("Dòng sản phẩm không đúng định dạng.");

        long giaNhap = Long.parseLong(t[3]);
        long giaBan = Long.parseLong(t[4]);
        double thue = Double.parseDouble(t[6]);

        if (t[0].equals("THUCAN"))
            return new ThucAn(t[1], t[2], giaNhap, giaBan, t[5], thue, t[7]);

        if (t[0].equals("DOUONG"))
            return new DoUong(t[1], t[2], giaNhap, giaBan, t[5], thue,
                    Integer.parseInt(t[7]));

        throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: " + t[0]);
    }
}
