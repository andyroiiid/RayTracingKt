import kotlin.math.sqrt

data class Vector3(val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0) {
    operator fun unaryMinus() = Vector3(-x, -y, -z)

    operator fun plus(v: Vector3) = Vector3(x + v.x, y + v.y, z + v.z)

    operator fun minus(v: Vector3) = Vector3(x - v.x, y - v.y, z - v.z)

    operator fun times(s: Double) = Vector3(x * s, y * s, z * s)

    operator fun div(s: Double) = Vector3(x / s, y / s, z / s)

    fun dot(v: Vector3): Double = x * v.x + y * v.y + z * v.z

    fun lengthSquared(): Double = dot(this)

    fun length(): Double = sqrt(lengthSquared())

    fun cross(v: Vector3): Vector3 = Vector3(
        y * v.z - z * v.y,
        z * v.x - x * v.z,
        x * v.y - y * v.x
    )

    fun normalized(): Vector3 = this / length()
}