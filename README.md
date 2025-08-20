# Donasikita

Donasikita adalah aplikasi donasi online open source yang memudahkan pengguna untuk berdonasi pada berbagai campaign, seperti bencana alam, pendidikan, dan kesehatan.  
Proyek ini terinspirasi dari tampilan dan pengalaman pengguna di **[KitaBisa](https://kitabisa.com/)**, namun dikembangkan secara independen agar dapat digunakan, dimodifikasi, dan dikembangkan bersama komunitas.

---

## Fitur Utama

- **Admin Panel (Laravel + Filament)**  
  - Dashboard ringkasan donasi (grafik, total donasi, campaign baru, donatur baru)
  - Manajemen campaign (buat, edit, hapus campaign)
  - Manajemen kategori campaign (buat, edit, hapus campaign)
  - Manajemen donasi (hanya menampilkan invoice donasi)
  - Manajemen pengguna (daftar user, buat, edit, status akun) 

- **Mobile App (Android Java + Firebase)**  
  - Autentikasi pengguna (Firebase Auth)  
  - List campaign donasi  
  - Detail campaign & update perkembangan  
  - Donasi online dengan integrasi Tripay Payment Gateway  
  - Riwayat donasi  

---

## Stack Teknologi

- **Backend**: Laravel 11  
- **Admin Panel**: Laravel Filament  
- **Mobile (User)**: Java (Android)  
- **Database**: MySQL / MariaDB  
- **Firebase**: Authentication
- **API**: REST API  

---

## Instalasi

### 1. Backend (Laravel + Filament)
```bash
git clone https://github.com/Code4Zaaa/donasikita.git
cd donasikita/backend
cp .env.example .env
composer install
php artisan key:generate
php artisan migrate --seed
php artisan serve
```

### 2. Mobile App (Java Android + Firebase)
- Buka folder `mobile` di **Android Studio**  
- Konfigurasikan `google-services.json` dari Firebase ke dalam project  
- Atur `BASE_URL` di network/ApiClient.java sesuai endpoint backend Laravel  
- Jalankan aplikasi di emulator atau perangkat Android  

---

## Referensi Tampilan

Tampilan aplikasi user merujuk pada pengalaman donasi yang ada di **[KitaBisa](https://kitabisa.com/)** sebagai inspirasi desain dan alur pengguna.  
Semua kode dan aset visual di repo ini dikembangkan secara independen.

---

## Kontribusi

Kontribusi sangat terbuka. Anda bisa membantu dengan:  
- Menambahkan fitur baru  
- Memperbaiki bug  
- Meningkatkan UI/UX  
- Menulis dokumentasi  

Buat **Pull Request** atau buka **Issue** untuk berdiskusi.

---

## Lisensi

Proyek ini dirilis dengan **MIT License**.  
Silakan gunakan, modifikasi, dan distribusikan sesuai kebutuhan.
