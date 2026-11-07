import java.util.Scanner;

public class PhuongTien {
    private String maXe;
    private String loaiXe;
    private String bienSo;
    private double taiTrongKg;

    public PhuongTien() {
        this.maXe = "";
        this.loaiXe = "";
        this.bienSo = "";
        this.taiTrongKg = 0;
    }

    public PhuongTien(String maXe, String loaiXe, String bienSo, double taiTrongKg) {
        this.maXe = maXe != null ? maXe.trim().toUpperCase() : "";
        this.loaiXe = loaiXe != null ? loaiXe.trim() : "";
        this.bienSo = bienSo != null ? bienSo.trim().toUpperCase() : "";
        this.taiTrongKg = Math.max(0, taiTrongKg);
    }

    public String getMaXe() { return maXe; }
    public void setMaXe(String maXe) { this.maXe = maXe != null ? maXe.trim().toUpperCase() : ""; }
    public String getLoaiXe() { return loaiXe; }
    public void setLoaiXe(String loaiXe) { this.loaiXe = loaiXe; }
    public String getBienSo() { return bienSo; }
    public void setBienSo(String bienSo) { this.bienSo = bienSo != null ? bienSo.trim().toUpperCase() : ""; }
    public double getTaiTrongKg() { return taiTrongKg; }
    public void setTaiTrongKg(double taiTrongKg) { this.taiTrongKg = Math.max(0, taiTrongKg); }

    public void nhap(Scanner sc) {
        this.loaiXe = Ktradulieu.nhapChuoi(sc, "Loại xe (xe máy, tải, van...): ", ".+", "Loại xe không được để trống!");
        this.bienSo = Ktradulieu.nhapChuoi(sc, "Biển số xe: ", "[0-9]{2}[A-Z0-9]-[0-9]{4,5}", "Biển số không hợp lệ (ví dụ: 59X1-12345)!");
        this.taiTrongKg = Ktradulieu.nhapSoThuc(sc, "Tải trọng (kg): ");
    }

    @Override
    public String toString() {
        return "Xe [" + maXe + "] " + loaiXe + " - Biển số: " + bienSo + " (tải: " + taiTrongKg + " kg)";
    }
}