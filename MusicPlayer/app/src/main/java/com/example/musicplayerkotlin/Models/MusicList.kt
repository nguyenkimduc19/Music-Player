package com.example.musicplayerkotlin.Models

import android.media.MediaMetadataRetriever
import android.util.Log
import com.example.musicplayerkotlin.FavouriteActivity
import com.example.musicplayerkotlin.PlayerActivity
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class MusicList(val id: String,val title: String,val duration: Long = 0,val album: String,val artist: String,val path:String
,val songimguri : String) {}

class Playlist{
    lateinit var name : String
    lateinit var playlist : ArrayList<MusicList>
    lateinit var createdBy : String
    lateinit var createdOn : String
}

class MusicPlaylist{
    var pList : ArrayList<Playlist> = ArrayList()
}

fun formatDuration(duration: Long): String{
    val minutes = TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes*TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))

//    Log.d("Duration",String.format("%02d:%02d",minutes,seconds))
    return String.format("%02d:%02d",minutes,seconds)
}

fun getSongImage(path : String): ByteArray? {
    val retriever  = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}


fun forsongPosition(increments: Boolean){
    if (!PlayerActivity.repeat){
        if (increments){
            if (PlayerActivity.musicListPA.size - 1 == PlayerActivity.SongPosition)
                PlayerActivity.SongPosition = 0
            else
                ++PlayerActivity.SongPosition
        }else{
            if (PlayerActivity.SongPosition == 0)
                PlayerActivity.SongPosition = PlayerActivity.musicListPA.size - 1
            else
                --PlayerActivity.SongPosition
        }
    }
}

fun exitApp(){
    if (PlayerActivity.musicService != null) {
        PlayerActivity.musicService!!.audioManager.abandonAudioFocus(PlayerActivity.musicService)
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mediaPlayer!!.release()
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}

fun favouriteCheck(id : String) : Int{

    PlayerActivity.isFavourite = false
    FavouriteActivity.favouriteSongs.forEachIndexed{index,music ->
        if (id == music.id){
            PlayerActivity.isFavourite = true
            return index
        }
    }
    return -1
}