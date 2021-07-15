import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class DielectricMaterial(private val indexOfRefraction: Double) : Material() {
    override fun scatter(ray: Ray, hit: HitRecord): ScatterResult {
        val refractionRatio = if (hit.frontFace) 1.0 / indexOfRefraction else indexOfRefraction
        val unitDirection = ray.direction.normalized()

        val cosTheta = min(-unitDirection.dot(hit.normal), 1.0)
        val sinTheta = sqrt(1.0 - cosTheta * cosTheta)

        val cannotRefract =
            refractionRatio * sinTheta > 1.0 || reflectance(cosTheta, refractionRatio) > Random.nextDouble()

        val refractDirection = if (cannotRefract) {
            hit.normal.reflect(unitDirection)
        } else {
            hit.normal.refract(unitDirection, refractionRatio)
        }

        val refractedRay = Ray(hit.point, refractDirection)
        return ScatterResult(false, Vector3(1.0, 1.0, 1.0), refractedRay)
    }

    private fun reflectance(cosine: Double, refractionRatio: Double): Double {
        // Schlick's approximation for reflectance
        var r0 = (1.0 - refractionRatio) / (1.0 + refractionRatio)
        r0 *= r0
        return r0 + (1.0 - r0) * (1.0 - cosine).pow(5.0)
    }
}