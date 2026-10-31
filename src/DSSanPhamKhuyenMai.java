import java.util.ArrayList;
import java.util.Scanner;

public class DSSanPhamKhuyenMai {
    private ArrayList<SPKhMai> danhSach;
    private static final Scanner sc = new Scanner(System.in);

    // Constructor
    public DSSanPhamKhuyenMai() {
        danhSach = new ArrayList<>();
    }

    // Getter
    public ArrayList<SPKhMai> getDanhSach() {
        return danhSach;
    }

    public int getSoLuong() {
        return danhSach.size();
    }

    // Tìm kiếm sản phẩm khuyến mãi
    public int timKiem(String maSP, String maKM) {
        for (int i = 0; i < danhSach.size(); i++) {
            if (danhSach.get(i).getMaSP().equalsIgnoreCase(maSP)
                    && danhSach.get(i).getMaKM().equalsIgnoreCase(maKM)) {
                return i;
            }
        }
        return -1;
    }

    // Thêm sản phẩm khuyến mãi
    public void themSP() {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm: ", ".+",
                "Mã sản phẩm không được để trống!");

        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi: ", ".+",
                "Mã khuyến mãi không được để trống!");

        if (timKiem(maSP, maKM) != -1) {
            System.out.println("Sản phẩm đã thuộc chương trình khuyến mãi này!");
            return;
        }

        danhSach.add(new SPKhMai(maSP, maKM));
        System.out.println("Thêm sản phẩm khuyến mãi thành công!");
    }

    // Sửa sản phẩm khuyến mãi
    public void suaSP() {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm cần sửa: ", ".+",
                "Mã sản phẩm không được để trống!");

        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi cần sửa: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKiem(maSP, maKM);

        if (viTri == -1) {
            System.out.println("Không tìm thấy sản phẩm khuyến mãi!");
            return;
        }

        System.out.println("\n===== CHỌN THÔNG TIN CẦN SỬA =====");
        System.out.println("1. Sửa mã sản phẩm");
        System.out.println("2. Sửa mã khuyến mãi");
        System.out.println("3. Sửa cả hai");
        System.out.println("0. Thoát");

        int chon = Ktradulieu.luaChon(sc, "Nhập lựa chọn: ", 0, 3);

        String maSPMoi = maSP;
        String maKMMoi = maKM;

        switch (chon) {
            case 1:
                maSPMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã sản phẩm mới: ", ".+",
                        "Mã sản phẩm không được để trống!");
                break;

            case 2:
                maKMMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã khuyến mãi mới: ", ".+",
                        "Mã khuyến mãi không được để trống!");
                break;

            case 3:
                maSPMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã sản phẩm mới: ", ".+",
                        "Mã sản phẩm không được để trống!");

                maKMMoi = Ktradulieu.nhapChuoi(
                        sc, "Nhập mã khuyến mãi mới: ", ".+",
                        "Mã khuyến mãi không được để trống!");
                break;

            case 0:
                System.out.println("Đã hủy sửa!");
                return;
        }

        // Kiểm tra trùng dữ liệu
        int viTriMoi = timKiem(maSPMoi, maKMMoi);

        if (viTriMoi != -1 && viTriMoi != viTri) {
            System.out.println("Sản phẩm khuyến mãi này đã tồn tại!");
            return;
        }

        danhSach.set(viTri, new SPKhMai(maSPMoi, maKMMoi));

        System.out.println("Sửa sản phẩm khuyến mãi thành công!");
    }

    // Xóa sản phẩm khuyến mãi
    public void xoaSP() {
        String maSP = Ktradulieu.nhapChuoi(
                sc, "Nhập mã sản phẩm cần xóa: ", ".+",
                "Mã sản phẩm không được để trống!");

        String maKM = Ktradulieu.nhapChuoi(
                sc, "Nhập mã khuyến mãi: ", ".+",
                "Mã khuyến mãi không được để trống!");

        int viTri = timKiem(maSP, maKM);

        if (viTri == -1) {
            System.out.println("Không tìm thấy sản phẩm khuyến mãi!");
            return;
        }

        danhSach.remove(viTri);
        System.out.println("Xóa sản phẩm khuyến mãi thành công!");
    }

    // Xuất danh sách sản phẩm khuyến mãi
    public void xuatDanhSach() {
        if (danhSach.isEmpty()) {
            System.out.println("Danh sách sản phẩm khuyến mãi đang trống!");
            return;
        }

        System.out.println("\n===== DANH SÁCH SẢN PHẨM KHUYẾN MÃI =====");

        for (int i = 0; i < danhSach.size(); i++) {
            System.out.println("Sản phẩm khuyến mãi thứ " + (i + 1));
            System.out.println(danhSach.get(i));
            System.out.println("----------------------------------------");
        }

        System.out.println("Tổng số sản phẩm khuyến mãi: " + danhSach.size());
    }
}