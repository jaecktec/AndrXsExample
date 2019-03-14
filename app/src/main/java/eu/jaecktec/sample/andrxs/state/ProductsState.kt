package eu.jaecktec.sample.andrxs.state

import android.util.Log
import androidx.work.WorkManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import eu.jaecktec.andrxs.Action
import eu.jaecktec.andrxs.Selector
import eu.jaecktec.andrxs.State
import eu.jaecktec.andrxs.StateContext
import eu.jaecktec.sample.andrxs.MainApplication
import org.json.JSONArray
import org.json.JSONObject


data class ProductsStateModel(
    val products: List<String> = listOf(),
    val loading: Boolean = false
)

class QueryProductsAction

@State(
    model = ProductsStateModel::class
)
class ProductsState {

    @Selector(name = "ProductsState::products")
    fun productsSelector(state: ProductsStateModel): List<String> {
        return state.products
    }

    @Selector(name = "ProductsState::loading")
    fun loadingSelector(state: ProductsStateModel): Boolean {
        return state.loading
    }

    @Action(QueryProductsAction::class)
    fun sampleAction(ctx: StateContext<ProductsStateModel>) {
        ctx.setState(
            ctx.getState()
                .copy(
                    loading = true, products = listOf()
                )
        )


        val queue = Volley.newRequestQueue(MainApplication.getAppContext())
        val url = "https://api.predic8.de/shop/products/?limit=100"

        val stringRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> { response ->
                val jsonArray = response.getJSONArray("products")
                val result = jsonArray.toList().map {
                    it.getString("name")
                }
                ctx.setState(ctx.getState().copy(products = result.toList(), loading = false))
            },
            Response.ErrorListener {
                Log.i("ProductsState", "That didn't work!")
            })

        queue.add(stringRequest)

    }

    private fun JSONArray.toList(): List<JSONObject> {
        if (this.length() < 1) return listOf()
        val result = mutableListOf<JSONObject>()

        for (idx in 0 until this.length()) {
            result.add(this.getJSONObject(idx))
        }
        return result
    }

}

