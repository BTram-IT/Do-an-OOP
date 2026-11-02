
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Khuyenmai {
    protected String maKM;
    protected String tenKM;
    protected String loaiKM;
    protected LocalDateTime ngayBatDau;
    protected LocalDateTime ngayKetThuc;
    protected double mucChiToiThieu;

    protected ArrayList<SanPham> danhSachKM;
    protected static final Scanner sc = new Scanner(System.in);

    public Khuyenmai() {
        maKM = "";
        tenKM = "";
        loaiKM = "";
        ngayBatDau = LocalDateTime.now();
        ngayKetThuc = LocalDateTime.now();
        mucChiToiThieu = 0;
        danhSachKM = new ArrayList<>();
    }

    public Khuyenmai(String maKM, String tenKM, String loaiKM,
                     LocalDateTime ngayBatDau,
                     LocalDateTime ngayKetThuc,
                     double mucChiToiThieu,
                     ArrayList<SanPham> danhSachKM) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.loaiKM = loaiKM;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.mucChiToiThieu = mucChiToiThieu;

        if (danhSachKM == null) {
            this.danhSachKM = new ArrayList<>();
        } else {
            this.danhSachKM = danhSachKM;
        }
    }

    public void thietLapKhuyenMai() {
        thietLapKhuyenMai(sc);
    }

    public void thietLapKhuyenMai(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner khong hop le!");
        }

        DateTimeFormatter dinhDang =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        maKM = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma khuyen mai: ",
                ".+",
                "Ma khuyen mai khong duoc de trong!"
        );

        tenKM = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ten khuyen mai: ",
                ".+",
                "Ten khuyen mai khong duoc de trong!"
        );

        loaiKM = Ktradulieu.nhapChuoi(
                sc,
                "Nhap loai khuyen mai: ",
                ".+",
                "Loai khuyen mai khong duoc de trong!"
        );

        while (true) {
            String ngay = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhap ngay bat dau (yyyy-MM-dd HH:mm): ",
                    ".+",
                    "Ngay bat dau khong duoc de trong!"
            );

            try {
                ngayBatDau = LocalDateTime.parse(ngay, dinhDang);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ngay bat dau khong hop le!");
            }
        }

        while (true) {
            String ngay = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhap ngay ket thuc (yyyy-MM-dd HH:mm): ",
                    ".+",
                    "Ngay ket thuc khong duoc de trong!"
            );

            try {
                ngayKetThuc = LocalDateTime.parse(ngay, dinhDang);

                if (ngayKetThuc.isBefore(ngayBatDau)) {
                    System.out.println("Ngay ket thuc khong hop le!");
                    continue;
                }

                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ngay ket thuc khong hop le!");
            }
        }

        while (true) {
            String mucChi = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhap muc chi toi thieu: ",
                    "^[+]?(\\d+(\\.\\d*)?|\\.\\d+)$",
                    "Muc chi toi thieu phai la so khong am!"
            );

            try {
                mucChiToiThieu = Double.parseDouble(mucChi);

                if (!Double.isFinite(mucChiToiThieu)
                        || mucChiToiThieu < 0) {
                    System.out.println("Muc chi toi thieu khong hop le!");
                    continue;
                }

                break;
            } catch (NumberFormatException e) {
                System.out.println("Muc chi toi thieu khong hop le!");
            }
        }
    }

    public void themSP(SanPham sp) {
        if (sp == null) {
            System.out.println("San pham khong ton tai!");
            return;
        }

        if (timKiemSP(sp.getMaSP()) != -1) {
            System.out.println("San pham da co trong danh sach!");
            return;
        }

        danhSachKM.add(sp);
        System.out.println("Them san pham thanh cong!");
    }

    public void xoaSP(String maSP) {
        int viTri = timKiemSP(maSP);

        if (viTri == -1) {
            System.out.println("Khong tim thay san pham!");
            return;
        }

        danhSachKM.remove(viTri);
        System.out.println("Xoa san pham thanh cong!");
    }

    public int timKiemSP(String maSP) {
        for (int i = 0; i < danhSachKM.size(); i++) {
            if (danhSachKM.get(i).getMaSP()
                    .equalsIgnoreCase(maSP)) {
                return i;
            }
        }
        return -1;
    }

    public void inDanhSachSPKM() {
        if (danhSachKM.isEmpty()) {
            System.out.println("Danh sach san pham khuyen mai trong!");
            return;
        }

        for (SanPham sp : danhSachKM) {
            System.out.println(sp);
        }
    }

    public boolean kiemTraThoiHan() {
        LocalDateTime hienTai = LocalDateTime.now();

        return !hienTai.isBefore(ngayBatDau)
                && !hienTai.isAfter(ngayKetThuc);
    }

    public boolean kiemTraApDung(String maSP, double tongTien) {
        if (!kiemTraThoiHan() || tongTien < mucChiToiThieu) {
            return false;
        }

        return danhSachKM.isEmpty() || timKiemSP(maSP) != -1;
    }

    public String getMaKM() {
        return maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public String getLoaiKM() {
        return loaiKM;
    }

    public LocalDateTime getNgayBatDau() {
        return ngayBatDau;
    }

    public LocalDateTime getNgayKetThuc() {
        return ngayKetThuc;
    }

    public double getMucChiToiThieu() {
        return mucChiToiThieu;
    }

    public ArrayList<SanPham> getDanhSachKM() {
        return danhSachKM;
    }

    public abstract double traCuuGiaSanPhamSauKM(String maSP);

    @Override
    public String toString() {
        return "Ma khuyen mai: " + maKM
                + "\nTen khuyen mai: " + tenKM
                + "\nLoai khuyen mai: " + loaiKM
                + "\nNgay bat dau: " + ngayBatDau
                + "\nNgay ket thuc: " + ngayKetThuc
                + "\nMuc chi toi thieu: " + mucChiToiThieu;
    }
}
