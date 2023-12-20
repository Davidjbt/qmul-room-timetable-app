import com.david.qmul_room_timetable_app.model.Campus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.InputStreamReader


fun parseJsonData(jsonFileName: String): List<Campus> {
    val inputStream = File(jsonFileName).inputStream()
    val reader = InputStreamReader(inputStream)

    val gson = Gson()
    val campusType = object : TypeToken<List<Campus>>() {}.type

    return gson.fromJson(reader, campusType)
}

fun main() {
    try {
        val campusList: List<Campus> = parseJsonData("app/src/main/assets/qmul_rooms.json")

        // Now you can use the parsed data as needed
        var a =  arrayOf(campusList.map { campus -> campus.campus })
        println(a[0])
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
