package dev.rlqd.alinotify

import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class CainiaoResponse(
    private val module: List<Module>,
) {
    @Serializable
    data class Module(
        val status: String,
        val statusDesc: String,
        val detailList: List<Details>,
    )
    @Serializable
    data class Details(
        val time: Long,
        val desc: String,
    )

    val status: String
        get() = module[0].statusDesc

    val details: List<Pair<Date,String>>
        get() = module[0].detailList.map {
            Pair(Date(it.time), it.desc)
        }

    override fun equals(other: Any?) =
        other is CainiaoResponse
            && status == other.status
            && details.size == other.details.size
            && details.firstOrNull() == other.details.firstOrNull()

    override fun toString() = "Current parcel status: ${status}\n" +
        "Latest update at ${details.firstOrNull()?.first}: ${details.firstOrNull()?.second}"
}
