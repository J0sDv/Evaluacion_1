package com.example.evaluacion_1

import java.time.LocalDateTime

//Clase Usuario
enum class TipoUsuario {
    REGULAR, SUSCRIPTOR, EMPRESA
}

// Clase base de Maquinas
abstract class Maquina(
    val codigo: String,
    val marcaModelo: String,
    val fechaIngreso: LocalDateTime = LocalDateTime.now(),
    val tipoUsuario: TipoUsuario
) {
    abstract fun calcularCostoBase(tiempoMinutos: Int): Double
}

// Subclases
class Lavadora(
    codigo: String,
    marcaModelo: String,
    tipoUsuario: TipoUsuario
) : Maquina(codigo, marcaModelo, tipoUsuario = tipoUsuario) {

    override fun calcularCostoBase(tiempoMinutos: Int): Double {
        var horas = tiempoMinutos / 60.0
        if (tipoUsuario == TipoUsuario.SUSCRIPTOR) {
            horas *= 0.80
        }
        return horas * 1200.0
    }
}

class Secadora(
    codigo: String,
    marcaModelo: String,
    tipoUsuario: TipoUsuario
) : Maquina(codigo, marcaModelo, tipoUsuario = tipoUsuario) {

    override fun calcularCostoBase(tiempoMinutos: Int): Double {
        if (tiempoMinutos < 30) return 0.0
        val horas = tiempoMinutos / 60.0
        return horas * 1000.0
    }
}

class LavasecaIndustrial(
    codigo: String,
    marcaModelo: String,
    tipoUsuario: TipoUsuario,
    val conVapor: Boolean
) : Maquina(codigo, marcaModelo, tipoUsuario = tipoUsuario) {

    override fun calcularCostoBase(tiempoMinutos: Int): Double {
        val horas = tiempoMinutos / 60.0
        var total = horas * 2800.0
        if (conVapor) {
            total *= 1.30
        }
        return total
    }
}
