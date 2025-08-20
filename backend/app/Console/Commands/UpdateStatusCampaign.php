<?php

namespace App\Console\Commands;

use App\Models\Campaign;
use Illuminate\Console\Command;
use Carbon\Carbon;

class UpdateStatusCampaign extends Command
{
    /**
     * The name and signature of the console command.
     * We'll use a more conventional name for the command.
     * @var string
     */
    protected $signature = 'campaign:update-status';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Updates the status of campaigns to "closed" if their deadline has passed.';

    /**
     * Execute the console command.
     */
    public function handle()
    {
        $this->info('Checking for active campaigns that have passed their deadline...');


        $campaigns = Campaign::where('status', 'active')
                             ->whereDate('deadline', '<', Carbon::today())
                             ->get();

        if ($campaigns->isEmpty()) {
            $this->info('No campaigns needed an update. Everything is up to date!');
            return self::SUCCESS;
        }

        $this->info("Found {$campaigns->count()} campaign(s) to close.");

        foreach ($campaigns as $campaign) {
            $campaign->status = 'completed'; 
            $campaign->save();
            $this->line("Campaign '{$campaign->title}' has been closed.");
        }

        $this->info('Campaign status update process completed successfully.');
        return self::SUCCESS;
    }
}
