package gg.realmforge.client
import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

/** Microsoft device OAuth, Xbox Live/XSTS, Minecraft token and entitlement check. */
class BedrockAccount(context: Context) {
 data class Result(val message:String,val owned:Boolean=false,val token:String?=null)
 private val prefs=context.getSharedPreferences("realmforge_auth",Context.MODE_PRIVATE)
 private val worker=Executors.newSingleThreadExecutor()
 // Register a public Android client in Entra and replace this value before use.
 private val clientId="00000000-0000-0000-0000-000000000000"
 private fun enc(s:String)=URLEncoder.encode(s,"UTF-8")
 private fun post(url:String,form:Map<String,String>):JSONObject {
  val c=URL(url).openConnection() as HttpURLConnection;c.requestMethod="POST";c.connectTimeout=15000;c.readTimeout=20000;c.doOutput=true;c.setRequestProperty("Content-Type","application/x-www-form-urlencoded")
  c.outputStream.use{it.write(form.entries.joinToString("&"){e->enc(e.key)+"="+enc(e.value)}.toByteArray())}
  val stream=if(c.responseCode in 200..299)c.inputStream else c.errorStream
  val j=JSONObject(stream.bufferedReader().use{it.readText()});c.disconnect()
  if(j.has("error"))throw IllegalStateException(j.optString("error_description",j.optString("error")));return j
 }
 private fun get(url:String,bearer:String):JSONObject {
  val c=URL(url).openConnection() as HttpURLConnection;c.connectTimeout=15000;c.readTimeout=20000;c.setRequestProperty("Authorization","Bearer $bearer")
  val stream=if(c.responseCode in 200..299)c.inputStream else c.errorStream
  val j=JSONObject(stream.bufferedReader().use{it.readText()});c.disconnect()
  if(j.has("error"))throw IllegalStateException(j.optString("error_description",j.optString("error")));return j
 }
 fun login(onCode:(String)->Unit,onResult:(Result)->Unit){
  worker.execute{try{
   val dc=post("https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode",mapOf("client_id" to clientId,"scope" to "XboxLive.signin offline_access"))
   onCode(dc.getString("message"));val interval=dc.optInt("interval",5);var elapsed=0;var oauth:JSONObject?=null
   while(elapsed<dc.optInt("expires_in",900)&&oauth==null){Thread.sleep(interval*1000L);elapsed+=interval
    try{oauth=post("https://login.microsoftonline.com/consumers/oauth2/v2.0/token",mapOf("grant_type" to "urn:ietf:params:oauth:grant-type:device_code","client_id" to clientId,"device_code" to dc.getString("device_code")))}
    catch(e:Exception){if(!e.message.orEmpty().contains("authorization_pending")&&!e.message.orEmpty().contains("slow_down"))throw e}
   }
   val access=oauth?.getString("access_token")?:error("Microsoft login timed out")
   val xbl=post("https://user.auth.xboxlive.com/user/authenticate",mapOf("Properties" to JSONObject().put("AuthMethod","RPS").put("SiteName","user.auth.xboxlive.com").put("RpsTicket","d=$access").toString(),"RelyingParty" to "http://auth.xboxlive.com","TokenType" to "JWT"))
   val xblToken=xbl.getString("Token");val uhs=xbl.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs")
   val xs=post("https://xsts.auth.xboxlive.com/xsts/authorize",mapOf("Properties" to JSONObject().put("SandboxId","RETAIL").put("UserTokens",org.json.JSONArray().put(xblToken)).toString(),"RelyingParty" to "rp://api.minecraftservices.com/","TokenType" to "JWT"))
   val mc=post("https://api.minecraftservices.com/authentication/login_with_xbox",mapOf("identityToken" to "XBL3.0 x=$uhs;"+xs.getString("Token")))
   val token=mc.getString("access_token");val ent=get("https://api.minecraftservices.com/entitlements/mcstore",token);val profile=get("https://api.minecraftservices.com/minecraft/profile",token)
   val owned=ent.optJSONArray("items")?.length()?.let{it>0}?:false;val name=profile.optString("name")
   prefs.edit().putString("mc_token",token).putString("player_name",name).putString("player_uuid",profile.optString("id")).putBoolean("owned",owned).apply()
   onResult(Result("Signed in: $name | "+(if(owned)"ownership verified" else "no Minecraft entitlement"),owned,token))
  }catch(e:Exception){onResult(Result("Login failed: "+(e.message?:e.javaClass.simpleName)))}}
 }
 fun name()=prefs.getString("player_name",null)
 fun ownsGame()=prefs.getBoolean("owned",false)
 fun signOut(){prefs.edit().clear().apply()}
}
