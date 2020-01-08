package com.example.api.recyclerview

import androidx.recyclerview.widget.RecyclerView

/**
 * Detonates that a _property_ should be bound to [RecyclerView] with
 * corresponding [android.view.View] of type [view] that it's [field] field should be bound.
 *
 * The view's id should be given in [id], or it may be omitted if the [id] is same as _property_'s
 * name.
 *
 * Note that properties with no backing field are not supported yet, and trying to use this binding
 * logic with these properties will throw [OperationNotImplementedException].
 *
 * It's not guaranteed that [view].[field] value is simply set to value given by _property_ as there
 * may be more sophisticated logic in background.
 * @sample [sample]
 * @author Mohamadhassan Ebrahimi & Mohsen Dehbashi
 */
@Target(AnnotationTarget.PROPERTY)
annotation class BindView(
    val id: String = DefaultID,
    val view: View,
    val field: Field,
    val observable: Boolean = false
) {
    enum class View {
        TextView, ImageView
    }

    enum class Field {
        Visibility, Enabled, URL, Text
    }

    companion object {
        const val DefaultID = ""
    }
}

operator fun BindView.component1() = this.id
operator fun BindView.component2() = this.view
operator fun BindView.component3() = this.field
operator fun BindView.component4() = this.observable

private fun sample() {
    class User(
        @BindView(view = BindView.View.TextView, field = BindView.Field.Text) val id: String,
        @BindView("imageView", BindView.View.ImageView, BindView.Field.URL) val imageUrl: String
    )
}