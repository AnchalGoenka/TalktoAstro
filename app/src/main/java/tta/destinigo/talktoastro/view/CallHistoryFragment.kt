package tta.destinigo.talktoastro.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import tta.destinigo.talktoastro.R
import tta.destinigo.talktoastro.util.ApplicationUtil
import tta.destinigo.talktoastro.util.MarginItemDecorator
import tta.destinigo.talktoastro.viewmodel.CallHistoryViewModel


class CallHistoryFragment : tta.destinigo.talktoastro.BaseFragment() {

    var callHistoryViewModel: CallHistoryViewModel? = null
    var callHistoryAdapter: tta.destinigo.talktoastro.adapter.CallHistoryAdapter? = null
    override fun getToolbarId(): Int {
        return 0
    }

    override val layoutResId: Int
        get() = R.layout.fragment_call_history

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        callHistoryViewModel = ViewModelProviders.of(this, MyViewModelFactory(myApplication)).get(CallHistoryViewModel::class.java)
        callHistoryViewModel?.arrayMutableCallHistory?.observe(this, Observer {
            callHistoryAdapter = tta.destinigo.talktoastro.adapter.CallHistoryAdapter(it!!)
            val call_history_list: androidx.recyclerview.widget.RecyclerView = myActivity.findViewById(R.id.call_history_list)
            call_history_list.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(ApplicationUtil.getContext())
            call_history_list.adapter = callHistoryAdapter
            if (call_history_list.layoutManager == null){
                call_history_list.addItemDecoration(MarginItemDecorator(1))
            }
            call_history_list.hasFixedSize()
        })
        // Inflate the layout for this fragment
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ApplicationUtil.setToolbarTitleAndRechargeButton("Call History", false, R.drawable.ic_hamburger)
    }

    class MyViewModelFactory(private val mApplication: tta.destinigo.talktoastro.BaseApplication): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CallHistoryViewModel(mApplication) as T
        }
    }


    companion object {
        internal val tagName
            get() = CallHistoryFragment::class.java.name

        fun newInstance(bundle: Bundle?): CallHistoryFragment {
            val fragment = CallHistoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}
