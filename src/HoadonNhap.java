import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

// Hóa đơn nhập kho (thay PhieuNhap cũ), kế thừa HoaDon của nhóm: một lần nhập hàng từ một nhà cung cấp, gồm nhiều dòng CTHDonNhap.
// Trạng thái: CHO_XAC_NHAN -> DA_XAC_NHAN -> DA_HUY (hoặc CHO_XAC_NHAN -> DA_HUY).
// Hàng chỉ vào kho ở xacNhan(): mỗi dòng chi tiết tạo MỘT LÔ (LoHang) cho sản phẩm, mang hạn sử dụng và ngày nhập.
// xacNhan() chỉ chạy khi đang CHO_XAC_NHAN nên mỗi phiếu tạo lô đúng MỘT lần.
// huy() phiếu đã xác nhận thì xóa các lô của phiếu, và chỉ được khi các lô đó chưa bị xuất.
// Phần thừa hưởng từ HoaDon: mã hóa đơn (MaHD), mã nhân viên (MaNV), thời gian lập (thoigianin).
// Phần riêng của nhập kho: ngayNhap (ngày hàng về, dùng cho lô), mã nhà cung cấp, trạng thái, các dòng chi tiết.
// Nhà cung cấp: hóa đơn chỉ lưu MÃ nhà cung cấp; QuanlyKho kiểm tra mã đó có trong DSNhaCungCap khi tạo phiếu.
public class HoadonNhap extends HoaDon {

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
    private ArrayList<CTHDonNhap> chiTiet = new ArrayList<>();
    private TrangThai trangThai;

    // Tạo phiếu mới, ở trạng thái chờ xác nhận
    public HoadonNhap(String maHoaDon, String maNhanVien, LocalDate ngayNhap, String maNhaCungCap) {
        this(maHoaDon, maNhanVien, LocalDateTime.now(), ngayNhap, maNhaCungCap,
                TrangThai.CHO_XAC_NHAN, new ArrayList<CTHDonNhap>());
    }

    // Dùng khi đọc file: giữ nguyên trạng thái và các dòng chi tiết đã lưu, KHÔNG đụng tới tồn kho
    public HoadonNhap(String maHoaDon, String maNhanVien, LocalDateTime thoiGianLap, LocalDate ngayNhap,
                      String maNhaCungCap, TrangThai trangThai, ArrayList<CTHDonNhap> cacDong) {
        // HoaDon (nhóm) không có constructor có tham số: gán qua setter
        super();
        // Gọi super.setX (không phải setX của lớp này) vì HoadonNhap chặn việc đổi các giá trị này sau khi tạo
        super.setMaHD(SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maHoaDon, "Mã hóa đơn nhập")));
        super.setMaNV(SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maNhanVien, "Mã nhân viên")));
        super.setThoigianin(thoiGianLap);
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
        for (CTHDonNhap ct : cacDong) {
            kiemTraChiTiet(ct);
            chiTiet.add(ct);
        }
        boDem = Math.max(boDem, SanPham.laySoTuMa(getMaHD(), TIEN_TO_MA));
    }

    // Mã hóa đơn là khóa liên kết với các lô (LoHang.maHoaDonNhap) và với file lưu, nên không được đổi sau khi tạo.
    // HoaDon của nhóm có setter public; ở đây chặn để không làm đứt liên kết giữa phiếu và lô.
    @Override
    public void setMaHD(String maHD) {
        throw new UnsupportedOperationException("Không được đổi mã hóa đơn nhập " + getMaHD() + " sau khi tạo.");
    }

    @Override
    public void setMaNV(String maNV) {
        throw new UnsupportedOperationException("Không được đổi nhân viên lập hóa đơn nhập " + getMaHD() + " sau khi tạo.");
    }

    @Override
    public void setThoigianin(LocalDateTime thoigianin) {
        throw new UnsupportedOperationException("Không được đổi thời gian lập hóa đơn nhập " + getMaHD() + " sau khi tạo.");
    }

    public static String maKeTiep() {
        return String.format(Locale.ROOT, "%s%03d", TIEN_TO_MA, boDem + 1);
    }

    // Mỗi sản phẩm một dòng; hạn sử dụng không được trước ngày nhập (không nhập hàng đã hết hạn)
    private void kiemTraChiTiet(CTHDonNhap ct) {
        if (ct.getHanSuDung() != null && ct.getHanSuDung().isBefore(ngayNhap)) {
            throw new IllegalArgumentException("Dòng " + ct.getMaSP() + ": hạn sử dụng không được trước ngày nhập "
                    + SanPham.dinhDangNgay(ngayNhap) + ".");
        }
        if (daCoMa(ct.getMaSP())) {
            throw new IllegalArgumentException("Hóa đơn nhập " + getMaHD() + " đã có sản phẩm " + ct.getMaSP()
                    + " (mỗi sản phẩm chỉ một dòng, muốn đổi thì xóa dòng cũ rồi thêm lại).");
        }
    }

    // ===================== CHI TIẾT =====================

    public void themChiTiet(CTHDonNhap ct) {
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
        for (CTHDonNhap ct : chiTiet) {
            if (ct.getMaSP().equals(maChuan)) {
                return true;
            }
        }
        return false;
    }

    // Tổng tiền hàng của phiếu, CHƯA thuế (Kế toán tính thuế nhập từ thueSuatNhap của từng dòng)
    public long tinhTongTien() {
        long tong = 0;
        for (CTHDonNhap ct : chiTiet) {
            tong = Math.addExact(tong, ct.getThanhTien());
        }
        return tong;
    }

    // Tổng thuế nhập của cả hóa đơn (cộng thuế từng dòng)
    public long tinhTongThue() {
        long tong = 0;
        for (CTHDonNhap ct : chiTiet) {
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
    // Vì bước 1 đã kiểm tra đủ nên bước 2 không thể lỗi giữa chừng, không bao giờ cập nhật dở dang.
    public void xacNhan(DSSanPham kho) {
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
        for (CTHDonNhap ct : chiTiet) {
            SanPham sp = kho.timTheoMa(ct.getMaSP());
            if (sp == null) {
                throw new IllegalArgumentException("Sản phẩm " + ct.getMaSP() + " không có trong kho, không thể xác nhận phiếu " + getMaHD() + ".");
            }
            sp.kiemTraCoTheNhapLo(ct.getHanSuDung(), ct.getSoLuongNhap());
        }
        // Đã kiểm tra hết nên bước này không nên lỗi; vẫn phòng hờ: lỗi giữa chừng thì gỡ các lô vừa thêm
        ArrayList<LoHang> daThem = new ArrayList<>();
        try {
            for (CTHDonNhap ct : chiTiet) {
                LoHang lo = new LoHang(LoHang.maKeTiep(), ct.getMaSP(), getMaHD(), ngayNhap,
                        ct.getHanSuDung(), ct.getSoLuongNhap(), ct.getSoLuongNhap(), ct.getGiaNhap());
                kho.themLo(lo);
                daThem.add(lo);
            }
        } catch (RuntimeException loi) {
            for (LoHang lo : daThem) {
                kho.xoaLo(lo.getMaSP(), lo.getMaLo());
            }
            throw loi;
        }
        trangThai = TrangThai.DA_XAC_NHAN;
    }

    // Hủy phiếu.
    // - Chờ xác nhận: chỉ đổi trạng thái (chưa có lô nào).
    // - Đã xác nhận: xóa các lô của phiếu khỏi kho. Chỉ được nếu MỌI lô còn nguyên chưa bị xuất;
    //   hàng đã bán mất thì từ chối. Kiểm tra hết rồi mới xóa.
    public void huy(DSSanPham kho) {
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
        for (CTHDonNhap ct : chiTiet) {
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
        for (CTHDonNhap ct : chiTiet) {
            SanPham sp = kho.timTheoMa(ct.getMaSP());
            kho.xoaLo(ct.getMaSP(), sp.timLoTheoHoaDonNhap(getMaHD()).getMaLo());
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
    public ArrayList<CTHDonNhap> getChiTiet() {
        return new ArrayList<CTHDonNhap>(chiTiet);
    }

    // ===================== FILE / HIỂN THỊ =====================

    // Dòng HDN|maHoaDon|maNV|thoiGianLap|ngayNhap|maNhaCungCap|trangThai rồi các dòng CT
    public ArrayList<String> toCacDongFile() {
        ArrayList<String> cacDong = new ArrayList<>();
        cacDong.add("HDN|" + getMaHD() + "|" + getMaNV() + "|" + getThoigianin() + "|" + ngayNhap
                + "|" + maNhaCungCap + "|" + trangThai.name());
        for (CTHDonNhap ct : chiTiet) {
            cacDong.add(ct.toDongFile());
        }
        return cacDong;
    }

    // Mô tả đầy đủ; nhanNhaCungCap là chuỗi hiển thị cho nhà cung cấp (QuanlyKho truyền "mã - tên")
    public String moTaDayDu(String nhanNhaCungCap) {
        String ketQua = moTa(nhanNhaCungCap);
        for (CTHDonNhap ct : chiTiet) {
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
        return "Hóa đơn nhập " + getMaHD() + " | NV: " + getMaNV() + " | Ngày: " + SanPham.dinhDangNgay(ngayNhap)
                + " | NCC: " + nhanNhaCungCap
                + " | " + trangThai.getNhan()
                + " | " + chiTiet.size() + " dòng"
                + " | Tổng tiền (chưa thuế): " + SanPham.dinhDangTien(tinhTongTien());
    }
}
