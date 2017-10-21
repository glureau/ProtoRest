package com.glureau.geno.sample.dogs

import com.glureau.geno.old.GenoApplication
import com.glureau.geno.old.rest.RestApi
import com.glureau.geno.old.rest.StringArray
import com.glureau.geno.old.rest.annotation.Image

// Inspired from 'Dog API' https://dog.ceo/dog-api/#all
class MainApplication : GenoApplication<DogApi>(api = DogApi()) {
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

data class BreedList(val message: StringArray)
data class PictureList(@Image val message: StringArray)

class DogApi : RestApi("https://dog.ceo/api/") {
    fun breeds() = get("breeds/list", BreedList::class.java)
    fun breedImages(breed: String) = get("breed/$breed/images", PictureList::class.java)
}
