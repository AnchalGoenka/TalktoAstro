package tta.destinigo.talktoastro.util

import android.graphics.Rect
import android.view.View


/**

 * Created by Vivek Singh on 2019-06-11.

 */
class MarginItemDecorator(private val spaceHeight: Int): androidx.recyclerview.widget.RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: androidx.recyclerview.widget.RecyclerView, state: androidx.recyclerview.widget.RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        with(outRect){
            if (parent.getChildAdapterPosition(view) == 0) {
                top = spaceHeight
            }
            left =  spaceHeight
            right = spaceHeight
            bottom = spaceHeight
        }
    }

}