package com.glureau.protorest_sample_dogs

import com.glureau.protorest_core.ProtoRestApplication
import com.glureau.protorest_core.RestApi
import com.glureau.protorest_core.StringArray

// Inspired from 'Dog API' https://dog.ceo/dog-api/#all
class MainApplication : ProtoRestApplication<DogApi>(title = "Dog API", api = DogApi()) {
    init {
        setup(
                group("Breeds",
                        feature("list", { api.breeds() })
                ),
                group("Breed",
                        feature("Boxer", { api.breedImages("boxer") }),
                        feature("Chihuahua", { api.breedImages("chihuahua") }),
                        feature("Corgi", { api.breedImages("corgi") }),
                        feature("Doberman", { api.breedImages("doberman") })
                )
        )
    }
}

data class BreedList(val message: List<String>)

data class PictureList(@RestApi.Image val message: StringArray)

class DogApi : RestApi("https://dog.ceo/api/") {
    fun breeds() = get("breeds/list", BreedList::class.java)
    fun breedImages(breed: String) = get("breed/$breed/images", PictureList::class.java)
}
