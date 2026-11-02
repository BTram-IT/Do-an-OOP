import java.time.LocalDate;
import java.time.Period;
import java.util.Scanner;

public class NhanVien extends Nguoidung {

    private String maNV;
    private double luongCoBan;
    private LocalDate ngayVaoLam;
    private String chucVu;

    // Constructor mặc định
    public NhanVien() {
        super();

        this.maNV = "";
        this.luongCoBan = 5000000;
        this.ngayVaoLam = LocalDate.now();
        this.chucVu = "Nhan vien";
    }

    // Constructor đầy đủ
    public NhanVien(String ten, String sdt, String diaChi,
                    String email, LocalDate ngaySinh,
                    String maNV, double luongCoBan,
                    LocalDate ngayVaoLam, String chucVu) {

        super(ten, sdt, diaChi, email, ngaySinh);

        setMaNV(maNV);
        setLuongCoBan(luongCoBan);
        setNgayVaoLam(ngayVaoLam);
        setChucVu(chucVu);
    }

    // Getter và Setter
    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ma nhan vien khong duoc de trong!"
            );
        }

        this.maNV = maNV.trim().toUpperCase();
    }

    public double getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(double luongCoBan) {
        if (!Double.isFinite(luongCoBan) || luongCoBan <= 0) {
            throw new IllegalArgumentException(
                    "Luong co ban phai lon hon 0!"
            );
        }

        this.luongCoBan = luongCoBan;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        if (ngayVaoLam == null || ngayVaoLam.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Ngay vao lam khong hop le!"
            );
        }

        this.ngayVaoLam = ngayVaoLam;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        if (chucVu == null || chucVu.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Chuc vu khong duoc de trong!"
            );
        }

        this.chucVu = chucVu.trim();
    }

    // Tinh kinh nghiem tai mot ngay cu the
    // Moi du 6 thang tang 1 muc kinh nghiem
    public int TinhKinhNghiem(LocalDate ngayTinh) {

        if (ngayVaoLam == null || ngayTinh == null
                || ngayVaoLam.isAfter(ngayTinh)) {
            return 0;
        }

        Period p = Period.between(ngayVaoLam, ngayTinh);

        int soThang = p.getYears() * 12 + p.getMonths();

        return soThang / 6;
    }

    // Tinh kinh nghiem den ngay hien tai
    public int TinhKinhNghiem() {
        return TinhKinhNghiem(LocalDate.now());
    }

    // Getter kinh nghiem neu cac lop khac can su dung
    public int getKinhNghiem() {
        return TinhKinhNghiem();
    }

    // Tinh luong tai mot ngay cu the
    public double Tinhluong(LocalDate ngayTinh) {

        if (ngayTinh == null) {
            throw new IllegalArgumentException(
                    "Ngay tinh luong khong hop le!"
            );
        }

        int mucKinhNghiem = TinhKinhNghiem(ngayTinh);

        return luongCoBan + mucKinhNghiem * 200000;
    }

    // Tinh luong hien tai
    public double Tinhluong() {
        return Tinhluong(LocalDate.now());
    }

    // Nhap thong tin nhan vien
    @Override
    public void nhap(Scanner sc) {

        if (sc == null) {
            throw new IllegalArgumentException(
                    "Scanner khong hop le!"
            );
        }

        this.maNV = Ktradulieu.nhapChuoi(
                sc,
                "Nhap ma nhan vien: ",
                "NV\\d{3}",
                "Ma nhan vien khong hop le! Vi du: NV001"
        ).toUpperCase();

        super.nhap(sc);

        this.luongCoBan = Ktradulieu.nhapSoNguyen(
                sc,
                "Nhap luong co ban: "
        );

        this.chucVu = Ktradulieu.nhapChuoi(
                sc,
                "Nhap chuc vu: ",
                "^[\\p{L}]+(?:\\s[\\p{L}]+)*$",
                "Chuc vu khong hop le!"
        ).trim();

        this.ngayVaoLam = LocalDate.now();
    }

    // Hien thi thong tin nhan vien
    @Override
    public String toString() {

        return super.toString()
                + " | MaNV: " + maNV
                + " | Chuc vu: " + chucVu
                + " | Ngay vao lam: " + ngayVaoLam
                + " | Muc kinh nghiem: " + TinhKinhNghiem()
                + " | Luong thuc nhan: "
                + String.format("%,.0f", Tinhluong())
                + " VND";
    }
}