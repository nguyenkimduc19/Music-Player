package com.example.musicplayerkotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerkotlin.Fragments.NowPlayingMusicFragment
import com.example.musicplayerkotlin.Models.exitApp
import com.example.musicplayerkotlin.Models.favouriteCheck
import com.example.musicplayerkotlin.Models.forsongPosition
import kotlin.system.exitProcess

class NotificationReciever : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        when (p1?.action) {
            ApplicationClass.PREVIOUS -> prevNextSong(false, context = p0!!)
            ApplicationClass.PLAY -> if (PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(true, context = p0!!)
            ApplicationClass.EXIT -> {
                exitApp()
            }
        }
    }

    private fun playMusic() {
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.prepare()
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause,1F)
        PlayerActivity.binding.playsong.setIconResource(R.drawable.ic_pause)
        NowPlayingMusicFragment.binding.playPausebtnfrg.setIconResource(R.drawable.ic_pause)
    }

    private fun pauseMusic() {
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play,0F)
        PlayerActivity.binding.playsong.setIconResource(R.drawable.ic_play)
        NowPlayingMusicFragment.binding.playPausebtnfrg.setIconResource(R.drawable.ic_play)
    }

    private fun prevNextSong(increment: Boolean, context: Context) {
        forsongPosition(increment)
        PlayerActivity.musicService!!.playSongs()

//        Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.SongPosition].songimguri)
//            .apply(
//                RequestOptions().placeholder(
//                    R.drawable.music
//                ).centerCrop()
//            ).into(PlayerActivity.binding.songimage)
        PlayerActivity.binding.songname.text =
            PlayerActivity.musicListPA[PlayerActivity.SongPosition].title
//
//        Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.SongPosition].songimguri)
//            .apply(
//                RequestOptions().placeholder(
//                    R.drawable.music
//                ).centerCrop()
//            ).into(NowPlayingMusicFragment.binding.songimgfrg)
        NowPlayingMusicFragment.binding.songNamefrg.text =
            PlayerActivity.musicListPA[PlayerActivity.SongPosition].title
        playMusic()

        PlayerActivity.fIndex = favouriteCheck(PlayerActivity.musicListPA[PlayerActivity.SongPosition].id)
        if(PlayerActivity.isFavourite) PlayerActivity.binding.addfavourite.setImageResource(R.drawable.ic_full_favorite)
        else PlayerActivity.binding.addfavourite.setImageResource(R.drawable.ic_favorite)
    }
}