package khuyenmai;

import java.time.LocalDate;
import java.util.Scanner;
import util.Ktradulieu;

public class KhuyenmaiMuatang extends Khuyenmai {

    private int soLuongMuaToiThieu;
    private int soLuongTang;

    public KhuyenmaiMuatang() {
        super();
        this.soLuongMuaToiThieu = 0;
        this.soLuongTang = 0;
        setLoaiKM("MuaTang");
    }


    public KhuyenmaiMuatang(String maKM, String loaiKM, LocalDate ngayBatDau, LocalDate ngayKetThuc, long mucChiToiThieu, int soLuongMuaToiThieu, int soLuongTang) {
        super(maKM, (loaiKM == null || loaiKM.trim().isEmpty()) ? "MuaTang" : loaiKM, ngayBatDau, ngayKetThuc, mucChiToiThieu);
        this.soLuongMuaToiThieu = soLuongMuaToiThieu;
        this.soLuongTang=soLuongTang;
        capNhatMoTaLoaiKM();
    }

    private void capNhatMoTaLoaiKM() {
        if (soLuongMuaToiThieu > 0 && soLuongTang > 0) {
            this.loaiKM = "Mua " + soLuongMuaToiThieu + " tặng " + soLuongTang;
        } else {
            this.loaiKM = "MuaTang";
        }
    }

    @Override
    public void nhapThongTin(Scanner sc, DSKhuyenMai dsKM) {
        super.nhapThongTin(sc, dsKM);
        System.out.println("--- THIẾT LẬP ĐIỀU KIỆN MUA TẶNG ---");
        while (true) {
            try {
                int mua = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng sản phẩm mua tối thiểu: ");
                setSoLuongMuaToiThieu(mua);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {
            try {
                int tang = Ktradulieu.nhapSoNguyen(sc, "Nhập số lượng sản phẩm tặng kèm: ");
                setSoLuongTang(tang);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println( e.getMessage());
            }
        }

        capNhatMoTaLoaiKM();
        System.out.println("Đã thiết lập xong chương trình: [" + this.maKM + "] " + this.loaiKM);
    }

    @Override
    public int tinhSoLuongTang(int soLuongMua) {
        if (soLuongMuaToiThieu <= 0 || soLuongTang <= 0 || soLuongMua < soLuongMuaToiThieu) {
            return 0;
        }
        return (soLuongMua / soLuongMuaToiThieu) * soLuongTang;
    }

    @Override
    public long tinhTienGiam(long giaBan) {
        return 0;
    }

    public int getSoLuongMuaToiThieu() {
        return soLuongMuaToiThieu;
    }

    public void setSoLuongMuaToiThieu(int soLuongMuaToiThieu) {
        if (soLuongMuaToiThieu <= 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng mua tối thiểu phải lớn hơn 0! Nhận: " + soLuongMuaToiThieu);
        }
        this.soLuongMuaToiThieu = soLuongMuaToiThieu;
        capNhatMoTaLoaiKM();
    }

    public int getSoLuongTang() {
        return soLuongTang;
    }

    public void setSoLuongTang(int soLuongTang) {
        if (soLuongTang <= 0) {
            throw new IllegalArgumentException("[Lỗi] Số lượng tặng phải lớn hơn 0! Nhận: " + soLuongTang);
        }
        this.soLuongTang = soLuongTang;
        capNhatMoTaLoaiKM();
    }

    @Override
    public Khuyenmai taoKhuyenMaiRong() {
        return new KhuyenmaiMuatang();
    }

    @Override
    public String toDongFile() {
        // Định dạng: MUATANG;maKM;loaiKM;ngayBatDau;ngayKetThuc;mucChiToiThieu;soLuongMuaToiThieu;soLuongTang
        return "MUATANG;" + super.toDongFile() + String.format(";%d;%d", soLuongMuaToiThieu, soLuongTang);
    }

    @Override
    public String toString() {
        return super.toString()
                + " | Mua tối thiểu: " + soLuongMuaToiThieu + " tặng " + soLuongTang;
    }
}