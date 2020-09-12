package com.example.lynx_workout.ui.user_profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.lynx_workout.R
import com.example.lynx_workout.utils.getImageUri
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.user_profile_fragment_layout.*
import org.jetbrains.anko.doAsync


class UserProfileFragment : Fragment() {

    private val TAG = UserProfileFragment::class.java.simpleName

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var selectedBitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_profile_fragment_layout, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        toggleLoading(true)
        startObserver()
        initListeners()
        displayUserInfo()
    }

    private fun displayUserInfo() {
        val user = profileViewModel.getUserDetails()
        user?.let {

            it.photoUrl?.let { url ->
                Glide.with(requireContext()).load(url).fitCenter()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(upf_profile_iv)

                doAsync {
                    selectedBitmap =
                        Glide.with(requireContext()).asBitmap().load(url).submit().get()
                }
            }

            it.displayName?.let { displayName ->
                upf_username_et.setText(displayName)
            }

            it.email?.let { email ->
                upf_email_tv.text = email
            }
        }
        toggleLoading(false)
    }

    private fun initListeners() {

        upf_save_changes_btn.setOnClickListener {
            toggleLoading(true)

            profileViewModel.saveChanges(
                upf_username_et.text.toString(),
                selectedBitmap.getImageUri(requireActivity()) ?: Uri.parse("")
            )
        }

        upf_edit_camera_iv.setOnClickListener {
            RxPermissions(requireActivity()).request(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                .subscribe { granted ->
                    if (granted) {
                        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0)
                    }
                }
        }

        upf_edit_gallery_iv.setOnClickListener {
            RxPermissions(requireActivity()).request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe { granted ->
                    if (granted) {
                        startActivityForResult(
                            Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            ), 1
                        )
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            val photo = (data?.extras?.get("data") as Bitmap?)
            photo?.let {
                selectedBitmap = it
                upf_profile_iv.setImageBitmap(it)
            }
        } else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                Glide.with(requireContext()).load(it).fitCenter()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(upf_profile_iv)

                doAsync {
                    selectedBitmap =
                        MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                }
            }
        }
    }

    private fun startObserver() {
        profileViewModel.updateProfileObservable.observe(viewLifecycleOwner, Observer {
            if (it.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.profile_updated_message_string),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
            toggleLoading(false)
        })
    }

    private fun stopObserver() {
        profileViewModel.updateProfileObservable.removeObservers(viewLifecycleOwner)
    }

    private fun toggleLoading(isLoading: Boolean) {
        upf_loading_pb.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopObserver()
    }
}