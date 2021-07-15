class XYRect(
    private val x0: Double, private val y0: Double,
    private val x1: Double, private val y1: Double,
    private val k: Double, facingPos: Boolean,
    private val material: Material
) : Hittable() {
    private val normal = if (facingPos) Vector3(0.0, 0.0, 1.0) else Vector3(0.0, 0.0, -1.0)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): HitRecord? {
        val t = (k - ray.origin.z) / ray.direction.z
        if (t < tMin || t > tMax) return null

        val x = ray.origin.x + t * ray.direction.x
        val y = ray.origin.y + t * ray.direction.y
        if (x < x0 || x > x1 || y < y0 || y > y1) return null

        val (frontFace, normal) = ray.fixNormal(normal)
        return HitRecord(t, ray.at(t), frontFace, normal, material)
    }
}

class YZRect(
    private val y0: Double, private val z0: Double,
    private val y1: Double, private val z1: Double,
    private val k: Double, facingPos: Boolean,
    private val material: Material
) : Hittable() {
    private val normal = if (facingPos) Vector3(1.0, 0.0, 0.0) else Vector3(-1.0, 0.0, 0.0)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): HitRecord? {
        val t = (k - ray.origin.x) / ray.direction.x
        if (t < tMin || t > tMax) return null

        val y = ray.origin.y + t * ray.direction.y
        val z = ray.origin.z + t * ray.direction.z
        if (y < y0 || y > y1 || z < z0 || z > z1) return null

        val (frontFace, normal) = ray.fixNormal(normal)
        return HitRecord(t, ray.at(t), frontFace, normal, material)
    }
}

class XZRect(
    private val x0: Double, private val z0: Double,
    private val x1: Double, private val z1: Double,
    private val k: Double, facingPos: Boolean,
    private val material: Material
) : Hittable() {
    private val normal = if (facingPos) Vector3(0.0, 1.0, 0.0) else Vector3(0.0, -1.0, 0.0)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): HitRecord? {
        val t = (k - ray.origin.y) / ray.direction.y
        if (t < tMin || t > tMax) return null

        val x = ray.origin.x + t * ray.direction.x
        val z = ray.origin.z + t * ray.direction.z
        if (x < x0 || x > x1 || z < z0 || z > z1) return null

        val (frontFace, normal) = ray.fixNormal(normal)
        return HitRecord(t, ray.at(t), frontFace, normal, material)
    }
}
