import java.util.ArrayList;

// Sổ ghi một giao dịch xuất kho (ví dụ một hóa đơn bán): đã xuất những lô nào, bao nhiêu, đã hoàn lại bao nhiêu.
// Mục đích: hoàn kho theo đúng giao dịch gốc (không hoàn trùng, không hoàn vượt, không hoàn nhầm lô).
// Mã giao dịch do bên gọi đặt (thường là mã hóa đơn bán). Lớp này không tự đổi tồn kho, chỉ ghi sổ;
// QuanlyKho vừa đổi tồn vừa ghi sổ.
public class GiaoDichXuat {

    private final String maGiaoDich;
    // Mỗi phần tử ứng với một cặp (sản phẩm, lô); hai danh sách luôn cùng độ dài và cùng thứ tự
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

    // Ghi nhận số lượng đã xuất của một lô (cộng dồn nếu lô đã có trong sổ)
    void ghiNhanXuat(LoXuat x) {
        int i = viTri(x.getMaSP(), x.getMaLo());
        if (i < 0) {
            daXuat.add(x);
            daHoan.add(0);
        } else {
            long tong = (long) daXuat.get(i).getSoLuong() + x.getSoLuong();
            if (tong > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Số lượng xuất của lô " + x.getMaLo() + " quá lớn.");
            }
            daXuat.set(i, new LoXuat(x.getMaSP(), x.getMaLo(), (int) tong));
        }
    }

    // Dùng khi đọc file: nạp thẳng số đã xuất và số đã hoàn của một lô
    void nap(String maSP, String maLo, int soXuat, int soHoan) {
        if (viTri(maSP, maLo) >= 0) {
            throw new IllegalArgumentException("Giao dịch " + maGiaoDich + " ghi lô " + maLo + " của " + maSP + " hai lần.");
        }
        if (soXuat <= 0 || soHoan < 0 || soHoan > soXuat) {
            throw new IllegalArgumentException("Giao dịch " + maGiaoDich + ", lô " + maLo + ": số xuất " + soXuat
                    + " và số hoàn " + soHoan + " không hợp lệ.");
        }
        daXuat.add(new LoXuat(maSP, maLo, soXuat));
        daHoan.add(soHoan);
    }

    // Ghi nhận đã hoàn thêm soLuong vào lô; không cho vượt số còn hoàn được
    void ghiNhanHoan(String maSP, String maLo, int soLuong) {
        int i = viTri(maSP, maLo);
        if (i < 0) {
            throw new IllegalStateException("Giao dịch " + maGiaoDich + " không xuất lô " + maLo + " của " + maSP + ".");
        }
        if (soLuong <= 0 || soLuong > soConHoanDuoc(maSP, maLo)) {
            throw new IllegalStateException("Giao dịch " + maGiaoDich + ": không hoàn thêm " + soLuong + " vào lô "
                    + maLo + " (còn hoàn được " + soConHoanDuoc(maSP, maLo) + ").");
        }
        daHoan.set(i, daHoan.get(i) + soLuong);
    }

    public int soDaXuat(String maSP, String maLo) {
        int i = viTri(maSP, maLo);
        return i < 0 ? 0 : daXuat.get(i).getSoLuong();
    }

    public int soDaHoan(String maSP, String maLo) {
        int i = viTri(maSP, maLo);
        return i < 0 ? 0 : daHoan.get(i);
    }

    public int soConHoanDuoc(String maSP, String maLo) {
        return soDaXuat(maSP, maLo) - soDaHoan(maSP, maLo);
    }

    // Số lượng đã xuất ròng (xuất trừ hoàn) của một lô, dùng để kiểm tra khớp với tồn kho khi đọc file
    public int soXuatRong(String maSP, String maLo) {
        return soConHoanDuoc(maSP, maLo);
    }

    // Danh sách (lô, số lượng) đã xuất, mỗi lô một dòng (bản sao)
    public ArrayList<LoXuat> getCacLoDaXuat() {
        return new ArrayList<LoXuat>(daXuat);
    }

    // Danh sách (lô, số lượng) còn hoàn được, mỗi lô một dòng, bỏ lô đã hoàn hết
    public ArrayList<LoXuat> getCacLoConHoanDuoc() {
        ArrayList<LoXuat> ketQua = new ArrayList<>();
        for (int i = 0; i < daXuat.size(); i++) {
            int con = daXuat.get(i).getSoLuong() - daHoan.get(i);
            if (con > 0) {
                ketQua.add(new LoXuat(daXuat.get(i).getMaSP(), daXuat.get(i).getMaLo(), con));
            }
        }
        return ketQua;
    }

    public int soDong() {
        return daXuat.size();
    }

    // Dòng ghi file: XK|maGiaoDich|maSP|maLo|soDaXuat|soDaHoan
    public ArrayList<String> toCacDongFile() {
        ArrayList<String> cacDong = new ArrayList<>();
        for (int i = 0; i < daXuat.size(); i++) {
            cacDong.add("XK|" + maGiaoDich + "|" + daXuat.get(i).getMaSP() + "|" + daXuat.get(i).getMaLo()
                    + "|" + daXuat.get(i).getSoLuong() + "|" + daHoan.get(i));
        }
        return cacDong;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Giao dịch xuất " + maGiaoDich + ":");
        for (int i = 0; i < daXuat.size(); i++) {
            sb.append(" [").append(daXuat.get(i)).append(", đã hoàn ").append(daHoan.get(i)).append("]");
        }
        return sb.toString();
    }
}
