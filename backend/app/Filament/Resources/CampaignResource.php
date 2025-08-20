<?php

namespace App\Filament\Resources;

use App\Filament\Resources\CampaignResource\Pages;
use App\Models\Campaign;
use Filament\Forms;
use Filament\Forms\Form;
use Filament\Resources\Resource;
use Filament\Tables;
use Filament\Tables\Table;
use Illuminate\Support\Str;

class CampaignResource extends Resource
{
    protected static ?string $model = Campaign::class;

    protected static ?string $navigationIcon = 'heroicon-o-megaphone';
    protected static ?string $navigationGroup = 'Manajemen Kampanye';


    public static function form(Form $form): Form
    {
        return $form
            ->schema([
                Forms\Components\Hidden::make('user_id')
                    ->default(auth()->id())
                    ->required(),

                Forms\Components\Section::make('Informasi Utama')
                    ->schema([
                        Forms\Components\TextInput::make('title')
                            ->label('Judul Kampanye')
                            ->required()
                            ->maxLength(255)
                            ->live(onBlur: true)
                            ->afterStateUpdated(fn (Forms\Set $set, ?string $state) => $set('slug', Str::slug($state))),

                        Forms\Components\TextInput::make('slug')
                            ->required()
                            ->maxLength(255)
                            ->disabled()
                            ->dehydrated()
                            ->unique(Campaign::class, 'slug', ignoreRecord: true),

                        Forms\Components\Select::make('category_id')
                            ->label('Kategori')
                            ->relationship('category', 'name')
                            ->searchable()
                            ->preload()
                            ->required(),
                            
                        Forms\Components\DatePicker::make('deadline')
                            ->label('Batas Waktu')
                            ->native(false)
                            ->required(),

                        Forms\Components\TextInput::make('target_donation')
                            ->label('Target Donasi')
                            ->required()
                            ->numeric()
                            ->prefix('Rp'),
                            
                        Forms\Components\Select::make('status')
                            ->options([
                                'active' => 'Aktif',
                                'completed' => 'Selesai',
                                'closed' => 'Ditutup',
                            ])
                            ->default('active')
                            ->required(),

                        Forms\Components\Toggle::make('is_recommendation')
                            ->label('Rekomendasikan Kampanye')
                            ->default(false),
                            
                    ])->columns(2),

                Forms\Components\Section::make('Konten')
                    ->schema([
                        Forms\Components\FileUpload::make('thumbnail')
                            ->image()
                            ->directory('campaign-thumbnails')
                            ->imageEditor(),

                        Forms\Components\RichEditor::make('description')
                            ->label('Deskripsi Lengkap')
                            ->required()
                            ->columnSpanFull(),
                    ]),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                Tables\Columns\ImageColumn::make('thumbnail')->label('Gambar'),
                Tables\Columns\TextColumn::make('title')->label('Judul')->searchable()->limit(30),
                Tables\Columns\TextColumn::make('category.name')->label('Kategori'),
                Tables\Columns\TextColumn::make('target_donation')->label('Target')->money('IDR')->sortable(),
                Tables\Columns\TextColumn::make('current_donation')->label('Terkumpul')->money('IDR')->sortable(),
                Tables\Columns\BadgeColumn::make('status')
                    ->colors([
                        'success' => 'active',
                        'warning' => 'completed',
                        'danger' => 'closed',
                    ]),

                Tables\Columns\IconColumn::make('is_recommendation')
                    ->label('Rekomendasi')
                    ->boolean(),

            ])
            ->actions([
                Tables\Actions\EditAction::make(),
            ]);
    }

    public static function getPages(): array
    {
        return [
            'index' => Pages\ListCampaigns::route('/'),
            'create' => Pages\CreateCampaign::route('/create'),
            'edit' => Pages\EditCampaign::route('/{record}/edit'),
        ];
    }
}