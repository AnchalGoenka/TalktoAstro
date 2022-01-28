package tta.destinigo.talktoastro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.ArticleList
import tta.destinigo.talktoastro.model.Geoname

class GeoDetailAdapter(val geoDetailList: ArrayList<Geoname>,val clickItem:(Geoname, Int) -> Unit
):RecyclerView.Adapter<GeoDetailAdapter.ViewHolder>() {
    class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView) {
        val mplaceNmae=itemView.findViewById(R.id.tv_placename) as TextView


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v=
            LayoutInflater.from(parent.context).inflate(R.layout.geo_list,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return geoDetailList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val geonamelist=geoDetailList.get(position)
        holder.mplaceNmae.text=geonamelist.placeName
        holder.mplaceNmae.setOnClickListener {
            clickItem(geonamelist,position)
        }
    }

}