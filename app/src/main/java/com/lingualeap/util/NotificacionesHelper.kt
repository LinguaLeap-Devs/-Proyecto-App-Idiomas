package com.lingualeap.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.lingualeap.MainActivity
import com.lingualeap.R

/**
 * Ayudante para gestionar las notificaciones de la aplicación.
 */
object NotificacionesHelper {

    private const val CANAL_ID = "canal_racha_lingualeap"
    private const val CANAL_NOMBRE = "Recordatorios de Racha"
    private const val CANAL_DESC = "Notificaciones para mantener tu racha diaria activa"

    /**
     * Crea el canal de notificación necesario para Android 8.0 o superior.
     */
    fun crearCanalNotificacion(contexto: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(CANAL_ID, CANAL_NOMBRE, importancia).apply {
                description = CANAL_DESC
            }
            val notificationManager = contexto.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    /**
     * Envía una notificación al usuario avisándole que su racha está en peligro.
     */
    fun enviarNotificacionRachaEnPeligro(contexto: Context, diasRacha: Int) {
        val intent = Intent(contexto, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            contexto, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val constructor = NotificationCompat.Builder(contexto, CANAL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_chat) // Usamos uno del sistema temporalmente
            .setContentTitle("¡Tu racha de $diasRacha días corre peligro! 🔥")
            .setContentText("No dejes que se apague. Haz una lección de 5 minutos ahora.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = contexto.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(contexto, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        notificationManager.notify(1001, constructor.build())
    }
}
