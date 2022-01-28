package tta.destinigo.talktoastro.util

import com.zoho.salesiqembed.ZohoSalesIQ


/**

 * Created by Vivek Singh on 2019-06-10.

 */
class ApplicationConstant {
    companion object {

        /**
         * List of URL's used in application
         */
      // const val BASE_URL = "https://ttadev.in/ttaweb/ttawebchat1/webserviceslive" //development url
       // const val BASE_URL = "https://www.ttadev.in/ttaweb/webserviceslive" //development url
      const val BASE_URL = "https://www.talktoastro.com/webserviceslive" //live url
     // const val BASE_URL = "https://www.talktoastro.com/webserviceslivenew"


        //const val audiourl = " https://www.ttadev.in/ttaweb/public/audio/astrologers/"
         const  val audiourl ="https://www.talktoastro.com/public/audio/astrologers/"// your URL here
        //const val audiourl = "http://www.all-birds.com/Sound/western%20bluebird.wav" // your URL here

        const val SEND_NOTIFICATION = "Firebasenotify.php"
        const val DEVICE_TOKEN ="${BASE_URL}/udid_new.php"


        const val ASTROLOGER_LIST = "${BASE_URL}/astrologers.php"
        const val ASTROLOGER_ONLINE = "${BASE_URL}/astrologers.php?astroStatus=online"
        const val ASTROLOGER_OFFLINE = "${BASE_URL}/astrologers.php?astroStatus=offline"
        const val ASTROLOGER_BUSY = "${BASE_URL}/astrologers.php?astroStatus=busy"
        const val ACTIVE_BANNER = "${BASE_URL}/slider.php"
        const val HOROSCOPES_LIST = "${BASE_URL}/horoscope.php"
        const val ACTIVE_ARTICLES = "${BASE_URL}/articles.php"
        const val CUSTOMER_STORIES = "${BASE_URL}/customerstory.php"
        const val REGISTRATIONS = "${BASE_URL}/register.php"
        const val LOGIN = "${BASE_URL}/login.php"
        const val TWOfactorAPI_KEY = "c74cb7b6-6776-11ea-9fa5-0200cd936042"
        const val TWOfactorTEMPLATE_NAME = "OTPTEMP"
        const val LOGINVIASMS = "https://2factor.in/API/V1/${TWOfactorAPI_KEY}/SMS/"
        const val VERIFYOTP = "${BASE_URL}/verifyotp.php"
        const val PAYPACKAGES = "${BASE_URL}/packages_new.php"
        const val COUPAN = "${BASE_URL}/coupon.php"
        const val GET_APP_VERSION = "${BASE_URL}/version.php"
        const val APP_NOTIFICATION="${BASE_URL}/appNotification.php"

      const val  RTMTOKEN="${BASE_URL}/RtmTokenBuilderSample.php"

      const val ChatBaseUrl ="https://www.talktoastro.com/api/chat/"

        //{"coupanCode":"VINS",
        // ttaOrderID:1433}

        const val notificationIcon = "${BASE_URL}/notification.php"
        const val checkNotifcation = "${BASE_URL}/notify.php"
        const val removeNotifcation = "${BASE_URL}/removenotify.php"
        const val PAYTM_ORDER = "${BASE_URL}/paytmOrder.php"

        //{
        // "paytmOrderID": 1234567,
        // "ttaOrderID" : 1434
        // }
        const val PROCESS_PAYTM_ORDER = "$BASE_URL/processPayTmOrder.php"

        const val PROCESS_RAZORPAY_ORDER = "${BASE_URL}/processRazorpayOrder.php"
        //{"ttaOrderID":1433
        // "razorpayPaymentID=
        // }

        const val GENERATE_RAZORPAY_ORDERID = "${BASE_URL}/generateOrder.php"
        const val REPORTS = "${BASE_URL}/reports.php"
        const val REPORT_PRICE = "$BASE_URL/reportPrices.php"
        const val GET_ASTRO_Chat_STATUS = "${BASE_URL}/getchatstatus.php"

        const val PROCESSREPORT = "${BASE_URL}/processReport.php"
        // {"serviceID":"1","userID":"469","astroID":"472","name":"vinay sharma","gender":"male","birthPlace":"kota",
        // "birthDate":"1986-10-21","birthTime":"11:45","question":"test question"}

        const val SUBMITCHATFORM = "$BASE_URL/chatform.php"
        const val CHANGECHATSTATUS = "$BASE_URL/changechatstatus.php"
        const val GETCHATFORM = "$BASE_URL/formcheck.php"
        const val CHATLOG =
            "$BASE_URL/chatlog.php" // {"success":1,"message":"Chat balance updated"}
        const val CHATHISTORY = "$BASE_URL/userchathistory.php" //{userID: ""}

        const val USERWALLETHISTORY = "${BASE_URL}/user.wallet.php"
        //{"userID":"469"}

        const val USERREPORTHISTORY = "${BASE_URL}/user.report.php"
        //{"userID":"469"}

        const val USERPROFILE = "${BASE_URL}/user.profile.php"
        //{"userID":"469"}

        const val EDITUSERPROFILE = "${BASE_URL}/user.profile.edit.php"
        //{"first_name":"vinay2","last_name":"sharma2","email":"vinay2110@gmail.com","phonecode":"91","mobile":"9950753898","userID":"469"}

        const val REPORTCOMMENTS = "${BASE_URL}/report.comments.php"
        //{"reportID":"27"}

        const val CALLAPI = "${BASE_URL}/callAstrologer.php"
        //       {"userID":"469","astroID":"509"}

        const val ASTRO_REVIEW = "${BASE_URL}/astrologer.review.php"

        /**
         * {"rating":"4","review":"test review","astroID":"472","userID":"505"}
         */

        const val ASTRO_PROFILE = "${BASE_URL}/astrologer.profile.php"
        const val post = "${BASE_URL}/post.php"

        /**
         * {"astroID":"330"}
         */
        const val REPORT_SUBMIT = "${BASE_URL}/report.submit.php"

        //{"reportID":"32","astroID":"472","reportComment":"this is your report"}
        const val REPORT_COMMENTS_SUBMIT = "${BASE_URL}/report.comments.submit.php"
        //{"reportID":"32","userID":"469","reportComment":"this is comment from user"}
        //
        //if logged in user is astrologer
        //
        //{"reportID":"32","userID":"472","reportComment":"this is comment from astrologer"}

        const val REPORT_COMPLETED = "${BASE_URL}/report.completed.php"

        //{"reportID":"32","userID":"469"}
        //
        //it has condition.. this must be userID only not any astrologer id

        const val REPORT_REQIUREMENT = "$BASE_URL/reportrequirement.php"

        const val FORGOT_PASSWORD = "${BASE_URL}/user.resetpasswordotp.php"
        const val RESET_NEW_PASSWORD = "${BASE_URL}/user.resetpasswordnew.php"

        //{"oldPassword":"1111","newPassword":"123456","userID":"508"}

        const val USER_CALL_HISTORY = "${BASE_URL}/user.phonecall.php"

       // const val FREE_HOROSCOPE = "https://api.vedicrishiastro.com/v1/astro_details"

        const val LAT_LONG_FREEHOROSCOPE = "https://json.astrologyapi.com/v1/geo_details"
        const val GET_TIMEZONE = "https://json.astrologyapi.com/v1/timezone_with_dst"
        const val BIRTH_DETAILS = "https://json.astrologyapi.com/v1/birth_details"
        const val FREE_HOROSCOPE = "https://json.astrologyapi.com/v1/astro_details"
        const val CHART = "https://json.astrologyapi.com/v1/horo_chart_image/d1"
        const val General_Ascendant_Report="https://json.astrologyapi.com/v1/general_ascendant_report"

        const val talkToAstroURL = "https://www.talktoastro.com/"
        const val ABOUT_US = "$BASE_URL/aboutus.php"
        const val PRIVACY_POLICY = "$BASE_URL/privacypolicy.php"
        const val TERMS_CONDITIONS = "$BASE_URL/termsconditions.php"
        const val AskFreeQuestion = "$BASE_URL/hubQuestion.php"
        const val HUBQUESTIONSUBMIT = "$BASE_URL/hubQuestionSubmit.php"
        const val  hubAnswer = "$BASE_URL/hubAnswer.php"
      const val  hubAnswerSubmit = "$BASE_URL/hubAnswerSubmit.php"

        var IS_INTERNET_AVAILABLE = true
        var IS_INTERNET_DIALOG_VISIBLE = false
        var SHOW_LOGS = true

        const val CALL_STRING = "Call"
        const val REPORT_STRING = "Report"
        const val CHAT_STRING = "Chat"
        const val TALKTOASTRO_PHONE_NUMBER = "+91-2268301019"
        const val CALLNOW = "Call Now"
        const val OFFLINE = "Offline"
        const val CHAT = "Chat"
        const val BUSY = "Busy"
        const val ONLINE = "Online"
        const val BUSYSTATUS = "busy"

        /**
         * package name INAUGURAL - common for both rupee and dollar
         */
        const val INAUGURAL = "INAUGURAL"

        /**
         * packages names - rupee
         */
        const val PEARL = "PEARL"
        const val EMERALD = "EMERALD"
        const val RUBY = "RUBY"
        const val SAPPHIRE = "SAPPHIRE"
        const val DIAMOND = "DIAMOND"

        /**
         * packages name - dollar
         */
        const val BRONZE = "BRONZE"
        const val SILVER = "SILVER"
        const val GOLD = "GOLD"
        const val PLATINUM = "PLATINUM"
        const val TITANIUM = "TITANIUM"

        /**
         * razorpay ids (TEst keys.)
         */
        const val RAZORPAY_TEST_API_KEY = "rzp_test_lomwYRitMxxx8N"
        const val RAZORPAY_SECRET_KEY = "8btHLxzabYw2DzNMat7Yo3k6"

        /**
         * razorpay live production keys
         */
        const val RAZORPAY_LIVE_API_KEY = "rzp_live_c5qb8WxI0QtmLX"

        /**
         * paytm ids and keys (Production keys)
         */

        const val PAYTM_MERCHANTID = "qgWKMK94246468416526"
        const val PAYTM_MERCHANTKEY = "KoTLzusa!QiKgLaV"

        /**
         * paytm test api
         */
        const val PAYTM_MERCHANTID_TEST = "OhSGzD61488496045330"


        /**
         * astrologyapi api user id and api secret key
         */
       /* const val ASTROLOGYAPI_USERID = "615451"
        const val ASTROLOGYAPI_API_KEY = "309736859c4c0e877d48d0714eccd4a3"*/
        const val ASTROLOGYAPI_USERID = "612997"
        const val ASTROLOGYAPI_API_KEY = "fdd183be1caa9e314a39b717a30fbb6d"

        /**
         * Zoho Support Chat  app key and access key
         */
        const val ZohoSalesIQ_Appkey = "nwoMymSBKJ1S8xxhRz7Rn7IcFNuxyWGI9EAJK7xtAC96NpinoEK7xvvWxQfN3Q4Y_in"
        const val ZohoSalesIQ_AccessKey = "t%2FwMH7va9FP0DR5iWsvNCT0IeAUDf1RYDKxcVmuvaaVEQ3i6JR6x07Dbrtpwt2LzEK6SeUBkkFtp4cSaMEi3tl3aP7B9PuVjAzauri0IK7ssebfGhJdsUYlxQpWZKAzTkXGWBF4BpuYCu0soa4HgA%2FeFq4d6yaEEPfONQ1EshoA%3D"




        /**
         * Shared preferences constant values
         */
        const val AgoraToken ="agoraToken"
        const val USERID = "userid"
        const val OTP = "otp"
        const val PHONECODE = "phonecode"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val MOBILE = "mobiile"
        const val Details = "Details"
        const val BALANCE = "balance"
        const val MINIMUMBALANCE = "100"
        const val REPORTCHECKED = "reportchecked"
        const val NAME = "name"
        const val ISCOMINGFROMREPORT = "isComingFromReport"
        const val DEVICETOKEN = "devieToken"
        const val ISDEVICETOKENENABLED = "isDeviceTokenEnabled"
        const val THANKYOU = "thankYou"
        const val RELOADHOMEFRAGMENT = "reloadHomeFragment"
        const val NOTIFYCOUNT = "notifyCount"
        const val CHECKNOTIFYASTROID = "notifyAstroID"
        const val CHATMESSAGE = "Chat Message"

        /**
         * Converting constant for rupee to dollar and dollar to rupee
         */
        const val constConverterVal = 65
        const val constPriceSize = 1.5f
        const val MINIMUM_BALANCE_RUPEE = 20
        const val MINIMUM_BALANCE_DOLLAR = 7
        const val SERVICECHARGE = "18%"

        const val  CURRENT_APP_VERSION = tta.destinigo.talktoastro.BuildConfig.VERSION_CODE

        /**
         * Payment processing string
         */
        const val PAYMENTPROCESSINGFAILED = "Payment processing failed."


        var SELECTPACKAGE=0


      // agora
      const val AppId = "11a0c7835f9a48b7bbf8cfcd6692320b"
      const val Agoratoken = "ed0c425b27c944d09e94a07889338bf8"

    }
}

interface Constants {
    companion object {
        /** Key into an Intent's extras data that points to a [Channel] object.  */
        val EXTRA_CHANNEL = "com.twilio.chat.Channel"

        /** Key into an Intent's extras data that contains Channel SID.  */
        val EXTRA_CHANNEL_SID = "C_SID"
        val CHAT_ASTRO_ID = "astroId"
        val CHAT_ASTRO_NAME = "astroName"
        val CHAT_DURATION = "chatDuration"
        val CHAT_ID = "chatID"
        val EXTRA_UNIQUE_NAME="UNIQUE_NAME"
    }
}