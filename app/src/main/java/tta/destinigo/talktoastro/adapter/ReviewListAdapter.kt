package tta.destinigo.talktoastro.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.ReviewListBinding
import tta.destinigo.talktoastro.model.Review


/**

 * Created by Vivek Singh on 2019-08-17.

 */
class ReviewListAdapter(private var arrayList: ArrayList<Review>?,private var iscoming:String): androidx.recyclerview.widget.RecyclerView.Adapter<tta.destinigo.talktoastro.adapter.ReviewListAdapter.customView>() {
    var displaySize =25
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): tta.destinigo.talktoastro.adapter.ReviewListAdapter.customView {
        val layoutInflator = LayoutInflater.from(parent.context)
        val reviewListBinding: ReviewListBinding =
            DataBindingUtil.inflate(layoutInflator, R.layout.fragment_child_reviews_list, parent, false)
        return customView(reviewListBinding)
    }

    override fun getItemCount(): Int {
        if(iscoming=="review"){
            if(arrayList!!.size>=displaySize){
                return displaySize
            } else {
                return arrayList!!.size}
            }
        return arrayList!!.size


    }

    fun setDisplayCount(numberOfEntries: Int) {
        displaySize = displaySize+numberOfEntries
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: tta.destinigo.talktoastro.adapter.ReviewListAdapter.customView, position: Int) {
        try {
            val reviewList = arrayList!![position]
            holder.bind(reviewList)
        } catch (e: Exception) {
            tta.destinigo.talktoastro.util.LogUtils.d("onBindViewHolder(): ${e.printStackTrace()}")
        }
    }

    inner class customView(val reviewListBinding: ReviewListBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(reviewListBinding.root) {
        fun bind(reviewListModel: Review) {
            reviewListBinding.reviewList = reviewListModel
            reviewListBinding.executePendingBindings()
            tta.destinigo.talktoastro.util.LogUtils.d("reportListModel: $reviewListBinding")

            when(reviewListModel.rating){
                "1" -> setImagesAsPerRating(reviewListBinding, R.drawable.ic_expertise,R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
                "1.5" -> setImagesAsPerRating(reviewListBinding, R.drawable.ic_expertise,R.drawable.ic_star_half_alt_solid, R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
                "2" -> setImagesAsPerRating(reviewListBinding, R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_star_regular, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
                "2.5" -> setImagesAsPerRating(reviewListBinding, R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_star_half_alt_solid, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
                "3" -> setImagesAsPerRating(reviewListBinding, R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_star_regular, R.drawable.ic_star_regular)
                "3.5" -> setImagesAsPerRating(reviewListBinding, R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_star_half_alt_solid, R.drawable.ic_star_regular)
                "4" -> setImagesAsPerRating(reviewListBinding, R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_star_regular)
                "4.5" -> setImagesAsPerRating(reviewListBinding, R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_star_half_alt_solid)
                "5" -> setImagesAsPerRating(reviewListBinding, R.drawable.ic_expertise,R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise, R.drawable.ic_expertise)
            }

        }

        fun setImagesAsPerRating(reviewListBinding: ReviewListBinding, img1: Int, img2: Int, img3: Int, img4: Int, img5: Int){
            reviewListBinding.imgStar1.setImageResource(img1)
            reviewListBinding.imgStar2.setImageResource(img2)
            reviewListBinding.imgStar3.setImageResource(img3)
            reviewListBinding.imgStar4.setImageResource(img4)
            reviewListBinding.imgStar5.setImageResource(img5)
        }

    }
}