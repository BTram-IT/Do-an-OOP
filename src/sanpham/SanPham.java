package sanpham;

import util.BangConsole;
import util.HienThiBang;


import java.time.LocalDate;
import java.util.Scanner;

import util.Ktradulieu;

public abstract class SanPham implements HienThiBang {
    protected String maSP;
    protected String tenSP;
    protected String maNCC;
    protected double donGiaNhap;
    protected double donGiaBan;
    protected int soLuongTon;
    protected String donViTinh;
    protected LocalDate nsx;
    protected LocalDate hsd;
    protected boolean daXoa;
    protected double phanTramThue;

    public SanPham(String maSP, String tenSP, double donGiaNhap, double donGiaBan, int soLuongTon,
                   String donViTinh, LocalDate nsx, LocalDate hsd, double phanTramThue, boolean daXoa) {
        this(maSP, tenSP, donGiaNhap, donGiaBan, soLuongTon, donViTinh, nsx, hsd, phanTramThue, "", daXoa);
    }

    public SanPham(String maSP, String tenSP, double donGiaNhap, double donGiaBan, int soLuongTon,
                   String donViTinh, LocalDate nsx, LocalDate hsd, double phanTramThue, String maNCC, boolean daXoa) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.maNCC = ""; // Dữ liệu cũ chưa có nhà cung cấp.
        if (maNCC != null && !maNCC.isBlank()) setMaNCC(maNCC);
        setDonGiaNhap(donGiaNhap);
        setDonGiaBan(donGiaBan);
        setSoLuongTon(soLuongTon);
        this.donViTinh = donViTinh;
        setHSD(nsx, hsd);
        setPhanTramThue(phanTramThue);
        this.daXoa = daXoa;
    }

    public SanPham() {
        maSP = "";
        tenSP = "";
        maNCC = "";
        donGiaNhap = 0;
        donGiaBan = 0;
        soLuongTon = 0;
        phanTramThue = 0;
        nsx = null;
        hsd = null;
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

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        if (maNCC == null) throw new IllegalArgumentException("[Lỗi] Mã nhà cung cấp không được trống!");
        String ma = Ktradulieu.chuanHoaMa(maNCC);
        if (!ma.matches("NCC\\d+")) {
            throw new IllegalArgumentException("[Lỗi] Mã nhà cung cấp phải có dạng NCC001!");
        }
        this.maNCC = ma;
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
        if (!Double.isFinite(donGiaNhap) || donGiaNhap < 0) {
            throw new IllegalArgumentException("[Lỗi] Đơn giá nhập phải >= 0! Nhận: " + donGiaNhap);
        }
        this.donGiaNhap = donGiaNhap;
    }

    public double getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(double donGiaBan) {
        if (!Double.isFinite(donGiaBan) || donGiaBan <= 0) {
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
        if (!Double.isFinite(phanTramThue) || phanTramThue < 0 || phanTramThue > 100) {
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


    public LocalDate getNsx() {
        return nsx;
    }

    public LocalDate getHsd() {
        return hsd;
    }

    public void setHSD(LocalDate nsx, LocalDate hsd) {
        if (nsx == null || hsd == null) {
            throw new IllegalArgumentException("[Lỗi Ngày] Ngày sản xuất và hạn sử dụng không được để trống!");
        }
        if (hsd.isBefore(nsx)) {
            throw new IllegalArgumentException("[Lỗi HSD] Hạn sử dụng ("
                    + Ktradulieu.dinhDangNgay(hsd) + ") không thể trước ngày sản xuất ("
                    + Ktradulieu.dinhDangNgay(nsx) + ")!");
        }
        this.nsx = nsx;
        this.hsd = hsd;
    }

    // Nhập bổ sung: giữ số ngày sử dụng của sản phẩm, tính lại từ ngày nhập kho.
    public LocalDate tinhHsdKhiNhapHang(LocalDate ngayNhap) {
        if (ngayNhap == null || nsx == null || hsd == null || hsd.isBefore(nsx)) {
            throw new IllegalArgumentException("[Lỗi] Chưa có NSX/HSD hợp lệ để cập nhật ngày nhập hàng!");
        }
        long soNgaySuDung = java.time.temporal.ChronoUnit.DAYS.between(nsx, hsd);
        try {
            return ngayNhap.plusDays(soNgaySuDung);
        } catch (java.time.DateTimeException e) {
            throw new IllegalArgumentException("[Lỗi] Hạn sử dụng mới vượt phạm vi ngày cho phép!", e);
        }
    }

    public boolean isDaXoa() {
        return daXoa;
    }

    public boolean daHetHan(LocalDate ngayKiemTra) {
        if (ngayKiemTra == null) throw new IllegalArgumentException("Ngày kiểm tra không được trống!");
        return hsd != null && hsd.isBefore(ngayKiemTra);
    }

    public boolean sapHetHan(LocalDate ngayKiemTra, int soNgayCanhBao) {
        if (ngayKiemTra == null || soNgayCanhBao < 0) {
            throw new IllegalArgumentException("Ngày kiểm tra hoặc số ngày cảnh báo không hợp lệ!");
        }
        if (hsd == null) return false;
        long soNgayConLai = java.time.temporal.ChronoUnit.DAYS.between(ngayKiemTra, hsd);
        return soNgayConLai >= 0 && soNgayConLai <= soNgayCanhBao;
    }

    public void nhapThemHang(int soLuongNhap, LocalDate ngayNhap) {
        if (daXoa || soLuongNhap <= 0) {
            throw new IllegalArgumentException("Sản phẩm đã xóa hoặc số lượng nhập không hợp lệ!");
        }
        LocalDate hsdMoi = tinhHsdKhiNhapHang(ngayNhap);
        boolean hetHan = daHetHan(ngayNhap);
        int tonCu = hetHan ? 0 : soLuongTon;
        int tonMoi = Math.addExact(tonCu, soLuongNhap);
        if (hetHan && soLuongTon > 0) {
            System.out.println("[Loại tồn hết hạn] " + maSP + ": " + soLuongTon + " " + donViTinh);
        }
        setSoLuongTon(tonMoi);
        setHSD(ngayNhap, hsdMoi);
    }

    public void setDaXoa(boolean daXoa) {
        this.daXoa = daXoa;
    }

    // kiểm tra hạn sử dụng và cập nhật kho

    public boolean kiemTraHanSuDung() {
        if (this.hsd == null) {
            return false;
        }

        if (daHetHan(LocalDate.now())) {
            if (this.soLuongTon > 0) {
                int tonCu = this.soLuongTon;
                this.soLuongTon = 0; // Cập nhật lại số lượng kho về 0

                System.out.println("Sản phẩm [" + this.maSP + "] " + this.tenSP
                        + " ĐÃ HẾT HẠN (HSD: " + Ktradulieu.dinhDangNgay(this.hsd) + ")! Đã loại "
                        + tonCu + " đơn vị hết hạn khỏi tồn kho (Tồn kho = 0).");
            }
            return false;
        }
        return true;
    }

    public void congTonKho ( int sl){
        if (sl <= 0) {
            throw new IllegalArgumentException("[Lỗi Kho] Số lượng cộng kho phải > 0! Nhận: " + sl);
        }
        this.soLuongTon = Math.addExact(this.soLuongTon, sl);
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
    public abstract String toFileString();


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
        this.setSoLuongTon(0);
        System.out.println("Tồn kho ban đầu = 0. Lập hóa đơn nhập và duyệt phiếu để cộng hàng vào kho.");
        this.setDonViTinh(Ktradulieu.nhapChuoi(sc, "Nhập đơn vị tính (gói/chai/hộp...): ", ".+", "Không được để trống!"));
        this.setPhanTramThue(Ktradulieu.nhapSoThuc(sc, "Nhập thuế VAT (%): "));

        LocalDate dNsx;

        while (true) {
            dNsx = Ktradulieu.nhapNgay(
                    sc, "Nhập ngày sản xuất (dd/MM/yyyy): "
            );

            if (dNsx.isAfter(LocalDate.now())) {
                System.out.println("[Lỗi] Ngày sản xuất không được ở tương lai!");
                System.out.println("Vui lòng nhập lại ngày sản xuất!");
                continue;
            }

            break;
        }

        while (true) {
            LocalDate dHsd = Ktradulieu.nhapNgay(
                    sc, "Nhập hạn sử dụng (dd/MM/yyyy): "
            );

            try {
                this.setHSD(dNsx, dHsd);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("Vui lòng nhập lại hạn sử dụng!");
            }
        }

        this.daXoa = false;
    }

    // Cột 14 mới là mã NCC; vẫn đọc được file cũ có cột duyệt hoặc chưa có mã NCC.
    public static SanPham tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty()) {
            throw new IllegalArgumentException("Dòng sản phẩm không được rỗng!");
        }
        String[] p = dong.split(";", -1);
        if (p.length != 15 && p.length != 16) {
            throw new IllegalArgumentException("Dòng sản phẩm phải có 16 cột (hoặc 15 cột ở file cũ)!");
        }
        String cotCuoi = p[p.length - 1].trim();
        boolean daXoa;
        if ("true".equalsIgnoreCase(cotCuoi) || "false".equalsIgnoreCase(cotCuoi)) {
            daXoa = Boolean.parseBoolean(cotCuoi);
        } else if (p.length == 15 && ("Đã duyệt".equalsIgnoreCase(cotCuoi)
                || "Chờ duyệt".equalsIgnoreCase(cotCuoi) || "Đang xử lý".equalsIgnoreCase(cotCuoi))) {
            daXoa = false; // File cũ chưa có cột xóa.
        } else {
            throw new IllegalArgumentException("Giá trị xóa sản phẩm phải là true hoặc false!");
        }
        String ma = p[1].trim();
        String ten = p[2].trim();
        double giaNhap = Double.parseDouble(p[3].trim());
        double giaBan = Double.parseDouble(p[4].trim());
        int ton = Integer.parseInt(p[5].trim());
        String dvt = p[6].trim();
        LocalDate nsx = Ktradulieu.chuyenChuoiThanhNgay(p[7]);
        LocalDate hsd = Ktradulieu.chuyenChuoiThanhNgay(p[8]);
        double thue = Double.parseDouble(p[9].trim());
        String maNCC = "";
        if (p.length == 16) {
            String cotNCC = p[14].trim();
            if (cotNCC.matches("(?i)NCC\\d+")) maNCC = cotNCC;
            else if (!cotNCC.isEmpty() && !"Đã duyệt".equalsIgnoreCase(cotNCC)
                    && !"Chờ duyệt".equalsIgnoreCase(cotNCC) && !"Đang xử lý".equalsIgnoreCase(cotNCC)) {
                throw new IllegalArgumentException("Mã nhà cung cấp trong file không hợp lệ!");
            }
        }
        if ("THUCAN".equalsIgnoreCase(p[0].trim())) {
            return new ThucAn(ma, ten, giaNhap, giaBan, ton, dvt, nsx, hsd, thue,
                    p[10].trim(), p[11].trim(), p[12].trim(), Boolean.parseBoolean(p[13].trim()), maNCC, daXoa);
        }
        if ("DOUONG".equalsIgnoreCase(p[0].trim())) {
            return new DoUong(ma, ten, giaNhap, giaBan, ton, dvt, nsx, hsd, thue,
                    Double.parseDouble(p[10].trim()), p[11].trim(), p[12].trim(), p[13].trim(), maNCC, daXoa);
        }
        throw new IllegalArgumentException("Loại sản phẩm không hợp lệ: " + p[0]);
    }

    @Override
    public String[] layTieuDeBang() {
        return new String[]{"MÃ SP", "LOẠI", "TÊN SẢN PHẨM", "MÃ NCC", "TỒN KHO", "ĐƠN VỊ", "NGÀY SẢN XUẤT", "HẠN SỬ DỤNG", "GIÁ BÁN", "THUẾ", "CHI TIẾT"};
    }

    @Override
    public Object[] layDongBang() {
        return new Object[]{maSP, getLoaiSanPham(), tenSP, maNCC.isEmpty() ? "Chưa gán" : maNCC, soLuongTon, donViTinh,
                Ktradulieu.dinhDangNgay(nsx), Ktradulieu.dinhDangNgay(hsd),
                Ktradulieu.dinhDangTien(tinhGiaBan()), phanTramThue + "% ("
                + Ktradulieu.dinhDangTien(tinhTienThueDonVi()) + "/đv)", layChiTietBang()};
    }

    protected String layChiTietBang() {
        return "-";
    }

    @Override
    public String toString() {
        return BangConsole.tuDoiTuong(this);
    }
}
