package com.example.creditcardcontroller.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.creditcardcontroller.data.local.SettingsDataStore
import com.example.creditcardcontroller.data.local.dao.PresupuestoDao
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

class OnboardingViewModel(
    private val presupuestoDao: PresupuestoDao,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _ingresoMensual = MutableStateFlow("")
    val ingresoMensual: StateFlow<String> = _ingresoMensual.asStateFlow()

    private val _limiteUnPago = MutableStateFlow("")
    val limiteUnPago: StateFlow<String> = _limiteUnPago.asStateFlow()

    private val _limiteCuotas = MutableStateFlow("")
    val limiteCuotas: StateFlow<String> = _limiteCuotas.asStateFlow()

    private val _impuestoSellos = MutableStateFlow("1.2")
    val impuestoSellos: StateFlow<String> = _impuestoSellos.asStateFlow()

    fun updateIngreso(value: String) { _ingresoMensual.value = value }
    fun updateLimiteUnPago(value: String) { _limiteUnPago.value = value }
    fun updateLimiteCuotas(value: String) { _limiteCuotas.value = value }
    fun updateImpuestoSellos(value: String) { _impuestoSellos.value = value }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            val date = YearMonth.now()
            val ingreso = _ingresoMensual.value.toDoubleOrNull() ?: 0.0
            val limite1 = _limiteUnPago.value.toDoubleOrNull() ?: 0.0
            val limiteC = _limiteCuotas.value.toDoubleOrNull() ?: 0.0
            val tax = _impuestoSellos.value.toDoubleOrNull() ?: 1.2

            // Seed initial items with user values
            presupuestoDao.insert(
                PresupuestoEntity(
                    mes = date.monthValue, anio = date.year,
                    titulo = "Ingreso mensual", monto = ingreso, tipo = PresupuestoEntity.TIPO_INGRESO,
                    icono = "AddChart", color = "#4CAF50"
                )
            )
            presupuestoDao.insert(
                PresupuestoEntity(
                    mes = date.monthValue, anio = date.year,
                    titulo = "Gasto 1 cuota en tarjeta", monto = limite1, tipo = PresupuestoEntity.TIPO_LIMITE,
                    icono = "CreditCard", color = "#00BFA5"
                )
            )
            presupuestoDao.insert(
                PresupuestoEntity(
                    mes = date.monthValue, anio = date.year,
                    titulo = "Gasto mensual de tarjeta", monto = limiteC, tipo = PresupuestoEntity.TIPO_LIMITE,
                    icono = "AccountBalanceWallet", color = "#E57373"
                )
            )
            presupuestoDao.insert(
                PresupuestoEntity(
                    mes = date.monthValue, anio = date.year,
                    titulo = "Disponible", monto = 0.0, tipo = PresupuestoEntity.TIPO_AHORRO,
                    icono = "AccountBalance", color = "#FFC107"
                )
            )

            // Save preferences
            settingsDataStore.setStampTaxPercentage(tax)
            settingsDataStore.setOnboardingCompleted(true)
            
            onComplete()
        }
    }

    class Factory(
        private val presupuestoDao: PresupuestoDao,
        private val settingsDataStore: SettingsDataStore
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OnboardingViewModel(presupuestoDao, settingsDataStore) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
