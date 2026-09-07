package com.gcr.android.inventorytest.operaciones


object FunGen {
    @JvmStatic
    fun ConvertDoubleStr(cad: String): Double {
        var cad = cad
        cad = cad.trim { it <= ' ' }
        if (cad.isEmpty()) {
            return 0.0
        } else {
            try {
                return cad.toDouble()
            } catch (e: Exception) {
                return 0.0
            }
        }
    }

    fun ConvertIntStr(cad: String): Int {
        var cad = cad
        cad = cad.trim { it <= ' ' }
        if (cad == null || cad.trim { it <= ' ' }.isEmpty()) {
            return 0
        } else {
            try {
                return cad.toInt()
            } catch (e: Exception) {
                return 0
            }
        }
    }

    fun ConvertFloatStr(cad: String): Float {
        var cad = cad
        cad = cad.trim { it <= ' ' }
        if (cad.trim { it <= ' ' }.isEmpty()) {
            return 0.0f
        } else {
            try {
                return cad.toFloat()
            } catch (e: Exception) {
                return 0.0f
            }
        }
    }

    fun CadeToInt(cad: String): Int {
        var cad = cad
        cad = cad.trim { it <= ' ' }
        if (cad == null || cad.trim { it <= ' ' }.isEmpty()) {
            return 0
        } else {
            try {
                return cad.toInt()
            } catch (e: Exception) {
                return 0
            }
        }
    }
}