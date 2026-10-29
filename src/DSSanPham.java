import java.util.ArrayList;

// Danh sách sản phẩm dùng MẢNG ĐỘNG (ArrayList): tự lớn lên khi thêm, không giới hạn số lượng.
// Mọi nghiệp vụ nằm ở DanhSachSanPhamCoSo, lớp này chỉ lo phần lưu trữ.
public class DSSanPham extends DanhSachSanPhamCoSo {

    private ArrayList<SanPham> danhSach = new ArrayList<>();

    @Override
    public int soLuong() {
        return danhSach.size();
    }

    @Override
    public int sucChuaToiDa() {
        return Integer.MAX_VALUE;
    }

    @Override
    public SanPham layTheoViTri(int viTri) {
        return danhSach.get(viTri);
    }

    @Override
    protected boolean themCuoi(SanPham sp) {
        danhSach.add(sp);
        return true;
    }

    @Override
    protected void xoaTaiViTri(int viTri) {
        danhSach.remove(viTri);
    }

    @Override
    protected void datTaiViTri(int viTri, SanPham sp) {
        danhSach.set(viTri, sp);
    }

    @Override
    protected void xoaHet() {
        danhSach.clear();
    }
}
