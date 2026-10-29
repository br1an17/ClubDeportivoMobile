package com.example.clubdeportivo

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Properties
import javax.activation.DataHandler
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

 object EmailHelper {

    fun enviarCarnet(context: Context, destinatario: String, nombreSocio: String, bitmapQR: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val remitente = "tucorreo@gmail.com"
                val password = "tu_password_de_aplicacion"

                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(remitente, password)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(remitente))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario))
                    subject = "Tu Carnet Digital - Club Deportivo"

                    val cuerpoTexto = MimeBodyPart().apply {
                        setText("Hola $nombreSocio,\n\nAdjunto encontrarás tu carnet digital con el código QR de acceso al club.")
                    }

                    val multipart = MimeMultipart().apply {
                        addBodyPart(cuerpoTexto)
                    }

                    if (bitmapQR != null) {
                        val stream = ByteArrayOutputStream()
                        bitmapQR.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val bytes = stream.toByteArray()

                        val adjunto = MimeBodyPart().apply {
                            dataHandler = DataHandler(ByteArrayDataSource(bytes, "image/png"))
                            fileName = "carnet_qr.png"
                        }
                        multipart.addBodyPart(adjunto)
                    }

                    setContent(multipart)
                }

                Transport.send(message)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "¡Correo enviado con éxito a $destinatario!", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al enviar: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}