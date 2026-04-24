package com.lingualeap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingualeap.data.model.AuthState
import com.lingualeap.data.model.Language
import com.lingualeap.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel: Gestión de Autenticación y Perfil
 * 🟢 MEJORA DE EXCELENCIA: Renombrado a español y validaciones honestas.
 */
class AuthViewModel : ViewModel() {

    private val _estadoAuth = MutableStateFlow<AuthState>(AuthState.Idle)
    val estadoAuth: StateFlow<AuthState> = _estadoAuth.asStateFlow()

    private val _usuarioActual = MutableStateFlow<User?>(null)
    val usuarioActual: StateFlow<User?> = _usuarioActual.asStateFlow()

    fun login(correo: String, contrasena: String) {
        val errorCorreo = validarCorreo(correo)
        val errorPass = validarContrasena(contrasena)

        if (errorCorreo != null || errorPass != null) {
            _estadoAuth.value = AuthState.Error(errorCorreo ?: errorPass!!)
            return
        }

        viewModelScope.launch {
            _estadoAuth.value = AuthState.Loading
            delay(1000)
            val usuarioDemo = User(
                id = "user_001",
                name = correo.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = correo,
                streakDays = 3,
                totalXp = 120,
                avatarInitials = correo.take(2).uppercase()
            )
            _usuarioActual.value = usuarioDemo
            _estadoAuth.value = AuthState.Success(usuarioDemo)
        }
    }

    fun registrar(nombre: String, correo: String, contrasena: String) {
        if (nombre.trim().length < 2) {
            _estadoAuth.value = AuthState.Error("Nombre demasiado corto")
            return
        }
        val errorCorreo = validarCorreo(correo)
        if (errorCorreo != null) {
            _estadoAuth.value = AuthState.Error(errorCorreo)
            return
        }

        viewModelScope.launch {
            _estadoAuth.value = AuthState.Loading
            delay(1000)
            val nuevoUsuario = User(
                id = "user_${System.currentTimeMillis()}",
                name = nombre.trim(),
                email = correo.trim(),
                streakDays = 0,
                totalXp = 0,
                avatarInitials = nombre.trim().take(2).uppercase()
            )
            _usuarioActual.value = nuevoUsuario
            _estadoAuth.value = AuthState.Success(nuevoUsuario)
        }
    }

    fun actualizarIdiomaUsuario(idioma: Language) {
        val usuario = _usuarioActual.value ?: return
        _usuarioActual.value = usuario.copy(selectedLang = idioma)
    }

    fun sumarXp(puntos: Int) {
        val usuario = _usuarioActual.value ?: return
        _usuarioActual.value = usuario.copy(totalXp = usuario.totalXp + puntos)
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
        _estadoAuth.value = AuthState.Idle
    }

    fun limpiarError() {
        if (_estadoAuth.value is AuthState.Error) {
            _estadoAuth.value = AuthState.Idle
        }
    }

    private fun validarCorreo(correo: String): String? {
        return when {
            correo.isBlank() -> "El correo es obligatorio"
            !correo.contains("@") || !correo.contains(".") -> "Formato de correo inválido"
            else -> null
        }
    }

    private fun validarContrasena(pass: String): String? {
        return if (pass.length >= 6) null else "Mínimo 6 caracteres"
    }
}
