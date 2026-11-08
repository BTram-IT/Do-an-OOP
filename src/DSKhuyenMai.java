import java.util.Scanner;

public class DSKhuyenMai {
    private Khuyenmai[] danhSach;
    private int soLuong;
    private static final int MAX = 100;
    private static final Scanner sc = new Scanner(System.in);

    // Constructor
    public DSKhuyenMai() {
        danhSach = new Khuyenmai[MAX];
        soLuong = 0;
    }

    // Getter
    public Khuyenmai[] getDanhSach() {
        return danhSach;
    }

    public int getSoLuong() {
        return soLuong;
    }

    // Tìm vị trí khuyến mãi theo mã
    public int timKhuyenMai(String maKM) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return -1;
        }
        String maChuan = SanPham.chuanHoaMa(maKM);
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] != null && danhSach[i].getMaKM() != null
                    && danhSach[i].getMaKM().equalsIgnoreCase(maChuan)) {
                return i;
            }
        }
        return -1;
    }

    // Tìm đối tượng khuyến mãi theo mã
    public Khuyenmai timTheoMa(String maKM) {
        int viTri = timKhuyenMai(maKM);
        return viTri == -1 ? null : danhSach[viTri];
    }

    // Thêm khuyến mãi
    public void themKhuyenMai(Khuyenmai km) {
        if (km == null || km.getMaKM() == null || km.getMaKM().trim().isEmpty()) {
            System.out.println("Khuyến mãi không hợp lệ!");
            return;
        }

        if (soLuong >= MAX) {
            System.out.println("Danh sách khuyến mãi đã đầy!");
            return;
        }

        if (timKhuyenMai(km.getMaKM()) != -1) {
            System.out.println("Mã khuyến mãi đã tồn tại!");
            return;
        }

        danhSach[soLuong] = km;
        soLuong++;

        System.out.println("Thêm khuyến mãi thành công!");
    }

    // Tìm kiếm khuyến mãi
    public void timKiemKhuyenMai() {
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần tìm: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKhuyenMai(maKM);

        if (viTri == -1) {
            System.out.println("Không tìm thấy khuyến mãi!");
            return;
        }

        System.out.println("Thông tin khuyến mãi:");
        System.out.println(danhSach[viTri]);
    }

    // Sửa khuyến mãi
    public void suaKhuyenMai() {
        String ma = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần sửa: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKhuyenMai(ma);

        if (viTri == -1) {
            System.out.println("Không tìm thấy khuyến mãi!");
            return;
        }

        Khuyenmai tam = danhSach[viTri].taoKhuyenMaiRong();
        tam.setKM();

        if (trungMa(tam.getMaKM(), viTri)) {
            System.out.println("Mã khuyến mãi đã tồn tại! Không thể sửa.");
            return;
        }

        danhSach[viTri] = tam;
        System.out.println("Sửa khuyến mãi thành công!");
    }

    // Kiểm tra trùng mã
    public boolean trungMa(String maKM, int viTriBoQua) {
        if (maKM == null || maKM.trim().isEmpty()) {
            return false;
        }
        String maChuan = SanPham.chuanHoaMa(maKM);
        for (int i = 0; i < soLuong; i++) {
            if (i != viTriBoQua && danhSach[i] != null
                    && danhSach[i].getMaKM() != null
                    && danhSach[i].getMaKM().equalsIgnoreCase(maChuan)) {
                return true;
            }
        }
        return false;
    }

    // Xóa khuyến mãi
    public boolean xoaKhuyenMai() {
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần xóa: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKhuyenMai(maKM);

        if (viTri == -1) {
            System.out.println("Không tìm thấy khuyến mãi!");
            return false;
        }

        for (int i = viTri; i < soLuong - 1; i++) {
            danhSach[i] = danhSach[i + 1];
        }

        danhSach[soLuong - 1] = null;
        soLuong--;

        System.out.println("Xóa khuyến mãi thành công!");
        return false;
    }

    // Xuất danh sách khuyến mãi
    public void xuatDanhSach() {
        if (soLuong == 0) {
            System.out.println("Danh sách khuyến mãi đang trống!");
            return;
        }

        System.out.println("========== DANH SÁCH KHUYẾN MÃI ==========");

        for (int i = 0; i < soLuong; i++) {
            System.out.println("Khuyến mãi thứ " + (i + 1));
            System.out.println(danhSach[i]);
            System.out.println("----------------------------------------");
        }

        System.out.println("Tổng số chương trình khuyến mãi: " + soLuong);
    }
}
