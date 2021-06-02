package ru.mail.fancywork.model.repo

import android.content.res.Resources
import android.graphics.Bitmap
import kotlinx.coroutines.*
import ru.mail.fancywork.R
import ru.mail.fancywork.model.datatype.MutablePair
import ru.mail.fancywork.model.datatype.MutableTriple
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.*
import kotlin.random.Random

class PixelizationRepository {

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
    suspend fun getPixelsFromImage(
        bitmap: Bitmap,
        pixelSize: Int,
        colorsCount: Int,
        colors: List<Pair<String, Triple<Int, Int, Int>>>):
            Pair<Bitmap, Array<Array<String?>>> = withContext(Dispatchers.IO){
        lateinit var mainColors: List<Pair<String, Triple<Int, Int, Int>>>
        lateinit var pixelatedBitmap: Bitmap
        val kmeansJob = launch {
            mainColors = kmeans(bitmap, colorsCount, colors)
        }
        val pixelizationJob = launch{
            pixelatedBitmap =
                Bitmap.createScaledBitmap(
                    bitmap,
                    bitmap.width / pixelSize,
                    bitmap.height / pixelSize,
                    true
                )
        }
        kmeansJob.join()
        pixelizationJob.join()
        val pixelatedWidth = pixelatedBitmap.width
        val pixelatedHeight = pixelatedBitmap.height
        val threadCodes = Array(pixelatedWidth) {
            arrayOfNulls<String>(pixelatedHeight)
        }
        val bitmapColors = IntArray(pixelatedWidth * pixelatedHeight)
        val listOfJobs = ConcurrentLinkedQueue<Job>()
        for (i in 0 until pixelatedBitmap.width)
            for (j in 0 until pixelatedBitmap.height) {
                listOfJobs.add(
                    launch {
                        val pixel = pixelatedBitmap.getPixel(i, j)
                        val pixelColor = colorToTriple(pixel)
                        val mainColor = mainColors.minByOrNull { x -> findDistance(x.second, pixelColor) }!!
                        val mainRGB = (mainColor.second.first shl 16) +
                                (mainColor.second.second shl 8) + mainColor.second.third
                        threadCodes[i][j] = mainColor.first
                        bitmapColors[j * pixelatedWidth + i] = mainRGB
                    }
                )
            }
        listOfJobs.joinAll()
        val resultBitmap =
            Bitmap.createBitmap(bitmapColors, pixelatedWidth, pixelatedHeight, Bitmap.Config.RGB_565)
        return@withContext resultBitmap to threadCodes
    }

    private fun colorToTriple(color: Int): MutableTriple<Int, Int, Int> {
        return MutableTriple(
            (color shr 16) and 0xff,
            (color shr 8) and 0xff,
            color and 0xff
        )
    }

    private fun findDistance(x: Triple<Int, Int, Int>, colorsAv: MutableTriple<Int, Int, Int>): Double {
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

    private suspend fun kmeans(
        image: Bitmap,
        k: Int,
        colors: List<Pair<String, Triple<Int, Int, Int>>>
    ): List<Pair<String, Triple<Int, Int, Int>>> = withContext(Dispatchers.IO) {
        // Извлекаем все цвета пикселей из картинки.
        val imageIntColors = IntArray(image.width * image.height)
        image.getPixels(imageIntColors, 0, image.width, 0, 0, image.width, image.height)
        val imageColors = mutableMapOf<MutableTriple<Int, Int, Int>, Int>()
        for (color in imageIntColors) {
            val colorTriple = colorToTriple(color)
            if (!imageColors.containsKey(colorTriple))
                imageColors.put(colorToTriple(color), 1)
            else
                imageColors[colorTriple] = imageColors[colorTriple]!! + 1
        }
        // Инициализируем центроиды для алгоритма.
        val centers = initCenters(imageColors, k)
        // Заготавливаем списки точек для кластеров.
        val clusters = Array(k) { MutablePair(MutableTriple(0, 0, 0), 0) }
        val listOfJobs = ConcurrentLinkedQueue<Deferred<Double>>()
        var diff = 1000000.0
        var iteration = 0
        // Обновляем центроиды, пока они не перестанут смещаться, либо пока не пройдет слишком много итераций.
        while (diff > 3.0 && iteration < 30) {
            for (cluster in clusters) {
                cluster.first.first = 0
                cluster.first.second = 0
                cluster.first.third = 0
                cluster.second = 0
            }
            // Для каждой точки выбираем ближайший центроид.
            imageColors.forEach { x ->
                val index = centers.minByOrNull { y ->
                    euclidDistance(x.key, y.second)
                }!!.first
                clusters[index].first.first += x.key.first * x.value
                clusters[index].first.second += x.key.second * x.value
                clusters[index].first.third += x.key.third * x.value
                clusters[index].second += x.value
            }
            diff = 0.0
            // Для каждого центроида меняем его положение на среднее из точек в его кластере.
            for (i in 0 until k) {
                listOfJobs.add(
                    async {
                        val newCenter = MutableTriple(0, 0, 0)
                        // Если в кластере этого центроида нет точек, рандомим ему новое поожение.
                        if (clusters[i].second != 0) {
                            newCenter.first = clusters[i].first.first / clusters[i].second
                            newCenter.second = clusters[i].first.second / clusters[i].second
                            newCenter.third = clusters[i].first.third / clusters[i].second
                        } else {
                            newCenter.first =
                                Random.nextInt(
                                    imageColors.minByOrNull { x -> x.key.first }!!.key.first,
                                    imageColors.maxByOrNull { x -> x.key.first }!!.key.first
                                )
                            newCenter.second =
                                Random.nextInt(
                                    imageColors.minByOrNull { x -> x.key.second }!!.key.second,
                                    imageColors.maxByOrNull { x -> x.key.second }!!.key.second
                                )
                            newCenter.third =
                                Random.nextInt(
                                    imageColors.minByOrNull { x -> x.key.third }!!.key.third,
                                    imageColors.maxByOrNull { x -> x.key.third }!!.key.third
                                )
                        }
                        // Вычисляем максимальное смещение центроидов.
                        val currentDiff = euclidDistance(centers[i].second, newCenter)
                        centers[i].first = i
                        centers[i].second.first = newCenter.first
                        centers[i].second.second = newCenter.second
                        centers[i].second.third = newCenter.third
                        diff
                    }
                )
                diff = listOfJobs.awaitAll().maxOrNull()!!
                listOfJobs.clear()
            }
            iteration++
        }
        return@withContext centers.map { x ->
            colors.minByOrNull { y -> findDistance(y.second, x.second) }!!
        }.toList()
    }

    private fun initCenters(colors: MutableMap<MutableTriple<Int, Int, Int>, Int>, k: Int):
            MutableList<MutablePair<Int, MutableTriple<Int, Int, Int>>> {
        val centers = mutableListOf<MutablePair<Int, MutableTriple<Int, Int, Int>>>()
        for (i in 0 until k) {
            centers.add(MutablePair(i, MutableTriple(
                Random.nextInt(
                    colors.minByOrNull { x -> x.key.first }!!.key.first,
                    colors.maxByOrNull { x -> x.key.first }!!.key.first
                ),
                Random.nextInt(
                    colors.minByOrNull { x -> x.key.second }!!.key.second,
                    colors.maxByOrNull { x -> x.key.second }!!.key.second
                ),
                Random.nextInt(
                    colors.minByOrNull { x -> x.key.third }!!.key.third,
                    colors.maxByOrNull { x -> x.key.third }!!.key.third
                )
            )))
        }
        return centers
    }

    private fun euclidDistance(x: MutableTriple<Int, Int, Int>, y: MutableTriple<Int, Int, Int>): Double =
        sqrt((
                (x.first - y.first) * (x.first - y.first) +
                        (x.first - y.first) * (x.first - y.first) +
                        (x.first - y.first) * (x.first - y.first)
                ).toDouble())
}
