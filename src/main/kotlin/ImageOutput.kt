import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ImageOutput {
    fun writePng(filename: String, imageWidth: Int, imageHeight: Int, pixels: Array<IntArray>) {
        val image = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
        pixels.forEachIndexed { y, line ->
            val outputY = imageHeight - 1 - y
            image.setRGB(0, outputY, imageWidth, 1, line, 0, imageWidth)
        }
        ImageIO.write(image, "png", File("$filename.png"))
    }
}