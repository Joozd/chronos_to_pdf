package postprocessing

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Registration @JsonCreator constructor(
    @JsonProperty("registration") val registration: String,
    @JsonProperty("type") val type: String
){
    constructor(dataList: List<String>): this(dataList[0], dataList[1])
}
