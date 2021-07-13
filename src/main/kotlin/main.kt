import java.io.File
import kotlin.random.Random

fun colorToString(color: Vector3): String {
    val r = (color.x * 255).toInt()
    val g = (color.y * 255).toInt()
    val b = (color.z * 255).toInt()
    return "$r $g $b\t"
}

fun raytrace(ray: Ray, world: Hittable, depth: Int): Vector3 {
    if (depth <= 0)
        return Vector3(0.0, 0.0, 0.0)

    val hit = world.hit(ray, 0.0001, Double.POSITIVE_INFINITY)
    if (hit != null) {
        val scatteredRay = Ray(hit.point, randomInHemisphere(hit.normal))
        return 0.5 * raytrace(scatteredRay, world, depth - 1)
    }

    val dir = ray.direction.normalized()
    return mix(Vector3(1.0, 1.0, 1.0), Vector3(0.5, 0.7, 1.0), 0.5 * dir.y + 0.5)
}

fun main() {
    val imageWidth = 400
    val imageHeight = 300
    val aspectRatio = imageWidth.toDouble() / imageHeight.toDouble()
    val samples = 32
    val maxDepth = 32

    val camera = Camera(aspectRatio, 2.0)

    val world = HittableList()
    world.add(Sphere(Vector3(0.0, 0.0, -1.0), 0.5))
    world.add(Sphere(Vector3(0.0, -100.5, -1.0), 100.0))

    val out = File("test.ppm").printWriter()
    out.println("P3")
    out.println("$imageWidth $imageHeight")
    out.println("255")
    for (y in (imageHeight - 1) downTo 0) {
        for (x in 0 until imageWidth) {
            var color = Vector3()
            repeat(samples) {
                val u = (x + Random.nextDouble()) / (imageWidth - 1).toDouble()
                val v = (y + Random.nextDouble()) / (imageHeight - 1).toDouble()
                color += raytrace(camera.getRay(u, v), world, maxDepth)
            }
            color /= samples.toDouble()
            color = gammaCorrect(color)
            out.println(colorToString(color))
        }
        println("finished line $y")
    }
    out.flush()
}