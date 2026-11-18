package person;

import java.util.List;
import java.util.Scanner;
import util.BangConsole;
import util.Ktradulieu;

/** Admin kế thừa nghiệp vụ vận hành của Quản lý và có thêm quyền quản trị hệ thống. */
public class Admin extends QuanLyCuaHang {
    public Admin() {
        super();
        this.vaiTro = "Admin";
    }

    public Admin(String maND, String tenND, String taiKhoan, String matKhau,
                 String soDienThoai, boolean daXoa) {
        super(maND, tenND, taiKhoan, matKhau, soDienThoai, "Admin", daXoa,
                "Toàn hệ thống", 1.0);
    }

    @Override
    public String[] getQuyenHan() {
        return new String[]{"QUAN_TRI_TAI_KHOAN", "QUAN_LY_NHAN_VIEN", "SA_THAI_NHAN_VIEN",
                "QUAN_LY_KHO", "QUAN_LY_SAN_PHAM", "QUAN_LY_KHUYEN_MAI", "QUAN_LY_NHA_CUNG_CAP",
                "QUAN_LY_KHACH_HANG", "QUYEN_TRA_CUU", "QUYEN_KIEM_KE_KHO",
                "TINH_LUONG_NHAN_VIEN", "XEM_THONG_KE_DOANH_THU"};
    }

    @Override
    public String thucHienCongViec() {
        return "Admin [" + tenND + "] quản trị tài khoản và toàn bộ hoạt động cửa hàng.";
    }

    @Override
    public void nhapThongTin(Scanner sc, DSNhanVien ds) {
        nhapThongTinCoBan(sc, ds);
    }

    @Override
    public boolean saThaiNhanVien(DSNhanVien dsNV, String maNV) {
        return dsNV != null && dsNV.xoa(null, this, maNV);
    }

    public void duyetYeuCauSaThai(Scanner sc, DSNhanVien dsNV) {
        if (!coQuyen("SA_THAI_NHAN_VIEN") || sc == null || dsNV == null) {
            System.out.println("[Từ chối] Tài khoản hoặc dữ liệu xử lý không hợp lệ!");
            return;
        }
        List<NhanVien> danhSach = dsNV.getDanhSachYeuCauSaThai();
        if (danhSach.isEmpty()) {
            System.out.println("[Thông báo] Không có yêu cầu sa thải đang chờ.");
            return;
        }
        System.out.println(BangConsole.tuDanhSach(danhSach));
        String maNV = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập mã nhân viên cần xử lý: ");
        NhanVien nv = dsNV.timNhanVienTheoMa(maNV);
        if (nv == null || nv.isDaXoa() || nv.getYeuCauSaThai() == null
                || nv.getYeuCauSaThai().isBlank()) {
            System.out.println("[Lỗi] Không tìm thấy yêu cầu sa thải hợp lệ!");
            return;
        }
        System.out.println("Nhân viên: " + nv.getTenND() + " | Lý do: " + nv.getYeuCauSaThai());
        System.out.println("1. Chấp nhận sa thải\n2. Từ chối sa thải\n0. Quay lại");
        int chon = Ktradulieu.luaChon(sc, "Chọn (0-2): ", 0, 2);
        if (chon != 0) dsNV.xuLyYeuCauSaThai(this, maNV, chon == 1);
    }

    @Override
    protected String layChiTietBang() {
        return "Toàn quyền quản trị hệ thống";
    }

    @Override
    public String toFileString() {
        return String.format("ADMIN;%s;%s;%s;%s;%s;Admin;%b",
                maND, tenND, taiKhoan, matKhau, soDienThoai, daXoa);
    }
}
