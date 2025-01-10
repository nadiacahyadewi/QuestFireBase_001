package com.example.firebase.ui.navigation

interface DestinasiNavigasi{
    val route: String
    val titleres: String
}

object DestinasiHome : DestinasiNavigasi{
    override val route: String = "home"
    override val titleres: String = "Home"
}

object DestinasiInsert : DestinasiNavigasi {
    override val route: String = "insert"
    override val titleres: String = "insert"
}