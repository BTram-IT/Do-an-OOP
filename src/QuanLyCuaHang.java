import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class QuanLyCuaHang extends NhanVien {

    private DSSanPham dsSP = new DSSanPham();
    private DSNhanVien dsNV = new DSNhanVien();
    private DsKhachHang dsKH = new DsKhachHang();
    private DSDoiTac dsDoiTac = new DSDoiTac();
    private DSKhuyenMai dsKhuyenMai = new DSKhuyenMai();
    private DSHoadon dsHoaDonBan = new DSHoadon();
    private DSHoadon dsHoaDonNhap = new DSHoadon();

    public QuanLyCuaHang(DsKhachHang dsKH, DSSanPham dsSP, DSNhanVien dsNV, DSDoiTac dsDoiTac, DSKhuyenMai dsKhuyenMai, DSHoadon dsHoaDonBan, DSHoadon dsHoaDonNhap) {
        this.dsKH = dsKH;
        this.dsSP = dsSP;
        this.dsNV = dsNV;
        this.dsDoiTac = dsDoiTac;
        this.dsKhuyenMai = dsKhuyenMai;
        this.dsHoaDonBan = dsHoaDonBan;
        this.dsHoaDonNhap = dsHoaDonNhap;
    }

    public QuanLyCuaHang() {
        super();
    }

    public QuanLyCuaHang(String ten, String sdt, String diaChi, String email, LocalDate ngaySinh, String maNV, double luongCoBan, LocalDate ngayVaoLam, String chucVu, DsKhachHang dsKH, DSSanPham dsSP, DSNhanVien dsNV, DSDoiTac dsDoiTac, DSKhuyenMai dsKhuyenMai, DSHoadon dsHoaDonBan, DSHoadon dsHoaDonNhap) {
        super(ten, sdt, diaChi, email, ngaySinh, maNV, luongCoBan, ngayVaoLam, chucVu);
        this.dsKH = dsKH;
        this.dsSP = dsSP;
        this.dsNV = dsNV;
        this.dsDoiTac = dsDoiTac;
        this.dsKhuyenMai = dsKhuyenMai;
        this.dsHoaDonBan = dsHoaDonBan;
        this.dsHoaDonNhap = dsHoaDonNhap;
    }

    public void ketNoi(DSSanPham dsSP, DSNhanVien dsNV, DsKhachHang dsKH, DSDoiTac dsDoiTac,
                       DSKhuyenMai dsKhuyenMai, DSHoadon dsHoaDonBan, DSHoadon dsHoaDonNhap) {
        if (dsSP != null) this.dsSP = dsSP;
        if (dsNV != null) this.dsNV = dsNV;
        if (dsKH != null) this.dsKH = dsKH;
        if (dsDoiTac != null) this.dsDoiTac = dsDoiTac;
        if (dsKhuyenMai != null) this.dsKhuyenMai = dsKhuyenMai;
        if (dsHoaDonBan != null) this.dsHoaDonBan = dsHoaDonBan;
        if (dsHoaDonNhap != null) this.dsHoaDonNhap = dsHoaDonNhap;
    }

    @Override
    public String getVaiTro() {
        return "QuanLyCuaHang";
    }

    public boolean themNhanVien(NhanVien nv) {
        dsNV.them(nv);
        return true;
    }

    public boolean xoaNhanVien(String ma) {
        if (ma.equalsIgnoreCase(getMaNV())) return false;
        return dsNV.xoa(ma);
    }

    public boolean themDoiTac(DoiTac dt) {
        dsDoiTac.them(dt);
        return true;
    }

    public boolean xoaDoiTac(String ma) {
        return dsDoiTac.xoa(ma);
    }

    public boolean themKhuyenMai(Khuyenmai km) {
        dsKhuyenMai.themKhuyenMai(km);
        return true;
    }

    public boolean xoaKhuyenMai(String ma) {
        return dsKhuyenMai.xoaKhuyenMai();
    }

    public HoadonNhap nhapThemHang(NhaCungCap ncc, String maSP, int soLuong, double giaNhap) {
        SanPham sp = dsSP.timTheoMa(maSP);
        if (ncc == null || sp == null || soLuong <= 0 || giaNhap < 0) return null;
        HoadonNhap hd = new HoadonNhap(Ktradulieu.taoMa("HN%03d", dsHoaDonNhap.size() + 1),getMaNV(),
                LocalDate.now(), ncc.getMaDT());
        hd.themChiTiet(sp, soLuong, giaNhap);
        if (!ncc.kiemTraHanMucNoCong(hd.tongTienDS())) {
            System.out.println("Vượt hạn mức nợ công của nhà cung cấp!");
            return null;
        }
        hd.capNhatKho(dsSP);
        ncc.ghiNo(hd.tongTienDS());
        dsHoaDonNhap.them(hd);
        return hd;
    }

    private int[] demSoLuongBan() {
        int[] ban = new int[dsSP.size()];
        for (Hoadonban hd : dsHoaDonBan.layHoaDonBan()) {
            if (hd.getCTHoaDon() == null) continue;
            for (CTHoaDon gh : hd.getCTHoaDon()) {
                for (int i = 0; i < dsSP.size(); i++) {
                    if (dsSP.get(i).getMaSP().equalsIgnoreCase(gh.getMaSP())) {
                        ban[i] += gh.getSoLuong();
                    }
                }
            }
        }
        return ban;
    }

    public void phanTichSPBanChay(int top) {
        int[] ban = demSoLuongBan();
        Integer[] idx = new Integer[ban.length];
        for (int i = 0; i < idx.length; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Integer.compare(ban[b], ban[a]));

        System.out.println("--- TOP " + top + " SẢN PHẨM BÁN CHẠY ---");
        int dem = 0;
        for (int i : idx) {
            if (dem == top || ban[i] == 0) break;
            SanPham sp = dsSP.get(i);
            System.out.printf("%d. %-6s %-25s Đã bán: %d%n", ++dem, sp.getMaSP(), sp.getTenSP(), ban[i]);
        }
        if (dem == 0) System.out.println("(Chưa bán được sản phẩm nào)");
    }

    public DSSanPham getDsSP() { return dsSP; }
    public DSNhanVien getDsNV() { return dsNV; }
    public DsKhachHang getDsKH() { return dsKH; }
    public DSDoiTac getDsDoiTac() { return dsDoiTac; }
    public DSKhuyenMai getDsKhuyenMai() { return dsKhuyenMai; }
    public ArrayList<Hoadonban> getHoaDonBan() { return dsHoaDonBan.layHoaDonBan(); }

    public void menuQuanLySanPham() {}
    public void menuQuanLyNhanVien() {}
    public void menuQuanLyKhachHang() {}
    public void menuQuanLyDoiTac() {}
    public void menuQuanLyKhuyenMai() {}
    public void menuQuanLySPKhuyenMai() {}
    public void menuQuanLyHoaDon() {}
    public void menuThongKeKinhDoanh() {}

}