package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.FavoriteTrackEntity

@Dao
interface TrackDao {

    @Insert(FavoriteTrackEntity::class, OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteTrack(favoriteTrack: FavoriteTrackEntity)

    @Delete(FavoriteTrackEntity::class)
    suspend fun deleteTrackFromFavorites(trackForDelete: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks ORDER BY rowid DESC")
    suspend fun getFavoriteTracks(): List<FavoriteTrackEntity>

    @Query("SELECT * FROM favorite_tracks WHERE track_id = :selectedTrackId")
    suspend fun getTrackById(selectedTrackId: Long): FavoriteTrackEntity?

}