class Box(p0: Vector3, p1: Vector3, material: Material) : HittableList(
    arrayListOf(
        XYRect(p0.x, p0.y, p1.x, p1.y, p0.z, false, material),
        XYRect(p0.x, p0.y, p1.x, p1.y, p1.z, true, material),
        YZRect(p0.y, p0.z, p1.y, p1.z, p0.x, false, material),
        YZRect(p0.y, p0.z, p1.y, p1.z, p1.x, true, material),
        XZRect(p0.x, p0.z, p1.x, p1.z, p0.y, false, material),
        XZRect(p0.x, p0.z, p1.x, p1.z, p1.y, true, material)
    )
)