package com.bangkit.scantion.data

import com.bangkit.scantion.model.SkinCase
import kotlin.random.Random

object DataDummy {

    fun generateDummyCaseEntity(): List<SkinCase> {
        val skinCases = ArrayList<SkinCase>()
        for (i in 1 .. 8) {
            val skinCase = SkinCase(
                userId = "202010370311197",
                photoUri = "https://storage.googleapis.com/kaggle-datasets-images/2035877/3376422/eefe34f4ff71025fced98dfcf6979b39/dataset-card.jpg?t=2022-03-29-11-56-39",
                bodyPart = "Tangan",
                howLong = "$i September 2021",
                symptom = "Gatal",
                cancerType = listOf("Melanoma", "Benign", "Normal").random(),
                accuracy = Random.nextFloat(),
                dateCreated = "2023-10-0$i 01:02:03"
            )
            skinCases.add(skinCase)
        }
        return skinCases
    }
}