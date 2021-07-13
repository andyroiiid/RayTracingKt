import kotlin.math.*
import kotlin.random.Random

operator fun Double.times(v: Vector3) = v * this

operator fun Double.div(v: Vector3) = v / this

fun mix(a: Vector3, b: Vector3, t: Double) = (1.0 - t) * a + t * b

fun clamp(x: Double, min: Double, max: Double) = min(max(x, min), max)

fun clamp01(x: Double) = clamp(x, 0.0, 1.0)

fun randomOnSphere(): Vector3 {
    val theta = 2.0 * PI * Random.nextDouble()
    val phi = acos(2.0 * Random.nextDouble() - 1.0)
    return Vector3(
        sin(phi) * cos(theta),
        sin(phi) * sin(theta),
        cos(phi)
    )
}

fun randomInSphere(): Vector3 {
    val r = Math.cbrt(Random.nextDouble())
    return randomOnSphere() * r
}

fun randomInHemisphere(normal: Vector3): Vector3 {
    val v = randomInSphere()
    return if (v.dot(normal) > 0.0) v else -v
}

fun gammaCorrect(x: Double) = sqrt(clamp01(x))

fun gammaCorrect(color: Vector3) = Vector3(
    gammaCorrect(color.x),
    gammaCorrect(color.y),
    gammaCorrect(color.z)
)