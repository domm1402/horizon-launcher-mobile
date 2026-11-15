package gg.realmforge.client
import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*
class MainActivity:Activity(){
 private val bg=Color.rgb(12,17,25);private val panel=Color.rgb(22,30,42);private val blue=Color.rgb(80,175,255);private lateinit var body:LinearLayout
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);window.statusBarColor=bg;window.navigationBarColor=bg;show("Home")}
 private fun label(s:String,z:Float=15f,c:Int=Color.LTGRAY,b:Boolean=false)=TextView(this).apply{text=s;textSize=z;setTextColor(c);gravity=Gravity.CENTER_VERTICAL;setPadding(0,10,0,10);if(b)setTypeface(null,Typeface.BOLD)}
 private fun button(s:String,a:()->Unit)=Button(this).apply{text=s;setTextColor(Color.WHITE);setBackgroundColor(panel);setOnClickListener{a()}}
 private fun show(page:String){
  val root=LinearLayout(this).apply{orientation=1;setBackgroundColor(bg);setPadding(22,16,22,12)}
  root.addView(label("REALMFORGE",25f,Color.WHITE,true));root.addView(label("BEDROCK CLIENT • ANDROID",11f,blue,true))
  val scroll=ScrollView(this);body=LinearLayout(this).apply{orientation=1};scroll.addView(body);root.addView(scroll,LinearLayout.LayoutParams(-1,0,1f))
  when(page){
   "Home"->{body.addView(label("Welcome to RealmForge",23f,Color.WHITE,true));body.addView(label("An independent Bedrock client project."));card("Account","Microsoft/Xbox sign-in integration is not implemented.");card("Play","Rendering and Bedrock session engine are under development.");card("Realms","Realms discovery and NetherNet relay are not available yet.");card("Local worlds","World browser and storage engine not implemented.")}
   "Play"->{body.addView(label("Play",22f,Color.WHITE,true));card("Worlds","Local world creation, loading and saving require the voxel engine.");card("Servers","Bedrock server list and protocol session are not implemented.");card("Realms","Requires Microsoft identity, entitlement checks, Realms API and NetherNet.")}
   "Account"->{body.addView(label("Account",22f,Color.WHITE,true));val auth=BedrockAccount(this);body.addView(label(auth.name()?.let{"Xbox: $it"}?:"Not signed in",17f,Color.WHITE,true));body.addView(button(if(auth.name()==null)"Sign in with Microsoft" else "Sign out"){if(auth.name()!=null){auth.signOut();show("Account")}else{val status=TextView(this).apply{text="Waiting for Microsoft…";setTextColor(Color.WHITE);textSize=14f};body.addView(status);auth.login({message->runOnUiThread{status.text=message}}, {result->runOnUiThread{status.text=result.message;Toast.makeText(this,result.message,Toast.LENGTH_LONG).show()}})}});card("Multiplayer gate",if(auth.ownsGame())"Minecraft ownership verified on this device." else "Multiplayer stays locked until ownership is verified.");card("Device-code login","The app displays a Microsoft code. Complete it on microsoft.com/link.")}
   "Settings"->{body.addView(label("Settings",22f,Color.WHITE,true));card("Graphics","Vulkan renderer, frame pacing and chunk optimisation not implemented.");card("Controls","Touch controls will be configurable once gameplay exists.");card("Storage","World/resource-pack storage is not wired up.")}
   else->{body.addView(label("Project status",22f,Color.WHITE,true));card("Current build","Android UI shell only. Not a playable Minecraft client.");card("Roadmap","1. Auth + verified entitlement\n2. Bedrock protocol/session encryption\n3. NetherNet WebRTC + TURN\n4. Voxel renderer + chunks\n5. Touch gameplay + local worlds\n6. Realms integration and testing")}
  }
  val nav=LinearLayout(this).apply{orientation=0};listOf("Home","Play","Account","Settings","About").forEach{p->nav.addView(button(p){show(p)},LinearLayout.LayoutParams(0,52,1f))};root.addView(nav);setContentView(root)
 }
 private fun card(t:String,d:String){val box=LinearLayout(this).apply{orientation=1;setPadding(16,10,16,10);setBackgroundColor(panel)};box.addView(label(t,17f,Color.WHITE,true));box.addView(label(d,14f));val lp=LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,10,0,8);body.addView(box,lp)}
}