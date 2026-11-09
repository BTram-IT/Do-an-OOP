//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Locale;
//
//// Hóa đơn xuất kho: ghi nhận hàng hỏng/hết hạn/không đủ điều kiện lưu thông bị loại khỏi kho. KHÔNG phải hóa đơn bán hàng.
//// Kế thừa HoaDon như HoadonNhap: mã hóa đơn, mã nhân viên lập, thời gian lập.
//// Trạng thái: DANG_XU_LY -> DA_XU_LY_THANH_CONG, hoặc DANG_XU_LY -> DA_HUY.
//// - Khi Đang xử lý: nhân viên thêm/xóa dòng; CHƯA trừ tồn kho.
//// - hoanTat(): trừ đúng lô của từng dòng (không trừ lô khác), làm kiểu "kiểm tra hết rồi mới ghi" và hoàn tác nếu lỗi.
//// - Sau khi thành công: khóa hóa đơn; gọi hoanTat() lần nữa sẽ bị từ chối nên không trừ kho hai lần.
//public class HDXuat extends HoaDon {
//
//    public enum TrangThai {
//        DANG_XU_LY("Đang xử lý"),
//        DA_XU_LY_THANH_CONG("Đã xử lý thành công"),
//        DA_HUY("Đã hủy");
//
//        private final String nhan;
//
//        TrangThai(String nhan) {
//            this.nhan = nhan;
//        }
//
//        public String getNhan() {
//            return nhan;
//        }
//    }
//
//    public static final String TIEN_TO_MA = "HDX";
//    private static int boDem = 0;
//
//    private final LocalDate ngayXuat;
//    private final ArrayList<CTHDXuat> chiTiet = new ArrayList<>();
//    private TrangThai trangThai;
//
//    // Tạo hóa đơn xuất mới (Đang xử lý)
//    public HDXuat(String maHoaDon, String maNhanVien, LocalDate ngayXuat) {
//        this(maHoaDon, maNhanVien, LocalDateTime.now(), ngayXuat, TrangThai.DANG_XU_LY, new ArrayList<CTHDXuat>());
//    }
//
//    // Dùng khi đọc file: giữ nguyên trạng thái, KHÔNG đụng tới tồn kho
//    public HDXuat(String maHoaDon, String maNhanVien, LocalDateTime thoiGianLap, LocalDate ngayXuat,
//                  TrangThai trangThai, ArrayList<CTHDXuat> cacDong) {
//        super();
//        super.setMaHD(SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maHoaDon, "Mã hóa đơn xuất")));
//        super.setMaNV(SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maNhanVien, "Mã nhân viên")));
//        if (thoiGianLap == null) {
//            throw new IllegalArgumentException("Thời gian lập hóa đơn không được null.");
//        }
//        super.setThoigianin(thoiGianLap);
//        if (ngayXuat == null) {
//            throw new IllegalArgumentException("Ngày xuất không được null.");
//        }
//        if (trangThai == null || cacDong == null) {
//            throw new IllegalArgumentException("Trạng thái và danh sách chi tiết không được null.");
//        }
//        this.ngayXuat = ngayXuat;
//        this.trangThai = trangThai;
//        for (CTHDXuat ct : cacDong) {
//            if (ct == null || daCoDong(ct.getMaSP(), ct.getMaLo())) {
//                throw new IllegalArgumentException("Chi tiết hóa đơn xuất bị null hoặc trùng sản phẩm/lô.");
//            }
//            ct.ganMaHD(getMaHD());
//            chiTiet.add(ct);
//        }
//        boDem = Math.max(boDem, SanPham.laySoTuMa(getMaHD(), TIEN_TO_MA));
//    }
//
//    public static String maKeTiep() {
//        return String.format(Locale.ROOT, "%s%03d", TIEN_TO_MA, boDem + 1);
//    }
//
//    // Mã, nhân viên, thời gian lập là khóa của hóa đơn: không đổi sau khi tạo
//    @Override
//    public void setMaHD(String maHD) {
//        throw new UnsupportedOperationException("Không được đổi mã hóa đơn xuất " + getMaHD() + " sau khi tạo.");
//    }
//
//    @Override
//    public void setMaNV(String maNV) {
//        throw new UnsupportedOperationException("Không được đổi nhân viên lập hóa đơn xuất " + getMaHD() + " sau khi tạo.");
//    }
//
//    @Override
//    public void setThoigianin(LocalDateTime thoigianin) {
//        throw new UnsupportedOperationException("Không được đổi thời gian lập hóa đơn xuất " + getMaHD() + " sau khi tạo.");
//    }
//
//    private void kiemTraConSua() {
//        if (trangThai != TrangThai.DANG_XU_LY) {
//            throw new IllegalStateException("Hóa đơn xuất " + getMaHD() + " đang ở trạng thái '" + trangThai.getNhan()
//                    + "', không được sửa nữa.");
//        }
//    }
//
//    private boolean daCoDong(String maSP, String maLo) {
//        String sp = SanPham.chuanHoaMa(maSP);
//        String lo = SanPham.chuanHoaMa(maLo);
//        for (CTHDXuat ct : chiTiet) {
//            if (ct.getMaSP().equals(sp) && ct.getMaLo().equals(lo)) return true;
//        }
//        return false;
//    }
//
//    // ===================== CHI TIẾT =====================
//
//    // Thêm một dòng: kiểm tra sản phẩm, lô và số lượng có thể xuất ngay lúc thêm (kiểm tra lại khi hoàn tất).
//    public void themChiTiet(DSSanPham kho, String maSP, String maLo, int soLuong, CTHDXuat.LyDo lyDo) {
//        kiemTraConSua();
//        if (kho == null) {
//            throw new IllegalArgumentException("Cần cung cấp kho để kiểm tra.");
//        }
//        SanPham sp = kho.layBatBuoc(maSP);
//        LoHang lo = sp.timLo(maLo);
//        if (lo == null) {
//            throw new IllegalArgumentException("Sản phẩm " + sp.getMaSP() + " không có lô " + maLo + ".");
//        }
//        if (daCoDong(sp.getMaSP(), lo.getMaLo())) {
//            throw new IllegalArgumentException("Hóa đơn " + getMaHD() + " đã có dòng cho lô " + lo.getMaLo()
//                    + " của " + sp.getMaSP() + " (mỗi lô một dòng, muốn đổi thì xóa dòng cũ rồi thêm lại).");
//        }
//        CTHDXuat ct = new CTHDXuat(sp.getMaSP(), sp.getTenSP(), lo.getMaLo(), soLuong, lyDo, lo.getGiaNhap());
//        kiemTraCoTheXuat(sp, lo, ct);
//        ct.ganMaHD(getMaHD());
//        chiTiet.add(ct);
//    }
//
//    private void kiemTraCoTheXuat(SanPham sp, LoHang lo, CTHDXuat ct) {
//        if (ct.getSoLuong() > lo.getSoLuongCon()) {
//            throw new IllegalStateException("Lô " + lo.getMaLo() + " của " + sp.getMaSP() + " chỉ còn " + lo.getSoLuongCon()
//                    + " " + sp.getDonViTinh() + ", không xuất được " + ct.getSoLuong() + ".");
//        }
//        if (ct.getLyDo() == CTHDXuat.LyDo.HET_HAN && !lo.isHetHan(ngayXuat)) {
//            throw new IllegalStateException("Lô " + lo.getMaLo() + " chưa hết hạn vào ngày " + SanPham.dinhDangNgay(ngayXuat)
//                    + " (HSD: " + SanPham.dinhDangNgay(lo.getHanSuDung()) + "), không thể xuất với lý do hết hạn.");
//        }
//    }
//
//    public boolean xoaChiTiet(String maSP, String maLo) {
//        kiemTraConSua();
//        String sp = SanPham.chuanHoaMa(maSP);
//        String lo = SanPham.chuanHoaMa(maLo);
//        for (int i = 0; i < chiTiet.size(); i++) {
//            if (chiTiet.get(i).getMaSP().equals(sp) && chiTiet.get(i).getMaLo().equals(lo)) {
//                chiTiet.remove(i);
//                return true;
//            }
//        }
//        return false;
//    }
//
//    // ===================== HOÀN TẤT / HỦY =====================
//
//    public void hoanTat(DSSanPham kho) {
//        if (kho == null) {
//            throw new IllegalArgumentException("Cần cung cấp kho để hoàn tất hóa đơn xuất.");
//        }
//        if (trangThai != TrangThai.DANG_XU_LY) {
//            throw new IllegalStateException("Hóa đơn xuất " + getMaHD() + " đang ở trạng thái '" + trangThai.getNhan()
//                    + "', không thể hoàn tất lần nữa (tránh trừ kho lặp).");
//        }
//        if (chiTiet.isEmpty()) {
//            throw new IllegalStateException("Hóa đơn xuất " + getMaHD() + " chưa có dòng chi tiết nào.");
//        }
//        // Bước 1: kiểm tra toàn bộ theo tồn kho thực tế lúc này
//        for (CTHDXuat ct : chiTiet) {
//            SanPham sp = kho.layBatBuoc(ct.getMaSP());
//            LoHang lo = sp.timLo(ct.getMaLo());
//            if (lo == null) {
//                throw new IllegalStateException("Sản phẩm " + ct.getMaSP() + " không còn lô " + ct.getMaLo() + ".");
//            }
//            kiemTraCoTheXuat(sp, lo, ct);
//        }
//        // Bước 2: trừ kho, hoàn tác nếu có lỗi giữa chừng
//        ArrayList<CTHDXuat> daTru = new ArrayList<>();
//        try {
//            for (CTHDXuat ct : chiTiet) {
//                kho.layBatBuoc(ct.getMaSP()).xuatTuLo(ct.getMaLo(), ct.getSoLuong());
//                daTru.add(ct);
//            }
//        } catch (RuntimeException loi) {
//            for (CTHDXuat ct : daTru) {
//                kho.layBatBuoc(ct.getMaSP()).hoanVaoLo(ct.getMaLo(), ct.getSoLuong());
//            }
//            throw loi;
//        }
//        trangThai = TrangThai.DA_XU_LY_THANH_CONG;
//    }
//
//    // Chỉ hủy được khi còn Đang xử lý (chưa trừ kho)
//    public void huy() {
//        kiemTraConSua();
//        trangThai = TrangThai.DA_HUY;
//    }
//
//    // ===================== GETTER / HIỂN THỊ / FILE =====================
//
//    public LocalDate getNgayXuat() { return ngayXuat; }
//    public TrangThai getTrangThai() { return trangThai; }
//    public ArrayList<CTHDXuat> getChiTiet() { return new ArrayList<>(chiTiet); }
//
//    // Tổng giá trị hàng bị loại (theo giá nhập của lô)
//    @Override
//    public long Tongtienhoadon() {
//        long tong = 0;
//        for (CTHDXuat ct : chiTiet) {
//            tong = Math.addExact(tong, ct.getGiaTri());
//        }
//        return tong;
//    }
//
//    @Override
//    public void inhoadon() {
//        System.out.println(moTaDayDu());
//    }
//
//    public String moTaDayDu() {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Hóa đơn xuất ").append(getMaHD())
//                .append(" | NV: ").append(getMaNV())
//                .append(" | Ngày xuất: ").append(SanPham.dinhDangNgay(ngayXuat))
//                .append(" | ").append(trangThai.getNhan())
//                .append(" | ").append(chiTiet.size()).append(" dòng")
//                .append(" | Giá trị hàng loại bỏ: ").append(SanPham.dinhDangTien(Tongtienhoadon()));
//        for (CTHDXuat ct : chiTiet) {
//            sb.append("\n    + ").append(ct);
//        }
//        return sb.toString();
//    }
//
//    // HDX|maHD|maNV|thoiGianLap|ngayXuat|trangThai  rồi tới các dòng CX|...
//    public ArrayList<String> toCacDongFile() {
//        ArrayList<String> cacDong = new ArrayList<>();
//        cacDong.add("HDX|" + getMaHD() + "|" + getMaNV() + "|" + getThoigianin() + "|" + ngayXuat + "|" + trangThai.name());
//        for (CTHDXuat ct : chiTiet) {
//            cacDong.add(ct.toDongFile());
//        }
//        return cacDong;
//    }
//
//    @Override
//    public String toString() {
//        return moTaDayDu();
//    }
//}
