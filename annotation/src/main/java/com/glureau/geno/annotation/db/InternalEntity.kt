package com.glureau.geno.annotation.db

import java.util.*

/**
 * Generated entities extends this interface so we can use the internal fields without reflection.
 */

interface InternalEntity {
    val _internal_id: Long
    val _internal_update_date: Date
}
