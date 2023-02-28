package com.example.musicplayerkotlin.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerkotlin.Models.forsongPosition
import com.example.musicplayerkotlin.PlayerActivity
import com.example.musicplayerkotlin.R
import com.example.musicplayerkotlin.databinding.FragmentNowPlayingMusicBinding

class NowPlayingMusicFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingMusicBinding
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_now_playing_music, container, false)
        binding = FragmentNowPlayingMusicBinding.bind(view)

        binding.root.visibility = View.INVISIBLE
        binding.playPausebtnfrg.setOnClickListener{
            if (PlayerActivity.isPlaying) pauseMusic()
            else playMusic()
        }

        binding.nextbtnfrg.setOnClickListener{
            forsongPosition(true)
            PlayerActivity.musicService!!.playSongs()
            PlayerActivity.binding.songname.text =
                PlayerActivity.musicListPA[PlayerActivity.SongPosition].title

//            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.SongPosition].songimguri)
//                .apply(
//                    RequestOptions().placeholder(
//                        R.drawable.music
//                    ).centerCrop()
//                ).into(binding.songimgfrg)
            Log.d("SongUri", PlayerActivity.musicListPA[PlayerActivity.SongPosition].songimguri)
            binding.songNamefrg.text = PlayerActivity.musicListPA[PlayerActivity.SongPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause,1F)
            playMusic()
        }

        binding.root.setOnClickListener{
            val intent = Intent(requireContext(),PlayerActivity::class.java)
            intent.putExtra("index",PlayerActivity.SongPosition)
            intent.putExtra("class","NowPlayingFragment")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            binding.songNamefrg.isSelected = true
//            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.SongPosition].songimguri).apply(
//                RequestOptions().placeholder(
//                    R.drawable.music
//                ).centerCrop()
//            ).into(binding.songimgfrg)

            binding.songNamefrg.text = PlayerActivity.musicListPA[PlayerActivity.SongPosition].title

            if (PlayerActivity.isPlaying) binding.playPausebtnfrg.setIconResource(R.drawable.ic_pause)
            else binding.playPausebtnfrg.setIconResource(R.drawable.ic_play)
        }
    }

    private fun playMusic(){
//        PlayerActivity.musicService!!.mediaPlayer!!.prepare()
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.playPausebtnfrg.setIconResource(R.drawable.ic_pause)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_pause,1F)
        PlayerActivity.binding.playsong.setIconResource(R.drawable.ic_pause)
        PlayerActivity.isPlaying = true
    }
    private fun pauseMusic(){
        PlayerActivity.musicService!!.mediaPlayer!!.stop()
        binding.playPausebtnfrg.setIconResource(R.drawable.ic_play)
        PlayerActivity.musicService!!.showNotification(R.drawable.ic_play,0F)
        PlayerActivity.binding.playsong.setIconResource(R.drawable.ic_play)
        PlayerActivity.isPlaying = false
    }
}