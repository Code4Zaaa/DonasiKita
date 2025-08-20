<?php

namespace App\Filament\Widgets;

use App\Models\Donation;
use Filament\Widgets\ChartWidget;
use Flowframe\Trend\Trend;
use Flowframe\Trend\TrendValue;

class DonationsChart extends ChartWidget
{
    protected static ?string $heading = 'Grafik Donasi (7 Hari Terakhir)';

    protected function getData(): array
    {
        $query = Donation::query()->where('status', 'success');

        $data = Trend::query($query)
            ->between(
                start: now()->subWeek(),
                end: now(),
            )
            ->perDay()
            ->sum('amount');

        return [
            'datasets' => [
                [
                    'label' => 'Donasi Masuk',
                    'data' => $data->map(fn (TrendValue $value) => $value->aggregate),
                ],
            ],
            'labels' => $data->map(fn (TrendValue $value) => date('d M', strtotime($value->date))),
        ];
    }

    protected function getType(): string
    {
        return 'line'; 
    }
}