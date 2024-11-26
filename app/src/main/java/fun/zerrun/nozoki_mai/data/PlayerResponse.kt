package `fun`.zerrun.nozoki_mai.data

data class PlayerResponse(
    val nickname: String,
    val rating: Int,
    val additional_rating: Int,
    val charts: Charts
)

data class Charts(
    val dx: List<Chart>,
    val sd: List<Chart>
)

data class Chart(
    val achievements: Double,
    val ds: Double,
    val dxScore: Int,
    val fc: String,
    val fs: String,
    val level: String,
    val level_index: Int,
    val level_label: String,
    val ra: Int,
    val rate: String,
    val song_id: Int,
    val title: String,
    val type: String
)
