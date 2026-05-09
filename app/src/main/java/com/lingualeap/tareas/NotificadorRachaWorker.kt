package com.lingualeap.tareas

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lingualeap.data.model.User
import com.lingualeap.util.NotificacionesHelper
import kotlinx.coroutines.tasks.await
import java.util.Calendar

/**
 * Trabajador en segundo plano que verifica si el usuario ha practicado hoy.
 * Si no lo ha hecho y tiene una racha activa, envía una notificación.
 */
class NotificadorRachaWorker(
    contexto: Context,
    parametros: WorkerParameters
) : CoroutineWorker(contexto, parametros) {

    override suspend fun doWork(): Result {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid ?: return Result.success()

        try {
            val documento = db.collection("users").document(userId).get().await()
            val usuario = documento.toObject(User::class.java) ?: return Result.success()

            // Si tiene racha pero no ha practicado hoy, avisamos
            if (usuario.streakDays > 0 && !haPracticadoHoy(usuario.fechaUltimaLeccion)) {
                NotificacionesHelper.enviarNotificacionRachaEnPeligro(applicationContext, usuario.streakDays)
            }
        } catch (e: Exception) {
            return Result.retry()
        }

        return Result.success()
    }

    private fun haPracticadoHoy(ultimaVez: Long): Boolean {
        if (ultimaVez == 0L) return false
        val hoy = Calendar.getInstance()
        val fechaUltima = Calendar.getInstance().apply { timeInMillis = ultimaVez }
        
        return hoy.get(Calendar.YEAR) == fechaUltima.get(Calendar.YEAR) &&
               hoy.get(Calendar.DAY_OF_YEAR) == fechaUltima.get(Calendar.DAY_OF_YEAR)
    }
}
