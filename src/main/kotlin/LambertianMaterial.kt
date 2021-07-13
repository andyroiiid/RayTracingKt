class LambertianMaterial(private val albedo: Vector3) : Material() {
    override fun scatter(ray: Ray, hit: HitRecord): ScatterResult {
        val scatteredRay = Ray(hit.point, randomInHemisphere(hit.normal))
        return ScatterResult(false, albedo, scatteredRay)
    }
}