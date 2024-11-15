package com.example.playlistmaker.ui.media.activity

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.utils.DimenConvertor

class SpacesItemDecoration(
    private val context: Context
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.right = if (parent.getChildLayoutPosition(view) % 2 == 0) {
            DimenConvertor.dpToPx(RIGHT_PADDING_FOR_ITEM_IN_DP, context)
        } else {
            0
        }
        outRect.top = if (parent.getChildLayoutPosition(view) in 0..1) {
            0
        } else {
            DimenConvertor.dpToPx(TOP_PADDING_FOR_ITEM_IN_DP, context)
        }
    }

    companion object {
        private const val RIGHT_PADDING_FOR_ITEM_IN_DP = 8f
        private const val TOP_PADDING_FOR_ITEM_IN_DP = 16f
    }

}