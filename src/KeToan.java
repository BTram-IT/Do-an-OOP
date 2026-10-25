import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

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


    public void tinhTongThueTheoThang() {}
    public void tinhDoanhThuTheoThang() {}
    public void tinhDoanhThuTheoQuy() {}
    public void tinhDoanhThuTheoNam() {}
    public void tinhLoiNhuanTheoThang() {}
    public void tinhLoiNhuanTheoQuy() {}
    public void tinhLoiNhuanTheoNam() {}
    public void tinhBangLuong() {}
    public void baoCaoSanPhamBanChay() {}
    public void inBaoCaoTaiChinh() {}
}
