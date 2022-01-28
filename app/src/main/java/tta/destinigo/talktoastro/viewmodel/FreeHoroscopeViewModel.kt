package tta.destinigo.talktoastro.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonParser
import tta.destinigo.talktoastro.model.FreeHoroscopeModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.volley.ext.RequestType
import org.json.JSONArray
import org.json.JSONObject
import tta.destinigo.talktoastro.model.GeoDetailResponse
import tta.destinigo.talktoastro.model.Geoname
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.volley.RequestIdentifier

class FreeHoroscopeViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app) {
    var arrayMutableFreeHoroscope = MutableLiveData<FreeHoroscopeModel>()
    var arrayGeoDetails = MutableLiveData<ArrayList<Geoname>>()
    var arrayGeonameModel = ArrayList<Geoname>()
    var responseDidFailed = MutableLiveData<String>()
    var chart=MutableLiveData<String>()
    var birthDetails=MutableLiveData<String>()
    var geneAscendantReport=MutableLiveData<String>()
    var jsonFreeHoroscope = JSONObject()
    var date = String()
    var latitude = String()
    var longitude = String()

    fun getLatLongWithTimezone(json: JSONObject, place: String, withDate: String){
        jsonFreeHoroscope = json
        date = withDate
        /*var newJson = JSONObject()
        newJson.put("place",place)
        newJson.put("maxRows", "1")
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.GETLATLONGFREEHOROSCOPE.ordinal,
            ApplicationConstant.LAT_LONG_FREEHOROSCOPE, newJson, null, null, false)*/
    }

    fun getgeo(place: String){
        var newJson = JSONObject()
        newJson.put("place",place)
        newJson.put("maxRows", "5")
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.GETLATLONGFREEHOROSCOPE.ordinal,
            ApplicationConstant.LAT_LONG_FREEHOROSCOPE, newJson, null, null, false)
    }
    fun getTimezone(json: JSONObject){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.GETTIMEZONE.ordinal,
            ApplicationConstant.GET_TIMEZONE, json, null, null, false)
    }


    fun getBirthDetails(){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.BIRTHDETAILS.ordinal,
            ApplicationConstant.BIRTH_DETAILS, jsonFreeHoroscope, null, null, false)
    }

    fun getascendantReport(){
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.GeneralAscendantReport.ordinal,
            ApplicationConstant.General_Ascendant_Report, jsonFreeHoroscope, null, null, false)
    }
    fun getHoroscope()
    {
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.FREEHOROSCOPE.ordinal,
            ApplicationConstant.FREE_HOROSCOPE, jsonFreeHoroscope, null, null, false)
    }
    fun getChart(){
        var jsonObject =JSONObject()
        jsonObject=jsonFreeHoroscope
        jsonObject.put("planetColor","black")
        jsonObject.put("signColor","black")
        jsonObject.put("lineColor","black")
        jsonObject.put("chartType","black")
        doApiRequest(
            RequestType.POST, tta.destinigo.talktoastro.volley.RequestIdentifier.CHART.ordinal,
            ApplicationConstant.CHART, jsonObject, null, null, false)

    }


    override fun onApiResponse(identifier: Int, response: JSONObject, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.FREEHOROSCOPE.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), FreeHoroscopeModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<FreeHoroscopeModel>() {
                    override fun onParseComplete(data: FreeHoroscopeModel) {
                        arrayMutableFreeHoroscope.postValue(data)
                    }
                    override fun onParseFailure(data: FreeHoroscopeModel) {

                    }
                })
        }

        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.GETLATLONGFREEHOROSCOPE.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response.toString(), GeoDetailResponse::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<GeoDetailResponse>() {
                    override fun onParseComplete(data: GeoDetailResponse) {
                        arrayGeoDetails.postValue(data.geonameList)

                    }
                    override fun onParseFailure(data: GeoDetailResponse) {

                    }
                })
        }
       /* if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.GETLATLONGFREEHOROSCOPE.ordinal) {
            try {
                val arr = response["geonames"] as JSONArray
                val arrIndexVal = arr.get(0) as JSONObject
                latitude = arrIndexVal.get("latitude") as String
                longitude = arrIndexVal.get("longitude") as String
                var json = JSONObject()
                json.put("latitude",latitude)
                json.put("longitude",longitude)
                json.put("date",date)
                getTimezone(json)
       ,     }catch (e:Exception){

            }

        }*/
        if(identifier==RequestIdentifier.CHART.ordinal){
            /*Toast.makeText(ApplicationUtil.getContext(), "All fields are mandatory." +response.get("svg").toString() , Toast.LENGTH_LONG)
                .show()*/
            /*  val jsonParser = JsonParser()
              val jsonObject = jsonParser.parse("$response.get(\"svg\").toString()").asJsonObject
              */
            chart.postValue(response.get("svg").toString())

        }
        if(identifier==RequestIdentifier.BIRTHDETAILS.ordinal){

            birthDetails.postValue(response.toString())

        }
        if(identifier==RequestIdentifier.GeneralAscendantReport.ordinal){

            geneAscendantReport.postValue(response.toString())

        }
        if(identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.GETTIMEZONE.ordinal) {
            try {
                val timeZone = response.get("timezone")
                jsonFreeHoroscope.put("tzone", timeZone)
                getBirthDetails()
                getChart()
                getascendantReport()
                getHoroscope()


            }catch (e:Exception){

            }

        }

    }
}