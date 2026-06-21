package com.cuidadoeterno.app.shared


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Drawer lateral reutilizable para cliente y cuidador.
 * Las opciones del menú varían según el rol.
 *
 * Uso:
 *   ModalNavigationDrawer(
 *       drawerContent = {
 *           DrawerMenu(
 *               nombreUsuario = "Juan Pérez",
 *               rol = "CLIENTE",
 *               onInicio = { ... },
 *               onHistorial = { ... },
 *               onPerfil = { ... },
 *               onCerrarSesion = { ... }
 *           )
 *       }
 *   ) { contenidoPantalla() }
 */
@Composable
fun DrawerMenu(
    nombreUsuario: String,
    rol: String,
    onInicio: () -> Unit,
    onHistorial: () -> Unit,
    onPagos: (() -> Unit)? = null,      // solo CUIDADOR
    onPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    onCloseDrawer: () -> Unit           // cierra el drawer al navegar
) {
    ModalDrawerSheet {

        // Encabezado con nombre y rol
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Cuidado Eterno",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = nombreUsuario,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = rol.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        HorizontalDivider()

        Spacer(modifier = Modifier.height(8.dp))

        // Inicio
        NavigationDrawerItem(
            label = { Text("Inicio") },
            selected = false,
            onClick = {
                onCloseDrawer()
                onInicio()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // Historial — nombre varía según rol
        NavigationDrawerItem(
            label = {
                Text(
                    if (rol == "CLIENTE") "Mis solicitudes"
                    else "Mis órdenes"
                )
            },
            selected = false,
            onClick = {
                onCloseDrawer()
                onHistorial()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        // Mis pagos — solo para el cuidador
        if (rol == "CUIDADOR" && onPagos != null) {
            NavigationDrawerItem(
                label = { Text("Mis pagos") },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onPagos()
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        // Perfil
        NavigationDrawerItem(
            label = { Text("Mi perfil") },
            selected = false,
            onClick = {
                onCloseDrawer()
                onPerfil()
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        // Cerrar sesión al fondo del drawer
        NavigationDrawerItem(
            label = {
                Text(
                    "Cerrar sesión",
                    color = MaterialTheme.colorScheme.error
                )
            },
            selected = false,
            onClick = {
                onCloseDrawer()
                onCerrarSesion()
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}