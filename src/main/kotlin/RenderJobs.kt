import java.awt.Color
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.random.Random

class RenderJobs(private val config: RenderConfig, private val world: World) {
    private val pixels = config.allocatePixels()

    private val nextLineToRender = AtomicInteger(0)

    private val workers = Array(config.numThreads) {
        thread(true) { worker() }
    }

    private fun worker() {
        while (true) {
            val y = nextLineToRender.getAndAdd(1)
            if (y >= config.imageHeight) break
            renderLine(y)
        }
    }

    private fun renderLine(y: Int) {
        val line = pixels[y]
        for (x in 0 until config.imageWidth) {
            var color = Vector3()
            repeat(config.samples) {
                val u = (x + Random.nextDouble()) / (config.imageWidth - 1).toDouble()
                val v = (y + Random.nextDouble()) / (config.imageHeight - 1).toDouble()
                color += raytrace(world.camera.getRay(u, v), config.maxDepth)
            }
            color /= config.samples.toDouble()
            color = gammaCorrect(color)
            line[x] = Color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat()).rgb
        }
        println("finished line $y")
    }

    private fun raytrace(ray: Ray, depth: Int): Vector3 {
        if (depth <= 0)
            return Vector3(0.0, 0.0, 0.0)

        val hit = world.hit(ray, 0.0001, Double.POSITIVE_INFINITY)
        return if (hit != null) {
            val scatter = hit.material.scatter(ray, hit)
            if (scatter.dropRay) {
                Vector3(0.0, 0.0, 0.0)
            } else {
                scatter.attenuation * raytrace(scatter.scatteredRay, depth - 1)
            }
        } else {
            world.skyColor(ray)
        }
    }

    fun getPixels(): Array<IntArray> {
        workers.forEach { it.join() }
        return pixels
    }
}