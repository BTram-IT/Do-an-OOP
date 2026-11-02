
import java.util.ArrayList;

public class ThuNgan extends NhanVien {
    private String maTN;
    private String tenThuNgan;
    private String chucVu;

    private String[] caLamViec = {
        "Ca Sang", "Ca Chieu", "Ca Toi"
    };

    private DSHoadon Hoadon;
    private DsKhachHang Khachhang;

    public ThuNgan() {
        super();
        this.maTN = "";
        this.tenThuNgan = "";
        this.chucVu = "Thu ngan";
        this.Hoadon = new DSHoadon();
        this.Khachhang = new DsKhachHang();
    }

    public ThuNgan(String maTN, String tenThuNgan, String chucVu) {
        super();
        this.maTN = maTN;
        this.tenThuNgan = tenThuNgan;
        this.chucVu = chucVu;
        this.Hoadon = new DSHoadon();
        this.Khachhang = new DsKhachHang();

        setMaNV(maTN);
        setTen(tenThuNgan);
        setChucVu(chucVu);
    }

    public ThuNgan(String maTN, String tenThuNgan, String chucVu,
                   DSHoadon hoaDon, DsKhachHang khachHang) {
        this(maTN, tenThuNgan, chucVu);
        if (hoaDon != null) {
            this.Hoadon = hoaDon;
        }
        if (khachHang != null) {
            this.Khachhang = khachHang;
        }
    }

    public String getMaTN() {
        return maTN;
    }

    public String getTenThuNgan() {
        return tenThuNgan;
    }

    public String getChucVu() {
        return chucVu;
    }

    public DSHoadon getHoadon() {
        return Hoadon;
    }

    public DsKhachHang getKhachhang() {
        return Khachhang;
    }

    public void hienThiThongTin() {
        System.out.println("Ma thu ngan: " + maTN);
        System.out.println("Thu ngan: " + tenThuNgan);
        System.out.println("Chuc vu: " + chucVu);
    }

    public void hienThiCaLamViec() {
        System.out.println("Danh sach ca lam viec:");
        for (int i = 0; i < caLamViec.length; i++) {
            System.out.println((i + 1) + ". " + caLamViec[i]);
        }
    }

    // Them khach hang
    public void themKhachHang(Khachhang kh) {
        if (kh == null) {
            System.out.println("Khach hang khong hop le!");
            return;
        }

        if (Khachhang.timKiemTheoMa(kh.getMaKH()) != null) {
            System.out.println("Ma khach hang da ton tai!");
            return;
        }

        Khachhang.them(kh);
        System.out.println("Them khach hang thanh cong!");
    }

    // Tim khach hang
    public Khachhang timKhachHang(String maKH) {
        return Khachhang.timKiemTheoMa(maKH);
    }

    public void timKhachHangTheoTen(String ten) {
        ArrayList<Khachhang> ketQua = Khachhang.timKiemTheoTen(ten);

        if (ketQua.isEmpty()) {
            System.out.println("Khong tim thay khach hang!");
            return;
        }

        for (Khachhang kh : ketQua) {
            System.out.println(kh);
        }
    }

    // Sua khach hang
    public boolean suaKhachHang(String maKH, Khachhang khMoi) {
        return Khachhang.sua(maKH, khMoi);
    }

    // Xoa khach hang
    public boolean xoaKhachHang(String maKH) {
        return Khachhang.xoa(maKH);
    }

    // In danh sach khach hang
    public void inDanhSachKhachHang() {
        Khachhang.xuatDanhSach();
    }

    public void inDanhSachKhachHangVip() {
        Khachhang.xuatDanhSachVip();
    }

    // Tao hoa don va luu lich su mua hang sau khi thanh toan thanh cong
    public boolean taohoadon(Hoadonban hd) {
        if (hd == null) {
            System.out.println("Hoa don khong hop le!");
            return false;
        }

        Khachhang kh = Khachhang.timKiemTheoMa(hd.getMaKH());

        if (kh == null) {
            System.out.println("Khong tim thay khach hang!");
            return false;
        }

        // Gan ma thu ngan neu hoa don chua co ma nhan vien
        if (hd.getMaNV() == null || hd.getMaNV().trim().isEmpty()) {
            hd.setMaNV(maTN);
        }

        if (!hd.isDaThanhToan()) {
            System.out.println("Hoa don chua thanh toan. Khong them lich su mua hang!");
            return false;
        }

        if (!Hoadon.them(hd)) {
            return false;
        }

        kh.themLichSu(hd.getMaHD(), hd.Tongtienhoadon());
        Khachhang.capNhatDanhSachVip();

        System.out.println("Da them hoa don vao lich su mua hang!");
        System.out.println("Hang thanh vien hien tai: " + kh.getThuHang());

        return true;
    }

    // Tim va in hoa don theo ma khach hang
    public void inds(String maKH) {
        if (Khachhang.timKiemTheoMa(maKH) == null) {
            System.out.println("Khong tim thay khach hang!");
            return;
        }

        boolean timThay = false;

        for (HoaDon hd : Hoadon.getDs()) {
            if (hd instanceof Hoadonban) {
                Hoadonban hdb = (Hoadonban) hd;

                if (hdb.getMaKH().equalsIgnoreCase(maKH)) {
                    hdb.inhoadon();
                    timThay = true;
                }
            }
        }

        if (!timThay) {
            System.out.println("Khach hang chua co hoa don!");
        }
    }

    public void inHoaDonTheoMa(String maHD) {
        Hoadon.xuatTheoMa(maHD);
    }
}
