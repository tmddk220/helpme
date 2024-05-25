package com.bokchi.MyPagetalk.setting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bokchi.MyPagetalk.R
import com.bokchi.MyPagetalk.auth.IntroActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var nicknameTextView: TextView
    private lateinit var birthdateTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        nicknameTextView = findViewById(R.id.nicknameText)
        birthdateTextView = findViewById(R.id.birthdateText)
        phoneTextView = findViewById(R.id.phoneText)

        val logoutBtn : Button = findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {

            auth.signOut()

            Toast.makeText(this, "로그아웃", Toast.LENGTH_LONG).show()

            val intent = Intent(this, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }

        // 사용자 정보 불러오기
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nickname = snapshot.child("nickname").getValue(String::class.java)
                    val birthdate = snapshot.child("birthdate").getValue(String::class.java)
                    val phone = snapshot.child("phone").getValue(String::class.java)

                    // 정보가 null이 아니면 TextView에 표시
                    if (nickname != null) nicknameTextView.text = "닉네임: $nickname"
                    if (birthdate != null) birthdateTextView.text = "생년월일: $birthdate"
                    if (phone != null) phoneTextView.text = "휴대폰 번호: $phone"
                }

                override fun onCancelled(error: DatabaseError) {
                    // 데이터 로딩 실패 시 처리
                    Toast.makeText(this@SettingActivity, "사용자 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
