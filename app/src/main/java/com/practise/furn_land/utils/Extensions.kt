package com.practise.furn_land.utils

import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavDirections

@BindingAdapter("strikeThrough")
fun TextView.isStrikeThrough(value: Boolean){
    if(value) this.paintFlags = (this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
}

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}
