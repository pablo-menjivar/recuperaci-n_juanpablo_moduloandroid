package Modelo

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class ClaseConexion {
    fun CadenaConexion(): Connection? {
        try {
            val url = "jdbc:oracle:thin:@192.168.1.22:1521:xe"
            val usuario = "system"
            val contrasena = "Bimbimcito135@"

            val connection = DriverManager.getConnection(url, usuario, contrasena)
            return connection
        } catch (e: Exception) {
            println("Error: $e")
            return null
        }
    }
}