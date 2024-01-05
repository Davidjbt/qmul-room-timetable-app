package com.david.qmul_room_timetable_app.service

import com.david.qmul_room_timetable_app.AddRoomTimetable
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.TextPage
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HtmlSelect
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput
import kotlinx.coroutines.Runnable
import java.time.LocalDate
import java.util.Locale

private var stylesSheetsFetched = false

class FetchRoomTimetableTask(private val roomTimetableQuery: AddRoomTimetable.RoomTimetableQuery) : Runnable {

    lateinit var roomTimetableHtml: String
    var roomTimetableCss: HashMap<String, String>? = null

    override fun run() {
        val webClient = WebClient(BrowserVersion.CHROME)

        webClient.options.isCssEnabled = false
        webClient.options.isJavaScriptEnabled = true

        val url = "https://timetables.qmul.ac.uk/default.aspx"
        var page: HtmlPage = webClient.getPage(url)

        val locationsBtn: HtmlAnchor = page.getHtmlElementById("LinkBtn_locations")
        page = locationsBtn.click()

        val campusDropdown: HtmlSelect = page.getElementByName("dlFilter2")

        campusDropdown.getOptionByText(roomTimetableQuery.campus).setSelected(true)
        webClient.waitForBackgroundJavaScript(3000)
        page = webClient.currentWindow.enclosedPage as HtmlPage

        val buildingDropdown: HtmlSelect = page.getElementByName("dlFilter")
        buildingDropdown.getOptionByText(roomTimetableQuery.building).setSelected(true)
        webClient.waitForBackgroundJavaScript(3000)
        page = webClient.currentWindow.enclosedPage as HtmlPage


        val roomsDropdown: HtmlSelect = page.getElementByName("dlObject")
        for (room in roomTimetableQuery.rooms) {
            roomsDropdown.getOptionByText(room).setSelected(true)
        }

        val weeksDropdown: HtmlSelect = page.getElementByName("lbWeeks")
        weeksDropdown.getOptionByText("All Weeks").setSelected(false)
        weeksDropdown.getOptionByText("This Week").setSelected(true)

        val day = LocalDate.now().dayOfWeek.toString()
        val formattedDay = day[0].toString() + day.substring(1).lowercase(Locale.getDefault())
        val daysDropdown: HtmlSelect = page.getElementByName("lbDays")

        daysDropdown.getOptionByText("All Weekdays").setSelected(false)
        daysDropdown.getOptionByText(formattedDay).setSelected(true)

        val viewTimetableBtn: HtmlSubmitInput = page.getHtmlElementById("bGetTimetable")
        viewTimetableBtn.click<HtmlPage>()

        webClient.waitForBackgroundJavaScript(3000)

        page = webClient.currentWindow.enclosedPage as HtmlPage

        roomTimetableHtml = page.webResponse.contentAsString

        synchronized(this) {
            if (!stylesSheetsFetched) {
                val stylesSheets = arrayOf("swscustom.css", "activitytype.css")
//                for (stylesSheet in stylesSheets) {
//                    roomTimetableCss.add(webClient.getPage<TextPage>("https://timetables.qmul.ac.uk/${stylesSheet}").webResponse.contentAsString)
//                }

                roomTimetableCss = HashMap<String, String>()

                stylesSheets.forEach {
                    val css = webClient.getPage<TextPage>("https://timetables.qmul.ac.uk/${it}")

                    roomTimetableCss?.put(it, css.webResponse.contentAsString)
                }

                stylesSheetsFetched = true
            }
        }
    }

}
