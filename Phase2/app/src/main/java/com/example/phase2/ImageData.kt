package com.example.phase2

import android.net.Uri


// added this base on the suggestion made by Josh
// will be used to save images
data class ImageData(
    val src: Uri,
    var x: Float,
    var y: Float
)
