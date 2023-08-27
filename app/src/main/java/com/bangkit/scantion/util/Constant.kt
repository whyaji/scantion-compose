package com.bangkit.scantion.util

import com.bangkit.scantion.model.SkinCase

object Constants {
    const val TABLE_NAME = "skin_cases"
    const val DATABASE_NAME = "skin_cases_database"

    fun List<SkinCase>?.orPlaceHolderList(userId: String): List<SkinCase> {
        fun placeHolderList(): List<SkinCase> {
            return listOf(SkinCase(id = "empty", userId = "Tidak ada riwayat pemeriksaan", "", "Silahkan periksa terlebih dahulu", "", "", "", 0f))
        }

        val list = this?.filter {  it.userId == userId }

        return if (!list.isNullOrEmpty()){
            list
        } else placeHolderList()
    }
    val skinCaseDetailPlaceHolder = SkinCase(id = "empty", userId = "Tidak ada riwayat pemeriksaan", "", "Silahkan periksa terlebih dahulu", "", "", "", 0f)
}