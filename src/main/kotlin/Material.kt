abstract class Material {
    data class ScatterResult(val dropRay: Boolean, val attenuation: Vector3, val scatteredRay: Ray)

    abstract fun scatter(ray: Ray, hit: HitRecord): ScatterResult
}