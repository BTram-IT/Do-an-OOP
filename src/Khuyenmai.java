import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Khuyenmai {
    private String maKM;
    private String loaiKM;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private long mucChiToiThieu;
    protected static final Scanner sc = new Scanner(System.in);
    private ArrayList<SanPham> danhSachKM;

    public Khuyenmai() {
        maKM = "";
        loaiKM = "";
        ngayBatDau = LocalDate.now();
        ngayKetThuc = LocalDate.now();
        mucChiToiThieu = 0;
        danhSachKM = new ArrayList<>();
    }

    public Khuyenmai(String maKM, String loaiKM,
                     LocalDate ngayBatDau, LocalDate ngayKetThuc,
                     long mucChiToiThieu, ArrayList<SanPham> danhSachKM) {
        this.maKM = maKM;
        this.loaiKM = loaiKM;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.mucChiToiThieu = mucChiToiThieu;
        this.danhSachKM = danhSachKM;
    }

    public void setKM() {
        maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi: ", ".+",
                "Mã khuyến mãi không được để trống!");

        ngayBatDau = Ktradulieu.nhapNgaySinh(
                sc, "Nhập ngày bắt đầu (yyyy-MM-dd): ");

        do {
            ngayKetThuc = Ktradulieu.nhapNgaySinh(
                    sc, "Nhập ngày kết thúc (yyyy-MM-dd): ");

            if (ngayKetThuc.isBefore(ngayBatDau)) {
                System.out.println("Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!");
            }
        } while (ngayKetThuc.isBefore(ngayBatDau));

        mucChiToiThieu = Ktradulieu.nhapSoNguyen(
                sc, "Nhập mức chi tiêu tối thiểu: ");
    }

    public void inDanhSachSPKM() {
        if (danhSachKM.isEmpty()) {
            System.out.println("Danh sách sản phẩm khuyến mãi đang trống!");
            return;
        }

        for (SanPham sp : danhSachKM) {
            System.out.println(sp);
        }
    }

    public void themSP(SanPham sp) {
        if (sp == null) {
            System.out.println("Sản phẩm không tồn tại!");
            return;
        }

        if (timKiemSP(sp.getMaSP()) != -1) {
            System.out.println("Sản phẩm đã có trong danh sách khuyến mãi!");
            return;
        }

        danhSachKM.add(sp);
        System.out.println("Thêm sản phẩm thành công!");
    }

    public void xoaSP(String maSP) {
        int viTri = timKiemSP(maSP);

        if (viTri == -1) {
            System.out.println("Không tìm thấy sản phẩm!");
            return;
        }

        danhSachKM.remove(viTri);
        System.out.println("Xóa sản phẩm thành công!");
    }

    public int timKiemSP(String maSP) {
        for (int i = 0; i < danhSachKM.size(); i++) {
            if (danhSachKM.get(i).getMaSP().equalsIgnoreCase(maSP)) {
                return i;
            }
        }
        return -1;
    }

    public boolean kiemTraThoiHan() {
        LocalDate homNay = LocalDate.now();

        return !homNay.isBefore(ngayBatDau)
                && !homNay.isAfter(ngayKetThuc);
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

    public String getLoaiKM() {
        return loaiKM;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public long getMucChiToiThieu() {
        return mucChiToiThieu;
    }

    public ArrayList<SanPham> getDanhSachKM() {
        return danhSachKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public void setLoaiKM(String loaiKM) {
        this.loaiKM = loaiKM;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public void setMucChiToiThieu(long mucChiToiThieu) {
        this.mucChiToiThieu = mucChiToiThieu;
    }

    public void setDanhSachKM(ArrayList<SanPham> danhSachKM) {
        this.danhSachKM = danhSachKM;
    }

    public abstract double traCuuGiaSanPhamSauKM(String maSP);

    @Override
    public String toString() {
        return "Mã khuyến mãi: " + maKM
                + " | Loại khuyến mãi: " + loaiKM
                + " | Ngày bắt đầu: " + ngayBatDau
                + " | Ngày kết thúc: " + ngayKetThuc
                + " | Mức chi tiêu tối thiểu: " + mucChiToiThieu;
    }
}

