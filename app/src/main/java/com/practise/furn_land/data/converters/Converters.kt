package com.practise.furn_land.data.converters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?) : ByteArray {
        val outputStream = ByteArrayOutputStream()
        var myBitmap: Bitmap = Bitmap.createBitmap(50,50,Bitmap.Config.RGB_565)
        if (bitmap==null){
            myBitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        }else{
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        }
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray) : Bitmap {
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }
}