import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;


public class TienIch {

    // Dòng đầu file dữ liệu kho; phiên bản 2 = có giá nhập (SanPham.giaNhap, LoHang.giaNhap) và HoaDonNhap
    public static final String DONG_PHIEN_BAN = "#PHIENBAN|2";

    public static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ===== Định dạng =====

    public static String dinhDangNgay(LocalDate ngay) {
        if (ngay == null) {
            return "không có";
        }
        return ngay.format(DINH_DANG_NGAY);
    }

    // 15000 -> "15.000 đ"
    public static String dinhDangTien(long soTien) {
        return NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(soTien) + " đ";
    }

    // Bỏ dấu tiếng Việt và đổi chữ thường: "Nước Ngọt" -> "nuoc ngot"
    public static String boDauVaThuong(String chuoi) {
        if (chuoi == null) {
            return "";
        }
        String khongDau = Normalizer.normalize(chuoi.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        khongDau = khongDau.replace('đ', 'd').replace('Đ', 'D');
        return khongDau.toLowerCase(Locale.ROOT);
    }

    // ===== Đọc từ bàn phím =====

    public static int docSoNguyen(Scanner sc, String loiNhac, int min, int max) {
        while (true) {
            System.out.print(loiNhac);
            String s = sc.nextLine().trim();
            try {
                int giaTri = Integer.parseInt(s);
                if (giaTri >= min && giaTri <= max) {
                    return giaTri;
                }
                System.out.println("Giá trị phải từ " + min + " đến " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập một số nguyên hợp lệ.");
            }
        }
    }

    public static long docSoLong(Scanner sc, String loiNhac, long min, long max) {
        while (true) {
            System.out.print(loiNhac);
            String s = sc.nextLine().trim();
            try {
                long giaTri = Long.parseLong(s);
                if (giaTri >= min && giaTri <= max) {
                    return giaTri;
                }
                System.out.println("Giá trị phải từ " + min + " đến " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập một số nguyên hợp lệ (không có dấu chấm hay phẩy).");
            }
        }
    }

    public static double docSoThuc(Scanner sc, String loiNhac, double min, double max) {
        while (true) {
            System.out.print(loiNhac);
            String s = sc.nextLine().trim().replace(',', '.');
            try {
                double giaTri = Double.parseDouble(s);
                if (giaTri >= min && giaTri <= max) {
                    return giaTri;
                }
                System.out.println("Giá trị phải từ " + min + " đến " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập một số hợp lệ.");
            }
        }
    }

    // Bắt buộc nhập, không được để trống
    public static String docChuoi(Scanner sc, String loiNhac) {
        while (true) {
            System.out.print(loiNhac);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("Không được để trống.");
        }
    }

    // Được để trống: trả về "" nếu người dùng chỉ bấm Enter
    public static String docChuoiTuyChon(Scanner sc, String loiNhac) {
        System.out.print(loiNhac);
        return sc.nextLine().trim();
    }

    // Ngày dd/MM/yyyy, bắt buộc nhập
    public static LocalDate docNgay(Scanner sc, String loiNhac) {
        while (true) {
            System.out.print(loiNhac);
            String s = sc.nextLine().trim();
            try {
                return LocalDate.parse(s, DINH_DANG_NGAY);
            } catch (DateTimeParseException e) {
                System.out.println("Ngày không hợp lệ, nhập dạng dd/MM/yyyy (ví dụ 25/10/2026).");
            }
        }
    }

    // Ngày dd/MM/yyyy, bấm Enter để bỏ trống (trả về null)
    public static LocalDate docNgayTuyChon(Scanner sc, String loiNhac) {
        while (true) {
            System.out.print(loiNhac);
            String s = sc.nextLine().trim();
            if (s.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(s, DINH_DANG_NGAY);
            } catch (DateTimeParseException e) {
                System.out.println("Ngày không hợp lệ, nhập dạng dd/MM/yyyy hoặc bấm Enter để bỏ trống.");
            }
        }
    }
}
