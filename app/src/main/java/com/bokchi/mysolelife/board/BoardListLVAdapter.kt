package com.bokchi.mysolelife.board

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.bokchi.mysolelife.R
import com.bokchi.mysolelife.utils.FBAuth
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class BoardListLVAdapter(val context: Context, val boardList: MutableList<BoardModel>, val boardKeyList: MutableList<String>) : BaseAdapter() {

    override fun getCount(): Int {
        return boardList.size
    }

    override fun getItem(position: Int): Any {
        return boardList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.board_list_item, parent, false)
        }

        val itemLinearLayoutView = view?.findViewById<LinearLayout>(R.id.itemView)
        val title = view?.findViewById<TextView>(R.id.titleArea)
        val reviewtitle = view?.findViewById<TextView>(R.id.reviewtitleArea)
        val author  = view?.findViewById<TextView>(R.id.authorArea)
        //val content = view?.findViewById<TextView>(R.id.contentArea)
        val time = view?.findViewById<TextView>(R.id.timeArea)
        val image = view?.findViewById<ImageView>(R.id.getImageArea)

        if (boardList[position].uid == FBAuth.getUid()) {
            itemLinearLayoutView?.setBackgroundColor(Color.parseColor("#ffa500"))
        }

        title!!.text = boardList[position].title
        //content!!.text = boardList[position].content
        time!!.text = boardList[position].time
        author!!.text = boardList[position].author
        reviewtitle!!.text = boardList[position].reviewtitle

        // 이미지 다운로드 및 표시
        getImageData(boardKeyList[position], image)

        return view!!
    }

    // 이미지 다운로드 및 표시
    private fun getImageData(key: String, imageView: ImageView?) {
        if (key.isNotEmpty()) { // key가 비어있지 않은지 확인
            val storageReference = Firebase.storage.reference.child("$key.png")
            storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(context)
                        .load(task.result)
                        .placeholder(R.drawable.iconsplaceholder)
                        .error(R.drawable.iconserror)
                        .into(imageView!!)
                } else {
                    imageView?.isVisible = false
                }
            })
        }
    }
}
