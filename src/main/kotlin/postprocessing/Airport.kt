package postprocessing

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class Airport @JsonCreator constructor(
    @JsonProperty("ident") val ident: String,
    @JsonProperty("latitude_deg") val latitude_deg: Double,
    @JsonProperty("longitude_deg") val longitude_deg: Double,
    @JsonProperty("iata_code") val iata_code: String
)
