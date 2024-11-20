package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.dao.TrackInPlaylistDao
import com.example.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity

@Database(
    version = 9,
    entities = [
        FavoriteTrackEntity::class,
        PlaylistEntity::class,
        TrackEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

    abstract fun playlistDao(): PlaylistDao

    abstract fun trackInPlaylistDao(): TrackInPlaylistDao

}