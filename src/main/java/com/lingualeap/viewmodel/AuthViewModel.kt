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
 * ViewModel encargado de la lógica de autenticación (Login y Registro).
 */
class AuthViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    /**
     * Intenta iniciar sesión con el correo y contraseña proporcionados.
     */
    fun login(email: String, password: String) {
        val emailError    = validateEmail(email)
        val passwordError = validatePassword(password)

        if (emailError != null || passwordError != null) {
            _authState.value = AuthState.Error(emailError ?: passwordError!!)
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1000)
            val demoUser = User(
                id             = "user_001",
                name           = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email          = email,
                streakDays     = 3,
                totalXp        = 120,
                avatarInitials = email.take(2).uppercase()
            )
            _currentUser.value = demoUser
            _authState.value   = AuthState.Success(demoUser)
        }
    }

    /**
     * Registra un nuevo usuario validando los campos.
     */
    fun register(name: String, email: String, password: String) {
        when {
            name.trim().length < 2 -> {
                _authState.value = AuthState.Error("El nombre debe tener al menos 2 caracteres")
                return
            }
            validateEmail(email) != null -> {
                _authState.value = AuthState.Error(validateEmail(email)!!)
                return
            }
            validatePassword(password) != null -> {
                _authState.value = AuthState.Error(validatePassword(password)!!)
                return
            }
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            delay(1000)
            val newUser = User(
                id             = "user_${System.currentTimeMillis()}",
                name           = name.trim(),
                email          = email.trim(),
                streakDays     = 0,
                totalXp        = 0,
                avatarInitials = name.trim().take(2).uppercase()
            )
            _currentUser.value = newUser
            _authState.value   = AuthState.Success(newUser)
        }
    }

    fun updateUserLanguage(language: Language) {
        val user = _currentUser.value ?: return
        _currentUser.value = user.copy(selectedLang = language)
    }

    fun logout() {
        _currentUser.value = null
        _authState.value   = AuthState.Idle
    }

    /**
     * Limpia el estado de error actual. Se llama usualmente cuando el usuario
     * comienza a escribir de nuevo para "redibujar" la UI sin el mensaje de error.
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    /**
     * Valida si el correo tiene un formato correcto y es dominio @gmail.com.
     * REVISIÓN: Ahora solo acepta correos de Gmail.
     */
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El correo es obligatorio"
            !email.endsWith("@gmail.com") -> "Solo se permiten correos de Gmail (@gmail.com)"
            else -> null
        }
    }

    /**
     * Valida la longitud de la contraseña.
     */
    private fun validatePassword(password: String): String? {
        return if (password.length >= 6) null else "Mínimo 6 caracteres"
    }
}
