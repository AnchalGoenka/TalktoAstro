package tta.destinigo.talktoastro.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import org.jetbrains.anko.textColor
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.AstrologerListBinding
import tta.destinigo.talktoastro.model.AstrologerListModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.ApplicationUtil.Companion.getContext
import tta.destinigo.talktoastro.util.SharedPreferenceUtils


/**

 * Created by Vivek Singh on 2019-06-09.

 */
class HomeAdapter(
    private var arrayList: ArrayList<AstrologerListModel>?,
    private val type: String,
    private val itemClick: (AstrologerListModel, Int) -> Unit,
    private val buttonClick: (AstrologerListModel, Int) -> Unit,
    private val notificationIconClick: (AstrologerListModel, Int) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        position: Int
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        val astrologerListBinding: AstrologerListBinding =
            DataBindingUtil.inflate(
                layoutInflator,
                R.layout.fragment_home_child_list,
                parent,
                false
            )
        return customView(astrologerListBinding, itemClick, buttonClick, notificationIconClick)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    fun filteredList(filteredArray: ArrayList<AstrologerListModel>) {
        this.arrayList = filteredArray
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        position: Int
    ) {
        try {
            val h1 = holder as tta.destinigo.talktoastro.adapter.HomeAdapter.customView
            val astrologerListModel = arrayList!![position]

            h1.bind(astrologerListModel, position)
            h1.setIsRecyclable(false)
        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onBindViewHolder(): ${e.printStackTrace()}")
        }

    }

    fun clearData() {
        val size: Int = arrayList?.size ?: 0
        arrayList?.clear()
        notifyItemRangeRemoved(0, size);
    }

    fun clear(){
        arrayList!!.clear()
         notifyDataSetChanged();
    }

    inner class customView(
        val astrologerListBinding: AstrologerListBinding,
        val itemClick: (AstrologerListModel, Int) -> Unit,
        val buttonClick: (AstrologerListModel, Int) -> Unit,
        val notificationIconClick: (AstrologerListModel, Int) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(astrologerListBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(astrologerListModel: AstrologerListModel, position: Int) {
            astrologerListBinding.astrologerList = astrologerListModel
            astrologerListBinding.executePendingBindings()

                AnimationUtils.loadAnimation(astrologerListBinding.constraintLayout.context, R.anim.text_transslate)
            Glide.with(ApplicationUtil.getContext()).load(Uri.parse(astrologerListModel.image))
                .placeholder(R.mipmap.default_profile).diskCacheStrategy(
                DiskCacheStrategy.RESOURCE
            )

                .skipMemoryCache(false).into(astrologerListBinding.imgProfile)

            astrologerListBinding.txvName.text =
                astrologerListModel.firstName + " " + astrologerListModel.lastName
            astrologerListBinding.txvExperience.text=astrologerListModel.experience
            if(astrologerListModel.verified=="1")
            {astrologerListBinding.ivVerified.visibility=View.VISIBLE}
            if (astrologerListModel.totalRatings != null)
                astrologerListBinding.textViewTotalRating.text =
                    "${astrologerListModel.totalRatings}"
            astrologerListBinding.imageView.visibility = View.GONE
            astrologerListBinding.layoutFlashSale.visibility = View.GONE
            val notifyCount =
                "0"//SharedPreferenceUtils.readString(ApplicationConstant.NOTIFYCOUNT, "0", SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext()))
            val notifyAstroID = SharedPreferenceUtils.readString(
                ApplicationConstant.CHECKNOTIFYASTROID,
                "",
                SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
            )
            if (type == ApplicationConstant.CALL_STRING) {

                if (astrologerListModel.phoneStatus == "online") {
                    astrologerListBinding.btnPhoneStatus.setBackgroundResource(R.drawable.button_rounded_corners_green)
                    astrologerListBinding.txvPhoneStatus.setTextColor(
                        ContextCompat.getColor(
                            ApplicationUtil.getContext(),
                            R.color.white
                        )
                    )
                    //astrologerListBinding.btnPhoneStatus.setTextColor(Color.parseColor("#47733E"))
                    astrologerListBinding.txvPhoneStatus.text = ApplicationConstant.CALLNOW
                    astrologerListBinding.btnPhoneStatus.setOnClickListener {
                        buttonClick(
                            astrologerListModel,
                            adapterPosition
                        )
                    }
                } else if (astrologerListModel.phoneStatus == "offline") {
                    astrologerListBinding.imageView.visibility = View.VISIBLE
                    if (notifyCount == "0" && (notifyAstroID == null || notifyAstroID.isEmpty())) {
                        astrologerListBinding.imageView.setBackgroundResource(R.drawable.ic_notification_unselected)
                    } else {
                        if (notifyAstroID == astrologerListModel.id) {
                            astrologerListBinding.imageView.setBackgroundResource(R.drawable.ic_notification_filled)
                        } else {
                            astrologerListBinding.imageView.setBackgroundResource(R.drawable.ic_notification_selected)
                        }
                    }
                    astrologerListBinding.imageView.setOnClickListener {
                        notificationIconClick(astrologerListModel, adapterPosition)
                    }
                    astrologerListBinding.btnPhoneStatus.setBackgroundResource(R.drawable.button_rounded_corners_gray)
                    astrologerListBinding.txvPhoneStatus.setTextColor(Color.DKGRAY)
                    astrologerListBinding.txvPhoneStatus.text = ApplicationConstant.OFFLINE
                } else if (astrologerListModel.phoneStatus == "busy") {
                    astrologerListBinding.imageView.visibility = View.VISIBLE
                    if (notifyCount == "0") {
                        astrologerListBinding.imageView.setBackgroundResource(R.drawable.ic_notification_unselected)
                    } else {
                        if (notifyAstroID == astrologerListModel.id) {
                            astrologerListBinding.imageView.setBackgroundResource(R.drawable.ic_notification_filled)
                        } else {
                            astrologerListBinding.imageView.setBackgroundResource(R.drawable.ic_notification_selected)
                        }
                    }
                    astrologerListBinding.imageView.setOnClickListener {
                        notificationIconClick(astrologerListModel, adapterPosition)
                    }
                    astrologerListBinding.btnPhoneStatus.setBackgroundResource(R.drawable.button_rounded_corners_red)
                    astrologerListBinding.txvPhoneStatus.setTextColor(
                        ContextCompat.getColor(
                            ApplicationUtil.getContext(),
                            R.color.white
                        )
                    )
                    astrologerListBinding.txvPhoneStatus.text = ApplicationConstant.BUSY
                }
             /*   if (astrologerListModel.isflash == "1") {
                   // astrologerListBinding.layoutFlashSale.visibility = View.VISIBLE
                    val value = "${astrologerListModel.flashdisplayprice}/Min"
                    val text = "Flash Offer Price"
                    val text2 = text + " " + value

                    val spannable = SpannableString(text2)

                    spannable.setSpan(
                        ForegroundColorSpan(Color.WHITE),
                        0,
                        text.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(
                        StyleSpan(Typeface.BOLD),
                        text.length + 1,
                        text2.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    astrologerListBinding.txvFlashSale.setText(
                        spannable,
                        TextView.BufferType.SPANNABLE
                    )
                } else if (astrologerListModel.isFreeMin == "1") {
                    astrologerListBinding.layoutFlashSale.visibility = View.VISIBLE
                    astrologerListBinding.txvFlashSale.text = "Introductory Free minutes*"
                    astrologerListBinding.txvFlashSale.textColor = Color.WHITE
                }*/
                val phoneCode = SharedPreferenceUtils.readString(
                    ApplicationConstant.PHONECODE, "",
                    SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                )
                if (phoneCode == "91") {
                    astrologerListBinding.txvPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_rupee, 0, 0, 0)
                    val s = "${astrologerListModel.price} / Min"
                    val ss1 = SpannableString(s)
                    if (astrologerListModel.price?.length == 2) {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            2,
                            0
                        ) // set size
                    } else if (astrologerListModel.price?.length == 3) {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            3,
                            0
                        ) // set size
                    } else {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            1,
                            0
                        ) // set size
                    }

                    if (astrologerListModel.isflash == "1") {
                        astrologerListBinding.viewOffer.visibility = View.INVISIBLE
                        astrologerListBinding.layoutFlashSale.visibility = View.VISIBLE
                        val value = "${astrologerListModel.flashdisplayprice}/Min"
                        val text = "${astrologerListModel.price}/Min  "
                        val text2 = text + " " + value
                        val text3 = "Flash Offer Price"

                        val spannable = SpannableString(text2)
                        val spannable1 = SpannableString(text3)

                        spannable1.setSpan(
                            ForegroundColorSpan(Color.WHITE),
                            0,
                            text3.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        astrologerListBinding.txvFlashSale.setText(
                            spannable1,
                            TextView.BufferType.SPANNABLE
                        )
                        spannable.setSpan(
                            ForegroundColorSpan(Color.LTGRAY),
                            0,
                            text.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannable.setSpan(StrikethroughSpan(),0,text.length,0)
                        spannable.setSpan(RelativeSizeSpan(1.3f), text.length + 1, text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        val context: Context = getContext()

                        ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null);
                      //  val color: Int = getColor(R.color.colorPrimary)
                        spannable.setSpan(
                            ForegroundColorSpan(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null)),
                            text.length + 1, text2.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        spannable.setSpan(StyleSpan(Typeface.BOLD), text.length + 1, text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                        astrologerListBinding.txvPrice.setText(spannable, TextView.BufferType.SPANNABLE)
                    } else if (astrologerListModel.isFreeMin == "1") {
                        astrologerListBinding.layoutFlashSale.visibility = View.VISIBLE
                        astrologerListBinding.txvFlashSale.text = "Introductory Free minutes*"
                        astrologerListBinding.txvFlashSale.textColor = Color.WHITE
                        astrologerListBinding.txvPrice.text = ss1
                    }else{
                    astrologerListBinding.txvPrice.text = ss1}
                } else {
                    astrologerListBinding.txvPrice.text = astrologerListModel.foreigndisplayprice
                    astrologerListBinding.txvPrice.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_dollar_orange,
                        0,
                        0,
                        0
                    )
                    val s = "${astrologerListModel.foreigndisplayprice} / Min"
                    val ss1 = SpannableString(s)
                    if (astrologerListModel.foreigndisplayprice?.length == 2) {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            2,
                            0
                        ) // set size
                    } else if (astrologerListModel.foreigndisplayprice?.length == 3) {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            3,
                            0
                        ) // set size
                    } else {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            1,
                            0
                        ) // set size
                    }
                    astrologerListBinding.txvPrice.text = ss1
                }

            }
            else if (type == ApplicationConstant.CHAT_STRING) {
                if (astrologerListModel.chatStatus == "online") {
                    astrologerListBinding.btnPhoneStatus.setBackgroundResource(R.drawable.button_rounded_corners_green)
                    astrologerListBinding.txvPhoneStatus.setTextColor(
                        ContextCompat.getColor(
                            ApplicationUtil.getContext(),
                            R.color.white
                        )
                    )
                    astrologerListBinding.txvPhoneStatus.text = "Chat"
                    astrologerListBinding.btnPhoneStatus.setOnClickListener {
                        buttonClick(
                            astrologerListModel,
                            adapterPosition
                        )
                    }
                }
                else if (astrologerListModel.chatStatus == "busy") {
                    astrologerListBinding.btnPhoneStatus.setBackgroundResource(R.drawable.button_rounded_corners_red)
                    astrologerListBinding.txvPhoneStatus.setTextColor(
                        ContextCompat.getColor(
                            ApplicationUtil.getContext(),
                            R.color.white
                        )
                    )
                    astrologerListBinding.txvPhoneStatus.text = ApplicationConstant.BUSY
                } else {
                    astrologerListBinding.btnPhoneStatus.setBackgroundResource(R.drawable.button_rounded_corners_gray)
                    astrologerListBinding.txvPhoneStatus.setTextColor(Color.DKGRAY)
                    astrologerListBinding.txvPhoneStatus.text = ApplicationConstant.OFFLINE

                    /*astrologerListBinding.btnPhoneStatus.setBackgroundResource(R.drawable.button_rounded_corners_green)
                    astrologerListBinding.txvPhoneStatus.setTextColor(
                        ContextCompat.getColor(
                            ApplicationUtil.getContext(),
                            R.color.white
                        )
                    )
                    astrologerListBinding.txvPhoneStatus.text = "Chat"
                    astrologerListBinding.btnPhoneStatus.setOnClickListener {
                        buttonClick(
                            astrologerListModel,
                            adapterPosition
                        )
                    }*/
                }

                if (SharedPreferenceUtils.readString(
                        ApplicationConstant.PHONECODE, "",
                        SharedPreferenceUtils.getSharedPref(ApplicationUtil.getContext())
                    ) == "91"
                ) {
                    astrologerListBinding.txvPrice.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_rupee,
                        0,
                        0,
                        0
                    )
                    val s = "${astrologerListModel.chatPrice} / Min"
                    val ss1 = SpannableString(s)
                    if (astrologerListModel.chatPrice?.length == 2) {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            2,
                            0
                        ) // set size
                    } else if (astrologerListModel.chatPrice?.length == 3) {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            3,
                            0
                        ) // set size
                    } else {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            1,
                            0
                        ) // set sizea
                    }
                    astrologerListBinding.txvPrice.text = ss1
                } else {
                    astrologerListBinding.txvPrice.text = astrologerListModel.foreigndisplayprice
                    astrologerListBinding.txvPrice.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_dollar_orange,
                        0,
                        0,
                        0
                    )
                    val s = "${astrologerListModel.foreigndisplayprice} / Min"
                    val ss1 = SpannableString(s)
                    if (astrologerListModel.foreigndisplayprice?.length == 2) {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            2,
                            0
                        ) // set size
                    } else if (astrologerListModel.foreigndisplayprice?.length == 3) {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            3,
                            0
                        ) // set size
                    } else {
                        ss1.setSpan(
                            RelativeSizeSpan(ApplicationConstant.constPriceSize),
                            0,
                            1,
                            0
                        ) // set size
                    }
                    astrologerListBinding.txvPrice.text = ss1
                }
            } else {
                astrologerListBinding.btnPhoneStatus.setBackgroundResource(R.drawable.button_rounded_corners_green)
                astrologerListBinding.txvPhoneStatus.setTextColor(
                    ContextCompat.getColor(
                        ApplicationUtil.getContext(),
                        R.color.white
                    )
                )
                astrologerListBinding.txvPhoneStatus.text = "Order Report"
                astrologerListBinding.btnPhoneStatus.setOnClickListener {
                    buttonClick(
                        astrologerListModel,
                        adapterPosition
                    )
                }
                astrologerListBinding.txvPrice.visibility = View.INVISIBLE
//                val layout_Param = astrologerListBinding.btnPhoneStatus.layoutParams
//                layout_Param.width = 250
//                layout_Param.height = 50
//                astrologerListBinding.btnPhoneStatus.layoutParams = layout_Param
            }

            if (astrologerListModel.justComing == "old"||astrologerListModel.justComing == null)
            {
            astrologerListBinding.tvStarRating.text = astrologerListModel.ratingAvg + " "}
           else  if (astrologerListModel.justComing == "new")
            {
                astrologerListBinding.tvStarRating.text =  "New "
              //  astrologerListBinding.tvStarRating.setTextColor(Color.GRAY)
               // setTextColor(R.color.Red)
                astrologerListBinding.imgStar5.visibility=View.GONE
            }



           /* when (astrologerListModel.ratingAvg) {
                "1" -> setImagesAsPerRating(
                    astrologerListBinding,
                    R.drawable.ic_expertise,
                    R.drawable.ic_star_regular,
                    R.drawable.ic_star_regular,
                    R.drawable.ic_star_regular,
                    R.drawable.ic_star_regular
                )
                "1.5" -> setImagesAsPerRating(
                    astrologerListBinding,
                    R.drawable.ic_expertise,
                    R.drawable.ic_star_half_alt_solid,
                    R.drawable.ic_star_regular,
                    R.drawable.ic_star_regular,
                    R.drawable.ic_star_regular
                )
                "2" -> setImagesAsPerRating(
                    astrologerListBinding,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_star_regular,
                    R.drawable.ic_star_regular,
                    R.drawable.ic_star_regular
                )
                "2.5" -> setImagesAsPerRating(
                    astrologerListBinding,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_star_half_alt_solid,
                    R.drawable.ic_star_regular,
                    R.drawable.ic_star_regular
                )
                "3" -> setImagesAsPerRating(
                    astrologerListBinding,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_star_regular,
                    R.drawable.ic_star_regular
                )
                "3.5" -> setImagesAsPerRating(
                    astrologerListBinding,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_star_half_alt_solid,
                    R.drawable.ic_star_regular
                )
                "4" -> setImagesAsPerRating(
                    astrologerListBinding,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_star_regular
                )
                "4.5" -> setImagesAsPerRating(
                    astrologerListBinding,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_star_half_alt_solid
                )
                "5" -> setImagesAsPerRating(
                    astrologerListBinding,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise,
                    R.drawable.ic_expertise
                )
            }*/

            itemView.setOnClickListener {

                itemClick(astrologerListModel, adapterPosition) }


        }
    }

   /* fun setImagesAsPerRating(
        astrologerListBinding: AstrologerListBinding,
        img1: Int,
        img2: Int,
        img3: Int,
        img4: Int,
        img5: Int
    ) {
        astrologerListBinding.imgStar1.setImageResource(img1)
        astrologerListBinding.imgStar2.setImageResource(img2)
        astrologerListBinding.imgStar3.setImageResource(img3)
        astrologerListBinding.imgStar4.setImageResource(img4)
        astrologerListBinding.imgStar5.setImageResource(img5)
    }*/
}