package com.video.tamas.Fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions.placeholderOf
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.like.LikeButton
import com.like.OnLikeListener
import com.video.tamas.Activities.*
import com.video.tamas.Adapters.CommentListRecyclerAdapter
import com.video.tamas.JsonParsingClass.ParcingUtils
import com.video.tamas.Models.HomePopularModel
import com.video.tamas.R
import com.video.tamas.Utils.*
import kotlinx.android.synthetic.main.comment_layout.view.*
import kotlinx.android.synthetic.main.fragment_exo_player.*
import org.apache.commons.lang3.StringEscapeUtils
import org.json.JSONException
import org.json.JSONObject
import java.io.File


@SuppressLint("ValidFragment")
class ExoPlayerFragment(var from: String, var firstPosition: Int, var videoDetail: HomePopularModel) : Fragment(), Player.EventListener, View.OnClickListener {

    private var sFlag: Boolean = true
    var userId: String = ""
    lateinit var resourceManager: DeviceResourceManager
    var playPauseHandler: Handler? = null
    lateinit var networkUtils: NetworkUtils
    var isPlay = false
    var pCount = 0
    var videoUri: String? = ""
    var player: SimpleExoPlayer? = null
    var mHandler: Handler? = null
    var mRunnable: Runnable? = null
    var isPlayerReddy = false
    var isPlayerVisible = false
    lateinit var mActivty: Activity
    lateinit var commentView: View
    lateinit var dialog: BottomSheetDialog
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isPlayerVisible = isVisibleToUser
        Log.v("dip", "inside visible. before if" + isPlayerVisible)
        if (isVisibleToUser) {
            //&& isPlayerReddy
            Log.v("dip", "inside visible.")

            //videoFullScreenPlayer.setPlayer(player)
            //  getVideoUriPath()
        } else {
            releasePlayer()
        }
    }


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        Log.v("dip", "inside on create fragmeent")

        return inflater.inflate(R.layout.fragment_exo_player, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivty = activity!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("dip", "inside onViewCreated")
        // videoUri="https://androidwave.com/media/androidwave-video-3.mp4"
        resourceManager = DeviceResourceManager(requireContext())
        userId = resourceManager.getDataFromSharedPref(Config.USER_ID, "")
        networkUtils = NetworkUtils(requireContext())
        initial()
        exo_playerView
        //videoUri = "http://meraatalent.com/upload/Vedio/1553590902282674.mp4"
        //videoUri =getVideoUri()

        /*if(AppSharedPreference(requireContext()).getString("hand").isEmpty()){
            val animation = AnimationUtils.loadAnimation(requireContext(),
             R.anim.slide_from_bottom);
            AppSharedPreference(requireContext()).saveString("hand","true")
        }*/

        setTextAnimation()
        isPlayerReddy = true
        Log.v("dip", "fragment number :" + firstPosition)

        if (firstPosition == 0 && PopularFragment.currenpage == 0 && PopularFragment.isPopulerFragmentVisible) {
            if (player != null) {
                releasePlayer()

            }
            if (player == null) {
                getVideoUriPath()
            }
        }
        val comments = StringEscapeUtils.unescapeJava(videoDetail.commentStatus)
        if (comments == "1") {
            ivComment.setImageResource(R.mipmap.ic_comment)
        } else {
            ivComment.setImageResource(R.drawable.ic_block)
        }

    }

    private fun showCase() {

        /* // single example
         MaterialShowcaseView.Builder(requireActivity())
                 .setTarget(imgLike)
                 .setDismissText("GOT IT")
                 .setContentText("This is some amazing feature you should know about")
                 //.setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
                 //.singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                 .show();*/

        /* //For Swipping Guide Animation
         TutoShowcase.from(requireActivity())
                 .setContentView(R.layout.tuto_sample) //a view in actionbar
                 .on(videoFullScreenPlayer)
                 .displayScrollable()
                 .show()*/


    }

    public fun getVideoUriPath() {
        if (progressBarOne != null)
            progressBarOne.visibility = View.VISIBLE
        PopularFragment.pageChangeStatus = -1
        //setTextAnimation()

        if (play_pause_img != null) {
            play_pause_img.visibility = View.INVISIBLE
        }

        if (!videoDetail.videoUrl.isEmpty()) {
            videoUri = videoDetail.videoUrl

            setUp()


        }
    }

    private fun setTextAnimation() {

        try {

            val animation = AnimationUtils.loadAnimation(context, R.anim.round)
            ivSong_tone.startAnimation(animation)
        } catch (e: Exception) {
            Log.v("dip", "text Animtionerror :" + e.message)

        }


    }

    private fun initial() {

        play_pause_img.visibility = View.INVISIBLE
        tvUserProfileId.text = videoDetail.userProfileName
        val fromServerUnicodeDecoded = StringEscapeUtils.unescapeJava(videoDetail.videoDescription)
        tvVideoDescription.text = fromServerUnicodeDecoded
        val songName = videoDetail.videoSongName
        if (songName == "false") {
        } else {
            tvSongName.text = songName
            tvSongName.setSelected(true)
            tvSongName.setEllipsize(TextUtils.TruncateAt.MARQUEE)
            tvSongName.setSingleLine(true)
            tvSongName.setMarqueeRepeatLimit(-1)
            tvSongName.setFocusableInTouchMode(true)
            tvSongName.setFocusable(true)
        }
        tvShareCount.setText(videoDetail.totalShareCount)
        tvViewCount.setText(videoDetail.videoLikesCount)
        tvLikeCount.text = videoDetail.totalLikeCount
        tvCommentCount.text = videoDetail.totalCommentCount


        if (from == "selfProfile") {
            if (videoDetail.userProfileId == userId) {
                ivDelete.visibility = View.VISIBLE
                rlSongTone.visibility = View.VISIBLE
                ivProfilePic.visibility = View.GONE
            } else {
                ivDelete.visibility = View.GONE
                rlSongTone.visibility = View.VISIBLE
                ivProfilePic.visibility = View.VISIBLE
            }


        } else if (from == "category") {
            rlSongTone.visibility = View.GONE
            ivDelete.visibility = View.GONE
        } else {
            ivDelete.visibility = View.GONE
            ivProfilePic.visibility = View.VISIBLE
            rlSongTone.visibility = View.VISIBLE
        }
        if (videoDetail.userProfileId == userId) {
            ivFollow.setVisibility(View.GONE)
        } else {
            ivFollow.setVisibility(View.VISIBLE)
        }
        if (videoDetail.isFollow == "1") {
            Glide.with(requireContext())
                    .load(R.drawable.ic_right_member)
                    .into(ivFollow)

        } else {
            Glide.with(requireContext())
                    .load(R.drawable.ic_add_user)
                    .into(ivFollow)
        }

        Glide.with(requireContext())
                .load(videoDetail.videoProfileImage)
                .apply(placeholderOf(R.drawable.progress_animation).transform(CircleCrop()).error(R.mipmap.ic_person))
                .into(ivProfilePic)
//        holder.ivSongImage.setImageResource(R.drawable.circle);
//         holder.ivProfilePic.setBackgroundResource(R.drawable.circle_imageview);
        Glide.with(requireContext())
                .load(videoDetail.songImage)
                .apply(placeholderOf(R.drawable.progress_animation).transform(CircleCrop()).error(R.drawable.ic_cd))
                .into(ivSong_tone)
        if (!TextUtils.isEmpty(userId)) {
            val isLike = videoDetail.isLike
            if (isLike == "1") {
                ivLike.setLiked(true)
            } else {
                ivLike.setLiked(false)
            }

        } else {
            ivLike.setLiked(false)
        }


        //sId = AppSharedPreference(requireContext()).getString("id")

        clickLisner()
    }

    private fun clickLisner() {
        //For Like ClickLIsner

        ivLike.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid")
                    likeVideoApi(videoDetail.videoId, "1")

                } else {
                    Log.wtf("userid", "notuserid")
                    requireActivity().startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }

            }

            override fun unLiked(likeButton: LikeButton) {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid")


                    likeVideoApi(videoDetail.videoId, "0")
                } else {
                    Log.wtf("userid", "notuserid")
                    requireActivity().startActivity(Intent(activity, LoginActivity::class.java))
                    requireActivity().finish()
                }
            }
        })

        ivComment.setOnClickListener(this)
        ivFollow.setOnClickListener(this)
        ivShare.setOnClickListener(this)
        //imgSong.setOnClickListener(this)
        ivDelete.setOnClickListener(this)
        play_pause_img.setOnClickListener(this)
        llMain.setOnClickListener(this)
        rlSongTone.setOnClickListener(this)
        ivProfilePic.setOnClickListener(this)
    }


    public fun setUp() {
        initializePlayer()
        if (videoUri == null) {
            return
        }
        buildMediaSource(Uri.parse(videoUri))
    }

    private fun initializePlayer() {
        if (player == null) {
            // 1. Create a default TrackSelector
            /*val loadControl = DefaultLoadControl(
                    DefaultAllocator(true, 16),
                    3000,
                    5000,
                    1500,
                    5000, -1, true
            )*/
            /*
LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    VideoPlayerConfig.MIN_BUFFER_DURATION,
                    VideoPlayerConfig.MAX_BUFFER_DURATION,
                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);
*/

            val bandwidthMeter = DefaultBandwidthMeter()
            val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
            val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(requireContext(), trackSelector)
            player!!.addListener(this)
        } else {
            Log.v("dip", "inside else ")
            releasePlayer()
            initializePlayer()

        }


    }

    private fun buildMediaSource(mUri: Uri) {
        // Measures bandwidth during playback. Can be null if not required.
        val bandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        /*val dataSourceFactory = DefaultDataSourceFactory(
                requireContext(),
                Util.getUserAgent(requireContext(), requireContext().resources.getString(R.string.app_name)), bandwidthMeter
        )*/

        // This is the MediaSource representing the media to be played.
        //val videoSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mUri)
        val dataSourceFactory = CacheDataSourceFactory(
                MyApplication.cache, DefaultHttpDataSourceFactory("MeraaTalent"))

        // This is the MediaSource representing the media to be played.
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoUri))
        // Prepare the player with the source.

        player!!.prepare(mediaSource)

        player!!.setPlayWhenReady(true)
        player!!.addListener(this)
        if (exo_playerView.player != null) {
            exo_playerView.player = null

        }
        exo_playerView.setPlayer(player)

        exo_playerView.setUseController(false)

    }

    public fun releasePlayer() {
        if (playPauseHandler != null) {
            playPauseHandler!!.removeCallbacksAndMessages(null)

        }
        Log.e("Jay", "in Destroy 0")

        if (player != null) {
            player!!.playWhenReady = false
            player!!.release()
            player = null
            Log.e("Jay", "in Destroy1");
            if (exo_playerView != null) {
                exo_playerView.player = null
                Log.e("Jay", "in Destroy2")
            }
        }
    }

    public fun pausePlayer() {
        if (player != null) {
            player!!.setPlayWhenReady(false)
            player!!.getPlaybackState()
        }
    }

    public fun resumePlayer() {
        if (player != null) {
            player!!.setPlayWhenReady(true)
            player!!.getPlaybackState()

        }
    }

    override fun onResume() {
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
        pausePlayer()
        if (mRunnable != null) {
            mHandler!!.removeCallbacks(mRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Jay", "in Destroy Exo")
        releasePlayer()
        resourceManager.addToSharedPref("CATEGORY_PAGE_LIST", false)

    }

    override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {

    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {

            Player.STATE_BUFFERING -> {
            }
            Player.STATE_ENDED -> {
                {


                }
                // Activate the force enable
                player!!.seekTo(0)
                if (pCount == 0) {
                    //  Toast.makeText(requireContext(), "Video Completed", Toast.LENGTH_SHORT).show()
                    pCount++
                }



                isPlay = false
            }
            Player.STATE_IDLE -> {
            }
            Player.STATE_READY -> {
                if (progressBarOne != null)
                    progressBarOne.visibility = View.GONE
                addVideoCount(videoDetail.videoId)
                isPlay = true
            }
            else -> {
            }
        }// status = PlaybackStatus.IDLE;
    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onPlayerError(error: ExoPlaybackException) {

    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {

    }

    override fun onSeekProcessed() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivFollow -> {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid")
                    followApi(videoDetail.userProfileId)
                } else {
                    Log.wtf("userid", "notuserid")
                    requireActivity().startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
            R.id.ivDelete -> {
                deleteConfirm()
            }
            R.id.ivComment -> {
                if (videoDetail.commentStatus == "1") {
                    getCommentList()
                } else {
                    showNoCommentDialog()
                }
            }
            R.id.ivShare -> {
                if (!TextUtils.isEmpty(userId)) {
                    Log.wtf("userid", "userid")
                    val sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val fileName = videoDetail.videoUrl.substring(videoDetail.videoUrl.lastIndexOf('/') + 1)
                    val myFile = File(sdcard, fileName)
                    if (myFile.exists()) {
                        shareVideo("Tamas", myFile.absolutePath)
                        addShareCount(videoDetail.videoId)
                    } else {
                        videoDownload(videoDetail.videoUrl)
                        addShareCount(videoDetail.videoId)
                    }
                } else {
                    Log.wtf("userid", "notuserid")
                    requireActivity().startActivity(Intent(activity, LoginActivity::class.java))
                }
            }
            R.id.ivProfilePic -> {
                val intent = Intent(requireContext(), ViewUserProfileActivity::class.java)
                intent.putExtra("userId", videoDetail.userProfileId)
                intent.putExtra("isFollow", videoDetail.isFollow)
                intent.putExtra("userName", videoDetail.userProfileName)
                activity!!.startActivity(intent)
            }
            R.id.play_pause_img -> {
                if (sFlag == false) {
                    play_pause_img.visibility = View.GONE
                    resumePlayer()
                    sFlag = true
                }
            }
            R.id.rlSongTone -> {
                val intent = Intent(requireContext(), VideoListBySongIdActivity::class.java)
                intent.putExtra("songId", videoDetail.videoSongId)
                activity!!.startActivity(intent)

            }
            R.id.llMain -> {
                Log.v("dip", "click on screen")

                if (sFlag) {
                    playPauseHandler = Handler()
                    playPauseHandler!!.postDelayed(object : Runnable {
                        override fun run() {
                            if (PopularFragment.pageChangeStatus == -1) {
                                play_pause_img.visibility = View.VISIBLE
                                pausePlayer()
                                sFlag = false
                            } else {
                                PopularFragment.pageChangeStatus = -1

                            }

                        }


                    }, 600)
                } else {
                    play_pause_img.visibility = View.GONE
                    resumePlayer()
                    sFlag = true
                }


            }
        }

    }

    private fun deleteConfirm() {
        AppAlerts().showAlertWithAction(requireContext(), "Confirm!", "Do you want to delete this video?", "Yes", "No", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                videoDelete(videoDetail.videoId)
            }


        }, true)

    }

    private fun shareTextUrl() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        @Suppress("DEPRECATION")
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "VidMate")
        share.putExtra(
                Intent.EXTRA_TEXT, "All artist on a single platform. for explore your talent as Artist Or Want to Hire an Artist\n" +
                "You can download app from this link \n" +
                "https://play.google.com/store/apps/details?id=" + context!!.packageName
        )
        startActivity(Intent.createChooser(share, "Share link!"))
    }

    fun likeVideoApi(videoId: String, likeUnLikeStatus: String) {

        val jsonBody = JSONObject()
        try {
            jsonBody.put("user_id", userId)
            jsonBody.put("video_id", videoId)
            Log.wtf("jsonLike", jsonBody.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        networkUtils.postDataVolley(Config.MethodName.like, jsonBody, object : VolleyCallback {
            override fun onSuccessResponse(result: String) {

                try {
                    val jsonObject = JSONObject(result)
                    Log.wtf("likeResponse", jsonObject.toString())
                    val status = jsonObject.optString("status")
                    val message = jsonObject.optString("message")
                    if (status == "success") {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                        if (likeUnLikeStatus == "1") {
                            if (PopularFragment.verticalPagerAdaptor.videoDetailList[firstPosition].totalLikeCount.isEmpty()) {

                                PopularFragment.verticalPagerAdaptor.videoDetailList[firstPosition].totalLikeCount = "1"
                            } else {
                                PopularFragment.verticalPagerAdaptor.videoDetailList[firstPosition].totalLikeCount = (videoDetail.totalLikeCount.toInt() + 1).toString()
                            }
                            PopularFragment.verticalPagerAdaptor.videoDetailList[firstPosition].isLike = likeUnLikeStatus
                            PopularFragment.verticalPagerAdaptor.notifyDataSetChanged()
                            //tvLikeCount.text = (videoDetail//.Like.toInt() -1).toString()
                            tvLikeCount.text = videoDetail.totalLikeCount

                            ivLike.isLiked = true


                        } else if (likeUnLikeStatus == "0") {

                            PopularFragment.verticalPagerAdaptor.videoDetailList[firstPosition].totalLikeCount = (videoDetail.totalLikeCount.toInt() - 1).toString()
                            PopularFragment.verticalPagerAdaptor.videoDetailList[firstPosition].isLike = likeUnLikeStatus
                            PopularFragment.verticalPagerAdaptor.notifyDataSetChanged()
                            //tvLikeCount.text = (videoDetail////.Like.toInt() + 1).toString()
                            tvLikeCount.text = videoDetail.totalLikeCount
                            ivLike.isLiked = false


                        }

                        //getVideo()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailureResponse(result: String) {

            }

        })
    }

    fun getCommentList() {
        val settingsDialog = Dialog(requireContext())
//        settingsDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
//        settingsDialog.setContentView(layoutInflater.inflate(R.layout.gif_progress_dialog, null))
//        val iv = settingsDialog.findViewById<ImageView>(R.id.imageView)
//        Glide.with(requireContext()).load(R.drawable.gif_tamas).into(iv)
//        settingsDialog.show()
//        settingsDialog.setCancelable(false)

        val jsonBody = JSONObject()
        try {
            jsonBody.put("video_id", videoDetail.videoId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        networkUtils.postDataVolley(Config.MethodName.getCommentList, jsonBody, object : VolleyCallback {
            override fun onSuccessResponse(result: String) {
                //settingsDialog.dismiss()
                try {
                    val jsonObject = JSONObject(result)
                    val status = jsonObject.optString("status")
                    commentView = layoutInflater.inflate(R.layout.comment_layout, null)
                    dialog = BottomSheetDialog(requireContext())
                    dialog.setContentView(commentView)
                    val etCommentMessage = commentView.etCommentMessage
                    val btnSendComment = commentView.btnSendComment
                    val ivCancel = commentView.ivCancel
                    btnSendComment.setOnClickListener(View.OnClickListener {
                        if (!TextUtils.isEmpty(userId)) {
                            Log.wtf("userid", "userid")
                            val commentMessage = etCommentMessage.getText().toString()
                            val toServerUnicodeEncoded = StringEscapeUtils.escapeJava(commentMessage)
                            sendComment(toServerUnicodeEncoded, videoDetail.videoId)
                        } else {
                            Log.wtf("userid", "notuserid")
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                        }
                    })
                    ivCancel.setOnClickListener(View.OnClickListener { dialog.dismiss() })

                    if (status != "false") {
                        val jsonArray = jsonObject.getJSONArray("data")
                        try {
                            val commentModelList = ParcingUtils.parseCommentModelList(jsonArray)
                            val commentListRecyclerAdapter = CommentListRecyclerAdapter(requireActivity(), commentModelList)
                            val layoutManager = LinearLayoutManager(requireContext())
                            val rvComment = commentView.rvComment


                            rvComment.setLayoutManager(layoutManager)
                            rvComment.setAdapter(commentListRecyclerAdapter)

                            dialog.show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        val tvErrorMsg = commentView.tvErrorMessage
                        tvErrorMsg.setVisibility(View.VISIBLE)
                        dialog.show()
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailureResponse(result: String) {
                //   settingsDialog.dismiss()
            }

        })
    }

    fun sendComment(commentMessage: String, videoId: String) {
        val materialDialog = MaterialDialog.Builder(requireContext())
                .content("Loading...")
                .cancelable(false)
                .progress(true, 0)
                .show()

        val jsonBody = JSONObject()
        try {
            jsonBody.put("user_id", userId)
            jsonBody.put("video_id", videoId)
            jsonBody.put("comment", commentMessage)
            Log.wtf("json", jsonBody.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        networkUtils.postDataVolley(Config.MethodName.comment, jsonBody, object : VolleyCallback {
            override fun onSuccessResponse(result: String) {
                materialDialog.dismiss()
                try {
                    val jsonObject = JSONObject(result)
                    Log.wtf("response", jsonObject.toString())
                    val status = jsonObject.optString("status")
                    val message = jsonObject.optString("message")
                    if (status == "success") {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        getCommentList()
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailureResponse(result: String) {
                materialDialog.dismiss()
            }

        })
    }

    fun showNoCommentDialog() {
        commentView = layoutInflater.inflate(R.layout.no_comment_layout, null)
        dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(commentView)
        dialog.show()
    }

    fun followApi(userProfileId: String) {
        lazyLoaderExoplayer.visibility = View.VISIBLE
        val jsonBody = JSONObject()
        try {
            jsonBody.put("user_id", userId)
            jsonBody.put("to_follow_id", userProfileId)
            Log.wtf("followJson", jsonBody.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        networkUtils.postDataVolley(Config.MethodName.followUnfollow, jsonBody, object : VolleyCallback {
            override fun onSuccessResponse(result: String) {
                lazyLoaderExoplayer.visibility = View.GONE
                try {
                    val jsonObject = JSONObject(result)
                    Log.wtf("followResponse", jsonObject.toString())
                    val status = jsonObject.optString("status")
                    val message = jsonObject.optString("message")
                    if (status == "success") {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        if (videoDetail.isFollow == "1") {
                            Glide.with(requireContext())
                                    .load(R.drawable.ic_add_user)
                                    .into(ivFollow)

                            PopularFragment.verticalPagerAdaptor.videoDetailList[firstPosition].isFollow = "0"
                            PopularFragment.verticalPagerAdaptor.notifyDataSetChanged()

                        } else {
                            Glide.with(requireContext())
                                    .load(R.drawable.ic_right_member)
                                    .into(ivFollow)
                            PopularFragment.verticalPagerAdaptor.videoDetailList[firstPosition].isFollow = "1"
                            PopularFragment.verticalPagerAdaptor.notifyDataSetChanged()


                        }


                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailureResponse(result: String) {
                lazyLoaderExoplayer.visibility = View.GONE
            }

        })
    }

    fun shareVideo(title: String, path: String) {
        MediaScannerConnection.scanFile(activity, arrayOf(path),
                null) { path, uri ->
            val shareIntent = Intent(
                    android.content.Intent.ACTION_SEND)
            shareIntent.type = "video/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            val msg = "https://play.google.com/store/apps/details?id=" + context!!.packageName + "\nफिल्म इन्डस्ट्री में शुरूवात करें\n" +
                    "घर बैठे अपना टैलेंट (एक्टिंग,डांसिंग,सिंगिंग आदि ) Tamas App>Talent\n" +
                    "कैटेगरी में ऑडिशन विडियो अपलोड करके प्रोडूसर, डायरेक्टर, आदि को दिखाएं और फिल्मों में काम पाऐं .\n" +
                    "घर बैठे ही Tamas App>Entertainment कैटेगरी में शार्ट वीडियो अपलोड करें  और अपने फॉलोवर्स बढ़ाऐं और मज़े करते हुए पैसे भी बनाऐं\n" +
                    "\n" +
                    "तो अब आपको बिना स्ट्रगल किये फिल्मों में काम भी मिलेगा, फेमस भी होंगे और पैसे भी बनाएँगे\n" +
                    "\uD83D\uDC47\uD83C\uDFFBनीचे दिए गए लिंक से ‘Tamas App’ अभी डाउनलोड करें\n"
            shareIntent.putExtra(
                    Intent.EXTRA_TEXT, msg +
                    "https://play.google.com/store/apps/details?id=" + context!!.packageName
            )
            shareIntent
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            requireContext().startActivity(Intent.createChooser(shareIntent, "Share"))
        }
    }

    fun addShareCount(videoId: String) {
        val jsonBody = JSONObject()
        try {
            jsonBody.put("user_id", userId)
            jsonBody.put("video_id", videoId)
            Log.wtf("jsonshare", jsonBody.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        networkUtils.postDataVolley(Config.MethodName.add_share_count, jsonBody, object : VolleyCallback {
            override fun onSuccessResponse(result: String) {
                try {
                    val jsonObject = JSONObject(result)
                    Log.wtf("shareCountResponse", jsonObject.toString())
                    val status = jsonObject.optString("status")
                    val message = jsonObject.optString("message")
                    if (status == "success") {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailureResponse(result: String) {

            }

        })
    }

    fun videoDownload(videoPath: String) {

        var materialDialog = MaterialDialog.Builder(mActivty)
                .content("downloading video ...")
                .cancelable(false)
                .progress(true, 0)
                .show()

        val sdcard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = videoPath.substring(videoPath.lastIndexOf('/') + 1)
        val myDir = File(sdcard, fileName)
        WatermarkUtil.addWaterMarkOnVideo(videoPath, myDir.absolutePath, mActivty, materialDialog)
        Log.wtf("downloadedClicked", "download")


//        Log.wtf("videoPathDir", myDir.absolutePath)
//        com.liulishuo.filedownloader.FileDownloader.setup(requireContext())
//        com.liulishuo.filedownloader.FileDownloader.getImpl().create(videoPath)
//                .setPath(myDir.absolutePath)
//                .setCallbackProgressTimes(300)
//                .setMinIntervalUpdateSpeed(400)
//                .setListener(object : FileDownloadLargeFileListener() {
//                    override fun pending(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
//                        Log.wtf("downloadedStart", "download")
//                    }
//
//                    override fun progress(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
//                        Log.wtf("downloadedProgress", "download")
//                    }
//
//                    override fun paused(task: BaseDownloadTask, soFarBytes: Long, totalBytes: Long) {
//
//                    }
//
//                    override fun completed(task: BaseDownloadTask) {
//                        materialDialog.dismiss()
//                        Log.wtf("downloaded", "download")
//                        Log.wtf("videoPath", myDir.absolutePath)
//                        Log.v("dip", "complete  : ")
//                        shareVideo("Tamas", myDir.absolutePath)
//                    }
//
//                    override fun error(task: BaseDownloadTask, e: Throwable) {
//                        Log.wtf("error", "error")
//                        Log.v("dip", "error : " + e.message)
//
//                    }
//
//                    override fun warn(task: BaseDownloadTask) {
//                        Log.v("dip", "warm error : " + task.errorCause)
//                    }
//                }).start()
    }


    fun videoDelete(videoId: String) {
        val jsonBody = JSONObject()
        try {
            jsonBody.put("video_id", videoId)
            jsonBody.put("user_id", userId)
            Log.v("dip", "Delete request : " + jsonBody.toString());
            Log.wtf("jsonLike", jsonBody.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        networkUtils.postDataVolley(Config.MethodName.videoDelete, jsonBody, object : VolleyCallback {
            override fun onSuccessResponse(result: String) {
                try {
                    val jsonObject = JSONObject(result)
                    Log.wtf("likeResponse", jsonObject.toString())
                    val status = jsonObject.optString("status")
                    val message = jsonObject.optString("message")
                    if (status == "Success") {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        PopularFragment.verticalPagerAdaptor.videoDetailList.removeAt(firstPosition)
                        PopularFragment.verticalPagerAdaptor.notifyDataSetChanged()
                        PopularFragment.videoViewPager.setCurrentItem(firstPosition + 1, true)
                    } else {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailureResponse(result: String) {
                Toast.makeText(requireContext(), "Fail : " + result, Toast.LENGTH_LONG).show()
            }

        })
    }

    fun addVideoCount(videoId: String) {
        val jsonBody = JSONObject()
        try {
            jsonBody.put("user_id", userId)
            jsonBody.put("video_id", videoId)
            Log.wtf("jsonshareVideo", jsonBody.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        networkUtils.postDataVolley(Config.MethodName.add_view_count, jsonBody, object : VolleyCallback {
            override fun onSuccessResponse(result: String) {

                try {
                    val jsonObject = JSONObject(result)
                    Log.wtf("shareCountVideoResponse", jsonObject.toString())
                    val status = jsonObject.optString("status")
                    val message = jsonObject.optString("message")
                    if (status == "success") {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }

            override fun onFailureResponse(result: String) {

            }

        })
    }


}
