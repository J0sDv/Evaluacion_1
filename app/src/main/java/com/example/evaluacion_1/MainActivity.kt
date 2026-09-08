package com.example.evaluacion_1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evaluacion_1.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val sistema = SistemaLavExpress()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            ejecutarPruebas()
        }
    }

    private fun escribirConsola(mensaje: String) {
        binding.textViewConsole.append("$mensaje\n")
        binding.scrollView.post {
            binding.scrollView.fullScroll(android.view.View.FOCUS_DOWN)
        }
    }

    private suspend fun ejecutarPruebas() {
        escribirConsola("SISTEMA DE CONSOLA LAVEXPRESS")

        // Formato de código inválido
        escribirConsola("\n--- Prueba Error: Codigo Invalido '123ABC' ---")
        try {
            val maquinaInvalida = Lavadora("123ABC", "Samsung Error", TipoUsuario.REGULAR)
            sistema.registrarEntrada(maquinaInvalida) { escribirConsola(it) }
        } catch (e: LavExpressException) {
            escribirConsola("CONTROL DE ERROR R6: ${e.message}")
        }

        // REGISTRO DE ENTRADAS CON DATOS
        escribirConsola("\n--- Registrando Entradas ---")
        val m1 = Lavadora("LV12CD", "Samsung WW90", TipoUsuario.SUSCRIPTOR)
        val m2 = Lavadora("LV99ZA", "LG F4WV509", TipoUsuario.REGULAR)
        val m3 = Secadora("SC22TO", "Bosch WTH85200", TipoUsuario.REGULAR)
        val m4 = LavasecaIndustrial("LI44RG", "Miele PW6", TipoUsuario.EMPRESA, conVapor = true)
        val m5 = LavasecaIndustrial("LI77RG", "Speed Queen SF7", TipoUsuario.REGULAR, conVapor = false)

        try {
            sistema.registrarEntrada(m1) { escribirConsola(it) }
            sistema.registrarEntrada(m2) { escribirConsola(it) }
            sistema.registrarEntrada(m3) { escribirConsola(it) }
            sistema.registrarEntrada(m4) { escribirConsola(it) }
            sistema.registrarEntrada(m5) { escribirConsola(it) }
        } catch (e: LavExpressException) {
            escribirConsola("CONTROL DE ERROR: ${e.message}")
        }

        // REGISTRO DE SALIDAS
        escribirConsola("\n--- Registrando Salidas ---")
        try {
            sistema.registrarSalida("LV12CD", 75) { escribirConsola(it) }
            sistema.registrarSalida("LV99ZA", 180) { escribirConsola(it) }
            sistema.registrarSalida("SC22TO", 25) { escribirConsola(it) }
            sistema.registrarSalida("LI44RG", 120) { escribirConsola(it) }
            sistema.registrarSalida("LI77RG", 45) { escribirConsola(it) }
        } catch (e: LavExpressException) {
            escribirConsola("CONTROL DE ERROR: ${e.message}")
        }

        // Salida de maquina no registrada
        escribirConsola("\n--- Prueba Error: Maquina No Encontrada '14' ---")
        try {
            sistema.registrarSalida("67", 50) { escribirConsola(it) }
        } catch (e: LavExpressException) {
            escribirConsola("CONTROL DE ERROR: ${e.message}")
        }

        // REPORTE DE CIERRE DE TURNO
        escribirConsola("\n" + sistema.generarReporteCierre())
    }
}