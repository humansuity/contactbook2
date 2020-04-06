package net.gas.gascontact.network

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser


class DownloadDbRequest(method: Int, mUrl: String,
                        listener: Response.Listener<ByteArray>,
                        errorListener: Response.ErrorListener,
                        params: HashMap<String, String>)
    : Request<ByteArray>(method, mUrl, errorListener) {

    private var mListener: Response.Listener<ByteArray>? = null
    private var mParams: Map<String, String>? = null
    var responseHeaders: Map<String, String>? = null

    init {
        setShouldCache(false)
        mListener = listener
        mParams = params
    }

    @Throws(com.android.volley.AuthFailureError::class)
    override fun getParams(): MutableMap<String, String> {
        return super.getParams()
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<ByteArray> {
        responseHeaders = response?.headers
        return Response.success(response?.data, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun deliverResponse(response: ByteArray?) {
        mListener?.onResponse(response)
    }


}