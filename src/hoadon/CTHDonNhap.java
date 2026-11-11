package hoadon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import sanpham.DSSanPham;
import sanpham.SanPham;
import sanpham.ThucAn;
import sanpham.DoUong;
import util.Ktradulieu;

public class CTHDonNhap extends CTHoaDon {

    private double thueSuatNhap;
    private LocalDate hanSuDung;

    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public CTHDonNhap() {
        super();
        this.thueSuatNhap = 0.0;
        this.hanSuDung = LocalDate.now().plusMonths(6);
    }


    public CTHDonNhap(String maHD, String maSP, String tenSP, int soLuongNhap,
                    double donGiaNhap, double thueSuatNhap, LocalDate hanSuDung) {
        super(maHD, maSP, tenSP, soLuongNhap, donGiaNhap);
        setThueSuatNhap(thueSuatNhap);
        setHanSuDung(hanSuDung);
    }


    public CTHDonNhap(String maHD, SanPham sp, int soLuongNhap, double donGiaNhap, LocalDate hanSuDung) {
        super(maHD, sp.getMaSP(), sp.getTenSP(), soLuongNhap, donGiaNhap > 0 ? donGiaNhap : sp.getDonGiaNhap());
        setThueSuatNhap(sp.getPhanTramThue());
        setHanSuDung(hanSuDung);
    }


    public int getSoLuongNhap() { return getSoLuong(); }
    public void setSoLuongNhap(int sl) { setSoLuong(sl); }

    public double getDonGiaNhap() { return getDonGia(); }
    public void setDonGiaNhap(double dg) { setDonGia(dg); }


    public boolean nhapThongTin(Scanner sc, String maHoaDon, DSSanPham dsSP) {
        if (sc == null) sc = new Scanner(System.in);
        setMaHD(maHoaDon);

        SanPham spDuocChon = null;

        while (true) {
            System.out.print("Nhập mã sản phẩm cần nhập kho (Ví dụ: TA001, DU001): ");
            String input = sc.nextLine().trim();

            try {
                String maChuan = Ktradulieu.chuanHoaMa(input);
                if (!maChuan.startsWith("TA") && !maChuan.startsWith("DU") && !maChuan.startsWith("SP")) {
                    System.out.println("[Lỗi] Mã sản phẩm phải bắt đầu bằng 'TA' (Thức ăn) hoặc 'DU' (Đồ uống)!");
                    continue;
                }

                if (dsSP != null) {
                    spDuocChon = dsSP.timKiem(maChuan);

                    // Truong hop 1: San pham da co trong kho
                    if (spDuocChon != null) {
                        setMaSP(spDuocChon.getMaSP());
                        setTenSP(spDuocChon.getTenSP());
                        setDonGia(spDuocChon.getDonGiaNhap());
                        setThueSuatNhap(spDuocChon.getPhanTramThue());
                        System.out.println("-> Đã nhận diện sản phẩm có sẵn: [" + this.maSP + "] " + this.tenSP);
                        break;
                    }

                    // Truong hop 2: San pham moi chua co trong kho -> Trinh Quan ly duyet
                    System.out.println("[Thông báo] Mã sản phẩm '" + maChuan + "' CHƯA TỒN TẠI trong danh mục kho!");
                    System.out.print("Bạn có muốn lập hồ sơ sản phẩm mới để gửi Quản lý duyệt không? (1: Có / 0: Hủy bỏ): ");
                    String chon = sc.nextLine().trim();
                    if (!chon.equals("1")) {
                        return false;
                    }

                    SanPham spMoi = taoHoSoSanPhamMoi(sc, maChuan);
                    if (spMoi == null) return false;

                    boolean duyet = pheDuyetQuanLy(sc, spMoi);
                    if (!duyet) {
                        System.out.println("[Từ chối] Quản lý KHÔNG DUYỆT sản phẩm mới này vào kho! Hủy bỏ dòng nhập hàng.");
                        return false;
                    }

                    spMoi.setTrangThaiDuyet("Đã duyệt");
                    dsSP.themSP(spMoi);
                    System.out.println("[Thành công] Quản lý ĐÃ DUYỆT! Sản phẩm [" + spMoi.getMaSP() + "] đã được thêm vào kho.");

                    setMaSP(spMoi.getMaSP());
                    setTenSP(spMoi.getTenSP());
                    setDonGia(spMoi.getDonGiaNhap());
                    setThueSuatNhap(spMoi.getPhanTramThue());
                    break;
                } else {
                    setMaSP(maChuan);
                    String tenNhap = Ktradulieu.nhapChuoi(sc, "Nhập tên sản phẩm: ", ".+", "Tên không được để trống!");
                    setTenSP(tenNhap);
                    break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Nhap so luong nhap
        while (true) {
            try {
                int sl = (int) Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng nhập kho: ");
                setSoLuong(sl);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Don gia nhap
        while (true) {
            try {
                System.out.print("Đơn giá nhập gốc là: " + Ktradulieu.dinhDangTien(this.donGia)
                        + ". Nhấn ENTER để giữ nguyên, hoặc nhập đơn giá mới: ");
                String dgInput = sc.nextLine().trim();
                if (!dgInput.isEmpty()) {
                    double dgMoi = Double.parseDouble(dgInput);
                    setDonGia(dgMoi);
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("[Lỗi] Đơn giá phải là số hợp lệ!");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Han su dung
        while (true) {
            try {
                LocalDate hsd = Ktradulieu.nhapNgay(sc, "Nhập hạn sử dụng cho lô hàng mới (dd/MM/yyyy): ");
                setHanSuDung(hsd);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        return true;
    }

    private SanPham taoHoSoSanPhamMoi(Scanner sc, String maSP) {
        System.out.println("\n--- KHAI BÁO HỒ SƠ SẢN PHẨM MỚI ---");
        String ten = Ktradulieu.nhapChuoi(sc, "Nhập tên sản phẩm mới: ", ".+", "Tên không được để trống!");
        double giaNhap = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập đơn giá nhập dự kiến (VND): ");
        double giaBan = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập đơn giá bán niêm yết (VND): ");
        String dvt = Ktradulieu.nhapChuoi(sc, "Nhập đơn vị tính (gói, lon, chai...): ", ".+", "DVT không được để trống!");
        double thue = Ktradulieu.nhapSoThucKhongAm(sc, "Nhập thuế suất VAT (%): ");
        LocalDate nsx = Ktradulieu.nhapNgay(sc, "Nhập ngày sản xuất (dd/MM/yyyy): ");
        LocalDate hsd = Ktradulieu.nhapNgay(sc, "Nhập hạn sử dụng (dd/MM/yyyy): ");

        String nsxStr = nsx.format(DINH_DANG_NGAY);
        String hsdStr = hsd.format(DINH_DANG_NGAY);

        // Khởi tạo đối tượng tương ứng
        SanPham spMoi;
        if (maSP.startsWith("DU")) {
            spMoi = new DoUong();
        } else {
            spMoi = new ThucAn();
        }

        spMoi.setMaSP(maSP);
        spMoi.setTenSP(ten);
        spMoi.setDonGiaNhap(giaNhap);
        spMoi.setDonGiaBan(giaBan);
        spMoi.setSoLuongTon(0);
        spMoi.setDonViTinh(dvt);
        spMoi.setHSD(nsxStr, hsdStr);
        spMoi.setPhanTramThue(thue);
        spMoi.setTrangThaiDuyet("Chờ duyệt");
        spMoi.setDaXoa(false);

        return spMoi;
    }

    private boolean pheDuyetQuanLy(Scanner sc, SanPham spMoi) {
        System.out.println("\n================ PHIẾU TRÌNH DUYỆT SẢN PHẨM MỚI ================");
        System.out.println("Mã SP     : " + spMoi.getMaSP());
        System.out.println("Tên SP    : " + spMoi.getTenSP());
        System.out.println("Giá nhập  : " + Ktradulieu.dinhDangTien(spMoi.getDonGiaNhap()));
        System.out.println("Giá bán   : " + Ktradulieu.dinhDangTien(spMoi.getDonGiaBan()));
        System.out.println("----------------------------------------------------------------");
        System.out.print("👉 Ý KIẾN QUẢN LÝ (1: Đồng ý duyệt vào kho  |  0: Từ chối): ");
        String yKien = sc.nextLine().trim();
        return yKien.equals("1");
    }

    // =========================================================================
    // TÍNH TOÁN TIỀN (TRIỂN KHAI TỪ LỚP CHA CTHoaDon)
    // =========================================================================
    @Override
    public double tinhTienHang() {
        return this.soLuong * this.donGia;
    }

    @Override
    public double tinhTienThue() {
        return tinhTienHang() * (this.thueSuatNhap / 100.0);
    }

    @Override
    public double tinhThanhTien() {
        return tinhTienHang() + tinhTienThue();
    }

    @Override
    public String toDongFile() {
        String hsdStr = (this.hanSuDung != null) ? this.hanSuDung.format(DINH_DANG_NGAY) : "";
        return String.format("%s;%s;%s;%d;%.0f;%.2f;%s",
                maHD, maSP, tenSP, soLuong, donGia, thueSuatNhap, hsdStr);
    }

    public static CTHDonNhap tuDongFile(String dong) {
        if (dong == null || dong.trim().isEmpty()) return null;
        String[] p = dong.split(";");
        if (p.length >= 7) {
            String maHD = p[0].trim();
            String maSP = p[1].trim();
            String tenSP = p[2].trim();
            int sl = Integer.parseInt(p[3].trim());
            double dg = Double.parseDouble(p[4].trim());
            double thue = Double.parseDouble(p[5].trim());
            LocalDate hsd = LocalDate.parse(p[6].trim(), DINH_DANG_NGAY);
            return new CTHDonNhap(maHD, maSP, tenSP, sl, dg, thue, hsd);
        }
        return null;
    }

    public double getThueSuatNhap() { return thueSuatNhap; }
    public void setThueSuatNhap(double thueSuatNhap) {
        if (thueSuatNhap < 0 || thueSuatNhap > 100) throw new IllegalArgumentException("[Lỗi] Thuế suất phải từ 0% đến 100%!");
        this.thueSuatNhap = thueSuatNhap;
    }

    public LocalDate getHanSuDung() { return hanSuDung; }
    public void setHanSuDung(LocalDate hanSuDung) {
        if (hanSuDung == null) throw new IllegalArgumentException("[Lỗi] Hạn sử dụng không được để trống!");
        if (hanSuDung.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("[Lỗi Logic] Hàng nhập kho không được là hàng đã hết hạn (" + hanSuDung.format(DINH_DANG_NGAY) + ")!");
        }
        this.hanSuDung = hanSuDung;
    }

    @Override
    public String toString() {
        String hsdStr = (this.hanSuDung != null) ? this.hanSuDung.format(DINH_DANG_NGAY) : "N/A";
        return String.format("%-8s | %-20s | SL: %-4d | Đơn giá: %-12s | Thuế: %-4.1f%% | HSD: %-10s | Thành tiền: %s",
                maSP, tenSP, soLuong, Ktradulieu.dinhDangTien(donGia),
                thueSuatNhap, hsdStr, Ktradulieu.dinhDangTien(tinhThanhTien()));
    }
}