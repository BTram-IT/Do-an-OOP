import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

public class HoaDonNhap extends HoaDon {

    public enum TrangThai {
        CHO_XAC_NHAN("Chờ xác nhận"),
        DA_XAC_NHAN("Đã xác nhận"),
        DA_HUY("Đã hủy");

        private String nhan;

        TrangThai(String nhan) {
            this.nhan = nhan;
        }

        public String getNhan() {
            return nhan;
        }
    }

    public static final String TIEN_TO_MA = "HDN";
    private static int boDem = 0;

    private LocalDate ngayNhap;
    private String maNhaCungCap;
    private ArrayList<ChiTietPhieuNhap> chiTiet = new ArrayList<>();
    private TrangThai trangThai;

    // Tạo phiếu mới, ở trạng thái chờ xác nhận
    public HoaDonNhap(String maHoaDon, String maNhanVien, LocalDate ngayNhap, String maNhaCungCap) {
        this(maHoaDon, maNhanVien, LocalDateTime.now(), ngayNhap, maNhaCungCap,
                TrangThai.CHO_XAC_NHAN, new ArrayList<ChiTietPhieuNhap>());
    }

    // Dùng khi đọc file: giữ nguyên trạng thái và các dòng chi tiết đã lưu, KHÔNG đụng tới tồn kho
    public HoaDonNhap(String maHoaDon, String maNhanVien, LocalDateTime thoiGianLap, LocalDate ngayNhap,
                      String maNhaCungCap, TrangThai trangThai, ArrayList<ChiTietPhieuNhap> cacDong) {
        
            super();
            
            setMaHD(SanPham.chuanHoaMa(
                SanPham.kiemTraVanBan(maHoaDon, "Mã hóa đơn nhập")
            ));
            
            setMaNV(SanPham.chuanHoaMa(
                SanPham.kiemTraVanBan(maNhanVien, "Mã nhân viên")
            ));
            
            setThoigianin(thoiGianLap);

        if (thoiGianLap == null) {
            throw new IllegalArgumentException("Thời gian lập hóa đơn không được null.");
        }
        if (ngayNhap == null) {
            throw new IllegalArgumentException("Ngày nhập không được null.");
        }
        this.ngayNhap = ngayNhap;
        this.maNhaCungCap = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maNhaCungCap, "Mã nhà cung cấp"));
        if (trangThai == null) {
            throw new IllegalArgumentException("Trạng thái phiếu không được null.");
        }
        this.trangThai = trangThai;
        for (ChiTietPhieuNhap ct : cacDong) {
            kiemTraChiTiet(ct);
            chiTiet.add(ct);
        }
        boDem = Math.max(boDem, SanPham.laySoTuMa(getMaHD(), TIEN_TO_MA));
    }

    public static String maKeTiep() {
        return String.format(Locale.ROOT, "%s%03d", TIEN_TO_MA, boDem + 1);
    }

    // Mỗi sản phẩm một dòng; hạn sử dụng không được trước ngày nhập 
    private void kiemTraChiTiet(ChiTietPhieuNhap ct) {
        if (ct.getHanSuDung() != null && ct.getHanSuDung().isBefore(ngayNhap)) {
            throw new IllegalArgumentException("Dòng " + ct.getMaSP() + ": hạn sử dụng không được trước ngày nhập "
                    + TienIch.dinhDangNgay(ngayNhap) + ".");
        }
        if (daCoMa(ct.getMaSP())) {
            throw new IllegalArgumentException("Hóa đơn nhập " + getMaHD() + " đã có sản phẩm " + ct.getMaSP()
                    + " (mỗi sản phẩm chỉ một dòng, muốn đổi thì xóa dòng cũ rồi thêm lại).");
        }
    }

    // ===================== CHI TIẾT =====================

    public void themChiTiet(ChiTietPhieuNhap ct) {
        kiemTraConSua();
        if (ct == null) {
            throw new IllegalArgumentException("Chi tiết phiếu nhập không được null.");
        }
        kiemTraChiTiet(ct);
        chiTiet.add(ct);
    }

    // Xóa dòng của một sản phẩm khỏi phiếu chưa xác nhận, trả về true nếu có xóa
    public boolean xoaChiTiet(String maSP) {
        kiemTraConSua();
        String maChuan = SanPham.chuanHoaMa(maSP);
        for (int i = 0; i < chiTiet.size(); i++) {
            if (chiTiet.get(i).getMaSP().equals(maChuan)) {
                chiTiet.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean coSanPham(String maSP) {
        return daCoMa(maSP);
    }

    private boolean daCoMa(String maSP) {
        String maChuan = SanPham.chuanHoaMa(maSP);
        for (ChiTietPhieuNhap ct : chiTiet) {
            if (ct.getMaSP().equals(maChuan)) {
                return true;
            }
        }
        return false;
    }

    // Tổng tiền hàng của phiếu, CHƯA thuế (Kế toán tính thuế nhập từ thueSuatNhap của từng dòng)
    public long tinhTongTien() {
        long tong = 0;
        for (ChiTietPhieuNhap ct : chiTiet) {
            tong = Math.addExact(tong, ct.getThanhTien());
        }
        return tong;
    }

    // Tổng thuế nhập của cả hóa đơn (cộng thuế từng dòng)
    public long tinhTongThue() {
        long tong = 0;
        for (ChiTietPhieuNhap ct : chiTiet) {
            tong = Math.addExact(tong, ct.tienThueNhap());
        }
        return tong;
    }

    // Tổng thanh toán cho nhà cung cấp = tiền hàng + thuế nhập.
    // [HoaDon-thật] nhóm cần xác nhận Tongtienhoadon() của hóa đơn nhập có gồm thuế hay không (ở đây: có).
    @Override
    public double Tongtienhoadon() {
        return (double) (tinhTongTien() + tinhTongThue());
    }

    // In hóa đơn nhập ra màn hình (yêu cầu abstract của HoaDon)
    @Override
    public void inhoadon() {
        System.out.println(moTaDayDu());
    }

    private void kiemTraConSua() {
        if (trangThai != TrangThai.CHO_XAC_NHAN) {
            throw new IllegalStateException("Hóa đơn nhập " + getMaHD() + " đang ở trạng thái '" + trangThai.getNhan()
                    + "', không được sửa nữa.");
        }
    }

    // ===================== XÁC NHẬN / HỦY =====================

    // Xác nhận phiếu: mỗi dòng tạo một lô hàng mới trong sản phẩm tương ứng.
    // Bước 1: kiểm tra TẤT CẢ dòng (chưa đổi gì). Bước 2: mới tạo lô.

    public void xacNhan(IDanhSachSanPham kho) {
        if (kho == null) {
            throw new IllegalArgumentException("Cần cung cấp kho để xác nhận phiếu.");
        }
        if (trangThai != TrangThai.CHO_XAC_NHAN) {
            throw new IllegalStateException("Hóa đơn nhập " + getMaHD() + " đang ở trạng thái '" + trangThai.getNhan()
                    + "', không thể xác nhận (tránh tạo lô lặp).");
        }
        if (chiTiet.isEmpty()) {
            throw new IllegalStateException("Hóa đơn nhập " + getMaHD() + " chưa có dòng chi tiết nào.");
        }
        for (ChiTietPhieuNhap ct : chiTiet) {
            SanPham sp = kho.timTheoMa(ct.getMaSP());
            if (sp == null) {
                throw new IllegalArgumentException("Sản phẩm " + ct.getMaSP() + " không có trong kho, không thể xác nhận phiếu " + getMaHD() + ".");
            }
            sp.kiemTraCoTheNhapLo(ct.getHanSuDung(), ct.getSoLuongNhap());
        }
        for (ChiTietPhieuNhap ct : chiTiet) {
            LoHang lo = new LoHang(LoHang.maKeTiep(), ct.getMaSP(), getMaHD(), ngayNhap,
                    ct.getHanSuDung(), ct.getSoLuongNhap(), ct.getSoLuongNhap(), ct.getGiaNhap());
            kho.timTheoMa(ct.getMaSP()).themLo(lo);
        }
        trangThai = TrangThai.DA_XAC_NHAN;
    }

    // Hủy phiếu.
    // - Chờ xác nhận: chỉ đổi trạng thái (chưa có lô nào).
    // - Đã xác nhận: xóa các lô của phiếu khỏi kho. Chỉ được nếu MỌI lô còn nguyên chưa bị xuất;
    //   hàng đã bán mất thì từ chối. Kiểm tra hết rồi mới xóa.
    public void huy(IDanhSachSanPham kho) {
        if (trangThai == TrangThai.DA_HUY) {
            throw new IllegalStateException("Hóa đơn nhập " + getMaHD() + " đã bị hủy trước đó.");
        }
        if (trangThai == TrangThai.CHO_XAC_NHAN) {
            trangThai = TrangThai.DA_HUY;
            return;
        }
        if (kho == null) {
            throw new IllegalArgumentException("Cần cung cấp kho để hủy phiếu đã xác nhận.");
        }
        for (ChiTietPhieuNhap ct : chiTiet) {
            SanPham sp = kho.timTheoMa(ct.getMaSP());
            if (sp == null) {
                throw new IllegalStateException("Không thể hủy phiếu " + getMaHD() + ": sản phẩm "
                        + ct.getMaSP() + " không còn trong kho.");
            }
            LoHang lo = sp.timLoTheoHoaDonNhap(getMaHD());
            if (lo == null) {
                throw new IllegalStateException("Không thể hủy phiếu " + getMaHD() + ": không tìm thấy lô của "
                        + ct.getMaSP() + " (dữ liệu kho không khớp phiếu).");
            }
            if (lo.getSoLuongCon() != lo.getSoLuongNhap()) {
                throw new IllegalStateException("Không thể hủy phiếu " + getMaHD() + ": lô " + lo.getMaLo() + " của "
                        + ct.getMaSP() + " đã xuất " + (lo.getSoLuongNhap() - lo.getSoLuongCon())
                        + " trên " + lo.getSoLuongNhap() + " (hàng đã bán).");
            }
        }
        for (ChiTietPhieuNhap ct : chiTiet) {
            SanPham sp = kho.timTheoMa(ct.getMaSP());
            sp.xoaLo(sp.timLoTheoHoaDonNhap(getMaHD()).getMaLo());
        }
        trangThai = TrangThai.DA_HUY;
    }

    // ===================== GETTER =====================

    public String getMaHoaDon() {
        return getMaHD();
    }

    public String getMaNhanVien() {
        return getMaNV();
    }

    public LocalDateTime getThoiGianLap() {
        return getThoigianin();
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public String getMaNCC() {
        return maNhaCungCap;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    // Trả về bản sao, bên ngoài sửa danh sách này không ảnh hưởng phiếu
    public ArrayList<ChiTietPhieuNhap> getChiTiet() {
        return new ArrayList<ChiTietPhieuNhap>(chiTiet);
    }

    // ===================== FILE / HIỂN THỊ =====================

    // Dòng HDN|maHoaDon|maNV|thoiGianLap|ngayNhap|maNhaCungCap|trangThai rồi các dòng CT
    public ArrayList<String> toCacDongFile() {
        ArrayList<String> cacDong = new ArrayList<>();
        cacDong.add("HDN|" + getMaHD() + "|" + getMaNV() + "|" + getThoigianin() + "|" + ngayNhap
                + "|" + maNhaCungCap + "|" + trangThai.name());
        for (ChiTietPhieuNhap ct : chiTiet) {
            cacDong.add(ct.toDongFile());
        }
        return cacDong;
    }

    //(QuanlyKho truyền "mã - tên")
    public String moTaDayDu(String nhanNhaCungCap) {
        String ketQua = moTa(nhanNhaCungCap);
        for (ChiTietPhieuNhap ct : chiTiet) {
            ketQua += "\n    + " + ct;
        }
        return ketQua;
    }

    public String moTaDayDu() {
        return moTaDayDu(maNhaCungCap);
    }

    @Override
    public String toString() {
        return moTa(maNhaCungCap);
    }

    private String moTa(String nhanNhaCungCap) {
        return "Hóa đơn nhập " + getMaHD() + " | NV: " + getMaNV() + " | Ngày: " + TienIch.dinhDangNgay(ngayNhap)
                + " | NCC: " + nhanNhaCungCap
                + " | " + trangThai.getNhan()
                + " | " + chiTiet.size() + " dòng"
                + " | Tổng tiền (chưa thuế): " + TienIch.dinhDangTien(tinhTongTien());
    }
}
