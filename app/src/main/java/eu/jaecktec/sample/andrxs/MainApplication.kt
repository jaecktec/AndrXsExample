package eu.jaecktec.sample.andrxs

import android.app.Application
import eu.jaecktec.andrxs.Store
import eu.jaecktec.sample.andrxs.state.ProductsState

class MainApplication : Application() {
    companion object {
        private val TAG = MainApplication::class.java.simpleName
        private var sInstance: MainApplication? = null

        fun getAppContext(): MainApplication {
            return sInstance!!
        }

    }
    private lateinit var store: Store

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        store = Store.createStore()
        store.addState(ProductsState())
    }

    fun getStore(): Store {
        return store
    }
}