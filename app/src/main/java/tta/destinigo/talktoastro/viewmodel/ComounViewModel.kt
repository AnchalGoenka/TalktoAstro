package tour.traveling.travel.ui.product

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import tta.destinigo.talktoastro.model.TokenModel
import tta.destinigo.talktoastro.model.request.SendNotiRequest

class ComounViewModel : ViewModel() {

    lateinit var mRepo: MainRespository
    init {
        mRepo = MainRespository()
    }



    fun callSendNotification(req: SendNotiRequest): LiveData<TokenModel> {
        return mRepo.callSendNotification(req)
    }

}