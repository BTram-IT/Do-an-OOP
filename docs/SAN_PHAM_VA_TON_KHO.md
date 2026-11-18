# Sản phẩm và tồn kho

Đã bỏ ba thuộc tính `tongSoSanPhamKhoiTao`, `soLuongHetHan`, `trangThaiDuyet` và các getter/setter tương ứng trong `SanPham`. Constructor của `SanPham`, `ThucAn`, `DoUong` không còn tham số trạng thái duyệt.

## Quy trình hiện tại

1. Admin/Quản lý thêm thức ăn hoặc đồ uống vào danh mục và chọn NCC đang hợp tác. Nhân viên cũng có thể thêm mới từ luồng nhập hàng. Tồn ban đầu luôn bằng 0; màn hình tạo danh mục không hỏi số lượng.
2. Nhân viên chọn hàng cần bổ sung hoặc tạo mặt hàng mới, nhập số lượng và giá. Chọn hàng có sẵn lấy NCC từ dữ liệu. Mục `3` xem/sửa danh sách hiện tại; mục `0` gom thành từng phiếu theo NCC và yêu cầu nhập mã `HDN...` cho từng phiếu, không tự sinh mã.
3. Người lập hoàn tất và lưu hóa đơn nhập chưa thanh toán để chờ duyệt; chưa làm tăng tồn kho.
4. Admin/Quản lý xác nhận phiếu tại chức năng duyệt, thanh toán thành công rồi mới nhập kho và in hóa đơn. Chương trình kiểm tra toàn bộ phiếu trước khi chi tiền/cộng hàng, chống tràn số lượng, chống thanh toán và duyệt hai lần. Hủy thanh toán giữ phiếu chờ duyệt.
5. Khi thanh toán hóa đơn bán thành công, kho giảm theo số hàng bán và hàng tặng.

Không còn quyền hoặc thao tác duyệt riêng từng sản phẩm. Luồng nhập thêm hàng cho phép tạo danh mục mới tồn 0 và chọn NCC ngay; không tạo hồ sơ sản phẩm chờ duyệt riêng. Menu sửa thông tin sản phẩm không cho nhập trực tiếp số lượng tồn kho.

Bán hàng kiểm tra mặt hàng còn tồn, chưa xóa, còn hạn và đủ số lượng mua/tặng. Giảm giá cận hạn cũng dựa vào tồn kho và hạn sử dụng, không phụ thuộc trạng thái duyệt.

## Hàng hết hạn và thống kê

Hết hàng được xác định bằng `getSoLuongTon() == 0`; hết hạn được xác định bằng ngày hiện tại so với `getHsd()`. Khi kiểm tra/cập nhật hàng hết hạn, chương trình đưa tồn của hàng hết hạn về 0 và thông báo số đơn vị vừa loại bỏ; không giữ bộ đếm tích lũy trong sản phẩm.

Không nhập hoặc lưu tách theo lô trong quy trình mới. Mỗi mã sản phẩm có một dòng trong phiếu; thêm lại mã đó sẽ cộng số lượng và dùng đơn giá/VAT lần nhập cuối cho cả dòng. Dữ liệu hóa đơn cũ được đọc nguyên trạng để giữ lịch sử tiền hàng.

Khi duyệt nhập kho, chương trình tự đặt NSX mới bằng ngày duyệt và HSD mới bằng ngày duyệt cộng số ngày giữa NSX/HSD trước đó của sản phẩm. Ví dụ sản phẩm có thời hạn 30 ngày thì sau nhập bổ sung vẫn có thời hạn 30 ngày. Ngày mới áp dụng cho toàn bộ lượng tồn của mã sản phẩm, không theo dõi riêng phần hàng cũ/mới. Tồn đã hết hạn được loại về 0 trước khi cộng hàng mới; tồn còn hạn được cộng dồn.

Danh sách sản phẩm hiển thị hai cột **NGÀY SẢN XUẤT** và **HẠN SỬ DỤNG**, định dạng `dd/MM/yyyy`. Lập phiếu, hủy hoặc thanh toán chưa duyệt không đổi ngày trên sản phẩm.

Số mặt hàng lấy từ `DSSanPham.getSoLuong()`. Tổng số đơn vị trong kho có thể tính bằng cách cộng `getSoLuongTon()` của các mặt hàng cần thống kê, không dùng biến đếm số lần tạo đối tượng.

## File dữ liệu

Sản phẩm mới được ghi với 16 cột, bao gồm mã NCC:

```text
THUCAN;ma;ten;giaNhap;giaBan;ton;donVi;nsx;hsd;thue;thanhPhan;baoQuan;xuatXu;laDoChay;maNCC;daXoa
DOUONG;ma;ten;giaNhap;giaBan;ton;donVi;nsx;hsd;thue;dungTich;thuongHieu;huongVi;loaiDoUong;maNCC;daXoa
```

`SanPham.tuDongFile(...)` dùng chung cho danh mục và hồ sơ đi kèm hóa đơn cũ. Bộ đọc hỗ trợ cả file 15 cột chưa có NCC, file 16 cột cũ có cột duyệt và file 16 cột mới có mã NCC. Dữ liệu mẫu đã được bổ sung NCC và các thuộc tính trống, giữ nguyên lượng tồn. Khi chạy, sản phẩm thiếu NCC hoặc có NCC không còn hợp tác phải được cập nhật ở danh mục; luồng chọn hàng không hỏi hoặc tự gán NCC. Xem [luồng nhập theo NCC và cách bổ sung dữ liệu mẫu](NHAP_HANG_THEO_NHA_CUNG_CAP.md).

Hồ sơ `SPMOI` trong hóa đơn cũ vẫn được đọc/lưu để bảo toàn dữ liệu. Nếu duyệt phiếu cũ mà mặt hàng chưa có danh mục, chương trình lấy hồ sơ đó tạo danh mục với tồn 0 rồi cộng đúng lượng nhập trên phiếu. Không hỏi duyệt riêng hồ sơ. Hóa đơn đã hoàn tất hoặc đã hủy không được cộng kho lại.

## Kiểm tra và tham khảo

`QuanLySanPhamTonKhoTest` kiểm tra thêm hai loại sản phẩm tồn 0, chặn bán khi chưa nhập, thanh toán/duyệt phiếu, chống cộng kho hai lần, nhập thay hàng hết hạn, chống cập nhật một phần khi phiếu sai và đọc/lưu file cũ/mới. Dữ liệu thử được lưu riêng, không ghi vào dữ liệu dự án.

Đã đối chiếu cách kiểm tra lượng tồn và cộng/trừ kho trong [SanPham.java của repo tham khảo](https://github.com/Phatmap2246/java-oop-mini-supermarket/blob/main/src/sanpham/SanPham.java) và [QuanLyTonKho.java](https://github.com/Phatmap2246/java-oop-mini-supermarket/blob/main/src/sanpham/QuanLyTonKho.java). Phần triển khai giữ cấu trúc hiện có của đồ án: kế thừa `SanPham` → `ThucAn`/`DoUong`, danh sách bằng mảng, menu `Scanner`/`switch` và lưu file văn bản.


## Thống kê và xử lý hạn sử dụng tại kho

Menu Kho bổ sung `9` thống kê hết hạn, `10` thống kê sắp hết hạn (0-30 ngày), `11` áp dụng giảm giá cận hạn (Admin/Quản lý), `12` loại bỏ lượng tồn hết hạn sau xác nhận. Xem báo cáo hoặc tồn kho không tự loại hàng. Khi duyệt nhập bổ sung: hàng cũ hết hạn thì bỏ lượng cũ, chỉ nhận lượng mới; còn hạn thì cộng cả hai. Xem [QUAN_LY_HAN_SU_DUNG_KHO.md](QUAN_LY_HAN_SU_DUNG_KHO.md).
