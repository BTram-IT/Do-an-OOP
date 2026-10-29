import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class QuanlyKho {

    public static final int NGUONG_SAP_HET_HANG = 10;
    public static final int SO_NGAY_CANH_BAO_HET_HAN = 7;
    public static final String THU_MUC_MAC_DINH = "du_lieu_kho";
    public static final String TEN_FILE_SAN_PHAM = "san_pham.txt";
    public static final String TEN_FILE_HOA_DON_NHAP = "hoa_don_nhap.txt";

    private IDanhSachSanPham dsSanPham;
    private ArrayList<HoadonNhap> dsHoaDonNhap = new ArrayList<>();
    private DSNhaCungCap dsNhaCungCap;

    public QuanlyKho(DSNhaCungCap dsNhaCungCap) {
        this(new DSSanPham(), dsNhaCungCap);
    }

    // Truyền danh sách muốn dùng: new DSSanPham() (mảng động) hoặc new DSSanPhamTinh(50) (mảng tĩnh).
    // KeToan đang giữ một DSSanPham riêng, nên truyền chính DSSanPham đó vào đây để hai nơi dùng chung dữ liệu.
    // dsNhaCungCap là danh sách nhà cung cấp của Quản lý cửa hàng (cùng một đối tượng, không sao chép).
    public QuanlyKho(IDanhSachSanPham dsSanPham, DSNhaCungCap dsNhaCungCap) {
        if (dsSanPham == null) {
            throw new IllegalArgumentException("Danh sách sản phẩm không được null.");
        }
        if (dsNhaCungCap == null) {
            throw new IllegalArgumentException("Danh sách nhà cung cấp không được null.");
        }
        this.dsSanPham = dsSanPham;
        this.dsNhaCungCap = dsNhaCungCap;
    }

    // ===================== NHÀ CUNG CẤP =====================

    // Trả về nhà cung cấp trong danh sách của cửa hàng, không có thì null
    public NhaCungCap layNhaCungCap(String ma) {
        return dsNhaCungCap.timTheoMa(ma);
    }

    public boolean coNhaCungCap(String ma) {
        return dsNhaCungCap.tonTai(ma);
    }

    // Chuỗi hiển thị "mã - tên". Nhà cung cấp đã bị xóa khỏi danh sách thì phiếu cũ vẫn in được mã.
    public String nhanNhaCungCap(String ma) {
        NhaCungCap ncc = dsNhaCungCap.timTheoMa(ma);
        if (ncc == null) {
            return ma + " (không còn trong danh sách nhà cung cấp)";
        }
        return ncc.getMa() + " - " + ncc.getTen();
    }

    // ===================== QUẢN LÝ SẢN PHẨM =====================

    public void themSanPham(SanPham sp) {
        dsSanPham.them(sp);
    }

    public int themNhieuSanPham(SanPham[] dsMoi) {
        return dsSanPham.themNhieu(dsMoi);
    }

    // Sửa tên, giá bán, thuế suất bán và thông tin riêng. Không đổi được mã, loại, đơn vị tính; các lô giữ nguyên.
    public void suaSanPham(String ma, SanPham thongTinMoi) {
        dsSanPham.sua(ma, thongTinMoi);
    }

    // Không xóa nếu sản phẩm còn tồn kho hoặc đã có trong BẤT KỲ phiếu nhập nào (để giữ lịch sử nhập hàng)
    public void xoaSanPham(String ma) {
        String maChuan = SanPham.chuanHoaMa(ma);
        if (!dsSanPham.tonTaiMa(maChuan)) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + ma);
        }
        for (HoadonNhap phieu : dsHoaDonNhap) {
            if (phieu.coSanPham(maChuan)) {
                throw new IllegalStateException("Không thể xóa " + maChuan + ": sản phẩm đã có trong phiếu nhập "
                        + phieu.getMaHoaDon() + " (cần giữ lịch sử nhập hàng).");
            }
        }
        dsSanPham.xoaTheoMa(maChuan);
    }

    // ===================== TRA CỨU =====================

    // Trả về sản phẩm hoặc null nếu không có
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

    public ArrayList<SanPham> timNhieuTieuChi(String tuKhoaTen, String loai, long giaTu, long giaDen) {
        return dsSanPham.timNhieuTieuChi(tuKhoaTen, loai, giaTu, giaDen);
    }

    public int soSanPham() {
        return dsSanPham.soLuong();
    }

    public void xuatDanhSachSanPham() {
        dsSanPham.xuat();
    }

    // ===================== LÔ HÀNG =====================

    // Bản sao danh sách lô của sản phẩm (lỗi nếu mã không tồn tại)
    public ArrayList<LoHang> layCacLo(String maSP) {
        SanPham sp = dsSanPham.timTheoMa(maSP);
        if (sp == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + maSP);
        }
        return sp.getCacLo();
    }

    public String moTaCacLo(String maSP) {
        SanPham sp = dsSanPham.timTheoMa(maSP);
        if (sp == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + maSP);
        }
        return sp.moTaCacLo();
    }

    // Các lô đã hết hạn mà còn hàng
    public ArrayList<LoHang> layLoHetHan(LocalDate ngay) {
        return dsSanPham.layLoHetHan(ngay);
    }

    // Các lô còn hạn nhưng hết hạn trong soNgay ngày tới
    public ArrayList<LoHang> layLoSapHetHan(LocalDate ngay, int soNgay) {
        return dsSanPham.layLoSapHetHan(ngay, soNgay);
    }

    public String baoCaoHanSuDung(LocalDate ngay, int soNgay) {
        ArrayList<LoHang> hetHan = layLoHetHan(ngay);
        ArrayList<LoHang> sapHet = layLoSapHetHan(ngay, soNgay);
        String ketQua = "===== HẠN SỬ DỤNG THEO LÔ (tính đến " + TienIch.dinhDangNgay(ngay) + ") =====\n";
        ketQua += "--- Lô ĐÃ HẾT HẠN, còn hàng: " + hetHan.size() + " ---\n";
        for (LoHang lo : hetHan) {
            ketQua += "  " + tenSanPham(lo.getMaSP()) + " | " + lo + "\n";
        }
        ketQua += "--- Lô sắp hết hạn trong " + soNgay + " ngày: " + sapHet.size() + " ---\n";
        for (LoHang lo : sapHet) {
            ketQua += "  " + tenSanPham(lo.getMaSP()) + " | " + lo + "\n";
        }
        return ketQua;
    }

    private String tenSanPham(String maSP) {
        SanPham sp = dsSanPham.timTheoMa(maSP);
        if (sp == null) {
            return maSP;
        }
        return sp.getTenSP();
    }

    // ===================== TỒN KHO VÀ XUẤT KHO (DÀNH CHO THU NGÂN) =====================

    // Tổng tồn của sản phẩm (cộng mọi lô, kể cả lô hết hạn). Lỗi nếu mã không tồn tại.
    public int kiemTraTon(String maSP) {
        return dsSanPham.layTon(maSP);
    }

    // Số lượng CÒN HẠN, bán được hôm nay
    public int kiemTraTonBanDuoc(String maSP) {
        return dsSanPham.layTonBanDuoc(maSP, LocalDate.now());
    }

    // Còn đủ soLuong hàng còn hạn để bán hôm nay không. Lỗi nếu mã không tồn tại.
    public boolean duTon(String maSP, int soLuong) {
        return dsSanPham.duTon(maSP, soLuong);
    }

    // Xuất một mặt hàng theo FEFO vào hôm nay. Lỗi và KHÔNG đổi gì nếu không đủ hàng còn hạn.
    // Trả về các lô đã lấy: hãy GIỮ lại để hoàn kho đúng lô khi hủy đơn.
    public ArrayList<LoXuat> xuatKho(String maSP, int soLuong) {
        return xuatKho(maSP, soLuong, LocalDate.now());
    }

    // Bản có ngày để kiểm thử hoặc xuất theo ngày khác hôm nay
    public ArrayList<LoXuat> xuatKho(String maSP, int soLuong, LocalDate ngay) {
        return dsSanPham.xuatFEFO(maSP, soLuong, ngay);
    }

    public ArrayList<LoXuat> xuatKhoNhieu(String[] maSP, int[] soLuong) {
        return xuatKhoNhieu(maSP, soLuong, LocalDate.now());
    }

    // Xuất cả một đơn hàng: maSP[i] bán soLuong[i]. Được tất cả hoặc không được gì.
    // Một mã xuất hiện nhiều lần thì được cộng dồn số lượng. Trả về mọi lô đã lấy của cả đơn.
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
        // Bước 1: kiểm tra đủ hàng CÒN HẠN cho mọi mã, chưa lấy gì
        for (int i = 0; i < maSP.length; i++) {
            if (daGapMaTruocDo(maSP, i)) {
                continue;
            }
            long tong = tongSoLuongCuaMa(maSP, soLuong, i);
            if (tong > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Số lượng xuất của " + maSP[i] + " quá lớn.");
            }
            if (!dsSanPham.duTon(maSP[i], (int) tong, ngay)) {
                throw new IllegalStateException("Không đủ hàng còn hạn cho " + SanPham.chuanHoaMa(maSP[i]) + ": cần "
                        + tong + ", còn hạn " + dsSanPham.layTonBanDuoc(maSP[i], ngay) + ".");
            }
        }
        // Bước 2: đã kiểm tra hết nên lấy hàng không thể lỗi giữa chừng
        ArrayList<LoXuat> ketQua = new ArrayList<>();
        for (int i = 0; i < maSP.length; i++) {
            if (daGapMaTruocDo(maSP, i)) {
                continue;
            }
            ketQua.addAll(dsSanPham.xuatFEFO(maSP[i], (int) tongSoLuongCuaMa(maSP, soLuong, i), ngay));
        }
        return ketQua;
    }

    private boolean daGapMaTruocDo(String[] maSP, int viTri) {
        for (int j = 0; j < viTri; j++) {
            if (SanPham.chuanHoaMa(maSP[j]).equals(SanPham.chuanHoaMa(maSP[viTri]))) {
                return true;
            }
        }
        return false;
    }

    private long tongSoLuongCuaMa(String[] maSP, int[] soLuong, int viTri) {
        long tong = 0;
        for (int j = 0; j < maSP.length; j++) {
            if (SanPham.chuanHoaMa(maSP[j]).equals(SanPham.chuanHoaMa(maSP[viTri]))) {
                tong += soLuong[j];
            }
        }
        return tong;
    }

    // Hoàn một khoản về đúng lô (hủy đơn, khách trả hàng...). Không hoàn vượt số đã nhập vào lô.
    public void hoanKho(String maSP, String maLo, int soLuong) {
        dsSanPham.hoanLo(maSP, maLo, soLuong);
    }

    // Hoàn cả một đơn: truyền đúng danh sách LoXuat mà xuatKho/xuatKhoNhieu đã trả về.
    // Được tất cả hoặc không được gì.
    public void hoanKho(ArrayList<LoXuat> cacLoDaXuat) {
        if (cacLoDaXuat == null || cacLoDaXuat.isEmpty()) {
            throw new IllegalArgumentException("Danh sách cần hoàn không được rỗng.");
        }
        // Bước 1: kiểm tra từng lô (cộng dồn nếu cùng một lô xuất hiện nhiều lần), chưa hoàn gì
        for (int i = 0; i < cacLoDaXuat.size(); i++) {
            if (daGapLoTruocDo(cacLoDaXuat, i)) {
                continue;
            }
            LoXuat x = cacLoDaXuat.get(i);
            SanPham sp = dsSanPham.timTheoMa(x.getMaSP());
            if (sp == null) {
                throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + x.getMaSP());
            }
            sp.kiemTraCoTheHoanVaoLo(x.getMaLo(), tongSoLuongCuaLo(cacLoDaXuat, i));
        }
        // Bước 2: hoàn
        for (int i = 0; i < cacLoDaXuat.size(); i++) {
            if (daGapLoTruocDo(cacLoDaXuat, i)) {
                continue;
            }
            LoXuat x = cacLoDaXuat.get(i);
            dsSanPham.hoanLo(x.getMaSP(), x.getMaLo(), tongSoLuongCuaLo(cacLoDaXuat, i));
        }
    }

    private boolean daGapLoTruocDo(ArrayList<LoXuat> ds, int viTri) {
        for (int j = 0; j < viTri; j++) {
            if (ds.get(j).getMaSP().equals(ds.get(viTri).getMaSP())
                    && ds.get(j).getMaLo().equals(ds.get(viTri).getMaLo())) {
                return true;
            }
        }
        return false;
    }

    private int tongSoLuongCuaLo(ArrayList<LoXuat> ds, int viTri) {
        long tong = 0;
        for (LoXuat x : ds) {
            if (x.getMaSP().equals(ds.get(viTri).getMaSP()) && x.getMaLo().equals(ds.get(viTri).getMaLo())) {
                tong += x.getSoLuong();
            }
        }
        if (tong > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Số lượng hoàn của lô " + ds.get(viTri).getMaLo() + " quá lớn.");
        }
        return (int) tong;
    }

    // ===================== PHIẾU NHẬP =====================

    // Tạo hóa đơn nhập mới (chờ xác nhận) với mã tự sinh, ví dụ HDN001; maNhanVien là nhân viên lập hóa đơn.
    // Nhà cung cấp phải CÓ TRONG DSNhaCungCap; phiếu lưu mã của nhà cung cấp đó. Ngày nhập không được ở tương lai.
    public HoadonNhap taoHoaDonNhap(String maNhanVien, String maNhaCungCap, LocalDate ngayNhap) {
        NhaCungCap ncc = dsNhaCungCap.timTheoMa(maNhaCungCap);
        if (ncc == null) {
            throw new IllegalArgumentException("Nhà cung cấp '" + maNhaCungCap
                    + "' không tồn tại trong danh sách nhà cung cấp của cửa hàng.");
        }
        if (ngayNhap == null) {
            throw new IllegalArgumentException("Ngày nhập không được null.");
        }
        if (ngayNhap.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nhập không được ở tương lai.");
        }
        HoadonNhap phieu = new HoadonNhap(HoadonNhap.maKeTiep(), maNhanVien, ngayNhap, ncc.getMa());
        dsHoaDonNhap.add(phieu);
        return phieu;
    }

    // Thêm một dòng vào phiếu: dòng này sẽ thành MỘT LÔ khi phiếu được xác nhận.
    // hanSuDung là hạn của lô này: thức ăn bắt buộc có, đồ uống có thể để null (không hạn).
    // giaNhap = null: lấy giá nhập tham khảo của SanPham làm giá dòng này (từ chối nếu sản phẩm chưa có, tức 0).
    // Giá dòng này là giá THỰC TẾ của lần nhập; không làm đổi SanPham.giaNhap.
    public void themChiTietPhieu(String maPhieu, String maSP, int soLuong, Long giaNhap,
                                 double thueSuatNhap, LocalDate hanSuDung) {
        if (giaNhap == null) {
            SanPham spMacDinh = dsSanPham.timTheoMa(maSP);
            if (spMacDinh != null && spMacDinh.getGiaNhap() == 0) {
                throw new IllegalArgumentException("Sản phẩm " + maSP + " chưa có giá nhập tham khảo, hãy nhập giá nhập cho dòng này.");
            }
            giaNhap = spMacDinh == null ? null : Long.valueOf(spMacDinh.getGiaNhap());
        }
        themChiTietPhieu(maPhieu, maSP, soLuong, giaNhap == null ? 0L : giaNhap.longValue(), thueSuatNhap, hanSuDung);
    }

    public void themChiTietPhieu(String maPhieu, String maSP, int soLuong, long giaNhap,
                                 double thueSuatNhap, LocalDate hanSuDung) {
        HoadonNhap phieu = layPhieu(maPhieu);
        SanPham sp = dsSanPham.timTheoMa(maSP);
        if (sp == null) {
            throw new IllegalArgumentException("Sản phẩm " + maSP + " chưa có trong danh mục, hãy thêm sản phẩm trước khi nhập hàng.");
        }
        sp.kiemTraCoTheNhapLo(hanSuDung, soLuong);
        phieu.themChiTiet(new CTHDonNhap(sp.getMaSP(), sp.getTenSP(), soLuong, giaNhap, thueSuatNhap, hanSuDung));
    }

    public void xacNhanHoaDonNhap(String maPhieu) {
        layPhieu(maPhieu).xacNhan(dsSanPham);
    }

    public void huyHoaDonNhap(String maPhieu) {
        layPhieu(maPhieu).huy(dsSanPham);
    }

    // Trả về phiếu hoặc null nếu không có
    public HoadonNhap timHoaDonNhap(String maPhieu) {
        String maChuan = SanPham.chuanHoaMa(maPhieu);
        for (HoadonNhap phieu : dsHoaDonNhap) {
            if (phieu.getMaHoaDon().equals(maChuan)) {
                return phieu;
            }
        }
        return null;
    }

    private HoadonNhap layPhieu(String maPhieu) {
        HoadonNhap phieu = timHoaDonNhap(maPhieu);
        if (phieu == null) {
            throw new IllegalArgumentException("Không tìm thấy phiếu nhập có mã: " + maPhieu);
        }
        return phieu;
    }

    // Bản sao danh sách phiếu nhập (Kế toán dùng để lấy tổng tiền, thuế suất nhập, ngày, nhà cung cấp)
    public ArrayList<HoadonNhap> layDanhSachHoaDonNhap() {
        return new ArrayList<HoadonNhap>(dsHoaDonNhap);
    }

    // Giá vốn của một lần xuất (kết quả xuatKho/xuatKhoNhieu): tổng soLuong x giaNhap THỰC TẾ của từng lô đã xuất.
    // Dùng giá của lô (không phải SanPham.giaNhap tham khảo). Lô đã bị xóa thì báo lỗi.
    public long tinhGiaVon(ArrayList<LoXuat> cacLoXuat) {
        long tong = 0;
        for (LoXuat lx : cacLoXuat) {
            SanPham sp = dsSanPham.timTheoMa(lx.getMaSP());
            LoHang lo = sp == null ? null : sp.timLo(lx.getMaLo());
            if (lo == null) {
                throw new IllegalArgumentException("Không tìm thấy lô " + lx.getMaLo() + " của " + lx.getMaSP() + " để tính giá vốn.");
            }
            tong = Math.addExact(tong, Math.multiplyExact((long) lx.getSoLuong(), lo.getGiaNhap()));
        }
        return tong;
    }

    // ===================== THỐNG KÊ =====================

    public String baoCaoTonKho() {
        return dsSanPham.baoCaoThongKe();
    }

    public ArrayList<SanPham> layHangSapHet() {
        return dsSanPham.layHangSapHet(NGUONG_SAP_HET_HANG);
    }

    public ArrayList<SanPham> layHangHetHang() {
        return dsSanPham.layHangHetHang();
    }

    // Sản phẩm có lô hết hạn mà còn hàng
    public ArrayList<SanPham> layHangHetHan(LocalDate ngay) {
        return dsSanPham.layHangHetHan(ngay);
    }

    // Tổng tiền hàng (chưa thuế) của các phiếu ĐÃ XÁC NHẬN có ngày nhập trong [tuNgay, denNgay]
    public long tongTienNhap(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        long tong = 0;
        for (HoadonNhap phieu : dsHoaDonNhap) {
            if (trongKhoang(phieu, tuNgay, denNgay)) {
                tong += phieu.tinhTongTien();
            }
        }
        return tong;
    }

    public String baoCaoNhapKho(LocalDate tuNgay, LocalDate denNgay) {
        kiemTraKhoangNgay(tuNgay, denNgay);
        String ketQua = "===== NHẬP KHO TỪ " + TienIch.dinhDangNgay(tuNgay) + " ĐẾN " + TienIch.dinhDangNgay(denNgay)
                + " (phiếu đã xác nhận) =====\n";
        int soPhieu = 0;
        for (HoadonNhap phieu : dsHoaDonNhap) {
            if (trongKhoang(phieu, tuNgay, denNgay)) {
                ketQua += phieu.moTaDayDu(nhanNhaCungCap(phieu.getMaNCC())) + "\n";
                soPhieu++;
            }
        }
        ketQua += "Số phiếu: " + soPhieu + " | Tổng tiền nhập (chưa thuế): "
                + TienIch.dinhDangTien(tongTienNhap(tuNgay, denNgay)) + "\n";
        return ketQua;
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

    // ===================== DỮ LIỆU MẪU =====================

    // Thêm vài sản phẩm mẫu (chưa có lô, tồn = 0; hàng vào kho qua phiếu nhập). Thuế suất 5 và 10 chỉ là SỐ GIẢ ĐỊNH để minh họa.
    public void nhapDuLieuMau() {
        themSanPham(new ThucAn(ThucAn.maKeTiep(), "Bánh mì thịt", 12000, 20000, "cái", 5, "Tiệm Hoa"));
        themSanPham(new ThucAn(ThucAn.maKeTiep(), "Xúc xích nướng", 9000, 15000, "cái", 5, "CP Việt Nam"));
        themSanPham(new DoUong(DoUong.maKeTiep(), "Nước ngọt Cola", 7000, 12000, "chai", 10, 330));
        themSanPham(new DoUong(DoUong.maKeTiep(), "Nước suối", 3000, 6000, "chai", 0, 500));
    }

    // ===================== LƯU / ĐỌC FILE =====================

    // Lưu vào thư mục: san_pham.txt (sản phẩm và các lô của chúng, do danh sách tự ghi) và hoa_don_nhap.txt.
    // Danh sách nhà cung cấp thuộc Quản lý cửa hàng nên không lưu ở đây.
    public void luuDuLieu(String thuMuc) throws IOException {
        File dir = new File(thuMuc);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Không tạo được thư mục: " + thuMuc);
        }
        dsSanPham.ghiFile(new File(dir, TEN_FILE_SAN_PHAM).getPath());

        try (BufferedWriter ghi = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(new File(dir, TEN_FILE_HOA_DON_NHAP)), StandardCharsets.UTF_8))) {
            ghi.write(TienIch.DONG_PHIEN_BAN);
            ghi.newLine();
            ghi.write("# HDN|maHoaDon|maNV|thoiGianLap|ngayNhap|maNhaCungCap|trangThai");
            ghi.newLine();
            ghi.write("# CT|maSP|tenSP|soLuongNhap|giaNhap|thueSuatNhap(%)|hanSuDung");
            ghi.newLine();
            for (HoadonNhap phieu : dsHoaDonNhap) {
                for (String dong : phieu.toCacDongFile()) {
                    ghi.write(dong);
                    ghi.newLine();
                }
            }
        }
    }

    // Đọc từ thư mục. Đọc phiếu và sản phẩm (kèm lô) vào bộ nhớ tạm, KIỂM TRA liên kết giữa lô và phiếu,
    // mọi thứ đúng mới thay dữ liệu hiện có; sai chỗ nào thì ném IOException và dữ liệu hiện có giữ nguyên.
    // Tồn kho tính từ các lô nên đọc lại phiếu đã xác nhận KHÔNG làm tăng tồn lần nữa.
    // Mã nhà cung cấp trong mỗi phiếu phải có trong DSNhaCungCap hiện tại: hãy nạp danh sách nhà cung cấp TRƯỚC khi đọc kho.
    public void docDuLieu(String thuMuc) throws IOException {
        File dir = new File(thuMuc);
        ArrayList<HoadonNhap> phieuMoi = docFileHoaDonNhap(new File(dir, TEN_FILE_HOA_DON_NHAP));
        ArrayList<SanPham> sanPhamMoi = dsSanPham.docSanPhamTuFile(new File(dir, TEN_FILE_SAN_PHAM).getPath());
        kiemTraLienKet(sanPhamMoi, phieuMoi);
        dsSanPham.thayTheBang(sanPhamMoi);
        dsHoaDonNhap = phieuMoi;
    }

    // Quy tắc liên kết: mỗi dòng của phiếu ĐÃ XÁC NHẬN có đúng một lô khớp (số lượng nhập, hạn, ngày nhập);
    // mỗi lô phải thuộc một phiếu đã xác nhận có dòng của đúng sản phẩm đó; mọi dòng phiếu phải có sản phẩm trong file;
    // mỗi phiếu phải có nhà cung cấp tồn tại trong danh sách nhà cung cấp.
    private void kiemTraLienKet(ArrayList<SanPham> dsSP, ArrayList<HoadonNhap> dsPhieu) throws IOException {
        for (HoadonNhap phieu : dsPhieu) {
            if (!dsNhaCungCap.tonTai(phieu.getMaNCC())) {
                throw new IOException("Dữ liệu không khớp: phiếu " + phieu.getMaHoaDon() + " có nhà cung cấp "
                        + phieu.getMaNCC() + " không có trong danh sách nhà cung cấp (hãy nạp danh sách nhà cung cấp trước khi đọc kho).");
            }
            for (CTHDonNhap ct : phieu.getChiTiet()) {
                SanPham sp = timTrong(dsSP, ct.getMaSP());
                if (sp == null) {
                    throw new IOException("Dữ liệu không khớp: phiếu " + phieu.getMaHoaDon() + " có sản phẩm "
                            + ct.getMaSP() + " không tồn tại trong file sản phẩm.");
                }
                if (phieu.getTrangThai() == HoadonNhap.TrangThai.DA_XAC_NHAN && sp.timLoTheoHoaDonNhap(phieu.getMaHoaDon()) == null) {
                    throw new IOException("Dữ liệu không khớp: phiếu " + phieu.getMaHoaDon() + " đã xác nhận nhưng "
                            + ct.getMaSP() + " không có lô tương ứng.");
                }
            }
        }
        for (SanPham sp : dsSP) {
            for (LoHang lo : sp.getCacLo()) {
                HoadonNhap phieu = timPhieuTrong(dsPhieu, lo.getMaHoaDonNhap());
                if (phieu == null) {
                    throw new IOException("Dữ liệu không khớp: lô " + lo.getMaLo() + " thuộc phiếu "
                            + lo.getMaHoaDonNhap() + " không tồn tại.");
                }
                if (phieu.getTrangThai() != HoadonNhap.TrangThai.DA_XAC_NHAN) {
                    throw new IOException("Dữ liệu không khớp: lô " + lo.getMaLo() + " thuộc phiếu "
                            + phieu.getMaHoaDon() + " đang ở trạng thái '" + phieu.getTrangThai().getNhan() + "'.");
                }
                if (sp.timLoTheoHoaDonNhap(lo.getMaHoaDonNhap()) != lo) {
                    throw new IOException("Dữ liệu không khớp: sản phẩm " + sp.getMaSP() + " có hai lô cùng thuộc phiếu "
                            + lo.getMaHoaDonNhap() + ".");
                }
                CTHDonNhap ct = null;
                for (CTHDonNhap dong : phieu.getChiTiet()) {
                    if (dong.getMaSP().equals(sp.getMaSP())) {
                        ct = dong;
                    }
                }
                if (ct == null) {
                    throw new IOException("Dữ liệu không khớp: phiếu " + phieu.getMaHoaDon() + " không có dòng cho "
                            + sp.getMaSP() + " nhưng lô " + lo.getMaLo() + " thuộc phiếu này.");
                }
                if (ct.getSoLuongNhap() != lo.getSoLuongNhap()
                        || ct.getGiaNhap() != lo.getGiaNhap()
                        || !Objects.equals(ct.getHanSuDung(), lo.getHanSuDung())
                        || !phieu.getNgayNhap().equals(lo.getNgayNhap())) {
                    throw new IOException("Dữ liệu không khớp: lô " + lo.getMaLo() + " khác với dòng "
                            + sp.getMaSP() + " của phiếu " + phieu.getMaHoaDon() + " (số lượng, giá nhập, hạn hoặc ngày nhập).");
                }
            }
        }
    }

    private SanPham timTrong(ArrayList<SanPham> ds, String ma) {
        for (SanPham sp : ds) {
            if (sp.getMaSP().equals(SanPham.chuanHoaMa(ma))) {
                return sp;
            }
        }
        return null;
    }

    private HoadonNhap timPhieuTrong(ArrayList<HoadonNhap> ds, String ma) {
        for (HoadonNhap phieu : ds) {
            if (phieu.getMaHoaDon().equals(SanPham.chuanHoaMa(ma))) {
                return phieu;
            }
        }
        return null;
    }

    private ArrayList<HoadonNhap> docFileHoaDonNhap(File file) throws IOException {
        ArrayList<HoadonNhap> ketQua = new ArrayList<>();
        String[] dauPhieu = null;
        boolean dungPhienBan = false;
        ArrayList<CTHDonNhap> chiTietDangDoc = new ArrayList<>();
        try (BufferedReader doc = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String dong;
            int soDong = 0;
            while ((dong = doc.readLine()) != null) {
                soDong++;
                dong = dong.trim();
                if (soDong == 1 && dong.startsWith("﻿")) {
                    dong = dong.substring(1).trim();
                }
                if (dong.equals(TienIch.DONG_PHIEN_BAN)) {
                    dungPhienBan = true;
                    continue;
                }
                if (dong.isEmpty() || dong.startsWith("#")) {
                    continue;
                }
                if (!dungPhienBan) {
                    throw new IOException(file.getPath() + ", dòng " + soDong + ": thiếu dòng '" + TienIch.DONG_PHIEN_BAN
                            + "' ở đầu file (file định dạng cũ, cần chuyển sang định dạng mới).");
                }
                try {
                    if (dong.startsWith("HDN|")) {
                        chotPhieuDaDoc(ketQua, dauPhieu, chiTietDangDoc); // chốt phiếu đọc trước đó
                        dauPhieu = dong.split("\\|", -1);
                        chiTietDangDoc = new ArrayList<>();
                    } else if (dong.startsWith("CT|")) {
                        if (dauPhieu == null) {
                            throw new IllegalArgumentException("Dòng CT xuất hiện trước dòng HDN.");
                        }
                        chiTietDangDoc.add(CTHDonNhap.tuDongFile(dong));
                    } else {
                        throw new IllegalArgumentException("Dòng phải bắt đầu bằng HDN| hoặc CT|.");
                    }
                } catch (IllegalArgumentException e) {
                    throw new IOException(file.getPath() + ", dòng " + soDong + ": " + e.getMessage());
                } catch (DateTimeParseException e) {
                    throw new IOException(file.getPath() + ", dòng " + soDong + ": ngày/giờ không hợp lệ (cần yyyy-MM-dd hoặc yyyy-MM-ddTHH:mm:ss).");
                }
            }
        }
        try {
            chotPhieuDaDoc(ketQua, dauPhieu, chiTietDangDoc); // chốt phiếu cuối file
        } catch (IllegalArgumentException e) {
            throw new IOException(file.getPath() + ", phiếu cuối file: " + e.getMessage());
        } catch (DateTimeParseException e) {
            throw new IOException(file.getPath() + ", phiếu cuối file: ngày không hợp lệ.");
        }
        return ketQua;
    }

    // Dựng phiếu từ dòng PN và các dòng CT đã đọc rồi thêm vào kết quả
    private void chotPhieuDaDoc(ArrayList<HoadonNhap> ketQua, String[] dauPhieu, ArrayList<CTHDonNhap> chiTiet) {
        if (dauPhieu == null) {
            return;
        }
        if (dauPhieu.length != 7) {
            throw new IllegalArgumentException("Dòng HDN cần 7 trường (HDN|maHoaDon|maNV|thoiGianLap|ngayNhap|maNhaCungCap|trangThai).");
        }
        HoadonNhap phieu = new HoadonNhap(dauPhieu[1], dauPhieu[2], LocalDateTime.parse(dauPhieu[3]),
                LocalDate.parse(dauPhieu[4]), dauPhieu[5], HoadonNhap.TrangThai.valueOf(dauPhieu[6]), chiTiet);
        for (HoadonNhap daCo : ketQua) {
            if (daCo.getMaHoaDon().equals(phieu.getMaHoaDon())) {
                throw new IllegalArgumentException("Mã phiếu bị trùng trong file: " + phieu.getMaHoaDon());
            }
        }
        ketQua.add(phieu);
    }

    // ===================== MENU CONSOLE =====================

    public void chayMenu(Scanner sc) {
        int chon;
        do {
            inMenu();
            chon = TienIch.docSoNguyen(sc, "Chọn chức năng: ", 0, 23);
            try {
                xuLyLuaChon(chon, sc);
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Lỗi: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Lỗi đọc/ghi file: " + e.getMessage());
            }
        } while (chon != 0);
    }

    private void inMenu() {
        System.out.println();
        System.out.println("========== QUẢN LÝ KHO ==========");
        System.out.println(" 1. Nhập n sản phẩm ban đầu");
        System.out.println(" 2. Xuất danh sách sản phẩm");
        System.out.println(" 3. Thêm một sản phẩm");
        System.out.println(" 4. Thêm k sản phẩm");
        System.out.println(" 5. Sửa sản phẩm theo mã");
        System.out.println(" 6. Xóa sản phẩm theo mã");
        System.out.println(" 7. Tìm kiếm sản phẩm");
        System.out.println(" 8. Sắp xếp danh sách");
        System.out.println(" 9. Thống kê tồn kho");
        System.out.println("10. Cảnh báo: sắp hết hàng, hết hàng, lô hết hạn/sắp hết hạn");
        System.out.println("11. Tạo phiếu nhập");
        System.out.println("12. Thêm chi tiết (một lô) vào phiếu nhập");
        System.out.println("13. Xác nhận phiếu nhập");
        System.out.println("14. Hủy phiếu nhập");
        System.out.println("15. Xem các phiếu nhập");
        System.out.println("16. Báo cáo nhập kho theo khoảng ngày");
        System.out.println("17. Xuất kho thử theo FEFO (mô phỏng Thu ngân bán hàng)");
        System.out.println("18. Lưu dữ liệu ra file");
        System.out.println("19. Đọc dữ liệu từ file");
        System.out.println("20. Thêm sản phẩm mẫu");
        System.out.println("21. Xem các lô của một sản phẩm");
        System.out.println("22. Hoàn kho về đúng lô");
        System.out.println("23. Xem danh sách nhà cung cấp");
        System.out.println(" 0. Thoát");
    }

    private void xuLyLuaChon(int chon, Scanner sc) throws IOException {
        switch (chon) {
            case 1:
                dsSanPham.nhapN(TienIch.docSoNguyen(sc, "Số sản phẩm cần nhập (n): ", 1, 1000), sc);
                break;
            case 2:
                dsSanPham.xuat();
                break;
            case 3:
                dsSanPham.nhapN(1, sc);
                break;
            case 4:
                dsSanPham.nhapN(TienIch.docSoNguyen(sc, "Số sản phẩm cần thêm (k): ", 1, 1000), sc);
                break;
            case 5:
                suaSanPhamTuBanPhim(sc);
                break;
            case 6:
                xoaSanPham(TienIch.docChuoi(sc, "Mã sản phẩm cần xóa: "));
                System.out.println("Đã xóa sản phẩm.");
                break;
            case 7:
                timKiemTuBanPhim(sc);
                break;
            case 8:
                sapXepTuBanPhim(sc);
                break;
            case 9:
                System.out.println(baoCaoTonKho());
                break;
            case 10:
                inCanhBao();
                break;
            case 11:
                taoPhieuTuBanPhim(sc);
                break;
            case 12:
                themChiTietTuBanPhim(sc);
                break;
            case 13:
                xacNhanHoaDonNhap(TienIch.docChuoi(sc, "Mã phiếu cần xác nhận: "));
                System.out.println("Đã xác nhận phiếu, các lô hàng mới đã vào kho.");
                break;
            case 14:
                huyHoaDonNhap(TienIch.docChuoi(sc, "Mã phiếu cần hủy: "));
                System.out.println("Đã hủy phiếu.");
                break;
            case 15:
                inHoaDonNhap();
                break;
            case 16:
                LocalDate tu = TienIch.docNgay(sc, "Từ ngày (dd/MM/yyyy): ");
                LocalDate den = TienIch.docNgay(sc, "Đến ngày (dd/MM/yyyy): ");
                System.out.println(baoCaoNhapKho(tu, den));
                break;
            case 17:
                xuatKhoThuTuBanPhim(sc);
                break;
            case 18:
                luuDuLieu(THU_MUC_MAC_DINH);
                System.out.println("Đã lưu vào thư mục '" + THU_MUC_MAC_DINH + "'.");
                break;
            case 19:
                docDuLieu(THU_MUC_MAC_DINH);
                System.out.println("Đã đọc dữ liệu từ thư mục '" + THU_MUC_MAC_DINH + "'.");
                break;
            case 20:
                nhapDuLieuMau();
                System.out.println("Đã thêm 4 sản phẩm mẫu (tồn = 0, hãy nhập kho bằng phiếu nhập).");
                break;
            case 21:
                System.out.println(moTaCacLo(TienIch.docChuoi(sc, "Mã sản phẩm: ")));
                break;
            case 22:
                String maSP = TienIch.docChuoi(sc, "Mã sản phẩm: ");
                String maLo = TienIch.docChuoi(sc, "Mã lô cần hoàn về: ");
                int soLuongHoan = TienIch.docSoNguyen(sc, "Số lượng hoàn: ", 1, Integer.MAX_VALUE);
                hoanKho(maSP, maLo, soLuongHoan);
                System.out.println("Đã hoàn. " + moTaCacLo(maSP));
                break;
            case 23:
                dsNhaCungCap.xuat();
                break;
            case 0:
                System.out.println("Thoát chức năng quản lý kho.");
                break;
            default:
                System.out.println("Chức năng không tồn tại.");
        }
    }

    // Sửa: nhập lại toàn bộ thông tin có thể sửa (màn hình hiện giá trị cũ để đối chiếu)
    private void suaSanPhamTuBanPhim(Scanner sc) {
        String ma = TienIch.docChuoi(sc, "Mã sản phẩm cần sửa: ");
        SanPham cu = dsSanPham.timTheoMa(ma);
        if (cu == null) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm có mã: " + ma);
        }
        System.out.println("Hiện tại: " + cu);
        System.out.println("(Không sửa được mã, loại, đơn vị tính. Hàng trong kho chỉ đổi qua phiếu nhập hoặc xuất kho.)");
        String ten = TienIch.docChuoi(sc, "Tên mới: ");
        long giaNhap = TienIch.docSoLong(sc, "Giá nhập tham khảo mới (đồng, chưa thuế, 0 = chưa có; không đổi giá các lần nhập cũ): ", 0, SanPham.GIA_TOI_DA);
        long giaBan = TienIch.docSoLong(sc, "Giá bán mới (đồng, chưa thuế): ", 1, SanPham.GIA_TOI_DA);
        double thue = TienIch.docSoThuc(sc, "Thuế suất bán mới (%): ", 0, SanPham.THUE_SUAT_TOI_DA);
        SanPham moi;
        if (cu instanceof ThucAn) {
            String nsx = TienIch.docChuoi(sc, "Nhà sản xuất mới: ");
            moi = new ThucAn(cu.getMaSP(), ten, giaNhap, giaBan, cu.getDonViTinh(), thue, nsx);
        } else if (cu instanceof DoUong) {
            int dungTich = TienIch.docSoNguyen(sc, "Dung tích mới (ml): ", 1, DoUong.DUNG_TICH_TOI_DA_ML);
            moi = new DoUong(cu.getMaSP(), ten, giaNhap, giaBan, cu.getDonViTinh(), thue, dungTich);
        } else {
            throw new IllegalStateException("Loại sản phẩm này chưa hỗ trợ sửa từ menu.");
        }
        suaSanPham(ma, moi);
        System.out.println("Đã sửa: " + dsSanPham.timTheoMa(ma));
    }

    private void timKiemTuBanPhim(Scanner sc) {
        System.out.println("1. Mã chính xác | 2. Mã gần đúng | 3. Tên chính xác | 4. Tên gần đúng");
        System.out.println("5. Loại | 6. Khoảng giá | 7. Kết hợp nhiều tiêu chí");
        int cachTim = TienIch.docSoNguyen(sc, "Chọn cách tìm: ", 1, 7);
        ArrayList<SanPham> ketQua = new ArrayList<>();
        if (cachTim == 1) {
            SanPham sp = timSanPham(TienIch.docChuoi(sc, "Mã: "));
            if (sp != null) {
                ketQua.add(sp);
            }
        } else if (cachTim == 2) {
            ketQua = dsSanPham.timTheoMaGanDung(TienIch.docChuoi(sc, "Một phần mã: "));
        } else if (cachTim == 3) {
            ketQua = dsSanPham.timTheoTenChinhXac(TienIch.docChuoi(sc, "Tên đầy đủ: "));
        } else if (cachTim == 4) {
            ketQua = dsSanPham.timTheoTenGanDung(TienIch.docChuoi(sc, "Một phần tên (không cần dấu): "));
        } else if (cachTim == 5) {
            ketQua = dsSanPham.timTheoLoai(TienIch.docChuoi(sc, "Loại (Thức ăn / Đồ uống): "));
        } else if (cachTim == 6) {
            long tu = TienIch.docSoLong(sc, "Giá từ: ", 0, SanPham.GIA_TOI_DA);
            long den = TienIch.docSoLong(sc, "Giá đến: ", 0, SanPham.GIA_TOI_DA);
            ketQua = dsSanPham.timTheoKhoangGia(tu, den);
        } else {
            String ten = TienIch.docChuoiTuyChon(sc, "Một phần tên (Enter = bỏ qua): ");
            String loai = TienIch.docChuoiTuyChon(sc, "Loại (Enter = bỏ qua): ");
            long tu = TienIch.docSoLong(sc, "Giá từ (0 = không giới hạn): ", 0, SanPham.GIA_TOI_DA);
            long den = TienIch.docSoLong(sc, "Giá đến (0 = không giới hạn): ", 0, SanPham.GIA_TOI_DA);
            if (den == 0) {
                den = SanPham.GIA_TOI_DA;
            }
            ketQua = dsSanPham.timNhieuTieuChi(ten, loai, tu, den);
        }
        inKetQua(ketQua);
    }

    private void sapXepTuBanPhim(Scanner sc) {
        System.out.println("1. Mã | 2. Tên | 3. Giá bán tăng | 4. Giá bán giảm | 5. Tồn tăng | 6. Tồn giảm");
        int cach = TienIch.docSoNguyen(sc, "Chọn tiêu chí: ", 1, 6);
        if (cach == 1) {
            dsSanPham.sapXepTheoMa();
        } else if (cach == 2) {
            dsSanPham.sapXepTheoTen();
        } else if (cach == 3) {
            dsSanPham.sapXepTheoGiaBan(true);
        } else if (cach == 4) {
            dsSanPham.sapXepTheoGiaBan(false);
        } else if (cach == 5) {
            dsSanPham.sapXepTheoTon(true);
        } else {
            dsSanPham.sapXepTheoTon(false);
        }
        dsSanPham.xuat();
    }

    private void inCanhBao() {
        System.out.println("--- Sắp hết hàng (còn hạn từ 1 đến " + NGUONG_SAP_HET_HANG + ") ---");
        inKetQua(layHangSapHet());
        System.out.println("--- Hết hàng (tồn 0) ---");
        inKetQua(layHangHetHang());
        System.out.println(baoCaoHanSuDung(LocalDate.now(), SO_NGAY_CANH_BAO_HET_HAN));
    }

    private void taoPhieuTuBanPhim(Scanner sc) {
        System.out.println("Nhà cung cấp hiện có:");
        dsNhaCungCap.xuat();
        String maNcc = TienIch.docChuoi(sc, "Mã nhà cung cấp: ");
        String maNv = TienIch.docChuoi(sc, "Mã nhân viên lập hóa đơn: ");
        String ngayChuoi = TienIch.docChuoiTuyChon(sc, "Ngày nhập (dd/MM/yyyy, Enter = hôm nay): ");
        LocalDate ngay = LocalDate.now();
        if (!ngayChuoi.isEmpty()) {
            try {
                ngay = LocalDate.parse(ngayChuoi, TienIch.DINH_DANG_NGAY);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Ngày không hợp lệ, cần dạng dd/MM/yyyy.");
            }
        }
        HoadonNhap phieu = taoHoaDonNhap(maNv, maNcc, ngay);
        System.out.println("Đã tạo " + phieu + ". Dùng chức năng 12 để thêm chi tiết.");
    }

    private void themChiTietTuBanPhim(Scanner sc) {
        String maPhieu = TienIch.docChuoi(sc, "Mã phiếu nhập: ");
        String maSP = TienIch.docChuoi(sc, "Mã sản phẩm: ");
        SanPham sp = dsSanPham.timTheoMa(maSP);
        if (sp == null) {
            throw new IllegalArgumentException("Sản phẩm " + maSP + " chưa có trong danh mục.");
        }
        int soLuong = TienIch.docSoNguyen(sc, "Số lượng nhập: ", 1, Integer.MAX_VALUE);
        long giaNhap = TienIch.docSoLong(sc, "Giá nhập lần này (đồng/đơn vị, chưa thuế; giá tham khảo hiện tại "
                + sp.getGiaNhap() + "): ", 0, SanPham.GIA_TOI_DA);
        double thue = TienIch.docSoThuc(sc, "Thuế suất nhập (%), nhập 0 nếu chưa khai báo: ", 0, SanPham.THUE_SUAT_TOI_DA);
        LocalDate han;
        if (sp.batBuocCoHanSuDung()) {
            han = TienIch.docNgay(sc, "Hạn sử dụng của lô này (dd/MM/yyyy): ");
        } else {
            han = TienIch.docNgayTuyChon(sc, "Hạn sử dụng của lô này (dd/MM/yyyy, Enter = không có): ");
        }
        themChiTietPhieu(maPhieu, maSP, soLuong, giaNhap, thue, han);
        System.out.println("Đã thêm. " + layPhieu(maPhieu));
    }

    private void inHoaDonNhap() {
        if (dsHoaDonNhap.isEmpty()) {
            System.out.println("(Chưa có phiếu nhập nào)");
            return;
        }
        for (HoadonNhap phieu : dsHoaDonNhap) {
            System.out.println(phieu.moTaDayDu(nhanNhaCungCap(phieu.getMaNCC())));
        }
    }

    private void xuatKhoThuTuBanPhim(Scanner sc) {
        String ma = TienIch.docChuoi(sc, "Mã sản phẩm bán: ");
        int soLuong = TienIch.docSoNguyen(sc, "Số lượng bán: ", 1, Integer.MAX_VALUE);
        ArrayList<LoXuat> cacLo = xuatKho(ma, soLuong);
        System.out.println("Đã xuất kho, lấy từ các lô (hãy ghi nhớ để hoàn đúng lô):");
        for (LoXuat x : cacLo) {
            System.out.println("  " + x);
        }
        System.out.println("Tồn còn lại của " + SanPham.chuanHoaMa(ma) + ": " + kiemTraTon(ma)
                + " (còn hạn: " + kiemTraTonBanDuoc(ma) + ")");
    }

    private void inKetQua(ArrayList<SanPham> ketQua) {
        if (ketQua.isEmpty()) {
            System.out.println("(Không có sản phẩm nào)");
            return;
        }
        for (SanPham sp : ketQua) {
            System.out.println(sp);
        }
    }
}