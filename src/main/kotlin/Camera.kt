class Camera(aspectRatio: Double, viewportHeight: Double, focalLength: Double = 1.0) {
    private val viewportWidth = aspectRatio * viewportHeight
    private val origin = Vector3(0.0, 0.0, 0.0)
    private val horizontal = Vector3(viewportWidth, 0.0, 0.0)
    private val vertical = Vector3(0.0, viewportHeight, 0.0)
    private val lowerLeftCorner = origin - horizontal * 0.5 - vertical * 0.5 - Vector3(0.0, 0.0, focalLength)

    fun getRay(u: Double, v: Double): Ray {
        return Ray(origin, lowerLeftCorner + u * horizontal + v * vertical - origin)
    }
}