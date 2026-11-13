package sanpham;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import util.Ktradulieu;

public abstract class SanPham {
    public static int tongSoSanPhamKhoiTao = 0;

    protected String maSP;
    protected String tenSP;
    protected double donGiaNhap;
    protected double donGiaBan;
    protected int soLuongTon;
    protected String donViTinh;
    protected String nsx;
    protected String hsd;
    protected String trangThaiDuyet;
    protected int soLuongHetHan;
    protected boolean daXoa;
    protected double phanTramThue;
    private Scanner sc=new Scanner(System.in);

    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public SanPham(String maSP, String tenSP, double donGiaNhap, double donGiaBan, int soLuongTon,
                   String donViTinh, String nsx, String hsd, double phanTramThue, String trangThaiDuyet, boolean daXoa) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.donGiaNhap = donGiaNhap;
        this.donGiaBan = donGiaBan;
        this.soLuongTon = soLuongTon;
        this.donViTinh = donViTinh;
        this.nsx = nsx;
        this.hsd = hsd;
        this.phanTramThue = phanTramThue;
        this.trangThaiDuyet = (trangThaiDuyet != null && !trangThaiDuyet.trim().isEmpty()) ? trangThaiDuyet : "Đang xử lý";
        this.soLuongHetHan = 0;
        this.daXoa = daXoa;
        tongSoSanPhamKhoiTao++;
    }

    public SanPham() {
        maSP = "";
        tenSP = "";
        donGiaNhap = 0;
        donGiaBan = 0;
        soLuongTon = 0;
        trangThaiDuyet = "";
        phanTramThue = 0;
        nsx = "";
        hsd = "";
        trangThaiDuyet = "";
        daXoa = false;
    }
    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        if (maSP == null || maSP.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Mã sản phẩm không được để trống!");
        }
        this.maSP = Ktradulieu.chuanHoaMa(maSP);
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        if (tenSP == null || tenSP.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Tên sản phẩm không được để trống!");
        }
        this.tenSP = Ktradulieu.chuanHoaChuoi(tenSP);
    }

    public double getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(double donGiaNhap) {
        if (donGiaNhap < 0) {
            throw new IllegalArgumentException("[Lỗi] Đơn giá nhập phải >= 0! Nhận: " + donGiaNhap);
        }
        this.donGiaNhap = donGiaNhap;
    }

    public double getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(double donGiaBan) {
        if (donGiaBan <= 0) {
            throw new IllegalArgumentException("[Lỗi] Đơn giá bán phải > 0! Nhận: " + donGiaBan);
        }
        this.donGiaBan = donGiaBan;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        if (soLuongTon < 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng tồn kho phải >= 0! Nhận: " + soLuongTon);
        }
        this.soLuongTon = soLuongTon;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        if (donViTinh == null || donViTinh.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Đơn vị tính không được để trống!");
        }
        this.donViTinh = donViTinh.trim();
    }

    public double getPhanTramThue() {
        return phanTramThue;
    }

    public void setPhanTramThue(double phanTramThue) {
        if (phanTramThue < 0 || phanTramThue > 100) {
            throw new IllegalArgumentException("[Lỗi Thuế] Thuế suất phải từ 0% đến 100%! Nhận: " + phanTramThue);
        }
        this.phanTramThue = phanTramThue;
    }

    // Tính tiền thuế trên 1 đơn vị sản phẩm
    public double tinhTienThueDonVi() {
        return this.donGiaBan * (this.phanTramThue / 100.0);
    }

    // Tính tổng tiền thuế khi bán số lượng n
    public double tinhTongTienThue(int soLuong) {
        if (soLuong < 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng tính thuế phải >= 0!");
        }
        return tinhTienThueDonVi() * soLuong;
    }


    public String getNsx() {
        return nsx;
    }

    public String getHsd() {
        return hsd;
    }

    public void setHSD(String nsx, String hsd) {
        if (nsx != null && !nsx.trim().isEmpty() && hsd != null && !hsd.trim().isEmpty()) {
            nsx = nsx.trim();
            hsd = hsd.trim();

            if (!nsx.matches(Ktradulieu.ngayRegex) || !hsd.matches(Ktradulieu.ngayRegex)) {
                throw new IllegalArgumentException("[Lỗi Ngày] NSX và HSD phải đúng định dạng dd/MM/yyyy (VD: 15/05/2025)!");
            }

            try {
                LocalDate dNsx = LocalDate.parse(nsx, DINH_DANG_NGAY);
                LocalDate dHsd = LocalDate.parse(hsd, DINH_DANG_NGAY);

                if (dHsd.isBefore(dNsx)) {
                    throw new IllegalArgumentException("[Lỗi HSD] Hạn sử dụng (" + hsd + ") không thể trước ngày sản xuất (" + nsx + ")!");
                }
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("[Lỗi Ngày] Ngày tháng không tồn tại trong thực tế!");
            }
        }
        this.nsx = nsx;
        this.hsd = hsd;
    }

    public String getTrangThaiDuyet() {
        return trangThaiDuyet;
    }

    public void setTrangThaiDuyet(String trangThaiDuyet) {
        if (trangThaiDuyet == null || trangThaiDuyet.trim().isEmpty()) {
            throw new IllegalArgumentException("[Lỗi] Trạng thái duyệt không được để trống!");
        }
        this.trangThaiDuyet = trangThaiDuyet.trim();
    }

    public int getSoLuongHetHan() {
        return soLuongHetHan;
    }

    public boolean isDaXoa() {
        return daXoa;
    }

    public void setDaXoa(boolean daXoa) {
        this.daXoa = daXoa;
    }

    // kiểm tra hạn sử dụng và cập nhật kho

    public boolean kiemTraHanSuDung() {
        if (this.hsd == null || this.hsd.trim().isEmpty()) {
            return true;
        }

        try {
            LocalDate han;
            String hsdStr = this.hsd.trim();
            if (hsdStr.matches(Ktradulieu.ngayRegex)) {
                han = LocalDate.parse(hsdStr, DINH_DANG_NGAY);
            } else {
                han = LocalDate.parse(hsdStr);
            }

            if (LocalDate.now().isAfter(han)) {
                if (this.soLuongTon > 0) {
                    this.soLuongHetHan += this.soLuongTon;
                    int tonCu = this.soLuongTon;
                    this.soLuongTon = 0; // Cập nhật lại số lượng kho về 0

                    System.out.println("Sản phẩm [" + this.maSP + "] " + this.tenSP
                            + " ĐÃ HẾT HẠN (HSD: " + this.hsd + ")! Đã chuyển "
                            + tonCu + " sản phẩm vào danh mục hết hạn (Tồn kho = 0).");
                }
                return false;
            }
        } catch (Exception e) {}
            return true;
        }

        public void congTonKho ( int sl){
            if (sl <= 0) {
                throw new IllegalArgumentException("[Lỗi Kho] Số lượng cộng kho phải > 0! Nhận: " + sl);
            }
            this.soLuongTon += sl;
        }

        public void truTonKho ( int sl){
            if (sl <= 0) {
                throw new IllegalArgumentException("[Lỗi Kho] Số lượng trừ kho phải > 0! Nhận: " + sl);
            }
            if (sl > this.soLuongTon) {
                throw new IllegalStateException("[Lỗi Kho] Kho không đủ hàng! Hiện có: " + this.soLuongTon + ", yêu cầu xuất: " + sl);
            }
            this.soLuongTon -= sl;
        }

        public abstract double tinhGiaBan ();
        public abstract String getLoaiSanPham ();


    public void nhapThongTin(Scanner sc, DSSanPham ds) {
        String tienTo = (this instanceof ThucAn) ? "TA" : ((this instanceof DoUong) ? "DU" : "SP");
        String maGoiY = (ds != null) ? ds.sinhMaTuDong(tienTo) : (tienTo + "001");

        while (true) {
            System.out.print("Nhập mã sản phẩm (Nhấn / để tự tạo mã): ");
            String input = sc.nextLine().trim();

            if (input.equals("/")) {
                this.setMaSP(maGoiY);
                System.out.println("-> Mã tự tạo: " + this.maSP);
                break;
            }

            String maChuan = Ktradulieu.chuanHoaMa(input);
            String regex = "^" + tienTo + "\\d+$";

            if (!maChuan.matches(regex)) {
                System.out.println("Lỗi: Mã phải bắt đầu bằng '" + tienTo + "' (Ví dụ: " + tienTo + "001)! Hoặc nhấn / để tự tạo mã.");
                continue;
            }

            // Kiểm tra xem có bị trùng mã trong kho không
            if (ds != null && ds.timKiem(maChuan) != null) {
                System.out.println("Lỗi: Mã '" + maChuan + "' đã tồn tại! Vui lòng nhập lại hoặc nhấn / để tự tạo mã.");
                continue;
            }
            this.setMaSP(maChuan);
            break;
        }

        this.setTenSP(Ktradulieu.nhapChuoiKhongRong(sc, "Nhập tên sản phẩm: "));
        this.setDonGiaNhap(Ktradulieu.nhapSoThucKhongAm(sc, "Nhập đơn giá nhập (đ): "));
        this.setDonGiaBan(Ktradulieu.nhapSoThuc(sc, "Nhập đơn giá bán (đ): "));
        this.setSoLuongTon(Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng nhập kho: "));
        this.setDonViTinh(Ktradulieu.nhapChuoi(sc, "Nhập đơn vị tính (gói/chai/hộp...): ", ".+", "Không được để trống!"));
        this.setPhanTramThue(Ktradulieu.nhapSoThuc(sc, "Nhập thuế VAT (%): "));

        LocalDate dNsx = Ktradulieu.nhapNgay(
                sc, "Nhập Ngày sản xuất (dd/MM/yyyy): "
        );

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            LocalDate dHsd = Ktradulieu.nhapNgay(
                    sc, "Nhập Hạn sử dụng (dd/MM/yyyy): "
            );

            try {
                this.setHSD(dNsx.format(dtf), dHsd.format(dtf));
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("Vui lòng nhập lại hạn sử dụng!");
            }
        }

        this.trangThaiDuyet = "Đang xử lý";
        this.daXoa = false;
    }

        @Override
        public String toString () {
            return "[" + getLoaiSanPham() + "] " + maSP + " - " + tenSP +
                    " | Tồn: " + soLuongTon + " " + donViTinh +
                    " | Bán: " + Ktradulieu.dinhDangTien(tinhGiaBan()) +
                    " | Thuế: " + phanTramThue + "% (" + Ktradulieu.dinhDangTien(tinhTienThueDonVi()) + "/đv)";
        }
    }
