import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class QuanlyKho {
    public static final int NGUONG_SAP_HET_HANG = 10;
    public static final int SO_NGAY_CANH_BAO_HET_HAN = 7;
    public static final String THU_MUC_MAC_DINH = "du_lieu_kho";
    public static final String TEN_FILE_SAN_PHAM = "san_pham.txt";
    public static final String TEN_FILE_HOA_DON_NHAP = "hoa_don_nhap.txt";
    public static final String TEN_FILE_XUAT_KHO = "xuat_kho.txt";

    private DSSanPham dsSanPham;
    private DSHoadon dsHoaDonNhap;
    private ArrayList<GiaoDichXuat> dsGiaoDichXuat;
    private DSNhaCungCap dsNhaCungCap;

    public  QuanlyKho() {
        super();
    }
    public QuanlyKho(DSNhaCungCap dsNhaCungCap) {
        this(new DSSanPham(), dsNhaCungCap, new DSHoadon());
    }

    public QuanlyKho(DSSanPham dsSanPham, DSNhaCungCap dsNhaCungCap) {
        this(dsSanPham, dsNhaCungCap, new DSHoadon());
    }

    public QuanlyKho(DSSanPham dsSanPham, DSNhaCungCap dsNhaCungCap, DSHoadon dsHoaDonNhap) {
        if (dsSanPham == null || dsNhaCungCap == null || dsHoaDonNhap == null) {
            throw new IllegalArgumentException("Dữ liệu khởi tạo không được null.");
        }
        this.dsSanPham = dsSanPham;
        this.dsNhaCungCap = dsNhaCungCap;
        this.dsHoaDonNhap = dsHoaDonNhap;
        this.dsGiaoDichXuat = new ArrayList<>();
    }

    private ArrayList<HoadonNhap> danhSachPhieuNhap() {
        return dsHoaDonNhap.layHoaDonNhap();
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

    // ==================== QUẢN LÝ SẢN PHẨM TRONG KHO ====================

    public SanPham timSanPham(String ma) {
        return dsSanPham.timTheoMa(ma);
    }

    public boolean tonTaiSanPham(String ma) {
        return dsSanPham.tonTaiMa(ma);
    }

    public ArrayList<SanPham> timTheoTenGanDung(String tuKhoa) {
        return dsSanPham.timTheoTenGanDung(tuKhoa);
    }

    public ArrayList<SanPham> timTheoLoai(String loai) {
        return dsSanPham.timTheoLoai(loai);
    }

    public ArrayList<SanPham> timTheoKhoangGia(long giaTu, long giaDen) {
        return dsSanPham.timTheoKhoangGia(giaTu, giaDen);
    }

    public int soSanPham() {
        return dsSanPham.soLuong();
    }

    public void xuatDanhSachSanPham() {
        dsSanPham.xuat();
    }

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

    public int kiemTraTon(String maSP) {
        return dsSanPham.layTon(maSP);
    }

    public int kiemTraTonBanDuoc(String maSP) {
        return dsSanPham.layTonBanDuoc(maSP, LocalDate.now());
    }

    public boolean duTon(String maSP, int soLuong) {
        return dsSanPham.duTon(maSP, soLuong);
    }

    // ==================== XUẤT KHO FEFO ====================

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
            throw new IllegalArgumentException("Danh sách xuất kho không hợp lệ.");
        }
        if (ngay == null) throw new IllegalArgumentException("Ngày xuất kho không được null.");

        for (int i = 0; i < maSP.length; i++) {
            if (maSP[i] == null || soLuong[i] <= 0) {
                throw new IllegalArgumentException("Mã sản phẩm không hợp lệ hoặc số lượng không dương.");
            }
        }

        for (int i = 0; i < maSP.length; i++) {
            if (daGapMaTruocDo(maSP, i)) continue;
            long tong = tongSoLuongCuaMa(maSP, soLuong, i);
            if (tong > Integer.MAX_VALUE || !dsSanPham.duTon(maSP[i], (int) tong, ngay)) {
                throw new IllegalStateException("Không đủ hàng còn hạn cho sản phẩm " + maSP[i]);
            }
        }

        ArrayList<LoXuat> ketQua = new ArrayList<>();
        for (int i = 0; i < maSP.length; i++) {
            if (!daGapMaTruocDo(maSP, i)) {
                ketQua.addAll(dsSanPham.xuatFEFO(maSP[i], (int) tongSoLuongCuaMa(maSP, soLuong, i), ngay));
            }
        }
        return ketQua;
    }

    private boolean daGapMaTruocDo(String[] maSP, int viTri) {
        for (int i = 0; i < viTri; i++) {
            if (SanPham.chuanHoaMa(maSP[i]).equals(SanPham.chuanHoaMa(maSP[viTri]))) return true;
        }
        return false;
    }

    private long tongSoLuongCuaMa(String[] maSP, int[] soLuong, int viTri) {
        long tong = 0;
        for (int i = 0; i < maSP.length; i++) {
            if (SanPham.chuanHoaMa(maSP[i]).equals(SanPham.chuanHoaMa(maSP[viTri]))) tong += soLuong[i];
        }
        return tong;
    }

    // ==================== HOÀN KHO ====================

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
            if (!daGapLoTruocDo(cacLoDaXuat, i)) {
                LoXuat x = cacLoDaXuat.get(i);
                dsSanPham.hoanLo(x.getMaSP(), x.getMaLo(), tongSoLuongCuaLo(cacLoDaXuat, i));
            }
        }
    }

    private boolean daGapLoTruocDo(ArrayList<LoXuat> ds, int viTri) {
        for (int i = 0; i < viTri; i++) {
            if (ds.get(i).getMaSP().equals(ds.get(viTri).getMaSP())
                    && ds.get(i).getMaLo().equals(ds.get(viTri).getMaLo())) return true;
        }
        return false;
    }

    private int tongSoLuongCuaLo(ArrayList<LoXuat> ds, int viTri) {
        long tong = 0;
        for (LoXuat x : ds) {
            if (x.getMaSP().equals(ds.get(viTri).getMaSP())
                    && x.getMaLo().equals(ds.get(viTri).getMaLo())) tong += x.getSoLuong();
        }
        if (tong > Integer.MAX_VALUE) throw new IllegalArgumentException("Số lượng hoàn quá lớn.");
        return (int) tong;
    }

    // ==================== QUẢN LÝ GIAO DỊCH XUẤT ====================

    private GiaoDichXuat timGiaoDich(String maGiaoDich) {
        String ma = SanPham.chuanHoaMa(maGiaoDich);
        for (GiaoDichXuat gd : dsGiaoDichXuat) {
            if (gd.getMaGiaoDich().equals(ma)) return gd;
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
        if (timGiaoDich(gd.getMaGiaoDich()) != null) {
            throw new IllegalArgumentException("Giao dịch đã tồn tại.");
        }
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
        if (conLai.isEmpty()) throw new IllegalStateException("Giao dịch đã hoàn hết.");
        hoanKho(conLai);
        for (LoXuat x : conLai) gd.ghiNhanHoan(x.getMaSP(), x.getMaLo(), x.getSoLuong());
        return conLai;
    }

    public void hoanKhoTheoGiaoDich(String maGiaoDich, String maSP, String maLo, int soLuong) {
        GiaoDichXuat gd = layGiaoDich(maGiaoDich);
        if (soLuong <= 0 || soLuong > gd.soConHoanDuoc(maSP, maLo)) {
            throw new IllegalArgumentException("Số lượng hoàn không hợp lệ.");
        }
        hoanKho(maSP, maLo, soLuong);
        gd.ghiNhanHoan(maSP, maLo, soLuong);
    }

    public ArrayList<LoXuat> hoanKhoTheoGiaoDich(String maGiaoDich, String maSP, int soLuong) {
        GiaoDichXuat gd = layGiaoDich(maGiaoDich);
        if (soLuong <= 0) throw new IllegalArgumentException("Số lượng hoàn phải lớn hơn 0.");
        String ma = SanPham.chuanHoaMa(maSP);
        ArrayList<LoXuat> keHoach = new ArrayList<>();
        int canHoan = soLuong;

        for (LoXuat x : gd.getCacLoConHoanDuoc()) {
            if (canHoan > 0 && x.getMaSP().equals(ma)) {
                int sl = Math.min(canHoan, x.getSoLuong());
                keHoach.add(new LoXuat(ma, x.getMaLo(), sl));
                canHoan -= sl;
            }
        }
        if (canHoan > 0) throw new IllegalStateException("Giao dịch không đủ hàng để hoàn.");
        hoanKho(keHoach);
        for (LoXuat x : keHoach) gd.ghiNhanHoan(x.getMaSP(), x.getMaLo(), x.getSoLuong());
        return keHoach;
    }

    // ==================== QUẢN LÝ NHẬP KHO ====================

    public HoadonNhap taoHoaDonNhap(String maNhanVien, String maNhaCungCap, LocalDate ngayNhap) {
        NhaCungCap ncc = dsNhaCungCap.timTheoMa(maNhaCungCap);
        if (ncc == null) throw new IllegalArgumentException("Nhà cung cấp không tồn tại: " + maNhaCungCap);
        HoadonNhap phieu = new HoadonNhap(HoadonNhap.maKeTiep(), maNhanVien, ngayNhap, ncc.getMaDT());

        if (!dsHoaDonNhap.them(phieu)) {
            throw new IllegalArgumentException("Không thể thêm hóa đơn nhập.");
        }
        return phieu;
    }

    public void themChiTietPhieu(String maPhieu, String maSP, int soLuong, Long giaNhap, double thueSuatNhap, LocalDate hanSuDung) {
        if (giaNhap == null) {
            SanPham sp = dsSanPham.timTheoMa(maSP);
            if (sp == null) throw new IllegalArgumentException("Sản phẩm không tồn tại: " + maSP);
            giaNhap = sp.getGiaNhap();
        }
        themChiTietPhieu(maPhieu, maSP, soLuong, giaNhap.longValue(), thueSuatNhap, hanSuDung);
    }

    public void themChiTietPhieu(String maPhieu, String maSP, int soLuong, long giaNhap, double thueSuatNhap, LocalDate hanSuDung) {
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
        String ma = SanPham.chuanHoaMa(maPhieu);
        for (HoadonNhap p : danhSachPhieuNhap()) {
            if (p.getMaHoaDon().equalsIgnoreCase(ma)) return p;
        }
        throw new IllegalArgumentException("Không tìm thấy phiếu nhập: " + maPhieu);
    }

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

    public ArrayList<HoadonNhap> layDanhSachHoaDonNhap() {
        return danhSachPhieuNhap();
    }

    public ArrayList<HoadonNhap> getDsHoaDonNhap() {
        return danhSachPhieuNhap();
    }

    public void inTatCaPhieuNhap() {
        inHoaDonNhap();
    }

    public void inPhieuNhap(String maPhieu) {
        HoadonNhap p = layPhieu(maPhieu);
        System.out.println(p.moTaDayDu(nhanNhaCungCap(p.getMaNCC())));
    }

    private void inHoaDonNhap() {
        ArrayList<HoadonNhap> ds = danhSachPhieuNhap();
        if (ds.isEmpty()) {
            System.out.println("(Chưa có hóa đơn nhập nào)");
            return;
        }
        for (HoadonNhap p : ds) System.out.println(p.moTaDayDu(nhanNhaCungCap(p.getMaNCC())));
    }

    // ==================== THỐNG KÊ VÀ CẢNH BÁO ====================

    public String baoCaoTonKho() {
        return dsSanPham.baoCaoThongKe();
    }

    public ArrayList<SanPham> layHangSapHet() {
        return dsSanPham.layHangSapHet(NGUONG_SAP_HET_HANG);
    }

    public ArrayList<SanPham> layHangHetHang() {
        return dsSanPham.layHangHetHang();
    }

    public ArrayList<SanPham> layHangHetHan(LocalDate ngay) {
        return dsSanPham.layHangHetHan(ngay);
    }

    public ArrayList<SanPham> layHangHetHan() {
        return layHangHetHan(LocalDate.now());
    }

    public ArrayList<LoHang> layLoHetHan(LocalDate ngay) {
        return dsSanPham.layLoHetHan(ngay);
    }

    public ArrayList<LoHang> layLoSapHetHan(LocalDate ngay, int soNgay) {
        return dsSanPham.layLoSapHetHan(ngay, soNgay);
    }

    public String baoCaoHanSuDung(LocalDate ngay, int soNgay) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== CẢNH BÁO HẠN SỬ DỤNG =====\n");
        sb.append("Ngày kiểm tra: ").append(SanPham.dinhDangNgay(ngay)).append("\n");

        ArrayList<LoHang> loHet = layLoHetHan(ngay);
        sb.append("\n1. Các lô đã hết hạn: ").append(loHet.size()).append("\n");
        if (loHet.isEmpty()) sb.append("(Không có)\n");
        for (LoHang lo : loHet) {
            SanPham sp = timSanPham(lo.getMaSP());
            sb.append("- ").append(lo.getMaSP()).append(" | ")
                    .append(sp == null ? "" : sp.getTenSP())
                    .append(" | Lô: ").append(lo.getMaLo())
                    .append(" | Tồn: ").append(lo.getSoLuongCon())
                    .append(" | HSD: ").append(SanPham.dinhDangNgay(lo.getHanSuDung())).append("\n");
        }

        ArrayList<LoHang> loSapHet = layLoSapHetHan(ngay, soNgay);
        sb.append("\n2. Các lô sắp hết hạn trong ").append(soNgay).append(" ngày: ").append(loSapHet.size()).append("\n");
        if (loSapHet.isEmpty()) sb.append("(Không có)\n");
        for (LoHang lo : loSapHet) {
            SanPham sp = timSanPham(lo.getMaSP());
            sb.append("- ").append(lo.getMaSP()).append(" | ")
                    .append(sp == null ? "" : sp.getTenSP())
                    .append(" | Lô: ").append(lo.getMaLo())
                    .append(" | Tồn: ").append(lo.getSoLuongCon())
                    .append(" | HSD: ").append(SanPham.dinhDangNgay(lo.getHanSuDung())).append("\n");
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
        System.out.println("--- Sản phẩm sắp hết hàng ---");
        inKetQua(layHangSapHet());
    }

    public void inDanhSachHetHang() {
        System.out.println("--- Sản phẩm đã hết hàng ---");
        inKetQua(layHangHetHang());
    }

    public void inDanhSachHetHan() {
        System.out.println("--- Sản phẩm có lô hết hạn ---");
        inKetQua(layHangHetHan());
    }

    private void inCanhBao() {
        inDanhSachSapHet();
        inDanhSachHetHang();
        inBaoCaoHanSuDung();
    }

    public long tongGiaTriTonKho() {
        return dsSanPham.tongGiaTriTon();
    }

    public int tongSoLuongTonKho() {
        return Math.toIntExact(dsSanPham.tongSoLuongTon());
    }

    private void inKetQua(ArrayList<SanPham> ds) {
        if (ds.isEmpty()) {
            System.out.println("(Không có sản phẩm nào)");
            return;
        }
        for (SanPham sp : ds) System.out.println(sp);
    }

    // ==================== BÁO CÁO NHẬP KHO ====================

    public long tongTienNhap(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        long tong = 0;
        for (HoadonNhap p : danhSachPhieuNhap()) {
            if (trongKhoang(p, tuNgay, denNgay)) tong += p.tinhTongTien();
        }
        return tong;
    }

    public String baoCaoNhapKho(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        StringBuilder sb = new StringBuilder();
        sb.append("===== BÁO CÁO NHẬP KHO =====\n");
        sb.append("Từ ngày: ").append(SanPham.dinhDangNgay(tuNgay));
        sb.append(" đến ngày: ").append(SanPham.dinhDangNgay(denNgay)).append("\n");

        int soPhieu = 0;
        for (HoadonNhap p : danhSachPhieuNhap()) {
            if (trongKhoang(p, tuNgay, denNgay)) {
                sb.append(p.moTaDayDu(nhanNhaCungCap(p.getMaNCC()))).append("\n");
                soPhieu++;
            }
        }
        sb.append("Số phiếu: ").append(soPhieu)
                .append(" | Tổng tiền nhập: ").append(SanPham.dinhDangTien(tongTienNhap(tuNgay, denNgay)));
        return sb.toString();
    }

    private void kiemTraKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null || tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
        }
    }

    private boolean trongKhoang(HoadonNhap p, LocalDate tuNgay, LocalDate denNgay) {
        return p.getTrangThai() == HoadonNhap.TrangThai.DA_XAC_NHAN
                && !p.getNgayNhap().isBefore(tuNgay)
                && !p.getNgayNhap().isAfter(denNgay);
    }

    // ==================== TRA CỨU VÀ SẮP XẾP ====================

    public ArrayList<SanPham> timTheoTenChinhXac(String ten) {
        return dsSanPham.timTheoTenChinhXac(ten);
    }

    public ArrayList<SanPham> timTheoMaGanDung(String tuKhoa) {
        return dsSanPham.timTheoMaGanDung(tuKhoa);
    }

    public ArrayList<SanPham> timNhieuTieuChi(String ten, String loai, long giaTu, long giaDen) {
        return dsSanPham.timNhieuTieuChi(ten, loai, giaTu, giaDen);
    }

    public void sapXepTheoMa() {
        dsSanPham.sapXepTheoMa();
    }

    public void sapXepTheoTen() {
        dsSanPham.sapXepTheoTen();
    }

    public void sapXepTheoGiaBan(boolean tangDan) {
        dsSanPham.sapXepTheoGiaBan(tangDan);
    }

    public void sapXepTheoTon(boolean tangDan) {
        dsSanPham.sapXepTheoTon(tangDan);
    }

    public void timKiemTuBanPhim(Scanner sc) {
        System.out.println("1. Tìm theo mã | 2. Tìm theo tên | 3. Tìm theo loại | 4. Tìm theo khoảng giá");
        int chon = Ktradulieu.luaChon(sc, "Chọn: ", 1, 4);
        if (chon == 1) {
            SanPham sp = timSanPham(Ktradulieu.nhapChuoi(sc, "Nhập mã sản phẩm: ", ".+", "Không được để trống!"));
            System.out.println(sp == null ? "Không tìm thấy sản phẩm." : sp);
        } else if (chon == 2) {
            inKetQua(timTheoTenGanDung(Ktradulieu.nhapChuoi(sc, "Nhập tên: ", ".+", "Không được để trống!")));
        } else if (chon == 3) {
            inKetQua(timTheoLoai(Ktradulieu.nhapChuoi(sc, "Nhập loại: ", ".+", "Không được để trống!")));
        } else {
            long tu = Ktradulieu.nhapSoNguyen(sc, "Giá từ: ");
            long den = Ktradulieu.nhapSoNguyen(sc, "Giá đến: ");
            if (tu > den) throw new IllegalArgumentException("Giá từ không được lớn hơn giá đến.");
            inKetQua(timTheoKhoangGia(tu, den));
        }
    }

    public double tinhGiaVon(ArrayList<LoXuat> dsLoXuat) {
        if (dsLoXuat == null || dsLoXuat.isEmpty()) {
            return 0;
        }

        double tongVon = 0;
        for (LoXuat x : dsLoXuat) {
            if (x == null) continue;

            // Tìm sản phẩm trong kho theo mã
            SanPham sp = dsSanPham.timTheoMa(x.getMaSP());
            if (sp != null) {
                // Lấy giá nhập của sản phẩm (hoặc từ lô cụ thể nếu lớp LoHang có lưu giá nhập riêng)
                long giaNhap = sp.getGiaNhap();

                // Nếu hệ thống quản lý giá theo từng lô cụ thể, bạn có thể viết thêm logic lấy giá của lô:
                // long giaNhapLo = sp.layGiaNhapLo(x.getMaLo());
                // if (giaNhapLo > 0) giaNhap = giaNhapLo;

                tongVon += (double) giaNhap * x.getSoLuong();
            }
        }
        return tongVon;
    }
    // ==================== GIAO DỊCH XUẤT KHO ====================

    public ArrayList<GiaoDichXuat> layDanhSachGiaoDichXuat() {
        return new ArrayList<>(dsGiaoDichXuat);
    }

    public ArrayList<GiaoDichXuat> getDsGiaoDichXuat() {
        return new ArrayList<>(dsGiaoDichXuat);
    }

    public GiaoDichXuat timGiaoDichXuat(String maGiaoDich) {
        return timGiaoDich(maGiaoDich);
    }

    public void inTatCaGiaoDichXuat() {
        if (dsGiaoDichXuat.isEmpty()) {
            System.out.println("(Chưa có giao dịch xuất kho nào)");
            return;
        }
        for (GiaoDichXuat gd : dsGiaoDichXuat) System.out.println(gd);
    }

    public void inGiaoDichXuat(String maGiaoDich) {
        System.out.println(moTaGiaoDichXuat(maGiaoDich));
    }

    // ==================== NHÀ CUNG CẤP ====================

    public void themNhaCungCap(NhaCungCap ncc) {
        dsNhaCungCap.them(ncc);
    }

    public void xuatDanhSachNhaCungCap() {
        dsNhaCungCap.xuat();
    }

    // ==================== GETTER / SETTER ====================

    public DSSanPham getDsSanPham() {
        return dsSanPham;
    }

    public void setDsSanPham(DSSanPham dsSanPham) {
        if (dsSanPham == null) throw new IllegalArgumentException("Danh sách sản phẩm không được null.");
        this.dsSanPham = dsSanPham;
    }

    public DSHoadon getDsHoaDonNhapChung() {
        return dsHoaDonNhap;
    }

    public DSNhaCungCap getDsNhaCungCap() {
        return dsNhaCungCap;
    }

    public void setDsNhaCungCap(DSNhaCungCap dsNhaCungCap) {
        if (dsNhaCungCap == null) throw new IllegalArgumentException("Danh sách nhà cung cấp không được null.");
        this.dsNhaCungCap = dsNhaCungCap;
    }

    // ==================== LƯU / ĐỌC DỮ LIỆU ====================

    public void luuDuLieu(String thuMuc) throws IOException {
        File dir = new File(thuMuc);
        if (!dir.exists() && !dir.mkdirs()) throw new IOException("Không tạo được thư mục dữ liệu.");

        dsSanPham.ghiRaFile(new File(dir, TEN_FILE_SAN_PHAM).getPath());
        dsHoaDonNhap.ghiFile(new File(dir, TEN_FILE_HOA_DON_NHAP).getPath());
        ghiFileXuatKho(new File(dir, TEN_FILE_XUAT_KHO));
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
        try (BufferedReader doc = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String dong;
            int soDong = 0;
            while ((dong = doc.readLine()) != null) {
                soDong++;
                dong = dong.trim();
                if (soDong == 1 && dong.startsWith("\uFEFF")) dong = dong.substring(1).trim();
                if (dong.equals(DSSanPham.DONG_PHIEN_BAN)) {
                    dungPhienBan = true;
                    continue;
                }
                if (dong.isEmpty() || dong.startsWith("#")) continue;
                if (!dungPhienBan) throw new IOException("Thiếu dòng phiên bản dữ liệu.");

                String[] t = dong.split("\\|", -1);
                if (t.length != 6 || !t[0].equals("XK")) {
                    throw new IOException("Dòng xuất kho không hợp lệ tại dòng " + soDong);
                }

                String ma = SanPham.chuanHoaMa(t[1]);
                GiaoDichXuat gd = null;
                for (GiaoDichXuat x : ketQua) {
                    if (x.getMaGiaoDich().equals(ma)) gd = x;
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

    public void docDuLieu(String thuMuc) throws IOException {
        File dir = new File(thuMuc);
        ArrayList<SanPham> sanPhamMoi = dsSanPham.docSanPhamTuFile(
                new File(dir, TEN_FILE_SAN_PHAM).getPath());
        ArrayList<GiaoDichXuat> xuatMoi = docFileXuatKho(
                new File(dir, TEN_FILE_XUAT_KHO));

        dsSanPham.thayTheBang(sanPhamMoi);
        dsGiaoDichXuat = xuatMoi;
        dsHoaDonNhap.docFile(new File(dir, TEN_FILE_HOA_DON_NHAP).getPath());

        for (HoadonNhap p : danhSachPhieuNhap()) {
            if (!dsNhaCungCap.tonTai(p.getMaNCC())) {
                throw new IOException("Nhà cung cấp không tồn tại: " + p.getMaNCC());
            }
        }
    }
    private void taoPhieuTuBanPhim(Scanner sc) {
        dsNhaCungCap.xuat();
        String ncc = Ktradulieu.nhapChuoi(sc, "Mã nhà cung cấp: ", ".+", "Không được để trống!");
        String nv = Ktradulieu.nhapChuoi(sc, "Mã nhân viên lập: ", ".+", "Không được để trống!");
        LocalDate ngay = Ktradulieu.nhapNgay(sc, "Ngày nhập (dd/MM/yyyy, Enter = hôm nay): ", false);
        if (ngay == null) ngay = LocalDate.now();
        HoadonNhap p = taoHoaDonNhap(nv, ncc, ngay);
        System.out.println("Đã tạo hóa đơn nhập: " + p.getMaHoaDon());
    }

    private void themChiTietTuBanPhim(Scanner sc) {
        String maPhieu = Ktradulieu.nhapChuoi(sc, "Mã hóa đơn: ", ".+", "Không được để trống!");
        String maSP = Ktradulieu.nhapChuoi(sc, "Mã sản phẩm: ", ".+", "Không được để trống!");
        int soLuong = Ktradulieu.luaChon(sc, "Số lượng: ", 1, Integer.MAX_VALUE);
        long giaNhap = Long.parseLong(Ktradulieu.nhapChuoi(sc, "Giá nhập: ", "\\d{1,18}", "Giá nhập không hợp lệ!"));
        double thue = Double.parseDouble(Ktradulieu.nhapChuoi(sc, "Thuế suất nhập (%): ", "\\d{1,9}(\\.\\d{1,4})?", "Thuế suất không hợp lệ!"));
        LocalDate hsd = Ktradulieu.nhapNgay(sc, "Hạn sử dụng (dd/MM/yyyy, Enter = không hạn): ", false);
        themChiTietPhieu(maPhieu, maSP, soLuong, giaNhap, thue, hsd);
        System.out.println("Đã thêm sản phẩm vào hóa đơn.");
    }

    private void xuatKhoThuTuBanPhim(Scanner sc) {
        String maSP = Ktradulieu.nhapChuoi(sc, "Mã sản phẩm: ", ".+", "Không được để trống!");
        int soLuong = Ktradulieu.luaChon(sc, "Số lượng xuất: ", 1, Integer.MAX_VALUE);
        ArrayList<LoXuat> ds = xuatKho(maSP, soLuong);
        System.out.println("Đã xuất kho theo nguyên tắc FEFO:");
        for (LoXuat x : ds) System.out.println(x);
    }
}