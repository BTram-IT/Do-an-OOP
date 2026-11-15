# Hướng dẫn bộ dữ liệu test đồ án

Mốc dữ liệu: **2026-11-15**. Dữ liệu giả lập bằng tiếng Việt, UTF-8, đúng định dạng các lớp đọc file hiện tại. Bộ này đã được cài vào `data/`; không cần import thủ công hay thay mã nguồn.

## 1. Số lượng và vị trí

| Nội dung | Số lượng | File đang được chương trình đọc |
|---|---:|---|
| Sản phẩm | **100**, gồm 50 thức ăn + 50 đồ uống | `data/sanpham.txt` |
| Nhà cung cấp | **10**, gồm 8 hoạt động + 2 đã xóa | `data/nhacungcap.txt` |
| Hóa đơn bán | **120** | `data/hoadon.txt`, dòng `HDBAN` |
| Hóa đơn nhập | **20** | `data/hoadon.txt`, dòng `HDNHAP` |
| Chi tiết bán | **377** | Dòng `CTBAN` trong `hoadon.txt` |
| Chi tiết nhập | **93** | Dòng `CTNHAP` trong `hoadon.txt` |
| Khách hàng | **60**, gồm 55 hoạt động + 5 đã xóa | `data/khachhang.txt` |
| Tài khoản | **15**: 10 nhân viên, 3 quản lý, 2 giám đốc | `data/nhanvien.txt` |
| Khuyến mãi | **18**: 6 mua tặng, 6 phần trăm, 6 trực tiếp | `data/khuyenmai.txt` |
| Liên kết sản phẩm–khuyến mãi | **120** | `data/sanphamkhuyenmai.txt` |

`hoadonban.txt`, `hoadonnhap.txt` và `doitac.txt` cũ được giữ nguyên. Luồng hiện tại trong `Giaodien.docDuLieu()` dùng `hoadon.txt` và `nhacungcap.txt`, không dùng ba file cũ này.

Trong phiếu nhập có thêm **5 hồ sơ sản phẩm mới nhúng bằng `SPMOI`**, chưa nằm trong 100 sản phẩm danh mục. Bốn hồ sơ ở phiếu chờ duyệt, một hồ sơ ở phiếu đã hủy. Chỉ khi duyệt thì sản phẩm mới được đưa vào kho.

## 2. Đăng nhập và cách chạy

Chạy `giaodien.Main` trong IntelliJ; working directory là thư mục gốc có `src/` và `data/`.

**Mật khẩu của toàn bộ tài khoản thử nghiệm: `123456`.**

| Tài khoản | Mã | Vai trò / tình huống |
|---|---|---|
| `nhanvien` | NV001 | Nhân viên hoạt động, vào làm từ 2023 |
| `nhanvien2` | NV002 | Nhân viên mới, vào làm hôm qua |
| `nhanvien3` … `nhanvien8` | NV003 … NV008 | Đang hoạt động, ngày vào làm/lương khác nhau |
| `nhanvien6` | NV006 | Lương cơ bản 0 để thử giá trị biên |
| `nhanvien7`, `nhanvien8` | NV007, NV008 | Có 2 đề xuất sa thải chờ giám đốc xử lý |
| `nhanvien9`, `nhanvien10` | NV009, NV010 | Đã nghỉ việc, đăng nhập phải bị từ chối |
| `quanly`, `quanly2` | QL001, QL002 | Quản lý hoạt động |
| `quanly3` | QL003 | Quản lý đã bị vô hiệu hóa |
| `giamdoc` | GD001 | Giám đốc hoạt động |
| `giamdoc2` | GD002 | Giám đốc đã bị vô hiệu hóa |

Menu nhân viên: `1` kho, `2` thu ngân, `3` đăng xuất. Menu thu ngân: `1` khách hàng, `2` lập hóa đơn, `3` quản lý hóa đơn bán, `4` khuyến mãi, `0` quay lại. Quản lý có menu danh mục/báo cáo; giám đốc có menu nhân sự/báo cáo/duyệt nhập.

## 3. Sản phẩm và khách hàng tiêu biểu

| Mã | Tình huống được chuẩn bị | Kết quả cần kiểm tra |
|---|---|---|
| TA001 | Giá 22.000đ, VAT 5%, mua 2 tặng 1; cạnh tranh với giảm 10% | Mua 2 thì ưu đãi tặng 1 có giá trị lớn hơn; xuất kho 3 |
| TA002 | Giảm trực tiếp 5.000đ / đơn vị | Tổng tiền và VAT tính trên tiền sau giảm |
| TA003, DU003 | HSD 16/11/2026 | Còn hạn ở mốc tạo dữ liệu |
| TA004, DU004 | HSD đúng 15/11/2026 | Ngày HSD vẫn được bán; ngày sau thì hết hạn |
| TA005 | Khuyến mãi trực tiếp 200.000đ, lớn hơn giá bán 50.000đ | Giảm bị chặn bằng tiền hàng; tổng 0đ |
| TA006 | Giảm 100% trong ngày tạo dữ liệu | Tổng 0đ và thanh toán tiền mặt thành công |
| TA007, DU007 | Tồn 1 | Không bán quá 1; không tặng vượt tồn |
| TA008, DU008 | Tồn 0 | Từ chối bán |
| TA009, DU009 | Tồn đúng 10 | Xuất hiện trong báo cáo sắp hết (<=10) |
| TA010 | Giá 100.000đ, VAT 0%, không khuyến mãi | Dễ kiểm tra tích điểm và đổi điểm |
| TA041 … TA045, DU041 … DU045 | 10 sản phẩm chờ duyệt | Không được bán |
| TA046 … TA048, DU046 … DU047 | 5 sản phẩm đã xóa | Không được bán; dữ liệu lịch sử vẫn đọc được |
| TA049, TA050, DU048 … DU050 | 5 sản phẩm đã hết hạn | Kiểm tra trả false và chuyển tồn hết hạn |
| KH001, KH005 | 0 điểm | Không đề nghị sử dụng điểm |
| KH002 | 5 điểm | Thử dùng 2 điểm hoặc nhập vượt 5 |
| KH003 | 300 điểm còn lại sau lịch sử mua | Thử dùng điểm đủ một phần/toàn bộ hóa đơn |
| KH004 | 100 điểm | Thử hóa đơn nhiều dòng |
| KH055 | 2.147.483.647 điểm, chưa xóa | Hóa đơn cộng thêm điểm phải bị chặn trước khi trừ kho |
| KH056 … KH060 | 5 khách đã xóa | Không được nhận làm thành viên/dùng điểm |
| NCC001 … NCC008 | Nhà cung cấp đang hoạt động | Thử thêm/sửa/tìm kiếm và nhập kho |
| NCC009, NCC010 | Nhà cung cấp đã xóa, còn hóa đơn lịch sử | Tra cứu lịch sử và kiểm tra hạn chế tác vụ mới |

Các trường hợp còn hạn/chưa đến ngày khuyến mãi phụ thuộc ngày thực tế chạy Java. Bộ được đánh theo ngày 2026-11-15; nếu chạy ngày khác, kết quả liên quan “hôm nay/ngày mai” sẽ thay đổi đúng theo đồng hồ.

## 4. Hóa đơn bán đã có sẵn

| Dải mã | Số lượng | Trạng thái |
|---|---:|---|
| HDB001 … HDB100 | 100 | DaXuLyThanhCong |
| HDB101 … HDB112 | 12 | DangXuLy |
| HDB113 … HDB120 | 8 | DaHuy |

| Mã | Tình huống |
|---|---|
| HDB001 | 25 dòng sản phẩm: kiểm tra mảng chi tiết vượt sức chứa ban đầu 20 |
| HDB098 | Giảm trực tiếp vượt giá bán, tổng 0đ |
| HDB099 | Hóa đơn 200.000đ đã dùng đủ 200 điểm, trả 0đ; kiểm tra đọc lại tiền điểm |
| HDB100 | Giảm 100%, tổng 0đ |
| HDB101 | Nháp TA001 mua 2, tặng 1, KH002 có 5 điểm |
| HDB102 | Nháp giảm trực tiếp về 0đ |
| HDB103 | Nháp giảm phần trăm về 0đ |
| HDB104 | Nháp TA007 tồn 1 |
| HDB105 | Nháp TA008 hết hàng, thanh toán phải từ chối |
| HDB106 | Nháp TA049 hết hạn, thanh toán phải từ chối |
| HDB107 | Nháp TA041 chờ duyệt, thanh toán phải từ chối |
| HDB108 | Nháp TA046 đã xóa, thanh toán phải từ chối |
| HDB109 | Nháp KH055 đã đạt giới hạn điểm, thêm điểm phải bị chặn |
| HDB110 | Nháp KH056 đã xóa, phải từ chối |
| HDB111 | Nháp KH001 không có điểm, mua TA010 |
| HDB112 | Nháp 25 sản phẩm còn hàng để xem/sửa nhiều dòng |
| HDB113 … HDB120 | Hóa đơn hủy: không thanh toán hoặc chuyển hoàn tất |

Giao diện hiện tại không có nút tiếp tục thanh toán mọi hóa đơn nháp theo mã. Các nháp trên phục vụ gọi hàm nghiệp vụ trực tiếp và xem chi tiết; khi thử bằng menu, lập hóa đơn mới với cùng sản phẩm/khách để tái hiện. Không diễn giải việc file có sẵn nháp thành tính năng tiếp tục đơn mà UI chưa cung cấp.

### Một chuỗi thao tác bán hàng dễ đối chiếu

1. Đăng nhập `nhanvien` / `123456` → Thu ngân (`2`) → Lập hóa đơn (`2`).
2. Nhập `/` sinh mã HDB121, Enter bỏ qua ghi chú.
3. Tiếp nhận khách (`1`) → Thành viên (`1`) → nhập `KH002`.
4. Kiểm tra/điều chỉnh (`2`) → Thêm sản phẩm (`1`) → `TA001` → số lượng `2` → quay lại (`0`).
5. Thanh toán (`3`) → dùng điểm (`1`) → `2` điểm → tiền mặt (`1`) → đưa `50000`.
6. Kỳ vọng: tiền hàng 44.000đ, VAT 2.200đ, trừ điểm 2.000đ, trả **44.200đ**, thối **5.800đ**; kho TA001 giảm 3, KH002 từ 5 còn 3 điểm.

Đặt lại bộ test trước khi lặp chuỗi này để điểm và tồn khớp đúng con số ban đầu.

## 5. Hóa đơn nhập và duyệt sản phẩm mới

| Mã | Tình huống |
|---|---|
| HDN001 … HDN014 | 14 phiếu nhập đã hoàn tất; không cộng kho lần hai khi nạp file |
| HDN015 | Chờ duyệt hàng có sẵn TA001 và DU001, mỗi mã +20 khi duyệt |
| HDN016 | Chờ duyệt thức ăn mới TA051, số lượng 30 |
| HDN017 | Chờ duyệt đồ uống mới DU051, số lượng 30; dùng để thử từ chối |
| HDN018 | Chờ duyệt đồng thời TA052 (+30) và DU052 (+40) |
| HDN019 | Phiếu hàng có sẵn đã hủy, không được duyệt |
| HDN020 | Phiếu đã hủy có hồ sơ mới TA053, không được thêm vào kho |

Đăng nhập quản lý/giám đốc → menu duyệt nhập → nhập mã phiếu → xác nhận duyệt. Với sản phẩm mới còn có bước chọn duyệt từng hồ sơ. Sau khi duyệt HDN016, danh mục tăng từ 100 lên 101 và TA051 có tồn 30; duyệt lại phải bị từ chối. Từ chối HDN017 giữ danh mục 100 nếu thử trên bộ gốc. Duyệt HDN018 trên bộ gốc tăng lên 102.

Mã sản phẩm tự sinh tiếp theo là TA051/DU051, đang được hồ sơ phiếu chờ duyệt sử dụng. Nếu thử thêm sản phẩm thủ công trước khi duyệt, nên chọn TA060/DU060 để không tạo trùng với các hồ sơ này. Mã tự sinh khác: NV011, KH061, NCC011, HDB121, HDN021, KM019.

## 6. Khuyến mãi, nhân sự và báo cáo

- KM001 … KM006: mua tặng. KM007 … KM012: giảm phần trăm. KM013 … KM018: giảm trực tiếp.
- Mỗi nhóm có chương trình đang hiệu lực, hết hạn, bắt đầu ngày mai và chỉ có hiệu lực trong ngày tạo dữ liệu. KM004/KM010/KM016 hết hạn; KM005/KM011/KM017 chưa bắt đầu; KM003/KM009/KM015 có hiệu lực đúng ngày tạo dữ liệu.
- Có sản phẩm gắn nhiều chương trình để kiểm tra chọn ưu đãi có giá trị lớn nhất. TA010 không gắn chương trình để thử giá gốc.
- NV007, NV008 có đề xuất sa thải. Dùng `giamdoc` để thử phê duyệt/từ chối; khôi phục bộ gốc giữa hai nhánh nếu cần.
- Ngày vào làm và lương khác nhau phục vụ thử bảng lương/thâm niên; NV006 có lương cơ bản 0, NV009/NV010 đã nghỉ để kiểm tra báo cáo có lọc trạng thái hay không.
- Hóa đơn trải các mốc 29/02/2024, cuối năm 2025, đầu năm 2026, ranh giới quý I/II/III/IV, đầu/cuối tháng và ngày tạo dữ liệu.

### Số liệu đối chiếu từ hóa đơn hoàn tất

Chỉ tính 100 hóa đơn bán và 14 hóa đơn nhập thành công; không tính nháp/hủy. Tổng khách trả đã gồm VAT và đã trừ điểm: **47.844.328 đ**. Tiền dùng điểm toàn bộ lịch sử: **280.000 đ**. Tổng chi nhập đã gồm VAT: **103.227.600 đ**.

| Năm | HĐ bán thành công | Khách trả, gồm VAT | Thu bán sau trừ VAT | HĐ nhập thành công | Chi nhập, gồm VAT |
|---|---:|---:|---:|---:|---:|
| 2024 | 8 | 7.563.580 đ | 7.213.100 đ | 2 | 15.688.700 đ |
| 2025 | 8 | 4.368.130 đ | 4.117.200 đ | 1 | 5.790.600 đ |
| 2026 | 84 | 35.912.618 đ | 34.176.100 đ | 11 | 81.748.300 đ |

Không gọi các con số này là lợi nhuận đầy đủ: đồ án hiện chưa có mô hình giá vốn từng lô/hàng đã bán. NSX/HSD trong danh mục là thông tin kho hiện tại; không dùng chúng để suy ngược lô sản xuất của hóa đơn lịch sử.

Tồn kho trong file là ảnh chụp **đã phản ánh** các giao dịch hoàn tất. Việc đọc hóa đơn lịch sử không được trừ/cộng kho lần nữa. Bộ tạo đã kiểm tra được tồn đầu kỳ không âm theo công thức: tồn hiện tại = tồn đầu kỳ + nhập hoàn tất − bán/tặng hoàn tất.

## 7. Các nhóm hàm có dữ liệu để thử

| Nhóm | Dữ liệu / thao tác gợi ý |
|---|---|
| Constructor, getter/setter, toString/toFileString | Đủ hai loại sản phẩm, ba vai trò, ba loại KM, hai loại hóa đơn và hai phương thức trả tiền |
| Đọc file, phân tích dòng, tra cứu liên kết | 7 file đầy đủ, 140 header, 470 dòng chi tiết, 120 liên kết |
| Sinh mã, kiểm tra trùng | Các dải mã liên tục; thử nhập lại mã đã có, rồi `/` |
| Thêm/sửa/xóa danh mục | Dùng mã mới TA060/DU060/KH061/NCC011; thử đối tượng hoạt động và đã xóa |
| Nhập liệu chuỗi/số/ngày | Thử Enter rỗng, abc ở trường số, số âm, 0, mã sai tiền tố, SĐT sai và 31/02/2026; dữ liệu file chính không chứa dòng hỏng |
| Thêm/gộp/sửa/xóa chi tiết bán | TA001 thêm hai lần, sửa số lượng, xóa rồi kiểm tra tổng; HDB001/HDB112 có 25 dòng |
| Áp dụng/so sánh khuyến mãi | TA001/TA002/TA005/TA006 và các KM hết hạn/chưa bắt đầu |
| Thanh toán tiền mặt/chuyển khoản | Bộ có lịch sử cả hai; khi lập mới thử tiền thiếu, tiền đủ, số tài khoản/mã giao dịch sai rồi nhập đúng |
| Điểm thưởng | KH001 không điểm, KH002 ít điểm, KH003 đủ điểm, KH055 chạm giới hạn, KH056 đã xóa |
| Kiểm kho/hạn dùng/duyệt | TA007–TA009, TA041/TA046/TA049 và HDN015–HDN020 |
| Quyền và trạng thái | Tài khoản vô hiệu, hóa đơn nháp/hủy/hoàn tất, đề xuất sa thải |
| Thống kê/bảng lương | Ngày, tháng, quý, năm, khoảng ngày; nhân viên thâm niên/nghỉ việc/lương 0 |
| Lưu rồi mở lại | Thử trên bản sao; so lại số lượng và tổng tiền sau khởi động mới |

**Giới hạn:** dữ liệu không tự chứng minh “toàn bộ hàm đều đúng”. Những nhánh không có dòng nhập, Scanner bị đóng, I/O lỗi, dữ liệu hỏng và tràn số cần kiểm thử riêng. Không đưa dòng hỏng/NaN vào file chính vì loader có thể từ chối cả danh sách.

## 8. Kết quả kiểm tra bộ dữ liệu này

- Biên dịch toàn bộ 39 file Java hiện có thành công; mã nguồn không sửa.
- **19 nhóm kiểm tra, 2.192 điều kiện, 0 thất bại** khi nạp/thử trên bản sao: số lượng, liên kết, nhân viên vào làm trước ngày lập, tổng tiền toàn bộ 140 hóa đơn, 25 dòng, thanh toán/điểm/quà tặng, duyệt/từ chối nhập, đề xuất sa thải, hiển thị và các báo cáo.
- Qua `giaodien.Main`: đăng nhập/đăng xuất đủ `nhanvien`, `quanly`, `giamdoc`; nạp đủ 100 sản phẩm và 140 hóa đơn, không có thông báo lỗi/cảnh báo nạp.
- Các lần thử làm thay đổi kho/điểm/trạng thái chỉ chạy trên bản sao tạm, không áp dụng vào bộ `data/` bàn giao.

**Lỗi chương trình đã biết, không phải lỗi bộ dữ liệu:** `DSHoadon.ghiFile()` hiện ghi ra `hoadon.txt.tmp` nhưng thiếu bước thay file chính. Vì vậy dữ liệu ban đầu nạp đúng, nhưng hóa đơn mới sau thao tác UI có thể chưa xuất hiện khi mở lại. Loader cũng chưa phục hồi các cột ngân hàng/tiền khách đưa mở rộng. Hai điểm này cần được tính đến khi kiểm tra hàm lưu/khôi phục; tác vụ tạo dữ liệu không sửa mã nghiệp vụ.

## 9. Đặt lại bộ test và khôi phục dữ liệu cũ

- Bộ test gốc để lặp lại từng ca: `docs/BO_DU_LIEU_TEST_100_SP_140_HD.zip`.
- Dữ liệu cũ trước khi thay: `docs/SAO_LUU_DATA_TRUOC_BO_TEST_20261115_173521.zip`.
- Cả hai ZIP dùng đường dẫn bên trong dạng `data/ten_file.txt`. ZIP bộ test chứa 7 file đang dùng; ZIP sao lưu chứa toàn bộ file dữ liệu cũ.

Thoát chương trình trước khi khôi phục. Mở PowerShell tại thư mục gốc dự án.

Đặt lại đúng bộ test hiện tại:

```powershell
Expand-Archive -LiteralPath 'docs/BO_DU_LIEU_TEST_100_SP_140_HD.zip' -DestinationPath . -Force
```

Khôi phục dữ liệu cũ:

```powershell
Expand-Archive -LiteralPath 'docs/SAO_LUU_DATA_TRUOC_BO_TEST_20261115_173521.zip' -DestinationPath . -Force
```

Không cần giữ thư mục biên dịch hoặc script sinh dữ liệu để chạy đồ án; chỉ cần `src/`, các file `data/` và cấu hình IntelliJ đang có. Các công cụ tạm đã được dọn sau khi kiểm tra.
