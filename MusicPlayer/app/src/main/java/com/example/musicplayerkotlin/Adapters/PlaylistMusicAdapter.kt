package com.example.musicplayerkotlin.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerkotlin.Models.MusicList
import com.example.musicplayerkotlin.Models.Playlist
import com.example.musicplayerkotlin.Models.exitApp
import com.example.musicplayerkotlin.PlayerActivity
import com.example.musicplayerkotlin.PlaylistActivity
import com.example.musicplayerkotlin.PlaylistDetailsActivity
import com.example.musicplayerkotlin.R
import com.example.musicplayerkotlin.databinding.FavouriteViewBinding
import com.example.musicplayerkotlin.databinding.PlalistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistMusicAdapter(private val context: Context, private var playlistList: ArrayList<Playlist>)
    : RecyclerView.Adapter<PlaylistMusicAdapter.MyViewHolder>() {
    class MyViewHolder(binding: PlalistViewBinding) : RecyclerView.ViewHolder(binding.root) {

        val image  = binding.plalistImg
        val name  = binding.plname
        val deleteplbtn  = binding.pldeletebtn
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistMusicAdapter.MyViewHolder {
        return MyViewHolder(PlalistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: PlaylistMusicAdapter.MyViewHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true

        holder.deleteplbtn.setOnClickListener{
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Are you sure want to delete playlist?")
                .setPositiveButton("Yes"){dialog,_->
                    PlaylistActivity.musicPlaylist.pList.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog,_->
                    dialog.dismiss()
                }

            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN)
        }
//        Glide.with(context).load(musicList[position].songimguri).apply(
//            RequestOptions().placeholder(
//                R.drawable.music
//            ).centerCrop()
//        ).into(holder.image)

        holder.root.setOnClickListener{
            val intent = Intent(context, PlaylistDetailsActivity::class.java)
            intent.putExtra("index",position)
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist(){
        playlistList = ArrayList()
        playlistList.addAll(PlaylistActivity.musicPlaylist.pList)
        notifyDataSetChanged()
    }
}