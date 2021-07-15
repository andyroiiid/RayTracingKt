data class RenderConfig(
    val imageWidth: Int,
    val imageHeight: Int,
    val samples: Int,
    val maxDepth: Int,
    val numThreads: Int
) {
    fun aspectRatio(): Double = imageWidth.toDouble() / imageHeight.toDouble()
    fun allocatePixels(): Array<IntArray> = Array(imageHeight) { IntArray(imageWidth) }
}