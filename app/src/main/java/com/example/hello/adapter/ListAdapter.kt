package com.example.hello.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hello.databinding.MapItemBinding

data class ListLayout(
    val id: String,
    val name: String,      // 장소명
    val road: String,      // 도로명 주소
    val address: String,   // 지번 주소
    val x: Double,         // 경도(Longitude)
    val y: Double         // 위도(Latitude)
)

class ListAdapter(val onClickSelect: (loc: ListLayout) -> Unit)
    : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    lateinit var items:ArrayList<ListLayout>

    fun build(i:ArrayList<ListLayout>): ListAdapter {
        items = i
        return this
    }

    class ViewHolder(val binding: MapItemBinding):
        RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(item: ListLayout){
            binding.listName.text = item.name
            binding.listRoad.text = item.road
            binding.listAddress.text = item.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            ListAdapter.ViewHolder =
        ViewHolder(MapItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
        holder.binding.select.setOnClickListener {
            onClickSelect.invoke(items[position]) //1. deleteimage가 눌렸을때 listposition를 전달하면서 onClickDeleteIcon함수를 실행한다.
        }
        holder.binding.locItem.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener

    override fun getItemCount(): Int = items.size
}