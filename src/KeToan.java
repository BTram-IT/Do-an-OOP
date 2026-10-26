import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

public class KeToan extends Nhanvien{
    private DSNhanVien NhanVien;
    private DSSanPham DSSanPham;
    private DSHoadon Hoadon;

    public KeToan(DSNhanVien nhanVien, DSSanPham DSSanPham, DSHoadon hoadon) {
        NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        Hoadon = hoadon;
    }

    public KeToan(String maNV, LocalDate ngaybatdaulam, String Vitri, String Hoten, String SDT, String Email, String Diachi, DSNhanVien nhanVien, DSSanPham DSSanPham, DSHoadon hoadon) {
        super(maNV, ngaybatdaulam, Vitri, Hoten, SDT, Email, Diachi);
        NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        Hoadon = hoadon;
    }

    public KeToan() {
        super();
        NhanVien = new DSNhanVien();
        DSSanPham = new DSSanPham();
        Hoadon = new DSHoadon();
    }

    public DSNhanVien getNhanVien() {
        return NhanVien;
    }

    public void setNhanVien(DSNhanVien nhanVien) {
        NhanVien = nhanVien;
    }

    public DSSanPham getDSSanPham() {
        return DSSanPham;
    }

    public void setDSSanPham(DSSanPham DSSanPham) {
        this.DSSanPham = DSSanPham;
    }

    public DSHoadon getHoadon() {
        return Hoadon;
    }

    public void setHoadon(DSHoadon hoadon) {
        Hoadon = hoadon;
    }


    public void tinhTongThueTheoThang() {
    }
    public void tinhDoanhThuTheoThang() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhap thang: ");
        int thang = Integer.parseInt(sc.nextLine());

        System.out.print("Nhap nam: ");
        int nam = Integer.parseInt(sc.nextLine());

        if (thang < 1 || thang > 12) {
            System.out.println("Thang khong hop le!");
            return;
        }

        double doanhThu = 0;

        for (HoaDon hd : Hoadon.getHoaDon()) {
            LocalDate ngay = LocalDate.from(hd.getThoiGianTao());

            if (ngay.getMonthValue() == thang
                    && ngay.getYear() == nam) {
                doanhThu += hd.tinhTongThanhToan();
            }
        }

        System.out.println("Doanh thu thang " + thang
                + "/" + nam + ": " + doanhThu);
    }
    public void tinhDoanhThuTheoQuy() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhap quy (1-4): ");
        int quy = Integer.parseInt(sc.nextLine());

        System.out.print("Nhap nam: ");
        int nam = Integer.parseInt(sc.nextLine());

        if (quy < 1 || quy > 4) {
            System.out.println("Quy khong hop le!");
            return;
        }

        int thangBatDau = (quy - 1) * 3 + 1;
        int thangKetThuc = thangBatDau + 2;

        double doanhThu = 0;

        for (HoaDon hd : Hoadon.getHoaDon()) {
            LocalDate ngay = LocalDate.from(hd.getThoiGianTao());

            if (ngay.getYear() == nam
                    && ngay.getMonthValue() >= thangBatDau
                    && ngay.getMonthValue() <= thangKetThuc) {
                doanhThu += hd.tinhTongThanhToan();
            }
        }

        System.out.println("Doanh thu quy " + quy
                + " nam " + nam + ": " + doanhThu);
    }
    public void tinhDoanhThuTheoNam() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Nhap nam: ");
        int nam = Integer.parseInt(sc.nextLine());

        double doanhThu = 0;

        for (HoaDon hd : Hoadon.getHoaDon()) {
            if (hd.getThoiGianTao().getYear() == nam) {
                doanhThu += hd.tinhTongThanhToan();
            }
        }

        System.out.println("Doanh thu nam " + nam
                + ": " + doanhThu);
    }
    public void tinhLoiNhuanTheoThang() {}
    public void tinhLoiNhuanTheoQuy() {}
    public void tinhLoiNhuanTheoNam() {}
    public void tinhBangLuong() {}
    public void baoCaoSanPhamBanChay() {}
    public void inBaoCaoTaiChinh() {}
}
