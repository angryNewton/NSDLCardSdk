package com.nexxo.nsdlsdk.model

data class ControlItemsDTO(

    var id: Int=0,
    var name: String = "",
    var description: String = "",
    var limits: String = "",
    var isChecked :Boolean=false
) {
    override fun toString(): String {
        return "ControlItemsDTO(id=$id, name='$name', description='$description', limits='$limits', isChecked=$isChecked)"
    }
}
