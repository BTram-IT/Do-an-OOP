import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class QuanlyKho {

    public static final int NGUONG_SAP_HET_HANG = 10;
    public static final int SO_NGAY_CANH_BAO_HET_HAN = 7;
    public static final String THU_MUC_MAC_DINH = "du_lieu_kho";
    public static final String TEN_FILE_SAN_PHAM = "san_pham.txt";
    public static final String TEN_FILE_HOA_DON_NHAP = "hoa_don_nhap.txt";
    public static final String TEN_FILE_XUAT_KHO = "xuat_kho.txt";

    private DSSanPham dsSanPham;
    private ArrayList<HoadonNhap> dsHoaDonNhap = new ArrayList<>();
    private ArrayList<GiaoDichXuat> dsGiaoDichXuat = new ArrayList<>();
    private DSNhaCungCap dsNhaCungCap;

    public QuanlyKho(DSNhaCungCap dsNhaCungCap) {
        this(new DSSanPham(), dsNhaCungCap);
    }

    public QuanlyKho(DSSanPham dsSanPham, DSNhaCungCap dsNhaCungCap) {
        if (dsSanPham == null) throw new IllegalArgumentException("Danh sách sản phẩm không được null.");
        if (dsNhaCungCap == null) throw new IllegalArgumentException("Danh sách nhà cung cấp không được null.");
        this.dsSanPham = dsSanPham;
        this.dsNhaCungCap = dsNhaCungCap;
    }

    public NhaCungCap layNhaCungCap(String ma) {
        return dsNhaCungCap.timTheoMa(ma);
    }

    public boolean coNhaCungCap(String ma) {
        return dsNhaCungCap.tonTai(ma);
    }

    public String nhanNhaCungCap(String ma) {
        NhaCungCap ncc = dsNhaCungCap.timTheoMa(ma);
        return ncc == null ? ma : ncc.getMaDT() + " - " + ncc.getTenDT();
    }

        public void themSanPham(SanPham sp) { dsSanPham.them(sp); }
        public int themNhieuSanPham(SanPham[] dsMoi) { return dsSanPham.themNhieu(dsMoi); }
        public void suaSanPham(String ma, SanPham thongTinMoi) { dsSanPham.sua(ma, thongTinMoi); }

        public void xoaSanPham(String ma) {
            String maChuan = SanPham.chuanHoaMa(ma);
            for (HoadonNhap phieu : dsHoaDonNhap) {
                if (phieu.coSanPham(maChuan)) {
                    throw new IllegalStateException("Không thể xóa " + maChuan + " vì đã có trong phiếu nhập " + phieu.getMaHoaDon());
                }
            }
            dsSanPham.xoaTheoMa(maChuan);
        }

    public SanPham timSanPham(String ma) { return dsSanPham.timTheoMa(ma); }
    public boolean tonTaiSanPham(String ma) { return dsSanPham.tonTaiMa(ma); }
    public ArrayList<SanPham> timTheoTenGanDung(String tuKhoa) { return dsSanPham.timTheoTenGanDung(tuKhoa); }
    public ArrayList<SanPham> timTheoLoai(String loai) { return dsSanPham.timTheoLoai(loai); }
    public ArrayList<SanPham> timTheoKhoangGia(long giaTu, long giaDen) { return dsSanPham.timTheoKhoangGia(giaTu, giaDen); }
    public int soSanPham() { return dsSanPham.soLuong(); }
    public void xuatDanhSachSanPham() { dsSanPham.xuat(); }

    public ArrayList<LoHang> layCacLo(String maSP) {
        SanPham sp = dsSanPham.timTheoMa(maSP);
        if (sp == null) throw new IllegalArgumentException("Không tìm thấy sản phẩm: " + maSP);
        return sp.getCacLo();
    }

    public String moTaCacLo(String maSP) {
        SanPham sp = dsSanPham.timTheoMa(maSP);
        if (sp == null) throw new IllegalArgumentException("Không tìm thấy sản phẩm: " + maSP);
        return sp.moTaCacLo();
    }

    public ArrayList<LoHang> layLoHetHan(LocalDate ngay) { return dsSanPham.layLoHetHan(ngay); }
    public ArrayList<LoHang> layLoSapHetHan(LocalDate ngay, int soNgay) { return dsSanPham.layLoSapHetHan(ngay, soNgay); }

    public int kiemTraTon(String maSP) { return dsSanPham.layTon(maSP); }
    public int kiemTraTonBanDuoc(String maSP) { return dsSanPham.layTonBanDuoc(maSP, LocalDate.now()); }
    public boolean duTon(String maSP, int soLuong) { return dsSanPham.duTon(maSP, soLuong); }

    public ArrayList<LoXuat> xuatKho(String maSP, int soLuong) {
        return dsSanPham.xuatFEFO(maSP, soLuong, LocalDate.now());
    }

    public ArrayList<LoXuat> xuatKho(String maSP, int soLuong, LocalDate ngay) {
        return dsSanPham.xuatFEFO(maSP, soLuong, ngay);
    }

    public ArrayList<LoXuat> xuatKhoNhieu(String[] maSP, int[] soLuong) {
        return xuatKhoNhieu(maSP, soLuong, LocalDate.now());
    }

    public ArrayList<LoXuat> xuatKhoNhieu(String[] maSP, int[] soLuong, LocalDate ngay) {
        if (maSP == null || soLuong == null || maSP.length == 0 || maSP.length != soLuong.length) {
            throw new IllegalArgumentException("Đơn xuất kho không hợp lệ: cần hai mảng cùng độ dài và không rỗng.");
        }
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày xuất kho không được null.");
        }
        for (int i = 0; i < maSP.length; i++) {
            if (maSP[i] == null || soLuong[i] <= 0) {
                throw new IllegalArgumentException("Dòng " + (i + 1) + " của đơn xuất kho có mã rỗng hoặc số lượng không dương.");
            }
        }
        for (int i = 0; i < maSP.length; i++) {
            if (daGapMaTruocDo(maSP, i)) continue;
            long tong = tongSoLuongCuaMa(maSP, soLuong, i);
            if (tong > Integer.MAX_VALUE) throw new IllegalArgumentException("Số lượng xuất của " + maSP[i] + " quá lớn.");
            if (!dsSanPham.duTon(maSP[i], (int) tong, ngay)) {
                throw new IllegalStateException("Không đủ hàng còn hạn cho " + SanPham.chuanHoaMa(maSP[i]) + ": cần "
                        + tong + ", còn hạn " + dsSanPham.layTonBanDuoc(maSP[i], ngay) + ".");
            }
        }
        ArrayList<LoXuat> ketQua = new ArrayList<>();
        for (int i = 0; i < maSP.length; i++) {
            if (daGapMaTruocDo(maSP, i)) continue;
            ketQua.addAll(dsSanPham.xuatFEFO(maSP[i], (int) tongSoLuongCuaMa(maSP, soLuong, i), ngay));
        }
        return ketQua;
    }

    private boolean daGapMaTruocDo(String[] maSP, int viTri) {
        for (int j = 0; j < viTri; j++) {
            if (SanPham.chuanHoaMa(maSP[j]).equals(SanPham.chuanHoaMa(maSP[viTri]))) return true;
        }
        return false;
    }

    private long tongSoLuongCuaMa(String[] maSP, int[] soLuong, int viTri) {
        long tong = 0;
        for (int j = 0; j < maSP.length; j++) {
            if (SanPham.chuanHoaMa(maSP[j]).equals(SanPham.chuanHoaMa(maSP[viTri]))) tong += soLuong[j];
        }
        return tong;
    }

    public void hoanKho(String maSP, String maLo, int soLuong) {
        dsSanPham.hoanLo(maSP, maLo, soLuong);
    }

    public void hoanKho(ArrayList<LoXuat> cacLoDaXuat) {
        if (cacLoDaXuat == null || cacLoDaXuat.isEmpty()) {
            throw new IllegalArgumentException("Danh sách cần hoàn không được rỗng.");
        }
        for (int i = 0; i < cacLoDaXuat.size(); i++) {
            if (daGapLoTruocDo(cacLoDaXuat, i)) continue;
            LoXuat x = cacLoDaXuat.get(i);
            SanPham sp = dsSanPham.timTheoMa(x.getMaSP());
            if (sp == null) throw new IllegalArgumentException("Không tìm thấy sản phẩm: " + x.getMaSP());
            sp.kiemTraCoTheHoanVaoLo(x.getMaLo(), tongSoLuongCuaLo(cacLoDaXuat, i));
        }
        for (int i = 0; i < cacLoDaXuat.size(); i++) {
            if (daGapLoTruocDo(cacLoDaXuat, i)) continue;
            LoXuat x = cacLoDaXuat.get(i);
            dsSanPham.hoanLo(x.getMaSP(), x.getMaLo(), tongSoLuongCuaLo(cacLoDaXuat, i));
        }
    }

    private boolean daGapLoTruocDo(ArrayList<LoXuat> ds, int viTri) {
        for (int j = 0; j < viTri; j++) {
            if (ds.get(j).getMaSP().equals(ds.get(viTri).getMaSP())
                    && ds.get(j).getMaLo().equals(ds.get(viTri).getMaLo())) return true;
        }
        return false;
    }

    private int tongSoLuongCuaLo(ArrayList<LoXuat> ds, int viTri) {
        long tong = 0;
        for (LoXuat x : ds) {
            if (x.getMaSP().equals(ds.get(viTri).getMaSP()) && x.getMaLo().equals(ds.get(viTri).getMaLo())) tong += x.getSoLuong();
        }
        if (tong > Integer.MAX_VALUE) throw new IllegalArgumentException("Số lượng hoàn quá lớn.");
        return (int) tong;
    }

    // ===================== XUẤT / HOÀN THEO GIAO DỊCH =====================
    private GiaoDichXuat timGiaoDich(String maGiaoDich) {
        String maChuan = SanPham.chuanHoaMa(maGiaoDich);
        for (GiaoDichXuat gd : dsGiaoDichXuat) {
            if (gd.getMaGiaoDich().equals(maChuan)) return gd;
        }
        return null;
    }

    private GiaoDichXuat layGiaoDich(String maGiaoDich) {
        GiaoDichXuat gd = timGiaoDich(maGiaoDich);
        if (gd == null) throw new IllegalArgumentException("Không có giao dịch xuất kho mã: " + maGiaoDich);
        return gd;
    }

    public boolean coGiaoDichXuat(String maGiaoDich) {
        return timGiaoDich(maGiaoDich) != null;
    }

    public ArrayList<LoXuat> xuatKhoChoGiaoDich(String maGiaoDich, String[] maSP, int[] soLuong) {
        return xuatKhoChoGiaoDich(maGiaoDich, maSP, soLuong, LocalDate.now());
    }

    public ArrayList<LoXuat> xuatKhoChoGiaoDich(String maGiaoDich, String[] maSP, int[] soLuong, LocalDate ngay) {
        GiaoDichXuat gd = new GiaoDichXuat(maGiaoDich);
        if (timGiaoDich(gd.getMaGiaoDich()) != null) throw new IllegalArgumentException("Giao dịch " + gd.getMaGiaoDich() + " đã tồn tại.");
        ArrayList<LoXuat> daLay = xuatKhoNhieu(maSP, soLuong, ngay);
        for (LoXuat x : daLay) gd.ghiNhanXuat(x);
        dsGiaoDichXuat.add(gd);
        return daLay;
    }

    public ArrayList<LoXuat> layLoDaXuatCuaGiaoDich(String maGiaoDich) {
        GiaoDichXuat gd = layGiaoDich(maGiaoDich);
        ArrayList<LoXuat> ketQua = new ArrayList<>();
        for (LoXuat x : gd.getCacLoDaXuat()) {
            int rong = gd.soXuatRong(x.getMaSP(), x.getMaLo());
            if (rong > 0) ketQua.add(new LoXuat(x.getMaSP(), x.getMaLo(), rong));
        }
        return ketQua;
    }

    public String moTaGiaoDichXuat(String maGiaoDich) {
        return layGiaoDich(maGiaoDich).toString();
    }

    public ArrayList<LoXuat> hoanKhoTheoGiaoDich(String maGiaoDich) {
        GiaoDichXuat gd = layGiaoDich(maGiaoDich);
        ArrayList<LoXuat> conLai = gd.getCacLoConHoanDuoc();
        if (conLai.isEmpty()) throw new IllegalStateException("Giao dịch " + gd.getMaGiaoDich() + " đã hoàn hết.");
        hoanKho(conLai);
        for (LoXuat x : conLai) gd.ghiNhanHoan(x.getMaSP(), x.getMaLo(), x.getSoLuong());
        return conLai;
    }

    public void hoanKhoTheoGiaoDich(String maGiaoDich, String maSP, String maLo, int soLuong) {
        GiaoDichXuat gd = layGiaoDich(maGiaoDich);
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng hoàn phải lớn hơn 0.");
        if (soLuong > gd.soConHoanDuoc(maSP, maLo)) {
            throw new IllegalStateException("Giao dịch " + gd.getMaGiaoDich() + " chỉ còn hoàn được "
                    + gd.soConHoanDuoc(maSP, maLo) + " của lô " + maLo + " (" + maSP + ").");
        }
        hoanKho(maSP, maLo, soLuong);
        gd.ghiNhanHoan(maSP, maLo, soLuong);
    }

    public ArrayList<LoXuat> hoanKhoTheoGiaoDich(String maGiaoDich, String maSP, int soLuong) {
        GiaoDichXuat gd = layGiaoDich(maGiaoDich);
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng hoàn phải lớn hơn 0.");
        String sp = SanPham.chuanHoaMa(maSP);
        ArrayList<LoXuat> keHoach = new ArrayList<>();
        int canHoan = soLuong;
        for (LoXuat x : gd.getCacLoConHoanDuoc()) {
            if (canHoan > 0 && x.getMaSP().equals(sp)) {
                int hoan = Math.min(canHoan, x.getSoLuong());
                keHoach.add(new LoXuat(sp, x.getMaLo(), hoan));
                canHoan -= hoan;
            }
        }
        if (canHoan > 0) throw new IllegalStateException("Giao dịch " + gd.getMaGiaoDich() + " không đủ hàng để hoàn.");
        hoanKho(keHoach);
        for (LoXuat x : keHoach) gd.ghiNhanHoan(x.getMaSP(), x.getMaLo(), x.getSoLuong());
        return keHoach;
    }

    public HoadonNhap taoHoaDonNhap(String maNhanVien, String maNhaCungCap, LocalDate ngayNhap) {
        NhaCungCap ncc = dsNhaCungCap.timTheoMa(maNhaCungCap);
        if (ncc == null) throw new IllegalArgumentException("Nhà cung cấp không tồn tại: " + maNhaCungCap);
        HoadonNhap phieu = new HoadonNhap(HoadonNhap.maKeTiep(), maNhanVien, ngayNhap, ncc.getMaDT());
        dsHoaDonNhap.add(phieu);
        return phieu;
    }

    public void themChiTietPhieu(String maPhieu, String maSP, int soLuong, Long giaNhap,
                                 double thueSuatNhap, LocalDate hanSuDung) {
        if (giaNhap == null) {
            SanPham spMacDinh = dsSanPham.timTheoMa(maSP);
            if (spMacDinh != null && spMacDinh.getGiaNhap() == 0) {
                throw new IllegalArgumentException("Sản phẩm " + maSP + " chưa có giá nhập tham khảo.");
            }
            giaNhap = spMacDinh == null ? 0L : spMacDinh.getGiaNhap();
        }
        themChiTietPhieu(maPhieu, maSP, soLuong, giaNhap.longValue(), thueSuatNhap, hanSuDung);
    }

    public void themChiTietPhieu(String maPhieu, String maSP, int soLuong, long giaNhap,
                                 double thueSuatNhap, LocalDate hanSuDung) {
        HoadonNhap phieu = layPhieu(maPhieu);
        SanPham sp = dsSanPham.layBatBuoc(maSP);
        sp.kiemTraCoTheNhapLo(hanSuDung, soLuong);
        phieu.themChiTiet(new CTHDonNhap(sp.getMaSP(), sp.getTenSP(), soLuong, giaNhap, thueSuatNhap, hanSuDung));
    }

    public void xacNhanHoaDonNhap(String maPhieu) {
        layPhieu(maPhieu).xacNhan(dsSanPham);
    }

    public void huyHoaDonNhap(String maPhieu) {
        layPhieu(maPhieu).huy(dsSanPham);
    }

    private HoadonNhap layPhieu(String maPhieu) {
        String maChuan = SanPham.chuanHoaMa(maPhieu);
        for (HoadonNhap p : dsHoaDonNhap) {
            if (p.getMaHoaDon().equalsIgnoreCase(maChuan)) return p;
        }
        throw new IllegalArgumentException("Không tìm thấy phiếu nhập: " + maPhieu);
    }

    public ArrayList<HoadonNhap> layDanhSachHoaDonNhap() {
        return new ArrayList<HoadonNhap>(dsHoaDonNhap);
    }

    public long tinhGiaVon(ArrayList<LoXuat> cacLoXuat) {
        if (cacLoXuat == null) return 0;
        long tong = 0;
        for (LoXuat lx : cacLoXuat) {
            SanPham sp = dsSanPham.timTheoMa(lx.getMaSP());
            LoHang lo = sp == null ? null : sp.timLo(lx.getMaLo());
            if (lo != null) {
                tong += (long) lx.getSoLuong() * lo.getGiaNhap();
            }
        }
        return tong;
    }

    public String baoCaoTonKho() { return dsSanPham.baoCaoThongKe(); }
    public ArrayList<SanPham> layHangSapHet() { return dsSanPham.layHangSapHet(NGUONG_SAP_HET_HANG); }
    public ArrayList<SanPham> layHangHetHang() { return dsSanPham.layHangHetHang(); }
    public ArrayList<SanPham> layHangHetHan(LocalDate ngay) { return dsSanPham.layHangHetHan(ngay); }
    public ArrayList<SanPham> layHangHetHan() { return layHangHetHan(LocalDate.now()); }

    public String baoCaoHanSuDung(LocalDate ngay, int soNgay) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== CẢNH BÁO HẠN SỬ DỤNG (Ngày kiểm tra: ").append(SanPham.dinhDangNgay(ngay)).append(") =====\n");
        ArrayList<LoHang> loHet = layLoHetHan(ngay);
        sb.append("1. Các lô ĐÃ HẾT HẠN (").append(loHet.size()).append(" lô):\n");
        if (loHet.isEmpty()) {
            sb.append("   (Không có lô nào hết hạn)\n");
        } else {
            for (LoHang lo : loHet) {
                SanPham sp = timSanPham(lo.getMaSP());
                sb.append("   - Lô ").append(lo.getMaLo()).append(" | SP: ").append(lo.getMaSP())
                        .append(sp != null ? " (" + sp.getTenSP() + ")" : "")
                        .append(" | Tồn: ").append(lo.getSoLuongCon())
                        .append(" | HSD: ").append(SanPham.dinhDangNgay(lo.getHanSuDung())).append("\n");
            }
        }
        ArrayList<LoHang> loSapHet = layLoSapHetHan(ngay, soNgay);
        sb.append("2. Các lô SẮP HẾT HẠN trong ").append(soNgay).append(" ngày (").append(loSapHet.size()).append(" lô):\n");
        if (loSapHet.isEmpty()) {
            sb.append("   (Không có lô nào sắp hết hạn)\n");
        } else {
            for (LoHang lo : loSapHet) {
                SanPham sp = timSanPham(lo.getMaSP());
                sb.append("   - Lô ").append(lo.getMaLo()).append(" | SP: ").append(lo.getMaSP())
                        .append(sp != null ? " (" + sp.getTenSP() + ")" : "")
                        .append(" | Tồn: ").append(lo.getSoLuongCon())
                        .append(" | HSD: ").append(SanPham.dinhDangNgay(lo.getHanSuDung())).append("\n");
            }
        }
        return sb.toString();
    }

    public String baoCaoHanSuDung() {
        return baoCaoHanSuDung(LocalDate.now(), SO_NGAY_CANH_BAO_HET_HAN);
    }

    public void inBaoCaoHanSuDung() {
        System.out.println(baoCaoHanSuDung());
    }

    public void inBaoCaoTonKho() {
        System.out.println(baoCaoTonKho());
    }

    public void inDanhSachSapHet() {
        System.out.println("--- Danh sách sản phẩm sắp hết hàng ---");
        inKetQua(layHangSapHet());
    }

    public void inDanhSachHetHang() {
        System.out.println("--- Danh sách sản phẩm đã hết hàng ---");
        inKetQua(layHangHetHang());
    }

    public void inDanhSachHetHan() {
        System.out.println("--- Danh sách sản phẩm có lô hết hạn ---");
        inKetQua(layHangHetHan(LocalDate.now()));
    }

    public long tongGiaTriTonKho() {
        return dsSanPham.tongGiaTriTon();
    }

    public int tongSoLuongTonKho() {
        return Math.toIntExact(dsSanPham.tongSoLuongTon());
    }

    // Các hàm tra cứu và sắp xếp ủy quyền từ DSSanPham
    public ArrayList<SanPham> timTheoTenChinhXac(String ten) { return dsSanPham.timTheoTenChinhXac(ten); }
    public ArrayList<SanPham> timTheoMaGanDung(String tuKhoa) { return dsSanPham.timTheoMaGanDung(tuKhoa); }
    public ArrayList<SanPham> timNhieuTieuChi(String tuKhoaTen, String loai, long giaTu, long giaDen) {
        return dsSanPham.timNhieuTieuChi(tuKhoaTen, loai, giaTu, giaDen);
    }
    public void sapXepTheoMa() { dsSanPham.sapXepTheoMa(); }
    public void sapXepTheoTen() { dsSanPham.sapXepTheoTen(); }
    public void sapXepTheoGiaBan(boolean tangDan) { dsSanPham.sapXepTheoGiaBan(tangDan); }
    public void sapXepTheoTon(boolean tangDan) { dsSanPham.sapXepTheoTon(tangDan); }

    // Quản lý và tra cứu phiếu nhập
    public HoadonNhap timHoaDonNhap(String maPhieu) {
        try {
            return layPhieu(maPhieu);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean tonTaiHoaDonNhap(String maPhieu) {
        return timHoaDonNhap(maPhieu) != null;
    }

    public void inTatCaPhieuNhap() {
        inHoaDonNhap();
    }

    public void inPhieuNhap(String maPhieu) {
        HoadonNhap p = layPhieu(maPhieu);
        System.out.println(p.moTaDayDu(nhanNhaCungCap(p.getMaNCC())));
    }

    // Quản lý và tra cứu giao dịch xuất kho
    public ArrayList<GiaoDichXuat> layDanhSachGiaoDichXuat() {
        return new ArrayList<GiaoDichXuat>(dsGiaoDichXuat);
    }

    public GiaoDichXuat timGiaoDichXuat(String maGiaoDich) {
        return timGiaoDich(maGiaoDich);
    }

    public void inTatCaGiaoDichXuat() {
        if (dsGiaoDichXuat.isEmpty()) {
            System.out.println("(Chưa có giao dịch xuất kho nào)");
            return;
        }
        for (GiaoDichXuat gd : dsGiaoDichXuat) {
            System.out.println(gd);
        }
    }

    public void inGiaoDichXuat(String maGiaoDich) {
        System.out.println(moTaGiaoDichXuat(maGiaoDich));
    }

    // Quản lý nhà cung cấp
    public void themNhaCungCap(NhaCungCap ncc) {
        dsNhaCungCap.them(ncc);
    }

    public void xuatDanhSachNhaCungCap() {
        dsNhaCungCap.xuat();
    }

    // Getters & Setters
    public DSSanPham getDsSanPham() { return dsSanPham; }
    public void setDsSanPham(DSSanPham dsSanPham) {
        if (dsSanPham == null) throw new IllegalArgumentException("Danh sách sản phẩm không được null.");
        this.dsSanPham = dsSanPham;
    }
    public ArrayList<HoadonNhap> getDsHoaDonNhap() { return dsHoaDonNhap; }
    public ArrayList<GiaoDichXuat> getDsGiaoDichXuat() { return dsGiaoDichXuat; }
    public DSNhaCungCap getDsNhaCungCap() { return dsNhaCungCap; }
    public void setDsNhaCungCap(DSNhaCungCap dsNhaCungCap) {
        if (dsNhaCungCap == null) throw new IllegalArgumentException("Danh sách nhà cung cấp không được null.");
        this.dsNhaCungCap = dsNhaCungCap;
    }

    public long tongTienNhap(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        long tong = 0;
        for (HoadonNhap phieu : dsHoaDonNhap) {
            if (trongKhoang(phieu, tuNgay, denNgay)) {
                tong += (long) phieu.tinhTongTien();
            }
        }
        return tong;
    }

    public String baoCaoNhapKho(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        StringBuilder ketQua = new StringBuilder();
        ketQua.append("===== NHẬP KHO TỪ ").append(SanPham.dinhDangNgay(tuNgay)).append(" ĐẾN ").append(SanPham.dinhDangNgay(denNgay))
                .append(" (phiếu đã xác nhận) =====\n");
        int soPhieu = 0;
        for (HoadonNhap phieu : dsHoaDonNhap) {
            if (trongKhoang(phieu, tuNgay, denNgay)) {
                ketQua.append(phieu.moTaDayDu(nhanNhaCungCap(phieu.getMaNCC()))).append("\n");
                soPhieu++;
            }
        }
        ketQua.append("Số phiếu: ").append(soPhieu).append(" | Tổng tiền nhập (chưa thuế): ")
                .append(SanPham.dinhDangTien(tongTienNhap(tuNgay, denNgay))).append("\n");
        return ketQua.toString();
    }

    private void kiemTraKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null || tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ: cần từ ngày <= đến ngày.");
        }
    }

    private boolean trongKhoang(HoadonNhap phieu, LocalDate tuNgay, LocalDate denNgay) {
        return phieu.getTrangThai() == HoadonNhap.TrangThai.DA_XAC_NHAN
                && !phieu.getNgayNhap().isBefore(tuNgay)
                && !phieu.getNgayNhap().isAfter(denNgay);
    }

    public void nhapDuLieuMau() {
        themSanPham(new ThucAn(ThucAn.maKeTiep(), "Bánh mì thịt", 12000, 20000, "cái", 5.0, "Tiệm Hoa"));
        themSanPham(new ThucAn(ThucAn.maKeTiep(), "Xúc xích nướng", 9000, 15000, "cái", 8.0, "CP Việt Nam"));
        themSanPham(new DoUong(DoUong.maKeTiep(), "Nước ngọt Coca Cola", 7000, 12000, "lon", 10.0, 330));
        themSanPham(new DoUong(DoUong.maKeTiep(), "Nước ngọt Pepsi", 7000, 12000, "lon", 8.0, 330));
        themSanPham(new DoUong(DoUong.maKeTiep(), "Nước suối", 3000, 6000, "chai", 5.0, 500));
    }

    public void luuDuLieu(String thuMuc) throws IOException {
        File dir = new File(thuMuc);
        if (!dir.exists() && !dir.mkdirs()) throw new IOException("Không tạo được thư mục: " + thuMuc);
        File[] dich = {new File(dir, TEN_FILE_SAN_PHAM), new File(dir, TEN_FILE_HOA_DON_NHAP), new File(dir, TEN_FILE_XUAT_KHO)};
        File[] tam = new File[dich.length];
        for (int i = 0; i < dich.length; i++) tam[i] = new File(dir, dich[i].getName() + ".tmp");
        try {
            dsSanPham.ghiRaFile(tam[0].getPath());
            ghiFileHoaDonNhap(tam[1]);
            ghiFileXuatKho(tam[2]);
        } catch (IOException e) {
            xoaTam(tam);
            throw e;
        }
        thayCacFile(tam, dich);
    }

    private void xoaTam(File[] cacFile) {
        for (File f : cacFile) {
            if (f != null && f.exists() && !f.delete()) f.deleteOnExit();
        }
    }

    private void thayCacFile(File[] tam, File[] dich) throws IOException {
        File[] sao = new File[dich.length];
        boolean[] daThay = new boolean[dich.length];
        try {
            for (int i = 0; i < dich.length; i++) {
                if (dich[i].exists()) {
                    sao[i] = new File(dich[i].getPath() + ".bak");
                    Files.copy(dich[i].toPath(), sao[i].toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
            for (int i = 0; i < dich.length; i++) {
                Files.move(tam[i].toPath(), dich[i].toPath(), StandardCopyOption.REPLACE_EXISTING);
                daThay[i] = true;
            }
        } catch (IOException e) {
            for (int i = 0; i < dich.length; i++) {
                if (!daThay[i]) continue;
                try {
                    if (sao[i] != null) Files.move(sao[i].toPath(), dich[i].toPath(), StandardCopyOption.REPLACE_EXISTING);
                    else Files.deleteIfExists(dich[i].toPath());
                } catch (IOException loiKhoiPhuc) {
                    e.addSuppressed(loiKhoiPhuc);
                }
            }
            xoaTam(tam);
            xoaTam(sao);
            throw e;
        }
        xoaTam(sao);
    }

    private void ghiFileHoaDonNhap(File file) throws IOException {
        try (BufferedWriter ghi = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            ghi.write(DSSanPham.DONG_PHIEN_BAN);
            ghi.newLine();
            for (HoadonNhap phieu : dsHoaDonNhap) {
                for (String dong : phieu.toCacDongFile()) {
                    ghi.write(dong);
                    ghi.newLine();
                }
            }
        }
    }

    private void ghiFileXuatKho(File file) throws IOException {
        try (BufferedWriter ghi = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            ghi.write(DSSanPham.DONG_PHIEN_BAN);
            ghi.newLine();
            for (GiaoDichXuat gd : dsGiaoDichXuat) {
                for (String dong : gd.toCacDongFile()) {
                    ghi.write(dong);
                    ghi.newLine();
                }
            }
        }
    }

    private ArrayList<GiaoDichXuat> docFileXuatKho(File file) throws IOException {
        ArrayList<GiaoDichXuat> ketQua = new ArrayList<>();
        if (!file.exists()) return ketQua;
        boolean dungPhienBan = false;
        try (BufferedReader doc = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String dong;
            int soDong = 0;
            while ((dong = doc.readLine()) != null) {
                soDong++;
                dong = dong.trim();
                if (soDong == 1 && dong.startsWith("\uFEFF")) dong = dong.substring(1).trim();
                if (dong.equals(DSSanPham.DONG_PHIEN_BAN)) { dungPhienBan = true; continue; }
                if (dong.isEmpty() || dong.startsWith("#")) continue;
                if (!dungPhienBan) throw new IOException(file.getPath() + ", dòng " + soDong + ": thiếu dòng '" + DSSanPham.DONG_PHIEN_BAN + "'.");
                String[] t = dong.split("\\|", -1);
                if (!t[0].equals("XK") || t.length != 6) throw new IllegalArgumentException("Dòng XK không hợp lệ.");
                String ma = SanPham.chuanHoaMa(t[1]);
                GiaoDichXuat gd = null;
                for (GiaoDichXuat co : ketQua) {
                    if (co.getMaGiaoDich().equals(ma)) gd = co;
                }
                if (gd == null) {
                    gd = new GiaoDichXuat(ma);
                    ketQua.add(gd);
                }
                gd.nap(t[2], t[3], Integer.parseInt(t[4].trim()), Integer.parseInt(t[5].trim()));
            }
        }
        return ketQua;
    }

    private void kiemTraSoXuatKho(ArrayList<GiaoDichXuat> dsGD, ArrayList<SanPham> dsSP) throws IOException {
        for (GiaoDichXuat gd : dsGD) {
            for (LoXuat x : gd.getCacLoDaXuat()) {
                SanPham sp = timTrong(dsSP, x.getMaSP());
                LoHang lo = sp == null ? null : sp.timLo(x.getMaLo());
                if (lo == null) {
                    throw new IOException("Dữ liệu không khớp: giao dịch " + gd.getMaGiaoDich() + " có lô " + x.getMaLo() + " không tồn tại trong kho.");
                }
            }
        }
    }

    public void docDuLieu(String thuMuc) throws IOException {
        File dir = new File(thuMuc);
        ArrayList<HoadonNhap> phieuMoi = docFileHoaDonNhap(new File(dir, TEN_FILE_HOA_DON_NHAP));
        ArrayList<SanPham> sanPhamMoi = dsSanPham.docSanPhamTuFile(new File(dir, TEN_FILE_SAN_PHAM).getPath());
        ArrayList<GiaoDichXuat> xuatMoi = docFileXuatKho(new File(dir, TEN_FILE_XUAT_KHO));
        kiemTraLienKet(sanPhamMoi, phieuMoi);
        kiemTraSoXuatKho(xuatMoi, sanPhamMoi);
        dsSanPham.thayTheBang(sanPhamMoi);
        dsHoaDonNhap = phieuMoi;
        dsGiaoDichXuat = xuatMoi;
    }

    private void kiemTraLienKet(ArrayList<SanPham> dsSP, ArrayList<HoadonNhap> dsPhieu) throws IOException {
        for (HoadonNhap phieu : dsPhieu) {
            if (!dsNhaCungCap.tonTai(phieu.getMaNCC())) {
                throw new IOException("Dữ liệu không khớp: nhà cung cấp " + phieu.getMaNCC() + " không có trong danh sách.");
            }
        }
    }

    private SanPham timTrong(ArrayList<SanPham> ds, String ma) {
        for (SanPham sp : ds) {
            if (sp.getMaSP().equalsIgnoreCase(SanPham.chuanHoaMa(ma))) return sp;
        }
        return null;
    }

    private ArrayList<HoadonNhap> docFileHoaDonNhap(File file) throws IOException {
        ArrayList<HoadonNhap> ketQua = new ArrayList<>();
        if (!file.exists()) return ketQua;
        String[] dauPhieu = null;
        ArrayList<CTHDonNhap> chiTietDangDoc = new ArrayList<>();
        try (BufferedReader doc = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String dong;
            while ((dong = doc.readLine()) != null) {
                dong = dong.trim();
                if (dong.isEmpty() || dong.startsWith("#") || dong.equals(DSSanPham.DONG_PHIEN_BAN)) continue;
                if (dong.startsWith("HDN|")) {
                    chotPhieuDaDoc(ketQua, dauPhieu, chiTietDangDoc);
                    dauPhieu = dong.split("\\|", -1);
                    chiTietDangDoc = new ArrayList<>();
                } else if (dong.startsWith("CT|")) {
                    chiTietDangDoc.add(CTHDonNhap.tuDongFile(dong));
                }
            }
            chotPhieuDaDoc(ketQua, dauPhieu, chiTietDangDoc);
        }
        return ketQua;
    }

    private void chotPhieuDaDoc(ArrayList<HoadonNhap> ketQua, String[] dauPhieu, ArrayList<CTHDonNhap> chiTiet) {
        if (dauPhieu == null || dauPhieu.length != 7) return;
        HoadonNhap phieu = new HoadonNhap(dauPhieu[1], dauPhieu[2], LocalDateTime.parse(dauPhieu[3]),
                LocalDate.parse(dauPhieu[4]), dauPhieu[5], HoadonNhap.TrangThai.valueOf(dauPhieu[6]), chiTiet);
        ketQua.add(phieu);
    }

    // ===================== MENU CONSOLE =====================
    public void chayMenu(Scanner sc) {
        int chon;
        do {
            inMenu();
            chon = Ktradulieu.luaChon(sc, "Chọn chức năng: ", 0, 23);
            try {
                xuLyLuaChon(chon, sc);
            } catch (Exception e) {
                System.out.println("Lỗi: " + e.getMessage());
            }
        } while (chon != 0);
    }

    private void inMenu() {
        System.out.println("\n========== QUẢN LÝ KHO ==========");
        System.out.println(" 1. Nhập n sản phẩm ban đầu |  2. Xuất danh sách sản phẩm");
        System.out.println(" 3. Thêm một sản phẩm      |  4. Thêm k sản phẩm");
        System.out.println(" 5. Sửa sản phẩm theo mã    |  6. Xóa sản phẩm theo mã");
        System.out.println(" 7. Tìm kiếm sản phẩm      |  8. Sắp xếp danh sách");
        System.out.println(" 9. Thống kê tồn kho       | 10. Cảnh báo hạn sử dụng");
        System.out.println("11. Tạo phiếu nhập         | 12. Thêm chi tiết vào phiếu");
        System.out.println("13. Xác nhận phiếu nhập    | 14. Hủy phiếu nhập");
        System.out.println("15. Xem các phiếu nhập     | 16. Báo cáo nhập kho");
        System.out.println("17. Xuất kho thử (FEFO)    | 18. Lưu dữ liệu ra file");
        System.out.println("19. Đọc dữ liệu từ file    | 20. Thêm sản phẩm mẫu");
        System.out.println("21. Xem các lô của sản phẩm| 22. Hoàn kho về đúng lô");
        System.out.println("23. Xem danh sách NCC      |  0. Thoát");
    }

    private void xuLyLuaChon(int chon, Scanner sc) throws IOException {
        switch (chon) {
            case 1: dsSanPham.nhapN(Ktradulieu.luaChon(sc, "Số sản phẩm (n): ", 1, 1000), sc); break;
            case 2: dsSanPham.xuat(); break;
            case 3: dsSanPham.nhapN(1, sc); break;
            case 4: dsSanPham.nhapN(Ktradulieu.luaChon(sc, "Số sản phẩm thêm (k): ", 1, 1000), sc); break;
            case 5: suaSanPhamTuBanPhim(sc); break;
            case 6:
                xoaSanPham(Ktradulieu.nhapChuoi(sc, "Mã SP cần xóa: ", ".+", "Không được để trống!"));
                System.out.println("Đã xóa sản phẩm.");
                break;
            case 7: timKiemTuBanPhim(sc); break;
            case 8: sapXepTuBanPhim(sc); break;
            case 9: System.out.println(baoCaoTonKho()); break;
            case 10: inCanhBao(); break;
            case 11: taoPhieuTuBanPhim(sc); break;
            case 12: themChiTietTuBanPhim(sc); break;
            case 13:
                xacNhanHoaDonNhap(Ktradulieu.nhapChuoi(sc, "Mã phiếu: ", ".+", "Không để trống!"));
                System.out.println("Đã xác nhận phiếu nhập!");
                break;
            case 14:
                huyHoaDonNhap(Ktradulieu.nhapChuoi(sc, "Mã phiếu cần hủy: ", ".+", "Không để trống!"));
                System.out.println("Đã hủy phiếu.");
                break;
            case 15: inHoaDonNhap(); break;
            case 16:
                LocalDate tu = Ktradulieu.nhapNgay(sc, "Từ ngày (dd/MM/yyyy): ", true);
                LocalDate den = Ktradulieu.nhapNgay(sc, "Đến ngày (dd/MM/yyyy): ", true);
                System.out.println(baoCaoNhapKho(tu, den));
                break;
            case 17: xuatKhoThuTuBanPhim(sc); break;
            case 18: luuDuLieu(THU_MUC_MAC_DINH); System.out.println("Đã lưu dữ liệu."); break;
            case 19: docDuLieu(THU_MUC_MAC_DINH); System.out.println("Đã nạp dữ liệu."); break;
            case 20: nhapDuLieuMau(); System.out.println("Đã thêm sản phẩm mẫu."); break;
            case 21:
                System.out.println(moTaCacLo(Ktradulieu.nhapChuoi(sc, "Mã sản phẩm: ", ".+", "Không để trống!")));
                break;
            case 22:
                String sp = Ktradulieu.nhapChuoi(sc, "Mã sản phẩm: ", ".+", "Không để trống!");
                String lo = Ktradulieu.nhapChuoi(sc, "Mã lô: ", ".+", "Không để trống!");
                int sl = Ktradulieu.luaChon(sc, "Số lượng hoàn: ", 1, Integer.MAX_VALUE);
                hoanKho(sp, lo, sl);
                System.out.println("Đã hoàn kho. " + moTaCacLo(sp));
                break;
            case 23: dsNhaCungCap.xuat(); break;
            case 0: System.out.println("Thoát chức năng kho."); break;
        }
    }

    private void suaSanPhamTuBanPhim(Scanner sc) {
        String ma = Ktradulieu.nhapChuoi(sc, "Mã sản phẩm cần sửa: ", ".+", "Không để trống!");
        SanPham cu = dsSanPham.timTheoMa(ma);
        if (cu == null) throw new IllegalArgumentException("Không tìm thấy: " + ma);
        System.out.println("Hiện tại: " + cu);
        String ten = Ktradulieu.nhapChuoi(sc, "Tên mới: ", ".+", "Không để trống!");
        long giaNhap = Long.parseLong(Ktradulieu.nhapChuoi(sc, "Giá nhập tham khảo mới: ", "\\d{1,18}", "Nhập số không âm!"));
        long giaBan = Ktradulieu.nhapSoNguyen(sc, "Giá bán mới: ");
        double thue = Double.parseDouble(Ktradulieu.nhapChuoi(sc, "Thuế suất bán mới (%): ", "\\d{1,9}(\\.\\d{1,4})?", "Nhập số không âm!"));
        SanPham moi;
        if (cu instanceof ThucAn) {
            String nsx = Ktradulieu.nhapChuoi(sc, "Nhà sản xuất mới: ", ".+", "Không để trống!");
            moi = new ThucAn(cu.getMaSP(), ten, giaNhap, giaBan, cu.getDonViTinh(), thue, nsx);
        } else {
            int dungTich = Ktradulieu.luaChon(sc, "Dung tích mới (ml): ", 1, DoUong.DUNG_TICH_TOI_DA_ML);
            moi = new DoUong(cu.getMaSP(), ten, giaNhap, giaBan, cu.getDonViTinh(), thue, dungTich);
        }
        suaSanPham(ma, moi);
        System.out.println("Đã sửa: " + dsSanPham.timTheoMa(ma));
    }

    private void timKiemTuBanPhim(Scanner sc) {
        System.out.println("1. Mã chính xác | 2. Tên gần đúng | 3. Theo loại | 4. Khoảng giá");
        int c = Ktradulieu.luaChon(sc, "Chọn: ", 1, 4);
        if (c == 1) {
            SanPham sp = timSanPham(Ktradulieu.nhapChuoi(sc, "Mã: ", ".+", "Không để trống!"));
            if (sp != null) System.out.println(sp); else System.out.println("Không tìm thấy!");
        } else if (c == 2) {
            inKetQua(timTheoTenGanDung(Ktradulieu.nhapChuoi(sc, "Tên: ", ".+", "Không để trống!")));
        } else if (c == 3) {
            inKetQua(timTheoLoai(Ktradulieu.nhapChuoi(sc, "Loại: ", ".+", "Không để trống!")));
        } else {
            long tu = Ktradulieu.nhapSoNguyen(sc, "Giá từ: ");
            long den = Ktradulieu.nhapSoNguyen(sc, "Giá đến: ");
            inKetQua(timTheoKhoangGia(tu, den));
        }
    }

    private void sapXepTuBanPhim(Scanner sc) {
        System.out.println("1. Theo Mã | 2. Theo Tên | 3. Giá tăng | 4. Giá giảm | 5. Tồn tăng | 6. Tồn giảm");
        int c = Ktradulieu.luaChon(sc, "Chọn: ", 1, 6);
        if (c == 1) dsSanPham.sapXepTheoMa();
        else if (c == 2) dsSanPham.sapXepTheoTen();
        else if (c == 3) dsSanPham.sapXepTheoGiaBan(true);
        else if (c == 4) dsSanPham.sapXepTheoGiaBan(false);
        else if (c == 5) dsSanPham.sapXepTheoTon(true);
        else dsSanPham.sapXepTheoTon(false);
        dsSanPham.xuat();
    }

    private void inCanhBao() {
        System.out.println("--- Sắp hết hàng ---");
        inKetQua(layHangSapHet());
        System.out.println("--- Hết hàng ---");
        inKetQua(layHangHetHang());
        System.out.println(baoCaoHanSuDung(LocalDate.now(), SO_NGAY_CANH_BAO_HET_HAN));
    }

    private void taoPhieuTuBanPhim(Scanner sc) {
        dsNhaCungCap.xuat();
        String ncc = Ktradulieu.nhapChuoi(sc, "Mã nhà cung cấp: ", ".+", "Không để trống!");
        String nv = Ktradulieu.nhapChuoi(sc, "Mã nhân viên lập: ", ".+", "Không để trống!");
        LocalDate ngay = Ktradulieu.nhapNgay(sc, "Ngày nhập (dd/MM/yyyy, Enter = hôm nay): ", false);
        if (ngay == null) ngay = LocalDate.now();
        HoadonNhap p = taoHoaDonNhap(nv, ncc, ngay);
        System.out.println("Đã tạo phiếu: " + p.getMaHoaDon());
    }

    private void themChiTietTuBanPhim(Scanner sc) {
        String p = Ktradulieu.nhapChuoi(sc, "Mã phiếu: ", ".+", "Không để trống!");
        String sp = Ktradulieu.nhapChuoi(sc, "Mã SP: ", ".+", "Không để trống!");
        int sl = Ktradulieu.luaChon(sc, "Số lượng: ", 1, Integer.MAX_VALUE);
        long gia = Long.parseLong(Ktradulieu.nhapChuoi(sc, "Giá nhập: ", "\\d{1,18}", "Nhập số không âm!"));
        double thue = Double.parseDouble(Ktradulieu.nhapChuoi(sc, "Thuế suất nhập (%): ", "\\d{1,9}(\\.\\d{1,4})?", "Nhập số không âm!"));
        LocalDate hsd = Ktradulieu.nhapNgay(sc, "Hạn sử dụng (dd/MM/yyyy, Enter = không hạn): ", false);
        themChiTietPhieu(p, sp, sl, gia, thue, hsd);
        System.out.println("Đã thêm vào phiếu " + p);
    }

    private void inHoaDonNhap() {
        if (dsHoaDonNhap.isEmpty()) {
            System.out.println("(Chưa có phiếu nhập nào)");
            return;
        }
        for (HoadonNhap p : dsHoaDonNhap) {
            System.out.println(p.moTaDayDu(nhanNhaCungCap(p.getMaNCC())));
        }
    }

    private void xuatKhoThuTuBanPhim(Scanner sc) {
        String sp = Ktradulieu.nhapChuoi(sc, "Mã SP bán: ", ".+", "Không để trống!");
        int sl = Ktradulieu.luaChon(sc, "Số lượng: ", 1, Integer.MAX_VALUE);
        ArrayList<LoXuat> lo = xuatKho(sp, sl);
        System.out.println("Đã xuất kho từ các lô FEFO:");
        for (LoXuat x : lo) System.out.println("  " + x);
    }

    private void inKetQua(ArrayList<SanPham> kq) {
        if (kq.isEmpty()) { System.out.println("(Không có sản phẩm nào)"); return; }
        for (SanPham sp : kq) System.out.println(sp);
    }
}