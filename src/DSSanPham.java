import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

// Danh sách sản phẩm của kho: class độc lập (không extends, không implements), dùng ArrayList<SanPham>.
// Tự lo nghiệp vụ (thêm, sửa, xóa, tìm, sắp xếp, tồn kho theo lô, FEFO, hoàn kho, thống kê) và tự đọc/ghi file UTF-8.
// Lỗi dữ liệu sai -> IllegalArgumentException; thao tác không hợp lệ lúc này (hết tồn, còn tồn khi xóa) -> IllegalStateException.
public class DSSanPham {

    // Dòng đầu của file dữ liệu kho; phiên bản 2 = có giá nhập (SanPham.giaNhap, LoHang.giaNhap) và HoadonNhap.
    // QuanlyKho cũng dùng hằng này cho file hóa đơn nhập.
    public static final String DONG_PHIEN_BAN = "#PHIENBAN|2";

    // Regex gắn vào Ktradulieu.nhapChuoi (Ktradulieu mới chỉ có nhapChuoi/nhapSoNguyen(>0)/nhapSoThuc(>0)/luaChon,
    // không có hàm "không rỗng" hay "số không âm", nên kho tự khai báo regex tương ứng)
    static final String REGEX_KHONG_RONG = ".+";
    static final String LOI_KHONG_RONG = "Không được để trống!";
    static final String REGEX_SO_KHONG_AM = "\\d{1,18}";
    static final String LOI_SO_KHONG_AM = "Phải nhập số nguyên không âm (tối đa 18 chữ số)!";
    static final String REGEX_SO_THUC_KHONG_AM = "\\d{1,9}(\\.\\d{1,4})?";
    static final String LOI_SO_THUC_KHONG_AM = "Phải nhập số không âm, dấu thập phân là dấu chấm (ví dụ 8 hoặc 8.5)!";

    private static final int THEO_MA = 1;
    private static final int THEO_TEN = 2;
    private static final int THEO_GIA = 3;
    private static final int THEO_TON = 4;

    private ArrayList<SanPham> ds;

    public DSSanPham() {
        ds = new ArrayList<>();
    }

    // ===================== THÔNG TIN CHUNG =====================

    public int soLuong() {
        return ds.size();
    }

    // Lấy sản phẩm ở vị trí viTri (0 đến soLuong()-1)
    public SanPham layTheoViTri(int viTri) {
        if (viTri < 0 || viTri >= ds.size()) {
            throw new IndexOutOfBoundsException("Vị trí " + viTri + " nằm ngoài danh sách (có " + ds.size() + " sản phẩm).");
        }
        return ds.get(viTri);
    }

    // ===================== THÊM / NHẬP =====================

    public void them(SanPham sp) {
        if (sp == null) {
            throw new IllegalArgumentException("Sản phẩm không được null.");
        }
        if (tonTaiMa(sp.getMaSP())) {
            throw new IllegalArgumentException("Mã sản phẩm đã tồn tại: " + sp.getMaSP());
        }
        ds.add(sp);
    }

    // Thêm nhiều sản phẩm cùng lúc: được tất cả hoặc không được cái nào
    public int themNhieu(SanPham[] dsMoi) {
        if (dsMoi == null || dsMoi.length == 0) {
            throw new IllegalArgumentException("Không có sản phẩm nào để thêm.");
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
            ds.add(dsMoi[i]);
        }
        return dsMoi.length;
    }

    // Nhập n sản phẩm từ bàn phím; sản phẩm sai thì báo lỗi và nhập lại đúng sản phẩm đó
    public void nhapN(int n, Scanner sc) {
        if (n <= 0) {
            throw new IllegalArgumentException("Số sản phẩm cần nhập phải lớn hơn 0.");
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

    // Form nhập một sản phẩm, dùng các hàm nhập của Ktradulieu.
    // Sản phẩm mới chưa có lô nào (tồn 0): hàng chỉ vào kho qua hóa đơn nhập để có lịch sử.
    public static SanPham nhapSanPhamTuBanPhim(Scanner sc) {
        int loai = Ktradulieu.luaChon(sc, "Loại sản phẩm (1 = Thức ăn, 2 = Đồ uống): ", 1, 2);
        String maGoiY = ThucAn.maKeTiep();
        if (loai == 2) {
            maGoiY = DoUong.maKeTiep();
        }
        // Mã nhập tay phải cùng dạng với mã kho tự sinh (TA### thức ăn, DU### đồ uống) để Thu ngân/giỏ hàng nhận ra;
        // để trống (Enter) = tự tạo. Regex gắn thẳng vào Ktradulieu.nhapChuoi.
        String tienTo = loai == 1 ? ThucAn.TIEN_TO_MA : DoUong.TIEN_TO_MA;
        String ma = Ktradulieu.nhapChuoi(sc, "Mã sản phẩm (Enter = tự tạo " + maGoiY + "): ",
                "(?i)(" + tienTo + "\\d{3,})?", "Mã phải có dạng " + tienTo + " và ít nhất 3 chữ số (ví dụ " + maGoiY + ")!");
        if (ma.isEmpty()) {
            ma = maGoiY;
        }
        String ten = Ktradulieu.nhapChuoi(sc, "Tên sản phẩm: ", DSSanPham.REGEX_KHONG_RONG, DSSanPham.LOI_KHONG_RONG);
        long giaNhap = Long.parseLong(Ktradulieu.nhapChuoi(sc, "Giá nhập tham khảo (đồng, CHƯA thuế, 0 = chưa có): ", DSSanPham.REGEX_SO_KHONG_AM, DSSanPham.LOI_SO_KHONG_AM));
        long giaBan = Ktradulieu.nhapSoNguyen(sc, "Giá bán (đồng, CHƯA thuế): ");
        String donVi = Ktradulieu.nhapChuoi(sc, "Đơn vị tính (chai, cái, phần...): ", DSSanPham.REGEX_KHONG_RONG, DSSanPham.LOI_KHONG_RONG);
        double thue = Double.parseDouble(Ktradulieu.nhapChuoi(sc, "Thuế suất bán (%), nhập 0 nếu chưa khai báo: ", DSSanPham.REGEX_SO_THUC_KHONG_AM, DSSanPham.LOI_SO_THUC_KHONG_AM));
        // Hạn sử dụng không hỏi ở đây: nó thuộc từng lô, khai báo khi lập hóa đơn nhập
        if (loai == 1) {
            String nsx = Ktradulieu.nhapChuoi(sc, "Nhà sản xuất: ", DSSanPham.REGEX_KHONG_RONG, DSSanPham.LOI_KHONG_RONG);
            return new ThucAn(ma, ten, giaNhap, giaBan, donVi, thue, nsx);
        }
        int dungTich = Ktradulieu.luaChon(sc, "Dung tích (ml): ", 1, DoUong.DUNG_TICH_TOI_DA_ML);
        return new DoUong(ma, ten, giaNhap, giaBan, donVi, thue, dungTich);
    }

    // ===================== SỬA / XÓA =====================

    // Sửa theo mã bằng thông tin trong thongTinMoi (không đổi được mã, loại, đơn vị tính; các lô hàng giữ nguyên)
    public void sua(String ma, SanPham thongTinMoi) {
        layBatBuoc(ma).capNhatThongTin(thongTinMoi);
    }

    // Xóa theo mã, không xóa được nếu còn tồn kho
    public void xoaTheoMa(String ma) {
        int viTri = timViTriTheoMa(ma);
        if (viTri < 0) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + ma);
        }
        SanPham sp = ds.get(viTri);
        if (sp.getSoLuongTon() > 0) {
            throw new IllegalStateException("Không thể xóa " + sp.getMaSP() + " vì còn tồn kho "
                    + sp.getSoLuongTon() + " " + sp.getDonViTinh() + ".");
        }
        ds.remove(viTri);
    }

    // ===================== TÌM KIẾM =====================

    private int timViTriTheoMa(String ma) {
        String maChuan = SanPham.chuanHoaMa(ma);
        if (maChuan == null) {
            return -1;
        }
        for (int i = 0; i < ds.size(); i++) {
            if (ds.get(i).getMaSP().equals(maChuan)) {
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

    // Thêm lô vào đúng sản phẩm (HoadonNhap.xacNhan gọi). SanPham.themLo tự kiểm tra mã lô, hạn dùng, tràn tồn.
    void themLo(LoHang lo) {
        if (lo == null) {
            throw new IllegalArgumentException("Lô hàng không được null.");
        }
        layBatBuoc(lo.getMaSP()).themLo(lo);
    }

    // Xóa lô chưa bị xuất của một sản phẩm (HoadonNhap.huy gọi)
    void xoaLo(String maSP, String maLo) {
        layBatBuoc(maSP).xoaLo(maLo);
    }

    public boolean tonTaiMa(String ma) {
        return timViTriTheoMa(ma) >= 0;
    }

    // Tìm chính xác theo mã, không thấy trả về null
    public SanPham timTheoMa(String ma) {
        int viTri = timViTriTheoMa(ma);
        if (viTri < 0) {
            return null;
        }
        return ds.get(viTri);
    }

    public ArrayList<SanPham> timTheoMaGanDung(String tuKhoa) {
        String khoa = SanPham.chuanHoaMa(kiemTraTuKhoa(tuKhoa));
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            if (sp.getMaSP().contains(khoa)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    public ArrayList<SanPham> timTheoTenChinhXac(String ten) {
        String khoa = kiemTraTuKhoa(ten);
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            if (sp.getTenSP().equalsIgnoreCase(khoa)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    // Tên chứa từ khóa, không phân biệt hoa thường và bỏ qua dấu
    public ArrayList<SanPham> timTheoTenGanDung(String tuKhoa) {
        String khoa = SanPham.boDauVaThuong(kiemTraTuKhoa(tuKhoa));
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            if (SanPham.boDauVaThuong(sp.getTenSP()).contains(khoa)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    public ArrayList<SanPham> timTheoLoai(String loai) {
        String khoa = SanPham.boDauVaThuong(kiemTraTuKhoa(loai));
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            if (SanPham.boDauVaThuong(sp.getLoai()).equals(khoa)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    public ArrayList<SanPham> timTheoKhoangGia(long giaTu, long giaDen) {
        if (giaTu < 0 || giaTu > giaDen) {
            throw new IllegalArgumentException("Khoảng giá không hợp lệ: cần 0 <= giá từ <= giá đến.");
        }
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            if (sp.getGiaBan() >= giaTu && sp.getGiaBan() <= giaDen) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    // Tìm kết hợp nhiều tiêu chí cùng lúc. Tên hoặc loại để "" thì bỏ qua tiêu chí đó.
    public ArrayList<SanPham> timNhieuTieuChi(String tuKhoaTen, String loai, long giaTu, long giaDen) {
        if (giaTu < 0 || giaTu > giaDen) {
            throw new IllegalArgumentException("Khoảng giá không hợp lệ: cần 0 <= giá từ <= giá đến.");
        }
        String khoaTen = SanPham.boDauVaThuong(tuKhoaTen);
        String khoaLoai = SanPham.boDauVaThuong(loai);
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            boolean hopLe = true;
            // Mỗi tiêu chí không đạt thì loại sản phẩm; tiêu chí để trống thì bỏ qua
            if (!khoaTen.isEmpty() && !SanPham.boDauVaThuong(sp.getTenSP()).contains(khoaTen)) {
                hopLe = false;
            }
            if (!khoaLoai.isEmpty() && !SanPham.boDauVaThuong(sp.getLoai()).equals(khoaLoai)) {
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
    // Sắp xếp chọn (selection sort) trực tiếp trên ArrayList.

    private int soSanh(SanPham a, SanPham b, int tieuChi) {
        int ketQua = 0;
        if (tieuChi == THEO_MA) {
            ketQua = a.getMaSP().compareTo(b.getMaSP());
        } else if (tieuChi == THEO_TEN) {
            ketQua = SanPham.boDauVaThuong(a.getTenSP()).compareTo(SanPham.boDauVaThuong(b.getTenSP()));
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
        int n = ds.size();
        for (int i = 0; i < n - 1; i++) {
            int viTriTot = i;
            for (int j = i + 1; j < n; j++) {
                int kq = soSanh(ds.get(j), ds.get(viTriTot), tieuChi);
                if (!tangDan) {
                    kq = -kq;
                }
                if (kq < 0) {
                    viTriTot = j;
                }
            }
            if (viTriTot != i) {
                SanPham tam = ds.get(i);
                ds.set(i, ds.get(viTriTot));
                ds.set(viTriTot, tam);
            }
        }
    }

    public void sapXepTheoMa() {
        sapXep(THEO_MA, true);
    }

    public void sapXepTheoTen() {
        sapXep(THEO_TEN, true);
    }

    public void sapXepTheoGiaBan(boolean tangDan) {
        sapXep(THEO_GIA, tangDan);
    }

    public void sapXepTheoTon(boolean tangDan) {
        sapXep(THEO_TON, tangDan);
    }

    // ===================== TỒN KHO THEO LÔ =====================

    // Tổng tồn của sản phẩm (cộng mọi lô, kể cả lô hết hạn)
    public int layTon(String ma) {
        return layBatBuoc(ma).getSoLuongTon();
    }

    // Số lượng còn hạn, bán được vào ngày đã cho
    public int layTonBanDuoc(String ma, LocalDate ngay) {
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày kiểm tra không được null.");
        }
        return layBatBuoc(ma).getSoLuongBanDuoc(ngay);
    }

    // Còn đủ hàng CÒN HẠN để bán soLuong không (bản không có ngày dùng ngày hôm nay)
    public boolean duTon(String ma, int soLuong) {
        return duTon(ma, soLuong, LocalDate.now());
    }

    public boolean duTon(String ma, int soLuong, LocalDate ngay) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng kiểm tra phải lớn hơn 0.");
        }
        return soLuong <= layTonBanDuoc(ma, ngay);
    }

    // Xuất theo FEFO (hết hạn sớm xuất trước), trả về các lô đã lấy. Không đủ hàng còn hạn thì lỗi và không đổi gì.
    public ArrayList<LoXuat> xuatFEFO(String ma, int soLuong, LocalDate ngay) {
        return layBatBuoc(ma).xuatTheoFEFO(soLuong, ngay);
    }

    // Hoàn soLuong về đúng lô maLo của sản phẩm
    public void hoanLo(String ma, String maLo, int soLuong) {
        layBatBuoc(ma).hoanVaoLo(maLo, soLuong);
    }

    // ===================== THỐNG KÊ =====================

    public long tongSoLuongTon() {
        long tong = 0;
        for (SanPham sp : ds) {
            tong += sp.getSoLuongTon();
        }
        return tong;
    }

    // Tổng tồn x giá bán (giá trị bán dự kiến, chưa thuế)
    public long tongGiaTriTon() {
        long tong = 0;
        for (SanPham sp : ds) {
            tong += sp.getGiaTriTonTheoGiaBan();
        }
        return tong;
    }

    public int demTheoLoai(String loai) {
        return timTheoLoai(loai).size();
    }

    // Hàng còn bán được (còn hạn) nhưng không quá nguong, tính theo hôm nay
    public ArrayList<SanPham> layHangSapHet(int nguong) {
        if (nguong < 0) {
            throw new IllegalArgumentException("Ngưỡng sắp hết hàng không được âm.");
        }
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            int banDuoc = sp.getSoLuongBanDuoc(LocalDate.now());
            if (banDuoc > 0 && banDuoc <= nguong) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    // Hàng tồn bằng 0
    public ArrayList<SanPham> layHangHetHang() {
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            if (sp.getSoLuongTon() == 0) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    // Sản phẩm đang có lô hết hạn mà còn hàng
    public ArrayList<SanPham> layHangHetHan(LocalDate ngay) {
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày kiểm tra không được null.");
        }
        ArrayList<SanPham> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            if (sp.coHangHetHan(ngay)) {
                ketQua.add(sp);
            }
        }
        return ketQua;
    }

    // Các LÔ đã hết hạn nhưng còn hàng
    public ArrayList<LoHang> layLoHetHan(LocalDate ngay) {
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày kiểm tra không được null.");
        }
        ArrayList<LoHang> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            for (LoHang lo : sp.getCacLo()) {
                if (lo.getSoLuongCon() > 0 && lo.isHetHan(ngay)) {
                    ketQua.add(lo);
                }
            }
        }
        return ketQua;
    }

    // Các lô còn hạn nhưng sẽ hết hạn trong soNgay ngày tới, còn hàng
    public ArrayList<LoHang> layLoSapHetHan(LocalDate ngay, int soNgay) {
        if (ngay == null || soNgay < 0) {
            throw new IllegalArgumentException("Cần ngày kiểm tra và số ngày không âm.");
        }
        ArrayList<LoHang> ketQua = new ArrayList<>();
        for (SanPham sp : ds) {
            for (LoHang lo : sp.getCacLo()) {
                if (lo.getSoLuongCon() > 0 && lo.sapHetHan(ngay, soNgay)) {
                    ketQua.add(lo);
                }
            }
        }
        return ketQua;
    }

    public String baoCaoThongKe() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== THỐNG KÊ KHO =====\n");
        sb.append("Tổng số mặt hàng : ").append(soLuong()).append("\n");
        sb.append("Tổng số lượng tồn: ").append(tongSoLuongTon()).append("\n");
        sb.append("Giá trị tồn (theo giá bán, chưa thuế): ").append(SanPham.dinhDangTien(tongGiaTriTon())).append("\n");
        long hetHan = 0;
        for (SanPham sp : ds) {
            hetHan += sp.getSoLuongHetHan(LocalDate.now());
        }
        sb.append("Số lượng nằm trong lô đã hết hạn (hôm nay): ").append(hetHan).append("\n");

        // Gom danh sách các loại đang có
        ArrayList<String> cacLoai = new ArrayList<>();
        for (SanPham sp : ds) {
            if (!cacLoai.contains(sp.getLoai())) {
                cacLoai.add(sp.getLoai());
            }
        }
        // Với mỗi loại: đếm mặt hàng, cộng tồn, cộng giá trị
        for (String loai : cacLoai) {
            int soMatHang = 0;
            long tongTon = 0;
            long giaTri = 0;
            for (SanPham sp : ds) {
                if (sp.getLoai().equals(loai)) {
                    soMatHang++;
                    tongTon += sp.getSoLuongTon();
                    giaTri += sp.getGiaTriTonTheoGiaBan();
                }
            }
            sb.append("- ").append(loai).append(": ").append(soMatHang).append(" mặt hàng, tồn ")
                    .append(tongTon).append(", giá trị ").append(SanPham.dinhDangTien(giaTri)).append("\n");
        }
        return sb.toString();
    }

    public void xuat() {
        if (ds.isEmpty()) {
            System.out.println("(Danh sách sản phẩm đang rỗng)");
            return;
        }
        System.out.println("Danh sách sản phẩm (" + ds.size() + "):");
        for (int i = 0; i < ds.size(); i++) {
            // Đa hình: toString() chạy theo đúng lớp thật (ThucAn hoặc DoUong)
            System.out.println((i + 1) + ". " + ds.get(i));
        }
    }

    // ===================== ĐỌC / GHI FILE (UTF-8) =====================
    // File gồm dòng phiên bản #PHIENBAN|2, các dòng sản phẩm (THUCAN|... hoặc DOUONG|...),
    // ngay sau mỗi sản phẩm là các dòng lô LO|... của nó.
    // Tồn kho KHÔNG ghi riêng: tồn luôn bằng tổng số lượng còn của các lô, nên không thể lệch nhau.

    // Ghi AN TOÀN: ghi ra file tạm (duongDan + ".tmp") trước, ghi xong hết mới đổi tên thành file chính.
    // Nếu ghi lỗi giữa chừng thì file chính cũ vẫn nguyên vẹn, không bị cụt hoặc rỗng.
    public void ghiFile(String duongDan) throws IOException {
        Path dich = Paths.get(duongDan);
        Path tam = Paths.get(duongDan + ".tmp");
        try {
            ghiRaFile(tam.toString());
            Files.move(tam, dich, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Files.deleteIfExists(tam);
            throw e;
        }
    }

    // package-private: QuanlyKho.luuDuLieu ghi file tạm của sản phẩm rồi thay cùng lúc với các file khác
    void ghiRaFile(String duongDan) throws IOException {
        try (BufferedWriter ghi = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(duongDan), StandardCharsets.UTF_8))) {
            ghi.write(DONG_PHIEN_BAN);
            ghi.newLine();
            ghi.write("# loai|ma|ten|giaNhapThamKhao|giaBan|donViTinh|thueSuatBan(%)|truongRieng (nhaSanXuat hoac dungTichMl)");
            ghi.newLine();
            ghi.write("# LO|maLo|maSP|maHoaDonNhap|ngayNhap|hanSuDung|soLuongNhap|soLuongCon|giaNhapThucTe");
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

    // Đọc lỗi ở bất kỳ dòng nào thì ném IOException và danh sách hiện tại giữ nguyên
    public void docFile(String duongDan) throws IOException {
        thayTheBang(docSanPhamTuFile(duongDan));
    }

    // Chỉ ĐỌC file thành danh sách tạm (chưa đụng danh sách hiện tại) để QuanlyKho kiểm tra liên kết với hóa đơn nhập trước
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
                if (soDong == 1 && dong.startsWith("﻿")) {
                    dong = dong.substring(1).trim(); // bỏ ký tự BOM nếu file lưu bằng Notepad
                }
                if (dong.equals(DONG_PHIEN_BAN)) {
                    dungPhienBan = true;
                    continue;
                }
                if (dong.isEmpty() || dong.startsWith("#")) {
                    continue;
                }
                if (!dungPhienBan) {
                    throw new IOException(duongDan + ", dòng " + soDong + ": thiếu dòng '" + DONG_PHIEN_BAN
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
        return dsMoi;
    }

    // Thay toàn bộ nội dung danh sách bằng danh sách đã đọc
    public void thayTheBang(ArrayList<SanPham> dsMoi) {
        if (dsMoi == null) {
            throw new IllegalArgumentException("Danh sách mới không được null.");
        }
        ds.clear();
        for (SanPham sp : dsMoi) {
            ds.add(sp);
        }
    }

    private static SanPham timTrongDanhSach(ArrayList<SanPham> danhSach, String ma) {
        for (SanPham sp : danhSach) {
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
