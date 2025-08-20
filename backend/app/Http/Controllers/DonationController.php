<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Str;

use App\Models\Campaign;
use App\Models\Donation;

use Nekoding\Tripay\Tripay;
use Nekoding\Tripay\TripayFacade as TripayFacade;
use Nekoding\Tripay\Signature;

class DonationController extends Controller
{
    public function getPaymentChannels(string $code = null)
    {
        $response = TripayFacade::getChannelPembayaran($code);

        if ($response["success"] === true) {
            return response()->json($response);
        }

        return response()->json(['success' => false, 'message' => 'Gagal mengambil data channel.'], 500);
    }

    public function createTransaction(Request $request)
    {
        $request->validate([
            'campaign_id' => 'required|exists:campaigns,id',
            'amount' => 'required|integer|min:1000',
            'payment_method' => 'required|string',
            'is_anonymous' => 'boolean',
            'doa' => 'nullable|string|max:245', 
        ]);

        $campaign = Campaign::find($request->campaign_id);
        $user = auth()->user();
        $merchantRef = "#" . mt_rand(100000000, 999999999);

        $signature = Signature::generate($merchantRef . $request->amount);

        $payload = [
            'method'         => $request->payment_method,
            'merchant_ref'   => $merchantRef,
            'amount'         => $request->amount,
            'customer_name'  => $request->is_anonymous ? 'Anonymous' : $user->name,
            'customer_email' => $user->email,
            'customer_phone' => $user->phone_number ?? '081234567890',
            'order_items'    => [
                [
                    'sku' => $campaign->slug, 
                    'name' => 'Donasi: ' . $campaign->title, 
                    'price' => $request->amount, 
                    'quantity' => 1,
                ]
            ],
            'expired_time' => (time() + (15 * 60)),
            'signature'    => $signature
        ];

        $transaction = TripayFacade::createTransaction($payload, Tripay::CLOSE_TRANSACTION);

        if ($transaction->getResponse()['success'] === false) {
            return response()->json(['success' => false, 'message' => $transaction['message']], 400);
        }
        
        Donation::create([
            'user_id' => $user->id,
            'campaign_id' => $campaign->id,
            'order_id' => $merchantRef,
            'tripay_reference' => $transaction->getResponse()['data']['reference'],
            'amount' => $request->amount,
            'doa' => $request->doa,
            'status' => 'pending',
            'payment_method' => $request->payment_method,
            'va_number' => $transaction->getResponse()['data']['pay_code'] ?? null,
            'qr_code_url' => $transaction->getResponse()['data']['qr_url'] ?? null,
            'payment_url' => $transaction->getResponse()['data']['pay_url'] ?? null,
            'is_anonymous' => $request->boolean('is_anonymous'),
            'expired_time' => \Carbon\Carbon::createFromTimestamp($transaction->getResponse()['data'] ['expired_time'],'Asia/Jakarta'),
        ]);

        return response()->json(['success' => true, 'data' => $transaction->getResponse()['data']]);
    }

    public function handleCallback(Request $request)
    {
        $callbackSignature = $request->header('X-Callback-Signature');
        $json = $request->getContent();

        $signature = hash_hmac('sha256', $json, config('tripay.tripay_private_key'));
        if ($signature !== $callbackSignature) {
            return response()->json(['success' => false, 'message' => 'Invalid Signature'], 403);
        }

        $data = json_decode($json);
        if (json_last_error() !== JSON_ERROR_NONE || $request->header('X-Callback-Event') !== 'payment_status') {
            return response()->json(['success' => false, 'message' => 'Invalid JSON or Event']);
        }

        $donation = Donation::where('order_id', $data->merchant_ref)->where('status', 'pending')->first();

        if ($donation) {
            $status = strtolower($data->status);

            if ($status == 'paid') {
                $donation->status = 'success';
                $donation->campaign->increment('current_donation', $data->amount_received);
            } else {
                $donation->status = $status; 
            }
            $donation->save();
        }

        return response()->json(['success' => true, 'message' => 'Callback processed successfully']);
    }

    public function getDonationByOrderId(string $order_id)
    {
        $donation = Donation::with('campaign')->where('order_id', $order_id)->first();

        if (!$donation) {
            return response()->json(['success' => false, 'message' => 'Donasi tidak ditemukan.'], 404);
        }

        return response()->json(['success' => true, "message" => "Detail donasi berhasil diambil.", 'data' => $donation]);
    }

    public function getMyDonations(Request $request)
    {
        $user = $request->user();

        $donations = Donation::where('user_id', $user->id)
            ->with('campaign:id,title,slug,thumbnail')
            ->orderBy('created_at', 'desc')
            ->get();

        return response()->json([
            'success' => true,
            'message' => 'Riwayat donasi berhasil diambil.',
            'data' => $donations
        ]);
    }
}