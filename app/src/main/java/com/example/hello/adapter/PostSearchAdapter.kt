package com.example.hello.adapter

import android.annotation.*
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.hello.R
import com.example.hello.model.PostDTO
import okio.ArrayIndexOutOfBoundsException
import java.lang.NullPointerException


class PostSearchAdapter(private var context: Context?, private var postList: ArrayList<PostDTO>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return postList.size
    }

    override fun getItem(position: Int): Any {
        return postList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater: LayoutInflater = LayoutInflater.from(context)
        var view = convertView
        view = inflater.inflate(R.layout.postview_item, parent, false);

        val postTitle: TextView = view.findViewById(R.id.postview_text)
        try {
            postTitle.setText(postList.get(position).title)
        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.e("POST SEARCH", "ERR: No Post Data, Position: ${position}")
            postTitle.setText("Post Title${position}")
        } catch (e: NullPointerException) {
            Log.e("POST SEARCH", "ERR: Null Pointer Exception, Position: ${position}")
            postTitle.setText("Post Title${position}")
        }

        val postThumbnailView: ImageView = view.findViewById(R.id.postview_img)
        try {
            val UrlStr: String = "http://10.0.2.2:8080" + postList.get(position).postData.first().pictures.first()
            Glide.with(context!!)
                .load(UrlStr)
                .into(postThumbnailView)
            // 지금은 밑에 있는 코드(더미 이미지) 쓰다 바꾸기
            // postThumbnailUtil.setImageResource(R.drawable.dummy_img)

        } catch (e: ArrayIndexOutOfBoundsException) {
            Log.e("POST SEARCH", "MSG: No Image Data, Position: ${position}")
            postThumbnailView.setImageResource(R.drawable.dummy_img)
        }

        view.setOnClickListener(View.OnClickListener() {
            val postId = postList.get(position).postId // 게시글 아이디
            Log.d("POST SEARCH", "MSG: ${postId}번 게시글 클릭!")
        })

        return view!!
    }

}