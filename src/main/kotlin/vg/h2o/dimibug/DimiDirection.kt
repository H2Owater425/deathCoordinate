package vg.h2o.dimibug

data class DimiDirection(val index: Int, val distance: Double)

val DimiDirection.arrow
    get() = ARROWS[index]