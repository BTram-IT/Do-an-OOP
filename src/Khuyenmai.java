import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class Khuyenmai {
    private String MaKM, LoaiKM;
    private LocalDate NgayBatDau, NgayKetThuc;
    private long MucChiToiThieu;
    Scanner sc = new Scanner(System.in);
    private ArrayList<SanPham> DanhsachKM;

    public Khuyenmai() {
        MaKM = LoaiKM = "";
        NgayBatDau = NgayKetThuc = LocalDate.now();
        MucChiToiThieu = 0;
        DanhsachKM = new ArrayList<>();
    }

    public Khuyenmai(String maKM, String loaiKM, LocalDate ngayBatDau, LocalDate ngayKetThuc, long mucChiToiThieu, ArrayList<SanPham> danhsachKM) {
        MaKM = maKM;
        LoaiKM = loaiKM;
        NgayBatDau = ngayBatDau;
        NgayKetThuc = ngayKetThuc;
        MucChiToiThieu = mucChiToiThieu;
        DanhsachKM = (danhsachKM == null) ? new ArrayList<>() : danhsachKM;
    }

    public void thietlapKM() {
        MaKM = Ktradulieu.taoMa("KM", 1);
        LoaiKM = Ktradulieu.nhapChuoi(sc, "Nhập loại khuyến mãi: ", ".+", "Không được để trống!");

        NgayBatDau = Ktradulieu.nhapNgaySinh(sc, "Nhập ngày bắt đầu (yyyy-MM-dd): ");
        NgayKetThuc = Ktradulieu.nhapNgaySinh(sc, "Nhập ngày kết thúc (yyyy-MM-dd): ");

        while (!NgayKetThuc.isAfter(NgayBatDau)) {
            System.out.println("Ngày kết thúc phải sau ngày bắt đầu!");
            NgayKetThuc = Ktradulieu.nhapNgaySinh(sc, "Nhập lại ngày kết thúc: ");
        }

        MucChiToiThieu = Ktradulieu.nhapSoNguyen(sc, "Nhập mức chi tối thiểu: ");
    }

    public void InDanhSachSPKM() {
        if (DanhsachKM.isEmpty()) {
            System.out.println("Danh sách sản phẩm khuyến mãi trống!");
            return;
        }

        for (SanPham sp : DanhsachKM) {
            System.out.println(sp);
        }
    }

    public void ThemSP(SanPham sp) {
        if (sp == null) {
            System.out.println("Sản phẩm không hợp lệ!");
            return;
        }

        if (TimKiemSP(sp.getMaSP()) != -1) {
            System.out.println("Sản phẩm đã tồn tại trong khuyến mãi!");
            return;
        }

        DanhsachKM.add(sp);
        System.out.println("Thêm sản phẩm thành công!");
    }

    public void XoaSP(String maSP) {
        int vitri = TimKiemSP(maSP);

        if (vitri == -1) {
            System.out.println("Không tìm thấy sản phẩm!");
            return;
        }

        DanhsachKM.remove(vitri);
        System.out.println("Xóa sản phẩm thành công!");
    }

    public void SuaSP(String maSP, SanPham spMoi) {
        int vitri = TimKiemSP(maSP);

        if (vitri == -1) {
            System.out.println("Không tìm thấy sản phẩm!");
            return;
        }

        if (spMoi == null) {
            System.out.println("Sản phẩm mới không hợp lệ!");
            return;
        }

        int viTriTrung = TimKiemSP(spMoi.getMaSP());

        if (viTriTrung != -1 && viTriTrung != vitri) {
            System.out.println("Mã sản phẩm mới đã tồn tại!");
            return;
        }

        DanhsachKM.set(vitri, spMoi);
        System.out.println("Sửa sản phẩm thành công!");
    }

    public int TimKiemSP(String maSP) {
        for (int i = 0; i < DanhsachKM.size(); i++) {
            if (DanhsachKM.get(i).getMaSP().equalsIgnoreCase(maSP)) {
                return i;
            }
        }
        return -1;
    }

    public LocalDate getNgayBatDau() {
        return NgayBatDau;
    }

    public ArrayList<SanPham> getDanhsachKM() {
        return DanhsachKM;
    }

    public void setDanhsachKM(ArrayList<SanPham> danhsachKM) {
        DanhsachKM = (danhsachKM == null) ? new ArrayList<>() : danhsachKM;
    }
}