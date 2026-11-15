from pathlib import Path
import re


def read(path):
    with path.open(encoding='utf-8', newline='') as source:
        return source.read()


def write(path, text):
    with path.open('w', encoding='utf-8', newline='') as target:
        target.write(text)


path = Path('src/giaodien/Giaodien.java')
text = read(path)
methods = [
    'chonKyBaoCao', 'batDau', 'hienThiManHinhDangNhap',
    'menuNhanVien', 'menuQuanLyKhoHang', 'menuThuNganTiepNhanKhachHang',
    'menuKiemTraHoaDonBan', 'menuTiepNhanKhachHang', 'menuXemVaTimKiemKhuyenMai',
    'menuQuanLyHoaDonBan', 'menuQuanLyCuaHang', 'menuQuanLySanPham',
    'menuQuanLyKhuyenMai', 'menuQuanLyNhapKhoQuyTrinh', 'menuQuanLyNhaCungCap',
    'menuQuanLyNhanVien', 'menuThongKeBaoCao', 'menuQuanLyNhanSuGiamDoc',
    'menuBaoCaoThongKeGiamDoc', 'menuGiamDoc', 'taoHoaDonNhap', 'taoHoaDonBan',
    'timKiemSanPham', 'timKiemNhaCungCap', 'timKiemKhachHang',
    'xemLichSuMuaHangKhach', 'traCuuHoaDonNhap', 'xemChiTietHoaDonBan',
    'themNhanVien', 'themQuanLy',
]
for name in methods:
    declaration = re.compile(
        r'(?m)^([ \t]*(?:public|private|protected)\s+[\w\[\]]+\s+'
        + re.escape(name) + r')\(([^)]*)\)'
    )
    text, count = declaration.subn(
        lambda match: match[1] + '(Scanner sc' + (', ' + match[2] if match[2] else '') + ')', text
    )
    assert count == 1, (name, count)
    if name == 'menuKiemTraHoaDonBan':
        text = text.replace(name + '(hdb)', name + '(sc, hdb)')
    else:
        text = text.replace(name + '()', name + '(sc)')
text = text.replace(
    'public void batDau(Scanner sc) {',
    'public void batDau(Scanner sc) {\n'
    '        if (sc == null) throw new IllegalArgumentException("Scanner không được null!");'
)
write(path, text)

fallback = 'if (sc == null) sc = new Scanner(System.in);'
guard = 'if (sc == null) throw new IllegalArgumentException("Scanner không được null!");'
removed_fallbacks = 0
for path in Path('src').rglob('*.java'):
    text = read(path)
    count = text.count(fallback)
    if count:
        write(path, text.replace(fallback, guard))
        removed_fallbacks += count

path = Path('src/util/Ktradulieu.java')
text = read(path)
for signature in [
    'public static String nhapChuoi(Scanner sc, String thongBao, String regex, String thongBaoLoi) {',
    'public static int luaChon(Scanner sc, String thongBao, int min, int max) {',
]:
    assert text.count(signature) == 1
    text = text.replace(signature, signature + '\n        ' + guard)
write(path, text)

path = Path('audit/AuditProbe.java')
text = read(path)
old = 'Giaodien.class.getMethod(args[1]).invoke(g);'
assert old in text
text = text.replace(old, 'Giaodien.class.getMethod(args[1], Scanner.class).invoke(g, new Scanner(System.in));')
write(path, text)
print(f'Updated {len(methods)} UI methods; removed {removed_fallbacks} Scanner fallbacks.')
