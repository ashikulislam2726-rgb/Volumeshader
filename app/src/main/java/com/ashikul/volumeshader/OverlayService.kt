package com.ashikul.volumeshader

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

class OverlayService : Service() {
    private lateinit var wm: WindowManager
    private lateinit var audio: AudioManager
    private var overlay: View? = null
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) { stopSelf(); return START_NOT_STICKY }
        if (overlay != null) return START_STICKY
        audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(12,12,12,12); background=shape(0xee071126.toInt(),22,0xff00ddff.toInt()) }
        val header = LinearLayout(this).apply { gravity=Gravity.CENTER_VERTICAL }
        header.addView(TextView(this).apply { text="✦ NEON SHADER"; textSize=12f; setTextColor(Color.WHITE); typeface=Typeface.DEFAULT_BOLD }, LinearLayout.LayoutParams(0,-2,1f))
        header.addView(TextView(this).apply { text="×"; textSize=25f; setTextColor(0xffff43df.toInt()); setPadding(10,0,6,0); setOnClickListener { stopSelf() } })
        root.addView(header)
        root.addView(TextView(this).apply { text="VOLUME"; setTextColor(0xff00ddff.toInt()); textSize=12f })
        root.addView(SeekBar(this).apply {
            max=audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC); progress=audio.getStreamVolume(AudioManager.STREAM_MUSIC)
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, user: Boolean) { if (user) audio.setStreamVolume(AudioManager.STREAM_MUSIC,p,0) }
                override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        })
        root.addView(TextView(this).apply { text="BRIGHTNESS"; setTextColor(0xffffd447.toInt()); textSize=12f })
        root.addView(SeekBar(this).apply {
            max=255; progress=170
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, user: Boolean) {
                    if (user && Settings.System.canWrite(this@OverlayService)) Settings.System.putInt(contentResolver,Settings.System.SCREEN_BRIGHTNESS,p.coerceAtLeast(1))
                }
                override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        })
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT).apply { gravity=Gravity.CENTER_VERTICAL or Gravity.RIGHT; x=16 }
        overlay=root
        try { wm.addView(root,params) } catch (_: Exception) { overlay=null; stopSelf() }
        return START_STICKY
    }
    override fun onDestroy() { overlay?.let { try { wm.removeView(it) } catch (_: Exception) {} }; overlay=null; super.onDestroy() }
    private fun shape(color:Int,radius:Int,stroke:Int)=GradientDrawable().apply { setColor(color); cornerRadius=radius.toFloat(); setStroke(2,stroke) }
}