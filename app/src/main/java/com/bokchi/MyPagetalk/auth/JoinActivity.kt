package com.bokchi.MyPagetalk.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bokchi.MyPagetalk.MainActivity
import com.bokchi.MyPagetalk.R
import com.bokchi.MyPagetalk.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.database

class JoinActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityJoinBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)
        database = Firebase.database.reference

        binding.joinBtn.setOnClickListener {

            var isGoToJoin = true

            val email = binding.emailArea.text.toString()
            val password1 = binding.passwordArea1.text.toString()
            val password2 = binding.passwordArea2.text.toString()
            val nickname = binding.nicknameArea.text.toString()
            val birthdate = binding.birthdateArea.text.toString()
            val phone = binding.phoneArea.text.toString()

            // 저기 값이 비어있는지 확인
            if(email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            if(password1.isEmpty()) {
                Toast.makeText(this, "Password1을 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            if(password2.isEmpty()) {
                Toast.makeText(this, "Password2을 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            // 비밀번호 2개가 같은지 확인
            if(!password1.equals(password2)) {
                Toast.makeText(this, "비밀번호를 똑같이 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            // 비밀번호가 6자 이상인지
            if (password1.length < 6) {
                Toast.makeText(this, "비밀번호를 6자리 이상으로 입력해주세요", Toast.LENGTH_LONG).show()
                isGoToJoin = false
            }

            if(isGoToJoin) {
                auth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 사용자 정보 저장
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            database.child("users").child(userId).apply {
                                child("nickname").setValue(nickname)
                                child("birthdate").setValue(birthdate)
                                child("phone").setValue(phone)
                            }
                        }

                        Toast.makeText(this, "성공", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "실패", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}