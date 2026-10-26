import java.time.LocalDate;
import java.util.Locale;

public abstract class SanPham {

    public static final long GIA_TOI_DA = 1_000_000_000L;

    public static final double THUE_SUAT_TOI_DA = 100.0;

    private final String maSP;
    private String tenSP;
    private long giaBan;
    private int soLuongTon;
    private final String donViTinh;
    private double thueSuat;


    private String donViTinh;
    private double giaNhap;
    private double giaBan;


    // Constructor
    public SanPham(String maSP, String tenSP,
                   double donGia, int soLuongTon) {

        if (maSP == null || maSP.isBlank()) {
            throw new IllegalArgumentException(
                    "Ma san pham khong duoc de trong");
        }

        if (tenSP == null || tenSP.isBlank()) {
            throw new IllegalArgumentException(
                    "Ten san pham khong duoc de trong");
        }

        if (!Double.isFinite(donGia) || donGia < 0) {
            throw new IllegalArgumentException(
                    "Don gia khong hop le");
        }

    protected SanPham(String maSP, String tenSP, long giaBan, int soLuongTon,
                      String donViTinh, double thueSuat) {
        this.maSP = chuanHoaMa(kiemTraVanBan(maSP, "Mã sản phẩm"));
        this.tenSP = kiemTraVanBan(tenSP, "Tên sản phẩm");
        this.giaBan = kiemTraGiaBan(giaBan);

        if (soLuongTon < 0) {
            throw new IllegalArgumentException("Số lượng tồn không được âm.");
        }
        this.soLuongTon = soLuongTon;
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

    protected static long kiemTraGiaBan(long giaBan) {
        if (giaBan <= 0 || giaBan > GIA_TOI_DA) {
            throw new IllegalArgumentException("Giá bán phải lớn hơn 0 và không vượt quá " + GIA_TOI_DA + " đồng.");
        }
        return giaBan;
    }

    protected static double kiemTraThueSuat(double thueSuat) {
        // Viết kiểu này để số NaN cũng bị loại
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

    public abstract boolean isHetHan(LocalDate ngay);

    public abstract String toDongFile();

    protected abstract void capNhatThongTinRieng(SanPham nguon);


    public boolean isHetHan() {
        return isHetHan(LocalDate.now());
    }

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
        this.giaBan = nguon.giaBan;
        this.thueSuat = nguon.thueSuat;
        capNhatThongTinRieng(nguon);
    }

    void tangTon(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng nhập kho phải lớn hơn 0.");
        }
        if (soLuong > Integer.MAX_VALUE - soLuongTon) {
            throw new IllegalStateException("Tồn kho của " + maSP + " vượt quá giới hạn cho phép.");
        }
        soLuongTon += soLuong;
    }

    void giamTon(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng xuất kho phải lớn hơn 0.");
        }
        if (soLuong > soLuongTon) {
            throw new IllegalStateException("Không đủ tồn kho cho " + maSP + ": cần " + soLuong
                    + ", chỉ còn " + soLuongTon + " " + donViTinh + ".");
        }
        soLuongTon -= soLuong;
    }

    public long getGiaTriTonTheoGiaBan() {
        return soLuongTon * giaBan;
    }

    protected String phanChungDeGhi() {
        return maSP + "|" + tenSP + "|" + giaBan + "|" + soLuongTon + "|" + donViTinh + "|" + thueSuat;
    }

    protected String thongTinChung() {
        return "[" + maSP + "] " + tenSP + " | " + getLoai()
                + " | Giá bán: " + TienIch.dinhDangTien(giaBan) + "/" + donViTinh
                + " | Tồn: " + soLuongTon
                + " | Thuế bán: " + thueSuat + "%";
    }



    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = kiemTraVanBan(tenSP, "Tên sản phẩm");
    }

    public long getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(long giaBan) {
        this.giaBan = kiemTraGiaBan(giaBan);
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public double getGiaBan() {
        return giaBan;
    }


    // Setter
    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.isBlank()) {
            throw new IllegalArgumentException(
                    "Ten san pham khong duoc de trong");
        }

        this.tenSP = tenSP.trim();

    public String getDonViTinh() {
        return donViTinh;

    }

    public double getThueSuat() {
        return thueSuat;
    }



    public void setDonViTinh(String donViTinh) {
        if (donViTinh == null || donViTinh.isBlank()) {
            throw new IllegalArgumentException(
                    "Don vi tinh khong hop le");
        }

        this.donViTinh = donViTinh.trim();
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }

    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
    }


    // Hien thi thong tin san pham
    @Override
    public String toString() {
        NumberFormat dinhDangTien =
                NumberFormat.getNumberInstance(
                        Locale.forLanguageTag("vi-VN"));
        dinhDangTien.setMaximumFractionDigits(0);

        return String.format(
                "[%s] %-8s | %-15s | Gia ban: %s VND | Ton: %d",
                getLoai(),
                maSP,
                tenSP,
                dinhDangTien.format(donGia),
                soLuongTon
        );
    }

    public abstract double tinhThue();
    public abstract String layLoaiSP();
    public abstract void xuat();

    public double tinhGiaSauThue() {
        return giaBan + tinhThue();
    }
}


    public void setThueSuat(double thueSuat) {
        this.thueSuat = kiemTraThueSuat(thueSuat);
    }
}
