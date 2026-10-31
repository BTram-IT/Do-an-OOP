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

    // Tìm khuyến mãi theo mã
    public int timKhuyenMai(String maKM) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaKM().equalsIgnoreCase(maKM)) {
                return i;
            }
        }
        return -1;
    }

    // Thêm khuyến mãi
    public void themKhuyenMai(Khuyenmai km) {
        if (km == null) {
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
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần sửa: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKhuyenMai(maKM);

        if (viTri == -1) {
            System.out.println("Không tìm thấy khuyến mãi!");
            return;
        }

        System.out.println("===== CHỌN THÔNG TIN CẦN SỬA =====");
        System.out.println("1. Sửa toàn bộ thông tin khuyến mãi");
        System.out.println("0. Thoát");

        int chon = Ktradulieu.luaChon(sc, "Nhập lựa chọn: ", 0, 1);

        switch (chon) {
            case 1:
                danhSach[viTri].setKM();

                // Kiểm tra mã sau khi sửa có bị trùng không
                for (int i = 0; i < soLuong; i++) {
                    if (i != viTri && danhSach[i].getMaKM()
                            .equalsIgnoreCase(danhSach[viTri].getMaKM())) {
                        System.out.println("Mã khuyến mãi đã tồn tại!");
                        return;
                    }
                }

                System.out.println("Cập nhật khuyến mãi thành công!");
                break;

            case 0:
                System.out.println("Đã hủy sửa!");
                break;
        }
    }

    // Xóa khuyến mãi
    public void xoaKhuyenMai() {
        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần xóa: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKhuyenMai(maKM);

        if (viTri == -1) {
            System.out.println("Không tìm thấy khuyến mãi!");
            return;
        }

        for (int i = viTri; i < soLuong - 1; i++) {
            danhSach[i] = danhSach[i + 1];
        }

        danhSach[soLuong - 1] = null;
        soLuong--;

        System.out.println("Xóa khuyến mãi thành công!");
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