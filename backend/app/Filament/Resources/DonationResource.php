<?php

namespace App\Filament\Resources;

use App\Filament\Resources\DonationResource\Pages;
use App\Models\Donation;
use Filament\Forms;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Table;
use Filament\Notifications\Notification;

class DonationResource extends Resource
{
    protected static ?string $model = Donation::class;

    protected static ?string $navigationIcon = 'heroicon-o-gift';
    protected static ?string $navigationGroup = 'Manajemen Donasi';

    public static function canCreate(): bool
    {
        return false;
    }

    public static function form(Form $form): Form
    {

        return $form
            ->schema([
                Forms\Components\TextInput::make('campaign.title')->label('Kampanye')->disabled(),
                Forms\Components\TextInput::make('user.name')->label('Donatur')->disabled(),
                Forms\Components\TextInput::make('amount')->label('Jumlah')->money('IDR')->disabled(),
                Forms\Components\TextInput::make('status')->disabled(),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                Tables\Columns\TextColumn::make('order_id')->label('Order ID')->searchable(),
                Tables\Columns\TextColumn::make('user.name')->label('Donatur')->searchable(),
                Tables\Columns\TextColumn::make('campaign.title')->label('Kampanye')->searchable()->limit(30),
                Tables\Columns\TextColumn::make('amount')->label('Jumlah')->money('IDR')->sortable(),
                Tables\Columns\BadgeColumn::make('status')
                    ->colors([
                        'warning' => 'pending',
                        'success' => 'success',
                        'danger' => 'failed',
                        'gray' => 'expired',
                    ]),
                Tables\Columns\TextColumn::make('created_at')->label('Tanggal')->dateTime()->sortable(),
            ])
            ->defaultSort('created_at', 'desc')
            ->actions([
                Tables\Actions\Action::make('verify')
                    ->label('Verifikasi')
                    ->color('success')
                    ->icon('heroicon-o-check-circle')
                    ->requiresConfirmation()
                    ->action(function (Donation $record) {
                        $record->status = 'success';
                        $record->save();
                        $record->campaign->increment('current_donation', $record->amount);
                        Notification::make()
                            ->title('Donasi berhasil diverifikasi')
                            ->success()
                            ->send();
                    })
                    ->visible(fn (Donation $record): bool => $record->status === 'pending'),
            ]);
    }
    
    public static function getPages(): array
    {
        return [
            'index' => Pages\ListDonations::route('/'),
        ];
    }
}