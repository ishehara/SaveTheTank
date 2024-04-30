package com.example.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Explosion(context: Context) {
    private val explosion = arrayOfNulls<Bitmap>(4)
    var explosionFrame = 0
    var explosionX = 0
    var explosionY = 0

    init {
        explosion[0] = BitmapFactory.decodeResource(context.resources, R.drawable.explode_0)
        explosion[1] = BitmapFactory.decodeResource(context.resources, R.drawable.explode_1)
        explosion[2] = BitmapFactory.decodeResource(context.resources, R.drawable.explode_2)
        explosion[3] = BitmapFactory.decodeResource(context.resources, R.drawable.explode_3)
    }

    fun getExplosion(explosionFrame: Int): Bitmap? {
        return explosion[explosionFrame]
    }
}
