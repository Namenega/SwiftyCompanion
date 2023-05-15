package com.example.swiftycompanion

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import android.widget.Toast

class PersonalInfoAdapter(private val listGroup: ArrayList<String>, private val listChild: HashMap<String, ArrayList<String>>) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return listGroup.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listChild.get(listGroup.get(groupPosition))!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return listGroup.get(groupPosition)
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listChild.get(listGroup.get(groupPosition))!!.get(childPosition)
    }

    override fun getGroupId(groupPosition: Int): Long {
        return 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return 0
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: View = LayoutInflater.from(parent!!.context)
            .inflate(android.R.layout.simple_expandable_list_item_1, parent, false)

        val tv: TextView = view.findViewById(android.R.id.text1)
        val sGroup: String = getGroup(groupPosition).toString()

        tv.text = sGroup
        tv.setTypeface(null, Typeface.BOLD)
        tv.setTextColor(Color.WHITE)

        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view: View = LayoutInflater.from(parent!!.context)
            .inflate(android.R.layout.simple_expandable_list_item_1, parent, false)

        val tv: TextView = view.findViewById(android.R.id.text1)
        val sChild: String = getChild(groupPosition, childPosition).toString()

        tv.setText(sChild)
        tv.setTextColor(Color.DKGRAY)

        tv.setOnClickListener {
            Toast.makeText(parent.context, sChild, Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }
}