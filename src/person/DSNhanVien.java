package person;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.Ktradulieu;

public class DSNhanVien {
    private List<NhanVien> danhSach;

    public DSNhanVien() {
        this.danhSach = new ArrayList<>();
    }

    public List<NhanVien> getDanhSach() {
        return danhSach;
    }

    public int getSoLuong() {
        return danhSach.size();
    }

    public NhanVien get(int index) {
        if (index < 0 || index >= danhSach.size()) return null;
        return danhSach.get(index);
    }

    public String sinhMaTuDong(String tienTo) {
        int maxSo = 0;
        for (NhanVien nv : danhSach) {
            int so = Ktradulieu.laySoTuMa(nv.getMaND(), tienTo);
            if (so > maxSo) {
                maxSo = so;
            }
        }
        return Ktradulieu.taoMa(tienTo + "%03d", maxSo + 1);
    }

    public boolean them(NhanVien nv) {
        if (nv == null) return false;
        if (timKiem(nv.getMaND()) != null) {
            System.out.println("Mã nhân viên " + nv.getMaND() + " đã tồn tại!");
            return false;
        }
        danhSach.add(nv);
        System.out.println("Đã thêm nhân viên [" + nv.getMaND() + "] " + nv.getTenND() + " vào hệ thống.");
        return true;
    }

    public boolean themNV(NhanVien nv) {
        return them(nv);
    }

    public NhanVien timKiem(String maHoacTen) {
        if (maHoacTen == null || maHoacTen.trim().isEmpty()) return null;
        String key = maHoacTen.trim();
        for (NhanVien nv : danhSach) {
            if (nv.getMaND().equalsIgnoreCase(key) || nv.getTenND().toLowerCase().contains(key.toLowerCase())) {
                return nv;
            }
        }
        return null;
    }

    public NhanVien timKiemTheoTaiKhoan(String taiKhoan) {
        if (taiKhoan == null || taiKhoan.trim().isEmpty()) return null;
        for (NhanVien nv : danhSach) {
            if (nv.getTaiKhoan().equalsIgnoreCase(taiKhoan.trim())) {
                return nv;
            }
        }
        return null;
    }

    public boolean xoa(Scanner sc, Nguoidung nguoiThucHien, String maNV) {
        NhanVien nv = timKiem(maNV);
        if (nv == null) {
            System.out.println("Không tìm thấy nhân viên có mã: " + maNV);
            return false;
        }

        if (nguoiThucHien instanceof Giamdoc) {
            nv.setDaXoa(true);
            nv.setYeuCauSaThai(null);
            System.out.println("[GIÁM ĐỐC PHÊ DUYỆT] Đã sa thải nhân viên [" + maNV + "] " + nv.getTenND() + ". (Dữ liệu cũ vẫn được bảo lưu).");
            return true;
        } else if (nguoiThucHien instanceof QuanLyCuaHang) {
            System.out.println("QUẢN LÝ KHÔNG CÓ QUYỀN SA THẢI! Bạn chỉ có thể gửi đề xuất lên Giám Đốc.");
            String lyDo = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập lý do đề xuất sa thải: ");
            nv.setYeuCauSaThai(lyDo);
            System.out.println(" Đã gửi đề xuất sa thải nhân viên [" + maNV + "] lên Giám Đốc phê duyệt.");
            return false;
        } else {
            System.out.println("[TỪ CHỐI TRUY CẬP] Bạn không có quyền hạn đối với tác vụ này!");
            return false;
        }
    }


    public void sua(Scanner sc, Nguoidung nguoiThucHien, String maNV) {
        if (!(nguoiThucHien instanceof QuanLyCuaHang || nguoiThucHien instanceof Giamdoc)) {
            System.out.println("[TỪ CHỐI TRUY CẬP] Chỉ Quản Lý và Giám Đốc mới có quyền chỉnh sửa nhân viên!");
            return;
        }
        NhanVien nv = timKiem(maNV);
        if (nv == null) {
            System.out.println("Không tìm thấy nhân viên có mã: " + maNV);
            return;
        }

        while (true) {
            System.out.println("\n--- CHỌN TRƯỜNG CẦN SỬA CHO NHÂN VIÊN [" + nv.getMaND() + "] ---");
            System.out.println("1. Sửa Họ và Tên");
            System.out.println("2. Sửa Số điện thoại");
            System.out.println("3. Sửa Mật khẩu");
            System.out.println("4. Sửa Lương cơ bản");
            System.out.println("5. Sửa Ngày vào làm (dd/MM/yyyy)");
            System.out.println("0. Hoàn tất sửa");
            int chon = Ktradulieu.luaChon(sc, "Nhập lựa chọn của bạn: ", 0, 5);

            try {
                switch (chon) {
                    case 1:
                        String tenMoi = Ktradulieu.nhapChuoiKhongRong(sc, "Nhập họ tên mới: ");
                        nv.setTenND(tenMoi);
                        System.out.println("Đã cập nhật họ tên thành công!");
                        break;
                    case 2:
                        String sdtMoi = Ktradulieu.nhapChuoi(sc, "Nhập số điện thoại mới: ", Ktradulieu.phoneRegex, "SĐT không hợp lệ!");
                        nv.setSoDienThoai(sdtMoi);
                        System.out.println("Đã cập nhật SĐT!");
                        break;
                    case 3:
                        String mkMoi = Ktradulieu.nhapChuoi(sc, "Nhập mật khẩu mới: ", ".{4,}", "Mật khẩu tối thiểu 4 ký tự!");
                        nv.setMatKhau(mkMoi);
                        System.out.println("Đã cập nhật mật khẩu!");
                        break;
                    case 4:
                        double luongMoi = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập mức lương cơ bản mới: ");
                        nv.setLuongCoBan(luongMoi);
                        System.out.println("Đã cập nhật mức lương cơ bản!");
                        break;
                    case 5:
                        LocalDate ngayMoi = Ktradulieu.nhapNgay(sc, "Nhập ngày vào làm mới (dd/MM/yyyy): ");
                        nv.setNgayVaoLam(ngayMoi);
                        System.out.println("Đã cập nhật ngày vào làm!");
                        break;
                    case 0:
                        return;
                }
            } catch (Exception e) {
                System.out.println("Lỗi cập nhật: " + e.getMessage());
            }
        }
    }

    public void xuatDanhSach() {
        System.out.println("\n================ DANH SÁCH NHÂN VIÊN CỬA HÀNG ================");
        for (NhanVien nv : danhSach) {
            System.out.println(nv.toString());
        }
        System.out.println("Tổng số nhân viên: " + danhSach.size());
    }

    //Ghi file
    public void ghiFile(String duongDan) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(duongDan))) {
            for (NhanVien nv : danhSach) {
                bw.write(nv.toFileString());
                bw.newLine();
            }
            System.out.println("Đã lưu danh sách nhân viên vào file: " + duongDan);
        } catch (IOException e) {
            System.out.println("Lỗi ghi file nhân viên: " + e.getMessage());
        }
    }

    // đọc file
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
                // Định dạng: NHANVIEN;maND;tenND;taiKhoan;matKhau;soDienThoai;vaiTro;ngayVaoLam;luongCoBan;yeuCauSaThai;daXoa
                if (p.length >= 8) {
                    int offset = p[0].equalsIgnoreCase("NHANVIEN") ? 1 : 0;
                    String ma = p[offset];
                    String ten = p[offset + 1];
                    String tk = p[offset + 2];
                    String mk = p[offset + 3];
                    String sdt = p[offset + 4];
                    LocalDate ngayVao = LocalDate.parse(p[offset + 6]);
                    boolean daXoa = Boolean.parseBoolean(p[p.length - 1]);

                    // Gọi constructor 7 tham số chuẩn của NhanVien
                    NhanVien nv = new NhanVien(ma, ten, tk, mk, sdt, ngayVao, daXoa);

                    // Nạp lương cơ bản nếu có
                    if (p.length > offset + 7) {
                        try {
                            nv.setLuongCoBan(Double.parseDouble(p[offset + 7]));
                        } catch (Exception ignored) {}
                    }
                    // Nạp đề xuất sa thải nếu có
                    if (p.length > offset + 8 && !p[offset + 8].equalsIgnoreCase("KHONG")) {
                        nv.setYeuCauSaThai(p[offset + 8]);
                    }

                    danhSach.add(nv);
                }
            }
            System.out.println("Đã đọc thành công " + danhSach.size() + " nhân viên từ file: " + duongDan);
        } catch (Exception e) {
            System.out.println("Lỗi đọc file nhân viên: " + e.getMessage());
        }
    }
}