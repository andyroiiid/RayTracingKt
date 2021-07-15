import kotlin.math.tan

class Camera(
    lookFrom: Vector3,
    lookAt: Vector3,
    up: Vector3,
    vFoV: Double,
    aspectRatio: Double,
    aperture: Double,
    focusDist: Double
) {
    private val viewportHeight = 2.0 * tan(vFoV / 2.0)
    private val viewportWidth = aspectRatio * viewportHeight

    private val w = (lookFrom - lookAt).normalized()
    private val u = up.cross(w).normalized()
    private val v = w.cross(u)

    private val origin = lookFrom
    private val horizontal = focusDist * viewportWidth * u
    private val vertical = focusDist * viewportHeight * v
    private val lowerLeftCorner = origin - horizontal * 0.5 - vertical * 0.5 - focusDist * w

    private val lensRadius = aperture / 2.0

    fun getRay(u: Double, v: Double): Ray {
        val random = lensRadius * randomInCircle()
        val origin = origin + u * random.x + v * random.y
        return Ray(origin, lowerLeftCorner + u * horizontal + v * vertical - origin)
    }
}