package RecyclerViewHelpers

import Modelo.ClaseConexion
import Modelo.Enfermeros
import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import juan.pablo.recuperacion_juanpablo20200135.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Adaptador(var Datos: List<Enfermeros>): RecyclerView.Adapter<ViewHolder>() {
    fun actualicePantalla(uuid: String, nuevoNombre: String, nuevaEdad: Int, nuevoPeso: Double, nuevoCorreo: String) {
        // Buscar el índice del enfermero cuyo UUID coincida
        val index = Datos.indexOfFirst { it.UUID == uuid }

        // Si se encuentra un índice válido, actualizamos todos los atributos
        if (index != -1) {
            val enfermerosActualizados = Datos.toMutableList()

            // Actualizar los campos del objeto encontrado
            enfermerosActualizados[index].Nombre = nuevoNombre
            enfermerosActualizados[index].Edad = nuevaEdad
            enfermerosActualizados[index].Peso = nuevoPeso
            enfermerosActualizados[index].Correo = nuevoCorreo

            // Actualizar la lista de datos
            Datos = enfermerosActualizados.toList()

            // Notificar al adaptador que los datos han cambiado
            notifyDataSetChanged()
        }
    }
    fun eliminarRegistro(context: Context, nombreEnfermero: String, edadEnfermero: Int, pesoEnfermero: Double, correoEnfermero: String, Position: Int) {
        val lista = Datos.toMutableList()
        lista.removeAt(Position)

        // Notificamos al adaptador que los datos han cambiado
        notifyItemRemoved(Position)
        notifyItemRangeChanged(Position, lista.size)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val objConexion = ClaseConexion().CadenaConexion()

                val deleteEnfermero =
                    objConexion?.prepareStatement("DELETE FROM tbEnfermero WHERE Nombre_Enfermero = ? AND Edad_Enfermero = ? AND Peso_Enfermero = ? AND Correo_Enfermero = ?")!!
                deleteEnfermero.setString(1, nombreEnfermero)
                deleteEnfermero.setInt(2, edadEnfermero)
                deleteEnfermero.setDouble(3, pesoEnfermero)
                deleteEnfermero.setString(4, correoEnfermero)
                deleteEnfermero.executeUpdate()

                val commit = objConexion.prepareStatement("COMMIT")
                commit.executeUpdate()

                // Cambiamos al hilo principal para notificar al usuario
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Enfermero eliminado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al eliminar el enfermero", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    //Creamos la función de editar o actualizar en la base de datos
    fun editarEnfermero(nombreEnfermero: String, edadEnfermero: Int, pesoEnfermero: Double, correoEnfermero: String, UUID: String) {
        //Creo una "coroutina"
        GlobalScope.launch(Dispatchers.IO) {
            //1- Creo un objeto de la clase conexión
            val objConexion = ClaseConexion().CadenaConexion()

            //2- Creo una variable que contenga un PrepareStatement
            val updateEnfermero = objConexion?.prepareStatement("update tbEnfermero set Nombre_Enfermero = ?, Edad_Enfermero = ?, Peso_Enfermero = ?, Correo_Enfermero = ? where UUID_Enfermero = ?")!!
            updateEnfermero.setString(1, nombreEnfermero)
            updateEnfermero.setInt(2, edadEnfermero)
            updateEnfermero.setDouble(3, pesoEnfermero)
            updateEnfermero.setString(4, correoEnfermero)
            updateEnfermero.setString(5, UUID)
            updateEnfermero.executeUpdate()

            val commit = objConexion.prepareStatement("commit")
            commit.executeUpdate()
            withContext(Dispatchers.Main) {
                actualicePantalla(UUID, nombreEnfermero, edadEnfermero, pesoEnfermero, correoEnfermero)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Unir el RecyclerView con la Card
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }
    //Devolver la cantidad de datos que se muestren
    override fun getItemCount() = Datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Controlar a la card
        val item = Datos[position]
        holder.txtNombreEnfermero.text = item.Nombre
        holder.txtEdadEnfermero.text = item.Edad.toString()
        holder.txtPesoEnfermero.text = item.Peso.toString()
        holder.txtCorreoEnfermero.text = item.Correo

        holder.imgDelete.setOnClickListener {
            val contexto = holder.txtNombreEnfermero.context

            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar el registro?")

            builder.setPositiveButton("Sí") { dialog, which ->
                eliminarRegistro(contexto, item.Nombre, item.Edad, item.Peso, item.Correo, position) // Pasamos el contexto
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }
        holder.imgEdit.setOnClickListener {
            //Creo un AlertDialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Editar")
            builder.setMessage("¿Desea editar el registro?")

            //Agregar cuadros de texto para que el usuario escriba el nuevonombre, nuevaedad, nuevopeso y nuevocorreo
            // Crear un layout para los cuadros de texto
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL

            //Creando los cuadros de texto
            val inputNombre = EditText(context)
            inputNombre.hint = item.Nombre
            layout.addView(inputNombre)

            val inputEdad = EditText(context)
            inputEdad.hint = item.Edad.toString()
            inputEdad.inputType = InputType.TYPE_CLASS_NUMBER
            layout.addView(inputEdad)

            val inputPeso = EditText(context)
            inputPeso.hint = item.Peso.toString()
            inputPeso.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            layout.addView(inputPeso)

            val inputCorreo = EditText(context)
            inputCorreo.hint = item.Correo
            layout.addView(inputCorreo)

            //Agrego al Layout al AlertDialog
            builder.setView(layout)

            builder.setPositiveButton("Guardar Cambios") { dialog, which ->
                editarEnfermero(inputNombre.text.toString(), inputEdad.text.toString().toInt(), inputPeso.text.toString().toDouble(), inputCorreo.text.toString(), item.UUID)
            }
            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }
    }
}