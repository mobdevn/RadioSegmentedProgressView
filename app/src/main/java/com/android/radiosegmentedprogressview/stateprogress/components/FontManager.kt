package com.android.radiosegmentedprogressview.stateprogress.components

import android.content.Context
import android.graphics.Typeface
import android.util.Log

class FontManager {

    private val mFontCache: MutableMap<String, Typeface?> = HashMap()

    private val FONTAWESOME = "fonts/fontawesome-webfont.ttf"

    fun getTypeface(context: Context): Typeface? {
        var typeface = mFontCache[FONTAWESOME]
        if (typeface == null) {
            typeface = Typeface.createFromAsset(context.assets, FONTAWESOME)
            mFontCache[FONTAWESOME] = typeface
        }
        return typeface
    }


    fun getTypeface(context: Context, filePath: String): Typeface? {
        synchronized(mFontCache) {
            try {
                if (!mFontCache.containsKey(filePath)) {
                    val typeface = Typeface.createFromAsset(context.assets, filePath)
                    mFontCache[filePath] = typeface
                    return typeface
                }
            } catch (e: Exception) {
                Log.w(
                    "StateProgressBar",
                    "Cannot create asset from $filePath. Ensure you have passed in the correct path and file name.",
                    e
                )
                mFontCache[filePath] = null
                return null
            }
            return mFontCache[filePath]
        }
    }

}