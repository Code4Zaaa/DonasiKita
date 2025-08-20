<?php

namespace App\Filament\Widgets;

use Filament\Tables;
use Filament\Tables\Table;
use Filament\Widgets\TableWidget as BaseWidget;
use App\Models\Donation;

class LatestDonations extends BaseWidget
{
    protected static ?string $heading = 'Donasi Terbaru';
    protected int | string | array $columnSpan = 'full';

    public function table(Table $table): Table
    {
        return $table
            ->query(
                Donation::query()
                    ->where('status', 'success')
                    ->latest()
                    ->limit(5)
            )
            ->columns([
                Tables\Columns\TextColumn::make('user.name')
                    ->label('Donatur')
                    ->formatStateUsing(function ($state, $record) {
                        return $record->is_anonymous ? 'Orang Baik' : $state;
                    }),
                Tables\Columns\TextColumn::make('campaign.title')
                    ->label('Kampanye')
                    ->limit(30),
                Tables\Columns\TextColumn::make('amount')
                    ->label('Jumlah')
                    ->money('IDR'),
                Tables\Columns\TextColumn::make('updated_at')
                    ->label('Waktu')
                    ->since(),
            ])
            ->paginated(false);
    }
}