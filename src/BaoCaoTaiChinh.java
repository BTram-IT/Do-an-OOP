import java.util.Scanner;

public class BaoCaoTaiChinh {
    private String maBaoCao;
    private String kyBaoCao; // VD: "Thang 10/2026"
    private String nguoiLap; // Mã hoặc tên Kế toán lập báo cáo
    private double tongDoanhThu; // Tổng tiền từ các HoaDon
    private double tongChiPhiNhapHang; // Tổng tiền từ các Phieunhap
    private double tongChiPhiLuong; // Tổng lương trả cho Nhanvien
    private double chiPhiKhac; // Điện, nước, mặt bằng...

    public BaoCaoTaiChinh() {
    }

    public BaoCaoTaiChinh(String maBaoCao, String kyBaoCao, String nguoiLap, 
                          double tongDoanhThu, double tongChiPhiNhapHang, 
                          double tongChiPhiLuong, double chiPhiKhac) {
        this.maBaoCao = maBaoCao;
        this.kyBaoCao = kyBaoCao;
        this.nguoiLap = nguoiLap;
        this.tongDoanhThu = tongDoanhThu;
        this.tongChiPhiNhapHang = tongChiPhiNhapHang;
        this.tongChiPhiLuong = tongChiPhiLuong;
        this.chiPhiKhac = chiPhiKhac;
    }

    // Getters và Setters
    public String getMaBaoCao() { return maBaoCao; }
    public void setMaBaoCao(String maBaoCao) { this.maBaoCao = maBaoCao; }

    public String getKyBaoCao() { return kyBaoCao; }
    public void setKyBaoCao(String kyBaoCao) { this.kyBaoCao = kyBaoCao; }

    public String getNguoiLap() { return nguoiLap; }
    public void setNguoiLap(String nguoiLap) { this.nguoiLap = nguoiLap; }

    public double getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(double tongDoanhThu) { this.tongDoanhThu = tongDoanhThu; }

    public double getTongChiPhiNhapHang() { return tongChiPhiNhapHang; }
    public void setTongChiPhiNhapHang(double tongChiPhiNhapHang) { this.tongChiPhiNhapHang = tongChiPhiNhapHang; }

    public double getTongChiPhiLuong() { return tongChiPhiLuong; }
    public void setTongChiPhiLuong(double tongChiPhiLuong) { this.tongChiPhiLuong = tongChiPhiLuong; }

    public double getChiPhiKhac() { return chiPhiKhac; }
    public void setChiPhiKhac(double chiPhiKhac) { this.chiPhiKhac = chiPhiKhac; }

    // Tính tổng chi phí
    public double tinhTongChiPhi() {
        return tongChiPhiNhapHang + tongChiPhiLuong + chiPhiKhac;
    }

    // Tính lợi nhuận ròng
    public double tinhLoiNhuan() {
        return tongDoanhThu - tinhTongChiPhi();
    }

    // Nhập thủ công hoặc bổ sung thông tin báo cáo
    public void nhapThongTinCoBan() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nhap ma bao cao: ");
        this.maBaoCao = sc.nextLine();
        System.out.print("Nhap ky bao cao (VD: Thang 10/2026): ");
        this.kyBaoCao = sc.nextLine();
        System.out.print("Nhap ten/ma nguoi lap (Ke toan): ");
        this.nguoiLap = sc.nextLine();
        System.out.print("Nhap chi phi phat sinh khac (dien, nuoc, mat bang...): ");
        this.chiPhiKhac = Double.parseDouble(sc.nextLine());
    }

    // Hàm tự động lấy số liệu từ danh sách Hóa đơn, Phiếu nhập, Chấm công (Bạn có thể điều chỉnh tên hàm cho khớp với project)
    public void tongHopSoLieu(HoaDon[] dsHoaDon, HoadonNhap[] dsPhieuNhap, ChamCong[] dsChamCong) {
        this.tongDoanhThu = 0;
        if (dsHoaDon != null) {
            for (HoaDon hd : dsHoaDon) {
                if (hd != null) {
                    // Giả sử lớp HoaDon của bạn có hàm getTongTien() hoặc tinhTongTien()
                    // this.tongDoanhThu += hd.getTongTien(); 
                }
            }
        }

        this.tongChiPhiNhapHang = 0;
        if (dsPhieuNhap != null) {
            for (HoadonNhap pn : dsPhieuNhap) {
                if (pn != null) {
                    // Giả sử lớp Phieunhap của bạn có hàm getTongTien()
                    // this.tongChiPhiNhapHang += pn.getTongTien();
                }
            }
        }

        this.tongChiPhiLuong = 0;
        if (dsChamCong != null) {
            for (ChamCong cc : dsChamCong) {
                if (cc != null) {
                    this.tongChiPhiLuong += cc.tinhTienCongNgay();
                }
            }
        }
    }

    public void xuatBaoCao() {
        System.out.println("==================================================");
        System.out.println("               BAO CAO TAI CHINH                  ");
        System.out.println("==================================================");
        System.out.println("Ma bao cao : " + maBaoCao);
        System.out.println("Ky bao cao : " + kyBaoCao);
        System.out.println("Nguoi lap  : " + nguoiLap);
        System.out.println("--------------------------------------------------");
        System.out.printf("1. Tong Doanh Thu Ban Hang  : %,15.0f VND\n", tongDoanhThu);
        System.out.printf("2. Chi Phi Nhap Hang        : %,15.0f VND\n", tongChiPhiNhapHang);
        System.out.printf("3. Chi Phi Luong Nhan Vien  : %,15.0f VND\n", tongChiPhiLuong);
        System.out.printf("4. Chi Phi Khac             : %,15.0f VND\n", chiPhiKhac);
        System.out.println("--------------------------------------------------");
        System.out.printf("=> TONG CHI PHI             : %,15.0f VND\n", tinhTongChiPhi());
        System.out.printf("=> LOI NHUAN RONG           : %,15.0f VND\n", tinhLoiNhuan());
        System.out.println("==================================================");
    }
}
