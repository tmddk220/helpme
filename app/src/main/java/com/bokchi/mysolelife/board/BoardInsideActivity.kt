package com.bokchi.mysolelife.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.comment.CommentLVAdapter
import com.bokchi.mysolelife.comment.CommentModel
import com.bokchi.mysolelife.databinding.ActivityBoardInsideBinding
import com.bokchi.mysolelife.utils.FBAuth
import com.bokchi.mysolelife.utils.FBRef
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

// BoardInsideActivity 클래스 선언
class BoardInsideActivity : AppCompatActivity() {

    private val TAG = BoardInsideActivity::class.java.simpleName // 로그 태그
    private lateinit var binding: ActivityBoardInsideBinding // ViewBinding 객체
    private lateinit var key: String // 게시글 키
    private val commentDataList = mutableListOf<CommentModel>() // 댓글 데이터 리스트
    private lateinit var commentAdapter: CommentLVAdapter // 댓글 리스트 뷰 어댑터

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // ViewBinding 설정
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_inside)

        // 설정 아이콘 클릭 리스너 설정
        binding.boardSettingIcon.setOnClickListener {
            showDialog()
        }

        // 인텐트로부터 게시글 키를 가져옴
        key = intent.getStringExtra("key").toString()
        getBoardData(key) // 게시글 데이터 가져오기
        getImageData(key) // 게시글 이미지 가져오기

        // 댓글 버튼 클릭 리스너 설정
        binding.commentBtn.setOnClickListener {
            insertComment(key)
        }

        getCommentData(key) // 댓글 데이터 가져오기

        // 댓글 리스트 뷰 어댑터 설정
        commentAdapter = CommentLVAdapter(commentDataList)
        binding.commentLV.adapter = commentAdapter

    }

    // 댓글 데이터 가져오기
    fun getCommentData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                commentDataList.clear() // 기존 댓글 데이터 삭제

                // Firebase로부터 댓글 데이터를 가져옴
                for (dataModel in dataSnapshot.children) {
                    val item = dataModel.getValue(CommentModel::class.java)
                    commentDataList.add(item!!)
                }

                commentAdapter.notifyDataSetChanged() // 어댑터에 데이터 변경 통보

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 데이터 가져오기 실패 시 로그 메시지 출력
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.commentRef.child(key).addValueEventListener(postListener)
    }

    // 댓글 입력하기
    fun insertComment(key: String) {
        FBRef.commentRef
            .child(key)
            .push()
            .setValue(
                CommentModel(
                    binding.commentArea.text.toString(),
                    FBAuth.getTime()
                )
            )

        Toast.makeText(this, "댓글 입력 완료", Toast.LENGTH_SHORT).show()
        binding.commentArea.setText("") // 댓글 입력창 초기화
    }

    // 게시글 수정/삭제 다이얼로그 보여주기
    private fun showDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("게시글 수정/삭제")

        val alertDialog = mBuilder.show()
        alertDialog.findViewById<Button>(R.id.editBtn)?.setOnClickListener {
            Toast.makeText(this, "수정 버튼을 눌렀습니다", Toast.LENGTH_LONG).show()

            val intent = Intent(this, BoardEditActivity::class.java)
            intent.putExtra("key", key)
            startActivity(intent)
        }

        alertDialog.findViewById<Button>(R.id.removeBtn)?.setOnClickListener {
            FBRef.boardRef.child(key).removeValue()
            Toast.makeText(this, "삭제완료", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    // 게시글 이미지 가져오기
    private fun getImageData(key: String) {
        val storageReference = Firebase.storage.reference.child(key + ".png") // 이미지 파일 참조
        val imageViewFromFB = binding.getImageArea // 이미지 뷰

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(imageViewFromFB)
            } else {
                binding.getImageArea.isVisible = false // 이미지가 없을 경우 이미지 뷰 숨김
            }
        })
    }

    // 게시글 데이터 가져오기
    private fun getBoardData(key: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    val dataModel = dataSnapshot.getValue(BoardModel::class.java)
                    Log.d(TAG, dataModel!!.title)

                    binding.titleArea.text = dataModel!!.reviewtitle
                    binding.textArea.text = dataModel!!.content
                    binding.timeArea.text = dataModel!!.time
                    binding.authorArea.text = dataModel!!.author
                    binding.ratingBar.rating = dataModel!!.rating
                    binding.booktitleArea.text = dataModel!!.title



                    val myUid = FBAuth.getUid()
                    val writerUid = dataModel.uid

                    if (myUid == writerUid) {
                        Log.d(TAG, "내가 쓴 글")
                        binding.boardSettingIcon.isVisible = true
                    } else {
                        Log.d(TAG, "내가 쓴 글 아님")
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "삭제완료")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.boardRef.child(key).addValueEventListener(postListener)
    }
}