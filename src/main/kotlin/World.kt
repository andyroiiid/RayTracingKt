import kotlin.math.PI

class World(cameraAspectRatio: Double) : HittableList() {
    val camera: Camera

    init {
        val position = Vector3(3.0, 4.0, 5.0)
        val target = Vector3(0.0, 1.0, 0.0)
        val up = Vector3(0.0, 1.0, 0.0)

        camera = Camera(
            position,
            target,
            up,
            PI / 4.0,
            cameraAspectRatio,
            0.1,
            (target - position).length()
        )

        val glassMaterial = DielectricMaterial(1.5)

        // ground
        add(XZRect(-10.0, -10.0, 10.0, 10.0, 0.0, true, LambertianMaterial(Vector3(0.5, 0.5, 0.5))))

        add(Box(Vector3(-1.0, 0.0, -1.0), Vector3(1.0, 2.0, 1.0), glassMaterial))
        add(Sphere(Vector3(0.0, 1.0, 0.0), -0.9, glassMaterial))

        add(Sphere(Vector3(-2.0, 1.0, 0.0), 1.0, LambertianMaterial(Vector3(0.4, 0.8, 1.0))))
        add(Sphere(Vector3(2.0, 1.0, 0.0), 1.0, MetalMaterial(Vector3(0.7, 0.6, 0.5), 0.0)))
    }

    fun skyColor(ray: Ray): Vector3 {
        val dir = ray.direction.normalized()
        return mix(Vector3(1.0, 1.0, 1.0), Vector3(0.5, 0.7, 1.0), 0.5 * dir.y + 0.5)
    }
}