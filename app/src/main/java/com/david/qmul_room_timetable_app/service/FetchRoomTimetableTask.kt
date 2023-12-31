package com.david.qmul_room_timetable_app.service

import com.david.qmul_room_timetable_app.AddRoomTimetable
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import kotlinx.coroutines.Runnable
import java.net.URL

class FetchRoomTimetableTask(private val roomTimetableQuery: AddRoomTimetable.RoomTimetableQuery) : Runnable {

    lateinit var roomTimetable: String

    override fun run() {
        val options = UiAutomator2Options()
            .setAppPackage("com.android.chrome")
            .setAppActivity("com.google.android.apps.chrome.Main")

        val driver: AppiumDriver = AndroidDriver(URL("https://timetables.qmul.ac.uk/default.aspx"), options)

//        driver["https://timetables.qmul.ac.uk/default.aspx"]
//        val wait = WebDriverWait(driver, Duration.ofSeconds(10))
//
//        val locationsBtn = driver.findElement(By.id("LinkBtn_locations"))
//        locationsBtn.click()
//
//        var dropdown = Select(driver.findElement(By.id("dlFilter2")))
//        dropdown.selectByVisibleText("Mile End Campus")
//
//        dropdown = Select(wait.until(EC.visibilityOfElementLocated(By.id("dlFilter"))))
//        dropdown.selectByVisibleText(roomTimetableQuery.building)
//
//        dropdown = Select(wait.until(EC.visibilityOfElementLocated(By.id("dlObject"))))
//
//        for (room in roomTimetableQuery.rooms) dropdown.selectByVisibleText(room)
//
//        dropdown = Select(wait.until(EC.visibilityOfElementLocated(By.id("lbWeeks"))))
//        dropdown.deselectByVisibleText("All Weeks")
//        dropdown.selectByVisibleText("This Week")
//
//        var day = LocalDate.now().dayOfWeek.toString()
//        day = day[0].toString() + day.substring(1).lowercase(Locale.getDefault())
//        dropdown = Select(wait.until(EC.visibilityOfElementLocated(By.id("lbDays"))))
//        dropdown.selectByVisibleText(day)
//
//        val viewTimetableBtn = driver.findElement(By.id("bGetTimetable"))
//        viewTimetableBtn.click()
//
//        roomTimetable = driver.pageSource
//            .replace("\n".toRegex(), "")
//            .replace("\"".toRegex(), "'")
//
//        driver.close()
    }

}
