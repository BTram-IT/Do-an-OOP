# Phân quyền Admin và Quản lý

Lớp `Giamdoc` được thay bằng `Admin`. Admin kế thừa `QuanLyCuaHang` để dùng toàn bộ nghiệp vụ vận hành và báo cáo, đồng thời bổ sung quyền quản trị tài khoản. Admin không sử dụng các trường phụ cấp chức vụ, cổ phần của vai trò cũ.

| Chức năng | Admin | Quản lý | Nhân viên |
| --- | --- | --- | --- |
| Lập hóa đơn nhập, bán và thanh toán | Có | Có | Có |
| Tiếp nhận, thêm, sửa, tra cứu khách hàng | Có | Có | Có |
| Xóa khách hàng | Có | Có | Không |
| Quản lý sản phẩm | Có | Có | Tra cứu; tạo mới trong luồng nhập hàng |
| Quản lý khuyến mãi | Có | Có | Chỉ tra cứu |
| Nhà cung cấp | Toàn bộ | Toàn bộ | Thêm, xem, tìm kiếm |
| Duyệt hóa đơn nhập kho | Có | Có | Không |
| Sửa/hủy hóa đơn chưa hoàn tất | Có | Có | Không |
| Báo cáo kinh doanh, thuế, tồn kho, bảng lương | Có | Có | Chỉ thống kê sản phẩm bán chạy |
| Thêm nhân viên, sửa hồ sơ/lương nhân viên thường | Có | Có | Không |
| Đề xuất sa thải nhân viên | Có thể sa thải trực tiếp | Có | Không |
| Chấp nhận/từ chối đề xuất sa thải | Có | Không | Không |
| Thêm Admin/Quản lý, sửa thông tin Quản lý | Có | Không | Không |
| Đặt lại mật khẩu, đổi tên đăng nhập các tài khoản | Có | Không | Không |
| Khóa/mở khóa tài khoản, khôi phục nhân sự | Có | Không | Không |

Admin có quyền `TOAN_QUYEN`; phương thức `Nguoidung.coQuyen(...)` chấp nhận mọi quyền nghiệp vụ khi tài khoản Admin đang hoạt động. Quản lý có các quyền vận hành được liệt kê trong `getQuyenHan()`. Tài khoản bị khóa không có quyền thao tác. Các menu kiểm tra quyền trước khi nhận lựa chọn; các hàm sửa/xóa/duyệt và quản trị tài khoản kiểm tra quyền người thực hiện.

Toàn quyền vẫn tuân theo tính hợp lệ của nghiệp vụ: hóa đơn đã hoàn tất không được sửa/hủy; Admin không thể tự khóa tài khoản đang đăng nhập. Khi khóa/sa thải, dữ liệu tài khoản vẫn được giữ để bảo toàn lịch sử hóa đơn.

Sản phẩm mới có tồn kho 0 và được chọn NCC đang hợp tác. Nhân viên có thể tạo mặt hàng mới trong luồng nhập thêm hàng. Chỉ duyệt hóa đơn nhập để cộng hàng vào kho; không có bước duyệt riêng từng sản phẩm. Xem [quy trình sản phẩm và tồn kho](SAN_PHAM_VA_TON_KHO.md).

## Menu và tài khoản mẫu

Menu Admin và Quản lý gồm: `1` sản phẩm, `2` khuyến mãi, `3` duyệt nhập kho, `4` nhà cung cấp, `5` nhân sự, `6` báo cáo, `7` khách hàng, `8` nghiệp vụ kho/lập phiếu nhập, `9` thu ngân/lập hóa đơn bán, `10` sửa/hủy hóa đơn, `0` đăng xuất.

Mục `5` của Admin mở quản trị mọi tài khoản: tạo nhân viên/Quản lý/Admin, xử lý yêu cầu sa thải, sửa thông tin/đặt lại mật khẩu và khóa/mở khóa. Mục `5` của Quản lý chỉ quản lý nhân viên thường và gửi đề xuất sa thải lên Admin.

Trong dữ liệu mẫu, đăng nhập `admin / 123456` (AD001) để dùng toàn quyền; `quanly / 123456` (QL001) để dùng quyền vận hành; `nhanvien / 123456` (NV001) để dùng quyền nhân viên. `admin2` (AD002) bị khóa. Nếu file tài khoản chưa tồn tại hoặc rỗng, chương trình tạo ba tài khoản mẫu hoạt động: `admin`, `quanly`, `nhanvien` với mật khẩu `123456`.

## Dữ liệu tài khoản

Định dạng Admin mới:

```text
ADMIN;maND;tenND;taiKhoan;matKhau;soDienThoai;Admin;daXoa
```

Tài khoản Admin mới dùng tiền tố mã `AD`. Bộ đọc vẫn hỗ trợ dòng cũ `GIAMDOC;...` và dòng bắt đầu bằng mã `GD...`: nạp thành đối tượng Admin, giữ nguyên mã, tên đăng nhập, mật khẩu và trạng thái khóa. Khi lưu lại, dòng được ghi bằng định dạng `ADMIN`. Hai trường phụ cấp/cổ phần cũ không còn được sử dụng. Tài khoản cũ mang tên `giamdoc` vẫn đăng nhập bằng tên đó khi người dùng nạp file cũ; dữ liệu mẫu hiện tại đã chuyển sang `admin`.

Người lập hóa đơn và người thực hiện thanh toán dùng kiểu `Nguoidung`, vì cả ba vai trò đều có thể thực hiện nghiệp vụ. Hóa đơn ghi đúng mã tài khoản đăng nhập và khôi phục đúng vai trò khi đọc file, không tạo tài khoản nhân viên thay thế cho Admin/Quản lý.

## Kiểm tra

Biên dịch và chạy các bộ kiểm tra từ thư mục dự án trong PowerShell:

```powershell
$sources = Get-ChildItem src,test -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d out/admin-check $sources
java -cp out/admin-check PhanQuyenAdminTest
java -cp out/admin-check TichHopKhuyenMaiHoaDonTest
java -cp out/admin-check QuyTrinhThanhToanHoaDonTest
```

`PhanQuyenAdminTest` sử dụng danh sách thử không ghi file dự án và file tạm riêng để kiểm tra chuyển đổi tài khoản, quyền quản trị, duyệt/sa thải, thanh toán theo vai trò, tài khoản bị khóa và chặn truy cập menu khi chưa đăng nhập.


## Thống kê và xử lý hạn sử dụng tại kho

Menu Kho bổ sung `9` thống kê hết hạn, `10` thống kê sắp hết hạn (0-30 ngày), `11` áp dụng giảm giá cận hạn (Admin/Quản lý), `12` loại bỏ lượng tồn hết hạn sau xác nhận. Xem báo cáo hoặc tồn kho không tự loại hàng. Khi duyệt nhập bổ sung: hàng cũ hết hạn thì bỏ lượng cũ, chỉ nhận lượng mới; còn hạn thì cộng cả hai. Xem [QUAN_LY_HAN_SU_DUNG_KHO.md](QUAN_LY_HAN_SU_DUNG_KHO.md).
