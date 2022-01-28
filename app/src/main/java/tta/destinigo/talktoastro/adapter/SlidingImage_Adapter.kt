package tta.destinigo.talktoastro.adapter

import android.net.Uri
import androidx.viewpager.widget.PagerAdapter
import android.os.Parcelable
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.R


/**

 * Created by Vivek Singh on 2019-06-19.

 */
class SlidingImage_Adapter(private val IMAGES: ArrayList<String>): PagerAdapter() {
    private val context = ApplicationUtil.getContext()
    val inflater = LayoutInflater.from(context)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return IMAGES.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false)!!

        val imageView = imageLayout!!
            .findViewById(R.id.image) as ImageView

        Glide.with(ApplicationUtil.getContext()).load(Uri.parse(IMAGES[position]))
            .placeholder(R.drawable.ic_banner_default).diskCacheStrategy(
                DiskCacheStrategy.RESOURCE
            )

            .skipMemoryCache(false).into(imageView)

        view.addView(imageLayout, 0)

        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object`)
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }
}