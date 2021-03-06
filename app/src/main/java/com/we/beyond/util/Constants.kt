package com.we.beyond.util

import com.we.beyond.interceptor.ApplicationController


object Constants
{

    /** Constants used in app */
    val BASE_URL = ApplicationController.getBaseUrl()
    val IS_INTERCEPTOR = "isInterceptor"
    val USERJSONSTRING = "userJsonString"
    val IP ="192.168.1.4"
   // val GOOGLE_API_KEY ="AIzaSyDMBHtdtCIxyrcND3SKn0SxwTLeJMw_Xgw"
    val GOOGLE_API_KEY ="AIzaSyAlExMAQU0MmDmZx5Kgh4zddg3UN2LBd_c"
    var DASHBOARD_SUMMARY ="dashboardSummeryCriteria"
    var UNRESOLVED_ISSUES = "unresolvedIssues"
    var RESOLVED_ISSUES = "resolvedIssues"
    var UPCOMING_GATHERING = "upcomingGathering"
    var PUBLISH_CONNECT = "publishConnect"
    var COMMENT_ID = "commentId"
    var USER_ID = "userId"
    var CONSTANT_URI ="constantUri"
    var CROPPED_URI ="cropUri"

}