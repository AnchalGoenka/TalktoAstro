package tta.destinigo.talktoastro.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.model.PackageList
import java.util.*

class packageAdapter(val payPackageslist: ArrayList<PackageList>?, val clickItem:(PackageList, Int)->Unit):
    RecyclerView.Adapter<packageAdapter.ViewHolder>() {
    var selectedItemPosition = mutableListOf<Int>(-1)

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val mcashback=itemView.findViewById(R.id.txv_cashback) as TextView
        val mname=itemView.findViewById(R.id.tv_name) as TextView
        val mamount=itemView.findViewById(R.id.tv_amount) as TextView
        val  mimgcashback=itemView.findViewById(R.id.iv_cashback) as ImageView
        val mcode=itemView.findViewById(R.id.tv_code) as TextView
        val  mlinearLayoutClickItem = itemView.findViewById(R.id.llpackage) as LinearLayout
        val  mrelativeLayoutClickItem = itemView.findViewById(R.id.rl_Package) as RelativeLayout

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): packageAdapter.ViewHolder {
        val v= LayoutInflater.from(parent.context).inflate(R.layout.packages_list,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return payPackageslist!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: packageAdapter.ViewHolder, position: Int) {
        if(!payPackageslist?.get(position)?.cashback.isNullOrEmpty()){
            holder.mcashback.text=payPackageslist?.get(position)?.cashback + "% Extra"
            holder.mimgcashback.visibility=View.VISIBLE
            holder.mcashback.visibility=View.VISIBLE
            holder.mcode.text=payPackageslist?.get(position)?.code
        }
        holder.mamount.text=payPackageslist?.get(position)?.amount
        holder.mname.text=payPackageslist?.get(position)?.name


        holder.mlinearLayoutClickItem.setOnClickListener {
            selectedItemPosition.add(position)
            selectedItemPosition.forEach { selectedItem ->  // this forEach is required to refresh all the list
                notifyItemChanged(selectedItem)
            }
            clickItem(payPackageslist!!.get(position),position)

        }

        selectedItemPosition.forEach {
            if (it == position) {
                holder.mname.setBackgroundResource(R.color.color_main_page_button_pressed)
                holder.mrelativeLayoutClickItem.setBackgroundResource(R.color.color_main_page_button_pressed)
            }
            else{
                holder.mname.setBackgroundResource(R.color.colorPrimaryLight)
                holder.mrelativeLayoutClickItem.setBackgroundResource(R.color.color_main_page_button)
            }
        }

    }
}