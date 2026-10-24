import java.util.ArrayList;
import java.util.List;

public class Khuyenmai {

    public abstract class KhuyenMai {
        protected String maKM;
        protected String tenKM;
        protected double mucGiam;

        protected List<String> dieuKienApDung = new ArrayList<>();

        public KhuyenMai(String maKM, String tenKM, double mucGiam) {
            this.maKM = maKM;
            this.tenKM = tenKM;
            this.mucGiam = mucGiam;
        }

        public abstract double tinhTienGiam(double tongTien);
    }

    // 1. Khuy3n mai theo phan tram
    class KhuyenMaiTheoPhanTram extends KhuyenMai {
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
    class KhuyenMaiGiamTructiep extends KhuyenMai {
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
    class KhuyenMaiMuaTang extends KhuyenMai {
        public KhuyenMaiMuaTang(String maKM, String tenKM) {
            super(maKM, tenKM, 20000); // mac dinh tang tuong duong
        }

        @Override
        public double tinhTienGiam(double tongTien) {
            return 20000; // Tru truc tiep qua tang vao HD
        }
    }
}
