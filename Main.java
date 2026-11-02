import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int luaChonChinh = 0;

        ThuNgan thuNgan = new ThuNgan("TN01", "Nguyen Van A", "Nhan vien chinh thuc");
        DonHang donHang = new DonHang("DH001", "24/10/2026");
        HoaDon hoaDon = new HoaDon("HD001", 0.0, "Chua thanh toan");

        do {
            System.out.println("\n============= QUAN LY CUA HANG THUC AN NHANH & GIAI KHAT =============");
            System.out.println("1. Chuc nang Thu Ngan");
            System.out.println("2. Chuc nang Quan ly kho");
            System.out.println("3. Chuc nang Ke toan");
            System.out.println("4. Chuc nang Quan ly cua hang (Tong the)");
            System.out.println("5. Chuc nang Quan ly khach hang va nhan vien");
            System.out.println("0. Thoat chuong trinh");
            System.out.print("==> Nhap lua chon cua ban: ");
            
            if (scanner.hasNextInt()) {
                luaChonChinh = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Vui long nhap mot so hop le!");
                scanner.next();
                continue;
            }

            switch (luaChonChinh) {
                case 1:
                    int luaChonThuNgan = 0;
                    do {
                        System.out.println("\n--- MENU THU NGAN ---");
                        System.out.println("1. Quan ly Don Hang");
                        System.out.println("2. Chi Tiet Don Hang");
                        System.out.println("3. Quan ly Hoa Don");
                        System.out.println("4. Chi Tiet Hoa Don");
                        System.out.println("5. Khuyen Mai");
                        System.out.println("6. Thong tin Thu Ngan va Ca lam");
                        System.out.println("0. Quay lai menu chinh");
                        System.out.print("Chon chuc nang (0-6): ");
                        luaChonThuNgan = scanner.nextInt();
                        scanner.nextLine();

                        switch (luaChonThuNgan) {
                            case 1:
                                System.out.println("\n--- QUAN LY DON HANG ---");
                                System.out.println("1. Xem danh sach san pham");
                                System.out.println("2. Them san pham");
                                System.out.println("3. Xoa san pham");
                                System.out.println("4. Sua san pham");
                                System.out.print("Chon thao tac (1-4): ");
                                int thaoTacDH = scanner.nextInt();
                                scanner.nextLine();

                                if (thaoTacDH == 1) {
                                    donHang.hienThiDanhSachSanPham();
                                } else if (thaoTacDH == 2) {
                                    System.out.print("Nhap ten san pham muon them: ");
                                    String tenSP = scanner.nextLine();
                                    donHang.themSanPham(tenSP);
                                } else if (thaoTacDH == 3) {
                                    donHang.hienThiDanhSachSanPham();
                                    System.out.print("Nhap STT san pham muon xoa: ");
                                    int sttXoa = scanner.nextInt();
                                    donHang.xoaSanPham(sttXoa - 1);
                                } else if (thaoTacDH == 4) {
                                    donHang.hienThiDanhSachSanPham();
                                    System.out.print("Nhap STT san pham muon sua: ");
                                    int sttSua = scanner.nextInt();
                                    scanner.nextLine();
                                    System.out.print("Nhap ten san pham moi: ");
                                    String tenMoi = scanner.nextLine();
                                   
                                }
                                break;
                            
                            case 2:
                                System.out.println("\n--- CHI TIET DON HANG ---");
                                System.out.println("1. Xem thong tin chi tiet don hang");
                                System.out.println("2. Luu lich su tinh tien");
                                System.out.println("3. Xem lich su tinh tien");
                                System.out.print("Chon thao tac (1-3): ");

                                int thaoTacCTDH = scanner.nextInt();
    
                                if (thaoTacCTDH == 1) {
                                    // ChiTietDonHang can duoc tao tu mot doi tuong SanPham
                                    System.out.println("Can chon SanPham tu danh sach san pham cua nhom.");
                                    System.out.println("Sau khi co doi tuong SanPham, tao ChiTietDonHang roi hien thi.");
                                } else if (thaoTacCTDH == 2) {
                                    System.out.println("Can tao ChiTietDonHang truoc khi luu lich su.");
                                    // Khi da co chiTietDH:
                                    // chiTietDH.luuLichSuTinhTien();
                                    // System.out.println("Da luu lich su tinh tien.");
                                } else if (thaoTacCTDH == 3) {
                                    System.out.println("Can tao ChiTietDonHang truoc khi xem lich su.");
                                    // Khi da co chiTietDH:
                                    // chiTietDH.hienThiLichSuTinhTien();
                                } else {
                                    System.out.println("Lua chon khong hop le!");
                                }
                                break;

                            case 3:
                                System.out.println("\n--- QUAN LY HOA DON ---");
                                System.out.println("1. Xem thong tin hoa don");
                                System.out.println("2. Luu hoa don nhap");
                                System.out.println("3. Xoa hoa don nhap");
                                System.out.print("Chon thao tac (1-3): ");
                                int thaoTacHD = scanner.nextInt();
                                scanner.nextLine();

                                if (thaoTacHD == 1) {
                                    hoaDon.hienThiHoaDon();
                                } 
                                break;
                            case 4:
                                System.out.println("\n--- CHI TIET HOA DON ---");
                                CTHoaDon chiTietHD = new CTHoaDon("CTHD01", "Ghi chu thanh toan");
                                chiTietHD.hienThiHinhThucThanhToan();
                                chiTietHD.hienThiThongTinChiTiet();
                                break;
                            case 5:
                                System.out.println("\n--- AP DUNG KHUYEN MAI ---");
                                System.out.println("1. Giam theo phan tram");
                                System.out.println("2. Giam truc tiep");
                                System.out.println("3. Mua tang qua");
                                System.out.print("Chon loai khuyen mai (1-3): ");
                                int loaiKM = scanner.nextInt();
                                scanner.nextLine();
                                
                                Khuyenmai km = null;
                                if (loaiKM == 1) {
                                    km = new KhuyenMaiTheoPhanTram("KM01", "Giam 10%", 10.0);
                                } else if (loaiKM == 2) {
                                    km = new KhuyenMaiGiamTructiep("KM02", "Giam 20k", 20000);
                                } else {
                                    km = new KhuyenMaiMuaTang("KM03", "Tang qua 20k");
                                }
                                double tienGiam = km.tinhTienGiam(100000);
                                System.out.println("-> So tien duoc giam la: " + tienGiam);
                                break;
                            case 6:
                                System.out.println("\n--- THONG TIN THU NGAN & CA LAM ---");
                                thuNgan.hienThiThongTin();
                                thuNgan.hienThiCaLamViec();
                                break;
                            case 0:
                                System.out.println("Quay lai menu chinh.");
                                break;
                            default:
                                System.out.println("Lua chon khong hop le!");
                        }
                    } while (luaChonThuNgan != 0);
                    break;

                case 2:
                    System.out.println("\n--- MENU QUAN LY KHO ---");
                    System.out.println("1. San Pham");
                    System.out.println("2. Danh Sach San Pham");
                    System.out.println("3. Phieu Nhap");
                    System.out.println("4. Chi Tiet Phieu Nhap");
                    System.out.println("5. Quan Ly Kho");
                    break;

                case 3:
                    System.out.println("\n--- MENU KE TOAN ---");
                    System.out.println("1. Thue");
                    System.out.println("2. Phieu Thu Chi");
                    System.out.println("3. Phieu Thu");
                    System.out.println("4. Phieu Chi");
                    System.out.println("5. Bang Luong");
                    System.out.println("6. Ke Toan");
                    break;

                case 4:
                    System.out.println("\n--- MENU QUAN LY CUA HANG ---");
                    System.out.println("1. Thanh Toan");
                    System.out.println("2. Doi Tac");
                    System.out.println("3. Nha Cung Cap");
                    System.out.println("4. Don Vi Van Chuyen");
                    System.out.println("5. Quan Ly Cua Hang");
                    break;

                case 5:
                    System.out.println("\n--- MENU KHACH HANG VA NHAN VIEN ---");
                    System.out.println("1. Person");
                    System.out.println("2. Khach Hang");
                    System.out.println("3. Danh Sach Khach Hang");
                    System.out.println("4. Nhan Vien");
                    System.out.println("5. Danh Sach Nhan Vien");
                    System.out.println("6. Ca Lam");
                    System.out.println("7. Cham Cong");
                    System.out.println("8. Bao Cao Tai Chinh");
                    break;

                case 0:
                    System.out.println("\nCam on ban da su dung chuong trinh!");
                    break;

                default:
                    System.out.println("\nLua chon khong hop le!");
            }
        } while (luaChonChinh != 0);

        scanner.close();
    }
}