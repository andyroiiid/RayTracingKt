import java.awt.Color
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.random.Random

fun raytrace(ray: Ray, world: World, depth: Int): Vector3 {
    if (depth <= 0)
        return Vector3(0.0, 0.0, 0.0)

    val hit = world.hit(ray, 0.0001, Double.POSITIVE_INFINITY)
    return if (hit != null) {
        val scatter = hit.material.scatter(ray, hit)
        if (scatter.dropRay) {
            Vector3(0.0, 0.0, 0.0)
        } else {
            scatter.attenuation * raytrace(scatter.scatteredRay, world, depth - 1)
        }
    } else {
        world.skyColor(ray)
    }
}

fun render(imageWidth: Int, imageHeight: Int, samples: Int, maxDepth: Int, numThreads: Int): Array<IntArray> {
    val world = World(imageWidth.toDouble() / imageHeight.toDouble())

    val pixels = Array(imageHeight) { IntArray(imageWidth) }
    val nextLine = AtomicInteger(0)
    val threads = Array(numThreads) {
        thread(true) {
            while (true) {
                val y = nextLine.getAndAdd(1)
                if (y >= imageHeight) break

                val line = pixels[y]
                for (x in 0 until imageWidth) {
                    var color = Vector3()
                    repeat(samples) {
                        val u = (x + Random.nextDouble()) / (imageWidth - 1).toDouble()
                        val v = (y + Random.nextDouble()) / (imageHeight - 1).toDouble()
                        color += raytrace(world.camera.getRay(u, v), world, maxDepth)
                    }
                    color /= samples.toDouble()
                    color = gammaCorrect(color)
                    line[x] = Color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat()).rgb
                }
                println("finished line $y")
            }
        }
    }

    for (thread in threads) {
        thread.join()
    }

    return pixels
}

fun main() {
    val imageWidth = 1024
    val imageHeight = 1024
    val pixels = render(imageWidth, imageHeight, 32, 32, 16)
    ImageOutput.writePng("test", imageWidth, imageHeight, pixels)
}