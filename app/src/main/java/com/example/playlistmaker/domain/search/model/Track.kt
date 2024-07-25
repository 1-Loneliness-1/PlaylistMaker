package com.example.playlistmaker.domain.search.model

data class Track(
    val trackId: Long, //Универсальный номер трека
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String, // Продолжительность трека
    val collectionName: String?, //Альбом
    val releaseDate: String, //Год выпуска трека
    val primaryGenreName: String, //Жанр
    val country: String, //Страна выпуска трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val previewUrl: String // Ссылка на 30 секундный отрывок песни
)