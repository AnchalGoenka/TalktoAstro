package com.vencha.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.databinding.SlidingimagesLayoutBinding

/**
 * Created by Arashjit Singh on 28/2/18.
 */
class ImageViewFragment : Fragment() {

    lateinit var binding: SlidingimagesLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.slidingimages_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var imgUrl: String = ""
        if (arguments != null) {
            imgUrl = arguments!!.getString("image").toString()
        } else {
            activity?.finish()
        }
        setImageUsingGlide(imgUrl.toString())
    }

    private fun setImageUsingGlide(imgUrl: String) {
        //set image
        val requestOptions: RequestOptions =
            RequestOptions().placeholder(R.drawable.ic_banner_default).error(
                R.drawable.ic_banner_default
            ).fallback(R.drawable.ic_banner_default).diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
        Glide.with(binding.image.context).load(imgUrl).apply(requestOptions).into(binding.image)
    }

}