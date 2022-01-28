package tta.destinigo.talktoastro.NewUI.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import tta.destinigo.talktoastro.model.ArticleList
import tta.destinigo.talktoastro.model.ArticleModel
import tta.destinigo.talktoastro.model.BannerModel
import tta.destinigo.talktoastro.model.TokenModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.viewmodel.BaseViewModel
import tta.destinigo.talktoastro.volley.ext.RequestType

class NewHomeFragmentViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication) : BaseViewModel(app){
    var arrayBannerModelData = ArrayList<BannerModel>()
    var arrayListMutableBannerData = MutableLiveData<ArrayList<BannerModel>>()

    val arrayArticleMutableLiveData = MutableLiveData<ArrayList<ArticleList>>()
    var arrayArticleModel = ArrayList<ArticleList>()

    init {
        getBanner()
        getArticles()
    }
    private fun getBanner() {
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.BANNER.ordinal,
            ApplicationConstant.ACTIVE_BANNER, null, null, null, false
        )
    }
    fun getArticles() {
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.ARTICLES.ordinal,
            ApplicationConstant.ACTIVE_ARTICLES, null, null, null, false)
    }

    override fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long
    ) {
        super.onApiResponse(identifier, response, serverDate, lastModified)
        Log.i("TAG", "" + response)
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.BANNER.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance()
                .parse(response, BannerModel::class.java,
                    object :
                        tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<BannerModel>() {
                        override fun onParseComplete(data: BannerModel) {
                            arrayBannerModelData.add(data)
                            arrayListMutableBannerData.postValue(arrayBannerModelData)
                        }

                        override fun onParseFailure(data: BannerModel) {
                            tta.destinigo.talktoastro.util.LogUtils.d(
                                "place list view model",
                                "parse failed due to error "
                            )
                        }
                    })
        }
        if (identifier == tta.destinigo.talktoastro.volley.RequestIdentifier.ARTICLES.ordinal) {
            tta.destinigo.talktoastro.volley.gson.GsonHelper.getInstance().parse(response, ArticleModel::class.java,
                object : tta.destinigo.talktoastro.volley.gson.OnGsonParseCompleteListener<ArticleModel>() {
                    override fun onParseComplete(data: ArticleModel) {


                        arrayArticleModel = data.articleList
                        arrayArticleMutableLiveData.postValue(data.articleList)

                    }

                    override fun onParseFailure(data: ArticleModel) {
                        tta.destinigo.talktoastro.util.LogUtils.d("place list view model", "parse failed due to error ")
                    }
                })
        }

    }

}