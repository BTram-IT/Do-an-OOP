package ConNguoi;

import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;

public abstract class NhanVien extends Nguoidung {
    private String maNV;
    private double luongCoBan;
    private LocalDate ngayVaoLam;
    private String chucVu;

    public NhanVien() {
        super();
        maNV = "";
        luongCoBan = 5000000;
        ngayVaoLam = LocalDate.now();
        chucVu = "Nhan vien";
    }

    public NhanVien(String ten, String sdt, String diaChi, String email,
                    LocalDate ngaySinh, String maNV, double luongCoBan,
                    LocalDate ngayVaoLam, String chucVu) {
        super(ten, sdt, diaChi, email, ngaySinh);
        setMaNV(maNV);
        setLuongCoBan(luongCoBan);
        setNgayVaoLam(ngayVaoLam);
        setChucVu(chucVu);
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) {
        maNV = Ktradulieu.chuanHoaMa(maNV);

        if (maNV == null || !maNV.matches("(NV|TN|KT|QL)\\d{3}")) {
            throw new IllegalArgumentException("Mã nhân viên không hợp lệ!");
        }

        this.maNV = maNV;
    }

    public double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(double luongCoBan) {
        if (!Double.isFinite(luongCoBan) || luongCoBan <= 0)
            throw new IllegalArgumentException("Lương cơ bản phải lớn hơn 0!");
        this.luongCoBan = luongCoBan;
    }

    public LocalDate getNgayVaoLam() { return ngayVaoLam; }
    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        if (ngayVaoLam == null || ngayVaoLam.isAfter(LocalDate.now()))
            throw new IllegalArgumentException("Ngày vào làm không hợp lệ!");
        this.ngayVaoLam = ngayVaoLam;
    }

    public String getChucVu() { return chucVu; }
    public void setChucVu(String chucVu) {
        if (chucVu == null || chucVu.trim().isEmpty())
            throw new IllegalArgumentException("Chức vụ không được để trống!");
        this.chucVu = chucVu.trim();
    }

    public int TinhKinhNghiem(LocalDate ngayTinh) {
        if (ngayVaoLam == null || ngayTinh == null || ngayVaoLam.isAfter(ngayTinh))
            return 0;
        Period p = Period.between(ngayVaoLam, ngayTinh);
        return (p.getYears() * 12 + p.getMonths()) / 6;
    }

    public int TinhKinhNghiem() {
        return TinhKinhNghiem(LocalDate.now());
    }

    public int getKinhNghiem() {
        return TinhKinhNghiem();
    }

    public double Tinhluong(LocalDate ngayTinh) {
        if (ngayTinh == null) throw new IllegalArgumentException("Ngày tính lương không hợp lệ!");
        return luongCoBan + TinhKinhNghiem(ngayTinh) * 500000;
    }

    public double Tinhluong() {
        return Tinhluong(LocalDate.now());
    }

    @Override
    public void nhap(Scanner sc) {
        if (sc == null) throw new IllegalArgumentException("Scanner không hợp lệ!");

        setMaNV(Ktradulieu.nhapChuoi(sc, "Nhập mã nhân viên: ",
                "NV\\d{3}", "Mã phải có dạng NV001!"));
        super.nhap(sc);
        setLuongCoBan(Ktradulieu.nhapSoNguyen(sc, "Nhập lương cơ bản: "));
        setChucVu(Ktradulieu.nhapChuoi(sc, "Nhập chức vụ: ",
                "^[\\p{L}]+(?:\\s[\\p{L}]+)*$", "Chức vụ không hợp lệ!"));
        setNgayVaoLam(LocalDate.now());
    }

    @Override
    public String toString() {
        return super.toString()
                + " | MaNV: " + maNV
                + " | Chuc vu: " + chucVu
                + " | Ngay vao lam: " + ngayVaoLam
                + " | Kinh nghiem: " + TinhKinhNghiem() + " muc"
                + " | Luong: " + String.format("%,.0f", Tinhluong()) + " VND";
    }

    public abstract String getVaiTro();
}
