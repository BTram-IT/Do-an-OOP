import java.time.LocalDate;
import java.util.Scanner;
import person.*;
import hoadon.*;
import sanpham.DSSanPham;
import doitac.DSNhaCungCap;

public class PhanQuyenGioiHanAdminTest {
    static class DanhSach extends DSNhanVien {
        @Override public boolean ghiFile(String path) { return true; }
    }
    static void check(boolean ok) { if (!ok) throw new AssertionError(); }
    public static void main(String[] args) {
        Admin ad = new Admin("AD001", "Admin", "ad", "123456", "0912345678", false);
        QuanLyCuaHang ql = new QuanLyCuaHang("QL001", "Quản lý", "ql", "123456", "0912345678", "Quản lý", false, "Kho", 1.5);
        for (String right : new String[]{"QUYEN_NHAP_KHO", "QUYEN_BAN_HANG", "SUA_HOA_DON", "HUY_HOA_DON", "DUYET_HOA_DON"})
            check(!ad.coQuyen(right));
        check(ad.coQuyen("QUYEN_TRA_CUU") && ad.coQuyen("QUAN_TRI_TAI_KHOAN"));
        check(!ql.coQuyen("QUAN_LY_NHAN_VIEN") && !ql.coQuyen("SA_THAI_NHAN_VIEN"));
        DanhSach ds = new DanhSach();
        NhanVien nv = new NhanVien("NV001", "Nhân viên", "nv", "123456", "0912345678", LocalDate.now(), false);
        check(!ds.them(ql, nv) && ds.getSoLuong() == 0);
        check(ds.them(ad, nv));
        check(!ds.xoa(new Scanner(""), ql, "NV001") && !nv.isDaXoa());
        String tenCu = nv.getTenND();
        ds.sua(new Scanner(""), ql, "NV001");
        check(nv.getTenND().equals(tenCu));
        ds.sua(new Scanner("1\nTên mới\n0\n"), ad, "NV001");
        check(!nv.getTenND().equals(tenCu));
        check(ds.xoa(new Scanner(""), ad, "NV001") && nv.isDaXoa());
        HoadonNhap hd = new HoadonNhap("HDN001", ad, LocalDate.now(), "DangXuLy", "", "NCC001");
        check(!hd.thanhToanHoaDon(new Scanner(""), ad, new DSSanPham(), new DSNhaCungCap()));
        DSHoadon bills = new DSHoadon();
        check(!bills.suaHD(new Scanner(""), ad) && !bills.xoaHD(new Scanner(""), ad));
        ad.setDaXoa(true);
        check(!ad.coQuyen("QUYEN_TRA_CUU") && !ad.coQuyen("QUAN_TRI_TAI_KHOAN"));
        System.out.println("PASS: Admin chỉ xem hóa đơn; chỉ Admin thêm/sửa/xóa nhân viên; chặn gọi hàm trực tiếp và tài khoản bị khóa.");
    }
}
