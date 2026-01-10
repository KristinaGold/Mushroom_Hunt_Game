package com.example.mushroomhuntgame.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mushroomhuntgame.callbacks.RecordCallback
import com.example.mushroomhuntgame.adapters.RecordAdapter
import com.example.mushroomhuntgame.database.RecordManager
import com.example.mushroomhuntgame.databinding.FragmentListBinding

class FragmentRecordList:  Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private var listener: RecordCallback? = null

    fun setListener(listener: RecordCallback) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = binding.rvLeaderboard
        list.layoutManager = LinearLayoutManager(requireContext())

        val topRecords = RecordManager.getInstance().getTopRecords()
        list.adapter = RecordAdapter(topRecords) { selectedRecord ->
            listener?.onRecordSelected(selectedRecord)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}