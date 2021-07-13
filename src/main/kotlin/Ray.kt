data class Ray(val origin: Vector3, val direction: Vector3) {
    fun at(t: Double): Vector3 = origin + t * direction

    fun fixNormal(outwardNormal: Vector3): Pair<Boolean, Vector3> {
        val frontFace = direction.dot(outwardNormal) < 0.0
        val normal = if (frontFace) outwardNormal else -outwardNormal
        return Pair(frontFace, normal)
    }
}