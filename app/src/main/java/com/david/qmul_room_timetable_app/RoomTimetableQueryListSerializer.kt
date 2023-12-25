package com.david.qmul_room_timetable_app

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object RoomTimetableQueryListSerializer : Serializer<RoomTimetableQueryList> {

    override val defaultValue: RoomTimetableQueryList = RoomTimetableQueryList.getDefaultInstance()

//    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun readFrom(input: InputStream): RoomTimetableQueryList {
        try {
            return RoomTimetableQueryList.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

//    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: RoomTimetableQueryList, output: OutputStream) = t.writeTo(output)

}