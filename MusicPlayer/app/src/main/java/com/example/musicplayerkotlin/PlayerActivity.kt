package com.example.musicplayerkotlin

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerkotlin.Models.*
import com.example.musicplayerkotlin.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.system.exitProcess

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {

    companion object {
        lateinit var musicListPA: ArrayList<MusicList>
        var SongPosition: Int = 0
        var isPlaying: Boolean = false
        var musicService: MusicService? = null

        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat: Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var nowPLayingMusic: String = ""
        var isFavourite: Boolean = false
        var fIndex: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.songname.isSelected = true

        if (intent.data?.scheme.contentEquals("content")){
            startService()
            musicListPA = ArrayList()
            musicListPA.add(getMusicDetails(intent.data!!))
            binding.songname.text = musicListPA[SongPosition].title
        }
        SongPosition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "FavouriteAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favouriteSongs)
                SetLayout()
            }
            "NowPlayingFragment" -> {
                SetLayout()
                binding.startingsongtime.text =
                    formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                binding.endingsongtime.text =
                    formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                binding.seeksongs.progress = musicService!!.mediaPlayer!!.currentPosition
                binding.seeksongs.max = musicService!!.mediaPlayer!!.duration

                if (isPlaying) binding.playsong.setIconResource(R.drawable.ic_pause)
                else binding.playsong.setIconResource(R.drawable.ic_play)
            }
            "MusicAdapterSearch" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListSearch)
                SetLayout()
            }
            "MusicAdapter" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                SetLayout()
//                playSong()
            }
            "ShuffleMainActivity" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                SetLayout()
            }

            "ShuffleFavouriteActivity" -> {
                startService()
                musicListPA = ArrayList()
                musicListPA.addAll(FavouriteActivity.favouriteSongs)
                musicListPA.shuffle()
                SetLayout()
                playSong()
            }
        }

        binding.backplayer.setOnClickListener { finish() }
        binding.playsong.setOnClickListener {
            if (isPlaying) pauseSong()
            else playSong()
        }

        binding.previoussong.setOnClickListener { previousNextSong(increments = false) }

        binding.nextsong.setOnClickListener { previousNextSong(increments = true) }

        binding.seeksongs.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    musicService!!.mediaPlayer!!.seekTo(p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
        binding.repeat.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeat.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
            } else {
                repeat = false
                binding.repeat.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            }
        }

        binding.equalizer.setOnClickListener {
            try {
                val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(
                    AudioEffect.EXTRA_AUDIO_SESSION,
                    musicService!!.mediaPlayer!!.audioSessionId
                )
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent, 11)
            } catch (e: java.lang.Exception) {
                Toast.makeText(this, "No Equalizer Found in your device...", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        }

        binding.timer.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) {
                showBottomSheetDialog()
            } else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Are you sure want to Stop Timer?")
                    .setPositiveButton("Yes") { _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timer.setColorFilter(ContextCompat.getColor(this, R.color.black))
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }

                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN)
            }
        }

        binding.share.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[SongPosition].path))
            startActivity(Intent.createChooser(shareIntent, "Share Music From Rockstar Music Hd"))
        }

        binding.addfavourite.setOnClickListener{
            if (isFavourite){
                isFavourite = false
                binding.addfavourite.setImageResource(R.drawable.ic_favorite)
                FavouriteActivity.favouriteSongs.removeAt(fIndex)
            }else{
                isFavourite = true
                binding.addfavourite.setImageResource(R.drawable.ic_full_favorite)
                FavouriteActivity.favouriteSongs.add(musicListPA[SongPosition])
            }
        }
    }

    private fun SetLayout() {

        fIndex = favouriteCheck(musicListPA[SongPosition].id)
        val uri = Uri.parse("content://media/external/audio/albumart")
        val artUric = Uri.withAppendedPath(uri,"4210738371465246769").toString()
        Glide.with(this).load(musicListPA[SongPosition].songimguri).apply(
            RequestOptions().placeholder(
                R.drawable.music
            ).centerCrop()
        ).into(binding.songimage)

//        Glide.with(this)
//            .load(musicListPA[SongPosition].songimguri)
//            .dontAnimate()
//            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//            .into(binding.songimage);
        Log.d("IMAGE", musicListPA[SongPosition].songimguri)

        binding.songname.text = musicListPA[SongPosition].title

        if (repeat) {
            binding.repeat.setColorFilter(ContextCompat.getColor(this, R.color.cool_blue))
        }

        if (min15 || min30 || min60) {
            binding.timer.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
        }
        if (isFavourite) binding.addfavourite.setImageResource(R.drawable.ic_full_favorite)
        else binding.addfavourite.setImageResource(R.drawable.ic_favorite)
    }

    private fun playSongs() {
        try {
            if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
            musicService!!.mediaPlayer!!.reset()
            musicService!!.mediaPlayer!!.setDataSource(musicListPA[SongPosition].path)
            musicService!!.mediaPlayer!!.prepare()
            musicService!!.mediaPlayer!!.start()
            isPlaying = true
            binding.playsong.setIconResource(R.drawable.ic_pause)
            musicService!!.showNotification(R.drawable.ic_pause,1F)

            binding.startingsongtime.text =
                formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
            binding.endingsongtime.text =
                formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
            binding.seeksongs.progress = 0
            binding.seeksongs.max = musicService!!.mediaPlayer!!.duration

            musicService!!.mediaPlayer!!.setOnCompletionListener { this }

            nowPLayingMusic = musicListPA[SongPosition].id
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playSong() {
        binding.playsong.setIconResource(R.drawable.ic_pause)
        musicService!!.showNotification(R.drawable.ic_pause,1F)
        isPlaying = true
//        if(isPlaying){
//                    musicService!!.mediaPlayer!!.prepare()
//        }
        musicService!!.mediaPlayer!!.start()
    }

    private fun pauseSong() {
        binding.playsong.setIconResource(R.drawable.ic_play)
        musicService!!.showNotification(R.drawable.ic_play,0F)
        isPlaying = false
        musicService!!.mediaPlayer!!.pause()
    }

    private fun previousNextSong(increments: Boolean) {
        if (increments) {
            forsongPosition(increments = true)
            SetLayout()
            playSongs()
        } else {
            forsongPosition(increments = false)
            SetLayout()
            playSongs()
        }
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as MusicService.MyBinder
        musicService = binder.currentService()
        playSongs()
        musicService!!.seekBarSetting()
        musicService!!.audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicService!!.audioManager.requestAudioFocus(musicService,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(p0: MediaPlayer?) {
        forsongPosition(true)
        playSongs()
        try {
            SetLayout()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 11 && resultCode == RESULT_OK) {
            return
        }
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.timer_bottom_sheet_dialog)
        dialog.show()

        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(this, "Song will stop after 15 Minutes...", Toast.LENGTH_SHORT).show()
            binding.timer.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min15 = true
            Thread {
                Thread.sleep(15 * 60000)
                if (min15) exitApp()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(this, "Song will stop after 30 Minutes...", Toast.LENGTH_SHORT).show()
            binding.timer.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min30 = true
            Thread {
                Thread.sleep(30 * 60000)
                if (min30) exitApp()
            }.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(this, "Song will stop after 60 Minutes...", Toast.LENGTH_SHORT).show()
            binding.timer.setColorFilter(ContextCompat.getColor(this, R.color.purple_500))
            min60 = true
            Thread {
                Thread.sleep(60 * 60000)
                if (min60) exitApp()
            }.start()
            dialog.dismiss()
        }
    }

    fun startService() {
        // for starting music services
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
    }

    private fun getMusicDetails(contentUri : Uri) : MusicList{
        var cursor : Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DURATION)
            cursor = this.contentResolver.query(contentUri,projection,null,null,null)
            val dataColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = cursor?.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            cursor!!.moveToFirst()
            val path = dataColumn?.let { cursor.getString(it) }
            val duration = durationColumn?.let { cursor.getLong(it) }
            return MusicList("unknown",path.toString(),duration!!.toLong(),"unknown","unknown",
                path.toString(),"unknown")
        }finally {
            cursor?.close()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (musicListPA[SongPosition].id == "unknown" && !isPlaying) exitApp()
    }
}