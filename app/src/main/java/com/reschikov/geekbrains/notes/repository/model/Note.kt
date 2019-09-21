package com.reschikov.geekbrains.notes.repository.model

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Note(var id: Int,
                val title: String?,
                val note: String?,
                var color: ColorNote,
                val lastModification: Long = Date().time) : Parcelable {

    init {
        if (id == 0) id = ++countId
    }

    companion object CREATOR : Parcelable.Creator<Note> {

        var countId = 0

        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Note
        return id == other.id
    }

    override fun hashCode() = id.hashCode()

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            ColorNote.valueOf(parcel.readString() ?: ColorNote.WHITE.toString()),
            parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(note)
        parcel.writeString(color.toString())
        parcel.writeLong(lastModification)
    }

    override fun describeContents(): Int {
        return 0
    }
}

enum class ColorNote() : Parcelable{
    WHITE,
    YELLOW,
    GREEN,
    BLUE,
    RED,
    VIOLET,
    PINK;

    constructor(parcel: Parcel) : this() {
       valueOf(parcel.readString() ?: WHITE.toString())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        when(this){
            WHITE -> parcel.writeString(WHITE.toString())
            YELLOW -> parcel.writeString(YELLOW.toString())
            GREEN -> parcel.writeString(GREEN.toString())
            BLUE -> parcel.writeString(BLUE.toString())
            RED -> parcel.writeString(RED.toString())
            VIOLET -> parcel.writeString(VIOLET.toString())
            PINK -> parcel.writeString(PINK.toString())
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ColorNote> {
        override fun createFromParcel(parcel: Parcel): ColorNote {
            return valueOf(parcel.readString() ?: WHITE.toString())
        }

        override fun newArray(size: Int): Array<ColorNote?> {
            return arrayOfNulls(size)
        }
    }
}