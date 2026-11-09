// Một dòng của hóa đơn xuất (HDXuat): xuất sản phẩm nào, từ LÔ nào, bao nhiêu, vì LÝ DO gì.
// Hóa đơn xuất dùng để ghi nhận hàng bị loại khỏi kho (hỏng, hết hạn, không đủ điều kiện lưu thông), KHÔNG phải bán cho khách.
// Không có setter: dòng đã tạo thì không sửa được, muốn đổi thì xóa dòng và thêm lại (khi hóa đơn còn Đang xử lý).
// giaVon: giá nhập của lô tại lúc lập dòng, để biết giá trị hàng bị loại.
public class CTHDXuat {

    public enum LyDo {
        HONG("Hỏng"),
        HET_HAN("Hết hạn sử dụng"),
        KHAC("Không đủ điều kiện lưu thông/sử dụng");

        private final String nhan;

        LyDo(String nhan) {
            this.nhan = nhan;
        }

        public String getNhan() {
            return nhan;
        }
    }

    private final String maSP;
    private final String tenSP;
    private final String maLo;
    private final int soLuong;
    private final LyDo lyDo;
    private final long giaVon;
    private String maHD = "";

    public CTHDXuat(String maSP, String tenSP, String maLo, int soLuong, LyDo lyDo, long giaVon) {
        this.maSP = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maSP, "Mã sản phẩm"));
        this.tenSP = SanPham.kiemTraVanBan(tenSP, "Tên sản phẩm");
        this.maLo = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maLo, "Mã lô"));
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng xuất phải lớn hơn 0.");
        }
        if (lyDo == null) {
            throw new IllegalArgumentException("Phải chọn lý do xuất.");
        }
        if (giaVon < 0 || giaVon > SanPham.GIA_TOI_DA) {
            throw new IllegalArgumentException("Giá vốn không hợp lệ.");
        }
        this.soLuong = soLuong;
        this.lyDo = lyDo;
        this.giaVon = giaVon;
    }

    void ganMaHD(String maHD) {
        if (this.maHD.isEmpty()) {
            this.maHD = maHD;
        }
    }

    public String getMaHD() { return maHD; }
    public String getMaSP() { return maSP; }
    public String getTenSP() { return tenSP; }
    public String getMaLo() { return maLo; }
    public int getSoLuong() { return soLuong; }
    public LyDo getLyDo() { return lyDo; }
    public long getGiaVon() { return giaVon; }

    // Giá trị hàng bị loại = số lượng x giá nhập của lô
    public long getGiaTri() {
        return Math.multiplyExact((long) soLuong, giaVon);
    }

    // Dòng ghi file: CX|maSP|tenSP|maLo|soLuong|lyDo|giaVon
    public String toDongFile() {
        return "CX|" + maSP + "|" + tenSP + "|" + maLo + "|" + soLuong + "|" + lyDo.name() + "|" + giaVon;
    }

    public static CTHDXuat tuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);
        if (t.length != 7 || !t[0].equals("CX")) {
            throw new IllegalArgumentException("Dòng chi tiết xuất cần dạng CX|maSP|tenSP|maLo|soLuong|lyDo|giaVon.");
        }
        return new CTHDXuat(t[1], t[2], t[3], Integer.parseInt(t[4]), LyDo.valueOf(t[5]), Long.parseLong(t[6]));
    }

    @Override
    public String toString() {
        return "MaSP: " + maSP + " | " + tenSP + " | Lô: " + maLo + " | SL xuất: " + soLuong
                + " | Lý do: " + lyDo.getNhan() + " | Giá trị: " + SanPham.dinhDangTien(getGiaTri());
    }
}
