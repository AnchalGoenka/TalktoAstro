package tta.destinigo.talktoastro.view

import androidx.lifecycle.MutableLiveData
import tta.destinigo.talktoastro.model.ArticleList
import tta.destinigo.talktoastro.model.ArticleModel
import tta.destinigo.talktoastro.util.ApplicationConstant
import tta.destinigo.talktoastro.viewmodel.BaseViewModel
import tta.destinigo.talktoastro.volley.ext.RequestType


/**

 * Created by Vivek Singh on 2019-06-15.

 */
class ArticleViewModel constructor(app: tta.destinigo.talktoastro.BaseApplication): BaseViewModel(app) {

    val arrayArticleMutableLiveData = MutableLiveData<ArrayList<ArticleList>>()
    var arrayArticleModel = ArrayList<ArticleList>()

    init {
        getArticles()
    }

    fun getArticles() {
        doApiRequest(
            RequestType.GET, tta.destinigo.talktoastro.volley.RequestIdentifier.ARTICLES.ordinal,
            ApplicationConstant.ACTIVE_ARTICLES, null, null, null, false)
    }

    override fun onApiResponse(identifier: Int, response: String, serverDate: Long, lastModified: Long) {
        super.onApiResponse(identifier, response, serverDate, lastModified)

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