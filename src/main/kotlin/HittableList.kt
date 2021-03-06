open class HittableList(private val hittableList: ArrayList<Hittable> = ArrayList()) : Hittable() {
    fun add(hittable: Hittable) = hittableList.add(hittable)

    override fun hit(ray: Ray, tMin: Double, tMax: Double): HitRecord? {
        var hitAnything: HitRecord? = null
        var closestSoFar = tMax
        for (hittable in hittableList) {
            val hit = hittable.hit(ray, tMin, closestSoFar)
            if (hit != null) {
                hitAnything = hit
                closestSoFar = hit.t
            }
        }
        return hitAnything
    }
}