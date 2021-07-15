import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

data class Vector3(val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0) {
    operator fun unaryMinus() = Vector3(-x, -y, -z)

    operator fun plus(s: Double) = Vector3(x + s, y + s, z + s)

    operator fun plus(v: Vector3) = Vector3(x + v.x, y + v.y, z + v.z)

    operator fun minus(s: Double) = Vector3(x - s, y - s, z - s)

    operator fun minus(v: Vector3) = Vector3(x - v.x, y - v.y, z - v.z)

    operator fun times(s: Double) = Vector3(x * s, y * s, z * s)

    operator fun times(v: Vector3) = Vector3(x * v.x, y * v.y, z * v.z)

    operator fun div(s: Double) = Vector3(x / s, y / s, z / s)

    operator fun div(v: Vector3) = Vector3(x / v.x, y * v.y, z * v.z)

    fun dot(v: Vector3): Double = x * v.x + y * v.y + z * v.z

    fun lengthSquared(): Double = dot(this)

    fun length(): Double = sqrt(lengthSquared())

    fun cross(v: Vector3): Vector3 = Vector3(
        y * v.z - z * v.y,
        z * v.x - x * v.z,
        x * v.y - y * v.x
    )

    fun normalized(): Vector3 = this / length()

    fun reflect(v: Vector3): Vector3 = v - 2.0 * dot(v) * this

    fun refract(v: Vector3, etaIOverEtaT: Double): Vector3 {
        val cosTheta = min(-this.dot(v), 1.0)
        val rOutPerpendicular = etaIOverEtaT * (v + cosTheta * this)
        val rOutParallel = -sqrt(abs(1.0 - rOutPerpendicular.lengthSquared())) * this
        return rOutPerpendicular + rOutParallel
    }

    companion object {
        fun random() = Vector3(
            Random.nextDouble(),
            Random.nextDouble(),
            Random.nextDouble()
        )
    }
}