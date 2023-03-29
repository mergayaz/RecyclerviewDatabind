package kz.kuz.recyclerviewdatabind

import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kz.kuz.recyclerviewdatabind.databinding.FragmentMainBinding
import kz.kuz.recyclerviewdatabind.databinding.ListItemBinding
import java.io.IOException
import java.util.*

class MainFragment : Fragment() {
    lateinit var mSoundPool: SoundPool

    inner class Sound {
        lateinit var soundName: String
        var soundID = 0
        fun playAudio() {
            mSoundPool.play(soundID, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    private val sounds: MutableList<Sound> = ArrayList()
    private lateinit var binding: FragmentMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true // чтобы фрагмент не уничтожался с активностью
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        activity?.setTitle(R.string.toolbar_title)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main,
                container, false)
        binding.mainRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        val mAssets = context?.assets
        val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        mSoundPool = SoundPool.Builder()
                .setAudioAttributes(attributes)
                .setMaxStreams(5)
                .build()
        var afd: AssetFileDescriptor
        var mSound: Int
        try {
            val filenames = mAssets?.list("my_sounds")
            for (filename in filenames!!) {
                val sound = Sound()
                sound.soundName = filename.replace(".wav", "")
                afd = mAssets.openFd("my_sounds/$filename")
                mSound = mSoundPool.load(afd, 1)
                sound.soundID = mSound
                sounds.add(sound)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        binding.mainRecyclerView.adapter = MainAdapter(sounds)
        return binding.root
    }

    private inner class MainAdapter(private val mSounds: List<Sound>) :
            RecyclerView.Adapter<MainAdapter.MainHolder>() {

        private inner class MainHolder(val mBinding: ListItemBinding) :
                RecyclerView.ViewHolder(mBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
            val inflater = LayoutInflater.from(activity)
            val binding: ListItemBinding = DataBindingUtil.inflate(inflater,
                    R.layout.list_item, parent,false)
            return MainHolder(binding)
        }

        override fun onBindViewHolder(holder: MainHolder, position: Int) {
            val sound = mSounds[position]
            holder.mBinding.viewModel = ViewModel(sound)
            holder.mBinding.executePendingBindings()
        }

        override fun getItemCount(): Int {
            return mSounds.size
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSoundPool.release() // освобождение ресурсов SoundPool
    }

    override fun onResume() {
        super.onResume()
        binding.mainRecyclerView.adapter?.notifyDataSetChanged()
    }
}