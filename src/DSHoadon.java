import java.util.Scanner;

public class DSHoadon {
    private static final int MAX = 100;
    private HoaDon[] ds;
    private int n;

    // Constructor mac dinh
    public DSHoadon() {
        ds = new HoaDon[MAX];
        n = 0;
    }

    // Constructor co tham so
    public DSHoadon(HoaDon[] ds, int n) {
        this.ds = new HoaDon[MAX];
        this.n = 0;

        if (ds != null && n >= 0 && n <= ds.length) {
            int soLuong = Math.min(n, MAX);

            for (int i = 0; i < soLuong; i++) {
                if (ds[i] != null) {
                    this.ds[this.n] = ds[i];
                    this.n++;
                }
            }
        }
    }

    public int getSoLuong() {
        return n;
    }

    public HoaDon[] getDanhSach() {
        HoaDon[] ketQua = new HoaDon[n];

        for (int i = 0; i < n; i++) {
            ketQua[i] = ds[i];
        }

        return ketQua;
    }

    public void setDs(HoaDon[] ds) {
        this.ds = ds;
    }

    public void setN(int n) {
        this.n = n;
    }

    // Tim hoa don theo ma
    public HoaDon timTheoMa(String maHD) {
        if (maHD == null) {
            return null;
        }

        for (int i = 0; i < n; i++) {
            if (ds[i].getMaHD() != null
                    && ds[i].getMaHD()
                    .equalsIgnoreCase(maHD.trim())) {
                return ds[i];
            }
        }

        return null;
    }

    public boolean daTonTaiMa(String maHD) {
        return timTheoMa(maHD) != null;
    }

    // Them hoa don da tao
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

        if (n >= MAX) {
            System.out.println("Danh sach hoa don da day!");
            return false;
        }

        ds[n] = hd;
        n++;

        System.out.println("Them hoa don thanh cong!");
        return true;
    }

    // Xuat danh sach hoa don
    public void xuatDS() {
        if (n == 0) {
            System.out.println("Danh sach hoa don dang trong!");
            return;
        }

        for (int i = 0; i < n; i++) {
            ds[i].inhoadon();
        }
    }

    // Xoa hoa don theo ma
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

        int viTri = -1;

        for (int i = 0; i < n; i++) {
            if (ds[i] == hd) {
                viTri = i;
                break;
            }
        }

        if (viTri == -1) {
            return false;
        }

        for (int i = viTri; i < n - 1; i++) {
            ds[i] = ds[i + 1];
        }

        ds[n - 1] = null;
        n--;

        System.out.println("Xoa hoa don thanh cong!");
        return true;
    }

    // Tim va xuat mot hoa don
    public boolean xuatTheoMa(String maHD) {
        HoaDon hd = timTheoMa(maHD);

        if (hd == null) {
            System.out.println("Khong tim thay hoa don!");
            return false;
        }

        hd.inhoadon();
        return true;
    }
}