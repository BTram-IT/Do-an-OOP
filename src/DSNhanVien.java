import java.util.ArrayList;
import java.util.List;

public class DSNhanVien {
    private List<Nhanvien> dsNhanVien = new ArrayList<>();

    public DSNhanVien() {
    }

    public DSNhanVien(List<Nhanvien> dsNhanVien) {
        if (dsNhanVien != null) {
            this.dsNhanVien = dsNhanVien;
        }
    }

    public void themNhanVien(Nhanvien nv) {
        if (nv == null) {
            System.out.println("-> Nhan vien khong hop le!");
            return;
        }
        if (timKiemTheoMa(nv.getMaNV()) != null) {
            System.out.println("-> Ma nhan vien " + nv.getMaNV() + " da ton tai!");
            return;
        }
        dsNhanVien.add(nv);
        System.out.println("-> Da them nhan vien: " + nv.getHoten());
    }

    public boolean xoaNhanVien(String maNV) {
        Nhanvien nv = timKiemTheoMa(maNV);
        if (nv != null) {
            dsNhanVien.remove(nv);
            System.out.println("-> Da xoa nhan vien co ma: " + maNV);
            return true;
        }
        System.out.println("-> Khong tim thay nhan vien co ma: " + maNV);
        return false;
    }

    public boolean capNhatNhanVien(String maNV, String viTriMoi, double luongMoi) {
        Nhanvien nv = timKiemTheoMa(maNV);
        if (nv != null) {
            nv.setVitri(viTriMoi);
            nv.setLuong(luongMoi);
            System.out.println("-> Da cap nhat nhan vien: " + maNV);
            return true;
        }
        System.out.println("-> Khong tim thay nhan vien co ma: " + maNV);
        return false;
    }

    public Nhanvien timKiemTheoMa(String maNV) {
        if (maNV == null) return null;
        for (Nhanvien nv : dsNhanVien) {
            if (maNV.equalsIgnoreCase(nv.getMaNV())) {
                return nv;
            }
        }
        return null;
    }

    public List<Nhanvien> timKiemTheoTen(String ten) {
        List<Nhanvien> ketQua = new ArrayList<>();
        if (ten == null) return ketQua;
        String lower = ten.toLowerCase();
        for (Nhanvien nv : dsNhanVien) {
            if (nv.getHoten() != null && nv.getHoten().toLowerCase().contains(lower)) {
                ketQua.add(nv);
            }
        }
        return ketQua;
    }

    public double tinhTongQuyLuong() {
        double tong = 0;
        for (Nhanvien nv : dsNhanVien) {
            tong += nv.getLuong();
        }
        return tong;
    }

    public void hienThiDanhSach() {
        System.out.println("\n--- DANH SACH NHAN VIEN (" + dsNhanVien.size() + ") ---");
        if (dsNhanVien.isEmpty()) {
            System.out.println("(Danh sach nhan vien dang trong)");
            return;
        }
        for (Nhanvien nv : dsNhanVien) {
            nv.Xuat();
        }
    }

    public List<Nhanvien> getDsNhanVien() {
        return dsNhanVien;
    }

    public int soLuongNhanVien() {
        return dsNhanVien.size();
    }
}
