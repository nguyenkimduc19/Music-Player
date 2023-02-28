package com.example.musicplayerkotlin.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerkotlin.MainActivity
import com.example.musicplayerkotlin.Models.MusicList
import com.example.musicplayerkotlin.Models.formatDuration
import com.example.musicplayerkotlin.PlayerActivity
import com.example.musicplayerkotlin.R
import com.example.musicplayerkotlin.databinding.MusicViewBinding

class MusicListAdapter(private val context: Context, private var musicList: ArrayList<MusicList>) : RecyclerView.Adapter<MusicListAdapter.MyViewHolder>() {
    class MyViewHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {

        val title  = binding.songname
        val album  = binding.albumname
        val songimage  = binding.imagemusicview
        val songDuration  = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicListAdapter.MyViewHolder {
        return MyViewHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicListAdapter.MyViewHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.songDuration.text = formatDuration(musicList[position].duration)
//        Glide.with(context).load(musicList[position].songimguri).apply(RequestOptions().placeholder(
//            R.drawable.music).centerCrop()).into(holder.songimage)
        holder.root.setOnClickListener{
            when{
                MainActivity.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                musicList[position].id == PlayerActivity.nowPLayingMusic -> {
                    sendIntent("NowPlayingMusic",PlayerActivity.SongPosition)
                }
                else -> sendIntent(ref = "MusicAdapter", pos = position)
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList :ArrayList<MusicList>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref : String, pos : Int ,){
        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
}