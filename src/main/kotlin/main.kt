import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.text.StringBuilder
import kotlin.random.Random

fun colorToString(color: Vector3): String {
    val r = (color.x * 255).toInt()
    val g = (color.y * 255).toInt()
    val b = (color.z * 255).toInt()
    return "$r $g $b\t"
}

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

fun render(imageWidth: Int, imageHeight: Int, samples: Int, maxDepth: Int, numThreads: Int = 8): List<StringBuilder> {
    val aspectRatio = imageWidth.toDouble() / imageHeight.toDouble()

    val position = Vector3(3.0, 3.0, 2.0)
    val target = Vector3(0.0, 0.0, 0.0)

    val camera = Camera(
        position,
        target,
        Vector3(0.0, 1.0, 0.0),
        PI / 6.0,
        aspectRatio,
        1.0,
        (target - position).length()
    )

    val ground = LambertianMaterial(Vector3(0.8, 0.8, 0.0))
    val red = LambertianMaterial(Vector3(1.0, 0.0, 0.0))
    val metal = MetalMaterial(Vector3(0.8, 0.6, 0.2), 0.3)
    val glass = DielectricMaterial(1.5)

    val world = HittableList()
    world.add(Sphere(Vector3(-1.0, 0.0, 0.0), 0.5, glass))
    world.add(Sphere(Vector3(-1.0, 0.0, 0.0), -0.45, glass))
    world.add(Sphere(Vector3(0.0, 0.0, 0.0), 0.5, red))
    world.add(Sphere(Vector3(1.0, 0.0, 0.0), 0.5, metal))
    world.add(Sphere(Vector3(0.0, -100.5, 0.0), 100.0, ground))

    val outputs = List(imageHeight) { StringBuilder() }

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
                    output.append(colorToString(color))
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

    val outputs = render(imageWidth, imageHeight, 64, 32)

    val out = File("test.ppm").printWriter()
    out.println("P3")
    out.println("$imageWidth $imageHeight")
    out.println("255")
    outputs.asReversed().forEach {
        out.println(it.toString())
    }
    out.flush()
}