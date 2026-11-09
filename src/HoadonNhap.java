import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class HoadonNhap extends HoaDon {

    @Override
    public void setmaHoaDon(String maHD) {

    }

    @Override
    public String getmaHoaDon() {

        return null;
    }

    @Override
    public void thanhTien() {

    }

    @Override
    public void hienThiHoaDon() {

    }

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
    private String maDonViVanChuyen;
    private ArrayList<CTHDonNhap> chiTiet = new ArrayList<>();
    private TrangThai trangThai;

    // Nhập thông tin tạo hóa đơn nhập
    public static HoadonNhap Nhap(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner khong hop le!");
        }

        String maHD = HoaDon.nhapMaHD(sc);
        String maNV = HoaDon.nhapMaNV(sc);

        LocalDate ngayNhap;
        while (true) {
            String ngay = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhap ngay nhap (yyyy-MM-dd): ",
                    "^\\d{4}-\\d{2}-\\d{2}$",
                    "Ngay nhap phai co dang yyyy-MM-dd!"
            );

            try {
                ngayNhap = LocalDate.parse(ngay);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ngay nhap khong hop le!");
            }
        }

        String maNCC = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma nha cung cap: ",
                ".+",
                "Ma nha cung cap khong duoc de trong!"
        );

        String maDVVC = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma don vi van chuyen: ",
                ".+",
                "Ma don vi van chuyen khong duoc de trong!"
        );

        return new HoadonNhap(maHD, maNV, ngayNhap, maNCC, maDVVC);
    }

    // Tạo phiếu mới, ở trạng thái chờ xác nhận
    public HoadonNhap(String maHoaDon, String maNhanVien, LocalDate ngayNhap, String maNhaCungCap) {
        this(maHoaDon, maNhanVien, LocalDateTime.now(), ngayNhap, maNhaCungCap,
                TrangThai.CHO_XAC_NHAN, new ArrayList<CTHDonNhap>());
    }

    // Dùng khi đọc file: giữ nguyên trạng thái và các dòng chi tiết đã lưu, KHÔNG đụng tới tồn kho
    public HoadonNhap(String maHoaDon, String maNhanVien, LocalDateTime thoiGianLap, LocalDate ngayNhap,
                      String maNhaCungCap, TrangThai trangThai, ArrayList<CTHDonNhap> cacDong) {
        this(maHoaDon, maNhanVien, thoiGianLap, ngayNhap, maNhaCungCap, "", trangThai, cacDong);
    }

    // Dùng khi đọc file có mã đơn vị vận chuyển
    public HoadonNhap(String maHoaDon, String maNhanVien, LocalDateTime thoiGianLap, LocalDate ngayNhap,
                      String maNhaCungCap, String maDonViVanChuyen, TrangThai trangThai, ArrayList<CTHDonNhap> cacDong) {
        // HoaDon (nhóm) không có constructor có tham số: gán qua setter
        super();
        // Gọi super.setX (không phải setX của lớp này) vì HoadonNhap chặn việc đổi các giá trị này sau khi tạo
        super.setmaHoaDon(SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maHoaDon, "Mã hóa đơn nhập")));
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
        this.maDonViVanChuyen = (maDonViVanChuyen != null && !maDonViVanChuyen.trim().isEmpty())
                ? SanPham.chuanHoaMa(maDonViVanChuyen) : "";
        if (trangThai == null) {
            throw new IllegalArgumentException("Trạng thái phiếu không được null.");
        }
        this.trangThai = trangThai;

        if (cacDong == null) {
            throw new IllegalArgumentException("Danh sách chi tiết không được null.");
        }

        for (CTHDonNhap ct : cacDong) {
            if (ct == null) {
                throw new IllegalArgumentException("Chi tiết hóa đơn nhập không được null.");
            }
            kiemTraChiTiet(ct);
            ct.ganMaHD(getMaHD());
            chiTiet.add(ct);
        }
        boDem = Math.max(boDem, SanPham.laySoTuMa(getMaHD(), TIEN_TO_MA));
    }

    // Tạo phiếu mới có mã đơn vị vận chuyển
    public HoadonNhap(String maHoaDon, String maNhanVien, LocalDate ngayNhap,
                      String maNhaCungCap, String maDonViVanChuyen) {
        this(maHoaDon, maNhanVien, ngayNhap, maNhaCungCap);

        this.maDonViVanChuyen = SanPham.chuanHoaMa(
                SanPham.kiemTraVanBan(maDonViVanChuyen, "Mã đơn vị vận chuyển")
        );
    }

    // Mã hóa đơn là khóa liên kết với các lô (LoHang.maHoaDonNhap) và với file lưu, nên không được đổi sau khi tạo.
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
        if (ct == null) {
            throw new IllegalArgumentException("Chi tiết hóa đơn nhập không được null.");
        }

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
        ct.ganMaHD(getMaHD());
        chiTiet.add(ct);
    }

    public void themChiTiet(SanPham sp, int soLuong, double giaNhap) {
        if (sp == null) throw new IllegalArgumentException("Sản phẩm không được null.");
        themChiTiet(new CTHDonNhap(sp.getMaSP(), sp.getTenSP(), soLuong, (long) giaNhap, sp.getThueSuat(), null));
    }

    public long tongTienDS() {
        return TongtienDS();
    }

    public void capNhatKho(DSSanPham kho) {
        xacNhan(kho);
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

    // Tổng tiền danh sách chi tiết hóa đơn nhập
    public long TongtienDS() {
        long tong = 0;
        for (CTHDonNhap ct : chiTiet) {
            tong = Math.addExact(tong, ct.Tongtien());
        }
        return tong;
    }

    // Tổng thanh toán cho nhà cung cấp = tiền hàng + thuế nhập.
    @Override
    public long Tongtienhoadon() {
        return (long) tinhTongTien() + (long) tinhTongThue();
    }

    // In hóa đơn nhập ra màn hình (yêu cầu abstract của HoaDon)
    @Override
    public void inhoadon() {
        Inhoadon();
    }

    // In đầy đủ hóa đơn nhập
    public void Inhoadon() {
        System.out.println("========== HOA DON NHAP ==========");
        System.out.println("Ma hoa don: " + getMaHD());
        System.out.println("Ma nhan vien: " + getMaNV());
        System.out.println("Ngay lap: " + getThoigianin());
        System.out.println("Ngay nhap: " + SanPham.dinhDangNgay(ngayNhap));
        System.out.println("Ma nha cung cap: " + maNhaCungCap);
        System.out.println("Ma don vi van chuyen: " + maDonViVanChuyen);
        System.out.println("Trang thai: " + trangThai.getNhan());
        System.out.println("----------------------------------");

        for (CTHDonNhap ct : chiTiet) {
            System.out.println(ct);
        }

        System.out.println("----------------------------------");
        System.out.println("Tong tien hang: " + SanPham.dinhDangTien(TongtienDS()));
        System.out.println("Tong thue nhap: " + SanPham.dinhDangTien(tinhTongThue()));
        System.out.println("Tong thanh toan: " + SanPham.dinhDangTien((long) Tongtienhoadon()));
        System.out.println("==================================");
    }

    private void kiemTraConSua() {
        if (trangThai != TrangThai.CHO_XAC_NHAN) {
            throw new IllegalStateException("Hóa đơn nhập " + getMaHD() + " đang ở trạng thái '" + trangThai.getNhan()
                    + "', không được sửa nữa.");
        }
    }

    // ===================== XÁC NHẬN / HỦY =====================

    // Xác nhận phiếu: mỗi dòng tạo một lô hàng mới trong sản phẩm tương ứng.
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

    public String getMaDVVC() {
        return maDonViVanChuyen;
    }

    public TrangThai getTrangThai() {
        return trangThai;
    }

    public ArrayList<CTHDonNhap> getChiTiet() {
        return new ArrayList<CTHDonNhap>(chiTiet);
    }

    // ===================== FILE / HIỂN THỊ =====================

    public ArrayList<String> toCacDongFile() {
        ArrayList<String> cacDong = new ArrayList<>();
        cacDong.add("HDN|" + getMaHD() + "|" + getMaNV() + "|" + getThoigianin() + "|" + ngayNhap
                + "|" + maNhaCungCap + "|" + trangThai.name() + "|" + (maDonViVanChuyen != null ? maDonViVanChuyen : ""));
        for (CTHDonNhap ct : chiTiet) {
            cacDong.add(ct.toDongFile());
        }
        return cacDong;
    }

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
        return moTa(maNhaCungCap)
                + " | DVVC: " + maDonViVanChuyen
                + " | Tổng thanh toán: " + SanPham.dinhDangTien((long) Tongtienhoadon());
    }

    private String moTa(String nhanNhaCungCap) {
        return "Hóa đơn nhập " + getMaHD() + " | NV: " + getMaNV() + " | Ngày: " + SanPham.dinhDangNgay(ngayNhap)
                + " | NCC: " + nhanNhaCungCap
                + " | " + trangThai.getNhan()
                + " | " + chiTiet.size() + " dòng"
                + " | Tổng tiền (chưa thuế): " + SanPham.dinhDangTien(tinhTongTien());
    }
}