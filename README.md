# Spring v4.0 Starter Proyek

## Logs

### 29-10-2025

- Melakukan inisialisasi proyek
- Nambahin semua 4 studi kasus PBO sebelumnya sebagai endpoint API baru di 'HomeController': informasi nim, perolehan nilai, perbedaan L, paling ter
- Memperbaiki unit test biar berhasil buat ngeters semua skenario (happy path & edge case)
- Ngebenerin beberapa bug di controller (terutama masalah line ending `\n` vs `%n` yang bikin tes gagal )

# Fitur Utama:
- `GET /informasiNim/{nim}`: Menganalisis NIM untuk mendapatkan info Program Studi, Angkatan, dan No. Urut.
- `GET /perolehanNilai`: Menerima input Base64, menghitung nilai akhir, dan menentukan grade (A, B, C, dst.).
- `GET /perbedaanL`: Menerima matriks Base64, menghitung nilai 'L', 'Kebalikan L', 'Tengah', dan 'Dominan'.
- `GET /palingTer`: Menerima list angka Base64, melakukan analisis frekuensi (Tertinggi, Terendah, Terbanyak, Tersedikit).

# Perbaikan & Konfigurasi:

- Mengonfigurasi `pom.xml` (jacoco-maven-plugin) untuk mengecualikan `SpringStarterApplication.class` dari kalkulasi coverage agar build dapat lolos dengan 100% coverage pada controller.

## Syntax

### Melakukan Instal Ulang Kebutuhan Paket

command: `mvn clean install`

### Menjalankan Aplikasi

Command: `mvn spring-boot:run`

URL: http://localhost:8080

### Menjalankan Test Covertage

command: `./mvnw test jacoco:report`

command-check: `./mvnw clean test jacoco:check`

update skalli ya

