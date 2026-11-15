package gg.realmforge.client

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(12, 17, 25)
        window.navigationBarColor = Color.rgb(12, 17, 25)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32); setBackgroundColor(Color.rgb(12,17,25))
        }
        fun text(value:String, size:Float, color:Int, bold:Boolean=false):TextView =
            TextView(this).apply { text=value; textSize=size; setTextColor(color); gravity=Gravity.CENTER; if(bold) setTypeface(null,1); setPadding(0,12,0,12) }
        root.addView(text("REALMFORGE",32,Color.WHITE,true))
        root.addView(text("BEDROCK • ANDROID",14,Color.rgb(90,190,255),true))
        root.addView(text("Independent client project",18,Color.LTGRAY))
        root.addView(text("Build status: foundation only",15,Color.rgb(255,190,80)))
        root.addView(text("Minecraft rendering, Xbox sign-in, Bedrock protocol and Realms relay are not implemented yet.",15,Color.LTGRAY))
        root.addView(text("This app does not connect to Minecraft servers.",14,Color.rgb(255,120,120)))
        setContentView(root)
    }
}
