# Nhật ký thay đổi và hướng dẫn đồ án

19 file mã nguồn · 41 hàm/constructor · 28 nhóm sửa đổi

Tài liệu ghi lại những chỗ trợ lý đã chỉnh trong cuộc trao đổi, giải thích lý do và chỉ rõ mã đang có. Phần sau hướng dẫn cách chạy, đọc cấu trúc lớp và sử dụng các nghiệp vụ chính.

| Mục | Thông tin |
| --- | --- |
| Mốc đối chiếu | 15/11/2026; số dòng lấy từ bản mã đang có khi lập tài liệu. |
| Điểm bắt đầu | src/giaodien/Main.java → Giaodien → các lớp nghiệp vụ. |
| Trọng tâm | Hoadonban, nhập kho, điểm thưởng, trạng thái và bảo vệ dữ liệu file. |
| Định dạng giữ lại | Các package, lớp kế thừa, mảng/danh sách và file TXT phân cách bằng dấu ;. |
| Tình trạng xác minh | Biên dịch thành công với JDK 26.0.1. Chạy lại 23 nhóm ca bổ sung và 8 nhóm ca ngoại lệ trong thư mục dữ liệu riêng. Có ca chưa đạt; xem phần 4. |

Cách đọc đánh dấu: mã SĐ-xx nhận diện nhóm thay đổi. Nền vàng trong đoạn code chỉ dòng được bổ sung hoặc sửa. Số ở lề là số dòng thật. Nhãn “một phần/chưa hoàn tất” có nghĩa là không được xem vấn đề đó đã giải quyết toàn bộ.

Phạm vi lịch sử: workspace đã có nhiều thay đổi chưa commit trước khi bắt đầu kiểm tra. Danh sách này dựa trên các thao tác sửa đã ghi trong cuộc trao đổi và đối chiếu lại mã hiện tại; không gán toàn bộ git diff cho trợ lý. Nội dung “trước” là mô tả logic, không phải bản diff từng byte của toàn dự án.

Trong lần tạo tài liệu này, chỉ thêm tài liệu và tệp kiểm tra trong thư mục riêng. Không chèn bình luận đánh dấu vào mã nghiệp vụ.

## 1. Bản đồ các vị trí đã sửa

| File | Hàm / vị trí hiện tại |
| --- | --- |
| src/hoadon/Hoadonban.java | themCTHD() — dòng 57kiemTraTonKhoTruocThanhToan() — dòng 140suaSoLuongSanPham() — dòng 172xoaSanPham() — dòng 246tiepNhanKhachHang() — dòng 418themSanPham() — dòng 463thanhToanHoaDon() — dòng 490hoanTatDonHang() — dòng 636themCTHDTuFile() — dòng 705khoiPhucThanhToanTuFile() — dòng 721khoiPhucChiTietThanhToanTuFile() — dòng 784 |
| src/hoadon/HoaDon.java | xacNhanXuLy() — dòng 234 |
| src/hoadon/HoadonNhap.java | themCTHD() — dòng 57duyetNhapKho() — dòng 387 |
| src/hoadon/CTHDBan.java | apDungKhuyenMai() — dòng 133 |
| src/hoadon/CTHDonNhap.java | toDongFile() — dòng 330tuDongFile() — dòng 360 |
| src/hoadon/CTHoaDon.java | setDonGia() — dòng 84 |
| src/hoadon/DSHoadon.java | docFile() — dòng 504ghiFile() — dòng 449 |
| src/sanpham/SanPham.java | setDonGiaNhap() — dòng 80setDonGiaBan() — dòng 91setPhanTramThue() — dòng 124kiemTraHanSuDung() — dòng 191congTonKho() — dòng 211 |
| src/sanpham/DSSanPham.java | ghiFile() — dòng 417 |
| src/person/Nguoidung.java | Nguoidung() — dòng 27setTaiKhoan() — dòng 131setMatKhau() — dòng 145 |
| src/person/NhanVien.java | setLuongCoBan() — dòng 57 |
| src/person/Khachhang.java | congDiem() — dòng 144 |
| src/person/DsKhachHang.java | docFile() — dòng 177ghiFile() — dòng 161 |
| src/doitac/DSNhaCungCap.java | docFile() — dòng 188ghiFile() — dòng 173 |
| src/person/QuanLyCuaHang.java | thongKeSanPhamBanDuoc() — dòng 162tinhToanBaoCao() — dòng 364 |
| src/giaodien/Giaodien.java | menuThuNganTiepNhanKhachHang() — dòng 320 |
| src/thanhtoan/ThanhToan.java | setSoTien() — dòng 90 |
| src/thanhtoan/TienMat.java | qTrinhThanhToan() — dòng 56 |
| src/thanhtoan/ChuyenKhoan.java | qTrinhThanhToan() — dòng 87 |

Số dòng có thể thay đổi khi bạn sửa tiếp. Tra theo tên hàm nếu vị trí không còn khớp. Bản kê JSON đi kèm ghi SHA-256 từng file để nhận biết đúng phiên bản được mô tả.

## 2. Những thay đổi đã chủ động giữ nhỏ

Không chuyển đồ án sang framework, cơ sở dữ liệu hay hệ thống quản lý lô. Các sửa đổi chủ yếu thêm điều kiện kiểm tra, gọi lại setter đã có, dùng phép cộng có kiểm tra tràn và ghi file qua file tạm.

| Giữ cách cũ | Bổ sung tại chỗ |
| --- | --- |
| Trạng thái lưu bằng String | Thêm kiểm tra isDaHuy tại các lối xử lý. Chưa chuyển sang enum. |
| Tiền vẫn dùng double | Chặn NaN/vô cực tại một số setter và trước thu tiền; đổi phép nhân điểm sang *1000.0. |
| Một sản phẩm / một cặp NSX–HSD | Duyệt nhập giữ hạn sớm hơn khi còn hàng cũ; chưa có danh sách từng lô. |
| Ghi file TXT theo từng danh sách | Đưa một số writer qua .tmp và dùng hàm thay file sẵn có. Lưu nhiều file cùng giao dịch chưa hoàn tất. |
| Constructor rỗng để nhập từng bước | Constructor Nguoidung đầy đủ gọi setter; không bỏ cách tạo rỗng. |

## 3. Nhật ký theo file và hàm

### src/hoadon/Hoadonban.java

### SĐ-01 — src/hoadon/Hoadonban.java — ĐÃ ÁP DỤNG

themCTHD(): dòng 57–136

Trước: Chỉ chặn hóa đơn hoàn tất; chưa chặn hóa đơn đã hủy, sản phẩm đã xóa/chưa duyệt. Khi gộp số lượng, sửa ngay dòng cũ.

Đã đổi: Chặn thêm vào hóa đơn đã hủy; yêu cầu sản phẩm chưa xóa, trạng thái Đã duyệt và còn hạn. Khi gộp, tính trên CTHDBan capNhat rồi mới thay dòng cũ.

Lý do: Giữ đúng vòng đời hóa đơn, tránh bán hàng chưa được phép và tránh thay đổi dòng cũ nếu bước cập nhật không hoàn tất.

```java
106: 
107: CTHDBan capNhat = new CTHDBan(ctCu);
108: capNhat.setSoLuongBan((int) slMoi);
```

```java
83: 
84: if (sp.isDaXoa() || !"\u0110\u00e3 duy\u1ec7t".equalsIgnoreCase(sp.getTrangThaiDuyet()) || !sp.kiemTraHanSuDung()) {
85:     System.out.println("[Lỗi] Sản phẩm đã hết hạn sử dụng!");
```

### SĐ-02 — src/hoadon/Hoadonban.java — ĐÃ ÁP DỤNG

kiemTraTonKhoTruocThanhToan(): dòng 140–170

Trước: Kiểm tra tồn từng dòng riêng; các dòng trùng mã nạp qua API có thể cùng đạt kiểm tra dù tổng vượt kho.

Đã đổi: Cộng số mua và số tặng của tất cả dòng cùng mã bằng long; so tổng với tồn thực tế. Bổ sung kiểm tra xóa/duyệt/còn hạn.

Lý do: Ngăn trừ được dòng đầu rồi lỗi ở dòng sau khi nhiều dòng dùng chung một sản phẩm.

```java
158: }
159: long total = 0;
160: for (int j = 0; j < soLuongCT; j++) {
```

```java
163: }
164: if (total > sp.getSoLuongTon()) {
165:     System.out.println("[Lỗi] Không đủ tồn kho cho sản phẩm " + ct.getMaSP() + " (Tồn: " + sp.getSoLuongTon() + ")");
```

### SĐ-03 — src/hoadon/Hoadonban.java — ĐÃ ÁP DỤNG

suaSoLuongSanPham(): dòng 172–244; xoaSanPham(): dòng 246–296

Trước: Chưa chặn hóa đơn đã hủy và Scanner null; sửa số lượng truy cập dsSP khi có thể null.

Đã đổi: Trả về khi Scanner null; chặn hóa đơn đã hủy. Sửa số lượng kiểm tra dsSP trước tìm kiếm. Hai hàm còn dùng hasNextLine để bắt Scanner đóng.

Lý do: Giảm NPE và không cho chỉnh hóa đơn đã hủy.

```java
207: CTHDBan ct = this.dsChiTiet[viTri];
208: if (dsSP == null) return;
209: SanPham sp = dsSP.timKiem(maSP);
```

```java
174: if (sc == null) { return; }
175: try { sc.hasNextLine(); } catch (IllegalStateException e) { return; }
176: 
```

Giới hạn cần nhớ: hasNextLine có thể chờ nhập trước khi kiểm tra trạng thái. EOF vẫn được batDau bắt ở tầng giao diện.

### SĐ-04 — src/hoadon/Hoadonban.java — ĐÃ ÁP DỤNG MỘT PHẦN

tiepNhanKhachHang(): dòng 418–461; themSanPham(): dòng 463–488

Trước: Tiếp nhận khách chỉ xét tồn tại; Scanner null có thể phát sinh lỗi. Thêm sản phẩm chỉ chặn hóa đơn hoàn tất.

Đã đổi: Kiểm tra Scanner null; từ chối khách đã xóa. themSanPham chặn cả hóa đơn đã hủy.

Lý do: Giữ quyền sử dụng thành viên và ngăn thêm hàng vào giao dịch đã hủy.

```java
445: 
446: if (khachHang == null || khachHang.isDaXoa()) {
447:     System.out.println("[Lỗi] Không tìm thấy khách hàng! Vui lòng nhập lại.");
```

Giới hạn cần nhớ: Kiểm tra Scanner đóng đã bị bỏ ở ba hàm boolean trong lần chỉnh sau. Ca Scanner đóng ở tiepNhanKhachHang hiện vẫn ném IllegalStateException.

### SĐ-05 — src/hoadon/Hoadonban.java — ĐÃ ÁP DỤNG

thanhToanHoaDon(): dòng 490–634

Trước: Có lúc mã đang sửa chứa if(this.trangThai), gây lỗi biên dịch. Phép đổi điểm dùng int*int; chưa kiểm tra thu ngân, khách bị xóa, tổng không hữu hạn và giới hạn điểm sau giao dịch.

Đã đổi: Bỏ điều kiện String trong if; chặn hóa đơn hủy, Scanner null, thu ngân thiếu mã/nghỉ việc và khách không hợp lệ. Đổi tiền điểm bằng *1000.0; kiểm tra tổng hữu hạn và điểm sau mua trước khi nhận tiền.

Lý do: Sửa lỗi không chạy được, tràn tiền giảm, sử dụng thành viên bị xóa và tràn điểm sau khi đã nhận tiền.

```java
557: this.diemDung = diemMuonDung;
558: this.tienDungDiem = diemMuonDung * 1000.0;
559: 
```

```java
571: if (khachHang != null) {
572:     long remaining = (long) khachHang.getDiemTichLuy() - (long) diemDung;
573:     if (remaining < 0 || Math.floor(phaiTra / 100000) > Integer.MAX_VALUE - remaining) return false;
```

```java
510: 
511: if (nhanVienDangNhap == null || nhanVienDangNhap.isDaXoa() || nhanVienDangNhap.getMaND() == null || nhanVienDangNhap.getMaND().isBlank()) return false;
512: Khachhang khachHang = null;
```

Giới hạn cần nhớ: Kiểm tra điểm trước thanh toán bảo vệ luồng đơn luồng hiện tại; chưa có giao dịch/khóa dữ liệu nếu trạng thái bị thay đổi trong lúc thanh toán.

### SĐ-06 — src/hoadon/Hoadonban.java — ĐÃ ÁP DỤNG MỘT PHẦN

hoanTatDonHang(): dòng 636–693

Trước: Chỉ chặn đã hoàn tất; bỏ qua kết quả boolean trả về của truDiem.

Đã đổi: Chặn hóa đơn đã hủy; kiểm tra kết quả truDiem và ném IllegalStateException nếu không trừ được.

Lý do: Không âm thầm hoàn tất hóa đơn khi việc trừ điểm thất bại.

```java
667: if (this.diemDung > 0) {
668:     if (!khachHang.truDiem((int) this.diemDung)) throw new IllegalStateException("Insufficient loyalty points");
669: }
```

Giới hạn cần nhớ: Thứ tự hiện tại vẫn trừ kho trước trừ điểm. Chưa rollback nếu dữ liệu điểm thay đổi sau kiểm tra ban đầu.

### SĐ-07 — src/hoadon/Hoadonban.java — ĐÃ ÁP DỤNG

themCTHDTuFile(): dòng 705–715

Trước: Biểu thức dsChiTiet[soLuongCT++] = new CTHDBan(ct) có thể tăng bộ đếm trước khi constructor sao chép ném lỗi.

Đã đổi: Tạo banSao thành công trước, sau đó gán vào mảng và tăng soLuongCT.

Lý do: Không để lỗ null trong vùng chi tiết đang được duyệt khi nạp chi tiết không hợp lệ.

```java
711: }
712: CTHDBan banSao = new CTHDBan(ct);
713: this.dsChiTiet[this.soLuongCT++] = banSao;
```

### SĐ-08 — src/hoadon/Hoadonban.java — ĐÃ ÁP DỤNG MỘT PHẦN

khoiPhucThanhToanTuFile(): dòng 721–736; khoiPhucChiTietThanhToanTuFile(): dòng 784–801

Trước: Giá trị điểm/tiền phục hồi chưa được kiểm tra hữu hạn; thông tin ngân hàng null bị gọi isEmpty trực tiếp.

Đã đổi: Chặn tiền âm, NaN và vô cực trước gán. Kiểm tra từng chuỗi ngân hàng khác null trước isEmpty.

Lý do: Tránh dữ liệu tiền không thể tính toán và NPE khi đọc trường tùy chọn.

```java
721: public void khoiPhucThanhToanTuFile(double tienDungDiem, double soTienThanhToan, String loaiThanhToan) {
722:     if (!Double.isFinite(tienDungDiem) || tienDungDiem < 0 || !Double.isFinite(soTienThanhToan) || soTienThanhToan < 0) throw new IllegalArgumentException("Invalid payment values");
723:     this.tienDungDiem = tienDungDiem;
```

```java
792: ChuyenKhoan ck = (ChuyenKhoan) this.phuongThucThanhToan;
793: if (maGD != null && !maGD.isEmpty()) ck.setMaGDNganHang(maGD);
794: if (tenNguoi != null && !tenNguoi.isEmpty()) ck.setTenNguoiChuyen(tenNguoi);
```

Giới hạn cần nhớ: API phục hồi vẫn public và chưa giới hạn chỉ cho bộ đọc file; chưa khóa việc gọi lại trên hóa đơn đã hoàn tất.

### src/hoadon/HoaDon.java

### SĐ-09 — src/hoadon/HoaDon.java — ĐÃ ÁP DỤNG

xacNhanXuLy(): dòng 234–241

Trước: Có thể gán trực tiếp trạng thái hoàn tất cho hóa đơn đã hủy.

Đã đổi: Thêm isDaHuy() và ném IllegalStateException trước khi đổi trạng thái.

Lý do: Chặn chuyển DaHuy sang DaXuLyThanhCong ở lớp cha.

```java
238: 
239: if (isDaHuy()) throw new IllegalStateException("H?a ??n ?? h?y!");
240: this.trangThai = "DaXuLyThanhCong";
```

Giới hạn cần nhớ: Trạng thái vẫn là String; không chuyển sang enum. Một thông báo mới hiện có ký tự dấu hỏi do mã hóa, cần sửa câu chữ khi dọn code.

### src/hoadon/HoadonNhap.java

### SĐ-10 — src/hoadon/HoadonNhap.java — ĐÃ ÁP DỤNG

themCTHD(): dòng 57–131; duyetNhapKho(): dòng 387–637

Trước: Chưa chặn hóa đơn đã hủy; kiểm tra giá &lt;=0 không bắt NaN. Duyệt nhập tự lấy thời hạn cũ rồi gán NSX bằng ngày lập.

Đã đổi: Chặn DaHuy, kiểm tra giá hữu hạn. Kiểm tra ngày của mọi chi tiết trước thêm sản phẩm/cộng kho. Lấy HSD từ chi tiết; nếu còn tồn cũ thì giữ hạn sớm hơn và giữ NSX cũ.

Lý do: Tránh duyệt phiếu hủy, đưa NaN vào kho, tự kéo dài hạn hàng cũ và lỗi ngày xảy ra sau khi đã cộng kho.

```java
573:     if (sp == null || sp.getNsx() == null || sp.getHsd() == null || ct.getHanSuDung() == null
574:             || ct.getHanSuDung().isBefore(sp.getNsx()) || ct.getHanSuDung().isBefore(LocalDate.now())) return false;
575: }
```

```java
622: sp.setDonGiaNhap(ct.getDonGiaNhap());
623: LocalDate hanMoi = ct.getHanSuDung();
624: if (sp.getSoLuongTon() > ct.getSoLuongNhap() && sp.getHsd().isBefore(hanMoi)) hanMoi = sp.getHsd();
```

Giới hạn cần nhớ: Đây là cách giữ hạn thận trọng cho một bản ghi mỗi mã sản phẩm, chưa phải quản lý lô. Hàng cũ hết hạn còn tồn có thể làm lô nhập mới giữ hạn cũ; giá vốn vẫn bị thay bằng giá nhập gần nhất.

### src/hoadon/CTHDBan.java

### SĐ-11 — src/hoadon/CTHDBan.java — ĐÃ ÁP DỤNG

apDungKhuyenMai(): dòng 133–214

Trước: Một khuyến mãi có phép tính vượt giới hạn có thể làm văng toàn bộ tác vụ thêm/sửa sản phẩm.

Đã đổi: Bắt IllegalArgumentException và ArithmeticException theo từng khuyến mãi ứng viên; bỏ qua ứng viên lỗi và xét chương trình tiếp theo.

Lý do: Giữ được giao dịch khi một cấu hình khuyến mãi quá lớn không tính được.

```java
179: 
180: } catch (IllegalArgumentException | ArithmeticException e) {
181:     continue; // Ignore a promotion whose arithmetic exceeds supported limits.
```

Giới hạn cần nhớ: Hiện bỏ qua ứng viên mà chưa in lý do; số học tiền hàng ngoài khuyến mãi vẫn có thể NaN khi đầu vào cực lớn.

### src/hoadon/CTHDonNhap.java

### SĐ-12 — src/hoadon/CTHDonNhap.java — ĐÃ ÁP DỤNG

toDongFile(): dòng 330–358; tuDongFile(): dòng 360–454

Trước: String.format dùng locale hệ thống, có thể ghi VAT 5,00 mà parser không đọc được. Parser gán thẳng số lượng/giá/thuế.

Đã đổi: Cố định Locale.US khi ghi; đọc số lượng, giá, VAT qua setter kiểm tra dữ liệu.

Lý do: Đọc lại được file bất kể máy dùng dấu chấm hay dấu phẩy; từ chối số lượng âm, giá NaN và VAT ngoài 0..100.

```java
333: 
334: String dong = String.format(java.util.Locale.US,
335:         "%s;%s;%s;%d;%.0f;%.2f;%s",
```

```java
388: 
389: ct.setSoLuongNhap(soLuong);
390: ct.setDonGiaNhap(donGia);
```

Giới hạn cần nhớ: Ngày lịch sử vẫn gán trực tiếp để giữ được hóa đơn cũ đã hết hạn. Bộ duyệt nhập mới kiểm tra còn hạn trước cập nhật kho.

### src/hoadon/CTHoaDon.java

### SĐ-13 — src/hoadon/CTHoaDon.java — ĐÃ ÁP DỤNG

setDonGia(): dòng 84–89

Trước: So sánh &lt;=0 cho phép NaN/vô cực đi qua.

Đã đổi: Thêm Double.isFinite(donGia), giữ yêu cầu giá &gt;0.

Lý do: Chi tiết hóa đơn không nhận đơn giá không hữu hạn qua setter.

```java
84: public void setDonGia(double donGia) {
85:     if (!Double.isFinite(donGia) || donGia <= 0) {
86:         throw new IllegalArgumentException("[Lỗi] Đơn giá phải > 0! Nhận: " + donGia);
```

### src/hoadon/DSHoadon.java

### SĐ-14 — src/hoadon/DSHoadon.java — ĐÃ ÁP DỤNG

docFile(): dòng 504–690

Trước: Bỏ phần tiền điểm/phương thức khi nạp; có dòng lỗi vẫn thay danh sách bằng phần đọc được.

Đã đổi: Gọi hàm phục hồi thanh toán cho hóa đơn bán/nhập. Nếu có dòng lỗi, đánh dấu bảo vệ file và giữ danh sách cũ.

Lý do: Giữ đúng tổng phải trả khi đọc lại và tránh lưu đè mất các dòng không nạp được.

```java
662: 
663: if (hd instanceof Hoadonban && p.length > 11) ((Hoadonban) hd).khoiPhucThanhToanTuFile(Double.parseDouble(p[9]), Double.parseDouble(p[10]), p[11]);
664: if (hd instanceof HoadonNhap && p.length > 9) ((HoadonNhap) hd).khoiPhucThanhToanTuFile(Double.parseDouble(p[8]), p[9]);
```

```java
675: 
676: if (loi > 0) { util.Ktradulieu.danhDauFileLoi(filePath, "D? li?u h?a ??n l?i"); return; }
677: this.danhSach.clear();
```

Giới hạn cần nhớ: Chưa gọi khoiPhucChiTietThanhToanTuFile cho các cột mở rộng: tiền khách đưa, mã giao dịch, tên người chuyển, số tài khoản và tên ngân hàng.

### SĐ-15 — src/hoadon/DSHoadon.java — CHƯA HOÀN TẤT — LỖI CẦN SỬA

ghiFile(): dòng 449–502

Trước: Ghi trực tiếp file đích, dễ làm file bị cụt khi việc ghi lỗi.

Đã đổi: Đổi đường dẫn qua duongDanGhi và ghi nội dung ra filePath + &quot;.tmp&quot;.

Lý do: Mục tiêu là bảo vệ file lỗi và thay file sau khi ghi xong.

```java
459: new OutputStreamWriter(
460:         new FileOutputStream(filePath + ".tmp"),
461:         StandardCharsets.UTF_8))) {
```

Giới hạn cần nhớ: Mã hiện tại thiếu đóng/hoàn tất ghi bằng Ktradulieu.hoanTatGhi để thay file đích. Ca chạy trên thư mục mới không đọc được hóa đơn vừa lưu. Không được xem việc lưu hóa đơn là đã sửa xong.

### src/sanpham/SanPham.java

### SĐ-16 — src/sanpham/SanPham.java — ĐÃ ÁP DỤNG

setDonGiaNhap(): dòng 80–85; setDonGiaBan(): dòng 91–96; setPhanTramThue(): dòng 124–129

Trước: Các điều kiện âm/không dương không chặn NaN; giá vô cực có thể được nhận.

Đã đổi: Thêm Double.isFinite cho cả ba setter; giữ các giới hạn nghiệp vụ cũ.

Lý do: Ngăn giá/thuế không hữu hạn gây sai tiền hoặc lỗi ở lớp sử dụng.

```java
80: public void setDonGiaNhap(double donGiaNhap) {
81:     if (!Double.isFinite(donGiaNhap) || donGiaNhap < 0) {
82:         throw new IllegalArgumentException("[Lỗi] Đơn giá nhập phải >= 0! Nhận: " + donGiaNhap);
```

```java
124: public void setPhanTramThue(double phanTramThue) {
125:     if (!Double.isFinite(phanTramThue) || phanTramThue < 0 || phanTramThue > 100) {
126:         throw new IllegalArgumentException("[Lỗi Thuế] Thuế suất phải từ 0% đến 100%! Nhận: " + phanTramThue);
```

Giới hạn cần nhớ: Constructor SanPham đầy đủ vẫn gán trực tiếp giá, tồn, thuế; không nên mô tả tất cả đường tạo sản phẩm là đã được kiểm tra.

### SĐ-17 — src/sanpham/SanPham.java — ĐÃ ÁP DỤNG

kiemTraHanSuDung(): dòng 191–209; congTonKho(): dòng 211–216

Trước: Hàng hết hạn đã chuyển tồn về 0 nhưng vẫn trả true. Cộng kho bằng += có thể tràn thành số âm.

Đã đổi: Nhánh hết hạn trả false. Cộng kho dùng Math.addExact trước khi gán.

Lý do: Caller nhận đúng kết luận hết hạn; tràn số được phát hiện và không thay đổi tồn cũ.

```java
205:     }
206:     return false;
207: }
```

```java
214:     }
215:     this.soLuongTon = Math.addExact(this.soLuongTon, sl);
216: }
```

Giới hạn cần nhớ: Math.addExact ném ArithmeticException nếu gọi trực tiếp vượt giới hạn; luồng duyệt nhập có kiểm tra tổng trước. Bộ đếm hàng hết hạn chưa được đổi kiểu.

### src/sanpham/DSSanPham.java

### SĐ-18 — src/sanpham/DSSanPham.java — ĐÃ ÁP DỤNG

ghiFile(): dòng 417–448

Trước: Ghi trực tiếp file sản phẩm.

Đã đổi: Dùng đường dẫn bảo vệ, ghi .tmp, đóng writer rồi gọi hoanTatGhi thay file.

Lý do: Tránh làm cụt file sản phẩm khi ghi chưa xong.

```java
427: 
428: try (BufferedWriter bw = new BufferedWriter(new FileWriter(duongDan + ".tmp"))) {
429:     for (int i = 0; i < this.soLuong; i++) {
```

```java
441: bw.close();
442: Ktradulieu.hoanTatGhi(new File(duongDan + ".tmp"), duongDan);
443: System.out.println("Đã lưu danh sách " + this.soLuong + " sản phẩm vào file: " + duongDan);
```

Giới hạn cần nhớ: Chỉ nguyên vẹn từng file. Không tạo giao dịch chung với file hóa đơn/điểm.

### src/person/Nguoidung.java

### SĐ-19 — src/person/Nguoidung.java — ĐÃ ÁP DỤNG

Nguoidung(): dòng 27–35; setTaiKhoan(): dòng 131–139; setMatKhau(): dòng 145–150

Trước: Constructor đầy đủ gán thẳng các trường; setter tài khoản/mật khẩu có thể nhận dấu ; phân cách file.

Đã đổi: Constructor đầy đủ gọi setter cho mã, tên, tài khoản, mật khẩu, SĐT, vai trò. Tài khoản/mật khẩu bị từ chối nếu chứa dấu ;.

Lý do: Dữ liệu tạo bằng constructor cũng được kiểm tra; giảm nguy cơ mật khẩu null và lệch cột file.

```java
27: public Nguoidung(String maND, String tenND, String taiKhoan, String matKhau, String soDienThoai, String vaiTro, boolean daXoa) {
28:     setMaND(maND);
29:     setTenND(tenND);
```

```java
131: public void setTaiKhoan(String taiKhoan) {
132:     if (taiKhoan == null || taiKhoan.contains(";") || taiKhoan.trim().isEmpty()) {
133:         throw new IllegalArgumentException("[Lỗi] Tài khoản đăng nhập không được để trống!");
```

```java
145: public void setMatKhau(String matKhau) {
146:     if (matKhau == null || matKhau.contains(";") || matKhau.trim().length() < 4) {
147:         throw new IllegalArgumentException("[Lỗi] Mật khẩu tối thiểu phải từ 4 ký tự trở lên!");
```

Giới hạn cần nhớ: Constructor rỗng vẫn dùng cho nhập dần. Thông báo của setter chưa mô tả riêng trường hợp có dấu ;. Các trường văn bản khác chưa có escape dùng chung.

### src/person/NhanVien.java

### SĐ-20 — src/person/NhanVien.java — ĐÃ ÁP DỤNG

setLuongCoBan(): dòng 57–62

Trước: Chỉ chặn lương âm.

Đã đổi: Thêm kiểm tra Double.isFinite trước gán.

Lý do: Không nhận NaN/vô cực làm hỏng báo cáo lương.

```java
57: public void setLuongCoBan(double luongCoBan) {
58:     if (!Double.isFinite(luongCoBan) || luongCoBan < 0) {
59:         throw new IllegalArgumentException("[Lỗi] Lương cơ bản không được âm! Nhận: " + luongCoBan);
```

### src/person/Khachhang.java

### SĐ-21 — src/person/Khachhang.java — ĐÃ ÁP DỤNG

congDiem(): dòng 144–146

Trước: Dùng +=, có thể tràn điểm thành số âm.

Đã đổi: Dùng Math.addExact để tính trước khi gán.

Lý do: Giữ nguyên điểm cũ khi vượt giới hạn. Thanh toán đã kiểm tra điểm dự kiến trước thu tiền.

```java
144: public void congDiem(int diem) {
145:     if (diem > 0) this.diemTichLuy = Math.addExact(this.diemTichLuy, diem);
146: }
```

### src/person/DsKhachHang.java

### SĐ-22 — src/person/DsKhachHang.java — ĐÃ ÁP DỤNG

docFile(): dòng 177–206; ghiFile(): dòng 161–174

Trước: Đọc lỗi giữa file để lại danh sách thiếu; ghi trực tiếp file thật.

Đã đổi: Lưu bản sao danh sách cũ và phục hồi khi đọc ném ngoại lệ; đánh dấu bảo vệ file. Ghi .tmp rồi dùng hoanTatGhi.

Lý do: Giữ dữ liệu đang dùng khi đọc lỗi và giảm nguy cơ mất file khi ghi dở.

```java
179: if (!file.exists()) return;
180: java.util.List<Khachhang> banCu = new java.util.ArrayList<>(danhSach);
181: danhSach.clear();
```

```java
168: bw.close();
169: Ktradulieu.hoanTatGhi(new File(duongDan + ".tmp"), duongDan);
170: System.out.println("Đã lưu danh sách khách hàng vào file: " + duongDan);
```

Giới hạn cần nhớ: Dòng thiếu trường bị bỏ qua vẫn chưa được xem là lỗi. Việc lưu đồng bộ nhiều file chưa có rollback.

### src/doitac/DSNhaCungCap.java

### SĐ-23 — src/doitac/DSNhaCungCap.java — ĐÃ ÁP DỤNG

docFile(): dòng 188–217; ghiFile(): dòng 173–186

Trước: Đọc lỗi có thể làm mất danh sách đầy đủ; ghi trực tiếp file đích.

Đã đổi: Phục hồi banCu khi đọc lỗi, đánh dấu bảo vệ; ghi qua .tmp và hoanTatGhi.

Lý do: Bảo vệ danh sách nhà cung cấp theo cùng cách của khách hàng.

```java
190: if (!file.exists()) return;
191: java.util.List<NhaCungCap> banCu = new java.util.ArrayList<>(danhSach);
192: danhSach.clear();
```

```java
180: bw.close();
181: Ktradulieu.hoanTatGhi(new File(duongDan + ".tmp"), duongDan);
182: System.out.println("Đã lưu danh sách nhà cung cấp vào file: " + duongDan);
```

Giới hạn cần nhớ: Tương tự khách hàng, dòng thiếu trường chưa được đánh dấu lỗi; chưa phải giao dịch nhiều file.

### src/person/QuanLyCuaHang.java

### SĐ-24 — src/person/QuanLyCuaHang.java — ĐÃ ÁP DỤNG

thongKeSanPhamBanDuoc(): dòng 162–208; tinhToanBaoCao(): dòng 364–390

Trước: Báo cáo sản phẩm bán được bỏ qua kỳ báo cáo. Doanh thu cộng tiền hàng trước giảm giá.

Đã đổi: Lọc ngày hóa đơn theo tuNgay/denNgay. Doanh thu cộng tinhThanhTien() trừ thuế bán, tức phần thu sau giảm/điểm chưa gồm VAT.

Lý do: Tránh báo cáo lẫn kỳ và tránh đếm phần tiền đã giảm như đã thu.

```java
172: if (!(hd instanceof Hoadonban) || !hd.isDaXuLyThanhCong()) { continue; }
173: if (tuNgay == null || denNgay == null || hd.getNgayLap().isBefore(tuNgay) || hd.getNgayLap().isAfter(denNgay)) continue;
174: CTHDBan[] dsCT = ((Hoadonban) hd).getDsChiTiet();
```

```java
374: if (ngayLap.isBefore(tuNgay) || ngayLap.isAfter(denNgay)) { continue; }
375: if (hd instanceof Hoadonban) { tongDoanhThuBan += hd.tinhThanhTien() - ((Hoadonban) hd).tinhTienThue(); donBanThanhCong++; }
376: else if (hd instanceof HoadonNhap) { tongChiPhiNhap += hd.tinhTongTien(); donNhapThanhCong++; }
```

Giới hạn cần nhớ: Cách ghi nhận điểm như khoản giảm cần thống nhất với yêu cầu môn học. Chưa tính lợi nhuận theo giá vốn hàng đã bán; không coi tổng chi phí nhập là giá vốn đã bán.

### src/giaodien/Giaodien.java

### SĐ-25 — src/giaodien/Giaodien.java — ĐÃ ÁP DỤNG

menuThuNganTiepNhanKhachHang(): dòng 320–353

Trước: Menu có 0 quay lại nhưng luaChon nhận khoảng 1..5.

Đã đổi: Đổi khoảng hợp lệ thành 0..4.

Lý do: Phím 0 quay lại được; không còn nhận lựa chọn 5 không có nhánh xử lý.

```java
329: 
330: int chon = Ktradulieu.luaChon(sc, "Chọn chức năng (1-5): ", 0, 4);
331: 
```

Giới hạn cần nhớ: Chuỗi nhắc hiện tại vẫn ghi (1-5); hành vi kiểm tra đã dùng 0..4. Đây là phần chữ nhỏ còn giữ lại.

### src/thanhtoan/ThanhToan.java

### SĐ-26 — src/thanhtoan/ThanhToan.java — ĐÃ ÁP DỤNG

setSoTien(): dòng 90–95

Trước: Chỉ chặn số tiền âm.

Đã đổi: Thêm Double.isFinite và giữ tiền &gt;=0.

Lý do: Không tạo thanh toán với NaN/vô cực qua constructor/setter.

```java
90: public void setSoTien(double soTien) {
91:     if (!Double.isFinite(soTien) || soTien < 0) {
92:         throw new IllegalArgumentException("[Lỗi] Số tiền thanh toán không được âm! Nhận: " + soTien);
```

### src/thanhtoan/TienMat.java

### SĐ-27 — src/thanhtoan/TienMat.java — ĐÃ ÁP DỤNG

qTrinhThanhToan(): dòng 56–65

Trước: Yêu cầu soTien&gt;0 nên hóa đơn giảm về 0đ luôn thất bại.

Đã đổi: Cho phép soTien&gt;=0 khi tiền khách đưa đủ.

Lý do: Hoàn tất hóa đơn dùng hết điểm hoặc giảm 100%.

```java
56: public boolean qTrinhThanhToan() {
57:     if (this.tienKhachDua >= this.soTien && this.soTien >= 0) {
58:         ketThuc(true);
```

### src/thanhtoan/ChuyenKhoan.java

### SĐ-28 — src/thanhtoan/ChuyenKhoan.java — ĐÃ ÁP DỤNG

qTrinhThanhToan(): dòng 87–102

Trước: Yêu cầu soTien&gt;0.

Đã đổi: Cho phép soTien&gt;=0; giữ yêu cầu thông tin ngân hàng.

Lý do: Đồng nhất quy tắc hóa đơn 0đ với tiền mặt.

```java
90: && (this.soTKChuyen != null && !this.soTKChuyen.isEmpty())
91: && (this.soTien >= 0);
92: 
```

Giới hạn cần nhớ: Đây là mô phỏng nhập thông tin chuyển khoản, không kết nối ngân hàng để xác minh tiền thực nhận.

## 4. Kiểm chứng và phần chưa hoàn tất

Đã biên dịch lại toàn bộ src vào .audit/documentation-check/classes bằng javac -encoding UTF-8. ReviewProbe và CrashProbe được chạy trong .audit/documentation-check/run, dùng bản sao dữ liệu. Các probe ghi nhận kết quả từng tình huống; số nhóm chạy không đồng nghĩa số kiểm thử tự động PASS.

| Ca kiểm tra | Kết quả hiện tại |
| --- | --- |
| Bán thường, gọi thanh toán lần hai | Lần đầu true, lần hai false, tồn 100 → 98. |
| Sản phẩm đã xóa/chưa duyệt | Thêm false; thanh toán false. |
| Hóa đơn bán/nhập đã hủy | Từ chối thanh toán/duyệt; giữ DaHuy. |
| Dùng 3 triệu điểm | Tiền giảm 3.000.000.000đ; phải trả 0đ, không tràn int. |
| Khách đã xóa / hóa đơn 0đ | Khách đã xóa bị từ chối; hóa đơn 0đ thanh toán thành công. |
| 16 menu một Scanner | Cả 16 menu trả về với lựa chọn thoát của menu đó. |
| Nhập kho giá NaN | Bị từ chối; giá nhập cũ được giữ. |
| Tràn điểm / cộng tồn trực tiếp | Math.addExact ném ArithmeticException; đây là phát hiện vượt giới hạn, không phải cộng thành số âm. |
| Thông tin ngân hàng null | Không còn NPE trong hàm phục hồi chi tiết. |
| Khuyến mãi quà tặng quá lớn | Không văng ngoại lệ trong ca đã chạy. |
| Lưu rồi nạp hóa đơn trong thư mục mới | CHƯA ĐẠT: loader không thấy hóa đơn; probe phát sinh NPE khi truy cập kết quả null. Writer mới ghi .tmp, thiếu bước thay file đích. |
| Scanner đã đóng ở tiếp nhận khách | CHƯA ĐẠT: IllegalStateException. Không xem là mọi hàm đã xử lý Scanner đóng. |
| Chi tiết giá cực lớn | Tổng chi tiết vẫn NaN; thanh toán có chặn tổng không hữu hạn. |
| Ngày hết hạn khi nhập thêm | Giữ hạn sớm hơn của hàng cũ; không bằng hạn lô mới. Đây là giới hạn một bản ghi sản phẩm, chưa phải kiểm thử quản lý lô. |

Đính chính kết quả cũ: một số lần chạy trước dùng lại thư mục đã có file hóa đơn từ lượt trước, nên ca đọc lại có thể nạp dữ liệu cũ. Kết quả trong tài liệu này dùng thư mục mới và có ưu tiên cao hơn nhận xét “lưu hóa đơn đã xong” trước đó.

| Ưu tiên | Việc còn lại |
| --- | --- |
| P1 — lưu dữ liệu | DSHoadon.ghiFile cần hoàn tất đổi .tmp sang file đích sau khi writer đóng. Hiện có thông báo lưu thành công dù file chính chưa cập nhật. |
| P1 — giao dịch nhiều file | Hóa đơn, kho và điểm vẫn ghi riêng; chưa có journal/rollback/recovery khi lỗi I/O hoặc tiến trình bị ngắt. Không có tính nguyên vẹn chung giữa các file. |
| P1 — cập nhật trong bộ nhớ | hoanTatDonHang vẫn trừ kho trước trừ điểm. Luồng đơn luồng có kiểm tra trước thu tiền, nhưng chưa hoàn tác nếu điểm/kho bị thay đổi trong lúc thanh toán. |
| P2 — API nạp file | Hàm phục hồi public vẫn sửa được hóa đơn hoàn tất; loader chưa nạp chi tiết thanh toán mở rộng. |
| P2 — kiểm tra đầu vào | Scanner đóng còn phát sinh lỗi ở một số hàm; constructor SanPham còn bỏ qua setter; các dòng thiếu trường trong file khách/NCC có thể bị bỏ qua. |
| P2 — mô hình nghiệp vụ | Chưa quản lý lô, giá vốn lịch sử và lợi nhuận theo hàng đã bán. Dùng double và String trạng thái vẫn có giới hạn. |

Tài liệu này ghi nhận trạng thái mã đang có, không tự sửa thêm các vấn đề trên. Việc biên dịch thành công chỉ xác nhận mã hợp lệ về cú pháp/kiểu, không chứng minh tất cả nghiệp vụ đúng.

## 5. Hướng dẫn chạy chương trình

Chạy bằng IntelliJ IDEA: mở thư mục Do-an-OOP; đặt src làm Sources Root; chọn Project SDK tương ứng. Cấu hình hiện tại trong .idea/misc.xml là JDK 26. Entry point là giaodien.Main. Working directory phải là thư mục gốc có src và data.

Trong Run Configuration, chọn lớp giaodien.Main và chạy bằng console hỗ trợ UTF-8. Chương trình dùng Scanner nhập console, không có giao diện web. Không chạy với working directory nằm trong src vì đường dẫn data/... là đường dẫn tương đối.

Chạy bằng PowerShell từ thư mục gốc: máy kiểm chứng dùng Java 26.0.1. Các lệnh sau tạo thư mục class riêng trong out/manual.

```java
: New-Item -ItemType Directory -Force out/manual | Out-Null
: $javaFiles = @(Get-ChildItem src -Recurse -Filter *.java |
:     ForEach-Object { $_.FullName })
: javac -encoding UTF-8 -d out/manual $javaFiles
: if ($LASTEXITCODE -eq 0) {
:     java -cp out/manual giaodien.Main
: }
```

Đăng nhập: dùng tài khoản trong dữ liệu của bạn. Chỉ khi data/nhanvien.txt thiếu hoặc rỗng, Giaodien.khoiTaoTaiKhoanMacDinh tạo ba tài khoản demo: nhanvien, quanly, giamdoc; mật khẩu demo 123456. Không xóa file tài khoản đang có để tạo lại nếu cần giữ dữ liệu.

Nhập liệu thường dùng: dấu / để sinh mã tự động ở các màn hình tạo; mã NV/QL/GD cho người dùng, TA/DU cho sản phẩm, HDB/HDN cho hóa đơn, KH/NCC/KM cho các danh mục tương ứng. Ngày nhập theo dd/MM/yyyy. SĐT di động gồm 10 số, bắt đầu 03/05/07/08/09. Không nhập dấu ; vào trường được lưu bằng phân cách cột.

Thoát: menu con thường dùng 0 để quay lại. Riêng menu nhân viên dùng 3 để đăng xuất. Tại màn hình đăng nhập, nhập 0 ở tên đăng nhập để thoát. batDau có xử lý EOF để lưu rồi thoát; RuntimeException thường quay về đăng nhập.

Lưu ý thao tác: trước khi demo dữ liệu quan trọng, sao chép cả thư mục data. Với lỗi writer hóa đơn đang ghi nhận ở phần 4, cần kiểm tra cả hoadon.txt và hoadon.txt.tmp; chưa coi file chính là đã được cập nhật chỉ vì console báo thành công.

## 6. Bản đồ cấu trúc và OOP

| Package | Trách nhiệm và lớp chính |
| --- | --- |
| giaodien | Main tạo Scanner; Giaodien giữ các danh sách, đọc dữ liệu, đăng nhập, phân luồng menu và gọi nghiệp vụ. |
| person | Nguoidung là lớp cha trừu tượng. NhanVien, QuanLyCuaHang, Giamdoc đại diện vai trò. DSNhanVien quản lý mọi tài khoản; DsKhachHang quản lý khách. |
| sanpham | SanPham là lớp cha; ThucAn và DoUong bổ sung thuộc tính. DSSanPham lưu, tìm, sửa/xóa và ghi sản phẩm. |
| hoadon | HoaDon và IHoaDon đặt hợp đồng chung. Hoadonban/HoadonNhap giữ mảng chi tiết; CTHoaDon là lớp cha của CTHDBan/CTHDonNhap. DSHoadon lưu và đọc danh sách hóa đơn. |
| khuyenmai | Khuyenmai có ba kiểu: mua tặng, giảm phần trăm, giảm trực tiếp. DSSanPhamKhuyenMai lưu liên kết maSP–maKM; DSKhuyenMai giữ chương trình. |
| thanhtoan | ThanhToan là lớp cha; TienMat/ChuyenKhoan thực hiện các bước nhập và ghi nhận trạng thái thanh toán. |
| doitac / util | NhaCungCap, DSNhaCungCap lưu đối tác. Ktradulieu gom chuẩn hóa, nhập liệu, định dạng ngày/tiền và bảo vệ file. |

```java
: Nguoidung
:   +-- NhanVien
:   +-- QuanLyCuaHang implements Thongke
:   +-- Giamdoc
: 
: HoaDon implements IHoaDon
:   +-- Hoadonban -- CTHDBan
:   +-- HoadonNhap -- CTHDonNhap
: 
: SanPham -> ThucAn / DoUong
: Khuyenmai -> MuaTang / Phantram / Tructiep
: ThanhToan -> TienMat / ChuyenKhoan
```

Khi giải thích OOP: dùng HoaDon, SanPham, ThanhToan để nói về trừu tượng và kế thừa; gọi phương thức tính tiền/thanh toán theo đối tượng cụ thể để minh họa đa hình; dùng setter kiểm tra dữ liệu để giải thích đóng gói. KeToan.java và ThuNgan.java hiện chứa code đã comment; Quanly.java là lớp rỗng, không phải các luồng đang hoạt động trong menu.

## 7. Quy trình sử dụng theo vai trò

| Vai trò | Luồng menu hiện tại |
| --- | --- |
| Nhân viên | 1: kho hàng; 2: thu ngân/khách hàng; 3: đăng xuất. Trong thu ngân: 1 tiếp nhận khách, 2 lập hóa đơn, 3 quản lý hóa đơn bán, 4 xem khuyến mãi, 0 quay lại. |
| Quản lý cửa hàng | 1 sản phẩm, 2 khuyến mãi, 3 nhập kho, 4 nhà cung cấp, 5 nhân viên, 6 báo cáo, 7 khách hàng, 0 đăng xuất. Quản lý gửi đề xuất sa thải thay vì tự quyết định. |
| Giám đốc | 1 nhân sự, 2 báo cáo/thống kê, 3 duyệt nhập kho, 0 đăng xuất. Có luồng phê duyệt yêu cầu sa thải. |

Bán hàng: đăng nhập nhân viên → Thu ngân → Lập hóa đơn bán → tạo mã/ghi chú → chọn khách lẻ hoặc khách thành viên → kiểm tra/điều chỉnh hóa đơn → thêm sản phẩm → thanh toán. Hàng phải còn tồn, còn hạn, chưa xóa và Đã duyệt. Hóa đơn hủy/hoàn tất bị chặn ở các thao tác đã sửa.

Tại thanh toán: xác định điểm muốn dùng nếu khách có điểm; chọn tiền mặt hoặc chuyển khoản. Tiền mặt nhập đủ tiền khách đưa; chuyển khoản nhập ngân hàng, số tài khoản, tên người chuyển và mã giao dịch. Thành công thì trừ số mua cộng quà tặng, cập nhật điểm, xác nhận trạng thái và in hóa đơn ra console. “In hóa đơn” trong code không gọi máy in giấy.

Nhập kho: chọn sản phẩm có sẵn hoặc tạo danh mục mới tồn 0, nhập số lượng/giá rồi gom phiếu theo NCC. Người lập nhập mã cho từng phiếu và hoàn tất để lưu chưa thanh toán/chờ duyệt. Admin/Quản lý mở chức năng duyệt, kiểm tra chi tiết, xác nhận và thanh toán; thành công mới cộng kho, đổi giá nhập, cập nhật trạng thái và in hóa đơn. Không duyệt riêng từng sản phẩm. Hủy thanh toán giữ phiếu chờ duyệt; phiếu cũ đã trả tiền không phải chi lại.

Khuyến mãi: tạo chương trình còn hiệu lực, đặt điều kiện và liên kết mã sản phẩm với mã khuyến mãi. Khi thêm/sửa số lượng dòng bán, CTHDBan xét ưu đãi và chọn chương trình có giá trị lớn nhất trong các ứng viên hợp lệ. Quà tặng bị giới hạn bởi tồn còn lại; ứng viên tràn số bị bỏ qua.

Báo cáo: chọn ngày/tháng/quý/năm hoặc khoảng ngày tại menu báo cáo. Sản phẩm bán được đã lọc kỳ. Báo cáo doanh thu hiện dùng tiền thực thu sau giảm/điểm rồi trừ VAT; không gọi đây là lợi nhuận đầy đủ nếu chưa trừ giá vốn và các chi phí liên quan.

## 8. Công thức và ý nghĩa dữ liệu cần nhớ

| Đại lượng | Quy tắc hiện tại |
| --- | --- |
| Tiền hàng | Số lượng mua × đơn giá bán. |
| Thuế chi tiết | Phần tiền hàng sau giảm khuyến mãi × phần trăm VAT / 100. |
| Tổng phải trả | Tiền hàng − giảm khuyến mãi + VAT − tiền đổi điểm; chặn dưới bằng 0. |
| Đổi điểm | 1 điểm = 1.000đ. Không đổi quá phần tiền hóa đơn cho phép. |
| Tích điểm | floor(tiền phải trả / 100.000); kiểm tra giới hạn trước thu tiền. |
| Xuất kho | Số lượng mua + số lượng tặng; kiểm tra tổng cho cùng mã sản phẩm. |
| Hạn dùng | Còn hiệu lực trong chính ngày HSD. Qua ngày HSD: chuyển tồn sang bộ đếm hết hạn và trả false. |
| Trạng thái hóa đơn | DangXuLy / DaXuLyThanhCong / DaHuy; vẫn lưu String. |

Ví dụ: mua 2 món giá 50.000đ, giảm khuyến mãi tổng 10.000đ, VAT 5% → 94.500đ. Dùng 2 điểm → trả 92.500đ; chưa đủ 100.000đ nên cộng 0 điểm. Nếu được tặng 1 món thì kho giảm 3, không chỉ giảm 2.

## 9. File dữ liệu và xử lý khi đọc lỗi

| File trong data/ | Nội dung chính |
| --- | --- |
| nhanvien.txt | Tài khoản nhiều vai trò; các dòng NHANVIEN / QUANLY / GIAMDOC. |
| khachhang.txt | Mã, tên, SĐT, địa chỉ, điểm và cờ đã xóa. |
| nhacungcap.txt | Thông tin nhà cung cấp và cờ đã xóa. |
| sanpham.txt | THUCAN / DOUONG; giá, tồn, NSX/HSD, thuế, thông tin riêng, trạng thái duyệt, đã xóa. |
| khuyenmai.txt / sanphamkhuyenmai.txt | Điều kiện khuyến mãi và bảng liên kết sản phẩm–khuyến mãi. |
| hoadon.txt | Dòng HDBAN/HDNHAP và các dòng CTBAN/CTNHAP đi kèm. Đây là file được Giaodien.docDuLieu sử dụng. |

Các file hoadonban.txt/hoadonnhap.txt có trong thư mục không đồng nghĩa đang là file chính của luồng đọc/lưu hiện tại. Hãy kiểm tra Giaodien.docDuLieu và ghiDuLieu khi xác định file được sử dụng.

Ktradulieu.danhDauFileLoi sao lưu file lỗi sang .loi.bak và đánh dấu để ghi dữ liệu phiên sang .moi. Không tự ghi đè bản gốc bằng một danh sách rỗng. Khi xử lý lỗi: thoát ứng dụng, giữ bản sao data, xem thông báo/dòng lỗi, đối chiếu .loi.bak và .moi, sửa hoặc hợp nhất có kiểm tra rồi khởi động lại. Không đổi tên đè file một cách tự động nếu chưa đối chiếu.

File .tmp là dữ liệu đang ghi hoặc chưa hoàn tất thay file. Với writer hóa đơn hiện tại, .tmp có thể chứa bản mới còn file chính là bản cũ. Điểm này phải sửa trước khi xem tính năng lưu hóa đơn là đáng tin cậy.

## 10. Cách đọc code và chuẩn bị trình bày đồ án

Thứ tự đọc phù hợp: Main → Giaodien.batDau/đăng nhập → menu vai trò → các lớp danh sách → lớp nghiệp vụ → lớp chi tiết → thanh toán/khuyến mãi → Ktradulieu. Đọc theo một giao dịch cụ thể sẽ dễ hiểu hơn đọc tất cả getter/setter trước.

Khi trình bày một bản sửa, dùng ba ý: đầu vào nào từng gây lỗi; điều kiện/phép tính nào đã đổi; kết quả chạy lại ra sao. Ví dụ tiền điểm: “3 triệu × 1.000 bị tràn int; đổi sang nhân với 1000.0; kết quả đúng 3 tỷ”. Phân biệt phát hiện tràn bằng ngoại lệ với việc đã xử lý ngoại lệ ở mọi nơi gọi.

Một buổi demo nên có: đăng nhập từng vai trò; bán thường; thành viên dùng điểm; hàng bị xóa/hết hạn bị từ chối; phiếu nhập đã hủy không duyệt được; khuyến mãi có liên kết; báo cáo đúng kỳ; thử mở lại dữ liệu. Kiểm tra lưu/mở lại chỉ được đánh dấu đạt khi file chính thực sự thay đổi và một tiến trình mới đọc đúng dữ liệu.

Không đưa .audit/classes, .audit/pdf-deps hoặc thư mục dữ liệu kiểm thử vào src. Các file .class là kết quả biên dịch, không phải mã nguồn nghiệp vụ. Không quy các thay đổi trước phiên trong Main, Ktradulieu, DSNhanVien hoặc toàn bộ phần LocalDate thành các thay đổi mới của bản kê này.

## 11. Tệp bàn giao và cách tái tạo tài liệu

PDF này có mục lục và bookmark để nhảy nhanh theo phần. Bản Markdown cùng tên dùng để tìm kiếm/chỉnh nội dung. File JSON cùng tên lưu danh sách SĐ-xx, số dòng và hash mã nguồn. Script audit/tao_nhat_ky_thay_doi.py sinh lại tài liệu từ bản kê và mã nguồn tại thời điểm chạy.

```java
: python audit/tao_nhat_ky_thay_doi.py
```

Log kiểm chứng mới: .audit/documentation-check/review-results.txt và crash-results.txt. Dữ liệu chạy nằm trong .audit/documentation-check/run/data. ReviewProbe/CrashProbe là công cụ kiểm tra bổ sung của cuộc trao đổi; AuditProbe, LocalDateProbe và ScannerProbe đã có khi bắt đầu lượt rà soát này.


## Cập nhật vai trò Admin thay Giám đốc

Lớp `Giamdoc` đã được thay bằng `Admin`, kế thừa các nghiệp vụ của `QuanLyCuaHang` và có toàn quyền quản trị. Các mô tả Giám đốc và menu ba mục trong phần ghi chép trước đây là hành vi cũ. Tài khoản mẫu hiện tại là `admin / 123456` (AD001); Quản lý chỉ quản trị nhân viên thường và đề xuất sa thải. Admin có toàn bộ menu nghiệp vụ, tạo/sửa/khóa/mở khóa tài khoản, đặt lại mật khẩu và quyết định sa thải. Người lập hóa đơn/thanh toán đã mở rộng sang `Nguoidung` để ghi nhận đúng tài khoản thực hiện.

Chi tiết ma trận quyền, menu, tương thích file tài khoản cũ và cách chạy kiểm tra nằm trong [PHAN_QUYEN_ADMIN.md](PHAN_QUYEN_ADMIN.md).


## Điều chỉnh sản phẩm và tồn kho

Bỏ `tongSoSanPhamKhoiTao`, `soLuongHetHan`, `trangThaiDuyet` cùng các getter/setter và tham số constructor liên quan. Danh mục mới luôn tồn 0; chỉ cộng hàng khi duyệt hóa đơn nhập, không duyệt riêng từng sản phẩm. Hàng hết hạn được xác định theo HSD, loại tồn cũ về 0 khi kiểm tra hoặc nhận lô mới. Các mô tả duyệt riêng sản phẩm trong ghi chép trước là hành vi cũ. File sản phẩm mới có 15 cột; bộ đọc vẫn tương thích file cũ và hồ sơ `SPMOI` của hóa đơn cũ. Xem [SAN_PHAM_VA_TON_KHO.md](SAN_PHAM_VA_TON_KHO.md).


## Cập nhật nhập hàng theo NCC

Sản phẩm có `maNCC` và cột hiển thị NCC. Bộ dữ liệu mẫu đã được bổ sung NCC và các thuộc tính còn trống. Menu Kho → `1` mở nhập hàng; chọn sản phẩm có sẵn không hỏi NCC, mục `3` xem/sửa danh sách hiện tại không hỏi mã hóa đơn. Mục `0` gom hàng theo NCC rồi yêu cầu nhập mã `HDN...` chưa tồn tại cho từng phiếu, không tự sinh mã. Từng phiếu chọn `6` hoàn tất để lưu chưa thanh toán và chờ duyệt, `7` xem mặt hàng; sửa phiếu cũ qua Kho → `13` cũng không có thao tác chi tiền. Admin/Quản lý vào Quản lý nhập kho → `2` để xác nhận, thanh toán rồi nhập kho và in hóa đơn. Hủy thanh toán giữ phiếu chờ duyệt; phiếu cũ đã trả tiền không bị thanh toán lại. Mã người lập và người thanh toán được lưu riêng. Xem [NHAP_HANG_THEO_NHA_CUNG_CAP.md](NHAP_HANG_THEO_NHA_CUNG_CAP.md) để dùng luồng hiện tại.


## Cập nhật nhập bổ sung không theo lô

Luồng mới chỉ hỏi số lượng và giá nhập. Cùng mã sản phẩm được cộng dồn một dòng, lấy đơn giá/VAT lần nhập cuối. Khi duyệt phiếu, NSX mới bằng ngày duyệt; HSD mới bằng NSX mới cộng số ngày sử dụng đã có (`HSD cũ - NSX cũ`). Ngày áp dụng chung cho sản phẩm; không giữ hạn sớm nhất giữa các lô. Lịch sử hóa đơn cũ vẫn được đọc nguyên các dòng để bảo toàn tiền hàng. Danh sách sản phẩm bổ sung NSX và HSD. Xem [SAN_PHAM_VA_TON_KHO.md](SAN_PHAM_VA_TON_KHO.md).


## Thống kê và xử lý hạn sử dụng tại kho

Menu Kho bổ sung `9` thống kê hết hạn, `10` thống kê sắp hết hạn (0-30 ngày), `11` áp dụng giảm giá cận hạn (Admin/Quản lý), `12` loại bỏ lượng tồn hết hạn sau xác nhận. Xem báo cáo hoặc tồn kho không tự loại hàng. Khi duyệt nhập bổ sung: hàng cũ hết hạn thì bỏ lượng cũ, chỉ nhận lượng mới; còn hạn thì cộng cả hai. Xem [QUAN_LY_HAN_SU_DUNG_KHO.md](QUAN_LY_HAN_SU_DUNG_KHO.md).
