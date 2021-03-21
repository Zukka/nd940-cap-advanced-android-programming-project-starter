package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

private const val TAG = "VoterInfoViewModel"


class VoterInfoViewModel(private val dataSource: ElectionDao, val election: Election) : ViewModel() {


    private val _followedElection = MutableLiveData<Election>()
    val followedElection: LiveData<Election>
        get() = _followedElection

    private val _administration = MutableLiveData<AdministrationBody>()
    val administration: LiveData<AdministrationBody>
        get() = _administration

    init {
        getVoterInfo()
        checkForFollowedElection()
    }

    private fun getVoterInfo() {
        viewModelScope.launch {
            try {
                val response = CivicsApi.retrofitService.getVoterInfo(election.division.state, election.id)
                _administration.value = response.state?.get(0)?.electionAdministrationBody
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
            }
        }
    }

    private fun checkForFollowedElection() {
        viewModelScope.launch {
            _followedElection.value = dataSource.getElection(election.id)
        }
    }

    fun toggleFollowButton() {
        viewModelScope.launch {
            if (_followedElection.value == null) {
                _followedElection.value = election
                dataSource.saveElection(election)
            } else {
                _followedElection.value = null
                dataSource.deleteElection(election.id)
            }
        }
    }
    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}