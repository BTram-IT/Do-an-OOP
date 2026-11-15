# Kiểm tra logic và ngoại lệ của đồ án

Ngày kiểm tra: 15/11/2026. Kiểm tra mã đang có trong workspace, gồm các thay đổi chưa commit. Không sửa src/ hoặc data/ gốc. Tất cả thao tác nghiệp vụ có ghi file chạy trong `.audit/review-current/run/`.

Không thể chứng minh đã chạy mọi đầu vào và mọi tổ hợp trạng thái của chương trình. Báo cáo này phân biệt kết quả thực thi, lỗi đọc mã và giới hạn của test.

## Kết quả chạy

- Biên dịch toàn bộ Java trong src/ và audit/: thành công.
- AuditProbe: 10.028 kiểm tra, 13 FAIL. Có 10.000 ca số học ngẫu nhiên; đây không phải 10.028 luồng nghiệp vụ khác nhau.
- ScannerProbe: PASS 10 kiểm tra.
- LocalDateProbe: dừng ở AssertionError `Expired stock must be removed`; những kiểm tra sau điểm này chưa chạy trong bộ đó.
- ReviewProbe: 23 nhóm ca bổ sung; nhóm cuối thử 16 menu một tham số Scanner, 15 menu trả về đúng, menu thu ngân không thoát được bằng 0.
- Ca thanh toán thường với ngày sản phẩm hợp lệ: lần đầu true, lần hai false, tồn 100 -> 98. FAIL tương ứng trong AuditProbe do sản phẩm mẫu của test không đặt HSD; không kết luận lỗi trừ kho hai lần.

## Các lỗi đã tái hiện

| # | Mức độ | Vị trí | Điều kiện và kết quả thực tế |
|---|---|---|---|
| 1 | Cao, kẹt luồng UI | src/giaodien/Giaodien.java:330 | Menu thu ngân hiển thị `0. Quay lại` nhưng gọi luaChon với khoảng 1..5. Nhập 0 bị từ chối; 5 được nhận nhưng không có case. Với input hữu hạn, kết thúc bằng NoSuchElementException. |
| 2 | Nghiêm trọng, sai tiền | src/hoadon/Hoadonban.java:538 | Dùng 3.000.000 điểm: phép nhân int `diemMuonDung * 1000` tràn trước khi gán double. Tiền giảm = -1.294.967.296, hóa đơn 3 tỷ thành 4.294.967.296. Ngưỡng bắt đầu vượt int là 2.147.484 điểm. |
| 3 | Nghiêm trọng, mất dữ liệu thanh toán | src/hoadon/DSHoadon.java:657 | docFile dựng hóa đơn nhưng không gọi khoiPhucThanhToanTuFile. Lưu hóa đơn 2.000đ đã dùng 1.000đ điểm, mở lại: tiền điểm = 0, phương thức = null, tổng tăng về 2.000đ. Cũng không khôi phục phương thức của hóa đơn nhập. |
| 4 | Nghiêm trọng, sai hạn sử dụng | src/hoadon/HoadonNhap.java:615 | Duyệt nhập bỏ qua ct.getHanSuDung(), lấy độ dài HSD cũ rồi gán NSX = ngày lập. Ca chạy yêu cầu HSD 13/02/2027 nhưng kho thành 05/12/2026. Toàn bộ hàng cũ cùng mã cũng bị đổi hạn, có thể kéo dài hạn hàng cũ. |
| 5 | Cao, phá trạng thái hóa đơn | src/hoadon/Hoadonban.java:474; src/hoadon/HoadonNhap.java:387; src/hoadon/HoaDon.java:271 | Cả thanh toán và duyệt nhập chỉ chặn đã thành công, không chặn DaHuy. Hóa đơn bán đã hủy vẫn trả paid=true; hóa đơn nhập đã hủy vẫn cộng kho và chuyển DaXuLyThanhCong qua xacNhanXuLy. Tái hiện bằng API nghiệp vụ; UI không nhất thiết cung cấp trực tiếp mọi đường gọi này. |
| 6 | Cao, bán hàng bị khóa | src/hoadon/Hoadonban.java:54; src/hoadon/Hoadonban.java:133 | Không kiểm tra daXoa/trạng thái duyệt sản phẩm trước bán. Sản phẩm vừa daXoa=true vừa Chờ duyệt vẫn thêm được và thanh toán thành công. |
| 7 | Cao, sai kiểm tra hết hạn | src/sanpham/SanPham.java:191 | Hàng đã hết hạn bị chuyển tồn về 0 nhưng hàm vẫn return true. Caller nhận kết luận còn hạn. Không khẳng định luôn bán được hàng hết hạn vì một số caller còn kiểm tra tồn. |
| 8 | Nghiêm trọng, mất dữ liệu file | src/hoadon/DSHoadon.java:449; src/hoadon/DSHoadon.java:503 | Đọc file lỗi vẫn thay danh sách bằng phần đọc được, sau đó ghi đè file. File BROKEN;RECORD thành 0 byte sau docFile/ghiFile. Thoát chương trình cũng gọi ghiDuLieu, nên không cần người dùng chủ động lưu riêng. |
| 9 | Cao, mất khách hàng sau một dòng lỗi | src/person/DsKhachHang.java:174 | Xóa danh sách trước đọc; gặp điểm không phải số thì dừng cả file. File 3 khách có dòng 2 lỗi chỉ còn khách đầu; lưu tiếp chỉ còn 1 dòng. DSNhaCungCap có mẫu clear/đọc/catch tương tự; DSSanPham bỏ qua dòng lỗi rồi vẫn cho lưu phần còn lại (xác định qua đọc mã). |
| 10 | Cao, làm bẩn giá/thuế | src/sanpham/SanPham.java:79; src/sanpham/SanPham.java:90; src/sanpham/SanPham.java:129; src/hoadon/CTHoaDon.java:92 | Các phép so sánh < hoặc <= không chặn NaN; giá bán +Infinity cũng được nhận. Constructor SanPham còn gán thẳng giá, thuế, tồn. Test cũ xác nhận setter nhận NaN; chi tiết bán với giá Double.MAX_VALUE có tổng NaN. |
| 11 | Cao, dữ liệu nhập không hợp lệ được duyệt | src/hoadon/CTHDonNhap.java:389; src/hoadon/HoadonNhap.java:420 | Parser gán trực tiếp số lượng/giá/VAT, bỏ setter. Nhận số lượng -1, giá NaN, VAT 101. Phiếu có giá NaN được duyệt thành công, giá nhập kho thành NaN vì kiểm tra <=0 không bắt NaN. |
| 12 | Cao, tràn tồn kho | src/sanpham/SanPham.java:211 | set tồn Integer.MAX_VALUE rồi congTonKho(1) thành -2147483648, không báo lỗi. Luồng duyệt nhập có kiểm tra tổng bằng long nên không quy mọi lần nhập thường đều kích hoạt lỗi này. |
| 13 | Cao, tràn điểm và mất dữ liệu lần sau | src/person/Khachhang.java:144 | Điểm Integer.MAX_VALUE cộng 1 thành -2147483648. Khi ghi rồi đọc lại, constructor chặn điểm âm làm docFile khách hàng dừng giữa file. |
| 14 | Cao, không hoàn tất hóa đơn hợp lệ 0đ | src/thanhtoan/TienMat.java:61; src/thanhtoan/ChuyenKhoan.java:87 | Hóa đơn 2.000đ, dùng đủ 2 điểm: tổng 0 nhưng thanh toán tiền mặt trả false do bắt buộc soTien>0. Chuyển khoản cũng bắt buộc >0. Khuyến mãi giảm 100% chịu cùng điều kiện. |
| 15 | Cao, tài khoản không hợp lệ | src/person/Nguoidung.java:27; src/person/DSNhanVien.java:72 | Constructor Nguoidung gán trực tiếp toàn bộ trường, bỏ mọi setter. Tạo được mã null, mật khẩu 1 ký tự, SĐT sai. them() chỉ kiểm tra mã và tài khoản; không chặn mật khẩu null. |
| 16 | Cao, làm hỏng file tài khoản | src/person/Nguoidung.java:131 | Setter tài khoản nhận `audit;injected`, serializer không escape. Lưu rồi đọc lại thất bại, danh sách mới có 0 tài khoản. Bộ nhập UI dùng Ktradulieu đã chặn `;`, nên đây là đường setter/API/constructor; không quy thành lỗ hổng nhập bàn phím thông thường. |
| 17 | Cao, quyền thành viên đã xóa | src/hoadon/Hoadonban.java:425; src/hoadon/Hoadonban.java:496 | Tìm khách không xét isDaXoa. Khách daXoa=true vẫn dùng điểm, thanh toán thành công và giảm 1.000đ. |
| 18 | Cao, báo cáo sai kỳ | src/person/QuanLyCuaHang.java:162 | thongKeSanPhamBanDuoc nhận tuNgay/denNgay nhưng không lọc ngày. Hóa đơn năm trước vẫn xuất hiện trong báo cáo hôm nay. Giám đốc dùng báo cáo qua lớp quản lý cũng chịu lỗi này. |
| 19 | Cao, không đọc lại được dữ liệu theo locale | src/hoadon/CTHDonNhap.java:334 | String.format không cố định Locale, parser dùng Double.parseDouble. Locale.GERMANY ghi VAT `5,00`, đọc lại trả null, chi tiết nhập bị mất. |

## Ngoại lệ và nguy cơ crash tác vụ

1. **NullPointerException khi kiểm tra mật khẩu**: constructor cho phép mật khẩu null, DSNhanVien.them nhận tài khoản này; biểu thức `nd.getMatKhau().equals(mk)` tại Giaodien.java:234 ném NPE. ReviewProbe đã tái hiện cùng biểu thức qua tài khoản vừa thêm. Với dữ liệu file bình thường, chuỗi `null` ghi ra không phải Java null, nên không khẳng định mọi lần khởi động lại gây NPE.
2. **DateTimeException sau khi đã cộng kho**: HoadonNhap.java:618 gọi ngayLap.plusDays mà không chuẩn bị trước/rollback. Dùng ngày lập LocalDate.MAX: kho 100 -> 101, sau đó ném ngoại lệ; hóa đơn vẫn DangXuLy. Gọi lại có nguy cơ cộng kho tiếp. Đây là đầu vào biên qua API; UI nhập ngày thường giới hạn định dạng 4 chữ số.
3. **NoSuchElementException ở menu thu ngân**: ca #1. Luồng tương tác thật bị kẹt khi chọn quay lại; với EOF, batDau bắt ngoại lệ, lưu và thoát.

`Giaodien.batDau` có catch RuntimeException và NoSuchElementException, nên các lỗi trên thường làm văng tác vụ/quay về đăng nhập hoặc thoát có lưu, không nhất thiết làm sập JVM. Gọi trực tiếp phương thức không có catch bên ngoài sẽ nhận ngoại lệ. Chưa có bằng chứng thực thi về crash toàn bộ chương trình từ đầu vào UI hợp lệ thông thường.

## Lỗi nghiêm trọng xác định thêm qua đọc mã

- **Lưu giao dịch qua nhiều file không có tính nguyên vẹn**: Giaodien.java:1116 ghi hóa đơn, kho, điểm bằng 3 lệnh riêng; các hàm ghi thường chỉ in lỗi và không trả thành công/thất bại. Lỗi I/O hoặc dừng tiến trình giữa chừng có thể lưu đơn mà chưa trừ kho/lưu điểm. Chưa mô phỏng dừng tiến trình hoặc hỏng đĩa.
- **Doanh thu bỏ qua giảm giá**: QuanLyCuaHang.java:374 cộng tinhTongTien (tiền hàng trước giảm), không trừ khuyến mãi và điểm đã dùng. Doanh thu có thể cao hơn thực tế; báo cáo mang tên doanh thu/lợi nhuận cũng không tính giá vốn hàng đã bán. Cần thống nhất định nghĩa nghiệp vụ khi sửa.
- **Không khóa hóa đơn hoàn tất ở các API phục hồi**: Hoadonban.themCTHDTuFile/khoiPhucThanhToanTuFile công khai và không kiểm tra trạng thái; gọi ngoài loader sửa được hóa đơn đã hoàn tất. Chưa có đường UI thông thường được chứng minh khai thác các hàm này.

## Tái chạy

Chạy javac từ thư mục gốc để biên dịch src/, audit/ và ReviewProbe.java vào `.audit/review-current/classes`. Chạy Java với working directory `.audit/review-current/run`, classpath `../classes`. Phải dùng dữ liệu sao chép vì các hàm themSP/them/ghiFile tự ghi file tương đối `data/`.

Tệp bằng chứng: `audit-results.txt`, `review-results.txt`; mã ca bổ sung: `ReviewProbe.java`. Không sửa ba bộ probe có sẵn. Nên ưu tiên sửa #2–#5, #8–#11 và menu #1 trước.
