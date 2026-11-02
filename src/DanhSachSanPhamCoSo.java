import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class DanhSachSanPhamCoSo implements IDanhSachSanPham {

    private static final int THEO_MA = 1;
    private static final int THEO_TEN = 2;
    private static final int THEO_GIA = 3;
    private static final int THEO_TON = 4;

    // Thêm vào cuối, trả về false nếu hết chỗ (chỉ xảy ra với mảng tĩnh)
    protected abstract boolean themCuoi(SanPham sp);

    protected abstract void xoaTaiViTri(int viTri);

    protected abstract void datTaiViTri(int viTri, SanPham sp);

    protected abstract void xoaHet();

    // ===================== THÊM / NHẬP =====================

    @Override
    public void them(SanPham sp) {
        if (sp == null) {
            throw new IllegalArgumentException("Sản phẩm không được null.");
        }
        if (tonTaiMa(sp.getMaSP())) {
            throw new IllegalArgumentException("Mã sản phẩm đã tồn tại: " + sp.getMaSP());
        }
        if (!themCuoi(sp)) {
            throw new IllegalStateException("Danh sách đã đầy (tối đa " + sucChuaToiDa() + " sản phẩm).");
        }
    }

    @Override
    public int themNhieu(SanPham[] dsMoi) {
        if (dsMoi == null || dsMoi.length == 0) {
            throw new IllegalArgumentException("Không có sản phẩm nào để thêm.");
        }
        if (dsMoi.length > sucChuaToiDa() - soLuong()) {
            throw new IllegalStateException("Không đủ chỗ: cần thêm " + dsMoi.length
                    + " sản phẩm nhưng chỉ còn chỗ cho " + (sucChuaToiDa() - soLuong()) + ".");
        }
        // Bước 1: kiểm tra tất cả trước, chưa thêm cái nào
        for (int i = 0; i < dsMoi.length; i++) {
            if (dsMoi[i] == null) {
                throw new IllegalArgumentException("Trong danh sách cần thêm có sản phẩm null.");
            }
            if (tonTaiMa(dsMoi[i].getMaSP())) {
                throw new IllegalArgumentException("Mã sản phẩm đã tồn tại: " + dsMoi[i].getMaSP());
            }
            for (int j = 0; j < i; j++) {
                if (dsMoi[j].getMaSP().equals(dsMoi[i].getMaSP())) {
                    throw new IllegalArgumentException("Mã bị trùng trong danh sách cần thêm: " + dsMoi[i].getMaSP());
                }
            }
        }
        // Bước 2: thêm
        for (int i = 0; i < dsMoi.length; i++) {
            themCuoi(dsMoi[i]);
        }
        return dsMoi.length;
    }

    @Override
    public void nhapN(int n, Scanner sc) {
        if (n <= 0) {
            throw new IllegalArgumentException("Số sản phẩm cần nhập phải lớn hơn 0.");
        }
        if (n > sucChuaToiDa() - soLuong()) {
            throw new IllegalStateException("Không đủ chỗ: chỉ còn chỗ cho " + (sucChuaToiDa() - soLuong()) + " sản phẩm.");
        }
        for (int i = 1; i <= n; i++) {
            System.out.println("--- Nhập sản phẩm thứ " + i + "/" + n + " ---");
            boolean xong = false;
            while (!xong) {
                try {
                    them(nhapSanPhamTuBanPhim(sc));
                    xong = true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Lỗi: " + e.getMessage() + " -> mời nhập lại sản phẩm này.");
                }
            }
        }
    }

    // Form nhập một sản phẩm. Sản phẩm mới chưa có lô nào (tồn 0): hàng chỉ vào kho qua phiếu nhập để có lịch sử.
    public static SanPham nhapSanPhamTuBanPhim(Scanner sc) {
        int loai = TienIch.docSoNguyen(sc, "Loại sản phẩm (1 = Thức ăn, 2 = Đồ uống): ", 1, 2);
        String maGoiY = ThucAn.maKeTiep();
        if (loai == 2) {
            maGoiY = DoUong.maKeTiep();
        }
        String ma = TienIch.docChuoiTuyChon(sc, "Mã sản phẩm (Enter = tự tạo " + maGoiY + "): ");
        if (ma.isEmpty()) {
            ma = maGoiY;
        }
        String ten = TienIch.docChuoi(sc, "Tên sản phẩm: ");
        long giaNhap = TienIch.docSoLong(sc, "Giá nhập tham khảo (đồng, CHƯA thuế, 0 = chưa có): ", 0, SanPham.GIA_TOI_DA);
        long giaBan = TienIch.docSoLong(sc, "Giá bán (đồng, CHƯA thuế): ", 1, SanPham.GIA_TOI_DA);
        String donVi = TienIch.docChuoi(sc, "Đơn vị tính (chai, cái, phần...): ");
        double thue = TienIch.docSoThuc(sc, "Thuế suất bán (%), nhập 0 nếu chưa khai báo: ", 0, SanPham.THUE_SUAT_TOI_DA);
        // Hạn sử dụng không hỏi ở đây: nó thuộc từng lô, khai báo khi lập phiếu nhập
        if (loai == 1) {
            String nsx = TienIch.docChuoi(sc, "Nhà sản xuất: ");
            return new ThucAn(ma, ten, giaNhap, giaBan, donVi, thue, nsx);
        }
        int dungTich = TienIch.docSoNguyen(sc, "Dung tích (ml): ", 1, DoUong.DUNG_TICH_TOI_DA_ML);
        return new DoUong(ma, ten, giaNhap, giaBan, donVi, thue, dungTich);
    }

    // ===================== SỬA / XÓA =====================

    @Override
    public void sua(String ma, SanPham thongTinMoi) {
        layBatBuoc(ma).capNhatThongTin(thongTinMoi);
    }

    @Override
    public void xoaTheoMa(String ma) {
        int viTri = timViTriTheoMa(ma);
        if (viTri < 0) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + ma);
        }
        SanPham sp = layTheoViTri(viTri);
        if (sp.getSoLuongTon() > 0) {
            throw new IllegalStateException("Không thể xóa " + sp.getMaSP() + " vì còn tồn kho "
                    + sp.getSoLuongTon() + " " + sp.getDonViTinh() + ".");
        }
        xoaTaiViTri(viTri);
    }

    // ===================== TÌM KIẾM =====================

    private int timViTriTheoMa(String ma) {
        String maChuan = SanPham.chuanHoaMa(ma);
        if (maChuan == null) {
            return -1;
        }
        for (int i = 0; i < soLuong(); i++) {
            if (layTheoViTri(i).getMaSP().equals(maChuan)) {
                return i;
            }
        }
        return -1;
    }

    // Như timTheoMa nhưng không thấy thì báo lỗi (dùng cho thao tác bắt buộc phải có sản phẩm)
    private SanPham layBatBuoc(String ma) {
        SanPham sp = timTheoMa(ma);
        if (sp == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + ma);
        }
        return sp;
    }

    private static String kiemTraTuKhoa(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            throw new IllegalArgumentException("Từ khóa tìm kiếm không được để trống.");
        }
        return tuKhoa.trim();
    }

    @Override
    public boolean tonTaiMa(String ma) {
        return timViTriTheoMa(ma) >= 0;
    }

    @Override
    public SanPham timTheoMa(String ma) {
        int viTri = timViTriTheoMa(ma);
        if (viTri < 0) {
            return null;
        }
        return layTheoViTri(viTri);
    }

    @Override
    public ArrayList<SanPham> timTheoMaGanDung(String tuKhoa) {
        String khoa = SanPham.chuanHoaMa(kiemTraTuKhoa(tuKhoa));
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            SanPham sp = layTheoViTri(i);
            if (sp.getMaSP().contains(khoa)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    @Override
    public ArrayList<SanPham> timTheoTenChinhXac(String ten) {
        String khoa = kiemTraTuKhoa(ten);
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            SanPham sp = layTheoViTri(i);
            if (sp.getTenSP().equalsIgnoreCase(khoa)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    @Override
    public ArrayList<SanPham> timTheoTenGanDung(String tuKhoa) {
        String khoa = TienIch.boDauVaThuong(kiemTraTuKhoa(tuKhoa));
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            SanPham sp = layTheoViTri(i);
            if (TienIch.boDauVaThuong(sp.getTenSP()).contains(khoa)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    @Override
    public ArrayList<SanPham> timTheoLoai(String loai) {
        String khoa = TienIch.boDauVaThuong(kiemTraTuKhoa(loai));
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            SanPham sp = layTheoViTri(i);
            if (TienIch.boDauVaThuong(sp.getLoai()).equals(khoa)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    @Override
    public ArrayList<SanPham> timTheoKhoangGia(long giaTu, long giaDen) {
        if (giaTu < 0 || giaTu > giaDen) {
            throw new IllegalArgumentException("Khoảng giá không hợp lệ: cần 0 <= giá từ <= giá đến.");
        }
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            SanPham sp = layTheoViTri(i);
            if (sp.getGiaBan() >= giaTu && sp.getGiaBan() <= giaDen) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    @Override
    public ArrayList<SanPham> timNhieuTieuChi(String tuKhoaTen, String loai, long giaTu, long giaDen) {
        if (giaTu < 0 || giaTu > giaDen) {
            throw new IllegalArgumentException("Khoảng giá không hợp lệ: cần 0 <= giá từ <= giá đến.");
        }
        String khoaTen = TienIch.boDauVaThuong(tuKhoaTen);
        String khoaLoai = TienIch.boDauVaThuong(loai);
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            SanPham sp = layTheoViTri(i);
            boolean hopLe = true;
            // Mỗi tiêu chí không đạt thì loại sản phẩm; tiêu chí để trống thì bỏ qua
            if (!khoaTen.isEmpty() && !TienIch.boDauVaThuong(sp.getTenSP()).contains(khoaTen)) {
                hopLe = false;
            }
            if (!khoaLoai.isEmpty() && !TienIch.boDauVaThuong(sp.getLoai()).equals(khoaLoai)) {
                hopLe = false;
            }
            if (sp.getGiaBan() < giaTu || sp.getGiaBan() > giaDen) {
                hopLe = false;
            }
            if (hopLe) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    // ===================== SẮP XẾP =====================
    // Dùng sắp xếp chọn (selection sort) trên các hàm lưu trữ, nên chạy đúng với cả mảng tĩnh lẫn mảng động.

    private int soSanh(SanPham a, SanPham b, int tieuChi) {
        int ketQua = 0;
        if (tieuChi == THEO_MA) {
            ketQua = a.getMaSP().compareTo(b.getMaSP());
        } else if (tieuChi == THEO_TEN) {
            ketQua = TienIch.boDauVaThuong(a.getTenSP()).compareTo(TienIch.boDauVaThuong(b.getTenSP()));
        } else if (tieuChi == THEO_GIA) {
            ketQua = Long.compare(a.getGiaBan(), b.getGiaBan());
        } else if (tieuChi == THEO_TON) {
            ketQua = Integer.compare(a.getSoLuongTon(), b.getSoLuongTon());
        }
        if (ketQua == 0) {
            ketQua = a.getMaSP().compareTo(b.getMaSP()); // bằng nhau thì xếp theo mã
        }
        return ketQua;
    }

    private void sapXep(int tieuChi, boolean tangDan) {
        int n = soLuong();
        for (int i = 0; i < n - 1; i++) {
            int viTriTot = i;
            for (int j = i + 1; j < n; j++) {
                int kq = soSanh(layTheoViTri(j), layTheoViTri(viTriTot), tieuChi);
                if (!tangDan) {
                    kq = -kq;
                }
                if (kq < 0) {
                    viTriTot = j;
                }
            }
            if (viTriTot != i) {
                SanPham tam = layTheoViTri(i);
                datTaiViTri(i, layTheoViTri(viTriTot));
                datTaiViTri(viTriTot, tam);
            }
        }
    }

    @Override
    public void sapXepTheoMa() {
        sapXep(THEO_MA, true);
    }

    @Override
    public void sapXepTheoTen() {
        sapXep(THEO_TEN, true);
    }

    @Override
    public void sapXepTheoGiaBan(boolean tangDan) {
        sapXep(THEO_GIA, tangDan);
    }

    @Override
    public void sapXepTheoTon(boolean tangDan) {
        sapXep(THEO_TON, tangDan);
    }

    // ===================== TỒN KHO =====================

    @Override
    public int layTon(String ma) {
        return layBatBuoc(ma).getSoLuongTon();
    }

    @Override
    public boolean duTon(String ma, int soLuong) {
        return duTon(ma, soLuong, LocalDate.now());
    }

    @Override
    public int layTonBanDuoc(String ma, LocalDate ngay) {
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày kiểm tra không được null.");
        }
        return layBatBuoc(ma).getSoLuongBanDuoc(ngay);
    }

    @Override
    public boolean duTon(String ma, int soLuong, LocalDate ngay) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng kiểm tra phải lớn hơn 0.");
        }
        return soLuong <= layTonBanDuoc(ma, ngay);
    }

    @Override
    public ArrayList<LoXuat> xuatFEFO(String ma, int soLuong, LocalDate ngay) {
        return layBatBuoc(ma).xuatTheoFEFO(soLuong, ngay);
    }

    @Override
    public void hoanLo(String ma, String maLo, int soLuong) {
        layBatBuoc(ma).hoanVaoLo(maLo, soLuong);
    }

    // ===================== THỐNG KÊ =====================

    @Override
    public long tongSoLuongTon() {
        long tong = 0;
        for (int i = 0; i < soLuong(); i++) {
            tong += layTheoViTri(i).getSoLuongTon();
        }
        return tong;
    }

    @Override
    public long tongGiaTriTon() {
        long tong = 0;
        for (int i = 0; i < soLuong(); i++) {
            tong += layTheoViTri(i).getGiaTriTonTheoGiaBan();
        }
        return tong;
    }

    @Override
    public int demTheoLoai(String loai) {
        return timTheoLoai(loai).size();
    }

    @Override
    public ArrayList<SanPham> layHangSapHet(int nguong) {
        if (nguong < 0) {
            throw new IllegalArgumentException("Ngưỡng sắp hết hàng không được âm.");
        }
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            SanPham sp = layTheoViTri(i);
            int banDuoc = sp.getSoLuongBanDuoc(LocalDate.now());
            if (banDuoc > 0 && banDuoc <= nguong) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    @Override
    public ArrayList<SanPham> layHangHetHang() {
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            SanPham sp = layTheoViTri(i);
            if (sp.getSoLuongTon() == 0) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    @Override
    public ArrayList<SanPham> layHangHetHan(LocalDate ngay) {
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày kiểm tra không được null.");
        }
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            SanPham sp = layTheoViTri(i);
            if (sp.coHangHetHan(ngay)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    @Override
    public ArrayList<LoHang> layLoHetHan(LocalDate ngay) {
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày kiểm tra không được null.");
        }
        ArrayList<LoHang> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            for (LoHang lo : layTheoViTri(i).getCacLo()) {
                if (lo.getSoLuongCon() > 0 && lo.isHetHan(ngay)) {
                    ketQua.add(lo);
                }
            }
        }
        return ketQua;
    }

    @Override
    public ArrayList<LoHang> layLoSapHetHan(LocalDate ngay, int soNgay) {
        if (ngay == null || soNgay < 0) {
            throw new IllegalArgumentException("Cần ngày kiểm tra và số ngày không âm.");
        }
        ArrayList<LoHang> ketQua = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            for (LoHang lo : layTheoViTri(i).getCacLo()) {
                if (lo.getSoLuongCon() > 0 && lo.sapHetHan(ngay, soNgay)) {
                    ketQua.add(lo);
                }
            }
        }
        return ketQua;
    }

    @Override
    public String baoCaoThongKe() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== THỐNG KÊ KHO =====\n");
        sb.append("Tổng số mặt hàng : ").append(soLuong()).append("\n");
        sb.append("Tổng số lượng tồn: ").append(tongSoLuongTon()).append("\n");
        sb.append("Giá trị tồn (theo giá bán, chưa thuế): ").append(TienIch.dinhDangTien(tongGiaTriTon())).append("\n");
        long hetHan = 0;
        for (int i = 0; i < soLuong(); i++) {
            hetHan += layTheoViTri(i).getSoLuongHetHan(LocalDate.now());
        }
        sb.append("Số lượng nằm trong lô đã hết hạn (hôm nay): ").append(hetHan).append("\n");

        // Gom danh sách các loại đang có
        ArrayList<String> cacLoai = new ArrayList<>();
        for (int i = 0; i < soLuong(); i++) {
            String loai = layTheoViTri(i).getLoai();
            if (!cacLoai.contains(loai)) {
                cacLoai.add(loai);
            }
        }
        // Với mỗi loại: đếm mặt hàng, cộng tồn, cộng giá trị
        for (String loai : cacLoai) {
            int soMatHang = 0;
            long tongTon = 0;
            long giaTri = 0;
            for (int i = 0; i < soLuong(); i++) {
                SanPham sp = layTheoViTri(i);
                if (sp.getLoai().equals(loai)) {
                    soMatHang++;
                    tongTon += sp.getSoLuongTon();
                    giaTri += sp.getGiaTriTonTheoGiaBan();
                }
            }
            sb.append("- ").append(loai).append(": ").append(soMatHang).append(" mặt hàng, tồn ")
                    .append(tongTon).append(", giá trị ").append(TienIch.dinhDangTien(giaTri)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void xuat() {
        if (soLuong() == 0) {
            System.out.println("(Danh sách sản phẩm đang rỗng)");
            return;
        }
        System.out.println("Danh sách sản phẩm (" + soLuong() + "):");
        for (int i = 0; i < soLuong(); i++) {
            // Đa hình: toString() chạy theo đúng lớp thật (ThucAn hoặc DoUong)
            System.out.println((i + 1) + ". " + layTheoViTri(i));
        }
    }

    // ===================== ĐỌC / GHI FILE =====================
    // File gồm các dòng sản phẩm (THUCAN|... hoặc DOUONG|...), ngay sau mỗi sản phẩm là các dòng lô LO|... của nó.
    // Tồn kho KHÔNG ghi riêng: tồn luôn bằng tổng số lượng còn của các lô, nên không thể lệch nhau.

    @Override
    public void ghiFile(String duongDan) throws IOException {
        try (BufferedWriter ghi = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(duongDan), StandardCharsets.UTF_8))) {
            ghi.write(TienIch.DONG_PHIEN_BAN);
            ghi.newLine();
            ghi.write("# loai|ma|ten|giaNhapThamKhao|giaBan|donViTinh|thueSuatBan(%)|truongRieng (nhaSanXuat hoac dungTichMl)");
            ghi.newLine();
            ghi.write("# LO|maLo|maSP|maHoaDonNhap|ngayNhap|hanSuDung|soLuongNhap|soLuongCon|giaNhapThucTe");
            ghi.newLine();
            for (int i = 0; i < soLuong(); i++) {
                SanPham sp = layTheoViTri(i);
                ghi.write(sp.toDongFile());
                ghi.newLine();
                for (LoHang lo : sp.getCacLo()) {
                    ghi.write(lo.toDongFile());
                    ghi.newLine();
                }
            }
        }
    }

    @Override
    public void docFile(String duongDan) throws IOException {
        thayTheBang(docSanPhamTuFile(duongDan));
    }

    @Override
    public ArrayList<SanPham> docSanPhamTuFile(String duongDan) throws IOException {
        // Đọc hết vào danh sách tạm; chỉ khi không có lỗi nào mới trả về
        ArrayList<SanPham> dsMoi = new ArrayList<>();
        ArrayList<String> cacMaLo = new ArrayList<>();
        boolean dungPhienBan = false;
        try (BufferedReader doc = new BufferedReader(
                new InputStreamReader(new FileInputStream(duongDan), StandardCharsets.UTF_8))) {
            String dong;
            int soDong = 0;
            while ((dong = doc.readLine()) != null) {
                soDong++;
                dong = dong.trim();
                if (soDong == 1 && dong.startsWith("\uFEFF")) {
                    dong = dong.substring(1).trim(); // bỏ ký tự BOM nếu file lưu bằng Notepad
                }
                if (dong.equals(TienIch.DONG_PHIEN_BAN)) {
                    dungPhienBan = true;
                    continue;
                }
                if (dong.isEmpty() || dong.startsWith("#")) {
                    continue;
                }
                if (!dungPhienBan) {
                    throw new IOException(duongDan + ", dòng " + soDong + ": thiếu dòng '" + TienIch.DONG_PHIEN_BAN
                            + "' ở đầu file (file cũ chưa có giá nhập, cần chuyển sang định dạng mới).");
                }
                try {
                    if (dong.startsWith("LO|")) {
                        LoHang lo = LoHang.tuDongFile(dong);
                        if (cacMaLo.contains(lo.getMaLo())) {
                            throw new IllegalArgumentException("Mã lô bị trùng trong file: " + lo.getMaLo());
                        }
                        SanPham chu = timTrongDanhSach(dsMoi, lo.getMaSP());
                        if (chu == null) {
                            throw new IllegalArgumentException("Lô " + lo.getMaLo() + " thuộc sản phẩm "
                                    + lo.getMaSP() + " chưa được khai báo ở các dòng phía trên.");
                        }
                        chu.themLo(lo);
                        cacMaLo.add(lo.getMaLo());
                    } else {
                        SanPham sp = taoSanPhamTuDongFile(dong);
                        if (timTrongDanhSach(dsMoi, sp.getMaSP()) != null) {
                            throw new IllegalArgumentException("Mã sản phẩm bị trùng trong file: " + sp.getMaSP());
                        }
                        dsMoi.add(sp);
                    }
                } catch (IllegalArgumentException e) {
                    throw new IOException(duongDan + ", dòng " + soDong + ": " + e.getMessage());
                } catch (IllegalStateException e) {
                    throw new IOException(duongDan + ", dòng " + soDong + ": " + e.getMessage());
                } catch (DateTimeParseException e) {
                    throw new IOException(duongDan + ", dòng " + soDong + ": ngày không hợp lệ (cần dạng yyyy-MM-dd).");
                }
            }
        }
        if (dsMoi.size() > sucChuaToiDa()) {
            throw new IOException("File có " + dsMoi.size() + " sản phẩm nhưng danh sách chỉ chứa tối đa "
                    + sucChuaToiDa() + ".");
        }
        return dsMoi;
    }

    @Override
    public void thayTheBang(ArrayList<SanPham> dsMoi) {
        if (dsMoi == null) {
            throw new IllegalArgumentException("Danh sách mới không được null.");
        }
        if (dsMoi.size() > sucChuaToiDa()) {
            throw new IllegalStateException("Danh sách mới có " + dsMoi.size() + " sản phẩm, vượt sức chứa "
                    + sucChuaToiDa() + ".");
        }
        xoaHet();
        for (SanPham sp : dsMoi) {
            themCuoi(sp);
        }
    }

    private static SanPham timTrongDanhSach(ArrayList<SanPham> ds, String ma) {
        for (SanPham sp : ds) {
            if (sp.getMaSP().equals(SanPham.chuanHoaMa(ma))) {
                return sp;
            }
        }
        return null;
    }

    // Tạo đúng lớp con (ThucAn hoặc DoUong) dựa vào trường loại ở đầu dòng
    private static SanPham taoSanPhamTuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);
        if (t.length != 8) {
            throw new IllegalArgumentException("Cần 8 trường ngăn cách bởi '|' nhưng có " + t.length + ".");
        }
        long giaNhap = Long.parseLong(t[3]);
        long giaBan = Long.parseLong(t[4]);
        double thue = Double.parseDouble(t[6]);
        if (t[0].equals("THUCAN")) {
            return new ThucAn(t[1], t[2], giaNhap, giaBan, t[5], thue, t[7]);
        }
        if (t[0].equals("DOUONG")) {
            return new DoUong(t[1], t[2], giaNhap, giaBan, t[5], thue, Integer.parseInt(t[7]));
        }
        throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: " + t[0]);
    }
}
