import java.util.ArrayList;

public class DSHoadon {
    private ArrayList<HoaDon> ds;

    // Constructor mac dinh
    public DSHoadon() {
        ds = new ArrayList<>();
    }

    // lấy sl hóa đơn
    public int getSoLuong() {
        return ds.size();
    }

    // Lấy ds hóa đơn
    public ArrayList<HoaDon> getDanhSach() {
        return new ArrayList<>(ds);
    }

    // Tiìm hđ theo mã
    public HoaDon timTheoMa(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) {
            return null;
        }

        for (HoaDon hd : ds) {
            if (hd.getMaHD() != null
                    && hd.getMaHD().equalsIgnoreCase(maHD.trim())) {
                return hd;
            }
        }

        return null;
    }

    // ktr hóa đơn tôồn tại k
    public boolean daTonTaiMa(String maHD) {
        return timTheoMa(maHD) != null;
    }

    // Them hoa don
    public boolean them(HoaDon hd) {
        if (hd == null) {
            System.out.println("Hoa don khong hop le!");
            return false;
        }

        if (hd.getMaHD() == null
                || hd.getMaHD().trim().isEmpty()) {
            System.out.println("Ma hoa don khong duoc de trong!");
            return false;
        }

        if (daTonTaiMa(hd.getMaHD())) {
            System.out.println("Ma hoa don da ton tai!");
            return false;
        }

        ds.add(hd);
        System.out.println("Them hoa don thanh cong!");
        return true;
    }


    public void xuatDS() {
        if (ds.isEmpty()) {
            System.out.println("Danh sach hoa don dang trong!");
            return;
        }

        for (HoaDon hd : ds) {
            hd.inhoadon();
        }
    }

    // xuất hđ theo mã
    public boolean xuatTheoMa(String maHD) {
        HoaDon hd = timTheoMa(maHD);

        if (hd == null) {
            System.out.println("Khong tim thay hoa don!");
            return false;
        }

        hd.inhoadon();
        return true;
    }

    // xóa hđ theo mã
    public boolean xoa(String maHD) {
        HoaDon hd = timTheoMa(maHD);

        if (hd == null) {
            System.out.println("Khong tim thay hoa don!");
            return false;
        }

        if (hd.isDaThanhToan()) {
            System.out.println(
                    "Khong the xoa hoa don da thanh toan!"
            );
            return false;
        }

        ds.remove(hd);
        System.out.println("Xoa hoa don thanh cong!");
        return true;
    }

    // sửa hóa đơn theo mã
    public boolean sua(String maHD, HoaDon hdMoi) {
        if (hdMoi == null) {
            System.out.println("Hoa don moi khong hop le!");
            return false;
        }

        HoaDon hdCu = timTheoMa(maHD);

        if (hdCu == null) {
            System.out.println("Khong tim thay hoa don!");
            return false;
        }

        if (hdCu.isDaThanhToan()) {
            System.out.println(
                    "Khong the sua hoa don da thanh toan!"
            );
            return false;
        }

        if (hdMoi.getMaHD() == null
                || hdMoi.getMaHD().trim().isEmpty()) {
            System.out.println("Ma hoa don khong duoc de trong!");
            return false;
        }

        // Kiem tra ma moi co bi trung khong
        if (!hdCu.getMaHD().equalsIgnoreCase(hdMoi.getMaHD().trim())
                && daTonTaiMa(hdMoi.getMaHD())) {
            System.out.println("Ma hoa don moi da ton tai!");
            return false;
        }

        int viTri = ds.indexOf(hdCu);
        ds.set(viTri, hdMoi);

        System.out.println("Sua hoa don thanh cong!");
        return true;
    }
}