import java.util.ArrayList;

public class DSNhaCungCap {
    private ArrayList<NhaCungCap> danhSachNhaCungCap = new ArrayList<>();

    public NhaCungCap timTheoMa(String ma) {
        String maChuan = SanPham.chuanHoaMa(ma);
        if (maChuan == null) {
            return null;
        }
        for (NhaCungCap ncc : danhSachNhaCungCap) {
            if (maChuan.equals(SanPham.chuanHoaMa(ncc.getMa()))) {
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