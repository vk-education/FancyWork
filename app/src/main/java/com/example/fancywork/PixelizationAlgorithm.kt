package com.example.fancywork

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import io.uuddlrlrba.closepixelate.Pixelate;
import io.uuddlrlrba.closepixelate.PixelateLayer;
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class PixelizationAlgorithm {
    companion object {
        // Method for getting thread colors from resources.
        fun getThreadColors(resources: Resources): List<Pair<String, Triple<Int, Int, Int>>> {
            val stream = resources.openRawResource(R.raw.colors)
            val colors = stream
                .bufferedReader()
                .readLines()
                .drop(1)
                .map { x -> x.split(",") }
                .map { x -> x[0] to Triple(x[1].toInt(), x[2].toInt(), x[3].toInt()) }
            stream.close()
            return colors
        }

        // This method makes a pixelated bitmap from image bitmap and provides an array of thread codes.
        fun getPixelsFromImage(
            bitmap: Bitmap,
            resources: Resources,
            pixelSize: Int,
            colors: List<Pair<String, Triple<Int, Int, Int>>>):
                Pair<Bitmap, Array<Array<String?>>> {
            val pixelatedBitmap = Pixelate.fromBitmap(
                bitmap,
                PixelateLayer.Builder(PixelateLayer.Shape.Square)
                    .setSize(pixelSize.toFloat())
                    .setEnableDominantColors(true)
                    .build()
            )
            val pixelatedWidth = ceil(bitmap.width.toDouble() / pixelSize).toInt()
            val pixelatedHeight = ceil(bitmap.height.toDouble() / pixelSize).toInt()
            val threadCodes = Array(pixelatedWidth) {
                arrayOfNulls<String>(pixelatedHeight)
            }
            val bitmapColors = IntArray(pixelatedWidth * pixelatedHeight)
            for (i in 0 until bitmap.width step pixelSize)
                for (j in 0 until bitmap.height step pixelSize) {
                    val pixel = pixelatedBitmap.getPixel(i, j)
                    val pixelColor = colorToTriple(pixel)
                    val mainColor = colors.minByOrNull { x -> findDistance(x.second, pixelColor) }!!
                    val mainRGB = (mainColor.second.first shl 16) +
                            (mainColor.second.second shl 8) + mainColor.second.third
                    threadCodes[i / pixelSize][j / pixelSize] = mainColor.first
                    bitmapColors[j / pixelSize * pixelatedWidth + i / pixelSize] = mainRGB
                }
            val resultBitmap =
                Bitmap.createBitmap(bitmapColors, pixelatedWidth, pixelatedHeight, Bitmap.Config.RGB_565)
            return resultBitmap to threadCodes
        }

        private fun colorToTriple(color: Int): Triple<Int, Int, Int> {
            return Triple(
                (color shr 16) and 0xff,
                (color shr 8) and 0xff,
                color and 0xff
            )
        }

        private fun findDistance(x: Triple<Int, Int, Int>, colorsAv: Triple<Int, Int, Int>): Double {
            return (((1 + max(x.first, colorsAv.first)).toDouble() / (1 + min(
                x.first,
                colorsAv.first
            ))).pow(2)
                    + ((1 + max(x.second, colorsAv.second)).toDouble() / (1 + min(
                x.second,
                colorsAv.second
            ))).pow(2)
                    + ((1 + max(x.third, colorsAv.third)).toDouble() / (1 + min(
                x.third,
                colorsAv.third
            ))).pow(2))
        }
    }
}