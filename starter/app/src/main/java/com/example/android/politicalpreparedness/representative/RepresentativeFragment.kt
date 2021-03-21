package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.CivicsApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.*


class DetailFragment : Fragment() {

    companion object {
        const val PERMISSIONS_ACCESS_FINE_LOCATION_REQUEST_CODE = 100
    }

    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProvider(this).get(RepresentativeViewModel::class.java)

        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.recycler.adapter = RepresentativeListAdapter()

        viewModel.representatives.observe(viewLifecycleOwner) { representatives ->
            if (representatives == null || representatives.isEmpty()) {
                binding.listPlaceholder.visibility = View.VISIBLE
            } else {
                binding.listPlaceholder.visibility = View.GONE
                binding.motionLayout.transitionToEnd()
            }
        }

        viewModel.status.observe(viewLifecycleOwner) { status ->
            binding.progress.visibility = if (status == CivicsApiStatus.LOADING) View.VISIBLE else View.GONE
            if (status == CivicsApiStatus.ERROR) {
                AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.dialog_title_error_net))
                        .setMessage(R.string.error_connection)
                        .setPositiveButton(R.string.button_ok, null)
                        .create()
                        .show()
            }
        }

        binding.buttonSearch.setOnClickListener {
            viewModel.searchWithAddress()
            hideKeyboard()
        }

        binding.buttonLocation.setOnClickListener {
            getLocation()
        }

        binding.state.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                viewModel.address.value?.state = binding.state.selectedItem as String
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.address.value?.state = binding.state.selectedItem as String
            }
        }

        return binding.root

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.motionLayout,
                            R.string.permission_denied_explanation,
                            Snackbar.LENGTH_INDEFINITE).show()
//                            .setAction(R.string.action_settings) {
//                                startActivity(Intent().apply {
//                                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
//                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                })
//                            }.show()
                }
            }
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_ACCESS_FINE_LOCATION_REQUEST_CODE)
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (isPermissionGranted()) {
            LocationServices.getFusedLocationProviderClient(requireActivity()).lastLocation.addOnSuccessListener { location ->
                val address = geoCodeLocation(location)
                viewModel.searchWithLocation(address)
            }
        } else {
            requestLocationPermission()
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

}