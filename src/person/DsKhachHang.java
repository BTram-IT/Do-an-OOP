package person;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.Ktradulieu;

public class DsKhachHang {
    private List<Khachhang> danhSach;

    public DsKhachHang() {
        this.danhSach = new ArrayList<>();
    }

    public List<Khachhang> getDanhSach() {
        return danhSach;
    }

    public int getSoLuong() {
        return danhSach.size();
    }

    public Khachhang get(int index) {
        if (index < 0 || index >= danhSach.size()) return null;
        return danhSach.get(index);
    }

    public String sinhMaTuDong(String tienTo) {
        int maxSo = 0;
        for (Khachhang kh : danhSach) {
            int so = Ktradulieu.laySoTuMa(kh.getMaKH(), tienTo);
            if (so > maxSo) {
                maxSo = so;
            }
        }
        return Ktradulieu.taoMa(tienTo + "%03d", maxSo + 1);
    }

    public void themTuBanPhim(Scanner sc) {
        Khachhang kh = new Khachhang();
        kh.nhapThongTin(sc, this);

        if (this.them(kh)) {
            this.ghiFile("data/khachhang.txt");
        }
    }

    public boolean them(Khachhang kh) {
        if (kh == null) return false;
        if (timKiem(kh.getMaKH()) != null) {
            System.out.println("Mã khách hàng đã tồn tại!");
            return false;
        }
        danhSach.add(kh);
        return true;
    }


    public Khachhang timKiem(String maHoacSdt) {
        if (maHoacSdt == null || maHoacSdt.trim().isEmpty()) return null;
        String key = maHoacSdt.trim();
        for (Khachhang kh : danhSach) {
            if (kh.getMaKH().equalsIgnoreCase(key) || kh.getSdt().equalsIgnoreCase(key)) {
                return kh;
            }
        }
        return null;
    }

    // xóa
    public boolean xoa(String maKH) {
        Khachhang kh = timKiem(maKH);

        if (kh == null) {
            System.out.println("Không tìm thấy khách hàng: " + maKH);
            return false;
        }

        kh.setDaXoa(true);

        // Cập nhật file sau khi xóa
        ghiFile("data/khachhang.txt");

        System.out.println("Đã xóa khách hàng: " + maKH);
        return true;
    }

    // sửa
    public boolean sua(Scanner sc, Nguoidung nguoiThucHien, String maKH) {
        if (nguoiThucHien == null || nguoiThucHien.isDaXoa()) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Tài khoản không hợp lệ hoặc đã bị vô hiệu hóa!");
            return false;
        }

        Khachhang kh = timKiem(maKH);
        if (kh == null) {
            System.out.println("Không tìm thấy khách hàng có mã: " + maKH);
            return false;
        }

        while (true) {
            System.out.println("\n--- SỬA THÔNG TIN KHÁCH HÀNG: " + kh.getTenKH() + " [" + kh.getMaKH() + "] ---");
            System.out.println("1. Sửa Họ và Tên");
            System.out.println("2. Sửa Số điện thoại");
            System.out.println("3. Sửa Địa chỉ");
            System.out.println("4. Sửa Điểm tích lũy");
            System.out.println("0. Hoàn tất chỉnh sửa (Thoát)");

            int chon = Ktradulieu.luaChon(sc, " Chọn trường cần sửa (0-4): ", 0, 4);

            try {
                switch (chon) {
                    case 1:
                        String tenMoi = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập họ tên mới: ");
                        kh.setTenKH(tenMoi);
                        System.out.println(">> Đã cập nhật họ tên thành công!");
                        break;
                    case 2:
                        String sdtMoi = Ktradulieu.nhapChuoi(sc, "Nhập số điện thoại mới: ",
                                Ktradulieu.phoneRegex, "Số điện thoại không hợp lệ (03/05/07/08/09)!");
                        kh.setSdt(sdtMoi);
                        System.out.println(">> Đã cập nhật số điện thoại thành công!");
                        break;
                    case 3:
                        String diaChiMoi = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập địa chỉ mới: ");
                        kh.setDiaChi(diaChiMoi);
                        System.out.println(">> Đã cập nhật địa chỉ thành công!");
                        break;
                    case 4:
                        int diemMoi = Ktradulieu.nhapSoNguyen(sc, "Nhập điểm tích lũy mới (>= 0): ");
                        kh.setDiemTichLuy(diemMoi);
                        System.out.println(">> Đã cập nhật điểm tích lũy thành công!");
                        break;
                    case 0:
                        ghiFile("data/khachhang.txt");

                        System.out.println("Đã hoàn tất chỉnh sửa khách hàng ["
                                + kh.getMaKH() + "].");
                        return true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi dữ liệu: " + e.getMessage());
            }
        }
    }
    // xuất
    public void xuatDanhSach() {
        System.out.println("\n================ DANH SÁCH KHÁCH HÀNG ================");
        for (Khachhang kh : danhSach) {
            System.out.println(kh.toString());
        }
    }

    // ghi
    public void ghiFile(String duongDan) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(duongDan))) {
            for (Khachhang kh : danhSach) {
                bw.write(kh.toFileString());
                bw.newLine();
            }
            System.out.println("Đã lưu danh sách khách hàng vào file: " + duongDan);
        } catch (IOException e) {
            System.out.println("Lỗi ghi file khách hàng: " + e.getMessage());
        }
    }

    // đọc
    public void docFile(String duongDan) {
        File file = new File(duongDan);
        if (!file.exists()) return;
        danhSach.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String dong;
            while ((dong = br.readLine()) != null) {
                dong = dong.trim();
                if (dong.isEmpty() || dong.startsWith("#")) continue;
                String[] p = dong.split(";");
                if (p.length >= 6) {
                    String ma = p[0].trim();
                    String ten = p[1].trim();
                    String sdt = p[2].trim();
                    String diaChi = p[3].trim();
                    int diem = Integer.parseInt(p[4].trim());
                    boolean daXoa = Boolean.parseBoolean(p[5].trim());

                    Khachhang kh = new Khachhang(ma, ten, sdt, diaChi, diem, daXoa);
                    danhSach.add(kh);
                }
            }
            System.out.println("Đã đọc thành công " + danhSach.size() + " khách hàng từ file: " + duongDan);
        } catch (Exception e) {
            System.out.println("Lỗi đọc file khách hàng: " + e.getMessage());
        }
    }
}