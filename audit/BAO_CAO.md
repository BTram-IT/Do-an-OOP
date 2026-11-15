# Báo cáo kiểm tra chương trình — 14/11/2026

Đã kiểm tra phiên bản mã đang có trong workspace, không sửa mã nghiệp vụ. Toàn bộ chương trình được chạy trong `.audit/`, không chạy trực tiếp trên thư mục dữ liệu thật. So sánh SHA-256 trước/sau xác nhận `data/` gốc không thay đổi.

## Kết quả thực chạy

- Java 26.0.1, Windows; biên dịch UTF-8, `javac -Xlint:all`: **39 file nguồn biên dịch thành công, 32 cảnh báo**. Cảnh báo chủ yếu gọi phương thức có thể override trong constructor và ép kiểu thừa; không đồng nghĩa với 32 lỗi crash.
- Batch đầu: **193 tiến trình** gồm bộ probe ban đầu, khởi động/EOF/thoát, file thiếu/rỗng/hỏng, 16 menu × 10 đầu vào và 12 chuỗi fuzz có seed cố định. Mỗi lựa chọn menu 0–8 được thử; nhánh cần nhập tiếp kết thúc bằng EOF. Đây là kiểm tra nhánh đầu menu, không phải hoàn tất mọi nghiệp vụ của từng nhánh.
- **11 kịch bản qua `giaodien.Main`**: đăng nhập/đăng xuất đủ ba vai trò, menu thu ngân, ba đầu vào gây lỗi, báo cáo dữ liệu rỗng và hủy hóa đơn nhập.
- Bộ probe cuối: **10.028 phép kiểm tra, 10.012 đạt, 16 thất bại**. Trong đó có 10.000 mẫu tính tiền bán sinh ngẫu nhiên seed `20261114`; các lỗi được gộp theo nguyên nhân bên dưới. Probe còn kiểm tra thanh toán tiền mặt, không trừ kho hai lần, ba loại khuyến mãi, số cực trị, ngày và lưu/nạp dữ liệu.
- **Chưa tái hiện lỗi làm JVM thoát do exception không được bắt** trong các kịch bản đã chạy. Ba đầu vào sai trong luồng thêm sản phẩm làm mất thao tác và quay về đăng nhập do `batDau()` bắt `RuntimeException`.
- Batch ban đầu có hai timeout 12 giây: probe benchmark và `menuQuanLyNhapKhoQuyTrinh_6`. Probe cuối chạy lại với giới hạn 120 giây hoàn thành; menu chạy lại hoàn thành sau khoảng 607 ms và trả `AUDIT_EOF`. Không kết luận hai timeout này là vòng lặp vô hạn. JSON gốc giữ nguyên kết quả ban đầu để đối chiếu.

## Lỗi đã xác nhận

| Mức | Vị trí | Cách tái hiện / kết quả | Hướng sửa |
|---|---|---|---|
| Cao | `src/hoadon/DSHoadon.java:502` | Ghi hóa đơn bán có dùng 1.000 đồng điểm và phương thức `TienMat`, nạp lại: mất điểm đã dùng và phương thức. Reader chỉ khởi tạo phần thông tin chính, không khôi phục các trường thanh toán đã lưu. Lần lưu sau ghi lại thông tin thiếu. | Khôi phục đầy đủ phần thanh toán, điểm và trường mở rộng; đối chiếu tổng tiền sau nạp. |
| Cao | `src/hoadon/DSHoadon.java:677` | File có `BROKEN;RECORD`, gọi `docFile()` rồi `ghiFile()` vào cùng đường dẫn: dòng gốc bị mất. Reader báo lỗi nhưng không bật cơ chế bảo vệ file. | Đánh dấu file lỗi, giữ bản gốc, không ghi đè bằng danh sách chỉ nạp được một phần. |
| Cao | `src/giaodien/Giaodien.java:1054` | Nhân viên → kho → tạo hóa đơn nhập → nhập mã/ghi chú/NCC → chọn sản phẩm `0` để hủy: vẫn thông báo lưu và tạo hóa đơn nhập rỗng. `nhapThongTin()` trả `false` nhưng caller bỏ qua. | Chỉ thêm/lưu nếu hàm nhập trả `true`; kiểm tra có chi tiết. |
| Cao | `src/sanpham/SanPham.java:292`, `:295` | Quản lý → sản phẩm → thêm thức ăn. Giá bán `-1`, VAT `-1` hoặc `101`: `IllegalArgumentException` đi lên `batDau()`, mất thao tác và trở về đăng nhập. Đã tái hiện qua `Main`. | Kiểm tra miền giá trị và cho nhập lại ngay tại trường sai. |
| Cao | `src/hoadon/CTHDonNhap.java:397` | Reader chấp nhận dòng có số lượng `-1`, đơn giá `NaN`, VAT `101`; gán trực tiếp thuộc tính, bỏ qua setter. | Kiểm tra toàn bộ trường trước khi tạo chi tiết; từ chối giá trị không hữu hạn. |
| Cao | `src/sanpham/SanPham.java:242` | Tồn kho `2147483647`, cộng thêm `1` → `-2147483648`, không báo lỗi. | Kiểm tra bằng `long`/`Math.addExact` trước khi thay đổi dữ liệu. |
| Cao | `src/person/Khachhang.java:145` | Điểm `2147483647`, cộng `1` → điểm âm. | Kiểm tra tràn số trước khi cập nhật; xác định giới hạn điểm. |
| Vừa | `src/giaodien/Giaodien.java:342` | Menu thu ngân hiển thị `0. Quay lại` nhưng chỉ nhận `1–5`. `0` bị từ chối; `5` được nhận mà không có nhánh xử lý. | Đổi giới hạn nhập thành `0–4`. |
| Vừa | `src/giaodien/Giaodien.java:99`, `src/sanpham/SanPham.java:169`, `src/hoadon/CTHDonNhap.java:388` | `31/02/2026` được formatter SMART điều chỉnh thành ngày cuối tháng thay vì từ chối. Hàm nhập ngày dùng chung trong `Ktradulieu` đã STRICT, nhưng các chỗ này dùng formatter riêng. | Dùng `dd/MM/uuuu` và `ResolverStyle.STRICT` thống nhất. |
| Vừa | `src/sanpham/SanPham.java:234` | Sản phẩm có HSD `garbage`: parse lỗi bị bỏ qua, `kiemTraHanSuDung()` trả `true`. | Báo dữ liệu lỗi và chặn bán cho đến khi sửa HSD. |
| Vừa | `src/sanpham/SanPham.java:97`, `:130`, `src/hoadon/CTHoaDon.java:84` | Setter giá nhận `NaN`/`Infinity`, setter thuế sản phẩm nhận `NaN`. Giá hữu hạn `Double.MAX_VALUE`, số lượng `2` cũng làm thành tiền thành `Infinity`. Đầu vào tương tác dùng chung chặn số không hữu hạn, nhưng API model chưa chặn đầy đủ. | Kiểm tra `Double.isFinite` và giới hạn tiền/tổng tiền ở model. |
| Vừa | `src/hoadon/CTHDonNhap.java:342` | Với `Locale.GERMANY`, ghi VAT `5,00`; reader dùng `Double.parseDouble` không đọc được chính dòng vừa ghi. | Ghi file bằng locale cố định, ví dụ `Locale.US`. |

16 phép kiểm tra thất bại không tương đương 16 lỗi độc lập: nhiều phép thử kiểm tra các biến thể cùng nguyên nhân. Ngược lại, một số lỗi menu và hủy hóa đơn được xác nhận trong bộ kịch bản riêng, không nằm trong 16 phép probe.

## Benchmark

Đo một lượt trên máy hiện tại, có ghi file thật trong thư mục cách ly; không phải JMH. Thời gian bị ảnh hưởng bởi JVM, ổ đĩa, phần mềm bảo vệ máy và các tác vụ kiểm thử khác. Chỉ nên dùng để nhận diện xu hướng, không dùng như cam kết hiệu năng.

| Số sản phẩm | Thêm lần lượt, có lưu file | 10.000 lần tìm theo mã |
|---:|---:|---:|
| 100 | 1.716 ms | 43,863 ms |
| 1.000 | 8.540 ms | 132,260 ms |
| 2.000 | 54.911 ms | 688,941 ms |

Trong bảng, dấu chấm phân cách hàng nghìn, dấu phẩy là phần thập phân. Ví dụ thêm 2.000 sản phẩm mất khoảng **54,9 giây**.

`DSSanPham.themSP()` tìm trùng tuyến tính và ghi lại toàn bộ danh sách sau mỗi lần thêm. Thêm hàng loạt phát sinh khối lượng ghi tăng gần bậc hai theo số sản phẩm. Nên có thao tác thêm hàng loạt và chỉ lưu một lần; có thể dùng chỉ mục mã để tăng tốc tra cứu.

## Chạy lại và bằng chứng

Từ thư mục gốc dự án, cần JDK và Python:

```powershell
python -X utf8 audit/run_audit.py
python -X utf8 audit/run_scenarios.py
```

- `.audit/compile.log`: cảnh báo compiler.
- `.audit/results.json`: 193 kết quả batch đầu, thời gian và đường dẫn log từng ca.
- `.audit/probe-final/summary.txt`: kết quả bộ probe cuối và benchmark được dùng trong báo cáo này.
- `.audit/probe-final/output.log`: toàn bộ 10.028 phép kiểm tra.
- `.audit/scenarios/results.json`: 11 kịch bản qua `Main`, xác nhận hash dữ liệu gốc.
- `.audit/scenarios/<tên-ca>/output.log`: nhật ký tái hiện các lỗi thao tác.
- `.audit/retest-menu/result.json`: chạy lại ca menu từng timeout.

Mã kiểm thử nằm trong `audit/`, mã sản phẩm trong `src/` chưa được sửa. `.audit/` là kết quả sinh ra, không cần đưa toàn bộ vào Git.

## Giới hạn

Không thể kiểm tra mọi chuỗi đầu vào và trạng thái có thể có. Đợt này chưa đo branch coverage bằng JaCoCo, chưa kiểm thử tổ hợp đầy đủ các chương trình khuyến mãi, hoàn tất mọi biến thể duyệt nhập kho/chuyển khoản, lỗi mất điện giữa nhiều lần ghi file, hết dung lượng ổ đĩa, nhiều tiến trình cùng sửa dữ liệu hoặc tải cực lớn gây hết RAM. Các menu phản chiếu dùng đối tượng vai trò giả lập và EOF để kiểm tra nhánh đầu; kịch bản `Main` dùng tài khoản mặc định trong dữ liệu cách ly. Không coi mọi exception do API từ chối dữ liệu sai là crash chương trình.
