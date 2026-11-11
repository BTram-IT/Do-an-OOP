package util;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class Ktradulieu {

    // =========================================================================
    // CÁC REGEX THÔNG DỤNG (GIỮ NGUYÊN 100% KHÔNG ĐỔI)
    // =========================================================================
    public static final String soNguyenRegex = "^[+-]?\\d+$";
    public static final String soThucRegex = "^[+-]?\\d+(\\.\\d+)?$";
    public static final String phoneRegex = "^(03|05|07|08|09)\\d{8}$";
    public static final String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String diaChiRegex = "^.+$";
    public static final String ngayRegex = "^\\d{2}/\\d{2}/\\d{4}$";

    private static final DateTimeFormatter DINH_DANG_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DecimalFormat DINH_DANG_TIEN = new DecimalFormat("###,###,### đ");

    // =========================================================================
    // 1. NHẬP CHUỖI BẮT BUỘC THEO REGEX (HÀM GỐC 1)
    // =========================================================================
    public static String nhapChuoi(Scanner sc, String thongBao, String regex, String thongBaoLoi) {
        while (true) {
            System.out.print(thongBao);
            String input = sc.nextLine().trim();
            if (regex == null || regex.isEmpty() || input.matches(regex)) {
                return input;
            }
            System.out.println(thongBaoLoi);
        }
    }

    // =========================================================================
    // 2. NHẬP CHUỖI TÙY CHỌN (HÀM GỐC 2 - ENTER = RỖNG)
    // =========================================================================
    public static String nhapChuoiTuyChon(Scanner sc, String thongBao, String regex, String thongBaoLoi) {
        if (sc == null) sc = new Scanner(System.in);
        while (true) {
            System.out.print(thongBao);
            String input = sc.nextLine().trim();
            if (input.isEmpty() || regex == null || input.matches(regex) || regex.isEmpty()) {
                return input;
            }
            System.out.println(thongBaoLoi);
        }
    }

    // =========================================================================
    // 3. NHẬP SỐ NGUYÊN DƯƠNG (> 0) (HÀM GỐC 3)
    // =========================================================================
    public static int nhapSoNguyen(Scanner sc, String thongBao) {
        if (sc == null) sc = new Scanner(System.in);
        while (true) {
            System.out.print(thongBao);
            String input = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val > 0) {
                    return val;
                }
                System.out.println("Vui lòng nhập số nguyên lớn hơn 0!");
            } catch (NumberFormatException e) {
                System.out.println("Giá trị không hợp lệ! Vui lòng nhập số nguyên.");
            }
        }
    }

    // =========================================================================
    // 4. NHẬP SỐ THỰC (HÀM GỐC 4)
    // =========================================================================
    public static double nhapSoThuc(Scanner sc, String thongBao) {
        if (sc == null) sc = new Scanner(System.in);
        while (true) {
            System.out.print(thongBao);
            String input = sc.nextLine().trim();
            try {
                double val =  Double.parseDouble(input);
                if( Double.isFinite(val))
                    return val ;
            } catch (NumberFormatException e) {
                System.out.println("Giá trị không hợp lệ! Vui lòng nhập số thực.");
            }
        }
    }

    // =========================================================================
    // 5. NHẬP SỐ THỰC KHÔNG ÂM (>= 0) (HÀM GỐC 5)
    // =========================================================================
    public static double nhapSoThucKhongAm(Scanner sc, String thongBao) {
        if (sc == null) sc = new Scanner(System.in);
        while (true) {
            System.out.print(thongBao);
            String input = sc.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val >= 0 && Double.isFinite(val)) {
                    return val;
                }
                System.out.println("Vui lòng nhập số không âm (>= 0)!");
            } catch (NumberFormatException e) {
                System.out.println("Giá trị không hợp lệ! Vui lòng nhập số thực.");
            }
        }
    }

    // =========================================================================
    // 6. NHẬP LỰA CHỌN MENU TỪ MIN ĐẾN MAX (HÀM GỐC 6)
    // =========================================================================
    public static int luaChon(Scanner sc, String thongBao, int min, int max) {
        if ( min > max )
        {
            int tam = min;
            min = max;
            max = tam;
        }
        while (true) {
            System.out.print(thongBao);
            String input = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Vui lòng chọn trong khoảng từ %d đến %d!%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Lựa chọn phải là chữ số!");
            }
        }
    }

    // =========================================================================
    // 7. NHẬP NGÀY SINH (yyyy-MM-dd) (HÀM GỐC 7)
    // =========================================================================
    public static LocalDate nhapNgaySinh(Scanner sc, String thongBao) {
        if (sc == null) sc = new Scanner(System.in);
        while (true) {
            System.out.print(thongBao);
            String input = sc.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(input);
                if (!date.isAfter(LocalDate.now())) {
                    return date;
                }
                System.out.println("Ngày sinh không thể ở tương lai!");
            } catch (DateTimeParseException e) {
                System.out.println("Ngày sinh không hợp lệ! Nhập theo định dạng yyyy-MM-dd (ví dụ: 2000-10-25).");
            }
        }
    }

    // =========================================================================
    // 8. NHẬP NGÀY THÔNG DỤNG (dd/MM/yyyy) (HÀM GỐC 8)
    // =========================================================================
    public static LocalDate nhapNgay(Scanner sc, String thongBao, boolean batBuoc) {
        if (sc == null) sc = new Scanner(System.in);
        while (true) {
            System.out.print(thongBao);
            String input = sc.nextLine().trim();
            if (!batBuoc && input.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(input, DINH_DANG_NGAY);
            } catch (DateTimeParseException e) {
                System.out.println("Ngày không hợp lệ! Nhập theo định dạng dd/MM/yyyy (ví dụ: 25/10/2026).");
            }
        }
    }

    // =========================================================================
    // 9. TỰ ĐỘNG SINH MÃ (HÀM GỐC 9)
    // =========================================================================
    public static String taoMa(String pattern, int so) {
        return String.format(pattern, so);
    }

    // =========================================================================
    // CÁC HÀM BỔ SUNG TIỆN ÍCH (ĐỂ XÓA BỎ HOÀN TOÀN CLASS TIỆN ÍCH RỜI RẠC)
    // =========================================================================

    /** Nhập chuỗi không được để trống */
    public static String nhapChuoiKhongRong(Scanner sc, String thongBao) {
        return nhapChuoi(sc, thongBao, ".+", "Không được để trống dữ liệu!");
    }

    /** Nhập số nguyên trong khoảng [min, max] */
    public static int nhapSoNguyen(Scanner sc, String thongBao, int min, int max) {
        return luaChon(sc, thongBao, min, max);
    }

    /** Nhập ngày bắt buộc dd/MM/yyyy */
    public static LocalDate nhapNgay(Scanner sc, String thongBao) {
        return nhapNgay(sc, thongBao, true);
    }

    /** Xác nhận Có/Không (y/n) */
    public static boolean nhapXacNhan(Scanner sc, String thongBao) {
        if (sc == null) sc = new Scanner(System.in);
        while (true) {
            System.out.print(thongBao + " (y/n): ");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes") || input.equals("c") || input.equals("co")) {
                return true;
            }
            if (input.equals("n") || input.equals("no") || input.equals("k") || input.equals("khong")) {
                return false;
            }
            System.out.println("Vui lòng chỉ nhập 'y' (đồng ý) hoặc 'n' (từ chối)!");
        }
    }

    /** Chuẩn hóa chuỗi họ tên hoặc văn bản: viết hoa chữ cái đầu mỗi từ */
    public static String chuanHoaChuoi(String str) {
        if (str == null || str.trim().isEmpty()) return "";
        String[] tu = str.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String t : tu) {
            if (!t.isEmpty()) {
                sb.append(Character.toUpperCase(t.charAt(0)))
                        .append(t.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }

    /** Định dạng tiền tệ */
    public static String dinhDangTien(long soTien) {
        return DINH_DANG_TIEN.format(Math.round(soTien));
    }

    public static String dinhDangTien(double soTien) {
        return DINH_DANG_TIEN.format(Math.round(soTien));
    }

    // tự động điền mã
    // Chuẩn hóa mã (Viết hoa và xóa khoảng trắng)
    public static String chuanHoaMa(String ma) {
        if (ma == null) return "";
        return ma.trim().toUpperCase();
    }

    // Tách số từ mã (Ví dụ: "TA005" -> lấy ra số 5)
    public static int laySoTuMa(String ma, String tienTo) {
        String maChuan = chuanHoaMa(ma);
        if (maChuan == null || !maChuan.startsWith(tienTo)) {
            return 0;
        }
        String phanSo = maChuan.substring(tienTo.length());
        if (!phanSo.matches("[0-9]+")) {
            return 0;
        }
        try {
            return Integer.parseInt(phanSo);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}