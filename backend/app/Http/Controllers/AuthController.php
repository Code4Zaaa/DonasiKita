<?php

namespace App\Http\Controllers;

use App\Http\Controllers\Controller;
use Kreait\Laravel\Firebase\Facades\Firebase;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use App\Models\User;

class AuthController extends Controller
{
    protected $auth;

    public function __construct()
    {
        $this->auth = Firebase::auth();
    }
    public function login(Request $request)
    {
        $credentials = $request->validate([
            'email' => 'required|email',
            'password' => 'required',
        ]);

        if (!Auth::attempt($credentials)) {
            return response()->json([
                'message' => 'Email atau password salah.'
            ], 401);
        }

        $user = User::where('email', $request->email)->firstOrFail();
        
        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'message' => 'Login berhasil',
            'access_token' => $token,
            'token_type' => 'Bearer',
            'user' => $user
        ]);
    }

    /**
     * Menangani register user dan membuat API token.
     */

    public function register(Request $request)
    {
        $data = $request->validate([
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:8|confirmed',
        ]);

        $user = User::create([
            'name' => $data['name'],
            'email' => $data['email'],
            'password' => bcrypt($data['password']),
        ]);

        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'message' => 'Register berhasil',
            'access_token' => $token,
            'token_type' => 'Bearer',
            'user' => $user
        ]);
    }

    public function loginOrRegister(Request $request)
    {
        $idToken = $request->bearerToken();

        try {   
            $verifiedIdToken = $this->auth->verifyIdToken($idToken);
        } catch (\Exception $e) {
            return response()->json(['message' => 'Unauthorized - Invalid Token'], 401);
        }

        $uid = $verifiedIdToken->claims()->get('sub');
        $firebaseUser = $this->auth->getUser($uid); 
        $user = User::updateOrCreate(
['email' => $firebaseUser->email], 
    [
                'firebase_uid' => $uid,
                'name' => $firebaseUser->displayName ?? 'User', 
                'photo_url' => $firebaseUser->photoUrl ?? null,
            ]
        );

        $apiToken = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'success' => true,
            'message' => 'Login successful',
            'user' => $user,
            'api_token' => $apiToken, 
        ]);
    }
    /**
     * Menangani logout user dan menghapus token.
     */
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Logout berhasil'
        ]);
    }
}