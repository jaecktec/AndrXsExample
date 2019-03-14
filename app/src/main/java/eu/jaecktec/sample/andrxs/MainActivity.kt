package eu.jaecktec.sample.andrxs

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import eu.jaecktec.andrxs.Select
import eu.jaecktec.andrxs.Store
import eu.jaecktec.sample.andrxs.state.QueryProductsAction
import io.reactivex.Observable

class MainActivity : AppCompatActivity() {

    @Select("ProductsState::products")
    private lateinit var products: Observable<List<String>>

    @Select("ProductsState::loading")
    private lateinit var loading: Observable<Boolean>

    private val mStore: Store by lazy(LazyThreadSafetyMode.NONE) {
        MainApplication.getAppContext().getStore()
    }

    private val mLoadProductsButton: Button by lazy(LazyThreadSafetyMode.NONE) {
        this.findViewById<Button>(R.id.button_refresh_main)
    }

    private val mProgressBar: ProgressBar by lazy(LazyThreadSafetyMode.NONE) {
        this.findViewById<ProgressBar>(R.id.progressbar_main)
    }

    private val mProductsListContainer: LinearLayout by lazy(LazyThreadSafetyMode.NONE) {
        this.findViewById<LinearLayout>(R.id.products_list_layout_main)
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStore.onCreate(this)
        setContentView(R.layout.activity_main)
        products.subscribe {
            runOnUiThread {
                mProductsListContainer.removeAllViews()
                it.forEach {
                    mProductsListContainer.addView(TextView(this).apply { text = it })
                }
            }
        }

        loading.subscribe {
            mProgressBar.visibility = if (it) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

        mProgressBar.visibility = View.INVISIBLE

        mLoadProductsButton.setOnClickListener {
            mStore.dispatch(QueryProductsAction())
            mProgressBar.visibility = View.VISIBLE
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mStore.onDestroy(this)
    }
}
