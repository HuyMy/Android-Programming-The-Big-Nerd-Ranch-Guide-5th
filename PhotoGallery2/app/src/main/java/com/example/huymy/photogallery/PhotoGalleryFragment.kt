package com.example.huymy.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.huymy.photogallery.api.FlickrApi
import com.example.huymy.photogallery.databinding.FragmentPhotoGalleryBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

private const val TAG = "PhotoGalleryFragment"

class PhotoGalleryFragment : Fragment() {
    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        binding.photoGrid.layoutManager = GridLayoutManager(context, 3)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://www.flickr.com/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val flickrApi: FlickrApi = retrofit.create<FlickrApi>()

        viewLifecycleOwner.lifecycleScope.launch {
            val response = flickrApi.fetchContents()
            Log.d(TAG, "Response received: $response")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}