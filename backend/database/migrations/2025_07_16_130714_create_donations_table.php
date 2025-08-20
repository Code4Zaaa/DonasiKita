<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('donations', function (Blueprint $table) {
            $table->id();
            $table->string('order_id')->unique();
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->foreignId('campaign_id')->constrained('campaigns')->onDelete('cascade');
            $table->decimal('amount', 15, 2);
            $table->boolean('is_anonymous')->default(false);
            $table->enum('status', ['pending', 'success', 'failed', 'expired'])->default('pending');
            
            // Kolom untuk data Tripay
            $table->string('payment_method')->nullable();
            $table->string('tripay_reference')->nullable()->index();
            $table->text('payment_url')->nullable();
            $table->string('va_number')->nullable();
            $table->text('qr_code_url')->nullable();
            
            $table->timestamps();
            $table->softDeletes();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('donations');
    }
};