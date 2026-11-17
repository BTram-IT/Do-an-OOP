# Sản phẩm và phiếu nhập theo nhà cung cấp

Đã đối chiếu luồng `them(...)` trong [QuanLySanPham.java](https://github.com/Phatmap2246/java-oop-mini-supermarket/blob/main/src/sanpham/QuanLySanPham.java) và `nhapThemHang(...)` trong [DanhSachHoaDonNhap.java](https://github.com/Phatmap2246/java-oop-mini-supermarket/blob/main/src/hoadon/DanhSachHoaDonNhap.java) của repo tham khảo. Triển khai giữ các lớp, tên biến tiếng Việt, mảng sản phẩm, danh sách hóa đơn, menu `Scanner`/`switch` và file văn bản của đồ án này.

## Sản phẩm

- `SanPham` có thêm `maNCC`, getter/setter, cột hiển thị và dữ liệu ghi file. `ThucAn`/`DoUong` có constructor nhận mã NCC; constructor cũ còn hỗ trợ dữ liệu chưa gán NCC.
- Thêm sản phẩm: nhập thông tin thức ăn/đồ uống, chọn NCC theo STT trong danh sách đang hợp tác, rồi mới lưu. NCC bị xóa/ngừng hợp tác không xuất hiện trong lựa chọn. Chưa có NCC thì cần thêm NCC trước.
- Tồn kho khi tạo danh mục luôn là 0. Admin/Quản lý tạo từ menu sản phẩm; nhân viên cũng có thể tạo mặt hàng mới trong luồng nhập hàng.
- Admin/Quản lý đổi NCC tại mục `6` trong menu sửa sản phẩm.

## Nhập thêm hàng

Từ menu nhân viên: `1` Kho → `1` Nhập thêm hàng. Admin/Quản lý dùng mục `8` Nghiệp vụ kho rồi chọn `1`.

Menu nhập thêm hàng:

1. Chọn hàng cần nhập theo đề xuất: hết hàng, tồn kho thấp, sắp hết hạn, hết hạn. Chọn toàn bộ danh sách hoặc từng mặt hàng rồi nhập số lượng/giá, không hỏi ngày từng lô.
2. Thêm sản phẩm mới, chọn NCC, lưu danh mục tồn 0 rồi nhập lượng hàng cần mua.
3. Xem/thêm/sửa/xóa danh sách cần nhập. Mỗi mã chỉ có một dòng: chọn lại mã sẽ cộng số lượng và áp dụng đơn giá/VAT lần nhập cuối. Không tách theo giá hoặc HSD.
0. Hoàn tất danh sách và thanh toán.
4. Hủy nhập hàng. Các danh mục mới đã tạo vẫn tồn tại với tồn 0.

Khi hoàn tất danh sách, chương trình tự gom các mặt hàng theo `maNCC`: một NCC tương ứng một phiếu nhập, mỗi phiếu có mã và chi tiết riêng. Không phải chọn lại NCC cho từng phiếu.

Từng phiếu mở bước chỉnh sửa/thanh toán: `6` thanh toán, `7` xem danh sách mặt hàng, `0` hủy phiếu. Tiền mặt hoặc chuyển khoản phải thành công mới in và lưu hóa đơn. Hủy/thất bại/hết đầu vào không in hóa đơn thành công; nếu các phiếu trước đã trả tiền thì chúng vẫn được giữ. Trước khi trả tiền chỉ có tạm tính và danh sách mặt hàng khi cần xem, không tự in hóa đơn đầy đủ.

Phiếu nhập đã thanh toán vẫn chờ Admin/Quản lý duyệt để cộng kho. Khi duyệt, NSX của sản phẩm được đặt bằng ngày nhập kho và HSD được tính lại bằng NSX mới cộng thời hạn sử dụng cũ. Ngày áp dụng chung cho mã sản phẩm, không tách từng lô; phần tồn còn hạn được giữ và cộng thêm, phần tồn hết hạn được loại trước khi cộng. Bảng sản phẩm hiển thị cả NSX/HSD. Kiểm tra chọn mặt hàng, thanh toán và duyệt kho đều chặn sản phẩm đã gán NCC khác với NCC của phiếu. Khi đổi NCC của phiếu, các mặt hàng đã gán NCC phải phù hợp.

## Dữ liệu cũ/mới

File sản phẩm mới có 16 cột. Sau 14 cột thông tin sản phẩm là `maNCC;daXoa`:

```text
THUCAN;ma;ten;giaNhap;giaBan;ton;donVi;nsx;hsd;thue;thanhPhan;baoQuan;xuatXu;laDoChay;maNCC;daXoa
DOUONG;ma;ten;giaNhap;giaBan;ton;donVi;nsx;hsd;thue;dungTich;thuongHieu;huongVi;loaiDoUong;maNCC;daXoa
```

Vẫn đọc được file 15 cột chưa có NCC và file cũ có cột duyệt sản phẩm. Không đoán NCC từ lịch sử, vì một sản phẩm có thể từng được nhập từ nhiều đối tác. Những mặt hàng cũ hiển thị **Chưa gán**; khi chọn vào luồng nhập thêm hàng, người dùng chọn một NCC đang hợp tác một lần, chương trình lưu lại cho các lần sau. Lượng tồn và thông tin cũ được giữ nguyên. Hồ sơ đi kèm hóa đơn cũ cũng được sao chép/đọc/lưu cùng mã NCC nếu có.

`NhapHangTheoNhaCungCapTest` kiểm tra chọn NCC hoạt động, đọc/lưu, gán NCC cho hàng cũ, tách phiếu, chặn nhập sai NCC, sửa/xóa hàng, thêm mới trong luồng nhập, hủy một trong nhiều phiếu, chỉ in hóa đơn sau thanh toán và chỉ cộng kho sau duyệt. Dữ liệu thử riêng không ghi vào file dự án.


## Thống kê và xử lý hạn sử dụng tại kho

Menu Kho bổ sung `9` thống kê hết hạn, `10` thống kê sắp hết hạn (0-30 ngày), `11` áp dụng giảm giá cận hạn (Admin/Quản lý), `12` loại bỏ lượng tồn hết hạn sau xác nhận. Xem báo cáo hoặc tồn kho không tự loại hàng. Khi duyệt nhập bổ sung: hàng cũ hết hạn thì bỏ lượng cũ, chỉ nhận lượng mới; còn hạn thì cộng cả hai. Xem [QUAN_LY_HAN_SU_DUNG_KHO.md](QUAN_LY_HAN_SU_DUNG_KHO.md).
