<?php

namespace App\Filament\Widgets;

use App\Models\Campaign;
use App\Models\Donation;
use App\Models\User;
use Filament\Widgets\StatsOverviewWidget as BaseWidget;
use Filament\Widgets\StatsOverviewWidget\Stat;

class CharityStatsOverview extends BaseWidget
{
    protected function getStats(): array
    {
        // --- 1. Kalkulasi Data Donasi ---
        $donationsThisMonth = Donation::where('status', 'success')->where('created_at', '>=', now()->startOfMonth())->sum('amount');
        $donationsLastMonth = Donation::where('status', 'success')->whereBetween('created_at', [now()->subMonth()->startOfMonth(), now()->subMonth()->endOfMonth()])->sum('amount');
        $donationDiff = $donationsLastMonth > 0 ? (($donationsThisMonth - $donationsLastMonth) / $donationsLastMonth) * 100 : 100;

        $campaignsThisMonth = Campaign::where('created_at', '>=', now()->startOfMonth())->count();
        $campaignsLastMonth = Campaign::whereBetween('created_at', [now()->subMonth()->startOfMonth(), now()->subMonth()->endOfMonth()])->count();
        $campaignDiff = $campaignsLastMonth > 0 ? (($campaignsThisMonth - $campaignsLastMonth) / $campaignsLastMonth) * 100 : 100;

        $donorsThisMonth = User::where('role', 'user')->where('created_at', '>=', now()->startOfMonth())->count();
        $donorsLastMonth = User::where('role', 'user')->whereBetween('created_at', [now()->subMonth()->startOfMonth(), now()->subMonth()->endOfMonth()])->count();
        $donorDiff = $donorsLastMonth > 0 ? (($donorsThisMonth - $donorsLastMonth) / $donorsLastMonth) * 100 : 100;


        return [
            Stat::make('Donasi Bulan Ini', 'Rp ' . number_format($donationsThisMonth, 0, ',', '.'))
                ->description(abs($donationDiff) . '% ' . ($donationDiff >= 0 ? 'kenaikan' : 'penurunan') . ' dari bulan lalu')
                ->descriptionIcon($donationDiff >= 0 ? 'heroicon-m-arrow-trending-up' : 'heroicon-m-arrow-trending-down')
                ->color($donationDiff >= 0 ? 'success' : 'danger'),
            
            Stat::make('Kampanye Baru Bulan Ini', $campaignsThisMonth)
                ->description(abs($campaignDiff) . '% ' . ($campaignDiff >= 0 ? 'kenaikan' : 'penurunan') . ' dari bulan lalu')
                ->descriptionIcon($campaignDiff >= 0 ? 'heroicon-m-arrow-trending-up' : 'heroicon-m-arrow-trending-down')
                ->color($campaignDiff >= 0 ? 'success' : 'danger'),

            Stat::make('Donatur Baru Bulan Ini', $donorsThisMonth)
                ->description(abs($donorDiff) . '% ' . ($donorDiff >= 0 ? 'kenaikan' : 'penurunan') . ' dari bulan lalu')
                ->descriptionIcon($donorDiff >= 0 ? 'heroicon-m-arrow-trending-up' : 'heroicon-m-arrow-trending-down')
                ->color($donorDiff >= 0 ? 'success' : 'danger'),
        ];
    }
}