package kz.kuz.recyclerviewdatabind

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import kz.kuz.recyclerviewdatabind.MainFragment.Sound

class ViewModel(var mSound: Sound) : BaseObservable() {
    val soundTitle: String = mSound.soundName

    fun playAudio() {
        mSound.playAudio()
    }

    init {
        notifyChange()
    }
}