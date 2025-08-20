<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use App\Models\Campaign;
use Illuminate\Http\Request;

class CampaignController extends Controller
{
    /**
     * Menampilkan daftar semua kampanye yang aktif.
     */
    public function index()
    {
        $campaigns = Campaign::where('status', 'active')
            ->with('category:id,name,slug')
            ->withCount(['donations' => function ($query) {
                $query->where('status', 'success');
            }])
            ->with(['donations' => function ($query) {
                $query->where('status', 'success')
                    ->with('user:id,name,photo_url')
                    ->latest()
                    ->limit(5);
            }])
            ->where(function ($query) {
                $query->whereNull('deadline')
                    ->orWhere('deadline', '>=', now());
            })
            ->latest()
            ->paginate(10);


        $campaigns->getCollection()->transform(function ($campaign) {
            $campaign->days_left = $campaign->deadline ? (int) now()->diffInDays($campaign->deadline, false) : 0;

            $campaign->donor_count = $campaign->donations_count;
            unset($campaign->donations_count);

            return $campaign;
        });

        return response()->json(array_merge(
            [
                'success' => true,
                'message' => 'Daftar kampanye berhasil diambil.',
            ],
            $campaigns->toArray()
        ));
    }

    public function show(Campaign $campaign)
    {
        if ($campaign->deadline && now()->greaterThan($campaign->deadline)) {
            return response()->json([
                'success' => false,
                'message' => 'Kampanye sudah berakhir.',
            ], 404);
        }

        $campaign->loadCount(['donations' => function($query) {
            $query->where('status', 'success');
        }]);

        $campaign->load(['donations' => function($query) {
            $query->where('status', 'success')
                ->with('user:id,name,photo_url')
                ->latest()
                ->limit(5);
        }]);

        $campaign->days_left = $campaign->deadline ? now()->diffInDays($campaign->deadline, false) : 0;

        $campaign->donor_count = $campaign->donations_count;
        unset($campaign->donations_count);

        return response()->json([
            'success' => true,
            'message' => 'Detail kampanye berhasil diambil.',
            'data' => $campaign
        ]);
    }
}
