<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Campaign;
use App\Models\Category;
use App\Models\User;
use Illuminate\Support\Str;

class CampaignSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Pastikan ada user dan kategori untuk dihubungkan.
        // Ambil user pertama, atau buat jika tidak ada.
        $user = User::first() ?? User::factory()->create();

        // Ambil kategori yang ada, atau berikan pesan error jika tidak ada.
        $categories = Category::all();
        if ($categories->isEmpty()) {
            $this->command->error('Tidak ada kategori ditemukan. Jalankan CategorySeeder terlebih dahulu.');
            return;
        }

        $campaigns = [
            [
                'title' => 'Bantuan Pendidikan Anak Yatim di Pelosok',
                'category_id' => $categories->where('name', 'Pendidikan')->first()->id,
                'target_donation' => 50000000,
                'current_donation' => 25500000,
                'deadline' => now()->addDays(30),
                'is_recommendation' => true,
                'description' => 'Mari kita bersama-sama memberikan harapan baru bagi anak-anak yatim di daerah terpencil dengan menyediakan akses pendidikan yang layak. Donasi Anda akan digunakan untuk membeli seragam, buku, dan perlengkapan sekolah lainnya.'
            ],
            [
                'title' => 'Operasi Jantung Darurat untuk Ibu Sarah',
                'category_id' => $categories->where('name', 'Kesehatan')->first()->id,
                'target_donation' => 150000000,
                'current_donation' => 95000000,
                'deadline' => now()->addDays(15),
                'is_recommendation' => true,
                'description' => 'Ibu Sarah, seorang ibu tunggal dengan dua anak, membutuhkan operasi jantung segera. Tanpa bantuan kita, kesempatannya sangat kecil. Setiap rupiah sangat berarti untuk menyelamatkan nyawanya.'
            ],
            [
                'title' => 'Tanam 10.000 Pohon Mangrove di Pesisir Utara',
                'category_id' => $categories->where('name', 'Lingkungan')->first()->id,
                'target_donation' => 75000000,
                'current_donation' => 15000000,
                'deadline' => now()->addDays(60),
                'is_recommendation' => false,
                'description' => 'Abrasi mengancam kehidupan para nelayan di pesisir utara. Dengan menanam 10.000 pohon mangrove, kita tidak hanya melindungi desa mereka, tetapi juga mengembalikan ekosistem laut yang kaya.'
            ],
            [
                'title' => 'Bantuan Logistik untuk Korban Banjir Bandang',
                'category_id' => $categories->where('name', 'Bencana Alam')->first()->id,
                'target_donation' => 100000000,
                'current_donation' => 80000000,
                'deadline' => now()->addDays(7),
                'is_recommendation' => true,
                'description' => 'Ratusan keluarga kehilangan tempat tinggal akibat banjir bandang. Mereka sangat membutuhkan bantuan berupa makanan, air bersih, selimut, dan obat-obatan. Uluran tangan Anda sangat dinantikan.'
            ],
            [
                'title' => 'Rumah Singgah untuk Kucing dan Anjing Terlantar',
                'category_id' => $categories->where('name', 'Hewan')->first()->id,
                'target_donation' => 40000000,
                'current_donation' => 5000000,
                'deadline' => now()->addDays(45),
                'is_recommendation' => false,
                'description' => 'Banyak hewan tak berdosa yang menderita di jalanan. Kami ingin membangun rumah singgah yang layak untuk merawat, mensterilkan, dan mencarikan rumah baru bagi mereka.'
            ],
        ];

        foreach ($campaigns as $campaignData) {
            Campaign::create([
                'user_id' => $user->id,
                'category_id' => $campaignData['category_id'],
                'title' => $campaignData['title'],
                'slug' => Str::slug($campaignData['title']),
                'description' => $campaignData['description'],
                'target_donation' => $campaignData['target_donation'],
                'current_donation' => $campaignData['current_donation'],
                'deadline' => $campaignData['deadline'],
                'is_recommendation' => $campaignData['is_recommendation'],
                'status' => 'active', // Default status
            ]);
        }
    }
}
