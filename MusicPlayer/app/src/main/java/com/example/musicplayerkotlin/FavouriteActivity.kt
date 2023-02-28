package com.example.musicplayerkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerkotlin.Adapters.FavouriteMusicAdapter
import com.example.musicplayerkotlin.Adapters.MusicListAdapter
import com.example.musicplayerkotlin.Models.MusicList
import com.example.musicplayerkotlin.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var favouriteMusicAdapter: FavouriteMusicAdapter

    companion object{
        var favouriteSongs : ArrayList<MusicList> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backfavourite.setOnClickListener{ finish() }

        binding.favouriterv.setHasFixedSize(true)
        binding.favouriterv.setItemViewCacheSize(13)
        binding.favouriterv.layoutManager = GridLayoutManager(this,3)

        val MusicList = ArrayList<String>()
//        MusicList.add("First Song")
//        MusicList.add("Second Song")
//        MusicList.add("Third Song")

        favouriteMusicAdapter = FavouriteMusicAdapter(this, favouriteSongs)
        binding.favouriterv.adapter = favouriteMusicAdapter

        if(favouriteSongs.size < 1) binding.shufflefavourite.visibility = View.INVISIBLE
        binding.shufflefavourite.setOnClickListener{
            Toast.makeText(this,"Shuffle", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@FavouriteActivity,PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","ShuffleFavouriteActivity")
            startActivity(intent)
        }
    }
}