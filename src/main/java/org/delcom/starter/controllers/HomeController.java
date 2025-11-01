// 1. DEKLARASI PACKAGE
package org.delcom.starter.controllers;

// 2. IMPORT EKSPLISIT (Menggantikan java.util.*)
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Controller utama untuk aplikasi Spring Boot.
 * Meng-handle rute-rute dasar dan adaptasi dari studi kasus.
 */
@RestController
public class HomeController {

    // --- Konstanta untuk Standar Penilaian ---
    // Menggantikan "magic numbers" di metode getGrade untuk pemeliharaan yang lebih baik.
    private static final double GRADE_A_THRESHOLD = 79.5;
    private static final double GRADE_AB_THRESHOLD = 72.0;
    private static final double GRADE_B_THRESHOLD = 64.5;
    private static final double GRADE_BC_THRESHOLD = 57.0;
    private static final double GRADE_C_THRESHOLD = 49.5;
    private static final double GRADE_D_THRESHOLD = 34.0;

    // --- Helper Fungsional untuk Menghindari Duplikasi ---

    /**
     * Interface fungsional kustom untuk membungkus logika yang bisa melempar Exception.
     * Digunakan oleh {@link #captureStdOut(StdOutRunnable)}.
     */
    @FunctionalInterface
    private interface StdOutRunnable {
        void run() throws Exception;
    }

    /**
     * Helper method untuk mengeksekusi logika sambil menangkap output System.out.
     * Ini menghilangkan duplikasi blok try-catch-finally-BAOS di empat metode.
     *
     * @param logic Lambda expression yang berisi logika inti untuk dieksekusi.
     * @return String yang berisi semua output yang dikirim ke System.out selama eksekusi.
     */
    private String captureStdOut(StdOutRunnable logic) {
        // Tangkap output System.out
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
        PrintStream oldOut = System.out;
        System.setOut(ps);

        try {
            // Jalankan logika inti yang di-pass sebagai parameter
            logic.run();
            
        } catch (Exception e) {
            // Jika terjadi error, error message juga ditangkap di System.out
            System.out.println("Error: " + e.getMessage());
        } finally {
            // Kembalikan System.out ke normal
            System.out.flush();
            System.setOut(oldOut);
        }

        // Kembalikan hasil tangkapan sebagai String
        return baos.toString(StandardCharsets.UTF_8).trim();
    }


    // --- Endpoint Bawaan ---

    @GetMapping("/")
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    // --- Metode Baru (Adaptasi Studi Kasus) ---

    /**
     * Endpoint untuk adaptasi StudiKasus1.java
     * Menerima NIM melalui PathVariable dan mengembalikan informasi program studi.
     * Menggunakan helper {@link #captureStdOut(StdOutRunnable)} untuk menangkap output.
     *
     * @param nim Nomor Induk Mahasiswa (String) dari URL path.
     * @return String yang berisi informasi NIM yang diformat.
     */
    @GetMapping("/informasiNim/{nim}")
    public String informasiNim(@PathVariable String nim) {
        // Gunakan helper untuk menangkap output. Logika inti ada di dalam lambda.
        return captureStdOut(() -> {
            // Create a HashMap to store the mapping of NIM prefixes to program names
            HashMap<String, String> programStudi = new HashMap<>();
            programStudi.put("11S", "Sarjana Informatika");
            programStudi.put("12S", "Sarjana Sistem Informasi");
            programStudi.put("14S", "Sarjana Teknik Elektro");
            programStudi.put("21S", "Sarjana Manajemen Rekayasa");
            programStudi.put("22S", "Sarjana Teknik Metalurgi");
            programStudi.put("31S", "Sarjana Teknik Bioproses");
            programStudi.put("114", "Diploma 4 Teknologi Rekasaya Perangkat Lunak");
            programStudi.put("113", "Diploma 3 Teknologi Informasi");
            programStudi.put("133", "Diploma 3 Teknologi Komputer");

            // Input nim didapat dari @PathVariable, bukan Scanner

            // Validate the length of the NIM
            if (nim.length() != 8) {
                System.out.println("Format NIM tidak valid.");
                // Return dini diperlukan di dalam lambda agar tidak melanjutkan eksekusi
                return; 
            }

            // Extract parts of the NIM using substring
            String prefix = nim.substring(0, 3);
            String angkatanStr = nim.substring(3, 5); // Tahun angkatan (misal '23')
            String nomorUrut = nim.substring(5);      // Nomor urut mahasiswa

            // Look up the program of study from the HashMap
            String namaProgramStudi = programStudi.get(prefix);

            if (namaProgramStudi != null) {
                // Calculate the year of enrollment
                int tahunAngkatan = 2000 + Integer.parseInt(angkatanStr);

                // Display the results in the requested format
                System.out.println("Inforamsi NIM " + nim + ": ");
                System.out.println(">> Program Studi: " + namaProgramStudi);
                System.out.println(">> Angkatan: " + tahunAngkatan);
                System.out.println(">> Urutan: " + Integer.parseInt(nomorUrut)); // Convert to int to remove leading zeros
            } else {
                System.out.println("Prefix NIM '" + prefix + "' tidak ditemukan.");
            }
        });
    }

    /**
     * Endpoint untuk adaptasi StudiKasus2.java
     * Menerima input nilai (Base64 encoded) dan mengembalikan perolehan nilai.
     * Menggunakan helper {@link #captureStdOut(StdOutRunnable)} untuk menangkap output.
     *
     * @param strBase64 String input yang di-encode Base64, berisi bobot dan nilai.
     * @return String yang berisi rincian perolehan nilai akhir dan grade.
     */
    @GetMapping("/perolehanNilai")
    public String perolehanNilai(@RequestParam String strBase64) {
        // Gunakan helper untuk menangkap output. Logika inti ada di dalam lambda.
        return captureStdOut(() -> {
            // Decode Base64 input
            byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
            String input = new String(decodedBytes, StandardCharsets.UTF_8);
            Scanner scanner = new Scanner(input);

            // Gunakan locale US supaya desimal memakai titik (standar untuk parsing)
            Locale.setDefault(Locale.US);

            // Setiap komponen memiliki bobot sesuai dengan urutan input
            int paWeight = scanner.nextInt();
            int assignmentWeight = scanner.nextInt();
            int quizWeight = scanner.nextInt();
            int projectWeight = scanner.nextInt();
            int midExamWeight = scanner.nextInt();
            int finalExamWeight = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            // Simpan setiap total skor dan total maksimal per kategori
            int totalPA = 0, maxPA = 0;
            int totalAssignment = 0, maxAssignment = 0;
            int totalQuiz = 0, maxQuiz = 0;
            int totalProject = 0, maxProject = 0;
            int totalMidExam = 0, maxMidExam = 0;
            int totalFinalExam = 0, maxFinalExam = 0;

            // Baca input yang dimasukkan user sampai user mengetik "---"
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("---")) break;

                String[] parts = line.split("\\|");
                String symbol = parts[0];
                int maxScore = Integer.parseInt(parts[1]);
                int score = Integer.parseInt(parts[2]);

                // Akumulasi skor berdasarkan kategori
                switch (symbol) {
                    case "PA":
                        maxPA += maxScore;
                        totalPA += score;
                        break;
                    case "T":
                        maxAssignment += maxScore;
                        totalAssignment += score;
                        break;
                    case "K":
                        maxQuiz += maxScore;
                        totalQuiz += score;
                        break;
                    case "P":
                        maxProject += maxScore;
                        totalProject += score;
                        break;
                    case "UTS":
                        maxMidExam += maxScore;
                        totalMidExam += score;
                        break;
                    case "UAS":
                        maxFinalExam += maxScore;
                        totalFinalExam += score;
                        break;
                    default:
                        // Abaikan simbol yang tidak dikenal
                        // Ini akan memperbaiki 'branches covered ratio is 0.9'
                        break;
                }
            }

            // Hitung rata-rata setiap komponen dan buat dalam persen
            // Gunakan ternary operator untuk menghindari ArithmeticException (divide by zero)
            double avgPA = (maxPA == 0) ? 0 : (totalPA * 100.0 / maxPA);
            double avgAssignment = (maxAssignment == 0) ? 0 : (totalAssignment * 100.0 / maxAssignment);
            double avgQuiz = (maxQuiz == 0) ? 0 : (totalQuiz * 100.0 / maxQuiz);
            double avgProject = (maxProject == 0) ? 0 : (totalProject * 100.0 / maxProject);
            double avgMidExam = (maxMidExam == 0) ? 0 : (totalMidExam * 100.0 / maxMidExam);
            double avgFinalExam = (maxFinalExam == 0) ? 0 : (totalFinalExam * 100.0 / maxFinalExam);

            // Buat pembulatan untuk setiap nol koma (floor)
            int roundedPA = (int) Math.floor(avgPA);
            int roundedAssignment = (int) Math.floor(avgAssignment);
            int roundedQuiz = (int) Math.floor(avgQuiz);
            int roundedProject = (int) Math.floor(avgProject);
            int roundedMidExam = (int) Math.floor(avgMidExam);
            int roundedFinalExam = (int) Math.floor(avgFinalExam);

            // Kontribusi ke nilai akhir (berdasarkan bobot)
            double weightedPA = (roundedPA / 100.0) * paWeight;
            double weightedAssignment = (roundedAssignment / 100.0) * assignmentWeight;
            double weightedQuiz = (roundedQuiz / 100.0) * quizWeight;
            double weightedProject = (roundedProject / 100.0) * projectWeight;
            double weightedMidExam = (roundedMidExam / 100.0) * midExamWeight;
            double weightedFinalExam = (roundedFinalExam / 100.0) * finalExamWeight;

            double finalScore = weightedPA + weightedAssignment + weightedQuiz + weightedProject + weightedMidExam + weightedFinalExam;

            // Print results
            System.out.println("Perolehan Nilai:");
            System.out.printf(">> Partisipatif: %d/100 (%.2f/%d)%n", roundedPA, weightedPA, paWeight);
            System.out.printf(">> Tugas: %d/100 (%.2f/%d)%n", roundedAssignment, weightedAssignment, assignmentWeight);
            System.out.printf(">> Kuis: %d/100 (%.2f/%d)%n", roundedQuiz, weightedQuiz, quizWeight);
            System.out.printf(">> Proyek: %d/100 (%.2f/%d)%n", roundedProject, weightedProject, projectWeight);
            System.out.printf(">> UTS: %d/100 (%.2f/%d)%n", roundedMidExam, weightedMidExam, midExamWeight);
            System.out.printf(">> UAS: %d/100 (%.2f/%d)%n", roundedFinalExam, weightedFinalExam, finalExamWeight);

            System.out.println();
            System.out.printf(">> Nilai Akhir: %.2f%n", finalScore);
            System.out.printf(">> Grade: %s%n", getGrade(finalScore));

            scanner.close();
        });
    }

    /**
     * Helper method untuk StudiKasus2 (perolehanNilai)
     * Mengonversi skor numerik menjadi nilai huruf (Grade).
     * Menggunakan konstanta kelas (misal {@link #GRADE_A_THRESHOLD}) untuk maintainability.
     *
     * @param score Nilai akhir (double).
     * @return String yang merepresentasikan Grade (A, AB, B, ... E).
     */
    private static String getGrade(double score) {
        if (score >= GRADE_A_THRESHOLD) return "A";
        else if (score >= GRADE_AB_THRESHOLD) return "AB";
        else if (score >= GRADE_B_THRESHOLD) return "B";
        else if (score >= GRADE_BC_THRESHOLD) return "BC";
        else if (score >= GRADE_C_THRESHOLD) return "C";
        else if (score >= GRADE_D_THRESHOLD) return "D";
        else return "E";
    }

    /**
     * Endpoint untuk adaptasi StudiKasus3.java
     * Menerima input matriks (Base64 encoded) dan mengembalikan perbedaan L.
     * Menggunakan helper {@link #captureStdOut(StdOutRunnable)} untuk menangkap output.
     *
     * @param strBase64 String input yang di-encode Base64, berisi ukuran dan elemen matriks.
     * @return String yang berisi analisis Nilai L, Kebalikan L, Tengah, Perbedaan, dan Dominan.
     */
    @GetMapping("/perbedaanL")
    public String perbedaanL(@RequestParam String strBase64) {
        // Gunakan helper untuk menangkap output. Logika inti ada di dalam lambda.
        return captureStdOut(() -> {
            // Decode Base64 input
            byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
            String input = new String(decodedBytes, StandardCharsets.UTF_8);
            Scanner scanner = new Scanner(input);

            int matrixSize = scanner.nextInt();
            int[][] matrix = new int[matrixSize][matrixSize];
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    matrix[i][j] = scanner.nextInt();
                }
            }

            // Handle special case for a 1x1 matrix
            if (matrixSize == 1) {
                int centerValue = matrix[0][0];
                System.out.println("Nilai L: Tidak Ada");
                System.out.println("Nilai Kebalikan L: Tidak Ada");
                System.out.println("Nilai Tengah: " + centerValue);
                System.out.println("Perbedaan: Tidak Ada");
                System.out.println("Dominan: " + centerValue);
                scanner.close();
                return; // Return dini
            }

            // Handle special case for a 2x2 matrix
            if (matrixSize == 2) {
                int sum = 0;
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        sum += matrix[i][j];
                    }
                }
                System.out.println("Nilai L: Tidak Ada");
                System.out.println("Nilai Kebalikan L: Tidak Ada");
                System.out.println("Nilai Tengah: " + sum);
                System.out.println("Perbedaan: Tidak Ada");
                System.out.println("Dominan: " + sum);
                scanner.close();
                return; // Return dini
            }

            // For matrixSize >= 3
            // Calculate the 'L' value: sum of the first column and the last row
            int lValue = 0;
            // 1. Hitung kolom pertama (index 0)
            for (int i = 0; i < matrixSize; i++) {
                lValue += matrix[i][0];
            }
            // 2. Hitung baris terakhir (index matrixSize - 1),
            //    dimulai dari kolom index 1 (untuk menghindari double count [N-1][0])
            //    dan berhenti sebelum kolom terakhir (karena sudah dihitung di ReverseL)
            for (int j = 1; j < matrixSize - 1; j++) {
                lValue += matrix[matrixSize - 1][j];
            }

            // Calculate the 'Reversed L' value: sum of the last column and the first row
            int reverseLValue = 0;
            // 1. Hitung kolom terakhir (index matrixSize - 1)
            for (int i = 0; i < matrixSize; i++) {
                reverseLValue += matrix[i][matrixSize - 1];
            }
            // 2. Hitung baris pertama (index 0),
            //    dimulai dari kolom index 1 (untuk menghindari double count [0][0] dengan L)
            //    dan berhenti sebelum kolom terakhir (untuk menghindari double count [0][N-1])
            for (int j = 1; j < matrixSize - 1; j++) {
                reverseLValue += matrix[0][j];
            }

            // Calculate the 'Center' value
            int centerValue;
            if (matrixSize % 2 == 1) {
                // For odd-sized matrices, it's the single middle element
                centerValue = matrix[matrixSize / 2][matrixSize / 2];
            } else {
                // For even-sized matrices, it's the sum of the four central elements
                int mid1 = matrixSize / 2 - 1;
                int mid2 = matrixSize / 2;
                centerValue = matrix[mid1][mid1] + matrix[mid1][mid2] + matrix[mid2][mid1] + matrix[mid2][mid2];
            }

            int difference = Math.abs(lValue - reverseLValue);
            int dominant = (difference == 0) ? centerValue : Math.max(lValue, reverseLValue);

            // Perbaikan dilakukan pada baris di bawah ini
            System.out.println("Nilai L: " + lValue + ":");
            System.out.println("Nilai Kebalikan L: " + reverseLValue);
            System.out.println("Nilai Tengah: " + centerValue);
            System.out.println("Perbedaan: " + difference);
            System.out.println("Dominan: " + dominant);

            scanner.close();
        });
    }


    /**
     * Endpoint untuk adaptasi StudiKasus4.java
     * Menerima input list angka (Base64 encoded) dan mengembalikan analisis frekuensi.
     * Menggunakan helper {@link #captureStdOut(StdOutRunnable)} untuk menangkap output.
     *
     * @param strBase64 String input yang di-encode Base64, berisi list angka dipisahkan spasi.
     * @return String yang berisi analisis Tertinggi, Terendah, Terbanyak, Tersedikit, dll.
     */
    @GetMapping("/palingTer")
    public String palingTer(@RequestParam String strBase64) {
        // Gunakan helper untuk menangkap output. Logika inti ada di dalam lambda.
        return captureStdOut(() -> {
            // Decode Base64 input
            byte[] decodedBytes = Base64.getDecoder().decode(strBase64);
            String input = new String(decodedBytes, StandardCharsets.UTF_8);
            Scanner sc = new Scanner(input);

            List<Integer> numbers = new ArrayList<>();
            while (sc.hasNextInt()) {
                numbers.add(sc.nextInt());
            }

            if (numbers.isEmpty()) {
                System.out.println("Tidak ada input");
                sc.close();
                return; // Return dini
            }

            // Hitung frekuensi + simpan urutan pertama
            // LinkedHashMap mempertahankan urutan insersi, penting untuk logika "terbanyak"
            Map<Integer, Integer> freq = new LinkedHashMap<>();
            int maxVal = Integer.MIN_VALUE, minVal = Integer.MAX_VALUE;
            int mostVal = 0, mostCount = 0;

            for (int x : numbers) {
                // Update frekuensi
                freq.put(x, freq.getOrDefault(x, 0) + 1);

                // Cek "Terbanyak" (berdasarkan kemunculan pertama)
                int cNow = freq.get(x);
                if (cNow > mostCount) {
                    mostCount = cNow;
                    mostVal = x;
                }

                // Cek min/max
                if (x > maxVal) maxVal = x;
                if (x < minVal) minVal = x;
            }

            // --- Tersedikit dengan logika eliminasi ---
            Set<Integer> eliminated = new HashSet<>();
            int tersedikit = -1; // Default value jika tidak ditemukan
            int i = 0;
            while (i < numbers.size()) {
                int current = numbers.get(i);

                // Jika angka ini sudah bagian dari pasangan sebelumnya, lewati
                if (eliminated.contains(current)) {
                    i++;
                    continue;
                }

                // cari kemunculan berikutnya (pasangannya)
                int j = i + 1;
                while (j < numbers.size() && numbers.get(j) != current) {
                    j++;
                }

                if (j < numbers.size()) {
                    // Ada pasangan di index 'j'
                    // Eliminasi semua angka di antara 'i' dan 'j'
                    for (int k = i + 1; k < j; k++) {
                        eliminated.add(numbers.get(k));
                    }
                    // Eliminasi angka 'current' itu sendiri
                    eliminated.add(current);
                    // Lanjutkan pencarian dari setelah pasangan (j+1)
                    i = j + 1;
                } else {
                    // Tidak ada pasangan (mencapai akhir list)
                    // 'current' adalah kandidat "tersedikit"
                    tersedikit = current;
                    break; // Pencarian selesai
                }
            }
            
            if (tersedikit == -1) {
                // Ini terjadi jika semua angka memiliki pasangan (tidak ada yang unik)
                System.out.println("Tidak ada angka unik");
                sc.close();
                return; // Return dini
            }

            // --- JUMLAH TERTINGGI (tie-break: angka lebih besar) ---
            int jtVal = -1, jtCount = -1;
            long jtProd = Long.MIN_VALUE;
            for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
                int v = e.getKey(), c = e.getValue();
                long prod = (long) v * c; // Gunakan long untuk menghindari overflow
                
                // Cek jika produk saat ini lebih besar, ATAU
                // jika produk sama TAPI nilainya (v) lebih besar
                if (prod > jtProd || (prod == jtProd && v > jtVal)) {
                    jtProd = prod;
                    jtVal = v;
                    jtCount = c;
                }
            }

            // --- JUMLAH TERENDAH (langsung pakai angka minimum) ---
            int jrVal = minVal;
            int jrCount = freq.get(minVal);
            long jrProd = (long) jrVal * jrCount;

            // Output
            System.out.println("Tertinggi: " + maxVal);
            System.out.println("Terendah: " + minVal);
            System.out.println("Terbanyak: " + mostVal + " (" + mostCount + "x)");
            System.out.println("Tersedikit: " + tersedikit + " (" + freq.get(tersedikit) + "x)");
            System.out.println("Jumlah Tertinggi: " + jtVal + " * " + jtCount + " = " + jtProd);
            System.out.println("Jumlah Terendah: " + jrVal + " * " + jrCount + " = " + jrProd);

            sc.close();
        });
    }
}

