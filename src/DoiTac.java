import java.util.Scanner;

public abstract class DoiTac {
    private static final Scanner sc = TienIch.sc;


    protected String maDT;
    protected String tenDT;
    protected String diaChi;
    protected String email;
    protected String maSoThue;
    protected String loaiDoiTac;

    public DoiTac(String loaiDoiTac) {
        this("", "", "", "", "", loaiDoiTac);
    }

    public DoiTac(String maDT, String tenDT, String diaChi, String email, String maSoThue, String loaiDoiTac) {
        this.maDT = maDT;
        this.tenDT = tenDT;
        this.diaChi = diaChi;
        this.email = email;
        this.maSoThue = maSoThue;
        this.loaiDoiTac = loaiDoiTac;
    }

    public void nhap() {
        if (maDT == null || maDT.isEmpty()) {
            maDT = Ktradulieu.nhapChuoi(sc, "Ma doi tac: ", TienIch.REGEX_MA_DT, TienIch.LOI_MA_DT);
        }
        tenDT = Ktradulieu.nhapChuoi(sc, "Ten doi tac: ", TienIch.REGEX_VAN_BAN, TienIch.LOI_VAN_BAN);
        diaChi = TienIch.nhapChuoiTuyChon(sc, "Dia chi: ", TienIch.REGEX_DIA_CHI, TienIch.LOI_DIA_CHI);
        email = TienIch.nhapChuoiTuyChon(sc, "Email: ", TienIch.REGEX_EMAIL, TienIch.LOI_EMAIL);
        maSoThue = TienIch.nhapChuoiTuyChon(sc, "Ma so thue (de trong neu chua co): ", TienIch.REGEX_MA_SO_THUE, TienIch.LOI_MA_SO_THUE);
    }

    public String getMaDT() {
        return maDT;
    }

    public void setMaDT(String maDT) {
        this.maDT = maDT;
    }

    public String getTenDT() {
        return tenDT;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public String getEmail() {
        return email;
    }

    public String getMaSoThue() {
        return maSoThue;
    }

    public String getLoaiDoiTac() {
        return loaiDoiTac;
    }

    @Override
    public String toString() {
        return String.format("%-6s %-24s %-16s %-22s MST: %-12s %s",
                maDT, tenDT, loaiDoiTac, email, maSoThue, diaChi);
    }
}
