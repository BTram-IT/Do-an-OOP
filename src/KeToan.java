import org.w3c.dom.ls.LSOutput;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

public class KeToan extends Nhanvien implements Thongke{
    private DSNhanVien NhanVien;
    private DSSanPham DSSanPham;
    private DSHoadon HoaDon;


    public KeToan(DSNhanVien nhanVien, DSSanPham DSSanPham, DSHoadon hoadon) {
        NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        HoaDon = hoadon;
    }

    public KeToan(String maNV, LocalDate ngaybatdaulam, String Vitri, String Hoten, String SDT, String Email, String Diachi, DSNhanVien nhanVien, DSSanPham DSSanPham, DSHoadon hoadon) {
        super(maNV, ngaybatdaulam, Vitri, Hoten, SDT, Email, Diachi);
        NhanVien = nhanVien;
        this.DSSanPham = DSSanPham;
        HoaDon = hoadon;
    }

    public KeToan() {
        super();
        NhanVien = new DSNhanVien();
        DSSanPham = new DSSanPham();
        HoaDon = new DSHoadon();
    }

    public DSNhanVien getNhanVien() {
        return NhanVien;
    }

    public void setNhanVien(DSNhanVien nhanVien) {
        NhanVien = nhanVien;
    }

    public DSSanPham getDSSanPham() {
        return DSSanPham;
    }

    public void setDSSanPham(DSSanPham DSSanPham) {
        this.DSSanPham = DSSanPham;
    }

    public DSHoadon getHoadon() {
        return HoaDon;
    }

    public void setHoadon(DSHoadon hoadon) {
        HoaDon = hoadon;
    }


    private Scanner sc = new Scanner(System.in);


    @Override
    public double tinhDoanhThuTheoThang(int thang, int nam) {
        double doanthu=0;
        for(HoaDon hd : HoaDon.getDs()) {
            if(hd.getThoigianin()!=null
                    && hd.getThoigianin().getMonthValue()==thang
                    &&hd.getThoigianin().getYear()==nam) {
                doanthu+=hd.Tongtienhoadon();
            }
        }
        return doanthu;
    }

    @Override
    public double tinhDoanhThuTheoQuy(int quy, int nam) {
        if(quy<1 || quy >4) {
            return 0;
        }

        double doanhthu=0;
        int thangbatdau=(quy-1)*3+1;
        for(HoaDon hd : HoaDon.getDs()) {
            if(hd.getThoigianin()!=null) {
                int thang=hd.getThoigianin().getMonthValue();
                int namHD=hd.getThoigianin().getYear();

                if(namHD==nam
                && thang>=thangbatdau && thang<thangbatdau+3) {
                    doanhthu+=hd.Tongtienhoadon();
                }
            }
        }
        return doanhthu;
    }


    @Override
    public double tinhDoanhThuTheoNam(int nam) {
        double doanthu=0;
        for(HoaDon hd : HoaDon.getDs()) {
            if(hd.getThoigianin()!=null && hd.getThoigianin().getYear()==nam) {
                doanthu+=hd.Tongtienhoadon();
            }
        }
        return doanthu;
    }

    public double Thuetheothang(int thang,int nam){
        double tongthue=0;
        for(HoaDon hd : HoaDon.getDs()) {
            if(hd instanceof Hoadonban) {
                Hoadonban hb = (Hoadonban) hd;
                if(hd.getThoigianin().getMonthValue()==thang
                        && hd.getThoigianin().getYear()==nam) {
                    tongthue+= hb.Tongthuedonhang(DSSanPham);          }
            }
        }
        return tongthue;
    }
    public double ThuetheoNam(int nam){
        double tongthue=0;

        for(HoaDon hd : HoaDon.getDs()) {
            if(hd instanceof Hoadonban) {
                Hoadonban hb = (Hoadonban) hd;

                if(hd.getThoigianin().getYear()==nam) {
                    tongthue+=hb.Tongthuedonhang(DSSanPham);
                }
            }
        }
        return tongthue;
    }

    public double tinhTongGiavonHangdaban(int thang, int nam) {
        double tongvon=0;
        for( HoaDon hd : HoaDon.getDs()) {
            if(hd instanceof Hoadonban) {
                Hoadonban hb = (Hoadonban) hd;

                if(hd.getThoigianin().getMonthValue()==thang
                && hd.getThoigianin().getYear()==nam) {
                    for(CTHoaDon ct : hb.getCTHoaDon()) {
                        SanPham sp=DSSanPham.TimSPtheoMa(ct.getMaSP());

                        if(sp != null) {
                            tongvon+=sp.getGiaNhap()*ct.getSoluong();
                        }
                    }
                }
            }
        }
        return tongvon;
    }
    @Override
    public double tinhLoiNhuanTheoThang(int thang, int nam) {
        double loinhuan=tinhDoanhThuTheoThang(thang, nam)-tinhTongGiavonHangdaban(thang, nam);
        return loinhuan;
    }

    @Override
    public double tinhLoiNhuanTheoQuy(int quy, int nam) {
        return 0;
    }

    @Override
    public double tinhLoiNhuanTheoNam(int nam) {
        return 0;
    }

    @Override
    public double thongKeThueTheoThang(int thang, int nam) {
        return 0;
    }

    @Override
    public double thongKeThueTheoNam(int nam) {
        return 0;
    }

    @Override
    public void thongKeSanPhamBanDuoc() {

    }

    @Override
    public void thongKeSanPhamBanRa() {

    }

    @Override
    public void thongKeLuong() {

    }
}
