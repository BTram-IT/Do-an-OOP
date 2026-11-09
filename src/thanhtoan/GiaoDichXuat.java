package thanhtoan;

import java.util.ArrayList;
import lohang.LoXuat;
import sanpham.SanPham;

public class GiaoDichXuat {

    private final String maGiaoDich;
    private final ArrayList<LoXuat> daXuat = new ArrayList<>();
    private final ArrayList<Integer> daHoan = new ArrayList<>();

    public GiaoDichXuat(String maGiaoDich) {
        this.maGiaoDich = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maGiaoDich, "Mã giao dịch xuất kho"));
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    private int viTri(String maSP, String maLo) {
        String sp = SanPham.chuanHoaMa(maSP);
        String lo = SanPham.chuanHoaMa(maLo);
        for (int i = 0; i < daXuat.size(); i++) {
            if (daXuat.get(i).getMaSP().equals(sp) && daXuat.get(i).getMaLo().equals(lo)) {
                return i;
            }
        }
        return -1;
    }

    public void ghiNhanXuat(LoXuat x) {
        if (x == null) {
            throw new IllegalArgumentException("LoXuat không được null.");
        }
        int idx = viTri(x.getMaSP(), x.getMaLo());
        if (idx >= 0) {
            int tong = daXuat.get(idx).getSoLuong() + x.getSoLuong();
            daXuat.set(idx, new LoXuat(x.getMaSP(), x.getMaLo(), tong));
        } else {
            daXuat.add(x);
            daHoan.add(0);
        }
    }

    public void ghiNhanHoan(String maSP, String maLo, int soLuong) {
        int idx = viTri(maSP, maLo);
        if (idx < 0) {
            throw new IllegalArgumentException("Lô này không thuộc giao dịch " + maGiaoDich);
        }
        int moi = daHoan.get(idx) + soLuong;
        if (moi > daXuat.get(idx).getSoLuong()) {
            throw new IllegalStateException("Số lượng hoàn vượt quá số đã xuất.");
        }
        daHoan.set(idx, moi);
    }

    public int soXuatRong(String maSP, String maLo) {
        int idx = viTri(maSP, maLo);
        return idx < 0 ? 0 : daXuat.get(idx).getSoLuong() - daHoan.get(idx);
    }

    public int soConHoanDuoc(String maSP, String maLo) {
        return soXuatRong(maSP, maLo);
    }

    public ArrayList<LoXuat> getCacLoDaXuat() {
        return new ArrayList<>(daXuat);
    }

    public ArrayList<LoXuat> getCacLoConHoanDuoc() {
        ArrayList<LoXuat> res = new ArrayList<>();
        for (int i = 0; i < daXuat.size(); i++) {
            int con = daXuat.get(i).getSoLuong() - daHoan.get(i);
            if (con > 0) {
                res.add(new LoXuat(daXuat.get(i).getMaSP(), daXuat.get(i).getMaLo(), con));
            }
        }
        return res;
    }

    public void nap(String maSP, String maLo, int xuat, int hoan) {
        daXuat.add(new LoXuat(maSP, maLo, xuat));
        daHoan.add(hoan);
    }

    public ArrayList<String> toCacDongFile() {
        ArrayList<String> ds = new ArrayList<>();
        for (int i = 0; i < daXuat.size(); i++) {
            ds.add("XK|" + maGiaoDich + "|" + daXuat.get(i).getMaSP() + "|" + daXuat.get(i).getMaLo()
                    + "|" + daXuat.get(i).getSoLuong() + "|" + daHoan.get(i));
        }
        return ds;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Giao dịch " + maGiaoDich + ":");
        for (int i = 0; i < daXuat.size(); i++) {
            sb.append("\n  - ").append(daXuat.get(i)).append(" | Đã hoàn: ").append(daHoan.get(i));
        }
        return sb.toString();
    }
}