package hoadon;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import sanpham.SanPham;
import util.Ktradulieu;

public class CTHDonNhap {

    private String maSP;
    private String tenSP;
    private int soLuongNhap;
    private long giaNhap;
    private double thueSuatNhap;
    private LocalDate hanSuDung;
    private String maHD;

    public CTHDonNhap(String maSP, String tenSP, int soLuongNhap, long giaNhap, double thueSuatNhap,
                      LocalDate hanSuDung) {
        this.maSP = SanPham.chuanHoaMa(SanPham.kiemTraVanBan(maSP, "Mã sản phẩm"));
        this.tenSP = SanPham.kiemTraVanBan(tenSP, "Tên sản phẩm");
        if (soLuongNhap <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0.");
        }
        if (giaNhap < 0 || giaNhap > SanPham.GIA_TOI_DA) {
            throw new IllegalArgumentException("Giá nhập phải từ 0 đến " + SanPham.GIA_TOI_DA + " đồng.");
        }
        try {
            Math.multiplyExact((long) soLuongNhap, giaNhap); // thành tiền không được tràn long
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Thành tiền (số lượng x giá nhập) quá lớn.");
        }
        this.soLuongNhap = soLuongNhap;
        this.giaNhap = giaNhap;
        this.thueSuatNhap = SanPham.kiemTraThueSuat(thueSuatNhap);
        this.hanSuDung = hanSuDung;
        this.maHD = ""; // sẽ đc gán khi thêm vào hóa đơn nhập
    }

    // Nhập thông tin chi tiết hóa đơn nhập
    public static CTHDonNhap Nhap(Scanner sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Scanner không hợp lệ!");
        }

        // Nhập mã sản phẩm
        String maSP = Ktradulieu.nhapChuoi(
                sc,
                "Nhập mã sản phẩm (TA### hoặc DU###): ",
                "(?i)(TA|DU)\\d{3}",
                "Mã sản phẩm không hợp lệ!"
        );

        // Nhập tên sản phẩm
        String tenSP = Ktradulieu.nhapChuoi(
                sc,
                "Nhập tên sản phẩm: ",
                ".+",
                "Tên sản phẩm không được để trống!"
        );

        // Nhập số lượng
        long soLuong;
        do {
            soLuong = Ktradulieu.nhapSoNguyen(
                    sc,
                    "Nhập số lượng nhập: "
            );

            if (soLuong > Integer.MAX_VALUE) {
                System.out.println("Số lượng quá lớn! Vui lòng nhập lại.");
            }
        } while (soLuong > Integer.MAX_VALUE);

        // Nhập giá nhập, cho phép bằng 0
        long giaNhap;

        while (true) {
            String gia = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhập giá nhập: ",
                    "\\d+",
                    "Giá nhập phải là số nguyên không âm!"
            );

            try {
                giaNhap = Long.parseLong(gia);

                if (giaNhap > SanPham.GIA_TOI_DA) {
                    System.out.println(
                            "Giá nhập không được vượt quá "
                                    + SanPham.GIA_TOI_DA + " đồng!"
                    );
                    continue;
                }

                break;
            } catch (NumberFormatException e) {
                System.out.println("Giá nhập quá lớn! Vui lòng nhập lại.");
            }
        }

        // Nhập thuế suất, cho phép bằng 0
        double thueSuat;

        while (true) {
            String thue = Ktradulieu.nhapChuoi(
                    sc,
                    "Nhập thuế suất nhập (%): ",
                    "\\d+(\\.\\d+)?",
                    "Thuế suất phải là số không âm!"
            );

            try {
                thueSuat = Double.parseDouble(thue);

                if (!Double.isFinite(thueSuat)) {
                    System.out.println("Thuế suất không hợp lệ!");
                    continue;
                }

                thueSuat = SanPham.kiemTraThueSuat(thueSuat);
                break;

            } catch (NumberFormatException e) {
                System.out.println("Thuế suất không hợp lệ!");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Nhập hạn sử dụng, được phép bỏ trống
        LocalDate hanSuDung = null;

        while (true) {
            System.out.println(
                    "Nhập hạn sử dụng (yyyy-MM-dd), bỏ trống nếu không có:"
            );

            String ngay = sc.nextLine().trim();

            if (ngay.isEmpty()) {
                break;
            }

            try {
                hanSuDung = LocalDate.parse(ngay);
                break;
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Ngày không hợp lệ! Nhập theo dạng yyyy-MM-dd."
                );
            }
        }

        return new CTHDonNhap(
                maSP,
                tenSP,
                (int) soLuong,
                giaNhap,
                thueSuat,
                hanSuDung
        );
    }

    public String getMaHD() {
    return maHD;
}

    // Gắn mã hóa đơn khi thêm chi tiết vào hóa đơn nhập
    void ganMaHD(String maHD) {
       if (this.maHD.isEmpty()) {
        this.maHD = maHD;
       }
   }

    public String getMaSP() {
        return maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public long getGiaNhap() {
        return giaNhap;
    }

    public double getThueSuatNhap() {
        return thueSuatNhap;
    }

    // null = không có hạn sử dụng
    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    // Thành tiền = số lượng x giá nhập (chưa thuế)
    public long getThanhTien() {
        return soLuongNhap * giaNhap; // đã kiểm tra không tràn ở constructor
    }

    // Tổng tiền của chi tiết hóa đơn nhập
    public long Tongtien() { 
        return getThanhTien();
    }

    // Tiền thuế nhập của dòng = thành tiền x thueSuatNhap%, làm tròn đến đồng
    public long tienThueNhap() {
        return Math.round(getThanhTien() * thueSuatNhap / 100.0);
    }

    // Dòng ghi file: CT|maSP|tenSP|soLuongNhap|giaNhap|thueSuatNhap|hanSuDung
    public String toDongFile() {
        String hanChuoi = "";
        if (hanSuDung != null) {
            hanChuoi = hanSuDung.toString();
        }
        return "CT|" + maSP + "|" + tenSP + "|" + soLuongNhap + "|" + giaNhap + "|" + thueSuatNhap + "|" + hanChuoi;
    }

    // Đọc lại từ dòng file, sai định dạng thì ném IllegalArgumentException
    public static CTHDonNhap tuDongFile(String dong) {
        String[] t = dong.split("\\|", -1);
        if (t.length != 7 || !t[0].equals("CT")) {
            throw new IllegalArgumentException("Dòng chi tiết cần dạng CT|maSP|tenSP|soLuong|giaNhap|thueSuat|hanSuDung.");
        }
        LocalDate han = null;
        if (!t[6].isEmpty()) {
            han = LocalDate.parse(t[6]);
        }
        return new CTHDonNhap(t[1], t[2], Integer.parseInt(t[3]), Long.parseLong(t[4]), Double.parseDouble(t[5]), han);
    }

    @Override
    public String toString() {
        String han = "không HSD";
        if (hanSuDung != null) {
            han = "HSD " + SanPham.dinhDangNgay(hanSuDung);
       }

        return "MaHD: " + maHD
            + " | MaSP: " + maSP
            + " | TenSP: " + tenSP
            + " | So luong: " + soLuongNhap
            + " | Gia ban: " + SanPham.dinhDangTien(giaNhap)
            + " | Tong tien: " + SanPham.dinhDangTien(Tongtien())
            + " | Thuế nhập: " + thueSuatNhap + "%"
            + " | " + han;
    }
}