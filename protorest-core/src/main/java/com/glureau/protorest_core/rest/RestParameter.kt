package com.glureau.protorest_core.rest

data class RestParameter(val name: String = "Undefined", val defaultValue: String = "", val suggestedValues: Array<String> = arrayOf<String>()) {
    var value = defaultValue
}