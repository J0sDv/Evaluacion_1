package com.example.evaluacion_1

object CalculadoraTarifas {

    // Validar codigo de maquina: 2 letras, 2 dígitos, 2 letras
    fun validarCodigo(codigo: String) {
        if (!codigo.matches(Regex("^[A-Z]{2}\\d{2}[A-Z]{2}$"))) {
            throw CodigoInvalidoException("El codigo '$codigo' no cumple con el formato (2 letras, 2 dígitos, 2 letras).")
        }
    }

    // Calculo general de tarifas
    fun calcularMontoTotal(maquina: Maquina, tiempoMinutos: Int): Double {
        val costoBase = maquina.calcularCostoBase(tiempoMinutos)
        val costoConIva = costoBase * 1.19 // Aplicar 19% IVA

        val montoFinal = if (maquina.tipoUsuario == TipoUsuario.EMPRESA) {
            costoConIva * 0.50 // 50% de descuento sobre el monto con IVA
        } else {
            costoConIva
        }

        // Validacion de tarifa positiva
        if (montoFinal <= 0.0 && !(maquina is Secadora && tiempoMinutos < 30)) {
            throw TarifaInvalidaException("El calculo arrojo un monto invalido ($$montoFinal).")
        }

        return montoFinal
    }
}

data class Ticket(
    val numeroTicket: Int,
    val maquina: Maquina,
    val tiempoMinutos: Int,
    val montoPagado: Double
)