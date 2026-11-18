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

1. Chọn hàng cần nhập theo đề xuất: hết hàng, tồn kho thấp, sắp hết hạn, hết hạn. Chọn toàn bộ danh sách hoặc từng mặt hàng rồi nhập số lượng/giá. NCC lấy từ thông tin sản phẩm, không hỏi lại khi chọn hàng; không hỏi ngày từng lô.
2. Thêm sản phẩm mới, chọn NCC, lưu danh mục tồn 0 rồi nhập lượng hàng cần mua.
3. Xem/thêm/sửa/xóa danh sách cần nhập. Mỗi mã chỉ có một dòng: chọn lại mã sẽ cộng số lượng và áp dụng đơn giá/VAT lần nhập cuối. Không tách theo giá hoặc HSD.
0. Hoàn tất, nhập mã hóa đơn và gửi duyệt.
4. Hủy nhập hàng. Các danh mục mới đã tạo vẫn tồn tại với tồn 0.

Mục `3` xem/thêm/sửa/xóa trực tiếp danh sách đang nhập, không yêu cầu mã hóa đơn. Không còn mục `5` trùng chức năng. Muốn sửa hóa đơn cũ dùng **Quản lý kho → 13. Nhập mã hóa đơn nhập để chỉnh sửa**.

Khi chọn `0`, chương trình gom các mặt hàng theo `maNCC`: một NCC tương ứng một phiếu nhập. Với từng NCC, người dùng nhập mã dạng `HDN` và các chữ số (ví dụ `HDN027`), rồi nhập ghi chú. Mã được chuẩn hóa thành chữ hoa, phải chưa tồn tại và được gán cho toàn bộ chi tiết của phiếu. Không tự sinh mã; `/`, mã trống, sai định dạng hoặc trùng sẽ yêu cầu nhập lại. Nhập `0` ở bước nhập mã để bỏ phiếu của NCC đó; các phiếu NCC khác vẫn được xử lý. Danh sách rỗng không mở bước nhập mã.

Từng phiếu mở bước chỉnh sửa/hoàn tất: `6` hoàn tất và gửi duyệt, `7` xem danh sách mặt hàng, `0` hủy phiếu. Hoàn tất hợp lệ thì lưu ngay hóa đơn với trạng thái `DangXuLy`, phương thức `CHUA_TT`, chưa thanh toán và chưa cộng kho. Người lập không có thao tác thanh toán tại màn hình tạo hoặc sửa phiếu; ngay cả Admin/Quản lý lập phiếu cũng gửi duyệt theo cùng luồng. Nếu hủy hoặc hết đầu vào thì không lưu phiếu đang soạn, các phiếu đã hoàn tất trước đó vẫn được giữ.

Admin/Quản lý vào **Quản lý nhập kho → 2. Thanh toán và duyệt hóa đơn nhập kho**, chọn phiếu và xác nhận. Chương trình kiểm tra phiếu trước khi yêu cầu chi tiền; tiền mặt hoặc chuyển khoản phải thành công rồi mới cộng kho, đánh dấu `DaXuLyThanhCong` và in hóa đơn đã nhập kho. Hủy/thất bại/hết đầu vào lúc thanh toán giữ phiếu chờ duyệt, không cộng tồn. Hàm thanh toán nhập yêu cầu quyền `DUYET_HOA_DON`; hàm nhập kho cũng từ chối phiếu chưa trả tiền. Phiếu cũ hoặc phiếu thử lại đã thanh toán được bỏ qua bước chi tiền để tránh trả hai lần. Nếu đã trả tiền nhưng bước nhập kho chưa hoàn tất, vẫn lưu giao dịch để lần duyệt sau không trả lại tiền.

Khi duyệt, NSX của sản phẩm được đặt bằng ngày nhập kho và HSD được tính lại bằng NSX mới cộng thời hạn sử dụng cũ. Ngày áp dụng chung cho mã sản phẩm, không tách từng lô; phần tồn còn hạn được giữ và cộng thêm, phần tồn hết hạn được loại trước khi cộng. Bảng sản phẩm hiển thị cả NSX/HSD. Kiểm tra chọn mặt hàng, thanh toán và duyệt kho đều chặn sản phẩm đã gán NCC khác với NCC của phiếu. Khi đổi NCC của phiếu, các mặt hàng đã gán NCC phải phù hợp.

Người lập phiếu được giữ nguyên. Người thực hiện thanh toán được lưu riêng ở cột thứ 18 của dòng `HDNHAP` và hiển thị trên hóa đơn; đọc lại vẫn giữ đúng mã Quản lý/Admin. Định dạng nhập 11 hoặc 17 cột cũ vẫn đọc được; các phiếu cũ thiếu thông tin người chi riêng dùng thông tin người lập đã có, không tự suy đoán lại người thanh toán lịch sử.

## Dữ liệu cũ/mới

File sản phẩm mới có 16 cột. Sau 14 cột thông tin sản phẩm là `maNCC;daXoa`:

```text
THUCAN;ma;ten;giaNhap;giaBan;ton;donVi;nsx;hsd;thue;thanhPhan;baoQuan;xuatXu;laDoChay;maNCC;daXoa
DOUONG;ma;ten;giaNhap;giaBan;ton;donVi;nsx;hsd;thue;dungTich;thuongHieu;huongVi;loaiDoUong;maNCC;daXoa
```

Vẫn đọc được file 15 cột chưa có NCC và file cũ có cột duyệt sản phẩm. Khi chạy, chương trình không hỏi hoặc tự gán NCC cho sản phẩm thiếu dữ liệu. Sản phẩm thiếu NCC, có NCC không tồn tại hoặc ngừng hợp tác sẽ được báo lỗi; cập nhật NCC ở menu sửa sản phẩm trước khi nhập hàng.

Bộ dữ liệu mẫu được rà và bổ sung ngày 18/11/2026:

- Bổ sung `maNCC` còn trống cho 102 sản phẩm; cả 133 sản phẩm có đủ 16 thuộc tính.
- Giữ NCC của phiếu đang xử lý; với hàng khác, dùng NCC đang hợp tác trong hóa đơn gần nhất nếu có. Với 43 sản phẩm chưa có lịch sử từ NCC đang hợp tác, gán NCC mẫu: `NCC001` cho thức ăn, `NCC003` cho đồ uống. Đây là dữ liệu giả lập được điền vào file, không phải quy tắc tự gán khi chạy.
- Bổ sung thành phần, bảo quản và xuất xứ mẫu cho thức ăn `TA111`: nguyên liệu thực phẩm; nơi khô thoáng, tránh ánh nắng; Việt Nam.
- Điền NCC cho 5 hồ sơ `SPMOI` theo đúng NCC của hóa đơn chứa hồ sơ. Không thay nội dung giao dịch, tổng tiền, tồn kho hoặc NSX/HSD.
- Các trường ngân hàng trống của thanh toán tiền mặt/chưa thanh toán và ghi chú tùy chọn vẫn hợp lệ; các cột khác nhau giữa vai trò tài khoản/loại khuyến mãi là định dạng riêng của từng lớp.

`NhapHangTheoNhaCungCapTest` kiểm tra đọc/lưu NCC, chọn hàng không hỏi NCC, tách phiếu, nhập mã riêng cho mỗi NCC, mã sai/trùng, sửa/xóa hàng, thêm mới, hủy từng phiếu và lưu phiếu chờ duyệt. `DuyetVaThanhToanHoaDonNhapTest` kiểm tra quyền chi tiền, hủy/thử lại, thanh toán tại bước duyệt, chống trả tiền/cộng kho hai lần và lưu người thanh toán riêng. Dữ liệu thử riêng không ghi vào file dự án.


## Thống kê và xử lý hạn sử dụng tại kho

Menu Kho bổ sung `9` thống kê hết hạn, `10` thống kê sắp hết hạn (0-30 ngày), `11` áp dụng giảm giá cận hạn (Admin/Quản lý), `12` loại bỏ lượng tồn hết hạn sau xác nhận. Xem báo cáo hoặc tồn kho không tự loại hàng. Khi duyệt nhập bổ sung: hàng cũ hết hạn thì bỏ lượng cũ, chỉ nhận lượng mới; còn hạn thì cộng cả hai. Xem [QUAN_LY_HAN_SU_DUNG_KHO.md](QUAN_LY_HAN_SU_DUNG_KHO.md).
