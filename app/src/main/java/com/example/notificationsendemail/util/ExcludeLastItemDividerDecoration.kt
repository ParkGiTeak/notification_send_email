package com.example.notificationsendemail.util

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class ExcludeLastItemDividerDecoration(private val divider: Drawable): ItemDecoration() {
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val drawableCount = parent.childCount - 1 // -1 처리를 하면서 마지막 아이템은 제와

        for (i in 0 until drawableCount) {
            val view = parent.getChildAt(i)
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight
            val top = view.bottom
            val bottom = top + divider.intrinsicHeight

            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }
}