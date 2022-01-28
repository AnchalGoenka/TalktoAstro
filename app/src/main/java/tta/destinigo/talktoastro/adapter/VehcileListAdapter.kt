package com.mooveall.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.slidingimages_layout.view.*
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.SlidingimagesLayoutBinding

/**
 * Created by Arashjit Singh on 10/30/18.
 */
class VehcileListAdapter() : RecyclerView.Adapter<VehcileListAdapter.ViewHolder>() {

    var mbannerList: List<String> = emptyList()

    fun setVehiclesList(comments: List<String>) {
        this.mbannerList = comments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflator: LayoutInflater = LayoutInflater.from(p0!!.context)
        val binding: SlidingimagesLayoutBinding = SlidingimagesLayoutBinding.inflate(layoutInflator, p0, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder != null) {
            holder.bindItem(position)
            val data = mbannerList.get(position)
            //set image
            /*Utility.setImageViaGlide(R.drawable.product_placeholder,
                    "" + data.vehicleImage,
                    holder.itemView.vehicle_image_view,
                    holder.itemView.context)*/

            val requestOptions: RequestOptions = RequestOptions().placeholder(R.drawable.ic_banner_default).error(R.drawable.ic_banner_default).fallback(R.drawable.ic_banner_default).diskCacheStrategy(
                DiskCacheStrategy.ALL)
            Glide.with(holder.itemView.context).load(data).apply(requestOptions).into(holder.itemView.image)
        }
    }

    override fun getItemCount(): Int {
        return mbannerList!!.size
    }


    class ViewHolder constructor(binding: SlidingimagesLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItem(position: Int) {
        }
    }

    interface Callback {
        abstract fun onVehicleSelected(vehicleId: Int, vehicleName: String)
    }

}