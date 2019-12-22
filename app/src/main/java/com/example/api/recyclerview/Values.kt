package com.example.api.recyclerview

import android.view.View

/**
 * @sample sample
 */
interface VisibilityBind {

    enum class State(val constant: Int) {
        Visible(View.VISIBLE), Invisible(View.INVISIBLE), Gone(View.GONE)
    }

    fun getVisibilityState(id: String, value: Any?): State

}

private fun sample() {
    class User(
        @BindView(
            view = BindView.View.TextView,
            field = BindView.Field.Visibility
        ) val name: String,
        @BindView(
            id = "imageView",
            view = BindView.View.TextView,
            field = BindView.Field.Visibility
        ) val imageSrc: String
    ) : VisibilityBind {

        override fun getVisibilityState(id: String, value: Any?) =
            when (id) {
                "name" -> {
                    value as String
                    if (value.startsWith("Mr")) VisibilityBind.State.Visible else VisibilityBind.State.Gone
                }
                "imageView" -> {
                    value as String
                    VisibilityBind.State.Invisible
                }
                else -> VisibilityBind.State.Gone
            }
    }
}