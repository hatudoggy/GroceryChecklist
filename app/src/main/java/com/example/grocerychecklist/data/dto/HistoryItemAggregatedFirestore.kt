package com.example.grocerychecklist.data.dto

import com.example.grocerychecklist.data.mapper.HistoryItemAggregated
import com.google.firebase.firestore.PropertyName

data class HistoryItemAggregatedFirestore(
    @get:PropertyName("sumOfPrice") @set:PropertyName("sumOfPrice")
    var sumOfPrice: Double = 0.0,

    @get:PropertyName("totalItems") @set:PropertyName("totalItems")
    var totalItems: Int = 0,

    @get:PropertyName("category") @set:PropertyName("category")
    var category: String = ""
) {
    fun toDomainModel(): HistoryItemAggregated {
        return HistoryItemAggregated(
            sumOfPrice = sumOfPrice,
            totalItems = totalItems,
            category = category
        )
    }

    companion object {
        fun fromDomainModel(domainModel: HistoryItemAggregated): HistoryItemAggregatedFirestore {
            return HistoryItemAggregatedFirestore(
                sumOfPrice = domainModel.sumOfPrice,
                totalItems = domainModel.totalItems,
                category = domainModel.category
            )
        }
    }
}