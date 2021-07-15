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

val glassMaterial = DielectricMaterial(1.5)

fun generateSphere(world: HittableList, center: Vector3) {
    val chooseMaterial = Random.nextDouble()
    when {
        chooseMaterial < 0.8 -> {
            // diffuse
            val albedo = Vector3.random() * Vector3.random()
            val material = LambertianMaterial(albedo)
            world.add(Sphere(center, 0.2, material))
        }
        chooseMaterial < 0.95 -> {
            // metal
            val albedo = Vector3.random() * 0.5 + 0.5
            val roughness = Random.nextDouble() * 0.5 + 0.5
            val material = MetalMaterial(albedo, roughness)
            world.add(Sphere(center, 0.2, material))
        }
        else -> {
            // glass
            world.add(Sphere(center, 0.2, glassMaterial))
        }
    }
}

fun generateWorld(): HittableList {
    val world = HittableList()

    val ground = LambertianMaterial(Vector3(0.5, 0.5, 0.5))
    world.add(Sphere(Vector3(0.0, -1000.0, 0.0), 1000.0, ground))

    val avoid = Vector3(4.0, 0.2, 0.0)
    for (x in -8..8) {
        for (y in -8..8) {
            val center = Vector3(x + 0.9 * Random.nextDouble(), 0.2, y + 0.9 * Random.nextDouble())
            if ((center - avoid).length() > 0.9) {
                generateSphere(world, center)
            }
        }
    }

    world.add(Sphere(Vector3(0.0, 1.0, 0.0), 1.0, glassMaterial))
    world.add(Sphere(Vector3(0.0, 1.0, 0.0), -0.9, glassMaterial))
    world.add(Sphere(Vector3(-4.0, 1.0, 0.0), 1.0, LambertianMaterial(Vector3(0.4, 0.8, 1.0))))
    world.add(Sphere(Vector3(4.0, 1.0, 0.0), 1.0, MetalMaterial(Vector3(0.7, 0.6, 0.5), 0.0)))

    return world
}

fun render(imageWidth: Int, imageHeight: Int, samples: Int, maxDepth: Int, numThreads: Int): List<MutableList<Int>> {
    val camera = Camera(
        Vector3(13.0, 2.0, 3.0),
        Vector3(0.0, 0.0, 0.0),
        Vector3(0.0, 1.0, 0.0),
        PI / 6.0,
        imageWidth.toDouble() / imageHeight.toDouble(),
        0.1,
        10.0
    )

    val world = generateWorld()

    val outputs = List(imageHeight) { mutableListOf<Int>() }
    val nextLine = AtomicInteger(0)
    val threads = List(numThreads) {
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
                    output.add(Color(color.x.toFloat(), color.y.toFloat(), color.z.toFloat()).rgb)
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
    val imageWidth = 800
    val imageHeight = 600

    val outputs = render(imageWidth, imageHeight, 32, 32, 16)

    val image = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until imageHeight) {
        val outputY = imageHeight - 1 - y
        for (x in 0 until imageWidth) {
            image.setRGB(x, y, outputs[outputY][x])
        }
    }

    ImageIO.write(image, "png", File("test.png"))
}