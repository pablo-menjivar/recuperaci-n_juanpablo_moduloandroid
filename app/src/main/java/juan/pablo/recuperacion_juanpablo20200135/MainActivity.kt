package juan.pablo.recuperacion_juanpablo20200135

import Modelo.ClaseConexion
import Modelo.Enfermeros
import RecyclerViewHelpers.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: Adaptador
    private lateinit var rcvDatos: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //1- Mando a llamar los elementos de la vista
        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)
        val txtPeso = findViewById<EditText>(R.id.txtPeso)
        val txtCorreo = findViewById<EditText>(R.id.txtCorreo)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        rcvDatos = findViewById(R.id.rcvDatos)

        // Inicializar RecyclerView con un LayoutManager
        rcvDatos.layoutManager = LinearLayoutManager(this)

        // Mostrar los datos iniciales de Enfermeros
        obtenerYMostrarEnfermeros()

        //2- Programar el botón de agregar
        btnAgregar.setOnClickListener {
            agregarEnfermero(txtNombre.text.toString(), txtEdad.text.toString().toInt(), txtPeso.text.toString().toDouble(), txtCorreo.text.toString())
        }
    }
    // Función para obtener los datos de la base de datos
    private fun obtenerYMostrarEnfermeros() {
        // Asignamos un adaptador vacío inicialmente para evitar el error
        adapter = Adaptador(emptyList())
        rcvDatos.adapter = adapter

        // Corutina para obtener los datos desde la base de datos
        CoroutineScope(Dispatchers.IO).launch {
            val enfermerosDB = ObtenerEnfermeros()
            withContext(Dispatchers.Main) {
                // Una vez obtenidos los datos, actualizamos el adaptador
                adapter = Adaptador(enfermerosDB)
                rcvDatos.adapter = adapter
            }
        }
    }
    // Función para agregar un nuevo enfermero a la base de datos
    private fun agregarEnfermero(nombre: String, edad: Int, peso: Double, correo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val objConexion = ClaseConexion().CadenaConexion()

            // Inserción de nuevo enfermero
            val addEnfermero = objConexion?.prepareStatement("insert into tbEnfermero values(?, ?, ?, ?, ?)")!!
            addEnfermero.setString(1, UUID.randomUUID().toString())
            addEnfermero.setString(2, nombre)
            addEnfermero.setInt(3, edad)
            addEnfermero.setDouble(4, peso)
            addEnfermero.setString(5, correo)
            addEnfermero.executeUpdate()

            // Refrescar la lista de enfermeros después de agregar uno nuevo
            withContext(Dispatchers.Main) {
                obtenerYMostrarEnfermeros()  // Refrescamos la lista
            }
        }
    }
    // Función para obtener enfermeros desde la base de datos
    private fun ObtenerEnfermeros(): List<Enfermeros> {
        val objConexion = ClaseConexion().CadenaConexion()
        val statement = objConexion?.createStatement()
        val resultSet = statement?.executeQuery("select * from tbEnfermero")!!

        val listaEnfermeros = mutableListOf<Enfermeros>()
        while (resultSet.next()) {
            val uuid = resultSet.getString("UUID_Enfermero")
            val nombre = resultSet.getString("Nombre_Enfermero")
            val edad = resultSet.getInt("Edad_Enfermero")
            val peso = resultSet.getDouble("Peso_Enfermero")
            val correo = resultSet.getString("Correo_Enfermero")

            val enfermero = Enfermeros(uuid, nombre, edad, peso, correo)
            listaEnfermeros.add(enfermero)
        }
        return listaEnfermeros
    }
}