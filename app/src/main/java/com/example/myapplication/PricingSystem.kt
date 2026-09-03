package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf

data class PricingTier(
    val minFee: Int,
    val maxFee: Int,
    var serviceFee: Int
)

object ServiceChargeEngine {
    val tiers = mutableStateListOf(
        PricingTier(0, 199, 50),
        PricingTier(200, 499, 100),
        PricingTier(500, 999, 200),
        PricingTier(1000, Int.MAX_VALUE, 250)
    )

    fun calculateServiceFee(officialFee: Int): Int {
        val tier = tiers.find { officialFee in it.minFee..it.maxFee }
        return tier?.serviceFee ?: 250 // Fallback to max tier
    }
    
    fun updateTierFee(index: Int, newFee: Int) {
        if (index in tiers.indices) {
            tiers[index] = tiers[index].copy(serviceFee = newFee)
        }
    }
}
