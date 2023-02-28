package com.example.musicplayerkotlin.Adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerkotlin.MainActivity
import com.example.musicplayerkotlin.Models.MusicList
import com.example.musicplayerkotlin.Models.formatDuration
import com.example.musicplayerkotlin.PlayerActivity
import com.example.musicplayerkotlin.PlayerActivity.Companion.SongPosition
import com.example.musicplayerkotlin.PlayerActivity.Companion.musicListPA
import com.example.musicplayerkotlin.R
import com.example.musicplayerkotlin.databinding.FavouriteViewBinding
import com.example.musicplayerkotlin.databinding.MusicViewBinding

class FavouriteMusicAdapter(private val context: Context, private var musicList: ArrayList<MusicList>)
    : RecyclerView.Adapter<FavouriteMusicAdapter.MyViewHolder>() {
    class MyViewHolder(binding: FavouriteViewBinding) : RecyclerView.ViewHolder(binding.root) {

        val title  = binding.songNamefv
        val image  = binding.songimagefv
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteMusicAdapter.MyViewHolder {
        return MyViewHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: FavouriteMusicAdapter.MyViewHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.title.isSelected = true
//
//        Glide.with(context).load(musicList[position].songimguri).apply(
//            RequestOptions().placeholder(
//                R.drawable.music
//            ).centerCrop()
//        ).into(holder.image)

        holder.root.setOnClickListener{
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","FavouriteAdapter")
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}