public class DSNhaCungCap {

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
}
