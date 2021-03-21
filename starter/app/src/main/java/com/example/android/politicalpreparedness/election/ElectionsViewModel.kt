package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

private const val TAG = "ElectionsViewModel"

class ElectionsViewModel(private val electionDao: ElectionDao): ViewModel() {

    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upcomingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _status = MutableLiveData<CivicsApiStatus>()
    val status: LiveData<CivicsApiStatus>
        get() = _status

    private val _navigateToVoterInfo = MutableLiveData<Election>()
    val navigateToVoterInfo: LiveData<Election>
        get() = _navigateToVoterInfo

    init {
        refreshUpComingElections()
        refreshSavedElections()
    }

    private fun refreshUpComingElections() {
        _status.value = CivicsApiStatus.LOADING
        viewModelScope.launch {
            try {
                val response = CivicsApi.retrofitService.getElections()
                _upcomingElections.value = response.elections
                _status.value = CivicsApiStatus.DONE
            } catch (e: Exception) {
                _status.value = CivicsApiStatus.ERROR
                Log.e(TAG, "${e.message}")
            }
        }
    }

    fun refreshSavedElections() {
        viewModelScope.launch {
            _savedElections.value = electionDao.getElections()
        }
    }

    fun clearSavedElections() {
        viewModelScope.launch {
            electionDao.clearElections()
            _savedElections.value = null
        }
    }

    fun navigateToVoterInfo(election: Election) {
        _navigateToVoterInfo.value = election
    }

    fun navigateToVoterInfoCompleted() {
        _navigateToVoterInfo.value = null
    }
}