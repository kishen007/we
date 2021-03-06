package com.we.beyond.ui.gathering.createGathering

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.we.beyond.adapter.MediaAdapter
import com.we.beyond.R
import com.we.beyond.RealPathUtils
import com.we.beyond.model.GatheringDetails
import com.we.beyond.model.LatLongSelectedPojo
import com.we.beyond.model.MediaUploadingPojo
import com.we.beyond.presenter.gathering.createGathering.CreateGatheringImpl
import com.we.beyond.presenter.gathering.createGathering.CreateGatheringPresenter
import com.we.beyond.presenter.mediaUpload.MediaImpl
import com.we.beyond.ui.AddLocationActivity
import com.we.beyond.ui.gathering.gathering.GatheringActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.FileUtils
import com.white.easysp.EasySP
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
/**
 * This activity creates the gathering with respected details
 */
class CreateGatheringActivity : AppCompatActivity(), CreateGatheringPresenter.ICreateGatheringView{

    /** initialize respected implementors */
    val context: Context = this
    var mediaAdapter: MediaAdapter? = null
    var createGatheringPresenter: CreateGatheringImpl? = null
    var mediaPresenter: MediaImpl? = null

    /** init image view */
    var back: ImageView? = null
    //var closeGallery : ImageView?=null
    //var close : ImageView?=null

    /** init text view */
    var title: TextView? = null
    var locationTitle: TextView? = null
    var addLocation: TextView? = null
    var mediaTitle: TextView? = null
    var or: TextView? = null
    /* var mediaOr : TextView?=null
     var mediaOrOption : TextView?=null*/

    /** init edit text */
    var gatheringTitle: EditText? = null
    var gatheringDetails: EditText? = null
    var dateTime: EditText? = null

    /** init text input edit text */
    var linkIssueTitle: TextInputEditText? = null

    /** init button */
    var useCamera: Button? = null
    var useGallery: Button? = null
    var create: Button? = null
    /* var image : Button?=null
     var video : Button?=null
     var imageGallery : Button?=null
     var videoGalley : Button?=null*/

    /** init text input layout */
    var linkIssueLayout : TextInputLayout?=null

    /** initialize strings */
    var issueId: String? = null
    var getIssueNumber: Int? = null
    var issueTitle: String? = null
    var address: String? = null
    var city: String? = null
    var gatheringDate: String = ""
    var selectedDate: String = ""
    var selectedTime: String = ""
    var connectTitleText: String = ""
    var connectDetailsText: String = ""
    var getSelectedDate: String = ""
    var getMedia : String = ""
    var fileUri: String? = null
    var picturePath : String?=null

    /** initialize array list */
    var mimeTypeArray : ArrayList<String>?=null
    var mediaArray : ArrayList<String>?=null
    var mediaStatusArray : ArrayList<MediaUploadingPojo>?=null

    var CAMERA = 1
    var GALLERY = 2
    /* var GALLERY_IMAGE = 3
     var GALLERY_VIDEO = 4*/

    var file: File? = null


    /** init date and time picker */
    var date: DatePickerDialog.OnDateSetListener? = null
    var myCalendar: Calendar? = null

    /** init recycler view */
    var mediaRecycler: RecyclerView? = null

    var mMediaUri: Uri? = null

    var count = 0
    var RequestPermissionCode = 1

    /** initialize booleans */
    var isEdit : Boolean = false
    var isMyGathering : Boolean = false
    var isIssue : Boolean = false
    var isIssueList : Boolean = false
    var isGatheringList : Boolean = false
    var gatheringDetailsData : GatheringDetails?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_gathering)

        /** array initialization */
        myCalendar = Calendar.getInstance()
        mimeTypeArray = ArrayList()
        mediaArray = ArrayList()
        mediaStatusArray = ArrayList()

        count = 0

        /** initialize implementation */
        createGatheringPresenter = CreateGatheringImpl(this)
//        mediaPresenter = MediaImpl(this)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA, "")

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get Shared data */
        getSharedData()

        /* if (ConstantMethods.checkPermission(context)) {
             val mydir =
                 File(Environment.getExternalStorageDirectory().toString() + resources.getString(R.string.app_name))
             if (!mydir.exists()) {
                 mydir.mkdirs()
             }
         } else {
             ConstantMethods.requestPermission(context)
         }

 */
        /** It get stored data using intent and checks the gathering details
         * if not null then set data to edit text
         * else call getSharedData()
         */
        val getIntentData = intent.getStringExtra("gatheringData")
        isEdit = intent.getBooleanExtra("edit",false)
        gatheringDetailsData = Gson().fromJson(getIntentData, GatheringDetails::class.java)
        isIssue = intent.getBooleanExtra("issue",false)
        isIssueList = intent.getBooleanExtra("issueList",false)
        isGatheringList = intent.getBooleanExtra("gathering",false)
        issueId = intent.getStringExtra("issueId")



        if(isEdit) {
            create!!.text = "update"

        }
        else{
            create!!.text = "create"
        }

        println("campaign data $isEdit")

        if(gatheringDetailsData!=null)
        {

            issueId = gatheringDetailsData!!.data.issue._id
            //campaignId = gatheringDetailsData!!.data._id
            linkIssueTitle = findViewById(R.id.et_link_issue_title)
            linkIssueTitle!!.setText(gatheringDetailsData!!.data.issue.title)
            linkIssueTitle!!.typeface = ConstantFonts.raleway_semibold
            gatheringTitle!!.setText(gatheringDetailsData!!.data.title)
            city = gatheringDetailsData!!.data.city
            val json1 = Gson().toJson(LatLongSelectedPojo(gatheringDetailsData!!.data.location.coordinates[0].toDouble(),gatheringDetailsData!!.data.location.coordinates[1].toDouble()))
            EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO,json1)
            /*  latitude = gatheringDetailsData!!.data.location.coordinates[0].toString()
              longitude = gatheringDetailsData!!.data.location.coordinates[1].toString()*/
            gatheringDetails!!.setText(gatheringDetailsData!!.data.description)
            dateTime!!.setText(ConstantMethods.convertStringToDateStringFull(gatheringDetailsData!!.data.gatheringDate))
            locationTitle = findViewById(R.id.txt_location_title)
            locationTitle!!.text = gatheringDetailsData!!.data.address
            locationTitle!!.typeface = ConstantFonts.raleway_semibold


            if(gatheringDetailsData!!.data.imageUrls!= null && gatheringDetailsData!!.data.imageUrls.isNotEmpty())
            {
                for(i in 0 until gatheringDetailsData!!.data.imageUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            gatheringDetailsData!!.data.imageUrls[i],
                            "image",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }


        }
        else{
            getSharedData()
        }


    }


    /** get all shared data and set to edit text  */
    private fun getSharedData() {
        //issueId = EasySP.init(this).getString("issueId")

        getIssueNumber = EasySP.init(this).getInt(ConstantEasySP.ISSUE_NUMBER)
        issueTitle = EasySP.init(this).getString(ConstantEasySP.ISSUE_TITLE)
        address = EasySP.init(this).getString(ConstantEasySP.SELECTED_GATHERING_ADDRESS)
        city = EasySP.init(this).getString("city")
        connectTitleText = EasySP.init(this).getString("gatheringTitle")
        connectDetailsText = EasySP.init(this).getString("gatheringDetails")
        getSelectedDate = EasySP.init(this).getString(ConstantEasySP.GATHERING_DATE)
        issueId = EasySP.init(this).getString(ConstantEasySP.ISSUE_ID)


        if (issueTitle != null && issueTitle!!.isNotEmpty()) {
            linkIssueTitle = findViewById(R.id.et_link_issue_title)
            linkIssueTitle!!.setText(issueTitle)
            linkIssueTitle!!.typeface = ConstantFonts.raleway_semibold
        }

        if (address != null && address!!.isNotEmpty()) {
            locationTitle = findViewById(R.id.txt_location_title)
            locationTitle!!.text = address
            locationTitle!!.typeface = ConstantFonts.raleway_semibold
        }

        gatheringTitle = findViewById(R.id.et_gathering_title)
        gatheringTitle!!.setText(connectTitleText)

        gatheringDetails = findViewById(R.id.et_gathering_details)
        gatheringDetails!!.setText(connectDetailsText)

        dateTime = findViewById(R.id.et_date_time)
        dateTime!!.setText(getSelectedDate)

    }


    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity */
        back!!.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            EasySP.init(this).putString(ConstantEasySP.GATHERING_DATE,"")
            EasySP.init(this).putString(ConstantEasySP.SELECTED_GATHERING_ADDRESS,"")
            EasySP.init(this).putString("gatheringTitle","")
            EasySP.init(this).putString("gatheringDetails","")
            //finish()
        }

        /** It opens date picker  */
        dateTime!!.setOnClickListener {

            ConstantMethods.hideKeyBoard(context, this)
            val datePicker = DatePickerDialog(
                context, date, myCalendar!!.get(Calendar.YEAR), myCalendar!!.get(Calendar.MONTH),
                myCalendar!!.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
            datePicker.datePicker.minDate = System.currentTimeMillis()

        }

        /** It selects the date and time using date picker and time picker and set it to text view */
        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            selectedDate = "$dayOfMonth-${(monthOfYear + 1)}-$year"

            var simpleDateFormat = SimpleDateFormat("dd-M-yyyy")
            val currentDate = simpleDateFormat.format(myCalendar!!.time)

            println("year===$selectedDate")
            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    myCalendar!!.set(Calendar.HOUR_OF_DAY, hour)
                    myCalendar!!.set(Calendar.MINUTE, minute)

                    println("selected date ${selectedDate}")
                    println("current date ${currentDate}")

                    if(selectedDate == currentDate) {
                        val c = Calendar.getInstance()

                        if (myCalendar!!.getTimeInMillis() >= c!!.getTimeInMillis()) {
                            //it's after current
                            selectedTime = SimpleDateFormat("K:mm a").format(myCalendar!!.time)
                            gatheringDate = "$selectedDate $selectedTime"
                            println("date and time $gatheringDate")

                            dateTime!!.setText(ConstantMethods.convertDateStringToShow(gatheringDate))

                            println(
                                "server date and time ${ConstantMethods.convertDateStringToServerDateFull(
                                    dateTime!!.text.toString()
                                )}"
                            )
                        } else {
                            //it's before current'
                            //Toast.makeText(getApplicationContext(), "Invalid Time", Toast.LENGTH_SHORT).show()
                            ConstantMethods.showWarning(
                                this,
                                "",
                                "Please select valid time for gathering."
                            )
                        }


                    }
                    else{
                        selectedTime = SimpleDateFormat("K:mm a").format(myCalendar!!.time)
                        gatheringDate = "$selectedDate $selectedTime"
                        println("date and time $gatheringDate")

                        dateTime!!.setText(ConstantMethods.convertDateStringToShow(gatheringDate))

                        println(
                            "server date and time ${ConstantMethods.convertDateStringToServerDateFull(
                                dateTime!!.text.toString()
                            )}"
                        )
                    }



                    EasySP.init(this)
                        .putString(ConstantEasySP.GATHERING_DATE, dateTime!!.text.toString().trim())

                },
                myCalendar!!.get(Calendar.HOUR_OF_DAY),
                myCalendar!!.get(Calendar.MINUTE),
                false
            ).show()

        }

        /** It opens AddLocationActivity  */
        addLocation!!.setOnClickListener {
            ConstantMethods.hideKeyBoard(context, this)
            val intent = Intent(context, AddLocationActivity::class.java)
            intent.putExtra("gatheringLocation", true)
            startActivityForResult(intent,5)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            EasySP.init(this).putString("gatheringTitle", gatheringTitle!!.text.toString())
            EasySP.init(this).putString("gatheringDetails", gatheringDetails!!.text.toString())

        }

        /** It opens camera intent to capture image */
        useCamera!!.setOnClickListener {
            try {
                ConstantMethods.hideKeyBoard(context, this)
                /*optionLayout!!.visibility = View.VISIBLE
                optionLayout!!.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_up))*/

                try {
                    mMediaUri = ConstantMethods.getMediaOutputUri(context)
                    if (mMediaUri == null) {
                        // error
                        Toast.makeText(
                            context,
                            "There was a problem accessing your device's storage.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        println("Mediya Url:- $mMediaUri")
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri)
                        startActivityForResult(intent, CAMERA)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /** It opens gallery to select image from gallery */
        useGallery!!.setOnClickListener {
            try {
                ConstantMethods.hideKeyBoard(context, this)
                /*galleryOptionLayout!!.visibility = View.VISIBLE
                galleryOptionLayout!!.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_up))*/

                try {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    startActivityForResult(intent, GALLERY)

                    //galleryOptionLayout!!.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        /** It checks required fields are empty or not, if empty then shows the warning dialog
         * and get all required fields from user to
         * call the respective method to add or update data to the server
         * */
        create!!.setOnClickListener {
            try {

                val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)

                val type = object : TypeToken<ArrayList<MediaUploadingPojo>>() {

                }.type
                var mediaPojo = ArrayList<MediaUploadingPojo>()
                try {
                    mediaPojo = Gson().fromJson<ArrayList<MediaUploadingPojo>>(media, type)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (linkIssueTitle!!.text!!.isEmpty())
                {
                    ConstantMethods.showWarning(this,"Please Link An Issue", "You need to link an issue to this gathering to post")
                }
                else if (gatheringTitle!!.text.isEmpty()) {
                    ConstantMethods.showWarning(this,"Title", "Please Enter Title")
                } else if (gatheringDetails!!.text.isEmpty()) {
                    ConstantMethods.showWarning(this,"Details", "Please Enter Gathering Details")
                } else if (dateTime!!.text.isEmpty()) {
                    ConstantMethods.showWarning(this,"Date And Time", "Please Select Date And Time")
                } else if (locationTitle!!.text.isEmpty()) {
                    ConstantMethods.showWarning(this,"Location", "Please Select Gathering Location")
                }else if (media!!.isEmpty()) {
                    ConstantMethods.showWarning(this,"Please Upload Photo", "You need to attach at least 1 photo to this gathering to post")
                }

                else {

                    if (isEdit) {

                        if (ConstantMethods.checkForInternetConnection(this@CreateGatheringActivity)) {
                            getDataToPostUpdate()
                        }
                    } else {
                        if (ConstantMethods.checkForInternetConnection(this@CreateGatheringActivity)) {
                            getDataToPost()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It opens NearByIssueActivity  */
        linkIssueTitle!!.setOnClickListener {
            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("gathering",true)
            startActivityForResult(intent,5)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }


    }

    /** It converts required data to json object and
     * call postDataToServerUpdate function with json object as parameter
     */
    private fun getDataToPostUpdate()
    {
        try{
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val details = gatheringDetails!!.text.toString().trim()
            val latlong = EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
            getSelectedDate = EasySP.init(this).getString(ConstantEasySP.GATHERING_DATE)

            val type = object : TypeToken<ArrayList<MediaUploadingPojo>>() {

            }.type
            var mediaPojo = ArrayList<MediaUploadingPojo>()
            try {
                mediaPojo = Gson().fromJson<ArrayList<MediaUploadingPojo>>(media, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val imageArray = ArrayList<String>()


            for (i in 0 until mediaPojo.size) {
                if (mediaPojo[i].isUpload) {
                    count++

                    //if (mediaPojo[i].mimeType.contains("image")) {
                    imageArray.add(mediaPojo[i].serverUrl)
                    /* } else {
                         videoArray.add(mediaPojo[i].serverUrl)
                     }
 */
                } else {
                    ConstantMethods.showError(this, "Please wait", "Please wait media uploading.")
                }
            }


            var latLongPojo: LatLongSelectedPojo =
                Gson().fromJson(latlong, LatLongSelectedPojo::class.java)


            println("latlong pojo in activity ${latLongPojo.latitude}")

            val latlongArray = ArrayList<String>()
            latlongArray.add(latLongPojo.latitude.toString())
            latlongArray.add(latLongPojo.longitude.toString())


            val jsonObject = JsonObject()
            jsonObject.addProperty("gatheringId", gatheringDetailsData!!.data._id)

            if(!gatheringTitle!!.text.equals(gatheringDetailsData!!.data.title)) {
                jsonObject.addProperty("title", gatheringTitle!!.text.toString().trim())
            }
            if(!gatheringDetails!!.text.equals(gatheringDetailsData!!.data.description)) {

                jsonObject.addProperty(
                    "description",
                    gatheringDetails!!.text.toString().trim()
                )
            }
            if(!ConstantMethods.convertStringToDateStringFull(gatheringDetailsData!!.data.gatheringDate).equals(dateTime!!.text.toString(),ignoreCase = true)) {
                jsonObject.addProperty(
                    "gatheringDate",
                    ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                )
            }
            if(!locationTitle!!.text.equals(gatheringDetailsData!!.data.address)) {
                jsonObject.addProperty("address", locationTitle!!.text.toString().trim())
                jsonObject.addProperty("city", city)


                val locationJsonArray = JsonArray()

                for (i in 0 until latlongArray.size) {

                    locationJsonArray.add(latlongArray[i])

                }
                jsonObject.add("location", locationJsonArray)
            }

            //image array
            val imageJsonArray = JsonArray()

            for (i in 0 until imageArray.size) {

                imageJsonArray.add(imageArray[i])

            }

            println("images json $imageArray")
            jsonObject.add("imageUrls", imageJsonArray)



            if (count == mediaPojo.size) {

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {
                        postDataToServerUpdate(jsonObject)
                    }
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }
            } else {

                count = 0

                ConstantMethods.showWarning(
                    this,
                    "Please wait",
                    "Please wait media uploading."
                )
            }




        }

        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onGatheringUpdated function of create gathering presenter */
    private fun postDataToServerUpdate(jsonObject: JsonObject)
    {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                ConstantMethods.showProgessDialog(
                    this@CreateGatheringActivity,
                    "Please Wait..."
                )
                createGatheringPresenter!!.onGatheringUpdated(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It converts required data to json object and
     * call postDataToServer function with json object as parameter
     * */
    private fun getDataToPost() {
        try {
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val latlong = EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
            val details = gatheringDetails!!.text.toString().trim()
            getSelectedDate = EasySP.init(this).getString(ConstantEasySP.GATHERING_DATE)


            if (linkIssueTitle!!.text!!.isEmpty())
            {
                ConstantMethods.showWarning(this,"Please Link An Issue", "You need to link an issue to this gathering to post")
            }
            else if (gatheringTitle!!.text.isEmpty()) {
                ConstantMethods.showWarning(this,"Title", "Please Enter Title")
            } else if (gatheringDetails!!.text.isEmpty()) {
                ConstantMethods.showWarning(this,"Details", "Please Enter Gathering Details")
            } else if (dateTime!!.text.isEmpty()) {
                ConstantMethods.showWarning(this,"Date And Time", "Please Select Date And Time")
            } else if (locationTitle!!.text.isEmpty()) {
                ConstantMethods.showWarning(this,"Location", "Please Select Gathering Location")
            }else if (media!!.isEmpty()) {
                ConstantMethods.showWarning(this,"Please Upload Photo", "You need to attach at least 1 photo to this gathering to post")
            }

            else {

                val type = object : TypeToken<ArrayList<MediaUploadingPojo>>() {

                }.type
                var mediaPojo = ArrayList<MediaUploadingPojo>()
                try {
                    mediaPojo = Gson().fromJson<ArrayList<MediaUploadingPojo>>(media, type)
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                var latLongPojo: LatLongSelectedPojo =
                    Gson().fromJson(latlong, LatLongSelectedPojo::class.java)

                val latlongArray = ArrayList<String>()
                latlongArray.add(latLongPojo.latitude.toString())
                latlongArray.add(latLongPojo.longitude.toString())


                val imageArray = ArrayList<String>()

                for (i in 0 until mediaPojo.size) {
                    if (mediaPojo[i].isUpload) {
                        count++

                        if (mediaPojo[i].mimeType.contains("image")) {
                            imageArray.add(mediaPojo[i].serverUrl)
                        }

                    } else {
                        ConstantMethods.showError(this, "", "Please wait while media is uploading.")
                    }
                }


/*
                if (details.isNotEmpty() && gatheringTitle!!.text.isNotEmpty() && gatheringDetails!!.text.isNotEmpty() && issueId!!.isNotEmpty() && getSelectedDate.isNotEmpty() && latlongArray.isNotEmpty() && imageArray.isEmpty()) {
                    if (ConstantMethods.checkForInternetConnection(this@CreateGatheringActivity)) {

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("issueId", issueId)
                        jsonObject.addProperty("title", gatheringTitle!!.text.toString().trim())
                        jsonObject.addProperty(
                            "description",
                            gatheringDetails!!.text.toString().trim()
                        )
                        jsonObject.addProperty(
                            "gatheringDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )
                        jsonObject.addProperty("address", locationTitle!!.text.toString().trim())
                        jsonObject.addProperty("city", city)

                        val locationJsonArray = JsonArray()

                        for (i in 0 until latlongArray.size) {

                            locationJsonArray.add(latlongArray[i])

                        }
                        jsonObject.add("location", locationJsonArray)

                        postDataToServer(jsonObject)

                        println("post data $jsonObject")


                    }
                } else*/ if (details.isNotEmpty() && gatheringTitle!!.text.isNotEmpty()  && issueId!!.isNotEmpty() && dateTime!!.text.isNotEmpty() && latlongArray.isNotEmpty() && imageArray.isNotEmpty()) {

                    if (ConstantMethods.checkForInternetConnection(this@CreateGatheringActivity)) {

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("issueId", issueId)
                        jsonObject.addProperty("title", gatheringTitle!!.text.toString().trim())
                        jsonObject.addProperty(
                            "description",
                            gatheringDetails!!.text.toString().trim()
                        )
                        jsonObject.addProperty(
                            "gatheringDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )
                        jsonObject.addProperty("address", locationTitle!!.text.toString().trim())
                        jsonObject.addProperty("city", city)

                        val locationJsonArray = JsonArray()

                        for (i in 0 until latlongArray.size) {

                            locationJsonArray.add(latlongArray[i])

                        }
                        jsonObject.add("location", locationJsonArray)


                        //image array
                        val imageJsonArray = JsonArray()

                        for (i in 0 until imageArray.size) {

                            imageJsonArray.add(imageArray[i])

                        }


                        jsonObject.add("imageUrls", imageJsonArray)



                        if (count == mediaPojo.size) {

                            postDataToServer(jsonObject)
                        } else {
                            count = 0
                            ConstantMethods.showWarning(
                                this,
                                "Please wait",
                                "Please wait media uploading."
                            )
                        }

                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    /** It takes the json object as input and send to onGatheringCreated function of create gathering presenter */
    private fun postDataToServer(jsonObject: JsonObject) {

        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                createGatheringPresenter!!.onGatheringCreated(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image view */
        back = findViewById(R.id.img_back)
        /* close = findViewById(R.id.img_close)
         closeGallery = findViewById(R.id.img_close_option)*/

        /** ids of text view */
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        locationTitle = findViewById(R.id.txt_location_title)
        locationTitle!!.text = ""
        locationTitle!!.hint = resources.getString(R.string.gathering_issue_location_hint)
        locationTitle!!.typeface = ConstantFonts.raleway_semibold

        addLocation = findViewById(R.id.txt_add_location)
        addLocation!!.typeface = ConstantFonts.raleway_semibold

        mediaTitle = findViewById(R.id.txt_media_title)
        mediaTitle!!.typeface = ConstantFonts.raleway_semibold


        or = findViewById(R.id.txt_or)
        or!!.typeface = ConstantFonts.raleway_regular

        /* mediaOr = findViewById(R.id.txt_media_or)
         mediaOr!!.typeface = ConstantFonts.raleway_regular

         mediaOrOption = findViewById(R.id.txt_media_or_option)
         mediaOrOption!!.typeface = ConstantFonts.raleway_regular
 */

        /** ids of edit text */
        gatheringTitle = findViewById(R.id.et_gathering_title)
        gatheringTitle!!.typeface = ConstantFonts.raleway_semibold

        gatheringDetails = findViewById(R.id.et_gathering_details)
        gatheringDetails!!.typeface = ConstantFonts.raleway_semibold

        dateTime = findViewById(R.id.et_date_time)
        dateTime!!.typeface = ConstantFonts.raleway_semibold

        linkIssueTitle = findViewById(R.id.et_link_issue_title)
        linkIssueTitle!!.typeface = ConstantFonts.raleway_semibold

        /** ids of button */
        create = findViewById(R.id.btn_create)
        create!!.typeface = ConstantFonts.raleway_semibold

        useCamera = findViewById(R.id.btn_use_camera)
        useCamera!!.typeface = ConstantFonts.raleway_semibold

        useGallery = findViewById(R.id.btn_browse_file)
        useGallery!!.typeface = ConstantFonts.raleway_semibold

        /* image = findViewById(R.id.btn_image)
         image!!.typeface = ConstantFonts.raleway_semibold

         video = findViewById(R.id.btn_video)
         video!!.typeface = ConstantFonts.raleway_semibold

         imageGallery = findViewById(R.id.btn_image_option)
         imageGallery!!.typeface = ConstantFonts.raleway_semibold

         videoGalley = findViewById(R.id.btn_video_option)
         videoGalley!!.typeface = ConstantFonts.raleway_semibold*/

        /** ids of recycler view */
        mediaRecycler = findViewById(R.id.recycler_media)
        mediaRecycler!!.layoutManager = GridLayoutManager(this, 3)

        /** ids of text input layout*/
        linkIssueLayout = findViewById(R.id.linkIssueLayout)

    }

    /** It opens respected activity */
    override fun goToNextScreen() {
        if(isEdit)
        {
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
            count = 0

            val intent = Intent(this, GatheringDetailsActivity::class.java)
            intent.putExtra("gatheringId", gatheringDetailsData!!.data._id)
            intent.putExtra("isEdit",true)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else if (isIssue)
        {
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
            count = 0

            val intent = Intent(this, NearByIssueDetailsActivity::class.java)
            intent.putExtra("issueId", issueId)
            setResult(Activity.RESULT_OK, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        }

        else if (isIssueList)
        {
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
            count = 0

            val intent = Intent(this, NearByIssueActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        }


        else {
            val intent = Intent(this, GatheringActivity::class.java)
            intent.putExtra("createGathering",true)
            startActivityForResult(intent, Activity.RESULT_OK)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

            count = 0
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
        }
    }

    /** It get image url which selected from camera or gallery and set it to media adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)

        var bitmap : Bitmap


        if (requestCode == GALLERY) {
            if(data!=null) {
                mMediaUri = data!!.getData()
                var imageStream: InputStream? = null
                try {
                    imageStream = context!!.getContentResolver().openInputStream(mMediaUri!!)

                    /* var file1 = File(mMediaUri!!.getPath())
                var bt: Bitmap
                bt = BitmapFactory.decodeStream(imageStream)
                bt = ConstantMethods.imageOrientationValidator(bt, file1.absolutePath)
                file1 = ConstantMethods.compressImage(context, bt)*/

                    //val path = RealPathUtils.getPath(context!!, mMediaUri!!)
                    val path = FileUtils().getRealPath(context!!, mMediaUri!!)

                    var file1: File? = null
                    if (path != null) {
                        var compressedPath = ConstantMethods.getCompressImage(context!!, path)
                        if (compressedPath != null) {
                            file1 = File(compressedPath)
                        } else {
                            file1 = RealPathUtils.getFile(context!!, mMediaUri!!)
                        }

                    } else {
                        file1 = RealPathUtils.getFile(context!!, mMediaUri!!)

                    }


                    var length: Long? = file1!!.length()
                    length = length!! / 1024

                    //5mb in kb
                    /* if (length > 5120) run {
                    ConstantMethods.showError(context,"Large Image Size","Maximum image allowed is 5 MB")

                }
                else
                {*/

                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            file1.absolutePath,
                            "",
                            "image",
                            false
                        )
                    )
                    val json1 = Gson().toJson(mediaStatusArray!!)
                    EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA,json1)

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                    // }


                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }

        }



        else if (requestCode == CAMERA) {
            if (mMediaUri != null) {
                try {
                    //var file = File(mMediaUri!!.getPath())

                    /* bitmap = BitmapFactory.decodeFile(mMediaUri!!.getPath())

                     bitmap =
                         ConstantMethods.imageOrientationValidator(bitmap, mMediaUri!!.getPath())

                     file = ConstantMethods.compressImage(context, bitmap)*/

                    //val path = RealPathUtils.getPath(context!!,mMediaUri!!)
                    val path = FileUtils().getRealPath(context!!,mMediaUri!!)

                    var file1 : File?= null
                    if(path!=null)
                    {
                        var compressedPath = ConstantMethods.getCompressImage(context!!,path)
                        if(compressedPath!=null) {
                            file1 = File(compressedPath)
                        }
                        else{
                            file1 = RealPathUtils.getFile(context!!, mMediaUri!!)
                        }

                    }
                    else{
                        file1 = RealPathUtils.getFile(context!!,mMediaUri!!)

                    }

                    var length: Long? = file1!!.length()
                    length = length!! / 1024

                    //decodeFile(picturePath);
                    //5mb in kb
                    /* if (length > 5120) {
                         ConstantMethods.showError(
                             context,
                             "Large Image Size",
                             "Maximum image allowed is 5 MB"
                         )


                     } else {
 */

                    mediaStatusArray!!.add(MediaUploadingPojo(file1.absolutePath,"","image",false))

                    val json1 = Gson().toJson(mediaStatusArray!!)
                    EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA,json1)

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter

                    EasySP.init(context).putString("image", fileUri)

                    // mediaPresenter!!.onFileUpload(context!!, file.absolutePath!!)
                    //}

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }

        else if(requestCode == 5)
        {
            getSharedData()
        }


    }




    /** It is runtime permission result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            RequestPermissionCode -> if (grantResults.size > 0) {
                val StoragePermission = (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if (StoragePermission) {
                    Toast.makeText(
                        context, "Permission Granted",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context, "Permission Denied ", Toast.LENGTH_LONG).show()

                }
            }
        }
    }



    /** It goes back to previous activity of fragment   */
    override fun onBackPressed() {
        super.onBackPressed()
        EasySP.init(this).putString(ConstantEasySP.GATHERING_DATE,"")
        EasySP.init(this).putString(ConstantEasySP.SELECTED_GATHERING_ADDRESS,"")
        EasySP.init(this).putString("gatheringTitle","")
        EasySP.init(this).putString("gatheringDetails","")
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        // finish()
    }
}
