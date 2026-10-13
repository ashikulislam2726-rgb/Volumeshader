package com.ashikul.volumeshader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private val bg = Color.rgb(5, 10, 25)
    private val panel = Color.rgb(14, 23, 47)
    private val cyan = Color.rgb(0, 220, 255)
    private val purple = Color.rgb(190, 50, 255)
    private lateinit var audio: android.media.AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audio = getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(22, 28, 22, 28); setBackgroundColor(bg) }
        scroll.addView(root); setContentView(scroll)
        root.addView(label("VOLUME SHADER", 29, Color.WHITE, true))
        root.addView(label("NEON GAMING CONTROL CENTER • v1.0.0", 12, cyan, true))
        root.addView(space(16))
        val card = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(18,18,18,18); background = rounded(panel, 22, purple) }
        root.addView(card, matchWrap())
        card.addView(label("AUDIO VOLUME", 16, cyan, true))
        val max = audio.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
        card.addView(SeekBar(this).apply {
            this.max = max; progress = audio.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, user: Boolean) { if (user) audio.setStreamVolume(android.media.AudioManager.STREAM_MUSIC, p, 0) }
                override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }, matchWrap())
        card.addView(label("APP BRIGHTNESS", 16, Color.YELLOW, true))
        card.addView(SeekBar(this).apply {
            max = 255; progress = 170
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(s: SeekBar?, p: Int, user: Boolean) { if (user) window.attributes = window.attributes.apply { screenBrightness = (p.coerceAtLeast(1) / 255f) } }
                override fun onStartTrackingTouch(s: SeekBar?) {}; override fun onStopTrackingTouch(s: SeekBar?) {}
            })
        }, matchWrap())
        root.addView(space(10))
        root.addView(label("FLOATING NEON OVERLAY", 18, Color.WHITE, true))
        root.addView(label("Custom volume and brightness controls over other apps.", 14, 0xffb7c8e8.toInt(), false))
        root.addView(action("①  ALLOW DISPLAY OVER OTHER APPS") {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            else Toast.makeText(this, "Overlay permission already enabled", Toast.LENGTH_SHORT).show()
        }, matchWrap())
        root.addView(action("②  ALLOW SYSTEM BRIGHTNESS CONTROL") {
            if (!Settings.System.canWrite(this)) startActivity(Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:$packageName")))
            else Toast.makeText(this, "Brightness permission enabled", Toast.LENGTH_SHORT).show()
        }, matchWrap())
        root.addView(action("✦  START NEON OVERLAY") {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Allow overlay permission first", Toast.LENGTH_LONG).show()
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else { startService(Intent(this, OverlayService::class.java)); Toast.makeText(this, "Overlay started • tap × to close", Toast.LENGTH_SHORT).show() }
        }, matchWrap())
        root.addView(action("STOP OVERLAY") { stopService(Intent(this, OverlayService::class.java)) }, matchWrap())
    }
    private fun action(text: String, click: () -> Unit) = label(text, 14, Color.WHITE, true).apply {
        gravity = Gravity.CENTER; setPadding(14,17,14,17); background = rounded(0xff111d3b.toInt(), 15, cyan); setOnClickListener { click() }
    }
    private fun label(text: String, size: Int, color: Int, bold: Boolean) = TextView(this).apply { this.text=text; textSize=size.toFloat(); setTextColor(color); if (bold) typeface=Typeface.DEFAULT_BOLD; setPadding(2,5,2,5) }
    private fun space(h: Int) = View(this).apply { layoutParams=LinearLayout.LayoutParams(1,h) }
    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply { bottomMargin=10 }
    private fun rounded(color: Int, radius: Int, stroke: Int) = android.graphics.drawable.GradientDrawable().apply { setColor(color); cornerRadius=radius.toFloat(); setStroke(2,stroke) }
}