import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.random.Random

fun raytraceSky(ray: Ray): Vector3 {
    val dir = ray.direction.normalized()
    return mix(Vector3(1.0, 1.0, 1.0), Vector3(0.5, 0.7, 1.0), 0.5 * dir.y + 0.5)
}

fun raytrace(ray: Ray, world: Hittable, depth: Int): Vector3 {
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
        raytraceSky(ray)
    }
}

fun generateWorld(): HittableList {
    val world = HittableList()

    val glassMaterial = DielectricMaterial(1.5)
    world.add(XZRect(-10.0, -10.0, 10.0, 10.0, 0.0, true, LambertianMaterial(Vector3(0.5, 0.5, 0.5))))
    world.add(Box(Vector3(-1.0, 0.0, -1.0), Vector3(1.0, 2.0, 1.0), glassMaterial))
    world.add(Sphere(Vector3(0.0, 1.0, 0.0), -0.9, glassMaterial))
    world.add(Sphere(Vector3(-2.0, 1.0, 0.0), 1.0, LambertianMaterial(Vector3(0.4, 0.8, 1.0))))
    world.add(Sphere(Vector3(2.0, 1.0, 0.0), 1.0, MetalMaterial(Vector3(0.7, 0.6, 0.5), 0.0)))

    return world
}

fun render(imageWidth: Int, imageHeight: Int, samples: Int, maxDepth: Int, numThreads: Int): Array<IntArray> {
    val position = Vector3(3.0, 4.0, 5.0)
    val target = Vector3(0.0, 1.0, 0.0)
    val up = Vector3(0.0, 1.0, 0.0)

    val camera = Camera(
        position,
        target,
        up,
        PI / 4.0,
        imageWidth.toDouble() / imageHeight.toDouble(),
        0.1,
        (target - position).length()
    )

    val world = generateWorld()

    val outputs = Array(imageHeight) { IntArray(imageWidth) }
    val nextLine = AtomicInteger(0)
    val threads = Array(numThreads) {
        thread(true) {
            while (true) {
                val y = nextLine.getAndAdd(1)
                if (y >= imageHeight) break

                val output = outputs[y]
                for (x in 0 until imageWidth) {
                    var color = Vector3()
                    repeat(samples) {
                        val u = (x + Random.nextDouble()) / (imageWidth - 1).toDouble()
                        val v = (y + Random.nextDouble()) / (imageHeight - 1).toDouble()
                        color += raytrace(camera.getRay(u, v), world, maxDepth)
                    }
                    color /= samples.toDouble()
                    color = gammaCorrect(color)
                    output[x] = Color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat()).rgb
                }

                println("finished line $y")
            }
        }
    }

    for (thread in threads) {
        thread.join()
    }

    return outputs
}

fun main() {
    val imageWidth = 1024
    val imageHeight = 1024

    val outputs = render(imageWidth, imageHeight, 32, 32, 16)

    val image = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until imageHeight) {
        val outputY = imageHeight - 1 - y
        image.setRGB(0, y, imageWidth, 1, outputs[outputY], 0, imageWidth)
    }

    ImageIO.write(image, "png", File("test.png"))
}