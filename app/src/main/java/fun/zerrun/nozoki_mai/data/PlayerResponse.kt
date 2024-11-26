package `fun`.zerrun.nozoki_mai.data

data class PlayerResponse(
    val additional_rating: Int,
    val charts: Map<String, Any>, // 这里可以更具体，根据实际需求定义
    val nickname: String,
    val plate: String,
    val rating: Int,
    val user_general_data: Any?, // 这里可以更具体，根据实际需求定义
    val username: String
)
