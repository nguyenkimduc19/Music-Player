package com.example.musicplayerkotlin

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerkotlin.Adapters.MusicListAdapter
import com.example.musicplayerkotlin.Models.MusicList
import com.example.musicplayerkotlin.Models.exitApp
import com.example.musicplayerkotlin.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicListAdapter: MusicListAdapter

    companion object{
        lateinit var MusicListMA : ArrayList<MusicList>
        lateinit var MusicListSearch : ArrayList<MusicList>
        var search : Boolean = false
        var sortOrder : Int = 0
        val soritngList = arrayOf(MediaStore.Audio.Media.DATE_ADDED+" DESC",MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.SIZE+" DESC")
    }
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestRunTimePermission()
        setTheme(R.style.coolPinkNav)
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

            // for nav drawer
        toggle = ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)

        if(requestRunTimePermission()){
            initializeLayout()
        }

        binding.shuffle.setOnClickListener{
            Toast.makeText(this,"Shuffle",Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity,PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","ShuffleMainActivity")
            startActivity(intent)

        }

        binding.favourite.setOnClickListener {
            Toast.makeText(this,"Favourite",Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity,FavouriteActivity::class.java)
            startActivity(intent)
        }

        binding.playlist.setOnClickListener {
            Toast.makeText(this,"Playlist",Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MainActivity,PlaylistActivity::class.java)
            startActivity(intent)
        }

        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.menu_feedback ->  startActivity(Intent(this,FeedbackActivity::class.java))
                R.id.menu_theme ->  Toast.makeText(this,"Themes",Toast.LENGTH_SHORT).show()
                R.id.menu_about ->  startActivity(Intent(this,AboutActivity::class.java))
                R.id.menu_exit ->  { exitMDiaog() }
                R.id.menu_settings ->  startActivity(Intent(this,SettingsActivity::class.java))
                R.id.menu_sort ->  Toast.makeText(this,"Sort",Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    // for requesing permission
    private fun requestRunTimePermission() : Boolean{
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 13){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted...",Toast.LENGTH_SHORT).show()
                initializeLayout()
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio() : ArrayList<MusicList>{

        val tempList = ArrayList<MusicList>()
        val selection =  MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM
            ,MediaStore.Audio.Media.ALBUM_ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED
            ,MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM_ID)
        val cursur = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,
            soritngList[sortOrder],null)
        if (cursur != null && cursur.getCount() > 0){
            if (cursur.moveToFirst()){
                do {
                    val titlec = cursur.getString(cursur.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idc = cursur.getString(cursur.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumc = cursur.getString(cursur.getColumnIndex(MediaStore.Audio.Media.ALBUM))
//                    val artistc = cursur.getString(cursur.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathc = cursur.getString(cursur.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationc = cursur.getLong(cursur.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val songimageidc = cursur.getLong(cursur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUric = Uri.withAppendedPath(uri,songimageidc).toString()
                    val musicl = MusicList(idc,titlec,durationc,albumc,"artistc",pathc,artUric)
                    val file = File(musicl.path)
                    if (file.exists()){
                        tempList.add(musicl)
                    }
                }while (cursur.moveToNext())
                cursur.close()
            }
        }
        binding.totalsongs.setText("123")
//        binding.totalsongs.text = tempList.size.toString()
        Log.d("TotalSongs",tempList.size.toString())
            return tempList
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initializeLayout(){
        search = false
        binding.musicRv.setHasFixedSize(true)
        binding.musicRv.setItemViewCacheSize(13)
        binding.musicRv.layoutManager = LinearLayoutManager(this)

        val MusicList = ArrayList<String>()
//        MusicList.add("First Song")
//        MusicList.add("Second Song")
//        MusicList.add("Third Song")

        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
       sortOrder = sortEditor.getInt("sortOrder",0)
        MusicListMA = getAllAudio()
        musicListAdapter = MusicListAdapter(this, MusicListMA)
        binding.musicRv.adapter = musicListAdapter
        binding.totalsongs.text = "Total Songs : "+ MusicListMA.size

    }

    override fun onDestroy() {
        super.onDestroy()
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null){
            exitApp()
        }
    }

    private fun exitMDiaog(){
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Exit")
            .setMessage("Are you sure want to exit?")
            .setPositiveButton("Yes"){_,_->
                exitApp()
            }
            .setNegativeButton("No"){dialog,_->
                dialog.dismiss()
            }

        val customDialog = builder.create()
        customDialog.show()
        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        val searchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean = true

            override fun onQueryTextChange(p0: String?): Boolean {
                MusicListSearch = ArrayList()

                if (p0 != null){
                    val userInput = p0.lowercase()
                    for (song in MusicListMA){
                        if (song.title.lowercase().contains(userInput)){
                            MusicListSearch.add(song)
                        }
                        search = true
                        musicListAdapter.updateMusicList(searchList = MusicListSearch)
                    }
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)

    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        // for Sot=rting songs
        val sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        val sortValue = sortEditor.getInt("sortOrder",0)
        if (sortOrder != sortValue){
            sortOrder = sortValue
            MusicListMA = getAllAudio()
            musicListAdapter.updateMusicList(MusicListMA)
        }

    }
}