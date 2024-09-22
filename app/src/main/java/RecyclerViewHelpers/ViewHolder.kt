package RecyclerViewHelpers

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import juan.pablo.recuperacion_juanpablo20200135.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val txtNombreEnfermero = view.findViewById<TextView>(R.id.txtNombreEnfermero)
    val txtEdadEnfermero = view.findViewById<TextView>(R.id.txtEdadEnfermero)
    val txtPesoEnfermero = view.findViewById<TextView>(R.id.txtPesoEnfermero)
    val txtCorreoEnfermero = view.findViewById<TextView>(R.id.txtCorreoEnfermero)
    val imgEdit = view.findViewById<ImageView>(R.id.imgEdit)
    val imgDelete = view.findViewById<ImageView>(R.id.imgDelete)

}