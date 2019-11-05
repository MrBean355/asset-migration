package com.android.assets

/** A method of receiving asset mappings. */
interface AssetMapping {
    fun getAllMappings(): Map<String, String>
}

class TestAssetMapping : AssetMapping {

    override fun getAllMappings(): Map<String, String> {
        return mapOf(
                "app_icon" to "app_logo_primary",
                "icn_alert" to "icn_alert_grey",
                "icn_payment" to "icn_payment_white"
        )
    }
}
