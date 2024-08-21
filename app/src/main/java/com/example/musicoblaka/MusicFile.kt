package com.example.musicoblaka

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

// Clase de datos MusicFile que implementa Parcelable para permitir la transferencia entre componentes de Android
data class MusicFile(
    val title: String,
    val path: String,
    val artist: String,
    val albumArtUri: Uri?,
    val duration: Long
) : Parcelable {

    // Constructor que crea una instancia de MusicFile a partir de un Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readLong()
    )

    // Escribe los datos de la instancia en el Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(path)
        parcel.writeString(artist)
        parcel.writeParcelable(albumArtUri, flags)
        parcel.writeLong(duration)
    }

    // Describe el contenido del Parcel (generalmente devuelve 0)
    override fun describeContents(): Int {
        return 0
    }

    // Objeto companion para crear instancias de MusicFile a partir de un Parcel
    companion object CREATOR : Parcelable.Creator<MusicFile> {
        // Crea una nueva instancia de MusicFile a partir del Parcel
        override fun createFromParcel(parcel: Parcel): MusicFile {
            return MusicFile(parcel)
        }

        // Crea un array de MusicFile de tamaño especificado
        override fun newArray(size: Int): Array<MusicFile?> {
            return arrayOfNulls(size)
        }
    }
}

