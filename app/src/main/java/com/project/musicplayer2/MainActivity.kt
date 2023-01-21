package com.project.musicplayer2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.musicplayer2.databinding.ActivityMainBinding
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var musicAdapter: MusicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // for nav drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestRuntimePermission())
            initializeLayout()

        binding.apply {
            shuffleBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, PlayerActivity::class.java)
                intent.putExtra("index", 0)
                intent.putExtra("class", "MainActivity")
                startActivity(intent)
            }
            favouriteBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, FavouriteActivity::class.java)
                startActivity(intent)
            }
            playlistBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, PlaylistActivity::class.java)
                startActivity(intent)
            }
            navView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.navFeedback -> Toast.makeText(this@MainActivity, "Feedback", Toast.LENGTH_SHORT).show()
                    R.id.navSettings -> Toast.makeText(this@MainActivity, "Settings", Toast.LENGTH_SHORT).show()
                    R.id.navAbout -> Toast.makeText(this@MainActivity, "About", Toast.LENGTH_SHORT).show()
                    R.id.navExit -> exitProcess(2)
                }
                true
            }
        }
    }
    // For requesting permission
    private fun requestRuntimePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
            return false
        }
        return true
    }

    // Action when the permission is granted or not granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    private fun initializeLayout() {

        MusicListMA = getAllAudio()
        binding.musicRV.setHasFixedSize(true)
        binding.musicRV.setItemViewCacheSize(30)
        binding.musicRV.layoutManager = LinearLayoutManager(this@MainActivity)
        musicAdapter = MusicAdapter(this@MainActivity, MusicListMA)
        binding.musicRV.adapter = musicAdapter
        binding.totalSongs.text = "Total Songs: "+musicAdapter.itemCount.toString()
    }

    @SuppressLint("Range")
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, MediaStore.Audio.Media.DATE_ADDED+" DESC", null)
        cursor?.let {
            if(it.moveToFirst())
                do {
                    val titleC = it.getString(it.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = it.getString(it.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = it.getString(it.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = it.getString(it.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = it.getLong(it.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = it.getLong(it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()
                    val music = Music(id = idC, title = titleC, album = albumC, artist = artistC, duration = durationC, path = pathC, artUri = artUriC)
                    val file = File(music.path)
                    if (file.exists())
                        tempList.add(music)
                } while (it.moveToNext())
            it.close()
        }
        return tempList
    }

    companion object{
        lateinit var MusicListMA: ArrayList<Music>
    }
}