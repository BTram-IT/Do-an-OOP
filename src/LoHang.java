import java.time.LocalDate;
import java.util.Locale;

// Một LÔ HÀNG: một lần nhập của MỘT sản phẩm, có hạn sử dụng và số lượng còn riêng.
// Ví dụ: Sữa tươi nhập ngày 1/10 hết hạn 10/10 là một lô; sữa nhập ngày 5/10 hết hạn 20/10 là lô khác.
// - hanSuDung = null nghĩa là hàng không ghi hạn (không bao giờ bị coi là hết hạn).
// - maHoaDonNhap cho biết lô sinh ra từ hóa đơn nhập nào (để truy xuất, tra ra nhà cung cấp và kiểm tra file).
// - giaNhap là giá nhập THỰC TẾ (chưa thuế) của lô, sao chép từ dòng ChiTietPhieuNhap lúc xác nhận; dùng tính giá vốn.
//   Nó KHÔNG lấy từ SanPham.giaNhap (giá tham khảo) và không đổi khi giá tham khảo đổi.
// - soLuongCon thay đổi khi xuất/hoàn kho. Hàm giam/tang không có chữ public: chỉ SanPham gọi,
//   code bên ngoài phải đi qua QuanlyKho để không làm sai số lượng.
public class LoHang {

    public static final String TIEN_TO_MA = "LO";
    private static int boDem = 0;

    private final String maLo;
    private final String maSP;
    private final String maHoaDonNhap;
    private final LocalDate ngayNhap;
    private final LocalDate hanSuDung;
    private final int soLuongNhap;
    private int soLuongCon;
    private final long giaNhap;

    public LoHang(String maLo, String maSP, String maHoaDonNhap, LocalDate ngayNhap,
                  LocalDate hanSuDung, int soLuongNhap, int soLuongCon, long giaNhap) {
        this.maLo = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maLo, "Mã lô"));
        this.maSP = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maSP, "Mã sản phẩm của lô"));
        this.maHoaDonNhap = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maHoaDonNhap, "Mã hóa đơn nhập của lô"));
        if (giaNhap < 0 || giaNhap > SanPham.GIA_TOI_DA) {
            throw new IllegalArgumentException("Lô " + maLo + ": giá nhập phải từ 0 đến " + SanPham.GIA_TOI_DA + " đồng.");
        }
        if (ngayNhap == null) {
            throw new IllegalArgumentException("Ngày nhập của lô không được null.");
        }
        if (hanSuDung != null && hanSuDung.isBefore(ngayNhap)) {
            throw new IllegalArgumentException("Lô " + this.maLo + ": hạn sử dụng không được trước ngày nhập.");
        }
        if (soLuongNhap <= 0) {
            throw new IllegalArgumentException("Lô " + this.maLo + ": số lượng nhập phải lớn hơn 0.");
        }
        if (soLuongCon < 0 || soLuongCon > soLuongNhap) {
            throw new IllegalArgumentException("Lô " + this.maLo + ": số lượng còn phải từ 0 đến số lượng nhập.");
        }
        this.ngayNhap = ngayNhap;
        this.hanSuDung = hanSuDung;
        this.soLuongNhap = soLuongNhap;
        this.soLuongCon = soLuongCon;
        this.giaNhap = giaNhap;
        boDem = Math.max(boDem, SanPham.laySoTuMa(this.maLo, TIEN_TO_MA));
    }

    // Mã lô tiếp theo, ví dụ LO001 (đếm chung cho cả chương trình)
    public static String maKeTiep() {
        return String.format(Locale.ROOT, "%s%03d", TIEN_TO_MA, boDem + 1);
    }

    public String getMaLo() {
        return maLo;
    }

    public String getMaSP() {
        return maSP;
    }

    public String getMaHoaDonNhap() {
        return maHoaDonNhap;
    }

    // Giá nhập thực tế của lô (chưa thuế, đồng)
    public long getGiaNhap() {
        return giaNhap;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    // null = hàng không có hạn sử dụng
    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public int getSoLuongCon() {
        return soLuongCon;
    }

    // Hết hạn khi ngày kiểm tra SAU hạn sử dụng (đúng ngày hạn vẫn còn dùng được)
    public boolean isHetHan(LocalDate ngay) {
        return hanSuDung != null && hanSuDung.isBefore(ngay);
    }

    // Còn hạn nhưng sẽ hết hạn trong vòng soNgay ngày tới (tính cả ngày cuối)
    public boolean sapHetHan(LocalDate ngay, int soNgay) {
        return hanSuDung != null && !hanSuDung.isBefore(ngay) && !hanSuDung.isAfter(ngay.plusDays(soNgay));
    }

    // Quy tắc FEFO (First Expired, First Out): lô nào hết hạn sớm hơn thì xuất trước.
    // Lô có hạn xuất trước lô không hạn. Cùng hạn thì lô nhập trước xuất trước, cuối cùng so theo mã lô.
    public static boolean uuTienHon(LoHang a, LoHang b) {
        if (a.hanSuDung != null && b.hanSuDung != null) {
            int kq = a.hanSuDung.compareTo(b.hanSuDung);
            if (kq != 0) {
                return kq < 0;
            }
        } else if (a.hanSuDung != null) {
            return true;
        } else if (b.hanSuDung != null) {
            return false;
        }
        int kq = a.ngayNhap.compareTo(b.ngayNhap);
        if (kq != 0) {
            return kq < 0;
        }
        return SanPham.laySoTuMa(a.maLo, TIEN_TO_MA) < SanPham.laySoTuMa(b.maLo, TIEN_TO_MA);
    }

    // Trừ hàng khỏi lô (chỉ SanPham gọi, đã kiểm tra đủ hàng trước)
    void giam(int soLuong) {
        if (soLuong <= 0 || soLuong > soLuongCon) {
            throw new IllegalStateException("Lô " + maLo + " không đủ hàng để trừ " + soLuong + ".");
        }
        soLuongCon -= soLuong;
    }

    // Cộng hàng trả lại vào lô, không vượt số lượng nhập ban đầu (chỉ SanPham gọi)
    void tang(int soLuong) {
        if (soLuong <= 0 ||
                (long) soLuongCon + soLuong > soLuongNhap) {
            throw new IllegalStateException(
                    "Lô " + maLo + " không nhận thêm " + soLuong
            );
        }
        soLuongCon += soLuong;
    }

    // Dòng ghi file: LO|maLo|maSP|maHoaDonNhap|ngayNhap|hanSuDung|soLuongNhap|soLuongCon|giaNhap
    public String toDongFile() {
        String hanChuoi = "";
        if (hanSuDung != null) {
            hanChuoi = hanSuDung.toString();
        }
        return "LO|" + maLo + "|" + maSP + "|" + maHoaDonNhap + "|" + ngayNhap + "|" + hanChuoi
                + "|" + soLuongNhap + "|" + soLuongCon + "|" + giaNhap;
    }

    // Đọc lại từ dòng file, sai định dạng thì ném IllegalArgumentException (hoặc DateTimeParseException)
    public static LoHang tuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);
        if (t.length != 9 || !t[0].equals("LO")) {
            throw new IllegalArgumentException("Dòng lô cần dạng LO|maLo|maSP|maHoaDonNhap|ngayNhap|hanSuDung|soLuongNhap|soLuongCon|giaNhap.");
        }
        LocalDate han = null;
        if (!t[5].isEmpty()) {
            han = LocalDate.parse(t[5]);
        }
        return new LoHang(t[1], t[2], t[3], LocalDate.parse(t[4]), han,
                Integer.parseInt(t[6]), Integer.parseInt(t[7]), Long.parseLong(t[8]));
    }

    @Override
    public String toString() {
        String han = "không có HSD";
        if (hanSuDung != null) {
            han = "HSD " + TienIch.dinhDangNgay(hanSuDung);
        }
        return maLo + " | " + maSP + " | hóa đơn " + maHoaDonNhap + " | nhập " + TienIch.dinhDangNgay(ngayNhap)
                + " | " + han + " | còn " + soLuongCon + "/" + soLuongNhap
                + " | giá nhập " + TienIch.dinhDangTien(giaNhap);
    }
}
