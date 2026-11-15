"""Export BAO_CAO.md to PDF with embedded Vietnamese fonts."""
from pathlib import Path
import re
import sys
from html import escape

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / '.audit/pdf-deps'))
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib.styles import ParagraphStyle
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle
from pypdf import PdfReader

for name, filename in [('Arial', 'arial.ttf'), ('Arial-Bold', 'arialbd.ttf'), ('Arial-Italic', 'ariali.ttf'), ('Arial-BoldItalic', 'arialbi.ttf')]:
    pdfmetrics.registerFont(TTFont(name, str(Path('C:/Windows/Fonts') / filename)))
pdfmetrics.registerFontFamily('Arial', normal='Arial', bold='Arial-Bold', italic='Arial-Italic', boldItalic='Arial-BoldItalic')

source = ROOT / 'audit/BAO_CAO.md'
target = source.with_suffix('.pdf')
width, height = landscape(A4)
body = ParagraphStyle('Body', fontName='Arial', fontSize=10, leading=15, spaceAfter=7)
title = ParagraphStyle('Title', parent=body, fontName='Arial-Bold', fontSize=19, leading=25, spaceAfter=16)
heading = ParagraphStyle('Heading', parent=body, fontName='Arial-Bold', fontSize=13, leading=18, spaceBefore=12, spaceAfter=9, keepWithNext=True)
cell = ParagraphStyle('Cell', parent=body, fontSize=8.5, leading=12, spaceAfter=0, splitLongWords=True)
cell_head = ParagraphStyle('CellHead', parent=cell, fontName='Arial-Bold', textColor=colors.white)

def inline(s):
    s = escape(s)
    s = re.sub(r'`([^`]+)`', r'<font color="#24476a">\1</font>', s)
    return re.sub(r'\*\*([^*]+)\*\*', r'<b>\1</b>', s)

story = []
lines = source.read_text(encoding='utf-8').splitlines()
i = 0
while i < len(lines):
    line = lines[i]
    if not line.strip():
        i += 1
        continue
    if line.startswith('```'):
        i += 1
        while i < len(lines) and not lines[i].startswith('```'):
            story.append(Paragraph(inline(lines[i]), body))
            i += 1
        i += 1
    elif line.startswith('|'):
        rows = []
        while i < len(lines) and lines[i].startswith('|'):
            fields = [s.strip() for s in lines[i].strip('|').split('|')]
            if not all(re.fullmatch(r':?-+:?', s) for s in fields):
                style = cell_head if not rows else cell
                rows.append([Paragraph(inline(s), style) for s in fields])
            i += 1
        available = width - 72
        proportions = [0.065, 0.22, 0.46, 0.255] if len(rows[0]) == 4 else [0.19, 0.405, 0.405]
        table = Table(rows, colWidths=[available*p for p in proportions], repeatRows=1, hAlign='LEFT')
        table.setStyle(TableStyle([
            ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#24476a')),
            ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.HexColor('#f1f5f9'), colors.white]),
            ('VALIGN', (0,0), (-1,-1), 'TOP'),
            ('LEFTPADDING', (0,0), (-1,-1), 7),
            ('RIGHTPADDING', (0,0), (-1,-1), 7),
            ('TOPPADDING', (0,0), (-1,-1), 7),
            ('BOTTOMPADDING', (0,0), (-1,-1), 7),
            ('LINEBELOW', (0,0), (-1,-1), 0.3, colors.HexColor('#cbd5e1')),
        ]))
        story.extend([table, Spacer(1,10)])
    elif line.startswith('# '):
        story.append(Paragraph(inline(line[2:]), title))
        i += 1
    elif line.startswith('## '):
        story.append(Paragraph(inline(line[3:]), heading))
        i += 1
    elif line.startswith('- '):
        story.append(Paragraph(inline(line[2:]), body, bulletText='•'))
        i += 1
    else:
        story.append(Paragraph(inline(line), body))
        i += 1

def footer(canvas, doc):
    canvas.setFont('Arial', 8)
    canvas.setFillColor(colors.HexColor('#64748b'))
    canvas.drawString(36, 20, 'Báo cáo kiểm tra chương trình • 14/11/2026')
    canvas.drawRightString(width-36, 20, f'Trang {doc.page}')

doc = SimpleDocTemplate(str(target), pagesize=(width,height), leftMargin=36, rightMargin=36,
                        topMargin=32, bottomMargin=36, title='Báo cáo kiểm tra chương trình', author='')
doc.build(story, onFirstPage=footer, onLaterPages=footer)
pdf = PdfReader(str(target))
text = '\n'.join(page.extract_text() for page in pdf.pages)
for phrase in ['Báo cáo kiểm tra', 'Kết quả thực chạy', 'Lỗi đã xác nhận', 'Benchmark', 'Giới hạn', '10.028']:
    assert phrase in text, f'Missing PDF text: {phrase}'
print(f'PDF: {target}\nPages: {len(pdf.pages)}\nBytes: {target.stat().st_size}\nVietnamese text: OK')
