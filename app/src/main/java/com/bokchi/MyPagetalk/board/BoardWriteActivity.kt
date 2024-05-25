package com.bokchi.MyPagetalk.board

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bokchi.MyPagetalk.R
import com.bokchi.MyPagetalk.databinding.ActivityBoardWriteBinding
import com.bokchi.MyPagetalk.utils.FBAuth
import com.bokchi.MyPagetalk.utils.FBRef
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class BoardWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardWriteBinding

    private val TAG = BoardWriteActivity::class.java.simpleName

    private var isImageUpload = false
    private var selectedRadioButtonId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_write)

        //책제목, 저자, 리뷰제목, 본문 변수선언
        binding.writeBtn.setOnClickListener {

            val title = binding.titleArea.text.toString()
            val content = binding.contentArea.text.toString()
            val author = binding.authorArea.text.toString()
            val reviewtitle = binding.reviewtitleArea.text.toString()
            val rating = binding.ratingBar.rating
            //val genre = grnreEidtText.text.toString()
            val uid = FBAuth.getUid()
            val time = FBAuth.getTime()

            Log.d(TAG, title)
            Log.d(TAG, content)
            Log.d(TAG, author)
            Log.d(TAG, reviewtitle)
            Log.d(TAG, rating.toString())


            // 파이어베이스 스토리지
            // 이미지 이름을 문서의 key값으로 해줘서 이미지에 대한 정보를 찾기 쉽게 해놓음

            val key = FBRef.boardRef.push().key.toString()

            FBRef.boardRef
                .child(key)
                .setValue(BoardModel(title, content, uid, time, reviewtitle, author, rating))
            Toast.makeText(this, "게시글 입력 완료", Toast.LENGTH_LONG).show()

            if (isImageUpload) {
                imageUpload(key)
            }

            finish()
        }

        binding.imageArea.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
            isImageUpload = true
        }

        val radioButtons = listOf(
            binding.genre1,
            binding.genre2,
            binding.genre3,
            binding.genre4,
            binding.genre5,
            binding.genre6,
            binding.genre7,
            binding.genre8,
            binding.genre9,
            binding.genre10,
            binding.genre11,
            binding.genre12,
            binding.genre13,
            binding.genre14
        )

        // 장르 버튼 선택, 취소 기능
        radioButtons.forEach { setupRadioButton(it, radioButtons) }
    }

    private fun imageUpload(key: String) {
        // Get the data from an ImageView as bytes

        val storage = Firebase.storage
        val storageRef = storage.reference
        val mountainsRef = storageRef.child("$key.png")

        val imageView = binding.imageArea
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d(TAG, "Image upload failed")
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            Log.d(TAG, "Image upload successful")
        }
    }

    private fun setupRadioButton(radioButton: RadioButton, radioButtons: List<RadioButton>) {
        radioButton.setOnClickListener {
            if (radioButton.isChecked) {
                if (selectedRadioButtonId == radioButton.id) {
                    radioButton.isChecked = false
                    selectedRadioButtonId = -1
                } else {
                    radioButtons.forEach { it.isChecked = false }
                    radioButton.isChecked = true
                    selectedRadioButtonId = radioButton.id
                }
            } else {
                radioButton.isChecked = true
                selectedRadioButtonId = radioButton.id
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            binding.imageArea.setImageURI(data?.data)
        }
    }
}
