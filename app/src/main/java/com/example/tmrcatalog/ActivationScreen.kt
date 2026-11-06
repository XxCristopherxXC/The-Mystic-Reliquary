package com.example.tmrcatalog

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tmrcatalog.util.ActivationManager

@Composable
fun ActivationScreen(
    onActivationSuccess: () -> Unit
) {
    val context = LocalContext.current
    val activationManager = remember { ActivationManager(context) }

    var keyInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isBlocked by remember { mutableStateOf(activationManager.isAppBlocked()) }
    var remainingAttempts by remember { mutableStateOf(activationManager.getRemainingAttempts()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo de la App
        Image(
            painter = painterResource(id = R.drawable.tmr),
            contentDescription = "Logo TMR",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Permiso de Instalación TMR",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isBlocked) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "APLICACIÓN BLOQUEADA",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Se ha superado el número máximo de 3 intentos fallidos. Esta aplicación ha sido inhabilitada de forma permanente en este dispositivo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Text(
                text = "Esta aplicación requiere la clave de autorización. Tienes un máximo de 3 intentos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Intentos restantes: $remainingAttempts",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (remainingAttempts == 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = keyInput,
                onValueChange = {
                    keyInput = it
                    errorMessage = null
                },
                label = { Text("Clave de Licencia / Permiso") },
                singleLine = true,
                enabled = !isBlocked,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (isPasswordVisible) Icons.Filled.Check else Icons.Filled.Lock
                    val description = if (isPasswordVisible) "Ocultar clave" else "Mostrar clave"

                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                isError = errorMessage != null,
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (activationManager.activateApp(keyInput)) {
                        Toast.makeText(context, "Dispositivo Autorizado", Toast.LENGTH_SHORT).show()
                        onActivationSuccess()
                    } else {
                        isBlocked = activationManager.isAppBlocked()
                        remainingAttempts = activationManager.getRemainingAttempts()

                        if (isBlocked) {
                            errorMessage = "Aplicación bloqueada por superar el límite de intentos."
                        } else {
                            errorMessage = "Clave incorrecta. Quedan $remainingAttempts intentos."
                        }
                    }
                },
                enabled = !isBlocked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Activar Dispositivo")
            }
        }
    }
}