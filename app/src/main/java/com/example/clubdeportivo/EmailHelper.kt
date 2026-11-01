package com.example.clubdeportivo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

    fun enviarCarnet(context: Context, destinatario: String, nombreSocio: String,numAfiliado: String,
                     documento: String,
                     estado: String, bitmapQR: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val remitente = BuildConfig.MAIL_REMITENTE
                val password = BuildConfig.MAIL_PASSWORD

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
                }

                val multipart = MimeMultipart("releted")

                val cuerpoHtml = MimeBodyPart().apply {
                    val htmlContent = """
                        <div style="font-family: Arial, sans-serif; color: #333; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                            <h2 style="color: #2c3e50;">¡Hola, $nombreSocio!</h2>
                            <p>Te damos la bienvenida a <b>Club Deportivo</b>. Adjunto a este correo encontrarás tu carnet digital con el código QR de acceso.</p>
                            
                            <img src="cid:logo_club" alt="Logo Club" style="width: 100px; height: auto; margin-bottom: 10px; object-fit: contain;" />
                            
                            <div style="background-color: #f8f9fa; padding: 15px; border-radius: 6px; margin: 15px 0;">
                                <p style="margin: 5px 0;"><b>Número de Afiliado:</b> $numAfiliado</p>
                                <p style="margin: 5px 0;"><b>Nombre completo:</b> $nombreSocio</p>
                                <p style="margin: 5px 0;"><b>Documento (DNI):</b> $documento</p>
                                <p style="margin: 5px 0;"><b>Estado de la cuota:</b> <span style="color: ${if (estado == "Al día") "green" else "red"};">$estado</span></p>
                            </div>
                            <div style="margin: 20px 0; padding: 10px; background: #fff; border: 1px dashed #ccc; border-radius: 6px; display: inline-block;">
                                <p style="font-size: 13px; color: #444; margin-bottom: 8px; font-weight: bold;">Tu Código QR de Acceso:</p>
                                <img src="cid:qr_carnet" alt="Código QR Carnet" style="width: 180px; height: 180px; display: block; margin: 0 auto;" />
                                <p style="font-size: 11px; color: #888; margin-top: 8px;">(Hacé una captura de pantalla a este código para ingresar)</p>
                            </div>
                            
                            <p style="font-size: 12px; color: #7f8c8d; margin-top: 20px;">Este es un correo automático, por favor no respondas a este mensaje.</p>
                        </div>
                    """.trimIndent()
                        setContent(htmlContent, "text/html; charset=utf-8")
                    }

                    multipart.addBodyPart(cuerpoHtml)

                try {

                    val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.logo)
                    if (logoBitmap != null) {
                        val logoStream = ByteArrayOutputStream()
                        logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, logoStream)
                        val logoBytes = logoStream.toByteArray()

                        val logoBodyPart = MimeBodyPart().apply {
                            dataHandler = DataHandler(ByteArrayDataSource(logoBytes, "image/png"))
                            setHeader("Content-ID", "<logo_club>")
                            disposition = MimeBodyPart.INLINE
                        }
                        multipart.addBodyPart(logoBodyPart)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                    if (bitmapQR != null) {
                        val stream = ByteArrayOutputStream()
                        bitmapQR.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        val bytes = stream.toByteArray()

                        val qrBodyPart = MimeBodyPart().apply {
                            dataHandler = DataHandler(ByteArrayDataSource(bytes, "image/png"))
                            setHeader("Content-ID", "<qr_carnet>")
                            disposition = MimeBodyPart.INLINE
                        }
                        multipart.addBodyPart(qrBodyPart)
                    }



                message.setContent(multipart)
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