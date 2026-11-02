import java.util.ArrayList;
import java.util.List;

public abstract class Khuyenmai {
    protected String maKM;
    protected String tenKM;
    protected double mucGiam; 

    protected List<String> dieuKienApDung = new ArrayList<>();

    public Khuyenmai(String maKM, String tenKM, double mucGiam) {
        this.maKM = maKM;
        this.tenKM = tenKM;
        this.mucGiam = mucGiam;
    }

    public abstract double tinhTienGiam(double tongTien);


    public double tinhTienGiamHopLe(double tongTien) {
        if (tongTien <= 0) {
            return 0;
        }

        double tienGiam = tinhTienGiam(tongTien);

        if (tienGiam < 0) {
            tienGiam = 0;
        }

        if (tienGiam > tongTien) {
            tienGiam = tongTien;
        }

        return tienGiam;
    }

    public void themDieuKien(String dieuKien) {
        if (dieuKien != null && !dieuKien.trim().isEmpty()) {
            dieuKienApDung.add(dieuKien);
        }
    }

    public void hienThiThongTin() {
        System.out.println("Ma khuyen mai: " + maKM);
        System.out.println("Ten khuyen mai: " + tenKM);
        System.out.println("Muc giam: " + mucGiam);

        System.out.println("Dieu kien ap dung:");
        if (dieuKienApDung.isEmpty()) {
            System.out.println("Chua co dieu kien.");
        } else {
            for (String dieuKien : dieuKienApDung) {
                System.out.println("- " + dieuKien);
            }
        }
    }

    public String getMaKM() {
        return maKM;
    }

    public String getTenKM() {
        return tenKM;
    }

    public double getMucGiam() {
        return mucGiam;
    }
}

// 1. Khuyen mai theo phan tram
class KhuyenMaiTheoPhanTram extends Khuyenmai {
    private double phanTram; 

    public KhuyenMaiTheoPhanTram(String maKM, String tenKM, double phanTram) {
        super(maKM, tenKM, phanTram);
        this.phanTram = phanTram;
    }

    @Override
    public double tinhTienGiam(double tongTien) {
        return tongTien * (phanTram / 100.0);
    }
}

// 2. Khuyen mai giam truc tiep so tien
class KhuyenMaiGiamTructiep extends Khuyenmai {
    private double soTienGiam; 

    public KhuyenMaiGiamTructiep(String maKM, String tenKM, double soTienGiam) {
        super(maKM, tenKM, soTienGiam);
        this.soTienGiam = soTienGiam;
    }

    @Override
    public double tinhTienGiam(double tongTien) {
        return Math.min(tongTien, soTienGiam); // Ko giam qua tien goc
    }
}

// 3. Khuyen mai mua tang (tang gia tri co dinh or voucher)
class KhuyenMaiMuaTang extends Khuyenmai {
    public KhuyenMaiMuaTang(String maKM, String tenKM) {
        super(maKM, tenKM, 20000); // mac dinh tang tuong duong
    }

    @Override
    public double tinhTienGiam(double tongTien) {
        return 20000; //Tru truc tiep vao HD
    }
}
