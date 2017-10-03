package com.glureau.protorest_core

class RestResult<out T>(val data: T, val enhancements: MutableMap<String, Any> = mutableMapOf())
