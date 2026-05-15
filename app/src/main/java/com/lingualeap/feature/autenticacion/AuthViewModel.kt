package com.lingualeap.feature.autenticacion

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lingualeap.data.ThemeManager
import com.lingualeap.data.model.AppData
import com.lingualeap.data.model.AuthState
import com.lingualeap.data.model.Language
import com.lingualeap.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * ViewModel: Gestión de Autenticación, Perfil y Gamificación (Rachas y Desafíos)
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val themeManager = ThemeManager(application)

    private val _estadoAuth = MutableStateFlow<AuthState>(AuthState.Idle)
    val estadoAuth: StateFlow<AuthState> = _estadoAuth.asStateFlow()

    private val _usuarioActual = MutableStateFlow<User?>(null)
    val usuarioActual: StateFlow<User?> = _usuarioActual.asStateFlow()

    private val _onboardingFinalizado = MutableStateFlow(false)
    val onboardingFinalizado: StateFlow<Boolean> = _onboardingFinalizado.asStateFlow()

    private val _cargandoOnboarding = MutableStateFlow(false)
    val cargandoOnboarding: StateFlow<Boolean> = _cargandoOnboarding.asStateFlow()

    private val _rankingUsuarios = MutableStateFlow<List<User>>(emptyList())
    val rankingUsuarios: StateFlow<List<User>> = _rankingUsuarios.asStateFlow()

    private val _rankingAmigos = MutableStateFlow<List<User>>(emptyList())
    val rankingAmigos: StateFlow<List<User>> = _rankingAmigos.asStateFlow()

    private val _resultadosBusqueda = MutableStateFlow<List<User>>(emptyList())
    val resultadosBusqueda: StateFlow<List<User>> = _resultadosBusqueda.asStateFlow()

    init {
        val userFirebase = auth.currentUser
        if (userFirebase != null) {
            cargarPerfilUsuario(userFirebase.uid)
        }
    }

    fun login(correo: String, contrasena: String) {
        viewModelScope.launch {
            _estadoAuth.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(correo, contrasena).await()
                result.user?.let { cargarPerfilUsuario(it.uid) }
            } catch (e: Exception) {
                _estadoAuth.value = AuthState.Error("Error al entrar: Revisa tus credenciales.")
            }
        }
    }

    fun registrar(nombre: String, correo: String, contrasena: String) {
        viewModelScope.launch {
            _estadoAuth.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(correo, contrasena).await()
                val uid = result.user?.uid ?: ""
                
                val nuevoUsuario = User(
                    id = uid,
                    name = nombre.trim(),
                    email = correo.trim(),
                    avatarInitials = nombre.trim().take(2).uppercase()
                )

                try {
                    db.collection("users").document(uid).set(nuevoUsuario).await()
                    _usuarioActual.value = nuevoUsuario
                    _estadoAuth.value = AuthState.Success(nuevoUsuario)
                } catch (firestoreEx: Exception) {
                    Log.e("AuthViewModel", "Error Firestore", firestoreEx)
                    _estadoAuth.value = AuthState.Error("Usuario creado, pero hubo un error al guardar el perfil.")
                }

            } catch (e: Exception) {
                val mensajeError = when (e) {
                    is FirebaseAuthUserCollisionException -> "Este correo electrónico ya está registrado."
                    else -> "No se pudo crear la cuenta: ${e.localizedMessage}"
                }
                _estadoAuth.value = AuthState.Error(mensajeError)
            }
        }
    }

    fun loginConGoogle(idToken: String) {
        viewModelScope.launch {
            _estadoAuth.value = AuthState.Loading
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val uid = firebaseUser.uid
                    val doc = db.collection("users").document(uid).get().await()
                    
                    if (!doc.exists()) {
                        val nuevoUsuario = User(
                            id = uid,
                            name = firebaseUser.displayName ?: "Usuario Google",
                            email = firebaseUser.email ?: "",
                            avatarInitials = (firebaseUser.displayName ?: "G").take(2).uppercase(),
                            photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                        )
                        db.collection("users").document(uid).set(nuevoUsuario).await()
                        _usuarioActual.value = nuevoUsuario
                        _estadoAuth.value = AuthState.Success(nuevoUsuario)
                    } else {
                        actualizarEstadoConPerfil(uid)
                    }
                }
            } catch (e: Exception) {
                _estadoAuth.value = AuthState.Error("Error de autenticación con Google.")
            }
        }
    }

    private suspend fun actualizarEstadoConPerfil(uid: String) {
        try {
            val document = db.collection("users").document(uid).get().await()
            val usuario = document.toObject(User::class.java)
            if (usuario != null) {
                _usuarioActual.value = usuario
                _estadoAuth.value = AuthState.Success(usuario)
                
                // Sincronizar preferencia de tema localmente
                themeManager.saveDarkMode(usuario.darkModeEnabled)
                
                verificarYReiniciarRachaSiEsNecesario(usuario)
                cargarRanking()
                cargarRankingAmigos()
            } else {
                _estadoAuth.value = AuthState.Error("Perfil no encontrado.")
            }
        } catch (e: Exception) {
            _estadoAuth.value = AuthState.Error("Error al cargar perfil.")
        }
    }

    private fun cargarPerfilUsuario(uid: String) {
        viewModelScope.launch {
            actualizarEstadoConPerfil(uid)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            themeManager.saveDarkMode(enabled)
            actualizarCampoUsuario("darkModeEnabled", enabled) { copy(darkModeEnabled = enabled) }
        }
    }

    fun actualizarNombre(nuevoNombre: String) {
        val usuario = _usuarioActual.value ?: return
        viewModelScope.launch {
            try {
                val iniciales = nuevoNombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.take(1).uppercase() }
                val updates = mapOf("name" to nuevoNombre, "avatarInitials" to iniciales)
                db.collection("users").document(usuario.id).update(updates).await()
                _usuarioActual.value = usuario.copy(name = nuevoNombre, avatarInitials = iniciales)
            } catch (e: Exception) { Log.e("AuthViewModel", "Error al actualizar nombre", e) }
        }
    }

    fun actualizarMetaDiaria(xp: Int) {
        val usuario = _usuarioActual.value ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(usuario.id).update("dailyGoalXp", xp).await()
                _usuarioActual.value = usuario.copy(dailyGoalXp = xp)
            } catch (e: Exception) { Log.e("AuthViewModel", "Error meta diaria", e) }
        }
    }

    fun cambiarContrasena(nueva: String) {
        viewModelScope.launch {
            try {
                auth.currentUser?.updatePassword(nueva)?.await()
            } catch (e: Exception) {
                _estadoAuth.value = AuthState.Error("No se pudo cambiar la contraseña.")
            }
        }
    }

    fun toggleNotificaciones(enabled: Boolean) {
        actualizarCampoUsuario("notificationsEnabled", enabled) { copy(notificationsEnabled = enabled) }
    }

    fun toggleStreakNotifications(enabled: Boolean) {
        actualizarCampoUsuario("streakNotificationsEnabled", enabled) { copy(streakNotificationsEnabled = enabled) }
    }

    fun toggleSounds(enabled: Boolean) {
        actualizarCampoUsuario("soundsEnabled", enabled) { copy(soundsEnabled = enabled) }
    }

    fun toggleEmailNotifications(enabled: Boolean) {
        actualizarCampoUsuario("emailNotificationsEnabled", enabled) { copy(emailNotificationsEnabled = enabled) }
    }

    fun toggleShowTranslations(enabled: Boolean) {
        actualizarCampoUsuario("showTranslations", enabled) { copy(showTranslations = enabled) }
    }

    fun toggleShowAiChatIdeas(enabled: Boolean) {
        actualizarCampoUsuario("showAiChatIdeas", enabled) { copy(showAiChatIdeas = enabled) }
    }

    fun updateChatTextSize(size: Float) {
        actualizarCampoUsuario("chatTextSize", size) { copy(chatTextSize = size) }
    }

    private fun actualizarCampoUsuario(campo: String, valor: Any, localUpdate: User.() -> User) {
        val usuario = _usuarioActual.value ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(usuario.id).update(campo, valor).await()
                _usuarioActual.value = usuario.localUpdate()
            } catch (e: Exception) { Log.e("AuthViewModel", "Error actualizando $campo", e) }
        }
    }

    fun marcarLeccionCompletada(lessonId: Int, xp: Int) {
        val usuario = _usuarioActual.value ?: return
        viewModelScope.launch {
            try {
                val yaEstabaCompletada = usuario.completedLessons.contains(lessonId)
                val userRef = db.collection("users").document(usuario.id)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val xpActual = snapshot.getLong("totalXp") ?: 0L
                    if (!yaEstabaCompletada) {
                        transaction.update(userRef, "completedLessons", FieldValue.arrayUnion(lessonId))
                        transaction.update(userRef, "totalXp", xpActual + xp)
                    }
                }.await()
                actualizarRachaTrasCompletarLeccion(usuario)
                val docActualizado = db.collection("users").document(usuario.id).get().await()
                val usuarioNuevo = docActualizado.toObject(User::class.java)
                _usuarioActual.value = usuarioNuevo
                usuarioNuevo?.let { verificarYCompletarDesafios(it) }
            } catch (e: Exception) { Log.e("AuthViewModel", "Error lección", e) }
        }
    }

    private fun verificarYCompletarDesafios(usuario: User) {
        viewModelScope.launch {
            val desafiosNuevos = mutableListOf<String>()
            var xpGanadaPorDesafios = 0
            AppData.listaDesafios.forEach { desafio ->
                if (!usuario.desafiosCompletados.contains(desafio.id)) {
                    val seCumple = when(desafio.id) {
                        "primer_paso" -> usuario.completedLessons.isNotEmpty()
                        "racha_3" -> usuario.streakDays >= 3
                        "maestro_xp" -> usuario.totalXp >= 100
                        else -> false
                    }
                    if (seCumple) {
                        desafiosNuevos.add(desafio.id)
                        xpGanadaPorDesafios += desafio.recompensaXp
                    }
                }
            }
            if (desafiosNuevos.isNotEmpty()) {
                val userRef = db.collection("users").document(usuario.id)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val xpActual = snapshot.getLong("totalXp") ?: 0L
                    transaction.update(userRef, "desafiosCompletados", FieldValue.arrayUnion(*desafiosNuevos.toTypedArray()))
                    transaction.update(userRef, "totalXp", xpActual + xpGanadaPorDesafios)
                }.await()
                val docFinal = db.collection("users").document(usuario.id).get().await()
                _usuarioActual.value = docFinal.toObject(User::class.java)
            }
        }
    }

    private suspend fun actualizarRachaTrasCompletarLeccion(usuario: User) {
        val momentoActual = System.currentTimeMillis()
        val ultimaVez = usuario.fechaUltimaLeccion
        val hoy = Calendar.getInstance()
        val fechaUltima = Calendar.getInstance().apply { timeInMillis = ultimaVez }
        val esMismoDia = hoy.get(Calendar.YEAR) == fechaUltima.get(Calendar.YEAR) && hoy.get(Calendar.DAY_OF_YEAR) == fechaUltima.get(Calendar.DAY_OF_YEAR)
        if (ultimaVez == 0L || !esMismoDia) {
            val diferenciaDias = if (ultimaVez == 0L) 0L else TimeUnit.MILLISECONDS.toDays(momentoActual - ultimaVez)
            val nuevosDias = if (diferenciaDias <= 1) usuario.streakDays + 1 else 1
            val actualizaciones = mapOf("streakDays" to nuevosDias, "fechaUltimaLeccion" to momentoActual, "activityHistory" to FieldValue.arrayUnion(momentoActual))
            db.collection("users").document(usuario.id).update(actualizaciones).await()
        }
    }

    private fun verificarYReiniciarRachaSiEsNecesario(usuario: User) {
        if (usuario.fechaUltimaLeccion == 0L) return
        val diferenciaDias = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - usuario.fechaUltimaLeccion)
        if (diferenciaDias > 1) {
            viewModelScope.launch {
                db.collection("users").document(usuario.id).update("streakDays", 0).await()
                _usuarioActual.value = _usuarioActual.value?.copy(streakDays = 0)
            }
        }
    }

    fun cargarRanking() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").orderBy("totalXp", Query.Direction.DESCENDING).limit(20).get().await()
                _rankingUsuarios.value = snapshot.toObjects(User::class.java)
            } catch (e: Exception) { Log.e("AuthViewModel", "Error ranking", e) }
        }
    }

    fun cargarRankingAmigos() {
        val usuario = _usuarioActual.value ?: return
        if (usuario.friendsIds.isEmpty()) {
            _rankingAmigos.value = listOf(usuario)
            return
        }
        
        viewModelScope.launch {
            try {
                // Incluimos al usuario actual y a sus amigos
                val idsParaConsultar = usuario.friendsIds + usuario.id
                val snapshot = db.collection("users")
                    .whereIn("id", idsParaConsultar)
                    .get().await()
                
                val amigos = snapshot.toObjects(User::class.java)
                    .sortedByDescending { it.totalXp }
                
                _rankingAmigos.value = amigos
            } catch (e: Exception) { 
                Log.e("AuthViewModel", "Error ranking amigos", e)
                _rankingAmigos.value = listOf(usuario)
            }
        }
    }

    fun agregarAmigo(amigoId: String) {
        val usuario = _usuarioActual.value ?: return
        if (usuario.friendsIds.contains(amigoId)) return
        
        viewModelScope.launch {
            try {
                db.collection("users").document(usuario.id)
                    .update("friendsIds", FieldValue.arrayUnion(amigoId)).await()
                
                val nuevaLista = usuario.friendsIds + amigoId
                _usuarioActual.value = usuario.copy(friendsIds = nuevaLista)
                cargarRankingAmigos()
            } catch (e: Exception) { Log.e("AuthViewModel", "Error agregar amigo", e) }
        }
    }

    fun buscarUsuarios(query: String) {
        if (query.isBlank()) {
            _resultadosBusqueda.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users")
                    .orderBy("name")
                    .startAt(query)
                    .endAt(query + "\uf8ff")
                    .limit(10)
                    .get().await()
                
                val result = snapshot.toObjects(User::class.java).filter { it.id != _usuarioActual.value?.id }
                _resultadosBusqueda.value = result
            } catch (e: Exception) { Log.e("AuthViewModel", "Error buscando", e) }
        }
    }

    fun limpiarBusqueda() {
        _resultadosBusqueda.value = emptyList()
    }

    fun cerrarSesion() { auth.signOut(); _usuarioActual.value = null; _estadoAuth.value = AuthState.Idle }
    fun limpiarError() { if (_estadoAuth.value is AuthState.Error) _estadoAuth.value = AuthState.Idle }

    fun mostrarErrorManual(mensaje: String) {
        _estadoAuth.value = AuthState.Error(mensaje)
    }
    
    fun toggleFavoriteWord(word: String) {
        val usuario = _usuarioActual.value ?: return
        val isFavorite = usuario.favoriteWordsIds.contains(word)
        viewModelScope.launch {
            val op = if (isFavorite) FieldValue.arrayRemove(word) else FieldValue.arrayUnion(word)
            db.collection("users").document(usuario.id).update("favoriteWordsIds", op).await()
            val nuevaLista = if (isFavorite) usuario.favoriteWordsIds - word else usuario.favoriteWordsIds + word
            _usuarioActual.value = usuario.copy(favoriteWordsIds = nuevaLista)
        }
    }
    
    fun registrarErrorEnPregunta(id: Int) {
        val usuario = _usuarioActual.value ?: return
        if (usuario.wrongQuestionsIds.contains(id)) return
        viewModelScope.launch {
            db.collection("users").document(usuario.id).update("wrongQuestionsIds", FieldValue.arrayUnion(id)).await()
            _usuarioActual.value = usuario.copy(wrongQuestionsIds = usuario.wrongQuestionsIds + id)
        }
    }
    
    fun eliminarErrorDePregunta(id: Int) {
        val usuario = _usuarioActual.value ?: return
        viewModelScope.launch {
            db.collection("users").document(usuario.id).update("wrongQuestionsIds", FieldValue.arrayRemove(id)).await()
            _usuarioActual.value = usuario.copy(wrongQuestionsIds = usuario.wrongQuestionsIds - id)
        }
    }

    fun actualizarIdiomaUsuario(lang: Language) {
        val u = _usuarioActual.value ?: return
        viewModelScope.launch {
            db.collection("users").document(u.id).update("selectedLang", lang).await()
            _usuarioActual.value = u.copy(selectedLang = lang)
        }
    }

    fun actualizarIdiomaNativo(idioma: String) {
        val u = _usuarioActual.value ?: return
        viewModelScope.launch {
            try {
                db.collection("users").document(u.id).update("nativeLang", idioma).await()
                _usuarioActual.value = u.copy(nativeLang = idioma)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al actualizar idioma nativo", e)
            }
        }
    }

    fun completarOnboarding(
        country: String,
        region: String,
        nativeLang: String,
        learningLanguage: Language,
        level: String,
        name: String,
        birthDate: String,
        gender: String
    ) {
        val usuario = _usuarioActual.value ?: return
        viewModelScope.launch {
            _cargandoOnboarding.value = true
            try {
                val updates = mapOf(
                    "country" to country,
                    "region" to region,
                    "nativeLang" to nativeLang,
                    "selectedLang" to learningLanguage,
                    "learningLevel" to level,
                    "name" to name,
                    "birthDate" to birthDate,
                    "gender" to gender
                )
                db.collection("users").document(usuario.id).update(updates).await()
                
                _usuarioActual.value = usuario.copy(
                    country = country,
                    region = region,
                    nativeLang = nativeLang,
                    selectedLang = learningLanguage,
                    learningLevel = level,
                    name = name,
                    birthDate = birthDate,
                    gender = gender
                )
                _onboardingFinalizado.value = true
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error completando onboarding", e)
                _estadoAuth.value = AuthState.Error("Error al guardar tus preferencias.")
            } finally {
                _cargandoOnboarding.value = false
            }
        }
    }
}
