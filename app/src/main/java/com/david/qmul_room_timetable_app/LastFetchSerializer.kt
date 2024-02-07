package com.david.qmul_room_timetable_app

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object LastFetchSerializer : Serializer<LastFetch> {

    override val defaultValue: LastFetch = LastFetch.getDefaultInstance()

    //    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun readFrom(input: InputStream): LastFetch {
        try {
            return LastFetch.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    //    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun writeTo(t: LastFetch, output: OutputStream) = t.writeTo(output)

}