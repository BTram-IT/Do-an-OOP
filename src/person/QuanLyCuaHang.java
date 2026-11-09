package person;

import doitac.DSDoiTac;
import doitac.DoiTac;
import doitac.DonViVanChuyen;
import doitac.NhaCungCap;
import hoadon.CTHoaDon;
import hoadon.DSHoadon;
import hoadon.HoaDon;
import hoadon.HoadonNhap;
import hoadon.Hoadonban;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import khuyenmai.DSKhuyenMai;
import khuyenmai.DSSanPhamKhuyenMai;
import khuyenmai.Khuyenmai;
import khuyenmai.KhuyenmaiMuatang;
import khuyenmai.KhuyenmaiPhantram;
import khuyenmai.KhuyenmaiTructiep;
import sanpham.DSSanPham;
import sanpham.SanPham;
import util.Ktradulieu;

public class QuanLyCuaHang extends NhanVien {

    private static final Scanner sc = new Scanner(System.in);

    private DSSanPham dsSP = new DSSanPham();
    private DSNhanVien dsNV = new DSNhanVien();
    private DsKhachHang dsKH = new DsKhachHang();
    private DSDoiTac dsDoiTac = new DSDoiTac();
    private DSKhuyenMai dsKhuyenMai = new DSKhuyenMai();
    private DSSanPhamKhuyenMai dsSPKM = new DSSanPhamKhuyenMai();
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

    public QuanLyCuaHang(DsKhachHang dsKH, DSSanPham dsSP, DSNhanVien dsNV, DSDoiTac dsDoiTac, DSKhuyenMai dsKhuyenMai, DSSanPhamKhuyenMai dsSPKM, DSHoadon dsHoaDonBan, DSHoadon dsHoaDonNhap) {
        this.dsKH = dsKH;
        this.dsSP = dsSP;
        this.dsNV = dsNV;
        this.dsDoiTac = dsDoiTac;
        this.dsKhuyenMai = dsKhuyenMai;
        this.dsSPKM = (dsSPKM == null) ? new DSSanPhamKhuyenMai() : dsSPKM;
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

    public void ketNoi(DSSanPham dsSP, DSNhanVien dsNV, DsKhachHang dsKH, DSDoiTac dsDoiTac,
                       DSKhuyenMai dsKhuyenMai, DSSanPhamKhuyenMai dsSPKM, DSHoadon dsHoaDonBan, DSHoadon dsHoaDonNhap) {
        ketNoi(dsSP, dsNV, dsKH, dsDoiTac, dsKhuyenMai, dsHoaDonBan, dsHoaDonNhap);
        if (dsSPKM != null) this.dsSPKM = dsSPKM;
    }

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
        return dsKhuyenMai.xoaKhuyenMai(ma);
    }

    public HoadonNhap nhapThemHang(NhaCungCap ncc, String maSP, int soLuong, double giaNhap) {
        SanPham sp = dsSP.timTheoMa(maSP);
        if (ncc == null || sp == null || soLuong <= 0 || giaNhap < 0) return null;
        HoadonNhap hd = new HoadonNhap(Ktradulieu.taoMa("HN%03d", dsHoaDonNhap.size() + 1), getMaNV(),
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
        if (dsSP.size() == 0) {
            System.out.println("Chưa có dữ liệu sản phẩm!");
            return;
        }
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
    public DSSanPhamKhuyenMai getDsSPKM() { return dsSPKM; }
    public void setDsSPKM(DSSanPhamKhuyenMai dsSPKM) { this.dsSPKM = dsSPKM; }
    public ArrayList<Hoadonban> getHoaDonBan() { return dsHoaDonBan.layHoaDonBan(); }

    // ==================== CÁC MENU QUẢN LÝ ====================

    public void menuQuanLySanPham() {
        int chon;
        do {
            System.out.println("\n--- QUẢN LÝ SẢN PHẨM ---");
            System.out.println("1. Xem danh sách sản phẩm");
            System.out.println("2. Thêm sản phẩm");
            System.out.println("3. Sửa sản phẩm");
            System.out.println("4. Xóa sản phẩm");
            System.out.println("5. Tìm kiếm sản phẩm theo mã");
            System.out.println("0. Quay lại");
            chon = Ktradulieu.luaChon(sc, "Chọn: ", 0, 5);
            switch (chon) {
                case 1:
                    dsSP.xuat();
                    break;
                case 2:
                    dsSP.nhapN(1, sc);
                    break;
                case 3:
                    String maSua = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã SP cần sửa: ");
                    if (dsSP.timTheoMa(maSua) != null) {
                        System.out.println("Nhập thông tin mới:");
                        SanPham spSua = DSSanPham.nhapSanPhamTuBanPhim(sc);
                        dsSP.sua(maSua, spSua);
                        System.out.println("Đã sửa!");
                    } else {
                        System.out.println("Không tìm thấy!");
                    }
                    break;
                case 4:
                    String maXoa = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã SP cần xóa: ");
                    try {
                        dsSP.xoaTheoMa(maXoa);
                        System.out.println("Đã xóa!");
                    } catch(Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 5:
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã SP: ");
                    SanPham sp = dsSP.timTheoMa(ma);
                    if (sp != null) System.out.println(sp.toString());
                    else System.out.println("Không tìm thấy sản phẩm!");
                    break;
            }
        } while (chon != 0);
    }

    public void menuQuanLyNhanVien() {
        int chon;
        do {
            System.out.println("\n--- QUẢN LÝ NHÂN VIÊN ---");
            System.out.println("1. Hiển thị danh sách nhân viên");
            System.out.println("2. Xóa nhân viên theo mã");
            System.out.println("3. Tìm kiếm nhân viên");
            System.out.println("0. Quay lại");
            chon = Ktradulieu.luaChon(sc, "Chọn: ", 0, 3);
            switch (chon) {
                case 1:
                    dsNV.xuatDanhSach();
                    break;
                case 2:
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã nhân viên cần xóa: ");
                    if (xoaNhanVien(ma)) {
                        System.out.println("Xóa thành công!");
                    } else {
                        System.out.println("Không thể xóa (mã không tồn tại hoặc trùng mã quản lý hiện tại)!");
                    }
                    break;
                case 3:
                    String maTK = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã nhân viên: ");
                    NhanVien nv = dsNV.timKiemTheoMa(maTK);
                    if (nv != null) System.out.println(nv);
                    else System.out.println("Không tìm thấy nhân viên!");
                    break;
            }
        } while (chon != 0);
    }

    public void menuQuanLyKhachHang() {
        int chon;
        do {
            System.out.println("\n--- QUẢN LÝ KHÁCH HÀNG ---");
            System.out.println("1. Hiển thị danh sách khách hàng");
            System.out.println("2. Hiển thị danh sách khách hàng VIP");
            System.out.println("3. Tìm kiếm theo mã khách hàng");
            System.out.println("4. Tìm kiếm theo tên");
            System.out.println("0. Quay lại");
            chon = Ktradulieu.luaChon(sc, "Chọn: ", 0, 4);
            switch (chon) {
                case 1:
                    dsKH.xuatDanhSach();
                    break;
                case 2:
                    dsKH.xuatDanhSachVip();
                    break;
                case 3:
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã KH: ");
                    Khachhang kh = dsKH.timKiemTheoMa(ma);
                    if (kh != null) System.out.println(kh);
                    else System.out.println("Không tìm thấy khách hàng!");
                    break;
                case 4:
                    String ten = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên KH: ");
                    ArrayList<Khachhang> kq = dsKH.timKiemTheoTen(ten);
                    if (kq.isEmpty()) System.out.println("Không có khách hàng phù hợp!");
                    else for (Khachhang item : kq) System.out.println(item);
                    break;
            }
        } while (chon != 0);
    }

    // ==================== QUẢN LÝ ĐỐI TÁC (THÊM / XÓA / SỬA / TÌM) ====================
    public void menuQuanLyDoiTac() {
        int chon;
        do {
            System.out.println("\n--- QUẢN LÝ ĐỐI TÁC ---");
            System.out.println("1. Hiển thị danh sách đối tác");
            System.out.println("2. Thêm đối tác mới");
            System.out.println("3. Sửa thông tin đối tác");
            System.out.println("4. Xóa đối tác");
            System.out.println("5. Tìm đối tác theo mã");
            System.out.println("0. Quay lại");
            chon = Ktradulieu.luaChon(sc, "Chọn: ", 0, 5);
            switch (chon) {
                case 1:
                    dsDoiTac.xuatDanhSach();
                    break;
                case 2:
                    themDoiTacTuBanPhim();
                    break;
                case 3:
                    suaDoiTacTuBanPhim();
                    break;
                case 4:
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã đối tác cần xóa: ");
                    if (dsDoiTac.xoa(ma)) System.out.println("Xóa thành công!");
                    else System.out.println("Không tìm thấy đối tác!");
                    break;
                case 5:
                    String maTK = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã đối tác: ");
                    DoiTac dt = dsDoiTac.timTheoMa(maTK);
                    if (dt != null) System.out.println(dt);
                    else System.out.println("Không tìm thấy đối tác!");
                    break;
            }
        } while (chon != 0);
    }

    private void themDoiTacTuBanPhim() {
        System.out.println("\nChọn loại đối tác cần thêm:");
        System.out.println("1. Nhà cung cấp");
        System.out.println("2. Đơn vị vận chuyển");
        int loai = Ktradulieu.luaChon(sc, "Chọn: ", 1, 2);
        try {
            if (loai == 1) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.nhap(sc);
                dsDoiTac.them(ncc);
            } else {
                DonViVanChuyen vc = new DonViVanChuyen();
                vc.nhap(sc);
                dsDoiTac.them(vc);
            }
            System.out.println("Thêm đối tác thành công!");
        } catch (Exception e) {
            System.out.println("Lỗi thêm đối tác: " + e.getMessage());
        }
    }

    private void suaDoiTacTuBanPhim() {
        String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã đối tác cần sửa: ");
        DoiTac cu = dsDoiTac.timTheoMa(ma);
        if (cu == null) {
            System.out.println("Không tìm thấy đối tác với mã: " + ma);
            return;
        }
        System.out.println("Thông tin hiện tại: " + cu);
        try {
            if (cu instanceof NhaCungCap) {
                NhaCungCap moi = new NhaCungCap();
                moi.setMaDT(cu.getMaDT());
                moi.nhap(sc);
                moi.setMaDT(cu.getMaDT());
                dsDoiTac.sua(ma, moi);
            } else if (cu instanceof DonViVanChuyen) {
                DonViVanChuyen moi = new DonViVanChuyen();
                moi.setMaDT(cu.getMaDT());
                moi.nhap(sc);
                moi.setMaDT(cu.getMaDT());
                dsDoiTac.sua(ma, moi);
            }
            System.out.println("Cập nhật đối tác thành công!");
        } catch (Exception e) {
            System.out.println("Lỗi sửa đối tác: " + e.getMessage());
        }
    }

    // ==================== QUẢN LÝ KHUYẾN MÃI (THÊM / XÓA / SỬA / TÌM) ====================
    public void menuQuanLyKhuyenMai() {
        int chon;
        do {
            System.out.println("\n--- QUẢN LÝ KHUYẾN MÃI ---");
            System.out.println("1. Hiển thị danh sách chương trình khuyến mãi");
            System.out.println("2. Thêm chương trình khuyến mãi mới");
            System.out.println("3. Sửa chương trình khuyến mãi");
            System.out.println("4. Xóa chương trình khuyến mãi");
            System.out.println("5. Tìm kiếm chương trình khuyến mãi");
            System.out.println("6. Quản lý sản phẩm khuyến mãi (SPKM)");
            System.out.println("0. Quay lại");
            chon = Ktradulieu.luaChon(sc, "Chọn: ", 0, 6);
            switch (chon) {
                case 1:
                    dsKhuyenMai.xuatDanhSach();
                    break;
                case 2:
                    themKhuyenMaiTuBanPhim();
                    break;
                case 3:
                    dsKhuyenMai.suaKhuyenMai();
                    break;
                case 4:
                    dsKhuyenMai.xoaKhuyenMai();
                    break;
                case 5:
                    dsKhuyenMai.timKiemKhuyenMai();
                    break;
                case 6:
                    menuQuanLySPKhuyenMai();
                    break;
            }
        } while (chon != 0);
    }

    private void themKhuyenMaiTuBanPhim() {
        System.out.println("\nChọn loại khuyến mãi:");
        System.out.println("1. Giảm giá trực tiếp");
        System.out.println("2. Giảm giá theo phần trăm");
        System.out.println("3. Mua tặng");
        int loai = Ktradulieu.luaChon(sc, "Chọn: ", 1, 3);
        Khuyenmai km = null;
        switch (loai) {
            case 1:
                km = new KhuyenmaiTructiep();
                break;
            case 2:
                km = new KhuyenmaiPhantram();
                break;
            case 3:
                km = new KhuyenmaiMuatang();
                break;
        }
        if (km != null) {
            try {
                km.setKM();
                dsKhuyenMai.themKhuyenMai(km);
            } catch (Exception e) {
                System.out.println("Lỗi tạo khuyến mãi: " + e.getMessage());
            }
        }
    }

    // ==================== QUẢN LÝ SẢN PHẨM KHUYẾN MÃI (THÊM / XÓA / SỬA / TÌM) ====================
    public void menuQuanLySPKhuyenMai() {
        int chon;
        do {
            System.out.println("\n--- QUẢN LÝ SẢN PHẨM KHUYẾN MÃI ---");
            System.out.println("1. Xem danh sách sản phẩm khuyến mãi");
            System.out.println("2. Thêm sản phẩm vào khuyến mãi");
            System.out.println("3. Sửa sản phẩm khuyến mãi");
            System.out.println("4. Xóa sản phẩm khỏi khuyến mãi");
            System.out.println("5. Kiểm tra liên kết (Tìm kiếm)");
            System.out.println("0. Quay lại");
            chon = Ktradulieu.luaChon(sc, "Chọn: ", 0, 5);
            switch (chon) {
                case 1:
                    dsSPKM.xuatDanhSach();
                    break;
                case 2:
                    dsSPKM.themSP();
                    break;
                case 3:
                    dsSPKM.suaSP();
                    break;
                case 4:
                    dsSPKM.xoaSP();
                    break;
                case 5:
                    String maSP = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã sản phẩm: ");
                    String maKM = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã khuyến mãi: ");
                    if (dsSPKM.kiemTraLienKet(maSP, maKM)) {
                        System.out.println("Sản phẩm [" + maSP + "] ĐANG ĐƯỢC ÁP DỤNG chương trình [" + maKM + "]!");
                    } else {
                        System.out.println("Không tìm thấy liên kết giữa sản phẩm và mã KM này!");
                    }
                    break;
            }
        } while (chon != 0);
    }

    public void menuQuanLyHoaDon() {
        int chon;
        do {
            System.out.println("\n--- QUẢN LÝ HÓA ĐƠN ---");
            System.out.println("1. Xem danh sách hóa đơn bán");
            System.out.println("2. Xem danh sách hóa đơn nhập");
            System.out.println("3. Tra cứu hóa đơn theo mã");
            System.out.println("0. Quay lại");
            chon = Ktradulieu.luaChon(sc, "Chọn: ", 0, 3);
            switch (chon) {
                case 1:
                    for(Hoadonban hd : dsHoaDonBan.layHoaDonBan()) hd.inhoadon();
                    break;
                case 2:
                    for(HoadonNhap hd : dsHoaDonNhap.layHoaDonNhap()) hd.inhoadon();
                    break;
                case 3:
                    String ma = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hóa đơn: ");
                    HoaDon hd = dsHoaDonBan.timKiemTheoMa(ma);
                    if (hd == null) hd = dsHoaDonNhap.timKiemTheoMa(ma);
                    if (hd != null) hd.inhoadon();
                    else System.out.println("Không tìm thấy hóa đơn!");
                    break;
            }
        } while (chon != 0);
    }

    public void menuThongKeKinhDoanh() {
        int chon;
        do {
            System.out.println("\n--- THỐNG KÊ KINH DOANH ---");
            System.out.println("1. Top 5 sản phẩm bán chạy nhất");
            System.out.println("2. Top 10 sản phẩm bán chạy nhất");
            System.out.println("3. Tổng số đơn hàng bán ra");
            System.out.println("0. Quay lại");
            chon = Ktradulieu.luaChon(sc, "Chọn: ", 0, 3);
            switch (chon) {
                case 1:
                    phanTichSPBanChay(5);
                    break;
                case 2:
                    phanTichSPBanChay(10);
                    break;
                case 3:
                    System.out.println("Tổng hóa đơn bán: " + dsHoaDonBan.layHoaDonBan().size());
                    break;
            }
        } while (chon != 0);
    }
}
