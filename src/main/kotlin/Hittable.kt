abstract class Hittable {
    abstract fun hit(ray: Ray, tMin: Double, tMax: Double): HitRecord?
}