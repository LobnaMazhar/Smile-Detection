package lobna.smile.detection.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import lobna.smile.detection.databinding.FragmentResultBinding

/**
 * A Simple [Fragment] subclass
 * Views the resulted image that was captured automatically on smiling
 * [args] should contain the image to view and its rotation info
 * */
class ResultFragment : Fragment() {

    lateinit var fragmentResultBinding: FragmentResultBinding

    private val args: ResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        fragmentResultBinding = FragmentResultBinding.inflate(inflater)
        return fragmentResultBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentResultBinding.smileImageView.apply {
            setImageBitmap(args.smileImage)
            rotation = args.rotationDegrees * 1f
        }
    }
}