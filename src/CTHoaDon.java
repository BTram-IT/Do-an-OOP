import java.util.ArrayList;
import java.util.Scanner;

public class CTHoaDon {
    private String MaSP;
    private String TenSP;
    private int Soluong;
    private double Giaban;
    private String MaHD;
    private String Mucgiamgia;
    private ArrayList<Khuyenmai> khuyenmai;

    Scanner sc = new Scanner(System.in);

    public String getMaSP() {
        return MaSP;
    }

    public void setMaSP(String maSP) {
        MaSP = maSP;
    }

    public String getTenSP() {
        return TenSP;
    }

    public void setTenSP(String tenSP) {
        TenSP = tenSP;
    }

    public int getSoluong() {
        return Soluong;
    }

    public void setSoluong(int soluong) {
        Soluong = soluong;
    }

    public double getGiaban() {
        return Giaban;
    }

    public void setGiaban(double giaban) {
        Giaban = giaban;
    }

    public ArrayList<Khuyenmai> getKhuyenmai() {
        return khuyenmai;
    }

    public void setKhuyenmai(ArrayList<Khuyenmai> khuyenmai) {
        this.khuyenmai = khuyenmai;
    }

    public String getMaHD() {
        return MaHD;
    }

    public void setMaHD(String maHD) {
        MaHD = maHD;
    }

    // Tiền hàng trước giảm
    public double tinhTienHang(){
        return Soluong * Giaban;
    }

    public double Apmagiamgia(){
        return 0;
    }

    // Chưa tính thuế
    public double TinhTienSaukhigiamgia() {
        return 0;
    }

    //tính tiền thuế
    public double TinhTienThue(DSSanPham sp) {
        return 0;
    }


    public double ThanhTien() {
        return 0;
    }


}