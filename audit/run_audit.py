"""Run from any directory: python -X utf8 audit/run_audit.py. No production data writes."""
from pathlib import Path
import hashlib
import json
import random
import re
import shutil
import subprocess
import time

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / '.audit'
OUT.mkdir(exist_ok=True)
CLASSES = OUT / 'classes'
CLASSES.mkdir(exist_ok=True)

def snapshot():
    return {str(p.relative_to(ROOT)): hashlib.sha256(p.read_bytes()).hexdigest()
            for p in (ROOT / 'data').rglob('*') if p.is_file()}

before = snapshot()
sources = list((ROOT / 'src').rglob('*.java')) + [ROOT / 'audit/AuditProbe.java']
r = subprocess.run(['javac', '-encoding', 'UTF-8', '-Xlint:all', '-d', str(CLASSES), *map(str, sources)], capture_output=True)
(OUT / 'compile.log').write_bytes(r.stdout + r.stderr)
if r.returncode:
    raise SystemExit('Compilation failed; see .audit/compile.log')

results = []
def run(name, input_text='', main='giaodien.Main', args=(), fixture='copy', mutate=None, timeout=12):
    case = OUT / 'runs' / name
    case.mkdir(parents=True, exist_ok=True)
    data = case / 'data'
    data.mkdir(exist_ok=True)
    # Reset only files in this case's isolated data directory, without deleting trees.
    for p in data.iterdir():
        if p.is_file():
            p.unlink()
    if fixture == 'copy':
        for p in (ROOT / 'data').glob('*.txt'):
            shutil.copy2(p, data / p.name)
    if mutate:
        mutate(data)
    start = time.perf_counter()
    log = case / 'output.log'
    with log.open('wb') as output:
        try:
            proc = subprocess.run(['java', '-Xmx128m', '-Dfile.encoding=UTF-8', '-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8', '-cp', str(CLASSES), main, *args],
                                  input=input_text.encode('utf-8'), stdout=output, stderr=subprocess.STDOUT, cwd=case, timeout=timeout)
            code = proc.returncode
        except subprocess.TimeoutExpired:
            code = 'TIMEOUT'
    text = log.read_text(encoding='utf-8', errors='replace')
    result = dict(name=name, exit=code, ms=round((time.perf_counter()-start)*1000, 3),
                  exception=('Exception in thread' in text or 'AUDIT_EXCEPTION' in text),
                  recovered_error='không mong muốn' in text,
                  eof='AUDIT_EOF' in text, returned='AUDIT_RETURN' in text,
                  log=str(log.relative_to(ROOT)))
    results.append(result)
    return text

probe = run('domain_probes', main='AuditProbe', timeout=120)
(OUT / 'probe_summary.txt').write_text('\n'.join(l for l in probe.splitlines() if not l.startswith('PASS sales arithmetic')), encoding='utf-8')
print((OUT / 'probe_summary.txt').read_text(encoding='utf-8'), flush=True)
for fixture in ['copy', 'empty']:
    for name, text in [('eof',''),('exit','0\n'),('invalid_login','\n;\nunknown\nwrong\n0\n')]:
        run(f'{fixture}_{name}', text, fixture=fixture)
for filename in ['nhanvien','khachhang','nhacungcap','sanpham','khuyenmai','sanphamkhuyenmai','hoadon']:
    for kind in ['malformed','append']:
        def mutate(d, filename=filename, kind=kind):
            p=d/(filename+'.txt')
            original=p.read_text(encoding='utf-8') if p.exists() and kind=='append' else ''
            p.write_text(original+'\nBROKEN;DATA;NaN;2147483648;31/02/2026\n',encoding='utf-8')
        run('file_'+filename+'_'+kind,'0\n',mutate=mutate)

source = (ROOT/'src/giaodien/Giaodien.java').read_text(encoding='utf-8')
menus = re.findall(r'public void (menu\w+)\(\)',source)
for menu in menus:
    role='director' if 'GiamDoc' in menu or 'Giamdoc' in menu else 'manager' if 'QuanLy' in menu else 'employee'
    # Every menu option 0..8 plus invalid text/ranges; EOF is expected for branches requiring further input.
    for option in range(9):
        run(menu+'_'+str(option),f'{option}\n',main='AuditProbe',args=('menu',menu,role))
    run(menu+'_invalid','abc\n-1\n9999999999999999999999\nNaN\n\n0\n',main='AuditProbe',args=('menu',menu,role))

random.seed(20261114)
for i in range(12):
    tokens = [random.choice(['0','1','2','3','4','5','6','7','8','9','-1','abc','NaN','Infinity','2147483648','31/02/2026',';','']) for _ in range(80)]
    run('fuzz_'+str(i),'\n'.join(tokens)+'\n',main='AuditProbe',args=('menu',menus[i%len(menus)],'director'))

unchanged = snapshot()==before
summary=dict(case_count=len(results),production_data_unchanged=unchanged,results=results)
(OUT/'results.json').write_text(json.dumps(summary,ensure_ascii=False,indent=2),encoding='utf-8')
print(json.dumps({k:v for k,v in summary.items() if k!='results'}),flush=True)
print('Timeout/crash/recovered:',json.dumps([r for r in results if r['exit']!=0 or r['exception'] or r['recovered_error']],ensure_ascii=False),flush=True)
if not unchanged:
    raise SystemExit('Production data changed unexpectedly')
