"""Additional end-to-end scenarios using only isolated default accounts/data."""
from pathlib import Path
import subprocess
import json
import hashlib
import shutil
ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'.audit'/'scenarios'
def hashes():
    return {str(p):hashlib.sha256(p.read_bytes()).hexdigest() for p in (ROOT/'data').rglob('*') if p.is_file()}
before=hashes()
cases={
    'employee_login_logout': 'nhanvien\n123456\n3\n0\n',
    'manager_login_logout': 'quanly\n123456\n0\n0\n',
    'director_login_logout': 'giamdoc\n123456\n0\n0\n',
    'cashier_back_zero': 'nhanvien\n123456\n2\n0\n',
    'cashier_option_five': 'nhanvien\n123456\n2\n5\n',
    'negative_product_price': 'quanly\n123456\n1\n1\n1\n/\nAudit\n100\n-1\n',
    'negative_product_tax': 'quanly\n123456\n1\n1\n1\n/\nAudit\n100\n200\n1\nbox\n-1\n',
    'product_tax_above_100': 'quanly\n123456\n1\n1\n1\n/\nAudit\n100\n200\n1\nbox\n101\n',
    'manager_reports_empty': 'quanly\n123456\n6\n1\n4\n2026\n2\n4\n2026\n3\n4\n2026\n4\n4\n2026\n5\n6\n0\n0\n0\n',
    'director_reports_empty': 'giamdoc\n123456\n2\n1\n4\n2026\n5\n6\n0\n0\n0\n',
    'cancel_import': 'nhanvien\n123456\n1\n1\n/\n\n/\n0\n0\n3\n0\n',
}
results=[]
for name,stdin in cases.items():
    case=OUT/name
    case.mkdir(parents=True,exist_ok=True)
    # Dedicated fresh fixture directory for each invocation.
    import tempfile
    work=Path(tempfile.mkdtemp(prefix='fixture-',dir=case))
    if name=='cancel_import':
        (work/'data').mkdir(exist_ok=True)
        shutil.copy2(ROOT/'data/nhacungcap.txt',work/'data/nhacungcap.txt')
    log=case/'output.log'
    with log.open('wb') as f:
        try:
            p=subprocess.run(['java','-Xmx128m','-Dstdout.encoding=UTF-8','-Dstderr.encoding=UTF-8','-cp',str(ROOT/'.audit/classes'),'giaodien.Main'],cwd=work,input=stdin.encode(),stdout=f,stderr=subprocess.STDOUT,timeout=15)
            code=p.returncode
        except subprocess.TimeoutExpired:
            code='TIMEOUT'
    s=log.read_text(encoding='utf-8',errors='replace')
    results.append(dict(name=name,exit=code,logged_in='ĐĂNG NHẬP THÀNH CÔNG' in s,
                        recovered='không mong muốn' in s,crashed='Exception in thread' in s,
                        zero_rejected='khoảng từ 1 đến 5' in s,
                        cancelled_invoice_saved=name=='cancel_import' and 'Đã lưu hóa đơn nhập' in s))
(OUT/'results.json').write_text(json.dumps(dict(production_data_unchanged=hashes()==before,results=results),ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps(results,ensure_ascii=False,indent=2))
