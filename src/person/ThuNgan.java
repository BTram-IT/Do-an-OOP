//package person;
//
//import hoadon.CTHoaDon;
//import hoadon.DSHoadon;
//import hoadon.HoaDon;
//import hoadon.Hoadonban;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Scanner;
//import khuyenmai.DSKhuyenMai;
//import khuyenmai.DSSanPhamKhuyenMai;
//import sanpham.DSSanPham;
//import sanpham.SanPham;
//import util.Ktradulieu;
//
//public class ThuNgan extends NhanVien {
//
//    private String tenThuNgan;
//    private String chucVu;
//
//    private final String[] caLamViec = {
//            "Ca Sang", "Ca Chieu", "Ca Toi"
//    };
//
//    private String caHienTai;
//
//    private DSHoadon Hoadon;
//    private DsKhachHang Khachhang;
//
//    // ================= CONSTRUCTOR =================
//
//    public ThuNgan() {
//        super();
//        this.tenThuNgan = "";
//        this.chucVu = "Thu ngan";
//        this.Hoadon = new DSHoadon();
//        this.Khachhang = new DsKhachHang();
//    }
//
//    public ThuNgan(String maNV, String tenThuNgan, String chucVu) {
//        this(maNV, tenThuNgan, chucVu,
//                new DSHoadon(), new DsKhachHang());
//    }
//
//    public ThuNgan(String maNV, String tenThuNgan, String chucVu,
//                   DSHoadon Hoadon, DsKhachHang Khachhang) {
//
//        super();
//
//        if (maNV != null && !maNV.trim().isEmpty()) {
//            setMaNV(maNV);
//        }
//
//        setTenThuNgan(tenThuNgan);
//        setChucVu(chucVu);
//
//        this.Hoadon = (Hoadon == null) ? new DSHoadon() : Hoadon;
//        this.Khachhang = (Khachhang == null) ? new DsKhachHang() : Khachhang;
//    }
//
//    public ThuNgan(DsKhachHang khachhang, DSHoadon hoadon) {
//        super();
//        this.chucVu = "Thu ngan";
//        this.Khachhang = (khachhang == null) ? new DsKhachHang() : khachhang;
//        this.Hoadon = (hoadon == null) ? new DSHoadon() : hoadon;
//    }
//
//    // ================= GETTER & SETTER =================
//
//    public String getMaTN() { return getMaNV(); }
//    public String getTenThuNgan() { return tenThuNgan; }
//    public String getChucVu() { return chucVu; }
//    public String getCaHienTai() { return caHienTai; }
//    public DSHoadon getHoadon() { return Hoadon; }
//    public DsKhachHang getKhachhang() { return Khachhang; }
//
//    public void setMaTN(String maTN) { setMaNV(maTN); }
//
//    public void setTenThuNgan(String tenThuNgan) {
//        if (tenThuNgan == null || tenThuNgan.trim().isEmpty()) {
//            if (this.tenThuNgan == null) {
//                this.tenThuNgan = "";
//                return;
//            }
//            throw new IllegalArgumentException("Tên thu ngân không được để trống!");
//        }
//        this.tenThuNgan = Ktradulieu.chuanHoaChuoi(tenThuNgan);
//        setTen(this.tenThuNgan);
//    }
//
//    @Override
//    public void setChucVu(String chucVu) {
//        if (chucVu == null || chucVu.trim().isEmpty()) {
//            throw new IllegalArgumentException("Chức vụ không được để trống!");
//        }
//        this.chucVu = Ktradulieu.chuanHoaChuoi(chucVu);
//        super.setChucVu(this.chucVu);
//    }
//
//    // ================= CA LÀM VIỆC =================
//
//    public void hienThiCaLamViec() {
//        System.out.println("\n===== DANH SÁCH CA LÀM VIỆC =====");
//        for (int i = 0; i < caLamViec.length; i++) {
//            System.out.println((i + 1) + ". " + caLamViec[i]);
//        }
//    }
//
//    public void chonCaLamViec(Scanner sc) {
//        if (sc == null) throw new IllegalArgumentException("Scanner không hợp lệ!");
//        hienThiCaLamViec();
//        int luaChon = Ktradulieu.luaChon(sc, "Chọn ca làm việc: ", 1, caLamViec.length);
//        caHienTai = caLamViec[luaChon - 1];
//        System.out.println("Ca làm việc hiện tại: " + caHienTai);
//    }
//
//    // ================= QUẢN LÝ KHÁCH HÀNG =================
//
//    public void themKhachHang(Khachhang kh) {
//        if (kh == null) throw new IllegalArgumentException("Khách hàng không hợp lệ!");
//        Khachhang.them(kh);
//    }
//
//    public Khachhang timKhachHangTheoMa(String maKH) {
//        return Khachhang.timKiemTheoMa(Ktradulieu.chuanHoaMa(maKH));
//    }
//
//    public ArrayList<Khachhang> timKhachHangTheoTen(String tenKH) {
//        return Khachhang.timKiemTheoTen(tenKH);
//    }
//
//    public boolean suaKhachHang(String maKH, Khachhang khMoi) {
//        if (khMoi == null) return false;
//        return Khachhang.sua(Ktradulieu.chuanHoaMa(maKH), khMoi);
//    }
//
//    public boolean xoaKhachHang(String maKH) {
//        return Khachhang.xoa(Ktradulieu.chuanHoaMa(maKH));
//    }
//
//    public void hienThiDanhSachKhachHang() { Khachhang.xuatDanhSach(); }
//    public void hienThiDanhSachKhachHangVIP() { Khachhang.xuatDanhSachVip(); }
//
//    // ================= LẬP HÓA ĐƠN BÁN HÀNG MỚI (TRỰC TIẾP CTHoaDon) =================
//
//    public void taoHoaDonMoi(Scanner sc, DSSanPham dsSP, DSSanPhamKhuyenMai dsSPKM, DSKhuyenMai dsKM) {
//        if (sc == null || dsSP == null) {
//            System.out.println("Dữ liệu đầu vào không hợp lệ!");
//            return;
//        }
//
//        System.out.println("\n===== LẬP HÓA ĐƠN BÁN HÀNG MỚI =====");
//
//        // 1. Nhập mã hóa đơn
//        String maHD = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã hóa đơn: ");
//
//        // 2. Nhập mã khách hàng
//        String maKH = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã khách hàng: ");
//        Khachhang kh = timKhachHangTheoMa(maKH);
//        if (kh == null) {
//            System.out.println("Không tìm thấy khách hàng! Vui lòng thêm khách hàng trước.");
//            return;
//        }
//
//        // Khởi tạo Hoadonban
//        Hoadonban hdMoi = new Hoadonban(
//                maHD,
//                getMaNV(),
//                LocalDateTime.now(),
//                maKH,
//                new ArrayList<>(),
//                null,
//                dsSP,
//                dsSPKM,
//                dsKM
//        );
//
//        LocalDate ngayBan = LocalDate.now();
//        char tiepTuc = 0;
//
//        do {
//            System.out.println("\n--- Thêm sản phẩm vào hóa đơn ---");
//            try {
//                // Nhập mã sản phẩm
//                String maSP = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã sản phẩm: ");
//                SanPham sp = dsSP.timTheoMa(SanPham.chuanHoaMa(maSP));
//
//                if (sp == null) {
//                    System.out.println("Không tìm thấy sản phẩm! Vui lòng nhập lại.");
//                    continue;
//                }
//
//                int soLuongConBan = sp.getSoLuongBanDuoc(ngayBan);
//                if (soLuongConBan <= 0) {
//                    System.out.println("Sản phẩm " + sp.getTenSP() + " hiện đã hết hàng hoặc hết hạn bán!");
//                    continue;
//                }
//
//                System.out.println("Tên sản phẩm: " + sp.getTenSP()
//                        + " | Giá bán: " + Ktradulieu.dinhDangTien(sp.getGiaBan())
//                        + " | Còn lại có thể bán: " + soLuongConBan);
//
//                // Nhập số lượng
//                long soLuongNhap = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng: ");
//                if (soLuongNhap <= 0 || soLuongNhap > soLuongConBan) {
//                    System.out.println("Số lượng không hợp lệ hoặc vượt quá tồn kho!");
//                    continue;
//                }
//
//                // Tạo trực tiếp CTHoaDon và thêm vào hóa đơn
//                CTHoaDon ct = new CTHoaDon(
//                        sp.getMaSP(),
//                        sp.getTenSP(),
//                        (int) soLuongNhap,
//                        sp.getGiaBan(),
//                        maHD
//                );
//
//                hdMoi.themChiTiet(ct);
//                System.out.println("Đã thêm sản phẩm thành công vào hóa đơn!");
//
//            } catch (Exception e) {
//                System.out.println("Lỗi khi thêm sản phẩm: " + e.getMessage());
//            }
//
//            System.out.print("Bạn có muốn mua thêm sản phẩm khác không? (y/n): ");
//            String luaChon = sc.nextLine().trim();
//            tiepTuc = luaChon.isEmpty() ? 'n' : Character.toLowerCase(luaChon.charAt(0));
//
//        } while (tiepTuc == 'y');
//
//        if (hdMoi.getCTHoaDon().isEmpty()) {
//            System.out.println("Hóa đơn chưa có sản phẩm nào. Hủy tạo hóa đơn!");
//            return;
//        }
//
//        // 3. Tiến hành thanh toán (Tiền mặt / Chuyển khoản)
//        try {
//            long tongTienCanThanhToan = hdMoi.Tongtienhoadon();
//
//            // Gọi phương thức nhapThanhToan có sẵn trong Hoadonban
//            hdMoi.nhapThanhToan(sc, tongTienCanThanhToan);
//
//            // 4. Lưu hóa đơn vào hệ thống
//            boolean ketQua = taoHoaDon(hdMoi);
//            if (ketQua) {
//                System.out.println("\n===== THANH TOÁN VÀ LẬP HÓA ĐƠN THÀNH CÔNG =====");
//                hdMoi.inhoadon();
//            } else {
//                System.out.println("Không thể lưu hóa đơn vào hệ thống!");
//            }
//
//        } catch (Exception e) {
//            System.out.println("Lỗi trong quá trình thanh toán: " + e.getMessage());
//        }
//    }
//
//    // ================= LƯU TRỮ HÓA ĐƠN =================
//
//    public boolean taoHoaDon(Hoadonban hd) {
//        if (hd == null || hd.getMaHD() == null || hd.getMaHD().trim().isEmpty()) {
//            System.out.println("Hóa đơn không hợp lệ!");
//            return false;
//        }
//
//        Khachhang kh = timKhachHangTheoMa(hd.getMaKH());
//        if (kh == null) {
//            System.out.println("Không tìm thấy khách hàng!");
//            return false;
//        }
//
//        if (hd.getMaNV() == null || !hd.getMaNV().equalsIgnoreCase(getMaNV())) {
//            hd.setMaNV(getMaNV());
//        }
//
//        if (!hd.isDaThanhToan()) {
//            System.out.println("Hóa đơn chưa hoàn tất thanh toán!");
//            return false;
//        }
//
//        boolean themThanhCong = Hoadon.them(hd);
//        if (!themThanhCong) {
//            System.out.println("Không thể thêm hóa đơn vào danh sách quản lý!");
//            return false;
//        }
//
//        // Cập nhật lịch sử mua hàng và xét điểm VIP cho khách hàng
//        kh.themLichSu(hd.getMaHD(), (long) hd.getTongTien());
//        Khachhang.capNhatDanhSachVip();
//
//        return true;
//    }
//
//    // ================= TRA CỨU & IN HÓA ĐƠN =================
//
//    public void traCuuHoaDon(String maHD) {
//        String ma = Ktradulieu.chuanHoaMa(maHD);
//        if (ma.isEmpty()) {
//            System.out.println("Mã hóa đơn không được để trống!");
//            return;
//        }
//
//        Hoadonban hd = null;
//        for (HoaDon hoaDon : Hoadon.getDs()) {
//            if (hoaDon instanceof Hoadonban) {
//                Hoadonban hoaDonBan = (Hoadonban) hoaDon;
//                if (hoaDonBan.getMaHD().equalsIgnoreCase(ma)) {
//                    hd = hoaDonBan;
//                    break;
//                }
//            }
//        }
//
//        if (hd == null) {
//            System.out.println("Không tìm thấy hóa đơn!");
//            return;
//        }
//        hd.inhoadon();
//    }
//
//    public void inDanhSachHoaDonTheoKhachHang(String maKH) {
//        String ma = Ktradulieu.chuanHoaMa(maKH);
//        boolean timThay = false;
//
//        for (HoaDon hoaDon : Hoadon.getDs()) {
//            if (hoaDon instanceof Hoadonban) {
//                Hoadonban hd = (Hoadonban) hoaDon;
//                if (hd.getMaKH() != null && hd.getMaKH().equalsIgnoreCase(ma)) {
//                    hd.inhoadon();
//                    timThay = true;
//                }
//            }
//        }
//
//        if (!timThay) {
//            System.out.println("Khách hàng chưa có hóa đơn bán nào!");
//        }
//    }
//
//    // ================= HIỂN THỊ THÔNG TIN =================
//
//    public void hienThiThongTin() {
//        System.out.println("\n========== THÔNG TIN THU NGÂN ==========");
//        System.out.println("Mã nhân viên: " + getMaNV());
//        System.out.println("Tên thu ngân: " + tenThuNgan);
//        System.out.println("Chức vụ: " + chucVu);
//        System.out.println("Ca làm việc: " + (caHienTai == null ? "Chưa phân ca" : caHienTai));
//        System.out.println("========================================");
//    }
//
//    @Override
//    public String toString() {
//        return "ThuNgan{" +
//                "maNV='" + getMaNV() + '\'' +
//                ", tenThuNgan='" + tenThuNgan + '\'' +
//                ", chucVu='" + chucVu + '\'' +
//                ", caHienTai='" + caHienTai + '\'' +
//                '}';
//    }
//
//    @Override
//    public String getVaiTro() {
//        return "ThuNgan";
//    }
//}