package postprocessing

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Aircraft @JsonCreator constructor(
    @JsonProperty("name") val name: String = "",
    @JsonProperty("shortName") val shortName: String = "",
    @JsonProperty("multiPilot") val multiPilot:Boolean = false,
    @JsonProperty("multiEngine") val multiEngine:Boolean = false
){
    constructor(dataList: List<String>): this(dataList[0], dataList[1], dataList[2] == "MP", dataList[3] == "ME")
}


