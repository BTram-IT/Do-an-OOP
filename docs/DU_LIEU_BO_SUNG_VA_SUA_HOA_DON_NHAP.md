# Dữ liệu bổ sung và chỉnh sửa hóa đơn nhập

Cập nhật ngày 17/11/2026. Đếm số bản ghi ngay trước khi bổ sung, nhân 1,3 và làm tròn lên cho từng loại. Bản dữ liệu nguồn có cả giao dịch HDB122 vừa tạo trong lúc chạy thử. Các dòng cũ, tồn kho, ngày sản xuất, hạn sử dụng và điểm khách hàng được giữ nguyên; dữ liệu mẫu mới được nối thêm vào cuối file.

## Số lượng

| Loại dữ liệu | Trước | Thêm | Sau |
|---|---:|---:|---:|
| Sản phẩm | 102 | 31 | 133 |
| Khách hàng | 60 | 18 | 78 |
| Nhà cung cấp | 10 | 3 | 13 |
| Tài khoản | 15 | 5 | 20 |
| Khuyến mãi | 18 | 6 | 24 |
| Liên kết sản phẩm – khuyến mãi | 120 | 36 | 156 |
| Hóa đơn bán | 122 | 37 | 159 |
| Chi tiết bán | 380 | 114 | 494 |
| Hóa đơn nhập | 20 | 6 | 26 |
| Chi tiết nhập | 93 | 28 | 121 |

Các bản ghi bổ sung là dữ liệu giả lập để thử chương trình. Hóa đơn lịch sử có sẵn số tiền và trạng thái thanh toán; nạp dữ liệu không chạy lại giao dịch nhập/bán hay cộng/trừ lại điểm và tồn kho.

## Sửa hóa đơn nhập theo mã

Trong **Quản lý kho hàng → 13. Nhập mã hóa đơn nhập để chỉnh sửa**:

1. Nhập mã hóa đơn; nhập `0` để quay lại.
2. Chương trình kiểm tra mã tồn tại, đúng loại hóa đơn nhập, chưa thanh toán/chưa duyệt/chưa hủy và quyền người sửa. Mã sai hoặc phiếu bị khóa sẽ yêu cầu nhập lại.
3. Sau khi kiểm tra thành công mới hiện ngày lập, người lập, mã nhà cung cấp, ghi chú và các dòng hàng của phiếu đã chọn.
4. Chọn thêm, sửa số lượng/đơn giá/VAT, xóa mặt hàng, đổi nhà cung cấp, sửa ghi chú, hoàn tất hoặc hủy phiếu. Đổi nhà cung cấp phải khớp toàn bộ mặt hàng hiện có; không có thao tác thanh toán trong màn hình sửa.
5. `0` lưu và quay lại; `6` hoàn tất chỉnh sửa và chờ duyệt. Admin/Quản lý thanh toán tại bước duyệt, thành công mới cộng kho và in hóa đơn hoàn tất.

Luồng này dành cho hóa đơn đã có trong hệ thống, tách khỏi việc soạn danh sách nhập mới.

Nhân viên chỉ sửa phiếu chưa thanh toán do mình lập. Admin/Quản lý sửa được phiếu nháp của nhân viên khác. Hóa đơn đã trả tiền, đã nhập kho hoặc đã hủy đều khóa chỉnh sửa.

Trong **Quản lý kho hàng → 1. Nhập thêm hàng**, **mục 3. Xem / chỉnh danh sách hàng đang nhập** mở danh sách hiện tại, không yêu cầu mã hóa đơn. Khi chưa có hàng, chương trình thông báo ngắn và cho thêm mặt hàng; không in bảng hóa đơn rỗng với tổng tiền 0. Mục `0` gom hàng theo NCC, yêu cầu nhập mã `HDN...` chưa tồn tại cho từng phiếu rồi chuyển sang chỉnh sửa/hoàn tất để lưu phiếu chưa thanh toán chờ duyệt; không tự sinh mã. Chọn sản phẩm có sẵn lấy NCC từ dữ liệu, không hỏi lại NCC. Xem [quy trình nhập hiện tại và dữ liệu đã bổ sung](NHAP_HANG_THEO_NHA_CUNG_CAP.md).

| Mã để thử | Trạng thái / kết quả |
|---|---|
| HDN021 | Phiếu nháp của NV001, NCC001, 5 mặt hàng. Đăng nhập `nhanvien` để thêm/sửa/xóa. Có cả hàng cũ hết hạn, HSD hôm nay và tồn 0 để thử duyệt nhập bổ sung. |
| HDN022 | Phiếu nháp của NV001, NCC002, 5 mặt hàng. Hàng còn hạn/cận hạn, duyệt nhập sẽ cộng cả tồn cũ và số lượng nhập mới. |
| HDN023 | Đã trả bằng chuyển khoản, chờ duyệt kho. Khóa chỉnh sửa, Admin/Quản lý duyệt được. |
| HDN024 | Đã thanh toán và duyệt nhập kho. Khóa chỉnh sửa. |
| HDN025 | Đã hủy. Khóa chỉnh sửa. |
| HDN026 | Phiếu nháp của NV002, NCC003. `nhanvien` không được sửa; `nhanvien2`, `quanly` hoặc `admin` sửa được. |
| HDB123 | Hóa đơn bán giảm 100%, thanh toán tiền mặt 0 đồng. |
| HDB148 … HDB153 | Hóa đơn bán nháp, chưa thanh toán. |
| HDB154 … HDB159 | Hóa đơn bán đã hủy. |

## Các trường hợp bổ sung

Ngày tạo dữ liệu mới là **17/11/2026**; trạng thái hết hạn/cận hạn được tính theo ngày thực tế chạy chương trình.

| Mã | Trường hợp |
|---|---|
| TA112, TA113, DU053 | Hết hạn và còn tồn, dùng để thống kê hoặc loại bỏ tồn hết hạn. |
| TA114, DU054 | HSD đúng ngày tạo dữ liệu; ngày HSD vẫn còn được bán. |
| TA116, TA118, TA120, TA121, TA122, TA123 | HSD còn lần lượt 3, 7, 15, 16, 30, 31 ngày; thử các mức giảm cận hạn và ranh giới 30 ngày. |
| TA115, TA126, DU061 | Tồn 0. |
| TA116, TA117, TA118, TA119, TA120 | Tồn lần lượt 1, 5, 9, 10, 11; thử ranh giới đề xuất nhập hàng tồn thấp. |
| DU064, DU066 | Sản phẩm đã xóa, không được chọn cho tác vụ mới. |
| DU067 | Tồn 2.147.483.647; thử chặn cộng tồn vượt giới hạn. |
| NCC011, NCC012 | Nhà cung cấp đang hợp tác, có sản phẩm liên kết. |
| NCC013, DU062 | Nhà cung cấp ngừng hợp tác và sản phẩm thuộc NCC này; nhập hàng mới phải bị chặn. |
| KH061 … KH076 | Khách hoạt động, điểm từ 0 đến 10.000, có trường hợp 1 và 5 điểm. |
| KH077, KH078 | Khách đã xóa. |
| KM019 | Mua 2 tặng 1 đang hiệu lực. |
| KM020 | Mua 3 tặng 1, bắt đầu từ 18/11/2026. |
| KM021 | Giảm 15%, mức chi tối thiểu 50.000đ và mức giảm tối đa 20.000đ. |
| KM022 | Giảm 100% trong ngày 17/11/2026. |
| KM023 | Giảm trực tiếp 5.000đ đang hiệu lực. |
| KM024 | Khuyến mãi hết hạn ngày 16/11/2026. |

Tài khoản mới: `nhanvien11` hoạt động, `nhanvien12` có đề xuất sa thải chờ Admin xem xét, `nhanvien13` đã khóa, `quanly4` và `admin3` hoạt động. Mật khẩu thử nghiệm đều là `123456`.

## Kiểm tra và sao lưu

Đã biên dịch toàn bộ `src/` và `test/`; 424 kiểm tra nghiệp vụ qua, trong đó có 32 kiểm tra mới cho luồng sửa hóa đơn nhập theo mã. Kiểm tra dữ liệu bằng chính các lớp đọc file: nạp đủ bản ghi, mã không trùng, liên kết tồn tại, chi tiết thuộc đúng hóa đơn và tổng tiền đọc lại khớp. Đã chạy qua `giaodien.Main` để thử nhập mã sai, chỉnh sửa/lưu/đọc lại phiếu, khóa phiếu không hợp lệ, quyền nhân viên/quản lý và thanh toán trước khi in hóa đơn.

Bản sao lưu dữ liệu trước khi bổ sung nằm trong các file `SAO_LUU_DATA_TRUOC_TANG_30_PHAN_TRAM_*.zip` cùng thư mục `docs/`. Khởi động lại chương trình để nạp đầy đủ dữ liệu mới.
