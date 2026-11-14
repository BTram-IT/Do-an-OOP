package khuyenmai;


import java.util.Locale;
import java.time.LocalDate;
import java.util.Scanner;
import util.Ktradulieu;

public class KhuyenmaiMuatang extends Khuyenmai {

    private int soLuongMuaToiThieu;
    private int soLuongTang;

    // Constructor mặc định
    public KhuyenmaiMuatang() {
        super();
        this.soLuongMuaToiThieu = 0;
        this.soLuongTang = 0;
        setLoaiKM("MuaTang");
    }

    // Constructor đầy đủ
    public KhuyenmaiMuatang(
            String maKM,
            String loaiKM,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            int soLuongMuaToiThieu,
            int soLuongTang) {

        super(
                maKM,
                (loaiKM == null || loaiKM.trim().isEmpty())
                        ? "MuaTang" : loaiKM,
                ngayBatDau,
                ngayKetThuc
        );

        setDieuKienMuaTang(soLuongMuaToiThieu, soLuongTang);
    }

    // Cập nhật mô tả loại khuyến mãi
    private void capNhatMoTaLoaiKM() {
        if (soLuongMuaToiThieu > 0 && soLuongTang > 0) {
            this.loaiKM = "Mua " + soLuongMuaToiThieu + " tặng " + soLuongTang;
        } else {
            this.loaiKM = "MuaTang";
        }
    }

    // Kiểm tra số lượng hợp lệ
    private void kiemTraSoLuong(int mua, int tang) {
        if (mua <= 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng mua tối thiểu phải lớn hơn 0!");
        }

        if (tang <= 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng tặng phải lớn hơn 0!");
        }
    }

    // Cập nhật đồng thời hai điều kiện mua và tặng
    public void setDieuKienMuaTang(int mua, int tang) {
        kiemTraSoLuong(mua, tang);

        this.soLuongMuaToiThieu = mua;
        this.soLuongTang = tang;

        capNhatMoTaLoaiKM();
    }

    // Nhập thông tin khuyến mãi
    @Override
    public void nhapThongTin(Scanner sc, DSKhuyenMai dsKM) {
        super.nhapThongTin(sc, dsKM);

        System.out.println("\n--- THIẾT LẬP ĐIỀU KIỆN MUA TẶNG ---");

        int mua;
        int tang;

        while (true) {
            try {
                mua = (int) Ktradulieu.nhapSoNguyen(
                        sc, "Nhập số lượng sản phẩm mua tối thiểu: ");

                tang = (int) Ktradulieu.nhapSoNguyen(
                        sc, "Nhập số lượng sản phẩm tặng kèm: ");

                setDieuKienMuaTang(mua, tang);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println("Vui lòng nhập lại cả hai số lượng!");
            }
        }

        System.out.println("Đã thiết lập xong chương trình: [" + this.maKM + "] " + this.loaiKM);
    }

    // Tính số lượng sản phẩm được tặng
    @Override
    public int tinhSoLuongTang(int soLuongMua) {
        if (soLuongMua <= 0) {
            return 0;
        }

        if (soLuongMuaToiThieu <= 0 || soLuongTang <= 0) {
            return 0;
        }

        // Tính số lần đạt điều kiện mua
        int soLanMua = soLuongMua / soLuongMuaToiThieu;

        try {
            long tongTang = Math.multiplyExact(
                    (long) soLanMua,
                    (long) soLuongTang
            );

            if (tongTang > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("[Lỗi] Số lượng sản phẩm tặng vượt giới hạn!");
            }

            return (int) tongTang;

        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("[Lỗi] Số lượng sản phẩm tặng quá lớn!", e);
        }
    }

    // Kiểm tra kho có đủ cả hàng mua và hàng tặng hay không
    public boolean kiemTraDuHangDeApDung(int soLuongMua, int soLuongTon) {
        if (soLuongMua <= 0 || soLuongTon < 0) {
            return false;
        }

        int soLuongTangDuoc = tinhSoLuongTang(soLuongMua);
        long tongCanXuat = (long) soLuongMua + soLuongTangDuoc;

        return soLuongTon >= tongCanXuat;
    }

    // Tính tổng số lượng xuất kho khi áp dụng khuyến mãi
    public int tinhTongSoLuongXuatKho(int soLuongMua) {
        if (soLuongMua <= 0) {
            return 0;
        }

        int soLuongTangDuoc = tinhSoLuongTang(soLuongMua);

        try {
            return Math.addExact(soLuongMua, soLuongTangDuoc);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("[Lỗi] Tổng số lượng xuất kho vượt giới hạn!", e);
        }
    }

    // Tính số lượng tặng khi xét đến tồn kho
    public int tinhSoLuongTangNeuDuHang(int soLuongMua, int soLuongTon) {
        if (!kiemTraDuHangDeApDung(soLuongMua, soLuongTon)) {
            return 0;
        }

        return tinhSoLuongTang(soLuongMua);
    }

    // Mua tặng không giảm tiền trực tiếp
    @Override
    public long tinhTienGiam(long giaBan) {
        return 0;
    }

    // Getter và Setter
    public int getSoLuongMuaToiThieu() {
        return soLuongMuaToiThieu;
    }

    public void setSoLuongMuaToiThieu(int soLuongMuaToiThieu) {
        kiemTraSoLuong(soLuongMuaToiThieu, this.soLuongTang);
        this.soLuongMuaToiThieu = soLuongMuaToiThieu;
        capNhatMoTaLoaiKM();
    }

    public int getSoLuongTang() {
        return soLuongTang;
    }

    public void setSoLuongTang(int soLuongTang) {
        kiemTraSoLuong(this.soLuongMuaToiThieu, soLuongTang);
        this.soLuongTang = soLuongTang;
        capNhatMoTaLoaiKM();
    }

    // Tạo đối tượng khuyến mãi rỗng
    @Override
    public Khuyenmai taoKhuyenMaiRong() {
        return new KhuyenmaiMuatang();
    }

    // Chuyển dữ liệu thành một dòng để ghi file
    @Override
    public String toDongFile() {
        kiemTraSoLuong(soLuongMuaToiThieu, soLuongTang);

        return "MUATANG;"
                + super.toDongFile()
                + String.format(
                Locale.ROOT,
                ";%d;%d",
                soLuongMuaToiThieu,
                soLuongTang
        );
    }

    @Override
    public String toString() {
        return "KhuyenmaiMuatang{" +
                "soLuongMuaToiThieu=" + soLuongMuaToiThieu +
                ", soLuongTang=" + soLuongTang +
                ", maKM='" + maKM + '\'' +
                ", loaiKM='" + loaiKM + '\'' +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                '}';
    }
}