# 🏥 Sistem Penjadwalan Dokter Klinik Sehat

Aplikasi berbasis CLI (Command Line Interface) untuk manajemen penjadwalan konsultasi dokter di klinik, dilengkapi dengan implementasi design patterns dan persistent storage menggunakan file .txt.

## 📋 Deskripsi

Sistem ini dirancang untuk mempermudah pasien dalam melakukan registrasi, pemesanan jadwal konsultasi, dan melihat riwayat kunjungan. Dokter dapat memperbarui jadwal ketersediaan praktiknya, dan sistem menyimpan semua data secara persistent ke dalam file .txt.

## 🎯 Fitur Utama

### Menu Pasien

- ✅ Registrasi akun baru dengan data pribadi
- ✅ Login menggunakan ID pasien
- ✅ Lihat daftar dokter beserta spesialisasi dan jadwal
- ✅ Booking jadwal konsultasi
- ✅ Validasi booking ganda (tidak dapat booking slot yang sama)
- ✅ Lihat riwayat konsultasi
- ✅ Pilih metode notifikasi (Email/SMS/WhatsApp)

### Menu Dokter

- ✅ Login menggunakan ID dokter
- ✅ Lihat jadwal praktek
- ✅ Tambah jadwal baru
- ✅ Hapus jadwal (jika belum dipesan)
- ✅ Lihat daftar appointment
- ✅ Selesaikan konsultasi dan buat riwayat
- ✅ Pilih metode notifikasi (SMS/Email/WhatsApp)

### Menu Informasi

- ✅ Lihat daftar semua dokter (dengan statistik)
- ✅ Lihat daftar semua pasien (dengan statistik)

## 🏗️ Design Patterns

Aplikasi ini mengimplementasikan 4 design patterns:

### 1. **Singleton Pattern**

- **Class**: `Database`
- **Fungsi**: Memastikan hanya ada satu instance database di seluruh aplikasi
- **Keuntungan**: Konsistensi data dan efisiensi memori

### 2. **Factory Method Pattern**

- **Class**: `UserFactory`, `ConcreteUserFactory`
- **Fungsi**: Membuat objek Patient dan Doctor dengan enkapsulasi pembuatan ID
- **Keuntungan**: Pemisahan logic pembuatan objek dan automatic persistence

### 3. **Strategy Pattern**

- **Interface**: `NotificationStrategy`
- **Implementasi**: `EmailNotification`, `SMSNotification`, `WhatsAppNotification`
- **Fungsi**: Memungkinkan pemilihan metode notifikasi secara dinamis
- **Keuntungan**: Fleksibilitas dalam mengubah behavior notifikasi

### 4. **Observer Pattern**

- **Interface**: `Subject`, `Observer`
- **Implementasi**: `Appointment` (Subject), `Patient` dan `Doctor` (Observer)
- **Fungsi**: Notifikasi otomatis saat terjadi perubahan status appointment
- **Keuntungan**: Real-time notification dan loose coupling

## 📁 Struktur Project

```
doctor-scheduling-app/
├── src/
│   └── DoctorSchedulingApp.java
├── bin/                          # Compiled .class files
│   ├── Observer.class
│   ├── Subject.class
│   ├── NotificationStrategy.class
│   ├── EmailNotification.class
│   ├── SMSNotification.class
│   ├── WhatsAppNotification.class
│   ├── NotificationContext.class
│   ├── Patient.class
│   ├── Doctor.class
│   ├── Schedule.class
│   ├── Appointment.class
│   ├── ConsultationHistory.class
│   ├── Database.class
│   ├── UserFactory.class
│   ├── ConcreteUserFactory.class
│   └── DoctorSchedulingApp.class
├── data/                         # Database .txt files
│   ├── patients.txt
│   ├── doctors.txt
│   ├── schedules.txt
│   ├── appointments.txt
│   ├── histories.txt
│   └── counters.txt
└── README.md
```

## 💾 Database (File .txt)

Aplikasi menggunakan 6 file .txt sebagai persistent storage:

| File               | Deskripsi                                                           |
| ------------------ | ------------------------------------------------------------------- |
| `patients.txt`     | Data pasien (ID, nama, email, telepon, alamat)                      |
| `doctors.txt`      | Data dokter (ID, nama, spesialisasi)                                |
| `schedules.txt`    | Jadwal dokter (ID, doctorID, tanggal, waktu, status)                |
| `appointments.txt` | Booking appointment (ID, patientID, scheduleID, tanggal, status)    |
| `histories.txt`    | Riwayat konsultasi (ID, appointmentID, tanggal, diagnosis, catatan) |
| `counters.txt`     | Counter untuk auto-increment ID                                     |

## 🚀 Cara Instalasi & Menjalankan

### Prerequisites

- Java Development Kit (JDK) 8 atau lebih tinggi
- Terminal/Command Prompt

### Langkah-langkah:

1. **Clone atau Download Source Code**

   ```bash
   # Buat struktur folder
   mkdir doctor-scheduling-app
   cd doctor-scheduling-app
   mkdir src bin data
   ```

2. **Simpan Source Code**

   - Simpan file `DoctorSchedulingApp.java` di folder `src/`

3. **Compile Program**

   ```bash
   # Compile dengan output ke folder bin
   javac -d bin src/DoctorSchedulingApp.java
   ```

4. **Jalankan Program**
   ```bash
   # Jalankan dari root directory
   java -cp bin DoctorSchedulingApp
   ```

### Alternatif One-liner:

```bash
# Compile dan jalankan sekaligus
javac -d bin src/DoctorSchedulingApp.java && java -cp bin DoctorSchedulingApp
```

## 📝 Cara Penggunaan

### 1. Registrasi Pasien

```
Menu Utama → 1 (Menu Pasien) → 1 (Registrasi Akun Baru)
- Masukkan data: Nama, Email, Telepon, Alamat
- Catat ID Pasien yang diberikan sistem
```

### 2. Login Pasien

```
Menu Utama → 1 (Menu Pasien) → 2 (Login Pasien)
- Masukkan ID Pasien
- Pilih metode notifikasi (Email/SMS/WhatsApp)
```

### 3. Booking Jadwal

```
Menu Pasien → 2 (Booking Jadwal Konsultasi)
- Lihat daftar dokter dan jadwal tersedia
- Masukkan ID Jadwal yang ingin dipesan
- Sistem akan membuat appointment dan mengirim notifikasi
```

### 4. Login Dokter

```
Menu Utama → 2 (Menu Dokter)
- Masukkan ID Dokter (Default: D001, D002, D003)
- Pilih metode notifikasi
```

### 5. Tambah Jadwal (Dokter)

```
Menu Dokter → 2 (Tambah Jadwal Baru)
- Format tanggal: dd-MM-yyyy (contoh: 25-12-2025)
- Format waktu: HH:mm (contoh: 09:00)
```

## 🔐 Data Login Default

### Dokter (Sudah Tersedia)

| ID   | Nama             | Spesialisasi |
| ---- | ---------------- | ------------ |
| D001 | Dr. Ahmad Yani   | Kardiologi   |
| D002 | Dr. Siti Rahma   | Pediatri     |
| D003 | Dr. Budi Santoso | Orthopedi    |

### Pasien

- Pasien perlu registrasi terlebih dahulu
- Setelah registrasi, gunakan ID yang diberikan sistem

## 📊 Requirements (Dari Dokumen)

Aplikasi ini mengimplementasikan semua requirements:

- **[R01]**: Pasien dapat melakukan registrasi akun baru ✅
- **[R02]**: Sistem menampilkan daftar dokter beserta spesialisasi dan jadwal ✅
- **[R03]**: Pasien dapat melakukan booking jadwal konsultasi ✅
- **[R04]**: Sistem menyimpan data booking ke database ✅
- **[R05]**: Sistem menolak booking ganda pada slot yang sama ✅
- **[R06]**: Pasien dapat melihat riwayat konsultasi ✅
- **[R07]**: Dokter dapat memperbarui jadwal ketersediaan ✅
- **[R08]**: Sistem memastikan integrasi antar modul berjalan baik ✅

## 🎨 Contoh Output

### Menu Utama

```
╔════════════════════════════════════════════════╗
║   SISTEM PENJADWALAN DOKTER KLINIK SEHAT      ║
║        (Singleton, Factory, Strategy,          ║
║         Observer Pattern + TXT Database)       ║
╚════════════════════════════════════════════════╝

==================================================
MENU UTAMA
==================================================
1. Menu Pasien
2. Menu Dokter
3. Lihat Daftar Dokter & Pasien
4. Keluar
==================================================
```

### Notifikasi Observer Pattern

```
[EMAIL] Mengirim ke patient@email.com: Booking berhasil! Appointment ID: A001 dengan Dr. Ahmad Yani pada 27-11-2025
[SMS] Mengirim ke Dr. Ahmad Yani: Booking berhasil! Appointment ID: A001 dengan Dr. Ahmad Yani pada 27-11-2025
```

## 🛠️ Troubleshooting

### Error: "javac tidak dikenali"

- Pastikan JDK sudah terinstall
- Set PATH environment variable ke JDK bin folder

### Error: "NoClassDefFoundError"

- Pastikan menjalankan dari root directory
- Gunakan `-cp bin` saat menjalankan program

### Data hilang setelah restart

- Pastikan folder `data/` berada di directory yang sama dengan tempat Anda menjalankan program
- Check permission folder untuk read/write

### File .txt tidak terbuat

- Program otomatis membuat file saat pertama kali dijalankan
- Folder `data/` harus sudah ada sebelum menjalankan program

## 📚 Teknologi & Tools

- **Bahasa**: Java 8+
- **Database**: Text files (.txt)
- **Design Patterns**: Singleton, Factory Method, Strategy, Observer
- **I/O**: BufferedReader, PrintWriter, FileReader, FileWriter
- **Date/Time**: java.time (LocalDate, LocalTime)
- **Collections**: HashMap, ArrayList

## 👨‍💻 Author

**Naufal Aqil** (2208107010043)

- Program Studi Informatika
- Fakultas Matematika dan Ilmu Pengetahuan Alam
- Universitas Syiah Kuala
- Mata Kuliah: Arsitektur Perangkat Lunak

## 📄 Lisensi

Proyek ini dibuat untuk keperluan Ujian Tengah Semester (UTS) Praktikum Arsitektur Perangkat Lunak tahun 2025.

## 🔄 Version History

- **v1.0.0** (2025) - Initial Release
  - Implementasi 4 Design Patterns
  - Persistent storage dengan .txt files
  - CLI Interface lengkap
  - Auto-save dan auto-load data

## 📞 Support

Nama : Naufal Aqil
NPM : 2208107010043
Aplikasi : Aplikasi Penjadwalan Dokter

Jika ada pertanyaan atau masalah:

1. Periksa bagian Troubleshooting di atas
2. Pastikan struktur folder sesuai dokumentasi
3. Verify JDK version compatibility

---

**Note**: Aplikasi ini adalah bagian dari tugas akademik dan dirancang untuk tujuan pembelajaran implementasi design patterns dalam pengembangan perangkat lunak.
