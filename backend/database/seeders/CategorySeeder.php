<?php

namespace Database\Seeders;

use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use App\Models\Category;
use Illuminate\Support\Str;

class CategorySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $categories = [
            'Pendidikan',
            'Kesehatan',
            'Lingkungan',
            'Sosial',
            'Hewan',
            'Infrastruktur'
        ];

        foreach ($categories as $categoryName) {
            // Menggunakan firstOrCreate untuk menghindari duplikasi jika seeder dijalankan lagi
            Category::firstOrCreate(
                ['name' => $categoryName],
                ['slug' => Str::slug($categoryName)]
            );
        }
    }
}
