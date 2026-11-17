# Tích hợp khuyến mãi và hóa đơn

Nguồn tham khảo: [Phatmap2246/java-oop-mini-supermarket](https://github.com/Phatmap2246/java-oop-mini-supermarket), bản tải về tại commit `ec90363009458da0053095c69a31d7bf17f92eda`.
Phần khuyến mãi của nguồn nằm trong `src/dichvu`; phần hóa đơn nằm trong `src/hoadon`.

## Các chức năng được điều chỉnh cho dự án

| Nguồn tham khảo | Tích hợp vào code hiện tại | Cách hoạt động |
| --- | --- | --- |
| `Khuyenmai.getMucChiToiThieu` và điều kiện áp dụng trong `CTHoaDonBan` | `Khuyenmai.duMucChiToiThieu`, `CTHDBan.apDungKhuyenMai` | Xét tiền hàng của dòng sản phẩm trước giảm và VAT. Chưa đủ mức chi thì xét khuyến mãi khác. |
| `KhuyenMaiPhanTram.mucGiamToiDa` | `KhuyenmaiPhantram.tinhTongTienGiam` | Giới hạn tiền giảm trên **cả dòng**, sau khi tính phần trăm. Giá trị 0 nghĩa là không giới hạn. |
| `Danhsachkhuyenmai.timKiemNhieuKhoa`, `thongKeTrangThaiKhuyenMai` | `DSKhuyenMai.timKiemTheoTuKhoa`, `locTheoTrangThai`, `thongKeTrangThai` | Tìm theo mã hoặc loại; lọc đang áp dụng, hết hạn, chưa bắt đầu. Cả ngày bắt đầu và ngày kết thúc đều có hiệu lực. |
| `DanhSachSPKM.tuDongGiamGiaCanHSD` | `DSSanPhamKhuyenMai.tuDongGiamGiaCanHSD` | Thực phẩm đã duyệt, còn tồn và còn 0–3 ngày: giảm 50%; 4–7 ngày: 30%; 8–15 ngày: 15%. Không sửa giá gốc hay tồn kho. |
| Tra cứu theo thời gian/đối tác/tổng giá trong hai danh sách hóa đơn | `DSHoadon.traCuuTheoKhoangNgay`, `timTheoDoiTac`, `locTheoTongTien`, `locChuaXuLy` | Trả về danh sách kết quả để dùng lại; menu in kết quả bằng bảng tự căn cột. |
| `DanhSachHoaDonBan.topNSanPham` | `DSHoadon.topNSanPham` | Chỉ lấy hóa đơn bán hoàn tất. Xếp theo số lượng bán, rồi tiền hàng sau giảm, rồi mã SP để kết quả ổn định. |
| `HoaDon.tuDongFile` và kiểm tra bản ghi của nguồn | `HoaDon.tuDongFile`, `kiemTraDuLieuFile` và các hàm đọc chi tiết | Kiểm tra loại, số cột, ngày, trạng thái, số tiền và liên kết cha–con. Giữ nguyên ưu đãi lịch sử, không tính lại khuyến mãi hoặc cập nhật kho khi đọc. |
| Đọc danh sách vào vùng tạm trước khi thay dữ liệu đang dùng | `DSHoadon.docFile`, `DSKhuyenMai.docFile`, `DSSanPhamKhuyenMai.docFile` | Gặp lỗi thì giữ danh sách cũ, báo dòng lỗi và dùng cơ chế bảo vệ file có sẵn của `Ktradulieu`. |
| Lưu và khôi phục thanh toán, in tiền nhận/thối | `HoaDon.tuDongFile`, `Hoadonban.inHoaDon` | Khôi phục cả các cột tiền khách đưa, mã GD, người chuyển, tài khoản, ngân hàng vốn đã có trong file của dự án. |

## Những quy tắc hiện tại được giữ

- Giữ package `khuyenmai`, các lớp `HoaDon`, `Hoadonban`, `HoadonNhap`, các tên hàm cũ, mảng/danh sách hiện có và hệ thống `BangConsole`.
- Phần trăm vẫn nhập dạng **20 = 20%**; không chuyển sang tỷ lệ 0.2 của nguồn tham khảo. Giữ cách làm tròn `HALF_UP` trên một đơn vị trước khi tính tổng giảm.
- Số lượng mua là số lượng trả tiền; hàng tặng được cộng riêng khi xuất kho. Không dùng quy ước tổng số lượng gồm quà tặng của nguồn.
- Giảm trực tiếp vẫn tính theo mỗi sản phẩm. Một sản phẩm có thể liên kết nhiều khuyến mãi; chỉ chọn một ưu đãi tốt nhất theo tiền giảm/giá trị quà thực tế còn trong kho.
- Trạng thái vẫn dùng `DangXuLy`, `DaXuLyThanhCong`, `DaHuy`. Đơn đã hủy không được sửa, xác nhận hoặc đưa vào danh sách chờ duyệt. Giữ kiểm tra quyền và quy trình duyệt nhập kho hiện tại.
- Các lô nhập cùng mã nhưng khác giá, VAT hoặc hạn sử dụng được giữ riêng.
- Giảm cận hạn được gọi từ menu quản lý. Gọi lặp không tạo mã trùng; lần sau cập nhật mức giảm theo hạn còn lại. Giữ chương trình thủ công đang chạy hoặc chưa bắt đầu. Tên `Giảm cận HSD tự động` dùng để nhận diện mã tự động qua các lần lưu/đọc.
- Số lượng trong Top N không cộng hàng tặng. Cột tiền hàng sau giảm chưa gồm VAT và chưa trừ điểm thưởng trên toàn hóa đơn.

## Cách sử dụng

- **Quản lý khuyến mãi → Cập nhật khuyến mãi**: mục 4 sửa mức chi tối thiểu, mục 5 sửa mức giảm tối đa cho chương trình phần trăm.
- **Quản lý khuyến mãi → 9**: lọc trạng thái; **→ 10**: tạo/cập nhật khuyến mãi thực phẩm cận hạn.
- **Quản lý hóa đơn bán → 3**: tra cứu nâng cao; **→ 4**: Top N.
- **Quản lý nhập kho → 5**: tra cứu nâng cao hóa đơn nhập.
- **Báo cáo của quản lý/giám đốc → 7**: Top N sản phẩm bán chạy.

### Lập, chỉnh sửa và thanh toán hóa đơn bán/nhập

Sau khi nhập thông tin và các mặt hàng, chương trình hiển thị bảng xem lại cùng menu:

| Phím | Thao tác |
| --- | --- |
| 1 | Thêm mặt hàng. |
| 2 | Sửa số lượng bán; với hóa đơn nhập có thể sửa cả đơn giá, VAT và hạn sử dụng. |
| 3 | Xóa mặt hàng. |
| 4 | Đổi khách hàng hoặc nhà cung cấp. |
| 5 | Sửa ghi chú. |
| 6 | Thanh toán và in hóa đơn. |
| 0 | Hủy lập hóa đơn. |

Có thể chỉnh sửa nhiều lần; tổng tiền, VAT và ưu đãi bán được tính theo dữ liệu sau cùng. Hóa đơn nhập chọn lô bằng STT để phân biệt các dòng cùng mã sản phẩm nhưng khác giá/VAT/hạn sử dụng. Bản nháp đã hủy không được thêm vào danh sách hóa đơn.

Khi chọn thanh toán:

- Chọn **0** ở menu phương thức để quay lại sửa.
- **Tiền mặt**: nhập tổng tiền khách đưa (bán) hoặc cửa hàng chi (nhập); thiếu tiền thì nhập lại, nhập 0 để hủy lần thanh toán. Hóa đơn 0 đồng cần xác nhận riêng. Chương trình tính tiền thối/tiền nhà cung cấp trả lại.
- **Chuyển khoản**: nhập ngân hàng, tài khoản, người chuyển/người nhận và mã giao dịch, sau đó xác nhận đã nhận/đã chuyển tiền. Chưa xác nhận thì quay về bước sửa. Đây là xác nhận thủ công tại console, không kết nối kiểm tra giao dịch ngân hàng thực tế.
- Khi thanh toán thành công, chương trình **in ngay hóa đơn** và khóa thêm/xóa/sửa/thanh toán lại. Hóa đơn bán cập nhật tồn kho và điểm đúng một lần.
- Hóa đơn nhập lưu trạng thái **đã thanh toán** độc lập với **chờ duyệt nhập kho**. Chỉ người có quyền duyệt mới cập nhật hàng vào kho; trả tiền không tự cộng tồn. Trạng thái thanh toán và chi tiết tiền mặt/chuyển khoản được giữ sau khi lưu và mở lại chương trình.

Quy trình dùng chung trong `Hoadonban.lapHoaDon` và `HoadonNhap.lapHoaDon`, được gọi từ cả giao diện và `DSHoadon.lapHoaDonMoi`.

## Tương thích file và bảo vệ dữ liệu

File cũ vẫn đọc được. Giữ nguyên thứ tự các cột hiện tại; chỉ thêm điều kiện ở cuối dòng khi cần:

```text
MUATANG;ma;loai;batDau;ketThuc;mua;tang[;mucChiToiThieu]
TRUCTIEP;ma;loai;batDau;ketThuc;soTienGiam[;mucChiToiThieu]
PHANTRAM;ma;loai;batDau;ketThuc;phanTram[;mucChiToiThieu;mucGiamToiDa]
```

Hóa đơn bán chấp nhận 13 cột cũ hoặc 18 cột có thanh toán mở rộng. Hóa đơn nhập đọc được 11 cột cũ hoặc 17 cột mới; giữ 11 cột đầu và nối thêm `trangThaiThanhToan;tienDaChi;maGD;tenNguoiNhan;soTK;tenNganHang`. Hồ sơ `SPMOI` giữ định dạng hiện có. Với dòng nhập 11 cột cũ có phương thức thanh toán, xem là đã trả tiền vì quy trình cũ thanh toán trước khi chờ duyệt kho.
Đọc lỗi giữ danh sách cũ; file lỗi được bảo vệ, dữ liệu mới ghi vào `.moi` theo cơ chế hiện tại.

Ghi hóa đơn đóng file tạm trước khi thay file chính. Mỗi lần dùng một file tạm riêng để giữ nguyên `.tmp` còn sót từ phiên trước. File `data/hoadon.txt.tmp` đã có từ trước lần tích hợp được giữ nguyên, không tự nhập vào danh sách chính.

## Kiểm tra

Chạy ở thư mục gốc dự án trong PowerShell:

```powershell
New-Item -ItemType Directory -Force out/test | Out-Null
$nguon = @(Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName)
javac -encoding UTF-8 -d out/test $nguon test/TichHopKhuyenMaiHoaDonTest.java test/QuyTrinhThanhToanHoaDonTest.java
java -cp out/test TichHopKhuyenMaiHoaDonTest
java -cp out/test QuyTrinhThanhToanHoaDonTest
```

Bài kiểm tra dùng file trong thư mục tạm của hệ điều hành; không thêm sản phẩm vào danh sách đang dùng của ứng dụng. Các trường hợp gồm: mức chi/giảm tối đa, chọn ưu đãi theo tồn kho, ngày biên, giảm cận hạn gọi lặp và qua mốc ngày, tương thích file cũ/mới, liên kết chi tiết, trạng thái hủy, Top N, khôi phục thanh toán và giữ file cũ khi đọc/ghi thất bại.

`QuyTrinhThanhToanHoaDonTest` mô phỏng bàn phím cho cả bán/nhập: thêm–sửa–xóa, đổi đối tác, hủy và thử thanh toán lại, nhập thiếu tiền, xác nhận chuyển khoản, hết đầu vào, khóa hóa đơn đã trả tiền, in đúng một hóa đơn sau thành công, lưu/đọc trạng thái nhập chờ duyệt, và duyệt phiếu nhập có hồ sơ sản phẩm từ định dạng file cũ (không duyệt riêng sản phẩm). Kho thử vô hiệu hóa ghi file tự động để không đụng tới dữ liệu thật.


Sản phẩm mới trong danh mục hiện được tạo với tồn 0, không có trạng thái duyệt. Bán hàng và khuyến mãi kiểm tra theo lượng tồn, HSD và cờ xóa. Chi tiết: [SAN_PHAM_VA_TON_KHO.md](SAN_PHAM_VA_TON_KHO.md).


## Cập nhật nhập hàng theo NCC

Sản phẩm có thêm `maNCC` và cột hiển thị NCC. Dữ liệu cũ thiếu NCC được chọn một NCC đang hợp tác khi nhập thêm hàng lần đầu. Menu Kho → `1` nay mở nhập hàng theo đề xuất/thêm mới/sửa danh sách rồi gom thành từng phiếu theo NCC; không phải nhập mã phiếu và chọn NCC trước như luồng cũ. Từng phiếu phải thanh toán thành công mới in/lưu hóa đơn, sau đó chờ duyệt để cộng kho. Menu chỉnh sửa có `6` thanh toán và `7` xem danh sách mặt hàng. Xem [NHAP_HANG_THEO_NHA_CUNG_CAP.md](NHAP_HANG_THEO_NHA_CUNG_CAP.md) để dùng luồng hiện tại.


## Cập nhật nhập bổ sung không theo lô

Luồng mới chỉ hỏi số lượng và giá nhập. Cùng mã sản phẩm được cộng dồn một dòng, lấy đơn giá/VAT lần nhập cuối. Khi duyệt phiếu, NSX mới bằng ngày duyệt; HSD mới bằng NSX mới cộng số ngày sử dụng đã có (`HSD cũ - NSX cũ`). Ngày áp dụng chung cho sản phẩm; không giữ hạn sớm nhất giữa các lô. Lịch sử hóa đơn cũ vẫn được đọc nguyên các dòng để bảo toàn tiền hàng. Danh sách sản phẩm bổ sung NSX và HSD. Xem [SAN_PHAM_VA_TON_KHO.md](SAN_PHAM_VA_TON_KHO.md).
