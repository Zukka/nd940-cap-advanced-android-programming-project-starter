package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled

class VoterInfoFragment : Fragment() {

    private lateinit var viewModel: VoterInfoViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        setDisplayHomeAsUpEnabled(true)

        val args = navArgs<VoterInfoFragmentArgs>()

        val election = args.value.argElection

        val binding = FragmentVoterInfoBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val electionDoa = ElectionDatabase.getInstance(requireContext()).electionDao
        val factory = VoterInfoViewModelFactory(electionDoa, election)
        viewModel = ViewModelProvider(this, factory).get(VoterInfoViewModel::class.java)
        binding.viewModel = viewModel

        binding.followButton.setOnClickListener {
            viewModel.toggleFollowButton()
        }

        return binding.root
    }
}