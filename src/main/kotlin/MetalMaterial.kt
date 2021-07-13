class MetalMaterial(private val albedo: Vector3, private val roughness: Double) : Material() {
    override fun scatter(ray: Ray, hit: HitRecord): ScatterResult {
        val scatterDirection = hit.normal.reflect(ray.direction)
        val scatteredRay = Ray(hit.point, scatterDirection + roughness * randomInSphere())
        return ScatterResult(scatterDirection.dot(hit.normal) < 0.0, albedo, scatteredRay)
    }
}