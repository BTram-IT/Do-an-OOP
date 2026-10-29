import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

public abstract class SanPham {

    public static final long GIA_TOI_DA = 1_000_000_000L;
    public static final double THUE_SUAT_TOI_DA = 100.0;

    private final String maSP;
    private String tenSP;
    private long giaNhap;
    private long giaBan;
    private final ArrayList<LoHang> cacLo = new ArrayList<>();
    private final String donViTinh;
    private double thueSuat;

    // Constructor
    // Chú ý: giaNhap đứng trước giaBan (hai tham số long liền nhau, đừng đảo)
    protected SanPham(String maSP, String tenSP, long giaNhap, long giaBan, String donViTinh, double thueSuat) {
        this.maSP = chuanHoaMa(kiemTraVanBan(maSP, "Mã sản phẩm"));
        this.tenSP = kiemTraVanBan(tenSP, "Tên sản phẩm");
        this.giaNhap = kiemTraGiaNhap(giaNhap);
        this.giaBan = kiemTraGiaBan(giaBan);

        this.donViTinh = kiemTraVanBan(donViTinh, "Đơn vị tính");
        this.thueSuat = kiemTraThueSuat(thueSuat);
    }

    protected static String kiemTraVanBan(String giaTri, String tenTruong) {
        if (giaTri == null || giaTri.trim().isEmpty()) {
            throw new IllegalArgumentException(tenTruong + " không được để trống.");
        }
        String sach = giaTri.trim();
        if (sach.indexOf('|') >= 0 || sach.indexOf('\n') >= 0 || sach.indexOf('\r') >= 0) {
            throw new IllegalArgumentException(tenTruong + " không được chứa ký tự '|' hoặc xuống dòng.");
        }
        return sach;
    }

    // Giá nhập tham khảo: 0 nghĩa là chưa thiết lập
    protected static long kiemTraGiaNhap(long giaNhap) {
        if (giaNhap < 0 || giaNhap > GIA_TOI_DA) {
            throw new IllegalArgumentException("Giá nhập tham khảo phải từ 0 đến " + GIA_TOI_DA + " đồng (0 = chưa thiết lập).");
        }
        return giaNhap;
    }

    protected static long kiemTraGiaBan(long giaBan) {
        if (giaBan <= 0 || giaBan > GIA_TOI_DA) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0 và không vượt quá " + GIA_TOI_DA + " đồng.");
        }
        return giaBan;
    }

    protected static double kiemTraThueSuat(double thueSuat) {
        if (!(thueSuat >= 0 && thueSuat <= THUE_SUAT_TOI_DA)) {
            throw new IllegalArgumentException("Thuế suất là phần trăm, phải nằm trong khoảng 0 đến " + THUE_SUAT_TOI_DA + ".");
        }
        return thueSuat;
    }

    public static String chuanHoaMa(String ma) {
        return ma == null ? null : ma.trim().toUpperCase(Locale.ROOT);
    }

    protected static int laySoTuMa(String ma, String tienTo) {
        if (ma == null || !ma.startsWith(tienTo)) {
            return 0;
        }
        String phanSo = ma.substring(tienTo.length());
        if (phanSo.isEmpty() || phanSo.length() > 9) {
            return 0;
        }
        for (int i = 0; i < phanSo.length(); i++) {
            if (phanSo.charAt(i) < '0' || phanSo.charAt(i) > '9') {
                return 0;
            }
        }
        return Integer.parseInt(phanSo);
    }

    public abstract String getLoai();

    // Sản phẩm loại này có bắt buộc mỗi lô phải ghi hạn sử dụng không (đa hình: ThucAn = có, DoUong = không bắt buộc)
    public abstract boolean batBuocCoHanSuDung();

    public abstract String toDongFile();

    protected abstract void capNhatThongTinRieng(SanPham nguon);

    public final void capNhatThongTin(SanPham nguon) {
        if (nguon == null) {
            throw new IllegalArgumentException("Thông tin mới không được null.");
        }
        if (!maSP.equals(nguon.maSP)) {
            throw new IllegalArgumentException("Không được đổi mã sản phẩm (" + maSP + " -> " + nguon.maSP + ").");
        }
        if (getClass() != nguon.getClass()) {
            throw new IllegalArgumentException("Không được đổi loại sản phẩm của " + maSP + ".");
        }
        if (!donViTinh.equals(nguon.donViTinh)) {
            throw new IllegalArgumentException("Không được đổi đơn vị tính của " + maSP + " vì sẽ làm sai số lượng đã nhập/xuất.");
        }
        this.tenSP = nguon.tenSP;
        this.giaNhap = nguon.giaNhap;
        this.giaBan = nguon.giaBan;
        this.thueSuat = nguon.thueSuat;
        capNhatThongTinRieng(nguon);
    }

    // ===================== TỒN KHO THEO LÔ =====================
    // Cách dùng đúng: gọi qua QuanlyKho (xuatKho, hoanKho...) để có kiểm tra đầy đủ.

    // Tồn kho = tổng số lượng còn của mọi lô (kể cả lô đã hết hạn)
    public int getSoLuongTon() {
        int tong = 0;
        for (LoHang lo : cacLo) {
            tong += lo.getSoLuongCon();
        }
        return tong;
    }

    // Số lượng còn dùng/bán được vào ngày đã cho (không tính lô hết hạn)
    public int getSoLuongBanDuoc(LocalDate ngay) {
        int tong = 0;
        for (LoHang lo : cacLo) {
            if (!lo.isHetHan(ngay)) {
                tong += lo.getSoLuongCon();
            }
        }
        return tong;
    }

    // Số lượng đang nằm trong các lô đã hết hạn vào ngày đã cho
    public int getSoLuongHetHan(LocalDate ngay) {
        return getSoLuongTon() - getSoLuongBanDuoc(ngay);
    }

    // Có lô nào hết hạn mà vẫn còn hàng không
    public boolean coHangHetHan(LocalDate ngay) {
        return getSoLuongHetHan(ngay) > 0;
    }

    // Còn đủ hàng CÒN HẠN để bán soLuong vào hôm nay không (tên khớp DonHang hiện tại)
    public boolean kiemTraTonKho(int soLuong) {
        return soLuong > 0 && soLuong <= getSoLuongBanDuoc(LocalDate.now());
    }

    // Trả về bản sao danh sách lô (thêm/xóa trong bản sao không ảnh hưởng sản phẩm)
    public ArrayList<LoHang> getCacLo() {
        return new ArrayList<LoHang>(cacLo);
    }

    public LoHang timLo(String maLo) {
        String maChuan = chuanHoaMa(maLo);
        for (LoHang lo : cacLo) {
            if (lo.getMaLo().equals(maChuan)) {
                return lo;
            }
        }
        return null;
    }

    // Lô sinh ra từ một hóa đơn nhập (mỗi hóa đơn chỉ có một dòng cho mỗi sản phẩm nên tối đa một lô), không có thì null
    public LoHang timLoTheoHoaDonNhap(String maHoaDon) {
        String maChuan = chuanHoaMa(maHoaDon);
        for (LoHang lo : cacLo) {
            if (lo.getMaHoaDonNhap().equals(maChuan)) {
                return lo;
            }
        }
        return null;
    }

    // Kiểm tra nhập thêm một lô (hạn sử dụng, số lượng) có hợp lệ với sản phẩm này không, chưa thay đổi gì
    public void kiemTraCoTheNhapLo(LocalDate hanSuDung, int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
        }
        kiemTraHanSuDung(hanSuDung);
        if ((long) getSoLuongTon() + soLuong > Integer.MAX_VALUE) {
            throw new IllegalStateException("Tồn kho của " + maSP + " sẽ vượt giới hạn nếu nhập thêm " + soLuong + ".");
        }
    }

    private void kiemTraHanSuDung(LocalDate hanSuDung) {
        if (hanSuDung == null && batBuocCoHanSuDung()) {
            throw new IllegalArgumentException(getLoai() + " " + maSP + " bắt buộc phải có hạn sử dụng cho mỗi lô.");
        }
    }

    // Thêm một lô vào sản phẩm (HoaDonNhap gọi khi xác nhận, và khi đọc file)
    public void themLo(LoHang lo) {
        if (lo == null) {
            throw new IllegalArgumentException("Lô hàng không được null.");
        }
        if (!lo.getMaSP().equals(maSP)) {
            throw new IllegalArgumentException("Lô " + lo.getMaLo() + " thuộc sản phẩm " + lo.getMaSP()
                    + ", không thêm vào " + maSP + " được.");
        }
        if (timLo(lo.getMaLo()) != null) {
            throw new IllegalArgumentException("Mã lô bị trùng: " + lo.getMaLo());
        }
        kiemTraHanSuDung(lo.getHanSuDung());
        if ((long) getSoLuongTon() + lo.getSoLuongCon() > Integer.MAX_VALUE) {
            throw new IllegalStateException("Tồn kho của " + maSP + " vượt giới hạn cho phép.");
        }
        cacLo.add(lo);
    }

    // Xóa lô khi hủy phiếu nhập. Chỉ xóa được lô CHƯA bị xuất, để không làm mất dấu hàng đã bán.
    public void xoaLo(String maLo) {
        LoHang lo = timLo(maLo);
        if (lo == null) {
            throw new IllegalArgumentException("Không có lô " + maLo + " trong sản phẩm " + maSP + ".");
        }
        if (lo.getSoLuongCon() != lo.getSoLuongNhap()) {
            throw new IllegalStateException("Lô " + lo.getMaLo() + " đã xuất " + (lo.getSoLuongNhap() - lo.getSoLuongCon())
                    + " nên không xóa được.");
        }
        cacLo.remove(lo);
    }

    // Xuất soLuong theo FEFO: lấy từ lô hết hạn sớm nhất trước, bỏ qua lô đã hết hạn vào ngày đã cho.
    // Đủ hàng còn hạn thì lấy hết, không thì báo lỗi và KHÔNG thay đổi gì.
    // Trả về danh sách (lô, số lượng) đã lấy để về sau hoàn đúng lô.
    public ArrayList<LoXuat> xuatTheoFEFO(int soLuong, LocalDate ngay) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng xuất kho phải lớn hơn 0.");
        }
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày xuất kho không được null.");
        }
        int banDuoc = getSoLuongBanDuoc(ngay);
        if (soLuong > banDuoc) {
            throw new IllegalStateException("Không đủ hàng còn hạn cho " + maSP + ": cần " + soLuong
                    + ", còn hạn " + banDuoc + " " + donViTinh + " (tổng tồn " + getSoLuongTon()
                    + ", trong đó hết hạn " + getSoLuongHetHan(ngay) + ").");
        }
        ArrayList<LoXuat> ketQua = new ArrayList<>();
        int canLay = soLuong;
        while (canLay > 0) {
            // Chọn lô ưu tiên nhất trong các lô còn hàng và còn hạn
            LoHang tot = null;
            for (LoHang lo : cacLo) {
                if (lo.getSoLuongCon() > 0 && !lo.isHetHan(ngay)
                        && (tot == null || LoHang.uuTienHon(lo, tot))) {
                    tot = lo;
                }
            }
            int lay = Math.min(canLay, tot.getSoLuongCon());
            tot.giam(lay);
            ketQua.add(new LoXuat(maSP, tot.getMaLo(), lay));
            canLay -= lay;
        }
        return ketQua;
    }

    // Kiểm tra có hoàn được soLuong vào lô không (không vượt số lượng nhập ban đầu), chưa thay đổi gì
    public void kiemTraCoTheHoanVaoLo(String maLo, int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng hoàn phải lớn hơn 0.");
        }
        LoHang lo = timLo(maLo);
        if (lo == null) {
            throw new IllegalArgumentException("Sản phẩm " + maSP + " không có lô " + maLo + ".");
        }
        if (lo.getSoLuongCon() + soLuong > lo.getSoLuongNhap()) {
            throw new IllegalStateException("Không hoàn " + soLuong + " vào lô " + lo.getMaLo() + ": lô nhập "
                    + lo.getSoLuongNhap() + ", hiện còn " + lo.getSoLuongCon() + " (hoàn thêm sẽ vượt số đã nhập).");
        }
    }

    // Hoàn hàng về đúng lô đã xuất
    public void hoanVaoLo(String maLo, int soLuong) {
        kiemTraCoTheHoanVaoLo(maLo, soLuong);
        timLo(maLo).tang(soLuong);
    }

    public long getGiaTriTonTheoGiaBan() {
        return (long) getSoLuongTon() * giaBan;
    }

    // Giá trị tồn theo GIÁ VỐN: tổng (số lượng còn x giá nhập thực tế của từng lô). Không dùng giaNhap tham khảo.
    public long getGiaTriTonTheoGiaVon() {
        long tong = 0;
        for (LoHang lo : cacLo) {
            tong = Math.addExact(tong, Math.multiplyExact((long) lo.getSoLuongCon(), lo.getGiaNhap()));
        }
        return tong;
    }

    // Giá vốn bình quân của hàng đang tồn (làm tròn xuống); 0 nếu hết hàng
    public long tinhGiaVonBinhQuanTon() {
        int ton = getSoLuongTon();
        return ton == 0 ? 0 : getGiaTriTonTheoGiaVon() / ton;
    }

    // Giá nhập thực tế của lô nhập gần nhất (khác giaNhap tham khảo); null nếu chưa nhập lần nào
    public Long getGiaNhapThucTeGanNhat() {
        LoHang moiNhat = null;
        for (LoHang lo : cacLo) {
            if (moiNhat == null || !lo.getNgayNhap().isBefore(moiNhat.getNgayNhap())) {
                moiNhat = lo;
            }
        }
        return moiNhat == null ? null : Long.valueOf(moiNhat.getGiaNhap());
    }

    // Mô tả các lô, mỗi lô một dòng
    public String moTaCacLo() {
        if (cacLo.isEmpty()) {
            return "(Sản phẩm " + maSP + " chưa có lô nào)";
        }
        String ketQua = "Các lô của " + maSP + " - " + tenSP + ":";
        for (LoHang lo : cacLo) {
            ketQua += "\n    + " + lo;
        }
        return ketQua;
    }

    protected String phanChungDeGhi() {
        return maSP + "|" + tenSP + "|" + giaNhap + "|" + giaBan + "|" + donViTinh + "|" + thueSuat;
    }

    protected String thongTinChung() {
        return "[" + maSP + "] " + tenSP + " | " + getLoai()
                + " | Giá nhập (tham khảo): " + TienIch.dinhDangTien(giaNhap)
                + " | Giá bán: " + TienIch.dinhDangTien(giaBan) + "/" + donViTinh + " (chưa thuế)"
                + " | Tồn: " + getSoLuongTon() + " (" + cacLo.size() + " lô)"
                + " | Thuế bán: " + thueSuat + "%";
    }

    // Hàm hỗ trợ cho lớp con tính thuế theo mức đã khai báo của từng sản phẩm.
    protected final double tinhThueTheoSuat() {
        return giaBan * thueSuat / 100.0;
    }

    // Mỗi loại sản phẩm tự cài đặt cách tính thuế.
    public abstract double tinhThue();

    // Giá khách phải trả cho 1 đơn vị (đã cộng thuế)
    public double tinhGiaSauThue() {
        return giaBan + tinhThue();
    }

    // Getters and Setters
    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = kiemTraVanBan(tenSP, "Tên sản phẩm");
    }

    public long getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(long giaNhap) {
        this.giaNhap = kiemTraGiaNhap(giaNhap);
    }

    public long getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(long giaBan) {
        this.giaBan = kiemTraGiaBan(giaBan);
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public double getThueSuat() {
        return thueSuat;
    }

    public void setThueSuat(double thueSuat) {
        this.thueSuat = kiemTraThueSuat(thueSuat);
    }

    @Override
    public String toString() {
        return thongTinChung();
    }
}