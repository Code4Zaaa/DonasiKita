<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;

// Import semua controller dari direktori 'Api'
use App\Http\Controllers\AuthController;
use App\Http\Controllers\DonationController;
use App\Http\Controllers\CampaignController;
use App\Http\Controllers\CategoryController;

Route::options('{any}', function (Request $request) {
    return response()->json([], 200);
})->where('any', '.*');


Route::post('/login', [AuthController::class, 'login']);
Route::post('/login-or-register', [AuthController::class, 'loginOrRegister']);
Route::get("/category", [CategoryController::class, 'index']);
Route::get('/campaigns', [CampaignController::class, 'index']);
Route::get('/campaigns/{campaign:slug}', [CampaignController::class, 'show']);
Route::get('/donations/by-order-id/{order_id}', [DonationController::class, 'getDonationByOrderId'])
     ->where('order_id', '.*');
Route::post('/donations/callback', [DonationController::class, 'handleCallback']);
Route::get('/payment-channels/{code?}', [DonationController::class, 'getPaymentChannels']);

Route::middleware('auth:sanctum')->group(function () {
    Route::get('/user', function (Request $request) {
        return $request->user();
    });

    Route::get('/donations/me', [DonationController::class, 'getMyDonations']);
    
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::post('/donations', [DonationController::class, 'createTransaction']);
});