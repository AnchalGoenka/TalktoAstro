package tta.destinigo.talktoastro.model

data class viewChatModel (
    val status : Boolean,
    val chat :Chat
)

data class Chat(
    val id : String ,
    val user_id :Int,
    val astro_id :Int,
    val status : String,
    val chat_id : Int ,
    val session_id : String,
    val chatprice : Int ,
    val astrologerchatprice : Int ,
    val ttachatprice : Int ,
    val timelimit : String ,
    val started_at : String,
    val ended_at : String,
    val chatduration : String,
    val created_at : String,
    val updated_at : String,
    val astro :Astro,
    val user :User,
    val message_list : ArrayList<MessageList>

)

data class Astro(
    val id :Int,
    val first_name :String,
    val last_name :String ,
    val email : String ,
    val balance : Int ,
    val phonecode :String ,
    val mobile : String ,
    val provider : String,
    val provider_id : String ,
    val is_admin : Int ,
    val url :String,
    val user_type : String,
    val verified : Int ,
    val meta_title : String,
    val meta_description :String ,
    val created_at :String ,
    val updated_at :String ,
    val rating_points : String,
    val otp_code : String,
    val call_chat_status : String,
    val is_loyalty_redem : Int ,
    val loyalty_redem_date : String,
    val earned_loyalty_point : Int


)
data class User(
    val id :Int,
    val first_name :String,
    val last_name :String ,
    val email : String ,
    val balance : Int ,
    val phonecode :String ,
    val mobile : String ,
    val provider : String,
    val provider_id : String ,
    val is_admin : Int ,
    val url :String,
    val user_type : String,
    val verified : Int ,
    val meta_title : String,
    val meta_description :String ,
    val created_at :String ,
    val updated_at :String ,
    val rating_points : String,
    val otp_code : String,
    val call_chat_status : String,
    val is_loyalty_redem : Int ,
    val loyalty_redem_date : String,
    val earned_loyalty_point : Int
)
data class MessageList(
    val id : Int ,
    val user_id : Int ,
    val chat_id : String ,
    val message :String? ,
    val type :String,
    val sent_at :String,
    val created_at : String ,
    val updated_at :String
)