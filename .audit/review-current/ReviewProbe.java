import person.*;
import sanpham.*;
import hoadon.*;
import khuyenmai.*;
import java.time.*;
import java.util.*;
import java.nio.file.*;
import java.io.*;

public class ReviewProbe {
 static PrintStream out=System.out; static int count;
 interface Case { String run() throws Exception; }
 static void test(String name, Case c) { count++; try {out.println(name+": "+c.run());} catch(Throwable e) {out.println(name+": EXCEPTION "+e);} }
 static NhanVien nv(){return new NhanVien("NV901","Audit","audit","1234","0912345678",LocalDate.now(),false);}
 static ThucAn sp(){return new ThucAn("TA901","Audit",100,200,100,"box",LocalDate.now().minusDays(10),LocalDate.now().plusDays(10),0,"a","b","c",false,"Đã duyệt",false);}
 static Hoadonban sale(){return new Hoadonban("HDB901",nv(),LocalDate.now(),"DangXuLy","","KHACH_LE");}
 static CTHDBan ct(int n,double price){return new CTHDBan("HDB901","TA901","Audit",n,price,0,0,0,"KHONG");}
 static String capture(Runnable r){PrintStream old=System.out; ByteArrayOutputStream b=new ByteArrayOutputStream(); try{System.setOut(new PrintStream(b));r.run();}finally{System.setOut(old);}return b.toString();}
 public static void main(String[] args)throws Exception{
 System.setOut(new PrintStream(OutputStream.nullOutputStream()));
 test("01 expired predicate",()->{ThucAn p=sp();p.setHSD(LocalDate.now().minusDays(10),LocalDate.now().minusDays(1));return "valid="+p.kiemTraHanSuDung()+", stock="+p.getSoLuongTon();});
 test("02 normal checkout twice",()->{DSSanPham d=new DSSanPham();ThucAn p=sp();d.themSP(p);Hoadonban h=sale();h.themCTHD(ct(2,200),d,new DSKhuyenMai(),new DSSanPhamKhuyenMai());return "first="+h.thanhToanHoaDon(new Scanner("1\n1000\n"),nv(),new DsKhachHang(),d)+", second="+h.thanhToanHoaDon(new Scanner(""),nv(),new DsKhachHang(),d)+", stock="+p.getSoLuongTon();});
 test("03 deleted unapproved product",()->{DSSanPham d=new DSSanPham();ThucAn p=sp();p.setDaXoa(true);p.setTrangThaiDuyet("Chờ duyệt");d.themSP(p);Hoadonban h=sale();return "added="+h.themCTHD(ct(1,200),d,new DSKhuyenMai(),new DSSanPhamKhuyenMai())+", paid="+h.thanhToanHoaDon(new Scanner("1\n200\n"),nv(),new DsKhachHang(),d);});
 test("04 canceled sale",()->{DSSanPham d=new DSSanPham();d.themSP(sp());Hoadonban h=sale();h.themCTHD(ct(1,200),d,null,null);h.huyHoaDon();return "paid="+h.thanhToanHoaDon(new Scanner("1\n200\n"),nv(),new DsKhachHang(),d)+", status="+h.getTrangThai();});
 test("05 point conversion overflow",()->{DSSanPham d=new DSSanPham();d.themSP(sp());DsKhachHang k=new DsKhachHang();k.them(new Khachhang("KH901","Audit","0912345678","A",3000000,false));Hoadonban h=sale();h.setMaKH("KH901");h.themCTHD(ct(1,3000000000d),d,null,null);h.thanhToanHoaDon(new Scanner("1\n3000000\n1\n5000000000\n"),nv(),k,d);return "discount="+h.getTienDungDiem()+", payable="+h.tinhThanhTien();});
 test("06 point addition overflow",()->{Khachhang k=new Khachhang();k.setDiemTichLuy(Integer.MAX_VALUE);k.congDiem(1);return "points="+k.getDiemTichLuy();});
 test("07 stock addition overflow",()->{ThucAn p=sp();p.setSoLuongTon(Integer.MAX_VALUE);p.congTonKho(1);return "stock="+p.getSoLuongTon();});
 test("08 constructor bypass",()->{NhanVien n=new NhanVien(null,null,"a b","x","bad",LocalDate.now(),false);return "id="+n.getMaND()+", password="+n.getMatKhau()+", phone="+n.getSoDienThoai();});
 test("09 null password login",()->{Files.writeString(Path.of("data/null-user.txt"),"");DSNhanVien ds=new DSNhanVien();ds.them(new NhanVien("NV902","A","nulluser",null,"0912345678",LocalDate.now(),false));return "equals="+ds.timKiemTheoTaiKhoan("nulluser").getMatKhau().equals("1234");});
 test("10 delimiter account",()->{DSNhanVien ds=new DSNhanVien();NhanVien n=nv();n.setTaiKhoan("audit;injected");ds.them(n);ds.ghiFile("data/users-roundtrip.txt");DSNhanVien x=new DSNhanVien();return "reload="+x.docFile("data/users-roundtrip.txt")+", count="+x.getSoLuong();});
 test("11 stock expiry rewritten on import",()->{ThucAn p=sp();DSSanPham d=new DSSanPham();d.themSP(p);HoadonNhap h=new HoadonNhap("HDN901",nv(),LocalDate.now(),"DangXuLy","","NCC901");LocalDate expected=LocalDate.now().plusDays(90);h.themCTHD(new CTHDonNhap("HDN901","TA901","Audit",2,100,0,expected));return "approved="+h.duyetNhapKho(new Scanner(""),d,new QuanLyCuaHang())+", detailExpiry="+expected+", actualExpiry="+p.getHsd();});
 test("12 NaN import partial mutation",()->{ThucAn p=sp();DSSanPham d=new DSSanPham();d.themSP(p);HoadonNhap h=new HoadonNhap("HDN901",nv(),LocalDate.now(),"DangXuLy","","NCC901");h.themCTHD(CTHDonNhap.tuDongFile("HDN901;TA901;Audit;2;NaN;0;01/01/2099"));boolean ok=h.duyetNhapKho(new Scanner(""),d,new QuanLyCuaHang());return "approved="+ok+", price="+p.getDonGiaNhap();});
 test("13 double overflow",()->"total="+ct(2,Double.MAX_VALUE).tinhThanhTien());
 test("14 payment persistence",()->{Hoadonban h=sale();h.themCTHDTuFile(ct(1,2000));h.khoiPhucThanhToanTuFile(1000,1000,"TienMat");h.xacNhanXuLy();DSHoadon d=new DSHoadon();d.themHD(h);d.ghiFile("data/roundtrip.txt");DSHoadon x=new DSHoadon();x.docFile("data/roundtrip.txt",null);Hoadonban r=(Hoadonban)x.timKiem("HDB901");return "discount="+r.getTienDungDiem()+", method="+r.getPhuongThucThanhToan()+", total="+r.tinhThanhTien();});
 test("15 corrupt invoice overwritten",()->{Path p=Path.of("data/corrupt.txt");Files.writeString(p,"BROKEN;RECORD\n");DSHoadon d=new DSHoadon();d.docFile(p.toString(),null);d.ghiFile(p.toString());return "remainingBytes="+Files.size(p);});
 test("16 corrupt customer partial load",()->{Path p=Path.of("data/corrupt-customer.txt");Files.writeString(p,"KH901;A;0912345678;A;1;false\nKH902;B;0912345679;B;bad;false\nKH903;C;0912345680;C;1;false\n");DsKhachHang d=new DsKhachHang();d.docFile(p.toString());d.ghiFile(p.toString());return "customers="+d.getSoLuong()+", remainingLines="+Files.readAllLines(p).size();});
 test("17 report ignores date range",()->{Hoadonban h=new Hoadonban("HDB901",nv(),LocalDate.now().minusYears(1),"DaXuLyThanhCong","","KHACH_LE");h.themCTHDTuFile(ct(1,200));DSHoadon d=new DSHoadon();d.themHD(h);String s=capture(()->new QuanLyCuaHang().thongKeSanPhamBanDuoc(d,new DSSanPham(),LocalDate.now(),LocalDate.now()));return "includesOldProduct="+s.contains("TA901");});
 test("18 import locale roundtrip",()->{Locale old=Locale.getDefault();try{Locale.setDefault(Locale.GERMANY);CTHDonNhap c=new CTHDonNhap("HDN901","TA901","A",1,100,5,LocalDate.now().plusDays(10));return "readable="+(CTHDonNhap.tuDongFile(c.toDongFile())!=null);}finally{Locale.setDefault(old);}});
 test("19 canceled import approval",()->{DSSanPham d=new DSSanPham();ThucAn p=sp();d.themSP(p);HoadonNhap h=new HoadonNhap("HDN901",nv(),LocalDate.now(),"DangXuLy","","NCC901");h.themCTHD(new CTHDonNhap("HDN901","TA901","A",1,100,0,LocalDate.now().plusDays(10)));h.huyHoaDon();return "approved="+h.duyetNhapKho(new Scanner(""),d,new QuanLyCuaHang())+", status="+h.getTrangThai()+", stock="+p.getSoLuongTon();});
 test("20 deleted customer uses points",()->{DSSanPham d=new DSSanPham();d.themSP(sp());DsKhachHang k=new DsKhachHang();k.them(new Khachhang("KH901","A","0912345678","A",1,true));Hoadonban h=sale();h.setMaKH("KH901");h.themCTHD(ct(1,2000),d,null,null);boolean ok=h.thanhToanHoaDon(new Scanner("1\n1\n1\n1000\n"),nv(),k,d);return "paid="+ok+", discount="+h.getTienDungDiem();});
 test("21 zero payable checkout",()->{DSSanPham d=new DSSanPham();d.themSP(sp());DsKhachHang k=new DsKhachHang();k.them(new Khachhang("KH901","A","0912345678","A",2,false));Hoadonban h=sale();h.setMaKH("KH901");h.themCTHD(ct(1,2000),d,null,null);return "paid="+h.thanhToanHoaDon(new Scanner("1\n2\n1\n0\n"),nv(),k,d)+", total="+h.tinhThanhTien();});
 test("22 date overflow leaves partial stock",()->{ThucAn p=sp();DSSanPham d=new DSSanPham();d.themSP(p);HoadonNhap h=new HoadonNhap("HDN901",nv(),LocalDate.MAX,"DangXuLy","","NCC901");h.themCTHD(new CTHDonNhap("HDN901","TA901","A",1,100,0,LocalDate.now().plusDays(10)));try{h.duyetNhapKho(new Scanner(""),d,new QuanLyCuaHang());return "no exception";}catch(DateTimeException e){return "exception="+e.getClass().getSimpleName()+", stock="+p.getSoLuongTon()+", status="+h.getTrangThai();}});
 test("23 menu invalid choices and return",()->{int menus=0;for(java.lang.reflect.Method m:giaodien.Giaodien.class.getMethods()){if(!m.getName().startsWith("menu")||!Arrays.equals(m.getParameterTypes(),new Class[]{Scanner.class}))continue;giaodien.Giaodien g=new giaodien.Giaodien();java.lang.reflect.Field f=giaodien.Giaodien.class.getDeclaredField("nguoiDangNhap");f.setAccessible(true);f.set(g,new Giamdoc());try{m.invoke(g,new Scanner("abc\n-1\n2147483648\n999\n"+(m.getName().equals("menuNhanVien")?"3":"0")+"\n"));menus++;}catch(java.lang.reflect.InvocationTargetException e){out.println("MENU_FAILURE "+m.getName()+": "+e.getCause());}}return "menusReturned="+menus;});
 out.println("CASES="+count);
 }
}
