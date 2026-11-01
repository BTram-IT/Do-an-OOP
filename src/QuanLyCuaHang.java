import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class QuanLyCuaHang extends NhanVien {

    private DSSanPham dsSP = new DSSanPham();
    private DSNhanVien dsNV = new DSNhanVien();
    private DSKhachHang dsKH = new DSKhachHang();
    private DSDoiTac dsDoiTac = new DSDoiTac();
    private DSKhuyenMai dsKhuyenMai = new DSKhuyenMai();
    private DSHoaDon dsHoaDonBan = new DSHoaDon();
    private DSHoaDon dsHoaDonNhap = new DSHoaDon();

    public QuanLyCuaHang() {
        super("Quan ly cua hang");
    }

    public QuanLyCuaHang(String maNV, String ten, String sdt, String diaChi, String email, LocalDate ngaySinh,
                         String taiKhoan, String matKhau, double luongCoBan, LocalDate ngayVaoLam, int kinhNghiem) {
        super(maNV, ten, sdt, diaChi, email, ngaySinh, taiKhoan, matKhau, luongCoBan, ngayVaoLam, kinhNghiem,
                "Quan ly cua hang");
    }

    /** Gan cac danh sach dung chung cua cua hang cho quan ly. */
    public void ketNoi(DSSanPham dsSP, DSNhanVien dsNV, DSKhachHang dsKH, DSDoiTac dsDoiTac,
                       DSKhuyenMai dsKhuyenMai, DSHoaDon dsHoaDonBan, DSHoaDon dsHoaDonNhap) {
        this.dsSP = dsSP;
        this.dsNV = dsNV;
        this.dsKH = dsKH;
        this.dsDoiTac = dsDoiTac;
        this.dsKhuyenMai = dsKhuyenMai;
        this.dsHoaDonBan = dsHoaDonBan;
        this.dsHoaDonNhap = dsHoaDonNhap;
    }

    @Override
    public String getVaiTro() {
        return "QuanLyCuaHang";
    }

    // ----- Nhan vien -----
    public boolean themNhanVien(NhanVien nv) {
        return dsNV.them(nv);
    }

    /** Khong cho xoa chinh minh. */
    public boolean xoaNhanVien(String ma) {
        if (ma.equalsIgnoreCase(getMa())) {
            return false;
        }
        return dsNV.xoa(ma);
    }

    public boolean suaNhanVien(String ma, String ten, String sdt, double luongCoBan, int kinhNghiem) {
        return dsNV.sua(ma, ten, sdt, luongCoBan, kinhNghiem);
    }

    // ----- Doi tac / khuyen mai -----
    public boolean themDoiTac(DoiTac dt) {
        return dsDoiTac.them(dt);
    }

    public boolean xoaDoiTac(String ma) {
        return dsDoiTac.xoa(ma);
    }

    public boolean themKhuyenMai(KhuyenMai km) {
        return dsKhuyenMai.them(km);
    }

    public boolean xoaKhuyenMai(String ma) {
        return dsKhuyenMai.xoa(ma);
    }

    // ----- Nhap them hang -----
    /**
     * Nhap them hang cho mot san pham tu nha cung cap (nha cung cap tu giao, khong mat phi van chuyen):
     * lap hoa don nhap, cong ton kho va ghi no nha cung cap.
     * @return hoa don nhap, hoac null neu khong nhap duoc (sai san pham, vuot han muc no cong...).
     */
    public HoaDonNhap nhapThemHang(NhaCungCap ncc, String maSP, int soLuong, double giaNhap) {
        SanPham sp = dsSP.timTheoMa(maSP);
        if (ncc == null || sp == null || soLuong <= 0 || giaNhap < 0) {
            return null;
        }
        HoaDonNhap hd = new HoaDonNhap(Ktradulieu.taoMa("HN%03d", dsHoaDonNhap.size() + 1), getMa(),
                ncc.getMaDT(), "", 0);
        hd.themChiTiet(sp, soLuong, giaNhap);
        if (!ncc.kiemTraHanMucNoCong(hd.tongTienDS())) {
            return null;
        }
        hd.capNhatKho(dsSP);
        ncc.ghiNo(hd.tongTienDS());
        dsHoaDonNhap.them(hd);
        return hd;
    }

    // ----- Trung bay -----
    /**
     * Chuyen soLuong san pham tu kho ra quay trung bay.
     * @return null neu thanh cong, nguoc lai la ly do that bai.
     */
    public String themSPTrungBay(String maSP, int soLuong) {
        SanPham sp = dsSP.timTheoMa(maSP);
        if (sp == null) {
            return "Khong tim thay san pham.";
        }
        if (sp.isHetHan()) {
            return "San pham da het han su dung, khong the trung bay.";
        }
        if (soLuong <= 0 || soLuong > sp.getSlTonKho()) {
            return "So luong khong hop le (trong kho con " + sp.getSlTonKho() + ").";
        }
        sp.setSlTonKho(sp.getSlTonKho() - soLuong);
        sp.setSlTrungBay(sp.getSlTrungBay() + soLuong);
        return null;
    }

    /**
     * Thu hoi soLuong san pham tu quay trung bay ve kho (soLuong = 0: thu hoi het).
     * @return null neu thanh cong, nguoc lai la ly do that bai.
     */
    public String xoaSPTrungBay(String maSP, int soLuong) {
        SanPham sp = dsSP.timTheoMa(maSP);
        if (sp == null) {
            return "Khong tim thay san pham.";
        }
        int sl = (soLuong == 0) ? sp.getSlTrungBay() : soLuong;
        if (sl <= 0 || sl > sp.getSlTrungBay()) {
            return "So luong khong hop le (dang trung bay " + sp.getSlTrungBay() + ").";
        }
        sp.setSlTrungBay(sp.getSlTrungBay() - sl);
        sp.setSlTonKho(sp.getSlTonKho() + sl);
        return null;
    }

    // ----- Phan tich san pham ban chay / ban e -----
    /** So luong ban duoc cua tung san pham trong dsSP (cung thu tu) tu cac hoa don ban. */
    private int[] demSoLuongBan() {
        int[] ban = new int[dsSP.size()];
        for (HoaDonBan hd : dsHoaDonBan.layHoaDonBan()) {
            for (ChiTietHoaDonBan ct : hd.getChiTiet()) {
                for (int i = 0; i < dsSP.size(); i++) {
                    if (dsSP.get(i).getMaSP().equalsIgnoreCase(ct.getMaSP())) {
                        ban[i] += ct.getSoLuong();
                    }
                }
            }
        }
        return ban;
    }

    private Integer[] sapXepTheoSoLuongBan(final int[] ban, final boolean giamDan) {
        Integer[] idx = new Integer[ban.length];
        for (int i = 0; i < idx.length; i++) {
            idx[i] = i;
        }
        Arrays.sort(idx, (a, b) -> giamDan ? Integer.compare(ban[b], ban[a]) : Integer.compare(ban[a], ban[b]));
        return idx;
    }

    /** In top san pham ban chay nhat (chi nhung san pham da ban duoc). */
    public void phanTichSPBanChay(int top) {
        int[] ban = demSoLuongBan();
        Integer[] idx = sapXepTheoSoLuongBan(ban, true);
        System.out.println("--- TOP " + top + " SAN PHAM BAN CHAY ---");
        int dem = 0;
        for (int i : idx) {
            if (dem == top || ban[i] == 0) {
                break;
            }
            SanPham sp = dsSP.get(i);
            System.out.printf("%d. %-6s %-24s da ban %d%n", ++dem, sp.getMaSP(), sp.getTenSP(), ban[i]);
        }
        if (dem == 0) {
            System.out.println("(Chua ban duoc san pham nao)");
        }
    }

    /** In top san pham ban e nhat (ban it nhat, bao gom san pham chua ban duoc cai nao). */
    public void phanTichSPBanE(int top) {
        int[] ban = demSoLuongBan();
        Integer[] idx = sapXepTheoSoLuongBan(ban, false);
        System.out.println("--- TOP " + top + " SAN PHAM BAN E ---");
        int dem = 0;
        for (int i : idx) {
            if (dem == top) {
                break;
            }
            SanPham sp = dsSP.get(i);
            System.out.printf("%d. %-6s %-24s da ban %d (kho %d, trung bay %d)%n",
                    ++dem, sp.getMaSP(), sp.getTenSP(), ban[i], sp.getSlTonKho(), sp.getSlTrungBay());
        }
        if (dem == 0) {
            System.out.println("(Chua co san pham nao)");
        }
    }

    public DSSanPham getDsSP() {
        return dsSP;
    }

    public DSNhanVien getDsNV() {
        return dsNV;
    }

    public DSKhachHang getDsKH() {
        return dsKH;
    }

    public DSDoiTac getDsDoiTac() {
        return dsDoiTac;
    }

    public DSKhuyenMai getDsKhuyenMai() {
        return dsKhuyenMai;
    }

    public ArrayList<HoaDonBan> getHoaDonBan() {
        return dsHoaDonBan.layHoaDonBan();
    }
}
