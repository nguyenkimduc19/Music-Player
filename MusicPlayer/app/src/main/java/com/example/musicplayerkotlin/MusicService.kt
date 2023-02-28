package com.example.musicplayerkotlin

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.musicplayerkotlin.Fragments.NowPlayingMusicFragment
import com.example.musicplayerkotlin.Models.formatDuration
import com.example.musicplayerkotlin.Models.getSongImage

class MusicService : Service(),AudioManager.OnAudioFocusChangeListener{

    private var myBinder: MyBinder = MyBinder()
    var mediaPlayer : MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager
    
    override fun onBind(p0: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(this,"Music")
        return myBinder
    }

    inner class MyBinder:Binder(){
        fun currentService(): MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseBtn : Int,playBackSpeed : Float){
        val prevPendingIntent : PendingIntent
        val playPendingIntent : PendingIntent
        val nextPendingIntent : PendingIntent
        val exitPendingIntent : PendingIntent
        val contextIntent : PendingIntent

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {

            val intent = Intent(baseContext,MainActivity::class.java)
            contextIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_IMMUTABLE)

            val prevIntent = Intent(this,NotificationReciever::class.java).setAction(ApplicationClass.PREVIOUS)
            prevPendingIntent = PendingIntent.getBroadcast(baseContext,10,prevIntent,PendingIntent.FLAG_IMMUTABLE)

            val playIntent = Intent(this,NotificationReciever::class.java).setAction(ApplicationClass.PLAY)
            playPendingIntent = PendingIntent.getBroadcast(baseContext,10,playIntent,PendingIntent.FLAG_IMMUTABLE)

            val nextIntent = Intent(this,NotificationReciever::class.java).setAction(ApplicationClass.NEXT)
            nextPendingIntent = PendingIntent.getBroadcast(baseContext,10,nextIntent,PendingIntent.FLAG_IMMUTABLE)

            val exitIntent = Intent(this,NotificationReciever::class.java).setAction(ApplicationClass.EXIT)
            exitPendingIntent = PendingIntent.getBroadcast(baseContext,10,exitIntent,PendingIntent.FLAG_IMMUTABLE)
        }
        else
        {
            val intent = Intent(baseContext,MainActivity::class.java)
            contextIntent = PendingIntent.getActivity(this,0,intent,0)

            val prevIntent = Intent(this,NotificationReciever::class.java).setAction(ApplicationClass.PREVIOUS)
             prevPendingIntent = PendingIntent.getBroadcast(baseContext,10,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val playIntent = Intent(this,NotificationReciever::class.java).setAction(ApplicationClass.PLAY)
             playPendingIntent = PendingIntent.getBroadcast(baseContext,10,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val nextIntent = Intent(this,NotificationReciever::class.java).setAction(ApplicationClass.NEXT)
             nextPendingIntent = PendingIntent.getBroadcast(baseContext,10,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val exitIntent = Intent(this,NotificationReciever::class.java).setAction(ApplicationClass.EXIT)
             exitPendingIntent = PendingIntent.getBroadcast(baseContext,10,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        }

//        val songImage = getSongImage(PlayerActivity.musicListPA[PlayerActivity.SongPosition].path)

//       val image = if (songImage != null){
//            BitmapFactory.decodeByteArray(songImage,0,songImage.size)
//        }else{
//            BitmapFactory.decodeResource(resources,R.drawable.music)
//        }

        val notification = NotificationCompat.Builder(this,ApplicationClass.CHANNEL_ID)
            .setContentIntent(contextIntent)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.SongPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.SongPosition].artist)
            .setSmallIcon(R.drawable.ic_service_music)
//            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_priveous_new,"Previous",prevPendingIntent)
            .addAction(playPauseBtn,"Play",playPendingIntent)
            .addAction(R.drawable.ic_next,"Next",nextPendingIntent)
            .addAction(R.drawable.ic_exit,"Exit",exitPendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            mediaSession.setMetadata(MediaMetadataCompat.Builder().putLong(MediaMetadataCompat.METADATA_KEY_DURATION
                ,mediaPlayer!!.duration.toLong()).build())

            mediaSession.setPlaybackState(PlaybackStateCompat.Builder().setState(PlaybackStateCompat.STATE_PLAYING
                ,mediaPlayer!!.currentPosition.toLong(),playBackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO).build())
        }

        startForeground(101,notification)
    }

    fun playSongs(){
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.SongPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()

            PlayerActivity.binding.playsong.setIconResource(R.drawable.ic_pause)
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause,0F)


            PlayerActivity.binding.startingsongtime.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.endingsongtime.text = formatDuration(mediaPlayer!!.duration.toLong())

            PlayerActivity.binding.seeksongs.progress = 0
            PlayerActivity.binding.seeksongs.max = mediaPlayer!!.duration

            PlayerActivity.nowPLayingMusic = PlayerActivity.musicListPA[PlayerActivity.SongPosition].id

        }catch (e : Exception ){
            e.printStackTrace()
        }
    }


    @SuppressLint("SetTextI18n")
    fun seekBarSetting(){
        runnable = Runnable {
            PlayerActivity.binding.startingsongtime.text = formatDuration(mediaPlayer!!.currentPosition.toLong())+""
            PlayerActivity.binding.seeksongs.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onAudioFocusChange(p0: Int) {
        if(p0 <= 0){
            // pause Music
            PlayerActivity.binding.playsong.setIconResource(R.drawable.ic_play)
            NowPlayingMusicFragment.binding.playPausebtnfrg.setIconResource(R.drawable.ic_play)
            showNotification(R.drawable.ic_play,0F)
            PlayerActivity.isPlaying = false
            mediaPlayer!!.pause()

        }else{
            // play Music
            PlayerActivity.binding.playsong.setIconResource(R.drawable.ic_pause)
            NowPlayingMusicFragment.binding.playPausebtnfrg.setIconResource(R.drawable.ic_pause)
            showNotification(R.drawable.ic_pause,1F)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.isPlaying = true
            mediaPlayer!!.start()
        }
    }
}