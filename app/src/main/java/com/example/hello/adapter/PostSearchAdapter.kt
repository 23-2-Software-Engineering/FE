package com.example.hello.adapter

import android.annotation.*
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.hello.model.PostDTO
import com.example.se_proj.data.model.PostDTO

class PostSearchAdapter(private var context: Context?, private var postList: ArrayList<PostDTO>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return postList.size
    }

    override fun getItem(position: Int): Any {
        return postList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (convertView == null) {
            var inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.postview_item, parent, false)

        }
        // 이미지 URL 소스 넣기
        val imageView: ImageView = convertView!!.findViewById(R.id.postview_img)
 //       val imageSource: String = postList[position].postData[0].pictures[0]
//        imageView.setImageURI(Uri.parse(imageSource))
        imageView.setBackgroundResource(R.drawable.dummy_img)

        // 포스트 제목 넣기
        val textView: TextView = convertView!!.findViewById(R.id.postview_text)
        textView.text = "post title" + position.toString();
//        val postTitle: String = postList[position].title
//        textView.text = postTitle

        return convertView!!
    }

}