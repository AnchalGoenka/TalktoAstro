package tta.destinigo.talktoastro.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_ask_free_question.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.main_toolbar.view.*
import org.json.JSONObject
import tta.destinigo.talktoastro.BaseFragment
import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.activity.LoginActivity
import tta.destinigo.talktoastro.adapter.AskFreeQuesAdapter
import tta.destinigo.talktoastro.model.hubQuestionList
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator
import tta.destinigo.talktoastro.viewmodel.AskFreeQuestionViewModel
import java.util.*
import kotlin.collections.ArrayList
/**
 * Created by Anchal
 */

class AskFreeQuestionFragment : BaseFragment() {

    private var askFreeQuestionViewModel: AskFreeQuestionViewModel? = null
    private var askFreeQuesAdapter: tta.destinigo.talktoastro.adapter.AskFreeQuesAdapter? = null
    var  isclicked:Boolean=false

    override val layoutResId: Int
        get() = R.layout.fragment_ask_free_question

    override fun getToolbarId(): Int {
        return 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_ask_free_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBarSetup()
        callQuestionList()

       /* tv_ask_questions.setOnClickListener {
            if (isclicked == false) {
                card_your_question.visibility = View.VISIBLE
                isclicked = true
            } else {
                card_your_question.visibility = View.GONE
                isclicked = false
            }
        }*/

        tv_show_more_less.setOnClickListener {
            if (isclicked == false) {
                tv.visibility = View.VISIBLE
                tv_show_more_less.text="show less"
                isclicked = true
            } else {
                tv.visibility = View.GONE
                tv_show_more_less.text="show more"
                isclicked = false
            }
        }

        btn_quetion_submit.setOnClickListener {

            questionSubmit()

        }

    }

    override fun onResume() {
        super.onResume()
      //  callQuestionList()
    }

    fun questionSubmit(){
        showProgressBar("Loading")
        val jsonObject=JSONObject()
        jsonObject.put("question",ed_Question_submit.text)
        jsonObject.put("user_type","user")
        jsonObject.put("status","active")
        if (!ApplicationUtil.checkLogin()) {
            val intent = Intent(
                ApplicationUtil.getContext(),
                LoginActivity::class.java
            )
            startActivity(intent)
        } else if(ed_Question_submit.text.isNotEmpty()){
       askFreeQuestionViewModel?.gethubQuestionSubmit(jsonObject)
        askFreeQuestionViewModel?.questionSubmitListMutableLiveData?.observe(myActivity, Observer {
            Toast.makeText(ApplicationUtil.getContext(), it, Toast.LENGTH_LONG)
                .show()
            ed_Question_submit.text.clear()
            card_your_question.visibility = View.GONE
            isclicked = false
            callQuestionList()

        })
        } else{

            Toast.makeText(ApplicationUtil.getContext(),"Please Write Your Question ?",Toast.LENGTH_LONG).show()
        }
        hideProgressBar()
    }

    fun callQuestionList(){
        showProgressBar("Loading")
        askFreeQuestionViewModel = ViewModelProvider(this, MyViewModelFactory(myApplication)).get(AskFreeQuestionViewModel::class.java)
        askFreeQuestionViewModel?.gethubQuestion()
        askFreeQuestionViewModel?.arrayListMutablehubQuestionData?.observe(myActivity, Observer {
            // Collections.reverse(it)
            askFreeQuesAdapter = AskFreeQuesAdapter(it as ArrayList<hubQuestionList>){ hubQuestionListItem, i ->

                val bundle = Bundle()
                bundle.putString("id", hubQuestionListItem.id)
                bundle.putString("question", hubQuestionListItem.question)
                bundle.putInt("position",i)
                // bundle.putParcelable("position",p)
                val answerFragment = AnswerFragment.newInstance(bundle)
                var transaction: FragmentTransaction =
                    myActivity.supportFragmentManager.beginTransaction()
                transaction.replace(
                    R.id.frame_layout,
                    answerFragment,
                    AnswerFragment.tagName
                )
                transaction.addToBackStack(AnswerFragment.tagName)
                transaction.commit()
            }
            val question_list: RecyclerView = myActivity.findViewById(R.id.rv_question_list)
            question_list.layoutManager = LinearLayoutManager(ApplicationUtil.getContext())
            question_list.scheduleLayoutAnimation()
            question_list.adapter = askFreeQuesAdapter
            if (question_list.layoutManager == null){
                question_list.addItemDecoration(MarginItemDecorator(1))
            }
            question_list.hasFixedSize()
            hideProgressBar()
        })

    }

    private fun toolBarSetup() {

        val toolbar = myActivity.findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.title = "AskFreeQuestion"
        myActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_hamburger)
        toolbar.setNavigationOnClickListener {
            val drawer = myActivity.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
        }
        toolbar.btn_recharge.visibility=View.GONE
        toolbar.ib_notification.visibility=View.GONE
    }

    companion object {
        internal val tagName
            get() = AskFreeQuestionFragment::class.java.name

        fun newInstance(bundle: Bundle?): AskFreeQuestionFragment {
            val fragment = AskFreeQuestionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication) :
        ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AskFreeQuestionViewModel(mApplication) as T
        }
    }

}