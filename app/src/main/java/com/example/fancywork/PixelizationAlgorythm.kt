package com.example.fancywork

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class PixelizationAlgorythm {
    fun getPixelsFromImage(bitmap: Bitmap, resources: Resources, pixelSize: Int):
            Array<Array<Pair<String, Triple<Int, Int, Int>>?>> {
        val stream = resources.openRawResource(R.raw.colors)
        val colors = stream
            .bufferedReader()
            .readLines()
            .drop(1)
            .map { x -> x.split(",") }
            .map { x -> x[0] to Triple(x[1].toInt(), x[2].toInt(), x[3].toInt()) }
        val resultTable = Array(ceil(bitmap.width.toDouble() / pixelSize).toInt()) {
            arrayOfNulls<Pair<String, Triple<Int, Int, Int>>>(ceil(bitmap.height.toDouble()).toInt())
        }
        for (i in 0 until bitmap.width step pixelSize) {
            for (j in 0 until bitmap.height step pixelSize) {
                val pixelWidth =
                    if (bitmap.width - i >= 2 * pixelSize) pixelSize else bitmap.width - i
                val pixelHeight =
                    if (bitmap.height - j >= 2 * pixelSize) pixelSize else bitmap.height - j
                val pixelColors = IntArray(pixelWidth * pixelHeight)
                bitmap.getPixels(pixelColors, 0, pixelWidth, i, j, pixelWidth, pixelHeight)
                val colorsAv = colorToTriple(findAverageColor(pixelColors, pixelWidth, pixelHeight))
                val mainColor = colors.minByOrNull { x -> findDistance(x.second, colorsAv) }!!
                resultTable[i / pixelSize][j / pixelSize] = mainColor
            }
        }
        return resultTable
    }

    private fun colorToTriple(color: Int): Triple<Int, Int, Int> {
        return Triple(
            (color shr 16) and 0xff,
            (color shr 8) and 0xff,
            color and 0xff
        )
    }

    private fun findAverageColor(pixel: IntArray, pixelWidth: Int, pixelHeight: Int): Int {
        val colorsSum = pixel
            .fold(Triple(0, 0, 0), { x, y ->
                Triple(
                    x.first + (y shr 16) and 0xff,
                    x.second + (y shr 8) and 0xff,
                    x.third + y and 0xff
                )
            })
        return Color.rgb(
            colorsSum.first / (pixelWidth * pixelHeight),
            colorsSum.second / (pixelWidth * pixelHeight),
            colorsSum.third / (pixelWidth * pixelHeight)
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