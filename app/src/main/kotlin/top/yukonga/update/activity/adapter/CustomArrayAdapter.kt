package top.yukonga.update.activity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes

class CustomArrayAdapter(context: Context, @LayoutRes val resource: Int, private var showData: List<String>) : BaseAdapter(), Filterable {
    private val listForRemember = ArrayList(showData)
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var customAdapterFilter = CustomAdapterFilter()

    override fun getCount(): Int {
        return showData.size
    }

    override fun getItem(position: Int): Any {
        return showData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(inflater, position, convertView, parent, resource)
    }

    private fun createViewFromResource(inflater: LayoutInflater, position: Int, convertView: View?, parent: ViewGroup, resource: Int): View {
        val view: View = convertView ?: inflater.inflate(resource, parent, false)
        val text = view as TextView
        text.text = getItem(position) as CharSequence
        return view
    }

    override fun getFilter(): Filter {
        return customAdapterFilter
    }

    inner class CustomAdapterFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            val list = if (constraint.isEmpty()) listForRemember else listForRemember.filter { item ->
                item.contains(constraint, ignoreCase = true) || item.replace(" ", "").contains(constraint, ignoreCase = true)
            }
            results.values = list
            results.count = list.size
            showData = list
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }
}