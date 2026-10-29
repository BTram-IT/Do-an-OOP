import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

// Lỗi dữ liệu sai -> IllegalArgumentException; thao tác không hợp lệ lúc này (hết tồn, danh sách đầy) -> IllegalStateException.
public interface IDanhSachSanPham {

    // ----- Thông tin chung -----
    int soLuong();

    int sucChuaToiDa();

    SanPham layTheoViTri(int viTri);

    // ----- Nhập, thêm, sửa, xóa -----
    void them(SanPham sp);

    // Thêm nhiều sản phẩm cùng lúc: được tất cả hoặc không được cái nào
    int themNhieu(SanPham[] dsMoi);

    void nhapN(int n, Scanner sc);

    // Sửa theo mã bằng thông tin trong thongTinMoi (không đổi được mã, loại, đơn vị tính; các lô hàng giữ nguyên)
    void sua(String ma, SanPham thongTinMoi);

    // Xóa theo mã, không xóa được nếu còn tồn kho
    void xoaTheoMa(String ma);

    // ----- Tìm kiếm -----
    boolean tonTaiMa(String ma);

    // Tìm chính xác theo mã, không thấy trả về null
    SanPham timTheoMa(String ma);

    ArrayList<SanPham> timTheoMaGanDung(String tuKhoa);

    ArrayList<SanPham> timTheoTenChinhXac(String ten);

    // Tên chứa từ khóa, không phân biệt hoa thường và bỏ qua dấu
    ArrayList<SanPham> timTheoTenGanDung(String tuKhoa);

    ArrayList<SanPham> timTheoLoai(String loai);

    ArrayList<SanPham> timTheoKhoangGia(long giaTu, long giaDen);

    // Tìm kết hợp nhiều tiêu chí cùng lúc. Tên hoặc loại để "" thì bỏ qua tiêu chí đó.
    ArrayList<SanPham> timNhieuTieuChi(String tuKhoaTen, String loai, long giaTu, long giaDen);

    // ----- Sắp xếp -----
    void sapXepTheoMa();

    void sapXepTheoTen();

    void sapXepTheoGiaBan(boolean tangDan);

    void sapXepTheoTon(boolean tangDan);

    // ----- Tồn kho theo lô -----
    // Tổng tồn của sản phẩm (cộng mọi lô, kể cả lô hết hạn)
    int layTon(String ma);

    // Số lượng còn hạn, bán được vào ngày đã cho
    int layTonBanDuoc(String ma, LocalDate ngay);

    // Còn đủ hàng CÒN HẠN để bán soLuong không (bản không có ngày dùng ngày hôm nay)
    boolean duTon(String ma, int soLuong);

    boolean duTon(String ma, int soLuong, LocalDate ngay);

    // Xuất theo FEFO, trả về các lô đã lấy. Không đủ hàng còn hạn thì lỗi và không đổi gì.
    ArrayList<LoXuat> xuatFEFO(String ma, int soLuong, LocalDate ngay);

    // Hoàn soLuong về đúng lô maLo của sản phẩm
    void hoanLo(String ma, String maLo, int soLuong);

    // ----- Thống kê -----
    long tongSoLuongTon();

    // Tổng tồn x giá bán (giá trị bán dự kiến, chưa thuế)
    long tongGiaTriTon();

    int demTheoLoai(String loai);

    // Hàng còn bán được (còn hạn) nhưng không quá nguong, tính theo hôm nay
    ArrayList<SanPham> layHangSapHet(int nguong);

    // Hàng tồn bằng 0
    ArrayList<SanPham> layHangHetHang();

    // Sản phẩm đang có lô hết hạn mà còn hàng
    ArrayList<SanPham> layHangHetHan(LocalDate ngay);

    // Các LÔ đã hết hạn nhưng còn hàng
    ArrayList<LoHang> layLoHetHan(LocalDate ngay);

    // Các lô còn hạn nhưng sẽ hết hạn trong soNgay ngày tới, còn hàng
    ArrayList<LoHang> layLoSapHetHan(LocalDate ngay, int soNgay);

    String baoCaoThongKe();

    void xuat();

    // ----- File (UTF-8): mỗi sản phẩm một dòng, ngay sau đó là các dòng LO của sản phẩm đó -----
    void ghiFile(String duongDan) throws IOException;

    // Đọc lỗi ở bất kỳ dòng nào thì ném IOException và danh sách hiện tại giữ nguyên
    void docFile(String duongDan) throws IOException;

    // Chỉ ĐỌC file thành danh sách tạm (chưa đụng danh sách hiện tại) để QuanlyKho kiểm tra liên kết với phiếu nhập trước
    ArrayList<SanPham> docSanPhamTuFile(String duongDan) throws IOException;

    // Thay toàn bộ nội dung danh sách bằng danh sách đã đọc
    void thayTheBang(ArrayList<SanPham> dsMoi);
}
