package com.glureau.protorest_core

import com.squareup.moshi.*

/**
 * Used for convenience only, this is nothing more than a typealias to Array<String>
 */
typealias StringArray = Array<String>

object StringArrayJsonAdapter : JsonAdapter<StringArray>() {
    override fun toJson(writer: JsonWriter?, value: StringArray?) {
        writer?.value(value?.joinToString() ?: "")
    }

    override fun fromJson(reader: JsonReader?): StringArray? {
        reader ?: return null
        val strings = ArrayList<String>()
        while (reader.hasNext()) {
            strings.add(reader.nextString())
        }
        return strings.toTypedArray()
    }
}