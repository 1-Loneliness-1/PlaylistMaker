package com.example.playlistmaker.ui.media.view_model

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.media.PlaylistsInteractor
import com.example.playlistmaker.domain.media.model.EditPlaylistScreenState
import com.example.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private var uriForSaveInDatabase: Uri? = null
    private var currentPlaylistCoverPath: String? = null
    private var idOfCurrentPlaylist: Long = 0

    private val editPlaylistScreenStatusLiveData = MutableLiveData<EditPlaylistScreenState>()

    fun getEditPlaylistScreenStatusLiveData(): LiveData<EditPlaylistScreenState> {
        return editPlaylistScreenStatusLiveData
    }

    fun saveUriOfPlaylistCover(uri: Uri) {
        uriForSaveInDatabase = uri
    }

    fun savePlaylistInDatabase(playlistTitle: String, playlistDescription: String?) {
        viewModelScope.launch {
            var countOfPlaylists = 0L
            playlistsInteractor.getAllPlaylists().collect { listOfPlaylists ->
                countOfPlaylists = listOfPlaylists.size.toLong()
            }

            val pathWithPlaylistCover = savePlaylistCoverInExternalMemory(countOfPlaylists + 1)

            val playlistForSave = Playlist(
                PLAYLIST_ID_PLACEHOLDER,
                playlistTitle,
                playlistDescription,
                pathWithPlaylistCover,
                null,
                INIT_NUMBER_OF_TRACKS_IN_PLAYLIST
            )

            playlistsInteractor.insertNewPlaylist(playlistForSave)
        }
    }

    fun getInfoAboutPlaylistById(selectedPlaylistId: Long) {
        idOfCurrentPlaylist = selectedPlaylistId

        viewModelScope.launch {
            playlistsInteractor
                .getPlaylistInfoById(selectedPlaylistId)
                .collect { playlist ->
                    currentPlaylistCoverPath = playlist.playlistCoverPath
                    editPlaylistScreenStatusLiveData.postValue(EditPlaylistScreenState(playlist))
                }
        }
    }

    fun updatePlaylist(updatedPlaylistTitle: String, updatedPlaylistDescription: String?) {
        viewModelScope.launch {
            val pathWithPlaylistCover = savePlaylistCoverInExternalMemory(idOfCurrentPlaylist)

            playlistsInteractor.updatePlaylist(
                idOfCurrentPlaylist,
                updatedPlaylistTitle,
                updatedPlaylistDescription,
                pathWithPlaylistCover
            )
        }
    }

    private fun savePlaylistCoverInExternalMemory(playlistId: Long): String? {
        val filePath = File(
            application.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            application.getString(R.string.name_of_folder_for_playlists_covers)
        )

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        if (uriForSaveInDatabase != null) {
            val file = File(filePath, "${playlistId}_cover.jpg")
            val inputStream =
                application.contentResolver.openInputStream(uriForSaveInDatabase!!)
            val outputStream = FileOutputStream(file)
            BitmapFactory
                .decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

            return file.path
        } else if (currentPlaylistCoverPath != null) {
            val actualImage = File(currentPlaylistCoverPath!!)

            return actualImage.path
        } else {
            return null
        }
    }

    companion object {
        private const val PLAYLIST_ID_PLACEHOLDER = 0L
        private const val INIT_NUMBER_OF_TRACKS_IN_PLAYLIST = 0
    }

}