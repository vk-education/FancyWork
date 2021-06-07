package ru.mail.fancywork.model.repo

import android.content.res.Resources
import android.graphics.Bitmap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.mail.fancywork.R
import ru.mail.fancywork.model.datatype.MutablePair
import ru.mail.fancywork.model.datatype.MutableTriple

class PixelizationRepository {
    companion object {
        private const val SECOND_BYTE = 8
        private const val THIRD_BYTE = 16
        private const val FULL_BYTE = 0xff
        private const val MAX_ITERATIONS = 30
        private const val STARTING_DIFF = 1000000.0
        private const val MIN_DIFF = 3.0
    }

    // Method for getting thread colors from resources.
    fun getThreadColors(resources: Resources): List<Pair<String, Triple<Int, Int, Int>>> {
        val stream = resources.openRawResource(R.raw.colors)
        val colors = stream
            .bufferedReader()
            .readLines()
            .drop(1)
            .map { x -> x.split(",") }
            .map { x ->
                var i = 0
                x[i++] to Triple(x[i++].toInt(), x[i++].toInt(), x[i].toInt())
            }
        stream.close()
        return colors
    }

    // This method makes a pixelated bitmap from image bitmap and provides an array of thread codes.
    suspend fun getPixelsFromImage(
        bitmap: Bitmap,
        pixelSize: Int,
        colorsCount: Int,
        colors: List<Pair<String, Triple<Int, Int, Int>>>
    ): Pair<Bitmap, Array<Array<String?>>> = withContext(Dispatchers.IO) {
        lateinit var mainColors: List<Pair<String, Triple<Int, Int, Int>>>
        lateinit var pixelatedBitmap: Bitmap
        val kmeansJob = launch {
            mainColors = kmeans(bitmap, colorsCount, colors)
        }
        val pixelizationJob = launch {
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
                        val mainColor =
                            mainColors.minByOrNull { x -> findDistance(x.second, pixelColor) }!!
                        val mainRGB = (mainColor.second.first shl THIRD_BYTE) +
                            (mainColor.second.second shl SECOND_BYTE) + mainColor.second.third
                        threadCodes[i][j] = mainColor.first
                        bitmapColors[j * pixelatedWidth + i] = mainRGB
                    }
                )
            }
        listOfJobs.joinAll()
        val resultBitmap =
            Bitmap.createBitmap(
                bitmapColors,
                pixelatedWidth,
                pixelatedHeight,
                Bitmap.Config.RGB_565
            )
        return@withContext resultBitmap to threadCodes
    }

    private fun colorToTriple(color: Int): MutableTriple<Int, Int, Int> {
        return MutableTriple(
            (color shr THIRD_BYTE) and FULL_BYTE,
            (color shr SECOND_BYTE) and FULL_BYTE,
            color and FULL_BYTE
        )
    }

    private fun singleColorDistance(a: Int, b: Int): Double {
        return ((1 + max(a, b)).toDouble() / (1 + min(a, b))).pow(2)
    }

    private fun findDistance(
        x: Triple<Int, Int, Int>,
        colorsAv: MutableTriple<Int, Int, Int>
    ): Double {
        return singleColorDistance(x.first, colorsAv.first) + singleColorDistance(
            x.second,
            colorsAv.second
        ) + singleColorDistance(x.third, colorsAv.third)
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
                imageColors[colorToTriple(color)] = 1
            else
                imageColors[colorTriple] = imageColors[colorTriple]!! + 1
        }
        // Инициализируем центроиды для алгоритма.
        val centers = initCenters(imageColors, k)
        // Заготавливаем списки точек для кластеров.
        val clusters = Array(k) { MutablePair(MutableTriple(0, 0, 0), 0) }
        val listOfJobs = ConcurrentLinkedQueue<Deferred<Double>>()
        var diff = STARTING_DIFF
        var iteration = 0
        // Обновляем центроиды, пока они не перестанут смещаться, либо пока не пройдет слишком много итераций.
        while (diff > MIN_DIFF && iteration < MAX_ITERATIONS) {
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
                    async(block = recenterCluster(clusters, i, imageColors, centers, diff))
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

    private fun recenterCluster(
        clusters: Array<MutablePair<MutableTriple<Int, Int, Int>, Int>>,
        i: Int,
        imageColors: MutableMap<MutableTriple<Int, Int, Int>, Int>,
        centers: MutableList<MutablePair<Int, MutableTriple<Int, Int, Int>>>,
        diff: Double
    ): suspend CoroutineScope.() -> Double {
        return {
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
    }

    private fun initCenters(
        colors: MutableMap<MutableTriple<Int, Int, Int>, Int>,
        k: Int
    ): MutableList<MutablePair<Int, MutableTriple<Int, Int, Int>>> {
        val centers = mutableListOf<MutablePair<Int, MutableTriple<Int, Int, Int>>>()
        for (i in 0 until k) {
            centers.add(
                MutablePair(
                    i,
                    MutableTriple(
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
                    )
                )
            )
        }
        return centers
    }

    private fun euclidDistance(
        x: MutableTriple<Int, Int, Int>,
        y: MutableTriple<Int, Int, Int>
    ): Double =
        sqrt(
            (
                (x.first - y.first) * (x.first - y.first) +
                    (x.first - y.first) * (x.first - y.first) +
                    (x.first - y.first) * (x.first - y.first)
                ).toDouble()
        )
}
