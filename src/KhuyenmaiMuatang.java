import java.time.LocalDate;

public class KhuyenmaiMuatang extends Khuyenmai {

    private int soLuongMuaToiThieu;
    private int soLuongTang;

    public KhuyenmaiMuatang() {
        super();
        this.soLuongMuaToiThieu = 0;
        this.soLuongTang = 0;
        setLoaiKM("Mua tặng");
    }

    public KhuyenmaiMuatang(
            String maKM,
            String loaiKM,
            LocalDate ngayBatDau,
            LocalDate ngayKetThuc,
            long mucChiToiThieu,
            int soLuongMuaToiThieu,
            int soLuongTang) {

        super(maKM, loaiKM, ngayBatDau, ngayKetThuc, mucChiToiThieu);
        setSoLuongMuaToiThieu(soLuongMuaToiThieu);
        setSoLuongTang(soLuongTang);
        if (loaiKM == null || loaiKM.trim().isEmpty()) {
            capNhatMoTaLoaiKM();
        }
    }

    private void capNhatMoTaLoaiKM() {
        if (soLuongMuaToiThieu > 0 && soLuongTang > 0) {
            setLoaiKM("Mua " + soLuongMuaToiThieu + " tặng " + soLuongTang);
        } else {
            setLoaiKM("Mua tặng");
        }
    }

    @Override
    public void setKM() {
        super.setKM();

        do {
            soLuongMuaToiThieu = (int) Ktradulieu.nhapSoNguyen(
                    sc,
                    "Nhập số lượng sản phẩm mua tối thiểu: "
            );

            if (soLuongMuaToiThieu <= 0) {
                System.out.println("Số lượng mua tối thiểu phải lớn hơn 0!");
            }
        } while (soLuongMuaToiThieu <= 0);

        do {
            soLuongTang = (int) Ktradulieu.nhapSoNguyen(
                    sc,
                    "Nhập số lượng sản phẩm tặng: "
            );

            if (soLuongTang <= 0) {
                System.out.println("Số lượng tặng phải lớn hơn 0!");
            }
        } while (soLuongTang <= 0);

        capNhatMoTaLoaiKM();
    }

    @Override
    public int tinhSoLuongTang(int soLuongMua) {
        if (soLuongMuaToiThieu <= 0 || soLuongTang <= 0
                || soLuongMua < soLuongMuaToiThieu) {
            return 0;
        }
        return (soLuongMua / soLuongMuaToiThieu) * soLuongTang;
    }

    public int getSoLuongMuaToiThieu() {
        return soLuongMuaToiThieu;
    }

    public int getSoLuongTang() {
        return soLuongTang;
    }

    public void setSoLuongMuaToiThieu(int soLuongMuaToiThieu) {
        if (soLuongMuaToiThieu <= 0) {
            throw new IllegalArgumentException(
                    "Số lượng mua tối thiểu phải lớn hơn 0!"
            );
        }
        this.soLuongMuaToiThieu = soLuongMuaToiThieu;
        capNhatMoTaLoaiKM();
    }

    public void setSoLuongTang(int soLuongTang) {
        if (soLuongTang <= 0) {
            throw new IllegalArgumentException(
                    "Số lượng tặng phải lớn hơn 0!"
            );
        }
        this.soLuongTang = soLuongTang;
        capNhatMoTaLoaiKM();
    }

    // Mua tặng không giảm tiền trực tiếp
    @Override
    public long tinhTienGiam(long giaBan) {
        return 0;
    }

    @Override
    public Khuyenmai taoKhuyenMaiRong() {
        return new KhuyenmaiMuatang();
    }

    @Override
    public String toString() {
        return super.toString()
                + "\nSố lượng mua tối thiểu: " + soLuongMuaToiThieu
                + "\nSố lượng tặng: " + soLuongTang;
    }
}
