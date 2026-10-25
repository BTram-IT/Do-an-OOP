import java.util.ArrayList;
import java.util.List;

public class DsKhachHang {
    private List<Khachhang> dsKhachHang = new ArrayList<>();

    public DsKhachHang() {
    }

    public DsKhachHang(List<Khachhang> dsKhachHang) {
        if (dsKhachHang != null) {
            this.dsKhachHang = dsKhachHang;
        }
    }

    public void themKhachHang(Khachhang kh) {
        if (kh == null) {
            System.out.println("-> Khach hang khong hop le!");
            return;
        }
        if (timKiemTheoMa(kh.getMaKH()) != null) {
            System.out.println("-> Ma khach hang " + kh.getMaKH() + " da ton tai!");
            return;
        }
        dsKhachHang.add(kh);
        System.out.println("-> Da them khach hang: " + kh.getHoten());
    }

    public boolean xoaKhachHang(String maKH) {
        Khachhang kh = timKiemTheoMa(maKH);
        if (kh != null) {
            dsKhachHang.remove(kh);
            System.out.println("-> Da xoa khach hang co ma: " + maKH);
            return true;
        }
        System.out.println("-> Khong tim thay khach hang co ma: " + maKH);
        return false;
    }

    public boolean suaThongTinKhachHang(String maKH, String tenMoi, String sdtMoi, String emailMoi, String diaChiMoi) {
        Khachhang kh = timKiemTheoMa(maKH);
        if (kh != null) {
            kh.setHoten(tenMoi);
            kh.setSdt(sdtMoi);
            kh.setEmail(emailMoi);
            kh.setDiachi(diaChiMoi);
            System.out.println("-> Da cap nhat thong tin khach hang: " + maKH);
            return true;
        }
        System.out.println("-> Khong tim thay khach hang co ma: " + maKH);
        return false;
    }

    public Khachhang timKiemTheoMa(String maKH) {
        if (maKH == null) return null;
        for (Khachhang kh : dsKhachHang) {
            if (maKH.equalsIgnoreCase(kh.getMaKH())) {
                return kh;
            }
        }
        return null;
    }

    public List<Khachhang> timKiemTheoTen(String tuKhoa) {
        List<Khachhang> ketQua = new ArrayList<>();
        if (tuKhoa == null) return ketQua;
        String lower = tuKhoa.toLowerCase();
        for (Khachhang kh : dsKhachHang) {
            if (kh.getHoten() != null && kh.getHoten().toLowerCase().contains(lower)) {
                ketQua.add(kh);
            }
        }
        return ketQua;
    }

    public Khachhang timKiemTheoSDT(String sdt) {
        if (sdt == null) return null;
        for (Khachhang kh : dsKhachHang) {
            if (sdt.equals(kh.getSdt())) {
                return kh;
            }
        }
        return null;
    }

    public void hienThiDanhSach() {
        System.out.println("\n--- DANH SACH KHACH HANG (" + dsKhachHang.size() + ") ---");
        if (dsKhachHang.isEmpty()) {
            System.out.println("(Danh sach khach hang dang trong)");
            return;
        }
        for (Khachhang kh : dsKhachHang) {
            kh.Xuat();
        }
    }

    public List<Khachhang> getDsKhachHang() {
        return dsKhachHang;
    }

    public int soLuongKhachHang() {
        return dsKhachHang.size();
    }
}
