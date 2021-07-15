import kotlin.math.tan

class Camera(lookFrom: Vector3, lookAt: Vector3, up: Vector3, vFoV: Double, aspectRatio: Double) {
    private val viewportHeight = 2.0 * tan(vFoV / 2.0)
    private val viewportWidth = aspectRatio * viewportHeight

    private val w = (lookFrom - lookAt).normalized()
    private val u = up.cross(w).normalized()
    private val v = w.cross(u)

    private val origin = lookFrom
    private val horizontal = viewportWidth * u
    private val vertical = viewportHeight * v
    private val lowerLeftCorner = origin - horizontal * 0.5 - vertical * 0.5 - w

    fun getRay(u: Double, v: Double): Ray {
        return Ray(origin, lowerLeftCorner + u * horizontal + v * vertical - origin)
    }
}