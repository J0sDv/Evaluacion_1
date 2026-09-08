package com.example.evaluacion_1

import kotlinx.coroutines.delay

class SistemaLavExpress(val capacidadSlots: Int = 10) {

    val slots = List(capacidadSlots) { id -> Slot(numero = id + 1) }
    val historialTurno = mutableListOf<Ticket>()
    private var contadorTickets = 1

    //Operacion de Entrada
    suspend fun registrarEntrada(maquina: Maquina, log: (String) -> Unit) {
        CalculadoraTarifas.validarCodigo(maquina.codigo)

        val slotLibre = slots.find { it.estado is EstadoSlot.Libre }
            ?: throw CapacidadAgotadaException("Sistema sin capacidad para registrar la maquina ${maquina.codigo}.")

        slotLibre.estado = EstadoSlot.EnCicloFinal("Registrando entrada de maquina")
        log("Slot ${slotLibre.numero}: Conectando con sensor fisico...")
        delay(3000)

        slotLibre.estado = EstadoSlot.EnUso(maquina)
        log("EXITO: Maquina ${maquina.codigo} asignada al Slot ${slotLibre.numero}.")
    }

    //Operacion de Salida
    suspend fun registrarSalida(codigoMaquina: String, tiempoMinutos: Int, log: (String) -> Unit) {
        val slot = slots.find {
            val estado = it.estado
            estado is EstadoSlot.EnUso && estado.maquina.codigo == codigoMaquina
        } ?: throw MaquinaNoEncontradaException("La maquina con codigo '$codigoMaquina' no se encuentra en ningun slot activo.")

        val maquina = (slot.estado as EstadoSlot.EnUso).maquina
        slot.estado = EstadoSlot.EnCicloFinal("Calculando tarifa de salida")
        log("Slot ${slot.numero}: Procesando salida y leyendo sensor...")
        delay(6500)

        try {
            val montoTotal = CalculadoraTarifas.calcularMontoTotal(maquina, tiempoMinutos)

            val ticket = Ticket(
                numeroTicket = contadorTickets++,
                maquina = maquina,
                tiempoMinutos = tiempoMinutos,
                montoPagado = montoTotal
            )

            historialTurno.add(ticket)
            slot.estado = EstadoSlot.Libre
            log("TICKET EMITIDO [#${ticket.numeroTicket}]: Codigo ${maquina.codigo} | Tiempo: ${tiempoMinutos}m | Cobro: $${String.format("%.2f", montoTotal)}")

        } catch (e: TarifaInvalidaException) {
            slot.estado = EstadoSlot.EnUso(maquina)
            throw e
        }
    }

    // Consultas de Negocio
    fun obtenerSlotsDisponibles() = slots.count { it.estado is EstadoSlot.Libre }

    fun obtenerClientesSuscriptores() = historialTurno
        .map { it.maquina }
        .filter { it.tipoUsuario == TipoUsuario.SUSCRIPTOR }

    fun obtenerIngresoPromedio() = if (historialTurno.isNotEmpty()) {
        historialTurno.sumOf { it.montoPagado } / historialTurno.size
    } else 0.0

    fun obtenerCodigosFinalizados() = historialTurno.map { it.maquina.codigo }

    fun obtenerMaquinaMasTiempo() = historialTurno.maxByOrNull { it.tiempoMinutos }

    fun generarReporteCierre(): String {
        val sb = StringBuilder()
        sb.append("REPORTE DE CIERRE DE TURNO ------------\n")

        historialTurno.forEach { t ->
            val tipoStr = t.maquina.javaClass.simpleName
            sb.append("* Ticket #${t.numeroTicket} | Tipo: $tipoStr | Codigo: ${t.maquina.codigo} | Tiempo: ${t.tiempoMinutos}m | Cobrado: $${String.format("%.2f", t.montoPagado)}\n")
        }

        val totalRecaudado = historialTurno.sumOf { it.montoPagado }
        val tipoMasIngresos = historialTurno
            .groupBy { it.maquina.javaClass.simpleName }
            .maxByOrNull { entry -> entry.value.sumOf { it.montoPagado } }?.key ?: "N/A"

        sb.append("\n---RESUMEN DEL TURNO---\n")
        sb.append("Total Recaudado: $${String.format("%.2f", totalRecaudado)}\n")
        sb.append("Maquinas Atendidas: ${historialTurno.size}\n")
        sb.append("Ingreso Promedio: $${String.format("%.2f", obtenerIngresoPromedio())}\n")
        sb.append("Tipo de Maquina con Mayor Ingreso: $tipoMasIngresos\n")
        sb.append("Slots Disponibles al Cierre: ${obtenerSlotsDisponibles()}\n")
        return sb.toString()
    }
}