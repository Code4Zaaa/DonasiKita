<?php

namespace App\Console\Commands;

use App\Models\Donation;
use Illuminate\Console\Command;
use Illuminate\Support\Facades\Log;
use Carbon\Carbon;

class UpdateExpiredDonations extends Command
{
    /**
     * The name and signature of the console command.
     * We'll use a more conventional name for the command.
     * @var string
     */
    protected $signature = 'donations:update-expired';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Cari donasi yang pending dan sudah kedaluwarsa, lalu ubah statusnya menjadi "expired"';

    /**
     * Execute the console command.
     */
    public function handle()
    {
        $expiredDonations = Donation::where('status', 'pending')
            ->where('expired_time', '<', Carbon::now())
            ->get();

        if ($expiredDonations->isEmpty()) {
            $this->info('Tidak ada donasi yang kedaluwarsa untuk diupdate.');
            Log::info('[Cron] Tidak ada donasi yang kedaluwarsa.');
            return;
        }

        foreach ($expiredDonations as $donation) {
            $donation->update(['status' => 'expired']);
            Log::info("[Cron] Donasi {$donation->order_id} => expired");
            $this->info("Donasi {$donation->order_id} diubah menjadi expired.");
        }

        $this->info("Total: {$expiredDonations->count()} donasi diubah.");
        Log::info("[Cron] Total expired: {$expiredDonations->count()}");
    }
}