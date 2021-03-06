package com.we.beyond.presenter.reportResolved

import android.content.Context
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.IssueResolvedApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.ReportedResolutionDetailsPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of ReportedResolutionDetails Activity
 * which implement all api regarding get resolution details */

class MarAsResolvedDetailsImpl(issueById: MarAsResolvedDetailsPresenter.IMarAsResolvedDetailsView) :
    MarAsResolvedDetailsPresenter.IMarAsResolvedDetailsPresenter {
    var issueById = issueById

    /** It calls getDataToPost method which takes below inputs  */
    override fun onMarAsResolvedDetails(context: Context, sizeNo: Int, size: Int, issueId: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, sizeNo, size, issueId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the getResolutionDetails api which takes below inputs
     * and set MarAsResolvedDetailsAdapter
     */
    private fun getDataToPost(context: Context, sizeNo: Int, size: Int, issueId: String) {

        try {
            val issueApi = ApplicationController.retrofit.create(IssueResolvedApi::class.java)
            val response: Single<ReportedResolutionDetailsPojo> =
                issueApi.getResolutionDetails(sizeNo, size, issueId)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ReportedResolutionDetailsPojo>() {
                    override fun onSuccess(issuePojo: ReportedResolutionDetailsPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (issuePojo != null) {


                            issueById.setMarAsResolvedDetailsAdapter(issuePojo)

                        }
                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if (e is IOException) {
                                ConstantMethods.showError(
                                    context,
                                    context.resources.getString(R.string.no_internet_title),
                                    context.resources.getString(
                                        R.string.no_internet_sub_title
                                    )
                                )
                            } else {
                                val exception: HttpException = e as HttpException
                                val er: String = exception.response()!!.errorBody()!!.string()
                                val errorPojo: ErrorPojo =
                                    Gson().fromJson(er, ErrorPojo::class.java)

                                if (errorPojo != null) {
                                    if (errorPojo.error.isNotEmpty()) {
                                        if (errorPojo.message.isNotEmpty()) {
                                            ConstantMethods.showError(
                                                context,
                                                errorPojo.error,
                                                errorPojo.message
                                            )
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            ConstantMethods.cancleProgessDialog()
                            ConstantMethods.showError(
                                context,
                                context.resources.getString(R.string.error_title),
                                context.resources.getString(
                                    R.string.error_message
                                )
                            )

                        }
                    }

                })


        } catch (e: Exception) {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(
                context,
                context.resources.getString(R.string.error_title),
                context.resources.getString(R.string.error_message)
            )

        }
    }

}