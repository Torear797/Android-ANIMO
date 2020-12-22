package com.animo.ru.utilities

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(val spaceRL:Int,val spaceTB:Int ): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        //добавить переданное кол-во пикселей отступа снизу
        outRect.top = spaceTB
        //outRect.bottom = spaceTB;
        outRect.left = spaceRL
        outRect.right = spaceRL
    }
}