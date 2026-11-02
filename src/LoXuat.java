// Kết quả một lần xuất kho: đã lấy bao nhiêu từ LÔ nào của sản phẩm nào.
// Thu ngân/DonHang phải GIỮ danh sách LoXuat này để khi hủy đơn thì hoàn hàng về đúng lô (QuanlyKho.hoanKho).
// Đối tượng không đổi được sau khi tạo.
public class LoXuat {

    private final String maSP;
    private final String maLo;
    private final int soLuong;

    public LoXuat(String maSP, String maLo, int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng xuất từ lô phải lớn hơn 0.");
        }
        this.maSP = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maSP, "Mã sản phẩm"));
        this.maLo = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maLo, "Mã lô"));
        this.soLuong = soLuong;
    }

    public String getMaSP() {
        return maSP;
    }

    public String getMaLo() {
        return maLo;
    }

    public int getSoLuong() {
        return soLuong;
    }

    @Override
    public String toString() {
        return maSP + " x " + soLuong + " (lô " + maLo + ")";
    }
}
