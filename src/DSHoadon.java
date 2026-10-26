import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class DSHoadon {
    private HoaDon[] ds;
    private int n;

    // Constructor mac dinh
    public DSHoadon() {
        this.ds = new HoaDon[0];
        this.n = 0;
    }

    // Constructor co tham so
    public DSHoadon(HoaDon[] ds, int n) {
        if (ds == null || n < 0 || n > ds.length) {
            this.ds = new HoaDon[0];
            this.n = 0;
        } else {
            this.ds = Arrays.copyOf(ds, n);
            this.n = n;
        }
    }

    // Getter
    public int getSoLuong() {
        return n;
    }

    public HoaDon[] getDanhSach() {
        return Arrays.copyOf(ds, n);
    }

    // Tim hoa don theo ma
    public HoaDon timTheoMa(String maHD) {
        if (maHD == null) {
            return null;
        }

        for (int i = 0; i < n; i++) {
            if (ds[i] != null
                    && ds[i].getMaHD() != null
                    && ds[i].getMaHD().equalsIgnoreCase(maHD)) {
                return ds[i];
            }
        }

        return null;
    }

    // Kiem tra ma hoa don da ton tai
    public boolean daTonTaiMa(String maHD) {
        return timTheoMa(maHD) != null;
    }

    // Them hoa don da tao tu don hang
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
            System.out.println(
                    "Ma hoa don " + hd.getMaHD() + " da ton tai!"
            );
            return false;
        }

        ds = Arrays.copyOf(ds, n + 1);
        ds[n] = hd;
        n++;

        System.out.println(
                "Da them hoa don " + hd.getMaHD() + " thanh cong!"
        );

        return true;
    }

    // Them hoa don tu don hang
    public boolean themTuDonHang(
            String maHD,
            int thang,
            int nam,
            Donhang donHang,
            ThanhToan phuongThucTT) {

        if (daTonTaiMa(maHD)) {
            System.out.println("Ma hoa don da ton tai!");
            return false;
        }

        try {
            HoaDon hd = new HoaDon(
                    maHD,
                    thang,
                    nam,
                    donHang,
                    phuongThucTT
            );

            return them(hd);

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Khong the tao hoa don: " + e.getMessage()
            );
            return false;
        }
    }

    // Xuat danh sach hoa don
    public void xuatDS() {
        System.out.println(
                "\n===== DANH SACH HOA DON (" + n + " hoa don) ====="
        );

        if (n == 0) {
            System.out.println("Danh sach hoa don dang trong!");
            return;
        }

        for (int i = 0; i < n; i++) {
            ds[i].xuat();
        }
    }

    // Sua thong tin co ban cua hoa don
    public boolean sua(String maHD, Scanner sc) {
        HoaDon hd = timTheoMa(maHD);

        if (hd == null) {
            System.out.println(
                    "Khong tim thay hoa don ma: " + maHD
            );
            return false;
        }

        if (hd.isDaThanhToan()) {
            System.out.println(
                    "Hoa don da thanh toan, khong the sua!"
            );
            return false;
        }

        System.out.println("Nhap thong tin can cap nhat:");
        System.out.println(
                "Luu y: Chi cap nhat ma hoa don, thang va nam."
        );

        hd.nhap(sc);

        System.out.println("Cap nhat hoa don thanh cong!");
        return true;
    }

    // Xoa hoa don
    public boolean xoa(String maHD) {
        HoaDon hd = timTheoMa(maHD);

        if (hd == null) {
            System.out.println(
                    "Khong tim thay hoa don can xoa!"
            );
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

        n--;
        ds = Arrays.copyOf(ds, n);

        System.out.println(
                "Da xoa hoa don ma: " + maHD
        );

        return true;
    }

    // Tim va xuat mot hoa don
    public boolean xuatTheoMa(String maHD) {
        HoaDon hd = timTheoMa(maHD);

        if (hd == null) {
            System.out.println("Khong tim thay hoa don!");
            return false;
        }

        hd.xuat();
        return true;
    }


}
