package com.example.evaluacion_1

sealed class EstadoSlot {
    object Libre : EstadoSlot()
    data class EnUso(val maquina: Maquina) : EstadoSlot()
    data class EnCicloFinal(val motivo: String) : EstadoSlot()
    data class FueraDeServicio(val motivo: String) : EstadoSlot()
}

data class Slot(
    val numero: Int,
    var estado: EstadoSlot = EstadoSlot.Libre
)