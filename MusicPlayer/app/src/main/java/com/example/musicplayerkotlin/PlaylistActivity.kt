package com.example.musicplayerkotlin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerkotlin.Adapters.MusicListAdapter
import com.example.musicplayerkotlin.Adapters.PlaylistMusicAdapter
import com.example.musicplayerkotlin.Models.MusicPlaylist
import com.example.musicplayerkotlin.Models.Playlist
import com.example.musicplayerkotlin.Models.exitApp
import com.example.musicplayerkotlin.databinding.ActivityPlaylistBinding
import com.example.musicplayerkotlin.databinding.AddPlaylistAlertDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adapter : PlaylistMusicAdapter

    companion object{
        var musicPlaylist : MusicPlaylist = MusicPlaylist()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backplaylist.setOnClickListener{
            Toast.makeText(this,"Back Button", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@PlaylistActivity,MainActivity::class.java)
            startActivity(intent)
        }

        binding.addplaylist.setOnClickListener{
            Toast.makeText(this,"Add Button", Toast.LENGTH_SHORT).show()
        }

        binding.backplaylist.setOnClickListener{ finish() }

        binding.playlistrv.setHasFixedSize(true)
        binding.playlistrv.setItemViewCacheSize(13)
        binding.playlistrv.layoutManager = GridLayoutManager(this,2)

        val MusicList = ArrayList<String>()
        adapter = PlaylistMusicAdapter(this, musicPlaylist.pList)
        binding.playlistrv.adapter = adapter

        binding.addplaylist.setOnClickListener{
            customAlertD()
        }
    }

    private fun customAlertD(){
        val customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_alert_dialog,binding.root,false)
        val binder = AddPlaylistAlertDialogBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog).setTitle("Add Playlist")
            .setPositiveButton("Add"){dialog,_->
               val playlistname = binder.plname.text
                val createdBy = binder.yournamepl.text

                if (playlistname != null && createdBy != null){
                    if (playlistname.isNotEmpty() && createdBy.isNotEmpty()){
                        addPlaylist(playlistname.toString(),createdBy.toString())
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("No"){dialog,_->
                dialog.dismiss()
            }.show()
    }

    private fun addPlaylist(name : String, createdBy : String){
        var alreadyPl = false
        for (i in musicPlaylist.pList){
            if (name.equals(i.name)){
                alreadyPl = true
                break
            }
        }
        if (alreadyPl) Toast.makeText(this,"Alerady Playlist Created...",Toast.LENGTH_SHORT).show()
        else{
            val templist = Playlist()
            templist.name = name
            templist.playlist = ArrayList()
            templist.createdBy = createdBy
            val calender = Calendar.getInstance().time
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy",Locale.ENGLISH)
            templist.createdOn = simpleDateFormat.format(calender)
            musicPlaylist.pList.add(templist)
            adapter.refreshPlaylist()
        }
    }
}