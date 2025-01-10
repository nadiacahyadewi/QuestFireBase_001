package com.example.firebase.ui.home.viewmodel

import com.example.firebase.model.Mahasiswa



//dataclass variabel yang menyimpan data input form
data class MahasiswaEvent(
    val nim: String = "",
    val nama: String = "",
    val jenisKelamin: String = "",
    val alamat: String = "",
    val kelas: String = "",
    val angkatan:String= ""
)