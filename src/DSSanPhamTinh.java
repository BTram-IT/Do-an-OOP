// Mọi nghiệp vụ nằm ở DanhSachSanPhamCoSo, lớp này chỉ lo phần lưu trữ.
public class DSSanPhamTinh extends DanhSachSanPhamCoSo {

    public static final int SUC_CHUA_MAC_DINH = 100;

    private SanPham[] mang;
    private int soPhanTu = 0;

    public DSSanPhamTinh(int sucChua) {
        if (sucChua <= 0) {
            throw new IllegalArgumentException("Sức chứa của mảng phải lớn hơn 0.");
        }
        mang = new SanPham[sucChua];
    }

    public DSSanPhamTinh() {
        this(SUC_CHUA_MAC_DINH);
    }

    @Override
    public int soLuong() {
        return soPhanTu;
    }

    @Override
    public int sucChuaToiDa() {
        return mang.length;
    }

    @Override
    public SanPham layTheoViTri(int viTri) {
        if (viTri < 0 || viTri >= soPhanTu) {
            throw new IndexOutOfBoundsException("Vị trí " + viTri + " nằm ngoài danh sách (có " + soPhanTu + " sản phẩm).");
        }
        return mang[viTri];
    }

    @Override
    protected boolean themCuoi(SanPham sp) {
        if (soPhanTu == mang.length) {
            return false; // mảng tĩnh không tự lớn lên
        }
        mang[soPhanTu] = sp;
        soPhanTu++;
        return true;
    }

    @Override
    protected void xoaTaiViTri(int viTri) {
        // Dồn các phần tử phía sau lên một ô
        for (int i = viTri; i < soPhanTu - 1; i++) {
            mang[i] = mang[i + 1];
        }
        soPhanTu--;
        mang[soPhanTu] = null;
    }

    @Override
    protected void datTaiViTri(int viTri, SanPham sp) {
        mang[viTri] = sp;
    }

    @Override
    protected void xoaHet() {
        for (int i = 0; i < soPhanTu; i++) {
            mang[i] = null;
        }
        soPhanTu = 0;
    }
}
