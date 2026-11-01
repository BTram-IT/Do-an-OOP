import java.util.ArrayList;

public class DSNhaCungCap {
    private ArrayList<NhaCungCap> danhSachNhaCungCap = new ArrayList<>();

    // Thêm khi tích hợp kho: kho cần nạp nhà cung cấp (kiểm tra mã không rỗng, không trùng, không phân biệt hoa thường)
    public void them(NhaCungCap ncc) {
        if (ncc == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được null.");
        }
        String ma = SanPham.chuanHoaMa(ncc.getMaDT());
        if (ma == null || ma.isEmpty()) {
            throw new IllegalArgumentException("Nhà cung cấp phải có mã.");
        }
        if (tonTai(ma)) {
            throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại: " + ma);
        }
        danhSachNhaCungCap.add(ncc);
    }

    public NhaCungCap timTheoMa(String ma) {
        String maChuan = SanPham.chuanHoaMa(ma);
        if (maChuan == null) {
            return null;
        }
        for (NhaCungCap ncc : danhSachNhaCungCap) {
            if (maChuan.equals(SanPham.chuanHoaMa(ncc.getMaDT()))) {
                return ncc;
            }
        }
        return null;
    }

    public boolean tonTai(String ma) {
        return timTheoMa(ma) != null;
    }

    public void xuat() {
        if (danhSachNhaCungCap == null || danhSachNhaCungCap.isEmpty()) {
            System.out.println("(Danh sách nhà cung cấp trống)");
            return;
        }
        System.out.println("===== DANH SÁCH NHÀ CUNG CẤP =====");
        for (NhaCungCap ncc : danhSachNhaCungCap) {
            System.out.println(ncc);
        }
    }
}