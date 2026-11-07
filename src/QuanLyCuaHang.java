//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class QuanLyCuaHang extends NhanVien {
//
//    private DSSanPham dsSP = new DSSanPham();
//    private DSNhanVien dsNV = new DSNhanVien();
//    private DsKhachHang dsKH = new DsKhachHang();
//    private DSDoiTac dsDoiTac = new DSDoiTac();
//    private DSKhuyenMai dsKhuyenMai = new DSKhuyenMai();
//    private DSHoadon dsHoaDonBan = new DSHoadon();
//    private DSHoadon dsHoaDonNhap = new DSHoadon();
//
//    public QuanLyCuaHang() {
//        super("Quan ly cua hang");
//    }
//
//    public QuanLyCuaHang(String maNV, String ten, String sdt, String diaChi, String email, LocalDate ngaySinh,
//                         String taiKhoan, String matKhau, double luongCoBan, LocalDate ngayVaoLam, int kinhNghiem) {
//        super(maNV, ten, sdt, diaChi, email, ngaySinh, taiKhoan, matKhau, luongCoBan, ngayVaoLam, kinhNghiem,
//                "Quan ly cua hang");
//    }
//
//    public void ketNoi(DSSanPham dsSP, DSNhanVien dsNV, DsKhachHang dsKH, DSDoiTac dsDoiTac,
//                       DSKhuyenMai dsKhuyenMai, DSHoadon dsHoaDonBan, DSHoadon dsHoaDonNhap) {
//        this.dsSP = dsSP;
//        this.dsNV = dsNV;
//        this.dsKH = dsKH;
//        this.dsDoiTac = dsDoiTac;
//        this.dsKhuyenMai = dsKhuyenMai;
//        this.dsHoaDonBan = dsHoaDonBan;
//        this.dsHoaDonNhap = dsHoaDonNhap;
//    }
//
//    @Override
//    public String getVaiTro() {
//        return "QuanLyCuaHang";
//    }
//
//    public boolean themNhanVien(NhanVien nv) {
//        return dsNV.them(nv);
//    }
//
//    public boolean xoaNhanVien(String ma) {
//        if (ma.equalsIgnoreCase(getMa())) {
//            return false;
//        }
//        return dsNV.xoa(ma);
//    }
//
//    public boolean suaNhanVien(String ma, String ten, String sdt, double luongCoBan, int kinhNghiem) {
//        return dsNV.sua(ma, ten, sdt, luongCoBan, kinhNghiem);
//    }
//
//    public boolean themDoiTac(DoiTac dt) {
//        return dsDoiTac.them(dt);
//    }
//
//    public boolean xoaDoiTac(String ma) {
//        return dsDoiTac.xoa(ma);
//    }
//
//    public boolean themKhuyenMai(Khuyenmai km) {
//        return dsKhuyenMai.them(km);
//    }
//
//    public boolean xoaKhuyenMai(String ma) {
//        return dsKhuyenMai.xoa(ma);
//    }
//
//    public HoadonNhap nhapThemHang(NhaCungCap ncc, String maSP, int soLuong, double giaNhap) {
//        SanPham sp = dsSP.timTheoMa(maSP);
//        if (ncc == null || sp == null || soLuong <= 0 || giaNhap < 0) {
//            return null;
//        }
//        HoadonNhap hd = new HoadonNhap(Ktradulieu.taoMa("HN%03d", dsHoaDonNhap.size() + 1), getMa(),
//                ncc.getMaDT(), "", 0);
//        hd.themChiTiet(sp, soLuong, giaNhap);
//        if (!ncc.kiemTraHanMucNoCong(hd.tongTienDS())) {
//            return null;
//        }
//        hd.capNhatKho(dsSP);
//        ncc.ghiNo(hd.tongTienDS());
//        dsHoaDonNhap.them(hd);
//        return hd;
//    }
//
//    public String themSPTrungBay(String maSP, int soLuong) {
//        SanPham sp = dsSP.timTheoMa(maSP);
//        if (sp == null) {
//            return "Khong tim thay san pham.";
//        }
//        if (sp.isHetHan()) {
//            return "San pham da het han su dung, khong the trung bay.";
//        }
//        if (soLuong <= 0 || soLuong > sp.getSlTonKho()) {
//            return "So luong khong hop le (trong kho con " + sp.getSlTonKho() + ").";
//        }
//        sp.setSlTonKho(sp.getSlTonKho() - soLuong);
//        sp.setSlTrungBay(sp.getSlTrungBay() + soLuong);
//        return null;
//    }
//
//    public String xoaSPTrungBay(String maSP, int soLuong) {
//        SanPham sp = dsSP.timTheoMa(maSP);
//        if (sp == null) {
//            return "Khong tim thay san pham.";
//        }
//        int sl = (soLuong == 0) ? sp.getSlTrungBay() : soLuong;
//        if (sl <= 0 || sl > sp.getSlTrungBay()) {
//            return "So luong khong hop le (dang trung bay " + sp.getSlTrungBay() + ").";
//        }
//        sp.setSlTrungBay(sp.getSlTrungBay() - sl);
//        sp.setSlTonKho(sp.getSlTonKho() + sl);
//        return null;
//    }
//
//    private int[] demSoLuongBan() {
//        int[] ban = new int[dsSP.size()];
//        for (Hoadonban hd : dsHoaDonBan.layHoaDonBan()) {
//            for (ChiTietHoadonban ct : hd.getChiTiet()) {
//                for (int i = 0; i < dsSP.size(); i++) {
//                    if (dsSP.get(i).getMaSP().equalsIgnoreCase(ct.getMaSP())) {
//                        ban[i] += ct.getSoLuong();
//                    }
//                }
//            }
//        }
//        return ban;
//    }
//
//    private Integer[] sapXepTheoSoLuongBan(final int[] ban, final boolean giamDan) {
//        Integer[] idx = new Integer[ban.length];
//        for (int i = 0; i < idx.length; i++) {
//            idx[i] = i;
//        }
//        Arrays.sort(idx, (a, b) -> giamDan ? Integer.compare(ban[b], ban[a]) : Integer.compare(ban[a], ban[b]));
//        return idx;
//    }
//
//    public void phanTichSPBanChay(int top) {
//        int[] ban = demSoLuongBan();
//        Integer[] idx = sapXepTheoSoLuongBan(ban, true);
//        System.out.println("--- TOP " + top + " SAN PHAM BAN CHAY ---");
//        int dem = 0;
//        for (int i : idx) {
//            if (dem == top || ban[i] == 0) {
//                break;
//            }
//            SanPham sp = dsSP.get(i);
//            System.out.printf("%d. %-6s %-24s da ban %d%n", ++dem, sp.getMaSP(), sp.getTenSP(), ban[i]);
//        }
//        if (dem == 0) {
//            System.out.println("(Chua ban duoc san pham nao)");
//        }
//    }
//
//    public void phanTichSPBanE(int top) {
//        int[] ban = demSoLuongBan();
//        Integer[] idx = sapXepTheoSoLuongBan(ban, false);
//        System.out.println("--- TOP " + top + " SAN PHAM BAN E ---");
//        int dem = 0;
//        for (int i : idx) {
//            if (dem == top) {
//                break;
//            }
//            SanPham sp = dsSP.get(i);
//            System.out.printf("%d. %-6s %-24s da ban %d (kho %d, trung bay %d)%n",
//                    ++dem, sp.getMaSP(), sp.getTenSP(), ban[i], sp.getSlTonKho(), sp.getSlTrungBay());
//        }
//        if (dem == 0) {
//            System.out.println("(Chua co san pham nao)");
//        }
//    }
//
//    public DSSanPham getDsSP() {
//        return dsSP;
//    }
//
//    public DSNhanVien getDsNV() {
//        return dsNV;
//    }
//
//    public DsKhachHang getDsKH() {
//        return dsKH;
//    }
//
//    public DSDoiTac getDsDoiTac() {
//        return dsDoiTac;
//    }
//
//    public DSKhuyenMai getDsKhuyenMai() {
//        return dsKhuyenMai;
//    }
//
//    public ArrayList<Hoadonban> getHoaDonBan() {
//        return dsHoaDonBan.layHoaDonBan();
//    }
//}
