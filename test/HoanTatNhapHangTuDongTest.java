import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;
import person.NhanVien;
import sanpham.*;
import doitac.*;
import hoadon.*;

public class HoanTatNhapHangTuDongTest {
    static class Kho extends DSSanPham { @Override public void ghiFile(String p) { } }
    static class Ncc extends DSNhaCungCap { @Override public void ghiFile(String p) { } }
    static class Phieu extends DSHoadon {
        int saved;
        @Override protected void ghiFile(String p, boolean show) { saved++; }
    }
    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        NhanVien nv = new NhanVien("NV001", "Nhân viên", "nv", "123456", "0912345678", now, false);
        Kho kho = new Kho(); Ncc ncc = new Ncc(); Phieu ds = new Phieu();
        for (int i=1; i<=2; i++) {
            String ma = "NCC00"+i;
            ncc.them(new NhaCungCap(ma,"NCC "+i,"0912345678","TP HCM","ncc@example.com",false));
            kho.themSP(new ThucAn("TA00"+i,"Bánh "+i,5000,10000,10,"gói",now,now.plusDays(90),0,
                    "Bột","Khô","Việt Nam",false,ma,false));
        }
        ds.themHD(new HoadonNhap("HDN009",nv,now,"DangXuLy","","NCC001"));
        Scanner keys = new Scanner("3\n1\nTA001\n2\n\n1\nTA002\n3\n\n0\n0\nCON_LAI\n");
        ByteArrayOutputStream log = new ByteArrayOutputStream(); PrintStream original=System.out;
        int count;
        try (PrintStream capture = new PrintStream(log,true,StandardCharsets.UTF_8)) {
            System.setOut(capture);
            count=ds.nhapThemHang(keys,nv,ncc,kho);
        } finally { System.setOut(original); }
        if(count!=2 || ds.saved!=2 || !keys.nextLine().equals("CON_LAI")) throw new AssertionError("Đọc thừa bàn phím hoặc thiếu phiếu");
        for(int i=1;i<=2;i++) {
            HoadonNhap hd=(HoadonNhap)ds.get(i);
            if(!hd.getMaHD().equals(i==1?"HDN010":"HDN011") || hd.isDaThanhToan() || hd.isDaXuLyThanhCong()) throw new AssertionError("Sai mã/trạng thái");
            for(CTHDonNhap ct:hd.getDsChiTiet()) if(!ct.getMaHD().equals(hd.getMaHD())) throw new AssertionError("Sai mã chi tiết");
            if(kho.get(i-1).getSoLuongTon()!=10) throw new AssertionError("Tự cộng kho");
        }
        String output=log.toString(StandardCharsets.UTF_8);
        if (output.split("===== HÓA ĐƠN NHẬP HÀNG =====", -1).length - 1 != 2
                || !output.contains("HDN010") || !output.contains("HDN011")
                || !output.contains("Chờ duyệt nhập kho") || !output.contains("Chưa thanh toán")
                || output.indexOf("===== HÓA ĐƠN NHẬP HÀNG =====") < output.indexOf("Đã hoàn tất hóa đơn nhập và chờ duyệt."))
            throw new AssertionError("Chưa in đúng các hóa đơn chờ duyệt sau khi hoàn tất");
        if(!output.contains("Đã hoàn tất hóa đơn nhập và chờ duyệt.") || output.contains("Nhập mã hóa đơn")
                || output.contains("Chọn thao tác") || output.contains("Chọn thanh toán") || output.contains("Ghi chú (")) throw new AssertionError(output);
        System.out.println("PASS: hoàn tất tự sinh hai mã theo NCC, không hỏi thêm, lưu phiếu chờ duyệt và giữ nguyên tồn kho.");
    }
}
