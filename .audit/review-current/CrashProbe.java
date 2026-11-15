import hoadon.*;import person.*;import sanpham.*;import khuyenmai.*;import thanhtoan.*;import java.util.*;import java.time.*;import java.io.*;
public class CrashProbe {
 interface Run{void run();} static PrintStream report=System.out;
 static void test(String n,Run r){try{r.run();report.println(n+": NO EXCEPTION");}catch(Throwable e){report.println(n+": "+e);}}
 static Hoadonban h(){return new Hoadonban("HDB901",null,LocalDate.now(),"DangXuLy","","KHACH_LE");}
 static CTHDBan c(){return new CTHDBan("HDB901","TA901","A",1,100,0,0,0,"KHONG");}
 public static void main(String[] x){System.setOut(new PrintStream(OutputStream.nullOutputStream()));
 test("null Scanner delete",()->{Hoadonban h=h();h.themCTHDTuFile(c());h.xoaSanPham(null);});
 test("null product list edit",()->{Hoadonban h=h();h.themCTHDTuFile(c());h.suaSoLuongSanPham(new Scanner("TA901\n"),null,null,null);});
 test("EOF delete",()->{Hoadonban h=h();h.themCTHDTuFile(c());h.xoaSanPham(new Scanner(""));});
 test("closed Scanner",()->{Scanner s=new Scanner("0");s.close();h().tiepNhanKhachHang(s,new DsKhachHang());});
 test("null bank restore fields",()->{Hoadonban h=h();h.khoiPhucThanhToanTuFile(0,100,"ChuyenKhoan");h.khoiPhucChiTietThanhToanTuFile(0,null,"A","123456","B");});
 test("invalid file detail then display",()->{Hoadonban h=h();try{h.themCTHDTuFile(new CTHDBan());}catch(IllegalArgumentException e){report.println("detail insertion rejected: "+e.getClass().getSimpleName());}h.inHoaDon();});
 test("invalid cashier id checkout",()->{DSSanPham d=new DSSanPham();ThucAn p=new ThucAn();p.setMaSP("TA901");p.setTenSP("A");p.setDonGiaBan(100);p.setSoLuongTon(10);p.setHSD(LocalDate.now(),LocalDate.now().plusDays(10));d.themSP(p);Hoadonban h=h();h.themCTHD(c(),d,null,null);h.thanhToanHoaDon(new Scanner("1\n100\n"),new NhanVien(),null,d);});
 test("gift overflow",()->{DSSanPham d=new DSSanPham();ThucAn p=new ThucAn();p.setMaSP("TA901");p.setTenSP("A");p.setSoLuongTon(10);p.setHSD(LocalDate.now(),LocalDate.now().plusDays(10));d.themSP(p);DSKhuyenMai km=new DSKhuyenMai();km.themKhuyenMai(new KhuyenmaiMuatang("KM901","MuaTang",LocalDate.now(),LocalDate.now(),1,Integer.MAX_VALUE));DSSanPhamKhuyenMai link=new DSSanPhamKhuyenMai();link.them(new SPKhMai("TA901","KM901"));Hoadonban h=h();CTHDBan c=c();c.setSoLuongBan(2);h.themCTHD(c,d,km,link);});
 }
}
