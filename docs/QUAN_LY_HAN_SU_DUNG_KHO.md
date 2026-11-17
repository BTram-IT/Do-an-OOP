# Quản lý hạn sử dụng trong kho

## Nhập bổ sung

Khi Admin/Quản lý duyệt phiếu nhập, chương trình kiểm tra trạng thái hàng cũ **trước khi đổi NSX/HSD**:

- HSD cũ trước ngày nhập kho: loại toàn bộ lượng tồn cũ hết hạn, tồn mới chỉ bằng số lượng thực nhập.
- HSD cũ bằng hoặc sau ngày nhập kho: cộng lượng tồn cũ với lượng nhập.
- Kiểm tra toàn bộ phiếu và giới hạn số nguyên trước khi thay đổi kho; một dòng không hợp lệ không làm xóa tồn hết hạn ở các dòng trước.
- Không cộng kho lần hai cho phiếu đã duyệt. Tạo hoặc thanh toán phiếu chưa làm thay đổi số lượng/ngày sản phẩm.

`SanPham.nhapThemHang(...)` thực hiện quy tắc trên, cùng với cập nhật NSX bằng ngày nhập kho và giữ thời hạn sử dụng đã khai báo để tính HSD mới. HSD đúng hôm nay vẫn được xem là còn hạn trong ngày.

## Menu Quản lý kho

Nhân viên mở menu `1` Kho. Admin/Quản lý mở menu `8` Nghiệp vụ kho.

| Mục | Chức năng |
| --- | --- |
| `9` | Thống kê sản phẩm hết hạn còn tồn |
| `10` | Thống kê sản phẩm sắp hết hạn trong 0-30 ngày còn tồn |
| `11` | Xử lý hàng sắp hết hạn bằng giảm giá, dành cho Admin/Quản lý |
| `12` | Xác nhận loại bỏ số lượng hàng hết hạn, dành cho tài khoản có quyền kiểm kê kho |

Cả thức ăn và đồ uống đều được kiểm tra theo HSD. Báo cáo bỏ qua sản phẩm đã xóa, không có HSD hoặc tồn bằng 0. Bảng gồm mã, loại, tên, NCC, số lượng tồn, đơn vị, NSX, HSD và số ngày còn lại/quá hạn; cuối bảng có số mặt hàng và tổng số đơn vị còn tồn.

**Xem thống kê không sửa dữ liệu.** Xem tồn kho tại menu sản phẩm cũng không tự loại hàng hết hạn. Muốn loại số lượng hết hạn, chọn mục `12` và xác nhận; chương trình giữ nguyên mã/danh mục, chỉ đưa tồn của hàng hết hạn về 0 rồi lưu. Hủy xác nhận không thay đổi dữ liệu.

## Xử lý hàng sắp hết hạn

Mục `11` hiển thị báo cáo trước, sau đó chọn `1` để tạo/cập nhật giảm giá cận hạn hoặc `0` để quay lại. Nhân viên kho được xem báo cáo nhưng không tự áp dụng giảm giá; Admin/Quản lý đang hoạt động mới có quyền thực hiện.

| Thời gian còn lại | Mức giảm tự động |
| --- | --- |
| 0-3 ngày | 50% |
| 4-7 ngày | 30% |
| 8-15 ngày | 15% |
| 16-30 ngày | 10% |

Giảm giá được tạo thành chương trình và liên kết khuyến mãi, kết thúc theo HSD; không thay giá bán gốc, NSX/HSD hoặc lượng tồn. Không áp dụng cho hàng hết hạn, đã xóa hoặc hết hàng. Giữ các chương trình thủ công đang chạy/chưa bắt đầu; gọi lại không tạo mã hoặc liên kết trùng.

Sau khi duyệt nhập kho, nếu ngày mới làm sản phẩm không còn thuộc khoảng cận hạn 30 ngày, hệ thống gỡ liên kết giảm cận hạn tự động cũ. Việc loại bỏ hàng hết hạn cũng dọn liên kết tự động không còn phù hợp. Các chương trình thủ công và thông tin khuyến mãi đã lưu trong hóa đơn lịch sử được giữ nguyên.

## Kiểm tra và tham khảo

`QuanLyHanSuDungKhoTest` kiểm tra các mốc hôm qua/hôm nay/3/4/7/8/15/16/30/31 ngày, số mặt hàng và tổng tồn, báo cáo không làm thay đổi kho, phân quyền giảm giá/loại hàng, nhập thay tồn hết hạn và cộng tồn còn hạn, tràn số lượng, mức giảm và tính lặp lại, bảo toàn khuyến mãi thủ công và lịch sử.

Đã đối chiếu cách lọc/thống kê và loại bỏ lượng hàng quá hạn trong [QuanLySanPham.java của repo tham khảo](https://github.com/Phatmap2246/java-oop-mini-supermarket/blob/main/src/sanpham/QuanLySanPham.java). Triển khai giữ cấu trúc hiện tại của đồ án và dùng HSD cho cả `ThucAn` và `DoUong`.
