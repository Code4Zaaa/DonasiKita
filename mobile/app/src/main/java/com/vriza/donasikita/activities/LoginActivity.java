package com.vriza.donasikita.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.vriza.donasikita.R;
import com.vriza.donasikita.network.ApiClient;
import com.vriza.donasikita.network.ApiService;
import com.vriza.donasikita.network.responses.UserResponse;
import com.vriza.donasikita.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnGoogleSignup;
    private TextView tvForgotPassword, tvSignUp;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private ApiService apiService;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initViews();
        setupClickListeners();
        setupProgressDialog();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogleSignup = findViewById(R.id.btn_google_signup);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        btnGoogleSignup.setOnClickListener(v -> signInWithGoogle());

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur lupa password akan segera tersedia", Toast.LENGTH_SHORT).show();
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        showLoadingDialog("Sedang masuk...");

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideLoadingDialog();

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            navigateToMainActivity();
                        } else {
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() : "Login gagal";
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        tilEmail.setError(null);
        tilPassword.setError(null);

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email tidak boleh kosong");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Format email tidak valid");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password tidak boleh kosong");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password minimal 6 karakter");
            isValid = false;
        }

        return isValid;
    }

    private void signInWithGoogle() {
        showLoadingDialog("Menghubungkan dengan Google...");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                hideLoadingDialog();
                Toast.makeText(this, "Google sign in gagal", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        progressDialog.setMessage("Melakukan autentikasi...");

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            getIdTokenAndSendToBackend(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showLoadingDialog(String message) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setMessage(message);
            progressDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void getIdTokenAndSendToBackend(FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            hideLoadingDialog();
            Toast.makeText(this, "Gagal mendapatkan data pengguna.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Memverifikasi dengan server...");
        firebaseUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();

                        sendTokenToBackend(idToken);
                    } else {
                        hideLoadingDialog();
                        Toast.makeText(this, "Gagal mendapatkan token autentikasi.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendTokenToBackend(String idToken) {
        String authToken = "Bearer " + idToken;
        Call<UserResponse> call = apiService.loginOrRegisterUser(authToken);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                hideLoadingDialog();
                if (response.isSuccessful() && response.body() != null) {
                    sessionManager.createLoginSession(response.body().getApiToken(), response.body().getUser());
                    navigateToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Verifikasi server gagal. Kode: " + response.code(), Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                hideLoadingDialog();
                Toast.makeText(LoginActivity.this, "Tidak dapat terhubung ke server: " + t.getMessage(), Toast.LENGTH_LONG).show();
                FirebaseAuth.getInstance().signOut();
            }
        });
    }


    private void setLoadingState(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        btnGoogleSignup.setEnabled(!isLoading);

        if (isLoading) {
            btnLogin.setText("Memuat...");
        } else {
            btnLogin.setText("Masuk");
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToMainActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}