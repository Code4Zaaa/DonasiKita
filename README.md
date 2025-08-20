# 💝 Donasikita

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Laravel](https://img.shields.io/badge/Laravel-11-FF2D20?logo=laravel)](https://laravel.com)
[![Android](https://img.shields.io/badge/Android-Java-3DDC84?logo=android)](https://developer.android.com)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?logo=firebase)](https://firebase.google.com)

> 🌟 **Aplikasi donasi online open source** yang memudahkan pengguna untuk berdonasi pada berbagai campaign, seperti bencana alam, pendidikan, dan kesehatan.

Proyek ini terinspirasi dari tampilan dan pengalaman pengguna di **[KitaBisa](https://kitabisa.com/)** 🎯, namun dikembangkan secara independen agar dapat digunakan, dimodifikasi, dan dikembangkan bersama komunitas.

---

## ✨ Fitur Utama

### 🖥️ **Admin Panel (Laravel + Filament)**
- 📊 Dashboard ringkasan donasi (grafik, total donasi, campaign baru, donatur baru)
- 📝 Manajemen campaign (buat, edit, hapus campaign)
- 🏷️ Manajemen kategori campaign (buat, edit, hapus kategori)
- 🧾 Manajemen donasi (hanya menampilkan invoice donasi)
- 👥 Manajemen pengguna (daftar user, buat, edit, status akun)

### 📱 **Mobile App (Android Java + Firebase)**
- 🔐 Autentikasi pengguna (Firebase Auth)
- 📋 List campaign donasi
- 🔍 Detail campaign & update perkembangan
- 💳 Donasi online dengan integrasi Tripay Payment Gateway
- 📈 Riwayat donasi

---

## 🛠️ Stack Teknologi

| Komponen | Teknologi | Versi |
|----------|-----------|--------|
| 🖥️ **Backend** | Laravel | 11 |
| 🎨 **Admin Panel** | Laravel Filament | Latest |
| 📱 **Mobile (User)** | Java (Android) | - |
| 🗄️ **Database** | MySQL / MariaDB | - |
| 🔥 **Authentication** | Firebase Auth | - |
| 🌐 **API** | REST API | - |
| 💰 **Payment** | Tripay Gateway | - |

---

## 🚀 Instalasi

### 1. 🖥️ Backend (Laravel + Filament)

```bash
# Clone repository
git clone https://github.com/Code4Zaaa/donasikita.git
cd donasikita/backend

# Setup environment
cp .env.example .env

# Install dependencies
composer install

# Generate application key
php artisan key:generate

# Run database migration and seeding
php artisan migrate --seed

# Start development server
php artisan serve
```

### 2. 📱 Mobile App (Java Android + Firebase)

- 📂 Buka folder `mobile` di **Android Studio**
- ⚙️ Konfigurasikan `google-services.json` dari Firebase ke dalam project
- 🔧 Atur `BASE_URL` di `network/ApiClient.java` sesuai endpoint backend Laravel
- ▶️ Jalankan aplikasi di emulator atau perangkat Android

---

## 🎨 Referensi Tampilan

Tampilan aplikasi user merujuk pada pengalaman donasi yang ada di **[KitaBisa](https://kitabisa.com/)** 🎯 sebagai inspirasi desain dan alur pengguna.

✅ Semua kode dan aset visual di repo ini dikembangkan secara independen.

---

## 🤝 Kontribusi

Kontribusi sangat terbuka! Anda bisa membantu dengan:

- ✨ Menambahkan fitur baru
- 🐛 Memperbaiki bug
- 🎨 Meningkatkan UI/UX
- 📖 Menulis dokumentasi
- 🧪 Menambahkan testing
- 🌐 Menerjemahkan ke bahasa lain

### 📋 Cara Berkontribusi:
1. 🍴 Fork repository ini
2. 🌿 Buat branch fitur (`git checkout -b fitur-baru`)
3. 💾 Commit perubahan (`git commit -am 'Menambah fitur baru'`)
4. 📤 Push ke branch (`git push origin fitur-baru`)
5. 🔄 Buat Pull Request

Atau buka **Issue** 💬 untuk berdiskusi ide dan saran!

---

## 💝 Support Project

Jika proyek ini bermanfaat bagi Anda, dukung pengembangan dengan:

[![Saweria](https://img.shields.io/badge/🎁_Saweria-FF6B35?style=for-the-badge&logo=data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTEyIDJMMTMuMDkgOC4yNkwyMCA5TDEzLjA5IDE1Ljc0TDEyIDIyTDEwLjkxIDE1Ljc0TDQgOUwxMC45MSA4LjI2TDEyIDJaIiBmaWxsPSJ3aGl0ZSIvPgo8L3N2Zz4K&logoColor=white)](https://saweria.co/forzaadev)
[![Donate](https://img.shields.io/badge/☕_Buy_me_a_coffee-FFDD00?style=for-the-badge&logo=buy-me-a-coffee&logoColor=black)](https://www.buymeacoffee.com/code4zaaa)
[![Sponsor](https://img.shields.io/badge/❤️_Sponsor_on_GitHub-EA4AAA?style=for-the-badge&logo=github-sponsors&logoColor=white)](https://github.com/sponsors/Code4Zaaa)

### 🌟 Atau berikan ⭐ Star untuk repository ini!

---

## 📞 Kontak & Dukungan

- 💬 **Discord**: [Join Server](https://discord.gg/donasikita)
- 📧 **Email**: support@donasikita.com
- 🐦 **Twitter**: [@donasikita](https://twitter.com/donasikita)
- 💼 **LinkedIn**: [Donasikita Project](https://linkedin.com/company/donasikita)

---

## 📄 Lisensi

Proyek ini dirilis dengan **MIT License** 📜.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

✅ Silakan gunakan, modifikasi, dan distribusikan sesuai kebutuhan.

---

## 🏆 Contributors

Terima kasih kepada semua kontributor yang telah membantu mengembangkan proyek ini! 🙏

[![Contributors](https://contrib.rocks/image?repo=Code4Zaaa/donasikita)](https://github.com/Code4Zaaa/donasikita/graphs/contributors)

---

<div align="center">

### 🚀 **Dibuat dengan ❤️ oleh komunitas untuk kemudahan berdonasi di Indonesia** 🇮🇩

**[⭐ Star](https://github.com/Code4Zaaa/donasikita)** • **[🐛 Report Bug](https://github.com/Code4Zaaa/donasikita/issues)** • **[💡 Request Feature](https://github.com/Code4Zaaa/donasikita/issues)**

</div>
