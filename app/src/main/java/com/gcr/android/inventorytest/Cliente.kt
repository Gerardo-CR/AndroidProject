package com.gcr.android.inventorytest


/**
 * Created by Gerardo Castillo on 13/09/2017.
 */
class Cliente(@JvmField val id: Int, var nombre: String?, var apellidos: String?) {
    var telefono: String? = null
    var correo: String? = null
    var direccion: String? = null

    @JvmOverloads
    constructor(Nombre: String?, Apellidos: String? = "") : this(0, Nombre, Apellidos)

    override fun toString(): String {
        return (this.nombre + ' ' + this.apellidos).trim { it <= ' ' }
    }
}
