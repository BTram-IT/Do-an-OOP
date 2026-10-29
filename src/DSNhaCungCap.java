import java.util.ArrayList;

public class DSNhaCungCap {

    private ArrayList<NhaCungCap> danhSach = new ArrayList<>();

    public int soLuong() {
        return danhSach.size();
    }

    // Mã không phân biệt hoa thường; mã trùng thì báo lỗi
    public void them(NhaCungCap ncc) {
        if (ncc == null) {
            throw new IllegalArgumentException("Nhà cung cấp không được null.");
        }
        String ma = SanPham.chuanHoaMa(ncc.getMa());
        if (ma == null || ma.isEmpty()) {
            throw new IllegalArgumentException("Nhà cung cấp phải có mã.");
        }
        if (tonTai(ma)) {
            throw new IllegalArgumentException("Mã nhà cung cấp đã tồn tại: " + ma);
        }
        danhSach.add(ncc);
    }

    public NhaCungCap timTheoMa(String ma) {
        String maChuan = SanPham.chuanHoaMa(ma);
        if (maChuan == null) {
            return null;
        }
        for (NhaCungCap ncc : danhSach) {
            if (maChuan.equals(SanPham.chuanHoaMa(ncc.getMa()))) {
                return ncc;
            }
        }
        return null;
    }

    public boolean tonTai(String ma) {
        return timTheoMa(ma) != null;
    }

    // Xóa nhà cung cấp khỏi danh sách; các phiếu nhập cũ vẫn giữ mã nhà cung cấp của mình
    public boolean xoa(String ma) {
        NhaCungCap ncc = timTheoMa(ma);
        if (ncc == null) {
            return false;
        }
        danhSach.remove(ncc);
        return true;
    }

    // Bản sao danh sách
    public ArrayList<NhaCungCap> layTatCa() {
        return new ArrayList<NhaCungCap>(danhSach);
    }

    public void xuat() {
        if (danhSach.isEmpty()) {
            System.out.println("(Chưa có nhà cung cấp nào)");
            return;
        }
        for (NhaCungCap ncc : danhSach) {
            System.out.println(ncc);
        }
    }
}
