package tta.destinigo.talktoastro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import tta.destinigo.talktoastro.R

class DemoInfiniteAdapter (
    context: Context,
    itemList: ArrayList<String>,
    isInfinite: Boolean
) : LoopingPagerAdapter<String>(context, itemList, isInfinite) {

    //This method will be triggered if the item View has not been inflated before.
    override fun inflateView(
        viewType: Int,
        container: ViewGroup,
        listPosition: Int
    ): View {
        return LayoutInflater.from(context).inflate(R.layout.slidingimages_layout, container, false)
    }

    //Bind your data with your item View here.
    //Below is just an example in the demo app.
    //You can assume convertView will not be null here.
    //You may also consider using a ViewHolder pattern.
    override fun bindView(
        convertView: View,
        listPosition: Int,
        viewType: Int
    ) {
        /*convertView.findViewById<View>(R.id.image).setBackgroundColor(context.resources.getColor(getBackgroundColor(listPosition)))
        val description = convertView.findViewById<TextView>(R.id.description)
        description.text = itemList?.get(listPosition).toString()*/
        val imageView = convertView.findViewById<ImageView>(R.id.image)

        val requestOptions: RequestOptions =
            RequestOptions().placeholder(R.drawable.ic_banner_default).error(
                R.drawable.ic_banner_default
            ).fallback(R.drawable.ic_banner_default).diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
        Glide.with(imageView.context).load(itemList!!.get(listPosition)).apply(requestOptions)
            .into(imageView)
    }
}