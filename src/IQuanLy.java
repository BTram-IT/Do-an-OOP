public interface IQuanLy<T> {
    void them(T item);
    boolean sua(String id, T itemMoi);
    boolean xoa(String id);
    T timKiemTheoMa(String id);
    void xuatDanhSach();
}
