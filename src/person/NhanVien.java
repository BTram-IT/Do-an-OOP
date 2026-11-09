package person;

import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;
import util.Ktradulieu;

public abstract class NhanVien extends Nguoidung {
    private String maNV;
    private LocalDate ngayVaoLam;
    private String chucVu;

    public NhanVien() {
        super();
        maNV = "";
        ngayVaoLam = LocalDate.now();
        chucVu = "Nhan vien";
    }

    public NhanVien(String ten, String sdt, String diaChi, String email,
                    LocalDate ngaySinh, String maNV,
                    LocalDate ngayVaoLam, String chucVu) {
        super(ten, sdt, diaChi, email, ngaySinh);
        setMaNV(maNV);
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

    // Chuyển mức lương cơ bản thành phương thức trừu tượng
    // Mỗi lớp con (ThuNgan, KeToan, QuanLy...) sẽ override và return mức lương cố định riêng
    public abstract double getLuongCoBan();

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
        // Gọi phương thức getLuongCoBan() thay vì dùng thuộc tính
        return getLuongCoBan() + TinhKinhNghiem(ngayTinh) * 500000;
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
        // Đã bỏ dòng nhập lương cơ bản vì mức lương được gắn cứng theo từng chức vụ con
        setChucVu(Ktradulieu.nhapChuoi(sc, "Nhập chức vụ: ",
                "^[\\p{L}]+(?:\\s[\\p{L}]+)*$", "Chức vụ không hợp lệ!"));
        setNgayVaoLam(LocalDate.now());
    }

    @Override
    public String toString() {
        return super.toString()
                + " | MaNV: " + maNV
                + " | Chuc vu: " + chucVu
                + " | Luong co ban: " + String.format("%,.0f", getLuongCoBan()) + " VND"
                + " | Ngay vao lam: " + ngayVaoLam
                + " | Kinh nghiem: " + TinhKinhNghiem() + " muc"
                + " | Luong: " + String.format("%,.0f", Tinhluong()) + " VND";
    }

    public abstract String getVaiTro();
}
