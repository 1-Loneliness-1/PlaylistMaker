package com.example.playlistmaker.domain.entities

data class Track(
    val trackId: Long, //Универсальный номер трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val collectionName: String?, //Альбом
    val releaseDate: String, //Год выпуска трека
    val primaryGenreName: String, //Жанр
    val country: String, //Страна выпуска трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val previewUrl: String // Ссылка на 30 секундный отрывок песни
)

class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
)