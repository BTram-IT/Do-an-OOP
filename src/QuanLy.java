import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class QuanLyCuaHang extends Nhanvien {

    public QuanLyCuaHang() {
        this("", "", "", "", "", 0);
    }

    public QuanLyCuaHang(String ma, String ten, String sdt, String tk, String mk, double luong) {
        super(ma, ten, sdt, tk, mk, luong);
    }

    @Override
    public String getVaiTro() {
        return "QuanLyCuaHang";
    }

    @Override
    public String getBoPhan() {
        return "QuanLyCuaHang";
    }

    public boolean themNhanVien(DSNhanVien ds, NhanVien nv) {
        if (nv == null || ds.tim(nv.getMa()) != null) {
            return false;
        }
//        ds.them(nv);
        return true;
    }

//    public boolean xoaNhanVien(DSNhanVien ds, String ma) {
//        if (ma.equalsIgnoreCase(getMa())) {
//            return false;
//        }
//        return ds.xoa(ma);
//    }

    public ChamCong chamCongVao(List<ChamCong> ds, NhanVien nv, CaLam ca, LocalDate ngay, LocalTime gio) {
        for (ChamCong cc : ds) {
            boolean trung = cc.getNhanVien() == nv
                    && cc.getNgay().equals(ngay)
                    && cc.getCaLam() == ca;
            if (trung) {
                return null;
            }
        }
        ChamCong moi = new ChamCong(nv, ca, ngay, gio);
        ds.add(moi);
        return moi;
    }

    public boolean chamCongRa(ChamCong cc, LocalTime gio) {
        if (cc == null || cc.daRa() || gio.isBefore(cc.getGioVao())) {
            return false;
        }
        cc.chamRa(gio);
        return true;
    }

    public BaoCaoTaiChinh lapBaoCaoTaiChinh(List<PhieuThuChi> ds, Thue vat, int thang, int nam) {
        double banHang = 0;
        double thuKhac = 0;
        double chiNhap = 0;
        double chiLuong = 0;
        double chiKhac = 0;

        for (PhieuThuChi p : ds) {
            if (thang != 0 && (p.getNgayLap().getMonthValue() != thang || p.getNgayLap().getYear() != nam)) {
                continue;
            }
            if (p instanceof PhieuThu) {
                PhieuThu thu = (PhieuThu) p;
                if (thu.getNguon().equals(PhieuThu.BAN_HANG)) {
                    banHang += thu.getSoTien();
                } else {
                    thuKhac += thu.getSoTien();
                }
            } else if (p instanceof PhieuChi) {
                PhieuChi chi = (PhieuChi) p;
                if (chi.getLoaiChi().equals(PhieuChi.NHAP_HANG)) {
                    chiNhap += chi.getSoTien();
                } else if (chi.getLoaiChi().equals(PhieuChi.LUONG)) {
                    chiLuong += chi.getSoTien();
                } else {
                    chiKhac += chi.getSoTien();
                }
            }
        }
        double thue = vat == null ? 0 : vat.tinhThueDaGom(banHang);
        String ky = (thang == 0) ? "Toan bo thoi gian" : thang + "/" + nam;
        return new BaoCaoTaiChinh(ky, banHang, thuKhac, chiNhap, chiLuong, chiKhac, thue);
    }
}

