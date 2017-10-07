package com.glureau.protorest_core.rest

data class RestParameter<T>(val name: String, val defaultValue: T? = null)