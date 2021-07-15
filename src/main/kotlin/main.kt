fun main() {
    val config = RenderConfig(1024, 1024, 32, 32, 16)
    val world = World(config.aspectRatio())
    val pixels = RenderJobs(config, world).getPixels()
    ImageOutput.writePng("test", config.imageWidth, config.imageHeight, pixels)
}