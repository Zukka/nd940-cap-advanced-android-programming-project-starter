package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled

class ElectionsFragment: Fragment() {

    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        setDisplayHomeAsUpEnabled(true)

        val binding = FragmentElectionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.upcomingRecycler.adapter = ElectionListAdapter(listener)
        binding.savedRecycler.adapter = ElectionListAdapter(listener)

        val electionDoa = ElectionDatabase.getInstance(requireContext()).electionDao
        val factory = ElectionsViewModelFactory(electionDoa)
        viewModel = ViewModelProvider(this, factory).get(ElectionsViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.status.observe(viewLifecycleOwner) {
            binding.loadingImage.visibility = if (it == CivicsApiStatus.LOADING) View.VISIBLE else View.GONE
            if (it == CivicsApiStatus.ERROR) {
                AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialog_title_error_net))
                        .setMessage(R.string.error_connection)
                        .setPositiveButton(R.string.button_ok, null)
                        .create()
                        .show()
            }
        }

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner) { election ->
            if (election != null) {
                val action = ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election)
                this.findNavController().navigate(action)
                viewModel.navigateToVoterInfoCompleted()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    private val listener = ElectionListener { election ->
        viewModel.navigateToVoterInfo(election)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.voterinfo_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clear) {
            viewModel.clearSavedElections()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshSavedElections()
    }
}