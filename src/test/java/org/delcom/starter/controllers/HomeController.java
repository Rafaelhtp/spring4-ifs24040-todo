package org.delcom.starter.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class HomeControllerTest {

    /**
     * Helper method untuk meng-encode string input tes menjadi Base64.
     */
    private String encodeBase64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    // --- Tes Asli ---

    @Test
    @DisplayName("Mengembalikan pesan selamat datang yang benar")
    void hello_ShouldReturnWelcomeMessage() {
        // Arrange
        HomeController controller = new HomeController();

        // Act
        String result = controller.hello();

        // Assert
        assertEquals("Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!", result);
    }

    @Test
    @DisplayName("Mengembalikan pesan sapaan yang dipersonalisasi")
    void helloWithName_ShouldReturnPersonalizedGreeting() {
        // Arrange
        HomeController controller = new HomeController();

        // Act
        String result = controller.sayHello("Abdullah");

        // Assert
        assertEquals("Hello, Abdullah!", result);
    }

    // --- Tes untuk informasiNim ---

    @Test
    @DisplayName("informasiNim - NIM Valid (11S)")
    void informasiNim_Valid() {
        HomeController controller = new HomeController();
        String nim = "11S24001";
        
        String expected = String.join(System.lineSeparator(),
                "Inforamsi NIM 11S24001: ",
                ">> Program Studi: Sarjana Informatika",
                ">> Angkatan: 2024",
                ">> Urutan: 1"
        );
        
        String result = controller.informasiNim(nim);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("informasiNim - NIM Panjang Tidak Valid")
    void informasiNim_InvalidLength() {
        HomeController controller = new HomeController();
        String nim = "11S24";
        String expected = "Format NIM tidak valid.";
        
        String result = controller.informasiNim(nim);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("informasiNim - Prefix NIM Tidak Dikenal")
    void informasiNim_InvalidPrefix() {
        HomeController controller = new HomeController();
        String nim = "99S24001";
        String expected = "Prefix NIM '99S' tidak ditemukan.";
        
        String result = controller.informasiNim(nim);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("informasiNim - Input Parse Error (Memicu Catch)")
    void informasiNim_InvalidParse() {
        HomeController controller = new HomeController();
        // "11SXX001" akan gagal di Integer.parseInt("XX")
        String nim = "11SXX001";
        String result = controller.informasiNim(nim);
        // Ini akan menguji blok catch
        assertTrue(result.startsWith("Error:"));
    }

    // --- Tes untuk perolehanNilai ---

    @Test
    @DisplayName("perolehanNilai - Skenario Kalkulasi Lengkap (Grade A)")
    void perolehanNilai_FullScenario() {
        HomeController controller = new HomeController();
        String input = String.join("\n",
                "10 15 10 15 20 30",
                "PA|100|80", "T|100|90", "K|100|85", "P|100|95", "UTS|100|75", "UAS|100|88", "---"
        );
        String base64Input = encodeBase64(input);
        
        String expected = String.join(System.lineSeparator(),
                "Perolehan Nilai:",
                ">> Partisipatif: 80/100 (8.00/10)",
                ">> Tugas: 90/100 (13.50/15)",
                ">> Kuis: 85/100 (8.50/10)",
                ">> Proyek: 95/100 (14.25/15)",
                ">> UTS: 75/100 (15.00/20)",
                ">> UAS: 88/100 (26.40/30)",
                "", // Baris kosong
                ">> Nilai Akhir: 85.65",
                ">> Grade: A"
        );
        
        String result = controller.perolehanNilai(base64Input);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade AB")
    void perolehanNilai_GradeAB() {
        HomeController controller = new HomeController();
        // Nilai 75 akan menghasilkan Grade AB
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|75", "T|100|75", "K|100|75", "P|100|75", "UTS|100|75", "UAS|100|75", "---");
        String base64Input = encodeBase64(input);
        String result = controller.perolehanNilai(base64Input);
        assertTrue(result.contains(">> Nilai Akhir: 75.00") && result.contains(">> Grade: AB"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade B")
    void perolehanNilai_GradeB() {
        HomeController controller = new HomeController();
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|65", "T|100|65", "K|100|65", "P|100|65", "UTS|100|65", "UAS|100|65", "---");
        String base64Input = encodeBase64(input);
        
        String result = controller.perolehanNilai(base64Input);
        assertTrue(result.contains(">> Nilai Akhir: 65.00") && result.contains(">> Grade: B"));
    }
    
    @Test
    @DisplayName("perolehanNilai - Skenario Grade BC")
    void perolehanNilai_GradeBC() {
        HomeController controller = new HomeController();
        // Input data (semua nilai 60) -> 60.00 -> Grade BC
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|60", "T|100|60", "K|100|60", "P|100|60", "UTS|100|60", "UAS|100|60", "---");
        String base64Input = encodeBase64(input);
        
        String result = controller.perolehanNilai(base64Input);
        assertTrue(result.contains(">> Nilai Akhir: 60.00") && result.contains(">> Grade: BC"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade D")
    void perolehanNilai_GradeD() {
        HomeController controller = new HomeController();
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|40", "T|100|40", "K|100|40", "P|100|40", "UTS|100|40", "UAS|100|40", "---");
        String base64Input = encodeBase64(input);
        
        String result = controller.perolehanNilai(base64Input);
        assertTrue(result.contains(">> Nilai Akhir: 40.00") && result.contains(">> Grade: D"));
    }

    // --- TES INI DIPERBARUI UNTUK MENGUJI avgFinalExam == 0 ---
    @Test
    @DisplayName("perolehanNilai - Skenario Input Jarang (Sparse)")
    void perolehanNilai_SparseInput() {
        HomeController controller = new HomeController();
        // Hanya menguji PA dan UTS. Lainnya harus 0.
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|80", "UTS|100|50", "---");
        String base64Input = encodeBase64(input);
        
        String result = controller.perolehanNilai(base64Input);
        // Memastikan cabang (max... == 0) ter-eksekusi
        assertTrue(result.contains(">> Partisipatif: 80/100 (8.00/10)"));
        assertTrue(result.contains(">> Tugas: 0/100 (0.00/15)"));
        assertTrue(result.contains(">> Kuis: 0/100 (0.00/10)"));
        assertTrue(result.contains(">> Proyek: 0/100 (0.00/15)"));
        assertTrue(result.contains(">> UTS: 50/100 (10.00/20)"));
        // INI YANG DIPERBAIKI: Memastikan UAS juga 0
        assertTrue(result.contains(">> UAS: 0/100 (0.00/30)"));
        assertTrue(result.contains(">> Nilai Akhir: 18.00")); // 8.00 + 10.00
        assertTrue(result.contains(">> Grade: E")); // 18.00 < 34
    }
    
    @Test
    @DisplayName("perolehanNilai - Skenario Simbol Tidak Valid")
    void perolehanNilai_InvalidSymbol() {
        HomeController controller = new HomeController();
        // Menguji "INVALID" yang akan diabaikan oleh switch
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|80", "INVALID|100|100", "UAS|100|80", "---");
        String base64Input = encodeBase64(input);
        
        String result = controller.perolehanNilai(base64Input);
        // Nilai dari "INVALID" harus diabaikan
        // PA(8.00) + UAS(24.00) = 32.00
        assertTrue(result.contains(">> Nilai Akhir: 32.00"));
        assertTrue(result.contains(">> Grade: E")); // 32 < 34
    }
    
    // --- TES INI UNTUK MENGUJI while(!hasNextLine()) ---
    @Test
    @DisplayName("perolehanNilai - Skenario Tanpa Terminator '---'")
    void perolehanNilai_NoTerminator() {
        HomeController controller = new HomeController();
        // Input ini akan berhenti karena hasNextLine() false, bukan 'break'
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|80", "UAS|100|80");
        String base64Input = encodeBase64(input);
        
        String result = controller.perolehanNilai(base64Input);
        // Hasilnya harus sama dengan tes InvalidSymbol
        assertTrue(result.contains(">> Nilai Akhir: 32.00"));
        assertTrue(result.contains(">> Grade: E"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade C")
    void perolehanNilai_GradeC() {
        HomeController controller = new HomeController();
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|50", "T|100|50", "K|100|50", "P|100|50", "UTS|100|50", "UAS|100|50", "---");
        String base64Input = encodeBase64(input);
        String result = controller.perolehanNilai(base64Input);
        assertTrue(result.contains(">> Nilai Akhir: 50.00") && result.contains(">> Grade: C"));
    }

    @Test
    @DisplayName("perolehanNilai - Skenario Grade E")
    void perolehanNilai_GradeE() {
        HomeController controller = new HomeController();
        String input = String.join("\n", "10 15 10 15 20 30", "PA|100|0", "T|100|0", "K|100|0", "P|100|0", "UTS|100|0", "UAS|100|0", "---");
        String base64Input = encodeBase64(input);
        String result = controller.perolehanNilai(base64Input);
        assertTrue(result.contains(">> Nilai Akhir: 0.00") && result.contains(">> Grade: E"));
    }

    @Test
    @DisplayName("perolehanNilai - Input Malformed (Memicu Catch Block)")
    void perolehanNilai_InvalidInput() {
        HomeController controller = new HomeController();
        String base64Input = encodeBase64("halo"); // Gagal di scanner.nextInt()
        String result = controller.perolehanNilai(base64Input);
        assertTrue(result.startsWith("Error:"));
    }

    // --- Tes untuk perbedaanL ---

    @Test
    @DisplayName("perbedaanL - Matriks 3x3 (Ganjil, Dominan=Tengah)")
    void perbedaanL_Matrix3x3() {
        HomeController controller = new HomeController();
        String input = String.join("\n", "3", "1 2 3", "4 5 6", "7 8 9");
        String base64Input = encodeBase64(input);
        String expected = String.join(System.lineSeparator(), "Nilai L: 20:", "Nilai Kebalikan L: 20", "Nilai Tengah: 5", "Perbedaan: 0", "Dominan: 5");
        String result = controller.perbedaanL(base64Input);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("perbedaanL - Matriks 4x4 (Genap, Dominan=L)")
    void perbedaanL_Matrix4x4() {
        HomeController controller = new HomeController();
        String input = String.join("\n", "4", "1 2 3 4", "5 6 7 8", "9 10 11 12", "13 14 15 16");
        String base64Input = encodeBase64(input);
        String expected = String.join(System.lineSeparator(), "Nilai L: 57:", "Nilai Kebalikan L: 45", "Nilai Tengah: 34", "Perbedaan: 12", "Dominan: 57");
        String result = controller.perbedaanL(base64Input);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("perbedaanL - Matriks 1x1 (Edge Case)")
    void perbedaanL_Matrix1x1() {
        HomeController controller = new HomeController();
        String base64Input = encodeBase64("1\n42");
        String expected = String.join(System.lineSeparator(), "Nilai L: Tidak Ada", "Nilai Kebalikan L: Tidak Ada", "Nilai Tengah: 42", "Perbedaan: Tidak Ada", "Dominan: 42");
        String result = controller.perbedaanL(base64Input);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("perbedaanL - Matriks 2x2 (Edge Case)")
    void perbedaanL_Matrix2x2() {
        HomeController controller = new HomeController();
        String base64Input = encodeBase64("2\n1 2\n3 4");
        String expected = String.join(System.lineSeparator(), "Nilai L: Tidak Ada", "Nilai Kebalikan L: Tidak Ada", "Nilai Tengah: 10", "Perbedaan: Tidak Ada", "Dominan: 10");
        String result = controller.perbedaanL(base64Input);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("perbedaanL - Input Malformed (Memicu Catch)")
    void perbedaanL_InvalidInput() {
        HomeController controller = new HomeController();
        String base64Input = encodeBase64("abc"); // Gagal di scanner.nextInt()
        String result = controller.perbedaanL(base64Input);
        assertTrue(result.startsWith("Error:"));
    }

    // --- Tes untuk palingTer ---

    @Test
    @DisplayName("palingTer - Skenario Dasar")
    void palingTer_BasicScenario() {
        HomeController controller = new HomeController();
        String base64Input = encodeBase64("10 5 8 10 9 5 10 8 7");
        String expected = String.join(System.lineSeparator(), "Tertinggi: 10", "Terendah: 5", "Terbanyak: 10 (3x)", "Tersedikit: 9 (1x)", "Jumlah Tertinggi: 10 * 3 = 30", "Jumlah Terendah: 5 * 2 = 10");
        String result = controller.palingTer(base64Input);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("palingTer - Input Kosong (Edge Case)")
    void palingTer_EmptyInput() {
        HomeController controller = new HomeController();
        String base64Input = encodeBase64(""); // Input kosong
        String expected = "Tidak ada input";
        String result = controller.palingTer(base64Input);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("palingTer - Tidak Ada Angka Unik (Edge Case)")
    void palingTer_NoUniqueNumber() {
        HomeController controller = new HomeController();
        String base64Input = encodeBase64("10 20 10 20");
        String expected = "Tidak ada angka unik";
        String result = controller.palingTer(base64Input);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("palingTer - Skenario Tie-Breaker (Angka Besar Menang)")
    void palingTer_TieBreakerWins() {
        HomeController controller = new HomeController();
        // L: 10*2=20. R: 20*1=20. Tie-break, 20 > 10.
        // Input: 10 20 10 9
        String base64Input = encodeBase64("10 20 10 9");
        String result = controller.palingTer(base64Input);
        
        // Tes ini menguji cabang (prod == jtProd && v > jtVal)
        assertTrue(result.contains("Tersedikit: 9 (1x)"));
        assertTrue(result.contains("Jumlah Tertinggi: 20 * 1 = 20"));
    }
    
    @Test
    @DisplayName("palingTer - Skenario Tie-Breaker (Angka Kecil Kalah)")
    void palingTer_TieBreakerLoses() {
        HomeController controller = new HomeController();
        // L: 20*1=20. R: 10*2=20. Tie-break, 10 < 20 (tidak ganti).
        // Input: 20 10 10 9
        String base64Input = encodeBase64("20 10 10 9");
        String result = controller.palingTer(base64Input);
        
        // Tes ini menguji cabang (prod == jtProd && v > jtVal) yang return false
        assertTrue(result.contains("Tersedikit: 20 (1x)")); // Perbaikan dari tes sebelumnya
        assertTrue(result.contains("Jumlah Tertinggi: 20 * 1 = 20"));
    }

    @Test
    @DisplayName("palingTer - Skenario (prod > jtProd) di Tengah Loop")
    void palingTer_ProdGtJtProdMidLoop() {
        HomeController controller = new HomeController();
        // Input: 10 9 20 9 (menambahkan 9 agar ada 'tersedikit')
        // freq = {10:1, 9:2, 20:1}
        // 1. e=(10,1): prod=10. jtProd=10, jtVal=10. (Tes 'prod > MIN_VALUE')
        // 2. e=(9,2): prod=18. (Tes 'prod > jtProd') -> jtProd=18, jtVal=9.
        // 3. e=(20,1): prod=20. (Tes 'prod > jtProd') -> jtProd=20, jtVal=20.
        String base64Input = encodeBase64("10 9 20 9");
        String result = controller.palingTer(base64Input);
        
        // Tes ini memastikan cabang (prod > jtProd) diuji setelah loop pertama
        assertTrue(result.contains("Tersedikit: 10 (1x)"));
        assertTrue(result.contains("Jumlah Tertinggi: 20 * 1 = 20"));
    }

    @Test
    @DisplayName("palingTer - Input Teks (Bukan Angka)")
    void palingTer_TextInput() {
        HomeController controller = new HomeController();
        String base64Input = encodeBase64("abc");
        String result = controller.palingTer(base64Input);
        // Ini adalah perilaku yang benar, bukan error
        assertEquals("Tidak ada input", result);
    }

    @Test
    @DisplayName("palingTer - Input Base64 Tidak Valid (Memicu Catch)")
    void palingTer_InvalidBase64() {
        HomeController controller = new HomeController();
        String invalidBase64 = "!!INVALID!!"; // String ini bukan Base64 yang valid
        String result = controller.palingTer(invalidBase64);
        // Ini akan memicu IllegalArgumentException di dalam Base64.getDecoder()
        assertTrue(result.startsWith("Error:"));
    }
}