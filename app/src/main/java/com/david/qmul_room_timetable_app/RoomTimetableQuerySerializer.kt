package com.david.qmul_room_timetable_app

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object RoomTimetableQuerySerializer : Serializer<RoomTimetableQuery> {

    override val defaultValue: RoomTimetableQuery = RoomTimetableQuery.getDefaultInstance()

//    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun readFrom(input: InputStream): RoomTimetableQuery {
        try {
            return RoomTimetableQuery.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

//    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: RoomTimetableQuery, output: OutputStream) = t.writeTo(output)

}