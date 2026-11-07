import java.util.ArrayList;
import java.util.Scanner;

public class DSHoadon {
    private ArrayList<HoaDon> ds;

    // Constructor mặc định
    public DSHoadon() {
        this.ds = new ArrayList<>();
    }

    // Lấy số lượng hóa đơn
    public int getSoLuong() {
        return ds.size();
    }

    // Lấy bản sao danh sách hóa đơn
    public ArrayList<HoaDon> getDanhSach() {
        return new ArrayList<>(ds);
    }

    // Lấy danh sách gốc (tương thích với KeToan.java)
    public ArrayList<HoaDon> getDs() {
        return ds;
    }

    // Lấy riêng danh sách các Hóa đơn bán
    public ArrayList<Hoadonban> getDanhSachHoaDonBan() {
        ArrayList<Hoadonban> ketQua = new ArrayList<>();
        for (HoaDon hd : ds) {
            if (hd instanceof Hoadonban) {
                ketQua.add((Hoadonban) hd);
            }
        }
        return ketQua;
    }

    // Tìm hóa đơn theo mã
    public HoaDon timTheoMa(String maHD) {
        if (maHD == null || maHD.trim().isEmpty()) {
            return null;
        }

        String maChuan = SanPham.chuanHoaMa(maHD);

        for (HoaDon hd : ds) {
            if (hd != null && hd.getMaHD() != null
                    && hd.getMaHD().equalsIgnoreCase(maChuan)) {
                return hd;
            }
        }

        return null;
    }

    // Kiểm tra mã hóa đơn đã tồn tại chưa
    public boolean daTonTaiMa(String maHD) {
        return timTheoMa(maHD) != null;
    }

    // Thêm hóa đơn
    public boolean them(HoaDon hd) {
        if (hd == null) {
            System.out.println("Hóa đơn không hợp lệ!");
            return false;
        }

        if (hd.getMaHD() == null || hd.getMaHD().trim().isEmpty()) {
            System.out.println("Mã hóa đơn không được để trống!");
            return false;
        }

        hd.setMaHD(SanPham.chuanHoaMa(hd.getMaHD()));

        if (daTonTaiMa(hd.getMaHD())) {
            System.out.println("Mã hóa đơn đã tồn tại: " + hd.getMaHD());
            return false;
        }

        ds.add(hd);
        System.out.println("Thêm hóa đơn thành công!");
        return true;
    }

    // Xuất toàn bộ danh sách hóa đơn
    public void xuatDS() {
        if (ds.isEmpty()) {
            System.out.println("Danh sách hóa đơn đang trống!");
            return;
        }

        System.out.println("\n========== DANH SÁCH HÓA ĐƠN (" + ds.size() + ") ==========");
        for (HoaDon hd : ds) {
            if (hd != null) {
                hd.inhoadon();
            }
        }
    }

    // Xuất hóa đơn theo mã
    public boolean xuatTheoMa(String maHD) {
        HoaDon hd = timTheoMa(maHD);

        if (hd == null) {
            System.out.println("Không tìm thấy hóa đơn có mã: " + maHD);
            return false;
        }

        hd.inhoadon();
        return true;
    }

    // Xóa hóa đơn theo mã (chỉ cho phép xóa hóa đơn chưa thanh toán)
    public boolean xoa(String maHD) {
        HoaDon hd = timTheoMa(maHD);

        if (hd == null) {
            System.out.println("Không tìm thấy hóa đơn!");
            return false;
        }

        if (hd.isDaThanhToan()) {
            System.out.println("Không thể xóa hóa đơn đã thanh toán!");
            return false;
        }

        ds.remove(hd);
        System.out.println("Xóa hóa đơn thành công!");
        return true;
    }

    // Sửa hóa đơn theo mã (chỉ cho phép sửa hóa đơn chưa thanh toán)
    public boolean sua(String maHD, HoaDon hdMoi) {
        if (hdMoi == null) {
            System.out.println("Hóa đơn mới không hợp lệ!");
            return false;
        }

        HoaDon hdCu = timTheoMa(maHD);

        if (hdCu == null) {
            System.out.println("Không tìm thấy hóa đơn cần sửa!");
            return false;
        }

        if (hdCu.isDaThanhToan()) {
            System.out.println("Không thể sửa hóa đơn đã thanh toán!");
            return false;
        }

        if (hdCu.getClass() != hdMoi.getClass()) {
            System.out.println("Không thể đổi loại hóa đơn khi sửa!");
            return false;
        }

        if (hdMoi.getMaHD() == null || hdMoi.getMaHD().trim().isEmpty()) {
            System.out.println("Mã hóa đơn mới không được để trống!");
            return false;
        }

        hdMoi.setMaHD(SanPham.chuanHoaMa(hdMoi.getMaHD()));

        // Kiểm tra mã mới có bị trùng với hóa đơn khác không
        if (!hdCu.getMaHD().equalsIgnoreCase(hdMoi.getMaHD())
                && daTonTaiMa(hdMoi.getMaHD())) {
            System.out.println("Mã hóa đơn mới đã tồn tại!");
            return false;
        }

        int viTri = ds.indexOf(hdCu);
        ds.set(viTri, hdMoi);

        System.out.println("Sửa hóa đơn thành công!");
        return true;
    }

    // Tìm kiếm hóa đơn từ bàn phím
    public void timKiem(Scanner sc) {
        if (sc == null) {
            return;
        }
        String maHD = Ktradulieu.nhapChuoi(
                sc, "Nhập mã hóa đơn cần tìm: ", ".+", "Mã hóa đơn không được để trống!");
        xuatTheoMa(maHD);
    }
}
