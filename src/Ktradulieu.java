import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Scanner;

public class Ktradulieu {

    // 1. Nhap chuoi theo bieu thuc chinh quy
    public static String NhapChuoi(
            Scanner sc, String Thongbao,
            String regex, String Baoloi) {

        while (true) {
            System.out.print(Thongbao);
            String value = sc.nextLine().trim();

            if (value.matches(regex)) {
                return value;
            }

            System.out.println(Baoloi);
        }
    }

    // 2. Nhap chuoi khong duoc de trong
    public static String NhapChuoiKhongRong(
            Scanner sc, String Thongbao) {

        return NhapChuoi(
                sc,
                Thongbao,
                "\\S(.*\\S)?|\\S",
                "Du lieu khong duoc de trong!"
        );
    }

    // 3. Tao ma tu dong
    // Vi du: TaoMa("SP%03d", 1) -> SP001
    public static String TaoMa(String regex, int SoTiepTheo) {
        return String.format(regex, SoTiepTheo);
    }

    // 4. Nhap so nguyen duong (> 0)
    public static long NhapSoNguyen(
            Scanner sc, String Thongbao) {

        while (true) {
            System.out.print(Thongbao);
            String value = sc.nextLine().trim();

            try {
                // Khong chap nhan chuoi rong, so thap phan
                // hoac ky tu khong phai chu so
                if (!value.matches("\\d+")) {
                    System.out.println(
                            "Loi: Vui long nhap so nguyen duong!"
                    );
                    continue;
                }

                long soNguyen = Long.parseLong(value);

                if (soNguyen <= 0) {
                    System.out.println(
                            "Loi: So phai lon hon 0!"
                    );
                    continue;
                }

                return soNguyen;

            } catch (NumberFormatException e) {
                System.out.println(
                        "Loi: So nguyen qua lon!"
                );
            }
        }
    }

    // 5. Nhap so nguyen khong am (>= 0)
    public static long NhapSoNguyenKhongAm(
            Scanner sc, String Thongbao) {

        while (true) {
            System.out.print(Thongbao);
            String value = sc.nextLine().trim();

            try {
                if (!value.matches("\\d+")) {
                    System.out.println(
                            "Loi: Vui long nhap so nguyen khong am!"
                    );
                    continue;
                }

                return Long.parseLong(value);

            } catch (NumberFormatException e) {
                System.out.println(
                        "Loi: So nguyen qua lon!"
                );
            }
        }
    }

    // 6. Nhap so thuc duong (> 0)
    public static double NhapSoThuc(
            Scanner sc, String Thongbao) {

        while (true) {
            System.out.print(Thongbao);
            String value = sc.nextLine().trim();

            try {
                // Chap nhan: 15000, 15000.5, 0.5
                if (!value.matches(
                        "\\d+(\\.\\d+)?")) {
                    System.out.println(
                            "Loi: Nhap so hop le, vi du 15000 hoac 15000.5!"
                    );
                    continue;
                }

                double soThuc = Double.parseDouble(value);

                if (!Double.isFinite(soThuc) || soThuc <= 0) {
                    System.out.println(
                            "Loi: So phai huu han va lon hon 0!"
                    );
                    continue;
                }

                return soThuc;

            } catch (NumberFormatException e) {
                System.out.println(
                        "Loi: Gia tri so khong hop le!"
                );
            }
        }
    }

    // 7. Nhap so thuc khong am (>= 0)
    public static double NhapSoThucKhongAm(
            Scanner sc, String Thongbao) {

        while (true) {
            System.out.print(Thongbao);
            String value = sc.nextLine().trim();

            try {
                if (!value.matches(
                        "\\d+(\\.\\d+)?")) {
                    System.out.println(
                            "Loi: Vui long nhap so thuc khong am!"
                    );
                    continue;
                }

                double soThuc = Double.parseDouble(value);

                if (!Double.isFinite(soThuc)) {
                    System.out.println(
                            "Loi: So phai la gia tri huu han!"
                    );
                    continue;
                }

                return soThuc;

            } catch (NumberFormatException e) {
                System.out.println(
                        "Loi: Gia tri so khong hop le!"
                );
            }
        }
    }

    // 8. Nhap lua chon trong khoang
    public static int LuaChon(
            Scanner sc, String Thongbao,
            int KhoangBatDau, int KhoangKetThuc) {

        while (true) {
            System.out.print(Thongbao);
            String value = sc.nextLine().trim();

            try {
                if (!value.matches("-?\\d+")) {
                    System.out.println(
                            "Loi: Lua chon phai la so nguyen!"
                    );
                    continue;
                }

                int soNguyen = Integer.parseInt(value);

                if (soNguyen < KhoangBatDau
                        || soNguyen > KhoangKetThuc) {
                    System.out.println(
                            "Vui long nhap so tu "
                                    + KhoangBatDau + " den "
                                    + KhoangKetThuc + "!"
                    );
                    continue;
                }

                return soNguyen;

            } catch (NumberFormatException e) {
                System.out.println(
                        "Loi: So nguyen qua gioi han!"
                );
            }
        }
    }

    // 9. Nhap email
    public static String NhapEmail(Scanner sc, String Thongbao) {
        return NhapChuoi(
                sc,
                Thongbao,
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                "Loi: Email khong dung dinh dang!"
        );
    }

    // 10. Nhap so dien thoai 10 chu so, bat dau bang 0
    public static String NhapSDT(Scanner sc, String Thongbao) {
        return NhapChuoi(
                sc,
                Thongbao,
                "^0\\d{9}$",
                "Loi: SDT phai co 10 chu so va bat dau bang 0!"
        );
    }

    // 11. Nhap ngay theo dinh dang dd/MM/uuuu
    public static LocalDate NhapNgay(
            Scanner sc, String Thongbao) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        .withResolverStyle(ResolverStyle.STRICT);

        while (true) {
            System.out.print(Thongbao);
            String value = sc.nextLine().trim();

            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Loi: Ngay khong hop le! "
                                + "Vui long nhap theo dd/MM/yyyy, vi du 25/10/2026."
                );
            }
        }
    }
}