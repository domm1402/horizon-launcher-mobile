package gg.horizon.launchermobile
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val bg=Color(0xFF0B1117); private val panel=Color(0xFF17232D); private val mint=Color(0xFF65E6B2); private val muted=Color(0xFF9EADB8)
class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{App()}}}
@Composable fun App(){
 val ctx=LocalContext.current; var java by remember{mutableStateOf(false)}; var page by remember{mutableStateOf(0)}; var message by remember{mutableStateOf("")}
 fun launch(){val p=if(java)"gg.horizon.client" else "com.mojang.minecraftpe";val i=ctx.packageManager.getLaunchIntentForPackage(p);if(i!=null)ctx.startActivity(i) else message=if(java)"Horizon Java client isn't installed." else "Minecraft Bedrock isn't installed. Install it from Google Play."}
 MaterialTheme(colorScheme=darkColorScheme(primary=mint,background=bg,surface=panel,onSurface=Color.White)){
 Scaffold(containerColor=bg,bottomBar={NavigationBar(containerColor=panel){listOf("Home","Versions","Settings","About").forEachIndexed{n,s->NavigationBarItem(selected=page==n,onClick={page=n},icon={Icon(listOf(Icons.Default.Home,Icons.Default.Layers,Icons.Default.Settings,Icons.Default.Info)[n],null)},label={Text(s)},colors=NavigationBarItemDefaults.colors(selectedIconColor=mint,selectedTextColor=mint,indicatorColor=Color(0xFF20313D)))}}}){pad->
 Column(Modifier.fillMaxSize().padding(pad).padding(20.dp),verticalArrangement=Arrangement.spacedBy(16.dp)){
 Row(verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Terrain,null,tint=mint,modifier=Modifier.size(35.dp));Column{Text("HORIZON",fontWeight=FontWeight.Black,letterSpacing=3.sp);Text("LAUNCHER MOBILE",fontSize=10.sp,color=muted,letterSpacing=2.sp)}}
 when(page){
 0->{Text("YOUR NEXT",color=muted,letterSpacing=3.sp);Text("WORLD AWAITS.",fontSize=29.sp,fontWeight=FontWeight.Black)
 Row(Modifier.fillMaxWidth().background(panel,RoundedCornerShape(15.dp)).padding(5.dp)){listOf("BEDROCK","JAVA").forEachIndexed{n,s->Box(Modifier.weight(1f).background(if(java==(n==1))mint else Color.Transparent,RoundedCornerShape(12.dp)).clickable{java=n==1}.padding(14.dp),contentAlignment=Alignment.Center){Text(s,color=if(java==(n==1))bg else muted,fontWeight=FontWeight.Bold)}}}
 Box(Modifier.fillMaxWidth().height(180.dp).background(Color(0xFF203A3B),RoundedCornerShape(20.dp)).padding(18.dp)){Column(Modifier.align(Alignment.BottomStart)){Text(if(java)"HORIZON" else "MINECRAFT",color=mint,letterSpacing=2.sp);Text(if(java)"Java Edition" else "Bedrock Edition",fontSize=25.sp,fontWeight=FontWeight.Bold);Text("Your games, one place",color=muted)}}
 Button(onClick={launch()},Modifier.fillMaxWidth().height(56.dp),colors=ButtonDefaults.buttonColors(containerColor=mint)){Icon(Icons.Default.PlayArrow,null,tint=bg);Text(if(java)"  PLAY JAVA" else "  PLAY BEDROCK",color=bg,fontWeight=FontWeight.Black)}
 CardRow("Minecraft Bedrock","Official Google Play version"){ctx.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=com.mojang.minecraftpe")))}
 CardRow("Horizon Java","Android community client"){ctx.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/Vasysik/horizon-android/releases")))}
 }
 1->{Text("GAME VERSIONS",color=mint,letterSpacing=2.sp);Text("This launcher opens installed game clients. It does not download game binaries.",color=muted);CardRow("Bedrock","Latest version managed by Minecraft"){};CardRow("Java","Client version managed by Horizon"){}}
 2->{Text("SETTINGS",color=mint,letterSpacing=2.sp);CardRow("Install status","Detected when launching"){};CardRow("Privacy","No account data collected by this launcher"){}}
 else->{Text("ABOUT HORIZON",color=mint,letterSpacing=2.sp);Text("A clean Android hub for Minecraft Bedrock and Horizon Java.",fontSize=18.sp);Text("Launcher shell only. Minecraft is not bundled; install compatible game clients separately.",color=muted);CardRow("Source code","GitHub project"){ctx.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/tiktoknotificationtroll-wq/horizon-launcher-mobile")))}}
 }
 }
 }
 if(message.isNotEmpty())AlertDialog(onDismissRequest={message=""},title={Text("Horizon")},text={Text(message)},confirmButton={TextButton(onClick={message=""}){Text("OK")}})
 }}
@Composable fun CardRow(title:String,sub:String,onClick:()->Unit){Row(Modifier.fillMaxWidth().background(panel,RoundedCornerShape(14.dp)).clickable(onClick=onClick).padding(15.dp),verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Apps,null,tint=mint);Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.SemiBold);Text(sub,color=muted,fontSize=12.sp)};Icon(Icons.Default.ChevronRight,null,tint=muted)}}
