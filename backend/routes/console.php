<?php

use App\Console\Commands\UpdateExpiredDonations;
use App\Console\Commands\UpdateStatusCampaign;
use Illuminate\Foundation\Inspiring;
use Illuminate\Support\Facades\Artisan;

Artisan::command('inspire', function () {
    $this->comment(Inspiring::quote());
})->purpose('Display an inspiring quote');

Schedule::command(UpdateExpiredDonations::class)->everySecond()->appendOutputTo(storage_path('logs/cron-donation.txt'));
Schedule::command(UpdateStatusCampaign::class)->dailyAt('00:00')->withoutOverlapping()->appendOutputTo(storage_path('logs/cron-campaign.txt'));
