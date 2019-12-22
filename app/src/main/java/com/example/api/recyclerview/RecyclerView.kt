package com.example.api.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
import com.example.api.recyclerview.BindView.Companion.DefaultID
import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaField

class RecyclerViewAdapter<T : Any>(
    dataClass: KClass<T>,
    private val recyclerView: RecyclerView,
    private val list: List<T>,
    private val context: Context,
    private val itemLayoutId: Int,
    private val attachToRoot: Boolean,
    private val layoutManager: LayoutManager
) {
    enum class LayoutManager {
        LinearLayoutManager {
            override fun getLayoutManager(context: Context) = LinearLayoutManager(context)
        },
        GridLayoutManager {
            override fun getLayoutManager(context: Context) = GridLayoutManager(
                context,
                androidx.recyclerview.widget.GridLayoutManager.DEFAULT_SPAN_COUNT
            )
        };

        abstract fun getLayoutManager(context: Context): RecyclerView.LayoutManager
    }

    private val views = mutableMapOf<BindView, View>()
    private val fields = mutableMapOf<BindView, Field>()
    private var bindAction: ((View, T, Int) -> Unit)? = null
    private var onItemClickListener: ((T, Int) -> Unit)? = null
    private var onHeaderClickListener: ((View) -> Unit)? = null
    private var onFooterClickListener: ((View) -> Unit)? = null

    /**
     * Gets a custom binding logic and binds that way.
     *
     * Note that this binding takes place after auto binding provided by this Generic class, so any
     * logic conflicting auto binding logic will override its behaviour.
     * @param bind lambda that provides item [View], [T] element in that position and [Int] position
     * of the element and View.
     *
     * Note that these do not include Header and Footer items, and lambda parameters refers to
     * Real Items in the list (Value is same with or without header)
     * @return RecyclerViewAdapter<T>
     */
    fun setCustomBind(bind: View.(element: T, position: Int) -> Unit): RecyclerViewAdapter<T> {
        bindAction = bind
        return this
    }

    /**
     * Sets the action performed on each Item clicked.
     * This not includes Header and footer actions, and second lambda parameter refers to position
     * of Real Item in the list (Value is same with or without header)
     * To set header and footer click actions use [setOnHeaderClickListener] and
     * [setOnFooterClickListener] instead.
     */
    fun setOnItemClickListener(action: T.(Int) -> Unit): RecyclerViewAdapter<T> {
        onItemClickListener = action
        return this
    }

    /**
     * Sets the action performed on header clicked, if that exists.
     *
     * It does not matter that you set the Listener before or after setting the header itself. It
     * has no effect to set the ActionListener without setting the header layout.
     */
    fun setOnHeaderClickListener(action: (View) -> Unit): RecyclerViewAdapter<T> {
        onHeaderClickListener = action
        return this
    }

    /**
     * Sets the action performed on footer clicked, if that exists.
     *
     * It does not matter that you set the Listener before or after setting the footer itself. It
     * has no effect to set the ActionListener without setting the footer layout.
     * @return RecyclerViewAdapter<T>
     */
    fun setOnFooterClickListener(action: (View) -> Unit): RecyclerViewAdapter<T> {
        onFooterClickListener = action
        return this
    }

    private var headerLayoutId = 0
    private var footerLayoutId = 0

    /**
     * Sets the headerLayoutId (the first item visible in the recycler view).
     * @return RecyclerViewAdapter<T>
     */
    fun setHeaderLayout(@LayoutRes headerLayout: Int): RecyclerViewAdapter<T> {
        this.headerLayoutId = headerLayout
        return this
    }

    /**
     * Sets the footerLayoutId (the last item visible in the recycler view).
     * @return RecyclerViewAdapter<T>
     */
    fun setFooterLayout(@LayoutRes footerLayout: Int): RecyclerViewAdapter<T> {
        this.footerLayoutId = footerLayout
        return this
    }

    init {
        dataClass.declaredMemberProperties.filter { BindView::class in it.annotations.map { it.annotationClass } }
            .forEach {
                val field = it.javaField ?: throw OperationNotImplementedException(
                    OperationNotImplementedException.Operation.PropertyWithNoBackingField
                )
                field.isAccessible = true
                fields += it.findAnnotation<BindView>()!! to field
            }
    }

    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {

        private val hasHeader = headerLayoutId != 0
        private val hasHeaderAndFooter = headerLayoutId != 0 && footerLayoutId != 0
        private val hasHeaderOrFooter = (headerLayoutId != 0) xor (footerLayoutId != 0)
        override fun getItemCount() = when {
            hasHeaderAndFooter -> list.size + 2
            hasHeaderOrFooter -> list.size + 1
            else -> list.size
        }

        override fun getItemViewType(position: Int) = when {
            hasHeaderAndFooter -> {
                when (position) {
                    0 -> HEADER
                    itemCount - 1 -> FOOTER
                    else -> ITEM
                }
            }
            hasHeaderOrFooter -> {
                if (hasHeader) {
                    when (position) {
                        0 -> HEADER
                        else -> ITEM
                    }
                } else {
                    when (position) {
                        itemCount - 1 -> FOOTER
                        else -> ITEM
                    }
                }
            }
            else -> ITEM
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            when (getItemViewType(position)) {
                HEADER, FOOTER -> {
                }
                ITEM -> holder.bind(list[if (hasHeader) position - 1 else position], position)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(context)
            when (viewType) {
                HEADER -> {
                    val headerView = inflater.inflate(headerLayoutId, parent, attachToRoot)
                    headerView.setOnClickListener(onHeaderClickListener)
                    return ViewHolder(headerView)
                }
                FOOTER -> {
                    val footerView = inflater.inflate(footerLayoutId, parent, attachToRoot)
                    footerView.setOnClickListener(onFooterClickListener)
                    return ViewHolder(footerView)
                }
                else -> {
                    val itemView = inflater.inflate(itemLayoutId, parent, attachToRoot)
                    for ((bindView, field) in fields) {
                        val id = bindView.id
                        val resId = itemView.resources.getIdentifier(
                            if (id == DefaultID) field.name else id,
                            "id",
                            context.packageName
                        )
                        if (resId == 0) {
                            throw ViewNotFoundException(id)
                        }
                        val view = itemView.findViewById<View>(resId)
                        views += bindView to view
                    }
                    return ViewHolder(itemView)
                }
            }
        }
    }

    /**
     * Starts the binding process.
     */
    fun apply(): Adapter {
        recyclerView.layoutManager = layoutManager.getLayoutManager(context)
        val adapter = Adapter()
        recyclerView.adapter = adapter
        return adapter
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(t: T, position: Int) {
            if (headerLayoutId != 0) {
                bindItem(itemView, t, position + 1)
            } else {
                bindItem(itemView, t, position)
            }
        }

        private fun bindItem(itemView: View, t: T, position: Int) {
            for ((bindView, field) in fields) {
                val value = field.get(t)
                val view1 = views[bindView]!!
                val (id, view, fieldName) = bindView
                when (fieldName) {
                    BindView.Field.Text -> when (view) {
                        BindView.View.TextView -> {
                            view1 as? TextView ?: throw UnexpectedViewTypeException(
                                TextView::class.simpleName,
                                view1::class.simpleName
                            )
                            value as? CharSequence ?: throw UnexpectedFieldTypeException(
                                CharSequence::class.simpleName,
                                value::class.simpleName
                            )
                            view1.text = value
                        }
                        // TODO Extendable
                        else -> {
                            throw OperationNotImplementedException()
                        }
                    }
                    BindView.Field.URL -> {
                        when (view) {
                            BindView.View.ImageView -> {
                                view1 as? ImageView ?: throw UnexpectedViewTypeException(
                                    ImageView::class.simpleName,
                                    view1::class.simpleName
                                )
                                value as? CharSequence ?: throw UnexpectedFieldTypeException(
                                    CharSequence::class.simpleName,
                                    value::class.simpleName
                                )
//                                Glide.with(context).load(value.toString())
//                                    .placeholder(view1.drawable).into(view1)
                            }
                            else -> {
                                throw OperationNotImplementedException()
                            }
                        }
                    }
                    BindView.Field.Visibility -> {
                        t as? VisibilityBind ?: throw VisibilityWithoutVisibilityBindException()
                        view1.visibility = t.getVisibilityState(id, value).constant
                    }
                    BindView.Field.Enabled -> {
                        value as? Boolean ?: throw UnexpectedFieldTypeException(
                            Boolean::class.simpleName,
                            value::class.simpleName
                        )
                        view1.isEnabled = value
                    }
                }
            }
            bindAction?.invoke(itemView, t, position)
            itemView.setOnClickListener {
                onItemClickListener?.invoke(t, position)
            }
        }
    }

    companion object {
        private const val FOOTER = -1
        private const val ITEM = 0
        private const val HEADER = 1
    }
}

/**
 * Constructs a [RecyclerViewAdapter] with the given arguments. Binds a [RecyclerView] given as
 * receiver to the data given in [data]. The properties in class of [data] should be annotated with
 * [BindView] to declare binding logic.
 *
 * You should call [RecyclerViewAdapter.apply] in order for binding to take place.
 * @see BindView
 * @sample [sample]
 */
inline fun <reified T : Any> RecyclerView.bind(
    data: List<T>,
    ctx: Context,
    @LayoutRes itemLayoutId: Int,
    layoutManager: RecyclerViewAdapter.LayoutManager = RecyclerViewAdapter.LayoutManager.LinearLayoutManager
) =
    RecyclerViewAdapter(
        T::class,
        this,
        data,
        ctx,
        itemLayoutId,
        false,
        layoutManager
    )

private fun sample() {
    //language=kotlin
    """
    data class User(
        @BindView(
            view = BindView.View.TextView,
            field = BindView.Field.Text
        ) val name: String
    )

    val data = listOf(User("Mohsen"), User("Mohamad"))
    val resId = R.layout.recycler_view
    val ctx = this
    fun main() {
        recyclerView.bind(data, ctx, resId).setOnItemClickListener {
            if (it == 1) {
                Log.d("TestTag", "You are the first in my list")
            }
        }.apply()
    }
    """
}