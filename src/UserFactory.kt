class UserFactory {

    fun getBuilderByName(name: String?): UserType {
        return when (name) {
            "PolarPoint" -> PolarPoint()
            "Integer" -> Integer()
            else -> throw IllegalArgumentException()
        }
    }

    fun typeNameList(): List<String> {
        return listOf("PolarPoint", "Integer")
    }
}