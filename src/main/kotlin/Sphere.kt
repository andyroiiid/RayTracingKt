import kotlin.math.sqrt

class Sphere(private val center: Vector3, private val radius: Double) : Hittable() {
    override fun hit(ray: Ray, tMin: Double, tMax: Double): HitRecord? {
        val oc = ray.origin - center
        val a = ray.direction.lengthSquared()
        val h = oc.dot(ray.direction)
        val c = oc.lengthSquared() - radius * radius

        val discriminant = h * h - a * c
        if (discriminant < 0.0) return null
        val sqrtDiscriminant = sqrt(discriminant)

        var root = (-h - sqrtDiscriminant) / a
        if (root < tMin || tMax < root) {
            root = (-h + sqrtDiscriminant) / a
            if (root < tMin || tMax < root)
                return null
        }

        val point = ray.at(root)
        val (frontFace, normal) = ray.fixNormal((point - center) / radius)
        return HitRecord(root, point, frontFace, normal)
    }
}