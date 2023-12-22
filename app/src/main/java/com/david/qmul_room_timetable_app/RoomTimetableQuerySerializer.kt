package com.david.qmul_room_timetable_app

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object RoomTimetableQuerySerializer : Serializer<RoomTimetableQuerySerializer> {

    override val defaultValue: RoomTimetableQuery = RoomTimetableQuery.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): RoomTimetableQuery {
        TODO("Not yet implemented")
    }

    override suspend fun writeTo(t: RoomTimetableQuerySerializer, output: OutputStream) {
        TODO("Not yet implemented")
    }

}