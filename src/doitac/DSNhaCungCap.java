package doitac;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import person.Nguoidung;
import person.QuanLyCuaHang;
import person.Giamdoc;
import util.Ktradulieu;

public class DSNhaCungCap {
    private List<NhaCungCap> danhSach;

    public DSNhaCungCap() {
        this.danhSach = new ArrayList<>();
    }

    public List<NhaCungCap> getDanhSach() {
        return danhSach;
    }

    public int getSoLuong() {
        return danhSach.size();
    }

    public NhaCungCap get(int index) {
        if (index < 0 || index >= danhSach.size()) return null;
        return danhSach.get(index);
    }

    public String sinhMaTuDong(String tienTo) {
        int maxSo = 0;
        for (NhaCungCap ncc : danhSach) {
            int so = Ktradulieu.laySoTuMa(ncc.getMaNCC(), tienTo);
            if (so > maxSo) {
                maxSo = so;
            }
        }
        return Ktradulieu.taoMa(tienTo + "%03d", maxSo + 1);
    }

    // Thêm nhà cung cấp
    public boolean them(NhaCungCap ncc) {
        if (ncc == null) return false;

        if (timKiem(ncc.getMaNCC()) != null) {
            System.out.println("Mã nhà cung cấp đã tồn tại!");
            return false;
        }
        danhSach.add(ncc);

        // Lưu danh sách vào file
        ghiFile("data/nhacungcap.txt");

        return true;
    }

    public boolean themNCC(NhaCungCap ncc) {
        return them(ncc);
    }
    //Phát sinh lỗi
    public NhaCungCap timKiem(String maHoacTen) {
        if (maHoacTen == null || maHoacTen.trim().isEmpty()) return null;
        NhaCungCap theoMa = timTheoMa(maHoacTen);
        if (theoMa != null) return theoMa;

        String key = maHoacTen.trim();
        for (NhaCungCap ncc : danhSach) {
            if (ncc.getTenNCC().equalsIgnoreCase(key)) {
                return ncc;
            }
        }
        return null;
    }

    public List<NhaCungCap> timKiemGanDung(String tuKhoa) {
        List<NhaCungCap> ketQua = new ArrayList<>();
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) return ketQua;
        String key = tuKhoa.trim().toLowerCase();
        for (NhaCungCap ncc : danhSach) {
            if (ncc.getMaNCC().toLowerCase().contains(key)
                    || ncc.getTenNCC().toLowerCase().contains(key)) {
                ketQua.add(ncc);
            }
        }
        return ketQua;
    }

    public NhaCungCap timTheoMa(String ma) {
        if (ma == null || ma.trim().isEmpty()) return null;
        String key = ma.trim();
        for (NhaCungCap ncc : danhSach) {
            if (ncc.getMaNCC().equalsIgnoreCase(key)) {
                return ncc;
            }
        }
        return null;
    }

    // 4. Xóa nhà cung cấp (Chỉ Quản lý hoặc Giám đốc, dùng Soft Delete để giữ lịch sử phiếu nhập)
    public boolean xoa(Nguoidung nguoiThucHien, String maNCC) {
        if (!(nguoiThucHien instanceof QuanLyCuaHang || nguoiThucHien instanceof Giamdoc)) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Chỉ Quản Lý hoặc Giám Đốc mới có quyền xóa nhà cung cấp!");
            return false;
        }

        NhaCungCap ncc = timTheoMa(maNCC);
        if (ncc == null) {
            System.out.println("Không tìm thấy nhà cung cấp: " + maNCC);
            return false;
        }

        ncc.setDaXoa(true);
        ghiFile("data/nhacungcap.txt");

        System.out.println("Đã chuyển nhà cung cấp [" + maNCC + "] sang trạng thái [NGỪNG HỢP TÁC]. Dữ liệu phiếu nhập cũ vẫn được bảo lưu.");
        return true;
    }

    public boolean xoaNCC(String maNCC, Nguoidung nguoiThucHien) {
        return xoa(nguoiThucHien, maNCC);
    }

    // sửa
    public boolean sua(Scanner sc, Nguoidung nguoiThucHien, String maNCC) {
        if (!(nguoiThucHien instanceof QuanLyCuaHang || nguoiThucHien instanceof Giamdoc)) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Chỉ Quản Lý hoặc Giám Đốc mới có quyền sửa nhà cung cấp!");
            return false;
        }

        NhaCungCap ncc = timTheoMa(maNCC);
        if (ncc == null) {
            System.out.println("Không tìm thấy nhà cung cấp: " + maNCC);
            return false;
        }

        while (true) {
            System.out.println("\n--- SỬA THÔNG TIN NHÀ CUNG CẤP: " + ncc.getTenNCC() + " [" + ncc.getMaNCC() + "] ---");
            System.out.println("1. Sửa Tên nhà cung cấp");
            System.out.println("2. Sửa Số điện thoại");
            System.out.println("3. Sửa Địa chỉ");
            System.out.println("4. Sửa Email");
            System.out.println("0. Hoàn tất chỉnh sửa (Thoát)");

            int chon = Ktradulieu.luaChon(sc, "Chọn trường cần sửa (0-4): ", 0, 4);
            try {
                switch (chon) {
                    case 1:
                        String tenMoi = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên mới: ");
                        ncc.setTenNCC(tenMoi);
                        System.out.println(">> Đã cập nhật tên nhà cung cấp!");
                        break;
                    case 2:
                        System.out.print("Nhập số điện thoại mới: ");
                        String sdtMoi = sc.nextLine().trim();
                        ncc.setSdt(sdtMoi);
                        System.out.println(">> Đã cập nhật số điện thoại!");
                        break;
                    case 3:
                        String diaChiMoi = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập địa chỉ mới: ");
                        ncc.setDiaChi(diaChiMoi);
                        System.out.println(">>Đã cập nhật địa chỉ!");
                        break;
                    case 4:
                        String emailMoi = Ktradulieu.nhapChuoi(sc, "Nhập email mới: ", Ktradulieu.emailRegex, "Email không hợp lệ!");
                        ncc.setEmail(emailMoi);
                        System.out.println(">> Đã cập nhật email!");
                        break;
                    case 0:
                        ghiFile("data/nhacungcap.txt");
                        return true;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Lỗi dữ liệu: " + e.getMessage());
            }
        }
    }

    public boolean suaNCC(Scanner sc, String maNCC, Nguoidung nguoiThucHien) {
        return sua(sc, nguoiThucHien, maNCC);
    }

    public void xuatDanhSach() {
        System.out.println("\n================ DANH SÁCH NHÀ CUNG CẤP ================");
        for (NhaCungCap ncc : danhSach) {
            System.out.println(ncc.toString());
        }
        System.out.println("Tổng số nhà cung cấp: " + danhSach.size());
    }

    // ghi (UTF-8, qua file tạm; file đang bị bảo vệ do đọc lỗi sẽ không bị ghi đè)
    public boolean ghiFile(String duongDan) {
        List<String> cacDong = new ArrayList<>();
        for (NhaCungCap ncc : danhSach) {
            cacDong.add(ncc.toFileString());
        }
        try {
            String dich = Ktradulieu.ghiDongAnToan(duongDan, cacDong);
            System.out.println("Đã lưu danh sách nhà cung cấp vào file: " + dich);
            return true;
        } catch (IOException | RuntimeException e) {
            System.out.println(" Lỗi ghi file nhà cung cấp: " + e.getMessage());
            return false;
        }
    }

    // đọc (UTF-8): đọc vào danh sách tạm, chỉ thay thế khi mọi dòng hợp lệ.
    // true = thành công (hoặc chưa có file); false = file lỗi, danh sách hiện tại giữ nguyên và file gốc được bảo vệ.
    public boolean docFile(String duongDan) {
        File file = new File(duongDan);
        if (!file.exists()) return true;

        List<NhaCungCap> tam = new ArrayList<>();
        int soDong = 0;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String dong;
            while ((dong = br.readLine()) != null) {
                soDong++;
                dong = dong.trim();
                if (dong.isEmpty() || dong.startsWith("#")) continue;
                String[] p = dong.split(";", -1);
                if (p.length < 6) {
                    throw new IllegalArgumentException("Thiếu trường dữ liệu (cần 6, có " + p.length + ")");
                }
                String xoa = p[5].trim();
                if (!xoa.equalsIgnoreCase("true") && !xoa.equalsIgnoreCase("false")) {
                    throw new IllegalArgumentException("Trạng thái xóa phải là true/false: '" + xoa + "'");
                }
                NhaCungCap ncc = new NhaCungCap(p[0].trim(), p[1].trim(), p[2].trim(),
                        p[3].trim(), p[4].trim(), Boolean.parseBoolean(xoa));
                for (NhaCungCap da : tam) {
                    if (da.getMaNCC().equalsIgnoreCase(ncc.getMaNCC())) {
                        throw new IllegalArgumentException("Mã nhà cung cấp bị trùng: " + ncc.getMaNCC());
                    }
                }
                tam.add(ncc);
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Lỗi đọc file nhà cung cấp tại dòng " + soDong + ": " + e.getMessage());
            System.out.println("Danh sách hiện tại được giữ nguyên, không có dữ liệu nào bị thay thế.");
            Ktradulieu.danhDauFileLoi(duongDan, "Dòng " + soDong + ": " + e.getMessage());
            return false;
        }
        danhSach.clear();
        danhSach.addAll(tam);
        System.out.println("Đã đọc thành công " + danhSach.size() + " nhà cung cấp từ file: " + duongDan);
        return true;
    }
}
