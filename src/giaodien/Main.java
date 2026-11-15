package giaodien;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            Giaodien giaoDien = new Giaodien();
            giaoDien.batDau(sc);
        }
    }
}
