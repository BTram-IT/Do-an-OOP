package util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Bảng console tự căn cột theo tiêu đề và nội dung dài nhất ở mỗi lần in. */
public class BangConsole {
    private final String[] tieuDe;
    private final List<String[]> cacDong = new ArrayList<>();
    private final boolean[] canPhai;

    public BangConsole(String... tieuDe) {
        if (tieuDe.length == 0) {
            throw new IllegalArgumentException("Bảng phải có ít nhất một cột!");
        }
        this.tieuDe = new String[tieuDe.length];
        for (int i = 0; i < tieuDe.length; i++) {
            this.tieuDe[i] = chuanHoaO(tieuDe[i]);
        }
        this.canPhai = new boolean[tieuDe.length];
    }

    public BangConsole canPhai(int... cacCot) {
        for (int cot : cacCot) {
            this.canPhai[cot] = true;
        }
        return this;
    }

    public BangConsole themDong(Object... giaTri) {
        if (giaTri.length != this.tieuDe.length) {
            throw new IllegalArgumentException("Số ô dữ liệu phải bằng số cột của bảng!");
        }
        String[] dong = new String[giaTri.length];
        for (int i = 0; i < giaTri.length; i++) {
            dong[i] = chuanHoaO(giaTri[i]);
            if (giaTri[i] instanceof Number) {
                this.canPhai[i] = true;
            }
        }
        this.cacDong.add(dong);
        return this;
    }

    public static String tuDoiTuong(HienThiBang doiTuong) {
        return new BangConsole(doiTuong.layTieuDeBang())
                .themDong(doiTuong.layDongBang()).toString();
    }

    /** Gom cả danh sách trước khi in để mọi hàng cùng dùng một bộ độ rộng cột. */
    public static String tuDanhSach(Iterable<? extends HienThiBang> danhSach) {
        BangConsole bang = null;
        String[] tieuDeChung = null;
        int stt = 0;
        for (HienThiBang doiTuong : danhSach) {
            if (doiTuong == null) {
                continue;
            }
            String[] tieuDeDong = doiTuong.layTieuDeBang();
            if (bang == null) {
                tieuDeChung = tieuDeDong;
                String[] tieuDe = new String[tieuDeDong.length + 1];
                tieuDe[0] = "STT";
                System.arraycopy(tieuDeDong, 0, tieuDe, 1, tieuDeDong.length);
                bang = new BangConsole(tieuDe);
            } else if (!Arrays.equals(tieuDeChung, tieuDeDong)) {
                throw new IllegalArgumentException("Các hàng phải dùng cùng tiêu đề cột!");
            }
            Object[] duLieu = doiTuong.layDongBang();
            Object[] dong = new Object[duLieu.length + 1];
            dong[0] = ++stt;
            System.arraycopy(duLieu, 0, dong, 1, duLieu.length);
            bang.themDong(dong);
        }
        return bang == null ? "[Thông báo] Danh sách trống." : bang.toString();
    }

    /** Các thuộc tính riêng hiển thị nhiều dòng trong một ô, với dấu ':' thẳng hàng. */
    public static String thongTin(String[] nhan, Object... giaTri) {
        if (nhan.length != giaTri.length) {
            throw new IllegalArgumentException("Số nhãn phải bằng số giá trị!");
        }
        int doRongNhan = 0;
        for (String ten : nhan) {
            doRongNhan = Math.max(doRongNhan, doRong(chuanHoaO(ten)));
        }
        StringBuilder ketQua = new StringBuilder();
        for (int i = 0; i < nhan.length; i++) {
            if (i > 0) ketQua.append(System.lineSeparator());
            String ten = chuanHoaO(nhan[i]);
            ketQua.append(ten).append(" ".repeat(doRongNhan - doRong(ten)))
                    .append(": ").append(chuanHoaO(giaTri[i]));
        }
        return ketQua.toString();
    }

    private static String chuanHoaO(Object giaTri) {
        String chuoi = giaTri == null ? "-" : String.valueOf(giaTri);
        return Normalizer.normalize(chuoi, Normalizer.Form.NFC).replace("\t", "    ");
    }

    private static int doRong(String chuoi) {
        return (int) chuoi.codePoints().filter(kyTu -> {
            int loai = Character.getType(kyTu);
            return loai != Character.NON_SPACING_MARK && loai != Character.COMBINING_SPACING_MARK
                    && loai != Character.ENCLOSING_MARK;
        }).count();
    }

    @Override
    public String toString() {
        int[] doRongCot = new int[this.tieuDe.length];
        List<String[]> tatCaDong = new ArrayList<>();
        tatCaDong.add(this.tieuDe);
        tatCaDong.addAll(this.cacDong);
        for (String[] dong : tatCaDong) {
            for (int cot = 0; cot < dong.length; cot++) {
                for (String dongCon : dong[cot].split("\\R", -1)) {
                    doRongCot[cot] = Math.max(doRongCot[cot], doRong(dongCon));
                }
            }
        }

        StringBuilder vien = new StringBuilder("+");
        for (int doRong : doRongCot) {
            vien.append("-".repeat(doRong + 2)).append('+');
        }
        String xuongDong = System.lineSeparator();
        StringBuilder bang = new StringBuilder(vien).append(xuongDong);
        for (int hang = 0; hang < tatCaDong.size(); hang++) {
            String[][] cacO = new String[this.tieuDe.length][];
            int chieuCao = 1;
            for (int cot = 0; cot < cacO.length; cot++) {
                cacO[cot] = tatCaDong.get(hang)[cot].split("\\R", -1);
                chieuCao = Math.max(chieuCao, cacO[cot].length);
            }
            for (int dongCon = 0; dongCon < chieuCao; dongCon++) {
                bang.append('|');
                for (int cot = 0; cot < cacO.length; cot++) {
                    String noiDung = dongCon < cacO[cot].length ? cacO[cot][dongCon] : "";
                    String khoangTrang = " ".repeat(doRongCot[cot] - doRong(noiDung));
                    bang.append(' ');
                    if (hang > 0 && this.canPhai[cot]) bang.append(khoangTrang);
                    bang.append(noiDung);
                    if (hang == 0 || !this.canPhai[cot]) bang.append(khoangTrang);
                    bang.append(" |");
                }
                bang.append(xuongDong);
            }
            // Phân cách các bản ghi nhiều dòng để dễ phân biệt thông tin từng đối tượng.
            if (hang == 0 || chieuCao > 1 || hang == tatCaDong.size() - 1) {
                bang.append(vien).append(xuongDong);
            }
        }
        return bang.substring(0, bang.length() - xuongDong.length());
    }
}
