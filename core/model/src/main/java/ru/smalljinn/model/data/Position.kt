package ru.smalljinn.model.data

data class Position(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        fun initialPosition() = Position(0.0,0.0)
    }
}