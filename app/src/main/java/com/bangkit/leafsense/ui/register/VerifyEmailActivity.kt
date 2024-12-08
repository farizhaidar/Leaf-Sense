package com.bangkit.leafsense.ui.register

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.leafsense.databinding.ActivityVerifyEmailBinding
import com.bangkit.leafsense.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class VerifyEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyEmailBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval = 5000L // Interval untuk cek status verifikasi (5 detik)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        binding.tvTitle.text = "Menunggu Verifikasi Email"
        binding.tvDescription.text = "Kami telah mengirimkan email verifikasi ke ${user.email}. Silakan periksa dan klik tautan verifikasi pada email Anda."

        binding.tvBackToRegister.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            user?.let {
                it.delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, RegisterActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Gagal menghapus data pengguna: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } ?: run {
                Toast.makeText(this, "Tidak ada pengguna yang terautentikasi.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Mulai pengecekan otomatis
        startCheckingEmailVerification()
    }

    private fun startCheckingEmailVerification() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                user.reload().addOnCompleteListener { task ->
                    if (task.isSuccessful && user.isEmailVerified) {
                        // Email berhasil diverifikasi
                        Toast.makeText(this@VerifyEmailActivity, "Email berhasil diverifikasi!", Toast.LENGTH_SHORT).show()
                        navigateToLoginActivity()
                    } else {
                        // Jika belum diverifikasi, cek ulang setelah interval
                        handler.postDelayed(this, checkInterval)
                    }
                }
            }
        }, checkInterval)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
