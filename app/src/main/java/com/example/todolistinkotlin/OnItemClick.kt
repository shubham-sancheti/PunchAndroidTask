package com.example.todolistinkotlin

import android.view.View

/**
 *   Created by Sundar Pichai on 12/8/19.
 */
interface OnItemClick {

    fun onItemClick(v: View, position: Int)
}