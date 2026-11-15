package gg.realmforge.client
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlin.concurrent.thread
/** Microsoft device flow using the public Bedrock Android relying-party ID. */
class BedrockAccount(context:Context){
 private val prefs:SharedPreferences=context.getSharedPreferences("bedrock_account",0)
 fun name():String?=prefs.getString("gamertag",null)
 fun ownsGame()=prefs.getBoolean("owns_game",false)
 fun signOut(){prefs.edit().clear().apply()}
 private val clientId="0000000048183522"
 private fun post(url:String,body:String):JSONObject{
  val c=URL(url).openConnection() as HttpURLConnection;c.requestMethod="POST";c.doOutput=true;c.setRequestProperty("Content-Type","application/x-www-form-urlencoded")
  c.outputStream.use{it.write(body.toByteArray())};val s=if(c.responseCode in 200..299)c.inputStream else c.errorStream
  val result=s.bufferedReader().use{it.readText()};c.disconnect();return JSONObject(result)
 }
 private fun e(s:String)=URLEncoder.encode(s,"UTF-8")
 fun login(status:(String)->Unit,error:(Exception)->Unit){thread{try{
  val dc=post("https://login.live.com/oauth20_devicecode.srf","client_id=$clientId&scope="+e("service::user.auth.xboxlive.com::MBI_SSL offline_access"))
  status("Visit "+dc.optString("verification_uri","https://www.microsoft.com/link")+" and enter "+dc.getString("user_code"))
  var msa:JSONObject?=null;val until=System.currentTimeMillis()+dc.optLong("expires_in",900)*1000
  while(System.currentTimeMillis()<until){Thread.sleep(dc.optLong("interval",5)*1000)
   val p=post("https://login.live.com/oauth20_token.srf","client_id=$clientId&grant_type="+e("urn:ietf:params:oauth:grant-type:device_code")+"&code="+e(dc.getString("device_code")))
   if(p.has("access_token")){msa=p;break};if(p.optString("error")!="authorization_pending"&&p.optString("error")!="slow_down")throw Exception(p.optString("error_description",p.optString("error")))
  }
  val m=msa?:throw Exception("Device code expired")
  val x=post("https://user.auth.xboxlive.com/user/authenticate",JSONObject().put("Properties",JSONObject().put("AuthMethod","RPS").put("SiteName","user.auth.xboxlive.com").put("RpsTicket","d="+m.getString("access_token"))).put("RelyingParty","http://auth.xboxlive.com").put("TokenType","JWT").toString()
  val u=x.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs")
  val z=post("https://xsts.auth.xboxlive.com/xsts/authorize",JSONObject().put("Properties",JSONObject().put("SandboxId","RETAIL").put("UserTokens",org.json.JSONArray().put(x.getString("Token")))).put("RelyingParty","rp://api.minecraftservices.com/").put("TokenType","JWT").toString()
  val realm=post("https://xsts.auth.xboxlive.com/xsts/authorize",JSONObject().put("Properties",JSONObject().put("SandboxId","RETAIL").put("UserTokens",org.json.JSONArray().put(x.getString("Token")))).put("RelyingParty","https://pocket.realms.minecraft.net/").put("TokenType","JWT").toString()
  val mc=post("https://api.minecraftservices.com/authentication/login_with_xbox",JSONObject().put("identityToken","XBL3.0 x=$u;"+z.getString("Token")).toString())
  fun get(path:String)=JSONObject(URL("https://api.minecraftservices.com/$path").openConnection().apply{setRequestProperty("Authorization","Bearer "+mc.getString("access_token"))}.getInputStream().bufferedReader().use{it.readText()})
  val profile=get("minecraft/profile");val ent=get("entitlements/mcstore");val owned=ent.optJSONArray("items")?.length()?.let{it>0}?:false
  prefs.edit().putString("realm_xsts",realm.getString("Token")).putString("xuid",u).putString("gamertag",profile.optString("name")).putString("uuid",profile.optString("id")).putBoolean("owns_game",owned).putString("msa_refresh",m.optString("refresh_token")).apply()
  status("Signed in: "+profile.optString("name")+" | ownership "+if(owned)"verified" else "not verified")
 }catch(ex:Exception){error(ex)}}}
}
