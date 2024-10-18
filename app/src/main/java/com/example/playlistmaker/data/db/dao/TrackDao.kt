package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Insert(TrackEntity::class, OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(favoriteTrack: TrackEntity)

    @Delete(TrackEntity::class)
    suspend fun deleteTrackFromFavorites(trackForDelete: TrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY rowid DESC")
    suspend fun getFavoriteTracks(): List<TrackEntity>

    @Query("SELECT * FROM favorite_tracks WHERE track_id = :selectedTrackId")
    suspend fun getTrackById(selectedTrackId: Long): TrackEntity?

}